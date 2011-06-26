/**
CreepTD is an online multiplayer towerdefense game
formerly created under the name CreepSmash as a project
at the Hochschule fuer Technik Stuttgart (University of Applied Science)

CreepTD (Since version 0.7.0+) Copyright (C) 2011 by
 * Daniel Wirtz, virtunity media
http://www.creeptd.com

CreepSmash (Till version 0.6.0) Copyright (C) 2008 by
 * Andreas Wittig
 * Bernd Hietler
 * Christoph Fritz
 * Fabian Kessel
 * Levin Fritz
 * Nikolaj Langner
 * Philipp Schulte-Hubbert
 * Robert Rapczynski
 * Ron Trautsch
 * Sven Supper
http://creepsmash.sf.net/

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
package com.creeptd.server.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;

import com.creeptd.common.Constants;
import com.creeptd.common.Constants.ResponseType;
import com.creeptd.common.messages.client.CreateGameMessage;
import com.creeptd.common.messages.client.GameMessage;
import com.creeptd.common.messages.client.JoinGameRequestMessage;
import com.creeptd.common.messages.server.GameDescription;
import com.creeptd.common.messages.server.JoinGameResponseMessage;
import com.creeptd.common.messages.server.PlayerJoinedMessage;
import com.creeptd.common.messages.server.ServerMessage;
import com.creeptd.server.PersistenceManager;
import com.creeptd.server.client.Client;
import com.creeptd.server.game.states.AbstractGameState;
import com.creeptd.server.game.states.WaitingGameState;
import com.creeptd.server.model.GameJournal;

/**
 * Coordinates a game of Tower Defence. This class's job is mainly coordinating
 * the various queues and threads, handling message and other events is
 * delegated to instances of {@link AbstractGameState GameState}.
 */
public class Game extends AbstractGame {

    private static Logger logger = Logger.getLogger(Game.class);
    private AbstractGameState gameState;
    private List<PlayerInGame> players;
    private List<GameObserverInterface> observers;
    private BlockingQueue<GameMessage> queue;
    private volatile boolean terminate = false;

    /**
     * Create a new game with the given gameId. This sets up the queue and the
     * thread that takes messages from the queue.
     *
     * @param gameId
     *            the game's id.
     */
    public Game(Client client, CreateGameMessage message) {
        super(message);

        this.players = new ArrayList<PlayerInGame>();
        this.observers = new LinkedList<GameObserverInterface>();
        this.queue = new LinkedBlockingQueue<GameMessage>();

        this.changeState(new WaitingGameState(this, client));

        this.setName("Game: " + this);
        this.start();
        logger.debug("New game created: " + this);
    }

    @Override
    public void run() {
        while (!this.terminate) {
            try {
                GameMessage message = queue.take();
                if (message == null) {
                    logger.error("Game " + this + ": shutdown...");
                    this.terminate();
                }
                PlayerInGame p = this.findPlayer(message.getClientId());
                this.changeState(this.getGameState().consume(message, p));
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }

    public synchronized void terminate() {
        this.terminate = true;
        this.interrupt();
    }

    public void receive(GameMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("'message' was null!");
        }

        PlayerInGame sender = this.findPlayer(message.getClientId());
        if (sender == null) {
            logger.error("got message from client " + message.getClientId() + ", but that client is not in the game.");
            return;
        }
        this.queue.add(message);
    }

    private void changeState(AbstractGameState newGameState) {
        if (newGameState == null) {
            return;
        }

        AbstractGameState oldGameState = this.gameState;
        if (oldGameState != null) {
            if (newGameState.getClass().equals(oldGameState.getClass())) {
                return;
            }
            oldGameState.leave();
        }
        logger.debug("Changed state of game " + this + " from " + oldGameState + " to " + newGameState);

        this.gameState = newGameState;
        this.gameState.enter();

        gameStateChanged();
        logger.debug("GameStatus changed");
    }

    protected AbstractGameState getGameState() {
        return this.gameState;
    }

    public void sendAll(ServerMessage message) {
        synchronized (this.players) {
            for (PlayerInGame p : this.players) {
                if (p.isConnected()) {
                    p.getClient().send(message);
                }
            }
        }
    }
    
    /**
     * Check if a client is allowed to join this game.
     * @param client The client to test
     * @return true if allowed, else false
     */
    public boolean mayJoin(Client client) {
        if (client == null) {
            return false;
        }
        if (!(this.getGameState() instanceof WaitingGameState)) {
            return false;
        }
        if (this.players.size() >= this.getMaxPlayers()) {
           return false;
        }
        if (this.isMultiaccount(client.getIPAddress(), client.getUid())) {
            return false;
        }
        if (this.findPlayer(client.getId()) != null) {
            return false;
        }
        return true;
    }

    /**
     * Add a player to the game.
     * @param client
     */
    public boolean addPlayer(Client client) {
        if (!mayJoin(client)) {
            return false;
        }
        synchronized (this.players) {
            client.send(new JoinGameResponseMessage(ResponseType.ok));
            for (PlayerInGame p : this.players) {
                client.send(new PlayerJoinedMessage(p.getClient().getPlayerModel().getName(), p.getClient().getId(), p.getClient().getPlayerModel().getPoints(), p.getClient().getPlayerModel().getSkill()));
            }
            PlayerInGame p = new PlayerInGame(client);
            this.players.add(p);
        }

        this.sendAll(new PlayerJoinedMessage(client.getPlayerModel().getName(),
                client.getId(),
                client.getPlayerModel().getPoints(), client.getPlayerModel().getSkill()));
        gamePlayersChanged();
        logger.debug("Player joined to the game");
        return true;
    }

    public void removePlayer(PlayerInGame player) {
        if (player == null) {
            throw new IllegalArgumentException("'player' was null!");
        }
        synchronized (this.players) {
            this.players.remove(player);
        }
        gamePlayersChanged();
    }

    public void removePlayer(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("'client' was null!");
        }
        this.removePlayer(this.findPlayer(client.getId()));
    }

    /**
     * Returns the number of still connected players.
     *
     * @return
     */
    public int numConnectedPlayers() {
        int num = 0;
        for (PlayerInGame p : this.players) {
            if (p.isConnected()) {
                num++;
            }
        }
        return num;
    }

    public List<PlayerInGame> getPlayers() {
        return new ArrayList<PlayerInGame>(this.players);
    }

    /**
     * Returns the number of players (including somewhen disconnected players)
     *
     * @return
     */
    public int numPlayers() {
        return this.players.size();
    }

    public PlayerInGame findPlayer(int clientId) {
        synchronized (this.players) {
            for (PlayerInGame p : this.players) {
                if (p.getClient().getId() == clientId) {
                    return p;
                }
            }
        }
        return null;
    }

    public PlayerInGame findPlayer(String playerName) {
        synchronized (this.players) {
            for (PlayerInGame p : this.players) {
                if (p.getClient().getPlayerModel().getName().equals(playerName)) {
                    return p;
                }
            }
        }
        return null;
    }

    public void shufflePlayers() {
        synchronized (this.players) {
            Collections.shuffle(this.players);
            this.players = new ArrayList<PlayerInGame>(this.players);
        }
    }

    public GameDescription getGameDescription() {
        String player1 = "";
        String player2 = "";
        String player3 = "";
        String player4 = "";

        int count = 0;
        int score = 0;
        synchronized (this.players) {
            for (PlayerInGame p : this.players) {
                if (count == 0) {
                    player1 = p.getClient().getPlayerModel().getName();
                } else if (count == 1) {
                    player2 = p.getClient().getPlayerModel().getName();
                } else if (count == 2) {
                    player3 = p.getClient().getPlayerModel().getName();
                } else if (count == 3) {
                    player4 = p.getClient().getPlayerModel().getName();
                }
                score += p.getClient().getPlayerModel().getSkill();
                count++;
            }
            if (count != 0) {
                score = (int) (score / count);
            }
        }
        return new GameDescription(this.getGameId(), this.getGameName(), this.getMapId(), this.getMaxPlayers(),
                count, this.getMaxPoints(), this.getMinPoints(), this.getPassword().length() > 0 ? "yes" : "no", this.getMode(), player1, player2, player3, player4, this.getGameState().toString(), this.getShufflePlayers());
    }

    public boolean canPlayerJoin(Client client, JoinGameRequestMessage jgrm) {
        if (!(this.getGameState() instanceof WaitingGameState)) {
            return false;
        }
        if (this.numPlayers() >= this.getMaxPlayers()) {
            return false;
        }
        if ((!this.getPassword().equals("")) && (!this.getPassword().equals(jgrm.getPassword()))) {
            return false;
        }
        if (this.getMinPoints() > 0 && client.getPlayerModel().getSkill() < this.getMinPoints()) {
            return false;
        }
        if (this.getMaxPoints() > 0 && client.getPlayerModel().getSkill() > this.getMaxPoints()) {
            return false;
        }
        return true;
    }

    /**
     * Adds an observer. It will be notified of any change in the game's state
     * or list of getPlayersInGame.
     *
     * @param observer
     *            the observer. Must not be null.
     */
    public void addObserver(GameObserverInterface observer) {
        if (observer == null) {
            throw new IllegalArgumentException("'observer' was null");
        }
        this.observers.add(observer);
    }

    /**
     * Notifies all observers.
     */
    public void gameStateChanged() {
        for (GameObserverInterface observer : this.observers) {
            observer.gameStateChanged((Game) this);
        }
    }

    /**
     * Notifies all observers.
     */
    public void gamePlayersChanged() {
        for (GameObserverInterface observer : this.observers) {
            observer.gamePlayersChanged((Game) this);
        }
    }

    /**
     * The Function saves actual ended game into DB
     *
     * @param playerNamePositionMap
     *            Players with position
     * @param startDate
     *            Start date of game
     */
    public void saveToJournal(List<PlayerInGame> players, List<PlayerInGame> playerPositions, long startDate, long endDate) {
        try {
            EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            GameJournal gameJournalEntry = new GameJournal();
            String mapname = Constants.Map.getMapById(this.getMapId()).getFilename();
            mapname = mapname.replaceAll("com/creeptd/client/resources/maps/map_", "");
            gameJournalEntry.setMap(mapname.replaceAll(".map", ""));
            gameJournalEntry.setName(this.getGameName());
            gameJournalEntry.setNumPlayers(players.size());
            gameJournalEntry.setStart_date(startDate);
            gameJournalEntry.setEnd_date(endDate);
            int i = 0;
            for (PlayerInGame player : players) {
                i++;
                Client client = player.getClient();
                if (client.getPlayerModel() != null) {
                    int num = playerPositions.indexOf(player) + 1;
                    if (i == 1) {
                        gameJournalEntry.setPlayer1(client.getPlayerModel().getName());
                        gameJournalEntry.setPlayer1Points(client.getPlayerModel().getPoints());
                        gameJournalEntry.setPlayer1GivenPoints(client.getPlayerModel().getLastgamePoints());
                        gameJournalEntry.setPlayer1Skill(client.getPlayerModel().getSkill());
                        gameJournalEntry.setPlayer1GivenSkill(client.getPlayerModel().getLastgameSkill());
                        gameJournalEntry.setPlayer1Position(num);
                        gameJournalEntry.setIp1(client.getPlayerModel().getIp());
                        gameJournalEntry.setMac1(client.getPlayerModel().getMac());
                    } else if (i == 2) {
                        gameJournalEntry.setPlayer2(client.getPlayerModel().getName());
                        gameJournalEntry.setPlayer2Points(client.getPlayerModel().getPoints());
                        gameJournalEntry.setPlayer2GivenPoints(client.getPlayerModel().getLastgamePoints());
                        gameJournalEntry.setPlayer2Skill(client.getPlayerModel().getSkill());
                        gameJournalEntry.setPlayer2GivenSkill(client.getPlayerModel().getLastgameSkill());
                        gameJournalEntry.setPlayer2Position(num);
                        gameJournalEntry.setIp2(client.getPlayerModel().getIp());
                        gameJournalEntry.setMac2(client.getPlayerModel().getMac());
                    } else if (i == 3) {
                        gameJournalEntry.setPlayer3(client.getPlayerModel().getName());
                        gameJournalEntry.setPlayer3Points(client.getPlayerModel().getPoints());
                        gameJournalEntry.setPlayer3GivenPoints(client.getPlayerModel().getLastgamePoints());
                        gameJournalEntry.setPlayer3Skill(client.getPlayerModel().getSkill());
                        gameJournalEntry.setPlayer3GivenSkill(client.getPlayerModel().getLastgameSkill());
                        gameJournalEntry.setPlayer3Position(num);
                        gameJournalEntry.setIp3(client.getPlayerModel().getIp());
                        gameJournalEntry.setMac3(client.getPlayerModel().getMac());
                    } else if (i == 4) {
                        gameJournalEntry.setPlayer4(client.getPlayerModel().getName());
                        gameJournalEntry.setPlayer4Points(client.getPlayerModel().getPoints());
                        gameJournalEntry.setPlayer4GivenPoints(client.getPlayerModel().getLastgamePoints());
                        gameJournalEntry.setPlayer4Skill(client.getPlayerModel().getSkill());
                        gameJournalEntry.setPlayer4GivenSkill(client.getPlayerModel().getLastgameSkill());
                        gameJournalEntry.setPlayer4Position(num);
                        gameJournalEntry.setIp4(client.getPlayerModel().getIp());
                        gameJournalEntry.setMac4(client.getPlayerModel().getMac());
                    } else {
                        logger.error("False number of players: " + i);
                    }
                } else {
                    logger.error("Empty PlayerModel");
                }
            }

            entityManager.persist(gameJournalEntry);
            entityManager.flush();
            entityTransaction.commit();
            logger.debug("GameJournal saved.");
        } catch (Throwable t) {
            logger.error("error while saving GameJournal", t);
        }
    }

    /**
     * Check for multi account using.
     *
     * @param ip Client's IP address
     * @param uid Client's UID
     * @return true if detected as multi account, else false
     */
    public boolean isMultiaccount(String ip, String uid) {
        if (Constants.MUTIACCOUNT_IP_CHECK > 0) {
            int n=0; for (PlayerInGame p : this.getPlayers()) {
                if (p.getClient().getIPAddress().equals(ip)) n++;
            }
            if (n >= Constants.MUTIACCOUNT_IP_CHECK) {
                return false;
            }
        }
        if (Constants.MUTIACCOUNT_UID_CHECK) {
            for (PlayerInGame p : this.getPlayers()) {
                if (p.getClient().getUid().equals(uid)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getGameId() + "/" + this.getGameName();
    }
}

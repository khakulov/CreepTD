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

import com.creeptd.common.IConstants;
import com.creeptd.common.messages.client.CreateGameMessage;
import com.creeptd.common.messages.client.GameMessage;
import com.creeptd.common.messages.client.JoinGameRequestMessage;
import com.creeptd.common.messages.server.GameDescription;
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
    private List<PlayerInGame> playersInGame;
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

        this.playersInGame = new ArrayList<PlayerInGame>();
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
                PlayerInGame p = this.findPlayerInGame(message.getClientId());
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

        PlayerInGame sender = this.findPlayerInGame(message.getClientId());
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
        synchronized (this.playersInGame) {
            for (PlayerInGame p : this.playersInGame) {
                p.getClient().send(message);
            }
        }
    }

    public void addPlayer(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("'newClient' was null!");
        }
        if (!(this.getGameState() instanceof WaitingGameState)) {
            throw new RuntimeException("Game has started, no more players can join (State is " + this.getGameState()+")");
        }
        if (this.getPlayersInGameSize() >= this.getMaxPlayers()) {
            throw new RuntimeException("Maximum number of players reached, no more players can join");
        }
        if (this.findPlayerInGame(client.getClientID()) != null) {
            return;
        }

        synchronized (this.playersInGame) {
            for (PlayerInGame p : this.playersInGame) {
                client.send(new PlayerJoinedMessage(p.getClient().getPlayerModel().getName(), p.getClient().getClientID(), p.getClient().getPlayerModel().getExperience(), p.getClient().getPlayerModel().getElopoints()));
            }
            PlayerInGame p = new PlayerInGame(client);
            this.playersInGame.add(p);
            this.players.add(p);
        }

        this.sendAll(new PlayerJoinedMessage(client.getPlayerModel().getName(),
                client.getClientID(),
                client.getPlayerModel().getExperience(), client.getPlayerModel().getElopoints()));
        gamePlayersChanged();
        logger.debug("Player joined to the game");
    }

    public void removePlayerInGame(PlayerInGame player) {
        if (player == null) {
            throw new IllegalArgumentException("'player' was null!");
        }
        synchronized (this.playersInGame) {
            this.playersInGame.remove(player);
        }
        gamePlayersChanged();
    }

    public void removePlayerInGame(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("'client' was null!");
        }
        this.removePlayerInGame(this.findPlayerInGame(client.getClientID()));
    }

    public int getPlayersInGameSize() {
        synchronized (this.playersInGame) {
            return this.playersInGame.size();
        }
    }

    public List<PlayerInGame> getPlayers() {
        return this.players;
    }

    public List<PlayerInGame> getPlayersInGame() {
        List<PlayerInGame> list = new ArrayList<PlayerInGame>();
        synchronized (this.playersInGame) {
            for (PlayerInGame p : this.playersInGame) {
                list.add(p);
            }
        }
        return list;
    }

    public PlayerInGame findPlayerInGame(int clientId) {
        synchronized (this.playersInGame) {
            for (PlayerInGame p : this.playersInGame) {
                if (p.getClient().getClientID() == clientId) {
                    return p;
                }
            }
        }
        return null;
    }

    public PlayerInGame findPlayerInGame(String playerName) {
        synchronized (this.playersInGame) {
            for (PlayerInGame p : this.playersInGame) {
                if (p.getClient().getPlayerModel().getName().equals(playerName)) {
                    return p;
                }
            }
        }
        return null;
    }

    public void shufflePlayersInGame() {
        synchronized (this.playersInGame) {
            Collections.shuffle(this.playersInGame);
            this.players = new ArrayList(this.playersInGame);
            logger.info("Shuffled players ("+this.playersInGame.size()+" in game, "+this.players.size()+" players)");
        }
    }

    public GameDescription getGameDescription() {
        String player1 = "";
        String player2 = "";
        String player3 = "";
        String player4 = "";

        int count = 0;
        int score = 0;
        synchronized (this.playersInGame) {
            for (PlayerInGame p : this.playersInGame) {
                if (count == 0) {
                    player1 = p.getClient().getPlayerModel().getName();
                } else if (count == 1) {
                    player2 = p.getClient().getPlayerModel().getName();
                } else if (count == 2) {
                    player3 = p.getClient().getPlayerModel().getName();
                } else if (count == 3) {
                    player4 = p.getClient().getPlayerModel().getName();
                }
                score += p.getClient().getPlayerModel().getElopoints();
                count++;
            }
            if (count != 0) {
                score = (int) (score / count);
            }
        }

        return new GameDescription(this.getGameId(), "[" + score + "]" + this.getGameName(), this.getMapId(), this.getMaxPlayers(),
                count, this.getMaxPoints(), this.getMinPoints(), this.getPasswort().length() > 0 ? "yes" : "no", this.getMode(), player1, player2, player3, player4, this.getGameState().toString(), this.getShufflePlayers());
    }

    public boolean canPlayerJoin(Client client, JoinGameRequestMessage jgrm) {
        if (!(this.getGameState() instanceof WaitingGameState)) {
            return false;
        }
        if (this.getPlayersInGameSize() >= this.getMaxPlayers()) {
            return false;
        }
        if ((!this.getPasswort().equals("")) && (!this.getPasswort().equals(jgrm.getPasswort()))) {
            return false;
        }
        if (this.getMinPoints() > 0 && client.getPlayerModel().getElopoints() < this.getMinPoints()) {
            return false;
        }
        if (this.getMaxPoints() > 0 && client.getPlayerModel().getElopoints() > this.getMaxPoints()) {
            return false;
        }
        /* int points = client.getPlayerModel().getElopoints() - 500; // oldEloPoints
        if (points > 1000) {
        synchronized (this.getPlayersInGame) {
        for (PlayerInGame p : this.getPlayersInGame) {
        if (p.getClient().getPlayerModel().getElopoints() < (points-500))
        return false;
        }
        }
        }
        synchronized (this.getPlayersInGame) {
        for (PlayerInGame p : this.getPlayersInGame) {
        if ((p.getClient().getPlayerModel().getElopoints() > 1500) &&
        (points < p.getClient().getPlayerModel().getElopoints()-1500))
        return false;
        }
        } */
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
            String mapname = IConstants.Map.getMapById(this.getMapId()).getFilename();
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
                        gameJournalEntry.setPlayer1Experience(client.getPlayerModel().getExperience());
                        gameJournalEntry.setPlayer1GivenExperience(client.getPlayerModel().getLastgameExperience());
                        gameJournalEntry.setPlayer1Elopoints(client.getPlayerModel().getElopoints());
                        gameJournalEntry.setPlayer1GivenElopoints(client.getPlayerModel().getLastgameElopoints());
                        gameJournalEntry.setPlayer1Position(num);
                        gameJournalEntry.setIp1(client.getPlayerModel().getIp());
                        gameJournalEntry.setMac1(client.getPlayerModel().getMac());
                    } else if (i == 2) {
                        gameJournalEntry.setPlayer2(client.getPlayerModel().getName());
                        gameJournalEntry.setPlayer2Experience(client.getPlayerModel().getExperience());
                        gameJournalEntry.setPlayer2GivenExperience(client.getPlayerModel().getLastgameExperience());
                        gameJournalEntry.setPlayer2Elopoints(client.getPlayerModel().getElopoints());
                        gameJournalEntry.setPlayer2GivenElopoints(client.getPlayerModel().getLastgameElopoints());
                        gameJournalEntry.setPlayer2Position(num);
                        gameJournalEntry.setIp2(client.getPlayerModel().getIp());
                        gameJournalEntry.setMac2(client.getPlayerModel().getMac());
                    } else if (i == 3) {
                        gameJournalEntry.setPlayer3(client.getPlayerModel().getName());
                        gameJournalEntry.setPlayer3Experience(client.getPlayerModel().getExperience());
                        gameJournalEntry.setPlayer3GivenExperience(client.getPlayerModel().getLastgameExperience());
                        gameJournalEntry.setPlayer3Elopoints(client.getPlayerModel().getElopoints());
                        gameJournalEntry.setPlayer3GivenElopoints(client.getPlayerModel().getLastgameElopoints());
                        gameJournalEntry.setPlayer3Position(num);
                        gameJournalEntry.setIp3(client.getPlayerModel().getIp());
                        gameJournalEntry.setMac3(client.getPlayerModel().getMac());
                    } else if (i == 4) {
                        gameJournalEntry.setPlayer4(client.getPlayerModel().getName());
                        gameJournalEntry.setPlayer4Experience(client.getPlayerModel().getExperience());
                        gameJournalEntry.setPlayer4GivenExperience(client.getPlayerModel().getLastgameExperience());
                        gameJournalEntry.setPlayer4Elopoints(client.getPlayerModel().getElopoints());
                        gameJournalEntry.setPlayer4GivenElopoints(client.getPlayerModel().getLastgameElopoints());
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
     * The function check for multiaccounting.
     *
     * @param ip
     *            IP Address of Client
     */
    public boolean check4Multi(String ip, String mac) {
        /*
         * TODO if (IConstants.MUTIACCOUNT_IP_CHECK ||
         * IConstants.MUTIACCOUNT_MAC_CHECK) { boolean a = false; synchronized
         * (this.clients) { for (PlayerInGame p : this.clients) { Client c =
         * p.getClient(); // If the client from same Computer if
         * (mac.equalsIgnoreCase(c.getPlayerModel().getMac()) &&
         * IConstants.MUTIACCOUNT_MAC_CHECK) {
         * logger.warn("Multiaccounting detected. MAC: " + mac); return false; }
         * // If the client from same IP if
         * (ip.equalsIgnoreCase(c.getIPAddress()) &&
         * IConstants.MUTIACCOUNT_IP_CHECK) { if (a) {
         * logger.warn("Multiaccounting detected. IP: " + ip); return false; } a
         * = true; } } } }
         */
        return true;
    }

    @Override
    public String toString() {
        return "[Game"+this.getGameId()+"/"+this.getGameState()+"]";
    }
}

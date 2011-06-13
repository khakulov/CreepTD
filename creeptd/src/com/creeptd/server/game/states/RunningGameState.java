/**
Creep Smash, a multiplayer towerdefence game
created as a project at the Hochschule fuer
Technik Stuttgart (University of Applied Science)
http://www.hft-stuttgart.de 

Copyright (C) 2008 by      
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
package com.creeptd.server.game.states;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.creeptd.common.IConstants;
import com.creeptd.common.messages.client.BuildCreepMessage;
import com.creeptd.common.messages.client.BuildTowerMessage;
import com.creeptd.common.messages.client.ChangeStrategyMessage;
import com.creeptd.common.messages.client.ExitGameMessage;
import com.creeptd.common.messages.client.GameMessage;
import com.creeptd.common.messages.client.GameOverMessage;
import com.creeptd.common.messages.client.LiveTakedMessage;
import com.creeptd.common.messages.client.LogoutMessage;
import com.creeptd.common.messages.client.SellTowerMessage;
import com.creeptd.common.messages.client.ClientChatMessage;
import com.creeptd.common.messages.client.UpgradeTowerMessage;
import com.creeptd.common.messages.server.BuildCreepRoundMessage;
import com.creeptd.common.messages.server.BuildTowerRoundMessage;
import com.creeptd.common.messages.server.ChangeStrategyRoundMessage;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.common.messages.server.PlayerQuitMessage;
import com.creeptd.common.messages.server.RoundMessage;
import com.creeptd.common.messages.server.SellTowerRoundMessage;
import com.creeptd.common.messages.server.UpgradeTowerRoundMessage;
import com.creeptd.server.HighscoreService;
import com.creeptd.server.game.Game;
import com.creeptd.server.game.PlayerInGame;
import com.creeptd.server.game.TickThread;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * GameState for a game that's running (has started).
 */
public class RunningGameState extends AbstractGameState implements
        TickThread.TickReceiver {

    private int nextTowerId;
    private long maxTick;
    private ArrayList<PlayerInGame> playerPositions;
    private long startDate;
    TickThread tickThread;
    private static Logger logger = Logger.getLogger(RunningGameState.class);
    private List<List> incomeLog = new LinkedList<List>(); // Round1(Player1 inc, ...), Round2(Player1 inc, ...)

    /**
     * Constructor.
     *
     * @param game
     *            the game. Must not be null. Must not be null or empty.
     */
    public RunningGameState(Game game) {
        super(game);
        this.nextTowerId = 1;
        this.maxTick = 0;
        this.playerPositions = new ArrayList();
        synchronized(this.playerPositions) {
            for (int i=0; i<game.getMaxPlayers(); i++) {
                this.playerPositions.add(null);
            }
            logger.info(game+" Initialized with "+this.playerPositions.size()+" players");
        }
        this.startDate = System.currentTimeMillis() / 1000;
        tickThread = new TickThread(this, IConstants.TICK_MS * 1000000);
    }

    /**
     * Advance the maxTick counter and send a "ROUND n OK" message to all
     * players.
     */
    public void tick() {
        if (this.maxTick == 0) {
            RoundMessage message = new RoundMessage();
            message.setRoundId(this.maxTick + IConstants.USER_ACTION_DELAY);
            this.getGame().sendAll(message);
        }

        this.maxTick += 1;

        if ((this.maxTick < IConstants.USER_ACTION_DELAY * 10) && (this.maxTick % IConstants.USER_ACTION_DELAY == 0)) {
            RoundMessage message = new RoundMessage();
            message.setRoundId(this.maxTick + IConstants.USER_ACTION_DELAY);
            this.getGame().sendAll(message);
        }

        if (this.maxTick % (IConstants.USER_ACTION_DELAY * 10) == 0) {
            RoundMessage message = new RoundMessage();
            message.setRoundId(this.maxTick + (IConstants.USER_ACTION_DELAY * 10));
            this.getGame().sendAll(message);
        }

        if (this.maxTick % (IConstants.INCOME_TIME / IConstants.TICK_MS) == 0) {
            List<Integer> incomeLogEntry = new LinkedList<Integer>();
            for (PlayerInGame p : this.getGame().getPlayers()) {
                if (p.isConnected()) {
                    p.anticheat_updateMoney(this.maxTick);
                    if (p.anticheat_getCurrentMoney() < 0) {
                        processIntegrity(p.getClient().getClientID());
                        incomeLogEntry.add(p.anticheat_getCurrentIncome());
                    }
                }
            }
            incomeLog.add(incomeLogEntry);
        }
    }

    /**
     * Handle a message (from a client, presumably).
     *
     * @param message
     *            the message. Must not be null.
     * @param sender
     *            the player who sent the message. Must not be null.
     * @return the new state
     */
    public AbstractGameState consume(GameMessage message, PlayerInGame sender) {
        if (message == null) {
            throw new IllegalArgumentException("'message' was null");
        }
        if (sender == null) {
            throw new IllegalArgumentException("'sender' was null");
        }

        if (message instanceof BuildCreepMessage) {
            this.handle((BuildCreepMessage) message);
        } else if (message instanceof UpgradeTowerMessage) {
            this.handle((UpgradeTowerMessage) message, sender);
        } else if (message instanceof BuildTowerMessage) {
            this.handle((BuildTowerMessage) message);
        } else if (message instanceof ChangeStrategyMessage) {
            this.handle((ChangeStrategyMessage) message, sender);
        } else if (message instanceof SellTowerMessage) {
            this.handle((SellTowerMessage) message, sender);
        } else if (message instanceof LiveTakedMessage) {
            this.handle((LiveTakedMessage) message);
        } else if (message instanceof GameOverMessage) {
            this.handle((GameOverMessage) message, sender);
        } else if (message instanceof ExitGameMessage) {
            ExitGameMessage exitMsg = (ExitGameMessage) message;
            if ("integrity".equals(exitMsg.getMessage())) {
                processIntegrity(exitMsg.getClientId());
            }
            return this.removePlayer(sender);
        } else if (message instanceof LogoutMessage) {
            return this.removePlayer(sender);
        } else if (message instanceof ClientChatMessage) {
            handle((ClientChatMessage) message, sender);
        } else {
            logger.error("cannot handle message: " + message);
        }
        return this;
        /*
         * for (PlayerInGame p : this.getGame().getClients()) {
         * logger.info("Player " + p.getClient().getUserName() + " have Money "
         * + p.anticheat_getCurrentMoney() + " at Round " + this.maxTick); }
         */
    }

    private void handle(ChangeStrategyMessage message, PlayerInGame sender) {
        int towerId = message.getTowerId();

        if (towerId <= 0) {
            logger.error("Invalid tower id (tried to change strategy " + towerId + ")");
            return;
        }
        if (towerId >= this.nextTowerId) {
            logger.error("No such tower (tried to change strategy " + towerId + ")");
            return;
        }

        ChangeStrategyRoundMessage csm = new ChangeStrategyRoundMessage();
        csm.setRoundId(this.maxTick + IConstants.USER_ACTION_DELAY);
        csm.setPlayerId(message.getClientId());
        csm.setTowerId(towerId);
        csm.setStrategyType(message.getStrategyType());
        csm.setLocked(message.isLocked());
        this.getGame().sendAll(csm);

    }

    private void handle(LiveTakedMessage m) {
        PlayerInGame from = this.getGame().findPlayer(m.getFromPlayerId());
        PlayerInGame to = this.getGame().findPlayer(m.getToPlayerId());
        PlayerInGame sender = this.getGame().findPlayer(m.getSenderId());

        if (from != null) {
            from.takeLive();
            from.anticheat_transferThisCreep(m.getCreepType(), m.getRoundId());
        }
        if (to != null) {
            to.anticheat_receivedThisCreep(m.getCreepType(), m.getRoundId());
        }
        if (sender != null) {
            sender.incraseTakedLive();
        }
    }

    /**
     * Handles the BuildTowerMessage.
     *
     * @param m
     *            the message
     */
    private void handle(BuildTowerMessage m) {
        String type = m.getTowerType();
        Point position = m.getPosition();
        int senderId = m.getClientId();
        long roundID = this.maxTick + IConstants.USER_ACTION_DELAY;

        BuildTowerRoundMessage n = new BuildTowerRoundMessage();
        n.setRoundId(roundID);
        n.setPlayerId(senderId);
        n.setTowerType(type);
        n.setTowerPosition(position);
        n.setTowerId(this.nextTowerId++);
        this.getGame().sendAll(n);

        /*
         * for (PlayerInGame p : this.getGame().getClients()) { if
         * (p.getClient().getClientID() == senderId) {
         * p.anticheat_TowerBuilt(type, bcrm.getTowerId(), m.getRoundId()); break;
         * } }
         */
    }

    /**
     * Handles the UpgradeTowerMessage.
     *
     * @param m
     *            the message.
     * @param sender
     *            the player who sent the message.
     */
    private void handle(UpgradeTowerMessage m, PlayerInGame sender) {
        int towerId = m.getTowerId();
        long roundID = this.maxTick + IConstants.USER_ACTION_DELAY;

        if (towerId <= 0) {
            logger.error("Invalid tower id (tried to upgrade tower " + towerId + ")");
            return;
        } else if (towerId >= this.nextTowerId) {
            logger.error("No such tower (tried to upgrade tower " + towerId + ")");
            return;
        }

        UpgradeTowerRoundMessage n = new UpgradeTowerRoundMessage();
        n.setRoundId(roundID);
        n.setPlayerId(m.getClientId());
        n.setTowerId(towerId);
        this.getGame().sendAll(n);
        //sender.anticheat_TowerUpgraded(towerId, m.getRoundId());

    }

    /**
     * Handles the SellTowerMessage.
     *
     * @param m
     *            the message.
     * @param sender
     *            the player who sent the message.
     */
    private void handle(SellTowerMessage m, PlayerInGame sender) {
        int towerId = m.getTowerId();

        if (towerId <= 0) {
            logger.error("Invalid tower id (tried to sell tower " + towerId + ")");
            return;
        }
        if (towerId >= this.nextTowerId) {
            logger.error("No such tower (tried to sell tower " + towerId + ")");
            return;
        }

        SellTowerRoundMessage n = new SellTowerRoundMessage();
        n.setRoundId(this.maxTick + IConstants.USER_ACTION_DELAY);
        n.setPlayerId(m.getClientId());
        n.setTowerId(towerId);
        this.getGame().sendAll(n);

        //sender.anticheat_TowerSold(m.getTowerId(), m.getRoundId());
    }

    /**
     * Handles the BuildCreepMessage.
     *
     * @param m
     *            the message
     */
    private void handle(BuildCreepMessage m) {
        String type = m.getCreepType();
        int senderId = m.getClientId();
        long roundID = this.maxTick + IConstants.USER_ACTION_DELAY;

        if ((this.getGame().getMode().equals(IConstants.Mode.ALLVSALL)) && (this.getGame().numPlayers() >= 2)) {
            for (PlayerInGame p : this.getGame().getPlayers()) {
                if ((p.getClient().getClientID() != senderId) && (!p.getGameOver())) {
                    BuildCreepRoundMessage n = new BuildCreepRoundMessage();
                    n.setRoundId(roundID);
                    n.setCreepType(type);
                    n.setSenderId(senderId);
                    n.setPlayerId(p.getClient().getClientID());
                    this.getGame().sendAll(n);
                    p.anticheat_receivedThisCreep(type, m.getRoundId());
                }
            }
        } else if ((this.getGame().getMode().equals(IConstants.Mode.SENDRANDOM)) && (this.getGame().numPlayers() > 2)) {
            List<PlayerInGame> pl = new ArrayList(this.getGame().getPlayers()); // Work with a copy
            while (!pl.isEmpty()) {
                PlayerInGame p = pl.get(new Random().nextInt(pl.size()));
                if (p != null) {
                    if ((p.getClient().getClientID() != senderId)) {
                        if (!p.getGameOver()) {
                            BuildCreepRoundMessage bcrm = new BuildCreepRoundMessage();
                            bcrm.setRoundId(roundID);
                            bcrm.setCreepType(type);
                            bcrm.setSenderId(senderId);
                            bcrm.setPlayerId(p.getClient().getClientID());
                            this.getGame().sendAll(bcrm);
                            p.anticheat_receivedThisCreep(type, m.getRoundId());
                            break;
                        } else {
                            pl.remove(p);
                        }
                    } else {
                        pl.remove(p);
                    }
                } else {
                    logger.error("Random send mode error. Player was null.");
                    break;
                }
            }
        } else if (this.getGame().getMode().equals(IConstants.Mode.TEAM2VS2)) { // Team 2vs2
            List<PlayerInGame> pl = this.getGame().getPlayers();
            int senderPosition = 0;
            Iterator<PlayerInGame> it = pl.iterator();
            while (it.hasNext()) {
                if (it.next().getClient().getClientID() == senderId) {
                    break;
                }
                senderPosition++;
            }
            PlayerInGame receiver = null;
            if (senderPosition <= 1) { // Team A (top)
                receiver = pl.get(2);
            } else { // Team B (bottom)
                receiver = pl.get(0);
            }
            BuildCreepRoundMessage n = new BuildCreepRoundMessage();
            n.setRoundId(roundID);
            n.setCreepType(type);
            n.setSenderId(senderId);
            n.setPlayerId(receiver.getClient().getClientID());
            this.getGame().sendAll(n);

            receiver.anticheat_receivedThisCreep(type, m.getRoundId());
            
        } else {
            // Send next
            List<PlayerInGame> pl = this.getGame().getPlayers();
            Iterator<PlayerInGame> it = pl.iterator();
            PlayerInGame receiver = null;
            while (it.hasNext()) {
                if (it.next().getClient().getClientID() == senderId) {
                    if (it.hasNext()) {
                        receiver = it.next();
                    } else {
                        receiver = pl.get(0);
                    }
                }
            }
            BuildCreepRoundMessage n = new BuildCreepRoundMessage();
            n.setRoundId(roundID);
            n.setCreepType(type);
            n.setSenderId(senderId);
            n.setPlayerId(receiver.getClient().getClientID());
            this.getGame().sendAll(n);

            receiver.anticheat_receivedThisCreep(type, m.getRoundId());
        }
    }

    /**
     * Handles the GameOverMessage.
     *
     * @param m
     *            the message
     * @param sender
     *            the player who sent the message.
     * @return the new state.
     */
    private AbstractGameState handle(GameOverMessage m, PlayerInGame sender) {
        this.playerGameOver(sender, m.getPosition());
        logger.info("Player "+sender+" sent winningPosition="+m.getPosition());
        if (this.gameOverForAll()) {
            this.endGame();
            return new EndedGameState(this.getGame());
        }
        return this;
    }

    /**
     * Returns a string identifying this state.
     *
     * @return "running"
     */
    @Override
    public String toString() {
        return "running";
    }

    @Override
    public void enter() {
        tickThread.start();
    }

    @Override
    public void leave() {
        tickThread.terminate();
    }

    private AbstractGameState removePlayer(PlayerInGame player) {
        this.playerGameOver(player, 0); // sort automatically
        // Don't remove, set disconnected. We'll need the complete players array!
        // Removal of players is done when entering EndedGameState
        player.setConnected(false);
        ServerChatMessage chatMsg = new ServerChatMessage();
        chatMsg.setPlayerName(player.getClient().getPlayerModel().getName());
        chatMsg.setMessage("has left the game");
        this.getGame().sendAll(chatMsg);
        this.getGame().sendAll(new PlayerQuitMessage(player.getClient().getPlayerModel().getName(), "", player.getClient().getClientID()));
        if (this.gameOverForAll()) {
            this.endGame();
            return new EndedGameState(this.getGame());
        }
        if (this.getGame().numConnectedPlayers() == 0) {
            return new TerminatedGameState(this.getGame());
        }
        return this;
    }

    private void playerGameOver(PlayerInGame player, int submitted_position) {
        int position = 0;
        synchronized (this.playerPositions) {
            if (!this.playerPositions.contains(player)) {
                if (submitted_position > 0 && submitted_position <= this.getGame().getMaxPlayers() && this.playerPositions.get(submitted_position-1) == null) {
                    position = submitted_position;
                    this.playerPositions.set(position-1, player);
                    logger.info(this.getGame()+" Setting player "+player+" to position "+position+" (Submitted by player)");
                } else if (this.getGame().getMode().equals(IConstants.Mode.TEAM2VS2) && submitted_position == 2 &&
                            this.playerPositions.get(1) != null && this.playerPositions.get(0) == null) {
                        this.playerPositions.set(0, player);
                        logger.info(this.getGame()+" Setting player "+player+" to position 1 (Team 2vs2 over)");
                } else {
                    for (position = this.getGame().getMaxPlayers(); position >= 1; position--) {
                        if (this.playerPositions.get(position-1) == null) {
                            this.playerPositions.set(position-1, player);
                            logger.info(this.getGame()+" Setting player "+player+" to position "+position+" (Automatic sort)");
                            break;
                        }
                    }
                }
                position = this.playerPositions.indexOf(player);
                logger.info("Game over for " + player + " (position: #" + (position+1) + ")");
            }
        }
        player.gameOver();
    }

    private boolean gameOverForAll() {
        PlayerInGame winplayer = null;

        // Team 2vs2
        if (this.getGame().getGameDescription().getGameMode().equals(IConstants.Mode.TEAM2VS2)) {
            boolean isover = false;
            List<PlayerInGame> players = this.getGame().getPlayers();
            if (players.get(0).getGameOver() && players.get(1).getGameOver()) {
                if (!players.get(2).getGameOver()) {
                    playerGameOver(players.get(2), 1);
                }
                if (!players.get(3).getGameOver()) {
                    playerGameOver(players.get(3), 2);
                }
                return true;
            }
            if (players.get(2).getGameOver() && players.get(3).getGameOver()) {
                if (!players.get(0).getGameOver()) {
                    playerGameOver(players.get(0), 1);
                }
                if (!players.get(1).getGameOver()) {
                    playerGameOver(players.get(1), 2);
                }
                return true;
            }
            return false;
        }

        // Other modes
        for (PlayerInGame p : this.getGame().getPlayers()) {
            if (!p.getGameOver()) {
                if (winplayer == null) {
                    winplayer = p;
                } else {
                    return false;
                }
            }
        }
        if (winplayer != null) {
            this.playerGameOver(winplayer, 0); // last player is sorted automatically
        }
        return true;
    }
    private boolean ended = false;

    private synchronized void endGame() { // only once
        if (this.ended) {
            return;
        }
        // Sort missing players into position map
        for (PlayerInGame p : this.getGame().getPlayers()) {
            synchronized (this.playerPositions) {
                if (!this.playerPositions.contains(p)) {
                    for (int position = this.getGame().getMaxPlayers(); position>=1; position--) {
                        if (this.playerPositions.get(position-1) == null) {
                            this.playerPositions.set(position-1, p);
                            logger.info(this.getGame()+" Added missing player "+p+" at position "+position);
                            break;
                        }
                    }
                }
            }
        }

        // Fix positions for team mode
        if (this.getGame().getMode().equals(IConstants.Mode.TEAM2VS2)) {
            List <PlayerInGame> players = this.getGame().getPlayers();
            synchronized (this.playerPositions) {
                if (this.playerPositions.get(0).equals(players.get(0)) || this.playerPositions.get(0).equals(players.get(1))) {
                    // Team A wins
                    this.playerPositions.clear();
                    this.playerPositions.add(players.get(0));
                    this.playerPositions.add(players.get(1));
                    this.playerPositions.add(players.get(2));
                    this.playerPositions.add(players.get(3));
                } else {
                    // Team B wins
                    this.playerPositions.clear();
                    this.playerPositions.add(players.get(2));
                    this.playerPositions.add(players.get(3));
                    this.playerPositions.add(players.get(0));
                    this.playerPositions.add(players.get(1));
                }
            }
            logger.info(this.getGame()+" Set 'Team 2vs2' positions ("+this.playerPositions.size()+" players)");
        }

        this.ended = true;
        this.tickThread.terminate();
        long endDate = System.currentTimeMillis() / 1000;
        if (endDate - startDate > 60) {
            String positionLog = "";
            synchronized (this.playerPositions) {
                for (int i=0; i<this.playerPositions.size(); i++) {
                    if (!positionLog.equals("")) {
                        positionLog += ", ";
                    }
                    positionLog += ""+this.playerPositions.get(i)+" is #"+(i+1);
                }
                logger.info("Saving scores: "+positionLog);
                HighscoreService.createHighscoreEntry(this.playerPositions, this.getGame());
            }
        } else {
            logger.info("Not saving scores, duration of game " + this.getGame() + " was too short ("+(endDate - startDate)+" seconds)");
        }
        // Save Game in DB (player locations, player positions, ...)
        synchronized (this.playerPositions) {
            this.getGame().saveToJournal(this.getGame().getPlayers(), this.playerPositions, startDate, endDate);
        }
    }

    public void processIntegrity(int clientId) {
        List<PlayerInGame> pl = this.getGame().getPlayers();
        for (PlayerInGame p : pl) {
            if (p.getClient().getClientID() == clientId) {
                logger.warn("Integrity missmatch: " + p.getClient().getPlayerModel().getName());
                p.anticheat_kickAndBan(this);
            }
        }
    }
}

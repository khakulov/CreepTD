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

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.creeptd.common.Constants;
import com.creeptd.common.messages.client.AsyncronousMessage;
import com.creeptd.common.messages.client.BuildCreepMessage;
import com.creeptd.common.messages.client.BuildTowerMessage;
import com.creeptd.common.messages.client.ChangeStrategyMessage;
import com.creeptd.common.messages.client.ExitGameMessage;
import com.creeptd.common.messages.client.GameMessage;
import com.creeptd.common.messages.client.GameOverMessage;
import com.creeptd.common.messages.client.CreepEscapedMessage;
import com.creeptd.common.messages.client.LogoutMessage;
import com.creeptd.common.messages.client.SellTowerMessage;
import com.creeptd.common.messages.client.ClientChatMessage;
import com.creeptd.common.messages.client.UpgradeTowerMessage;
import com.creeptd.common.messages.server.BuildCreepRoundMessage;
import com.creeptd.common.messages.server.BuildTowerRoundMessage;
import com.creeptd.common.messages.server.ChangeStrategyRoundMessage;
import com.creeptd.common.messages.server.PlayerGameOverMessage;
import com.creeptd.common.messages.server.PlayerLosesLifeMessage;
import com.creeptd.common.messages.server.PlayerQuitMessage;
import com.creeptd.common.messages.server.RoundMessage;
import com.creeptd.common.messages.server.SellTowerRoundMessage;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.common.messages.server.TransferCreepMessage;
import com.creeptd.common.messages.server.UpgradeTowerRoundMessage;
import com.creeptd.server.HighscoreService;
import com.creeptd.server.Server;
import com.creeptd.server.game.Game;
import com.creeptd.server.game.PlayerInGame;
import com.creeptd.server.game.TickThread;
import com.creeptd.server.game.modes.GameMode;
import com.creeptd.server.game.modes.Team2vs2GameMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * GameState for a game that's running (has started).
 */
public class RunningGameState extends AbstractGameState implements
        TickThread.TickReceiver {

    private int nextTowerId;
    private long maxTick;
    private long roundId = 0;
    private final ArrayList<PlayerInGame> playerPositions = new ArrayList<PlayerInGame>();
    private long startDate;
    TickThread tickThread;
    private static Logger logger = Logger.getLogger(RunningGameState.class);
    private List<List> incomeLog = new LinkedList<List>(); // Round1(Player1 inc, ...), Round2(Player1 inc, ...)
    private Integer currentCreepId = 1;
    private final Object currentCreepIdLock = new Object();
    private final Map<String, Integer> lifeTakenVoting = new HashMap<String, Integer>();
    private GameMode gameMode;

    /**
     * Constructor.
     *
     * @param game
     *            the game. Must not be null. Must not be null or empty.
     */
    public RunningGameState(Game game) {
        super(game);
        this.gameMode = GameMode.forGame(game);
        this.nextTowerId = 1;
        this.maxTick = 0;
        List<PlayerInGame> players = this.getGame().getPlayers();
        Iterator<PlayerInGame> i = players.iterator();
        while (i.hasNext()) {
            i.next();
            synchronized (this.playerPositions) {
                this.playerPositions.add(null);
            }
        }
        logger.info(game + " Initialized with " + this.playerPositions.size() + " players");
        this.startDate = System.currentTimeMillis() / 1000;
        tickThread = new TickThread(this, Constants.TICK_MS * 1000000);
    }
    private boolean gameStarted = false;

    /**
     * Advance the maxTick counter and send a "ROUND n OK" message to all
     * players.
     */
    public void tick() {
        roundId = this.maxTick + Constants.USER_ACTION_DELAY;

        // First tick, roundId = 50
        if (this.maxTick == 0) {
            RoundMessage message = new RoundMessage();
            message.setRoundId(roundId);
            this.getGame().sendAll(message);
        }

        this.maxTick += 1;

        if ((this.maxTick < Constants.USER_ACTION_DELAY * 10) && (this.maxTick % Constants.USER_ACTION_DELAY == 0)) {
            RoundMessage message = new RoundMessage();
            message.setRoundId(roundId);
            this.getGame().sendAll(message);
        }

        if (this.maxTick % (Constants.USER_ACTION_DELAY * 10) == 0) {
            RoundMessage message = new RoundMessage();
            message.setRoundId(this.maxTick + (Constants.USER_ACTION_DELAY * 10));
            this.getGame().sendAll(message);
        }

        this.gameMode.onTick(this.maxTick); // Call the listener

        if (this.maxTick % (Constants.INCOME_TIME / Constants.TICK_MS) == 0) {
            List<Integer> incomeLogEntry = new LinkedList<Integer>();
            for (PlayerInGame p : this.getGame().getPlayers()) {
                if (p.isConnected()) {
                    p.anticheat_updateMoney(this.maxTick);
                    if (p.anticheat_getCurrentMoney() < 0) {
                        processIntegrity(p.getClient().getId());
                        incomeLogEntry.add(p.anticheat_getCurrentIncome());
                    }
                }
            }
            incomeLog.add(incomeLogEntry);
            if (!gameStarted) {
                gameStarted = true;
                this.gameMode.onGameStart();
            }
            this.gameMode.onNewIncome();
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
        } else if (message instanceof CreepEscapedMessage) {
            return this.handle((CreepEscapedMessage) message, sender);
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
        } else if (message instanceof AsyncronousMessage) {
            this.handle((AsyncronousMessage) message, sender);
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
        csm.setRoundId(this.maxTick + Constants.USER_ACTION_DELAY);
        csm.setPlayerId(message.getClientId());
        csm.setTowerId(towerId);
        csm.setStrategyType(message.getStrategyType());
        csm.setLocked(message.isLocked());
        this.getGame().sendAll(csm);

    }

    private AbstractGameState handle(CreepEscapedMessage m, PlayerInGame messageSender) {
        PlayerInGame from = this.getGame().findPlayer(m.getFromPlayerId());
        PlayerInGame creator = this.getGame().findPlayer(m.getCreatorId());

        // Check message format
        if (from == null || creator == null) {
            logger.warn("Got a malformed LifeTakenMessage sent by " + messageSender + ": from=" + from + ", creator=" + creator);
            return this;
        }
        // Transfer creep only if the voting (50%+ rule) is successful
        if (voteCreepEscaped(m.getCreepId(), m.getTransferCount())) {
            // Close the voting on success
            closeCreepEscapedVote(m.getCreepId(), m.getTransferCount());

            // Send player loses life message only, if there are lifes left
            if (from.getLifes() > 0) {
                // Take the life
                from.takeLife();
                from.anticheat_transferThisCreep(m.getCreepType(), m.getRoundId());
                creator.increaseTakenLifes();

                this.gameMode.onLifeTaken(from); // Call the event

                // Send a message about the lost life to all connected clients
                PlayerLosesLifeMessage pllm = new PlayerLosesLifeMessage();
                pllm.setRoundId(this.getRoundId());
                pllm.setPlayerId(from.getClient().getId());
                pllm.setCreatorId(creator.getClient().getId());
                pllm.setCreepType(m.getCreepType());
                pllm.setCreepId(m.getCreepId());
                pllm.setLifes(from.getLifes());
                this.getGame().sendAll(pllm);

                // If player is dead, sent game over message to all clients
                if (this.gameMode.isDead(from) && !from.isGameOver()) {
                    this.playerGameOver(from, 0); // Automatic sort
                    if (this.gameOverForAll()) {
                        this.endGame();
                        return new EndedGameState(this.getGame());
                    }
                }
            }

            // Find the transfer contexts and send the transfer messages
            List<PlayerInGame> transfers = this.gameMode.findTransfers(creator, from);
            for (PlayerInGame p : transfers) {
                p.anticheat_receivedThisCreep(m.getCreepType(), m.getRoundId());
                TransferCreepMessage tcm = new TransferCreepMessage();
                tcm.setRoundId(this.getRoundId());
                tcm.setPlayerId(p.getClient().getId());
                tcm.setFromId(from.getClient().getId());
                tcm.setCreatorId(creator.getClient().getId());
                tcm.setCreepId(m.getCreepId());
                tcm.setCreepType(m.getCreepType());
                tcm.setCreepHealth(m.getCreepHealth());
                tcm.setTransferCount(m.getTransferCount()+1);
                this.getGame().sendAll(tcm);
            }
        }
        return this;
    }

    /**
     * Vote for taking a life from a player.
     *
     * @param creepId The creep comming through
     * @param transferCount The transfer count for this creep
     * @return true on voting success, else false
     */
    private boolean voteCreepEscaped(int creepId, int transferCount) {
        String key = creepId+":"+transferCount;
        synchronized (this.lifeTakenVoting) {
            Integer count = this.lifeTakenVoting.get(key);
            if (count == null) {
                count = 0;
            } else if (count == -1) {
                return false; // Voting has already ended
            }
            count++;
            if (count > (int) (this.getGame().numConnectedPlayers() / 2)) { // 50%+ voting
                this.lifeTakenVoting.put(key, new Integer(-1));
                return true;
            } else {
                this.lifeTakenVoting.put(key, count);
                return false;
            }
        }
    }

    /**
     * Close voting.
     *
     * All entries, that are left at the end and are not set to -1 (closed),
     * give us a decent hint about the asynchronity of the game.
     *
     * @param creepId The creep comming through
     * @param transferCount The transfer count of this creep
     */
    private void closeCreepEscapedVote(int creepId, int transferCount) {
        String key = creepId+":"+transferCount;
        synchronized (this.lifeTakenVoting) {
            this.lifeTakenVoting.put(key, new Integer(-1));
        }
    }

    /**
     * Handles the BuildTowerMessage.
     *
     * @param m The message
     */
    private void handle(BuildTowerMessage m) {
        BuildTowerRoundMessage n = new BuildTowerRoundMessage();
        n.setRoundId(this.getRoundId());
        n.setPlayerId(m.getClientId());
        n.setTowerType(m.getTowerType());
        n.setTowerPosition(m.getPosition());
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
        if (towerId <= 0) {
            logger.error("Invalid tower id (tried to upgrade tower " + towerId + ")");
            return;
        } else if (towerId >= this.nextTowerId) {
            logger.error("No such tower (tried to upgrade tower " + towerId + ")");
            return;
        }

        UpgradeTowerRoundMessage n = new UpgradeTowerRoundMessage();
        n.setRoundId(this.getRoundId());
        n.setPlayerId(m.getClientId());
        n.setTowerId(towerId);
        this.getGame().sendAll(n);
        //sender.anticheat_TowerUpgraded(towerId, m.getRoundId());

    }

    /**
     * Handles the SellTowerMessage.
     *
     * @param m The message
     * @param sender The player who sent the message
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
        n.setRoundId(this.getRoundId());
        n.setPlayerId(m.getClientId());
        n.setTowerId(towerId);
        this.getGame().sendAll(n);

        //sender.anticheat_TowerSold(m.getTowerId(), m.getRoundId());
    }

    /**
     * Get current round id.
     *
     * @return The current round id
     */
    private long getRoundId() {
        return this.maxTick + Constants.USER_ACTION_DELAY;
    }

    /**
     * Handles the BuildCreepMessage.
     *
     * @param m
     *            the message
     */
    private void handle(BuildCreepMessage m) {
        PlayerInGame sender = this.getGame().findPlayer(m.getClientId());
        sender.anticheat_sentThisCreep(m.getCreepType(), this.getRoundId());

        List<PlayerInGame> receivers = this.gameMode.findReceivers(sender);
        for (PlayerInGame p : receivers) {
            BuildCreepRoundMessage bcrm = new BuildCreepRoundMessage();
            synchronized (this.currentCreepIdLock) {
                bcrm.setCreepId(this.currentCreepId);
                this.currentCreepId++;
            }
            bcrm.setRoundId(this.getRoundId());
            bcrm.setCreepType(m.getCreepType());
            bcrm.setSenderId(m.getClientId());
            bcrm.setPlayerId(p.getClient().getId());
            this.getGame().sendAll(bcrm);
            p.anticheat_receivedThisCreep(m.getCreepType(), m.getRoundId());
        }
    }

    /**
     * Handles the GameOverMessage.
     *
     * @param m the message
     * @param sender the player who sent the message.
     * @return the new state.
     */
    private AbstractGameState handle(GameOverMessage m, PlayerInGame sender) {
        // The game over message sent by the client is useful for us only when
        // clients decide to be game over, before the server side calculation
        // decides so. There should be no use case currently.
        /* if (!this.gameMode.isDead(sender)) {
            logger.info("Player " + sender + " sent game over, position=" + m.getPosition());
            this.playerGameOver(sender, 0); // Automatic sort, don't trust clients
            if (this.gameOverForAll()) {
                this.endGame();
                return new EndedGameState(this.getGame());
            }
        } */
        return this;
    }

    /**
     * Handle async message.
     *
     * @param message The message
     * @param sender The message's sender being async
     */
    private void handle(AsyncronousMessage message, PlayerInGame sender) {
        if (!sender.isAsynchronous()) {
            sender.setAsynchronous(true);
            ServerChatMessage scm = new ServerChatMessage();
            scm.setPlayerName(sender.getClient().getPlayerName());
            scm.setMessage("is asynchronous...");
            scm.setTranslate(true);
            this.getGame().sendAll(scm);
            logger.info(this.getGame() + ": Player " + sender + " is asynchronous (round=" + message.getCurrentRoundId() + ", received=" + message.getReceivedRoundId() + ")");
        }
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
        this.getGame().sendAll(new PlayerQuitMessage(player.getClient().getPlayerModel().getName(), "", player.getClient().getId()));
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
        if (player.isGameOver()) {
            return;
        }
        int position = 0;
        synchronized (this.playerPositions) {
            if (!this.playerPositions.contains(player)) {
                if (submitted_position > 0 && submitted_position <= this.getGame().getMaxPlayers() && this.playerPositions.get(submitted_position - 1) == null) {
                    position = submitted_position;
                    this.playerPositions.set(position - 1, player);
                    logger.info(this.getGame() + " Setting player " + player + " to position " + position + " (Submitted by player)");
                } else if (this.getGame().getMode().equals(Constants.Mode.TEAM2VS2) && submitted_position == 2 &&
                        this.playerPositions.get(1) != null && this.playerPositions.get(0) == null) {
                    this.playerPositions.set(0, player);
                    logger.info(this.getGame() + " Setting player " + player + " to position 1 (Team 2vs2 over)");
                } else {
                    for (position = this.getGame().getMaxPlayers(); position >= 1; position--) {
                        if (this.playerPositions.get(position - 1) == null) {
                            this.playerPositions.set(position - 1, player);
                            logger.info(this.getGame() + " Setting player " + player + " to position " + position + " (Automatic sort)");
                            break;
                        }
                    }
                }
            }
            position = this.playerPositions.indexOf(player) + 1;
            logger.info("Game over for " + player + " (position: #" + position + ")");
        }
        player.setGameOver(position);
        PlayerGameOverMessage pgom = new PlayerGameOverMessage();
        pgom.setRoundId(this.getRoundId());
        pgom.setPlayerId(player.getClient().getId());
        pgom.setWinner(this.gameMode.isWinner(player));
        this.getGame().sendAll(pgom);
        this.gameMode.onPlayerGameOver(player); // Call the event
    }

    private boolean gameOverForAll() {
        boolean gameOver = this.gameMode.isGameOver();
        if (gameOver) {
            for (PlayerInGame p : this.getGame().getPlayers()) {
                if (!p.isGameOver()) {
                    playerGameOver(p, 0); // Automatic sort
                }
            }
            return true;
        }
        return false;
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
                    for (int position = this.getGame().getMaxPlayers(); position >= 1; position--) {
                        if (this.playerPositions.get(position - 1) == null) {
                            this.playerPositions.set(position - 1, p);
                            logger.info(this.getGame() + " Added missing player " + p + " at position " + position);
                            break;
                        }
                    }
                }
            }
        }

        // Fix positions for team mode
        if (this.gameMode instanceof Team2vs2GameMode) {
            List<PlayerInGame> players = this.getGame().getPlayers();
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
            logger.info(this.getGame() + " Set 'Team 2vs2' positions (" + this.playerPositions.size() + " players)");
        }

        // Update winning order to clients
        for (PlayerInGame p : this.getGame().getPlayers()) {
            PlayerGameOverMessage pgom = new PlayerGameOverMessage();
            pgom.setPlayerId(p.getClient().getId());
            pgom.setRoundId(this.getRoundId());
            pgom.setWinner(this.gameMode.isWinner(p));
            this.getGame().sendAll(pgom);
        }

        this.ended = true;
        this.tickThread.terminate();

        this.gameMode.onGameOver(); // Call the event

        if (!Server.isLANVersion()) { // No saving for LAN games
            long endDate = System.currentTimeMillis() / 1000;
            if (endDate - startDate > 60) {
                String positionLog = "";
                synchronized (this.playerPositions) {
                    for (int i = 0; i < this.playerPositions.size(); i++) {
                        if (!positionLog.equals("")) {
                            positionLog += ", ";
                        }
                        positionLog += "" + this.playerPositions.get(i) + " is #" + (i + 1);
                    }
                    logger.info("Saving scores: " + positionLog);
                    HighscoreService.createHighscoreEntry(this.playerPositions, this.getGame());
                }
            } else {
                logger.info("Not saving scores, duration of game " + this.getGame() + " was too short (" + (endDate - startDate) + " seconds)");
            }
            // Save Game in DB (player locations, player positions, ...)
            synchronized (this.playerPositions) {
                this.getGame().saveToJournal(this.getGame().getPlayers(), this.playerPositions, startDate, endDate);
            }
        }
    }

    public void processIntegrity(int clientId) {
        List<PlayerInGame> pl = this.getGame().getPlayers();
        for (PlayerInGame p : pl) {
            if (p.getClient().getId() == clientId) {
                logger.warn("Integrity missmatch: " + p.getClient().getPlayerModel().getName());
                p.anticheat_kickAndBan(this);
            }
        }
    }
}

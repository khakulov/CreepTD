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
package com.creeptd.client.game;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.creeptd.client.network.MessageListener;
import com.creeptd.client.network.Network;
import com.creeptd.client.panel.GamePanel;
import com.creeptd.client.sound.SoundManagement;
import com.creeptd.client.tower.Tower;
import com.creeptd.common.Constants;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.common.messages.server.RoundMessage;
import com.creeptd.common.messages.server.ServerMessage;
import com.creeptd.common.messages.server.StartGameMessage;

import static com.creeptd.client.i18n.Translator.*;

/**
 * Main GameLoop for updates and repaint. Sync with Server tick and framerate
 * approximation.
 */
public class GameLoop extends Thread implements MessageListener, Constants {
    private static final int NO_DELAYS_PER_YIELD = 10;
    private static final int MAX_FRAME_SKIPS = 5;

    /** Logging functionality */
    private static Logger logger = Logger.getLogger(GameLoop.class.getName());
    /** The context of the local player */
    private GameContext myContext;
    /** List of all contexts */
    private final ArrayList<GameContext> contexts = new ArrayList<GameContext>();
    /** Map of playerId => position */
    private TreeMap<Integer, Integer> playersOrder = new TreeMap<Integer, Integer>();
    /** Map if playerId => name */
    private TreeMap<Integer, String> players = new TreeMap<Integer, String>();
    /** The underlying network */
    private Network network;
    /** The game panel */
    private GamePanel gamePanel;
    /** The played map */
    private Map map;
    /** The game mode */
    private Constants.Mode gameMode = null;
    /** Running flag */
    private boolean running = false;
    /** Current round id */
    private long roundId = 0;
    /** The maximum round id received from the server */
    private long maxRoundId = 0;
    /** Income counter */
    private int incomeCounter = 0;
    /** Flag if game is over or not */
    private boolean gameOver;
    /** The underlying sound management or null, if none */
    private SoundManagement soundManagement = null;
    /** Asynchronous flag */
    private boolean asynchronous = false;

    /**
     * Create the GameLoop.
     *
     * @param gamePanel The game panel
     * @param network The network instance
     * @param soundManagement The sound management, if any
     */
    public GameLoop(GamePanel gamePanel, Network network, SoundManagement soundManagement) {
        this.gamePanel = gamePanel;
        this.network = network;
        this.network.addListener(this);
        this.soundManagement = soundManagement;
        this.network.getQueue().clear(); // Clear the queue, discard previous messages
        this.network.makeContact();
        GameContext.setPositionCounter(0);
    }

    /**
     * Find the context for a given player id.
     * 
     * @param playerId
     * @return
     */
    public GameContext findContextByPlayerId(int playerId) {
        synchronized (contexts) {
            for (GameContext gc : contexts) {
                if (gc.getPlayerId() == playerId) {
                    return gc;
                }
            }
        }
        return null;
    }

    /**
     * Get the underlying game's mode.
     *
     * @return The game mode
     */
    public Mode getGameMode() {
        return gameMode;
    }

    /**
     * Set the underlying game's mode.
     *
     * @param gameMode The game mode
     */
    public void setGameMode(Mode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * Check if the game is over.
     *
     * @return true if game is over, else false
     */
    public synchronized boolean isGameOver() {
        return gameOver;
    }

    /**
     * Set if the game is over or not.
     *
     * @param gameOver true if game over, else false
     */
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * Get underlying sound management.
     *
     * @return The soundmanagement or null, if none
     */
    public SoundManagement getSoundManagement() {
        return this.soundManagement;
    }

    /** Test if the loop is asynchronous.
     *
     * @return true if asynchronous, else false
     */
    public boolean isAsynchronous() {
        return asynchronous;
    }

    /**
     * Set the asynchronous flag.
     *
     * @param asynchronous true for asynchronous, else false
     */
    public void setAsynchronous(boolean asynchronous) {
        this.asynchronous = asynchronous;
    }



    /**
     * Initialize the game mode.
     */
    public void init() {
        logger.info("GameLoop init...");
        contextSetup();
        gamePanel.setSoundManagementObject(soundManagement);
        gamePanel.getCreepPanel().setContext(myContext);
        gamePanel.getChatPanel().setContext(myContext);
        gamePanel.getTowerPanel().setContext(myContext);
        gamePanel.getBuildTowerInfoPanel().setContext(myContext);
        gamePanel.getSelectTowerInfoPanel().setContext(myContext);
        gamePanel.getCreepInfoPanel().setContext(myContext);
        gamePanel.getNoInfoPanel().setContext(myContext);
        gamePanel.setContext(myContext);
    }

    /**
     * Internal run method.
     */
    @Override
    public void run() {
        logger.info("GameLoop running...");

        init();
        running = true;
        gamePanel.getQuit().setEnabled(true);

        long afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        long period = TICK_MS * 1000000;
        long beforeTime = System.nanoTime();
        long excess = 0L;
        int noDelays = 0;
        long maxRound_old = 0;

        // The gameloop
        while (running) {
            if (roundId > maxRoundId) {
                Thread.yield();
                continue;
            }
            if (maxRound_old != maxRoundId) {
                if ((roundId + 1) < maxRound_old) {
                    int skips = 0;
                    while (((roundId + 1) < maxRound_old) && (skips < MAX_FRAME_SKIPS)) {
                        gameUpdate();
                        roundId++;
                        skips++;
                    }
                    gameUpdate(); // updates the gamestate
                    gameRender(); // paints new screen in a buffer
                    gamePanel.getBoardPanel().getStrategy().show(); // draw buffer to screen
                    roundId++;
                    continue;
                }
                maxRound_old = maxRoundId;
            }
            gameUpdate(); // updates the gamestate
            gameRender(); // paints new screen in a buffer
            gamePanel.getBoardPanel().getStrategy().show(); // draw buffer to screen
            roundId++;

            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;

            if (sleepTime > 0) { // some time left in this cycle
                try {
                    Thread.sleep(sleepTime / 1000000L); // nano -> ms
                } catch (InterruptedException ex) {
                }
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            } else { // sleepTime <= 0; frame took longer than the period
                excess -= sleepTime; // store excess time value
                overSleepTime = 0L;

                if (++noDelays >= NO_DELAYS_PER_YIELD) {
                    Thread.yield(); // give another thread a chance to run
                    noDelays = 0;
                }
            }

            beforeTime = System.nanoTime();

            /*
             * If frame animation is taking too long, update the game state
             * without rendering it, to get the updates/sec nearer to the
             * required FPS.
             */
            int skips = 0;
            while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
                excess -= period;
                gameUpdate(); // update state but don't render
                roundId++;
                skips++;
            }
        }
    }

    /**
     * Set up the players' contexts.
     */
    private void contextSetup() {
        logger.info("Creating contexts...");
        int cnt = 0;
        GameContext.BoardLocation loc = null;
        GameContext context = null;
        GameContext.setPositionCounter(players.size());
        for (Integer id : players.keySet()) {
            int position = playersOrder.get(id);
            switch (position) {
                case 0:
                    loc = GameContext.BoardLocation.TOPLEFT;
                    break;
                case 1:
                    loc = GameContext.BoardLocation.TOPRIGHT;
                    break;
                case 2:
                    loc = GameContext.BoardLocation.BOTTOMRIGHT;
                    break;
                case 3:
                    loc = GameContext.BoardLocation.BOTTOMLEFT;
                    break;
                default:
                    logger.warning("Creating context without location");
            }
            cnt++;
            if (id.equals(gamePanel.getCore().getPlayerId())) {
                logger.info("Own context (id=" + id + ") created");
                context = new PlayerContext(loc, network, soundManagement, map, this, id, players.get(id));
                gamePanel.getBoardPanel().addMouseMotionListener(context.getGameBoard());
                gamePanel.getBoardPanel().addMouseListener(context.getGameBoard());
                gamePanel.getGameInfoPanel().addPlayerContext((PlayerContext) context);
                myContext = context;
            } else {
                logger.info("Opponent context (id=" + id + ") created");
                context = new OpponentContext(loc, network, map, this, id, players.get(id));
                gamePanel.getGameInfoPanel().addOpponentContext((OpponentContext) context);
            }
            contexts.add(context);

            context.fireCreditsChangedEvent();
            context.fireIncomeChangedEvent();
            context.fireLifesChangedEvent();
        }
    }

    /**
     * Receive messages from the server.
     *
     * @param m The ServerMessage
     */
    public void update(ServerMessage m) {
        if (m instanceof StartGameMessage) {
            if (!this.isAlive()) {
                this.start();
            }
        } else if (m instanceof ServerChatMessage) {
            ServerChatMessage scm = (ServerChatMessage) m;
            String msg = scm.getMessage();
            if (scm.getTranslate()) {
                msg = _(msg);
            }
            gamePanel.getChatPanel().setMessage(scm.getPlayerName(), msg);
        }
        if (m instanceof RoundMessage) {
            maxRoundId = ((RoundMessage) m).getRoundId();
        }
    }

    /**
     * Updates the income every INCOME_TIME.
     */
    private synchronized void updateIncome() {

        for (GameContext gc : contexts) {
            gc.setCredits(gc.getCredits() + gc.getIncome());
        }
        this.soundManagement.cashSound();

        logger.info("new income");
    }

    /**
     * Updates the internal game state.
     */
    private synchronized void gameUpdate() {
        if (gameOver) {
            return;
        }
        int deadCount = 0;
        for (GameContext gc : contexts) {
            gc.update(roundId);
            if (gc.isDead()) {
                deadCount++;

                // ?
                for (Tower t : gc.getTowers()) {
                    t.setCoolDownNow(t.getCoolDown() - 1);
                }
            }
        }

        // Check if the game is over
        if (deadCount == this.getPlayers().size()) {
            logger.info("The game is over");

            // Stop the thread
            this.running = false;
        }

        // Count down start counter
        if (myContext.getStartCounter() >= 0) {
            if (roundId % (1000 / TICK_MS) == 0) {
                gameRender();
                gamePanel.getBoardPanel().getStrategy().show();
                myContext.setStartCounter(myContext.getStartCounter() - 1);
            }
        }
        // New round begins
        if (myContext.getStartCounter() < 0) {
            // Update income if player is alive
            if (!myContext.isDead()) {
                // updates income every INCOME_TIME ms
                if (roundId % (INCOME_TIME / TICK_MS) == 0) {
                    updateIncome();
                }
                // countdown for next income
                if (roundId % (1000 / TICK_MS) == 0) {
                    if (--incomeCounter < 1) {
                        // milis>>secs
                        incomeCounter = Constants.INCOME_TIME / 1000;
                    }
                    gamePanel.getGameInfoPanel().setIncomeCounter(incomeCounter);
                }
            }
        }
    }

    /**
     * Prepares the game for painting.
     */
    private void gameRender() {
        Graphics2D g2 = gamePanel.getBoardPanel().getImgGraphics();
        for (GameContext gc : contexts) {
            gc.paint(g2);
        }
    }

    /**
     * Get players in game.
     *
     * @return The players
     */
    public TreeMap<Integer, String> getPlayers() {
        return players;
    }

    /**
     * Set the players of this game.
     *
     * @param The players to set
     */
    public void setPlayers(TreeMap<Integer, String> players) {
        this.players = players;
    }

    /**
     * Get the played map.
     *
     * @return The map
     */
    public Map getMap() {
        return map;
    }

    /**
     * Set the player map.
     *
     * @param map The map to set
     */
    public void setMap(Map map) {
        this.map = map;
    }

    /**
     * Test if the loop is running.
     *
     * @return true if the loop is running, else false
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Terminate the game loop.
     */
    public void terminate() {
        this.running = false;
    }

    /**
     * Get the players' order.
     *
     * playerid => position.
     *
     * @return The players' order
     */
    public TreeMap<Integer, Integer> getPlayersOrder() {
        return playersOrder;
    }

    /**
     * Set the player's order.
     *
     * @param playersOrder The players' order to set
     */
    public void setPlayersOrder(TreeMap<Integer, Integer> playersOrder) {
        this.playersOrder = playersOrder;
    }

    /**
     * Get the current round id.
     *
     * @return The current round's id
     */
    public long getRoundId() {
        return roundId;
    }
}

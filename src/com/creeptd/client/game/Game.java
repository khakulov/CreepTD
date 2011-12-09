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
import java.util.Deque;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.creeptd.client.Core;
import com.creeptd.client.network.MessageListener;
import com.creeptd.client.panel.game.RunningGameScreen;
import com.creeptd.common.Constants;
import com.creeptd.common.messages.server.GameMessage;
import com.creeptd.common.messages.server.PausedMessage;
import com.creeptd.common.messages.server.RoundMessage;
import com.creeptd.common.messages.server.ServerMessage;
import com.creeptd.common.messages.server.StartGameMessage;

/**
 * Main GameLoop for updates and repaint. Sync with Server tick and framerate
 * approximation.
 */
public class Game implements MessageListener {

    /** Logging functionality */
    private static Logger logger = Logger.getLogger(Game.class.getName());
    /** List of all contexts */
    private final ArrayList<GameContext> contexts = new ArrayList<GameContext>();
    /** List of empty game contexts */
    private final ArrayList<EmptyContext> emptyContexts = new ArrayList<EmptyContext>();
    /** Map of playerId => position */
    private TreeMap<Integer, Integer> playersOrder = new TreeMap<Integer, Integer>();
    /** Map of playerId => name */
    private TreeMap<Integer, String> players = new TreeMap<Integer, String>();
    /** The game panel */
    private RunningGameScreen gamePanel;
    /** The played map */
    private Constants.Map map;
    /** The game mode */
    private Constants.Mode gameMode = null;
    /** Running flag */
    private boolean running = false;
    /** Paused flag */
    private boolean paused = false;
    /** Current round id */
    private long roundId = 0;
    /** The current income round */
    private int incomeRound = 0;
    
    private GameLoop gameLoop;
    
    private Deque<ServerMessage> history = new LinkedList<ServerMessage>();
    
    private int speed = Constants.TICK_MS;

    /**
     * Create the GameLoop.
     *
     * @param gamePanel The game panel
     * @param soundManagement The sound management, if any
     */
    public Game(RunningGameScreen gamePanel) {
        this.gamePanel = gamePanel;
        this.gameLoop = new GameLoop(this);
        GameContext.setPositionCounter(0);
    }

	public void start() {
        Core.getInstance().getNetwork().addListener(this);
        this.contextSetup();
		this.gameLoop.start();
		this.running = true;
	}

	public void stop() {
		this.running = false;
		this.gameLoop.terminate();
        Core.getInstance().getNetwork().removeListener(this);
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
     * Set up the players' contexts.
     */
    private void contextSetup() {
        logger.info("Creating contexts...");
        BoardLocation loc = null;
        GameContext context = null;
        GameContext.setPositionCounter(players.size());
        for (Integer id : players.keySet()) {
            if (players.size() == 1) {
                loc = BoardLocation.CENTER; // Draw solo context to center
            } else {
                int position = playersOrder.get(id);
                loc = BoardLocation.forIndex(position);
                if (loc == null) {
                    logger.warning("Creating context without location");
                }
            }
            logger.info("Game context (id=" + id + ") created at "+loc);
            context = new GameContext(this, loc, id, players.get(id));
            contexts.add(context);
        }
        if (players.size() > 1) {
            for (int position=0; position < 4; position++) {
                if (!this.playersOrder.containsValue(position)) {
                    EmptyContext ec = new EmptyContext(BoardLocation.forIndex(position), map);
                    this.emptyContexts.add(ec);
                }
            }
        }
    }

    /**
     * Receive messages from the server.
     *
     * @param m The ServerMessage
     */
    public void update(ServerMessage m) {
    	this.history.push(m);
        if (m instanceof StartGameMessage) {
            if (!this.running) {
                this.start();
            }
        } else if (m instanceof PausedMessage) {
            PausedMessage pm = (PausedMessage) m;
            this.paused = pm.isPaused();
            GameContext context = this.findContextByPlayerId(pm.getPlayerId());
            if (context != null) {
                context.onPaused(pm.isPaused());
            }
        } else if (m instanceof RoundMessage) {
            this.gameLoop.setMaxRound(((RoundMessage) m).getRoundId());
        } else if (m instanceof GameMessage) {
        	GameMessage gm = (GameMessage) m;

        	GameContext context = this.findContextByPlayerId(gm.getPlayerId());
        	assert context != null;
            context.addGameMessage(gm);
        }
    }

    /**
     * Updates the internal game state.
     */
    public void gameUpdate() {
        if (!this.running)
            return;
        
        this.roundId++;

        int deadCount = 0;
        for (GameContext gc : contexts) {
            gc.update(roundId);
            if (gc.isGameOver()) {
                deadCount++;
            }
        }

        // Check if the game is over
        if (deadCount == this.getPlayers().size()) {
            logger.info("The game is over");

            this.running = false; // Stop the thread
        }

        // Count down start counter
        if (this.roundId < 300) {
            if (this.roundId == 280) {
                Core.getInstance().getSoundManagement().startSound();
            } else if (this.roundId >= 220 && this.roundId % 20 == 0) {
            	Core.getInstance().getSoundManagement().countdownSound();
            }
        }

        if (this.roundId >= 300) {
        	// update round
            if (roundId % Constants.INCOME_TIME == 0) {
                this.incomeRound++;

                for (GameContext gc : contexts) {
                    if (!gc.isGameOver()) {
	                    gc.setCredits(gc.getCredits() + gc.getIncome());
                    }
                }
                Core.getInstance().getSoundManagement().cashSound();
                gamePanel.getGameInfoPanel().roundChanged(incomeRound);
                
                if (this.gameMode == Constants.Mode.ALLVSALL) {
                	AllvsAllChecker.garbageCollection(this, roundId - 2*Constants.USER_ACTION_DELAY);
                }
            }

            // countdown for next income
            if (roundId % 20 == 0) {
            	int incomeCounter = (int)((300 - (roundId % Constants.INCOME_TIME)) / 20);
                if (incomeCounter == 5) {
                    System.gc();
                }
                gamePanel.getGameInfoPanel().setIncomeCounter(incomeCounter);
            }
        }
    }

    /**
     * Prepares the game for painting.
     */
    public void gameRender() {
        Graphics2D g2 = gamePanel.getBoardPanel().getImgGraphics();
        if (g2 == null)
        	return;
        for (GameContext gc : contexts) {
            gc.paint(g2);
        }
        for (EmptyContext ec : emptyContexts) {
            ec.paint(g2);
        }
        this.gamePanel.getBoardPanel().getStrategy().show(); // draw buffer to screen
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
     * Get the played map.
     *
     * @return The map
     */
    public Constants.Map getMap() {
        return map;
    }

    /**
     * Set the player map.
     *
     * @param map The map to set
     */
    public void setMap(Constants.Map map) {
        this.map = map;
    }

    /**
     * Get the underlying game's mode.
     *
     * @return The game mode
     */
    public Constants.Mode getGameMode() {
        return gameMode;
    }

    /**
     * Set the underlying game's mode.
     *
     * @param gameMode The game mode
     */
    public void setGameMode(Constants.Mode gameMode) {
        this.gameMode = gameMode;
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

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public Deque<ServerMessage> getHistory() {
    	return this.history;
    }

	public long getRoundId() {
		return roundId;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String getWinner() {
		for (GameContext context : this.contexts) {
			if (context.isWinner())
				return context.getPlayerName();
		}
		return null;
	}
}

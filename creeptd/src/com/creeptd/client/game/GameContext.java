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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.creeptd.client.creep.Creep;
import com.creeptd.client.creep.CreepFactory;
import com.creeptd.client.grid.GameBoard;
import com.creeptd.client.grid.Grid;
import com.creeptd.client.network.Network;
import com.creeptd.client.sound.SoundManagement;
import com.creeptd.client.tower.AbstractTower;
import com.creeptd.client.tower.FindCreepStrategy;
import com.creeptd.client.tower.StrategyFactory;
import com.creeptd.client.tower.Tower;
import com.creeptd.client.tower.TowerFactory;
import com.creeptd.common.IConstants;
import com.creeptd.common.messages.client.BuildCreepMessage;
import com.creeptd.common.messages.client.ChangeStrategyMessage;
import com.creeptd.common.messages.client.ExitGameMessage;
import com.creeptd.common.messages.client.SellTowerMessage;
import com.creeptd.common.messages.client.SendMessageMessage;
import com.creeptd.common.messages.client.UpgradeTowerMessage;
import com.creeptd.common.messages.server.BuildCreepRoundMessage;
import com.creeptd.common.messages.server.BuildTowerRoundMessage;
import com.creeptd.common.messages.server.ChangeStrategyRoundMessage;
import com.creeptd.common.messages.server.GameMessage;
import com.creeptd.common.messages.server.SellTowerRoundMessage;
import com.creeptd.common.messages.server.UpgradeTowerRoundMessage;

/**
 * Abstract class representing the context for one player.
 * 
 * @author Philipp
 * 
 */
public abstract class GameContext {

    private static Logger logger = Logger.getLogger(GameContext.class.getName());

    /**
     * Enumeration for the four positions where a GameBoard can be.
     *
     * @author Philipp
     *
     */
    public static enum BoardLocation {

        TOPLEFT(30, 30, 320, 320), BOTTOMLEFT(30, 350, 320, 320), TOPRIGHT(350,
        30, 320, 320), BOTTOMRIGHT(350, 350, 320, 320);
        private final double x;
        private final double y;
        private final int width;
        private final int height;
        private final Rectangle bounds;

        /**
         * BoardLocation constructor.
         *
         * @param x
         *            the x position
         * @param y
         *            the y position
         * @param width
         *            the width
         * @param height
         *            the height
         */
        BoardLocation(double x, double y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.bounds = new Rectangle((int) x, (int) y, width, height);
        }

        /**
         * Getter for the x location.
         *
         * @return the x location
         */
        public double getX() {
            return this.x;
        }

        /**
         * Getter for the y location.
         *
         * @return the x location
         */
        public double getY() {
            return this.y;
        }

        /**
         * Getter for the width.
         *
         * @return the the width
         */
        public int getWidth() {
            return this.width;
        }

        /**
         * Getter for the the height.
         *
         * @return the the height
         */
        public int getHeight() {
            return this.height;
        }

        /**
         * Getter for the bounding box.
         *
         * @return a Rectangle specifying the bounding box
         */
        public Rectangle getBounds() {
            return bounds;
        }
    }
    private int playerId;
    private String playerName;
    private ArrayList<ContextListener> contextListeners;
    private List<Tower> towers;
    private List<Creep> creeps;
    private List<Creep> transfer; // creeps for context switch
    // private List<Integer> towerIDs; //builded tower IDs
    private int credits = IConstants.CREDITS;
    private String _credits = Integer.toBinaryString(IConstants.CREDITS);
    private int income = IConstants.START_INCOME;
    private String _income = Integer.toBinaryString(IConstants.START_INCOME);
    private int lives = IConstants.LIVES;
    private String _lives = Integer.toBinaryString(IConstants.LIVES);
    private GameBoard gameBoard;
    private BoardLocation location;
    private AffineTransform save = null;
    private AffineTransform translation = new AffineTransform();
    private Network network;
    private GameLoop gameLoop;
    private SoundManagement managementSound;
    private boolean deathsoundPlayed = false;
    // default map
    private IConstants.Map mapfile = IConstants.Map.REDWORLD;
    private IConstants.Towers nextTower = null;
    private Tower selectedTower = null;
    private static Integer winningPosition = 0;
    private boolean dead = false;
    private int startCounter = IConstants.INCOME_TIME / 1000;
    private long lastCreepSent = 0;
    private long lastWaveSent = 0;
    private long lastWaveDelay = 0;
    public int takedlives;

    /**
     * @return the startCounter
     */
    public int getStartCounter() {
        return startCounter;
    }

    /**
     * @param startCounter
     *            the startCounter to set
     */
    public void setStartCounter(int startCounter) {
        this.startCounter = startCounter;
    }

    /**
     * Implementation for default init.
     *
     * @param location
     *            the boardLocation (GameContext.BoardLocation)
     * @param network
     *            the current network connection object
     * @param mSound
     *            SoundManagement object for sound
     * @param map
     *            the map to play
     */
    public GameContext(BoardLocation location, Network network, SoundManagement mSound, IConstants.Map map, GameLoop gameLoop) {
        this.location = location;
        this.network = network;
        this.mapfile = map;
        this.gameLoop = gameLoop;
        this.setGameBoard(new GameBoard(this));
        this.towers = Collections.synchronizedList(new ArrayList<Tower>());
        this.creeps = Collections.synchronizedList(new ArrayList<Creep>());
        this.transfer = Collections.synchronizedList(new ArrayList<Creep>());
        this.contextListeners = new ArrayList<ContextListener>();
        this.managementSound = mSound;
        synchronized (GameContext.winningPosition) {
            GameContext.winningPosition++;
        }
    }

    /**
     * Creates a gameContext without sound.
     *
     * @param location
     *            the boardLocation (GameContext.BoardLocation)
     * @param network
     *            the current network connection object
     * @param map
     *            the map to play
     */
    public GameContext(BoardLocation location, Network network, IConstants.Map map, GameLoop gameLoop) {
        this.location = location;
        this.network = network;
        this.mapfile = map;
        this.gameLoop = gameLoop;
        this.setGameBoard(new GameBoard(this));
        this.towers = Collections.synchronizedList(new ArrayList<Tower>());
        this.creeps = Collections.synchronizedList(new ArrayList<Creep>());
        this.transfer = Collections.synchronizedList(new ArrayList<Creep>());
        this.contextListeners = new ArrayList<ContextListener>();
        this.managementSound = null;
        synchronized (GameContext.winningPosition) {
            GameContext.winningPosition++;
        }
    }

    /**
     * A hook for special pre-update work.
     *
     * @param roundID
     *            the current tick
     */
    public abstract void preUpdate(long roundID);

    /**
     * A hook for special post-update work.
     *
     * @param roundID
     *            the current tick
     */
    public abstract void postUpdate(long roundID);

    /**
     * Update the context by one tick.
     *
     * @param roundID
     *            the current tick
     */
    public void update(long roundID) {
        // if lives are 0, the player is dead!

        preUpdate(roundID);

        ArrayList<Tower> towersCopy = new ArrayList<Tower>(getTowers());
        ArrayList<Creep> creepsCopy = new ArrayList<Creep>(getCreeps());

        // update towers only if the player is not dead
        if (!this.isDead()) {
            for (Tower t : towersCopy) {
                t.update(roundID);
            }
        } else {
            if (!deathsoundPlayed) {
                if (managementSound != null) {
                    managementSound.playerLooseSound();
                }
                deathsoundPlayed = true;
            }
        }

        for (Creep c : creepsCopy) {
            c.update(roundID);
        }

        // has to happen after the creeps received their update, otherwise they
        // are duplicated...
        ArrayList<Creep> transferCopy = new ArrayList<Creep>(getTransfer());

        // remove creeps which are transferred
        for (Creep c : transferCopy) {
            getCreeps().remove(c);
        }
        postUpdate(roundID);

        // check all gamemessage and invoke actions...
        ArrayList<GameMessage> queueCopy = new ArrayList<GameMessage>(network.getQueue());

        for (GameMessage gm : queueCopy) {
            if (gm.getPlayerId() == this.playerId) {
                // take actions
                processMessage(gm, roundID);
                // remove from queue
                network.getQueue().remove(gm);
            }
        }
    }

    /**
     * Process a message from the server and invoke the action associated with
     * it.
     *
     * @param gm
     *            the server message
     * @param roundId
     *            the current round id
     */
    private void processMessage(GameMessage gm, long roundId) {

        int update_Round = (int) (gm.getRoundId() - roundId);
        if (update_Round <= 0) {
            logger.info("processMessage is out of sync!!!");
            if (managementSound != null) {
                managementSound.playerWonSound();
                managementSound.playerWonSound();
            }
        }

        if (gm instanceof BuildTowerRoundMessage) {
            BuildTowerRoundMessage btrm = (BuildTowerRoundMessage) gm;
            Grid grid = this.gameBoard.getGrid((int) btrm.getTowerPosition().getX(), (int) btrm.getTowerPosition().getY());

            if (grid.isFree()) {
                Tower t = TowerFactory.createTower(this, IConstants.Towers.valueOf(IConstants.Towers.class, btrm.getTowerType()),
                        grid);
                t.setBuilding(true);
                t.setBuildTime(update_Round);
                t.setId(btrm.getTowerId());
                this.addtower(t);

            }

        } else if (gm instanceof UpgradeTowerRoundMessage) {
            UpgradeTowerRoundMessage utrm = (UpgradeTowerRoundMessage) gm;
            Tower upgradeTower;
            // find the tower we want to upgrade
            for (Tower tower : towers) {
                if (tower.getId() == utrm.getTowerId()) {
                    upgradeTower = tower;
                    // set UpgradTime
                    upgradeTower.setUpgradeTime(update_Round, true);
                    upgradeTower.setUpgrading(true);
                    break;
                }
            }

        } else if (gm instanceof ChangeStrategyRoundMessage) {
            ChangeStrategyRoundMessage csm = (ChangeStrategyRoundMessage) gm;
            // find the tower we want to change
            for (Tower tower : towers) {
                if (tower.getId() == csm.getTowerId()) {
                    FindCreepStrategy fcs = StrategyFactory.getStrategyForName(
                            csm.getStrategyType(), tower);
                    fcs.setCreepLock(csm.isLocked());
                    ((AbstractTower) tower).setChangeStrategyTime(update_Round);
                    ((AbstractTower) tower).setSelectedStrategy(fcs);
                    break;
                }
            }

        } else if (gm instanceof SellTowerRoundMessage) {
            SellTowerRoundMessage strm = (SellTowerRoundMessage) gm;
            Tower sellTower;
            // find the tower we want to sell
            for (Tower tower : towers) {
                if (tower.getId() == strm.getTowerId() && tower.isReady()) {
                    sellTower = tower;
                    sellTower.setSellTime(update_Round);
                    sellTower.setSelling(true);
                    break;
                }
            }
        } else if (gm instanceof BuildCreepRoundMessage) {

            BuildCreepRoundMessage bcrm = (BuildCreepRoundMessage) gm;
            Creep c = CreepFactory.createCreep(this, IConstants.Creeps.valueOf(
                    IConstants.Creeps.class, bcrm.getCreepType()));
            c.setBuildTime(update_Round);
            c.setSenderId(bcrm.getSenderId());
            c.setPlayerID(bcrm.getPlayerId());

            if (!this.isDead()) { //  || this.getGameLoop().getGameMod() == 3
                this.getCreeps().add(c);
            } else {
                this.getTransfer().add(c);
            }

            // play sound now
            if (managementSound != null) {
                managementSound.creepStartsSound(c.getType());
            }
        }
    }

    /**
     * Paint the the context with its elements.
     *
     * @param g
     *            the graphics object
     */
    public void paint(Graphics2D g) {

        g.setFont(new Font("Verdana", Font.PLAIN, 10));

        save = g.getTransform();
        translation.setToIdentity();
        translation.translate(this.getLocation().getX(), this.getLocation().getY());
        g.transform(translation);

        this.getGameBoard().paint(g);

        for (Creep c : getCreeps()) {
            c.paint(g);
        }

        synchronized (towers) {
            for (Tower t : getTowers()) {
                t.paintEffect(g);
            }
        }

        if ((startCounter >= 0) && (this instanceof PlayerContext)) {
            if (startCounter > 8) {
                g.setColor(Color.GRAY);
                g.setFont(new Font("Verdana", Font.BOLD, 30));
                g.drawString("loading...", 100, 160);
            } else if (startCounter > 5) {
                g.setColor(Color.RED);
                g.setFont(new Font("Verdana", Font.BOLD, 20));
                g.drawString("Game starting in...", 70, 160);
            } else if (startCounter > 0) {
                g.setColor(Color.RED);
                g.setFont(new Font("Verdana", Font.BOLD, 200));
                g.drawString(String.valueOf(startCounter), 95, 230);
            } else {
                g.setColor(Color.RED);
                g.setFont(new Font("Verdana", Font.BOLD, 190));
                g.drawString("GO", 5, 230);
            }
        }

        paintPlayerInfo(g);

        boolean drawWinner = false;
        boolean drawGameover = false;
        boolean drawDead = false;

        // Team 2vs2 mode
        if (this.getGameLoop().getGameMod() == 3) {
            GameContext[] players = this.getGameLoop().getOrderedPlayers();

            // Check if Team A is dead
            if (players[0].isDead() && players[1].isDead()) {
                if (this.playerId != players[0].getPlayerId() && this.playerId != players[1].getPlayerId()) {
                    drawWinner = true; // If not in Team A, you are a winner
                } else {
                    drawGameover = true; // etc.
                }
                // Check if Team B is dead
            } else if (players[2].isDead() && players[3].isDead()) {
                if (this.playerId != players[2].getPlayerId() && this.playerId != players[3].getPlayerId()) {
                    drawWinner = true;
                } else {
                    drawGameover = true;
                }
            } else {
                if (this.isDead()) {
                    drawDead = true;
                }
            }

            // All other modes
        } else {
            synchronized (GameContext.winningPosition) {
                if (this.isDead()) {
                    drawGameover = true;
                } else if (!this.isDead() && GameContext.winningPosition <= 1) {
                    drawWinner = true;
                }
            }
        }

        // Draw winner/gameover
        if (drawWinner) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Verdana", Font.BOLD, 45));
            g.drawString("Winner", 80, 140);
            if (this instanceof PlayerContext) {
                g.setFont(new Font("Verdana", Font.BOLD, 15));
                g.drawString("Press ESC to leave", 90, 180);
            }
        } else if (drawGameover) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Verdana", Font.BOLD, 45));
            g.drawString("Game Over", 25, 140);
            if (this instanceof PlayerContext) {
                g.setFont(new Font("Verdana", Font.BOLD, 15));
                g.drawString("Sad but true", 100, 180);
            }
        } else if (drawDead) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Verdana", Font.BOLD, 45));
            g.drawString("Dead", 90, 140);
            if (this instanceof PlayerContext) {
                g.setFont(new Font("Verdana", Font.BOLD, 15));
                g.drawString("Hope for your mate", 90, 180);
            }
        }

        // Reset transformation
        g.setTransform(save);
    }

    /**
     * Method to paint the playernames and other things.
     *
     * @param g
     *            the graphics context
     */
    private void paintPlayerInfo(Graphics2D g) {
        StringBuffer sb = new StringBuffer();
        sb.append(this.playerName);
        sb.append(" | Lives left: ");
        sb.append(this.lives);

        g.setColor(Color.WHITE);
        if (this instanceof PlayerContext) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Verdana", Font.BOLD, 12));
        } else {
            g.setFont(new Font("Verdana", Font.PLAIN, 10));
        }
        switch (location) {
            case TOPLEFT:
                g.drawString(sb.toString(), 10, -10);
                break;
            case TOPRIGHT:
                g.drawString(sb.toString(), 10, -10);
                break;
            case BOTTOMLEFT:
                g.drawString(sb.toString(), 10, 335);
                break;
            case BOTTOMRIGHT:
                g.drawString(sb.toString(), 10, 335);
                break;
            default:
                break;
        }
    }

    public boolean readyForNewWave() {
        if (lastWaveSent + this.lastWaveDelay < System.currentTimeMillis()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean readyForNewCreep() {
        if (lastCreepSent + IConstants.CREEP_DELAY < System.nanoTime()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sendet Threadsafe neue Creep
     */
    public synchronized boolean sendCreep(IConstants.Creeps type) {
        if (this.getCredits() >= type.getPrice() && startCounter < 0 && !this.isDead()) {

            BuildCreepMessage bcm = new BuildCreepMessage();
            bcm.setClientId(this.getPlayerId());
            bcm.setCreepType(type.toString());
            bcm.setRoundId(this.gameLoop.getRoundID());
            this.network.sendMessage(bcm);
            this.setCredits(this.getCredits() - type.getPrice());
            this.setIncome(this.getIncome() + type.getIncome());
            lastCreepSent = System.nanoTime();

            return true;
        }
        return false;
    }

    /**
     * Sendet eine ganze Welle von Creeps
     *
     * @param context
     * @param gamepanel
     * @param type
     */
    public void sendCreepsWave(final IConstants.Creeps type) {

        if (this.getCredits() >= type.getPrice() && startCounter < 0 && !this.isDead()) {
            lastWaveSent = System.currentTimeMillis();
            final GameContext context = this;
            new Thread() {

                @Override
                public void run() {

                    try {
                        // get the max size of the wave that could be send
                        long maxWaveSize = context.getCredits() / type.getPrice();
                        // if size exceeds CREEPS_IN_WAVE, set it to CREEPS_IN_WAVE
                        if (maxWaveSize > IConstants.CREEPS_IN_WAVE) {
                            maxWaveSize = IConstants.CREEPS_IN_WAVE;
                        }
                        // set delay to send new creeps
                        context.lastWaveDelay = maxWaveSize * IConstants.SEND_WAVE_DELAY;

                        // while loop needed to get the number of completed loops
                        long i = 0;
                        while (i < maxWaveSize && !isInterrupted()) {
                            if (context.sendCreep(type)) {
                                sleep(IConstants.SEND_WAVE_DELAY);
                            } else {
                                interrupt();
                            }
                            i++;
                        }
                        // correct lastWaveDelay to prevent short send bug after spending money for
                        // tower/upgrades while sending the wave.
                        context.lastWaveDelay = i * IConstants.SEND_WAVE_DELAY;
                        interrupt();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    /**
     * sells the selected or highlighted tower. Only Msg-sending.
     *
     * @return true if a tower could be sold.
     */
    public boolean sellTower() {
        Tower t = this.selectTower();

        if ((t != null) && (!this.isDead()) && (t.isReady()) && (!t.getGrid().isOccupied())) {
            SellTowerMessage stm = new SellTowerMessage();
            stm.setClientId(getPlayerId());
            stm.setTowerId(t.getId());
            stm.setRoundId(this.gameLoop.getRoundID());
            getNetwork().sendMessage(stm);
            fireSelectedChangedEvent("sell");
            t.getGrid().setOccupiedStatus(true);
            return true;
        }
        return false;
    }

    public void buyTower(IConstants.Towers tower) {
        if (startCounter < 0) {
            this.gameBoard.buyTowerPerShortcut(tower);
        }
    }

    /**
     * Upgrades the given tower.
     *
     * @return true if tower is upgraded
     * @param t tower to upgrade
     */
    private boolean upgradeTower(Tower t) {
        if ((t != null) && (!isDead()) && (t.isReady()) && (t.isUpgradable()) && (!t.getGrid().isOccupied()) && (getCredits() >= t.getType().getNext().getPrice())) {

            UpgradeTowerMessage utm = new UpgradeTowerMessage();
            utm.setClientId(getPlayerId());
            utm.setTowerId(t.getId());
            utm.setRoundId(this.gameLoop.getRoundID());
            getNetwork().sendMessage(utm);
            setCredits(getCredits() - t.getType().getNext().getPrice());
            this.fireSelectedChangedEvent("upgrade");
            t.getGrid().setOccupiedStatus(true);
            return true;

        }
        return false;
    }

    /**
     * Upgrades the selected tower.
     *
     * @return true if tower is upgraded
     */
    public boolean upgradeTower() {
        if (this.selectTower() != null) {
            this.upgradeTower(this.selectTower());
        } else {
            return false;
        }

        return true;
    }

    /**
     * Sends the game over msg to the server.
     */
    public synchronized void sendDeathMessage() {
        if (!this.dead) {
            synchronized (GameContext.winningPosition) {
                GameContext.winningPosition--;
            }
            this.dead = true;
        }
    }

    /**
     * Fires an event when the lives changed.
     */
    protected void fireLivesChangedEvent() {
        for (ContextListener cl : contextListeners) {
            cl.livesChanged(this);
        }
    }

    /**
     * Fires an event when the income changed.
     */
    protected void fireIncomeChangedEvent() {
        for (ContextListener cl : contextListeners) {
            cl.incomeChanged(this);
        }
    }

    /**
     * Fires an event when the credits changed.
     */
    protected void fireCreditsChangedEvent() {
        for (ContextListener cl : contextListeners) {
            cl.creditsChanged(this);
        }
    }

    /**
     * Fires an event when the selected var changed.
     *
     * @param message what changed
     */
    public void fireSelectedChangedEvent(String message) {
        for (ContextListener cl : contextListeners) {
            cl.selectedChanged(this, message);
        }
    }

    /**
     * Tests if this context has no more lives left.
     *
     * @return true if the player with this context is dead
     */
    public boolean isDead() {
        return (lives <= 0);
    }

    public void setStrategy(FindCreepStrategy fcs) {
        Tower t = this.selectTower();
        if ((t != null) && (!this.isDead()) && (t.isReady()) && (!t.getGrid().isOccupied())) {
            ChangeStrategyMessage btm = new ChangeStrategyMessage();
            btm.setClientId(getPlayerId());
            btm.setTowerId(t.getId());
            btm.setStrategyType(fcs.getClass().getSimpleName());
            btm.setLocked(fcs.isCreepLock());
            getNetwork().sendMessage(btm);
            t.updateSelectedStrategy(fcs);
            fireSelectedChangedEvent("strategy");
            t.getGrid().setOccupiedStatus(true);
        }
    }

    /**
     * Adds a tower to the context.
     *
     * @param t
     *            the tower
     */
    public void addtower(Tower t) {
        synchronized (towers) {
            this.getTowers().add(t);
        }
        repaintBoard();
    }

    /**
     * Removes a tower from the context.
     *
     * @param t
     *            the tower to remove
     */
    public void removeTower(Tower t) {
        this.getGameBoard().removeTower(t.getId());
        this.getTowers().remove(t);
        repaintBoard();
    }

    /**
     * Repaints the game board with the towers. Used to make changes in the grid
     * or towers visible.
     */
    public void repaintBoard() {
        this.getGameBoard().clearImage();
    }

    /**
     * @param towers
     *            the towers to set
     */
    public void setTowers(List<Tower> towers) {
        this.towers = towers;
    }

    /**
     * @return the towers
     */
    public List<Tower> getTowers() {
        return towers;
    }

    /**
     * @param creeps
     *            the creeps to set
     */
    public void setCreeps(List<Creep> creeps) {
        this.creeps = creeps;
    }

    /**
     * @return the creeps
     */
    public List<Creep> getCreeps() {
        return creeps;
    }

    /**
     * @param credits
     *            the credits to set
     */
    public synchronized void setCredits(int credits) {
        if (this.isDead()) {
            return;
        }
        cheaterCheck();

        this.credits = credits;
        this._credits = Integer.toBinaryString(this.credits);
        fireCreditsChangedEvent();
    }

    /**
     * @return the credits
     */
    public synchronized int getCredits() {
        return credits;
    }

    /**
     * Removes one live from the context.
     */
    public void removeLive() {
        cheaterCheck();

        if (lives > 0) {
            this.lives--;
            this._lives = Integer.toBinaryString(this.lives);
            fireLivesChangedEvent();
        }
    }

    /**
     * setter for lives.
     *
     * @param lives
     *            the lives to set
     */
    public void setLives(int lives) {

        cheaterCheck();
        this.lives = lives;
        this._lives = Integer.toBinaryString(this.lives);
    }

    /**
     * @return the lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * @return the location
     */
    public BoardLocation getLocation() {
        return location;
    }

    /**
     * @param gameBoard
     *            the gameBoard to set
     */
    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    /**
     * @return the gameBoard
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * @return the gameBoard
     */
    public GameContext getGameContext(int player) {
        return this.gameLoop.getGameContext(player);
    }

    /**
     * Adds a contextListener to this context.
     *
     * @param contextListener
     *            the listeners to add
     */
    public void addContextListener(ContextListener contextListener) {
        this.contextListeners.add(contextListener);
    }

    /**
     * Removes a contextListener from this context.
     *
     * @param contextListener
     *            the listeners to remove
     */
    public void removeContextListener(ContextListener contextListener) {
        this.contextListeners.remove(contextListener);
    }

    /**
     * @param network
     *            the network to set
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * @return the network
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * @param playerId
     *            the playerId to set
     */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /**
     * @return the playerId
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * @param playerName
     *            the playerName to set
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * @return the playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @param transfer
     *            the transfer to set
     */
    public void setTransfer(List<Creep> transfer) {
        this.transfer = transfer;
    }

    /**
     * @return the transfer
     */
    public List<Creep> getTransfer() {
        return transfer;
    }

    /**
     * Getter for the income of the player.
     *
     * @return the income
     */
    public synchronized int getIncome() {
        return income;
    }

    /**
     * Setter for the income of the player.
     *
     * @param income
     *            the new income
     */
    public synchronized void setIncome(int income) {

        cheaterCheck();
        this.income = income;
        this._income = Integer.toBinaryString(this.income);
        fireIncomeChangedEvent();
    }

    public boolean cheaterCheck() {

        if (this.getPlayerId() != getNetwork().getCore().getPlayerId()) {
            return true;
        }

        String message = null;
        //Income
        if (!this._income.equals(Integer.toBinaryString(this.income))) {
            message = "I am a big cheater!!! Income cheated. My income is " + this.income;
        }
        //Lives
        if (!this._lives.equals(Integer.toBinaryString(this.lives))) {
            message = "I am a big cheater!!! Live cheated. My live is " + this.lives;
        }
        //Credits
        if (!this._credits.equals(Integer.toBinaryString(this.credits))) {
            message = "I am a big cheater!!! Credits cheated. New credit is " + this.credits;
        }

        if (message != null) {

            SendMessageMessage mes = new SendMessageMessage();
            mes.setClientId(getPlayerId());
            mes.setMessage(message);
            getNetwork().sendMessage(mes);

            // sends a messages to all players that we have left
            SendMessageMessage chatMsg = new SendMessageMessage();
            chatMsg.setClientId(getPlayerId());
            chatMsg.setMessage("has left the game");
            getNetwork().sendMessage(chatMsg);

            getNetwork().sendMessage(new ExitGameMessage());
            System.exit(1);
            return false;
        }

        return true;
    }

    /**
     * Getter for SoundManagement.
     *
     * @return managementSound object for sound management
     */
    public SoundManagement getSoundManagement() {
        return managementSound;
    }

    /**
     * Setter for Map on which the game will be played on.
     *
     * @param m
     *            one Map out of the enum
     */
    public void setMap(IConstants.Map m) {
        mapfile = m;
    }

    /**
     * Getter for Map on which the game will be played on.
     *
     * @return just the Map
     */
    public IConstants.Map getMap() {
        return mapfile;
    }

    /**
     * @return the nextTower
     */
    public IConstants.Towers getNextTower() {
        return nextTower;
    }

    /**
     * @param nextTower
     *            the nextTower to set
     */
    public void setNextTower(IConstants.Towers nextTower) {
        this.nextTower = nextTower;
    }

    /**
     * @return the selectedTower
     */
    public Tower getSelectedTower() {
        return selectedTower;
    }

    /**
     * @param selectedTower
     *            the selectedTower to set
     */
    public void setSelectedTower(Tower selectedTower) {
        this.selectedTower = selectedTower;
    }

    /**
     * @return the winningPosition
     */
    public static int getWinningPosition() {
        return GameContext.winningPosition;
    }

    /**
     * @param winningPosition
     *            the winningPosition to set
     */
    public static void setWinningPosition(int winningPosition) {
        synchronized (GameContext.winningPosition) {
            GameContext.winningPosition = winningPosition;
        }
    }

    public Grid getHighlightedGrid() {
        return this.gameBoard.getHighlightedGrid();
    }

    /**
     * This methods returns the selected tower. If no tower
     * is selected, the highlighted tower will be returned.
     * Is neither a tower selected or highlighted, null will
     * be returned
     *
     * @return tower or null
     */
    public Tower selectTower() {
        Tower t = null;
        if (this.getSelectedTower() != null) {
            t = this.getSelectedTower();
        } else if (this.getHighlightedGrid().getTower() != null) {
            t = this.getHighlightedGrid().getTower();
        }

        return t;
    }

    public GameLoop getGameLoop() {
        return gameLoop;
    }
}

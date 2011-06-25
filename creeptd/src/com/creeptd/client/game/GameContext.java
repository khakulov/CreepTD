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
import com.creeptd.common.Constants;
import com.creeptd.common.Password;
import com.creeptd.common.messages.client.AsyncronousMessage;
import com.creeptd.common.messages.client.BuildCreepMessage;
import com.creeptd.common.messages.client.ChangeStrategyMessage;
import com.creeptd.common.messages.client.ExitGameMessage;
import com.creeptd.common.messages.client.SellTowerMessage;
import com.creeptd.common.messages.client.UpgradeTowerMessage;
import com.creeptd.common.messages.server.BuildCreepRoundMessage;
import com.creeptd.common.messages.server.BuildTowerRoundMessage;
import com.creeptd.common.messages.server.ChangeStrategyRoundMessage;
import com.creeptd.common.messages.server.GameMessage;
import com.creeptd.common.messages.server.PlayerGameOverMessage;
import com.creeptd.common.messages.server.PlayerLosesLifeMessage;
import com.creeptd.common.messages.server.PlayerQuitMessage;
import com.creeptd.common.messages.server.SellTowerRoundMessage;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.common.messages.server.TransferCreepMessage;
import com.creeptd.common.messages.server.UpgradeTowerRoundMessage;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import static com.creeptd.client.i18n.Translator.*;

/**
 * Abstract class representing the context for one player.
 * 
 * @author Philipp
 */
public abstract class GameContext {

    /** Logging functionality */
    private static Logger logger = Logger.getLogger(GameContext.class.getName());

    /**
     * Enumeration for the four positions where a GameBoard can be.
     *
     * @author Philipp
     */
    public static enum BoardLocation {
        TOPLEFT(30, 30, 320, 320),
        BOTTOMLEFT(30, 350, 320, 320),
        TOPRIGHT(350, 30, 320, 320),
        BOTTOMRIGHT(350, 350, 320, 320);

        /** X coordinate */
        private final double x;
        /** Y coordinate */
        private final double y;
        /** Width */
        private final int width;
        /** Height */
        private final int height;
        /** Bound rectangle */
        private final Rectangle bounds;

        /**
         * BoardLocation constructor.
         *
         * @param x the x position
         * @param y the y position
         * @param width the width
         * @param height the height
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
         * @return A Rectangle specifying the bounding box
         */
        public Rectangle getBounds() {
            return bounds;
        }
    }
    
    /** The player's id */
    private int playerId;
    /** The player's name */
    private String playerName;
    /** Listeners for this context */
    private ArrayList<ContextListener> contextListeners;
    /** Towers inside the contex */
    private final List<Tower> towers;
    /** Creeps inside the ontext */
    private final List<Creep> creeps;
    /** Credit amount of this player */
    private int credits = Constants.CREDITS;
    private String credits_hash = null;
    /** Income amount of this player */
    private int income = Constants.START_INCOME;
    private String income_hash = null;
    /** Lifes amount of this player */
    private int lifes = Constants.LIVES;
    private String lives_hash = null;
    /** This context's game board */
    private GameBoard gameBoard;
    /** The board's location (TOPLEFT, ...) */
    private BoardLocation location;
    /** The boards translation matrix */
    private AffineTransform translation = new AffineTransform();
    /** Reference to the underlying network */
    private Network network;
    /** Reference to the underlying game loop */
    private GameLoop gameLoop;
    /** Reference to sound management (null for no sound, e.g. in opponent contexts) */
    private SoundManagement soundManagement;
    /** The map */
    private Constants.Map mapfile = Constants.Map.Random_Map;
    /** Next tower to build */
    private Constants.Towers nextTower = null;
    /** Selected tower */
    private Tower selectedTower = null;
    /** Start counter for "loading...", "game starting in..", 5, 4 etc. */
    private int startCounter = Constants.INCOME_TIME / 1000;
    /** Flag if this context is a winner or not */
    private boolean winner = false;
    // Creep times
    private long lastCreepSentTime = 0;
    private long lastWaveSentTime = 0;
    private long lastWaveDelay = 0;
    /** The amount of lifes taken from other players */
    public int takenLifes;
    /** Flag if the game over sound has been played already */
    private boolean endSoundPlayed = false;
    /** Counter for the player's position after game over */
    private static Integer positionCounter;
    /** A heart image */
    private static Image oneHeartImg = Toolkit.getDefaultToolkit().getImage(GameContext.class.getClassLoader().getResource("com/creeptd/client/resources/panel/icon_onelife.gif"));
    /** A half heart image */
    private static Image halfHeartImg = Toolkit.getDefaultToolkit().getImage(GameContext.class.getClassLoader().getResource("com/creeptd/client/resources/panel/icon_halflife.gif"));


    /** Set the static position counter */
    public static void setPositionCounter(int value) {
        positionCounter = value;
    }

    /**
     * Create a game context.
     *
     * @param location The boardLocation (GameContext.BoardLocation)
     * @param network The current network connection object
     * @param mSound SoundManagement Sound management (null for opponents)
     * @param map The map to play
     * @param int The context owner's player id
     * @param String The context owner's player name
     */
    public GameContext(BoardLocation location, Network network, SoundManagement mSound, Constants.Map map, GameLoop gameLoop, int player_id, String player_name) {
        this.location = location;
        this.network = network;
        this.mapfile = map;
        this.gameLoop = gameLoop;
        this.setGameBoard(new GameBoard(this));
        this.towers = Collections.synchronizedList(new ArrayList<Tower>());
        this.creeps = Collections.synchronizedList(new ArrayList<Creep>());
        this.contextListeners = new ArrayList<ContextListener>();
        this.soundManagement = mSound;
        this.setPlayerId(player_id);
        this.setPlayerName(player_name);
        this.setCredits(credits);
        this.setIncome(income);
        this.setLifes(lifes);
    }


    /**
     * Get the start counter value.
     *
     * @return The start counter value (0 if game has begun)
     */
    public int getStartCounter() {
        return startCounter;
    }

    /**
     * Set the start counter value.
     *
     * @param startCounter The start counter value to set
     */
    public void setStartCounter(int startCounter) {
        this.startCounter = startCounter;
    }

    /**
     * Update the context by one tick.
     *
     * @param roundID The current tick
     */
    public void update(long roundID) {
        ArrayList<Tower> towersCopy = new ArrayList<Tower>(getTowers());
        ArrayList<Creep> creepsCopy = new ArrayList<Creep>(getCreeps());

        // Update towers if the player is not dead
        if (!this.isDead()) {
            for (Tower t : towersCopy) {
                t.update(roundID);
            }
        }
        // Update creeps
        for (Creep c : creepsCopy) {
            c.update(roundID);
        }

        // Get game messages and invoke actions
        ArrayList<GameMessage> queueCopy = new ArrayList<GameMessage>(network.getQueue());
        for (GameMessage gm : queueCopy) {
            if (gm.getPlayerId() == this.playerId) {
                processMessage(gm, roundID);
                network.getQueue().remove(gm);
            }
        }
    }

    /**
     * Process a message from the server.
     *
     * @param gm The server message
     * @param roundId The current round id
     */
    private void processMessage(GameMessage gm, long roundId) {
        int updateRound = (int) (gm.getRoundId() - roundId);
        if (updateRound <= 0) {
            if (soundManagement != null) {
                soundManagement.error();
            }
            this.getGameLoop().setAsynchronous(true);
            AsyncronousMessage am = new AsyncronousMessage();
            am.setClientId(this.getPlayerId());
            am.setCurrentRoundId(roundId);
            am.setReceivedRoundId(gm.getRoundId());
            this.getNetwork().sendMessage(am);
            logger.warning("Asynchronicity detected, server informed - let's hope for the best");
        }

        if (gm instanceof BuildTowerRoundMessage) {
            BuildTowerRoundMessage btrm = (BuildTowerRoundMessage) gm;
            Grid grid = this.gameBoard.getGrid((int) btrm.getTowerPosition().getX(), (int) btrm.getTowerPosition().getY());

            if (grid.isFree()) {
                Tower t = TowerFactory.createTower(this, Constants.Towers.valueOf(Constants.Towers.class, btrm.getTowerType()),
                        grid);
                t.setBuilding(true);
                t.setBuildTime(updateRound);
                t.setId(btrm.getTowerId());
                this.addTower(t);

            }

        } else if (gm instanceof UpgradeTowerRoundMessage) {
            UpgradeTowerRoundMessage utrm = (UpgradeTowerRoundMessage) gm;
            Tower upgradeTower;
            // find the tower we want to upgrade
            for (Tower tower : towers) {
                if (tower.getId() == utrm.getTowerId()) {
                    upgradeTower = tower;
                    // set UpgradTime
                    upgradeTower.setUpgradeTime(updateRound, true);
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
                    ((AbstractTower) tower).setChangeStrategyTime(updateRound);
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
                    sellTower.setSellTime(updateRound);
                    sellTower.setSelling(true);
                    break;
                }
            }
        } else if (gm instanceof BuildCreepRoundMessage) {
            BuildCreepRoundMessage bcrm = (BuildCreepRoundMessage) gm;
            Creep c = CreepFactory.createCreep(this, Constants.Creeps.valueOf(bcrm.getCreepType()));
            c.setID(bcrm.getCreepId());
            c.setBuildTime(updateRound);
            c.setSenderId(bcrm.getSenderId());
            c.setPlayerID(bcrm.getPlayerId());
            this.getCreeps().add(c);
            if (!this.isDead() && this.soundManagement != null) {
                this.soundManagement.creepWarnSound(c.getType());
            }

        // Server says: There is a creep transfered to this context
        } else if (gm instanceof TransferCreepMessage) {
            TransferCreepMessage tcm = (TransferCreepMessage) gm;
            Creep c = CreepFactory.createCreep(this, Constants.Creeps.valueOf(tcm.getCreepType()));
            c.setID(tcm.getCreepId());
            c.setBuildTime(updateRound);
            c.setSenderId(tcm.getCreatorId());
            c.setPlayerID(tcm.getPlayerId());
            c.setHealth(tcm.getCreepHealth());
            c.setTransferCount(tcm.getTransferCount());
            this.getCreeps().add(c);
            if (!this.isDead() && this.soundManagement != null) {
                this.soundManagement.creepWarnSound(c.getType());
            }

        // Server says: This context loses a life
        } else if (gm instanceof PlayerLosesLifeMessage) {
            PlayerLosesLifeMessage pllm = (PlayerLosesLifeMessage) gm;
            GameContext sender = this.findContextByPlayerId(pllm.getCreatorId());
            if (this.getLifes() > 0) {
                this.setLifes(pllm.getLifes());
                sender.takenLifes++;
            }
            if (this.soundManagement != null) {
                this.soundManagement.creepEscapedSound(Constants.Creeps.valueOf(pllm.getCreepType()));
            }

        // Server says: This player has quit, create a message
        } else if (gm instanceof PlayerQuitMessage) {
            PlayerQuitMessage pqm = (PlayerQuitMessage) gm;
            ServerChatMessage scm = new ServerChatMessage();
            scm.setPlayerName(this.getPlayerName());
            scm.setMessage(_("has left..."));
            this.getGameLoop().update(scm);

        // Server says: This context is game over
        } else if (gm instanceof PlayerGameOverMessage) {
            PlayerGameOverMessage pgom = (PlayerGameOverMessage) gm;
            if (this.lifes > 0) {
                this.setLifes(0);
                this.sendDeathMessage(); // Sets winning position
                this.fireLifesChangedEvent();
            }
            this.setWinner(pgom.isWinner()); // May be updated later

        // Server says: This context receives a creep
        } else if (gm instanceof BuildCreepRoundMessage) {
            BuildCreepRoundMessage bcrm = (BuildCreepRoundMessage) gm;
            GameContext sender = this.findContextByPlayerId(bcrm.getSenderId());
            if (sender != null) {
                Creep c = CreepFactory.createCreep(this, Constants.Creeps.valueOf(bcrm.getCreepType()));
                c.setPlayerID(bcrm.getPlayerId());
                c.setSenderId(bcrm.getSenderId());
                synchronized (this.creeps) {
                    this.creeps.add(c);
                }
                if (this.getSoundManagement() != null) {
                    this.getSoundManagement().creepWarnSound(c.getType());
                }
            } else {
                // Should never happen
                logger.warning("Received a build creep message but sender id "+bcrm.getSenderId()+" is not in game");
            }
        }
    }

    /**
     * Paint the the context with its elements.
     *
     * @param g The graphics object
     */
    public void paint(Graphics2D g) {
        // Render game board with antialiasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font("Verdana", Font.PLAIN, 10));
        AffineTransform previousTransform = g.getTransform();
        translation.setToIdentity();
        translation.translate(this.getLocation().getX(), this.getLocation().getY());
        g.transform(translation);
        this.getGameBoard().paint(g);
        
        // Render creeps without antialiasing (performance++ & looks better, rly)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        synchronized (creeps) {
            for (Creep c : creeps) {
                c.paint(g);
            }
        }

        // Render towers etc. with antialiasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        synchronized (towers) {
            for (Tower t : getTowers()) {
                t.paintEffect(g);
            }
        }
        if ((startCounter >= 0) && (this instanceof PlayerContext)) {
            if (startCounter > 8) {
                g.setColor(Color.WHITE);
                drawCentered(g, new Font("Verdana", Font.BOLD, 30), _("Loading..."), 0);
            } else if (startCounter > 5) {
                g.setColor(Color.RED);
                drawCentered(g, new Font("Verdana", Font.BOLD, 20), _("Game starting in..."), 0);
            } else if (startCounter > 0) {
                g.setColor(Color.RED);
                drawCentered(g, new Font("Verdana", Font.BOLD, 200), String.valueOf(startCounter), 150);
            } else {
                g.setColor(Color.RED);
                drawCentered(g, new Font("Verdana", Font.BOLD, 100), _("GO!"), 67);
                g.setFont(new Font("Verdana", Font.BOLD, 190));
            }
        }
        paintPlayerInfo(g);

        // Draw game over or winner, if dead
        if (this.isDead()) {
            if (!this.isWinner()) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Verdana", Font.BOLD, 45));
                drawCentered(g, new Font("Verdana", Font.BOLD, 45), _("Game Over"), 0);
                if (this instanceof PlayerContext) {
                    drawCentered(g, new Font("Verdana", Font.BOLD, 15), _("Sad but true"), 20);
                    if (!this.endSoundPlayed) {
                        this.getGameLoop().getSoundManagement().gameOver();
                        this.endSoundPlayed = true;
                    }
                }
            } else {
                g.setColor(Color.WHITE);
                drawCentered(g, new Font("Verdana", Font.BOLD, 45), _("Winner"), 0);
                if (this instanceof PlayerContext) {
                    drawCentered(g, new Font("Verdana", Font.BOLD, 15), _("Press ESC to leave"), 20);
                    if (!this.endSoundPlayed) {
                        this.getGameLoop().getSoundManagement().gameOver();
                        this.endSoundPlayed = true;
                    }
                }
            }
        }

        // Reset transformation
        g.setTransform(previousTransform);
    }

    private void drawCentered(Graphics2D g, Font f, String s, int offset_y) {
        int width = 320;
        int height = 350;
        FontMetrics fm = g.getFontMetrics(f);
        int x = width/2-fm.stringWidth(s)/2;
        int y = height/2-fm.getAscent()/2 + offset_y;
        g.setFont(f);
        g.drawString(s, x, y);
    }

    /**
     * Method to paint the playernames and other things.
     *
     * @param g
     *            the graphics context
     */
    private void paintPlayerInfo(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        StringBuffer sb = new StringBuffer();
        sb.append(this.playerName);
        sb.append(" | ");
        /* if (this.lifes == 1) {
            sb.append(_("1 life"));
        } else {
            Map<String,String> args = new HashMap<String,String>();
            args.put("n", this.lifes+"");
            sb.append(__("%n% lifes", args));
        } */
        sb.append(this.lifes);

        g.setColor(Color.WHITE);
        if (this instanceof PlayerContext) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Verdana", Font.BOLD, 11));
        } else {
            g.setFont(new Font("Verdana", Font.PLAIN, 11));
        }
        int hearts_x = 0;
        int hearts_y = 0;
        switch (location) {
            case TOPLEFT:
                g.drawString(sb.toString(), 10, -10);
                hearts_x = 310;
                hearts_y = -23;
                break;
            case TOPRIGHT:
                g.drawString(sb.toString(), 10, -10);
                hearts_x = 310;
                hearts_y = -23;
                break;
            case BOTTOMLEFT:
                g.drawString(sb.toString(), 10, 337);
                hearts_x = 310;
                hearts_y = 323;
                break;
            case BOTTOMRIGHT:
                g.drawString(sb.toString(), 10, 337);
                hearts_x = 310;
                hearts_y = 323;
                break;
            default:
                break;
        }
        for (int i=this.getLifes(); i>0; i--) {
            if (i == 1) {
                hearts_x -= 9;
                AffineTransform at = new AffineTransform();
                at.setToTranslation(hearts_x, hearts_y);
                g.drawImage(oneHeartImg, at, null);
            } else {
                hearts_x -= 6;
                AffineTransform at = new AffineTransform();
                at.setToTranslation(hearts_x, hearts_y);
                g.drawImage(halfHeartImg, at, null);
            }
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public boolean readyForNewWave() {
        if (lastWaveSentTime + this.lastWaveDelay < System.currentTimeMillis()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean readyForNewCreep() {
        if (lastCreepSentTime + Constants.CREEP_DELAY < System.nanoTime()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sendet Threadsafe neue Creep
     */
    public synchronized boolean sendCreep(Constants.Creeps type) {
        if (this.getCredits() >= type.getPrice() && startCounter < 0 && !this.isDead()) {
            BuildCreepMessage bcm = new BuildCreepMessage();
            bcm.setClientId(this.getPlayerId());
            bcm.setCreepType(type.toString());
            bcm.setRoundId(this.gameLoop.getRoundId());
            this.network.sendMessage(bcm);
            this.setCredits(this.getCredits() - type.getPrice());
            this.setIncome(this.getIncome() + type.getIncome());
            lastCreepSentTime = System.nanoTime();
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
    public void sendCreepsWave(final Constants.Creeps type) {

        if (this.getCredits() >= type.getPrice() && startCounter < 0 && !this.isDead()) {
            lastWaveSentTime = System.currentTimeMillis();
            final GameContext context = this;
            new Thread() {

                @Override
                public void run() {

                    try {
                        // get the max size of the wave that could be send
                        long maxWaveSize = context.getCredits() / type.getPrice();
                        // if size exceeds CREEPS_IN_WAVE, set it to CREEPS_IN_WAVE
                        if (maxWaveSize > Constants.CREEPS_IN_WAVE) {
                            maxWaveSize = Constants.CREEPS_IN_WAVE;
                        }
                        // set delay to send new creeps
                        context.lastWaveDelay = maxWaveSize * Constants.SEND_WAVE_DELAY;

                        // while loop needed to get the number of completed loops
                        long i = 0;
                        while (i < maxWaveSize && !isInterrupted()) {
                            if (context.sendCreep(type)) {
                                sleep(Constants.SEND_WAVE_DELAY);
                            } else {
                                interrupt();
                            }
                            i++;
                        }
                        // correct lastWaveDelay to prevent short send bug after spending money for
                        // tower/upgrades while sending the wave.
                        context.lastWaveDelay = i * Constants.SEND_WAVE_DELAY;
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
            stm.setRoundId(this.gameLoop.getRoundId());
            getNetwork().sendMessage(stm);
            fireSelectedChangedEvent("sell");
            t.getGrid().setOccupiedStatus(true);
            return true;
        }
        return false;
    }

    public void buyTower(Constants.Towers tower) {
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
            utm.setRoundId(this.gameLoop.getRoundId());
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

    private boolean deathMsgSent = false;
    
    /**
     * Sends the game over msg to the server.
     */
    public synchronized void sendDeathMessage() {
        if (!this.deathMsgSent) {
            if (this instanceof PlayerContext) {
                logger.info("I am dead");
                // GameOverMessage gom = new GameOverMessage();
                // gom.setClientId(this.getPlayerId());
                // gom.setPosition(positionCounter);
                // this.getNetwork().sendMessage(gom);
            } else {
                logger.info(this.getPlayerName()+" is dead");
            }
            synchronized (positionCounter) {
                positionCounter--; // static
            }
            this.deathMsgSent = true;
        }
        // We finally set the player game over (this.dead), when the server says so
    }

    /**
     * Fires an event when the lives changed.
     */
    protected void fireLifesChangedEvent() {
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
     * Check if this context is a winner.
     *
     * @return true on winner, else false
     */
    public boolean isWinner() {
        return this.winner;
    }

    /**
     * Set if this context is a winner.
     *
     * @param winner true for winner, else false
     */
    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    /**
     * Check if this context is dead.
     *
     * @return true on dead, else false
     */
    public boolean isDead() {
        return this.lifes <= 0;
    }

    /**
     * Set a tower's strategy.
     *
     * @param fcs The strategy to set
     */
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
     * @param t The tower
     */
    public void addTower(Tower t) {
        synchronized (towers) {
            this.getTowers().add(t);
        }
        repaintBoard();
    }

    /**
     * Removes a tower from the context.
     *
     * @param t The tower to remove
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
     * Set this context's towers.
     * 
     * @param towers The towers to set
     */
    public void setTowers(List<Tower> towers) {
        synchronized (this.towers) {
            this.towers.clear();
            this.towers.addAll(towers);
        }
    }

    /**
     * Get this context's towers.
     * 
     * @return the towers
     */
    public List<Tower> getTowers() {
        return towers;
    }

    /**
     * Set this context's creeps.
     *
     * @param creeps The creeps to set
     */
    public void setCreeps(List<Creep> creeps) {
        synchronized (this.creeps) {
            this.creeps.clear();
            this.creeps.addAll(creeps);
        }
    }

    /**
     * Get this context's creeps.
     *
     * @return The creeps
     */
    public List<Creep> getCreeps() {
        return creeps;
    }

    /**
     * Set this context's credit amount.
     * 
     * @param credits The credit amount to set
     */
    public synchronized void setCredits(int credits) {
        if (this.isDead()) {
            return;
        }
        checkIntegrity();
        this.credits = credits;
        this.credits_hash = Password.md5(""+credits, this.getPlayerName());
        fireCreditsChangedEvent();
    }

    /**
     * Get this context's credit amount.
     *
     * @return The credit amount
     */
    public synchronized int getCredits() {
        return credits;
    }

    /**
     * Set this context's lifes.
     *
     * @param lifes The lifes amount to set
     */
    public void setLifes(int lives) {
        checkIntegrity();
        this.lifes = lives;
        this.lives_hash = Password.md5(""+this.lifes, this.getPlayerName());
    }

    /**
     * Get number of left lifes.
     * 
     * @return Lifes left
     */
    public int getLifes() {
        return lifes;
    }

    /**
     * Get the board location associated with this context.
     *
     * @return The location
     */
    public BoardLocation getLocation() {
        return location;
    }

    /**
     * Assign the game board.
     * 
     * @param gameBoard The gameBoard to set
     */
    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    /**
     * Get the game board instance, this context belongs to.
     *
     * @return The gameBoard
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * Find the context belonging to the specified player id.
     *
     * @return The found game context
     */
    public GameContext findContextByPlayerId(int playerId) {
        return this.gameLoop.findContextByPlayerId(playerId);
    }

    /**
     * Adds a contextListener to this context.
     *
     * @param contextListener The listeners to add
     */
    public void addContextListener(ContextListener contextListener) {
        this.contextListeners.add(contextListener);
    }

    /**
     * Removes a contextListener from this context.
     *
     * @param contextListener The listener to remove
     */
    public void removeContextListener(ContextListener contextListener) {
        this.contextListeners.remove(contextListener);
    }

    /**
     * Assign the underlying network instance.
     *
     * @param network The network instance to set
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * Get the underlying network instance.
     *
     * @return The network instance
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * Set the player id of this context.
     * 
     * @param playerId The playerId to set
     */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /**
     * Get the player id of this context.
     * 
     * @return The playerId
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Set the player name of this context.
     * 
     * @param playerName The playerName to set
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Get the player name of this context.
     *
     * @return The playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Get the income of the associated player.
     *
     * @return The current income
     */
    public synchronized int getIncome() {
        return income;
    }

    /**
     * Set the income of the associated player.
     *
     * @param income The new income
     */
    public synchronized void setIncome(int income) {
        checkIntegrity();
        this.income = income;
        this.income_hash = Password.md5(""+this.income, this.getPlayerName());
        fireIncomeChangedEvent();
    }

    /**
     * Check context data for integrity.
     *
     * @return true on valid, else false
     */
    public boolean checkIntegrity() {
        if (this.getPlayerId() != getNetwork().getCore().getPlayerId()) {
            return true;
        }
        String message = null;
        if (
                (this.income_hash == null || Password.md5(""+this.income, this.getPlayerName()).equals(this.income_hash)) &&
                (this.lives_hash == null || Password.md5(""+this.lifes, this.getPlayerName()).equals(this.lives_hash)) &&
                (this.credits_hash == null || Password.md5(""+this.credits, this.getPlayerName()).equals(this.credits_hash))
        ) {
            return true;
        }
        ExitGameMessage exitMsg = new ExitGameMessage();
        exitMsg.setMessage("integrity");
        getNetwork().sendMessage(exitMsg);
        System.exit(1);
        return false;
    }

    /**
     * Get the ´SoundManagement.
     *
     * @return SoundManagement object assigned to this context or null if none
     */
    public SoundManagement getSoundManagement() {
        return soundManagement;
    }

    /**
     * Set the map the game will be played on.
     *
     * @param m The map to set
     */
    public void setMap(Constants.Map m) {
        mapfile = m;
    }

    /**
     * Get the map the game is played on.
     *
     * @return The map
     */
    public Constants.Map getMap() {
        return mapfile;
    }

    /**
     * Get next building tower.
     *
     * @return The next building tower
     */
    public Constants.Towers getNextTower() {
        return nextTower;
    }

    /**
     * Set the next building tower.
     *
     * @param nextTower The next building tower
     */
    public void setNextTower(Constants.Towers nextTower) {
        this.nextTower = nextTower;
    }

    /**
     * Get the currently selected tower.
     *
     * @return The selected tower
     */
    public Tower getSelectedTower() {
        return selectedTower;
    }

    /**
     * Set the currently selected tower.
     *
     * @param selectedTower The selected tower
     */
    public void setSelectedTower(Tower selectedTower) {
        this.selectedTower = selectedTower;
    }

    /**
     * Get the highlighted grid.
     * 
     * @return The highlighted grid
     */
    public Grid getHighlightedGrid() {
        return this.gameBoard.getHighlightedGrid();
    }

    /**
     * This methods returns the selected tower. If no tower
     * is selected, the highlighted tower will be returned.
     * Is neither a tower selected or highlighted, null will
     * be returned.
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

    /**
     * Get the underlying game loop.
     *
     * @return The game loop
     */
    public GameLoop getGameLoop() {
        return gameLoop;
    }
}

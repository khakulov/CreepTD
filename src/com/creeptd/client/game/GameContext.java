package com.creeptd.client.game;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.creeptd.client.game.creeps.Creep;
import com.creeptd.client.game.creeps.CreepFactory;
import com.creeptd.client.game.grids.Grid;
import com.creeptd.client.game.strategies.Strategy;
import com.creeptd.client.game.strategies.StrategyFactory;
import com.creeptd.client.game.towers.Tower;
import com.creeptd.client.game.towers.TowerFactory;
import com.creeptd.common.Constants;
import com.creeptd.common.messages.server.BuildCreepRoundMessage;
import com.creeptd.common.messages.server.BuildTowerRoundMessage;
import com.creeptd.common.messages.server.ChangeStrategyRoundMessage;
import com.creeptd.common.messages.server.GameMessage;
import com.creeptd.common.messages.server.PlayerGameOverMessage;
import com.creeptd.common.messages.server.PlayerLosesLifeMessage;
import com.creeptd.common.messages.server.SellTowerRoundMessage;
import com.creeptd.common.messages.server.TransferCreepMessage;
import com.creeptd.common.messages.server.UpgradeTowerRoundMessage;


public class GameContext {

    private static Logger logger = Logger.getLogger(GameContext.class.getName());

    private int playerId;
    private String playerName;

    private final List<Tower> towers;
    private final List<Creep> creeps;
    private final List<GameMessage> messages;

    private long credits = Constants.CREDITS;
    private long income = Constants.START_INCOME;
    private int lifes = Constants.LIVES;

    private boolean gameOver = false;

    /** This context's game board */
    private GameBoard gameBoard;
    /** The board's location (TOPLEFT, ...) */
    private BoardLocation location;
    /** The boards translation matrix */
    private AffineTransform translation = new AffineTransform();

    /** Reference to the underlying game */
    private Game game;

    /** Flag if this context is a winner or not */
    private boolean winner = false;
    /** The amount of lifes taken from other players */
    public int takenLifes;
    /** Time in millis the game was paused at */
    private long pausedTime = 0;
    /** Counter for the player's position after game over */
    private static Integer positionCounter;
    /** Life lost time to display a red warning overlay */
    private long lifeLostAt = 0;

    /**
     * Create a game context.
     *
     * @param Game The Game
     * @param location The boardLocation (GameContext.BoardLocation)
     * @param int The context owner's player id
     * @param String The context owner's player name
     */
    public GameContext(Game game, BoardLocation location, int playerId, String playerName) {
        this.location = location;
        this.game = game;
        this.gameBoard = new GameBoard(this);
        this.towers = Collections.synchronizedList(new ArrayList<Tower>());
        this.creeps = Collections.synchronizedList(new ArrayList<Creep>());
        this.messages = Collections.synchronizedList(new ArrayList<GameMessage>());
        this.playerId = playerId;
        this.playerName = playerName;
        this.setCredits(credits);
        this.setIncome(income);
        this.setLifes(lifes);
    }

    /**
     * Update the context by one tick.
     *
     * @param roundID The current tick
     */
    public void update(long roundID) {
        List<Tower> towersCopy = new ArrayList<Tower>(this.towers);
        List<Creep> creepsCopy = new ArrayList<Creep>(this.creeps);
        List<GameMessage> messagesCopy = new ArrayList<GameMessage>(this.messages);

        // Update towers if the player is not gameOver
        if (!this.gameOver) {
        	synchronized(this.towers) {
	            for (Tower tower : towersCopy) {
	            	tower.update(roundID);
	            }
        	}
        }
        // Update creeps
    	synchronized(this.creeps) {
	        for (Creep creep : creepsCopy) {
	        	creep.update(roundID);
	        }
    	}

        // Get game messages and invoke actions
    	synchronized(this.messages) {
	        for (GameMessage gm : messagesCopy) {
	        	if (gm.getRoundId() < roundID+50) {
		        	assert gm.getPlayerId() == this.playerId;
		            processMessage(gm, roundID);
		            this.messages.remove(gm);
	        	}
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
            logger.warning("Asynchronicity detected.");
        }

        if (gm instanceof BuildTowerRoundMessage) {
            BuildTowerRoundMessage btrm = (BuildTowerRoundMessage) gm;
            Grid grid = this.gameBoard.getGrid((int) btrm.getTowerPosition().getX(), (int) btrm.getTowerPosition().getY());

            if (grid.isFree()) {
                Tower t = TowerFactory.createTower(Constants.Towers.valueOf(Constants.Towers.class, btrm.getTowerType()),
                		this, grid);
                t.setId(btrm.getTowerId());
                t.build(updateRound);
                this.towers.add(t);
            	this.setCredits(this.getCredits() - t.getType().getPrice());
            	this.gameBoard.clearImage();
            }

        } else if (gm instanceof UpgradeTowerRoundMessage) {
            UpgradeTowerRoundMessage utrm = (UpgradeTowerRoundMessage) gm;
            Tower upgradeTower;
            // find the tower we want to upgrade
            for (Tower tower : towers) {
                if (tower.getId() == utrm.getTowerId()) {
                    upgradeTower = tower;
                    // set UpgradTime
                    upgradeTower.upgrade(updateRound);
                    this.setCredits(this.getCredits() - upgradeTower.getType().getNext().getPrice());
                    break;
                }
            }

        } else if (gm instanceof ChangeStrategyRoundMessage) {
            ChangeStrategyRoundMessage csm = (ChangeStrategyRoundMessage) gm;
            // find the tower we want to change
            for (Tower tower : towers) {
                if (tower.getId() == csm.getTowerId()) {
                    Strategy fcs = StrategyFactory.createStrategy(csm.getStrategy(), tower);
                    tower.changeStrategy(updateRound, fcs);
                    if (csm.isLocked()) {
                    	fcs.lock(); 
                    } else {
                    	fcs.unlock();
                    }
                    break;
                }
            }

        } else if (gm instanceof SellTowerRoundMessage) {
            SellTowerRoundMessage strm = (SellTowerRoundMessage) gm;
            Tower sellTower;
            // find the tower we want to sell
            for (Tower tower : towers) {
                if (tower.getId() == strm.getTowerId()) {
                    sellTower = tower;
                    sellTower.sell(updateRound);
                    break;
                }
            }
            
        // Server says: This context receives a creep
        } else if (gm instanceof BuildCreepRoundMessage) {
            BuildCreepRoundMessage bcrm = (BuildCreepRoundMessage) gm;
            Creep c = CreepFactory.createCreep(this, Constants.Creeps.valueOf(bcrm.getCreepType()));
            c.setBuildTime(updateRound);
            this.getCreeps().add(c);
            // Update income & credits if it is not already has been done.
            if (!AllvsAllChecker.check(this.game, bcrm)) {
            	GameContext senderContext = this.game.findContextByPlayerId(bcrm.getSenderId());
	            if (senderContext != null) {
		            senderContext.setCredits(senderContext.getCredits() - c.getType().getPrice());
		            senderContext.setIncome(senderContext.getIncome() + c.getType().getIncome());
		            
		            if ((long)(roundId / Constants.INCOME_TIME)
		            		- (long)(gm.getRoundId() / Constants.INCOME_TIME ) > 0 ) {
		            	senderContext.setCredits(senderContext.getCredits() + c.getType().getIncome());
		            }
	            }
            }

        // Server says: There is a creep transfered to this context
        } else if (gm instanceof TransferCreepMessage) {
            TransferCreepMessage tcm = (TransferCreepMessage) gm;
            Creep c = CreepFactory.createCreep(this, Constants.Creeps.valueOf(tcm.getCreepType()));
            c.setBuildTime(updateRound);
            c.setHealth(tcm.getCreepHealth());
            this.getCreeps().add(c);

        // Server says: This context loses a life
        } else if (gm instanceof PlayerLosesLifeMessage) {
            PlayerLosesLifeMessage pllm = (PlayerLosesLifeMessage) gm;
            GameContext creator = this.game.findContextByPlayerId(pllm.getCreatorId());
            if (this.getLifes() > 0) {
                this.setLifes(pllm.getLifes());
                if (creator != null) creator.takenLifes++; // May be null for Survivor mode
                this.lifeLostAt = new Date().getTime();
            }

        // Server says: This context is game over
        } else if (gm instanceof PlayerGameOverMessage) {
            PlayerGameOverMessage pgom = (PlayerGameOverMessage) gm;
            if (!this.gameOver) {
                this.gameOver = true;

                logger.info(this.getPlayerName()+" is dead");
                positionCounter--; // Sets winning position(static)
                if (!pgom.isWinner()) {
                    this.setLifes(0);
                }
            }
            this.winner = pgom.isWinner(); // May be updated later
            logger.info("Player " + this.getPlayerName() + "(" + this.getPlayerId() + ") is game over");
        }
    }

    /**
     * Called when this context pauses/resumes the game.
     * 
     * @param paused true if paused, false if resumed
     */
    public void onPaused(boolean paused) {
        if (paused) {
            this.pausedTime = new Date().getTime();
            logger.info(this.getPlayerName()+" paused the game");
        } else {
            this.pausedTime = 0;
            logger.info(this.getPlayerName()+" unpaused the game");
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
        this.gameBoard.paint(g);
        
        // Render creeps without antialiasing (performance++ & looks better, rly)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        synchronized (creeps) {
            for (Creep c : creeps) {
                c.paint(g);
            }
        }
        // Render tower effects (splash arcs, shooting lines etc.)
        synchronized (towers) {
            for (Tower t : getTowers()) {
                t.paintEffect(g);
            }
        }
        // Render text messages etc. with antialiasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw start counter and start highlight
        if (this.game.getRoundId() < 300) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.66f));
            g.setColor(Color.YELLOW);
            g.setStroke(new BasicStroke(2f));
            g.drawRect(1, 1, this.location.getWidth()-2, this.location.getHeight()-2);
            g.setStroke(new BasicStroke(1f));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            Point start = this.gameBoard.getPath().getPoint(0);
            if ((this.game.getRoundId() / 20) % 2 == 0) {
                Image warn = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("com/creeptd/client/resources/game/warn.png"));
                g.drawImage(warn, start.x*Grid.SIZE, start.y*Grid.SIZE, null);
            }
            if (this.game.getRoundId() <= 140) {
                g.setColor(Color.WHITE);
                drawCentered(g, new Font("Verdana", Font.BOLD, 30), "Loading...", 0);
            } else if (this.game.getRoundId() <= 220) {
                g.setColor(Color.RED);
                drawCentered(g, new Font("Verdana", Font.BOLD, 20), "Game starting in...", 0);
            } else if (this.game.getRoundId() <= 280) {
                g.setColor(Color.RED);
                long count = (300 - this.game.getRoundId()) / 20;
                drawCentered(g, new Font("Verdana", Font.BOLD, 200), String.valueOf(count), 150);
            } else {
                g.setColor(Color.RED);
                drawCentered(g, new Font("Verdana", Font.BOLD, 100), "GO!", 67);
                g.setFont(new Font("Verdana", Font.BOLD, 190));
            }
        }
        paintPlayerInfo(g);
        
        // Draw paused message
        if (!this.gameOver && this.pausedTime != 0) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Verdana", Font.BOLD, 30));
            int timeleft = -1;
            if (Constants.MAX_PAUSE_MS > 0) {
                timeleft = 1 + (int) Math.floor((Constants.MAX_PAUSE_MS - new Date().getTime() + this.pausedTime) / 1000);
                if (timeleft < 0) timeleft = 0;
            }
            if (timeleft != -1) {
            	drawCentered(g, new Font("Verdana", Font.BOLD, 30), "Paused"+" ("+timeleft+")", 0);
            } else {
                drawCentered(g, new Font("Verdana", Font.BOLD, 30), "Paused", 0);
            }
        }

        // Draw life lost warning overlay
        long now = new Date().getTime();
        if (!this.gameOver && this.lifeLostAt > now - 500) {
            float alpha = 0.5f - 0.5f*(now - this.lifeLostAt)/500;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setColor(Color.RED);
            g.fillRect(0, 0, this.location.getWidth(), this.location.getHeight());
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        // Draw game over or winner, if gameOver
        if (this.gameOver) {
            if (!this.winner) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Verdana", Font.BOLD, 45));
                drawCentered(g, new Font("Verdana", Font.BOLD, 45), "Game Over", 0);
            } else {
                g.setColor(Color.WHITE);
                drawCentered(g, new Font("Verdana", Font.BOLD, 45), "Winner", 0);
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

        // Draw shadow
        Color previousColor = g.getColor();
        g.setColor(Color.BLACK);
        g.drawString(s, x+1, y+1);
        // Draw text
        g.setColor(previousColor);
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
        sb.append(this.lifes);
        sb.append(" | Credits: ");
        sb.append(this.credits);
        sb.append(" | Income: ");
        sb.append(this.income);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Verdana", Font.PLAIN, 11));

        switch (location) {
            case TOPLEFT:
            case CENTER:
                g.drawString(sb.toString(), 10, -10);
                break;
            case TOPRIGHT:
                g.drawString(sb.toString(), 10, -10);
                break;
            case BOTTOMLEFT:
                g.drawString(sb.toString(), 10, 337);
                break;
            case BOTTOMRIGHT:
                g.drawString(sb.toString(), 10, 337);
                break;
            default:
                break;
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
     * Removes a tower from the context.
     *
     * @param t The tower to remove
     */
    public void removeTower(Tower t) {
        this.gameBoard.removeTower(t.getId());
        this.getTowers().remove(t);
        this.gameBoard.clearImage();
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
     * Get the income of the associated player.
     *
     * @return The current income
     */
    public long getIncome() {
        return income;
    }

    /**
     * Set the income of the associated player.
     *
     * @param income The new income
     */
    public void setIncome(long income) {
        this.income = income;
    }
    
    /**
     * Get this context's credit amount.
     *
     * @return The credit amount
     */
    public long getCredits() {
        return credits;
    }
    
    /**
     * Set this context's credit amount.
     * 
     * @param credits The credit amount to set
     */
    public void setCredits(long credits) {
        this.credits = credits;
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
     * Set this context's lifes.
     *
     * @param lifes The lifes amount to set
     */
    public void setLifes(int lives) {
        this.lifes = lives;
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
     * Get the game board instance, this context belongs to.
     *
     * @return The gameBoard
     */
    public GameBoard getGameBoard() {
        return gameBoard;
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
     * Get the player name of this context.
     *
     * @return The playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Get the underlying game loop.
     *
     * @return The game loop
     */
    public Game getGameLoop() {
        return game;
    }

    /** Set the static position counter */
    public static void setPositionCounter(int value) {
        positionCounter = value;
    }

    /**
     * add a GameMessage to the queue.
     *
     * @param gm
     *            the GameMessage to add to the queue
     */
    public void addGameMessage(GameMessage gm) {
        this.messages.add(gm);
    }

    /**
     * Check if this context is gameOver.
     * @return true on gameOver, else false
     */
    public boolean isGameOver() {
        return this.gameOver;
    }

    /**
     * Check if this context is winner.
     * @return true on winner, else false
     */
    public boolean isWinner() {
        return this.winner;
    }
}

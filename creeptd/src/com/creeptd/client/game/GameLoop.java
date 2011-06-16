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

import com.creeptd.client.creep.Creep;
import com.creeptd.client.creep.CreepFactory;
import com.creeptd.client.game.GameContext.BoardLocation;
import com.creeptd.client.network.MessageListener;
import com.creeptd.client.network.Network;
import com.creeptd.client.panel.GamePanel;
import com.creeptd.client.sound.SoundManagement;
import com.creeptd.client.tower.Tower;
import com.creeptd.common.IConstants;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.common.messages.server.PlayerQuitMessage;
import com.creeptd.common.messages.server.RoundMessage;
import com.creeptd.common.messages.server.ServerMessage;
import com.creeptd.common.messages.server.StartGameMessage;

import com.creeptd.common.messages.client.LiveTakedMessage;
import java.util.Iterator;

/**
 * Main GameLoop for updates and repaint. Sync with Server tick and framerate
 * approximation.
 */
public class GameLoop extends Thread implements MessageListener, IConstants {

    private static Logger logger = Logger.getLogger(GameLoop.class.getName());
    private GameContext myContext;
    private ArrayList<GameContext> contexts = new ArrayList<GameContext>();
    // key PlayerID, value position
    private TreeMap<Integer, Integer> playersOrder = new TreeMap<Integer, Integer>();
    private TreeMap<Integer, String> players = new TreeMap<Integer, String>();
    private GameContext[] orderedPlayers = null; // Cache field
    private Network tdNetwork;
    private GamePanel gamePanel;
    private Map map;
    private IConstants.Mode gameMode;
    private boolean running = false;
    private long roundID = 0;
    private long maxRound = 0;
    private int incomeCounter = 0;
    private boolean gameOver;
    private boolean asynchronous = false;
    // one loop-cycle lasts TICK_INTERVAL time
    // private static final long TICK_INTERVAL = TICK_MS * 1000000;
    // private static final long TICK_INTERVAL_TIME_OUT = ((TICK_MS * USER_ACTION_DELAY) + TICK_MS) * 1000000;
    private static final int NO_DELAYS_PER_YIELD = 10;
    private static final int MAX_FRAME_SKIPS = 5;
    private SoundManagement soundManagement;

    /**
     * @return the gameOver
     */
    public synchronized boolean isGameOver() {
        return gameOver;
    }

    /**
     * @param gameOver
     *            the gameOver to set
     */
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * @return if the Client is asynchronous
     */
    public boolean isAsynchronous() {
        return asynchronous;
    }

    /**
     * @param asynchronous
     *            the cliet is asynchronous true/false
     */
    public void setAsynchronous(boolean Asynchronous) {
        this.asynchronous = Asynchronous;
    }

    public SoundManagement getSoundManagement() {
        return this.soundManagement;
    }

    /**
     * Creates a new GameLoop.
     *
     * @param gamePanel
     *            the gamePanel
     * @param network
     *            the network instance
     */
    public GameLoop(GamePanel gamePanel, Network network, SoundManagement soundM) {
        this.gamePanel = gamePanel;
        tdNetwork = network;
        tdNetwork.addListener(this);
        this.soundManagement = soundM;

        network.makeContact();
    }

    /**
     * finds the PlayerContext.
     */
    private synchronized void findMyContext() {
        for (GameContext gc : contexts) {
            if (gc instanceof PlayerContext) {
                myContext = gc;
            }
        }
    }

    /**
     * some configurations before the gameloop starts.
     */
    public void init() {
        logger.info("GameLoop init...");
        contextSetup();
        findMyContext();
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
            if (roundID > maxRound) {
                Thread.yield();
                continue;
            }
            if (maxRound_old != maxRound) {
                if ((roundID + 1) < maxRound_old) {
                    int skips = 0;
                    while (((roundID + 1) < maxRound_old) && (skips < MAX_FRAME_SKIPS)) {
                        gameUpdate();
                        roundID++;
                        skips++;
                    }
                    gameUpdate(); // updates the gamestate
                    gameRender(); // paints new screen in a buffer
                    gamePanel.getBoardPanel().getStrategy().show(); // draw buffer to screen
                    roundID++;
                    continue;
                }
                maxRound_old = maxRound;
            }
            gameUpdate(); // updates the gamestate
            gameRender(); // paints new screen in a buffer
            gamePanel.getBoardPanel().getStrategy().show(); // draw buffer to screen
            roundID++;

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
                roundID++;
                skips++;
            }
        }
    }

    /**
     * Transfers the creeps from one Context to the next.
     */
    public void transferCreeps() {
        int size = contexts.size();

        for (int i = 0; i < size; i++) {
            ArrayList<Creep> transCopy = new ArrayList<Creep>(contexts.get(i).getTransfer());
            GameContext copyTo = null;
            if (this.getGameMode().equals(IConstants.Mode.TEAM2VS2)) {  // Send to same team, if dead or not
                int playerPosition = playersOrder.get(contexts.get(i).getPlayerId());
                if (playerPosition == 1) {
                    for (int j = 0; j < contexts.size(); j++) {
                        int matePosition = this.playersOrder.get(contexts.get(j).getPlayerId());
                        if (matePosition == 0) {
                            copyTo = this.contexts.get(j);
                        }
                    }
                    if (copyTo == null) {
                        System.out.println("Mate position detection failed");
                    }
                } else if (playerPosition == 3) {
                    for (int j = 0; j < contexts.size(); j++) {
                        int matePosition = this.playersOrder.get(contexts.get(j).getPlayerId());
                        if (matePosition == 2) {
                            copyTo = this.contexts.get(j);
                        }
                    }
                    if (copyTo == null) {
                        System.out.println("Mate position detection failed");
                    }
                } else {
                    copyTo = findNextContext(contexts.get(i));
                }
                // Don't check for dead
            } else if (this.getGameMode().equals(IConstants.Mode.ALLVSALL)) { // ALL vs ALL, send to self
                if (contexts.get(i).isDead()) {
                    continue; // Discard creeps if dead
                }
                copyTo = contexts.get(i); // Else copy to self
            } else { // Standard mode
                copyTo = findNextContext(contexts.get(i));
                // If some Playaer is dead, find other living Player
                while (copyTo.isDead()) {
                    copyTo = findNextContext(copyTo);
                }
            }

            for (Creep c : transCopy) {
                Creep copy = CreepFactory.createCreep(copyTo, c.getType());
                copy.setHealth(c.getHealth());
                copy.setBuildTime(c.getBuildTime());
                copy.setSenderId(c.getSenderId());
                copy.setPlayerID(c.getPlayerID());

                if (c.getSenderId() == copyTo.getPlayerId()) { // If sender and receiver the same Player
                    copyTo.getTransfer().add(copy);
                } else {
                    copy.setPlayerID(copyTo.getPlayerId());
                    copyTo.getCreeps().add(copy);
                    if (this.myContext.equals(copyTo)) {
                        if (this.myContext.getSoundManagement() != null) {
                            this.myContext.getSoundManagement().creepWarnSound(copy.getType());
                        }
                    }

                    /********************************
                     * Here is calculating of taked * lives from each Player *
                     ********************************/
                    if (myContext.getPlayerId() == c.getSenderId()) {
                        myContext.takedlives++;

                        LiveTakedMessage ltm = new LiveTakedMessage();
                        ltm.setCreepType(c.getType().name());
                        ltm.setFromPlayerId(c.getPlayerID());
                        ltm.setToPlayerId(copyTo.getPlayerId());
                        ltm.setSenderId(c.getSenderId());
                        ltm.setRoundId(roundID);

                        tdNetwork.sendMessage(ltm);
                    }
                }
                contexts.get(i).getTransfer().remove(c);
            }
        }
    }

    /**
     *
     * find the next context.
     *
     * @param start
     *            startcontext
     * @return found context
     */
    public GameContext findNextContext(GameContext start) {
        GameContext found = null;
        BoardLocation loc = start.getLocation();
        while (found == null) {
            loc = getSuccessor(loc);
            found = findContextByLocation(loc);
        }
        return found;
    }

    /**
     * find a context by the location.
     *
     * @param loc
     *            the location
     * @return found context
     */
    private GameContext findContextByLocation(BoardLocation loc) {
        for (GameContext gc : contexts) {
            if (gc.getLocation().equals(loc)) {
                return gc;
            }
        }
        return null;
    }

    /**
     * get the Successor.
     *
     * @param loc
     *            location
     * @return location
     */
    private BoardLocation getSuccessor(BoardLocation loc) {
        switch (loc) {
            case TOPLEFT:
                return BoardLocation.TOPRIGHT;
            case TOPRIGHT:
                return BoardLocation.BOTTOMRIGHT;
            case BOTTOMRIGHT:
                return BoardLocation.BOTTOMLEFT;
            case BOTTOMLEFT:
                return BoardLocation.TOPLEFT;
            default:
                return null;
        }
    }

    /**
     * Instatiates the game context for all orderedPlayers.
     */
    private void contextSetup() {
        logger.info("Creating the context...");
        int cnt = 0;
        GameContext.BoardLocation loc = null;
        GameContext context = null;
        GameContext.setWinningPosition(0);
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
                logger.info("Own context (id="+id+") created");
                context = new PlayerContext(loc, tdNetwork, soundManagement, map, this, id, players.get(id));
                gamePanel.getBoardPanel().addMouseMotionListener(context.getGameBoard());
                gamePanel.getBoardPanel().addMouseListener(context.getGameBoard());
                gamePanel.getGameInfoPanel().addPlayerContext((PlayerContext) context);
            } else {
                logger.info("Opponent context (id="+id+") created");
                context = new OpponentContext(loc, tdNetwork, map, this, id, players.get(id));
                gamePanel.getGameInfoPanel().addOpponentContext((OpponentContext) context);
            }
            contexts.add(context);

            context.fireCreditsChangedEvent();
            context.fireIncomeChangedEvent();
            context.fireLivesChangedEvent();
        }
    }

    /**
     * Receive messages from the server.
     *
     * @param m
     *            the ServerMessage
     */
    public void update(ServerMessage m) {
        if (m instanceof StartGameMessage) {
            if (!this.isAlive()) {
                this.start();
            }
        } else if (m instanceof ServerChatMessage) {
            ServerChatMessage mMChat = (ServerChatMessage) m;
            gamePanel.getChatPanel().setMessage(mMChat.getPlayerName(),
                    mMChat.getMessage());
        } else if (m instanceof PlayerQuitMessage) {
            PlayerQuitMessage pqm = (PlayerQuitMessage) m;
            for (GameContext con : contexts) {
                if (pqm.getPlayerName().equals(con.getPlayerName())) {
                    con.setLives(0);
                    con.sendDeathMessage();
                    con.fireLivesChangedEvent();
                }
            }
        }
        if (m instanceof RoundMessage) {
            maxRound = ((RoundMessage) m).getRoundId();
        }
    }

    /**
     * updates the income every INCOME_TIME.
     */
    private synchronized void updateIncome() {

        for (GameContext gc : contexts) {
            gc.setCredits(gc.getCredits() + gc.getIncome());
        }
        this.soundManagement.cashSound();

        logger.info("new income");
    }

    /**
     * updates the gamestate.
     */
    private synchronized void gameUpdate() {

        if (gameOver) {
            return;
        }

        transferCreeps();

        int deadCount = 0;
        for (GameContext gc : contexts) {
            gc.update(roundID);
            if (gc.isDead()) {
                gc.sendDeathMessage();
                deadCount++;
                for (Tower t : gc.getTowers()) {
                    t.setCoolDownNow(t.getCoolDown() - 1);
                }
            }
        }

        if (this.getGameMode().equals(IConstants.Mode.TEAM2VS2)) {
            GameContext[] ordererdPlayers = this.getOrderedPlayers();
            if (ordererdPlayers != null) { // Becomes != null when context is completely set up
                if ((ordererdPlayers[0].isDead() && ordererdPlayers[1].isDead()) || (ordererdPlayers[2].isDead() && ordererdPlayers[3].isDead())) {
                    gameOver = true;
                }
            }
        } else {
            if (deadCount >= contexts.size() - 1) {
                gameOver = true;
            }
        }

        if (gameOver) {
            // Stop the thread.
            if (!myContext.isDead()) {
                // send gameover Message to the server
                ((PlayerContext) myContext).sendDeathMessage();
                // set the context to dead so that no more
                // user actions are allowed
                myContext.sendDeathMessage();
            }
            logger.info("DEAD!");
            this.running = false;
        }

        if (myContext.getStartCounter() >= 0) {
            if (roundID % (1000 / TICK_MS) == 0) {
                gameRender();
                gamePanel.getBoardPanel().getStrategy().show();
                myContext.setStartCounter(myContext.getStartCounter() - 1);
            }
        }
        if (myContext.getStartCounter() < 0) {
            // only do that if player is alive
            if (!myContext.isDead()) {
                // updates income everu INCOME_TIME millis
                if (roundID % (INCOME_TIME / TICK_MS) == 0) {
                    updateIncome();
                }
                // countdown for next income
                if (roundID % (1000 / TICK_MS) == 0) {

                    if (--incomeCounter < 1) {
                        // milis>>secs
                        incomeCounter = IConstants.INCOME_TIME / 1000;
                    }

                    gamePanel.getGameInfoPanel().setIncomeCounter(incomeCounter);
                }
            }
        }
    }

    /**
     * prepares the game for painting.
     */
    private void gameRender() {
        Graphics2D g2 = gamePanel.getBoardPanel().getImgGraphics();
        for (GameContext gc : contexts) {
            gc.paint(g2);
        }
    }

    /**
     * @return the orderedPlayers
     */
    public TreeMap<Integer, String> getPlayers() {
        return players;
    }

    /**
     * @return the player GameContext
     */
    public GameContext getGameContext(int PlayerID) {
        GameContext gcR = null;
        for (GameContext gc : contexts) {
            if (gc.getPlayerId() == PlayerID) {
                gcR = gc;
            }
        }
        return gcR;
    }

    /**
     * @param orderedPlayers
     *            the orderedPlayers to set
     */
    public void setPlayers(TreeMap<Integer, String> players) {
        this.players = players;
    }

    /**
     * @return the map
     */
    public Map getMap() {
        return map;
    }

    /**
     * @param map
     *            the map to set
     */
    public void setMap(Map map) {
        this.map = map;
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * @param game mod
     */
    public void setGameMode(IConstants.Mode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * @return the game mod
     */
    public IConstants.Mode getGameMode() {
        return this.gameMode;
    }

    /**
     * @param running
     *            the running to set
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * @return the playersOrder
     */
    public TreeMap<Integer, Integer> getPlayersOrder() {
        return playersOrder;
    }

    /**
     * Get board location ordered ordererdPlayers array.
     *
     * @return
     */
    public GameContext[] getOrderedPlayers() {
        if (orderedPlayers != null) {
            return orderedPlayers;
        }
        orderedPlayers = new GameContext[4];
        Iterator<Integer> i = playersOrder.keySet().iterator();
        int n = 0;
        while (i.hasNext()) {
            Integer iPlayerId = i.next();
            Integer playerLocation = playersOrder.get(iPlayerId);
            orderedPlayers[playerLocation] = this.getGameContext(iPlayerId);
            System.out.println("DEBUG: " + playerLocation + "=" + iPlayerId + " (" + orderedPlayers[playerLocation] + ")");
            if (orderedPlayers[playerLocation] == null) {
                this.orderedPlayers = null;
                System.out.println("DEBUG: Reset");
                return null;
            }
            n++;
        }
        if (n < 4) {
            orderedPlayers = null;
            System.out.println("DEBUG: Reset");
            return null;
        }
        return orderedPlayers;
    }

    /**
     * @param playersOrder
     *            the playersOrder to set
     */
    public void setPlayersOrder(TreeMap<Integer, Integer> playersOrder) {
        this.playersOrder = playersOrder;
    }

    public long getRoundID() {
        return roundID;
    }
}

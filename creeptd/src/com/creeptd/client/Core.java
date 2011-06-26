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
package com.creeptd.client;

import com.creeptd.client.i18n.Translator;
import java.awt.Dimension;
import java.util.Stack;
import java.util.logging.Logger;

import com.creeptd.client.network.Network;
import com.creeptd.client.panel.GameScreen;
import com.creeptd.client.panel.LoginPanel;
import com.creeptd.client.sound.SoundManagement;
import com.creeptd.client.util.JNLP;
import com.creeptd.common.Constants;
import com.creeptd.common.messages.server.GameDescription;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;

import java.util.Random;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import static com.creeptd.client.i18n.Translator.*;

/**
 * The core of the game. This class manages the displayed screens and holds the
 * network connection.
 * 
 * @author Philipp
 */
public class Core extends JPanel {

    private static Core instance = null;
    private static Logger logger = Logger.getLogger("com.creeptd.client");
    public static final int HEIGHT = 700;
    public static final int WIDTH = 933;
    public static final Dimension SCREENSIZE = new Dimension(WIDTH, HEIGHT);
    private static final long serialVersionUID = 1L;
    protected static String host;
    protected static int port;
    private boolean gamecreator = false;
    private final Stack<GameScreen> screens = new Stack<GameScreen>();
    protected static Network network;
    private String playerName;
    private Integer playerPoints = 0;
    private Integer playerSkill = 1500;
    private Integer playerId;
    private GameDescription activeGame;
    private SoundManagement soundManagement;
    private Object creator;
    private Translator translator;
    
    /**
     * Creates a new core instance.
     */
    public Core(Object creator) {
        this.creator = creator;
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        boolean localMode = host.equals("localhost") || host.equals("127.0.0.1");
        if (!localMode && !JNLP.isAvailable()) {
            JOptionPane.showMessageDialog(this, _("Sorry, JNLP functionality seems not to be available. Please update your Java Runtime Environment (JRE): www.java.com"), _("An error occured"), JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        network = new Network(host, port, localMode);
        soundManagement = new SoundManagement();
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);
        instance = this;
        this.translator = new Translator();
        ToolTipManager.sharedInstance().setInitialDelay(500);
    }

    public static Core getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return logger;
    }

    public int getCreatorX() {
        if (creator instanceof JFrame) {
            return ((JFrame) creator).getX();
        } else {
            return ((JApplet) creator).getX();
        }
    }

    public int getCreatorY() {
        if (creator instanceof JFrame) {
            return ((JFrame) creator).getY();
        } else {
            return ((JApplet) creator).getY();
        }
    }

    public Image getIconImage() {
        return Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("com/creeptd/client/resources/panel/icon.gif"));
    }

    /**
     * Init the core.
     */
    public void init() {
        this.pushScreen(new LoginPanel());
    }

    /**
     * Adds a screen to the top of the stack and makes it visible. If there is a
     * screen on the stack, its end method is called.
     *
     * @param screen
     *            the game screen to add
     */
    public void pushScreen(GameScreen screen) {

        if (!this.screens.isEmpty()) {
            this.screens.peek().end();
            this.remove(this.screens.peek());
        }

        screen.initialize(this);

        // set the bounds to let the screen know he should paint himself...
        screen.setBounds(0, 0, this.getWidth(), this.getHeight());

        this.add(screen);

        screen.requestFocusInWindow();

        this.screens.push(screen);

        screen.validate();
        screen.repaint();

        screen.start();
    }

    /**
     * Shows the current Screen.
     *
     * @return GameScreen current screen
     */
    public GameScreen peekScreen() {
        return this.screens.peek();
    }

    /**
     * Removes a screen from the stack.
     */
    public void popScreen() {

        if (!this.screens.isEmpty()) {

            GameScreen popped = this.screens.peek();
            this.remove(this.screens.pop());

            this.add(this.screens.peek());
            this.screens.peek().setBounds(0, 0, this.getWidth(),
                    this.getHeight());
            this.screens.peek().start();
            this.screens.peek().validate();
            this.screens.peek().initialize(this);

            popped.end();
        }

        this.repaint();
    }

    /**
     * Clear the screen.
     */
    public void clearScreen() {
        while (!this.screens.empty()) {
            GameScreen pop = this.screens.peek();
            this.remove(pop);
            pop.end();
            this.screens.pop();
        }
    }

    /**
     * Remove actual screen from list and add a new screen to list
     */
    public void switchScreen(GameScreen screen) {
        if (!this.screens.isEmpty()) {
            GameScreen pop = this.screens.peek();
            this.remove(pop);
            pop.end();
            this.screens.pop();
        }

        screen.initialize(this);

        // set the bounds to let the screen know he should paint himself...
        screen.setBounds(0, 0, this.getWidth(), this.getHeight());

        this.add(screen);

        screen.requestFocusInWindow();

        this.screens.push(screen);

        screen.validate();
        screen.repaint();

        screen.start();
    }

    public Translator getTranslator() {
        return this.translator;
    }
    
    public void setLanguage(String lang) {
        this.translator = new Translator(lang);
    }

    /**
     * Getter for the network.
     *
     * @return the network
     */
    public Network getNetwork() {
        return network;
    }
    
    /**
     * Getter for the Soundmanagement in the core.
     *
     * @return SoundManagement
     */
    public SoundManagement getSoundManagement() {
        return soundManagement;
    }
    
    /**
     * @return the gamecreator
     */
    public boolean isGamecreator() {
        return this.gamecreator;
    }

    /**
     * @param gamecreator
     *            the gamecreator to set
     */
    public void setGamecreator(boolean gamecreator) {
        this.gamecreator = gamecreator;
    }

    /**
     * @return the playerName
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * @param playerName
     *            the playerName to set
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public int getPlayerSkill() {
        return this.playerSkill;
    }
    
    public void setPlayerSkill(int skill) {
        this.playerSkill = skill;
    }

    public Integer getPlayerPoints() {
        return playerPoints;
    }

    public void setPlayerPoints(Integer points) {
        this.playerPoints = points;
    }

    /**
     * @return the playerId
     */
    public Integer getPlayerId() {
        return this.playerId;
    }

    /**
     * @param playerId
     *            the playerId to set
     */
    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    /**
     * @return the activeGame
     */
    public GameDescription getActiveGame() {
        return activeGame;
    }

    /**
     * @param activeGame
     *            the activeGame to set
     */
    public void setActiveGame(GameDescription activeGame) {
        this.activeGame = activeGame;
    }

    /**
     * @return the version
     */
    public static String getVersion() {
        /* String version = null;

        InputStream inStream = Core.class.getResourceAsStream("version");
        try {
            if (inStream.available() > 0) {
                InputStreamReader inStreamReader = null;

                try {
                    inStreamReader = new InputStreamReader(inStream);
                } catch (Exception e) {
                    logger.info("IOException: " + e);
                    inStream.close();
                }

                try {
                    BufferedReader reader =
                            new BufferedReader(inStreamReader);
                    version = reader.readLine();
                    reader.close();
                } catch (IOException e) {
                    logger.info("IOException: " + e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (version == null) {
            version = "-unknown-";
        }
        return version; */
        return Constants.VERSION;
    }

    /**
     * Check if this is a LAN only version.
     *
     * @return true if LAN version, else false
     */
    public static boolean isLANVersion() {
        return getVersion().indexOf("LAN") >= 0;
    }

    /**
     * Get the UID.
     *
     * @return The UID
     */
    public final String getUid() {
        if (com.creeptd.client.util.JNLP.isAvailable()) {
            String uid = com.creeptd.client.util.JNLP.getValue("uid");
            if (uid == null) {
                uid = new Long((new Random()).nextLong()).toString();
                com.creeptd.client.util.JNLP.setValue("uid", uid);
            }
            return uid;
        } else {
            return this.network.getMACAddress();
        }
    }
}

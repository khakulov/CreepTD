package com.creeptd.client;

import java.awt.Dimension;
import java.util.Stack;

import com.creeptd.client.network.Network;
import com.creeptd.client.network.NetworkImpl;
import com.creeptd.client.panel.Screen;
import com.creeptd.client.panel.LoginScreen;
import com.creeptd.client.sound.SoundManagement;
import com.creeptd.common.messages.server.GameDescription;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URI;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

public class Core extends JPanel {

    private static Core instance = null;
    public static final int HEIGHT = 700;
    public static final int WIDTH = 940;
    public static final Dimension SCREENSIZE = new Dimension(WIDTH, HEIGHT);
    private static final long serialVersionUID = 1L;
    private static String host;
    private static int port;
    private final Stack<Screen> screens = new Stack<Screen>();
    private String playerName;
    private Integer playerPoints = 0;
    private Integer playerSkill = 1500;
    private Integer playerId;
    private GameDescription activeGame;
    private SoundManagement soundManagement;
    private Network network;
	private Client client;

    /**
     * Creates a new core instance.
     */
    public Core(Client client) {
        instance = this;

        this.client = client;
        this.network = new NetworkImpl(host, port);
        this.soundManagement = new SoundManagement();
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);
        ToolTipManager.sharedInstance().setInitialDelay(500);
    }

    public static Core getInstance() {
        return instance;
    }

    public Client getClient() {
		return client;
	}

    public int getCreatorX() {
        return this.client.getX();
    }

    public int getCreatorY() {
    	return this.client.getY();
    }

    /**
     * Open URL in browser.
     *
     * @param url The URL to open
     * @return true on success, else false
     */
    public boolean openBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    public Image getIconImage() {
        return Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("com/creeptd/client/resources/panel/icon.gif"));
    }

    /**
     * Init the core.
     */
    public void init() {
        this.pushScreen(new LoginScreen());
    }

    /**
     * Adds a screen to the top of the stack and makes it visible. If there is a
     * screen on the stack, its end method is called.
     *
     * @param screen
     *            the game screen to add
     */
    public void pushScreen(Screen screen) {

        if (!this.screens.isEmpty()) {
            this.screens.peek().end();
            this.remove(this.screens.peek());
        }

        screen.initialize();

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
    public Screen peekScreen() {
        return this.screens.peek();
    }

    /**
     * Removes a screen from the stack.
     */
    public void popScreen() {

        if (!this.screens.isEmpty()) {

            Screen popped = this.screens.peek();
            this.remove(this.screens.pop());

            this.add(this.screens.peek());
            this.screens.peek().setBounds(0, 0, this.getWidth(),
                    this.getHeight());
            this.screens.peek().start();
            this.screens.peek().validate();
            this.screens.peek().initialize();

            popped.end();
        }

        this.repaint();
    }

    /**
     * Clear the screen.
     */
    public void clearScreen() {
        while (!this.screens.empty()) {
            Screen pop = this.screens.peek();
            this.remove(pop);
            pop.end();
            this.screens.pop();
        }
    }

    /**
     * Remove actual screen from list and add a new screen to list
     */
    public void switchScreen(Screen screen) {
        if (!this.screens.isEmpty()) {
            Screen pop = this.screens.peek();
            this.remove(pop);
            pop.end();
            this.screens.pop();
        }

        screen.initialize();

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
     * Setter for the network.
     *
     * @return the network
     */
    public Network setNetwork(Network network) {
        return this.network = network;
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

    public String getPlayerName() {
        return this.playerName;
    }

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

    public Integer getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public GameDescription getActiveGame() {
        return activeGame;
    }

    public void setActiveGame(GameDescription activeGame) {
        this.activeGame = activeGame;
    }

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		Core.host = host;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		Core.port = port;
	}
}

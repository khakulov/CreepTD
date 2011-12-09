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
package com.creeptd.client.panel;

import static com.creeptd.client.i18n.Translator._;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Deque;
import java.util.Random;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.creeptd.client.Core;
import com.creeptd.client.network.MessageListener;
import com.creeptd.client.network.Network;
import com.creeptd.client.network.NetworkFakeImpl;
import com.creeptd.client.network.NetworkImpl;
import com.creeptd.client.panel.common.Button;
import com.creeptd.client.panel.game.RunningGameScreen;
import com.creeptd.client.util.JNLP;
import com.creeptd.common.Constants;
import com.creeptd.common.Password;
import com.creeptd.common.messages.client.LoginMessage;
import com.creeptd.common.messages.client.ServerOnlineRequestMessage;
import com.creeptd.common.messages.server.LoginResponseMessage;
import com.creeptd.common.messages.server.ServerMessage;
import com.creeptd.common.messages.server.ServerOnlineResponseMessage;

/**
 * LoginPanel at the beginning of the game.
 * 
 * @author sven
 */
public class LoginScreen extends Screen implements MessageListener {

    private static final long serialVersionUID = 1L;
    private JLabel nameLabel;
    private JLabel passwordField;
    private JTextField name;
    private JPasswordField password;
    private Button loginButton;
    private Button loadButton;
    private JLabel versionLabel;
    private JLabel serverOnlineLabel;

    private String loginName = null;
    private String loginPass = null;

    /**
     * constructor for LoginPanel.
     */
    public LoginScreen() {
        this.setLayout(null);
        this.setBackground(Color.BLACK);

        UIManager.put("TextField.border", new EmptyBorder(2, 2, 2, 2));
        UIManager.put("PasswordField.border", new EmptyBorder(2, 2, 2, 2));
        UIManager.put("EditorPane.border", new EmptyBorder(2, 2, 2, 2));

        nameLabel = new JLabel(_("Username"));
        nameLabel.setBounds(350, 250, 200, 30);
        nameLabel.setForeground(Color.GRAY);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        name = new JTextField();
        name.setBounds(350, 230, 200, 25);
        name.setFont(new Font("Arial", Font.PLAIN, 12));
        this.setGameScreenFocus(name);

        passwordField = new JLabel(_("Password"));
        passwordField.setBounds(350, 330, 200, 25);
        passwordField.setForeground(Color.GRAY);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 11));

        password = new JPasswordField();
        password.setBounds(350, 305, 200, 25);
        password.setEchoChar('*');
        password.setFont(new Font("Arial", Font.PLAIN, 12));
        if (name.getText().length() > 0) {
            this.setGameScreenFocus(password);
        }

        loginButton = new Button(_("Login"));
        loginButton.setBounds(350, 380, 200, 25);
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.GREEN);
        if (name.getText().length() > 0 && password.getPassword().length > 0) {
            this.setGameScreenFocus(loginButton);
        }

        loadButton = new Button(_("Load Game"));
        loadButton.setBounds(820, 20, 100, 25);
        loadButton.setBackground(Color.BLACK);
        loadButton.setForeground(Color.GREEN);
        loadButton.addActionListener(new ActionListener() {
            @SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
            	Object[] objects = (Object[])JNLP.openFile();
                Deque<ServerMessage> list = (Deque<ServerMessage>) objects[4];
				Core.getInstance().getNetwork().disconnect();
                Network network = new NetworkFakeImpl(list);
				Core.getInstance().setNetwork(network);
            	
                RunningGameScreen gamePanel = new RunningGameScreen();
                Core.getInstance().pushScreen(gamePanel);
                gamePanel.getGame().setMap((Constants.Map)objects[0]);
                gamePanel.getGame().setGameMode((Constants.Mode)objects[1]);
                gamePanel.getGame().setPlayers((TreeMap<Integer, String>)objects[2]);
                gamePanel.getGame().setPlayersOrder((TreeMap<Integer, Integer>)objects[3]);
                gamePanel.getGame().start();

            	network.connect();
            }
        });

        versionLabel = new JLabel();
        versionLabel.setText(_("Version")+": " + Constants.VERSION);
        versionLabel.setBounds(20, 665, 200, 25);
        versionLabel.setForeground(Color.GRAY);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        serverOnlineLabel = new JLabel();
        serverOnlineLabel.setBounds(450, 665, 460, 25);
        serverOnlineLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        serverOnlineLabel.setFont(new Font("Arial", Font.BOLD, 11));
        serverOnlineLabel.setText(_("Checking server..."));
        serverOnlineLabel.setForeground(Color.GRAY);

        this.add(name);
        this.add(nameLabel);
        this.add(password);
        this.add(passwordField);
        this.add(loginButton);
        this.add(loadButton);
        this.add(versionLabel);
        this.add(serverOnlineLabel);

        name.requestFocus();

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginProcess();
            }
        });

        KeyAdapter loginKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }
                loginProcess();
            }
        };
        password.addKeyListener(loginKeyAdapter);
        name.addKeyListener(loginKeyAdapter);
        loginButton.addKeyListener(loginKeyAdapter);
    }
   
    /**
     * Paint login panel.
     *
     * @param g Graphics object
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        java.net.URL imageURL = getClass().getClassLoader().getResource("com/creeptd/client/resources/panel/splashscreen.jpg");
        Image img = new ImageIcon(imageURL).getImage();
        g.drawImage(img, 0, 0, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() {
        this.password.setText("");
        Core.getInstance().getNetwork().removeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
    	if (!(Core.getInstance().getNetwork() instanceof NetworkImpl))
    		Core.getInstance().setNetwork( new NetworkImpl(Core.getHost(), Core.getPort()));
    	Core.getInstance().getNetwork().addListener(this);
        this.loginButton.setEnabled(true);
        serverOnlineProcess();
    }

    /**
     * {@inheritDoc}
     */
    public void update(ServerMessage m) {
        if (m instanceof LoginResponseMessage) {
            LoginResponseMessage response = (LoginResponseMessage) m;
            if (response.getResponseType() == Constants.ResponseType.ok) {
            	Core.getInstance().pushScreen(new LobbyScreen());
            } else if (response.getResponseType() == Constants.ResponseType.version) {
                errorDialog(_("Please download the latest version.\n\nIf that doesn't work, you may need to clear the Java WebStart cache:\nGo to your system panel, select \"Java\", \"Show temporary files\" and delete the old version of the game."));
                loginButton.setEnabled(true);
            } else {
                errorDialog(_("Login failed"));
                loginButton.setEnabled(true);
            }
        } else if (m instanceof ServerOnlineResponseMessage) {
            ServerOnlineResponseMessage sorm = (ServerOnlineResponseMessage) m;
            if (sorm.isCorrectVersion()) {
                this.serverOnlineLabel.setText(_("Server is up and running!"));
                this.serverOnlineLabel.setForeground(Color.GREEN);
            } else {
                this.serverOnlineLabel.setText(_("Server is up but your version differs!"));
                this.serverOnlineLabel.setForeground(Color.YELLOW);
            }
            Core.getInstance().getNetwork().disconnect();
        }

    }

    /**
     * Dialog to show errors in the same colours than GUI.
     * @param msg msg
     */
    public void errorDialog(String msg) {
        UIManager.put("OptionPane.background", Color.BLACK);
        UIManager.put("OptionPane.JButton.setForground", Color.BLACK);
        UIManager.put("Panel.background", Color.BLACK);
        UIManager.put("OptionPane.messageForeground", Color.GREEN);
        JOptionPane.showMessageDialog(this, msg, _("An error occured"), JOptionPane.ERROR_MESSAGE);
    }

    /**
     * login.
     */
    public void loginProcess() {
    	Core.getInstance().getNetwork().connect();
        if (name.getText().length() == 0 || String.valueOf(password.getPassword()).length() == 0) {
            errorDialog(_("The login failed. Please check your username and your password!"));
            return;
        }

        LoginMessage loginMessage = new LoginMessage();
        this.loginName = name.getText();
        this.loginPass = Password.encodePassword(String.valueOf(password.getPassword()));
        loginMessage.setVersion(Constants.VERSION);
        loginMessage.setUsername(loginName);
        Core.getInstance().setPlayerName(loginName);
        loginMessage.setPassword(loginPass);
        loginMessage.setUid(new Long((new Random()).nextLong()).toString());
        loginMessage.setLanguage("en_US");
        Core.getInstance().getNetwork().sendMessage(loginMessage);
        loginButton.setEnabled(false);
    }

    public void serverOnlineProcess() {
        new Thread() {
            @Override
            public void run() {
                if (Core.getInstance().getNetwork().connect()) {
                    ServerOnlineRequestMessage sorm = new ServerOnlineRequestMessage();
                    sorm.setVersion(Constants.VERSION);
                    Core.getInstance().getNetwork().sendMessage(sorm);
                } else {
                    serverOnlineLabel.setText(_("Sorry, the server seems to be offline. Please try again later!"));
                }
            }
        }.start();
    }
}

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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.creeptd.client.Core;
import com.creeptd.client.network.MessageListener;
import com.creeptd.common.Password;
import com.creeptd.common.IConstants;
import com.creeptd.common.messages.client.LoginRequestMessage;
import com.creeptd.common.messages.client.ServerOnlineRequestMessage;
import com.creeptd.common.messages.server.LoginResponseMessage;
import com.creeptd.common.messages.server.ServerMessage;
import com.creeptd.common.messages.server.ServerOnlineResponseMessage;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * LoginPanel at the beginning of the game.
 * 
 * @author sven
 */
public class LoginPanel extends GameScreen implements MessageListener {

    private static final long serialVersionUID = 1L;
    private JLabel logoImage;
    private JLabel nameLabel;
    private JLabel passwordField;
    private JTextField name;
    private JPasswordField password;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel registerLabel;
    private JLabel versionLabel;
    private JLabel serverOnlineLabel;

    /**
     * constructor for LoginPanel.
     */
    public LoginPanel() {
        this.setLayout(null);
        this.setBackground(Color.BLACK);

        UIManager.put("TextField.border", new EmptyBorder(2, 2, 2, 2));
        UIManager.put("PasswordField.border", new EmptyBorder(2, 2, 2, 2));
        UIManager.put("EditorPane.border", new EmptyBorder(2, 2, 2, 2));

        java.net.URL imageURL = getClass().getClassLoader().getResource("com/creeptd/client/resources/panel/creeptd-logo.jpg");
        logoImage = new JLabel();
        logoImage.setBounds(225, 50, 450, 100);
        logoImage.setText("<html><img src=\"" + imageURL + "\"></html>");

        nameLabel = new JLabel("Username");
        nameLabel.setBounds(350, 220, 200, 30);
        nameLabel.setForeground(Color.GRAY);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        name = new JTextField();
        name.setBounds(350, 200, 200, 25);
        name.setFont(new Font("Arial", Font.PLAIN, 12));
        this.setGameScreenFocus(name);

        if (!Core.isLANVersion()) {
            passwordField = new JLabel("Password");
            passwordField.setBounds(350, 300, 200, 25);
            passwordField.setForeground(Color.GRAY);
            passwordField.setFont(new Font("Arial", Font.PLAIN, 11));

            password = new JPasswordField();
            password.setBounds(350, 275, 200, 25);
            password.setEchoChar('*');
            password.setFont(new Font("Arial", Font.PLAIN, 12));
        }

        loginButton = new JButton("Login");
        loginButton.setBounds(350, 350, 200, 25);
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.GREEN);

        registerButton = new JButton("Create an account");
        registerButton.setBounds(350, 550, 200, 25);
        registerButton.setBackground(Color.BLACK);
        registerButton.setForeground(Color.GREEN);

        registerLabel = new JLabel();
        registerLabel.setText("Don't have an account yet?");
        registerLabel.setBounds(350, 500, 200, 25);
        registerLabel.setForeground(Color.GREEN);
        registerLabel.setFont(new Font("Arial", Font.BOLD, 14));

        versionLabel = new JLabel();
        versionLabel.setText("Version: " + Core.getVersion());
        versionLabel.setBounds(20, 665, 200, 25);
        versionLabel.setForeground(Color.GRAY);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        serverOnlineLabel = new JLabel();
        serverOnlineLabel.setBounds(510, 665, 400, 25);
        serverOnlineLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        serverOnlineLabel.setFont(new Font("Arial", Font.BOLD, 11));
        if (!Core.isLANVersion()) {
            serverOnlineLabel.setText("Checking server...");
        } else {
            serverOnlineLabel.setText("LAN Version");
        }
        serverOnlineLabel.setForeground(Color.GRAY);

        this.add(logoImage);
        this.add(name);
        this.add(nameLabel);
        if (!Core.isLANVersion()) { // No registration for LAN games
            this.add(password);
            this.add(passwordField);
            this.add(registerButton);
            this.add(registerLabel);
        } else {
            this.loginButton.setText("Go");
        }
        this.add(loginButton);
        this.add(versionLabel);
        this.add(serverOnlineLabel);
        name.requestFocus();

        ActionListener a1 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                loginProcess();
            }
        };
        loginButton.addActionListener(a1);

        KeyAdapter loginKeyAdapter = new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }

                loginProcess();
            }
        };
        if (!Core.isLANVersion()) {
            password.addKeyListener(loginKeyAdapter);
        }
        name.addKeyListener(loginKeyAdapter);
        loginButton.addKeyListener(loginKeyAdapter);

        ActionListener a2 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                getCore().pushScreen(new RegisterPanel());
            }
        };
        registerButton.addActionListener(a2);

        registerButton.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                getCore().pushScreen(new RegisterPanel());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() {
        getCore().getNetwork().removeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        getCore().getNetwork().addListener(this);
        this.loginButton.setEnabled(true);
        if (!Core.isLANVersion()) {
            this.password.setText("");
            serverOnlineProcess();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void update(ServerMessage m) {
        if (m instanceof LoginResponseMessage) {
            LoginResponseMessage response = (LoginResponseMessage) m;
            if (response.getResponseType() == IConstants.ResponseType.ok) {
                getCore().pushScreen(new GameLobby());
            } else if (response.getResponseType() == IConstants.ResponseType.version) {
                errorDialog("Wrong version \u2013 Please download the latest version." + "\n\n" + "If that doesn't work, you may need to clear the Java WebStart cache:\nGo to your system panel, select \"Java\", \"Show temporary files\" and clear your copy of the game.");
                loginButton.setEnabled(true);
            } else {
                errorDialog("Login failed");
                loginButton.setEnabled(true);
            }
        } else if (m instanceof ServerOnlineResponseMessage) {
            ServerOnlineResponseMessage sorm = (ServerOnlineResponseMessage) m;
            if (sorm.isCorrectVersion()) {
                this.serverOnlineLabel.setText("Server is up and running!");
                this.serverOnlineLabel.setForeground(Color.GREEN);
            } else {
                this.serverOnlineLabel.setText("Server is up but your version differs!");
                this.serverOnlineLabel.setForeground(Color.YELLOW);
            }
            getCore().getNetwork().shutdown();
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
        JOptionPane.showMessageDialog(this, msg, "login error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * login.
     */
    public void loginProcess() {
        getCore().getNetwork().makeContact();
        if (name.getText().length() == 0 || String.valueOf(
                password.getPassword()).length() == 0) {
            errorDialog("Login failed");
            return;
        }

        LoginRequestMessage loginMessage = new LoginRequestMessage();
        loginMessage.setVersion(Core.getVersion());
        loginMessage.setUsername(name.getText());
        this.getCore().setPlayerName(name.getText());
        loginMessage.setPassword(Password.encodePassword(String.valueOf(password.getPassword())));
        loginMessage.setMacaddress(getCore().getNetwork().getMACAddress());
        getCore().getNetwork().sendMessage(loginMessage);
        loginButton.setEnabled(false);
    }

    public void serverOnlineProcess() {
        new Thread() {
            public void run() {
                if (getCore().getNetwork().makeContact()) {
                    ServerOnlineRequestMessage sorm = new ServerOnlineRequestMessage();
                    sorm.setVersion(Core.getVersion());
                    getCore().getNetwork().sendMessage(sorm);
                } else {
                    serverOnlineLabel.setText("Sorry, the server seems to be offline. Please try again later!");
                }
            }
        }.start();
    }
}

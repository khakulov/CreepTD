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
import com.creeptd.common.Constants;
import com.creeptd.common.messages.client.LoginRequestMessage;
import com.creeptd.common.messages.client.ServerOnlineRequestMessage;
import com.creeptd.common.messages.server.LoginResponseMessage;
import com.creeptd.common.messages.server.ServerMessage;
import com.creeptd.common.messages.server.ServerOnlineResponseMessage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import static com.creeptd.client.i18n.Translator.*;

/**
 * LoginPanel at the beginning of the game.
 * 
 * @author sven
 */
public class LoginPanel extends GameScreen implements MessageListener {

    private static final long serialVersionUID = 1L;
    private JLabel nameLabel;
    private JLabel passwordField;
    private JTextField name;
    private JPasswordField password;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel registerLabel;
    private JLabel versionLabel;
    private JLabel serverOnlineLabel;
    private JComboBox languageSelect;

    /**
     * constructor for LoginPanel.
     */
    public LoginPanel() {
        this.setLayout(null);
        this.setBackground(Color.BLACK);

        UIManager.put("TextField.border", new EmptyBorder(2, 2, 2, 2));
        UIManager.put("PasswordField.border", new EmptyBorder(2, 2, 2, 2));
        UIManager.put("EditorPane.border", new EmptyBorder(2, 2, 2, 2));

        languageSelect = new JComboBox();
        languageSelect.setBounds(750, 20, 163, 30);
        languageSelect.setForeground(Color.WHITE);
        languageSelect.setBackground(Color.BLACK);
        languageSelect.setFont(new Font("Arial", Font.PLAIN, 11));
        languageSelect.setBorder(new LineBorder(Color.GRAY, 1));
        Map<String,String> langs = Core.getInstance().getTranslator().getAvailableLanguages();
        Iterator<String> i = langs.keySet().iterator();
        int n=0;
        while (i.hasNext()) {
            String key = i.next();
            String value = langs.get(key);
            JLabel label = new JLabel();
            label.setText(value);
            URL imgURL = Core.getInstance().getTranslator().getLanguageIconURL(key);
            languageSelect.addItem("<html>&nbsp;<img src="+imgURL+"> <b>"+value+"</b></html>");
            if (Core.getInstance().getTranslator().getLanguageKey().equals(key)) {
                languageSelect.setSelectedIndex(n);
            }
            n++;
        }
        languageSelect.revalidate();
        languageSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String lang = (String) languageSelect.getSelectedItem();
                int posEnd = lang.lastIndexOf("</b>");
                int posStart = lang.indexOf("<b>")+3;
                String langName = lang.substring(posStart, posEnd);
                Core.getInstance().getTranslator().setLanguageByName(langName);
                String myName = name.getText();
                String myPass = new String(password.getPassword());
                Core.getInstance().clearScreen();
                LoginPanel newPanel = new LoginPanel();
                newPanel.name.setText(myName);
                newPanel.password.setText(myPass);
                Core.getInstance().pushScreen(newPanel);
            }
        });

        nameLabel = new JLabel(_("Username"));
        nameLabel.setBounds(350, 250, 200, 30);
        nameLabel.setForeground(Color.GRAY);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        name = new JTextField();
        name.setBounds(350, 230, 200, 25);
        name.setFont(new Font("Arial", Font.PLAIN, 12));
        this.setGameScreenFocus(name);

        if (!Core.isLANVersion()) {
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
        }

        loginButton = new JButton(_("Login"));
        loginButton.setBounds(350, 380, 200, 25);
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.GREEN);
        if (name.getText().length() > 0 && password.getPassword().length > 0) {
            this.setGameScreenFocus(loginButton);
        }

        registerButton = new JButton(_("Create an account"));
        registerButton.setBounds(350, 550, 200, 25);
        registerButton.setBackground(Color.BLACK);
        registerButton.setForeground(Color.GREEN);

        registerLabel = new JLabel();
        registerLabel.setText(_("Don't have an account yet?"));
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerLabel.setBounds(300, 500, 300, 25);
        registerLabel.setForeground(Color.GREEN);
        registerLabel.setFont(new Font("Arial", Font.BOLD, 14));

        versionLabel = new JLabel();
        versionLabel.setText(_("Version")+": " + Core.getVersion());
        versionLabel.setBounds(20, 665, 200, 25);
        versionLabel.setForeground(Color.GRAY);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        serverOnlineLabel = new JLabel();
        serverOnlineLabel.setBounds(450, 665, 460, 25);
        serverOnlineLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        serverOnlineLabel.setFont(new Font("Arial", Font.BOLD, 11));
        if (!Core.isLANVersion()) {
            serverOnlineLabel.setText(_("Checking server..."));
        } else {
            serverOnlineLabel.setText(_("Also available online")+": www.creeptd.com");
        }
        serverOnlineLabel.setForeground(Color.GRAY);

        this.add(languageSelect);
        this.add(name);
        this.add(nameLabel);
        if (!Core.isLANVersion()) { // No registration for LAN games
            this.add(password);
            this.add(passwordField);
            this.add(registerButton);
            this.add(registerLabel);
        } else {
            this.loginButton.setText(_("Go"));
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

            @Override
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

            @Override
            public void keyPressed(KeyEvent e) {
                getCore().pushScreen(new RegisterPanel());
            }
        });

        createLogoCreeps();
    }

    /**
     * A logo creep.
     */
    private class LogoCreep {
        private Image img;
        private double rotation;

        /** Create logo creep from image with defined rotation.
         *
         * @param img The image
         * @param rotation Rotation
         */
        public LogoCreep(Image img, double rotation) {
            this.img = img;
            this.rotation = rotation;
        }

        /**
         *  Paint logo creep.
         *
         * @param g Graphics to paint to
         */
        public void paint(Graphics g) {
            int offset = (int) (logoThread.offset*20);
            Graphics2D g2d = (Graphics2D) g;
            int x = 450 + (int) ((offset+140)*Math.sin(rotation));
            int y = 314 + (int) ((offset+130)*Math.cos(rotation));
            AffineTransform af = new AffineTransform();
            af.setTransform(Math.cos(rotation), -Math.sin(rotation), Math.sin(rotation), Math.cos(rotation), x, y);
            g2d.drawImage(img, af, null);
            this.rotation += 0.010;
        }
    }

    /**
     * Logo creeps thread.
     *
     * Paints the logo creeps.
     */
    private class LogoCreepsThread extends Thread {
        private LoginPanel panel;
        private boolean running = true;
        /** Calculated offset position used by logo creeps */
        public double offset = 0;
        private double offset_exp = 0.01;

        /**
         * Create new logo creeps thread.
         *
         * @param panel The parent panel
         */
        private LogoCreepsThread(LoginPanel panel) {
            this.panel = panel;
            this.start();
        }

        /**
         * Internal run method.
         */
        @Override
        public void run() {
            while (this.running) {
                try {
                    Thread.sleep(25);
                    this.panel.repaint();
                    if (offset <= 0) {
                        offset_exp = 0.01;
                    }
                    offset += offset_exp;
                    if (offset >= 1) {
                        offset_exp = -0.01;
                    }
                } catch (Exception ex) {
                    //
                }
            }
        }

        /**
         * Stop the logo thread.
         */
        public void terminate() {
            this.running = false;
        }
    }

    /** Logo creeps used */
    private List<LogoCreep> logoCreeps = new LinkedList<LogoCreep>();
    /** Logo thread */
    private LogoCreepsThread logoThread;

    /**
     * Create the logo creeps.
     */
    private void createLogoCreeps() {
        double rotation = 0.0;
        for (int i=0; i<31; i++) {
            int creepId = new Random().nextInt(16)+1;
            java.net.URL imageURL = getClass().getClassLoader().getResource("com/creeptd/client/resources/creeps/"+creepId+".png");
            Image img = new ImageIcon(imageURL).getImage();
            logoCreeps.add(new LogoCreep(img, rotation));
            rotation += 0.2;
        }
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
     * Paint logo creeps to login panel.
     *
     * @param g Graphics object
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (LogoCreep logoCreep : logoCreeps) {
            logoCreep.paint(g);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() {
        this.password.setText("");
        getCore().getNetwork().removeListener(this);
        if (this.logoThread != null) {
            this.logoThread.terminate();
            this.logoThread = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        getCore().getNetwork().addListener(this);
        this.loginButton.setEnabled(true);
        if (!Core.isLANVersion()) {
            serverOnlineProcess();
        }
        this.logoThread = new LogoCreepsThread(this);
    }

    /**
     * {@inheritDoc}
     */
    public void update(ServerMessage m) {
        if (m instanceof LoginResponseMessage) {
            LoginResponseMessage response = (LoginResponseMessage) m;
            if (response.getResponseType() == Constants.ResponseType.ok) {
                getCore().pushScreen(new GameLobby());
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
        JOptionPane.showMessageDialog(this, msg, _("An error occured"), JOptionPane.ERROR_MESSAGE);
    }

    /**
     * login.
     */
    public void loginProcess() {
        getCore().getNetwork().makeContact();
        if (name.getText().length() == 0 || String.valueOf(password.getPassword()).length() == 0) {
            errorDialog(_("The login failed. Please check your username and your password!"));
            return;
        }

        LoginRequestMessage loginMessage = new LoginRequestMessage();
        loginMessage.setVersion(Core.getVersion());
        loginMessage.setUsername(name.getText());
        this.getCore().setPlayerName(name.getText());
        loginMessage.setPassword(Password.encodePassword(String.valueOf(password.getPassword())));
        loginMessage.setUid(getCore().getUid());
        getCore().getNetwork().sendMessage(loginMessage);
        loginButton.setEnabled(false);
    }

    public void serverOnlineProcess() {
        new Thread() {
            @Override
            public void run() {
                if (getCore().getNetwork().makeContact()) {
                    ServerOnlineRequestMessage sorm = new ServerOnlineRequestMessage();
                    sorm.setVersion(Core.getVersion());
                    getCore().getNetwork().sendMessage(sorm);
                } else {
                    serverOnlineLabel.setText(_("Sorry, the server seems to be offline. Please try again later!"));
                }
            }
        }.start();
    }
}

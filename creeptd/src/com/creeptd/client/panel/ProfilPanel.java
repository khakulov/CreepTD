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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.creeptd.client.network.MessageListener;
import com.creeptd.common.IConstants;
import com.creeptd.common.messages.client.DeleteRequestMessage;
import com.creeptd.common.messages.client.LogoutMessage;
import com.creeptd.common.messages.client.UpdateDataRequestMessage;
import com.creeptd.common.messages.server.DeleteResponseMessage;
import com.creeptd.common.messages.server.ServerMessage;
import com.creeptd.common.messages.server.UpdateDataResponseMessage;

/**
 * RegisterPanel to register users in the database.
 * 
 * @author sven
 * 
 */
public class ProfilPanel extends GameScreen implements MessageListener {

    /**
     *
     */
    private static final long serialVersionUID = 7804821539529169821L;
    private JLabel name;
    private JLabel password;
    private JLabel logoImage;
    private JLabel password2;
    private JLabel email;
    private JLabel oldPassword;
    private JTextField lName;
    private JTextField lEmail;
    private JPasswordField lPassword;
    private JPasswordField lPassword2;
    private JPasswordField loldPassword;
    private JButton confirm;
    private JButton back;
    private JButton delete;

    /**
     * constructor to initialize elements.
     */
    public ProfilPanel() {

        this.setLayout(null);
        this.setBackground(Color.BLACK);

        java.net.URL imageURL = getClass().getClassLoader().getResource("com/creeptd/client/resources/panel/header-editprofile.jpg");
        logoImage = new JLabel();
        logoImage.setBounds(225, 50, 450, 100);
        logoImage.setText("<html><img src=\"" + imageURL + "\"></html>");

        email = new JLabel("New Email");
        email.setBounds(350, 470, 200, 30);
        email.setForeground(Color.GRAY);
        email.setFont(new Font("Arial", Font.PLAIN, 11));

        name = new JLabel("Username");
        name.setBounds(350, 170, 200, 30);
        name.setForeground(Color.GRAY);
        name.setFont(new Font("Arial", Font.PLAIN, 11));

        password = new JLabel("New password");
        password.setBounds(350, 322, 200, 25);
        password.setForeground(Color.GRAY);
        password.setFont(new Font("Arial", Font.PLAIN, 11));

        password2 = new JLabel("Confirm new password");
        password2.setBounds(350, 397, 200, 25);
        password2.setForeground(Color.GRAY);
        password2.setFont(new Font("Arial", Font.PLAIN, 11));

        oldPassword = new JLabel("Actual password");
        oldPassword.setBounds(350, 247, 200, 25);
        oldPassword.setForeground(Color.GRAY);
        oldPassword.setFont(new Font("Arial", Font.PLAIN, 11));

        lName = new JTextField();
        lName.setBounds(350, 150, 200, 25);
        lName.setFont(new Font("Arial", Font.PLAIN, 12));
        lName.setEditable(false);

        lEmail = new JTextField();
        lEmail.setBounds(350, 450, 200, 25);
        lEmail.setFont(new Font("Arial", Font.PLAIN, 12));

        lPassword = new JPasswordField(20);
        lPassword.setBounds(350, 300, 200, 25);
        lPassword.setEchoChar('*');
        lPassword.setFont(new Font("Arial", Font.PLAIN, 12));

        lPassword2 = new JPasswordField(20);
        lPassword2.setBounds(350, 375, 200, 25);
        lPassword2.setEchoChar('*');
        lPassword2.setFont(new Font("Arial", Font.PLAIN, 12));

        loldPassword = new JPasswordField(20);
        loldPassword.setBounds(350, 225, 200, 25);
        loldPassword.setEchoChar('*');
        loldPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        this.setGameScreenFocus(loldPassword);

        confirm = new JButton("Confirm");
        confirm.setBounds(225, 550, 200, 25);
        confirm.setBackground(Color.BLACK);
        confirm.setForeground(Color.GREEN);

        back = new JButton("Back");
        back.setBounds(475, 550, 200, 25);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.GREEN);

        delete = new JButton("Delete Account");
        delete.setBounds(350, 600, 200, 25);
        delete.setBackground(Color.BLACK);
        delete.setForeground(Color.GREEN);

        this.add(password);
        this.add(name);
        this.add(lPassword);
        this.add(loldPassword);
        this.add(oldPassword);
        this.add(lName);
        this.add(confirm);
        this.add(lPassword2);
        this.add(password2);
        this.add(logoImage);
        this.add(lEmail);
        this.add(email);
        this.add(back);
        // this.add(delete);

        ActionListener a1 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                confirmProcess();
            }
        };
        confirm.addActionListener(a1);

        KeyAdapter confirmKeyAdapter = new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }
                confirmProcess();
            }
        };
        lPassword.addKeyListener(confirmKeyAdapter);
        lPassword2.addKeyListener(confirmKeyAdapter);
        loldPassword.addKeyListener(confirmKeyAdapter);
        lEmail.addKeyListener(confirmKeyAdapter);
        confirm.addKeyListener(confirmKeyAdapter);

        ActionListener a2 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                deleteProcess();
            }
        };
        delete.addActionListener(a2);

        delete.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                deleteProcess();
            }
        });

        ActionListener a3 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                getCore().popScreen();
            }
        };
        back.addActionListener(a3);

        back.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                getCore().popScreen();
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
        this.lName.setText(getCore().getPlayerName());
    }

    /**
     * {@inheritDoc}
     */
    public void update(ServerMessage m) {

        if (m instanceof UpdateDataResponseMessage) {
            UpdateDataResponseMessage udResponsem = (UpdateDataResponseMessage) m;

            if (udResponsem.getResponseType() == IConstants.ResponseType.ok) {
                UIManager.put("OptionPane.background", Color.BLACK);
                UIManager.put("Panel.background", Color.BLACK);
                UIManager.put("OptionPane.messageForeground", Color.GREEN);
                JOptionPane.showMessageDialog(this, "Change Successful",
                        "Thank You", 2);
                getCore().popScreen();
            }
            if (udResponsem.getResponseType() == IConstants.ResponseType.failed) {
                errorDialog("Unknown error");
                confirm.setEnabled(true);
                lPassword.requestFocus();
            }

        }

        if (m instanceof DeleteResponseMessage) {
            DeleteResponseMessage drm = (DeleteResponseMessage) m;
            if (drm.getResponseType() == IConstants.ResponseType.ok) {
                LogoutMessage gm = new LogoutMessage();
                getCore().getNetwork().sendMessage(gm);
                getCore().getNetwork().shutdown();
                getCore().clearScreen();
                getCore().pushScreen(new LoginPanel());
            }
        }

    }

    /**
     * process which sends changerequest to server.
     */
    public void confirmProcess() {
        getCore().getNetwork().makeContact();
        UpdateDataRequestMessage udrm = new UpdateDataRequestMessage();

        Pattern pWord = Pattern.compile("[a-zA-Z_0-9]*");
        Pattern pEmail = Pattern.compile("^\\S+@\\S+$");
        Matcher mPassword = pWord.matcher(String.valueOf(lPassword.getPassword()));
        Matcher mEmail = pEmail.matcher(lEmail.getText());
        boolean pMatchesPwd = mPassword.matches();
        boolean pMatchesEmail = mEmail.matches();

        if (!pMatchesPwd) {
            errorDialog("only a-zA-Z and 0-9 is allowed");
            lPassword.requestFocus();
        } else if (!String.valueOf(lPassword.getPassword()).equals(
                String.valueOf(lPassword2.getPassword()))) {
            errorDialog("The passwords you entered weren't identical");
            lPassword.requestFocus();
        } else if (!pMatchesEmail && !lEmail.getText().equals("")) {
            errorDialog("Not a valid email address!");
            lEmail.requestFocus();
        } else if (String.valueOf(loldPassword.getPassword()).equals(String.valueOf(lPassword.getPassword()))) {
            errorDialog("Your actual password and your " + "new password are the same!");
            lEmail.requestFocus();

        } else {
            if (lEmail.getText() == null) {
                lEmail.setText("");
                udrm.setEmail(lEmail.getText());
            } else {
                udrm.setEmail(lEmail.getText());
            }
            if (String.valueOf(lPassword.getPassword()) == null) {
                udrm.setPassword("");
            } else {
                udrm.setPassword(String.valueOf(lPassword.getPassword()));
            }
            if (String.valueOf(loldPassword.getPassword()) == null) {
                udrm.setOldPassword("");
            } else {
                udrm.setOldPassword(String.valueOf(loldPassword.getPassword()));
            }
            udrm.setClientId(getCore().getPlayerId());

            getCore().getNetwork().sendMessage(udrm);
            confirm.setEnabled(false);

        }
    }

    /**
     * @param msg
     *            msg
     */
    public void errorDialog(String msg) {
        UIManager.put("OptionPane.background", Color.BLACK);
        UIManager.put("Panel.background", Color.BLACK);
        UIManager.put("OptionPane.messageForeground", Color.GREEN);
        JOptionPane.showMessageDialog(this, msg, "error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * delete the process.
     *
     */
    public void deleteProcess() {

        String[] options = {"Yes", "No"};
        UIManager.put("OptionPane.background", Color.BLACK);
        UIManager.put("Panel.background", Color.BLACK);
        UIManager.put("OptionPane.messageForeground", Color.GREEN);
        int n = JOptionPane.showOptionDialog(this, "Are you sure?",
                "Account Deletion", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (n == JOptionPane.YES_OPTION) {
            DeleteRequestMessage drm = new DeleteRequestMessage();
            drm.setClientId(getCore().getPlayerId());
            getCore().getNetwork().sendMessage(drm);
        }
        System.out.println(n);

    }
}

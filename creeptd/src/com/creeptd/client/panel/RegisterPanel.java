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

import com.creeptd.client.Core;
import com.creeptd.client.network.MessageListener;
import com.creeptd.client.util.Fonts;
import com.creeptd.common.Constants;
import com.creeptd.common.Password;
import com.creeptd.common.messages.client.LoginRequestMessage;
import com.creeptd.common.messages.client.RegistrationRequestMessage;
import com.creeptd.common.messages.server.RegistrationResponseMessage;
import com.creeptd.common.messages.server.ServerMessage;

import javax.swing.SwingConstants;
import static com.creeptd.client.i18n.Translator.*;

/**
 * RegisterPanel to register users in the database.
 */
public class RegisterPanel extends GameScreen implements MessageListener {

    private static final long serialVersionUID = 1L;
    private JLabel name;
    private JLabel password;
    private JLabel info2;
    private JLabel title;
    private JLabel password2;
    private JLabel email;
    private JTextField lName;
    private JTextField lEmail;
    private JPasswordField lPassword;
    private JPasswordField lPassword2;
    private JButton register;
    private JButton back;
    private String[] dictionary = {"arsch", "nigga", "hitler", "fucker", "sex", "penis", "vagina",
        "suck", "hoden", "eichel", "fuck", "schlampe", "hure", "flittchen",
        "bitch", "slut", "nigga", "neger", "negar", "porno", "p0rno", "porn0",
        "p0rn0", "muschi", "titte", "boobs", "busen", "mudda", "mutter",
        "mother", "vadda", "vater", "father", "pussy"};

    /**
     * constructor to initialize elements.
     */
    public RegisterPanel() {

        this.setLayout(null);
        this.setBackground(Color.BLACK);
        
        title = Fonts.getFrameTitle(_("Create an account"), 50);

        email = new JLabel(_("Email"));
        email.setBounds(350, 402, 200, 30);
        email.setForeground(Color.GRAY);
        email.setFont(new Font("Arial", Font.PLAIN, 11));

        name = new JLabel(_("Username")+" (*)");
        name.setBounds(350, 220, 200, 30);
        name.setForeground(Color.GRAY);
        name.setFont(new Font("Arial", Font.PLAIN, 11));

        password = new JLabel(_("Password")+" (*)");
        password.setBounds(350, 282, 200, 25);
        password.setForeground(Color.GRAY);
        password.setFont(new Font("Arial", Font.PLAIN, 11));

        password2 = new JLabel(_("Repeat password")+" (*)");
        password2.setBounds(350, 342, 200, 25);
        password2.setForeground(Color.GRAY);
        password2.setFont(new Font("Arial", Font.PLAIN, 11));

        info2 = new JLabel(_("Fields marked with (*) are required"));
        info2.setBounds(350, 450, 350, 25);
        info2.setForeground(Color.GRAY);
        info2.setFont(new Font("Arial", Font.PLAIN, 11));

        lName = new JTextField();
        lName.setBounds(350, 200, 200, 25);
        lName.setFont(new Font("Arial", Font.PLAIN, 12));
        this.setGameScreenFocus(lName);

        lEmail = new JTextField();
        lEmail.setBounds(350, 380, 200, 25);
        lEmail.setFont(new Font("Arial", Font.PLAIN, 12));

        lPassword = new JPasswordField(20);
        lPassword.setBounds(350, 260, 200, 25);
        lPassword.setEchoChar('*');
        lPassword.setFont(new Font("Arial", Font.PLAIN, 12));

        lPassword2 = new JPasswordField(20);
        lPassword2.setBounds(350, 320, 200, 25);
        lPassword2.setEchoChar('*');
        lPassword2.setFont(new Font("Arial", Font.PLAIN, 12));

        register = new JButton(_("Create account"));
        register.setBounds(230, 550, 200, 25);
        register.setBackground(Color.BLACK);
        register.setForeground(Color.GREEN);

        back = new JButton(_("Cancel"));
        back.setBounds(470, 550, 200, 25);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.GREEN);

        this.add(password);
        this.add(name);
        this.add(lPassword);
        this.add(lName);
        this.add(info2);
        this.add(register);
        this.add(lPassword2);
        this.add(password2);
        this.add(title);
        this.add(lEmail);
        this.add(email);
        this.add(back);

        ActionListener a1 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registrationProcess();
            }
        };
        register.addActionListener(a1);

        KeyAdapter registerKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }
                registrationProcess();
            }
        };
        lName.addKeyListener(registerKeyAdapter);
        lPassword.addKeyListener(registerKeyAdapter);
        lPassword2.addKeyListener(registerKeyAdapter);
        lEmail.addKeyListener(registerKeyAdapter);
        register.addKeyListener(registerKeyAdapter);

        ActionListener a3 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getCore().popScreen();
            }
        };
        back.addActionListener(a3);

        back.addKeyListener(new KeyAdapter() {
            @Override
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
    }

    /**
     * {@inheritDoc}
     */
    public void update(ServerMessage m) {

        if (m instanceof RegistrationResponseMessage) {
            RegistrationResponseMessage response = (RegistrationResponseMessage) m;
            if (response.getResponseType() == Constants.ResponseType.username) {

                errorDialog(_("Username already exists!"));
                register.setEnabled(true);
                lName.requestFocus();
            }
            if (response.getResponseType() == Constants.ResponseType.ok) {
                UIManager.put("OptionPane.background", Color.BLACK);
                UIManager.put("Panel.background", Color.BLACK);
                UIManager.put("OptionPane.messageForeground", Color.GREEN);
                JOptionPane.showMessageDialog(this, _("Account created"), _("Thank you!"), 2);
                getCore().popScreen();
                // Automatic login after sign up
                LoginRequestMessage loginMessage = new LoginRequestMessage();
                loginMessage.setVersion(Core.getVersion());
                loginMessage.setUsername(lName.getText());
                loginMessage.setPassword(String.valueOf(Password.encodePassword(new String(lPassword.getPassword()))));
                loginMessage.setMacaddress(getCore().getNetwork().getMACAddress());
                getCore().getNetwork().sendMessage(loginMessage);
            }
            if (response.getResponseType() == Constants.ResponseType.failed) {
                errorDialog(_("Your account could not be created. Please check your inputs!"));
                register.setEnabled(true);
                lName.requestFocus();
            }

        }

    }

    /**
     * process which sends registrationrequest to server.
     */
    public void registrationProcess() {
        getCore().getNetwork().makeContact();
        RegistrationRequestMessage request = new RegistrationRequestMessage();

        Pattern pWord = Pattern.compile("[a-zA-Z0-9]+");
        Pattern pEmail = Pattern.compile("^\\S+@\\S+$");
        Matcher mName = pWord.matcher(lName.getText());
        Matcher mPassword = pWord.matcher(String.valueOf(lPassword.getPassword()));
        Matcher mEmail = pEmail.matcher(lEmail.getText());
        boolean pMatchesName = mName.matches();
        boolean pMatchesPwd = mPassword.matches();
        boolean pMatchesEmail = mEmail.matches();

        if (!this.isInDictionary(lName.getText().toLowerCase())) {
            if (lName.getText().equals("") || String.valueOf(lPassword.getPassword()).equals("") || String.valueOf(lPassword2.getPassword()).equals("")) {
                errorDialog(_("Please fill out all the required fields."));
                lName.requestFocus();
            } else if (lName.getText().length() > 16) {
                errorDialog(_("Maximum length of username is 16."));
                lName.requestFocus();
            } else if (lPassword.getPassword().length < 5 || lPassword.getPassword().length > 32) {
                errorDialog("Minimum password length is 5, maximum is 32 characters.");
                lPassword.requestFocus();
            } else if (!String.valueOf(lPassword.getPassword()).equals(String.valueOf(lPassword2.getPassword()))) {
                errorDialog(_("The passwords you entered weren't identical."));
                lPassword.requestFocus();
            } else if (!pMatchesName) {
                errorDialog(_("Only the characters a-z, A-Z and 0-9 are allowed for the name."));
                lName.requestFocus();
            } else if (!pMatchesEmail && !lEmail.getText().equals("")) {
                errorDialog(_("Please specify a valid email address or none."));
                lEmail.requestFocus();
            } else {
                request.setPassword(String.valueOf(lPassword.getPassword()));
                request.setEmail(lEmail.getText());
                request.setUsername(lName.getText());
                this.getCore().setPlayerName(lName.getText());
                getCore().getNetwork().sendMessage(request);
                register.setEnabled(false);
            }
        } else {
            errorDialog(_("Your Nickname includes prohibited Words!"));
            lName.requestFocus();
        }
    }

    private boolean isInDictionary(String word) {
        if (this.dictionary.length > 0) {
            for (String s : this.dictionary) {
                if (word.indexOf(s, 0) != -1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Dialog to show errors in the same colors than GUI.
     * @param msg
     *            msg
     */
    public void errorDialog(String msg) {
        UIManager.put("OptionPane.background", Color.BLACK);
        UIManager.put("Panel.background", Color.BLACK);
        UIManager.put("OptionPane.messageForeground", Color.GREEN);
        JOptionPane.showMessageDialog(this, msg, _("An error occured"), JOptionPane.ERROR_MESSAGE);
    }
}


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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.creeptd.client.game.GameContext;
import com.creeptd.client.sound.SoundManagement;
import com.creeptd.common.messages.client.ClientChatMessage;

/**
 * ChatPanel to send and receive messages to the other players in
 * the game.
 * @author ron 
 */
public class ChatPanel extends JPanel {

    private static final long serialVersionUID = -4978301434543438476L;
    private final int height;
    private final int width;
    private JTextField message;
    private PlayerChat chatdialog;
    private GameContext context;
    private GamePanel gamepanel;
    private JScrollPane jScrollPanejchatdialog = null;
    private SoundManagement managementSound;

    /**
     *
     * @param width
     *            of chatpanel
     * @param height
     *            of chatpanel
     * @param gamepanel
     * 			  of Gamepanel
     */
    public ChatPanel(GamePanel gamepanel, final int height, final int width) {

        this.gamepanel = gamepanel;
        this.height = height;
        this.width = width;
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.setSize(this.width, this.height);

        this.setLayout(null);

        this.jScrollPanejchatdialog = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.jScrollPanejchatdialog.setBounds(0, 0, this.width, this.height-25);
        this.chatdialog = new PlayerChat(this.jScrollPanejchatdialog);
        this.jScrollPanejchatdialog.setViewportView(this.chatdialog);
        this.chatdialog.setShowDatum(false);

        this.message = new JTextField();
        this.message.setEditable(true);
        this.message.setFont(new Font("Arial", 0, 13));
        this.message.setBounds(0, this.height-25, this.width, 25);

        this.add(jScrollPanejchatdialog);
        this.add(message);

        this.message.requestFocus(); // cursor on the message-textfield

        message.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                sendText(evt);
            }
        });

        setVisible(true);

    }

    /**
     *
     *
     * @param evt
     *            gets ActionEvent
     */
    public void sendText(final KeyEvent evt) {

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            // send Message
            if (context != null) {
                ClientChatMessage chatMsg = new ClientChatMessage();
                chatMsg.setClientId(context.getPlayerId());
                chatMsg.setMessage(getText());
                gamepanel.getCore().getNetwork().sendMessage(chatMsg);
                this.message.setText("");
            }

        }

    }

    /**
     * getter for textinput.
     *
     * @return message
     */
    public final String getText() {
        return this.message.getText();
    }

    /**
     *
     * @param nickname
     *            of player
     * @param newMessage
     *            message of player
     */
    public final void setMessage(String nickname, String newMessage) {
        this.chatdialog.sendChatText(nickname, newMessage, gamepanel.getCore());
        managementSound = gamepanel.getCore().getSoundManagement();
        if (managementSound != null) {
            managementSound.clapSound();
        }
    }

    /**
     * Setter to set Context.
     *
     * @param context
     *            to specify which context
     */
    public void setContext(GameContext context) {
        this.context = context;
    }

    public JTextField getMessageField() {
        return message;
    }
}

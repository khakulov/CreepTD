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
package com.creeptd.client.panel.game;

import com.creeptd.client.panel.common.*;
import com.creeptd.client.Core;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.creeptd.client.sound.SoundManagement;
import com.creeptd.common.messages.client.ChatMessage;

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
    private Chat chatdialog;
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
    public ChatPanel(final int width, final int height) {
        this.height = height;
        this.width = width;
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.setSize(this.width, this.height);

        this.setLayout(null);

        this.jScrollPanejchatdialog = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.jScrollPanejchatdialog.setBounds(0, 0, this.width, this.height-25);
        this.chatdialog = new Chat(this.jScrollPanejchatdialog);
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

        if (evt.getKeyCode() == KeyEvent.VK_ENTER && !"".equals(getText())) {
            // send Message
            ChatMessage chatMsg = new ChatMessage();
            chatMsg.setClientId(Core.getInstance().getPlayerId());
            chatMsg.setMessage(getText());
            Core.getInstance().getNetwork().sendMessage(chatMsg);
            this.message.setText("");

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
    public final void addMessage(String nickname, String newMessage, boolean action) {
        this.chatdialog.addChatText(nickname, newMessage, action);
        managementSound = Core.getInstance().getSoundManagement();
        if (managementSound != null) {
            managementSound.clapSound();
        }
    }

    public JTextField getMessageField() {
        return message;
    }
}

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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import com.creeptd.client.network.MessageListener;
import com.creeptd.client.sound.SoundManagement;
import com.creeptd.common.IConstants;
import com.creeptd.common.messages.client.ExitGameMessage;
import com.creeptd.common.messages.client.KickPlayerRequestMessage;
import com.creeptd.common.messages.client.SendMessageMessage;
import com.creeptd.common.messages.server.KickPlayerResponseMessage;
import com.creeptd.common.messages.server.KickedMessage;
import com.creeptd.common.messages.server.MessageMessage;
import com.creeptd.common.messages.server.PlayerJoinedMessage;
import com.creeptd.common.messages.server.PlayerQuitMessage;
import com.creeptd.common.messages.server.ServerMessage;
import com.creeptd.common.messages.server.StartGameMessage;
import com.creeptd.common.messages.server.StartGameResponseMessage;

/**
 * The WaitingGamePanel is coming directly to the CreateGamePanel.
 * Also with JOIN from the Lobby. There are waiting the players before 
 * the Game can start. Only the Owner (player who creates game) can 
 * start the game, he can also kick players. 
 *
 *
 * @Robert
 */
public class WaitingGamePanel extends GameScreen implements MessageListener {

    private Logger logger = Logger.getLogger(WaitingGamePanel.class.getName());
    private static final long serialVersionUID = 1L;
    private JButton kick = new JButton();
    private JButton start = new JButton();
    private JButton quit = new JButton();
    private JButton send = new JButton();
    private JTextArea contextinfodialog = new JTextArea("");
    private PlayerChat chatdialog;
    private JList playerlist;
    private JScrollPane jScrollPanejcontextinfodialog = new JScrollPane();
    private JScrollPane jScrollPanejwPlayersdialog = new JScrollPane();
    private JScrollPane jScrollPanejchatdialog = null;
    private JLabel logoImage = new JLabel();
    private JLabel description;
    private JTextField message = new JTextField();
    private ImageIcon preview;
    private JLabel previewLabel;
    private ImageIcon locationImage;
    private JLabel locationLabel;
    private GamePanel gamePanel;
    private boolean ownPlayerIdMessageReceived = false;
    //key PlayerID, value position
    private TreeMap<Integer, Integer> playersOrder = new TreeMap<Integer, Integer>();
    private TreeMap<Integer, String> players = new TreeMap<Integer, String>();
    private int mapId;
    private int minimumPlayerNumber;
    private SoundManagement managementSound;
    private IConstants.Mode gameMode = IConstants.Mode.ALLVSALL;

    /**
     * Creates a new instance of CreateGamePanel.
     * This constructor is for players who join
     *
     */
    public WaitingGamePanel() {
        this(0);
    }

    /**
     * Creates a new instance of CreateGamePanel.
     * @param minimumPlayerNumber
     *
     */
    public WaitingGamePanel(int minimumPlayerNumber) {
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        this.minimumPlayerNumber = minimumPlayerNumber;

        java.net.URL imageURL = getClass().getClassLoader().getResource("com/creeptd/client/resources/panel/header-gamewaiting.jpg");
        logoImage = new JLabel();
        logoImage.setBounds(225, 5, 450, 100);
        logoImage.setText("<html><img src=\"" + imageURL + "\"></html>");

        description = new JLabel();
        description.setBounds(256, 110, 417, 124);
        description.setBackground(Color.WHITE);
        description.setForeground(Color.green);

        kick.setBackground(Color.BLACK);
        kick.setForeground(Color.GREEN);
        kick.setBounds(688, 270, 75, 25);
        kick.setText("Kick");
        kick.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                kickButtonActionPerformed(evt);
            }
        });

        start.setBackground(Color.BLACK);
        start.setForeground(Color.GREEN);
        start.setBounds(688, 246, 75, 25);
        start.setText("Start");
        start.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                startButtonActionPerformed();
            }
        });

        start.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                startButtonActionPerformed();
            }
        });

        quit.setBackground(Color.BLACK);
        quit.setForeground(Color.GREEN);
        quit.setBounds(688, 294, 75, 25);
        quit.setText("Quit");
        quit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                quitButtonActionPerformed();
            }
        });

        quit.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                quitButtonActionPerformed();
            }
        });

        send.setBackground(Color.BLACK);
        send.setForeground(Color.WHITE);
        send.setBounds(688, 604, 75, 25);
        send.setText("Send");
        send.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });
        this.message.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    send.doClick();
                }
            }
        });

        jScrollPanejcontextinfodialog.setBounds(256, 214, 417, 33);
        contextinfodialog.setText("");
        contextinfodialog.setBackground(Color.BLACK);
        contextinfodialog.setForeground(Color.WHITE);
        contextinfodialog.setEditable(false);
        jScrollPanejcontextinfodialog.setViewportView(contextinfodialog);


        jScrollPanejwPlayersdialog.setBounds(256, 246, 417, 80);
        playerlist = new JList();
        playerlist.setModel(new DefaultListModel());
        playerlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerlist.setLayoutOrientation(JList.VERTICAL);
        playerlist.setVisibleRowCount(-1);
        jScrollPanejwPlayersdialog.setViewportView(playerlist);


        this.jScrollPanejchatdialog = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPanejchatdialog.setBounds(256, 326, 417, 278);
        this.chatdialog = new PlayerChat(jScrollPanejchatdialog);
        jScrollPanejchatdialog.setViewportView(this.chatdialog);

        message.setBounds(256, 605, 417, 24);
        message.setEditable(true);
        message.setText("");
        this.setGameScreenFocus(message);


        previewLabel = new JLabel();
        previewLabel.setBounds(553, 115, 100, 100);

        locationLabel = new JLabel();
        locationLabel.setBounds(663, 115, 100, 100);

        //Buttons
        add(kick);
        add(start);
        add(quit);
        add(send);
        add(description);
        //Labels
        add(logoImage);
        add(previewLabel);
        add(locationLabel);

        //JTextAreas
//	    add(jScrollPanejcontextinfodialog);
        add(jScrollPanejwPlayersdialog);
        add(jScrollPanejchatdialog);

        //JTextField
        add(message);
    }

    /**
     * displays the screen.
     */
    @Override
    public void start() {
        this.gamePanel = new GamePanel();
        getCore().getNetwork().addListener(this);
        managementSound = this.getCore().getCoreManagementSound();
        if (!getCore().isGamecreator()) {
            kick.setEnabled(false);
            start.setEnabled(false);
        }

        String gamename = this.getCore().getActiveGame().getGameName();
        if (gamename.equals("Game of")) {
            gamename += " ("+getCore().getPlayerName()+")";
        }
        if (gamename.length() > 30) gamename = gamename.substring(0, 27)+"...";
        description.setText("<html><p style=\"padding-bottom: 3; color: yellow;\"><b>„"+gamename+"”</b></p><table width=\"180px\"><tr><td>Map: </td><td>" + IConstants.Map.getMapById(this.getCore().getActiveGame().getMapId()).toString() + "</td></tr><tr><td>Players:  </td><td>" + this.getCore().getActiveGame().getNumberOfPlayers() + "<tr><td>Min/Max:  </td><td>" + ((this.getCore().getActiveGame().getMinEloPoints() == 0) ? "all" : this.getCore().getActiveGame().getMinEloPoints()) + "/" + ((this.getCore().getActiveGame().getMaxEloPoints() == 0) ? "all" : this.getCore().getActiveGame().getMaxEloPoints()) + "</td></tr>" + "<tr><td>Mode:  </td><td>" + this.getCore().getActiveGame().getGameModeString() + "</td></tr>" + "<tr><td>Password:  </td><td>" + ("".equals(this.getCore().getActiveGame().getPasswort()) ? "(not set)" : this.getCore().getActiveGame().getPasswort()) + "</td></tr>" + "</td></tr></table></html>");

        try {
            preview = new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(IConstants.Map.getPicturePath(IConstants.Map.getMapById(this.getCore().getActiveGame().getMapId()).toString()))));
            if (this.getCore().getActiveGame().getGameMode().equals(IConstants.Mode.TEAM2VS2)) {
                locationImage = new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("com/creeptd/client/resources/panel/boardlocations-team.jpg")));
            } else if (this.getCore().getActiveGame().getShufflePlayers()) {
                locationImage = new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("com/creeptd/client/resources/panel/boardlocations-random.jpg")));
            } else {
                locationImage = new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("com/creeptd/client/resources/panel/boardlocations.jpg")));
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        preview.setImage(preview.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        previewLabel.setIcon(preview);
        locationImage.setImage(locationImage.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        locationLabel.setIcon(locationImage);

    }

    /**
     * method for disappearing the screen.
     */
    @Override
    public void end() {
        getCore().setGamecreator(false);
        getCore().getNetwork().removeListener(this);
    }

    /**
     * method for ActionEvent on startButton to start the game.
     * with the given number of players
     */
    public void startButtonActionPerformed() {
        start.setEnabled(false);
        WaitingGameCountdownThread wgct = new WaitingGameCountdownThread(getCore());
        wgct.start();
    }

    /**
     * method for ActionEvent on kickButton to kick a Player.
     * Only the owner can kick.
     * @param evt for ActionEvent
     */
    public void kickButtonActionPerformed(ActionEvent evt) {
        KickPlayerRequestMessage kprm = new KickPlayerRequestMessage();
        kprm.setPlayerName((String) playerlist.getSelectedValue());

        // can't kick the player himself
        if (((String) playerlist.getSelectedValue()).equals(
                getCore().getPlayerName())) {
            return;
        }
        getCore().getNetwork().sendMessage(kprm);
    }

    /**
     * method for ActionEvent on quitButton to go back to the lobby.
     * @param evt for ActionEvent
     */
    public void quitButtonActionPerformed() {

        ExitGameMessage egm = new ExitGameMessage();
        egm.setClientId(getCore().getPlayerId());

        getCore().getNetwork().sendMessage(egm);
        getCore().popScreen();
    }

    /**
     * method for ActionEvent on sendButton to sen a message to chatdialog.
     * @param evt for ActionEvent
     */
    public void sendButtonActionPerformed(ActionEvent evt) {
        if (!message.getText().equals("")) {
            SendMessageMessage m = new SendMessageMessage();
            m.setMessage(message.getText());
            this.getCore().getNetwork().sendMessage(m);
            message.setText("");
        }
    }

    /**
     * method for update.
     * @param m for ServerMessage
     */
    public void update(ServerMessage m) {

        if (m instanceof PlayerJoinedMessage) {
            PlayerJoinedMessage pjm = (PlayerJoinedMessage) m;
            DefaultListModel dl = (DefaultListModel) this.playerlist.getModel();

            dl.addElement(pjm.getPlayerName());

            logger.info("PJM received");

            if (pjm.getPlayerName().equalsIgnoreCase(getCore().getPlayerName())) {
                players.put(pjm.getPlayerId(), pjm.getPlayerName());
                this.getCore().setPlayerId(pjm.getPlayerId());
                this.getCore().setPlayerName(pjm.getPlayerName());
                ownPlayerIdMessageReceived = true;
            } else {
                players.put(pjm.getPlayerId(), pjm.getPlayerName());
            }

            if (managementSound != null) {
                managementSound.hornbeepSound();
            }
            this.chatdialog.sendChatText("System", pjm.getPlayerName() + "[" + pjm.getPlayerExperience() + "/" + pjm.getPlayerElopoints() + "]" + " has joined!", getCore());

            changeButton();

        }

        if (m instanceof StartGameResponseMessage) {

            StartGameResponseMessage sgrm = (StartGameResponseMessage) m;
            if (sgrm.getResponseType() == IConstants.ResponseType.failed) {
                UIManager.put("OptionPane.background", Color.BLACK);
                UIManager.put("OptionPane.JButton.setForground", Color.BLACK);
                UIManager.put("Panel.background", Color.BLACK);
                UIManager.put("OptionPane.messageForeground", Color.GREEN);
                JOptionPane.showMessageDialog(this, "Could not start game!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (m instanceof KickPlayerResponseMessage) {
            KickPlayerResponseMessage kprm = (KickPlayerResponseMessage) m;
            if (kprm.getResponseType().equals(IConstants.ResponseType.ok)) {
                DefaultListModel dl = (DefaultListModel) this.playerlist.getModel();

                removePlayer((String) this.playerlist.getSelectedValue());
                dl.removeElement(this.playerlist.getSelectedValue());
                changeButton();
            }
        }

        if (m instanceof KickedMessage) {
            UIManager.put("OptionPane.background", Color.BLACK);
            UIManager.put("OptionPane.JButton.setForground", Color.BLACK);
            UIManager.put("Panel.background", Color.BLACK);
            UIManager.put("OptionPane.messageForeground", Color.GREEN);
            JOptionPane.showMessageDialog(this, "You were kicked by the creator of the game", "Sorry", JOptionPane.ERROR_MESSAGE);
            getCore().popScreen();
        }

        if (m instanceof StartGameMessage) {
            StartGameMessage sgm = (StartGameMessage) m;
            List<Integer> list = sgm.getPlayers();

            for (int i = 0; i < list.size(); i++) {
                this.playersOrder.put(list.get(i), i);
            }

            this.mapId = sgm.getMapID();
            getCore().switchScreen(this.gamePanel);
            gamePanel.getLoop().setPlayersOrder(this.playersOrder);
            gamePanel.getLoop().setPlayers(this.players);
            gamePanel.getLoop().setGameMode(this.gameMode);
            gamePanel.getLoop().setMap(IConstants.Map.getMapById(this.mapId));
            gamePanel.getLoop().start();
        }

        if (m instanceof PlayerQuitMessage) {
            PlayerQuitMessage pqm = (PlayerQuitMessage) m;
            DefaultListModel dl = (DefaultListModel) this.playerlist.getModel();

            if (managementSound != null) {
                managementSound.hornbeepSound();
            }
            dl.removeElement(pqm.getPlayerName());
            removePlayer(pqm.getPlayerName());
            this.chatdialog.sendChatText("System", pqm.getPlayerName() + " has left...", getCore());

        }

        if (m instanceof MessageMessage) {
            MessageMessage mm = (MessageMessage) m;
            this.chatdialog.sendChatText(mm.getPlayerName(), mm.getMessage(), getCore());

            if (managementSound != null) {
                managementSound.clapSound();
            }
        }
    }

    /**
     * Removes a player from the players list.
     * @param name the playername
     */
    private void removePlayer(String name) {
        int kickId = -1;
        for (Integer i : players.keySet()) {
            if (players.get(i).equals(name)) {
                kickId = i.intValue();
            }
        }
        players.remove(kickId);
    }

    /**
     * @return the mapId
     */
    public int getMapId() {
        return mapId;
    }

    /**
     * @param mapId the mapId to set
     */
    public void setMapId(int mapId) {
        this.mapId = mapId;
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
     * changes state of button when only 1 player is in waitingpanel.
     */
    public void changeButton() {
        if (getCore().isGamecreator()) {
            if (this.playerlist.getModel().getSize() <= 1) {
                kick.setEnabled(false);
            } else if (this.playerlist.getModel().getSize() > 1) {
                kick.setEnabled(true);
            }
            if (this.playerlist.getModel().getSize() ==
                    this.minimumPlayerNumber) {
                start.setEnabled(true);
            } else {
                start.setEnabled(false);
            }
        }
    }
}

package com.creeptd.client.panel.game;

import com.creeptd.client.Core;
import com.creeptd.client.panel.LobbyScreen;
import com.creeptd.client.panel.Screen;
import com.creeptd.client.panel.common.Chat;
import com.creeptd.client.panel.common.Button;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

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
import com.creeptd.client.util.Fonts;
import com.creeptd.common.Constants;
import com.creeptd.common.messages.client.ExitGameMessage;
import com.creeptd.common.messages.client.ChatMessage;
import com.creeptd.common.messages.server.GameDescription;
import com.creeptd.common.messages.server.KickedMessage;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.common.messages.server.PlayerJoinedMessage;
import com.creeptd.common.messages.server.PlayerQuitMessage;
import com.creeptd.common.messages.server.ServerMessage;
import com.creeptd.common.messages.server.StartGameMessage;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import static com.creeptd.client.i18n.Translator.*;

public class WaitingGameScreen extends Screen implements MessageListener {
    private static final long serialVersionUID = 1L;

    private JButton quit = new Button();
    private JButton send = new Button();
    private JTextArea contextinfodialog = new JTextArea("");
    private Chat chatdialog;
    private JList playerlist;
    private JList spectatorlist;
    private JScrollPane jScrollPanejcontextinfodialog = new JScrollPane();
    private JScrollPane playerlistScroll = new JScrollPane();
    private JScrollPane spectatorlistScroll = new JScrollPane();
    private JScrollPane chatdialogScroll = null;
    private JLabel title = new JLabel();
    private JLabel description;
    private JTextField message = new JTextField();
    private ImageIcon preview;
    private JLabel previewLabel;
    private ImageIcon locationImage;
    private JLabel locationLabel;
    private Button options;

    private TreeMap<Integer, Integer> playersOrder = new TreeMap<Integer, Integer>();
    private TreeMap<Integer, String> players = new TreeMap<Integer, String>();
    private TreeMap<Integer, String> spectators = new TreeMap<Integer, String>();

    private Constants.Mode gameMode = Constants.Mode.ALLVSALL;

    /**
     * Creates a new instance of CreateGamePanel.
     * This constructor is for players who join
     *
     */
    public WaitingGameScreen() {
        this(0);
    }

    public WaitingGameScreen(GameDescription game) {
        this(game.getNumberOfPlayers());
        this.gameMode = game.getGameMode();
    }

    /**
     * Creates a new instance of CreateGamePanel.
     * @param minimumPlayerNumber
     *
     */
    public WaitingGameScreen(int minimumPlayerNumber) {
        this.setLayout(null);
        this.setBackground(Color.BLACK);

        title = Fonts.getFrameTitle(_("Waiting for players..."), 5);

        description = new JLabel();
        description.setBounds(206, 110, 417, 124);
        description.setBackground(Color.WHITE);
        description.setForeground(Color.green);

        quit.setBackground(Color.BLACK);
        quit.setForeground(Color.GREEN);
        quit.setBounds(638, 294, 125, 25);
        quit.setText(_("Quit"));
        quit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                quitButtonActionPerformed();
            }
        });

        quit.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                quitButtonActionPerformed();
            }
        });

        this.options = new Button(_("Options"));
        this.options.setFont(new Font("Helvetica", Font.PLAIN, 9));
        this.options.setBounds(833, 20, 80, 20);
        this.options.setBackground(Color.BLACK);
        this.options.setForeground(Color.YELLOW);
        this.options.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                LobbyScreen.openOptionsPanel();
            }
        });

        this.add(this.options);

        send.setBackground(Color.BLACK);
        send.setForeground(Color.WHITE);
        send.setBounds(638, 604, 125, 25);
        send.setText(_("Send"));
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

        jScrollPanejcontextinfodialog.setBounds(206, 299, 417, 33);
        contextinfodialog.setText("");
        contextinfodialog.setBackground(Color.BLACK);
        contextinfodialog.setForeground(Color.WHITE);
        contextinfodialog.setEditable(false);
        jScrollPanejcontextinfodialog.setViewportView(contextinfodialog);
        jScrollPanejcontextinfodialog.setBorder(new EmptyBorder(0,0,0,0));

        playerlistScroll.setBounds(206, 246, 417, 80);
        playerlist = new JList();
        playerlist.setModel(new DefaultListModel());
        playerlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerlist.setLayoutOrientation(JList.VERTICAL);
        playerlist.setVisibleRowCount(-1);
        playerlist.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                JList list = (JList) e.getSource();
                if (list.getSelectedIndex() >= 0) {
                    WaitingGameScreen.this.spectatorlist.clearSelection();
                }
            }
        });
        playerlistScroll.setViewportView(playerlist);
        playerlistScroll.setBorder(new EmptyBorder(3,3,3,3));

        spectatorlistScroll.setBounds(638, 326, 125, 273);
        spectatorlist = new JList();
        spectatorlist.setModel(new DefaultListModel());
        spectatorlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        spectatorlist.setLayoutOrientation(JList.VERTICAL);
        spectatorlist.setVisibleRowCount(-1);
        spectatorlist.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                JList list = (JList) e.getSource();
                if (list.getSelectedIndex() >= 0) {
                    WaitingGameScreen.this.playerlist.clearSelection();
                }
            }
        });
        spectatorlistScroll.setViewportView(spectatorlist);
        spectatorlistScroll.setBorder(new EmptyBorder(3,3,3,3));

        this.chatdialogScroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatdialogScroll.setBounds(206, 326, 417, 278);
        this.chatdialog = new Chat(chatdialogScroll);
        chatdialogScroll.setViewportView(this.chatdialog);
        chatdialogScroll.setBorder(new LineBorder(Color.GRAY));

        message.setBounds(206, 605, 417, 24);
        message.setEditable(true);
        message.setText("");
        this.setGameScreenFocus(message);


        previewLabel = new JLabel();
        previewLabel.setBounds(553, 115, 100, 100);

        locationLabel = new JLabel();
        locationLabel.setBounds(663, 115, 100, 100);

        //Buttons
        add(quit);
        add(send);
        add(description);
        //Labels
        add(title);
        add(previewLabel);
        add(locationLabel);

        //JTextAreas
//	    add(jScrollPanejcontextinfodialog);
        add(playerlistScroll);
        add(spectatorlistScroll);
        add(chatdialogScroll);

        //JTextField
        add(message);
    }

    /**
     * displays the screen.
     */
    @Override
    public void start() {
        Core.getInstance().getNetwork().addListener(this);
        /*
        if (!getCore().isGamecreator()) {
            kick.setEnabled(false);
            start.setEnabled(false);
        }
        */

        String gamename = Core.getInstance().getActiveGame().getGameName();
        if (gamename.equals(_("Game of"))) {
            gamename += " (" + Core.getInstance().getPlayerName() + ")";
        }
        if (gamename.length() > 30) gamename = gamename.substring(0, 27)+"...";
        String map = Constants.Map.getMapById(Core.getInstance().getActiveGame().getMapId()).toString();
        if (map.equals("Random_Map")) {
            map = _("Random map");
        }
        description.setText("<html><p style=\"padding-bottom: 3; color: yellow;\"><b>"+gamename+"</b></p><table width=\"180px\"><tr><td>"+_("Map")+": </td><td>" + map + "</td></tr><tr><td>"+_("Players")+":  </td><td>" + Core.getInstance().getActiveGame().getNumberOfPlayers() + "<tr><td>"+_("Min-Max")+":  </td><td>" + ((Core.getInstance().getActiveGame().getMinSkill() == 0) ? _("all") : Core.getInstance().getActiveGame().getMinSkill()) + "-" + ((Core.getInstance().getActiveGame().getMaxSkill() == 0) ? _("all") : Core.getInstance().getActiveGame().getMaxSkill()) + "</td></tr>" + "<tr><td>"+_("Mode")+":  </td><td>" + _(Core.getInstance().getActiveGame().getGameModeString()) + "</td></tr>" + "<tr><td>"+_("Password")+":  </td><td>" + ("".equals(Core.getInstance().getActiveGame().getPassword()) ? _("(not set)") : Core.getInstance().getActiveGame().getPassword()) + "</td></tr>" + "</td></tr></table></html>");

        try {
            preview = new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(Constants.Map.getPicturePath(Constants.Map.getMapById(Core.getInstance().getActiveGame().getMapId()).toString()))));
            if (Core.getInstance().getActiveGame().getGameMode().equals(Constants.Mode.TEAM2VS2)) {
                locationImage = new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("com/creeptd/client/resources/panel/boardlocations-team.jpg")));
            } else if (Core.getInstance().getActiveGame().getShufflePlayers()) {
                locationImage = new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("com/creeptd/client/resources/panel/boardlocations-random.jpg")));
            } else {
                locationImage = new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("com/creeptd/client/resources/panel/boardlocations.jpg")));
            }
        } catch (IOException e1) {
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
    	Core.getInstance().getNetwork().removeListener(this);
    }

    /**
     * method for ActionEvent on quitButton to go back to the lobby.
     * @param evt for ActionEvent
     */
    public void quitButtonActionPerformed() {
        ExitGameMessage egm = new ExitGameMessage();
        egm.setClientId(Core.getInstance().getPlayerId());
        Core.getInstance().getNetwork().sendMessage(egm);

        Core.getInstance().popScreen();
    }

    /**
     * method for ActionEvent on sendButton to sen a message to chatdialog.
     * @param evt for ActionEvent
     */
    public void sendButtonActionPerformed(ActionEvent evt) {
        if (!message.getText().equals("")) {
            ChatMessage m = new ChatMessage();
            m.setMessage(message.getText());
            Core.getInstance().getNetwork().sendMessage(m);
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
            if (!pjm.isSpectate()) {
                DefaultListModel dl = (DefaultListModel) this.playerlist.getModel();

                dl.addElement(pjm.getPlayerName());
                if (pjm.getPlayerName().equalsIgnoreCase(Core.getInstance().getPlayerName())) {
                    players.put(pjm.getPlayerId(), pjm.getPlayerName());
                    Core.getInstance().setPlayerId(pjm.getPlayerId());
                    Core.getInstance().setPlayerName(pjm.getPlayerName());
                } else {
                    players.put(pjm.getPlayerId(), pjm.getPlayerName());
                }

                Core.getInstance().getSoundManagement().hornbeepSound();

                Map<String,String> args = new HashMap<String,String>();
                args.put("name", "<b>"+pjm.getPlayerName()+"</b>[" + pjm.getPlayerPoints() + "/" + pjm.getPlayerSkill() + "]");
                this.chatdialog.addChatText("Server", _("%name% has joined!", args), false);
            } else {
                DefaultListModel dl = (DefaultListModel) this.spectatorlist.getModel();

                dl.addElement(pjm.getPlayerName());
                spectators.put(pjm.getPlayerId(), pjm.getPlayerName());
                if (pjm.getPlayerName().equalsIgnoreCase(Core.getInstance().getPlayerName())) {
                	Core.getInstance().setPlayerId(pjm.getPlayerId());
                	Core.getInstance().setPlayerName(pjm.getPlayerName());
                }
                Map<String,String> args = new HashMap<String,String>();
                args.put("name", "<b>"+pjm.getPlayerName()+"</b>[" + pjm.getPlayerPoints() + "/" + pjm.getPlayerSkill() + "]");
                this.chatdialog.addChatText("Server", _("%name% has joined!", args)+" ("+_("Spectator")+")", false);
            }

        }

        if (m instanceof KickedMessage) {
            UIManager.put("OptionPane.background", Color.BLACK);
            UIManager.put("OptionPane.JButton.setForground", Color.BLACK);
            UIManager.put("Panel.background", Color.BLACK);
            UIManager.put("OptionPane.messageForeground", Color.GREEN);
            JOptionPane.showMessageDialog(this, _("You have been kicked by the creator of the game."), _("Sorry"), JOptionPane.ERROR_MESSAGE);
            Core.getInstance().popScreen();
        }

        if (m instanceof StartGameMessage) {
            StartGameMessage sgm = (StartGameMessage) m;
            List<Integer> list = sgm.getPlayers();
            for (int i = 0; i < list.size(); i++) {
                this.playersOrder.put(list.get(i), i);
            }

            RunningGameScreen gamePanel = new RunningGameScreen();
            Core.getInstance().switchScreen(gamePanel);
            gamePanel.getGame().setPlayersOrder(this.playersOrder);
            gamePanel.getGame().setPlayers(this.players);
            gamePanel.getGame().setGameMode(this.gameMode);
            gamePanel.getGame().setMap(Constants.Map.getMapById(sgm.getMapID()));
            gamePanel.getGame().start();
        }

        if (m instanceof PlayerQuitMessage) {
            PlayerQuitMessage pqm = (PlayerQuitMessage) m;
            Map<String,String> args = new HashMap<String,String>();
            args.put("name", "<b>"+pqm.getPlayerName()+"</b>");
            if (removePlayer(pqm.getPlayerName())) {
                DefaultListModel dl = (DefaultListModel) this.playerlist.getModel();
                Core.getInstance().getSoundManagement().hornbeepSound();
                dl.removeElement(pqm.getPlayerName());
                this.chatdialog.addChatText("Server", _("%name% has left...", args), false);
            } else if (removeSpectator(pqm.getPlayerName())) {
                DefaultListModel dl = (DefaultListModel) this.spectatorlist.getModel();
                dl.removeElement(pqm.getPlayerName());
                this.chatdialog.addChatText("Server", _("%name% has left...", args)+" ("+_("Spectator")+")", false);
            }
        }

        if (m instanceof ServerChatMessage) {
            ServerChatMessage scm = (ServerChatMessage) m;
            String msg = scm.getMessage();
            if (scm.getTranslate()) {
                msg = _(msg);
            }
            this.chatdialog.addChatText(scm.getPlayerName(), msg, scm.isAction());

            Core.getInstance().getSoundManagement().clapSound();
        }
    }

    /**
     * Removes a player from the players list.
     * @param name the playername
     * @return true if removed, false if not found
     */
    private boolean removePlayer(String name) {
        int kickId = -1;
        for (Integer i : players.keySet()) {
            if (players.get(i).equals(name)) {
                kickId = i.intValue();
            }
        }
        if (players.containsKey(kickId)) {
            players.remove(kickId);
            return true;
        }
        return false;
    }

    /**
     * Remove a spectator from the panel.
     * @param name The spectator's player name
     * @return true if removed, false if not found
     */
    private boolean removeSpectator(String name) {
        int kickId = -1;
        for (Integer i : spectators.keySet()) {
            if (spectators.get(i).equals(name)) {
                kickId = i.intValue();
            }
        }
        if (spectators.containsKey(kickId)) {
            spectators.remove(kickId);
            return true;
        }
        return false;
    }
}

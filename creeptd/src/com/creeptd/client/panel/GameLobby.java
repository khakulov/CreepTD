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

import com.creeptd.client.Core;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.html.HTMLEditorKit;

import com.creeptd.client.network.MessageListener;
import com.creeptd.client.sound.SoundManagement;
import com.creeptd.client.util.SkillImage;
import com.creeptd.common.Constants;
import com.creeptd.common.messages.client.JoinGameRequestMessage;
import com.creeptd.common.messages.client.LogoutMessage;
import com.creeptd.common.messages.client.RefreshMessage;
import com.creeptd.common.messages.client.ClientChatMessage;
import com.creeptd.common.messages.server.GameDescription;
import com.creeptd.common.messages.server.GamesMessage;
import com.creeptd.common.messages.server.JoinGameResponseMessage;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.common.messages.server.PlayersMessage;
import com.creeptd.common.messages.server.ServerMessage;
import java.awt.Component;
import java.awt.Cursor;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import javax.swing.Timer;
import java.util.TreeMap;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import static com.creeptd.client.i18n.Translator.*;

/**
 * The GameLobby screen.
 * 
 * @author nikolaj
 * 
 */
public class GameLobby extends GameScreen implements MessageListener {

    /** Logging functionality */
    private final static Logger logger = Logger.getLogger(GameLobby.class.getName());
    // Components
    private final JLabel lobbyTitle;
    private JLabel onlinePlayersLabel;
    private JEditorPane gameInfoEditorPane;
    private String gameInfoEditorPaneContent = "";
    private final JTextField message;
    private PlayerChat chatdialog;
    private JScrollPane chatScrollPane;
    private JList playerList;
    private JScrollPane playersScrollPane;
    private JTabbedPane gamesTabbedPane;
    private JTable gameinfoWaiting;
    private JScrollPane gamesScrollPaneWaiting;
    private JTable gameinfoRunning;
    private JScrollPane gamesScrollPaneRunning;
    private final JButton highscoresButton;
    private final JButton joinButton;
    private final JButton createButton;
    private final JButton profileButton;
    private final JButton optionsButton;
    private final JButton refreshButton;
    private final JButton helpButton;
    // private final JButton forum;
    private JButton quit;
    private final JButton chat;
    private SoundManagement managementSound;
    private int joinGameId;
    private GameDescription joinGame;
    private ArrayList<GameDescription> games;
    private int playersInGame;
    private static JDialog helpFrame = new HelpGamePanel();
    public static JDialog optionsFrame = new OptionsPanel();
    public static final SortedMap<String, Player> allPlayers = new TreeMap<String, Player>();
    Timer swingUpdateTimer = null;

    /**
     * A player
     */
    public class Player {

        private int id;
        private String name;
        private int points;
        private int skill;
        private String state;

        public Player(int id, String name, int points, int skill, String state) {
            this.id = id;
            this.name = name;
            this.points = points;
            this.skill = skill;
            this.state = state;
            synchronized (allPlayers) {
                allPlayers.put(this.name.toLowerCase(), this);
            }
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public int getPoints() {
            return points;
        }

        public int getSkill() {
            return skill;
        }

        public boolean remove() {
            synchronized (allPlayers) {
                Player rem = allPlayers.remove(this.name.toLowerCase());
                return rem != null;
            }
        }
    };

    /**
     * Find a player inside the lobby by its name.
     *
     * @param name The player's name
     * @return The player or null, if not found
     */
    public static Player findPlayerByName(String name) {
        synchronized (allPlayers) {
            return allPlayers.get(name.toLowerCase());
        }
    }

    /**
     * The GameLobby screen.
     */
    public GameLobby() {
        this.setLayout(null);
        this.setBackground(Color.BLACK);

        UIManager.put("ToolTip.background", Color.WHITE);
        UIManager.put("ToolTip.foreground", Color.DARK_GRAY);
        UIManager.put("ToolTip.border", new EmptyBorder(2, 2, 2, 2));

        this.lobbyTitle = new JLabel();
        this.lobbyTitle.setBounds(20, 2, 600, 50);
        this.lobbyTitle.setBorder(null);
        this.lobbyTitle.setForeground(Color.green);
        this.lobbyTitle.setBackground(Color.BLACK);
        this.lobbyTitle.setFont(new Font("Arial", Font.BOLD, 28));
        java.net.URL imageURL = getClass().getClassLoader().getResource("com/creeptd/client/resources/panel/creeptd-logo-long.jpg");
        this.lobbyTitle.setText("<html><img src=\"" + imageURL + "\"></html>");

        Map<String, String> opl = new HashMap<String, String>();
        opl.put("n", "0");
        this.onlinePlayersLabel = new JLabel(_("%n% players online", opl), SwingConstants.RIGHT);
        this.onlinePlayersLabel.setBounds(635, 19, 270, 10);
        this.onlinePlayersLabel.setForeground(Color.GRAY);
        this.onlinePlayersLabel.setFont(new Font("Arial", Font.BOLD, 11));

        this.gameInfoEditorPane = new JEditorPane();
        this.gameInfoEditorPane.setBounds(640, 50, 270, 200);
        this.gameInfoEditorPane.setEditable(false);
        this.gameInfoEditorPane.setFont(new Font("Arial", Font.PLAIN, 10));
        this.gameInfoEditorPane.setEditorKit(new HTMLEditorKit());
        this.gameInfoEditorPane.setAutoscrolls(true);
        this.gameInfoEditorPane.setFocusable(false);
        this.gameInfoEditorPane.setDoubleBuffered(true);
        this.gameInfoEditorPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        this.gameInfoEditorPane.setBackground(Color.BLACK);

        this.playersScrollPane = this.initPlayersList();
        this.playersScrollPane.setBounds(640, 270, 270, 365);
        this.playersScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        this.playerList.setBackground(Color.BLACK);
        this.playerList.setForeground(Color.GREEN);

        this.gamesTabbedPane = new JTabbedPane();
        this.gamesTabbedPane.setBounds(20, 50, 600, 300);
        this.gamesTabbedPane.setForeground(Color.black);

        UIManager.put("TabbedPane.contentBorderInsets", new InsetsUIResource(0, 0, 0, 0));
        UIManager.put("TabbedPane.border", new EmptyBorder(0, 0, 0, 0));
        UIManager.put("TabbedPane.darkShadow", Color.lightGray);
        UIManager.put("TabbedPane.focus", Color.lightGray);
        UIManager.put("TabbedPane.highlight", Color.lightGray);
        UIManager.put("TabbedPane.shadow", Color.lightGray);
        UIManager.put("TabbedPane.selected", Color.white);
        UIManager.put("TabbedPane.tabAreaBackground", Color.black);

        UIManager.put("Panel.contentBorderInsets", new InsetsUIResource(0, 0, 0, 0));

        SwingUtilities.updateComponentTreeUI(this.gamesTabbedPane);

        this.gamesTabbedPane.setOpaque(true);

        this.gamesScrollPaneWaiting = new JScrollPane();
        this.gamesScrollPaneWaiting.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        this.gamesScrollPaneWaiting = this.initGamesTableWaiting();
        this.gamesScrollPaneWaiting.setBackground(Color.LIGHT_GRAY);
        this.gamesTabbedPane.addTab(_("Games waiting for players"), null, this.gamesScrollPaneWaiting);

        this.gamesScrollPaneRunning = new JScrollPane();
        this.gamesScrollPaneRunning.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        this.gamesScrollPaneRunning = this.initGamesTableRunning();
        this.gamesScrollPaneRunning.setBackground(Color.LIGHT_GRAY);
        this.gamesTabbedPane.addTab(_("Games currently running"), null, this.gamesScrollPaneRunning);

        this.chatScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.chatScrollPane.setPreferredSize(new Dimension(500, 180));
        this.chatScrollPane.setBounds(20, 370, 600, 241);
        this.chatScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        this.chatdialog = new PlayerChat(this.chatScrollPane);
        this.chatdialog.setFont(new Font("Arial", Font.PLAIN, 11));
        this.chatScrollPane.setViewportView(this.chatdialog);

        // Welcome Chat MSG
        chatdialog.sendChatText("Server", _("Welcome to CreepTD Chat! :)"), false, null);
        chatdialog.sendChatText("Server", _("Please avoid flaming and spam."), false, null);

        this.message = new JTextField();
        this.message.setBounds(20, 610, 520, 25);
        this.message.setEditable(true);
        this.message.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.setGameScreenFocus(message);

        this.add(this.gameInfoEditorPane);
        this.add(this.playersScrollPane);
        this.add(this.gamesTabbedPane);
        this.add(this.chatScrollPane);
        this.add(this.message);
        this.add(this.lobbyTitle);
        this.add(this.onlinePlayersLabel);

        this.createButton = new JButton(_("Create game"));
        this.createButton.setBounds(20, 650, 110, 25);
        this.createButton.setBackground(Color.BLACK);
        this.createButton.setForeground(Color.GREEN);

        this.joinButton = new JButton(_("Join game"));
        this.joinButton.setBounds(140, 650, 110, 25);
        this.joinButton.setBackground(Color.BLACK);
        this.joinButton.setForeground(Color.GREEN);
        this.joinButton.setEnabled(false);

        this.refreshButton = new JButton(_("Refresh"));
        this.refreshButton.setBounds(260, 650, 110, 25);
        this.refreshButton.setBackground(Color.BLACK);
        this.refreshButton.setForeground(Color.GREEN);

        this.highscoresButton = new JButton(_("Highscores"));
        this.highscoresButton.setBounds(380, 650, 110, 25);
        this.highscoresButton.setBackground(Color.BLACK);
        this.highscoresButton.setForeground(Color.GREEN);

        this.helpButton = new JButton(_("Help"));
        this.helpButton.setBounds(500, 650, 120, 25);
        this.helpButton.setBackground(Color.BLACK);
        this.helpButton.setForeground(Color.GREEN);

        this.optionsButton = new JButton(_("Options"));
        this.optionsButton.setBounds(640, 650, 85, 25);
        this.optionsButton.setBackground(Color.BLACK);
        this.optionsButton.setForeground(Color.YELLOW);

        this.profileButton = new JButton(_("Edit account"));
        this.profileButton.setBounds(735, 650, 105, 25);
        this.profileButton.setBackground(Color.BLACK);
        this.profileButton.setForeground(Color.YELLOW);

        this.quit = new JButton(_("Quit"));
        this.quit.setBounds(850, 650, 60, 25);
        this.quit.setBackground(Color.BLACK);
        this.quit.setForeground(Color.RED);

        this.chat = new JButton(_("Send"));
        this.chat.setBounds(540, 610, 80, 25);
        this.chat.setBackground(Color.BLACK);
        this.chat.setForeground(Color.WHITE);

        // this.add(this.forum);
        this.add(this.joinButton);
        this.add(this.refreshButton);
        this.add(this.createButton);
        this.add(this.highscoresButton);
        this.add(this.helpButton);
        this.add(this.quit);
        this.add(this.chat);
        this.add(this.optionsButton);
        this.add(this.profileButton);

        ActionListener a1 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JoinGameRequestMessage grm = new JoinGameRequestMessage();
                String pw = null;
                if (GameLobby.this.joinGame == null) {
                    GameLobby.this.logger.info("Could not select the game");
                    return;
                }
                if (GameLobby.this.joinGame.getMinEloPoints() > getCore().getPlayerElopoints() && GameLobby.this.joinGame.getMinEloPoints() != 0) {
                    Map<String, String> args = new HashMap<String, String>();
                    args.put("min", GameLobby.this.joinGame.getMinEloPoints() + "");
                    errorDialog(_("A minimum skill of %min% is required for this game.", args));
                    return;
                }
                if (GameLobby.this.joinGame.getMaxEloPoints() < getCore().getPlayerElopoints() && GameLobby.this.joinGame.getMaxEloPoints() != 0) {
                    Map<String, String> args = new HashMap<String, String>();
                    args.put("max", GameLobby.this.joinGame.getMaxEloPoints() + "");
                    errorDialog(_("A maximum skill of %max% is required for this game.", args));
                    return;
                }
                if (!GameLobby.this.joinGame.getState().equals("waiting")) {
                    errorDialog(_("Sorry, this game has already started."));
                    return;
                }
                if (GameLobby.this.joinGame.getPassword().equals("yes")) {

                    UIManager.put("OptionPane.background", Color.BLACK);
                    UIManager.put("Panel.background", Color.BLACK);
                    UIManager.put("OptionPane.messageForeground", Color.GREEN);
                    pw = JOptionPane.showInputDialog(null, _("Please enter the game's password:"));
                    if (pw != null) {
                        if (pw.equals("")) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                GameLobby.this.joinGameId = GameLobby.this.joinGame.getGameId();
                grm.setGameId(GameLobby.this.joinGameId);
                grm.setPassword(pw);
                GameLobby.this.getCore().getNetwork().sendMessage(grm);
                GameLobby.this.joinButton.setEnabled(false);
            }
        };
        this.joinButton.addActionListener(a1);

        joinButton.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                // Make it simple
                joinButton.getActionListeners()[0].actionPerformed(null);

                /* JoinGameRequestMessage; grm = new JoinGameRequestMessage();
                String pw = null;
                if (GameLobby.this.joinGame == null) {
                GameLobby.this.logger.info("Could not select the game");
                return;
                }
                if (GameLobby.this.joinGame.getMinEloPoints() > getCore().getPlayerElopoints() && GameLobby.this.joinGame.getMinEloPoints() != 0) {
                errorDialog("A minimum skill of "+GameLobby.this.joinGame.getMinEloPoints() + " is required for this game.");
                return;
                }
                if (GameLobby.this.joinGame.getMaxEloPoints() < getCore().getPlayerElopoints() && GameLobby.this.joinGame.getMaxEloPoints() != 0) {
                errorDialog("A maximum skill of " + GameLobby.this.joinGame.getMaxEloPoints() + " is required for this game.");
                return;
                }
                if (!GameLobby.this.joinGame.getState().equals("waiting")) {
                errorDialog("Sorry, this game has already started.");
                return;
                }
                if (GameLobby.this.joinGame.getPassword().equals("yes")) {

                UIManager.put("OptionPane.background", Color.BLACK);
                UIManager.put("Panel.background", Color.BLACK);
                UIManager.put("OptionPane.messageForeground", Color.GREEN);
                pw = JOptionPane.showInputDialog(null, "Please enter the game's password:");
                if (pw.equals("")) {
                return;
                }
                }
                GameLobby.this.joinGameId = GameLobby.this.joinGame.getGameId();

                grm.setGameId(GameLobby.this.joinGameId);
                grm.setPassword(pw);
                GameLobby.this.getCore().getNetwork().sendMessage(grm);
                GameLobby.this.joinButton.setEnabled(false); */
            }
        });

        ActionListener a2 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                GameLobby.this.getCore().pushScreen(new CreateGamePanel());
            }
        };
        this.createButton.addActionListener(a2);

        createButton.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                getCore().pushScreen(new CreateGamePanel());
            }
        });

        ActionListener a3 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                GameLobby.this.getCore().pushScreen(new HighscorePanel());
            }
        };
        this.highscoresButton.addActionListener(a3);

        highscoresButton.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                getCore().pushScreen(new HighscorePanel());
            }
        });

        ActionListener a4 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ((GameLobby.this.message.getText() != null) && (!GameLobby.this.message.getText().equals(""))) {
                    if (GameLobby.this.message.getText().length() > 250) {
                        Map<String, String> args = new HashMap<String, String>();
                        args.put("n", "250");
                        GameLobby.this.errorDialog(_("Please don't enter more than %n% characters.", args));
                        return;
                    } else {
                        ClientChatMessage m = new ClientChatMessage();
                        m.setMessage(GameLobby.this.message.getText());
                        GameLobby.this.message.setText("");
                        m.setClientId(GameLobby.this.getCore().getPlayerId());
                        GameLobby.this.getCore().getNetwork().sendMessage(m);
                    }
                }
            }
        };
        this.chat.addActionListener(a4);
        this.message.addActionListener(a4);

        // Quitbutton
        ActionListener a5 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LogoutMessage gm = new LogoutMessage();
                getCore().getNetwork().sendMessage(gm);
                getCore().getNetwork().shutdown();
                getCore().popScreen();
            }
        };
        this.quit.addActionListener(a5);

        quit.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                LogoutMessage gm = new LogoutMessage();
                getCore().getNetwork().sendMessage(gm);
                getCore().getNetwork().shutdown();
                getCore().popScreen();
            }
        });

        ActionListener a6 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                getCore().pushScreen(new ProfilPanel());
            }
        };
        this.profileButton.addActionListener(a6);

        profileButton.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                getCore().pushScreen(new ProfilPanel());
            }
        });



        this.message.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    GameLobby.this.message.postActionEvent();
                }
            }
        });


        ActionListener a7 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                getCore().getNetwork().sendMessage(new RefreshMessage());
            }
        };

        this.refreshButton.addActionListener(a7);

        this.refreshButton.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                getCore().getNetwork().sendMessage(new RefreshMessage());
            }
        });

        this.helpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                helpFrame.setVisible(true);
            }
        });

        this.optionsButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                optionsFrame.setVisible(true);
            }
        });

        //Button Forum
        /* ActionListener a10 = new ActionListener() {

        public void actionPerformed(ActionEvent e) {

        openURL("http://forum.creepsmash.com/");
        }
        };
        this.forum.addActionListener(a10); */
    }

    /**
     * Open URL
     *
     */
    private void openURL(String URL) {
        try {
            java.net.URI url = new java.net.URI(URL);
            Desktop.getDesktop().browse(url);
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
    }

    /**
     * Creates the games table Wating.
     *
     * @return a scrollpane with the table
     */
    private JScrollPane initGamesTableWaiting() {

        if (this.gameinfoWaiting == null) {
            this.gameinfoWaiting = new JTable() {

                private static final long serialVersionUID = -5995847295484708948L;
            };
            this.gameinfoWaiting.setBackground(Color.BLACK);
            this.gameinfoWaiting.setForeground(Color.GREEN);
            this.gameinfoWaiting.setSelectionBackground(Color.GREEN);
            this.gameinfoWaiting.setSelectionForeground(Color.BLACK);
            this.gameinfoWaiting.setModel(new DefaultTableModel() {

                private static final long serialVersionUID = -5995847295484708948L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });

            this.gameinfoWaiting.setDoubleBuffered(true);
            this.gameinfoWaiting.setIntercellSpacing(new Dimension(0, 0));
            this.gameinfoWaiting.setShowVerticalLines(false);
            this.gameinfoWaiting.setShowHorizontalLines(false);

            this.gameinfoWaiting.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.gameinfoWaiting.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {

                        public void valueChanged(ListSelectionEvent e) {
                            int index = GameLobby.this.gameinfoWaiting.getSelectedRow();

                            if (index < 0) {
                                GameLobby.this.joinButton.setEnabled(false);
                            } else {
                                int indeX = GameLobby.this.gameinfoRunning.getSelectedRow();
                                if (indeX != -1) {
                                    GameLobby.this.gameinfoRunning.removeRowSelectionInterval(indeX, indeX);
                                }
                                int GameID = Integer.parseInt(GameLobby.this.gameinfoWaiting.getValueAt(index, 7).toString());
                                GameLobby.this.joinGame = GameLobby.this.games.get(GameID);
                                GameLobby.this.joinButton.setEnabled(true);
                                // Set Game info label
                                GameLobby.this.setGameInfoEditorPaneSelectGame(GameID);
                            }
                        }
                    });
            this.gameinfoWaiting.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    if (e.getClickCount() >= 2) {
                        if (GameLobby.this.joinButton.isEnabled() && GameLobby.this.joinGame != null) {
                            GameLobby.this.joinButton.doClick();
                        }
                    }
                }
            });
        }
        this.gamesScrollPaneWaiting.setViewportView(this.gameinfoWaiting);
        return this.gamesScrollPaneWaiting;
    }

    /**
     * Creates the games table Running.
     *
     * @return a scrollpane with the table
     */
    private JScrollPane initGamesTableRunning() {

        if (this.gameinfoRunning == null) {
            this.gameinfoRunning = new JTable() {

                private static final long serialVersionUID = -5995847295484708948L;
            };
            this.gameinfoRunning.setBackground(Color.BLACK);
            this.gameinfoRunning.setForeground(Color.GREEN);
            this.gameinfoRunning.setSelectionBackground(Color.GREEN);
            this.gameinfoRunning.setSelectionForeground(Color.BLACK);
            this.gameinfoRunning.setModel(new DefaultTableModel() {

                private static final long serialVersionUID = -5995847295484708948L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });

            this.gameinfoRunning.setDoubleBuffered(true);
            this.gameinfoRunning.setIntercellSpacing(new Dimension(0, 0));
            this.gameinfoRunning.setShowVerticalLines(false);
            this.gameinfoRunning.setShowHorizontalLines(false);
            this.gameinfoRunning.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.gameinfoRunning.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {

                        public void valueChanged(ListSelectionEvent e) {
                            int index = GameLobby.this.gameinfoRunning.getSelectedRow();

                            if (index < 0) {
                                GameLobby.this.joinButton.setEnabled(false);
                            } else {
                                int indeX = GameLobby.this.gameinfoWaiting.getSelectedRow();
                                if (indeX != -1) {
                                    GameLobby.this.gameinfoWaiting.removeRowSelectionInterval(indeX, indeX);
                                }
                                int GameID = Integer.parseInt(GameLobby.this.gameinfoRunning.getValueAt(index, 7).toString());
                                GameLobby.this.joinButton.setEnabled(false);
                                // Set Game info label
                                GameLobby.this.setGameInfoEditorPaneSelectGame(GameID);
                            }
                        }
                    });
        }
        this.gamesScrollPaneRunning.setViewportView(this.gameinfoRunning);
        return this.gamesScrollPaneRunning;
    }

    /**
     * Gets all available games and add them to the gameslist textarea.
     *
     * @param g
     *            the gamesMessage
     */
    private synchronized void setGameList(GamesMessage g) {

        int oldSelectionWaiting = this.gameinfoWaiting.getSelectedRow();
        int oldSelectionRunning = this.gameinfoRunning.getSelectedRow();

        final Object[] headerNames = new Object[]{_("Game name"), _("Players"), _("Map"), _("Mode"), _("Min-Max"), _("PW"), _("State"), "GameID"};

        final Vector<Vector<String>> rowsWaiting = new Vector<Vector<String>>();
        final Vector<Vector<String>> rowsRunning = new Vector<Vector<String>>();

        final DefaultTableModel modelWaiting = (DefaultTableModel) this.gameinfoWaiting.getModel();
        final DefaultTableModel modelRunning = (DefaultTableModel) this.gameinfoRunning.getModel();

        if (g != null) {
            this.games = new ArrayList<GameDescription>(g.getGames());
            Collections.sort(this.games, new Comparator<GameDescription>() {

                public int compare(GameDescription a, GameDescription b) {
                    return a.getState().compareToIgnoreCase(b.getState()) * -1;
                }
            });
            this.playersInGame = 0;
            int count = 0;
            for (GameDescription gd : this.games) {
                final int fullCount = gd.getNumberOfPlayers();
                final int playerCount = gd.getCurrentPlayers();
                this.playersInGame = this.playersInGame + playerCount;
                final Vector<String> rowsData = new Vector<String>();

                rowsData.add(gd.getGameName());
                Map<String, String> args = new HashMap<String, String>();
                args.put("min", String.valueOf(playerCount));
                args.put("max", String.valueOf(fullCount));
                rowsData.add(_("%min% of %max%", args));
                String map = Constants.Map.getMapById(gd.getMapId()).toString();
                if (map.equals("Random_Map")) {
                    map = "Random map";
                    map = _(map);
                }
                rowsData.add(map);

                // Mode
                rowsData.add("<html><b>" + _(gd.getGameModeString()) + "</b></html>");
                rowsData.add(((gd.getMinEloPoints() == 0) ? _("all") : gd.getMinEloPoints()) + "-" + ((gd.getMaxEloPoints() == 0) ? _("all") : gd.getMaxEloPoints()));
                rowsData.add((gd.getPassword().equals("yes") ? "<html><DIV style=\"color:red;\"><b>" + _(gd.getPassword()) + "</DIV></HTML>" : _(gd.getPassword())));

                // State
                String State = _(gd.getState());
                String StateMSG = "";
                if (State.compareToIgnoreCase(_("waiting")) == 0) {
                    StateMSG = "<html><DIV style=\"color:red;\"><b>" + State + "</DIV></HTML>";
                } else if (State.compareToIgnoreCase(_("ended")) == 0) {
                    StateMSG = "<html><DIV style=\"color:yellow;\">" + State + "</DIV></HTML>";
                } else {
                    StateMSG = "" + State;
                }
                rowsData.add(StateMSG);

                // GameID (not visible)
                rowsData.add("" + count);

                if (State.equals(_("waiting"))) {
                    rowsWaiting.add(rowsData);
                } else {
                    rowsRunning.add(rowsData);
                }
                count++;
            }
        }

        TableColumn col = null;

        // Show information message if there are no games waiting for players
        if (rowsWaiting.size() == 0) {
            int selectedRow = this.gameinfoWaiting.getSelectedRow();
            if (selectedRow != -1) {
                this.gameinfoWaiting.removeRowSelectionInterval(selectedRow, selectedRow);
                oldSelectionWaiting = -1;
            }
            final Object[] Info = new Object[]{_("For your information")};
            final Vector<String> rowsData = new Vector<String>();
            rowsData.add("<html><DIV style='color:red;'><h3>&nbsp;" + _("There are currently no games waiting for players.") + "<br>&nbsp;" + _("Please provide a new. (Create game)") + "</h3></div></html>");
            rowsWaiting.add(rowsData);
            modelWaiting.setDataVector(rowsWaiting, new Vector<Object>(Arrays.asList(Info)));
            modelWaiting.fireTableDataChanged();
            this.gameinfoWaiting.setEnabled(false);

            col = gameinfoWaiting.getColumnModel().getColumn(0);
            col.setPreferredWidth(gamesTabbedPane.getWidth());
            col.setMinWidth(gamesTabbedPane.getWidth());
            col.setMaxWidth(gamesTabbedPane.getWidth());
            gameinfoWaiting.setRowHeight(50);


            // Show games waiting for players
        } else {
            modelWaiting.setDataVector(rowsWaiting, new Vector<Object>(Arrays.asList(headerNames)));
            modelWaiting.fireTableDataChanged();
            gameinfoWaiting.setRowHeight(gameinfoRunning.getRowHeight());
            this.gameinfoWaiting.setEnabled(true);
            col = gameinfoWaiting.getColumnModel().getColumn(0); // Game name
            col.setPreferredWidth(205);
            col.setMinWidth(205);
            col.setMaxWidth(205);

            col = gameinfoWaiting.getColumnModel().getColumn(1); // Players
            col.setPreferredWidth(50);
            col.setMinWidth(50);
            col.setMaxWidth(50);

            col = gameinfoWaiting.getColumnModel().getColumn(2); // Map
            col.setPreferredWidth(100);
            col.setMinWidth(100);
            col.setMaxWidth(100);

            col = gameinfoWaiting.getColumnModel().getColumn(3); // Mode
            col.setPreferredWidth(100);
            col.setMinWidth(100);
            col.setMaxWidth(100);

            col = gameinfoWaiting.getColumnModel().getColumn(4); // Min/max
            col.setPreferredWidth(65);
            col.setMinWidth(65);
            col.setMaxWidth(65);

            col = gameinfoWaiting.getColumnModel().getColumn(5); // PW
            col.setPreferredWidth(30);
            col.setMinWidth(30);
            col.setMaxWidth(30);

            col = gameinfoWaiting.getColumnModel().getColumn(6); // State
            col.setPreferredWidth(50);
            col.setMinWidth(50);
            col.setMaxWidth(50);

            col = gameinfoWaiting.getColumnModel().getColumn(7); // gameId
            col.setPreferredWidth(0);
            col.setMinWidth(0);
            col.setMaxWidth(0);
            col.setWidth(0);
        }

        modelRunning.setDataVector(rowsRunning, new Vector<Object>(Arrays.asList(headerNames)));
        modelRunning.fireTableDataChanged();

        col = gameinfoRunning.getColumnModel().getColumn(0); // Game name
        col.setPreferredWidth(205);
        col.setMinWidth(205);
        col.setMaxWidth(205);

        col = gameinfoRunning.getColumnModel().getColumn(1); // PLayers
        col.setPreferredWidth(50);
        col.setMinWidth(50);
        col.setMaxWidth(50);

        col = gameinfoRunning.getColumnModel().getColumn(2); // Map
        col.setPreferredWidth(100);
        col.setMinWidth(100);
        col.setMaxWidth(100);

        col = gameinfoRunning.getColumnModel().getColumn(3); // Mode
        col.setPreferredWidth(100);
        col.setMinWidth(100);
        col.setMaxWidth(100);

        col = gameinfoRunning.getColumnModel().getColumn(4); // Min/max
        col.setPreferredWidth(65);
        col.setMinWidth(65);
        col.setMaxWidth(65);

        col = gameinfoRunning.getColumnModel().getColumn(5); // PW
        col.setPreferredWidth(30);
        col.setMinWidth(30);
        col.setMaxWidth(30);

        col = gameinfoRunning.getColumnModel().getColumn(6); // State
        col.setPreferredWidth(50);
        col.setMinWidth(50);
        col.setMaxWidth(50);

        col = gameinfoRunning.getColumnModel().getColumn(7); // gameId
        col.setPreferredWidth(0);
        col.setMinWidth(0);
        col.setMaxWidth(0);
        col.setWidth(0);

        if (oldSelectionWaiting != -1) {
            try {
                this.gameinfoWaiting.setRowSelectionInterval(oldSelectionWaiting, oldSelectionWaiting);
            } catch (IllegalArgumentException e) {
                GameLobby.this.logger.info("setRowSelectionInterval illegal argument");
                this.setGameInfoEditorPaneHTML(null);
            }
        } else if (oldSelectionRunning != -1) {
            try {
                this.gameinfoRunning.setRowSelectionInterval(oldSelectionRunning, oldSelectionRunning);
            } catch (IllegalArgumentException e) {
                GameLobby.this.logger.info("setRowSelectionInterval illegal argument");
                this.setGameInfoEditorPaneHTML(null);
            }
        } else {
            this.setGameInfoEditorPaneHTML(null);
        }
    }

    /**
     * Creates the player list.
     *
     * @return a scrollpane with the playerList
     */
    private JScrollPane initPlayersList() {
        this.playerList = new JList();
        this.playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.playerList.setLayoutOrientation(JList.VERTICAL);
        this.playerList.setVisibleRowCount(-1);

        if (this.playersScrollPane == null) {
            this.playersScrollPane = new JScrollPane(this.playerList);
        }
        return this.playersScrollPane;

    }

    /**
     * Renderer for player cells.
     */
    private class PlayerCellRenderer implements ListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Player p = (Player) value;
            JLabel label = new JLabel();
            label.setLayout(null);
            label.setBorder(new EmptyBorder(2, 1, 1, 1));
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            String html = "<html><table><tr><td width=\"210\">";
            if (GameLobby.this.getCore().getPlayerName().equalsIgnoreCase(p.getName())) {
                html += "<font color=\"red\">" + p.getName() + "</font>";
            } else {
                if (p.state.equals("ingame")) {
                    html += "<font color=\"gray\">" + p.getName() + "</font>";
                } else {
                    html += p.name;
                }
            }
            if (!Core.isLANVersion()) {
                html += " <font color=\"gray\">(" + p.getPoints() + ")</font>";
                html += "</td><td align=\"right\" width=\"50\">";
                URL skillImageURL = SkillImage.getURL(p.getSkill());
                html += "<img src=\"" + skillImageURL + "\">";
            }
            html += "</td></tr></table></html>";
            if (isSelected) {
                label.setForeground(Color.YELLOW);
            } else {
                label.setForeground(Color.GREEN);
            }
            label.setText(html);
            label.setToolTipText("<html><b>" + p.getName() + "</b><br>" + _("Points") + ": " + p.getPoints() + "<br>" + _("Skill") + ": " + p.getSkill() + "</html>");
            return label;
        }
    }

    /**
     * Update the players list
     */
    private void updatePlayersList() {
        int selindex = this.playerList.getSelectedIndex();
        DefaultListModel listModel = new DefaultListModel();
        synchronized (allPlayers) {
            Iterator<Player> i = allPlayers.values().iterator();
            while (i.hasNext()) {
                Player p = i.next();
                listModel.addElement(p);
            }
        }
        this.playerList.setCellRenderer(new PlayerCellRenderer());
        this.playerList.setModel(listModel);
        this.playerList.setSelectedIndex(selindex);
    }

    /**
     * Process a players message.
     *
     * @param pm The playersMessage
     */
    private void processPlayersMessage(PlayersMessage pm) {
        if (pm != null) {
            for (PlayersMessage.Player p : pm.getPlayers()) {
                if (p.operation.equals("add") || p.operation.equals("join")) {
                    Player pon = allPlayers.get(p.name.toLowerCase());
                    if (pon == null) {
                        new Player(p.id, p.name, p.points, p.skill, "inlobby");
                    } else {
                        logger.warning("Got player \"add\" but player is already listed: " + pon);
                    }
                    // if (p.operation.equals("join")) {
                    //     Map<String,String> args = new HashMap<String,String>();
                    //     args.put("name", p.name);
                    //    this.chatdialog.sendChatText("Server", _("%name% has joined!", args), Core.getInstance());
                    // }
                } else if (p.operation.equals("remove") || p.operation.equals("leave")) {
                    Player pon = allPlayers.get(p.name.toLowerCase());
                    if (pon != null) {
                        pon.remove();
                    }
                    // if (p.operation.equals("leave")) {
                    //     Map<String,String> args = new HashMap<String,String>();
                    //     args.put("name", p.name);
                    //     this.chatdialog.sendChatText(_("Server"), _("%name% has left...", args), Core.getInstance());
                    // }
                } else if (p.operation.equals("ingame")) {
                    Player pon = allPlayers.get(p.name.toLowerCase());
                    if (pon != null) {
                        pon.state = "ingame";
                    }
                    Map<String, String> args = new HashMap<String, String>();
                    args.put("name", p.name);
                    this.chatdialog.sendChatText(_("Server"), _("<b>%name%</b> joined a game...", args), false, Core.getInstance());
                } else if (p.operation.equals("outgame")) {
                    Player pon = allPlayers.get(p.name.toLowerCase());
                    if (pon != null) {
                        pon.points = p.points;
                        pon.skill = p.skill;
                        pon.state = "";
                    }
                    Map<String, String> args = new HashMap<String, String>();
                    args.put("name", p.name);
                    this.chatdialog.sendChatText(_("Server"), _("<b>%name%</b> returned from playing...", args), false, Core.getInstance());
                } else {
                    logger.warning("Invalid players list operation: " + p.operation + " for " + p);
                }
            }
        }
        this.setPlayerCountLabel();
    }

    /**
     * Set the Game Info Label, NULL = Start Screen
     *
     * @param txt Set the Label info
     *
     */
    public void setGameInfoEditorPaneHTML(String HTML) {
        String content = "<html><body text=\"#00FF00\" style=\"font-family: Arial\" bgcolor=\"#000000\">";
        if (HTML == null || HTML.equals("")) {
            URL imageURL = this.getClass().getClassLoader().getResource("com/creeptd/client/resources/panel/lobby.jpg");
            content = content + "<center><img width=270 height=200 src=\"" + imageURL + "\"></center>";
            this.setBorder(new EmptyBorder(0, 0, 0, 0));
        } else {
            content = content + HTML;
            this.setBorder(new LineBorder(Color.GRAY));
        }
        content = content + "</body></html>";
        this.gameInfoEditorPaneContent = content; // Updated by swing timer
    }

    /**
     * Set the LabelGameInfo with Slected Game Info
     *
     * @param selectedRow
     */
    public synchronized void setGameInfoEditorPaneSelectGame(int selectedRow) {
        GameDescription GameRow = this.games.get(selectedRow);
        java.net.URL imageURL = getClass().getClassLoader().getResource(Constants.Map.getPictureThumbnailPath(Constants.Map.getMapById(GameRow.getMapId()).toString()));

        String mode = "<b>" + _(GameRow.getGameModeString()) + "</b>";
        String state = "";
        if (GameRow.getState().compareToIgnoreCase("waiting") == 0) {
            state = "<span style=\"color:red;\"><b>" + _(GameRow.getState()) + "</span>";
        } else if (GameRow.getState().compareToIgnoreCase("ended") == 0) {
            state = "<span style=\"color:yellow;\">" + _(GameRow.getState()) + "</span>";
        } else {
            state = _(GameRow.getState());
        }
        Map<String, String> minOfMaxArgs = new HashMap<String, String>();
        minOfMaxArgs.put("min", GameRow.getCurrentPlayers() + "");
        minOfMaxArgs.put("max", GameRow.getNumberOfPlayers() + "");
        String minSkill = GameRow.getMinEloPoints() == 0 ? _("all") : GameRow.getMinEloPoints() + "";
        String maxSkill = GameRow.getMaxEloPoints() == 0 ? _("all") : GameRow.getMaxEloPoints() + "";
        String txt = "<div align=\"center\"><div style=\"background: gray; color: white; margin-bottom: 5\"><b>" + GameRow.getGameName() + "</b><br>(" + Constants.Map.getMapById(GameRow.getMapId()).toString() + ")<br> </div>" + "<table border='0' style='border-collapse: collapse' width='270' height='102' cellpadding='3'>" + "<tr>" + "<td width='122' valign='top' height='102'>" + "<center>" + "	<img src='" + imageURL + "' width='100' height='100'></center></td>" + "	<td valign='top' width='150' height='102'>" + "	" + _("Players") + ": " + _("%min% of %max%", minOfMaxArgs) + "<br>" + "	" + _("Mode") + ": " + _(mode) + "<br>" + "	" + _("State") + ": " + state + "<br>" + "	" + _("Min/Max") + ": " + minSkill + "-" + maxSkill + "<br>" + "	" + _("Password") + ": " + _(GameRow.getPassword()) + "<br>" + "  </td>" + "</tr>" + "</table>" + "</div><div style=\"margin-top: 5\">" + "<b>" + _("Players") + ":</b> " + GameRow.getPlayer1() + " " + GameRow.getPlayer2() + " " + GameRow.getPlayer3() + " " + GameRow.getPlayer4() + "<br>" + "</div></font>";

        this.setGameInfoEditorPaneHTML(txt);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() {
        this.getCore().getNetwork().removeListener(this);
        if (this.swingUpdateTimer != null) {
            this.swingUpdateTimer.stop();
            this.swingUpdateTimer = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        managementSound = this.getCore().getSoundManagement();
        this.getCore().getNetwork().addListener(this);
        this.repaint();
        this.setGameInfoEditorPaneHTML(null);
        // Using Swing timers to update component contents
        this.swingUpdateTimer = new Timer(250, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePlayersList();
                if (gameInfoEditorPaneContent != null) {
                    gameInfoEditorPane.setText(gameInfoEditorPaneContent);
                    gameInfoEditorPaneContent = null;
                }
            }
        });
        this.swingUpdateTimer.setRepeats(true);
        this.swingUpdateTimer.start();
    }

    /**
     * {@inheritDoc}
     */
    public void update(ServerMessage m) {
        if (m instanceof GamesMessage) {
            this.setGameList((GamesMessage) m);
        } else if (m instanceof PlayersMessage) {
            PlayersMessage pm = (PlayersMessage) m;
            this.processPlayersMessage((PlayersMessage) m);
        } else if (m instanceof ServerChatMessage) {
            ServerChatMessage scm = (ServerChatMessage) m;
            String msg = scm.getMessage();
            if (scm.getTranslate()) {
                msg = _(msg);
            }
            this.chatdialog.sendChatText(scm.getPlayerName(), msg, scm.isAction(), getCore());
            if (managementSound != null) {
                managementSound.clapSound();
            }
        } else if (m instanceof JoinGameResponseMessage) {
            JoinGameResponseMessage jgrm = (JoinGameResponseMessage) m;
            if (jgrm.getResponseType().equals(Constants.ResponseType.ok)) {
                WaitingGamePanel wgp = new WaitingGamePanel();
                if (this.joinGame == null) {
                    logger.severe("The game to join was null!");
                    errorDialog(_("You cannot join to this game."));
                } else {
                    wgp.setMapId(this.joinGame.getMapId());
                    wgp.setGameMode(this.joinGame.getGameMode());
                    this.getCore().setActiveGame(this.joinGame);
                    this.getCore().pushScreen(wgp);
                }
            } else if (jgrm.getResponseType().equals(Constants.ResponseType.multi)) {
                errorDialog(_("You already joined to this game."));
            } else {
                errorDialog(_("You cannot join to this game."));
            }
            this.joinButton.setEnabled(true);
        }
    }

    /**
     * Rewrite online players number label
     */
    public synchronized void setPlayerCountLabel() {
        Map<String, String> args = new HashMap<String, String>();
        args.put("n", (allPlayers.size() + this.playersInGame) + "");
        onlinePlayersLabel.setText(_("%n% players online", args));
    }

    /**
     * @param msg
     *            msg
     */
    public void errorDialog(String msg) {
        UIManager.put("OptionPane.background", Color.BLACK);
        UIManager.put("Panel.background", Color.BLACK);
        UIManager.put("OptionPane.messageForeground", Color.GREEN);
        JOptionPane.showMessageDialog(this, msg, _("An error occured"), JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        GameLobby gl = new GameLobby();
        JFrame f = new JFrame();
        f.setSize(933, 700);
        f.setLocation(100, 100);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(gl);
        f.setVisible(true);
    }
}

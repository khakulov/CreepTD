package com.creeptd.client.panel;

import static com.creeptd.client.i18n.Translator._;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.html.HTMLEditorKit;

import com.creeptd.client.Core;
import com.creeptd.client.i18n.Language;
import com.creeptd.client.network.MessageListener;
import com.creeptd.client.panel.common.Button;
import com.creeptd.client.panel.common.Chat;
import com.creeptd.client.panel.common.ChatDialog;
import com.creeptd.client.panel.common.OptionsPanel;
import com.creeptd.client.panel.game.WaitingGameScreen;
import com.creeptd.common.Constants;
import com.creeptd.common.messages.client.ChatMessage;
import com.creeptd.common.messages.client.JoinGameRequestMessage;
import com.creeptd.common.messages.client.LogoutMessage;
import com.creeptd.common.messages.client.RefreshMessage;
import com.creeptd.common.messages.server.GameDescription;
import com.creeptd.common.messages.server.GamesMessage;
import com.creeptd.common.messages.server.JoinGameResponseMessage;
import com.creeptd.common.messages.server.PlayersMessage;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.common.messages.server.ServerMessage;

/**
 * The GameLobby screen.
 */
public class LobbyScreen extends Screen implements MessageListener {

	private static final long serialVersionUID = 4866661364906538715L;
    private final static Logger logger = Logger.getLogger(LobbyScreen.class.getName());

    // Components
    private JLabel onlinePlayersLabel;
    private JEditorPane gameInfoEditorPane;
    private String gameInfoEditorPaneContent = "";
    private final JTextField message;
    private Chat chatdialog;
    private JScrollPane chatScrollPane;
    private JList playerList;
    private JScrollPane playersScrollPane;
    private JTabbedPane gamesTabbedPane;
    private JTable gameinfoWaiting;
    private JScrollPane gamesScrollPaneWaiting;
    private JTable gameinfoRunning;
    private JScrollPane gamesScrollPaneRunning;
    private final JButton optionsButton;
    // private final JButton forum;
    private JButton quit;
    private final JButton send;
    private int joinGameId;
    private GameDescription joinGame;
    private ArrayList<GameDescription> games;
    private int playersInGame;
    private boolean playersChanged = true;
    public static final SortedMap<String, Player> allPlayers = new TreeMap<String, Player>();
    private Timer swingUpdateTimer = null;
    private boolean waitingForRefresh = false;
    private static JDialog optionsPanel = null;

    public static void openOptionsPanel() {
        if (optionsPanel != null) return;
        optionsPanel = new OptionsPanel();
        optionsPanel.setVisible(true);
    }

    public static void onCloseOptionsPanel() {
        optionsPanel = null;
    }

    /**
     * A player
     */
    public class Player implements Comparable<Player> {

        private int id;
        private String name;
        private int points;
        private int skill;
        private boolean inGame = false;
        private String language = "en_US";

        public Player(int id, String name, int points, int skill, String state, String language) {
            this.id = id;
            this.name = name;
            this.points = points;
            this.skill = skill;
            this.language = language;
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

        public boolean isInGame() {
            return this.inGame;
        }
        
        public void setInGame(boolean inGame) {
            this.inGame = inGame;
        }

        public String getLanguage() {
            return this.language;
        }
        
        public int compareTo(Player p) {
            if (this.inGame && !p.inGame) {
                return 1;
            }
            if (!this.inGame && p.inGame) {
                return -1;
            }
            return this.name.toLowerCase().compareTo(p.name.toLowerCase());
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
    public LobbyScreen() {
        this.setLayout(null);
        this.setBackground(Color.BLACK);

        UIManager.put("ToolTip.background", Color.WHITE);
        UIManager.put("ToolTip.foreground", Color.DARK_GRAY);
        UIManager.put("ToolTip.border", new EmptyBorder(2, 2, 2, 2));
        
        this.onlinePlayersLabel = new JLabel(_("Loading..."), SwingConstants.RIGHT);
        this.onlinePlayersLabel.setBounds(635, 10, 270, 20);
        this.onlinePlayersLabel.setForeground(Color.GRAY);
        this.onlinePlayersLabel.setFont(new Font("Arial", Font.BOLD, 11));
        this.setPlayerCountLabel();

        this.gameInfoEditorPane = new JEditorPane();
        this.gameInfoEditorPane.setBounds(640, 41, 270, 200);
        this.gameInfoEditorPane.setEditable(false);
        this.gameInfoEditorPane.setFont(new Font("Arial", Font.PLAIN, 10));
        this.gameInfoEditorPane.setEditorKit(new HTMLEditorKit());
        this.gameInfoEditorPane.setAutoscrolls(true);
        this.gameInfoEditorPane.setFocusable(false);
        this.gameInfoEditorPane.setDoubleBuffered(true);
        this.gameInfoEditorPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        this.gameInfoEditorPane.setBackground(Color.BLACK);
        this.gameInfoEditorPane.setVisible(false);

        this.playersScrollPane = this.initPlayersList();
        this.playersScrollPane.setBounds(640, 285, 270, 390);
        this.playersScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        this.playerList.setBackground(Color.BLACK);
        this.playerList.setForeground(Color.GREEN);

        this.gamesTabbedPane = new JTabbedPane();
        this.gamesTabbedPane.setBounds(20, 20, 600, 260);
        this.gamesTabbedPane.setForeground(Color.black);

        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 11));
        UIManager.put("TabbedPane.contentBorderInsets", new InsetsUIResource(0, 0, 0, 0));
        UIManager.put("TabbedPane.border", new EmptyBorder(0, 0, 0, 0));
        UIManager.put("TabbedPane.darkShadow", Color.lightGray);
        UIManager.put("TabbedPane.focus", Color.lightGray);
        UIManager.put("TabbedPane.highlight", Color.lightGray);
        UIManager.put("TabbedPane.shadow", Color.lightGray);
        UIManager.put("TabbedPane.selected", Color.white);
        UIManager.put("TabbedPane.tabAreaBackground", Color.black);
        SwingUtilities.updateComponentTreeUI(this.gamesTabbedPane);

        this.gamesTabbedPane.setOpaque(false);

        this.gamesScrollPaneWaiting = new JScrollPane();
        this.gamesScrollPaneWaiting.setBorder(new LineBorder(Color.GRAY, 1));
        this.gamesScrollPaneWaiting = this.initGamesTableWaiting();
        this.gamesScrollPaneWaiting.setBackground(Color.BLACK);
        this.gamesTabbedPane.addTab(_("Games waiting for players"), null, this.gamesScrollPaneWaiting);

        this.gamesScrollPaneRunning = new JScrollPane();
        this.gamesScrollPaneRunning.setBorder(new LineBorder(Color.GRAY, 1));
        this.gamesScrollPaneRunning = this.initGamesTableRunning();
        this.gamesScrollPaneRunning.setBackground(Color.BLACK);
        this.gamesTabbedPane.addTab(_("Games currently running"), null, this.gamesScrollPaneRunning);

        this.chatScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.chatScrollPane.setPreferredSize(new Dimension(500, 180));
        this.chatScrollPane.setBounds(20, 285, 600, 360);
        this.chatScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        this.chatdialog = new Chat(this.chatScrollPane);
        this.chatdialog.setFont(new Font("Arial", Font.PLAIN, 11));
        this.chatScrollPane.setViewportView(this.chatdialog);

        // Welcome Chat MSG
        chatdialog.addChatText("Server", _("Welcome to CreepTD Chat! :)"), false);
        chatdialog.addChatText("Server", _("Please avoid flaming and spam."), false);

        this.message = new JTextField();
        this.message.setBounds(20, 650, 500, 25);
        this.message.setEditable(true);
        this.message.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.setGameScreenFocus(message);


        this.optionsButton = new Button(_("Options"));
        this.optionsButton.setBounds(640, 250, 130, 25);
        this.optionsButton.setBackground(Color.BLACK);
        this.optionsButton.setForeground(Color.YELLOW);

        this.quit = new Button(_("Quit"));
        this.quit.setBounds(780, 250, 130, 25);
        this.quit.setBackground(Color.BLACK);
        this.quit.setForeground(Color.RED);

        this.send = new Button(_("Send"));
        this.send.setBounds(520, 650, 100, 25);
        this.send.setBackground(Color.BLACK);
        this.send.setForeground(Color.WHITE);

        this.add(this.gameInfoEditorPane);
        this.add(this.playersScrollPane);
        this.add(this.gamesTabbedPane);
        this.add(this.chatScrollPane);
        this.add(this.message);
        this.add(this.onlinePlayersLabel);
        this.add(this.quit);
        this.add(this.send);
        this.add(this.optionsButton);

        ActionListener a4 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ((LobbyScreen.this.message.getText() != null) && (!LobbyScreen.this.message.getText().equals(""))) {
                    if (LobbyScreen.this.message.getText().length() > 250) {
                        Map<String, String> args = new HashMap<String, String>();
                        args.put("n", "250");
                        LobbyScreen.this.errorDialog(_("Please don't enter more than %n% characters.", args));
                        return;
                    } else {
                        ChatMessage m = new ChatMessage();
                        m.setMessage(LobbyScreen.this.message.getText());
                        LobbyScreen.this.message.setText("");
                        m.setClientId(Core.getInstance().getPlayerId());
                        Core.getInstance().getNetwork().sendMessage(m);
                    }
                }
            }
        };
        this.send.addActionListener(a4);
        this.message.addActionListener(a4);

        // Quitbutton
        ActionListener a5 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LogoutMessage gm = new LogoutMessage();
                Core.getInstance().getNetwork().sendMessage(gm);
                Core.getInstance().getNetwork().disconnect();
                Core.getInstance().popScreen();
            }
        };
        this.quit.addActionListener(a5);

        quit.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                LogoutMessage gm = new LogoutMessage();
                Core.getInstance().getNetwork().sendMessage(gm);
                Core.getInstance().getNetwork().disconnect();
                Core.getInstance().popScreen();
            }
        });

        this.message.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    LobbyScreen.this.message.postActionEvent();
                }
            }
        });

        this.optionsButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                openOptionsPanel();
            }
        });
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
                            int index = LobbyScreen.this.gameinfoWaiting.getSelectedRow();
                            if (index >= 0) {
                                int indeX = LobbyScreen.this.gameinfoRunning.getSelectedRow();
                                if (indeX != -1) {
                                    LobbyScreen.this.gameinfoRunning.removeRowSelectionInterval(indeX, indeX);
                                }
                                int GameID = Integer.parseInt(LobbyScreen.this.gameinfoWaiting.getValueAt(index, 8).toString());
                                LobbyScreen.this.joinGame = LobbyScreen.this.games.get(GameID);

                                // Set Game info label
                                LobbyScreen.this.setGameInfoEditorPaneSelectGame(GameID);
                            }
                        }
                    });
            this.gameinfoWaiting.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() >= 2) {
                        if (LobbyScreen.this.joinGame != null && 
                        	LobbyScreen.this.joinGame.isSpectateable() &&
                            LobbyScreen.this.joinGame.numSpectators() < Constants.MAX_SPECTATORS) {
                            JoinGameRequestMessage grm = new JoinGameRequestMessage();
                            String pw = null;
                            if (LobbyScreen.this.joinGame == null) {
                                LobbyScreen.logger.info("Could not select the game");
                                return;
                            }
                            if (!LobbyScreen.this.joinGame.getState().equals("waiting")) {
                                errorDialog(_("Sorry, this game has already started."));
                                return;
                            }
                            if (!LobbyScreen.this.joinGame.isSpectateable()) {
                                LobbyScreen.logger.info("Could not select the game");
                            }
                            LobbyScreen.this.joinGameId = LobbyScreen.this.joinGame.getGameId();
                            grm.setGameId(LobbyScreen.this.joinGameId);
                            grm.setPassword(pw);
                            grm.setSpectate(true);
                            Core.getInstance().getNetwork().sendMessage(grm);
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
                            int index = LobbyScreen.this.gameinfoRunning.getSelectedRow();

                            if (index >= 0) {
                                int indeX = LobbyScreen.this.gameinfoWaiting.getSelectedRow();
                                if (indeX != -1) {
                                    LobbyScreen.this.gameinfoWaiting.removeRowSelectionInterval(indeX, indeX);
                                }
                                int GameID = Integer.parseInt(LobbyScreen.this.gameinfoRunning.getValueAt(index, 8).toString());
                                // Set Game info label
                                LobbyScreen.this.setGameInfoEditorPaneSelectGame(GameID);
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

        final Object[] headerNames = new Object[]{_("Game name"), _("Players"), _("Map"), _("Mode"), _("Min-Max"), _("PW"), _("State"), "Spectacable", "GameID"};

        final Vector<Vector<String>> rowsWaiting = new Vector<Vector<String>>();
        final Vector<Vector<String>> rowsRunning = new Vector<Vector<String>>();

        final DefaultTableModel modelWaiting = (DefaultTableModel) this.gameinfoWaiting.getModel();
        final DefaultTableModel modelRunning = (DefaultTableModel) this.gameinfoRunning.getModel();

        this.games = new ArrayList<GameDescription>(g.getGames());

        Collections.sort(this.games, new Comparator<GameDescription>() {
            public int compare(GameDescription a, GameDescription b) {
                return a.getState().compareToIgnoreCase(b.getState()) * -100
                	+ a.getGameMode().compareTo(b.getGameMode()) * -10
                	+ a.getCurrentPlayers().compareTo(b.getCurrentPlayers())*-1;
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
            String lPlayer = _("%min% of %max%", args);
            if (gd.isSpectateable()) {
                lPlayer += " ("+gd.numSpectators()+")";
            }
            rowsData.add(lPlayer);
            String map = Constants.Map.getMapById(gd.getMapId()).toString();
            if (map.equals("Random_Map")) {
                map = "Random map";
                map = _(map);
            }
            rowsData.add(map);

            // Mode
            rowsData.add("<html><b>" + _(gd.getGameModeString()) + "</b></html>");
            rowsData.add(((gd.getMinSkill() == 0) ? _("all") : gd.getMinSkill()) + "-" + ((gd.getMaxSkill() == 0) ? _("all") : gd.getMaxSkill()));
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

            rowsData.add(gd.isSpectateable().toString());
            // GameID
            rowsData.add(""+count);

            if (State.equals(_("waiting"))) {
                rowsWaiting.add(rowsData);
            } else {
                rowsRunning.add(rowsData);
            }
            count++;
        }

        TableColumn col = null;

        modelWaiting.setDataVector(rowsWaiting, new Vector<Object>(Arrays.asList(headerNames)));
        modelWaiting.fireTableDataChanged();
        gameinfoWaiting.setRowHeight(gameinfoRunning.getRowHeight());
        this.gameinfoWaiting.setEnabled(true);
        col = gameinfoWaiting.getColumnModel().getColumn(0); // Game name
        col.setPreferredWidth(145);
        col.setMinWidth(145);
        col.setMaxWidth(145);

        col = gameinfoWaiting.getColumnModel().getColumn(1); // Players
        col.setPreferredWidth(60);
        col.setMinWidth(60);
        col.setMaxWidth(60);

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


        col = gameinfoWaiting.getColumnModel().getColumn(7);
        col.setPreferredWidth(50);
        col.setMinWidth(50);
        col.setMaxWidth(50);

        col = gameinfoWaiting.getColumnModel().getColumn(8); // gameId
        col.setPreferredWidth(0);
        col.setMinWidth(0);
        col.setMaxWidth(0);
        col.setWidth(0);


        modelRunning.setDataVector(rowsRunning, new Vector<Object>(Arrays.asList(headerNames)));
        modelRunning.fireTableDataChanged();

        col = gameinfoRunning.getColumnModel().getColumn(0); // Game name
        col.setPreferredWidth(145);
        col.setMinWidth(145);
        col.setMaxWidth(145);

        col = gameinfoRunning.getColumnModel().getColumn(1); // Players
        col.setPreferredWidth(60);
        col.setMinWidth(60);
        col.setMaxWidth(60);

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

        col = gameinfoRunning.getColumnModel().getColumn(7);
        col.setPreferredWidth(50);
        col.setMinWidth(50);
        col.setMaxWidth(50);

        col = gameinfoRunning.getColumnModel().getColumn(8); // gameId
        col.setPreferredWidth(0);
        col.setMinWidth(0);
        col.setMaxWidth(0);
        col.setWidth(0);

        if (oldSelectionWaiting != -1) {
            try {
                this.gameinfoWaiting.setRowSelectionInterval(oldSelectionWaiting, oldSelectionWaiting);
            } catch (IllegalArgumentException e) {
                logger.info("setRowSelectionInterval illegal argument");
                this.setGameInfoEditorPaneHTML(null);
            }
        } else if (oldSelectionRunning != -1) {
            try {
                this.gameinfoRunning.setRowSelectionInterval(oldSelectionRunning, oldSelectionRunning);
            } catch (IllegalArgumentException e) {
                logger.info("setRowSelectionInterval illegal argument");
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
        
        this.playerList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                	Player player = (Player)list.getModel().getElementAt(index);
                	ChatDialog.getOrCreate(player.getName());
                }
            }
        });

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
            String html = "<html>&nbsp;<img src=\""+Language.getIconURLForLanguage(p.language)+"\">&nbsp;";
            if (Core.getInstance().getPlayerName().equalsIgnoreCase(p.getName())) {
                html += "<font color=\"red\">" + p.getName() + "</font>";
            } else {
                if (p.isInGame()) {
                    html += "<font color=\"gray\">" + p.getName() + "</font>";
                } else {
                    html += p.name;
                }
            }

            html += " <font color=\"gray\">(" + p.getPoints() + "/" + p.getSkill() +")</font></html>";
            if (isSelected) {
                label.setForeground(Color.YELLOW);
            } else {
                label.setForeground(Color.GREEN);
            }
            label.setText(html);
            String tt = "<html><b>" + p.getName() + "</b><br>" + _("Points") + ": " + p.getPoints() + "<br>" + _("Skill") + ": " + p.getSkill();
            if (p.isInGame()) {
                tt += "<br /><i>"+_("In game")+"</i>";
            }
            tt += "</html>";
            label.setToolTipText(tt);
            return label;
        }
    }

    /**
     * Update the players list
     */
    private void updatePlayersList() {
        if (!playersChanged) return;
        playersChanged = false;
        int selindex = this.playerList.getSelectedIndex();
        // Sort the players
        List<Player> list = new LinkedList<Player>();
        synchronized (allPlayers) {
            Iterator<Player> i = allPlayers.values().iterator();
            while (i.hasNext()) {
                Player p = i.next();
                list.add(p);
            }
        }
        Collections.sort(list);
        // Create the list model
        DefaultListModel listModel = new DefaultListModel();
        for (Player p : list) {
            listModel.addElement(p);
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
                        new Player(p.id, p.name, p.points, p.skill, "inlobby", p.language);
                        if (p.name.equalsIgnoreCase(Core.getInstance().getPlayerName())) {
                            Core.getInstance().setPlayerName(p.name);
                            Core.getInstance().setPlayerPoints(p.points);
                            Core.getInstance().setPlayerSkill(p.skill);
                            Core.getInstance().setPlayerId(p.id);
                        }
                    } else {
                        logger.warning("Got player \"add\" but player is already listed: " + pon);
                    }
                    // if (p.operation.equals("join")) {
                    //     Map<String,String> args = new HashMap<String,String>();
                    //     args.put("name", p.name);
                    //    this.chatdialog.addChatText("Server", _("%name% has joined!", args), Core.getInstance());
                    // }
                } else if (p.operation.equals("remove") || p.operation.equals("leave")) {
                    Player pon = allPlayers.get(p.name.toLowerCase());
                    if (pon != null) {
                        pon.remove();
                    }
                    // if (p.operation.equals("leave")) {
                    //     Map<String,String> args = new HashMap<String,String>();
                    //     args.put("name", p.name);
                    //     this.chatdialog.addChatText(_("Server"), _("%name% has left...", args), Core.getInstance());
                    // }
                } else if (p.operation.equals("ingame")) {
                    Player pon = allPlayers.get(p.name.toLowerCase());
                    if (pon != null) {
                        pon.setInGame(true);
                    }
                    if (!this.waitingForRefresh) {
                        Map<String, String> args = new HashMap<String, String>();
                        args.put("name", "<b>"+p.name+"</b>");
                        this.chatdialog.addChatText(_("Server"), _("%name% joined a game...", args), false);
                    }
                } else if (p.operation.equals("outgame")) {
                    Player pon = allPlayers.get(p.name.toLowerCase());
                    if (pon != null) {
                        pon.points = p.points;
                        pon.skill = p.skill;
                        pon.setInGame(false);
                    }
                    if (!this.waitingForRefresh) {
                        Map<String, String> args = new HashMap<String, String>();
                        args.put("name", "<b>"+p.name+"</b>");
                        this.chatdialog.addChatText(_("Server"), _("%name% returned from playing...", args), false);
                    }
                } else {
                    logger.warning("Invalid players list operation: " + p.operation + " for " + p);
                }
            }
        }
        this.waitingForRefresh = false;
        this.setPlayerCountLabel();
        playersChanged = true;
    }

    /**
     * Set the Game Info Label, NULL = Start Screen
     *
     * @param txt Set the Label info
     *
     */
    public void setGameInfoEditorPaneHTML(String HTML) {
        String content = "";
        if (HTML != null && !HTML.equals("")) {
            content = "<html><body text=\"#00FF00\" style=\"font-family: Arial\" bgcolor=\"#000000\">"+ HTML + "</body></html>";
        }
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
        String minSkill = GameRow.getMinSkill() == 0 ? _("all") : GameRow.getMinSkill() + "";
        String maxSkill = GameRow.getMaxSkill() == 0 ? _("all") : GameRow.getMaxSkill() + "";
        String txt = "<div align=\"center\"><div style=\"background: gray; color: white; margin-bottom: 5\"><b>" + GameRow.getGameName() + "</b><br>(" + Constants.Map.getMapById(GameRow.getMapId()).toString() + ")<br> </div>" + "<table border='0' style='border-collapse: collapse' width='270' height='102' cellpadding='3'>" + "<tr>" + "<td width='122' valign='top' height='102'>" + "<center>" + "	<img src='" + imageURL + "' width='100' height='100'></center></td>" + "	<td valign='top' width='150' height='102'>" + "	" + _("Players") + ": " + _("%min% of %max%", minOfMaxArgs) + "<br>" + "	" + _("Mode") + ": " + _(mode) + "<br>" + "	" + _("State") + ": " + state + "<br>" + "	" + _("Min/Max") + ": " + minSkill + "-" + maxSkill + "<br>" + "	" + _("Password") + ": " + _(GameRow.getPassword()) + "<br>" + "  </td>" + "</tr>" + "</table>" + "</div><div style=\"margin-top: 5\">" + "<b>" + _("Players") + ":</b> " + GameRow.getPlayer1() + " " + GameRow.getPlayer2() + " " + GameRow.getPlayer3() + " " + GameRow.getPlayer4() + "<br>" + "</div></font>";

        this.setGameInfoEditorPaneHTML(txt);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() {
    	Core.getInstance().getNetwork().removeListener(this);
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
        allPlayers.clear();
        Core.getInstance().getNetwork().addListener(this);
        this.repaint();
        this.setGameInfoEditorPaneHTML(null);
        // Using Swing timers to update component contents
        this.swingUpdateTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePlayersList();
                if (gameInfoEditorPaneContent != null) {
                    if (gameInfoEditorPaneContent.equals("")) {
                        gameInfoEditorPane.setVisible(false);
                    } else {
                        gameInfoEditorPane.setText(gameInfoEditorPaneContent);
                        gameInfoEditorPane.setVisible(true);
                    }
                    gameInfoEditorPaneContent = null;
                }
            }
        });
        if (!this.waitingForRefresh) {
            this.waitingForRefresh = true;
            RefreshMessage rm = new RefreshMessage();
            Core.getInstance().getNetwork().sendMessage(rm);
        }
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
            this.processPlayersMessage((PlayersMessage) m);
        } else if (m instanceof ServerChatMessage) {
            ServerChatMessage scm = (ServerChatMessage) m;
            String msg = scm.getMessage();
            if (scm.getTranslate()) {
                msg = _(msg);
            }
            this.chatdialog.addChatText(scm.getPlayerName(), msg, scm.isAction());
            Core.getInstance().getSoundManagement().clapSound();
        } else if (m instanceof JoinGameResponseMessage) {
            JoinGameResponseMessage jgrm = (JoinGameResponseMessage) m;
            if (jgrm.getResponseType().equals(Constants.ResponseType.ok)) {
                if (this.joinGame == null) {
                    logger.severe("The game to join was null!");
                    errorDialog(_("You cannot join to this game."));
                } else {
                	Core.getInstance().setActiveGame(this.joinGame);
                    WaitingGameScreen wgp = new WaitingGameScreen(this.joinGame);
                    Core.getInstance().pushScreen(wgp);
                }
            } else if (jgrm.getResponseType().equals(Constants.ResponseType.multi)) {
                errorDialog(_("You already joined to this game."));
            } else {
                errorDialog(_("You cannot join to this game."));
            }
        }
    }

    /**
     * Rewrite online players number label
     */
    public synchronized void setPlayerCountLabel() {
        Map<String, String> args = new HashMap<String, String>();
        args.put("online", allPlayers.size()+"");
        args.put("playing", ""+this.playersInGame);
        onlinePlayersLabel.setText(_("%online% players online, %playing% in game", args));
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
}

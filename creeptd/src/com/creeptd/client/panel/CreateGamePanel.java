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
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.creeptd.client.network.MessageListener;
import com.creeptd.client.util.Fonts;
import com.creeptd.common.Constants;
import com.creeptd.common.messages.client.CreateGameMessage;
import com.creeptd.common.messages.server.CreateGameResponseMessage;
import com.creeptd.common.messages.server.GameDescription;
import com.creeptd.common.messages.server.ServerMessage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBox;

import static com.creeptd.client.i18n.Translator.*;

/**
 * The create game panel.
 * 
 * @author Ron
 */
public class CreateGamePanel extends GameScreen implements MessageListener {
    private static final long serialVersionUID = 1L;
    private JLabel title;
    private JLabel name;
    private JLabel player;
    private JLabel map;
    private JLabel Passwort;
    private JLabel MaxEloPoints;
    private JLabel lGamemode;
    private JTextField tName;
    private JTextField tPasswort;
    private JTextField tMaxEloPoints;
    private JTextField tMinEloPoints;
    private JComboBox tPlayer;
    private JComboBox tGamemode;
    private JButton tMap;
    private JButton create;
    private JButton quit;
    private JLabel previewDescription;
    private ImageIcon preview;
    private JLabel previewLabel;
    private JCheckBox shufflePlayers;
    private JFrame OpenCreateGameDialog = null;
    private int selectMap = 0;

    /**
     * Creates a new instance of CreateGamePanel.
     */
    public CreateGamePanel() {
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        
        title = Fonts.getFrameTitle(_("Create game"), 70);

        name = new JLabel(_("Game name")+": ");
        name.setBounds(200, 200, 200, 30);
        name.setForeground(Color.GRAY);
        name.setFont(new Font("Arial", Font.PLAIN, 12));

        tName = new JTextField();
        tName.setText(_("Game of"));
        tName.setBounds(300, 200, 200, 25);
        tName.setFont(new Font("Arial", Font.PLAIN, 12));
        this.setGameScreenFocus(tName);

        player = new JLabel(_("Players")+": ");
        player.setBounds(200, 250, 200, 25);
        player.setForeground(Color.GRAY);
        player.setFont(new Font("Arial", Font.PLAIN, 12));

        tPlayer = new JComboBox();
        tPlayer.setBackground(Color.BLACK);
        tPlayer.setForeground(Color.GREEN);
        tPlayer.setBounds(300, 250, 200, 25);
        tPlayer.setFont(new Font("Arial", Font.PLAIN, 12));

        lGamemode = new JLabel(_("Mode")+": ");
        lGamemode.setBounds(200, 450, 200, 25);
        lGamemode.setForeground(Color.GRAY);
        lGamemode.setFont(new Font("Arial", Font.PLAIN, 12));

        tGamemode = new JComboBox();
        tGamemode.setBackground(Color.BLACK);
        tGamemode.setForeground(Color.GREEN);
        tGamemode.setBounds(300, 450, 200, 25);
        tGamemode.setFont(new Font("Arial", Font.PLAIN, 12));

        map = new JLabel(_("Map")+": ");
        map.setBounds(200, 300, 200, 25);
        map.setForeground(Color.GRAY);
        map.setFont(new Font("Arial", Font.PLAIN, 12));

        tMap = new JButton(_("Select map"));
        tMap.setBackground(Color.BLACK);
        tMap.setForeground(Color.GREEN);
        tMap.setBounds(300, 300, 200, 25);

        Passwort = new JLabel(_("Password")+": ");
        Passwort.setBounds(200, 350, 200, 25);
        Passwort.setForeground(Color.GRAY);
        Passwort.setFont(new Font("Arial", Font.PLAIN, 12));

        tPasswort = new JPasswordField();
        tPasswort.setBounds(300, 350, 200, 25);
        tPasswort.setFont(new Font("Arial", Font.PLAIN, 12));

        MaxEloPoints = new JLabel(_("Min-Max skill")+": ");
        MaxEloPoints.setBounds(200, 400, 200, 25);
        MaxEloPoints.setForeground(Color.GRAY);
        MaxEloPoints.setFont(new Font("Arial", Font.PLAIN, 12));

        tMinEloPoints = new JTextField();
        tMinEloPoints.setBounds(300, 400, 80, 25);
        tMinEloPoints.setFont(new Font("Arial", Font.PLAIN, 12));

        tMaxEloPoints = new JTextField();
        tMaxEloPoints.setBounds(420, 400, 80, 25);
        tMaxEloPoints.setFont(new Font("Arial", Font.PLAIN, 12));

        create = new JButton(_("Create game"));
        create.setBounds(225, 600, 200, 25);
        create.setBackground(Color.BLACK);
        create.setForeground(Color.GREEN);

        quit = new JButton(_("Cancel"));
        quit.setBounds(475, 600, 200, 25);
        quit.setBackground(Color.BLACK);
        quit.setForeground(Color.GREEN);

        previewDescription = new JLabel("preview");
        previewDescription.setBounds(600, 200, 250, 25);
        previewDescription.setHorizontalAlignment(SwingConstants.CENTER);
        previewDescription.setText(_("Random map"));
        previewDescription.setBackground(Color.BLACK);
        previewDescription.setForeground(Color.GREEN);

        Map<String,String> args = new HashMap<String,String>();
        args.put("n", "2"); tPlayer.addItem(_("%n% players", args));
        args.put("n", "3"); tPlayer.addItem(_("%n% players", args));
        args.put("n", "4"); tPlayer.addItem(_("%n% players", args));
        //don't change the index
        tGamemode.addItem(_(Constants.Mode.ALLVSALL.toString()));
        tGamemode.addItem(_(Constants.Mode.SENDNEXT.toString()));
        tGamemode.addItem(_(Constants.Mode.SENDRANDOM.toString()));
        tGamemode.addItem(_(Constants.Mode.TEAM2VS2.toString()));
        tGamemode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox tGamemode = (JComboBox) e.getSource();
                if (tGamemode.getSelectedIndex() == 3) { // Team 2vs2
                    shufflePlayers.setVisible(false);
                } else {
                    shufflePlayers.setVisible(true);
                }
            }
        });
        // tGamemode.addItem("Last man standing");

        try {
            preview = new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("com/creeptd/client/resources/maps/random.jpg")));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        preview.setImage(preview.getImage().getScaledInstance(250, 250,
                Image.SCALE_SMOOTH));
        previewLabel = new JLabel(preview);
        previewLabel.setBounds(600, 240, 250, 250);

        shufflePlayers = new JCheckBox(_("Shuffle player positions"));
        shufflePlayers.setSelected(true);
        shufflePlayers.setBounds(300, 490, 200, 25);
        shufflePlayers.setForeground(Color.green);
        shufflePlayers.setBackground(Color.BLACK);
        shufflePlayers.setFont(new Font("Arial", Font.PLAIN, 12));

        this.selectMap = 0;

        this.add(MaxEloPoints);
        this.add(tName);
        this.add(Passwort);
        this.add(tPasswort);
        this.add(tMaxEloPoints);
        this.add(tMinEloPoints);
        this.add(title);
        this.add(name);
        this.add(tName);
        this.add(player);
        this.add(tPlayer);
        this.add(map);
        this.add(tMap);
        this.add(create);
        this.add(quit);
        this.add(previewDescription);
        this.add(previewLabel);
        this.add(lGamemode);
        this.add(tGamemode);
        this.add(shufflePlayers);

        ActionListener a3 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                openCreateGameDialog();
            }
        };

        this.tMap.addActionListener(a3);

        ActionListener a1 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                getCore().popScreen();
                if (OpenCreateGameDialog != null && OpenCreateGameDialog.isVisible()) {
                    OpenCreateGameDialog.dispose();
                }
            }
        };
        quit.addActionListener(a1);

        quit.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                getCore().popScreen();
                if (OpenCreateGameDialog.isVisible()) {
                    OpenCreateGameDialog.dispose();
                }
            }
        });

        ActionListener a2 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                StartButton();
            }
        };
        create.addActionListener(a2);

        KeyAdapter createKeyAdapter = new KeyAdapter() {

            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }

                StartButton();
            }
        };

        create.addKeyListener(createKeyAdapter);
        tName.addKeyListener(createKeyAdapter);
        tPlayer.addKeyListener(createKeyAdapter);
        tMap.addKeyListener(createKeyAdapter);

    }

    private void openCreateGameDialog() {

        if (OpenCreateGameDialog == null) {
            OpenCreateGameDialog = new CreateGameListPanel(this, "CreepTD - "+_("Select map"));
        }

        OpenCreateGameDialog.setVisible(true);
        OpenCreateGameDialog.requestFocus();
    }

    private void StartButton() {

        boolean StartGame = false;

        if (tName.getText().length() != 0) {
            StartGame = true;
        } else {
            errorDialog(_("Please enter a name for the game!"));
            StartGame = false;
        }
        if (tName.getText().length() > 12) {
            errorDialog(_("Maximum length of game name is 12 characters."));
            StartGame = false;
        }

        if (StartGame == true && (!tMaxEloPoints.getText().equals("")) || !tMinEloPoints.getText().equals("")) {
            int minEloPoints = 0;
            try {
                minEloPoints = Integer.parseInt(tMinEloPoints.getText());
            } catch (Exception ex) {
            }
            int maxEloPoints = 0;
            try {
                maxEloPoints = Integer.parseInt(tMaxEloPoints.getText());
            } catch (Exception ex) {
            }

            if (maxEloPoints < minEloPoints) {
                errorDialog(_("Maximum skill must be larger than minimum skill!"));
                StartGame = false;

            } else {
                StartGame = true;
            }
        }
        if (tGamemode.getSelectedIndex() == 1 || tGamemode.getSelectedIndex() == 2) {
            if (tPlayer.getSelectedIndex() == 0) {
                errorDialog(_("This game mode requires 3 or 4 players!"));
                StartGame = false;
            }
        }
        if (tGamemode.getSelectedIndex() == 3) {
            if (tPlayer.getSelectedIndex() != 2) {
                errorDialog(_("This game mode requires 4 players!"));
                StartGame = false;
            }
        }
        if (StartGame == true) {
            if (OpenCreateGameDialog != null) {
                if (OpenCreateGameDialog.isVisible()) {
                    OpenCreateGameDialog.dispose();
                }
            }
            getCore().setGamecreator(true);
            sendCreateGameMessage();

        }

    }

    /**
     * Sends the Message to create a GameMessage.
     */
    private void sendCreateGameMessage() {

        CreateGameMessage gM = new CreateGameMessage();
        gM.setGameName(tName.getText() + " (" + this.getCore().getPlayerName() + ")");
        gM.setMapId(this.selectMap);
        gM.setMaxPlayers(tPlayer.getSelectedIndex() + 2);
        try {
            gM.setMaxEloPoints(Integer.parseInt(tMaxEloPoints.getText()));
        } catch (NumberFormatException ex) {
            gM.setMaxEloPoints(0);
        }
        try {
            gM.setMinEloPoints(Integer.parseInt(tMinEloPoints.getText()));
        } catch (NumberFormatException ex) {
            gM.setMinEloPoints(0);
        }
        gM.setPasswort(tPasswort.getText());
        int selectedMode = tGamemode.getSelectedIndex();
        Constants.Mode mode = Constants.Mode.ALLVSALL;
        if (selectedMode == 1) mode = Constants.Mode.SENDNEXT;
        if (selectedMode == 2) mode = Constants.Mode.SENDRANDOM;
        if (selectedMode == 3) mode = Constants.Mode.TEAM2VS2;
        gM.setGameMode(mode);
        if (selectedMode == 3) {
            gM.setShufflePlayers(false);
        } else {
            gM.setShufflePlayers(shufflePlayers.isSelected());
        }

        this.getCore().getNetwork().sendMessage(gM);
    }

    /**
     *
     * @param g
     *            Creates a new Game
     */
    private void createGame(CreateGameResponseMessage g) {
        if (g.getResponseType().equals(Constants.ResponseType.failed)) {
            errorDialog(_("A game of this name already exists."));

        } else if (g.getResponseType().equals(Constants.ResponseType.ok)) {
            GameDescription gd = new GameDescription();
            gd.setNumberOfPlayers(tPlayer.getSelectedIndex() + 2);
            gd.setMapId(this.selectMap);
            gd.setGameName(tName.getText());
            try {
                gd.setMaxEloPoints(Integer.valueOf(tMaxEloPoints.getText()));
            } catch (NumberFormatException ex) {
                gd.setMaxEloPoints(0);
            }
            try {
                gd.setMinEloPoints(Integer.valueOf(tMinEloPoints.getText()));
            } catch (NumberFormatException ex) {
                gd.setMinEloPoints(0);
            }
            int selectedMode = tGamemode.getSelectedIndex();
            Constants.Mode mode = Constants.Mode.ALLVSALL;
            if (selectedMode == 1) mode = Constants.Mode.SENDNEXT;
            if (selectedMode == 2) mode = Constants.Mode.SENDRANDOM;
            if (selectedMode == 3) mode = Constants.Mode.TEAM2VS2;
            gd.setGameMode(mode);
            gd.setPassword(tPasswort.getText());
            gd.setShufflePlayers(shufflePlayers.isSelected());

            getCore().setActiveGame(gd);

            WaitingGamePanel wgp = new WaitingGamePanel(gd.getNumberOfPlayers());
            wgp.setMapId(this.selectMap);
            wgp.setGameMode(mode);
            getCore().switchScreen(wgp);
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
        JOptionPane.showMessageDialog(this, msg, _("An error occured"),
                JOptionPane.ERROR_MESSAGE);
    }

    public void selectMap(int id) {

        this.selectMap = id;

        try {
            preview = new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(
                    Constants.Map.getPicturePath(Constants.Map.getMapById(id).toString()))));
            String map = Constants.Map.getMapById(id).toString();
            if (map.equals("Random_Map")) {
                map = _("Random map");
            }
            previewDescription.setText(map);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        preview.setImage(preview.getImage().getScaledInstance(250, 250,
                Image.SCALE_SMOOTH));
        previewLabel.setIcon(preview);

        OpenCreateGameDialog.setVisible(false);

    }

    /**
     * displays the screen.
     */
    @Override
    public void start() {
        this.getCore().getNetwork().makeContact();
        this.getCore().getNetwork().addListener(this);
        int playerScore = 0;
        playerScore = this.getCore().getPlayerElopoints();
        this.repaint();



    }

    /**
     * method for disappearing the screen.
     */
    @Override
    public void end() {
        this.getCore().getNetwork().removeListener(this);
        // back to LobbyPanel
    }

    /**
     * if mouse released, do run.
     *
     * @param evt
     *            for MouseEvent
     */
    @Override
    public void mouseReleased(MouseEvent evt) {
        // TODO Auto-generated method stub
    }

    /**
     * method for update.
     *
     * @param m
     *            for ServerMessage
     */
    public void update(ServerMessage m) {
        if (m instanceof CreateGameResponseMessage) {
            createGame((CreateGameResponseMessage) m);
        }
    }
}

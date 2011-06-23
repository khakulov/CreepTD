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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.creeptd.client.network.MessageListener;
import com.creeptd.client.util.Fonts;
import com.creeptd.common.messages.client.ScoreRequestMessage;
import com.creeptd.common.messages.server.HighscoreEntry;
import com.creeptd.common.messages.server.HighscoreResponseMessage;
import com.creeptd.common.messages.server.ScoreResponseMessage;
import com.creeptd.common.messages.server.ServerMessage;

import static com.creeptd.client.i18n.Translator.*;

/**
 * Panel for the highscore.
 * @author philipp
 *
 */
public class GameResultPanel extends GameScreen implements MessageListener {

    private static final long serialVersionUID = 4925493108175118730L;
    private final JLabel title;
    private JTable highscoreTable;
    private JScrollPane highscoreScrollPane;
    private final JButton back;
    private TreeMap<Integer, String> players = new TreeMap<Integer, String>();
    private Vector<Vector<String>> tableData = new Vector<Vector<String>>();

    /**
     * Creates a new HighscorePanel.
     */
    public GameResultPanel(TreeMap<Integer, String> players) {
        this.players = players;
        this.setLayout(null);
        this.setBackground(Color.BLACK);

        title = Fonts.getFrameTitle(_("Game results"), 10);

        this.back = new JButton(_("OK"));
        this.back.setBounds(385, 640, 120, 25);
        this.back.setBackground(Color.BLACK);
        this.back.setForeground(Color.GREEN);

        this.initHighscoreTable();

        this.add(this.title);
        this.add(this.highscoreScrollPane);
        this.add(this.back);

        ActionListener a2 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                GameResultPanel.this.getCore().popScreen();
            }
        };
        this.back.addActionListener(a2);

        this.processHighscoreMessage(null);
    }

    /**
     * Initializes the highscore tabel.
     */
    private void initHighscoreTable() {
        if (this.highscoreScrollPane == null) {
            this.highscoreScrollPane = new JScrollPane();

            this.highscoreScrollPane.setBounds(240, 120, 410, 500);
        }

        if (this.highscoreTable == null) {
            this.highscoreTable = new JTable();
            this.highscoreTable.setDoubleBuffered(true);
            this.highscoreTable.setBackground(Color.BLACK);
            this.highscoreTable.setForeground(Color.GREEN);

            this.highscoreTable.setIntercellSpacing(new Dimension(0, 0));
            this.highscoreTable.setShowVerticalLines(false);
            this.highscoreTable.setShowHorizontalLines(false);
        }

        this.highscoreScrollPane.setViewportView(this.highscoreTable);
    }

    /**
     * {@inheritDoc}
     */
    public void update(ServerMessage m) {
        if (m instanceof ScoreResponseMessage) {
            this.processScoreMessage((ScoreResponseMessage) m);
        }
    }

    /**
     * process the score response message.
     * @param srm the message
     */
    private void processScoreMessage(ScoreResponseMessage srm) {
        Vector<String> row = new Vector<String>();
        row.add(srm.getPlayerName());
        String exp = "";
        if (srm.getLastgameExperience() >= 0) {
            exp += "+" + srm.getLastgameExperience();
        } else {
            exp += srm.getLastgameExperience();
        }
        exp += " (" + srm.getExperience() + ")";
        row.add(exp);
        String elo = "";
        if (srm.getLastgameElopoints() >= 0) {
            elo += "+" + srm.getLastgameElopoints();
        } else {
            elo += srm.getLastgameElopoints();
        }
        elo += " (" + srm.getElopoints() + ")";
        row.add(elo);
        tableData.add(row);

        updateTable();
    }

    /**
     * update the table.
     *
     */
    private void updateTable() {
        final Object[] headerNames = new Object[]{_("Username"), _("Points (Total)"), _("Skill (Total)")};

        final DefaultTableModel model = new DefaultTableModel() {
            private static final long serialVersionUID = 6115678865194002026L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Collections.sort(tableData, new Comparator<Vector<String>>() {
            public int compare(Vector<String> a1, Vector<String> a2) {
                String val2 = a2.get(2).substring(0, a2.get(2).indexOf(' '));
                String val1 = a1.get(2).substring(0, a1.get(2).indexOf(' '));
                if (val2.charAt(0) == '+') val2 = val2.substring(1); // 'cause valueOf sucks
                if (val1.charAt(0) == '+') val1 = val1.substring(1);
                return (Integer.valueOf(val2).compareTo(Integer.valueOf(val1)));
            }
        });

        model.setDataVector(tableData, new Vector<Object>(Arrays.asList(headerNames)));
        this.highscoreTable.setModel(model);
    }

    /**
     * take actions needed for highscore.
     * @param hrm the highscoreResponseMessage
     */
    private void processHighscoreMessage(HighscoreResponseMessage hrm) {
        final Object[] headerNames = new Object[]{_("Rank"), _("Username"), _("Points (Last)"), _("Skill (Last)")};
        final Vector<Vector<String>> rows = new Vector<Vector<String>>();
        final DefaultTableModel model = new DefaultTableModel() {
            private static final long serialVersionUID = 6115678865194002026L;
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (hrm != null) {
            ArrayList<HighscoreEntry> listA = new ArrayList<HighscoreEntry>(hrm.getHighscoreEntries());
            ArrayList<HighscoreEntry> list = new ArrayList<HighscoreEntry>();

            for (HighscoreEntry s : listA) {
                if (players.containsValue(s.getPlayerName())) {
                    list.add(s);
                }
            }
            Collections.sort(list, new Comparator<HighscoreEntry>() {
                public int compare(HighscoreEntry a, HighscoreEntry b) {
                    Integer aPoints = a.getLastgameExperience();
                    Integer bPoints = b.getLastgameExperience();
                    if (aPoints.compareTo(bPoints) != 0) {
                        return aPoints.compareTo(bPoints);
                    } else {
                        return a.getPlayerName().compareTo(b.getPlayerName());
                    }
                }
            });

            int nr = 1;
            for (HighscoreEntry he : list) {
                final Vector<String> rowsData = new Vector<String>();

                rowsData.add(String.valueOf(nr));
                rowsData.add(he.getPlayerName().toString());

                if (he.getLastgameExperience() >= 0) {
                    rowsData.add("+" + he.getExperience().toString());
                } else {
                    rowsData.add(he.getExperience().toString());
                }

                if (he.getLastgameElopoints() >= 0) {
                    rowsData.add("+" + he.getElopoints().toString());
                } else {
                    rowsData.add(he.getElopoints().toString());
                }

                rows.add(rowsData);
                nr++;
            }
        }
        model.setDataVector(rows, new Vector<Object>(Arrays.asList(headerNames)));
        this.highscoreTable.setModel(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() {
        // TODO Auto-generated method stub
        this.getCore().getNetwork().removeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        this.getCore().getNetwork().addListener(this);

        // Request player scores
        for (Integer i : this.players.keySet()) {
            ScoreRequestMessage srm = new ScoreRequestMessage(this.players.get(i));
            this.getCore().getNetwork().sendMessage(srm);
        }
    }
}

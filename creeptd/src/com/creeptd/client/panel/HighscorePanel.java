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
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.creeptd.client.network.MessageListener;
import com.creeptd.client.util.Fonts;
import com.creeptd.common.messages.client.HighscoreRequestMessage;
import com.creeptd.common.messages.server.HighscoreEntry;
import com.creeptd.common.messages.server.HighscoreResponseMessage;
import com.creeptd.common.messages.server.ServerMessage;
import javax.swing.border.EmptyBorder;

import static com.creeptd.client.i18n.Translator.*;

/**
 * Panel for the highscore.
 * @author philipp
 *
 */
public class HighscorePanel extends GameScreen implements MessageListener {

    private static final long serialVersionUID = 4925493108175118730L;
    private final JLabel title;
    private JTable highscoreTable;
    private JScrollPane highscoreScrollPane;
    private final JButton back;
    private final JButton refresh;
    private final JButton plus;
    private final JButton minus;
    // the actual highscore position
    private int position = 0;
    private static int STEP = 30;

    /**
     * Creates a new HighscorePanel.
     */
    public HighscorePanel() {

        this.setLayout(null);
        this.setBackground(Color.BLACK);
        
        this.title = Fonts.getFrameTitle(_("Highscores"), 10);

        this.plus = new JButton(">");
        this.plus.setBounds(590, 640, 60, 25);
        this.plus.setBackground(Color.BLACK);
        this.plus.setForeground(Color.GREEN);

        this.minus = new JButton("<");
        this.minus.setBounds(240, 640, 60, 25);
        this.minus.setBackground(Color.BLACK);
        this.minus.setForeground(Color.GREEN);
        this.minus.setEnabled(false);

        this.back = new JButton(_("Back"));
        this.back.setBounds(320, 640, 120, 25);
        this.back.setBackground(Color.BLACK);
        this.back.setForeground(Color.GREEN);

        this.refresh = new JButton(_("Refresh"));
        this.refresh.setBounds(450, 640, 120, 25);
        this.refresh.setBackground(Color.BLACK);
        this.refresh.setForeground(Color.GREEN);

        this.initHighscoreTable();


        this.add(this.title);
        this.add(this.highscoreScrollPane);
        this.add(this.back);
        this.add(this.refresh);
        this.add(this.plus);
        this.add(this.minus);

        ActionListener a1 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                HighscorePanel.this.getCore().getNetwork().sendMessage(
                        new HighscoreRequestMessage(position));
            }
        };
        this.refresh.addActionListener(a1);

        ActionListener a2 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                HighscorePanel.this.getCore().popScreen();
            }
        };
        this.back.addActionListener(a2);

        ActionListener a3 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                position += STEP;
                sendRequest();
            }
        };
        this.plus.addActionListener(a3);

        ActionListener a4 = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (position > 0) {
                    position -= STEP;
                }
                sendRequest();
            }
        };
        this.minus.addActionListener(a4);

        this.processHighscoreMessage(null);
    }

    /**
     * sends Request.
     */
    private void sendRequest() {
        if (position == 0) {
            minus.setEnabled(false);
        } else {
            minus.setEnabled(true);
        }
        HighscorePanel.this.getCore().getNetwork().sendMessage(
                new HighscoreRequestMessage(position));
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
            this.highscoreTable.setBorder(new EmptyBorder(0, 0, 0, 0));
            this.highscoreScrollPane.setBorder(new EmptyBorder(1, 1, 1, 1));

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
        if (m instanceof HighscoreResponseMessage) {
            this.processHighscoreMessage((HighscoreResponseMessage) m);
        }
    }

    /**
     * take actions needed for highscore.
     * @param hrm the highscoreResponseMessage
     */
    private void processHighscoreMessage(HighscoreResponseMessage hrm) {
        final Object[] headerNames = new Object[]{
            _("Rank"), _("Username"), _("Points (Last)"), _("Skill (Last)")};

        final Vector<Vector<String>> rows = new Vector<Vector<String>>();

        final DefaultTableModel model = new DefaultTableModel() {

            private static final long serialVersionUID = 6115678865194002026L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (hrm != null) {

            ArrayList<HighscoreEntry> list =
                    new ArrayList<HighscoreEntry>(hrm.getHighscoreEntries());

            Collections.sort(list, new Comparator<HighscoreEntry>() {

                public int compare(HighscoreEntry a, HighscoreEntry b) {
                    if (b.getExperience().compareTo(a.getExperience()) != 0) {
                        return b.getExperience().compareTo(a.getExperience());
                    } else {
                        return a.getPlayerName().compareTo(b.getPlayerName());
                    }
                }
            });

            int nr = 1;
            for (HighscoreEntry he : list) {
                final Vector<String> rowsData = new Vector<String>();

                rowsData.add(String.valueOf(position + nr));
                rowsData.add(he.getPlayerName().toString());

                String exp = he.getExperience().toString() + " (";
                if (he.getLastgameExperience() >= 0) {
                    exp += "+" + he.getLastgameExperience();
                } else {
                    exp += he.getLastgameExperience();
                }
                exp += ")";
                rowsData.add(exp);

                String elo = he.getElopoints().toString() + " (";
                if (he.getLastgameElopoints() >= 0) {
                    elo += "+" + he.getLastgameElopoints();
                } else {
                    elo += he.getLastgameElopoints();
                }
                elo += ")";
                rowsData.add(elo);
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
        this.getCore().getNetwork().sendMessage(new HighscoreRequestMessage(position));
    }

    /**
     * Main method for testing.
     * @param args the cmd args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        final HighscorePanel gl = new HighscorePanel();

        JFrame f = new JFrame();
        f.setSize(933, 700);
        f.setLocation(100, 100);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(gl);

        f.setVisible(true);

        Thread t = new Thread(new Runnable() {

            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

//					HighscoreResponseMessage hrm = 
//						new HighscoreResponseMessage();
//					hrm.setPlayerHighsoreMap(new HashMap<String, Integer>());
//					
//					hrm.getPlayerHighsoreMap().put("Bernd", (int) (Math.random() * 100));
//					hrm.getPlayerHighsoreMap().put("Andi", (int) (Math.random() * 100));
//					hrm.getPlayerHighsoreMap().put("Levin", (int) (Math.random() * 100));
//					hrm.getPlayerHighsoreMap().put("Sven", (int) (Math.random() * 100));
//					hrm.getPlayerHighsoreMap().put("Philipp", (int) (Math.random() * 100));
//					hrm.getPlayerHighsoreMap().put("Fabi", (int) (Math.random() * 100));
//					
//					gl.update(hrm);
//					
//					System.out.println(hrm.getMessageString());
                }
            }
        });

        t.start();
    }
}

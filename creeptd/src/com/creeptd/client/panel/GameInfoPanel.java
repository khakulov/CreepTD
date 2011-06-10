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
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.creeptd.client.game.ContextListener;
import com.creeptd.client.game.GameContext;
import com.creeptd.client.game.OpponentContext;
import com.creeptd.client.game.PlayerContext;

/**
 * Class painting the game information about the player and his opponents.
 * @author Philipp
 *
 */
public class GameInfoPanel extends JPanel implements ContextListener {

    private static final long serialVersionUID = -4446563975717092495L;
    private static final NumberFormat decimalFormat = NumberFormat.getInstance();
    private PlayerContext playerContext;
    private OpponentContext opponent1Context;
    private OpponentContext opponent2Context;
    private OpponentContext opponent3Context;
    private JLabel credits;
    private JLabel income;
    private JLabel playerLives;
    private JLabel incomeCounter;
    private JLabel opponent1Lives;
    private JLabel opponent2Lives;
    private JLabel opponent3Lives;

    /**
     * Creates a new GameInfoPanel.
     */
    public GameInfoPanel() {

        this.setLayout(null);
        this.setBackground(Color.BLACK);

        incomeCounter = new JLabel("Next income in: ");
        incomeCounter.setForeground(Color.WHITE);
        incomeCounter.setBounds(0, 0, 233, 15);

        credits = new JLabel("Credits: ");
        credits.setForeground(Color.WHITE);
        credits.setBounds(0, 15, 233, 15);

        income = new JLabel("Income: ");
        income.setForeground(Color.WHITE);
        income.setBounds(0, 30, 233, 15);

        playerLives = new JLabel("Lives: ");
        playerLives.setForeground(Color.WHITE);
        playerLives.setBounds(0, 45, 233, 15);

        opponent1Lives = new JLabel();
        opponent1Lives.setForeground(Color.WHITE);
        opponent1Lives.setBounds(0, 60, 233, 13);

        opponent2Lives = new JLabel();
        opponent2Lives.setForeground(Color.WHITE);
        opponent2Lives.setBounds(0, 73, 233, 13);

        opponent3Lives = new JLabel();
        opponent3Lives.setForeground(Color.WHITE);
        opponent3Lives.setBounds(0, 86, 233, 13);

        this.add(incomeCounter);
        this.add(credits);
        this.add(income);
        this.add(playerLives);
        this.add(opponent1Lives);
        this.add(opponent2Lives);
        this.add(opponent3Lives);

    }

    /**
     * Adds the players own context to the panel.
     * @param context the player context
     */
    public void addPlayerContext(PlayerContext context) {
        this.playerContext = context;
        context.addContextListener(this);
    }

    /**
     * Adds one opponent context to the player. This Method should called max
     * three times.
     * @param context the opponent context
     */
    public void addOpponentContext(OpponentContext context) {
        context.addContextListener(this);
        if (this.opponent1Context == null) {
            this.opponent1Context = context;
            return;
        }
        if (this.opponent2Context == null) {
            this.opponent2Context = context;
            return;
        }
        if (this.opponent3Context == null) {
            this.opponent3Context = context;
            return;
        }

    }

    /**
     * {@inheritDoc}
     */
    public void creditsChanged(GameContext context) {
        if (context.equals(playerContext)) {
            this.credits.setText("Credits: " + format(context.getCredits()));
            this.repaint();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void incomeChanged(GameContext context) {
        if (context.equals(playerContext)) {
            this.income.setText("Income: " + format(context.getIncome()));
            this.repaint();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void livesChanged(GameContext context) {

        if (context == playerContext) {
            this.playerLives.setText("Lives: " + context.getLives());
        } else if (context.equals(opponent1Context)) {
            this.opponent1Lives.setText(context.getPlayerName() + ": " + context.getLives());
        } else if (context.equals(opponent2Context)) {
            this.opponent2Lives.setText(context.getPlayerName() + ": " + context.getLives());
        } else if (context.equals(opponent3Context)) {
            this.opponent3Lives.setText(context.getPlayerName() + ": " + context.getLives());
        }
        this.repaint();
    }

    /**
     * changes the time to next income.
     * @param counter time to next income
     */
    public void setIncomeCounter(int counter) {
        this.incomeCounter.setText("New income in " + counter);

    }

    /**
     * selected changed.
     *
     */
    public void selectedChanged(GameContext context, String message) {
        // TODO Auto-generated method stub
    }

    private String format(int value) {
        return decimalFormat.format(value);
    }
}

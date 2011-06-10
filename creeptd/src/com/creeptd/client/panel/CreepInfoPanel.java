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

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.creeptd.client.game.GameContext;

/**
 * Panel that shows the attributes of the creep the user wants to send.
 * @author mi7hr4ndir
 *
 */
public class CreepInfoPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private Font headFont;
    private int width;
    private int height;
    private GamePanel gamePanel;
    private GameContext context;
    private JLabel nameInfo;
    private JLabel priceInfo;
    private JLabel incomeInfo;
    private JLabel healthInfo;
    private JLabel speedInfo;
    private JLabel specialInfo;

    /**
     * @param gameContext the gameContext to set
     */
    public void setContext(GameContext gameContext) {
        this.context = gameContext;
    }

    /**
     * creates new instance of CreepInfoPanle.
     * @param gamepanel gamepanel
     * @param width	width of the Panel
     * @param height height of the Panel
     */
    public CreepInfoPanel(GamePanel gamepanel, int width, int height) {
        this.width = width;
        this.height = height;
        this.gamePanel = gamepanel;
        init();

    }

    /**
     * initialize the gui elements.
     */
    private void init() {
        this.headFont = new Font("Helvetica", Font.BOLD, 15);
        nameInfo = new JLabel();
        priceInfo = new JLabel();
        incomeInfo = new JLabel();
        healthInfo = new JLabel();
        speedInfo = new JLabel();
        specialInfo = new JLabel();
        specialInfo.setFont(new Font("Helvetica", Font.BOLD, 10));

        nameInfo.setFont(headFont);
        nameInfo.setBounds(5, 5, 200, 20);
        priceInfo.setBounds(5, 35, 200, 15);
        incomeInfo.setBounds(5, 50, 200, 15);
        healthInfo.setBounds(5, 65, 200, 15);
        speedInfo.setBounds(5, 80, 200, 15);
        specialInfo.setBounds(5, 95, 200, 15);

        this.setBackground(Color.BLACK);
        nameInfo.setForeground(Color.GREEN);
        priceInfo.setForeground(Color.GREEN);
        incomeInfo.setForeground(Color.GREEN);
        healthInfo.setForeground(Color.GREEN);
        speedInfo.setForeground(Color.GREEN);
        specialInfo.setForeground(Color.YELLOW);

        this.setLayout(null);
        this.add(nameInfo);
        this.add(priceInfo);
        this.add(incomeInfo);
        this.add(healthInfo);
        this.add(speedInfo);
        this.add(specialInfo);

    }

    /**
     * @return the nameInfo
     */
    public JLabel getNameInfo() {
        return nameInfo;
    }

    /**
     * @param nameInfo the nameInfo to set
     */
    public void setNameInfo(JLabel nameInfo) {
        this.nameInfo = nameInfo;
    }

    /**
     * @return the priceInfo
     */
    public JLabel getPriceInfo() {
        return priceInfo;
    }

    /**
     * @param priceInfo the priceInfo to set
     */
    public void setPriceInfo(JLabel priceInfo) {
        this.priceInfo = priceInfo;
    }

    /**
     * @return the incomeInfo
     */
    public JLabel getIncomeInfo() {
        return incomeInfo;
    }

    /**
     * @param incomeInfo the incomeInfo to set
     */
    public void setIncomeInfo(JLabel incomeInfo) {
        this.incomeInfo = incomeInfo;
    }

    /**
     * @return the healthInfo
     */
    public JLabel getHealthInfo() {
        return healthInfo;
    }

    /**
     * @param healthInfo the healthInfo to set
     */
    public void setHealthInfo(JLabel healthInfo) {
        this.healthInfo = healthInfo;
    }

    /**
     * @return the speedInfo
     */
    public JLabel getSpeedInfo() {
        return speedInfo;
    }

    /**
     * @param speedInfo the speedInfo to set
     */
    public void setSpeedInfo(JLabel speedInfo) {
        this.speedInfo = speedInfo;
    }

    /**
     * @return the specialInfo
     */
    public JLabel getSpecialInfo() {
        return specialInfo;
    }

    /**
     * @param specialInfo the specialInfo to set
     */
    public void setSpecialInfo(JLabel specialInfo) {
        this.specialInfo = specialInfo;
    }
}

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import com.creeptd.client.game.ContextListener;
import com.creeptd.client.game.GameContext;
import com.creeptd.client.tower.AbstractTower;
import com.creeptd.client.tower.FindClosestCreep;
import com.creeptd.client.tower.FindCreepStrategy;
import com.creeptd.client.tower.FindFarthestCreep;
import com.creeptd.client.tower.FindFastestCreep;
import com.creeptd.client.tower.FindStrongestCreep;
import com.creeptd.client.tower.FindWeakestCreep;
import com.creeptd.common.Constants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

/**
 * Panel that shows the attributes of the tower, the user has selected.
 * 
 * @author mi7hr4ndir
 * 
 */
public class SelectTowerInfoPanel extends JPanel implements ContextListener {

    /**
     *
     */
    private static final long serialVersionUID = 6433143407545707929L;
    private int width;
    private int height;
    private GamePanel gamePanel;
    private JPanel info;
    private JPanel upgrade;
    private JPanel towerButtons;
    private JButton sellButton;
    private JButton upgradeButton;
    private JToggleButton weakStrategyButton;
    private JToggleButton hardStrategyButton;
    private JToggleButton closeStrategyButton;
    private JToggleButton fastestStrategyButton;
    private JToggleButton farthestStrategyButton;
    private JToggleButton lockCreepButton;
    private Font buttonFont;
    private Font headFont;
    private Font specialFont;
    private JLabel infoTower;
    private JLabel infoSellPrice;
    private JLabel infoDamage;
    private JLabel infoSpeed;
    private JLabel infoRange;
    private JLabel infoSpecial;
    private JLabel upgradeTower;
    private JLabel upgradePrice;
    private JLabel upgradeDamage;
    private JLabel upgradeSpeed;
    private JLabel upgradeRange;
    private JLabel upgradeSpecial;
    private JLabel strategyLabel;
    private GameContext context;

    /**
     * @param gameContext
     *            the gameContext to set
     */
    public void setContext(GameContext gameContext) {
        this.context = gameContext;
        context.addContextListener(this);
    }

    /**
     * creates new instance of SelectTowerInfoPanle.
     *
     * @param gamepanel
     *            gamepanel
     * @param width
     *            width of the Panel
     * @param height
     *            height of the Panel
     */
    public SelectTowerInfoPanel(GamePanel gamepanel, int width, int height) {
        this.width = width;
        this.height = height;
        this.gamePanel = gamepanel;
        this.setBackground(Color.BLACK);
        buttonFont = new Font("Helvetica", Font.PLAIN, 12);
        headFont = new Font("Helvetica", Font.BOLD, 15);
        specialFont = new Font("Helvetica", Font.BOLD, 10);
        UIManager.put("ToggleButton.select", Color.GRAY);
        init();

    }

    /**
     * initialize the gui elements.
     */
    private void init() {

        initInfoPanel();
        initUpgradePanel();
        initTowerPanel();

        this.setLayout(null);
        this.add(info);
        this.add(upgrade);
        this.add(towerButtons);

        upgradeButton.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent e) {
                if ((context.getSelectedTower() != null) && (context.getSelectedTower().getType().getNext() != null)) {
                    info.setVisible(false);
                    upgrade.setVisible(true);

                }

            }

            public void mouseExited(MouseEvent e) {
                upgrade.setVisible(false);
                info.setVisible(true);

            }
        });

        upgradeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                context.upgradeTower();
            }
        });

        sellButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                context.sellTower();

            }
        });

        weakStrategyButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AbstractTower t;
                t = (AbstractTower) context.selectTower();
                System.out.println("test: " + t.isReady());
                if (t.isReady()) {
                    if (weakStrategyButton.getModel().isSelected()) {
                        FindCreepStrategy fcs = new FindWeakestCreep(t);
                        fcs.setCreepLock(lockCreepButton.getModel().isSelected());
                        context.setStrategy(fcs);
                    } else {
                        weakStrategyButton.setSelected(true);
                    }
                } else {
                    weakStrategyButton.setSelected(false);
                }
            }
        });

        hardStrategyButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AbstractTower t;
                t = (AbstractTower) context.selectTower();
                if (t.isReady()) {
                    if (hardStrategyButton.getModel().isSelected()) {
                        FindCreepStrategy fcs = new FindStrongestCreep(t);
                        fcs.setCreepLock(lockCreepButton.getModel().isSelected());
                        context.setStrategy(fcs);
                    } else {
                        hardStrategyButton.setSelected(true);
                    }
                } else {
                    hardStrategyButton.setSelected(false);
                }
            }
        });

        closeStrategyButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AbstractTower t;
                t = (AbstractTower) context.selectTower();
                if (t.isReady()) {
                    if (closeStrategyButton.getModel().isSelected()) {
                        FindCreepStrategy fcs = new FindClosestCreep(t);
                        fcs.setCreepLock(lockCreepButton.getModel().isSelected());
                        context.setStrategy(fcs);
                    } else {
                        closeStrategyButton.setSelected(true);
                    }
                } else {
                    closeStrategyButton.setSelected(false);
                }
            }
        });

        fastestStrategyButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AbstractTower t;
                t = (AbstractTower) context.selectTower();
                if (t.isReady()) {
                    if (fastestStrategyButton.getModel().isSelected()) {
                        FindCreepStrategy fcs = new FindFastestCreep(t);
                        fcs.setCreepLock(lockCreepButton.getModel().isSelected());
                        context.setStrategy(fcs);
                    } else {
                        fastestStrategyButton.setSelected(true);
                    }
                } else {
                    fastestStrategyButton.setSelected(false);
                }
            }
        });

        farthestStrategyButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AbstractTower t;
                t = (AbstractTower) context.selectTower();
                if (t.isReady()) {
                    if (farthestStrategyButton.getModel().isSelected()) {
                        FindCreepStrategy fcs = new FindFarthestCreep(t);
                        fcs.setCreepLock(lockCreepButton.getModel().isSelected());
                        context.setStrategy(fcs);
                    } else {
                        farthestStrategyButton.setSelected(true);
                    }
                } else {
                    farthestStrategyButton.setSelected(false);
                }
            }
        });

        lockCreepButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AbstractTower t;
                t = (AbstractTower) context.getSelectedTower();
                if (t.isReady()) {
                    FindCreepStrategy fcs = null;
                    if (fastestStrategyButton.getModel().isSelected()) {
                        fcs = new FindFastestCreep(t);
                    }
                    if (farthestStrategyButton.getModel().isSelected()) {
                        fcs = new FindFarthestCreep(t);
                    }
                    if (closeStrategyButton.getModel().isSelected()) {
                        fcs = new FindClosestCreep(t);
                    }
                    if (hardStrategyButton.getModel().isSelected()) {
                        fcs = new FindStrongestCreep(t);
                    }
                    if (weakStrategyButton.getModel().isSelected()) {
                        fcs = new FindWeakestCreep(t);
                    }
                    fcs.setCreepLock(lockCreepButton.getModel().isSelected());
                    context.setStrategy(fcs);
                } else {
                    lockCreepButton.setSelected(false);
                }
            }
        });

    }

    private void initInfoPanel() {
        java.awt.GridBagConstraints gridBagConstraints;

        this.info = new JPanel();
        this.info.setBounds(0, 0, 130, 125);
        this.info.setVisible(false);
        this.info.setBackground(Color.BLACK);
        this.info.setLayout(new java.awt.GridBagLayout());

        // general Tower information

        this.infoTower = new JLabel("Tower Info");
        this.infoSellPrice = new JLabel("Sell for 75%");
        this.infoDamage = new JLabel("Damage Info");
        this.infoSpeed = new JLabel("Speed Info");
        this.infoRange = new JLabel("Range Info");
        this.infoSpecial = new JLabel("Specials Info");

        this.infoTower.setFont(headFont);
        this.infoTower.setBounds(5, 5, 150, 20);

        this.infoSpecial.setFont(specialFont);

        this.infoTower.setForeground(Color.GREEN);
        this.infoSellPrice.setForeground(Color.GREEN);
        this.infoDamage.setForeground(Color.GREEN);
        this.infoSpeed.setForeground(Color.GREEN);
        this.infoRange.setForeground(Color.GREEN);
        this.infoSpecial.setForeground(Color.GREEN);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        this.info.add(infoTower, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        this.info.add(infoSellPrice, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        this.info.add(infoDamage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        this.info.add(infoSpeed, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        this.info.add(infoRange, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        this.info.add(infoSpecial, gridBagConstraints);
    }

    private void initUpgradePanel() {
        java.awt.GridBagConstraints gridBagConstraints;

        this.upgrade = new JPanel();
        this.upgrade.setBounds(0, 0, 130, 125);
        this.upgrade.setVisible(true);
        this.upgrade.setBackground(Color.BLACK);
        this.upgrade.setLayout(new java.awt.GridBagLayout());

        // Tower upgrade informations
        this.upgradeTower = new JLabel("Upgrade tower to");
        this.upgradePrice = new JLabel("Price: ");
        this.upgradeDamage = new JLabel("Damage: ");
        this.upgradeSpeed = new JLabel("Speed: ");
        this.upgradeRange = new JLabel("Range: ");
        this.upgradeSpecial = new JLabel("Specials: ");

        this.upgradeTower.setFont(headFont);
        this.upgradeSpecial.setFont(specialFont);

        this.upgradeTower.setForeground(Color.GREEN);
        this.upgradePrice.setForeground(Color.GREEN);
        this.upgradeDamage.setForeground(Color.GREEN);
        this.upgradeSpeed.setForeground(Color.GREEN);
        this.upgradeRange.setForeground(Color.GREEN);
        this.upgradeSpecial.setForeground(Color.GREEN);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        this.upgrade.add(upgradeTower, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        this.upgrade.add(upgradePrice, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        this.upgrade.add(upgradeDamage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        this.upgrade.add(upgradeSpeed, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        this.upgrade.add(upgradeRange, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        this.upgrade.add(upgradeSpecial, gridBagConstraints);
    }

    private void initTowerPanel() {
        java.awt.GridBagConstraints gridBagConstraints;

        this.towerButtons = new JPanel();
        this.towerButtons.setBounds(130, 0, 102, 124);
        this.towerButtons.setVisible(true);
        this.towerButtons.setBackground(Color.BLACK);
        this.towerButtons.setLayout(new java.awt.GridBagLayout());

        this.sellButton = new JButton("Sell");
        this.upgradeButton = new JButton("Upgrade");
        this.strategyLabel = new JLabel("Strategy");
        this.weakStrategyButton = new JToggleButton("Weak");
        this.hardStrategyButton = new JToggleButton("Hard");
        this.closeStrategyButton = new JToggleButton("Close");
        this.fastestStrategyButton = new JToggleButton("Fastest");
        this.farthestStrategyButton = new JToggleButton("Farthest");
        this.lockCreepButton = new JToggleButton("Lock");
        this.lockCreepButton.setForeground(Color.RED);

        this.sellButton.setFont(buttonFont);
        this.sellButton.setBackground(Color.black);
        this.sellButton.setForeground(Color.GREEN);
        this.sellButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        this.upgradeButton.setFont(buttonFont);
        this.upgradeButton.setBackground(Color.black);
        this.upgradeButton.setForeground(Color.GREEN);
        this.upgradeButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        this.strategyLabel.setBackground(Color.black);
        this.strategyLabel.setForeground(Color.GREEN);
        this.strategyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        this.weakStrategyButton.setFont(buttonFont);
        this.weakStrategyButton.setBackground(Color.black);
        this.weakStrategyButton.setForeground(Color.GREEN);
        this.weakStrategyButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        this.hardStrategyButton.setFont(buttonFont);
        this.hardStrategyButton.setBackground(Color.black);
        this.hardStrategyButton.setForeground(Color.GREEN);
        this.hardStrategyButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        this.closeStrategyButton.setFont(buttonFont);
        this.closeStrategyButton.setBackground(Color.black);
        this.closeStrategyButton.setForeground(Color.GREEN);
        this.closeStrategyButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        this.fastestStrategyButton.setFont(buttonFont);
        this.fastestStrategyButton.setBackground(Color.black);
        this.fastestStrategyButton.setForeground(Color.GREEN);
        this.fastestStrategyButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        this.farthestStrategyButton.setFont(buttonFont);
        this.farthestStrategyButton.setBackground(Color.black);
        this.farthestStrategyButton.setForeground(Color.GREEN);
        this.farthestStrategyButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        this.lockCreepButton.setFont(buttonFont);
        this.lockCreepButton.setBackground(Color.black);
        this.lockCreepButton.setForeground(Color.GREEN);
        this.lockCreepButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        this.towerButtons.add(sellButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        this.towerButtons.add(upgradeButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 1.0;
        this.towerButtons.add(strategyLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        this.towerButtons.add(weakStrategyButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        this.towerButtons.add(hardStrategyButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        this.towerButtons.add(closeStrategyButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        this.towerButtons.add(fastestStrategyButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        this.towerButtons.add(lockCreepButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        this.towerButtons.add(farthestStrategyButton, gridBagConstraints);


    }

    /**
     * Not used Contextlistener Method.
     *
     * @param context
     *            context
     */
    public void creditsChanged(GameContext context) {
        updateButton();

    }

    /**
     * Not used Contextlistener Method.
     *
     * @param context
     *            context
     */
    public void incomeChanged(GameContext context) {
        // TODO Auto-generated method stub
    }

    /**
     * Not used Contextlistener Method.
     *
     * @param context
     *            context
     */
    public void livesChanged(GameContext context) {
        // TODO Auto-generated method stub
    }

    /**
     * changes the TowerInfoPanels.
     *
     * @param context
     *            context
     * @param message
     *            which Panel
     */
    public void selectedChanged(GameContext context, String message) {
        if (context.getSelectedTower() == null) {
            return;
        }
        // show SelectTowerInfoPanel
        if (message.equals("tower")) {
            // general info panel
            this.infoTower.setText(context.getSelectedTower().getType().getName());
            this.infoSellPrice.setText("Sell for: " + (int) (context.getSelectedTower().getTotalPrice() * 0.75));
            this.infoDamage.setText("Damage: " + context.getSelectedTower().getDamage());
            this.infoSpeed.setText("Speed: " + Constants.Towers.translateSpeed(context.getSelectedTower().getCoolDown()));
            this.infoRange.setText("Range: " + (int) context.getSelectedTower().getRange());
            this.infoSpecial.setText(context.getSelectedTower().getType().getSpecial());

            // upgrade info panel
            if (context.getSelectedTower().getType().getNext() != null) {
                upgradeTower.setText(context.getSelectedTower().getType().getNext().getName());
                upgradePrice.setText("Price: " + context.getSelectedTower().getType().getNext().getPrice());
                upgradeDamage.setText("Damage: " + context.getSelectedTower().getType().getNext().getDamage());
                upgradeSpeed.setText("Speed: " + Constants.Towers.translateSpeed(context.getSelectedTower().getType().getNext().getSpeed()));
                upgradeRange.setText("Range: " + (int) context.getSelectedTower().getType().getNext().getRange());
                upgradeSpecial.setText(context.getSelectedTower().getType().getNext().getSpecial());
            }

            if ((context.getSelectedTower().getType().getNext() != null) && (!context.getSelectedTower().isUpgrading()) && (context.getCredits() >= context.getSelectedTower().getType().getNext().getPrice())) {
                this.upgradeButton.setEnabled(true);
            } else {
                upgradeButton.setEnabled(false);
            }
            gamePanel.getBuildTowerInfoPanel().setVisible(false);
            gamePanel.getNoInfoPanel().setVisible(false);
            gamePanel.setLastTowerInfoPanel(this);
            this.setVisible(true);

        } else if (message.equals("sell")) {
            gamePanel.getLastTowerInfoPanel().setVisible(false);
            gamePanel.setLastTowerInfoPanel(gamePanel.getNoInfoPanel());
            gamePanel.getNoInfoPanel().setVisible(true);
        } else if (message.equalsIgnoreCase("upgrade")) {
            updateButton();
        } else if (message.equalsIgnoreCase("strategy")) {
            FindCreepStrategy fcs;
            fcs = ((AbstractTower) context.getSelectedTower()).getSelectedStrategy();
            weakStrategyButton.getModel().setSelected(
                    fcs instanceof FindWeakestCreep);
            hardStrategyButton.getModel().setSelected(
                    fcs instanceof FindStrongestCreep);
            closeStrategyButton.getModel().setSelected(
                    fcs instanceof FindClosestCreep);
            fastestStrategyButton.getModel().setSelected(
                    fcs instanceof FindFastestCreep);
            farthestStrategyButton.getModel().setSelected(
                    fcs instanceof FindFarthestCreep);
            lockCreepButton.getModel().setSelected(fcs.isCreepLock());

        }
    }

    /**
     * disables the upgrade button if there is not enough money.
     */
    private void updateButton() {
        if (context == null) {
            return;
        }
        if (context.getSelectedTower() == null) {
            return;
        }
        int credits = context.getCredits();
        if ((context.getSelectedTower().getType().getNext() != null) && (!context.getSelectedTower().isUpgrading()) && (credits >= context.getSelectedTower().getType().getNext().getPrice())) {
            this.upgradeButton.setEnabled(true);
        } else {
            this.upgradeButton.setEnabled(false);
        }

    }
}

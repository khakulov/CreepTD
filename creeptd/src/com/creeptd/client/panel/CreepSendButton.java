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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.creeptd.client.game.GameContext;
import com.creeptd.common.IConstants;

public class CreepSendButton extends JButton implements ActionListener, MouseListener {

    private static final long serialVersionUID = 1L;
    private GamePanel gamepanel;
    private IConstants.Creeps type;

    public CreepSendButton(GamePanel gamepanel, IConstants.Creeps type, String iconName) {
        super();
        this.gamepanel = gamepanel;
        this.type = type;

        setBackground(Color.GRAY);
        setBorderPainted(false);
        setIcon(createIcon(iconName));
        setFocusable(false);
        setEnabled(false);
        addActionListener(this);
        addMouseListener(this);
    }

    private Icon createIcon(String name) {
        // FIXME This should be a Constant (in IConstants?)
        String path = "com/creeptd/client/resources/creeps/";
        return new ImageIcon(getClass().getClassLoader().getResource(path + name + ".png"));
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {
        if (gamepanel.getCreepPanel().getContext() != null) {
            GameContext context = gamepanel.getCreepPanel().getContext();
            if (!context.isDead()) {
                if (((e.getModifiers() & ActionEvent.SHIFT_MASK) > 0) && context.readyForNewWave()) {
                    context.sendCreepsWave(type);
                } else if (context.readyForNewCreep() && context.readyForNewWave()) {
                    context.sendCreep(type);
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        gamepanel.getLastTowerInfoPanel().setVisible(false);
        CreepInfoPanel infoPanel = gamepanel.getCreepInfoPanel();
        infoPanel.getNameInfo().setText(type.getName());
        infoPanel.getPriceInfo().setText("Price: " + format(type.getPrice()));
        infoPanel.getIncomeInfo().setText("Income: +" + format(type.getIncome()) + " (" + format(type.getIncomePercentage()) + "%)");
        infoPanel.getHealthInfo().setText("Health: " + format(type.getHealth()));
        infoPanel.getSpeedInfo().setText("Speed: " + type.getSpeedString());
        infoPanel.getSpecialInfo().setText(type.getSpecial());
        infoPanel.setVisible(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        gamepanel.getCreepInfoPanel().setVisible(false);
        gamepanel.getLastTowerInfoPanel().setVisible(true);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    private String format(int value) {
        return NumberFormat.getInstance().format(value);
    }
}

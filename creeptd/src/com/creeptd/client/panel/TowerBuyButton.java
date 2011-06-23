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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.creeptd.client.game.GameContext;
import com.creeptd.common.Constants;

import java.net.URL;
import static com.creeptd.client.i18n.Translator.*;

public class TowerBuyButton extends JButton implements ActionListener,
        MouseListener {

    private static final long serialVersionUID = 1L;
    private GamePanel gamepanel;
    private Constants.Towers type;

    public TowerBuyButton(GamePanel gamepanel, Constants.Towers type,
            String iconName) {
        super();
        this.gamepanel = gamepanel;
        this.type = type;
        Font font = new Font("Helvetica", Font.PLAIN, 9);
        setFont(font);
        setBackground(Color.BLACK);
        setBorderPainted(false);
        setForeground(Color.GREEN);
        setIcon(createIcon(iconName));
        setDisabledIcon(createIcon(iconName + "disable"));
        setEnabled(false);
        addActionListener(this);
        addMouseListener(this);
    }

    private Icon createIcon(String name) {
        // FIXME This should be a Constant (in Constants?)
        String path = "com/creeptd/client/resources/pictures/";
        return new ImageIcon(getClass().getClassLoader().getResource(
                path + name + ".png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GameContext context = gamepanel.getContext();
        if (context.isDead()) {
            context.setSelectedTower(null);
            return;
        }
        context.setNextTower(type);
        context.getGameBoard().deSelectTowers();
        context.setSelectedTower(null);
        gamepanel.getSelectTowerInfoPanel().setVisible(false);
        gamepanel.setLastTowerInfoPanel(gamepanel.getBuildTowerInfoPanel());
        updateTowerInfo();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        gamepanel.getLastTowerInfoPanel().setVisible(false);
        updateTowerInfo();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        gamepanel.getTowerInfoPanel().setVisible(false);
        gamepanel.getLastTowerInfoPanel().setVisible(true);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    private void updateTowerInfo() {
        BuildTowerInfoPanel towerInfo = gamepanel.getTowerInfoPanel();
        towerInfo.getNameInfo().setText(type.getName());
        URL imageURL = this.getClass().getClassLoader().getResource("com/creeptd/client/resources/panel");
        towerInfo.getPriceInfo().setText("<html><img src=\""+imageURL+"/icon_credits.gif\"> &nbsp;"+format(type.getPrice())+"</html>");
        towerInfo.getDamageInfo().setText("<html><img src=\""+imageURL+"/icon_damage.gif\"> &nbsp;"+format(type.getDamage())+"</html>");
        towerInfo.getSpeedInfo().setText("<html><img src=\""+imageURL+"/icon_speed.gif\"> &nbsp;"+_(type.getSpeedString())+"</html>");
        towerInfo.getRangeInfo().setText("<html><img src=\""+imageURL+"/icon_range.gif\"> &nbsp;"+(int) type.getRange()+"</html>");
        towerInfo.getSpecialInfo().setText(_(type.getSpecial()));
        towerInfo.setVisible(true);
    }

    private String format(int value) {
        return NumberFormat.getInstance().format(value);
    }
}

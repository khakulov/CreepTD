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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;


import javax.swing.JButton;
import javax.swing.JPanel;

import com.creeptd.client.game.ContextListener;
import com.creeptd.client.game.GameContext;
import com.creeptd.common.Constants;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import static com.creeptd.client.i18n.Translator.*;

/**
 * Panel with Buttons to select Creeps.
 * 
 * @author sven
 */
public class CreepSelectPanel extends JPanel implements ContextListener {

    private static Logger logger = Logger.getLogger(CreepSelectPanel.class.getName());
    private static final long serialVersionUID = -5978301134543431476L;
    private Font font;
    private Font labelFont;
    private final int height;
    private final int width;
    private JLabel title;
    private JPanel buttonPanel;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JButton button6;
    private JButton button7;
    private JButton button8;
    private JButton button9;
    private JButton button10;
    private JButton button11;
    private JButton button12;
    private JButton button13;
    private JButton button14;
    private JButton button15;
    private JButton button16;
    private GameContext context;
    private GamePanel gamepanel;

    /**
     * @return the context
     */
    public synchronized GameContext getContext() {
        return context;
    }

    /**
     * sets Layout to Gridlayout. adds Buttons to the Panel
     *
     * @param width
     *            of the Panel
     * @param height
     *            of the Panel
     * @param gamepanel
     *            for getting object Gamepanel
     */
    public CreepSelectPanel(GamePanel gamepanel, int width, int height) {
        this.height = height;
        this.width = width;
        this.gamepanel = gamepanel;
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.setSize(this.width, this.height);
        this.setLayout(new BorderLayout());
        font = new Font("Helvetica", Font.PLAIN, 6);
        labelFont = new Font("Helvetica", Font.PLAIN, 10);
        init();
    }

    /**
     * init method to which inits the object of the Panel.
     */
    private void init() {
        this.setBackground(Color.BLACK);

        title = new JLabel() {
            @Override
            public void paintComponent(Graphics g) {
                java.net.URL imageURL = getClass().getClassLoader().getResource("com/creeptd/client/resources/panel/sidebar-title.jpg");
                Image img = new ImageIcon(imageURL).getImage();
                g.drawImage(img, 0, 0, null);
                super.paintComponent(g);
            }
        };
        title.setBounds(0, 0, this.width, 15);
        title.setForeground(Color.BLACK);
        title.setFont(new Font("Arial", Font.BOLD, 12));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setText(_("Attack with creeps"));

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 4));
        buttonPanel.setBounds(0, 15, this.getWidth(), this.getHeight()-15);

        button1 = new CreepSendButton(gamepanel, Constants.Creeps.creep1, "1");
        button2 = new CreepSendButton(gamepanel, Constants.Creeps.creep2, "2");
        button3 = new CreepSendButton(gamepanel, Constants.Creeps.creep3, "3");
        button4 = new CreepSendButton(gamepanel, Constants.Creeps.creep4, "4");
        button5 = new CreepSendButton(gamepanel, Constants.Creeps.creep5, "5");
        button6 = new CreepSendButton(gamepanel, Constants.Creeps.creep6, "6");
        button7 = new CreepSendButton(gamepanel, Constants.Creeps.creep7, "7");
        button8 = new CreepSendButton(gamepanel, Constants.Creeps.creep8, "8");
        button9 = new CreepSendButton(gamepanel, Constants.Creeps.creep9, "9");
        button10 = new CreepSendButton(gamepanel, Constants.Creeps.creep10, "10");
        button11 = new CreepSendButton(gamepanel, Constants.Creeps.creep11, "11");
        button12 = new CreepSendButton(gamepanel, Constants.Creeps.creep12, "12");
        button13 = new CreepSendButton(gamepanel, Constants.Creeps.creep13, "13");
        button14 = new CreepSendButton(gamepanel, Constants.Creeps.creep14, "14");
        button15 = new CreepSendButton(gamepanel, Constants.Creeps.creep15, "15");
        button16 = new CreepSendButton(gamepanel, Constants.Creeps.creep16, "16");
        
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(button3);
        buttonPanel.add(button4);
        buttonPanel.add(button5);
        buttonPanel.add(button6);
        buttonPanel.add(button7);
        buttonPanel.add(button8);
        buttonPanel.add(button9);
        buttonPanel.add(button10);
        buttonPanel.add(button11);
        buttonPanel.add(button12);
        buttonPanel.add(button13);
        buttonPanel.add(button14);
        buttonPanel.add(button15);
        buttonPanel.add(button16);

        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Setter to set Context.
     *
     * @param context
     *            to specify which context
     */
    public synchronized void setContext(GameContext context) {
        this.context = context;
        context.addContextListener(this);
    }

    /**
     * Enable/disable Buttons according to income.
     */
    public synchronized void updateButtons() {
        if (context == null) {
            return;
        }
        int credits = context.getCredits();

        if ((credits >= Constants.Creeps.creep1.getPrice())) {
            this.button1.setEnabled(true);
            this.button1.setBackground(Color.BLACK);
        } else {
            this.button1.setEnabled(false);
            this.button1.setBackground(Color.GRAY);
        }
        if ((credits >= Constants.Creeps.creep2.getPrice())) {
            this.button2.setEnabled(true);
            this.button2.setBackground(Color.BLACK);
        } else {
            this.button2.setEnabled(false);
            this.button2.setBackground(Color.GRAY);
        }
        if ((credits >= Constants.Creeps.creep3.getPrice())) {
            this.button3.setEnabled(true);
            this.button3.setBackground(Color.BLACK);
        } else {
            this.button3.setEnabled(false);
            this.button3.setBackground(Color.GRAY);
        }
        if ((credits >= Constants.Creeps.creep4.getPrice())) {
            this.button4.setEnabled(true);
            this.button4.setBackground(Color.BLACK);
        } else {
            this.button4.setEnabled(false);
            this.button4.setBackground(Color.GRAY);
        }
        if ((credits >= Constants.Creeps.creep5.getPrice())) {
            this.button5.setEnabled(true);
            this.button5.setBackground(Color.BLACK);
        } else {
            this.button5.setEnabled(false);
            this.button5.setBackground(Color.GRAY);
        }
        if ((credits >= Constants.Creeps.creep6.getPrice())) {
            this.button6.setEnabled(true);
            this.button6.setBackground(Color.BLACK);
        } else {
            this.button6.setEnabled(false);
            this.button6.setBackground(Color.GRAY);
        }

        if ((credits >= Constants.Creeps.creep7.getPrice())) {
            this.button7.setEnabled(true);
            this.button7.setBackground(Color.BLACK);
        } else {
            this.button7.setEnabled(false);
            this.button7.setBackground(Color.GRAY);
        }
        if ((credits >= Constants.Creeps.creep8.getPrice())) {
            this.button8.setEnabled(true);
            this.button8.setBackground(Color.BLACK);
        } else {
            this.button8.setEnabled(false);
            this.button8.setBackground(Color.GRAY);
        }
        if ((credits >= Constants.Creeps.creep9.getPrice())) {
            this.button9.setEnabled(true);
            this.button9.setBackground(Color.BLACK);
        } else {
            this.button9.setEnabled(false);
            this.button9.setBackground(Color.GRAY);
        }
        if ((credits >= Constants.Creeps.creep10.getPrice())) {
            this.button10.setEnabled(true);
            this.button10.setBackground(Color.BLACK);
        } else {
            this.button10.setEnabled(false);
            this.button10.setBackground(Color.GRAY);
        }
        if ((credits >= Constants.Creeps.creep11.getPrice())) {
            this.button11.setEnabled(true);
            this.button11.setBackground(Color.BLACK);
        } else {
            this.button11.setEnabled(false);
            this.button11.setBackground(Color.GRAY);
        }
        if ((credits >= Constants.Creeps.creep12.getPrice())) {
            this.button12.setEnabled(true);
            this.button12.setBackground(Color.BLACK);
        } else {
            this.button12.setEnabled(false);
            this.button12.setBackground(Color.GRAY);
        }
        if ((credits >= Constants.Creeps.creep13.getPrice())) {
            this.button13.setEnabled(true);
            this.button13.setBackground(Color.BLACK);
        } else {
            this.button13.setEnabled(false);
            this.button13.setBackground(Color.GRAY);
        }
        if ((credits >= Constants.Creeps.creep14.getPrice())) {
            this.button14.setEnabled(true);
            this.button14.setBackground(Color.BLACK);
        } else {
            this.button14.setEnabled(false);
            this.button14.setBackground(Color.GRAY);
        }
        if ((credits >= Constants.Creeps.creep15.getPrice())) {
            this.button15.setEnabled(true);
            this.button15.setBackground(Color.BLACK);
        } else {
            this.button15.setEnabled(false);
            this.button15.setBackground(Color.GRAY);
        }
        if ((credits >= Constants.Creeps.creep16.getPrice())) {
            this.button16.setEnabled(true);
            this.button16.setBackground(Color.BLACK);
        } else {
            this.button16.setEnabled(false);
            this.button16.setBackground(Color.GRAY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent arg0) {
    }

    /**
     * {@inheritDoc}
     */
    public void creditsChanged(GameContext context) {
        gamepanel.getCreepPanel().updateButtons();

    }

    /**
     * {@inheritDoc}
     */
    public void incomeChanged(GameContext context) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public void livesChanged(GameContext context) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public void selectedChanged(GameContext context, String message) {
        // TODO Auto-generated method stub
    }

    /**
     * @return the gamepanel
     */
    public GamePanel getGamepanel() {
        return gamepanel;
    }

    /**
     * @param gamepanel
     *            the gamepanel to set
     */
    public void setGamepanel(GamePanel gamepanel) {
        this.gamepanel = gamepanel;
    }
}

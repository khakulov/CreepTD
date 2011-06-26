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

import com.creeptd.client.Core;
import com.creeptd.client.util.Fonts;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import static com.creeptd.client.i18n.Translator.*;

/**
 *
 * @author Daniel
 */
public class OptionsPanel extends JDialog {

    private JLabel title;
    private JCheckBox soundsCheckBox;
    private JButton closeButton;
    private static boolean sounds = true;

    public OptionsPanel() {
        this.setLayout(null);
        this.setBounds(Core.getInstance().getCreatorX()+250, Core.getInstance().getCreatorY()+200, 400, 200);
        this.getContentPane().setForeground(Color.GREEN);
        this.getContentPane().setBackground(Color.BLACK);
        this.setTitle("CreepTD - "+_("Options"));
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        title = Fonts.getFrameTitle(_("Options"), 0, this);

        soundsCheckBox = new JCheckBox();
        soundsCheckBox.setText(_("Play sounds in game"));
        soundsCheckBox.setSelected(sounds);
        soundsCheckBox.setBounds(100, 70, 190, 30);
        soundsCheckBox.setBackground(Color.BLACK);
        soundsCheckBox.setForeground(Color.GREEN);
        soundsCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (soundsCheckBox.isSelected()) {
                    sounds = true;
                    sounds = true;
                } else {
                    sounds = false;
                }
                Core.getInstance().getSoundManagement().setMute(!sounds);
            }
        });

        closeButton = new JButton();
        closeButton.setBounds(150, 125, 110, 30);
        closeButton.setBackground(Color.BLACK);
        closeButton.setForeground(Color.GREEN);
        closeButton.setBorder(new LineBorder(Color.GRAY, 1));
        closeButton.setText(_("Close"));
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        this.add(title);
        this.add(soundsCheckBox);
        this.add(closeButton);
        this.validate();
    }

    @Override
    public void dispose() {
        super.dispose();
        GameLobby.onCloseOptionsPanel();
    }
}

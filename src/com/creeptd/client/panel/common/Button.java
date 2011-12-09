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
package com.creeptd.client.panel.common;

import com.creeptd.client.Core;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * A button.
 *
 * Extends JButton with some extra functionality for our needs.
 * 
 * @author Daniel
 */
public class Button extends JButton {

	private static final long serialVersionUID = 1L;

	/**
     * Create a button.
     */
    public Button() {
        super();
        this.init();
    }

    /**
     * Create a button.
     *
     * @param text The button's text
     */
    public Button(String text) {
        super(text);
        this.init();
    }

    private void init() {
        this.setMargin(new Insets(0, 0, 0, 0));
        this.setBackground(Color.BLACK);
        this.setForeground(Color.GREEN);
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) Core.getInstance().getSoundManagement().hoverSound();
            }
        });
    }
}

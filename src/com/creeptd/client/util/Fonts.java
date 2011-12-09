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
package com.creeptd.client.util;

import com.creeptd.client.Core;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Custom fonts.
 * 
 * @author Daniel
 */
public enum Fonts {
    DOTS("DOTS.ttf");

    private Font font;

    private Fonts(String file) {
        Logger logger = Logger.getLogger(Fonts.class.getName());
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("com/creeptd/client/resources/fonts/"+file);
        try {
            this.font = Font.createFont(Font.TRUETYPE_FONT, is);
            logger.info("Font "+file+" loaded");
        } catch (FontFormatException ex) {
            logger.warning("Unable to load font "+file+": "+ex);
            this.font = null;
        } catch (IOException ex) {
            logger.warning("Unable to load font "+file+": "+ex);
            this.font = null;
        }
    }

    public Font getFont(int style, int size) {
        return this.font.deriveFont(style, size);
    }

    public static JLabel getFrameTitle(String text, int yOffset) {
        return getFrameTitle(text, yOffset, Core.getInstance());
    }
    
    public static JLabel getFrameTitle(String text, int yOffset, Object base) {
        JLabel title = new JLabel();
        try {
            Integer width = (Integer) base.getClass().getMethod("getWidth").invoke(base);
            title.setBounds(0, yOffset, width, 70);
            title.setFont(Fonts.DOTS.getFont(Font.PLAIN, 34));
            title.setForeground(Color.GREEN);
            title.setHorizontalAlignment(SwingConstants.CENTER);
            title.setText("<html>&lt;&lt; <span style=\"color:black;\">.</span><span style=\"color:red;\">"+text.replace(" ", " <span style=\"color:black;\">.</span>").replace(".", " .").toLowerCase()+"</span></html>");
            return title;
        } catch (Exception ex) {
            System.out.println("Unexpected exception: "+ex);
        }
        return null;
    }
}

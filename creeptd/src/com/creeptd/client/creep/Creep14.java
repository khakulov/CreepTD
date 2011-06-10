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
/**
 * 
 */
package com.creeptd.client.creep;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.creeptd.client.game.GameContext;
import com.creeptd.client.util.Cache;
import com.creeptd.common.IConstants;

/**
 * implementation of a Creep14.
 * @author sven
 *
 */
public class Creep14 extends AbstractCreep {

    /**
     * constructor to set gamecontext and type of creep.
     * @param context gamecontext
     * @param t type of creep
     */
    public Creep14(GameContext context, IConstants.Creeps t) {
        super(context, t);
        setSlowImmune(true);
//		
//		setImage(new BufferedImage(getWidth(), getHeight(),
//				BufferedImage.TYPE_INT_ARGB));
//		Graphics2D g2 = (Graphics2D) getImage().getGraphics();
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
//				 RenderingHints.VALUE_ANTIALIAS_ON);
//		g2.setColor(Color.WHITE);
//		g2.setStroke(new BasicStroke(1f));
//		
//		g2.drawOval(7, 7, 6, 6);
//		
//		g2.drawLine(13, 10, 16, 10);
//
//		
//		
//
//		g2.dispose();
        loadImage();

    }

    /**
     * Creates a new instance of DefaultCreep.
     * @param context the game context
     */
    //private BufferedImage image;
    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     *
     */
    public void loadImage() {

        if (Cache.getInstance().hasCreepImg(IConstants.Creeps.creep14)) {
            setImage(Cache.getInstance().getCreepImg(IConstants.Creeps.creep14));
        } else {

            try {
                BufferedImage forCache;
                this.image = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(
                        IConstants.CREEPS_URL + "14.png"));
                forCache = this.image;
                Cache.getInstance().putCreepImg(IConstants.Creeps.creep14, forCache);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}

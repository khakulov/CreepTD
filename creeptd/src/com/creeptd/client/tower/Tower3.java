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
package com.creeptd.client.tower;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import com.creeptd.client.creep.Creep;
import com.creeptd.client.game.GameContext;
import com.creeptd.client.grid.Grid;
import com.creeptd.common.IConstants;

/**
 * implementation of tower3.
 * 
 * @author sven
 */
public class Tower3 extends AbstractTower {
    // private int coolDown = 10;
    // private int coolDownNow = 0;

    // private Color shapeColor;
    private Line2D beam;
    private Line2D splash;

    /**
     * Creates a new instance of DefaultTower.
     *
     * @param context
     *            the gameContext for the tower
     * @param grid
     *            the grid where the tower is placed into
     */
    public Tower3(GameContext context, Grid grid) {
        super(IConstants.Towers.tower3, context, grid);
        this.setStrategy(new FindClosestCreep(this));
        this.updateSelectedStrategy(this.getStrategy());
    }

    /**
     * {@inheritDoc}
     */
    protected void init() {
        // shapeColor = new Color(0, 204, 255);
    }

    /**
     * Draws the image for the tower.
     */
    public void generateImage() {
        Graphics2D g2 = (Graphics2D) getImage().getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(this.getTowerColor());

        g2.drawRect(2, 2, 16, 16);
        g2.drawRect(8, 8, 4, 4);

        g2.drawRect(6, 6, 8, 8);
        g2.drawLine(10, 10, 10, 10);

        g2.dispose();

    }

    /**
     * {@inheritDoc}
     */
    public String getTowerInfo() {
        return "Tower3";
    }

    /**
     * {@inheritDoc}
     */
    public void update(long tickNumber) {
        super.update(tickNumber);

    }

    /**
     * {@inheritDoc}
     */
    public void paintEffect(Graphics2D g) {
        super.paintEffect(g);
        if (this.getTarget() != null && getCoolDownNow() == getCoolDown()) {
            Stroke s = g.getStroke();
            Color c = g.getColor();

            beam = new Line2D.Float(this.getGrid().getLocation()[0] + Grid.SIZE / 2, this.getGrid().getLocation()[1] + Grid.SIZE / 2, this.getTarget().getX() + Grid.SIZE / 2, this.getTarget().getY() + Grid.SIZE / 2);

            g.setStroke(new BasicStroke(3));
            g.setColor(Color.WHITE);
            g.draw(beam);

            g.setStroke(new BasicStroke(1));
            g.setColor(Color.GRAY);
            g.draw(beam);

            for (Creep creep : getTargets()) {
                splash = new Line2D.Float(getTarget().getX() + Grid.SIZE / 2,
                        getTarget().getY() + Grid.SIZE / 2, creep.getX() + Grid.SIZE / 2, creep.getY() + Grid.SIZE / 2);
                g.setStroke(new BasicStroke(1));
                g.setColor(new Color(255, 255, 255));
                g.draw(splash);

            }

            g.setStroke(s);
            g.setColor(c);
        }
    }
}

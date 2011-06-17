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

import com.creeptd.client.game.GameContext;
import com.creeptd.client.grid.Grid;
import com.creeptd.common.Constants;

/**
 * implementation of tower5.
 * 
 * @author mi7hr4ndir
 */
public class Tower5 extends AbstractTower {
    // private int coolDown = 10;
    // private int coolDownNow = 0;

    // private Color shapeColor;
    private Line2D beam;

    /**
     * Creates a new instance of Tower5.
     *
     * @param context
     *            the gameContext for the tower
     * @param grid
     *            the grid where the tower is placed into
     */
    public Tower5(GameContext context, Grid grid) {
        super(Constants.Towers.tower5, context, grid);
        this.setStrategy(new FindStrongestCreep(this));
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

        g2.drawArc(2, 2, 16, 16, 1, 360);

        g2.drawArc(5, 5, 10, 10, 1, 360);

        g2.drawArc(8, 8, 4, 4, 1, 360);

        // g2.drawLine(10, 2, 10, 18);
        // g2.drawLine(2, 10, 18, 10);

        g2.dispose();

    }

    /**
     * {@inheritDoc}
     */
    public String getTowerInfo() {
        return "Tower5";
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
            g.setColor(Color.PINK);
            g.draw(beam);

            g.setStroke(new BasicStroke(1));
            g.setColor(Color.RED);
            g.draw(beam);

            g.setStroke(s);
            g.setColor(c);
        }
    }
}

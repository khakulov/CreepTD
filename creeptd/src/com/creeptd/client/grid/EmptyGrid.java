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
package com.creeptd.client.grid;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

import com.creeptd.client.game.GameContext;
import com.creeptd.client.game.PlayerContext;
import com.creeptd.client.tower.AbstractTower;
import com.creeptd.client.tower.Tower;
import com.creeptd.common.IConstants;
import java.awt.Composite;

/**
 * Grid for all the places the player can build his towers onto.
 * 
 * @author Philipp
 * 
 */
public class EmptyGrid implements Grid {

    private GameContext context;
    private int xLocation;
    private int yLocation;
    private int[] location = new int[2];
    private Tower tower;
    private boolean highlight = false;
    private AffineTransform pos = new AffineTransform();
    private Color gridColor = Color.ORANGE;
    private final Color gridHighlightColor = new Color(255, 255, 255);
    private final Color gridHighlightColorUnavailable = new Color(255, 0, 0);
    /**
     * Determines whether this field is busy or not (upgrading, selling etc.)
     */
    private boolean isOccupied = false;

    /**
     * Creates a new instance of EmptyGrid.
     *
     * @param x
     *            the x location on the board
     * @param y
     *            thy y location on the board
     * @param context
     *            the gameContext
     */
    public EmptyGrid(int x, int y, GameContext context) {
        this.xLocation = x;
        this.yLocation = y;
        this.location[0] = x;
        this.location[1] = y;
        this.context = context;
        if (this.context instanceof PlayerContext) {
            gridColor = new Color(255, 100, 0);
        }
    }

    /**
     * Returns whether this field is busy or not (upgrading, selling etc.).
     * This status is only used for local use to make sure, multiple actions are not done.
     * @return boolean
     */
    public boolean isOccupied() {
        return this.isOccupied;
    }

    public void setOccupiedStatus(boolean status) {
        this.isOccupied = status;
    }

    /**
     * {@inheritDoc}
     */
    public int[] getLocation() {
        return location;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFree() {
        return (this.tower == null);
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D g) {
        // TODO Auto-generated method stub
        AffineTransform save = g.getTransform();

        pos.setToIdentity();
        pos.translate(xLocation, yLocation);

        g.transform(pos);

        g.setColor(gridColor);
        g.drawLine(0, 0, SIZE, 0);
        g.drawLine(SIZE, 0, SIZE, SIZE);
        g.drawLine(0, SIZE, SIZE, SIZE);
        g.drawLine(0, 0, 0, SIZE);
        g.setTransform(save);
    }

    /**
     * {@inheritDoc}
     */
    public void paintHighlight(Graphics2D g) {
        if (this.highlight) {
            AffineTransform save = g.getTransform();

            pos.setToIdentity();
            pos.translate(xLocation, yLocation);

            g.transform(pos);

            if (context.getNextTower() != null) {

                if (context.getCredits() < context.getNextTower().getPrice()) {
                    g.setColor(gridHighlightColorUnavailable);
                } else {
                    g.setColor(gridHighlightColor);
                }
                if (this.getTower() == null) {
                    Composite oldComposite = g.getComposite();
                    IConstants.Towers current = this.context.getNextTower();
                    while (current != null) {
                        Arc2D rangeArcUpgrade = new Arc2D.Float();
                        rangeArcUpgrade.setArcByCenter(Grid.SIZE / 2, Grid.SIZE / 2, current.getRange(), 0.0, 360.0, Arc2D.CHORD);
                        g.setColor(current.getTowerColor());
                        if (AbstractTower.ALPHA) {
                            if (!this.context.getNextTower().equals(current)) {
                                g.setComposite(AbstractTower.rangeAlpha);
                            } else {
                                g.setComposite(AbstractTower.noAlpha);
                            }
                        }
                        g.draw(rangeArcUpgrade);
                        current = current.getNext();
                    }

                    g.setComposite(oldComposite);
                    g.setColor(Color.WHITE);
                    g.fillRect(1, 1, SIZE - 1, SIZE - 1);
                } else {
                    g.setColor(Color.WHITE);
                    g.drawRect(0, 0, SIZE, SIZE);
                }

            } else {
                g.setColor(Color.WHITE);
                g.drawRect(0, 0, SIZE, SIZE);
            }

            g.setTransform(save);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeTower() {
        this.tower = null;
    }

    /**
     * {@inheritDoc}
     */
    public void setTower(Tower t) {
        this.tower = t;
    }

    /**
     * Getter for the tower in the grid.
     *
     * @return the tower or null if no tower is in the grid
     */
    public Tower getTower() {
        return this.tower;
    }

    /**
     * @param highlight
     *            the highlight to set
     */
    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public boolean getHighlight() {
        return this.highlight;
    }

    @Override
    public int getX() {
        return xLocation;
    }

    @Override
    public int getY() {
        return yLocation;
    }
}

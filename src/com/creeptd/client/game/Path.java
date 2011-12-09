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
package com.creeptd.client.game;

import java.awt.Point;
import java.util.ArrayList;

import com.creeptd.client.game.grids.Grid;

/**
 * This class represents the path a creep walks on the board.
 * The path is a list of segments. A segment is basically one grid within the 
 * board, and means the access integer used get a grid out of the gridarray
 * in the board class. So segment 0,0 is the center of the grid in the upper
 * left corner.
 */
public class Path {

    private ArrayList<Integer> segmentsX;
    private ArrayList<Integer> segmentsY;

    /**
     * Default constructor.
     */
    public Path() {
        this.segmentsX = new ArrayList<Integer>();
        this.segmentsY = new ArrayList<Integer>();
    }

    /**
     * Adds one segment to the path.
     *
     * @param x the x value
     * @param y the y value
     */
    public void addSegment(Integer x, Integer y) {
        this.segmentsX.add(x);
        this.segmentsY.add(y);
    }

    /**
     * Gets the next position.
     * @param segment the current segment
     * @param segmentStep the step in the current segment
     * @return a float array with the x and y coordinates
     * @throws IllegalArgumentException "segment is too long!"
     */
    public float[] getStep(int segment, int segmentStep)
            throws IllegalArgumentException { //FIXME checkstyle.conf

        float[] ret = new float[]{0F, 0F};

        if (segment > (segmentsX.size() - 1)) {
            throw new IllegalArgumentException("segment too long!");
        }

        float xStart = segmentsX.get(segment) * Grid.SIZE;
        float yStart = segmentsY.get(segment) * Grid.SIZE;

        float xEnd = segmentsX.get(segment + 1) * Grid.SIZE;
        float yEnd = segmentsY.get(segment + 1) * Grid.SIZE;

        ret[0] = (xStart + (xEnd - xStart) * (float) segmentStep / 1000f);
        ret[1] = (yStart + (yEnd - yStart) * (float) segmentStep / 1000f);

        return ret;
    }

    /**
     * Returns the Point from Path
     */
    public Point getPoint(int index) {
        return new Point(segmentsX.get(index), segmentsY.get(index));
    }

    /**
     * Get a raw step from the path.
     * @param index the segment number
     * @return an integer array with x and y
     */
    public Integer[] getStep(int index) {
        Integer[] ret = new Integer[2];
        ret[0] = segmentsX.get(index);
        ret[1] = segmentsY.get(index);
        return ret;
    }

    /**
     * Getter for the length of the path.
     * @return the length in segments
     */
    public int getLength() {
        return this.segmentsX.size();
    }
}

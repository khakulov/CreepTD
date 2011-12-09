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
package com.creeptd.client.game.towers;

import com.creeptd.client.game.GameContext;
import com.creeptd.client.game.grids.Grid;
import com.creeptd.common.Constants;

/**
 * Factory to create towers by their type.
 */
public class TowerFactory {

    /**
     * Creates a tower by the given type.
     *
     * @param type
     *            the type of the tower
     * @param context
     *            the gameContext for the tower
     * @param grid
     *            the grid in which the tower is placed
     * @return the created tower
     */
    public static Tower createTower(Constants.Towers type, GameContext context, Grid grid) {
    	return new TowerImpl(type, context, grid);
    }
}

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
package com.creeptd.client.game.strategies;

import java.util.List;

import com.creeptd.client.game.creeps.Creep;
import com.creeptd.client.game.grids.Grid;
import com.creeptd.client.game.towers.Tower;

/**
 * A strategy to find the closest Creep.
 */
public class ClosestStrategy extends AbstractStrategy {

    public ClosestStrategy(Tower tower) {
        super(tower);
    }

    @Override
    public Creep findNewCreep(List<Creep> creeps) {
        Creep found = null;
        float found_distance = 99999f;
        for (Creep creep : creeps) {
            float dX = (creep.getX() + Grid.SIZE / 2) - (tower.getGrid().getLocation()[0] + Grid.SIZE / 2);
            float dY = (creep.getY() + Grid.SIZE / 2) - (tower.getGrid().getLocation()[1] + Grid.SIZE / 2);

			// squared distance
            float creep_dist = dX * dX + dY * dY;
			if (found != null && creep_dist > found_distance)
				continue;
			found_distance = creep_dist;
			found = creep;
        }
        lastCreep = found;
        return found;
    }
}

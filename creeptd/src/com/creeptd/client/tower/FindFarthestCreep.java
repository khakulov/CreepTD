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

import com.creeptd.client.creep.Creep;
import com.creeptd.client.grid.Grid;

/**
 * A strategy to find the farthest Creep.
 * 
 * @author mi7hr4ndir
 */
public class FindFarthestCreep implements FindCreepStrategy {

    private Tower tower;
    private boolean locked;
    private Creep lastCreep;

    /**
     * Creates new FindClosestCreepStrategy.
     *
     * @param t
     *            tower
     */
    public FindFarthestCreep(Tower t) {
        tower = t;
    }

    /**
     * Strategy.
     *
     * @return Creep
     */
    public Creep findCreep() {
        if (locked && lastCreep != null && tower.getContext().getCreeps().contains(lastCreep) && lastCreep.isValidTarget()) {
            float distanceMin = tower.getRange() * tower.getRange();
            float dX = (lastCreep.getX() + Grid.SIZE / 2) - (tower.getGrid().getLocation()[0] + Grid.SIZE / 2);
            float dY = (lastCreep.getY() + Grid.SIZE / 2) - (tower.getGrid().getLocation()[1] + Grid.SIZE / 2);
            // squared distance
            float dist = dX * dX + dY * dY;
            if (dist < distanceMin) {
                return lastCreep;
            }
        }

        lastCreep = null;
        Creep found = null;

        int size = tower.getContext().getCreeps().size();

        float distanceMax = tower.getRange() * tower.getRange();
        for (int i = 0; i < size; i++) {
            if (tower.getContext().getCreeps().get(i).isValidTarget()) {
                float dX = (tower.getContext().getCreeps().get(i).getX() + Grid.SIZE / 2) - (tower.getGrid().getLocation()[0] + Grid.SIZE / 2);
                float dY = (tower.getContext().getCreeps().get(i).getY() + Grid.SIZE / 2) - (tower.getGrid().getLocation()[1] + Grid.SIZE / 2);

                // squared distance
                float dist = dX * dX + dY * dY;
                if (dist <= distanceMax) {
                    if (found == null) {
                        found = tower.getContext().getCreeps().get(i);
                    } else {
                        if (tower.getContext().getCreeps().get(i).getTotalSegmentSteps() > found.getTotalSegmentSteps()) {
                            found = tower.getContext().getCreeps().get(i);
                        }
                    }
                }

            }
        }
        lastCreep = found;
        return found;
    }

    @Override
    public String strategieInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCreepLock() {
        // TODO Auto-generated method stub
        return locked;
    }

    @Override
    public void setCreepLock(boolean selected) {
        locked = selected;

    }
}

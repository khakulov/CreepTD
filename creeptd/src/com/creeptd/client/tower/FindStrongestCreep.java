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
import com.creeptd.client.game.GameContext;

/**
 * A strategy to find the strongest Creep.
 * 
 * @author mi7hr4ndir
 */
public class FindStrongestCreep implements FindCreepStrategy {

    private Tower tower;
    private boolean locked;
    private Creep lastCreep;

    /**
     * Creates new FindClosestCreepStrategy.
     *
     * @param t
     *            tower
     */
    public FindStrongestCreep(Tower t) {
        tower = t;
    }

    /**
     * Strategy.
     *
     * @return Creep
     */
    public Creep findCreep() {
        float distanceMin = tower.getRange() * tower.getRange();
        if (locked && lastCreep != null && tower.getContext().getCreeps().contains(lastCreep) && lastCreep.isValidTarget() && (lastCreep.getHealth() > 0)) {
            float dX = lastCreep.getX() - tower.getGrid().getX();
            float dY = lastCreep.getY() - tower.getGrid().getY();
            // squared distance
            float dist = dX * dX + dY * dY;
            if (dist < distanceMin) {
                return lastCreep;
            }
        }
        GameContext context = tower.getContext();
        lastCreep = null;
        Creep found = null;
        int health = 0;
        for (Creep creep : context.getCreeps()) {
            if (creep.isValidTarget()) {
                float dX = creep.getX() - tower.getGrid().getX();
                float dY = creep.getY() - tower.getGrid().getY();
                // squared distance
                float dist = dX * dX + dY * dY;
                if (dist < distanceMin && creep.getHealth() > health) {
                    health = creep.getHealth();
                    found = creep;
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

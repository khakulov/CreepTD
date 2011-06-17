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
package com.creeptd.client.creep;

import com.creeptd.client.game.GameContext;
import com.creeptd.common.Constants;

/**
 * Factory to create new creeps.
 * @author Philipp
 *
 */
public class CreepFactory {

    /**
     * creates a Creep.
     * @param context gets GameContext
     * @param t type of creep
     * @return type of creep
     */
    public static Creep createCreep(GameContext context, Constants.Creeps t) {
        switch (t) {
            case creep1:
                return new Creep1(context, Constants.Creeps.creep1);
            case creep2:
                return new Creep2(context, Constants.Creeps.creep2);
            case creep3:
                return new Creep3(context, Constants.Creeps.creep3);
            case creep4:
                return new Creep4(context, Constants.Creeps.creep4);
            case creep5:
                return new Creep5(context, Constants.Creeps.creep5);
            case creep6:
                return new Creep6(context, Constants.Creeps.creep6);
            case creep7:
                return new Creep7(context, Constants.Creeps.creep7);
            case creep8:
                return new Creep8(context, Constants.Creeps.creep8);
            case creep9:
                return new Creep9(context, Constants.Creeps.creep9);
            case creep10:
                return new Creep10(context, Constants.Creeps.creep10);
            case creep11:
                return new Creep11(context, Constants.Creeps.creep11);
            case creep12:
                return new Creep12(context, Constants.Creeps.creep12);
            case creep13:
                return new Creep13(context, Constants.Creeps.creep13);
            case creep14:
                return new Creep14(context, Constants.Creeps.creep14);
            case creep15:
                return new Creep15(context, Constants.Creeps.creep15);
            case creep16:
                return new Creep16(context, Constants.Creeps.creep16);
            default:
                return null;
        }
    }
}

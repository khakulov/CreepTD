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

import com.creeptd.client.network.Network;
import com.creeptd.common.Constants;

/**
 * The GameContext for opponents.
 * 
 * @author Philipp
 */
public class OpponentContext extends GameContext {

    /**
     * Creates a new instance of OpponentContext.
     *
     * @param location The boardLocation (GameContext.BoardLocation)
     * @param network The current network object
     * @param map The map
     * @param gameLoop The game loop
     * @param playerId The player's id
     * @param playerName The player's name
     */
    public OpponentContext(BoardLocation location, Network network, Constants.Map map, GameLoop gameLoop, int playerId, String playerName) {
        super(location, network, null, map, gameLoop, playerId, playerName);
    }
}

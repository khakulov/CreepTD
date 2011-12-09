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

import com.creeptd.client.game.towers.Tower;
import com.creeptd.common.Constants;

public class StrategyFactory {

	public static Strategy createStrategy(Tower tower) {
		Strategy strategy = null;
		switch (tower.getType()) {
		case tower1:
		case tower11:
		case tower12:
		case tower13:
		case tower3:
		case tower31:
		case tower32:
		case tower33:
			strategy = new ClosestStrategy(tower);
			break;
		case tower2:
		case tower21:
		case tower22:
		case tower23:
			strategy = new FastestStrategy(tower);
			break;
		default:
			strategy = new StrongestStrategy(tower);
			break;
		}
		return strategy;
	}

	public static Strategy createStrategy(Constants.StrategyType strategyType, Tower tower) {
		Strategy strategy = null;
		switch (strategyType) {
		case CLOSEST:
			strategy = new ClosestStrategy(tower);
			break;
		case FARTHEST:
			strategy = new FarthestStrategy(tower);
			break;
		case FASTEST:
			strategy = new FastestStrategy(tower);
			break;
		case STRONGEST:
			strategy = new StrongestStrategy(tower);
			break;
		case WEAKEST:
			strategy = new WeakestStrategy(tower);
			break;
		}
		return strategy;
	}
}

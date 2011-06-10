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
package com.creeptd.server.game.states;

import org.apache.log4j.Logger;

import com.creeptd.common.messages.client.GameMessage;
import com.creeptd.server.game.Game;
import com.creeptd.server.game.GameManager;
import com.creeptd.server.game.PlayerInGame;

/**
 * GameState for a game that has terminated, meaning that there are no players
 * left.
 */
public class TerminatedGameState extends AbstractGameState {

    private static Logger logger = Logger.getLogger(TerminatedGameState.class);

    /**
     * Creates a new one.
     * @param game the game. Must not be null.
     */
    public TerminatedGameState(Game game) {
        super(game);
    }

    /**
     * Handle a message. All messages are ignored.
     * @param message the message
     * @param sender the player who sent the message.
     * @return this.
     */
    public AbstractGameState consume(GameMessage message, PlayerInGame sender) {
        logger.info("ignoring message: " + message);
        return this;
    }

    /**
     * Returns a string identifying this state.
     * @return "terminated"
     */
    public String toString() {
        return "terminated";
    }

    @Override
    public void enter() {
        this.getGame().terminate();
        GameManager.remove(this.getGame());
    }

    @Override
    public void leave() {
    }
}

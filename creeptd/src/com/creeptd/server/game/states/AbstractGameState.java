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

import com.creeptd.common.messages.client.GameMessage;
import com.creeptd.common.messages.client.ClientChatMessage;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.server.game.Game;
import com.creeptd.server.game.PlayerInGame;

/**
 * A GameState is used by a Game instance to handle incoming messages and
 * "ticks". GameState and its subclasses implement the state pattern as well as
 * the related Strategy pattern.
 */
public abstract class AbstractGameState {

    private Game game;

    /**
     * Constructor to be called by subclasses.
     *
     * @param game
     *            the game. Must not be null.
     */
    protected AbstractGameState(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("'game' was null");
        }
        this.game = game;
    }

    /**
     * Handle a message (from a client, presumably).
     *
     * @param message
     *            a message (from a client, presumably).
     * @param sender
     *            the player who sent the message.
     * @return the new GameState.
     */
    public abstract AbstractGameState consume(GameMessage message,
            PlayerInGame sender);

    public abstract void enter();

    public abstract void leave();

    /**
     * Returns the game.
     *
     * @return the game.
     */
    public Game getGame() {
        return this.game;
    }

    /**
     * Handle SEND_MESSAGE.
     *
     * @param m
     *            the message.
     */
    protected void handle(ClientChatMessage m, PlayerInGame player) {
        this.game.sendAll(new ServerChatMessage(player.getClient().getPlayerModel().getName(), m.getMessage()));
    }
}

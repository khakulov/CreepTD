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

import com.creeptd.common.messages.client.BuildCreepMessage;
import com.creeptd.common.messages.client.BuildTowerMessage;
import com.creeptd.common.messages.client.ChangeStrategyMessage;
import org.apache.log4j.Logger;

import com.creeptd.common.messages.client.ExitGameMessage;
import com.creeptd.common.messages.client.GameMessage;
import com.creeptd.common.messages.client.GameOverMessage;
import com.creeptd.common.messages.client.LogoutMessage;
import com.creeptd.common.messages.client.ClientChatMessage;
import com.creeptd.common.messages.client.CreepEscapedMessage;
import com.creeptd.common.messages.client.SellTowerMessage;
import com.creeptd.common.messages.client.UpgradeTowerMessage;
import com.creeptd.common.messages.server.PlayerQuitMessage;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.server.game.Game;
import com.creeptd.server.game.PlayerInGame;
import java.util.List;

/**
 * GameState for a game that has ended, meaning that all clients have sent
 * GAME_OVER, but there are still clients lurking around. Chat still works in
 * this state, but that's about it.
 */
public class EndedGameState extends AbstractGameState {

    private static Logger logger = Logger.getLogger(EndedGameState.class);

    /**
     * Creates a new one.
     *
     * @param game
     *            the game. Must not be null.
     */
    public EndedGameState(Game game) {
        super(game);
    }

    /**
     * Handle a message.
     *
     * @param message
     *            the message
     * @param sender
     *            the player who sent the message.
     * @return the new state.
     */
    public AbstractGameState consume(GameMessage message, PlayerInGame sender) {
        if (message == null) {
            throw new IllegalArgumentException("'message' was null!");
        }
        if (sender == null) {
            throw new IllegalArgumentException("'sender' was null!");
        }
        if (message instanceof ExitGameMessage) {
            return removePlayer(sender);
        } else if (message instanceof ClientChatMessage) {
            handle((ClientChatMessage) message, sender);
        } else if (message instanceof LogoutMessage) {
            return removePlayer(sender);
        } else if (message instanceof GameOverMessage ||
                message instanceof BuildCreepMessage ||
                message instanceof BuildTowerMessage ||
                message instanceof SellTowerMessage ||
                message instanceof UpgradeTowerMessage ||
                message instanceof ChangeStrategyMessage ||
                message instanceof CreepEscapedMessage) {
            return this; // Silently ignore
        } else {
            logger.warn("Cannot handle message: " + message);
        }
        return this;
    }

    /**
     * Returns a string identifying this state.
     *
     * @return "ended"
     */
    @Override
    public String toString() {
        return "ended";
    }

    @Override
    public void enter() {
        // Remove players who left the game while it was running
        List<PlayerInGame> pl = this.getGame().getPlayers();
        for (int i=0; i<pl.size(); i++) {
            PlayerInGame p = pl.get(i);
            if (!p.isConnected()) {
                this.getGame().removePlayer(p);
            }
        }
    }

    @Override
    public void leave() {
    }

    private AbstractGameState removePlayer(PlayerInGame sender) {
        this.getGame().removePlayer(sender);
        this.getGame().sendAll(new PlayerQuitMessage(sender.getClient().getPlayerModel().getName(), "", sender.getClient().getId()));
        ServerChatMessage scm = new ServerChatMessage();
        scm.setMessage("has left...");
        scm.setPlayerName(sender.getClient().getPlayerModel().getName());
        scm.setTranslate(true);
        this.getGame().sendAll(scm);
        if (this.getGame().numPlayers() == 0) {
            return new TerminatedGameState(this.getGame());
        }
        return this;
    }
}

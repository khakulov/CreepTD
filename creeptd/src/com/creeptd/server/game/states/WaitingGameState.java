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

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.creeptd.common.IConstants.ResponseType;
import com.creeptd.common.messages.client.ExitGameMessage;
import com.creeptd.common.messages.client.GameMessage;
import com.creeptd.common.messages.client.KickPlayerRequestMessage;
import com.creeptd.common.messages.client.LogoutMessage;
import com.creeptd.common.messages.client.ClientChatMessage;
import com.creeptd.common.messages.client.StartGameRequestMessage;
import com.creeptd.common.messages.server.KickPlayerResponseMessage;
import com.creeptd.common.messages.server.KickedMessage;
import com.creeptd.common.messages.server.PlayerQuitMessage;
import com.creeptd.common.messages.server.StartGameMessage;
import com.creeptd.common.messages.server.StartGameResponseMessage;
import com.creeptd.server.client.Client;
import com.creeptd.server.game.Game;
import com.creeptd.server.game.GameManager;
import com.creeptd.server.game.PlayerInGame;
import java.util.ArrayList;

/**
 * GameState for a game that has not yet started.
 */
public class WaitingGameState extends AbstractGameState {

    private Client creator;
    private static Logger logger = Logger.getLogger(WaitingGameState.class);

    /**
     * Creates a new one, initially with zero players.
     *
     * @param game the game. Must not be null.
     * @param creator the client who created the game.
     */
    public WaitingGameState(Game game, Client creator) {
        super(game);
        this.creator = creator;
    }

    /**
     * Handle a message (from a client, presumably).
     *
     * @param message the message. Must not be null.
     * @param sender the player who sent the message. Must not be null.
     * @return the new state
     */
    public AbstractGameState consume(GameMessage message, PlayerInGame sender) {
        if (message == null) {
            throw new IllegalArgumentException("'message' was null!");
        }
        if (sender == null) {
            throw new IllegalArgumentException("'sender' was null!");
        }
        if (message instanceof StartGameRequestMessage) {
            return handle((StartGameRequestMessage) message, sender);
        } else if (message instanceof KickPlayerRequestMessage) {
            handle((KickPlayerRequestMessage) message, sender);
        } else if (message instanceof ExitGameMessage) {
            return this.removePlayer(sender);
        } else if (message instanceof LogoutMessage) {
            return this.removePlayer(sender);
        } else if (message instanceof ClientChatMessage) {
            handle((ClientChatMessage) message, sender);
        } else {
            logger.error("Cannot handle message: " + message);
        }
        return this;
    }

    /**
     * Handles the StartGameRequestMessage.
     *
     * @param m
     *            the message
     * @param sender
     *            the player who sent the message.
     * @return the new state
     */
    private AbstractGameState handle(StartGameRequestMessage m, PlayerInGame sender) {
        if (this.creator != sender.getClient()) {
            logger.info(sender.getClient() + " tried to start game, but he isn't creator.");
            sender.getClient().send(new StartGameResponseMessage(ResponseType.failed));
            return this;
        }
        if (this.getGame().numPlayers() < this.getGame().getMaxPlayers()) {
            sender.getClient().send(new StartGameResponseMessage(ResponseType.failed));
            return this;
        }
        if (this.getGame().getShufflePlayers()) {
            this.getGame().shufflePlayers();
        }
        if (this.getGame().getMapId() == 0) {
            this.getGame().setRandomMap();
        }
        // Create and send the StartGameMessage to all users.
        StartGameMessage sgm = new StartGameMessage();
        List<Integer> list = new LinkedList<Integer>();
        for (PlayerInGame p : this.getGame().getPlayers()) {
            list.add(p.getClient().getClientID());
        }
        sgm.setPlayers(list);
        sgm.setMapID(this.getGame().getMapId());
        this.getGame().sendAll(sgm);

        sender.getClient().send(new StartGameResponseMessage(ResponseType.ok));

        return new RunningGameState(this.getGame());
    }

    /**
     * Handles the KickPlayerRequestMessage.
     *
     * @param m
     *            the message
     * @param sender
     *            the player who sent the message.
     */
    private void handle(KickPlayerRequestMessage m, PlayerInGame sender) {
        PlayerInGame player = this.getGame().findPlayer(m.getPlayerName());
        if (player == null) {
            logger.warn("Cannot find player '" + m.getPlayerName() + "'");
            sender.getClient().send(new KickPlayerResponseMessage(ResponseType.failed));
            return;
        }
        logger.info("Kicking " + player.getClient() + " from game '" + this.getGame() + "'");
        player.getClient().send(new KickedMessage());
        this.getGame().removePlayer(player);
        sender.getClient().send(new KickPlayerResponseMessage(ResponseType.ok));
    }

    private AbstractGameState removePlayer(PlayerInGame sender) {
        this.getGame().removePlayer(sender);
        if (sender.getClient() == this.creator) {
            for (PlayerInGame p : this.getGame().getPlayers()) {
                p.getClient().send(new KickedMessage());
                this.getGame().removePlayer(p);
            }
        }
        this.getGame().sendAll(new PlayerQuitMessage(sender.getClient().getPlayerModel().getName(), "", sender.getClient().getClientID()));
        if (this.getGame().numPlayers() == 0) {
            return new TerminatedGameState(this.getGame());
        }
        return this;
    }

    /**
     * Returns a string identifying this state.
     *
     * @return "waiting"
     */
    public String toString() {
        return "waiting";
    }

    @Override
    public void enter() {
        GameManager.add(this.getGame());
    }

    @Override
    public void leave() {
    }
}

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
package com.creeptd.server.client.states;

import org.apache.log4j.Logger;

import com.creeptd.common.messages.client.ClientMessage;
import com.creeptd.common.messages.client.ExitGameMessage;
import com.creeptd.common.messages.client.GameMessage;
import com.creeptd.common.messages.client.LogoutMessage;
import com.creeptd.common.messages.client.ClientChatMessage;
import com.creeptd.server.AuthenticationService;
import com.creeptd.server.client.Client;
import com.creeptd.server.game.Game;

/**
 * 
 * This class represents the state of the client when it is in an active game.
 * 
 * @author Bernd Hietler
 */
public class InGameState extends AbstractClientState {

    private final Game game;
    private static Logger logger = Logger.getLogger(InGameState.class);
    private final AuthenticatedState authenticatedState;

    /**
     * Constructor method instantiates the InGameState object.
     *
     * @param client
     *            Client
     * @param game
     *            Game
     * @param authenticatedState
     *            the previous state.
     */
    public InGameState(Client client, Game game, AuthenticatedState authenticatedState) {
        super(client);
        this.game = game;
        this.authenticatedState = authenticatedState;
    }

    /**
     * Handles the game-messages.
     *
     * @param message
     *            ClientMessage
     * @return ClientState
     */
    @Override
    public AbstractClientState receiveMessage(ClientMessage message) {
        if (message == null) {
            LogoutMessage m = new LogoutMessage();
            m.setClientId(this.getClient().getClientID());
            this.game.receive(m);
            AuthenticationService.logout(this.getClient());
            logger.info("client " + this.getClient() + " disconnected in InGameState");
            return null;
        }
        if (message instanceof GameMessage) {
            if (message instanceof ClientChatMessage) {
                String[] msgSplit = ((ClientChatMessage) message).getMessage().split(" ");
                if (msgSplit.length >= 1) {
                    if ((msgSplit.length > 2) && msgSplit[0].equalsIgnoreCase("/to") && !this.getClient().getPlayerModel().getName().equalsIgnoreCase(msgSplit[1])) {
                        authenticatedState.receiveMessage(message);
                        return this;
                    }
                }
            }
            this.game.receive((GameMessage) message);
            if (message instanceof ExitGameMessage) {
                return this.authenticatedState;
            }
            if (message instanceof LogoutMessage) {
                AuthenticationService.logout(this.getClient());
                return this.authenticatedState.getAnonymousState();
            }
            return this;
        }
        logger.error("Wrong messagetype for GameQueue: " + message.getMessageString());
        return this;
    }

    public AbstractClientState getAuthenticatedState() {
        return this.authenticatedState;
    }

    @Override
    public void enter() {
    }

    @Override
    public void leave() {
    }
}

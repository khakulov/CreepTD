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

import com.creeptd.common.messages.client.ClientMessage;
import com.creeptd.server.client.Client;

/**
 * Implements the basics for the different States of a client:
 * InGameState, AnonymousState and AuthenticatedState. 
 */
public abstract class AbstractClientState {

    private Client client;

    /**
     * Initiates the outQeue.
     * @param outQueue
     * Expects an BlockingQueue object with the ServerMessage.
     * @param client Client
     * @param authenticationService the AuthenticationService
     */
    public AbstractClientState(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("'client' was null");
        }
        this.client = client;
    }

    /**
     * Returns the Client.
     * @return Client
     */
    protected Client getClient() {
        return this.client;
    }

    /**
     * receive messages from Client.
     * @param message
     * Expects a ClientMessage.
     * @return ClientState
     */
    public abstract AbstractClientState receiveMessage(ClientMessage message);

    /**
     * Enter to this state.
     */
    public abstract void enter(AbstractClientState oldState);

    /**
     * Leave from this state.
     */
    public abstract void leave(AbstractClientState newState);
}

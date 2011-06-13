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

import com.creeptd.client.Core;
import org.apache.log4j.Logger;

import com.creeptd.common.IConstants;
import com.creeptd.common.messages.client.ClientMessage;
import com.creeptd.common.messages.client.LoginRequestMessage;
import com.creeptd.common.messages.client.RegistrationRequestMessage;
import com.creeptd.common.messages.client.ServerOnlineRequestMessage;
import com.creeptd.common.messages.server.LoginResponseMessage;
import com.creeptd.common.messages.server.RegistrationResponseMessage;
import com.creeptd.common.messages.server.ServerOnlineResponseMessage;
import com.creeptd.server.AuthenticationService;
import com.creeptd.server.Server;
import com.creeptd.server.client.Client;

/**
 * This class represents the state of the client when it is not yet logged in.
 * 
 * @author Bernd Hietler
 */
public class AnonymousState extends AbstractClientState {

    private static Logger logger = Logger.getLogger(AnonymousState.class);

    /**
     * Constructor.
     *
     * @param client
     *            Client
     */
    public AnonymousState(Client client) {
        super(client);
    }

    /**
     * Receiving messages from the client.
     *
     * @param message
     *            ClientMessage
     * @return ClientState
     */
    @Override
    public AbstractClientState receiveMessage(ClientMessage message) {
        if (message == null) {
            logger.info("Client " + this.getClient() + " disconnected in AnonymousState");
            return null;
        }
        if (message instanceof RegistrationRequestMessage) {
            RegistrationResponseMessage registrationResponseMessage = new RegistrationResponseMessage();
            registrationResponseMessage.setResponseType(AuthenticationService.create((RegistrationRequestMessage) message));
            this.getClient().send(registrationResponseMessage);
            return this;
        }
        if (message instanceof LoginRequestMessage) {
            LoginRequestMessage loginRequestMessage = (LoginRequestMessage) message;
            LoginResponseMessage loginResponseMessage = new LoginResponseMessage();
            String clientVersion = loginRequestMessage.getVersion();

            // Check Version
            if (!clientVersion.equals(Server.getVersion())) {
                logger.warn("client " + this.getClient() + " has wrong version: " + clientVersion);
                loginResponseMessage.setResponseType(IConstants.ResponseType.version);
                this.getClient().send(loginResponseMessage);
                return this;
            }

            // Check BanList
            if (AuthenticationService.isBanned(this.getClient(),
                    loginRequestMessage)) {
                logger.warn("blocked user try to login: " + loginRequestMessage.getUsername());
                loginResponseMessage.setResponseType(IConstants.ResponseType.failed);
                this.getClient().send(loginResponseMessage);
                return this;
            }

            // Check login
            if (!AuthenticationService.login(this.getClient(),
                    loginRequestMessage)) {
                loginResponseMessage.setResponseType(IConstants.ResponseType.failed);
                this.getClient().send(loginResponseMessage);
                return this;
            }
            loginResponseMessage.setResponseType(IConstants.ResponseType.ok);
            this.getClient().send(loginResponseMessage);
            return new AuthenticatedState(this.getClient(), this);

        } else if (message instanceof ServerOnlineRequestMessage) {
            ServerOnlineRequestMessage sor = (ServerOnlineRequestMessage) message;
            ServerOnlineResponseMessage sorm = new ServerOnlineResponseMessage();
            sorm.setCorrectVersion(Server.getVersion().equals(sor.getVersion()));
            this.getClient().send(sorm);
            return this;
        }

        logger.error("cannot handle message: " + message);
        return this;
    }

    @Override
    public void enter() {
        // do nothing
    }

    @Override
    public void leave() {
        // do nothing
    }
}

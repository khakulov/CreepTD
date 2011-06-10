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
package com.creeptd.server.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.creeptd.common.messages.client.ClientMessage;
import com.creeptd.common.messages.server.KickedMessage;
import com.creeptd.common.messages.server.ServerMessage;
import com.creeptd.server.client.states.AnonymousState;
import com.creeptd.server.client.states.AbstractClientState;
import com.creeptd.server.client.states.InGameState;
import com.creeptd.server.model.Player;

/**
 * This Class represents the instance of a client inside the server-package.
 * Every client has a ClientID, is in a GameState and is able to send or to
 * receive Messages. Adds also calculation of the clients current money that can
 * be used for cheating detection.
 * 
 */
public class Client {

    private static Logger logger = Logger.getLogger(Client.class);
    private static AtomicInteger clientCount = new AtomicInteger(0);
    private int clientID;
    private AbstractClientState clientState = null;
    private ClientInThread clientInThread;
    private ClientOutThread clientOutThread;
    private Socket socket;
    private Player playerModel = null;

    /**
     * Constructor ...
     *
     * @param id
     *            the client's ID is a simple integer.
     * @param socket
     *            the client's socket is a simple client-socket which is used to
     *            connect to the server.
     * @param authenticationService
     *            the AuthenticationService
     */
    public Client(Socket socket) {
        this.clientID = clientCount.incrementAndGet();
        this.socket = socket;
        logger.info("new client: " + this.clientID);
        try {
            this.clientOutThread = new ClientOutThread(socket.getOutputStream(), this);
            this.clientOutThread.start();
            this.clientInThread = new ClientInThread(socket.getInputStream(), this);
            this.clientInThread.start();

            this.changeState(new AnonymousState(this));
        } catch (IOException e) {
            logger.error("Problem with Client constructor in serverpacket.", e);
        }
    }

    /**
     * Receive a message from the client (over the network).
     *
     * @param message
     *            the message, or null if the connection to the client has been
     *            closed.
     */
    public void receive(ClientMessage message) {
        if (message == null) {
            this.disconnect();
            return;
        }
        message.setClientId(this.getClientID());
        this.changeState(this.clientState.receiveMessage(message));
    }

    /**
     * Send a message to the client (over the network).
     *
     * @param message
     *            the message
     */
    public void send(ServerMessage message) {
        this.clientOutThread.send(message);
        if ((this.clientState instanceof InGameState) && (message instanceof KickedMessage)) {
            this.changeState(((InGameState) this.clientState).getAuthenticatedState());
        }
    }

    /**
     * Change the current client state.
     *
     * @param newState new client state
     */
    private void changeState(AbstractClientState newState) {
        if (this.clientState == newState) {
            return;
        }

        if (this.clientState != null) {
            this.clientState.leave();
        }

        this.clientState = newState;

        if (this.clientState != null) {
            this.clientState.enter();
        }
    }

    public void disconnect() {
        logger.info("Client " + this.getClientID() + ": disconnecting... disconnect()");
        this.changeState(this.clientState.receiveMessage(null));
        this.clientInThread.terminate();
        this.clientOutThread.terminate();
        try {
            this.socket.close();
        } catch (IOException e) {
            String socketState;
            if (socket.isClosed()) {
                socketState = "closed";
            } else {
                socketState = "open";
            }
            logger.error("Disconnect client " + this + " failed. socket is " + socketState, e);
        }
    }

    /**
     * Wenn users socket closed ist, dann Killt alle Threads und liefert false
     * zuruck, sonst true
     *
     * @return boolean
     */
    public boolean check() {
        if (this.socket.isClosed() || !this.clientInThread.isAlive() || !this.clientOutThread.isAlive()) {
            logger.info("Client " + this + ": disconnecting... check()");
            this.disconnect();
            return false;
        }
        return true;
    }

    /**
     * Returns a string with id and userName.
     *
     * @return a string with id and userName.
     */
    @Override
    public String toString() {
        if (this.getPlayerModel() != null) {
            return this.clientID + "/" + this.getPlayerModel().getName();
        }
        return Integer.toString(this.clientID);
    }

    /**
     * Returns the client's ID.
     *
     * @return clientID
     */
    public int getClientID() {
        return this.clientID;
    }

    public String getIPAddress() {
        return this.socket.getInetAddress().toString().replaceAll("/", "");
    }

    public void setPlayerModel(Player player) {
        this.playerModel = player;
    }

    public Player getPlayerModel() {
        return playerModel;
    }
}

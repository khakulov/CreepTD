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
import com.creeptd.server.AuthenticationService;
import com.creeptd.server.client.states.AnonymousState;
import com.creeptd.server.client.states.AbstractClientState;
import com.creeptd.server.client.states.InGameState;
import com.creeptd.server.model.Player;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A server side client.
 * 
 * Every client has a ClientID, is in a GameState and is able to send or to
 * receive Messages.
 */
public class Client {
    /** Map of all connected clients, clientId => Client */
    public static final SortedMap<Integer,Client> allClients = new TreeMap<Integer,Client>();

    /** Logging functionality */
    private static Logger logger = Logger.getLogger(Client.class);
    /** Client coutner */
    private static AtomicInteger clientCount = new AtomicInteger(0);
    /** The client's id */
    private int clientId;
    /** The client's current state */
    private AbstractClientState clientState = null;
    /** Client in thread, reading messages */
    private ClientInThread clientInThread;
    /** Client out thread, writing messages */
    private ClientOutThread clientOutThread;
    /** The socket */
    private Socket socket;
    /** The player's database model */
    private Player playerModel = null;

    /**
     * Get a client by id.
     *
     * @param clientId The client's id
     * @return The client or null, if unknown
     */
    public static final Client findClientById(int clientId) {
        synchronized (allClients) {
            return allClients.get(clientId);
        }
    }

    /**
     * Get the number of connected clients.
     *
     * @return Number of connected clients
     */
    public static final int numClients() {
        synchronized (allClients) {
            return allClients.size();
        }
    }

    /**
     * Create a client.
     *
     * @param socket The client' socket
     */
    public Client(Socket socket) {
        this.clientId = clientCount.incrementAndGet();
        this.socket = socket;
        logger.info("New client: " + this.clientId);
        try {
            this.clientOutThread = new ClientOutThread(socket.getOutputStream(), this);
            this.clientOutThread.start();
            this.clientInThread = new ClientInThread(socket.getInputStream(), this);
            this.clientInThread.start();
            this.changeState(new AnonymousState(this));
        } catch (IOException e) {
            logger.error("Problem with Client constructor in serverpacket", e);
        }
        synchronized (allClients) {
            allClients.put(this.clientId, this);
        }
    }

    /**
     * Get the client's player name.
     *
     * @return The player name or null if not authenticated
     */
    public String getPlayerName() {
        if (this.playerModel != null) {
            return this.playerModel.getName();
        }
        return null;
    }

    /**
     * Receive a message from the client.
     *
     * @param message The recevied message or null, if disconnected
     */
    public void receive(ClientMessage message) {
        if (message == null) {
            this.disconnect();
            return;
        }
        message.setClientId(this.getId());
        this.changeState(this.clientState.receiveMessage(message));
    }

    /**
     * Send a message to the client.
     *
     * @param message The message to send
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
     * @param newState New client state
     */
    private void changeState(AbstractClientState newState) {
        // Do nothing if the state has not changed
        if ((this.clientState == null && newState == null) || (this.clientState != null && this.clientState.equals(newState))) {
            return;
        }
        // Leave the old state
        if (this.clientState != null) {
            this.clientState.leave();
        }
        this.clientState = newState;
        // Enter the new state
        if (this.clientState != null) {
            this.clientState.enter();
        }
    }

    /**
     * Disconnect the client.
     */
    public void disconnect() {
        logger.info("Client " + this.getId() + ": disconnecting... disconnect()");
        if (this.clientState != null) this.changeState(this.clientState.receiveMessage(null));
        this.clientInThread.terminate();
        this.clientOutThread.terminate();
        try {
            this.socket.close();
        } catch (IOException e) {
            // Uninteresting
        }
        synchronized (allClients) {
            allClients.remove(this.clientId);
        }
        if (this.getPlayerModel() != null) {
            AuthenticationService.logout(this);
        }
    }

    /**
     * Check this client.
     *
     * If the socket is closed, kill all threads.
     *
     * // FIXME: Required...why exactly? This should never happen if the code
     * // isn't bad.
     *
     * @return boolean false if socket is already closed was client is still alive, else true
     */
    public boolean doCheck() {
        if (this.socket.isClosed() || !this.clientInThread.isAlive() || !this.clientOutThread.isAlive()) {
            logger.info("Client " + this + ": disconnecting... doCheck()");
            this.disconnect();
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (this.getPlayerModel() != null) {
            return this.clientId + "/" + this.getPlayerModel().getName();
        }
        return "Client #"+Integer.toString(this.clientId);
    }

    /**
     * Returns the client's id.
     *
     * @return The client's id
     */
    public int getId() {
        return this.clientId;
    }

    /**
     * Get the client's ip address.
     *
     * @return The ip address
     */
    public String getIPAddress() {
        return this.socket.getInetAddress().toString().replaceAll("/", "");
    }

    /**
     * Set the player model.
     *
     * @param player The player instance
     */
    public void setPlayerModel(Player player) {
        this.playerModel = player;
    }

    /**
     * Get the player's database model.
     *
     * @return The player's database model
     */
    public Player getPlayerModel() {
        return playerModel;
    }
}

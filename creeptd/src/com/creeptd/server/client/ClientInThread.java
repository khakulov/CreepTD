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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.creeptd.common.Permission;
import com.creeptd.common.messages.client.ClientMessage;
import com.creeptd.common.messages.client.InvalidMessage;
import com.creeptd.common.messages.client.PongMessage;
import com.creeptd.common.messages.server.PingMessage;

import org.apache.log4j.Logger;

/**
 * Thread for process incoming messages from clients socket
 * 
 * @author Azim
 */
public class ClientInThread extends Thread {

    private static Logger logger = Logger.getLogger(ClientInThread.class.getName());
    private BufferedReader bufferedReader;
    private Client client;
    private volatile boolean terminate = false;
    private int inactivityCount = 0;

    /**
     * Create Thread for Clients input stream.
     *
     * @param inputStream
     *            Input stream from clients socket
     * @param client
     *            Client object
     */
    public ClientInThread(InputStream inputStream, Client client) {
        super();
        this.bufferedReader = new BufferedReader(new InputStreamReader(
                inputStream));
        this.client = client;

        this.setName("Client " + client.getClientID() + ": InThread");
    }

    /**
     * Start the thread.
     */
    @Override
    public void run() {
        boolean timeout = false;
        while (!this.terminate) {
            try {
                String messageString = this.bufferedReader.readLine();
                ClientMessage message = ClientMessage.renderMessageString(messageString);
                if (message instanceof InvalidMessage) {
                    logger.warn("Client " + this.client.getClientID() + ": received invalid message " + messageString);
                    continue;
                }
                if (message instanceof PongMessage) {
                    // logger.info("Client " + this.client.getClientID() +
                    // ": timeout -- received PONG");
                    timeout = false;
                    continue;
                }
                this.inactivityCount = 0;
                this.client.receive(message);
            } catch (SocketException e) {
                logger.warn("Client " + this.client.getClientID() + ": disconnected (Socket Exception)");
                this.client.disconnect();
            } catch (NullPointerException e) {
                logger.warn("Client " + this.client.getClientID() + ": disconnected (Connection closed)");
                this.client.disconnect();
            } catch (SocketTimeoutException e) {
                if (this.inactivityCount > 60) {
                    // Wenn der Client mehr als halbe Stunde nichts anders als
                    // Pingpong spielt...
                    // 30sek * 60 = 0.5 h
                    logger.warn("Client " + this.client.getClientID() + ": more than half our inactive");
                    if ((this.client.getPlayerModel() == null) || (!this.client.getPlayerModel().hasPermission(
                            Permission.NO_TIMEOUT))) {
                        this.client.disconnect();
                    }
                }
                if (!timeout) {
                    // logger.info("Client " + this.client.getClientID() +
                    // ": timeout -- sending PING");
                    timeout = true;
                    this.client.send(new PingMessage());
                    this.inactivityCount++;
                    continue;
                }
                logger.warn("Client " + this.client.getClientID() + ": timeout -- got no response to PING");
                this.client.disconnect();
            } catch (IOException e) {
                logger.warn("IO-error: " + e);
            }
        }
    }

    /**
     * Terminate the thread.
     */
    public synchronized void terminate() {
        this.terminate = true;
        this.interrupt();
    }
}

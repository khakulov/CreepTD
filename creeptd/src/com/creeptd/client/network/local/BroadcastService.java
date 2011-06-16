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
package com.creeptd.client.network.local;

import com.creeptd.client.Core;
import com.creeptd.common.messages.Message;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Broadcast service.
 *
 * Sends and receives encapsulated BroadcastMessages.
 *
 * @author Daniel
 */
public class BroadcastService extends Thread {
    private static final String MCAST_ADDR = "235.1.1.1";
    private static final int DEST_PORT = 4748;
    private static final int BUFFER_SIZE = 1024;
    private final List<BroadcastListener> listeners = new ArrayList<BroadcastListener>();
    private boolean running = true;
    private MulticastSocket socket;
    
    /**
     * Create BroadcastService.
     */
    public BroadcastService() {
        try {
            this.socket = new MulticastSocket(DEST_PORT);
            this.socket.joinGroup(InetAddress.getByName(MCAST_ADDR));
        } catch (Exception ex) {
            Core.getLogger().warning("Unable to create broadcast service: "+ex);
        }
        this.start();
    }

    /**
     * Send a broadcast message.
     *
     * @param message The message to send
     * @return true on success, else false
     */
    public synchronized boolean send(String message) {
        byte[] b = new byte[message.getBytes().length+1];
        System.arraycopy(message.getBytes(), 0, b, 0, message.getBytes().length);
        b[b.length-1] = 0; // Terminate data with null byte
        try {
            DatagramPacket packet = new DatagramPacket(b, b.length, InetAddress.getByName(MCAST_ADDR), DEST_PORT);
            socket.send(packet);
            return true;
        } catch (Exception ex) {
            Core.getLogger().warning("Unable to send broadcast: "+ex);
        }
        return false;
    }

    /**
     * Send a broadcast message.
     *
     * @param message The message to send
     * @return true on success, else false
     */
    public boolean send(Message message) {
        return send(message.toString());
    }

    /**
     * Receive the next broadcast message.
     *
     * This method will block until a message is received.
     *
     * @return The received message
     * @throws IOException
     */
    private BroadcastMessage receive() throws IOException {
        byte[] b = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(b, b.length);
        socket.receive(packet);
        packet.setLength(b.length);
        int len = 0;
        for (int i=0; i<b.length; i++) {
            if (b[i] == 0) {
                len = i;
            }
        }
        String message = new String(b, 0, len);
        String ip = ((InetSocketAddress) packet.getSocketAddress()).getAddress().getHostAddress();
        return new BroadcastMessage(ip, message);
    }

    /**
     * Internal run method.
     */
    @Override
    public void run() {
        while (this.running) {
            try {
                BroadcastMessage bcm = this.receive();
                synchronized (this.listeners) {
                    Iterator<BroadcastListener> i = this.listeners.iterator();
                    while (i.hasNext()) {
                        i.next().receive(bcm);
                    }
                }
            } catch (Exception ex) {
                Core.getLogger().warning("Cannot receive broadcast message: "+ex+" (Exiting)");
                this.running = false;
            }
        }
    }

    /**
     * Stop listener thread.
     */
    public void end() {
        this.running = false;
    }

    /**
     * Add a listener.
     *
     * @param listener The listener
     * @return true on success, else, if it already exists, false
     */
    public boolean addListener(BroadcastListener listener) {
        synchronized (this.listeners) {
            if (this.listeners.contains(listener)) {
                return false;
            }
            this.listeners.add(listener);
            return true;
        }
    }

    /**
     * Remove a listener.
     *
     * @param listener The listener to remove
     * @return true on success, else, if it did not exist, false
     */
    public boolean removeListener(BroadcastListener listener) {
        synchronized (this.listeners) {
            return this.listeners.remove(listener);
        }
    }
}

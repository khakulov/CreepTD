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
package com.creeptd.client.network;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.creeptd.client.Core;
import com.creeptd.common.messages.Message;
import com.creeptd.common.messages.client.ClientMessage;
import com.creeptd.common.messages.server.GameMessage;
import com.creeptd.common.messages.server.ServerMessage;
import java.net.ConnectException;

/**
 * Networkclass that handles the network communication on clientside.
 * 
 * @author fabian
 * 
 */
public class Network implements MessageSubject {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private String host;
    private int port;
    private Core core;
    private Socket socket;
    private PrintWriter out;
    private String macaddress = null;
    private ClientWatcher clientWatcher = null;
    private List<GameMessage> queue;
    // max Round the gameloop is allowed to go
    private long maxRound = 0;
    // List of all Listener for nongame messages
    private ArrayList<MessageListener> listeners;
    private ArrayList<MessageListener> remlisteners;
    private ArrayList<MessageListener> addlisteners;
    private boolean connected = false;

    /**
     * Constructor of Network.
     *
     * @param host
     * @param port
     * @param core
     *
     */
    public Network(String host, int port, Core core) {
        this.core = core;
        this.host = host;
        this.port = port;

        this.queue = Collections.synchronizedList(new ArrayList<GameMessage>());
        this.listeners = new ArrayList<MessageListener>();
        this.remlisteners = new ArrayList<MessageListener>();
        this.addlisteners = new ArrayList<MessageListener>();
    }

    /**
     * Creates a new network instance.
     *
     * @param host
     *            the Host
     * @param port
     *            the Port
     */
    public Network(String host, int port) {
        this.host = host;
        this.port = port;
        this.queue = Collections.synchronizedList(new ArrayList<GameMessage>());
        this.listeners = new ArrayList<MessageListener>();
        this.remlisteners = new ArrayList<MessageListener>();
        this.addlisteners = new ArrayList<MessageListener>();
    }

    /**
     * contact the server.
     */
    public boolean makeContact() {
        try {
            if (connected) {
                return true;
            }
            logger.info("Connecting to " + host + ":" + port);
            this.socket = new Socket(host, port);

            this.out = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream(), Charset.forName("UTF-8")), true);

            // wait for messages
            this.clientWatcher = new ClientWatcher(this, this.socket.getInputStream());
            this.clientWatcher.start();
            this.connected = true;
            this.macaddress = byteArrayToHexString(NetworkInterface.getByInetAddress(this.socket.getLocalAddress()).getHardwareAddress());
            if (this.macaddress == null) {
                this.macaddress = String.valueOf((int) Math.round(Math.random() * 10000000) + 1000000);
            }
            // logger.info("MAC: " + this.macaddress);
        } catch (ConnectException e) {
            return false;
        } catch (UnknownHostException e) {
            // errorDialog("Couldn't find specified host!");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // errorDialog("Couldn't connect to host, please check your internet connection!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Closes the connection to the server.
     */
    public void shutdown() {
        if (!this.connected) {
            return;
        }
        this.connected = false;
        this.out.close();
        this.clientWatcher.shutdown();
    }

    /**
     * Method to send ClientMessages to the server.
     *
     * @param message ClientMessage
     */
    public void sendMessage(ClientMessage message) {
        if (message != null) {
            this.sendMessage(message.getMessageString());
        }
    }

    /**
     * Method to send messages to the server.
     *
     * @param message Message
     */
    public void sendMessage(Message message) {
        this.sendMessage(message.getMessageString());
    }

    /**
     * Internal method to send String messages to the server.
     *
     * @param message The message
     */
    private void sendMessage(String message) {
        if (message != null && !message.equals("")) {
            this.out.println(message);
            logger.info("Sent: " + message);
        }
    }

    /**
     * @return the maxRound
     */
    public long getMaxRound() {
        return this.maxRound;
    }

    /**
     * @param maxRound
     *            the maxRound to set
     */
    public void setMaxRound(long maxRound) {
        this.maxRound = maxRound;
    }

    /**
     * @return the queue
     */
    public List<GameMessage> getQueue() {
        return this.queue;
    }

    /**
     * add a GameMessage to the queue.
     *
     * @param gm
     *            the GameMessage to add to the queue
     */
    public void addGameMessage(GameMessage gm) {
        this.queue.add(gm);
    }

    /**
     * add listener for messages.
     *
     * @param messageListener
     *            messageListener
     */
    public void addListener(MessageListener messageListener) {
        synchronized (listeners) {
            this.addlisteners.add(messageListener);
        }
    }

    /**
     * sends the message to all listeners.
     *
     * @param message
     *            the message object to be notified of
     */
    public synchronized void notifyListeners(final ServerMessage message) {
        synchronized (listeners) {
            for (MessageListener ml : this.addlisteners) {
                this.listeners.add(ml);
            }
            this.addlisteners.clear();

            for (MessageListener ml : this.remlisteners) {
                this.listeners.remove(ml);
            }
            this.remlisteners.clear();

            for (final MessageListener ml : this.listeners) {
                ml.update(message);
            }
        }
    }

    /**
     * removes a listener.
     *
     * @param messageListener
     *            messageListener
     */
    public void removeListener(MessageListener messageListener) {
        synchronized (listeners) {
            this.remlisteners.add(messageListener);
        }
    }

    /**
     * returns the HOST Name.
     *
     * @return HOST
     */
    public String getHost() {
        return this.host;
    }

    /**
     * returns the Port.
     *
     * @return PORT
     */
    public int getPort() {
        return this.port;
    }

    /**
     * returns whether connected or not.
     *
     * @return true if connected
     */
    public boolean isConnected() {
        return this.connected;
    }

    /**
     * @param sHost
     *            the hOST to set
     */
    public void setHost(String sHost) {
        host = sHost;
    }

    /**
     * @param sPort
     *            the pORT to set
     */
    public void setPort(int sPort) {
        port = sPort;
    }

    /**
     * Dialog to show errors in the same colours than GUI.
     *
     * @param msg
     *            msg
     */
    public void errorDialog(String msg) {

        if (isThisHostknownToHaveNoGUI()) {
            return;
        }

        UIManager.put("OptionPane.background", Color.BLACK);
        UIManager.put("Panel.background", Color.BLACK);
        UIManager.put("OptionPane.messageForeground", Color.GREEN);
        JOptionPane.showMessageDialog(null, msg, "error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * getter.
     *
     * @return core
     */
    public Core getCore() {
        return core;
    }

    /**
     * setter.
     *
     * @param core
     *            core
     */
    public void setCore(Core core) {
        this.core = core;
    }

    /**
     * GUI conditions for the host.
     *
     * @return true or false
     */
    public boolean isThisHostknownToHaveNoGUI() {
        java.net.InetAddress i = null;
        try {
            i = java.net.InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            return true;
        }

        if (i.getHostName().contains("lvis03")) {
            return true;
        }

        return false;
    }

    /**
     * Convert a byte[] array to readable string format. This makes the "hex"
     * readable!
     *
     * @return result String buffer in String format
     * @param in
     *            byte[] buffer to convert to string format
     */
    static String byteArrayToHexString(byte in[]) {
        byte ch = 0x00;
        int i = 0;
        if (in == null || in.length <= 0) {
            return null;
        }
        String pseudo[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "B", "C", "D", "E", "F"};
        StringBuffer out = new StringBuffer(in.length * 2);

        while (i < in.length) {
            ch = (byte) (in[i] & 0xF0); // Strip off high nibble
            ch = (byte) (ch >>> 4); // shift the bits down
            ch = (byte) (ch & 0x0F); // must do this is high order bit is on!
            out.append(pseudo[(int) ch]); // convert the nibble to a String
            // Character
            ch = (byte) (in[i] & 0x0F); // Strip off low nibble
            out.append(pseudo[(int) ch]); // convert the nibble to a String
            // Character
            out.append("-"); // add "-" for separating bytes
            i++;
        }
        out.deleteCharAt(out.length() - 1); // remove last "-"
        String rslt = new String(out);
        return rslt;
    }

    /**
     * @return the macaddress
     */
    public String getMACAddress() {
        return macaddress;
    }
}

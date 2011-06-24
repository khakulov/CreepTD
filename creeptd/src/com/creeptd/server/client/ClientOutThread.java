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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.creeptd.common.messages.server.ServerMessage;

public class ClientOutThread extends Thread {

    private static Logger logger = Logger.getLogger(ClientOutThread.class.getName());
    private PrintWriter printWriter;
    private Client client;
    private volatile boolean terminate = false;
    private BlockingQueue<ServerMessage> queue;

    public ClientOutThread(OutputStream outputStream, Client client) {
        super();
        this.printWriter = new PrintWriter(new OutputStreamWriter(outputStream, Charset.forName("UTF-8")), true);
        this.client = client;
        this.queue = new LinkedBlockingQueue<ServerMessage>();

        this.setName("Client " + client.getId() + ": OutThread");
    }

    @Override
    public void run() {
        while (!this.terminate) {
            try {
                ServerMessage message = queue.take();
                if (message == null) {
                    logger.warn("Client " + this.client.getId() + ": disconnected... null");
                    this.client.disconnect();
                } else {
                    // logger.info("Send to "+this.client+": " + message.getMessageString());
                    this.printWriter.println(message.getMessageString());
                    this.printWriter.flush();
                }
            } catch (InterruptedException e) {
                //do nothing
            }
        }
    }

    public void send(ServerMessage message) {
        this.queue.add(message);
    }

    public synchronized void terminate() {
        this.terminate = true;
        this.interrupt();
    }
}


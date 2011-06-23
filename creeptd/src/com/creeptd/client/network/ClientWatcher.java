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

import com.creeptd.client.Core;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.creeptd.client.panel.LoginPanel;
import com.creeptd.common.messages.client.PongMessage;
import com.creeptd.common.messages.server.GameMessage;
import com.creeptd.common.messages.server.PingMessage;
import com.creeptd.common.messages.server.ServerMessage;

import static com.creeptd.client.i18n.Translator.*;

/**
 * Watches for incomming messages form the Server.
 * @author fabian
 *
 */
public class ClientWatcher extends Thread {

    private static Logger logger = Logger.getLogger(
            ClientWatcher.class.getName());
    private Network network;
    private InTranslator inTrans;
    private InputStream in;
    private boolean interrupt = false;

    /**
     * Creates a new instance of ClientWatcher.
     * @param n Client
     * @param in BufferedReader
     */
    public ClientWatcher(Network n, InputStream in) {
        this.network = n;
        this.in = in;
        this.inTrans = new InTranslator(in);
    }

    /**
     * run method.
     */
    @Override
    public void run() {
        while (!interrupt) {

            try {
                final ServerMessage m = inTrans.getNextMessage();

                if (m instanceof GameMessage) {
                    network.addGameMessage((GameMessage) m);
                } else if (m instanceof PingMessage) {
                    network.sendMessage(new PongMessage());
                } else {
                    network.notifyListeners(m);
                }

            } catch (IOException e) {
                this.shutdown();
                logger.warning(e.getMessage());
                if (e.getMessage().contains("reset")) {
                    errorHandling();
                }
            } catch (NullPointerException e) {
                this.shutdown();
                logger.info("Nullpointer detected");
                e.printStackTrace();
                errorHandling();
            }

        }
        try {
            in.close();
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
    }

    /**
     * Stops reading from the server.
     */
    public void shutdown() {
        this.interrupt = true;
    }

    /**
     * Method to handle the error exceptions.
     */
    public void errorHandling() {
        String[] options = {_("OK")};
        UIManager.put("OptionPane.background", Color.BLACK);
        UIManager.put("Panel.background", Color.BLACK);
        UIManager.put("OptionPane.messageForeground", Color.GREEN);
        JOptionPane.showOptionDialog(null, _("Connection aborted."), _("An error occured"),
                JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);
        this.network.shutdown();
        Core.getInstance().clearScreen();
        Core.getInstance().pushScreen(new LoginPanel());
    }
}

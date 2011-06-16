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
package com.creeptd.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;

import com.creeptd.common.IConstants;
import com.creeptd.server.client.Client;

/**
 * The class Server creates a ServerSocket for the Server. It also contains the
 * Method used to instantiate new Clients.
 *
 * @author Bernd Hietler
 *
 */
public class Server {

    private static Logger logger = Logger.getLogger(Server.class.getName());
    private static String configFile;
    private static Map<String, String> configDB;

    /**
     * It is possible to start the server with a different portnumber then the
     * defaultport.
     *
     * @param args
     *            args[] = port
     */
    public static void main(String[] args) {

        //actice client-sockets
        Set<Socket> activeSockets = new HashSet<Socket>();

        // init the log4J logger
        initLogger();

        logger.info("Starting server version " + getVersion() + "...");

        // check if working directory is writeable
        if (!new File("./").canWrite()) {
            logger.error("working directory was not writeable.");
            System.exit(1);
        }

        // load entity manager, this could take some time :-)
        logger.info("Loading database configuration...");
        setConfigFile("configSQL.xml");
        try {
            readConfigDB();
        } catch (Exception ex) {
            logger.error("Unable to read config file: "+ex);
            ex.printStackTrace();
            System.exit(0);
        }

        int gameServerPort = IConstants.DEFAULT_SERVER_PORT;
        int maxClients = IConstants.DEFAULT_MAX_CLIENTS;

        logger.info("Parsing runtime arguments...");
        if (args.length >= 1) {
            try {
                gameServerPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.error("parameter for gameserver-port was nut a number");
                System.exit(1);
            }
        }
        if (args.length >= 2) {
            try {
                maxClients = Integer.valueOf(args[1]);
            } catch (NumberFormatException e) {
                logger.error("parameter for max clients was nut a number");
                System.exit(1);
            }
        }

        // Instantiate sockets + clients
        try {
            logger.info("Starting server on port "+gameServerPort+"...");
            ServerSocket serverSocket = new ServerSocket(gameServerPort);

            while (true) {
                for (Socket socket : new HashSet<Socket>(activeSockets)) {
                    if (socket.isClosed() || !socket.isConnected()) {
                        activeSockets.remove(socket);
                    }
                }
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(IConstants.TIMEOUT);
                activeSockets.add(socket);
                if (activeSockets.size() >= maxClients) {
                    socket.close();
                    logger.error("Number of max clients reached");
                } else {
                    new Client(socket);
                }
            }

        } catch (BindException e) {
            logger.error("Cannot start server: Port " + gameServerPort + " is already in use");
            System.exit(1);
        } catch (IOException e) {
            logger.error("Cannot start server", e);
            e.printStackTrace();
        }
    }

    /**
     * initialize logger.
     */
    private static void initLogger() {
        // load log4j configuration
        try {
            InputStream is = Server.class.getClassLoader().getResourceAsStream("com/creeptd/server/server_log4j.xml");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            DOMConfigurator.configure(doc.getDocumentElement());
        } catch (ParserConfigurationException e) {
            System.err.println("Could not read log4j configuration: " + e.getLocalizedMessage());
        } catch (org.xml.sax.SAXException e) {
            System.err.println("Could not read log4j configuration: " + e.getLocalizedMessage());
        } catch (IOException e) {
            System.err.println("Could not read log4j configuration: " + e.getLocalizedMessage());
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setConfigFile(String configFile) {
        Server.configFile = configFile;
    }

    public static void setConfigDB(Map<String, String> ConfigDB) {
        Server.configDB = ConfigDB;
    }

    public static Map<String, String> getConfigFileDB() {

        return Server.configDB;

    }

    public static void readConfigDB() throws Exception {

        try {

            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(configFile);
            if (in.available() == 0) {
                throw(new Exception("File not found or empty"));
            }
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            Map<String, String> newConfig = new HashMap<String, String>();

            while (eventReader.hasNext()) {

                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {

                    String ConfigSetting = "";
                    if (event.asStartElement().getName().getLocalPart() == ("config") || event.asStartElement().getName().getLocalPart() == ("root")) {
                        event = eventReader.nextTag();

                    }

                    String ConfigKey = event.asStartElement().getName().getLocalPart();
                    event = eventReader.nextEvent();

                    if (event.isEndElement()) {
                        newConfig.put(ConfigKey, null);
                        logger.info("Config: " + ConfigKey + " (not set)");
                    } else {
                        try {
                            ConfigSetting = event.asCharacters().getData();
                            newConfig.put(ConfigKey, ConfigSetting);
                            logger.info("Config: " + ConfigKey + "=" + ConfigSetting);
                        } catch (Exception ex) {
                            logger.warn("Parse error", ex);
                        }
                    }
                }
            }

            setConfigDB(newConfig);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the server version
     */
    public static String getVersion() {
        String version = null;

        InputStream inStream = Server.class.getResourceAsStream("../common/version");
        try {
            if (inStream.available() > 0) {
                InputStreamReader inStreamReader = new InputStreamReader(inStream);
                BufferedReader reader = new BufferedReader(inStreamReader);
                version = reader.readLine();
            }
        } catch (Exception e) {
            logger.warn("Caught exception while finding out version", e);
        }

        if (version == null) {
            version = "-unknown-";
        }

        return version;
    }

    /**
     * Check if this is a LAN only version.
     *
     * @return true if LAN version, else false
     */
    public static boolean isLANVersion() {
        return getVersion().indexOf("LAN") >= 0;
    }
}

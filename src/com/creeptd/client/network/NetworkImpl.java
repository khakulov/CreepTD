package com.creeptd.client.network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import com.creeptd.common.messages.client.ClientMessage;
import java.net.ConnectException;

/**
 * Networkclass that handles the network communication on clientside.
 * 
 */
public class NetworkImpl extends AbstractNetwork {

    private Logger logger = Logger.getLogger("com.creeptd.client.network");

    private String host;
    private int port;

    private Socket socket;

	private InThread inThread = null;
	private OutThread outThread = null;

    private boolean connected = false;

    /**
     * Constructor of Network.
     *
     * @param host
     * @param port
     */
    public NetworkImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * contact the server.
     */
    @Override
    public boolean connect() {
        try {
            if (connected) {
                return true;
            }
            logger.info("Connecting to " + host + ":" + port);
            this.socket = new Socket(host, port);

            // wait for messages
            this.inThread = new InThread(this.socket.getInputStream());
            this.inThread.start();
            this.outThread = new OutThread(this.socket.getOutputStream());
            this.outThread.start();

            this.connected = true;

        } catch (ConnectException e) {
            return false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Closes the connection to the server.
     */
    @Override
    public void disconnect() {
        if (!this.connected) {
            return;
        }
        this.connected = false;

        this.inThread.terminate();
        this.outThread.terminate();

		try {
			this.socket.close();
		} catch (IOException e) {
			logger.warning("Network disconnect failed. Socket is "
					+ (this.socket.isClosed()? "closed.": "open."));
		}
    }

    /**
     * Method to send ClientMessages to the server.
     *
     * @param message ClientMessage
     */
    @Override
    public void sendMessage(ClientMessage message) {
        this.outThread.send(message); 
    }
}

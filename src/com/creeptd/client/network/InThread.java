package com.creeptd.client.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import com.creeptd.client.Core;
import com.creeptd.common.messages.client.PongMessage;
import com.creeptd.common.messages.server.PingMessage;
import com.creeptd.common.messages.server.ServerMessage;

/**
 * Watches for incomming messages form the Server.
 */
public class InThread extends Thread {
    private Logger logger = Logger.getLogger("com.creeptd.client.network");

	private BufferedReader bufferedReader;
	private volatile boolean terminate;

	/**
	 * Creates a new instance of ClientWatcher.
	 * 
	 * @param n
	 *            Client
	 * @param in
	 *            BufferedReader
	 */
	public InThread(InputStream inputStream) {
		super();
		this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
		this.setName("InThread");
	}

	/**
	 * run method.
	 */
	public void run() {
		this.terminate = false;
		while (!this.terminate) {
			try {
				String messageString = this.bufferedReader.readLine();
				logger.info("RECEIVED: " + messageString);
				ServerMessage message = ServerMessage.renderMessageString(messageString);

				if (message == null) {
					logger.warning("received invalid message " + messageString);
				} else if (message instanceof PingMessage) {
					Core.getInstance().getNetwork().sendMessage(new PongMessage());
				} else {
					Core.getInstance().getNetwork().notifyListeners(message);
				}

			} catch (IOException e) {
				Core.getInstance().getNetwork().disconnect();
				logger.warning(e.getMessage());
			}
		}
	}

	/**
	 * Stops reading from the server.
	 */
	public synchronized void terminate() {
		this.terminate = true;
		this.interrupt();
	}
} 
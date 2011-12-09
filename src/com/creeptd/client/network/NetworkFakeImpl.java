package com.creeptd.client.network;

import java.util.Deque;
import java.util.logging.Logger;

import com.creeptd.client.Core;
import com.creeptd.common.messages.client.ClientMessage;
import com.creeptd.common.messages.server.ServerMessage;

public class NetworkFakeImpl extends AbstractNetwork {

    private Logger logger = Logger.getLogger("com.creeptd.client.network");
    private Deque<ServerMessage> messages;

    public NetworkFakeImpl(Deque<ServerMessage> messages) {
        this.messages = messages;
    }

	@Override
	public boolean connect() {
		logger.info("Fake connected");

    	while (!this.messages.isEmpty()) {
    		ServerMessage message = this.messages.pollLast();
    		Core.getInstance().getNetwork().notifyListeners(message);
    	}

		return true;
	}

	@Override
	public void disconnect() {
		logger.info("Fake disconnected");
	}

	@Override
	public void sendMessage(ClientMessage message) {
		logger.info("Fake SEND: " + message.getMessageString());
	}

}

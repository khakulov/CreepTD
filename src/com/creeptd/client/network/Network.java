package com.creeptd.client.network;

import com.creeptd.common.messages.client.ClientMessage;
import com.creeptd.common.messages.server.ServerMessage;


public interface Network {

    /**
     * contact the server.
     */
    public boolean connect();

    /**
     * Closes the connection to the server.
     */
    public void disconnect();

    /**
     * Method to send ClientMessages to the server.
     *
     * @param message ClientMessage
     */
    public void sendMessage(ClientMessage message);

    /**
     * add listener for messages.
     *
     * @param messageListener
     *            messageListener
     */
    public void addListener(MessageListener messageListener);

    /**
     * sends the message to all listeners.
     *
     * @param message
     *            the message object to be notified of
     */
    public void notifyListeners(ServerMessage message);

    /**
     * removes a listener.
     *
     * @param messageListener
     *            messageListener
     */
    public void removeListener(MessageListener messageListener);


}

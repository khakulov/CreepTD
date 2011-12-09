package com.creeptd.client.network;

import java.util.ArrayList;
import java.util.List;

import com.creeptd.common.messages.server.ServerMessage;

public abstract class AbstractNetwork implements Network {

    /** List of all Listener for server messages */
	private List<MessageListener> listeners;

    public AbstractNetwork() {
        this.listeners = new ArrayList<MessageListener>();
    }

    /**
     * add listener for messages.
     *
     * @param messageListener
     *            messageListener
     */
    @Override
    public void addListener(MessageListener messageListener) {
        synchronized (this.listeners) {
        	if (this.listeners.contains(messageListener))
				return;
        	this.listeners.add(messageListener);
        }
    }

    /**
     * sends the message to all listeners.
     *
     * @param message
     *            the message object to be notified of
     */
    @Override
    public void notifyListeners(final ServerMessage message) {
    	List<MessageListener> list = new ArrayList<MessageListener>();
		synchronized (this.listeners) {
			for (MessageListener messageListener : this.listeners) {
				list.add(messageListener);
			}
		}
		for (MessageListener messageListener : list) {
			messageListener.update(message);
		}
    }

    /**
     * removes a listener.
     *
     * @param messageListener
     *            messageListener
     */
    @Override
    public void removeListener(MessageListener messageListener) {
        synchronized (this.listeners) {
        	if (this.listeners.contains(messageListener))
        		this.listeners.remove(messageListener); 
        }
    }

}

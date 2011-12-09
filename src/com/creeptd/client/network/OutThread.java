package com.creeptd.client.network;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.creeptd.client.Core;
import com.creeptd.common.messages.client.ClientMessage;

public class OutThread  extends Thread {
    private Logger logger = Logger.getLogger("com.creeptd.client.network");

	private PrintWriter printWriter;
	private volatile boolean terminate;
	private BlockingQueue<ClientMessage> queue;

	public OutThread(OutputStream outputStream) {
		super();
		OutputStreamWriter osw = new OutputStreamWriter(outputStream, Charset.forName("UTF-8"));
		this.printWriter = new PrintWriter(new BufferedWriter(osw));
		this.queue = new LinkedBlockingQueue<ClientMessage>();
		this.setName("OutThread");
	}

	@Override
	public void run() {
		this.terminate = false;
		while (!this.terminate) {
			try {
				ClientMessage message = queue.take();
				if (message == null) {
					Core.getInstance().getNetwork().disconnect();
				} else {
					logger.info("SEND: " + message.toString());
					this.printWriter.println(message.toString());
					this.printWriter.flush();
				}
			} catch (InterruptedException e) {
				//do nothing
			}
		}
	}
	
	public void send(ClientMessage message) {
		this.queue.add(message);
	}

	public synchronized void terminate() {
		this.terminate = true;
		this.interrupt();
	}
}

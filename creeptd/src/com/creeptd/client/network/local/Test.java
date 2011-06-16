/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.creeptd.client.network.local;

/**
 *
 * @author Daniel
 */
public class Test extends Thread implements BroadcastListener {
    private BroadcastService bcs;
    private boolean running = true;

    public static void main(String[] args) {
        new Test();
    }
    
    public Test() {
        bcs = new BroadcastService();
        bcs.addListener(this);
        this.start();
    }

    public void run() {
        while (this.running) {
            System.out.println("Sending test message");
            bcs.send("Test");
            try {
                Thread.sleep(5000);
            } catch (Exception ex) { }
        }
    }

    public void receive(BroadcastMessage bcm) {
        System.out.println("Received message from "+bcm.getIP()+": "+bcm.getMessage());
    }
}

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
package com.creeptd.server.game;

/**
 * A thread that periodically calls a TickReceiver instance's {@link
 * TickReceiver#tick tick} method.
 */
public class TickThread extends Thread {

    private volatile boolean terminate;
    private TickReceiver receiver;
    private long interval;

    /**
     * An object that receives 'ticks'.
     */
    public interface TickReceiver {

        /**
         * Called periodically by TickThread.
         */
        void tick();
    }

    /**
     * Create the thread.
     *
     * @param receiver
     *            the TickReceiver instance
     * @param interval
     *            the interval (in milliseconds) between two ticks.
     */
    public TickThread(TickReceiver receiver, long interval) {
        this.terminate = false;
        this.receiver = receiver;
        this.interval = interval;
    }

    /**
     * Start the thread.
     */
    @Override
    public void run() {
        long beforeTime = System.nanoTime();
        long afterTime, sleepTime, timeDiff;
        long overSleepTime = 0L;
        long excess = 0L;

        while (!this.terminate) {
            this.receiver.tick();
            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (this.interval - timeDiff) - overSleepTime;
            if (sleepTime > 0) { // some time left in this cycle
                try {
                    Thread.sleep(sleepTime / 1000000L); // nano -> ms
                } catch (InterruptedException ex) {
                }
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            } else { // sleepTime <= 0; frame took longer than the period
                excess -= sleepTime; // store excess time value
                overSleepTime = 0L;
            }

            beforeTime = System.nanoTime();

            while (excess > this.interval) {
                excess -= this.interval;
                this.receiver.tick();
            }
        }
    }

    /**
     * Ask the thread to terminate gracefully.
     */
    public void terminate() {
        this.terminate = true;
        this.interrupt();
    }
}

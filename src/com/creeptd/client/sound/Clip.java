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
package com.creeptd.client.sound;

import java.applet.AudioClip;
import java.util.Date;

/**
 * An audio clip.
 *
 * This class aims to provide some of the functionality of a java sampled clip
 * which unfortunately is not available for applets (SecurityException).
 *
 * @author Daniel
 */
public class Clip {
    private static ClipRunner clipRunner = new ClipRunner();
    private AudioClip clip;
    private int duration;
    private long lastplayedAt = 0;

    /**
     * Create a new Clip.
     *
     * @param clip The underlying AudioClip
     * @param duration The AudioClip's duration
     */
    public Clip(AudioClip clip, int duration) {
        this.clip = clip;
        this.duration = duration;
    }
    
    /**
     * Set the current frame position inside the clip.
     *
     * @param position The frame position
     */
    public void setFramePosition(int position) {
        // Unused
    }

    /**
     * Start playback of this clip.
     */
    public void start() {
        synchronized (this) {
            this.lastplayedAt = new Date().getTime();
            clipRunner.play(this); // clip.play();
        }
    }

    /**
     * Play the underlying clip inside the ClipRunner.
     */
    protected void playUnderlyingClip() {
        clip.play();
    }

    /**
     * Test if this clip is currently active.
     *
     * @return true if active, else false
     */
    public boolean isActive() {
        synchronized (this) {
            long now = new Date().getTime();
            return this.lastplayedAt > 0 && now < this.lastplayedAt + this.duration;
        }
    }

    /**
     * Test if this clip is currently running.
     *
     * @return true if running, else false
     */
    public boolean isRunning() {
        return this.isActive();
    }
}

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

/**
 * Soundclass for sound.
 */
public class Sound implements ISound, Runnable {

    private AudioClip sndc;
    private boolean playAsLoop = false;

    /**
     * Creates instance of sounds.
     * @param s gets kind of sound
     */
    public Sound(AudioClip s) {
        sndc = s;
    }

    /**
     * Creates instance of sounds.
     * @param s gets kind of sound
     * @param asLoop if started, it will play sound as loop
     */
    public Sound(AudioClip s, boolean asLoop) {
        sndc = s;
        playAsLoop = asLoop;
    }

    /**
     *
     * @param clip clip
     */
    public void playLoop(AudioClip clip) {
        clip.loop();
    }

    /**
     * {@inheritDoc}
     */
    public void play(AudioClip clip) {
        clip.play();
    }

    /**
     * plays Soundfile.
     */
    public void run() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        if (playAsLoop) {
            this.playLoop(sndc);
        } else {
            this.play(sndc);
        }

        try {
            Thread.sleep(100, 1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}





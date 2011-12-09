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

import com.creeptd.common.Constants;
import java.applet.Applet;
import java.applet.AudioClip;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * A sound.
 *
 * Unfortunately, we cannot use javax.sound inside of applets, so I decided to
 * emulate the required behaviour based on parsing WAVE file headers and
 * using applet.AudioClip instead.
 *
 * @author Daniel
 */
public class Sound {

    /** The minimum delay between two calls to play() in milliseconds */
    private static int MINIMUM_DELAY = 100;

    /** Clip objects (n = maximum concurrency) */
    private final Clip[] clips;

    /** The last played time in milliseconds */
    private long lastplayedAt = 0;

    /**
     * Load the underlying sound clip.
     *
     * Unfortunately, this functionality is not available for applets. See below
     * for the emulated version.
     *
     * @param filename The clip's file name
     * @return The clip or null on failure
     */
    /* private static Clip loadClip(String filename) {
        try {
            // Load the clip stream and determine its format
            URL soundURL = Sound.class.getClassLoader().getResource(Constants.SOUNDS_URL+filename);
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File(soundURL.getFile()));
            AudioFormat format = stream.getFormat();
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                format = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    format.getSampleRate(),
                    format.getSampleSizeInBits()*2,
                    format.getChannels(),
                    format.getFrameSize()*2,
                    format.getFrameRate(),
                    true // BE
                );
                stream = AudioSystem.getAudioInputStream(format, stream);
            }
            // Create the clip
            DataLine.Info info = new DataLine.Info(
                Clip.class,
                stream.getFormat(),
                 ((int)stream.getFrameLength()*format.getFrameSize())
            );
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream); // Blocks
            return clip;
        } catch (Exception e) {
            logger.warning("Could not create sound clip: "+e);
            e.printStackTrace();
        }
        return null;
    } */

    /**
     * Calculate the clip's duration from WAVE file headers.
     *
     * @param filename The clip's file name
     * @return Duration in milliseconds
     */
    private static int getClipDuration(URL fileURL) {
        try {
            // Read the WAVE file header
            byte[] b = new byte[44];
            InputStream is = fileURL.openStream();
            is.read(b);
            is.close();
            // Calculate clip length
            int byteRate = (b[28]&0xff) | (b[29]&0xff)<<8 | (b[30]&0xff)<<16 | (b[31]&0xff)<<24; // Bytes per second
            int bytes = (b[40]&0xff) | (b[41]&0xff)<<8 | (b[42]&0xff)<<16 | (b[43]&0xff)<<24; // Bytes
            return (int) Math.ceil(1000*bytes/byteRate);
        } catch (Exception ex) { ex.printStackTrace(); }
        return -1;
    }

    /**
     * Load the underlying sound clip (AudioClip emulation).
     *
     * @param filename The clip's file name
     * @return The clip or null on failure
     */
    private static Clip loadClip(String filename) {
        URL fileURL = Sound.class.getClassLoader().getResource(Constants.SOUNDS_URL + filename);
        AudioClip aclip = Applet.newAudioClip(fileURL);
        int duration = getClipDuration(fileURL);
        Clip clip = new Clip(aclip, duration);
        // System.out.println(filename+" = "+duration+"ms");
        return clip;
    }

    /**
     * Create a Sound object with a maximum concurrency of 1.
     *
     * @param filename The sound file
     */
    public Sound(String filename) {
        this(filename, 1);
    }

    /**
     * Create a Sound object.
     *
     * @param filename The sound file
     * @param concurrency Maximum concurrency to play
     */
    public Sound(String filename, int concurrency) {
        this.clips = new Clip[concurrency];
        for (int i=0; i<concurrency; i++) {
            this.clips[i] = loadClip(filename);
        }
    }

    /**
     * Try to play the sound.
     *
     * @return true on success, false if maximum concurrency is reached or the delay is too short
     */
    public boolean play() {
        synchronized (this.clips) {
            long now = (new Date()).getTime();
            if (this.lastplayedAt > 0 && now < this.lastplayedAt + MINIMUM_DELAY) {
                return false;
            }
            for (int i=0; i<this.clips.length; i++) {
                if (!this.clips[i].isActive()) {
                    this.lastplayedAt = now;
                    this.clips[i].setFramePosition(0);
                    this.clips[i].start();
                    return true;
                }
            }
        }
        return false;
    }
}

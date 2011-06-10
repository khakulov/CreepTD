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
 * Interface for Sound.
 */
public interface ISound {
    //TODO ADD/CLEAN SOME SOUNDS

    //game
    String FIN = "fin.wav";
    String WON = "won.wav";
    String BUTTON = "button.wav";
    //Tower upgrade
    String HOLY = "holy.wav";
    //Tower shoot 
    String SHOOT1 = "shoot1.wav";
    String SHOOT2 = "shoot2.wav";
    String SHOOT3 = "shoot3.wav";
    String SHOOT4 = "shoot4.wav";
    String SHOOT5 = "shoot5.wav";
    String SHOOT6 = "shoot6.wav";
    String LASER1 = "laser1.wav";
    String LASER2 = "laser2.wav";
    String LASER3 = "laser3.wav";
    //Creep dead or starts
    String DEAD1 = "dead.wav";
    String DEAD2 = "dead2.wav";
    String DEAD3 = "dead3.wav";
    String DEAD4 = "dead4.wav";
    String DEAD5 = "dead5.wav";
    //Creep escape
    String ESCAPE = "dcloak.wav";
    String WARN = "warn.wav";
    String INTRO = "intro.wav";
    String CLAP = "clap.wav";
    String HORNBEEP = "hornbeep.wav";
    String CASH = "cash.wav";

    /**
     * Set the AudioClipObject for the thread.
     * @param clip ClipObject
     */
    void play(AudioClip clip);
}

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
import java.util.Random;

/**
 * The sound management.
 *
 * Always use this class to play sounds since it manages sound concurrency and
 * so on.
 * 
 * @author Daniel
 */
public class SoundManagement {
    // All used sounds
    private static int EFFECT_CONCURRENCY = 3;
    public static Sound ERROR = new Sound("error.wav");
    public static Sound GAMEOVER = new Sound("gameover.wav");
    public static Sound HORNBEEP = new Sound("hornbeep.wav");
    public static Sound COUNTDOWN = new Sound("countdown.wav");
    public static Sound START = new Sound("start.wav");
    public static Sound CLAP = new Sound("clap.wav", EFFECT_CONCURRENCY);
    public static Sound HOVER1 = new Sound("hover1.wav", EFFECT_CONCURRENCY);
    public static Sound HOVER2 = new Sound("hover2.wav", EFFECT_CONCURRENCY);
    public static Sound HOVER3 = new Sound("hover3.wav", EFFECT_CONCURRENCY);
    public static Sound CLICK = new Sound("click.wav");
    public static Sound CASH = new Sound("cash.wav");
    public static Sound BUILD = new Sound("build.wav", EFFECT_CONCURRENCY);
    public static Sound UPGRADE = new Sound("upgrade.wav", EFFECT_CONCURRENCY);
    public static Sound SHOOT1 = new Sound("shoot1.wav", EFFECT_CONCURRENCY);
    public static Sound SHOOT2 = new Sound("shoot2.wav", EFFECT_CONCURRENCY);
    public static Sound SHOOT3 = new Sound("shoot3.wav", EFFECT_CONCURRENCY);
    public static Sound SHOOT4 = new Sound("shoot4.wav", EFFECT_CONCURRENCY);
    public static Sound SHOOT5 = new Sound("shoot5.wav", EFFECT_CONCURRENCY);
    public static Sound SHOOT6 = new Sound("shoot6.wav", EFFECT_CONCURRENCY);
    public static Sound EXPLODE = new Sound("explode.wav", EFFECT_CONCURRENCY);
    public static Sound LASER1 = new Sound("laser1.wav", EFFECT_CONCURRENCY);
    public static Sound LASER2 = new Sound("laser2.wav", EFFECT_CONCURRENCY);
    public static Sound LASER3 = new Sound("laser3.wav", EFFECT_CONCURRENCY);
    public static Sound DEAD1 = new Sound("dead1.wav", EFFECT_CONCURRENCY);
    public static Sound DEAD2 = new Sound("dead2.wav", EFFECT_CONCURRENCY);
    public static Sound DEAD3 = new Sound("dead3.wav", EFFECT_CONCURRENCY);
    public static Sound DEAD4 = new Sound("dead4.wav", EFFECT_CONCURRENCY);
    public static Sound DEAD5 = new Sound("dead5.wav", EFFECT_CONCURRENCY);
    public static Sound ESCAPE = new Sound("escape.wav", EFFECT_CONCURRENCY);
    public static Sound WARN1 = new Sound("warn1.wav", EFFECT_CONCURRENCY);
    public static Sound WARN2 = new Sound("warn2.wav", EFFECT_CONCURRENCY);
    public static Sound WARN3 = new Sound("warn3.wav", EFFECT_CONCURRENCY);
    public static Sound WARN4 = new Sound("warn4.wav", EFFECT_CONCURRENCY);

    /** Muted flag */
    private boolean muted = false;

    /**
     * Get SoundManagement instance.
     */
    public SoundManagement() {
        HORNBEEP.play();
    }

    /**
     * Set muted flag.
     *
     * @param muted true for muted, else false
     */
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    /**
     * Test if sounds are muted.
     *
     * @return true if muted, else false
     */
    public boolean isMuted() {
        return this.muted;
    }

    /**
     * Toggle muted flag.
     */
    public void toggleMute() {
        this.setMuted(!this.muted);
    }

    /**
     * Play creep dies sound.
     *
     * @param t Type of creep
     * @return true if the sound is played, else false
     */
    public boolean creepDiesSound(Constants.Creeps t) {
        if (muted) return false;
        switch (t) {
            case creep1:
            case creep2:
            case creep3:
            case creep4:
                return DEAD1.play();
            case creep5:
            case creep6:
            case creep7:
            case creep8:
                return DEAD2.play();
            case creep9:
            case creep10:
            case creep11:
            case creep12:
                return DEAD3.play();
            case creep13:
            case creep14:
                return DEAD4.play();
            case creep15:
            case creep16:
                return DEAD5.play();
        }
        return false;
    }

    /**
     * Play creep warn sound.
     *
     * @param t The creep's type
     * @return true if the sound is played, else false
     */
    public boolean creepWarnSound(Constants.Creeps t) {
        if (muted) return false;
        switch (t) {
            case creep1:
            case creep2:
            case creep3:
            case creep4:
                return WARN1.play();
            case creep5:
            case creep6:
            case creep7:
            case creep8:
                return WARN2.play();
            case creep9:
            case creep10:
            case creep11:
            case creep12:
                return WARN3.play();
            case creep13:
            case creep14:
            case creep15:
            case creep16:
                return WARN4.play();
        }
        return false;
    }

    /**
     * Play creep escaped sound.
     *
     * @param t The creep's type.
     * @return true if the sound is played, else false
     */
    public boolean creepEscapedSound(Constants.Creeps t) {
        if (muted) return false;
        return ESCAPE.play();
    }

    /**
     * Play tower shoots sound.
     *
     * @param t The tower's type
     * @return true if the sound is played, else false
     */
    public boolean towerShootsSound(Constants.Towers t) {
        if (muted) return false;
        switch (t) {
            // Basic tower
            case tower1:
                return SHOOT1.play();
            case tower11:
            case tower12:
            case tower13:
                return SHOOT2.play();
            // Slow tower
            case tower2:
                return LASER1.play();
            case tower21:
            case tower22:
            case tower23:
                return LASER2.play();
            // Splash tower
            case tower3:
                return SHOOT3.play();
            case tower31:
            case tower32:
            case tower33:
                return LASER3.play();
            // Rocket tower
            case tower4:
            case tower41:
            case tower42:
            case tower43:
                return SHOOT4.play();
            // Speed tower
            case tower5:
            case tower51:
            case tower52:
            case tower53:
                return SHOOT5.play();
            // Ultimate tower
            case tower6:
            case tower61:
                return SHOOT6.play();
        }
        return false;
    }

    /**
     * Play rocket explode sound.
     *
     * @return true if the sound is played, else false
     */
    public boolean explodeSound() {
        if (this.muted) return false;
        return EXPLODE.play();
    }

    /**
     * Play tower build sound.
     *
     * @param t The tower's type
     * @return true if the sound is played, else false
     */
    public boolean towerBuildSound(Constants.Towers t) {
        if (muted) return false;
        return BUILD.play();
    }

    /**
     * Play tower upgrade sound.
     *
     * @param t The tower's type
     * @return true if the sound is played, else false
     */
    public boolean towerUpgradeSound(Constants.Towers t) {
        if (muted) return false;
        return UPGRADE.play();
    }

    /**
     * Play an errorSound sound.
     *
     * @return true if the sound is played, else false
     */
    public boolean errorSound() {
        if (muted) return false;
        return ERROR.play();
    }

    /**
     * Play game over sound.
     *
     * @return true if sound is played, else false
     */
    public boolean gameOver() {
        if (muted) return false;
        return GAMEOVER.play();
    }

    /**
     * Play clap sound.
     *
     * @return true if the sound is played, else false
     */
    public boolean clapSound() {
        if (muted) return false;
        return CLAP.play();
    }

    /**
     * Play hornbeep sound.
     *
     * @return true if the sound is played, else false
     */
    public boolean hornbeepSound() {
        if (muted) return false;
        return HORNBEEP.play();
    }

    /**
     * Play countdown sound.
     *
     * @return true if the sound is played, else false
     */
    public boolean countdownSound() {
        if (muted) return false;
        return COUNTDOWN.play();
    }

    /**
     * Play start sound.
     *
     * @return true if the sound is played, else false
     */
    public boolean startSound() {
        if (muted) return false;
        return START.play();
    }

    /**
     * Play cash sound.
     *
     * @return true if the sound is played, else false
     */
    public boolean cashSound() {
        if (muted) return false;
        return CASH.play();
    }

    /**
     * Play click sound.
     *
     * @return true if the sound is played, else false
     */
    public boolean clickSound() {
        if (muted) return false;
        return CLICK.play();
    }

    /**
     * Play hover sound.
     *
     * @return true if the sound is played, else false
     */
    public boolean hoverSound() {
        if (muted) return false;
        int r = new Random().nextInt(3);
        switch (r) {
            case 0:
                return HOVER1.play();
            case 1:
                return HOVER2.play();
            case 2:
                return HOVER3.play();
        }
        return false;
    }
}

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

import java.applet.Applet;
import java.applet.AudioClip;

import com.creeptd.common.Constants;

//       At this revision, only one soundsample per soundtype is allowed.
//       For example:
//           Only one shoot at the time.
//           Mixing of shoot and creapdeath is allowed.
/**
 * If you want to use sound in game, use the object from this class, it's
 * instanced in GameLoop.
 */
public class SoundManagement {

    private Thread usedThreadCreepDiesS = new Thread();
    private Thread usedThreadCreepGoesToNextPlayerS = new Thread();
    private Thread usedThreadCreepStartsS = new Thread();
    private Thread usedThreadTowerShootS = new Thread();
    private Thread usedThreadTowerUpgradeS = new Thread();
    private boolean mute = false;
    private AudioClip clipERROR = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.ERROR));
    private AudioClip clipGAMEOVER = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.GAMEOVER));
    private AudioClip clipHOLY = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.HOLY));
    private AudioClip clipSHOOT1 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.SHOOT1));
    private AudioClip clipSHOOT2 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.SHOOT2));
    private AudioClip clipSHOOT3 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.SHOOT3));
    private AudioClip clipSHOOT4 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.SHOOT4));
    private AudioClip clipSHOOT5 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.SHOOT5));
    private AudioClip clipSHOOT6 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.SHOOT6));
    private AudioClip clipLASER1 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.LASER1));
    private AudioClip clipLASER2 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.LASER2));
    private AudioClip clipLASER3 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.LASER3));
    private AudioClip clipDEAD1 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.DEAD1));
    private AudioClip clipDEAD2 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.DEAD2));
    private AudioClip clipDEAD3 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.DEAD3));
    private AudioClip clipDEAD4 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.DEAD4));
    private AudioClip clipDEAD5 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.DEAD5));
    private AudioClip clipESCAPE = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.ESCAPE));
    private AudioClip clipWARN1 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.WARN1));
    private AudioClip clipWARN2 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.WARN2));
    private AudioClip clipWARN3 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.WARN3));
    private AudioClip clipWARN4 = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.WARN4));
    private AudioClip clipHORNBEEP = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.HORNBEEP));
    private AudioClip clipCLAP = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.CLAP));
    private AudioClip clipCASH = Applet.newAudioClip(this.getClass().getClassLoader().getResource(Constants.SOUNDS_URL + ISound.CASH));

    /**
     * @return the thread
     */
    public Thread getUsedThreadCreepDiesS() {
        return usedThreadCreepDiesS;
    }

    /**
     * @return the thread
     */
    public Thread getUsedThreadCreepGoesToNextPlayerS() {
        return usedThreadCreepGoesToNextPlayerS;
    }

    /**
     * @return the thread
     */
    public Thread getUsedThreadCreepStartsS() {
        return usedThreadCreepStartsS;
    }

    /**
     * @return the thread
     */
    public Thread getUsedThreadTowerShootS() {
        return usedThreadTowerShootS;
    }

    /**
     * @return the thread
     */
    public Thread getUsedThreadTowerUpgradeS() {
        return usedThreadTowerUpgradeS;
    }

    /**
     * Constructor plays a sound to welcome each other.
     */
    public SoundManagement() {
        Thread t = new Thread(new Sound(this.clipHORNBEEP));
        this.usedThreadCreepDiesS = t;
        this.usedThreadCreepStartsS = t;
        this.usedThreadCreepGoesToNextPlayerS = t;
        this.usedThreadCreepStartsS = t;
        this.usedThreadTowerShootS = t;
        this.usedThreadTowerUpgradeS = t;
        t.start();

    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    /**
     * Toggle Mute/Play.
     */
    public void toggleMute() {
        this.mute = !this.mute;
    }

    /**
     * Use this for dying creeps.
     *
     * @param t
     *            Type of creep
     * @return true if the last thread terminated false if the last thread not
     *         terminated
     */
    public boolean creepDiesSound(Constants.Creeps t) {

        if (usedThreadCreepDiesS.getState() != Thread.State.TERMINATED || mute) {
            return false;
        }

        switch (t) {
            case creep1:
                Thread creepd1 = new Thread(new Sound(this.clipDEAD1));
                usedThreadCreepDiesS = creepd1;
                creepd1.start();
                break;
            case creep2:
                Thread creepd2 = new Thread(new Sound(this.clipDEAD2));
                usedThreadCreepDiesS = creepd2;
                creepd2.start();
                break;
            case creep3:
                Thread creepd3 = new Thread(new Sound(this.clipDEAD3));
                usedThreadCreepDiesS = creepd3;
                creepd3.start();
                break;
            case creep4:
                Thread creepd4 = new Thread(new Sound(this.clipDEAD4));
                usedThreadCreepDiesS = creepd4;
                creepd4.start();
                break;
            case creep5:
                Thread creepd5 = new Thread(new Sound(this.clipDEAD5));
                usedThreadCreepDiesS = creepd5;
                creepd5.start();
                break;
            default:
                Thread creepSixTOsixteen = new Thread(new Sound(this.clipDEAD5));
                usedThreadCreepDiesS = creepSixTOsixteen;
                creepSixTOsixteen.start();
                break;
        }
        return true;
    }

    /**
     * Use this for a creep that that enters the own board.
     *
     * @param t Type of creep
     * @return true if the last thread terminated false if the last thread not terminated
     */
    public boolean creepWarnSound(Constants.Creeps t) {
        if (usedThreadCreepStartsS.getState() != Thread.State.TERMINATED || mute) {
            return false;
        }
        AudioClip c = null;
        if (t.equals(t.creep1) || t.equals(t.creep2) || t.equals(t.creep3) || t.equals(t.creep4)) {
            c = this.clipWARN1;
        } else if (t.equals(t.creep5) || t.equals(t.creep6) || t.equals(t.creep7) || t.equals(t.creep8)) {
            c = this.clipWARN2;
        } else if (t.equals(t.creep9) || t.equals(t.creep10) || t.equals(t.creep11) || t.equals(t.creep12)) {
            c = this.clipWARN3;
        } else if (t.equals(t.creep13) || t.equals(t.creep14) || t.equals(t.creep15) || t.equals(t.creep16)) {
            c = this.clipWARN4;
        }
        Thread creepsDef = new Thread(new Sound(c));
        usedThreadCreepStartsS = creepsDef;
        creepsDef.start();
        return true;
    }

    /**
     * A creepy creatures walks its way.
     *
     * @param t
     *            Type of creep
     * @return true if the last thread terminated false if the last thread not
     *         terminated
     */
    public boolean creepGoesToNextPlayerSound(Constants.Creeps t) {

        if (usedThreadCreepGoesToNextPlayerS.getState() != Thread.State.TERMINATED || mute) {
            return false;
        }

        switch (t) {
            case creep1:
                Thread creepn1 = new Thread(new Sound(this.clipESCAPE));
                usedThreadCreepGoesToNextPlayerS = creepn1;
                creepn1.start();
                break;
            case creep2:
                Thread creepn2 = new Thread(new Sound(this.clipESCAPE));
                usedThreadCreepGoesToNextPlayerS = creepn2;
                creepn2.start();
                break;
            case creep3:
                Thread creepn3 = new Thread(new Sound(this.clipESCAPE));
                usedThreadCreepGoesToNextPlayerS = creepn3;
                creepn3.start();
                break;
            case creep4:
                Thread creepn4 = new Thread(new Sound(this.clipESCAPE));
                usedThreadCreepGoesToNextPlayerS = creepn4;
                creepn4.start();
                break;
            default:
                Thread creepnDef = new Thread(new Sound(this.clipESCAPE));
                usedThreadCreepGoesToNextPlayerS = creepnDef;
                creepnDef.start();
                break;
        }
        return true;
    }

    /**
     * Towers shoot with sound.
     *
     * @param t
     *            Type of tower
     * @return true if the last thread terminated false if the last thread not
     *         terminated
     */
    public boolean towerShootsSound(Constants.Towers t) {

        if (usedThreadTowerShootS.getState() != Thread.State.TERMINATED || mute) {
            return false;
        }

        switch (t) {
            case tower1:
                Thread towers1 = new Thread(new Sound(this.clipSHOOT1));
                usedThreadTowerShootS = towers1;
                towers1.start();
                break;
            case tower11:
                Thread towers11 = new Thread(new Sound(this.clipSHOOT2));
                usedThreadTowerShootS = towers11;
                towers11.start();
                break;
            case tower12:
                Thread towers12 = new Thread(new Sound(this.clipSHOOT2));
                usedThreadTowerShootS = towers12;
                towers12.start();
                break;
            case tower13:
                Thread towers13 = new Thread(new Sound(this.clipSHOOT2));
                usedThreadTowerShootS = towers13;
                towers13.start();
                break;
            case tower2:
                Thread towers2 = new Thread(new Sound(this.clipLASER1));
                usedThreadTowerShootS = towers2;
                towers2.start();
                break;
            case tower21:
                Thread towers21 = new Thread(new Sound(this.clipLASER2));
                usedThreadTowerShootS = towers21;
                towers21.start();
                break;
            case tower22:
                Thread towers22 = new Thread(new Sound(this.clipLASER2));
                usedThreadTowerShootS = towers22;
                towers22.start();
                break;
            case tower23:
                Thread towers23 = new Thread(new Sound(this.clipLASER2));
                usedThreadTowerShootS = towers23;
                towers23.start();
                break;
            case tower3:
                Thread towers3 = new Thread(new Sound(this.clipSHOOT3));
                usedThreadTowerShootS = towers3;
                towers3.start();
                break;
            case tower31:
                Thread towers31 = new Thread(new Sound(this.clipLASER3));
                usedThreadTowerShootS = towers31;
                towers31.start();
                break;
            case tower32:
                Thread towers32 = new Thread(new Sound(this.clipLASER3));
                usedThreadTowerShootS = towers32;
                towers32.start();
                break;
            case tower33:
                Thread towers33 = new Thread(new Sound(this.clipLASER3));
                usedThreadTowerShootS = towers33;
                towers33.start();
                break;
            case tower4:
                Thread towers4 = new Thread(new Sound(this.clipSHOOT4));
                usedThreadTowerShootS = towers4;
                towers4.start();
                break;
            case tower41:
                Thread towers41 = new Thread(new Sound(this.clipSHOOT4));
                usedThreadTowerShootS = towers41;
                towers41.start();
                break;
            case tower42:
                Thread towers42 = new Thread(new Sound(this.clipSHOOT4));
                usedThreadTowerShootS = towers42;
                towers42.start();
                break;
            case tower43:
                Thread towers43 = new Thread(new Sound(this.clipSHOOT4));
                usedThreadTowerShootS = towers43;
                towers43.start();
                break;
            case tower5:
                Thread towers5 = new Thread(new Sound(this.clipSHOOT5));
                usedThreadTowerShootS = towers5;
                towers5.start();
                break;
            case tower51:
                Thread towers51 = new Thread(new Sound(this.clipSHOOT5));
                usedThreadTowerShootS = towers51;
                towers51.start();
                break;
            case tower52:
                Thread towers52 = new Thread(new Sound(this.clipSHOOT5));
                usedThreadTowerShootS = towers52;
                towers52.start();
                break;
            case tower53:
                Thread towers53 = new Thread(new Sound(this.clipSHOOT5));
                usedThreadTowerShootS = towers53;
                towers53.start();
                break;
            case tower6:
                Thread towers6 = new Thread(new Sound(this.clipSHOOT6));
                usedThreadTowerShootS = towers6;
                towers6.start();
                break;
            case tower61:
                Thread towers61 = new Thread(new Sound(this.clipSHOOT6));
                usedThreadTowerShootS = towers61;
                towers61.start();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Towers upgrade with sound.
     *
     * @param t
     *            Type of tower
     * @return true if the last thread terminated false if the last thread not
     *         terminated
     */
    public boolean towerUpgradeSound(Constants.Towers t) {

        if (usedThreadTowerUpgradeS.getState() != Thread.State.TERMINATED || mute) {
            return false;
        }

        switch (t) {
            case tower1:
                Thread toweru1 = new Thread(new Sound(this.clipHOLY));
                usedThreadTowerUpgradeS = toweru1;
                toweru1.start();
                break;
            case tower2:
                Thread toweru2 = new Thread(new Sound(this.clipHOLY));
                usedThreadTowerUpgradeS = toweru2;
                toweru2.start();
                break;
            case tower3:
                Thread toweru3 = new Thread(new Sound(this.clipHOLY));
                usedThreadTowerUpgradeS = toweru3;
                toweru3.start();
                break;
            case tower4:
                Thread toweru4 = new Thread(new Sound(this.clipHOLY));
                usedThreadTowerUpgradeS = toweru4;
                toweru4.start();
                break;
            default:
                Thread towerDef = new Thread(new Sound(this.clipHOLY));
                usedThreadTowerUpgradeS = towerDef;
                towerDef.start();
                break;
        }
        return true;
    }

    /**
     * Player looses.
     *
     * @return if played or not
     */
    public boolean error() {
        if (mute) {
            return false;
        }
        Thread loose = new Thread(new Sound(this.clipERROR));
        loose.start();
        return true;
    }

    /**
     * Player wins.
     *
     * @return if played or not
     */
    public boolean gameOver() {
        if (mute) {
            return false;
        }
        Thread won = new Thread(new Sound(this.clipGAMEOVER));
        won.start();
        return true;
    }

    /**
     * Plays a CLAP, *knock* *knock*.
     *
     * @return if played or not
     */
    public boolean clapSound() {
        if (mute) {
            return false;
        }
        Thread clap = new Thread(new Sound(this.clipCLAP));
        clap.start();
        return true;
    }

    /**
     * Plays a nice HORNBEEP, *beep*.
     *
     * @return if played or not
     */
    public boolean hornbeepSound() {
        if (mute) {
            return false;
        }
        Thread hbeep = new Thread(new Sound(this.clipHORNBEEP));
        hbeep.start();
        return true;
    }

    /**
     * Plays a cash register sound
     *
     * @return if played or not
     */
    public boolean cashSound() {
        if (mute) {
            return false;
        }
        Thread cash = new Thread(new Sound(this.clipCASH));
        cash.start();
        return true;
    }
}

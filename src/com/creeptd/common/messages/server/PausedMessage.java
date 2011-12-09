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
package com.creeptd.common.messages.server;

import com.creeptd.common.Constants;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A pause round message.
 * 
 * @author Daniel
 */
public class PausedMessage extends ServerMessage {
	private static final long serialVersionUID = 132893098708098224L;

	private static final String REG_EXP = "PAUSED\\s([0-9]+)\\s(on|off)\\s(yes|no)\\s([0-9]+)";
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private int playerId;
    private boolean paused = false;
    private boolean pauseDisabled = false;
    private int maxPauseMs = Constants.MAX_PAUSE_MS;

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public Integer getPlayerId() {
        return this.playerId;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public boolean isPauseDisabled() {
        return pauseDisabled;
    }

    public void setPauseDisabled(boolean pauseDisabled) {
        this.pauseDisabled = pauseDisabled;
    }

    public int getMaxPauseMs() {
        return maxPauseMs;
    }

    public void setMaxPauseMs(int maxPauseMs) {
        this.maxPauseMs = maxPauseMs;
    }

    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setPlayerId(Integer.parseInt(matcher.group(1)));
            this.setPaused(matcher.group(2).equals("on"));
            this.setPauseDisabled(matcher.group(3).equals("yes"));
            this.setMaxPauseMs(Integer.parseInt(matcher.group(4)));
        }
    }

    @Override
    public String getMessageString() {
        return "PAUSED " + this.getPlayerId() + " " + (this.paused ? "on" : "off") + " " + (this.pauseDisabled ? "yes" : "no") + " " + this.maxPauseMs;
    }
}

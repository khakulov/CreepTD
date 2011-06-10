/**
Creep Smash, a multiplayer towerdefence game
created as a project at the Hochschule fuer
Technik Stuttgart (University of Applied Science)
http://www.hft-stuttgart.de 

Copyright (C) 2008 by      
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Message reply for one player.
 * @author philipp
 *
 */
public class ScoreResponseMessage extends ServerMessage {

    /**
     * Regular expression for this message.
     */
    private static final String REG_EXP =
            "SCORE\\s\"([^\"]+)\"\\s([0-9]+)\\s([0-9]+)\\s([-]?[0-9]+)\\s([-]?[0-9]+)[\\s]?";
    private String playerName;
    private Integer experience;
    private Integer elopoints;
    private Integer lastgame_experience;
    private Integer lastgame_elopoints;
    /**
     * Pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);

    /**
     * Default no-parameter constructor.
     */
    public ScoreResponseMessage() {
    }

    /**
     * Creates a new ScoreResponseMessage.
     * @param playerName the players name
     * @param oldPoints the points for the last game
     * @param points the actual points in the highscore
     */
    public ScoreResponseMessage(String playerName, Integer experience, Integer elopoints, Integer lastgame_experience, Integer lastgame_elopoints) {
        this.playerName = playerName;
        this.experience = experience;
        this.elopoints = elopoints;
        this.lastgame_experience = experience;
        this.lastgame_elopoints = elopoints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageString() {
        return "SCORE \"" + this.playerName + "\" " + this.experience + " " + this.elopoints + " " + this.lastgame_experience + " " + this.lastgame_elopoints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setPlayerName(matcher.group(1));
            this.setExperience(Integer.valueOf(matcher.group(2)));
            this.setElopoints(Integer.valueOf(matcher.group(3)));
            this.setLastgameExperience(Integer.valueOf(matcher.group(4)));
            this.setLastgameElopoints(Integer.valueOf(matcher.group(5)));
        }
    }

    /**
     * @return the playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @param playerName the playerName to set
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getElopoints() {
        return elopoints;
    }

    public void setElopoints(Integer elopoints) {
        this.elopoints = elopoints;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getLastgameElopoints() {
        return lastgame_elopoints;
    }

    public void setLastgameElopoints(Integer lastgame_elopoints) {
        this.lastgame_elopoints = lastgame_elopoints;
    }

    public Integer getLastgameExperience() {
        return lastgame_experience;
    }

    public void setLastgameExperience(Integer lastgame_experience) {
        this.lastgame_experience = lastgame_experience;
    } 
}

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
    private Integer points;
    private Integer skill;
    private Integer lastgame_points;
    private Integer lastgame_skill;
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
    public ScoreResponseMessage(String playerName, Integer points, Integer skill, Integer lastgame_points, Integer lastgame_skill) {
        this.playerName = playerName;
        this.points = points;
        this.skill = skill;
        this.lastgame_points = points;
        this.lastgame_skill = skill;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageString() {
        return "SCORE \"" + this.playerName + "\" " + this.points + " " + this.skill + " " + this.lastgame_points + " " + this.lastgame_skill;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setPlayerName(matcher.group(1));
            this.setPoints(Integer.valueOf(matcher.group(2)));
            this.setSkill(Integer.valueOf(matcher.group(3)));
            this.setLastgamePoints(Integer.valueOf(matcher.group(4)));
            this.setLastgameSkill(Integer.valueOf(matcher.group(5)));
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

    public Integer getSkill() {
        return skill;
    }

    public void setSkill(Integer skill) {
        this.skill = skill;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getLastgameSkill() {
        return lastgame_skill;
    }

    public void setLastgameSkill(Integer skill) {
        this.lastgame_skill = skill;
    }

    public Integer getLastgamePoints() {
        return lastgame_points;
    }

    public void setLastgamePoints(Integer points) {
        this.lastgame_points = points;
    }
}

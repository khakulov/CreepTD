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
 * Message from client, to update a tower.
 * 
 * @author andreas
 *
 */
public class ChangeStrategyRoundMessage extends ServerMessage implements GameMessage {

	private static final long serialVersionUID = -2217125631278736940L;

	/**
     *	regular expression for message-parsing.
     */
    private static final String REGEXP_CHANGE_STRATEGY = "STRATEGY_CHANGED\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9])(L|)";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REGEXP_CHANGE_STRATEGY);
    private Integer towerId;
    private Constants.StrategyType strategy;
    private boolean locked;
    private Long roundId;
    private Integer playerId;

    /**
     * @return the id of the tower to upgrade
     */
    public Integer getTowerId() {
        return this.towerId;
    }

    /**
     * @param towerId of the tower to upgrade
     */
    public void setTowerId(Integer towerId) {
        this.towerId = towerId;
    }

    /**
     * @param messageString the message as String.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setRoundId(Long.parseLong(matcher.group(1)));
            this.setPlayerId(Integer.parseInt(matcher.group(2)));
            this.setTowerId(Integer.valueOf(matcher.group(3)));
            this.setStrategy(Constants.StrategyType.valueForIdentifier(Integer.parseInt(matcher.group(4))));
            this.setLocked(Boolean.valueOf(matcher.group(5)));
        }

    }

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "STRATEGY_CHANGED " + this.getRoundId() + " " + this.playerId + " " + this.getTowerId() + " " + this.strategy.getIdentifier() + (this.isLocked() ? "L" : "");
    }

    public void setStrategy(Constants.StrategyType strategy) {
        this.strategy = strategy;
    }

    public Constants.StrategyType getStrategy() {
        return this.strategy;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return this.locked;
    }

    /**
     * @return the roundId
     */
    public Long getRoundId() {
        return this.roundId;
    }

    /**
     * @param roundId the roundId to set
     */
    public void setRoundId(Long roundId) {
        this.roundId = roundId;
    }

    /**
     * @return the playerId
     */
    public Integer getPlayerId() {
        return this.playerId;
    }

    /**
     * @param playerId the playerId to set
     */
    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }
}

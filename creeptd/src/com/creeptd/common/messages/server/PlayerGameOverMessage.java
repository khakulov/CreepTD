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

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Message sent from server to client, if a player is dead.
 *
 * @author Daniel
 */
public class PlayerGameOverMessage extends RoundMessage implements GameMessage {
    
    private static final String REG_EXP = "PLAYER_GAMEOVER\\s([0-9]+)\\s([0-9]+)\\s(0|1)";
    
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private Integer playerId;
    private boolean winner;
    
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

    /**
     * Check if the associated player is a winner or not.
     *
     * @return true if winner, else false
     */
    public boolean isWinner() {
        return winner;
    }

    /**
     * Set if this player is a winner or not. An updated message may be sent
     * later.
     *
     * @param winner true for winner, else false
     */
    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "PLAYER_GAMEOVER "+this.getRoundId()+" "+this.playerId+" "+(this.winner ? "1" : "0");
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
            this.setWinner(matcher.group(3).equals("1"));
        }

    }

    /**
     * Returns true if o is a PlayerJoinedMessage with the same contents
     * as this one.
     * @param o the object to compare to.
     * @return true if o is equal to this object.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PlayerGameOverMessage)) {
            return false;
        }
        PlayerGameOverMessage m = (PlayerGameOverMessage) o;
        return this.getRoundId() == m.getRoundId() && this.playerId == m.getPlayerId() && this.winner == m.isWinner();
    }
    
    /**
     * Returns a hash code for this object.
     * @return a hash code for this object.
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ this.getRoundId().hashCode() ^ this.playerId ^ new Boolean(this.winner).hashCode();
    }
}

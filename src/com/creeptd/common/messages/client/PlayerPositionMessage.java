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
package com.creeptd.common.messages.client;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.creeptd.common.messages.client.ClientMessage;

/**
 * Message send from server to client, if a new player joined.
 * 
 * @author andreas
 * 
 */
public class PlayerPositionMessage extends ClientMessage implements GameMessage {

    private static final String REG_EXP = "PLAYER_POSIOTION\\s([a-z0-9]+)\\s([a-z0-9]+)\\s([a-z0-9]+)\\s([a-z0-9]+)\\s([a-z0-9]+)\\s([a-z0-9]+)\\s([a-z0-9]+)\\s([a-z0-9]+)";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private Integer playerPositionID1 = null;
    private Integer playerPositionID2 = null;
    private Integer playerPositionID3 = null;
    private Integer playerPositionID4 = null;
    private Integer player1 = null;
    private Integer player2 = null;
    private Integer player3 = null;
    private Integer player4 = null;

    /**
     * Default constructor.
     */
    public PlayerPositionMessage() {
        super();
    }

    /**
     * @param playerName
     *            the name of the player
     * @param playerId
     *            the id of the player
     */
    public PlayerPositionMessage(Integer playerPosition1,
            Integer playerPosition2, Integer playerPosition3,
            Integer playerPosition4, Integer player1, Integer player2,
            Integer player3, Integer player4) {
        this.playerPositionID1 = playerPosition1;
        this.playerPositionID2 = playerPosition2;
        this.playerPositionID3 = playerPosition3;
        this.playerPositionID4 = playerPosition4;

        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.player4 = player4;

    }

    /**
     * @return position PID
     */
    public TreeMap<Integer, Integer> getOrder() {

        TreeMap<Integer, Integer> playersOrder = new TreeMap<Integer, Integer>();

        if (this.player1 != null) {
            playersOrder.put(this.playerPositionID1, this.player1);
        }
        if (this.player2 != null) {
            playersOrder.put(this.playerPositionID2, this.player2);
        }
        if (this.player3 != null) {
            playersOrder.put(this.playerPositionID3, this.player3);
        }
        if (this.player4 != null) {
            playersOrder.put(this.playerPositionID4, this.player4);
        }

        return playersOrder;
    }

    /**
     * @return the player1
     */
    public Integer getPlayer1() {
        return this.player1;
    }

    /**
     * @param set
     *            Player1 ID;
     */
    public void setpPlayer1(Integer Player1) {
        this.player1 = Player1;
    }

    /**
     * @return the player2
     */
    public Integer getPlayer2() {
        return this.player2;
    }

    /**
     * @param set
     *            Player2 ID;
     */
    public void setpPlayer2(Integer Player2) {
        this.player2 = Player2;
    }

    /**
     * @return the player3
     */
    public Integer getPlayer3() {
        return this.player3;
    }

    /**
     * @param set
     *            Player3 ID;
     */
    public void setpPlayer3(Integer Player3) {
        this.player3 = Player3;
    }

    /**
     * @return the player1
     */
    public Integer getPlayer4() {
        return this.player4;
    }

    /**
     * @param set
     *            Player4 ID;
     */
    public void setpPlayer4(Integer Player4) {
        this.player4 = Player4;
    }

    /**
     * @return the playerPositionID1
     */
    public Integer getplayerPositionID1() {
        return this.playerPositionID1;
    }

    /**
     * @param set
     *            PlayerPositionID1 1-4;
     */
    public void setplayerPositionID1(Integer PlayerPositionID1) {
        this.playerPositionID1 = PlayerPositionID1;
    }

    /**
     * @return the playerPositionID2
     */
    public Integer getplayerPositionID2() {
        return this.playerPositionID2;
    }

    /**
     * @param set
     *            PlayerPositionID1 1-4;
     */
    public void setplayerPositionID2(Integer PlayerPositionID1) {
        this.playerPositionID2 = PlayerPositionID1;
    }

    /**
     * @return the playerPositionID3
     */
    public Integer getplayerPositionID3() {
        return this.playerPositionID3;
    }

    /**
     * @param set
     *            PlayerPositionID1 1-4;
     */
    public void setplayerPositionID3(Integer PlayerPositionID1) {
        this.playerPositionID3 = PlayerPositionID1;
    }

    /**
     * @return the playerPositionID14
     */
    public Integer getplayerPositionID4() {
        return this.playerPositionID4;
    }

    /**
     * @param set
     *            PlayerPositionID1 1-4;
     */
    public void setplayerPositionID4(Integer PlayerPositionID1) {
        this.playerPositionID4 = PlayerPositionID1;
    }

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "PLAYER_POSIOTION " + this.playerPositionID1 + " " + this.playerPositionID2 + " " + this.playerPositionID3 + " " + this.playerPositionID4 + " " + this.player1 + " " + this.player2 + " " + this.player3 + " " + this.player4;
    }

    /**
     * @param messageString
     *            the message as String.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {

            if (!matcher.group(1).equals("null")) {
                this.setplayerPositionID1(Integer.parseInt(matcher.group(1)));
            }
            if (!matcher.group(2).equals("null")) {
                this.setplayerPositionID2(Integer.parseInt(matcher.group(2)));
            }
            if (!matcher.group(3).equals("null")) {
                this.setplayerPositionID3(Integer.parseInt(matcher.group(3)));
            }
            if (!matcher.group(4).equals("null")) {
                this.setplayerPositionID4(Integer.parseInt(matcher.group(4)));
            }

            if (!matcher.group(5).equals("null")) {
                this.setpPlayer1(Integer.parseInt(matcher.group(5)));
            }
            if (!matcher.group(6).equals("null")) {
                this.setpPlayer2(Integer.parseInt(matcher.group(6)));
            }
            if (!matcher.group(7).equals("null")) {
                this.setpPlayer3(Integer.parseInt(matcher.group(7)));
            }
            if (!matcher.group(8).equals("null")) {
                this.setpPlayer4(Integer.parseInt(matcher.group(8)));
            }
        }
    }
}

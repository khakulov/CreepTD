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

import com.creeptd.common.Constants;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.creeptd.common.messages.MessageUtil;

/**
 * Message from client. Client creates a new game.
 * 
 * @author andreas
 *
 */
public class CreateGameMessage extends ClientMessage implements LobbyMessage {

    /**
     * regular expression for message-parsing.
     */
    private static final String REGEXP_CREATE_GAME_REQUEST =
            "CREATE_GAME_REQUEST\\s\"([^\"]+)\"\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s\"(.*)\"\\s([0-9]+)\\s(0|1)";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN =
            Pattern.compile(REGEXP_CREATE_GAME_REQUEST);
    private String gameName;
    private Integer mapId;
    private Integer maxPlayers;
    private String Passwort = "";
    private Integer MaxSkill = 0;
    private Integer MinSkill = 0;
    private Constants.Mode gameMode;
    private Boolean shufflePlayers = true;

    /**
     * No-arg constructor.
     */
    public CreateGameMessage() {
        super();
    }

    /**
     * Constructor that initializes the fields.
     * @param gameName initial gameName
     * @param mapId initial mapId
     * @param maxPlayers initial maxPlayers
     */
    public CreateGameMessage(String gameName, Integer mapId, Integer maxPlayers, Integer MaxSkill, Integer MinSkill, String Passwort) {
        this.gameName = gameName;
        this.mapId = mapId;
        this.maxPlayers = maxPlayers;
        this.Passwort = Passwort;
        this.MaxSkill = MaxSkill;
        this.MinSkill = MinSkill;

    }

    /**
     * @return the name of the game
     */
    public String getGameName() {
        return this.gameName;
    }

    /**
     * @param gameName the name of the game
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * @return the map id
     */
    public Integer getMapId() {
        return this.mapId;
    }

    /**
     * @param mapId the map id
     */
    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }

    /**
     * @return the number of max players
     */
    public Integer getMaxPlayers() {
        return this.maxPlayers;
    }

    /**
     * @param maxPlayers the number of max players
     */
    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * @param Passwort the Passwort of this game
     */
    public void setPasswort(String Passwort) {
        this.Passwort = Passwort;
    }

    /**
     * @return Game Passwort
     */
    public String getPassword() {
        return this.Passwort;
    }

    /**
     * @param MaxSkill of this game
     */
    public void setMaxSkill(Integer MaxSkill) {
        this.MaxSkill = MaxSkill;
    }

    /**
     * @return the number of MaxSkill
     */
    public Integer getMaxSkill() {
        return this.MaxSkill;
    }

    /**
     * @param maxPlayers MinSkill of this game
     */
    public void setMinSkill(Integer MinSkill) {
        this.MinSkill = MinSkill;
    }

    /**
     * @return the number of MinSkill
     */
    public Integer getMinSkill() {
        return this.MinSkill;
    }

    /**
     * @param gameMode Mode number of game
     */
    public void setGameMode(Constants.Mode mode) {
        this.gameMode = mode;
    }

    /**
     * @return the Gamemode number
     */
    public Constants.Mode getGameMode() {
        return gameMode;
    }

    public Boolean getShufflePlayers() {
        return shufflePlayers;
    }

    public void setShufflePlayers(Boolean shufflePlayers) {
        this.shufflePlayers = shufflePlayers;
    }

    /**
     * @param messageString the message as String.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setGameName(matcher.group(1));
            this.setMapId(Integer.valueOf(matcher.group(2)));
            this.setMaxPlayers(Integer.valueOf(matcher.group(3)));
            this.setMaxSkill(Integer.valueOf(matcher.group(4)));
            this.setMinSkill(Integer.valueOf(matcher.group(5)));
            this.setPasswort(matcher.group(6));
            Constants.Mode mode = Constants.Mode.ALLVSALL;
            try {
                int sentModeValue = Integer.valueOf(matcher.group(7));
                Constants.Mode sentMode = Constants.Mode.forValue(sentModeValue);
                if (sentMode != null) mode = sentMode;
            } catch (Exception ex) {}
            this.setGameMode(mode);
            this.setShufflePlayers(matcher.group(8).equals("1"));
        }
    }

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "CREATE_GAME_REQUEST \"" + MessageUtil.prepareToSend(this.getGameName()) + "\" " + this.getMapId() + " " + this.getMaxPlayers() + " " + this.getMaxSkill() + " " + this.getMinSkill() + " " + "\"" + MessageUtil.prepareToSend(this.getPassword()) + "\" " + this.getGameMode().getValue() + " " + (this.getShufflePlayers() ? "1" : "0");
    }
}

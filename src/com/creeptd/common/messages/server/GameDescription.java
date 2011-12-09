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

import com.creeptd.common.messages.MessageUtil;

/**
 * Description of a game. Part of GamesMessage.
 * 
 * @author andreas
 *
 */
public class GameDescription {

    private Integer gameId;
    private String gameName;
    private Integer mapId;
    private Integer numberOfPlayers;
    private Integer currentPlayers;
    private String Password = "";
    private String Player1;
    private String Player2;
    private String Player3;
    private String Player4;
    private Integer numSpectators = 0;
    private Integer MaxSkill = 0;
    private Integer MinSkill = 0;
    private Boolean shufflePlayers = true;
    private String state;
    private Constants.Mode gameMode;
    private Boolean pausable;
    protected static final String REG_EXP = "(\\s)*([0-9]+)\\s\"([^\"]+)\"\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)" + "\\s\"([^\"]*)\"\\s\"(.*)\"\\s\"(.*)\"\\s\"(.*)\"\\s\"(.*)\"\\s\"([^\"]+)\"\\s(0|1)\\s(\\-?[0-9]+)\\s(0|1)(\\s)*";
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);

    /**
     * default constructor.
     */
    public GameDescription() {
        super();
    }

    /**
     * @param gameId the id of the game
     * @param gameName the name of the game
     * @param mapId the id of the map
     * @param maxPlayers the number of max players
     * @param currentPlayers the number of current players
     * @param state the state of the game
     */
    public GameDescription(Integer gameId, String gameName, Integer mapId,
            Integer maxPlayers, Integer currentPlayers, Integer MaxSkill, Integer MinSkill,
            String Password,
            Constants.Mode gameMode,
            String Player1,
            String Player2,
            String Player3,
            String Player4,
            String state, Boolean shufflePlayers, Integer numSpectators, Boolean pausable) {
        super();
        this.gameId = gameId;
        this.gameName = gameName;
        this.mapId = mapId;
        this.numberOfPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
        this.MaxSkill = MaxSkill;
        this.MinSkill = MinSkill;
        this.Password = Password;
        this.gameMode = gameMode;
        this.Player1 = Player1;
        this.Player2 = Player2;
        this.Player3 = Player3;
        this.Player4 = Player4;
        this.state = state;
        this.shufflePlayers = shufflePlayers;
        this.numSpectators = numSpectators;
        this.pausable = pausable;
    }

    /**
     * @return the gameMode
     */
    public Constants.Mode getGameMode() {
        return this.gameMode;
    }

    public String getGameModeString() {
        return this.gameMode.toString();
    }

    /**
     * @param The gameMode to set
     */
    public void setGameMode(Constants.Mode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * @return the gameId
     */
    public Integer getGameId() {
        return this.gameId;
    }

    /**
     * @param gameId the gameId to set
     */
    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    /**
     * @return the gameName
     */
    public String getGameName() {
        return this.gameName;
    }

    /**
     * @param gameName the gameName to set
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * @return the mapId
     */
    public Integer getMapId() {
        return this.mapId;
    }

    /**
     * @param mapId the mapId to set
     */
    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }

    /**
     * @return the Plyer1
     */
    public String getPlayer1() {
        return this.Player1;
    }

    /**
     * @param  Player to set Player1
     */
    public void setPlayer1(String Player) {
        this.Player1 = Player;
    }

    /**
     * @return the Plyer2
     */
    public String getPlayer2() {
        return this.Player2;
    }

    /**
     * @param  Player to set Player2
     */
    public void setPlayer2(String Player) {
        this.Player2 = Player;
    }

    /**
     * @return the Plyer3
     */
    public String getPlayer3() {
        return this.Player3;
    }

    /**
     * @param  Player to set Player3
     */
    public void setPlayer3(String Player) {
        this.Player3 = Player;
    }

    /**
     * @return the Plyer4
     */
    public String getPlayer4() {
        return this.Player4;
    }

    /**
     * @param  Player to set Player4
     */
    public void setPlayer4(String Player) {
        this.Player4 = Player;
    }

    /**
     * @return the quantity of players
     */
    public Integer getNumberOfPlayers() {
        return this.numberOfPlayers;
    }

    /**
     * @param numberOfPlayers set the quantity players
     */
    public void setNumberOfPlayers(Integer numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    /**
     * @return the currentPlayers
     */
    public Integer getCurrentPlayers() {
        return this.currentPlayers;
    }

    /**
     * @param currentPlayers the currentPlayers to set
     */
    public void setCurrentPlayers(Integer currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    /**
     * @return the state
     */
    public String getState() {
        return this.state;
    }

    /**
     * @param Password the Password of this game
     */
    public void setPassword(String Passwort) {
        this.Password = Passwort;
    }

    /**
     * @return Game Password
     */
    public String getPassword() {
        return this.Password;
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
     * @param numberOfPlayers MinSkil of this game
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
     * @return the number of spectators
     */
    public Integer numSpectators() {
        return this.numSpectators;
    }

    /**
     * Set the number of spectators in game.
     * @param numSpectators Number of spectators
     */
    public void setNumSpectators(Integer numSpectators) {
        this.numSpectators = numSpectators;
    }

    public Boolean isSpectateable() {
        return this.numSpectators >= 0;
    }

    public Boolean isPausable() {
        return pausable;
    }

    public void setPausable(Boolean pausable) {
        this.pausable = pausable;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    public Boolean getShufflePlayers() {
        return shufflePlayers;
    }

    public void setShufflePlayers(Boolean shufflePlayers) {
        this.shufflePlayers = shufflePlayers;
    }

    /**
     * @return the String representation of the message.
     */
    @Override
    public String toString() {
        return this.gameId.toString() + " " + "\"" + this.gameName + "\" " + this.mapId.toString() + " " + this.numberOfPlayers + " " + this.currentPlayers + " " + this.getMaxSkill() + " " + this.getMinSkill() + " " + "" + this.getGameMode().getValue() + " " + "\"" + MessageUtil.prepareToSend(this.getPassword()) + "\" " + "\"" + MessageUtil.prepareToSend(this.getPlayer1()) + "\" " + "\"" + MessageUtil.prepareToSend(this.getPlayer2()) + "\" " + "\"" + MessageUtil.prepareToSend(this.getPlayer3()) + "\" " + "\"" + MessageUtil.prepareToSend(this.getPlayer4()) + "\" " + "\"" + this.state + "\"" + " " + (this.getShufflePlayers() ? "1" : "0")+" "+this.numSpectators+" "+(this.pausable ? "1" : "0");
    }

    /**
     * @param messageString the messageString
     */
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setGameId(Integer.valueOf(matcher.group(2)));
            this.setGameName(matcher.group(3));
            this.setMapId(Integer.valueOf(matcher.group(4)));
            this.setNumberOfPlayers(Integer.valueOf(matcher.group(5)));
            this.setCurrentPlayers(Integer.valueOf(matcher.group(6)));
            this.setMaxSkill(Integer.valueOf(matcher.group(7)));
            this.setMinSkill(Integer.valueOf(matcher.group(8)));
            Constants.Mode mode = Constants.Mode.ALLVSALL;
            try {
                int sentModeValue = Integer.valueOf(matcher.group(9));
                Constants.Mode sentMode = Constants.Mode.forValue(sentModeValue);
                if (sentMode != null) mode = sentMode;
            } catch (Exception ex) {}
            this.setGameMode(mode);
            this.setPassword(matcher.group(10));
            this.setPlayer1(matcher.group(11));
            this.setPlayer2(matcher.group(12));
            this.setPlayer3(matcher.group(13));
            this.setPlayer4(matcher.group(14));
            this.setState(matcher.group(15));
            this.setShufflePlayers(matcher.group(16).equals("1"));
            this.setNumSpectators(Integer.parseInt(matcher.group(17)));
            this.setPausable(matcher.group(18).equals("1"));
        }
    }
}

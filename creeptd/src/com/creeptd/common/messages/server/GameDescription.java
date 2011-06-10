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
    private String Passwort = "";
    private String Player1;
    private String Player2;
    private String Player3;
    private String Player4;
    private Integer MaxEloPoints = 0;
    private Integer MinEloPoints = 0;
    private Boolean shufflePlayers = true;
    private String state;
    private Integer GameMod;
    private static final String REG_EXP = "(\\s)*([0-9]+)\\s\"([^\"]+)\"\\s" + "([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)" + "\\s\"([^\"]*)\"\\s\"(.*)\"\\s\"(.*)\"\\s\"(.*)\"\\s\"(.*)\"\\s\"([^\"]+)\"\\s(0|1)(\\s)*";
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
            Integer maxPlayers, Integer currentPlayers, Integer MaxEloPoints, Integer MinEloPoints, String Passwort,
            Integer GameMod,
            String Player1,
            String Player2,
            String Player3,
            String Player4,
            String state, Boolean shufflePlayers) {
        super();
        this.gameId = gameId;
        this.gameName = gameName;
        this.mapId = mapId;
        this.numberOfPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
        this.MaxEloPoints = MaxEloPoints;
        this.MinEloPoints = MinEloPoints;
        this.Passwort = Passwort = "";
        this.GameMod = GameMod;
        this.Player1 = Player1;
        this.Player2 = Player2;
        this.Player3 = Player3;
        this.Player4 = Player4;
        this.state = state;
        this.shufflePlayers = shufflePlayers;
    }

    /**
     * @return the GameMod
     */
    public Integer getGameMod() {
        return this.GameMod;
    }

    public String getGameModString() {
        if (this.GameMod == 0) {
            return "Send to next";
        }
        if (this.GameMod == 1) {
            return "ALL vs ALL";
        }
        if (this.GameMod == 2) {
            return "Send to random";
        }
        if (this.GameMod == 3) {
            return "Team 2vs2";
        }
        if (this.GameMod == 4) {
            return "Last man standing";
        }
        return "Invalid game mod";
    }

    /**
     * @param The GameMod to set
     */
    public void setGameMod(Integer GameMod) {
        this.GameMod = GameMod;
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
    public void setPlyer1(String Player) {
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
    public void setPlyer2(String Player) {
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
    public void setPlyer3(String Player) {
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
    public void setPlyer4(String Player) {
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
     * @param Passwort the Passwort of this game
     */
    public void setPasswort(String Passwort) {
        this.Passwort = Passwort;
    }

    /**
     * @return Game Passwort
     */
    public String getPasswort() {
        return this.Passwort;
    }

    /**
     * @param MaxEloPoints of this game
     */
    public void setMaxEloPoints(Integer MaxEloPoints) {
        this.MaxEloPoints = MaxEloPoints;
    }

    /**
     * @return the number of MaxEloPoints
     */
    public Integer getMaxEloPoints() {
        return this.MaxEloPoints;
    }

    /**
     * @param numberOfPlayers MinEloPoints of this game
     */
    public void setMinEloPoints(Integer MinEloPoints) {
        this.MinEloPoints = MinEloPoints;
    }

    /**
     * @return the number of MinEloPoints
     */
    public Integer getMinEloPoints() {
        return this.MinEloPoints;
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
        return this.gameId.toString() + " " + "\"" + this.gameName + "\" " + this.mapId.toString() + " " + this.numberOfPlayers.toString() + " " + this.currentPlayers + " " + this.getMaxEloPoints() + " " + this.getMinEloPoints() + " " + "" + this.getGameMod() + " " + "\"" + MessageUtil.prepareToSend(this.getPasswort()) + "\" " + "\"" + MessageUtil.prepareToSend(this.getPlayer1()) + "\" " + "\"" + MessageUtil.prepareToSend(this.getPlayer2()) + "\" " + "\"" + MessageUtil.prepareToSend(this.getPlayer3()) + "\" " + "\"" + MessageUtil.prepareToSend(this.getPlayer4()) + "\" " + "\"" + this.state + "\"" + " " + (this.getShufflePlayers() ? "1" : "0");
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
            this.setMaxEloPoints(Integer.valueOf(matcher.group(7)));
            this.setMinEloPoints(Integer.valueOf(matcher.group(8)));
            this.setGameMod(Integer.valueOf(matcher.group(9)));
            this.setPasswort(matcher.group(10));
            this.setPlyer1(matcher.group(11));
            this.setPlyer2(matcher.group(12));
            this.setPlyer3(matcher.group(13));
            this.setPlyer4(matcher.group(14));
            this.setState(matcher.group(15));
            this.setShufflePlayers(matcher.group(16).equals("1"));
        }
    }

    /**
     * Returns true if o is a GameDescription instance with all fields equal
     * to this object's.
     * @param o the object to compare to.
     * @return true if o is equal to this object.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GameDescription)) {
            return false;
        }
        GameDescription d = (GameDescription) o;
        return this.gameId == d.getGameId() && this.gameName.equals(d.getGameName()) && this.mapId == d.getMapId() && this.numberOfPlayers == d.getNumberOfPlayers() && this.currentPlayers == d.getCurrentPlayers() && this.MaxEloPoints == d.getMaxEloPoints() && this.MinEloPoints == d.getMinEloPoints() && this.GameMod == d.getGameMod() && this.Passwort.equals(d.getPasswort()) && this.Player1.equals(d.getPlayer1()) && this.Player2.equals(d.getPlayer2()) && this.Player3.equals(d.getPlayer3()) && this.Player4.equals(d.getPlayer4()) && this.state.equals(d.getState()) && this.shufflePlayers.equals(d.getShufflePlayers());
    }

    /**
     * Returns a hash code for this object.
     * @return a hash code
     */
    @Override
    public int hashCode() {
        return this.gameId;
    }
}

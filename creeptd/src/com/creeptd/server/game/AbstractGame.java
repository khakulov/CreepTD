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
package com.creeptd.server.game;

import java.util.concurrent.atomic.AtomicInteger;

import com.creeptd.common.IConstants;
import com.creeptd.common.messages.client.CreateGameMessage;

public abstract class AbstractGame extends Thread {

    private static AtomicInteger gameCount = new AtomicInteger(0);
    private int gameId;
    private String gameName;
    private int mode = 0; // 0 = Normal, 1 = All vs. All, 2 = Random
    private int mapId;
    private int maxPlayers;
    private String passwort;
    private Integer maxPoints;
    private Integer minPoints;
    private Boolean shufflePlayers = true;

    public AbstractGame(CreateGameMessage message) {
        this.gameId = gameCount.incrementAndGet();
        this.gameName = message.getGameName();
        this.mode = message.getGameMode();
        this.mapId = message.getMapId();
        this.maxPlayers = message.getMaxPlayers();
        this.passwort = message.getPasswort();
        this.maxPoints = message.getMaxEloPoints();
        this.minPoints = message.getMinEloPoints();
        this.shufflePlayers = message.getShufflePlayers();
    }

    public int getGameId() {
        return gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public int getMode() {
        return mode;
    }

    public int getMapId() {
        return mapId;
    }

    public void setRandomMap() {
        this.mapId = (int) ((Math.random() * IConstants.Map.values().length) + 1);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getPasswort() {
        return passwort;
    }

    public Integer getMaxPoints() {
        return maxPoints;
    }

    public Integer getMinPoints() {
        return minPoints;
    }

    public Boolean getShufflePlayers() {
        return shufflePlayers;
    }

    @Override
    public String toString() {
        return this.getGameId() + "/" + this.getGameName();
    }
}

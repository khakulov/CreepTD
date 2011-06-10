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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.creeptd.common.IConstants;
import com.creeptd.common.messages.server.MessageMessage;
import com.creeptd.server.PersistenceManager;
import com.creeptd.server.client.Client;
import com.creeptd.server.game.states.RunningGameState;
import com.creeptd.server.model.Player;

/**
 * Represents a player participating in a game.
 */
public class PlayerInGame {

    private Client client;
    private boolean gameOver;
    private int takedLive = 0;
    private int live = 20;
    private int anticheatCurrentMoney;
    private int anticheatCurrentIncome;
    private HashMap<Integer, String> anticheatTowerId = new HashMap<Integer, String>();
    private HashMap<Integer, Integer> anticheatTowerIdPrices = new HashMap<Integer, Integer>();
    private List<AnticheatItem> items = new ArrayList<AnticheatItem>();
    private long anticheatLastTick = 300L;

    private static enum AnticheatType {

        CREDIT, INCOME
    }

    private static class AnticheatItem {

        public long tick;
        public AnticheatType type;
        public int value;

        public AnticheatItem(long tick, AnticheatType type, int value) {
            this.tick = tick;
            this.type = type;
            this.value = value;
        }
    }

    /**
     * Creates a new one with the given client and gameOver = false.
     *
     * @param client
     *            the client. Must not be null.
     */
    public PlayerInGame(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("'client' was null");
        }
        this.client = client;
        this.gameOver = false;

        this.anticheatCurrentMoney = IConstants.CREDITS;
        this.anticheatCurrentIncome = IConstants.START_INCOME;

    }

    /**
     * Returns the client.
     *
     * @return the client.
     */
    public Client getClient() {
        return this.client;
    }

    /**
     * The gameOver flag (true if this client has sent a GAME_OVER message).
     *
     * @return the gameOver flag.
     */
    public boolean getGameOver() {
        return this.gameOver;
    }

    /**
     * Sets the gameOver flag.
     */
    public void gameOver() {
        this.gameOver = true;
    }

    /**
     * Incrase Takedlive
     */
    public void incraseTakedLive() {
        this.takedLive++;
    }

    /**
     * Take live
     */
    public void takeLive() {
        this.live--;
    }

    public int getLive() {
        return this.live;
    }

    public int getTakedLive() {
        return this.takedLive;
    }

    @Override
    public String toString() {
        return this.client.toString();
    }
    /*
     * Anticheat stuff starts here, could be used in the handlers of
     * RunningGameState
     */

    /**
     * Get the clients calculated current money.
     *
     * The server doesn't get a message if a creep walks through a players game
     * context (map). So it's purposed that all incoming creeps are getting
     * killed. This leads to a error.
     *
     * So bear in mind that you have to add for example 2* IConstants.LIVE *
     * bounty of the best creeps sent * the number of alive opponents * 2. So
     * you can be sure that if this player gets over this amount of money, he's
     * definitely cheating!
     *
     * This is a ugly approximation!
     *
     * Possible solution: - Every client sends a message if a creep crosses the
     * context (map) - Calculate the game on the server-side
     *
     * If this return value plus tolerance is less than zero, this client is
     * pretty sure cheating!
     *
     * @param atTick
     *            the current tick-time
     * @return money the client has at this tick-time (with error!)
     *
     */
    public synchronized int anticheat_getCurrentMoney() {
        return this.anticheatCurrentMoney;
    }

    public synchronized int anticheat_getCurrentIncome() {
        return this.anticheatCurrentIncome;
    }

    /**
     * Update creeps + money of this client.
     */
    public synchronized void anticheat_updateItems(long tick) {
        List<AnticheatItem> removedItems = new ArrayList<AnticheatItem>();
        synchronized (this.items) {
            if (this.items.size() > 0) {
                for (AnticheatItem item : this.items) {
                    if (item.tick <= tick) {
                        if (item.type == AnticheatType.CREDIT) {
                            this.anticheatCurrentMoney += item.value;
                            removedItems.add(item);
                        }
                        if (item.type == AnticheatType.INCOME) {
                            this.anticheatCurrentIncome += item.value;
                            removedItems.add(item);
                        }
                    }
                }
                for (AnticheatItem item : removedItems) {
                    items.remove(item);
                }
            }
        }
    }

    /**
     * Update money of this client.
     *
     * @param tick
     *            the current tick-time
     */
    public synchronized void anticheat_updateMoney(long tick) {
        if (this.anticheatLastTick >= tick) {
            return;
        }
        if (tick % (IConstants.INCOME_TIME / IConstants.TICK_MS) == 0) {
            long checkTick = tick - (IConstants.INCOME_TIME / IConstants.TICK_MS);
            if (checkTick + 1 > 0) {
                this.anticheat_updateItems(checkTick);
                this.anticheatCurrentMoney += this.anticheatCurrentIncome;
            }
        }

        this.anticheatLastTick = tick;
    }

    /**
     * Handles towers being built.
     *
     * Creates a new tower by id in a list. So later the tower can be upgraded
     * or sold and we know the price. It also decreases the currentMoney Value.
     *
     * @param m
     *            BuildTowerMessage to extract type of tower
     * @param id
     *            Identification Number of the new generated tower
     *
     */
    public synchronized void anticheat_TowerBuilt(String towerType, int towerId, long roundID) {
        this.anticheatTowerId.put(towerId, towerType);
        this.anticheatTowerIdPrices.put(towerId, IConstants.Towers.valueOf(
                IConstants.Towers.class, this.anticheatTowerId.get(towerId)).getPrice());
        synchronized (this.items) {
            items.add(new AnticheatItem(roundID, AnticheatType.CREDIT, -1 * this.anticheatTowerIdPrices.get(towerId)));
        }
    }

    /**
     * Handles towers being sold.
     *
     * @param m
     *            SellTowerMessage
     *
     */
    public synchronized void anticheat_TowerSold(int towerId, long roundID) {
        synchronized (this.items) {
            items.add(new AnticheatItem(roundID, AnticheatType.CREDIT, (int) (this.anticheatTowerIdPrices.get(towerId) * 0.75)));
        }
        this.anticheatTowerId.remove(towerId);
        this.anticheatTowerIdPrices.remove(towerId);
    }

    /**
     * Handles towers being upgraded.
     *
     * @param m
     *            UpgradeTowerMessage
     */
    public synchronized void anticheat_TowerUpgraded(int towerId, long roundID) {
        String nameOfNextTower = IConstants.Towers.valueOf(
                this.anticheatTowerId.get(towerId)).getNext().name();

        this.anticheatTowerId.remove(towerId);
        this.anticheatTowerId.put(towerId, nameOfNextTower);

        int oldPrice = this.anticheatTowerIdPrices.get(towerId);
        int newPrice = IConstants.Towers.valueOf(IConstants.Towers.class,
                this.anticheatTowerId.get(towerId)).getPrice();
        this.anticheatTowerIdPrices.remove(towerId);
        this.anticheatTowerIdPrices.put(towerId, oldPrice + newPrice);

        synchronized (this.items) {
            items.add(new AnticheatItem(roundID, AnticheatType.CREDIT, -1 * newPrice));
        }
    }

    /**
     * Handles creeps this client sent.
     *
     * It decreases the anticheatCurrentMoney with the price of the creep from
     * this client and increases anticheatCurrentIncome according to the type of
     * creep.
     *
     * @param creepType
     */
    public void anticheat_sentThisCreep(String creepType, long roundID) {
        synchronized (this.items) {
            items.add(new AnticheatItem(roundID, AnticheatType.CREDIT, -1 * IConstants.Creeps.valueOf(IConstants.Creeps.class, creepType).getPrice()));
            items.add(new AnticheatItem(roundID, AnticheatType.INCOME, IConstants.Creeps.valueOf(IConstants.Creeps.class, creepType).getIncome()));
        }
    }

    /**
     * Handles received creeps.
     *
     * Bear in mind that this function is called for the right client!
     *
     * Every smashed creep gives money.
     *
     * This increases the anticheatCurrentMoney according to the type of creep.
     * It is purposed that the client always kills all enemies an gets the
     * bounty of it. Because the server doesn't know when a creep walks through
     * the client's game context (map). This leads to a small error in the
     * calculation.
     *
     * @param creepType
     */
    public void anticheat_receivedThisCreep(String creepType, long roundID) {
        synchronized (this.items) {
            items.add(new AnticheatItem(roundID, AnticheatType.CREDIT, IConstants.Creeps.valueOf(IConstants.Creeps.class, creepType).getBounty()));
        }
    }

    /**
     * Handles transfered creeps.
     *
     * @param creepType
     */
    public void anticheat_transferThisCreep(String creepType, long roundID) {
        synchronized (this.items) {
            items.add(new AnticheatItem(roundID, AnticheatType.CREDIT, -1 * IConstants.Creeps.valueOf(IConstants.Creeps.class, creepType).getBounty()));
        }
    }

    /**
     *
     */
    public synchronized void anticheat_kickAndBan(RunningGameState state) {
        /*
         * KICK
         */
        state.getGame().sendAll(
                new MessageMessage("Server", "<span style=\"color:red;\">" + this.getClient().getPlayerModel().getName() + " was kicked by <b>System</b></span>"));
        state.getGame().sendAll(
                new MessageMessage("Server", this.getClient().getPlayerModel().getName() + " has left..."));
        // TODO state.removeClient(this.getClient(), "Kick");
        this.getClient().disconnect();

        /*
         * BAN
         */
        EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        Player player = entityManager.find(Player.class, this.getClient().getPlayerModel().getName());
        if (player != null) {
            player.setBlocked(true);
            entityManager.merge(player);
            entityManager.flush();
        }

        entityTransaction.commit();
    }
}

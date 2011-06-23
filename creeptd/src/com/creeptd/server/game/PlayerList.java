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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.creeptd.server.client.Client;

/**
 * A list of the players participating in a game.
 */
public class PlayerList implements Iterable<PlayerInGame>, Cloneable {

    private List<PlayerInGame> players;

    /**
     * Create a new, empty one.
     */
    public PlayerList() {
        this.players = new ArrayList<PlayerInGame>();
    }

    /**
     * Returns an iterator over the players.
     * @return an iterator that yields the players in the right order.
     */
    public Iterator<PlayerInGame> iterator() {
        return this.players.iterator();
    }

    /**
     * Returns true if this PlayerList contains zero players.
     * @return true is this list is empty.
     */
    public boolean isEmpty() {
        boolean b = false;
        synchronized (this.players) {
            b = this.players.isEmpty();
        }
        return b;
    }

    /**
     * Add a player, which will be wrapped in a PlayerInGame object.
     * @param c the client to add. Must not be null.
     */
    public void add(Client c) {
        if (c == null) {
            throw new IllegalArgumentException("'c' was null");
        }
        synchronized (this.players) {
            this.players.add(new PlayerInGame(c));
            Collections.sort(
                    this.players,
                    new Comparator<PlayerInGame>() {

                        public int compare(PlayerInGame p1, PlayerInGame p2) {
                            Client c1 = p1.getClient();
                            Integer id1 = c1.getId();
                            Client c2 = p2.getClient();
                            Integer id2 = c2.getId();
                            return id1.compareTo(id2);
                        }
                    });
        }
    }

    /**
     * Returns the player with the given id.
     * @param clientId the clientId to look for
     * @return the player with the given id, or null if it can't be found.
     */
    public PlayerInGame get(int clientId) {
        PlayerInGame p = null;
        synchronized (this.players) {
            int i = find(clientId);
            if (i != -1) {
                p = this.players.get(i);
            }
        }
        return p;

    }

    /**
     * Returns the player in the given index.
     * @param index the index in the list
     * @return the player in the given index of list, or null if it can't be found.
     */
    public PlayerInGame getAt(int index) {
        PlayerInGame p = null;
        synchronized (this.players) {
            if (index < this.players.size()) {
                p = this.players.get(index);
            }
        }
        return p;
    }

    /**
     * Returns the player with the given username.
     * @param userName the userName to look for
     * @return the player with the given username, or null if there is it
     * can't be found.
     */
    public PlayerInGame get(String userName) {
        PlayerInGame p = null;
        synchronized (this.players) {
            for (PlayerInGame player : this.players) {
                if (player.getClient().getPlayerModel().getName().equals(userName)) {
                    p = player;
                }
            }
        }
        return p;
    }

    /**
     * Find the player with the given id.
     * @param clientId the clientId to look for
     * @return an index into the 'players' list
     */
    private int find(int clientId) {
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getClient().getId() == clientId) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Remove the player with the given id from the list.
     * @param clientId the clientId to look for
     * @return the player that was remove; or null if it could not be found
     */
    public PlayerInGame remove(int clientId) {
        PlayerInGame p = null;
        synchronized (this.players) {
            int i = find(clientId);
            if (i != -1) {
                p = this.players.remove(i);
            }
        }
        return p;
    }

    /**
     * Returns the successor of the player with the given id (the next one on
     * the playing field).
     * @param clientId the clientId to look for
     * @return the successor of the player with 'clientId', or null it that one
     * could not be found
     */
    public PlayerInGame succ(int clientId) {
        PlayerInGame p = null;
        synchronized (this.players) {
            int i = find(clientId);
            if (i != -1) {
                p = this.players.get(i == this.players.size() - 1 ? 0 : i + 1);
            }
        }
        return p;
    }

    /**
     * Return the number of players in this list.
     * @return the number of players in this list.
     */
    public int size() {
        int size = 0;
        synchronized (this.players) {
            size = this.players.size();
        }
        return size;
    }

    /**
     * Changes the players' order randomly.
     */
    public void shuffle() {
        List<PlayerInGame> newList = new LinkedList<PlayerInGame>();
        synchronized (this.players) {
            List<PlayerInGame> oldList = this.players;
            Collections.shuffle(oldList);
            Random random = new Random();
            while (!oldList.isEmpty()) {
                int i = random.nextInt(oldList.size());
                newList.add(oldList.remove(i));
            }
        }
        this.players = newList;
    }

    @Override
    public PlayerList clone() {
        try {
            PlayerList pl = (PlayerList) super.clone();
            ArrayList<PlayerInGame> al = new ArrayList<PlayerInGame>();
            synchronized (this.players) {
                for (PlayerInGame p : this.players) {
                    al.add(p);
                }
            }
            pl.players = al;
            return pl;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

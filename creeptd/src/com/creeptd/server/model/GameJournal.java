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
package com.creeptd.server.model;

import com.creeptd.server.PersistenceManager;
import java.util.List;
import java.util.Random;
import javax.persistence.Query;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
// @Table(name = "GameJournal", uniqueConstraints = {@UniqueConstraint(columnNames={"gameKey"})})
public class GameJournal {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String gameKey = null;
    private String name;
    private String map;
    private long start_date;
    private long end_date;
    private int numPlayers;
    private String player1;
    private String player2;
    private String player3;
    private String player4;
    private int player1_points;
    private int player1_given_points;
    private int player2_points;
    private int player2_given_points;
    private int player3_points;
    private int player3_given_points;
    private int player4_points;
    private int player4_given_points;
    private int player1_skill;
    private int player1_given_skill;
    private int player2_skill;
    private int player2_given_skill;
    private int player3_skill;
    private int player3_given_skill;
    private int player4_skill;
    private int player4_given_skill;
    private int player1_position;
    private int player2_position;
    private int player3_position;
    private int player4_position;
    private String ip1;
    private String ip2;
    private String ip3;
    private String ip4;
    private String mac1;
    private String mac2;
    private String mac3;
    private String mac4;

    private static String generateKey(int len) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        String key = "";
        for (int i=0; i<len; i++) {
            key += chars.charAt(new Random().nextInt(chars.length()));
        }
        return key;
    }

    private static boolean existsGameKey(String gameKey) {
        EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
        String queryString = "SELECT id FROM GameJournal WHERE gameKey='"+gameKey+"'";
        Query query = entityManager.createNativeQuery(queryString, GameJournal.class);
        List<?> resultList = query.getResultList();
        if ((resultList != null) && (resultList.size() > 0)) {
            return true;
        }
        return false;
    }

    public GameJournal() {
        super();
        if (this.gameKey == null) {
            do {
                this.gameKey = generateKey(32);
            } while (existsGameKey(this.gameKey));
        }
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the map
     */
    public String getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(String map) {
        this.map = map;
    }

    /**
     * @return the start_date
     */
    public long getStart_date() {
        return start_date;
    }

    /**
     * @param start_date the start_date to set
     */
    public void setStart_date(long start_date) {
        this.start_date = start_date;
    }

    /**
     * @return the end_date
     */
    public long getEnd_date() {
        return end_date;
    }

    /**
     * @param end_date the end_date to set
     */
    public void setEnd_date(long end_date) {
        this.end_date = end_date;
    }

    /**
     * @return the numPlayers
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * @param numPlayers the numPlayers to set
     */
    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    /**
     * @return the player1
     */
    public String getPlayer1() {
        return player1;
    }

    /**
     * @param player1 the player1 to set
     */
    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    /**
     * @return the player2
     */
    public String getPlayer2() {
        return player2;
    }

    /**
     * @param player2 the player2 to set
     */
    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    /**
     * @return the player3
     */
    public String getPlayer3() {
        return player3;
    }

    /**
     * @param player3 the player3 to set
     */
    public void setPlayer3(String player3) {
        this.player3 = player3;
    }

    /**
     * @return the player4
     */
    public String getPlayer4() {
        return player4;
    }

    /**
     * @param player4 the player4 to set
     */
    public void setPlayer4(String player4) {
        this.player4 = player4;
    }

    public int getPlayer1Skill() {
        return player1_skill;
    }

    public void setPlayer1Skill(int skill) {
        this.player1_skill = skill;
    }

    public int getPlayer1Points() {
        return player1_points;
    }

    public void setPlayer1Points(int points) {
        this.player1_points = points;
    }

    public int getPlayer1GivenSkill() {
        return player1_given_skill;
    }

    public void setPlayer1GivenSkill(int skill) {
        this.player1_given_skill = skill;
    }

    public int getPlayer1GivenPoints() {
        return player1_given_points;
    }

    public void setPlayer1GivenPoints(int points) {
        this.player1_given_points = points;
    }

    public int getPlayer2Skill() {
        return player2_skill;
    }

    public void setPlayer2Skill(int skill) {
        this.player2_skill = skill;
    }

    public int getPlayer2Points() {
        return player2_points;
    }

    public void setPlayer2Points(int points) {
        this.player2_points = points;
    }

    public int getPlayer2GivenSkill() {
        return player2_given_skill;
    }

    public void setPlayer2GivenSkill(int skill) {
        this.player2_given_skill = skill;
    }

    public int getPlayer2GivenPoints() {
        return player2_given_points;
    }

    public void setPlayer2GivenPoints(int points) {
        this.player2_given_points = points;
    }

    public int getPlayer3Skill() {
        return player3_skill;
    }

    public void setPlayer3Skill(int skill) {
        this.player3_skill = skill;
    }

    public int getPlayer3Points() {
        return player3_points;
    }

    public void setPlayer3Points(int points) {
        this.player3_points = points;
    }

    public int getPlayer3GivenSkill() {
        return player3_given_skill;
    }

    public void setPlayer3GivenSkill(int skill) {
        this.player3_given_skill = skill;
    }

    public int getPlayer3GivenPoints() {
        return player3_given_points;
    }

    public void setPlayer3GivenPoints(int points) {
        this.player3_given_points = points;
    }

    public int getPlayer4Skill() {
        return player4_skill;
    }

    public void setPlayer4Skill(int skill) {
        this.player4_skill = skill;
    }

    public int getPlayer4Points() {
        return player4_points;
    }

    public void setPlayer4Points(int points) {
        this.player4_points = points;
    }

    public int getPlayer4GivenSkill() {
        return player4_given_skill;
    }

    public void setPlayer4GivenSkill(int skill) {
        this.player4_given_skill = skill;
    }

    public int getPlayer4GivenPoints() {
        return player4_given_points;
    }

    public void setPlayer4GivenPoints(int points) {
        this.player4_given_points = points;
    }

    /**
     * @return the player1_score
     */
    /**
     * @return the player1_position
     */
    public int getPlayer1Position() {
        return player1_position;
    }

    /**
     * @param player1_position the player1_position to set
     */
    public void setPlayer1Position(int player1_position) {
        this.player1_position = player1_position;
    }

    /**
     * @return the player2_position
     */
    public int getPlayer2Position() {
        return player2_position;
    }

    /**
     * @param player2_position the player2_position to set
     */
    public void setPlayer2Position(int player2_position) {
        this.player2_position = player2_position;
    }

    /**
     * @return the player3_position
     */
    public int getPlayer3Position() {
        return player3_position;
    }

    /**
     * @param player3_position the player3_position to set
     */
    public void setPlayer3Position(int player3_position) {
        this.player3_position = player3_position;
    }

    /**
     * @return the player4_position
     */
    public int getPlayer4Position() {
        return player4_position;
    }

    /**
     * @param player4_position the player4_position to set
     */
    public void setPlayer4Position(int player4_position) {
        this.player4_position = player4_position;
    }

    /**
     * @return the ip1
     */
    public String getIp1() {
        return ip1;
    }

    /**
     * @param ip1 the ip1 to set
     */
    public void setIp1(String ip1) {
        this.ip1 = ip1;
    }

    /**
     * @return the ip2
     */
    public String getIp2() {
        return ip2;
    }

    /**
     * @param ip2 the ip2 to set
     */
    public void setIp2(String ip2) {
        this.ip2 = ip2;
    }

    /**
     * @return the ip3
     */
    public String getIp3() {
        return ip3;
    }

    /**
     * @param ip3 the ip3 to set
     */
    public void setIp3(String ip3) {
        this.ip3 = ip3;
    }

    /**
     * @return the ip4
     */
    public String getIp4() {
        return ip4;
    }

    /**
     * @param ip4 the ip4 to set
     */
    public void setIp4(String ip4) {
        this.ip4 = ip4;
    }

    /**
     * @return the mac1
     */
    public String getMac1() {
        return mac1;
    }

    /**
     * @param mac1 the mac1 to set
     */
    public void setMac1(String mac1) {
        this.mac1 = mac1;
    }

    /**
     * @return the mac2
     */
    public String getMac2() {
        return mac2;
    }

    /**
     * @param mac2 the mac2 to set
     */
    public void setMac2(String mac2) {
        this.mac2 = mac2;
    }

    /**
     * @return the mac3
     */
    public String getMac3() {
        return mac3;
    }

    /**
     * @param mac3 the mac3 to set
     */
    public void setMac3(String mac3) {
        this.mac3 = mac3;
    }

    /**
     * @return the mac4
     */
    public String getMac4() {
        return mac4;
    }

    /**
     * @param mac4 the mac4 to set
     */
    public void setMac4(String mac4) {
        this.mac4 = mac4;
    }
}

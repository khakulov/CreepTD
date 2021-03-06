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

import com.creeptd.common.Password;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A registered and persistable player.
 * 
 * @author andreas
 *
 */
@Entity
@Table(name = "Player")
public class Player {

    @Id
    private String nameKey;
    private String name;
    private String password;
    private String email;
    private int points = 0;
    private int skill = 1000;
    private int lastgame_points = 0;
    private int lastgame_skill = 0;
    private Integer lastgame_id = -1;
    private long lastlogin = 0;
    private boolean blocked = false;
    private int permission = 0;
    private String ip;
    private String mac;

    public String getNameKey() {
        return nameKey;
    }

    public void setNameKey(String key) {
        this.nameKey = key;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
        this.setNameKey(this.name.toLowerCase());
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Set and encode new password.
     * 
     * @param password
     */
    public void setAndEncodePassword(String password) {
        this.password = Password.encodePassword(password);
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * @return the skill
     */
    public int getSkill() {
        return this.skill;
    }

    /**
     * @param skill the skill to set
     */
    public void setSkill(int skill) {
        this.skill = skill;
    }

    public int getLastgameSkill() {
        return lastgame_skill;
    }

    public void setLastgameSkill(int skill) {
        this.lastgame_skill = skill;
    }

    public int getLastgamePoints() {
        return lastgame_points;
    }

    public void setLastgamePoints(int points) {
        this.lastgame_points = points;
    }

    public int getLastgameId() {
        return lastgame_id;
    }

    public void setLastgameId(int lastgame_id) {
        this.lastgame_id = lastgame_id;
    }

    /**
     * @return the lastlogin
     */
    public long getLastlogin() {
        return lastlogin;
    }

    /**
     * @param lastlogin the lastlogin to set
     */
    public void setLastlogin(long lastlogin) {
        this.lastlogin = lastlogin;
    }

    /**
     * @return the blocked
     */
    public boolean isBlocked() {
        return blocked;
    }

    /**
     * @param blocked the blocked to set
     */
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    /**
     * @return the permission
     */
    public int getPermission() {
        return permission;
    }

    /**
     * @param permission the permission to set
     */
    public void setPermission(int permissions) {
        this.permission = permissions;
    }

    public void addPermission(int permission) {
        this.permission |= permission;
    }

    public void removePermission(int permission) {
        this.permission &= ~permission;
    }

    public boolean hasPermission(int permission) {
        return (this.permission & permission) != 0;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the mac
     */
    public String getMac() {
        return mac;
    }

    /**
     * @param mac the mac to set
     */
    public void setMac(String mac) {
        this.mac = mac;
    }
}

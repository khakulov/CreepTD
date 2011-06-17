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
package com.creeptd.client.tower;

import java.awt.Color;
import java.awt.Graphics2D;

import com.creeptd.client.game.GameContext;
import com.creeptd.client.grid.Grid;
import com.creeptd.common.Constants;
import com.creeptd.common.Constants.DamageType;

/**
 * Interface for all towers.
 * 
 * @author Philipp
 * 
 */
public interface Tower {

    /**
     * Updates the tower.
     *
     * @param tickNumber
     *            the current tick in the gameloop
     */
    void update(long tickNumber);

    /**
     * Paints the tower and his projectiles.
     *
     * @param g
     *            the graphics2D instance to draw at
     */
    void paint(Graphics2D g);

    /**
     * Paint the shooting effect.
     *
     * @param g
     *            the graphics context
     */
    void paintEffect(Graphics2D g);

    /**
     * A string to show how the tower works and what he does.
     *
     * @return info string, html-markup?
     */
    String getTowerInfo();

    /**
     * A string to display the towers status. Range, Damage, Upgrade cost Maybe
     * these infos for the next upgrade?
     *
     * @return the status string
     */
    String getStatus();

    /**
     * Indicates if the tower can be upgraded.
     *
     * @return true if an upgrade can be made, else false
     */
    boolean isUpgradable();

    /**
     * Upgrades the tower by one level.
     */
    void upgrade();

    /**
     * The money you get by selling the tower.
     *
     * @return the amount of money
     */
    int getSellValue();

    /**
     * Sets the tower to be selected. If a tower is selected, it should be
     * painted with a highlight.
     *
     * @param selected
     *            true if the tower is selected
     */
    void setSelected(boolean selected);

    /**
     * @return true if tower is selected, else false
     */
    boolean isSelected();

    /**
     * sets the color of the tower.
     *
     * @param color
     *            the color
     */
    void setTowerColor(Color color);

    /**
     * gets the color of the tower.
     *
     * @return the color of the tower
     */
    Color getTowerColor();

    /**
     * Sets the build time for the tower. Used to correct the time if the server
     * sends a message.
     *
     * @param buildTime
     *            the remaining build time in rounds
     */
    void setBuildTime(int buildTime);

    /**
     * @return the buildTime
     */
    int getBuildTime();

    /**
     * Getter for the tower's id.
     *
     * @return the id from the tower
     */
    int getId();

    /**
     * Setter for the tower's id.
     *
     * @param id
     *            the id
     * @param id
     */
    void setId(int id);

    /**
     * Setter for the towers upgradeTime.
     *
     * @param upgradeTime
     *            the time
     */
    void setUpgradeTime(int upgradeTime, boolean newUpgrade);

    /**
     * getter for the towers upgradeTime.
     *
     * @return upgradeTime
     */
    int getUpgradeTime();

    /**
     * getter.
     *
     * @return the price
     */
    int getPrice();

    /**
     * setter.
     *
     * @param price
     *            the price to set
     */
    void setPrice(int price);

    /**
     * getter.
     *
     * @return the range
     */
    float getRange();

    /**
     * setter.
     *
     * @param range
     *            the range to set
     */
    void setRange(float range);

    /**
     * getter.
     *
     * @return the coolDown
     */
    int getCoolDown();

    /**
     * setter.
     *
     * @param damage
     *            the damage to set
     */
    void setDamage(int damage);

    /**
     * getter.
     *
     * @return the damage
     */
    int getDamage();

    /**
     * setter.
     *
     * @param damageNow
     *            the damageNow to set
     */
    void setDamageNow(int damageNow);

    /**
     * getter.
     *
     * @return the damageNow
     */
    int getDamageNow();

    /**
     * setter.
     *
     * @param active
     *            the active to set
     */
    void setActive(boolean active);

    /**
     * is active true?
     *
     * @return the active
     */
    boolean isActive();

    /**
     * @param type
     *            the type to set
     */
    void setType(Constants.Towers type);

    /**
     * @return the type
     */
    Constants.Towers getType();

    /**
     * @return the sellPrice
     */
    int getTotalPrice();

    /**
     * @param price
     *            the sellPrice to set
     */
    void setTotalPrice(int price);

    /**
     * @return the slowDamage
     */
    double getSlowRate();

    /**
     * @param slowRate
     *            the slowDamage to set
     */
    void setSlowRate(double slowRate);

    /**
     * @return the damageType
     */
    DamageType getDamageType();

    /**
     * @return the slowTime
     */
    int getSlowTime();

    /**
     * @param slowTime the slowTime to set
     */
    void setSlowTime(int slowTime);

    /**
     *
     * @param coolDown
     *            of the Tower
     */
    void setCoolDown(int coolDown);

    /**
     * @return CoolDownNow
     */
    int getCoolDownNow();

    /**
     *
     * @param coolDownNow
     *            of the Tower
     */
    void setCoolDownNow(int coolDownNow);

    /**
     * getter.
     * @return context
     */
    GameContext getContext();

    /**
     * getter.
     * @return grid
     */
    Grid getGrid();

    /**
     * getter.
     * @return sellTime
     */
    int getSellTime();

    /**
     * Setter.
     * @param sellTime sellTime
     */
    void setSellTime(int sellTime);

    /**
     * @return the upgrading
     */
    boolean isUpgrading();

    /**
     * @param upgrading the upgrading to set
     */
    void setUpgrading(boolean upgrading);

    /**
     * @return the building
     */
    boolean isBuilding();

    /**
     * @param building the building to set
     */
    void setBuilding(boolean building);

    /**
     * @return the strategy
     */
    public FindCreepStrategy getStrategy();

    /**
     * @param strategy
     *            the strategy to set
     */
    public void setStrategy(FindCreepStrategy strategy);

    public void setSelling(boolean selling);

    public boolean isSelling();

    public void setChangingStrategy(boolean changingStrategy);

    public boolean isChangingStrategy();

    public boolean isReady();

    public FindCreepStrategy getSelectedStrategy();

    public void updateSelectedStrategy(FindCreepStrategy fcs);

    public void setRunningTowerUpgrades(int RunningTowerUpgrades);

    public int getRunningTowerUpgrades();
}

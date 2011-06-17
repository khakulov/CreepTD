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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import com.creeptd.client.creep.Creep;
import com.creeptd.client.game.GameContext;
import com.creeptd.client.game.PlayerContext;
import com.creeptd.client.grid.Grid;
import com.creeptd.client.sound.SoundManagement;
import com.creeptd.client.util.Cache;
import com.creeptd.common.Constants;
import com.creeptd.common.Constants.DamageType;
import com.creeptd.common.Constants.Towers;
import java.awt.Composite;

/**
 * Implements the basic methods for a tower.
 * 
 * @author Philipp
 * 
 */
public abstract class AbstractTower implements Tower {

    private Constants.Towers type;
    private GameContext context;
    private boolean selected = false;
    private int price;
    private int totalPrice;
    private int coolDown;
    private int coolDownNow = 0;
    private int id;
    //private int level;
    private int damage;
    private int damageNow;
    private DamageType damageType;
    private double slowRate;
    private int slowTime;
    private int splashRadius;
    // reduction of the damage
    // the damage at the radius is 1 - damageReductionAtRadius
    private double damageReductionAtRadius;
    private float range;
    private Grid grid;
    //private Color buildColor = new Color(255, 200, 0);
    private Color towerColor;
    private int buildTime;
    private boolean building;
    private int upgradeTime;
    private int sellTime;
    private int changeStrategyTime;
    private boolean active = false;
    private boolean upgrading = false;
    private boolean selling = false;
    private boolean changingStrategy = false;
    protected BufferedImage image;
    private AffineTransform translation = new AffineTransform();
    private Creep target = null;
    private ArrayList<Creep> targets = new ArrayList<Creep>();
    public static boolean ALPHA = true;
    public static final AlphaComposite overlayAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);
    public static final AlphaComposite rangeAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.33f);
    public static final AlphaComposite noAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
    private FindCreepStrategy strategy;
    private FindCreepStrategy selectedStrategy;
    /**
     * SoundManagement is public because every kind of tower calculates on its
     * own when/if it shoots. So sound has to played there.
     */
    SoundManagement sound;
    private int runningTowerUpgrades = 0;

    /**
     * @return the strategy
     */
    public FindCreepStrategy getStrategy() {
        return strategy;
    }

    /**
     * @param strategy
     *            the strategy to set
     */
    public void setStrategy(FindCreepStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * @return the sellTime
     */
    public int getSellTime() {
        return sellTime;
    }

    /**
     * @param sellTime
     *            the sellTime to set
     */
    public void setSellTime(int sellTime) {
        this.sellTime = sellTime;
    }

    public void setSelectedStrategy(FindCreepStrategy fcs) {
        this.selectedStrategy = fcs;
        this.changingStrategy = true;
    }

    /**
     * This method is used to update the panel info.
     * @param fcs
     */
    public void updateSelectedStrategy(FindCreepStrategy fcs) {
        this.selectedStrategy = fcs;
    }

    /**
     * @return gets the selected strategy
     */
    public FindCreepStrategy getSelectedStrategy() {
        return this.selectedStrategy;
    }

    /**
     *
     * @return gets the change strategy time
     */
    public int getChangeStrategyTime() {
        return this.changeStrategyTime;
    }

    /**
     *
     * @param changeTime sets the change time for the strategy
     */
    public void setChangeStrategyTime(int changeTime) {
        this.changeStrategyTime = changeTime;
    }

    /**
     * @return the targets
     */
    public ArrayList<Creep> getTargets() {
        return targets;
    }

    /**
     * @param targets
     *            the targets to set
     */
    public void setTargets(ArrayList<Creep> targets) {
        this.targets = targets;
    }

    /**
     * @return the splashRadius
     */
    public int getSplashRadius() {
        return splashRadius;
    }

    /**
     * @param splashRadius
     *            the splashRadius to set
     */
    public void setSplashRadius(int splashRadius) {
        this.splashRadius = splashRadius;
    }

    /**
     * @return the damageReductionAtRadius
     */
    public double getDamageReductionAtRadius() {
        return damageReductionAtRadius;
    }

    /**
     * @param damageReductionAtRadius
     *            the damageReductionAtRadius to set
     */
    public void setDamageReductionAtRadius(double damageReductionAtRadius) {
        this.damageReductionAtRadius = damageReductionAtRadius;
    }

    /**
     * @return the slowDamage
     */
    public double getSlowRate() {
        return slowRate;
    }

    /**
     * @param slowRate
     *            the slowDamage to set
     */
    public void setSlowRate(double slowRate) {
        this.slowRate = slowRate;
    }

    /**
     * @return the damageType
     */
    public DamageType getDamageType() {
        return damageType;
    }

    /**
     * @param damageType
     *            the damageType to set
     */
    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    /**
     * @return the totalPrice
     */
    public int getTotalPrice() {
        return totalPrice;
    }

    /**
     * @param totalPrice
     *            the totalPrice to set
     */
    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * Creates a new instance of AbstractTower.
     *
     * @param context
     *            the gameContext for the tower
     * @param type
     *            the type of the tower
     * @param grid
     *            the grid where the tower is placed
     */
    protected AbstractTower(Constants.Towers type, GameContext context,
            Grid grid) {
        this.setBuilding(false);
        this.buildTime = Constants.USER_ACTION_DELAY;
        this.upgradeTime = 0;
        this.changeStrategyTime = 0;
        this.context = context;
        this.setType(type);
        this.grid = grid;
        grid.setTower(this);
        this.towerColor = type.getTowerColor();
        this.active = false;
        init();

        if (Cache.getInstance().hasArrayTowerImg(this.getType())) {
            this.setImage(Cache.getInstance().getTowerImg(this.getType()));
        } else {
            this.image = new BufferedImage(20, 20,
                    BufferedImage.TYPE_INT_ARGB_PRE);
            generateImage();
            Cache.getInstance().putTowerImg(this.getType(), this.getImage());
        }
        this.translation.setToIdentity();
        this.translation.translate(grid.getLocation()[0], grid.getLocation()[1]);

        this.sound = context.getSoundManagement();


        this.setDamage(type.getDamage());
        this.setDamageNow(type.getDamage());
        this.setRange(type.getRange());
        this.setCoolDown(type.getSpeed());
        this.setTotalPrice(type.getPrice());
        this.setDamageType(type.getDamageType());
        this.setSplashRadius(type.getSplashRadius());
        this.setDamageReductionAtRadius(type.getDamageReductionAtRadius());
        this.setSlowRate(type.getSlowRate());
        this.setSlowTime(type.getSlowTime());
    }

    /**
     * Init method for tower creation.
     */
    protected abstract void init();

    /**
     * @return the slowTime
     */
    public int getSlowTime() {
        return slowTime;
    }

    /**
     * @param slowTime
     *            the slowTime to set
     */
    public void setSlowTime(int slowTime) {
        this.slowTime = slowTime;
    }

    /**
     * {@inheritDoc}
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    public int getId() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    public String getStatus() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public int getSellValue() {
        // TODO Auto-generated method stub

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUpgradable() {
        if (this.type.getNext() == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Creates the image for the tower.
     */
    public abstract void generateImage();

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D g) {

        AffineTransform save = g.getTransform();

        g.transform(translation);

        g.drawImage(image, 0, 0, null);

        g.setTransform(save);
    }

    /**
     * {@inheritDoc}
     */
    public void paintEffect(Graphics2D g) {
        AffineTransform save = g.getTransform();

        g.transform(translation);

        if (isSelected()) {
            Composite oldComposite = g.getComposite();
            Constants.Towers current = this.type;
            while (current != null) {
                Arc2D rangeArcUpgrade = new Arc2D.Float();
                rangeArcUpgrade.setArcByCenter(Grid.SIZE / 2, Grid.SIZE / 2, current.getRange(), 0.0, 360.0, Arc2D.CHORD);
                g.setColor(current.getTowerColor());
                if (ALPHA) {
                    if (!this.type.equals(current)) {
                        g.setComposite(rangeAlpha);
                    } else {
                        g.setComposite(noAlpha);
                    }
                }
                g.draw(rangeArcUpgrade);
                current = current.getNext();
            }
            g.setComposite(oldComposite);
            g.setColor(Color.YELLOW);
            g.drawRect(0, 0, Grid.SIZE, Grid.SIZE);
        }

        // works only if a client does not lag too much
        // or the build time can be bigger than USER_ACTION_DELAY
        if (this.buildTime > 0) {

            g.setColor(Color.BLACK);
            g.fillRect(20 - 20 * this.buildTime / Constants.USER_ACTION_DELAY,
                    1, 20 * this.buildTime / Constants.USER_ACTION_DELAY, 19);
        }

        if (this.upgradeTime > 0) {
            g.setColor(Color.BLUE);
            g.fillRect(1, 1, 20 * this.upgradeTime / Constants.USER_ACTION_DELAY, 3);
        }

        if (this.sellTime > 0) {
            g.setColor(Color.RED);
            g.fillRect(1, 1, 20 * this.sellTime / Constants.USER_ACTION_DELAY, 3);
        }

        if (this.changeStrategyTime > 0) {
            g.setColor(Color.ORANGE);
            g.fillRect(1, 1, 20 * this.changeStrategyTime / Constants.USER_ACTION_DELAY, 3);
        }

        g.setTransform(save);
    }

    /**
     * {@inheritDoc}
     */
    public void update(long tickNumber) {
        // upgrade and build animation
        if (this.buildTime > 0) {
            if (isBuilding()) {
                this.buildTime--;
                if (this.buildTime == 0) {
                    this.active = true;
                    this.grid.setOccupiedStatus(false);
                }
            }
        }

        if (!this.isActive()) {
            return;
        }

        // strategy change update
        if (this.changeStrategyTime > 0) {
            this.changeStrategyTime--;
            if (this.changeStrategyTime == 0) {
                if (this.selectedStrategy != null) {
                    this.strategy = this.selectedStrategy;
                    this.changingStrategy = false;
                    this.grid.setOccupiedStatus(false);
                }
            }
        }

        // sell animation
        if (sellTime > 0) {
            sellTime--;
            if (sellTime == 0) {
                if (this.getContext() instanceof PlayerContext) {
                    getContext().setCredits(getContext().getCredits() + (int) (this.getTotalPrice() * 0.75));
                    this.grid.setOccupiedStatus(false);
                }
                getContext().removeTower((Tower) this);
            }
        }


        //upgrade animation
        if (this.upgradeTime > 0) {
            this.upgradeTime--;
            if (this.runningTowerUpgrades > 0 && this.upgradeTime == Constants.USER_ACTION_DELAY) {

                this.upgrade();
                this.runningTowerUpgrades = 0;
                getContext().fireSelectedChangedEvent("tower");
                getContext().fireSelectedChangedEvent("upgrade");
                this.grid.setOccupiedStatus(false);

            } else if (this.upgradeTime == 0) {
                this.upgrade();
                this.upgrading = false;
                getContext().fireSelectedChangedEvent("tower");
                getContext().fireSelectedChangedEvent("upgrade");
                this.grid.setOccupiedStatus(false);
            }
        }

        // decrease cooldown
        if (getCoolDownNow() > 0) {
            this.setCoolDownNow(getCoolDownNow() - 1);
        }

        // find Creep
        if (this.getCoolDownNow() == 0) {
            double xDiff, yDiff, dist, reduction;
            int damage;
            Creep target = this.getStrategy().findCreep();
            this.setTarget(target);

            // splash shoot
            if (this.getSplashRadius() > 0) {
                if (getTarget() != null && this.getTarget().isValidTarget()) {

                    getTargets().clear();
                    float x = target.getX();
                    float y = target.getY();
                    Creep next;

                    // find creeps in splashradius
                    for (int i = 0; i < getContext().getCreeps().size(); i++) {
                        if (getContext().getCreeps().get(i).isValidTarget()) {
                            next = getContext().getCreeps().get(i);

                            if ((x - next.getX() <= getSplashRadius()) && (x - next.getX() >= (getSplashRadius() * -1)) && (y - next.getY() <= getSplashRadius()) && (y - next.getY() >= (getSplashRadius() * -1))) {
                                getTargets().add(next);

                            }

                        }

                    }
                    // damage them
                    for (Creep creep : getTargets()) {
                        if (creep != null && creep.isValidTarget()) {
                            xDiff = creep.getX() - getTarget().getX();
                            yDiff = creep.getY() - getTarget().getY();
                            dist = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

                            damage = this.getDamageNow();

                            // if the distance is high the damage becomes lower
                            reduction = ((getDamageReductionAtRadius() / getSplashRadius()) * dist);
                            reduction = damage * reduction;
                            creep.damageCreep(damage - (int) reduction,
                                    (Tower) this);

                        }
                    }
                    this.setCoolDownNow(getCoolDown());
                    // play shoot-sound
                    if (sound != null) {
                        sound.towerShootsSound(this.getType());
                    }
                }
                // normal shot
            } else {
                if (this.getTarget() != null && this.getTarget().isValidTarget()) {
                    this.getTarget().damageCreep(this.getDamageNow(),
                            (Tower) this);
                    this.setCoolDownNow(getCoolDown());
                    // play shoot-sound
                    if (sound != null) {
                        sound.towerShootsSound(this.getType());
                    }
                }
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public void upgrade() {

        Towers nameOfNextTower = this.type.getNext();

        if (nameOfNextTower != null) {
            this.damage = nameOfNextTower.getDamage();
            this.damageNow = nameOfNextTower.getDamage();
            this.range = nameOfNextTower.getRange();
            this.coolDown = nameOfNextTower.getSpeed();
            this.setDamageReductionAtRadius(nameOfNextTower.getDamageReductionAtRadius());
            this.setSplashRadius(nameOfNextTower.getSplashRadius());
            this.setSlowRate(nameOfNextTower.getSlowRate());
            this.setSlowTime(nameOfNextTower.getSlowTime());
            this.setTowerColor(nameOfNextTower.getTowerColor());
            this.setTotalPrice(getTotalPrice() + nameOfNextTower.getPrice());
            this.setCoolDownNow(getCoolDown() - 1);
            this.type = nameOfNextTower;

            if (Cache.getInstance().hasArrayTowerImg(this.getType())) {
                this.setImage(Cache.getInstance().getTowerImg(this.getType()));
            } else {
                this.image = new BufferedImage(20, 20,
                        BufferedImage.TYPE_INT_ARGB_PRE);
                generateImage();
                Cache.getInstance().putTowerImg(this.getType(), this.getImage());
            }

            context.repaintBoard();

        }
        // play music
        if (sound != null) {
            sound.towerUpgradeSound(this.type);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSelected() {
        return this.selected;
    }

    /**
     * {@inheritDoc}
     */
    public void setTowerColor(Color color) {
        this.towerColor = color;
    }

    /**
     * {@inheritDoc}
     */
    public Color getTowerColor() {
        return this.towerColor;
    }

    /**
     * {@inheritDoc}
     */
    public void setBuildTime(int buildTime) {
        this.buildTime = buildTime;
    }

    /**
     * @param target
     *            the target to set
     */
    protected void setTarget(Creep target) {
        this.target = target;
    }

    /**
     * @return the target
     */
    protected Creep getTarget() {
        return target;
    }

    /**
     * @param damage
     *            the damage to set
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * @return the damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @param damageNow
     *            the damageNow to set
     */
    public void setDamageNow(int damageNow) {
        this.damageNow = damageNow;
    }

    /**
     * @return the damageNow
     */
    public int getDamageNow() {
        return damageNow;
    }

    /**
     * @param active
     *            the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(Constants.Towers type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public Constants.Towers getType() {
        return type;
    }

    /**
     * @param grid
     *            the grid to set
     */
    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    /**
     * @return the grid
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * @param image
     *            the image to set
     */
    protected void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * @return the image
     */
    protected BufferedImage getImage() {
        return image;
    }

    /**
     * @return the range
     */
    public float getRange() {
        return range;
    }

    /**
     * @param range
     *            the range to set
     */
    public void setRange(float range) {
        this.range = range;
    }

    /**
     * @return the coolDown
     */
    public int getCoolDown() {
        return coolDown;
    }

    /**
     *
     * @param coolDown
     *            of the Tower
     */
    public void setCoolDown(int coolDown) {
        this.coolDown = coolDown;
    }

    /**
     * @return CoolDownNow
     */
    public int getCoolDownNow() {
        return coolDownNow;
    }

    /**
     *
     * @param coolDownNow
     *            of the Tower
     */
    public void setCoolDownNow(int coolDownNow) {
        this.coolDownNow = coolDownNow;
    }

    /**
     * @return the upgradeTime
     */
    public int getUpgradeTime() {
        return upgradeTime;
    }

    /**
     * @param upgradeTime
     *            the upgradeTime to set
     */
    public void setUpgradeTime(int upgradeTime, boolean newUpgrade) {
        if (this.isUpgrading() && newUpgrade == true) {
            this.runningTowerUpgrades = 1;
            this.upgradeTime += upgradeTime;
        } else {
            this.upgradeTime = upgradeTime;
        }
    }

    /**
     * @return the price
     */
    public int getPrice() {
        return price;
    }

    /**
     * @param price
     *            the price to set
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * @param the RunningTowerUpgrades
     */
    public void setRunningTowerUpgrades(int RunningTowerUpgrades) {
        runningTowerUpgrades = RunningTowerUpgrades;
    }

    /**
     * @return the RunningTowerUpgrades
     */
    public int getRunningTowerUpgrades() {
        return runningTowerUpgrades;
    }

    /**
     * @return the buildTime
     */
    public int getBuildTime() {
        return buildTime;
    }

    /**
     * @return the context
     */
    public GameContext getContext() {
        return context;
    }

    /**
     * @return the upgrading
     */
    public boolean isUpgrading() {
        return upgrading;
    }

    /**
     * @param upgrading the upgrading to set
     */
    public void setUpgrading(boolean upgrading) {
        this.upgrading = upgrading;
    }

    /**
     * @return the building
     */
    public boolean isBuilding() {
        return building;
    }

    /**
     * @param building the building to set
     */
    public void setBuilding(boolean building) {
        this.building = building;
    }

    public boolean isSelling() {
        return this.selling;
    }

    public void setSelling(boolean selling) {
        this.selling = selling;
    }

    public void setChangingStrategy(boolean changingStrategy) {
        this.changingStrategy = changingStrategy;
    }

    public boolean isChangingStrategy() {
        return this.changingStrategy;
    }

    /**
     * Returns if this tower is ready for selling, upgrading or changing strategy.
     *
     * @return boolean
     */
    public boolean isReady() {
        return (this.active && !this.isUpgrading() && !this.isSelling() && !this.isChangingStrategy());
    }
}

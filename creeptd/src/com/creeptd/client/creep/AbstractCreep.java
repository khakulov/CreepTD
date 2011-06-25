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
package com.creeptd.client.creep;

import static com.creeptd.common.Constants.EAST;
import static com.creeptd.common.Constants.NORTH;
import static com.creeptd.common.Constants.SOUTH;
import static com.creeptd.common.Constants.WEST;
import static com.creeptd.common.Constants.WEST_MINUS;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import com.creeptd.client.game.GameContext;
import com.creeptd.client.sound.SoundManagement;
import com.creeptd.client.tower.Tower;
import com.creeptd.common.Constants;
import com.creeptd.common.messages.client.CreepEscapedMessage;

/**
 * Abstract implementation for a creep.
 * 
 * @author Philipp
 * 
 */
public abstract class AbstractCreep implements Creep {

    private static Logger logger = Logger.getLogger(AbstractCreep.class.getName());
    private GameContext context;
    private Constants.Creeps type;
    private long roundID;
    private int playerID;
    private int price;
    private int id;
    private int senderId;
    private int bounty;
    private int incomePercentage;
    private int income;
    private int health;
    private int healthMax;
    private float speed;
    private boolean isSlowed;
    private int slowTime;
    private boolean slowImmune = false;
    private int regenerationRate = 0;
    private int buildTime;
    private boolean active = false;
    private float xLocation;
    private float yLocation;
    private int height = 20;
    private int width = 20;
    private double rotateWert = 0.0;
    protected BufferedImage image;
    private Line2D healthBar;
    private Color healthBarColor = Color.GREEN;
    // the current segment
    private int segment = 0;
    // the step within the segment (1000 steps per segment)
    private int segmentStep = 0;
    // the total segment steps moved
    private int totalSegmentSteps = 0;
    private float xOld;
    private float yOld;
    private AffineTransform translation;
    private AffineTransform rotation;
    private double deltaChanged = 0;
    /** Number of done transfers */
    private int transferCount = 0;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the slowImmune
     */
    public boolean isSlowImmune() {
        return slowImmune;
    }

    /**
     * @return gets the total segment steps moved
     */
    public int getTotalSegmentSteps() {
        return this.totalSegmentSteps;
    }

    /**
     * @param slowImmune
     *            the slowImmune to set
     */
    public void setSlowImmune(boolean slowImmune) {
        this.slowImmune = slowImmune;
    }

    /**
     * @return the slowTime
     */
    public synchronized int getSlowTime() {
        return slowTime;
    }

    public int getRegenerationRate() {
        return this.regenerationRate;
    }

    public void setRegenerationRate(int rate) {
        this.regenerationRate = rate;
    }

    /**
     * @param slowTime
     *            the slowTime to set
     */
    public synchronized void setSlowTime(int slowTime) {
        this.slowTime = slowTime;
    }

    public int getTransferCount() {
        return transferCount;
    }

    public void setTransferCount(int transferCount) {
        this.transferCount = transferCount;
    }

    /**
     * Creates a new instance of this class.
     *
     * @param context
     *            the game context
     * @param type
     *            the type of the creep
     */
    protected AbstractCreep(GameContext context, Constants.Creeps type) {
        this.context = context;
        this.type = type;

        // set the position at this point to avoid that towers recognize
        // the creep with the wrong position at the first appearance of the
        // creep.
        float[] pos = context.getGameBoard().getPath().getStep(this.segment,
                this.segmentStep);
        this.setPosition(pos[0], pos[1]);
        this.segmentStep++;

        translation = new AffineTransform();
        rotation = new AffineTransform();

        setBounty(type.getBounty());
        setHealthMax(type.getHealth());
        setHealth(type.getHealth());
        setSpeed(type.getSpeed());
        setIncomePercentage(type.getIncomePercentage()); // Sets income, too
        setRegenerationRate(type.getRegenerationRate());
    }

    /**
     * Implemented by the subclasses (Creep1 ... Creep 16)
     */
    public abstract void loadImage();

    /**
     * @param id
     *            id
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * @return id
     */
    public int getID() {
        return this.id;
    }

    /**
     * @param width
     *            width
     * @param height
     *            height
     */
    public void setDimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * @param x
     *            x
     * @param y
     *            y
     */
    public void setPosition(float x, float y) {
        this.xLocation = x;
        this.yLocation = y;
    }

    /**
     * @return width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * @return height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * {@inheritDoc}
     */
    public void damageCreep(int damage, Tower t) {
        int h = getHealth();

        switch (t.getDamageType()) {
            case slow:
                if (!isSlowImmune()) {
                    if (!isSlowed) {
                        speed -= speed * t.getSlowRate();
                        setSlowed(true);
                    } else {
                        if (speed > (this.getType().getSpeed() - this.getType().getSpeed() * t.getSlowRate())) {
                            speed = (float) (this.getType().getSpeed() - (this.getType().getSpeed() * t.getSlowRate()));
                        }
                    }
                    setSlowTime(t.getSlowTime());
                }

            default:
                h = h - damage;
                setHealth(h);
        }

        // if the creep is death...
        if (this.health <= 0) {
            // ...play sound
            SoundManagement sm = this.context.getSoundManagement();
            if (sm != null) {
                sm.creepDiesSound(this.type);
            }
            // ...remove the creep
            context.getCreeps().remove(this);
            // ...increase money by bounty
            context.setCredits(context.getCredits() + getBounty());
        }
    }
    
    private void updateHealthBar() {
        // calculate remaining percent of hit points
        float healthPercent = (float) health / (float) healthMax * 100;
        // change color according to remaining hit points
        if (healthPercent <= 10f) {
            this.healthBarColor = Color.RED;
        } else if (healthPercent <= 25f) {
            this.healthBarColor = Color.ORANGE;
        } else if (healthPercent <= 50f) {
            this.healthBarColor = Color.YELLOW;
        } else {
            this.healthBarColor = Color.GREEN;
        }
        // set the bar
        this.healthBar = new Line2D.Float(3f, 2f, ((float) health / (float) healthMax * 14f) + 3f, 2);
    }

    /**
     * {@inheritDoc}
     */
    public void setHealth(int h) {
        this.health = h;
        this.updateHealthBar();
    }

    /**
     * {@inheritDoc}
     */
    public int getHealth() {
        return this.health;
    }

    /**
     * {@inheritDoc}
     */
    public float getSpeed() {
        return this.speed;
    }

    /**
     * {@inheritDoc}
     */
    public float getX() {
        return this.xLocation;
    }

    /**
     * {@inheritDoc}
     */
    public float getY() {
        return this.yLocation;
    }

    /**
     * @param r
     *            r
     */
    public void setRotate(double r) {
        this.rotateWert = r;
    }

    /**
     * @return rotateWert
     */
    public double getRotate() {
        return this.rotateWert;
    }

    /**
     * {@inheritDoc}
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Gets the price of the creep.
     *
     * @return the price
     */
    public int getPrice() {
        return this.price;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValidTarget() {
        return (this.active);

    }

    /**
     * {@inheritDoc}
     */
    public Constants.Creeps getType() {
        return this.type;
    }

    public long getRound() {
        return this.roundID;
    }

    /**
     * Paints the health bar of the creeps.
     *
     * @param g
     *            the Graphics2D for drawing
     */
    private void paintHealthbar(Graphics2D g) {
        // save the stroke
        Stroke s = g.getStroke();
        // set color of stroke
        g.setColor(this.healthBarColor);
        // make the line bigger
        g.setStroke(new BasicStroke(2));
        // paint the line
        g.draw(this.healthBar);
        // set the stroke to what it was before
        g.setStroke(s);
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D g) {
        // only paint when creep is active
        if (this.active) {
            AffineTransform save = g.getTransform();

            translation.setToIdentity();
            translation.translate(getX(), getY());

            // transform to correct location
            g.transform(translation);

            // paint creep health bar
            paintHealthbar(g);

            rotation.setToIdentity();
            // rotate around the image center
            rotation.rotate(rotateWert, 10, 10);

            // apply the rotation to the creep
            g.transform(rotation);
            g.drawImage(image, 0, 0, null);
            g.setTransform(save);
        }

    }

    /**
     * {@inheritDoc}
     */
    public synchronized void update(long tickNumber) {
        roundID = tickNumber;

        if (this.buildTime > 1) {
            buildTime--;
            return;
        } else if (!this.active) {
            this.active = true;
        }

        if (slowTime > 0) {
            slowTime--;
            if (slowTime == 0) {
                setSlowed(false);
                setSpeed(type.getSpeed());
            }
        }
        // store last position
        this.xOld = this.getX();
        this.yOld = this.getY();

        // regenerate
        if (this.regenerationRate > 0 && this.health < this.healthMax) {
            this.health += this.regenerationRate;
            if (this.health > this.healthMax) {
                this.health = this.healthMax;
            }
            this.updateHealthBar();
        }

        // change the segmentStep
        this.segmentStep += speed;
        this.totalSegmentSteps += speed;
        if (this.segmentStep > 1000) {
            // if the segmentStep exceeds the max steps per segment
            // we need to correct
            this.segmentStep -= 1000;
            // and go to the next segment
            this.segment++;

            if (this.segment >= context.getGameBoard().getPath().getLength() - 1) {
                // we have run all along the path
                // for simplicity reset the segment
                // later we need to remove a life from the player
                this.segment = 0;

                // Vote for taken life
                if (this.health > 0) {
                    logger.info("Voting to remove life from " + context.getPlayerName() + ": original sender=" + this.getSenderId()+ ", creep=" + this.getType().getName()+" ("+this.getID()+")");
                    // Send a life taken message (fromPlayer loses a life)
                    CreepEscapedMessage cem = new CreepEscapedMessage();
                    cem.setCreepId(this.getID());
                    cem.setCreepType(this.getType().name());
                    cem.setFromPlayerId(this.getPlayerID());
                    cem.setCreatorId(this.getSenderId());
                    cem.setCreepHealth(this.getHealth());
                    cem.setRoundId(context.getGameLoop().getRoundId());
                    cem.setTransferCount(this.getTransferCount());
                    context.getNetwork().sendMessage(cem);
                } else {
                    SoundManagement sm = this.context.getSoundManagement();
                    if (sm != null) {
                        sm.creepDiesSound(this.type);
                    }
                }
                context.getCreeps().remove(this);
                return;
            }
        }

        /**
         * Berechnet die Poition & den Winkel des Creeps
         */
        int iMAX = context.getGameBoard().getPath().getLength() - 1;

        int i0 = this.segment; // Aktuell
        int i1 = this.segment + 1; // Nächster // der creep befindet sich IMMER
        // irgendwo zwischen i0 & i1
        int i2 = this.segment + 2; // ÜberNächster um den Winkel zu berechnen

        if (i1 > iMAX) {
            i1 = iMAX;
        }
        if (i2 > iMAX) {
            i2 = iMAX;
        }

        float f = (float) this.segmentStep / 1000.0f; // 0 = AktuellerPfad //
        // 0.99f = (fast)
        // NächsterPfad

        // Point p1 = context.getGameBoard().getPointFromPath(i1);
        // p1 *= 20; //funzt nicht ?!? dumme Point-Klasse ?

        float x0 = context.getGameBoard().getPointFromPath(i0).x * 20.0f;
        float y0 = context.getGameBoard().getPointFromPath(i0).y * 20.0f;
        float x1 = context.getGameBoard().getPointFromPath(i1).x * 20.0f;
        float y1 = context.getGameBoard().getPointFromPath(i1).y * 20.0f;
        float x2 = context.getGameBoard().getPointFromPath(i2).x * 20.0f;
        float y2 = context.getGameBoard().getPointFromPath(i2).y * 20.0f;

        float x = x0 * (1.0f - f) + x1 * f;
        float y = y0 * (1.0f - f) + y1 * f;
        this.setPosition(x, y);

        double angleCurrent = calcAngle(x0, y0, x1, y1);
        double angleNext = calcAngle(x1, y1, x2, y2);

        if (angleNext != -1.0d) {
            if (angleCurrent == -1.0d) {
                angleCurrent = angleNext;
            }
            if (angleNext > angleCurrent + Math.PI) {
                angleNext -= Math.PI * 2.0d;
            }
            if (angleNext < angleCurrent - Math.PI) {
                angleNext += Math.PI * 2.0d;
            }
            // IMBA smooth function ^^
            f += 0.5f;
            f %= 1.0f;
            float new_f = (((float) Math.pow(f * 2 - 1, 3) + 1) / 2.0f + f) / 2.0f;
            if (f < 0.5f) {
                new_f += 0.5f;
            } else {
                new_f -= 0.5f;
            }
            this.setRotate(angleCurrent * (1.0f - new_f) + angleNext * new_f);
        }
    }

    /**
     * Berechnet den winkel von anfangs (x) & endpunkt (x2) bei stopps gibt
     * diese funktion -1.0d zurück wegen length = 0;
     */
    static double calcAngle(float x, float y, float x2, float y2) {
        double length = Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
        if (length == 0.0d) {
            return -1.0d; // Double.NAN funktioniert nicht ?!?
        }
        double angle = Math.acos((double) (x2 - x) / length);
        if (y > y2) {
            angle = Math.PI * 2 - angle;
        }
        return angle;
    }

    /**
     * Calculates next angle considering to smooth out big changes by slowly
     * changing the delta on the last 800 segmentSteps / frame rate.
     *
     * @return delta of Angle
     * @param pos
     *            at which position
     */
    public double getDeltaAngle(float[] pos) {
        double delta = Math.atan2(pos[1] - yOld, pos[0] - xOld);

        float[] ptemp0 = context.getGameBoard().getPath().getStep(segment, 1000);

        float[] ptemp1 = new float[]{0F, 0F};

        try {
            ptemp1 = context.getGameBoard().getPath().getStep(segment + 1, 1);
        } catch (Exception e) {
            deltaChanged = delta;
            return delta;
        }

        double deltaFarAway = Math.atan2(ptemp1[1] - ptemp0[1], ptemp1[0] - ptemp0[0]);

        if (delta == EAST || delta == WEST || delta == NORTH || delta == SOUTH) {

            if (deltaFarAway == delta) {
                return delta;
            }

            if (deltaFarAway != delta) {
                // from 0.001 to 1
                double i = (segmentStep / 1000D);

                // walks east at the moment
                if (delta == EAST) {
                    // will turn to south
                    if (deltaFarAway == SOUTH) {
                        delta = SOUTH * i;
                    }
                    // will turn to north
                    if (deltaFarAway == NORTH) {
                        delta = NORTH * i;
                    }
                }
                if (delta == WEST) {
                    // will turn to south
                    if (deltaFarAway == SOUTH) {
                        delta = WEST + NORTH * i;
                    }
                    // will turn to north
                    if (deltaFarAway == NORTH) {
                        delta = WEST_MINUS + SOUTH * i;
                    }
                }
                if (delta == NORTH) {
                    // will turn to west
                    if (deltaFarAway == WEST) {
                        delta = NORTH + NORTH * i;
                    }
                    // will turn to east
                    if (deltaFarAway == EAST) {
                        delta = NORTH + SOUTH * i;
                    }
                }
                if (delta == SOUTH) {
                    // will turn to west
                    if (deltaFarAway == WEST) {
                        delta = SOUTH + SOUTH * i;
                    }
                    // will turn to east
                    if (deltaFarAway == EAST) {
                        delta = SOUTH + NORTH * i;
                    }
                }
                deltaChanged = delta;
                return delta;
            }
        }
        return deltaChanged;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setBuildTime(int buildTime) {
        this.buildTime = buildTime;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized int getBuildTime() {
        return this.buildTime;
    }

    /**
     *
     * @return income
     */
    public int getIncome() {
        return income;
    }

    public int getIncomePercentage() {
        return incomePercentage;
    }

    /**
     *
     * @param income
     *            income
     */
    public void setIncomePercentage(int incomePercentage) {
        this.incomePercentage = incomePercentage;
        this.income = this.price * this.incomePercentage * 100;
    }

    /**
     * @param senderId
     *            the senderId to set
     */
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    /**
     * @return the senderId
     */
    public int getSenderId() {
        return senderId;
    }

    /**
     * @param speed
     *            the speed to set
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * @param height
     *            the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @param width
     *            the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * @param image
     *            the image to set
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * @return the healthMax
     */
    public int getHealthMax() {
        return healthMax;
    }

    /**
     * @param healthMax
     *            the healthMax to set
     */
    public void setHealthMax(int healthMax) {
        this.healthMax = healthMax;
    }

    /**
     * @return gets the bounty
     */
    public int getBounty() {
        return bounty;
    }

    /**
     * @param killMoney
     *            the killMoney to set
     */
    public void setBounty(int bounty) {
        this.bounty = bounty;
    }

    /**
     * @return the isSolwed
     */
    public boolean isSlowed() {
        return isSlowed;
    }

    /**
     * @param isSlowed
     *            the isSolwed to set
     */
    public void setSlowed(boolean isSlowed) {
        this.isSlowed = isSlowed;
    }

    /**
     * @return the playerID
     */
    public int getPlayerID() {
        return playerID;
    }

    /**
     * @param playerID
     *            the playerID to set
     */
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }
}

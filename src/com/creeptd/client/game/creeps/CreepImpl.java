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
package com.creeptd.client.game.creeps;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.creeptd.client.game.GameContext;
import com.creeptd.client.game.Path;
import com.creeptd.client.util.Cache;
import com.creeptd.common.Constants;

/**
 * Abstract implementation for a creep.
 */
public class CreepImpl implements Creep {
    private GameContext context;
    private Constants.Creeps type;

    private int health;
    private double speed;
    private int slowTime;

    private int buildTime;
    private boolean active = false;
    private float xLocation;
    private float yLocation;

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
    private AffineTransform translation;
    private AffineTransform rotation;

    /**
     * Creates a new instance of this class.
     *
     * @param context
     *            the game context
     * @param type
     *            the type of the creep
     */
    protected CreepImpl(GameContext context, Constants.Creeps creepType) {
        this.context = context;
        this.type = creepType;
        this.speed = this.type.getSpeed();

        // set the position at this point to avoid that towers recognize
        // the creep with the wrong position at the first appearance of the
        // creep.
        float[] pos = context.getGameBoard().getPath().getStep(this.segment, this.segmentStep);
        this.xLocation = pos[0];
        this.yLocation = pos[1];
        this.segmentStep++;

        translation = new AffineTransform();
        rotation = new AffineTransform();

        setHealth(type.getHealth());
        
        loadImage();
    }

	@Override
    public float getX() {
        return this.xLocation;
    }

	@Override
    public float getY() {
        return this.yLocation;
    }

	@Override
	public void slow(double speed, int slowTime) {
		this.speed = speed;
		this.slowTime = slowTime;
	}

	@Override
    public void setHealth(int h) {
        this.health = h;
        this.updateHealthBar();
    }

	@Override
    public int getHealth() {
        return this.health;
    }

	@Override
    public double getSpeed() {
        return this.speed;
    }

	@Override
    public boolean isActive() {
        return this.active && this.health > 0;
    }

	@Override
    public Constants.Creeps getType() {
        return this.type;
    }

	@Override
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

	@Override
    public void update(long tickNumber) {
        if (this.buildTime > 1) {
            buildTime--;
            return;
        }
        if (!this.active) {
            this.active = true;
        }

        if (slowTime > 0) {
            slowTime--;
            if (slowTime == 0) {
                this.speed = type.getSpeed();
            }
        }

        // regenerate
        if (this.type.getRegenerationRate() > 0 && this.health < this.type.getHealth()) {
            this.health += this.type.getRegenerationRate();
            if (this.health > this.type.getHealth()) {
                this.health = this.type.getHealth();
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
        Path path = context.getGameBoard().getPath();
        float x0 = path.getPoint(i0).x * 20.0f;
        float y0 = path.getPoint(i0).y * 20.0f;
        float x1 = path.getPoint(i1).x * 20.0f;
        float y1 = path.getPoint(i1).y * 20.0f;
        float x2 = path.getPoint(i2).x * 20.0f;
        float y2 = path.getPoint(i2).y * 20.0f;

        this.xLocation = x0 * (1.0f - f) + x1 * f;
        this.yLocation = y0 * (1.0f - f) + y1 * f;

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
            this.rotateWert = angleCurrent * (1.0f - new_f) + angleNext * new_f;
        }
    }

	@Override
    public void setBuildTime(int buildTime) {
        this.buildTime = buildTime;
    }

	@Override
    public int getTotalSegmentSteps() {
        return this.totalSegmentSteps;
    }

	private void loadImage() {
		if (Cache.getInstance().hasCreepImg(this.getType())) {
			this.image = Cache.getInstance().getCreepImg(this.getType());
			return;
		}
		try {
			this.image = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(this.getType().getImageFileName())); 
			Cache.getInstance().putCreepImg(this.getType(), this.image);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

    private void updateHealthBar() {
        // calculate remaining percent of hit points
        float healthPercent = (float) health / (float) this.type.getHealth() * 100;
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
        this.healthBar = new Line2D.Float(3f, 2f, ((float) health / (float) this.type.getHealth() * 14f) + 3f, 2);
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
     * Berechnet den winkel von anfangs (x) & endpunkt (x2) bei stopps gibt
     * diese funktion -1.0d zurück wegen length = 0;
     */
    private static double calcAngle(float x, float y, float x2, float y2) {
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
}

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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.creeptd.client.creep.Creep;
import com.creeptd.client.game.GameContext;
import com.creeptd.client.game.PlayerContext;
import com.creeptd.client.grid.Grid;
import com.creeptd.common.IConstants;

/**
 * implementation of tower4.
 * 
 * @author sven
 */
public class Tower4 extends AbstractTower {

    private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
    private float projectileSpeed = 0.05f;

    /**
     * Creates a new instance of DefaultTower.
     *
     * @param context
     *            the gameContext for the tower
     * @param grid
     *            the grid where the tower is placed into
     */
    public Tower4(GameContext context, Grid grid) {
        super(IConstants.Towers.tower4, context, grid);
        this.setStrategy(new FindStrongestCreep(this));
        this.updateSelectedStrategy(this.getStrategy());
    }

    /**
     * {@inheritDoc}
     */
    protected void init() {
        // shapeColor = new Color(0, 204, 255);
    }

    /**
     * Draws the image for the tower.
     */
    public void generateImage() {
        Graphics2D g2 = (Graphics2D) getImage().getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(this.getTowerColor());
        g2.drawRect(2, 2, 16, 16);
        g2.drawRect(8, 8, 4, 4);

        g2.drawLine(2, 2, 8, 8);
        g2.drawLine(18, 18, 12, 12);

        g2.drawLine(2, 18, 8, 12);
        g2.drawLine(18, 2, 12, 8);

        g2.dispose();

    }

    /**
     * {@inheritDoc}
     */
    public String getTowerInfo() {
        return "Tower4";
    }

    /**
     * {@inheritDoc}
     */
    public void update(long tickNumber) {
        if (this.getBuildTime() > 0) {
            if (isBuilding()) {
                this.setBuildTime(getBuildTime() - 1);
                if (this.getBuildTime() == 0) {
                    this.setActive(true);
                    this.getGrid().setOccupiedStatus(false);
                }
            }
        }

        if (!this.isActive()) {
            return;
        }

        // sell animation
        if (this.getSellTime() > 0) {
            this.setSellTime(this.getSellTime() - 1);
            if (this.getSellTime() == 0) {
                if (this.getContext() instanceof PlayerContext) {
                    getContext().setCredits(getContext().getCredits() + (int) (this.getTotalPrice() * 0.75));
                    this.getGrid().setOccupiedStatus(false);
                }
                getContext().removeTower((Tower) this);
            }
        }



        // upgrade animation
        if (this.getUpgradeTime() > 0) {
            this.setUpgradeTime(getUpgradeTime() - 1, false);
            if (this.getRunningTowerUpgrades() > 0 && this.getUpgradeTime() == IConstants.USER_ACTION_DELAY) {

                this.upgrade();
                this.setRunningTowerUpgrades(0);
                getContext().fireSelectedChangedEvent("tower");
                getContext().fireSelectedChangedEvent("upgrade");
                this.getGrid().setOccupiedStatus(false);
                setUpgrading(true);

            } else if (this.getUpgradeTime() == 0) {

                this.upgrade();
                setUpgrading(false);
                getContext().fireSelectedChangedEvent("tower");
                getContext().fireSelectedChangedEvent("upgrade");
                this.getGrid().setOccupiedStatus(false);

            }
        }


        // strategy change update
        if (this.getChangeStrategyTime() > 0) {
            this.setChangeStrategyTime(this.getChangeStrategyTime() - 1);
            if (this.getChangeStrategyTime() == 0) {
                if (this.getSelectedStrategy() != null) {
                    this.setStrategy(this.getSelectedStrategy());
                    this.setChangingStrategy(false);
                    this.getGrid().setOccupiedStatus(false);
                }
            }
        }

        if (this.getCoolDownNow() > 0) {
            this.setCoolDownNow(this.getCoolDownNow() - 1);
        } else {
            Creep c = this.getStrategy().findCreep();

            if (c != null) {
                Projectile p = new Projectile();
                p.setTarget(c);
                p.setX(this.getGrid().getLocation()[0] + Grid.SIZE / 2);
                p.setY(this.getGrid().getLocation()[1] + Grid.SIZE / 2);
                p.setSpeed(this.projectileSpeed);
                this.projectiles.add(p);
                this.setCoolDownNow(this.getCoolDown());
                // play shoot-sound
                if (sound != null) {
                    sound.towerShootsSound(this.getType());
                }
            }
        }

        for (int ip = this.projectiles.size() - 1; ip >= 0; ip--) {
            Projectile p = this.projectiles.get(ip);

            if (!p.getTarget().isValidTarget() || (p.getTarget().getHealth() <= 0)) {
                Creep cNew = this.getStrategy().findCreep();
                if (cNew != null) {
                    p.setTarget(cNew);
                } else {
                    p.setTarget(null);
                    this.projectiles.remove(ip);
                }
            } else {
                float dx, dy, r, factor;
                if (p.getStatus() == 0) {
                    dx = p.getTarget().getX() + Grid.SIZE / 2 - p.getX();
                    dy = p.getTarget().getY() + Grid.SIZE / 2 - p.getY();
                    r = (float) Math.sqrt(dx * dx + dy * dy);
                    factor = p.getSpeed() / (r / 1.5f);
                    if (r > 2) {
                        dx *= factor;
                        dy *= factor;
                        p.setSpeed(p.getSpeed() + 0.02f);
                        p.setX(p.getX() + dx);
                        p.setY(p.getY() + dy);
                    } else {
                        // HIT!
                        p.setX(p.getTarget().getX() + Grid.SIZE / 2);
                        p.setY(p.getTarget().getY() + Grid.SIZE / 2);
                        p.setStatus(1);
                    }
                } else if (p.getStatus() == 1) {
                    float x = p.getX();
                    float y = p.getY();
                    Creep next;

                    for (int i = 0; i < getContext().getCreeps().size(); i++) {
                        if (getContext().getCreeps().get(i).isValidTarget()) {
                            next = getContext().getCreeps().get(i);

                            float dX = (next.getX() + Grid.SIZE / 2) - x;
                            float dY = (next.getY() + Grid.SIZE / 2) - y;

                            // squared distance
                            float dist = dX * dX + dY * dY;
                            if (dist < getSplashRadius() * getSplashRadius()) {
                                p.getSplashTargets().add(next);
                            }
                        }
                    }
                    double xDiff, yDiff, dist, reduction;
                    for (Creep creep : p.getSplashTargets()) {
                        if (p.getTarget() != null && p.getTarget().isValidTarget() && creep != null && creep.isValidTarget()) {
                            xDiff = creep.getX() - p.getTarget().getX();
                            yDiff = creep.getY() - p.getTarget().getY();
                            dist = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

                            // if the distance is high the damage becomes lower
                            reduction = ((getDamageReductionAtRadius() / getSplashRadius()) * dist);
                            reduction = getDamageNow() * reduction;
                            creep.damageCreep(this.getDamageNow() - (int) reduction, (Tower) this);
                        }
                    }
                    p.setStatus(2);
                } else if (p.getStatus() == 2) {
                    // status for the splash display.
                    p.setStatus(3);

                } else if (p.getStatus() > 2) {
                    p.setTarget(null);
                    this.projectiles.remove(ip);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void paintEffect(Graphics2D g) {
        super.paintEffect(g);

        Stroke s = g.getStroke();
        Color c = g.getColor();
        g.setColor(this.getTowerColor());
        for (int ip = 0; ip < this.projectiles.size(); ip++) {
            Projectile p = this.projectiles.get(ip);
            Rectangle2D rec;
            if (p.getStatus() == 0) {
                rec = new Rectangle2D.Float(p.getX(), p.getY(), 3, 3);
                g.fill(rec);
            } else if (p.getStatus() == 2) {
                Arc2D arc = new Arc2D.Float();
                arc.setArcByCenter(p.getX(), p.getY(), this.getSplashRadius(),
                        0, 360, Arc2D.CHORD);
                g.draw(arc);
                for (int i = 0; i < p.getSplashTargets().size(); i++) {
                    Line2D splash = new Line2D.Float(p.getX(), p.getY(), p.getSplashTargets().get(i).getX() + Grid.SIZE / 2, p.getSplashTargets().get(i).getY() + Grid.SIZE / 2);
                    g.setStroke(new BasicStroke(1));
                    g.draw(splash);
                    p.setStatus(3);
                }
            }

        }

        g.setStroke(s);
        g.setColor(c);

    }
}

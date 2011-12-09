package com.creeptd.client.game.weapons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.creeptd.client.game.creeps.Creep;
import com.creeptd.client.game.grids.Grid;
import com.creeptd.client.game.towers.Tower;

public class ProjectileWeapon extends AbstractWeapon {
	private List<Projectile> projectiles;
	private static final float projectileSpeed = 0.05f;

	public ProjectileWeapon(Tower tower) {
		super(tower);
		this.projectiles = new ArrayList<Projectile>();
	}

	@Override
	protected void attack() {
		Projectile p = new Projectile();
		p.setTarget(this.target);
		p.setX(this.tower.getGrid().getLocation()[0] + Grid.SIZE / 2);
		p.setY(this.tower.getGrid().getLocation()[1] + Grid.SIZE / 2);
		p.setSpeed(projectileSpeed);
		this.projectiles.add(p);
	}

	@Override
	public void update(long roundId) {
		super.update(roundId);
		float dx, dy, r;
		List<Projectile> projectiles2remove = new ArrayList<Projectile>();
		for (Projectile p : this.projectiles) {
			if (p.getTarget().getHealth() <= 0) {
				Creep newTarget = this.tower.getStrategy().findCreep();
				if (newTarget != null) {
					p.setTarget(newTarget);
				} else {
					projectiles2remove.add(p);
				}
			} else {
				if (p.getStatus() == 0) {
					dx = p.getTarget().getX() + Grid.SIZE / 2 - p.getX();
					dy = p.getTarget().getY() + Grid.SIZE / 2 - p.getY();
					r = (float)Math.sqrt(dx * dx + dy * dy);
					double factor = p.getSpeed() / (r / 1.5d);
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
					for (Creep c : this.tower.getContext().getCreeps()) {
						dx = (c.getX() + Grid.SIZE / 2) - p.getX();
						dy = (c.getY() + Grid.SIZE / 2) - p.getY();

						// squared distance
						r = dx * dx + dy * dy;
						if (r < this.tower.getType().getSplashRadius() * this.tower.getType().getSplashRadius())
							p.getSplashTargets().add(c);
					}

					double reduction;
					for (Creep creep : p.getSplashTargets()) {
						dx = creep.getX() - p.getTarget().getX();
						dy = creep.getY() - p.getTarget().getY();
						r = (float)Math.sqrt(dx * dx + dy * dy);

						// if the distance is high the damage becomes lower
						reduction = ((this.tower.getType().getDamageReductionAtRadius()
								/ this.tower.getType().getSplashRadius()) * r);
						reduction = this.tower.getType().getDamage() * reduction;
						this.damageCreep(creep, this.tower.getType().getDamage() - (int) reduction);
					}
					p.setStatus(2);
				} else if (p.getStatus() == 2) {
					// status for the splash display.
					p.setStatus(3);
				} else if (p.getStatus() > 2) {
					p.setTarget(null);
					projectiles2remove.add(p);
				}
			}
		}
		for (Projectile p : projectiles2remove) {
			this.projectiles.remove(p);
		}
	}

	@Override
	public void paintEffect(Graphics2D graphics) {
		if (this.projectiles.size() == 0)
			return;
		Stroke s = graphics.getStroke();
		Color c = graphics.getColor();

		graphics.setColor(Color.WHITE);

		for (Projectile p : this.projectiles) {
			Rectangle2D rec;
			if (p.getStatus() == 0) {
				rec = new Rectangle2D.Double(p.getX(), p.getY(), 3, 3);
				graphics.fill(rec);
			} else if (p.getStatus() == 2) {
				Arc2D arc = new Arc2D.Float();
				arc.setArcByCenter(p.getX(), p.getY(), this.tower.getType().getSplashRadius(), 0, 360, Arc2D.CHORD);
				graphics.draw(arc);

				for (int i = 0; i < p.getSplashTargets().size(); i++) {
					Line2D splash = new Line2D.Double(p.getX(),
							                          p.getY(),
							                          p.getSplashTargets().get(i).getX() + Grid.SIZE / 2,
							                          p.getSplashTargets().get(i).getY() + Grid.SIZE / 2);

					graphics.setStroke(new BasicStroke(1));
					graphics.draw(splash);
					p.setStatus(3);
				}
			}
		}

		graphics.setStroke(s);
		graphics.setColor(c);
	}
}

package com.creeptd.client.game.weapons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import com.creeptd.client.game.creeps.Creep;
import com.creeptd.client.game.grids.Grid;
import com.creeptd.client.game.towers.Tower;

public class SplashLaserWeapon extends AbstractWeapon {
	protected List<Creep> targets;

	public SplashLaserWeapon(Tower tower) {
		super(tower);
	}

	@Override
	protected void attack() {
		this.targets = new ArrayList<Creep>();
		List<Creep> creeps = this.tower.getContext().getCreeps();
		// find creeps in splashradius
		for (Creep creep: creeps) {
			if (this.inRange(this.target.getX(), this.target.getY(), creep.getX(), creep.getY(),
					this.tower.getType().getSplashRadius())) {
				this.targets.add(creep);
			}
		}
		// damage targets
		double xDiff, yDiff, dist, reduction;
		for (Creep creep: this.targets) {
			xDiff = creep.getX() - this.target.getX();
			yDiff = creep.getY() - this.target.getY();
			dist = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

			// if the distance is high the damage becomes lower
			reduction = (this.tower.getType().getDamageReductionAtRadius() / this.tower.getType().getSplashRadius()) * dist;
			reduction = this.tower.getType().getDamage() * reduction;
			this.damageCreep(creep, this.tower.getType().getDamage() - (int) reduction);
		}
	}

	@Override
	public void paintEffect(Graphics2D graphics) {
		if (this.target == null)
			return;
		Stroke s = graphics.getStroke();
		Color c = graphics.getColor();

		Line2D beam = new Line2D.Double(this.tower.getGrid().getX() + Grid.SIZE / 2,
									    this.tower.getGrid().getY() + Grid.SIZE / 2,
				                 		this.target.getX() + Grid.SIZE / 2,
				                 		this.target.getY() + Grid.SIZE / 2);

		graphics.setStroke(new BasicStroke(3));
		graphics.setColor(Color.WHITE);
		graphics.draw(beam);

		graphics.setStroke(new BasicStroke(1));
		graphics.setColor(Color.BLUE);
		graphics.draw(beam);

		for (Creep creep : this.targets) {
			Line2D splash = new Line2D.Double(this.target.getX() + Grid.SIZE / 2,
											  this.target.getY() + Grid.SIZE / 2,
											  creep.getX() + Grid.SIZE / 2,
											  creep.getY() + Grid.SIZE / 2);
			graphics.setStroke(new BasicStroke(1));
			graphics.setColor(Color.WHITE);
			graphics.draw(splash);
		}

		graphics.setStroke(s);
		graphics.setColor(c);
	}

	private boolean inRange(double x1, double y1, double x2, double y2, double range) {
		return (((x2-x1) * (x2-x1)) + ((y2-y1) * (y2-y1))) < (range*range);
	}
}

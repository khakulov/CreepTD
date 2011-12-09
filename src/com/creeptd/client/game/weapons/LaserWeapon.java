package com.creeptd.client.game.weapons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import com.creeptd.client.game.grids.Grid;
import com.creeptd.client.game.towers.Tower;

public class LaserWeapon extends AbstractWeapon {

	public LaserWeapon(Tower tower) {
		super(tower);
	}

	@Override
	protected void attack() {
		this.damageCreep(this.target, this.tower.getType().getDamage());
	}

	@Override
	public void paintEffect(Graphics2D graphics) {
		if (this.target == null)
			return;
		Stroke s = graphics.getStroke();
		Color c = graphics.getColor();

		Line2D beam = new Line2D.Double(this.tower.getGrid().getLocation()[0] + Grid.SIZE / 2,
										this.tower.getGrid().getLocation()[1] + Grid.SIZE / 2,
										this.target.getX() + Grid.SIZE / 2,
										this.target.getY() + Grid.SIZE / 2);

		graphics.setStroke(new BasicStroke(3));
		graphics.setColor(Color.WHITE);
		graphics.draw(beam);

		graphics.setStroke(new BasicStroke(1));
		graphics.setColor(Color.BLUE);
		graphics.draw(beam);

		graphics.setStroke(s);
		graphics.setColor(c);
	}
}

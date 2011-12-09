package com.creeptd.client.game.weapons;

import com.creeptd.client.game.towers.Tower;

public class SlowerWeapon extends LaserWeapon {
	
	public SlowerWeapon(Tower tower) {
		super(tower);
	}

	@Override
	protected void attack() {
		super.attack();
		if (!this.target.getType().isSlowImmune()) {
			double slowedSpeed = this.target.getType().getSpeed() * (1 - this.tower.getType().getSlowRate());
			if (this.target.getSpeed() > slowedSpeed) {
				this.target.slow(slowedSpeed, this.tower.getType().getSlowTime());
			}
		}
	}
}

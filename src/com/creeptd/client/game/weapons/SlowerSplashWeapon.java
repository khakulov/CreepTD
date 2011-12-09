package com.creeptd.client.game.weapons;

import com.creeptd.client.game.creeps.Creep;
import com.creeptd.client.game.towers.Tower;

public class SlowerSplashWeapon extends SplashLaserWeapon {

	public SlowerSplashWeapon(Tower tower) {
		super(tower);
	}

	@Override
	protected void attack() {
		super.attack();
		for (Creep creep: this.targets) {
			if (!creep.getType().isSlowImmune()) {
				double slowedSpeed = creep.getType().getSpeed() * (1 - this.tower.getType().getSlowRate());
				if (creep.getSpeed() > slowedSpeed) {
					creep.slow(slowedSpeed, this.tower.getType().getSlowTime());
				}
			}
		}
	}

}
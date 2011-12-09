package com.creeptd.client.game.weapons;

import com.creeptd.client.game.towers.Tower;

public class WeaponFactory {
	public static Weapon createWeapon(Tower tower) {
		Weapon weapon;
		switch(tower.getType().getWeaponType()) {
			case laser:
				weapon = new LaserWeapon(tower);
				break;
			case splashlaser:
				weapon = new SplashLaserWeapon(tower);
				break;
			case slower:
				weapon = new SlowerWeapon(tower);
				break;
			case multislower:
				weapon = new SlowerSplashWeapon(tower);
				break;
			case projectile:
				weapon = new ProjectileWeapon(tower);
				break;
			default:
				weapon = null;
		}
		return weapon;
	}
}

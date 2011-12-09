package com.creeptd.client.game.weapons;

import com.creeptd.client.game.creeps.Creep;
import com.creeptd.client.game.towers.Tower;
import com.creeptd.common.Constants;

public abstract class AbstractWeapon implements Weapon {
	protected Tower tower;
	protected Creep target;
	protected int coolDown;

	public AbstractWeapon(Tower tower) {
		this.tower = tower;
		this.coolDown = 0;
	}

	protected abstract void attack();

	@Override
	public void update(long round) {
		this.target = null;

		// decrease cooldown
		if (this.coolDown > 0) {
			this.coolDown--;
			if (this.coolDown > 0) return;
		}

		this.target = this.tower.getStrategy().findCreep();
		if (this.target != null) {
			this.attack();
			this.coolDown = this.tower.getType().getSpeed();
		}
	}

	/**
	 * Calculates the damage done to the creep.
	 * @param creep creep to damage
	 * @param damage value of the damage
	 */
	protected void damageCreep(Creep creep, int damage) {
		creep.setHealth(creep.getHealth() - damage);
		// if the creep is death ...
		if (creep.getHealth() <= 0) {
			// ... remove the creep
			this.tower.getContext().getCreeps().remove(creep);
			// ... increase money by bounty
            if (this.tower.getContext().getGameLoop().getGameMode().equals(Constants.Mode.SURVIVOR)) {
            	this.tower.getContext().setIncome(this.tower.getContext().getIncome()+creep.getType().getIncome());
            }
			this.tower.getContext().setCredits(this.tower.getContext().getCredits() + creep.getType().getBounty());
		}
	}
}

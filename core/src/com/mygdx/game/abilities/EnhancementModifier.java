package com.mygdx.game.abilities;

import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.*;
import java.util.*;


public class EnhancementModifier extends Ability.Modifier {
	float mPercentage;
	// @Override
	static class Builder implements Ability.ModifierBuilder {
		@Override
		public String name() {
			return "Enhancement";
	       	}
		@Override
		public Ability.Modifier instance(JsonValue json) {
			return new EnhancementModifier(json);
		}
	}

	// public EnhancementModifier() { }
	public EnhancementModifier(JsonValue json) { super(json); }

	@Override
	public boolean parse(JsonValue json) {
		Iterator<JsonValue> iter = json.iterator();
		if (!iter.hasNext())
			return false;
		mPercentage = iter.next().asFloat();
		return true;
	}

	@Override
	public boolean apply(Unit caster, Unit target) {
		int rawAtk = caster.getAtk();
		int damageInt = (int)(rawAtk * mPercentage);

		Gdx.app.log("ExtraDamage", "caster: " +
				caster.getOwner().getName() +
				" - " + caster.getName());
		Gdx.app.log("ExtraDamage", "target: " +
				target.getOwner().getName() +
				" - " + target.getName());
		target.dealDamage(caster.buildDamage(damageInt));
		return true;
	}

}

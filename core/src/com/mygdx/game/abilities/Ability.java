package com.mygdx.game.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.*;
import java.util.*;

/**
 * An Ability has multiple modifier.
 */

public class Ability {

	public static Map<String, ModifierBuilder>	sAbilityMap;
	public static EnumMap<EventType, Modifier>	sEventMap;

	protected Unit			mOwner;
	protected Props			mProps;
	// protected Ability		mNext;
	// icon
	// description

	static {
		sAbilityMap = new HashMap<String, ModifierBuilder>();
		ModifierBuilder builder;

		builder = new ExtraDamageModifier.Builder();
		sAbilityMap.put(builder.name().toUpperCase(), builder);

		sEventMap = new EnumMap<EventType, Modifier>(EventType.class);
		// adding more modifiers here
	}

	/** EvenType defines when will an ability to be triggered
	 */
	public enum EventType {
		BEFORE_BATTLE,
		BEFORE_ATTACKING,
		AFTER_ATTACKING,
		BEFORE_ATTACKED,
		AFTER_ATTACKED,
		UPON_DEATH,
		UPON_KILLING,
		UPON_TARGETED, // become the main target
		UPON_TARGETING, // after target selection
		BUFF_ACQUIRED,
		BUFF_LOST,
		DEBUFF_ACQUIRED,
		DEBUFF_LOST,
	}

	static public class Props {
		public String			mName;
		public String			mDescriptions;
		public Texture			mIcon;
		public Array<Modifier>		mModifiers;
		public EventType		mType;
		public Ability instance() {
			return new Ability(this);
		}
	}

	static abstract public class Modifier {
		String mInfo;
		// Unit mOwner;
		public Modifier(JsonValue json) {
			parse(json);
		}

		/**
		 * The real ability effect is to be implement here
		 */
		abstract public boolean apply(Unit owner, Unit target);
		abstract public boolean parse(JsonValue json);
	}
	/**
	 * Every derived class must declare a unique name and register itself
	 * in sAbilityMap.
	 */
	static interface ModifierBuilder {
		String name();
		Modifier instance(JsonValue json);
	}


	public class Event {
		EventType type;
		public Unit target;
		public Unit self;
		public String toString() {
			return type.toString();
		}
	}

	private Ability(Props props) {
		mProps = props;
	}

	// Ability(JsonValue json) { parse(); }

	static public Ability.Props parseProp(JsonValue json) {

		Ability.Props prop = new Props();
		prop.mName = json.getString("name");
		prop.mDescriptions = json.getString("descriptions");
		prop.mType = EventType.valueOf(json.getString("trigger").toUpperCase());

		JsonValue modifiersJson = json.get("modifiers");
		prop.mModifiers = new Array<Modifier>();
		for (JsonValue modifierJson: modifiersJson.iterator()) {
			String modName = modifierJson.getString("modifier").toUpperCase();
			ModifierBuilder builder = sAbilityMap.get(modName);
			if (builder == null) {
				Gdx.app.log("Ability", "Can not find ability named: " + modName);
				continue;
			}
			Modifier modifier = builder.instance(modifierJson.get("args"));
			prop.mModifiers.add(modifier);
		}
		return prop;
	}

	public String getName() { return mProps.mName; }

	public String getInfo() {
		// TODO compose modifier informations
		return getName();
	}

	static public void notify (Event what) {
	}

	public EventType getType() {
		return mProps.mType;
	}

	public void apply(Unit caster, Unit target) {
		Gdx.app.log("ExtraDamage", "caster: " + caster.getOwner().getName() + " - " + caster.getName());
		Gdx.app.log("ExtraDamage", "target: " + target.getOwner().getName() + " - " + target.getName());
		for (Modifier mod: mProps.mModifiers) {
			mod.apply(caster, target);
		}
	}

}

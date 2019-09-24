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

	public static Map<String, ModifierBuilder>		sAbilityMap;
	// public static EnumMap<EventType, Modifier>		sEventMap;
	public static EnumMap<ObjectType, ObjectBuilder>	sObjTypeMap;

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

		// sEventMap = new EnumMap<EventType, Modifier>(EventType.class);

		sObjTypeMap = new EnumMap<ObjectType,
			       ObjectBuilder>(ObjectType.class);
		sObjTypeMap.put(ObjectType.SELF,		Self.sBuilder);
		sObjTypeMap.put(ObjectType.TARGET,		Target.sBuilder);
		sObjTypeMap.put(ObjectType.ATTACKER,	Attacker.sBuilder);
		// adding more modifiers here
	}

	public enum ObjectType {
		SELF,
		TARGET,
		ATTACKER
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

	static public interface ObjectBuilder {
		Object build();
	}

	static public interface Object {
		Array<Unit> get(Unit source);
	}

	static public class Self implements Object {
		static public ObjectBuilder sBuilder = new ObjectBuilder() {
			public Object build() {
				return new Self();
			}
		};
		@Override
		public Array<Unit> get(Unit source) {
			Array<Unit> r = new Array<Unit>();
			r.add(source);
			return r;
		}
	}

	static public class Target implements Object {
		static public ObjectBuilder sBuilder = new ObjectBuilder() {
			public Object build() {
				return new Target();
			}
		};
		@Override
		public Array<Unit> get(Unit source) {
			return source.getTargets();
		}
	}

	static public class Attacker implements Object {
		static public ObjectBuilder sBuilder = new ObjectBuilder() {
			public Object build() {
				return new Attacker();
			}
		};
		@Override
		public Array<Unit> get(Unit source) {
			Array<Unit> r = new Array<Unit>();
			r.add(source.getAttacker());
			return r;
		}
	}

	static abstract public class Modifier {
		String mInfo;
		Object mObject;
		// Unit mOwner;
		public Modifier(JsonValue json) {
			parse(json);
		}

		/**
		 * The real ability effect is to be implement here
		 */
		abstract public boolean apply(Unit owner);
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
			Modifier modifier = builder.instance(modifierJson);
			ObjectType oType = ObjectType.valueOf(
					modifierJson.getString("object").toUpperCase()
					);
			modifier.mObject = sObjTypeMap.get(oType).build();
			// modifierJson.getString().toUpperCase();
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

	public void apply(Unit caster) {
		Gdx.app.log("ExtraDamage", "caster: " + caster.getOwner().getName() + " - " + caster.getName());
		// Gdx.app.log("ExtraDamage", "target: " + target.getOwner().getName() + " - " + target.getName());
		for (Modifier mod: mProps.mModifiers) {
			mod.apply(caster);
		}
	}

}

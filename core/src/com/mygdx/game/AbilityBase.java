package com.mygdx.game;

import com.badlogic.gdx.utils.*;

public class AbilityBase {
	enum EventType {
		BEFORE_BATTLE,
		BEFORE_ATTACKING,
		AFTER_ATTACKING,
		BEFORE_ATTACKED,
		AFTER_ATTACKED,
		UPON_DEATH,
		UPON_KILLING,
		UPON_TARGETED, // become the main target
		UPON_TARGETING // after target selection
	}

	public Array<Modifier>[] mModifiers;

	public class Event {
		EventType type;
		public Unit target;
		public Unit self;
		public String toString() {
			return type.toString();
		}
	}

	public class Modifier {
		public Modifier() {
		}
		public void run(Event e) { }
	}

	AbilityBase() {}
	
	public void handleEvent(Event e) {

		for (Modifier m: mModifiers[e.type.ordinal()])
			m.run(e);
		return ;
	}
}

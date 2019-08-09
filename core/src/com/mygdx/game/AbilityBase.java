package com.mygdx.game;

class AbilityBase {
	enum EventType {
		BEFORE_BATTLE,
		BEFORE_ATTACKING,
		AFTER_ATTACKING,
		BEFORE_ATTACKED,
		AFTER_ATTACKED,
		UPON_DEATH,
		UPON_KILLING	
	}
	class Event {
		EventType type;
		public Unit target;
		public Unit self;
		public String toString() {
			return type.toString();
		}
	}
	AbilityBase() {}
	
	public void handleEvent(Event e) {
		switch (e.type){
			case BEFORE_BATTLE:
		}
		return ;
	}
}

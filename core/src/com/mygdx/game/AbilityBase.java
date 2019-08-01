package com.mygdx.game;

class AbilityBase {
	enum EventType {
		beforeBattle,
		beforeAttacking,
		afterAttacking,
		beforeAttacked,
		afterAttacked,
		uponDeath,
		uponKilling
	}
	class Event {
		EventType type;
		public Unit target;
		public Unit self;
		public String toString() {
			return "";
		}
	}
	AbilityBase() {}
	
	public void handleEvent(Event e) {
		switch (e.type){
			case beforeBattle:
		}
		return ;
	}
}

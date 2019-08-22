package com.mygdx.game;
public class Buff {
	public enum Target {
		TARGET_SELF,
		TARGET_TARGET,
		TARGET_ATTACKER,
	};
	Unit mOwner;
	public Buff() { }
	private void handleEndOfTurn() {
	}
}

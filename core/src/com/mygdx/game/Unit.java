package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Unit {
	// static public Texture testTexture = new Texture(Gdx.files.internal("bucket.png"));
	static public Unit chosen = null;

	// static properties
	// public boolean deployed = false;
	/* cordinates relative to major */

	class Attack {
		private int atkPoints;
		private int damageDealt;
	}

	/* constant unit properties */
	final private UnitProperties prop;

	private int gridX, gridY;
	private int order = -1;
	private int currentHp;
	private boolean prepared;
	private Unit mAttackingTarget;
	private Unit[] mAttackingGroup;
	// private float posX, posY;
	Player mOwner;
	Tile tile;

	private Buff[] mBuffs;

	public Unit(UnitProperties p, Player o) {
		mOwner = o;
		prop = p;
		prepared = false; // for static only;
		currentHp = prop.hitpoints;
	}

	public void deploy(Grid grid, int x, int y) {
		gridX = x;
		gridY = y;
	}

	public String getName() {
		return prop.name;
	}

	public int getHp() {
		return currentHp;
	}

	public int getAtk() { return prop.damage; }

	public Sprite getIllust() { return prop.illustSprite; }

	public UnitProperties.Range getRange() { return prop.range; }
	public UnitProperties.Type getType() { return prop.type; }

	public Tile getTile() { return tile; }
	public void setTile(Tile t) { tile = t; }

	public int getOrder() { return order; }
	public void setOrder(int o) { order = o; }

	public Player getOwner() { return mOwner; }
	public UnitProperties.Pattern getPattern() { return prop.pattern; }

	public boolean isDead() { return currentHp > 0; }

	public boolean isDeployed() { return tile == null; }

	public boolean switchPlayer() {
		switch (prop.type) {
			case TROOP:
			case TURRET:
				return true;
			case STATIC:
				return prepared;
			case INFRA:
				return false;
		}
		return true;
	}

	private void engage(Unit target) {
	}


	public void getTargets() {
		int lane = getTile().y;
		mAttackingGroup = mOwner.getOpponent().getTargets(lane, getRange(), getPattern()).toArray();
	}

	public void runTurn() {
		getTargets();

		for (Unit t: mAttackingGroup) {
			engage(t);
			// if (t.isDead())
				// mUnitQueue.remove(t);
		}
		// end turn
		// for (ab: prop.abilities) { ab.do }
	}
}

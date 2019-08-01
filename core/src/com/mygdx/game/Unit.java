package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;

class Unit {
	// static public Texture testTexture = new Texture(Gdx.files.internal("bucket.png"));
	static public Unit chosen = null;

	// static properties
	public boolean deployed = false;
	/* cordinates relative to major */

	/* constant unit properties */
	final private UnitProperties prop;

	private int gridX, gridY;
	private int order = -1;
	private int currentHp;
	private boolean prepared;
	// private float posX, posY;
	Player owner;
	Tile tile;

	Unit(UnitProperties p, Player o) {
		owner = o;
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

	public int getAtk() {
		return prop.damage;
	}

	public Sprite getIllust() {
		return prop.illustSprite;
	}

	public String getRangeText() {
		return prop.range.toString();
	}
	public String getTypeText() {
		return prop.type.toString();
	}

	public Tile getTile() { return tile; }
	public void setTile(Tile t) { tile = t; }

	public int getOrder() { return order; }
	public void setOrder(int o) { order = o; }

	public Player getOwner() { return owner; }

	public boolean isDead() { return currentHp > 0; }

	public boolean switchPlayer() {
		switch (prop.type) {
			case UNIT:
			case TURRET:
				return true;
			case STATIC:
				return prepared;
			case INFRA:
				return false;
		}
		return true;
	}

	public void engage(Unit target) {
	}
}

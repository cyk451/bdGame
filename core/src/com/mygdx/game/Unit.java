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
	// int[] pattern;

	/* constant unit properties */
	final private UnitProperties prop;

	private int gridX, gridY;
	private int order;
	private int currentHp;
	// private float posX, posY;
	Player owner;

	Unit(UnitProperties p) {
		prop = p;
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
		return prop.range.asText();
	}
	public String getTypeText() {
		return prop.type.asText();
	}
}

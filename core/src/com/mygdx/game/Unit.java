package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;

class Unit {
	static Texture testTexture = new Texture(Gdx.files.internal("bucket.png"));

	class Range {
		static final int FIRST = 0;
		static final int SKIP = 1;
		static final int LAST = 2;
	}
	class Class {
		static final int UNIT = 0;
		static final int SUPPORT = 1;
		static final int STATIC = 2;
	}

	// static properties
	private String name;
	int hp;
	int atk;
	int range;
	int unitClass;
	public boolean deployed = false;
	static public Unit chosen = null;
	/* cordinates relative to major */
	int[] pattern;

	Sprite chibiSprite, orderSprite, iconSprite;

	private int gridX, gridY;
	private int order;
	// private float posX, posY;
	Player owner;

	Unit(Sprite chibi) {
		// chibiSprite = new Sprite(testTexture, 64, 64);

		// owner = p;
		chibiSprite = chibi;
		deployed = false;

	}

	/*
	public void setPosition(float x, float y) {
		posX = x;
		posY = y;
	}
	*/

	public void deploy(Grid grid, int x, int y) {
		gridX = x;
		gridY = y;
		// Vector3 imageBase = grid.getUnitImageBase(x, y);
	}

	public int getHp() {
		return hp;
	}
	public Sprite getSprite() {
		return chibiSprite;
	}

	/*
	public void render() {
		if (deployed == true) {
		}
	}
	*/
}

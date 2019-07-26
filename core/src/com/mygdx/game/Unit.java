package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;

class Unit {
	static Texture testTexture = new Texture(Gdx.files.internal("bucket.png"));

	static final int RANGE_FIRST = 0;
	static final int RANGE_SKIP = 1;
	static final int RANGE_LAST = 2;

	// static properties
	private String name;
	int hp;
	int atk;
	int range;
	/* cordinates relative to major */
	int[] pattern;

	Sprite chibiSprite, orderSprite, iconSprite;

	private int order;
	private float posX, posY;
	Player owner;

	Unit(Sprite chibi) {
		chibiSprite = new Sprite(testTexture, 64, 64);

		// chibiSprite = chibi;
	}

	public void setPosition(float x, float y) {
		posX = x;
		posY = y;
	}
	public void render() {
	}
}

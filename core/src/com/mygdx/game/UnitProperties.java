package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.*;


class UnitProperties implements Json.Serializable {
	// static Texture testTexture = new Texture(Gdx.files.internal("bucket.png"));

	static Array<UnitProperties> unitPool = new Array<UnitProperties>();

	class Range {
		static final int FIRST = 0;
		static final int SKIP = 1;
		static final int LAST = 2;
		private int range = FIRST;

		Range() {}

		Range(int r) {
			range = r;
		}

		String asText() {
			switch(range) {
				case FIRST:
					return "First";
				case SKIP:
					return "Skip";
				case LAST:
					return "Last";
			}
			return "Unknown";
		}
	}

	class Type {
		static final int UNIT = 0;
		static final int INFRA = 1;
		static final int STATIC = 2;
		static final int TURRET = 4;
		private int type = UNIT;
		Type() {}
		Type(int t) {
			type = t;
		}

		String asText () {
			switch(type) {
				case UNIT:
					return "Unit";
				case INFRA:
					return "Building";
				case STATIC:
					return "Static";
				case TURRET:
					return "Turret";
			}
			return "Unknown";
		}
	}

	public String name;
	public String flavorText;

	// this id is given when resource loaded; should match its index in unitPool.
	public int id;
	public int hitpoints;
	public int damage;
	public Range range;
	public Type type;

	public Sprite illustSprite;

	UnitProperties() { }

	static void addToPool(UnitProperties up) {
		up.id = unitPool.size;
		unitPool.add(up);
	}

	public void read(Json json, JsonValue jsonData) {
	}

	public void write(Json json) {
	}
}

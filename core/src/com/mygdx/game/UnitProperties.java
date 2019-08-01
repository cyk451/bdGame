package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Pixmap;


import com.badlogic.gdx.utils.*;
import java.util.*;


class UnitProperties /* implements Json.Serializable */ {
	// static Texture testTexture = new Texture(Gdx.files.internal("bucket.png"));

	static Array<UnitProperties> unitPool = new Array<UnitProperties>();

	public enum Range { FIRST, SKIP, LAST };

	public enum Type { UNIT, INFRA, STATIC, TURRET };

	public String name;
	public String flavorText;

	// this id is given when resource loaded; should match its index in unitPool.
	public int id;
	public int hitpoints;
	public int damage;
	public Pattern pattern;
	public Pixmap patternTexture; // show how the range is
	public Range range;
	public Type type;

	public Sprite illustSprite;

	class Pattern extends Array<Vector2> {
		Pattern() { super(); }
	}

	class PatternVertexComparator implements Comparator<Vector2> {
		@Override
		public int compare(Vector2 l, Vector2 r) {
			if (l.y == r.y) {
				return (int)(l.x - r.x);
			}
			return (int)(l.y - r.y);
		}
	}

	UnitProperties() { }
	UnitProperties(JsonValue json) { 
		name = json.getString("name");
		flavorText = json.getString("flavorText");
		damage = json.getInt("damage");
		hitpoints = json.getInt("hitpoints");
		range = Range.valueOf(json.getString("range").toUpperCase());
		type = Type.valueOf(json.getString("type").toUpperCase());
		Texture texture = new Texture(Gdx.files.internal(json.getString("illust_texture")));
		illustSprite = new Sprite(texture);
		id = unitPool.size;
		unitPool.add(this);
		JsonValue patternJson = json.get("pattern");
		int i = 0;
		Vector2 vec = new Vector2();
		pattern = new Pattern();
		for (JsonValue patVertJson : patternJson.iterator()) {
			if ((i & 1) == 0) {
				vec.x = (patVertJson.asInt());
			} else {
				vec.y = (patVertJson.asInt());
				pattern.add(vec);
			}
			i += 1;
		}
		pattern.sort(new PatternVertexComparator());
		// Collections.sort(pattern, new PatternVertexComparator());
	}


	/*
	public void read(Json json, JsonValue jsonData) {
	}

	public void write(Json json) {
	}
*/
}

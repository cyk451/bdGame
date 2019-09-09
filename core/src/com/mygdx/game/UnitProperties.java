package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;


import com.badlogic.gdx.utils.*;
import java.util.*;

/*
 * UnitProperties desribes the static status of a unit.
 *
 * UnitProperties having static information about a unit that won't change
 * after resouce files are loaded. A Unit, in contrast, may contain stats that
 * can vary duing the battle. It's also possible multiple unit instances
 * initiated from the same UnitProperty.
 */

public class UnitProperties {
	// static Texture testTexture = new Texture(Gdx.files.internal("bucket.png"));

	static Array<UnitProperties> unitPool = new Array<UnitProperties>();

	public enum Range { FIRST, SKIP, LAST };
	public enum Type { TROOP, INFRA, STATIC, TURRET };

	static public Sprite sDebrickSprite = loadDebrickSprite();
	public String name;
	public String flavorText;

	// this id is given when resource loaded; should match its index in unitPool.
	public int		id;
	public int		hitpoints;
	public int		damage;
	public Pattern		pattern;
	public Pixmap		patternTexture; // show user how the range is
	public Range		range;
	public Type		type;

	public Sprite illustSprite;

	public class Pattern extends Array<GridPoint2> {
		final int DOT_SIZE = 3;
		final int EDGE = 8;
		final int ICON_SIZE = 48;
		Sprite mSprite;
		Pattern() {
			super();
		}
		public Sprite asSprite() {
			if (mSprite != null)
				return mSprite;
			Pixmap pixMap = new Pixmap(ICON_SIZE, ICON_SIZE, Pixmap.Format.RGB565);
			pixMap.setColor(Color.RED);
			for (GridPoint2 vec: this) {
				System.out.println("pnt " + vec.x + ", " + vec.y);
				int x = ICON_SIZE / 2 + EDGE * vec.x;
				if ((vec.y & 1) == 1)
					x += EDGE / 2;
				int y = ICON_SIZE / 2 + EDGE * vec.y;
				pixMap.fillCircle(x, y, DOT_SIZE);
			}

			mSprite = new Sprite(new Texture(pixMap));
			pixMap.dispose();
			return mSprite;
		}
	}

	class PatternVertexComparator implements Comparator<GridPoint2> {
		@Override
		public int compare(GridPoint2 l, GridPoint2 r) {
			if (l.y == r.y) {
				return (int)(l.x - r.x);
			}
			return (int)(l.y - r.y);
		}
	}

	static public Sprite loadDebrickSprite() {
		Texture texture = new Texture(Gdx.files.internal("debrick.png"));
		return new Sprite(texture);
	}

	/* constructors */
	public UnitProperties() { }
	public UnitProperties(JsonValue json) {
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
		GridPoint2 vec = new GridPoint2();
		pattern = new Pattern();
		for (JsonValue patVertJson : patternJson.iterator()) {
			if ((i & 1) == 0) {
				vec.x = patVertJson.asInt();
			} else {
				vec.y = patVertJson.asInt();
				pattern.add(new GridPoint2(vec));
			}
			i += 1;
		}
		pattern.sort(new PatternVertexComparator());
		System.out.println("mk " + pattern.get(0).x + ", " + pattern.get(0).y);

		System.out.println(pattern);
		// Collections.sort(pattern, new PatternVertexComparator());
	}

	public String getName() { return name; }
}

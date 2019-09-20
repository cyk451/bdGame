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

	static public Array<UnitProperties> sUnitSet = new Array<UnitProperties>();
	static private EnumMap<Type, Color> sUnitMap;
	static private EnumMap<Range, TargetSelector> sTargetSelectorMap;
	static {
		sTargetSelectorMap = new EnumMap(Range.class);
		sTargetSelectorMap.put(Range.FIRST, new First());
		sTargetSelectorMap.put(Range.SKIP, new Skip());
		sTargetSelectorMap.put(Range.LAST, new Last());
		sTargetSelectorMap.put(Range.SELF, new Self());
		sTargetSelectorMap.put(Range.NEXT, new Next());
	}

	public enum Range { FIRST, SKIP, LAST, SELF, NEXT};
	public enum Type { TROOP, INFRA, STATIC, TURRET };


	static public Sprite sDebrickSprite = loadDebrickSprite();
	public String name;
	public String flavorText;

	// this id is given when resource loaded; should match its index in sUnitSet.
	public int		id;
	public int		hitpoints;
	public int		damage;
	public Pattern		pattern;
	public Pixmap		patternTexture; // show user how the range is
	public Range		range;
	public Type		type;
	public TargetSelector	selector;

	public Sprite illustSprite;

	static public abstract class TargetSelector {
		public Texture mIcon;
		protected boolean validUnit(Unit unit) {
			if (unit == null)
				return false;
			if (unit.isDead())
				return false;
			return true;
		}
		abstract public Unit findInLane(Grid.Lane l);
		public Unit findTarget(Unit u) {
			Player player = u.getTargetingPlayer();
			Grid grid = player.getGrid();

			for (int i = 0; i < Grid.HEIGHT; ++i) {
				Grid.Lane lane = grid.getLane((u.getY() + i) % Grid.HEIGHT);
				Unit mainTarget = findInLane(lane);
				if (mainTarget == null)
					continue;
				return mainTarget;
			}
			return null;
		}
		abstract public TargetSelector build();
	}

	static public class First extends TargetSelector {
		@Override
		public Unit findInLane(Grid.Lane lane) {
			for (int c = 0; c < lane.size; c += 1) {
				Unit unit = lane.get(c).getUnit();
				if (!validUnit(unit))
					continue;
				return unit;
			}
			return null;
		}
		@Override
		public TargetSelector build() {
			return new First();
		}
	}

	static public class Skip extends TargetSelector {
		@Override
		public Unit findInLane(Grid.Lane lane) {
			int numMatches = 2;
			Unit result = null;
			for (int c = 0; c < lane.size; c += 1) {
				Unit unit = lane.get(c).getUnit();
				if (!validUnit(unit))
					continue;
				result = unit;
				if (--numMatches == 0)
					break;
			}
			return result;
		}
		@Override
		public TargetSelector build() {
			return new Skip();
		}
	}

	static public class Last extends TargetSelector {
		@Override
		public Unit findInLane(Grid.Lane lane) {
			for (int c = lane.size - 1; c >= 0; c -= 1) {
				Unit unit = lane.get(c).getUnit();
				if (validUnit(unit))
					return unit;
			}
			return null;
		}
		@Override
		public TargetSelector build() {
			return new Last();
		}
	}


	static public class Self extends TargetSelector {

		@Override
		public Unit findTarget(Unit u) { return u; }
		@Override
		public Unit findInLane(Grid.Lane lane) { return null; }
		@Override
		public TargetSelector build() {
			return new Self();
		}
	}

	static public class Next extends TargetSelector {
		First mOtherwise;
		@Override
		public Unit findTarget(Unit u) {
			Player player = u.getTargetingPlayer();
			Iterator<Unit> iter = player.iterator();
			while(iter.hasNext()) {
				Unit c = iter.next();
				if (iter.next() != u)
					continue;
				if (iter.hasNext())
					return iter.next();
				else
					return player.getUnitByOrder(0);
			}
			// this can happen if the unit does not belong to
			// target player, according to bd, follow the rule of
			// choosing first.
			mOtherwise = new First();
			return mOtherwise.findTarget(u);
		}
		@Override
		public Unit findInLane(Grid.Lane lane) { return null; }
		@Override
		public TargetSelector build() {
			return new Last();
		}
	}

	static public class Pattern extends Array<GridPoint2> {
		final static int DOT_SIZE = 3;
		final static int EDGE = 8;
		final static int ICON_SIZE = 48;
		Sprite mSprite;
		Pattern() {
			super();
		}
		public Sprite asSprite() {
			if (mSprite != null)
				return mSprite;
			Pixmap pixMap = new Pixmap(ICON_SIZE, ICON_SIZE, Pixmap.Format.RGB565);
			pixMap.setColor(Color.RED);
			for (int i = -2; i < 3; ++i) {
				for (int j = -2; j < 3; ++j) {
					int x = ICON_SIZE / 2 + EDGE * i;
					if ((j & 1) == 1)
						x += EDGE / 2;
					int y = ICON_SIZE / 2 + EDGE * j;
					pixMap.drawCircle(x, y, DOT_SIZE);
				}
			}

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
		Gdx.app.log("UnitProperties", "begin ");
		name = json.getString("name");
		flavorText = json.getString("flavorText");
		damage = json.getInt("damage");
		hitpoints = json.getInt("hitpoints");
		range = Range.valueOf(json.getString("range").toUpperCase());
		Gdx.app.log("UnitProperties", "range: " + range);
		// selector = sTargetSelectorMap.get(range);
		type = Type.valueOf(json.getString("type").toUpperCase());
		Texture texture = new Texture(Gdx.files.internal(json.getString("illust_texture")));
		illustSprite = new Sprite(texture);

		id = sUnitSet.size;

		sUnitSet.add(this);
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

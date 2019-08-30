package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;


/* must create left player first */
public class Player {
	private Grid		mGrid;
	String				mName;
	Player				mOpponent;
	Formation			format;
	Array<Unit>			orderList;
	Color				color;
	int					mIndex;

	public Player(float x, float y, Player opponent, Color c) {
		color = c;
		// we are the bad guys!
		if (opponent != null) {
			x += opponent.mGrid.width();
			// c = Color.RED;
			opponent.mOpponent = this;
			mOpponent = opponent;
		}

		mGrid = new Grid(x, y, this);
		orderList = new Array<Unit>();
	}

	public Player setName(String name) { mName = name; return this; }
	public String getName() { return mName; }


	public void render(MyGdxGame game) {
		mGrid.render(game);
	}

	public boolean handleTouch(Vector3 pos) {
		return mGrid.handleTouch(pos);
	}

	public void addUnit(Unit u) {
		orderList.add(u);

		u.setOrder(orderList.size);
		System.out.println(u + " is " + u.getOrder());
	}

	public void removeUnit(Unit toBeRemoved) {
		orderList.removeValue(toBeRemoved, true);
		toBeRemoved.setOrder(-1);

		// reorder units
		int i = 1;
		for (Unit u: orderList) { u.setOrder(i++); }
		System.out.println(toBeRemoved + " back " + toBeRemoved.getOrder());
	}

	public void rewind() { mIndex = 0; }

	public Unit getNextUnit() {
		if (mIndex >= orderList.size)
			return null;
		Unit result;
		do {
			System.out.println(getName() + " popping " + mIndex);
			result = orderList.get(mIndex);
			mIndex += 1;
			if (!result.isDead())
				return result;
		} while (mIndex < orderList.size);
		return null;
	}

	public Color getColor() { return color; }
	public Player getOpponent() { return mOpponent; }

	public Tile getMainTargetTile(int l, UnitProperties.Range range) {
		Array<Tile> lane = mGrid.getLane(l);
		Tile result = null;
		int from = 0, to = lane.size, inc = 1, match = 1;
		switch(range) {
			case FIRST:
				break;
			case SKIP:
				match = 2;
				break;
			case LAST:
				from = lane.size - 1; 
				to = -1; 
				inc = -1; 
				match = 1;
		}
		for (int c = from; c != to; c += inc) {
			Tile tile = lane.get(c);
			if (tile.getUnit() != null) {
				if (--match == 0)
					break;
				result = tile;
			}
		}
		return result;
	}

	public Array<Unit> getTargets(Unit main) {
		Array<Unit> list = new Array<Unit>();

		int x = main.getX(), y = main.getY();
		for (GridPoint2 offset: main.getPattern()) {
			Tile t = mGrid.getTile(x + offset.x, y + offset.y);
			if (t.getUnit() != null)
				list.add(t.getUnit());
		}
		return list;

	}

	public Array<Unit> getTargets(
			int lane,
			UnitProperties.Range range, 
			UnitProperties.Pattern pat) 
	{
		Array<Unit> list = new Array<Unit>();

		if (lane < 0 || lane >= Grid.HEIGHT)
			return list;

		Tile tile = null;
		for (int i = 0; i < Grid.HEIGHT; ++i) {
			tile = getMainTargetTile((lane + i) % Grid.HEIGHT, range);
			if (tile == null) 
				continue;
			list.add(tile.getUnit());
			break;
		}

		return list;
	}

	static public Formation parseFormation(JsonValue root) {
		return new Formation(root);
	}

	public static class Formation {
		public class Deployment {
			int mX;
			int mY;
			UnitProperties mUnitProp;
		}

		Array<Deployment> mOrderList;

		Formation() {
		}

		Formation(JsonValue root) {
			mOrderList = new Array<Deployment>();
			for (JsonValue item: root.iterator()) {
				Deployment dep = new Deployment();
				int []pos = new int[]{0, 0};
				int i = 0;
				for (JsonValue p: item.get("position").iterator()) {
					if (i == 0)
						dep.mX = p.asInt();
					else
						dep.mY = p.asInt();
					++i;
				}

				String unitName = item.getString("unitName");

				dep.mUnitProp = MyGdxGame.getUnitPropByName(unitName);

				mOrderList.add(dep);
			}
		}

		public boolean validate() {
			return true;
		}
	}

	public void applyFormation(Formation f) {
		for (Formation.Deployment d: f.mOrderList) {
			mGrid.getTile(d.mX, d.mY).setUnit(new Unit(d.mUnitProp, this));
		}
	}

}

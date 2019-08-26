package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;


/* must create left player first */
public class Player {
	private Grid		mGrid;
	Player				mOpponent;
	Formation			format;
	Array<Unit>			orderList;
	Color				color;
	int					nextIdx;

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

	public void rewind() { nextIdx = 0; }

	public Unit getNextUnit() {
		if (nextIdx >= orderList.size)
			return null;
		Unit result;
		do {
			result = orderList.get(nextIdx);
			nextIdx += 1;
		} while (result.isDead() && nextIdx < orderList.size);
		if (nextIdx < orderList.size)
			return result;
		else
			return null;
	}

	public Color getColor() { return color; }
	public Player getOpponent() { return mOpponent; }

	private Tile getMainTargetTile(int l, UnitProperties.Range range) {
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

	public Array<Unit> getTargets(
			int lane,
			UnitProperties.Range range, 
			UnitProperties.Pattern pat) 
	{
		Array<Unit> list = new Array<Unit>();

		Tile tile = null;
		for (int i = 5; i == 0 && tile == null; --i) {
			tile = getMainTargetTile(lane, range);
			lane -= 1;
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

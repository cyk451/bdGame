package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import java.util.*;

/** Player represents real players of the game and AI palyers as well.
 * It's the top most object in the model hierachy.
 */
public class Player {
	private Grid			mGrid;
	String				mName;
	Player				mOpponent;
	Formation			mFormation;
	Array<Unit>			mOrderList;
	Color				mColor;
	int				mIndex;
	/** flip property affacts the facing of a player.
	 */
	boolean				mFlip;
	private ActMode			mMode;

	private Set<Unit>		mLostUnitSet;
	private int			mBattleUnitCount;

	/** ActMode defines actions a player object can perform. It is mostly
	 * control by owning screen object. */
	public enum ActMode {
		/** Player in this mode is waiting for opponent actions and can
		 * not perform any modifications.
		 */
		WAITING,
		/** Player in this mode can modify its deployment
		 */
		DEPLOY,
		/** In this mode user can adjust order of units, but not
		 * formation
		 */
		ORDER,
		/** A mode in which palyer can remove individual units.
		 */
		CLEAR,
		/** Unit rendering can change in this mode.
		 */
		BATTLE
	}

	public Player(float x, float y, Player opponent, Color c) {
		mFlip = false;
		mColor = c;
		// we are the bad guys!
		if (opponent != null) {
			x += opponent.mGrid.width();
			// c = Color.RED;
			opponent.mOpponent = this;
			mOpponent = opponent;
			mFlip = true;
		}

		mGrid = new Grid(x, y, this);
		mOrderList = new Array<Unit>();
		mLostUnitSet = new TreeSet<Unit>();
		mMode = ActMode.WAITING;
	}

	public void setMode(ActMode newMode) { mMode = newMode; }
	public ActMode getMode() { return mMode; }

	public Player setName(String name) { mName = name; return this; }
	public String getName() { return mName; }

	public void render(MyGdxGame game) {
		mGrid.render(game);
		for (Unit u :mOrderList)
			u.render(game);
	}

	public boolean handleDown(Vector3 pos) {
		return mGrid.handleDown(pos);
	}

	public boolean handleUp(Vector3 pos) {
		return mGrid.handleUp(pos);
	}

	public Unit getUnitByOrder(int order) {
		order -= 1;
		if (order >= mOrderList.size)
			return null;
		return mOrderList.get(order);
	}

	public void addUnit(Unit u) {
		// updateOrder();

		if (mOrderList.indexOf(u, false) != -1)
			return;

		mOrderList.add(u);

		u.setOrder(mOrderList.size);
		Gdx.app.log("addUnit", u + " is " + u.getOrder());
		if (u.getType() != UnitProperties.Type.INFRA)
			mBattleUnitCount += 1;

	}

	private void updateOrder() {
		Array<Unit> toBeRemoved = new Array<Unit>();

		int i = 1;
		for (Unit u: mOrderList) {
			if (u.getTile() == null) {
				toBeRemoved.add(u);
				continue;
			}
			u.setOrder(i++);
		}

		for (Unit u: toBeRemoved) {
			mOrderList.removeValue(u, true);
			u.setOrder(-1);
		}
	}

	public void notifyUnitLost(Unit u) {
		if (u.getType() != UnitProperties.Type.INFRA)
			mLostUnitSet.add(u);
	}

	public void removeUnit(Unit toBeRemoved) {
		mOrderList.removeValue(toBeRemoved, true);
		toBeRemoved.setOrder(-1);

		// reorder units
		int i = 0;
		for (Unit u: mOrderList) {
			i++;
			Gdx.app.log("Unit",
					u.getName() + " reordered from " +
					u.getOrder() + " to " + i);
			u.setOrder(i);
		}
	}

	public class UnitIterator implements Iterator<Unit> {
		private int mIndex;
		// private Array<Unit> mList;
		UnitIterator() {
			mIndex = 0;
		}

		@Override
		public boolean hasNext() {
			if (mIndex >= mOrderList.size)
				return false;
			do {
				if (!mOrderList.get(mIndex).isDead())
					return true;
				mIndex += 1;
			} while(mIndex < mOrderList.size);
			return false;
		}

		@Override
		public Unit next() {
			if (!hasNext())
				return null;
			return mOrderList.get(mIndex++);
		}
	}

	public Iterator<Unit> iterator() {
		return new UnitIterator();
	}

	public Unit getNextUnit() {
		if (mIndex >= mOrderList.size)
			return null;
		Unit result = null;
		do {
			result = mOrderList.get(mIndex);
			mIndex += 1;
			if (!result.isDead())
				return result;
		} while (mIndex < mOrderList.size);
		return null;
	}

	public Color getColor() { return mColor; }
	public Player getOpponent() { return mOpponent; }

	public Unit getMainTarget(int l, UnitProperties.Range range) {
		Grid.Lane lane = mGrid.getLane(l);
		Unit result = null;
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
			Unit unit = tile.getUnit();
			if (unit == null)
				continue;
			if (unit.isDead())
				continue;
			result = unit;
			if (--match == 0)
				break;
		}
		return result;
	}

	public Grid getGrid() { return mGrid; }

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

		for (int i = 0; i < Grid.HEIGHT; ++i) {
			Unit mainTarget = getMainTarget((lane + i) % Grid.HEIGHT, range);
			if (mainTarget == null)
				continue;
			list.add(mainTarget);
			break;
		}

		return list;
	}

	static public Formation parseFormation(JsonValue root) {
		return new Formation(root);
	}
	public boolean isLose() {
		Gdx.app.log("Player", getName() + " has " + mBattleUnitCount);
		return mBattleUnitCount == mLostUnitSet.size();
	}

	static public class Formation {
		public class Deployment {
			int mX;
			int mY;
			UnitProperties mUnitProp;
		}

		Array<Deployment> mOrderList;

		Formation() { }

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
			// mGrid.getTile(d.mX, d.mY).deployUnit(new Unit(d.mUnitProp, this), this);
			deployUnit(new Unit(d.mUnitProp, this),
					mGrid.getTile(d.mX, d.mY));
		}
	}

	// return boolean: deployUnit successful.
	public boolean deployUnit(Unit toBeDeployed, Tile tile) {
		if (mMode != ActMode.DEPLOY)
			return false;

		if (tile == null) {
			toBeDeployed.setTile(null);
			return true;
		}

		if (tile.getUnit() == toBeDeployed)
			return false;

		if (tile.getOwner() != this)
			return false;

		if (toBeDeployed.getOwner() != this)
			return false;

		Tile theOtherTile = toBeDeployed.getTile();
		toBeDeployed.setTile(null);

		Unit occupyingUnit = tile.getUnit();
		if (occupyingUnit != null) {
			tile.setUnit(null);
			if (theOtherTile != null) {
				occupyingUnit.setTile(theOtherTile);
			}
		}
		toBeDeployed.setTile(tile);
		return true;
	}

	public boolean swapOrder(Unit a, Unit b) {
		if (mMode != ActMode.ORDER && mMode != ActMode.CLEAR)
			return false;
		if (a == b)
			return true;
		if (a == null || a.getOwner() != this || !a.isDeployed())
			return false;
		if (b == null || b.getOwner() != this || !b.isDeployed())
			return false;
		// int ai = mOrderList.indexOf(a, false);
		// int bi = mOrderList.indexOf(b, false);
		int ai = a.getOrder() - 1;
		int bi = b.getOrder() - 1;
		mOrderList.set(ai, b);
		mOrderList.set(bi, a);
		a.setOrder(bi + 1);
		b.setOrder(ai + 1);
		return true;
	}

	public boolean getFlip() { return mFlip; }

}

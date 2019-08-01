package com.mygdx.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;


/* must create left player first */
class Player {
	private Grid		grid;
	Player				opponent;
	Grid.Formation		format;
	Array<Unit>			orderList;
	Color				color;
	int					nextIdx;

	Player(float x, float y, Player opponent, Color c) {
		color = c;
		// we are the bad guys!
		if (opponent != null) {
			x += opponent.grid.width();
			// c = Color.RED;
			opponent.opponent = this;
		}

		grid = new Grid(x, y, this);
		orderList = new Array<Unit>();
	}

	public void render(MyGdxGame game) {
		grid.render(game);
	}

	public boolean handleTouch(Vector3 pos) {
		return grid.handleTouch(pos);
	}

	public void addUnit(Unit u) {
		orderList.add(u);
		u.setOrder(orderList.size);
	}

	public void rewind() {
		nextIdx = 0;
	}

	public Unit getNextUnit() {
		if (nextIdx > orderList.size)
			return null;
		Unit result = orderList.get(nextIdx);
		nextIdx += 1;
		return result;
	}

	public Color getColor() { return color; }
	public Player getOpponent() { return opponent; }

	private Tile getMainTargetTile(int l, UnitProperties.Range range) {
		Array<Tile> lane = grid.getLane(l);
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
		while (tile == null) {
			tile = getMainTargetTile(lane, range);
			lane -= 1;
		}

		/*
		for (;;) {
		}
		*/
		return list;
	}
}

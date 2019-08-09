package com.mygdx.game;

import java.util.Iterator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Circle;


/* well, should i use tile map? */
class Grid extends Array<Array<Tile> > {

	float offX, offY;
	// Color color;
	Player owner;
	// haven't see a need to let them configurable
	static final int TILE_WIDTH = 6;
	static final int TILE_HEIGHT = 5;
	static final int TILE_EDGE_PXL = 35;
	static final int TILE_SPACE_PXL = 3;
	static Circle circle = new Circle(0, 0, 3);
	static final float SQRT3 = 1.732f;

	class Formation {
		class DeployedUnit {
			int gridX;
			int gridY;
			Unit unit;
		}
		Array<DeployedUnit> orderList;
		Formation() {
		}
	}

	public Grid(float x, float y, Player p) {
		placeTiles(x, y, p);
	}

	private void placeTiles(float x, float y, Player p) {
		offX = x; 
		offY = y;
		owner = p;

		// okay im lazy
		float e = TILE_EDGE_PXL;
		float s = TILE_SPACE_PXL;
		float[] shapePnts = {
			0, -e, 
			-e * SQRT3 * 0.5f, -e * 0.5f, 
			-e * SQRT3 * 0.5f, e * 0.5f, 
			0, e, 
			e * SQRT3 * 0.5f, e * 0.5f, 
			e * SQRT3 * 0.5f, -e * 0.5f, 
		};

		float x0 = offX + 0.5f * SQRT3 * e;
		y = offY + height() - e;

		for(int i = 0; i < TILE_HEIGHT; ++i) {
			Array<Tile> lane = new Array<Tile> (TILE_WIDTH);
			x = x0;
			if ((i & 1) == 1) {
				x += 0.5 * e * SQRT3;
				x += 0.5 * s;
			}
			for (int j = 0; j < TILE_WIDTH; ++j) {
				Tile tile = new Tile(shapePnts, x, y, owner);
				tile.x = j;
				tile.y = i;
				lane.add(tile);
				x += SQRT3 * e;
				x += s;
			}
			add(lane);
			y -= 1.5 * e;
			y -= 0.5 * s * SQRT3;
		}
	}

	public void render(MyGdxGame game) {
		ShapeRenderer sr = game.shapeRenderer;
		for (Iterator<Array<Tile> > liter = this.iterator(); liter.hasNext();) {
			Array<Tile> lane = liter.next();
			for (Iterator<Tile> titer = lane.iterator(); titer.hasNext(); ) {
				Tile tile = titer.next();
				tile.render(game);
			}
		}

		sr.begin(ShapeType.Line);
		sr.setColor(Color.GREEN);
		sr.circle(circle.x, circle.y, circle.radius);
		sr.end();
	}

	static public float width() {
		return 1.00f * ( TILE_WIDTH * (1.732f * TILE_EDGE_PXL + TILE_SPACE_PXL));
	}

	static public float height() {
		return 1.00f * ( TILE_HEIGHT * (1.5f * TILE_EDGE_PXL + TILE_SPACE_PXL) + 0.5f * TILE_EDGE_PXL);
	}

	public void applyFormation(Formation f) {
	}

	public Array<Tile> getLane(int count) {
		return get(count);
	}

	public Tile getCell(int x, int y) {
		return getLane(y).get(x);
	}

	public boolean contains(Vector3 pos) {
		float w = Grid.width();
		w += 0.5 * SQRT3 * TILE_EDGE_PXL;
		return new Rectangle(offX, offY, w, height()).contains(pos.x, pos.y);
	}

	public boolean handleTouch(Vector3 pos) {
		if (!contains(pos))
			return false;
		// System.out.println("grid is pressed...");
		// debugging draws
		circle.setX(pos.x);
		circle.setY(pos.y);

		for (Iterator<Array<Tile> > liter = this.iterator(); liter.hasNext();) {
			Array<Tile> lane = liter.next();
			for (Iterator<Tile> titer = lane.iterator(); titer.hasNext(); ) {
				Tile tile = titer.next();
				if (tile.handleTouch(pos))
					return true;
			}
		}
		return false;
	}
	/*
	public Vector2 getUnitImageBase(int x, int y) {
		return get(x)get(y);
	}
	*/
}

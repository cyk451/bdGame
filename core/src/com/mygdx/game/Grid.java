package com.mygdx.game;

import java.util.Iterator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Circle;

/*
 * A home-brew grid system. which is rather simple.
 */
/* well, should i use tile map? */
class Grid extends Array<Array<Tile> > {

	float mOffX, mOffY;
	Player mOwner;
	// Color color;
	// haven't see a need to let them configurable
	static final int WIDTH = 6;
	static final int HEIGHT = 5;
	static final int TILE_EDGE_PXL = 35;
	static final int TILE_SPACE_PXL = 3;
	static final float SQRT3 = 1.732f;

	// debug onlye
	static Circle circle = new Circle(0, 0, 3);

	public Grid(float x, float y, Player p) {
		placeTiles(x, y, p);
	}

	private void placeTiles(float x, float y, Player p) {
		mOffX = x; 
		mOffY = y;
		mOwner = p;

		// okay im lazy
		float e = TILE_EDGE_PXL;
		float s = TILE_SPACE_PXL;

		float x0 = mOffX + 0.5f * SQRT3 * e;
		y = mOffY + height() - e;

		for(int i = 0; i < HEIGHT; ++i) {
			Array<Tile> lane = new Array<Tile> (WIDTH);
			x = x0;
			if ((i & 1) == 1) {
				x += 0.5 * e * SQRT3;
				x += 0.5 * s;
			}
			for (int j = 0; j < WIDTH; ++j) {
				Tile tile = new Tile(x, y, mOwner);
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
		return 1.00f * ( WIDTH * (SQRT3 * TILE_EDGE_PXL + TILE_SPACE_PXL));
	}

	static public float height() {
		return 1.00f * ( HEIGHT * (1.5f * TILE_EDGE_PXL + TILE_SPACE_PXL) + 0.5f * TILE_EDGE_PXL);
	}

	public void applyFormation(Player.Formation f) {
	}

	public Array<Tile> getLane(int y) {
		if (y < 0 || y >= Grid.HEIGHT)
			return null;
		return get(y);
	}

	public Tile getTile(int x, int y) {
		if (x < 0 || x >= Grid.WIDTH)
			return null;
		if (getLane(y) == null)
			return null;
		return getLane(y).get(x);
	}

	public boolean contains(Vector3 pos) {
		float w = Grid.width();
		w += 0.5 * SQRT3 * TILE_EDGE_PXL;
		return new Rectangle(mOffX, mOffY, w, height()).contains(pos.x, pos.y);
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

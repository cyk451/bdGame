package com.mygdx.game;

import com.mygdx.game.screen.GameScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import java.util.Arrays;


public class Tile extends Polygon {
	// private Color color = Color.RED;
	private Player	mOwner;
	private Unit	mUnit;
	private Vector2 mRenderSpot;
	private float[] mTransformed;

	static Tile sHighlight = null;

	public int mX, mY;

	static private float[] getGridShapeVertices() {
		float e = Grid.TILE_EDGE_PXL;
		float s = Grid.TILE_SPACE_PXL;
		return new float[]{
			0, -e,
			-e * Grid.SQRT3 * 0.5f, -e * 0.5f,
			-e * Grid.SQRT3 * 0.5f, e * 0.5f,
			0, e,
			e * Grid.SQRT3 * 0.5f, e * 0.5f,
			e * Grid.SQRT3 * 0.5f, -e * 0.5f,
		};
	}

	public Player getOwner() { return mOwner; }

	// private Texture dropImage;
	public Tile(float x, float y, Player player) {
		super(getGridShapeVertices());
		setPosition(x, y);
		mOwner = player;

		mTransformed = getTransformedVertices();
		// this is some constant actually
		float []arr = mTransformed;
		mRenderSpot = new Vector2(
			(arr[0] + arr[6]) * 0.5f,
			(arr[1] + arr[7]) * 0.5f
		);

		// TODO don't hardcode here
		mRenderSpot.x -= 48 / 2;
		mRenderSpot.y -= 48 / 2;
	}


	public void render(MyGdxGame game) {

		ShapeRenderer sr = game.mShapeRenderer;

		/*
		sr.begin(ShapeType.Line);

		sr.setColor((sHighlight == this)? Color.WHITE: getColor());
		sr.polygon(mTransformed);
		sr.end();
		*/

		float radius = Grid.TILE_EDGE_PXL * 0.8f;
		float x = mRenderSpot.x + 48 / 2;
		float y = mRenderSpot.y + 48 / 2;
		sr.begin(ShapeType.Filled);

		sr.setColor(Color.GRAY);
		sr.circle(x, y, radius);
		sr.end();

		sr.begin(ShapeType.Line);

		sr.setColor((sHighlight == this)? Color.WHITE: getColor());
		sr.circle(x, y, radius);
		sr.end();

		if (mUnit != null)
			mUnit.render(mRenderSpot, game);
			// renderUnit(game);
	}

	public Color getColor() { return mOwner.getColor(); }

	public Unit getUnit() { return mUnit; }

	public void setUnit(Unit unit) {
		if (mUnit == unit)
			return ;

		Unit oldUnit = mUnit;
		mUnit = unit;

		if (oldUnit != null)
			oldUnit.setTile(null);

		if (mUnit != null)
			mUnit.setTile(this);
	}


	/*
	 * sChosenUnit tile => sChosenUnit tile
	 *    A    B         X    A
	 *    A    X         X    A
	 *    X    A         A    A
	 *    X    X         X    X
	 */
	// return boolean: is touch event handled.
	public boolean handleTouch(Vector3 pos) {
		if (!contains(pos.x, pos.y))
			return false;

		sHighlight = this;

		if (Unit.sChosenUnit == null && !isClear()) {
			System.out.println("select it");
			Unit.sChosenUnit = mUnit;
			GameScreen.sInfoBar.setInformation(mUnit);
		}
		return true;
	}

	public boolean handleUp(Vector3 pos) {
		if (!contains(pos.x, pos.y))
			return false;

		if (GameScreen.sClearButton.isChecked() && mUnit != null) {
			GameScreen.getControllingPlayer().deployUnit(mUnit, null);
			return true;
		}

		Unit temp = Unit.sChosenUnit;

		if (temp == null)
			return true;

		Unit.sChosenUnit = null;

		if (temp == mUnit)
			return true;

		System.out.println("deploying it");
		boolean handled = false;

		if (GameScreen.sOrderChangeButton.isChecked()) {
			handled = GameScreen.getControllingPlayer().swapOrder(temp, mUnit);
		} else {
			handled = GameScreen.getControllingPlayer().deployUnit(temp, this);
		}
		return true;
	}

	private boolean isClear() { return mUnit == null; }

	private void clear() {
		mUnit = null;
	}
}

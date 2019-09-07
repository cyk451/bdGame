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
import java.util.Arrays;


public class Tile extends Polygon {
	private PolygonRegion polygonRegion = null;
	// private Color color = Color.RED;
	private Player	mOwner;
	private Unit	mUnit;
	private float 	[]mRenderSpot;
	private float	[]mTransformed;

	static Tile highlight = null;

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
		mRenderSpot = new float[]{
			(arr[0] + arr[6]), 
			(arr[1] + arr[7])
		};
		mRenderSpot[0] *= 0.5;
		mRenderSpot[1] *= 0.5;

		mRenderSpot[1] -= 48 / 2;
		mRenderSpot[0] -= 48 / 2;
	}


	public PolygonRegion getPolygonRegion() {
		return polygonRegion;
	}

	public void render(MyGdxGame game) {

		ShapeRenderer sr = game.mShapeRenderer;

		sr.begin(ShapeType.Line);

		sr.setColor((highlight == this)? Color.WHITE: getColor());
		sr.polygon(mTransformed);
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
		if (contains(pos.x, pos.y)) {
			highlight = this;

			if (Unit.sChosenUnit != null) {
				if (GameScreen.getControllingPlayer().deployUnit(Unit.sChosenUnit, this))
					Unit.sChosenUnit = null;
				return true;
			} 

			if (!isClear()) {
				System.out.println("select it");
				Unit.sChosenUnit = mUnit;
				GameScreen.sInfoBar.setInformation(mUnit);
			}
			return true;
		}
		return false;
	}

	private boolean isClear() { return mUnit == null; }

	private void clear() { 
		/*
		Sprite unitSprite = mUnit.getIllust();

		mRenderSpot[1] += unitSprite.getHeight() / 2;
		mRenderSpot[0] += unitSprite.getWidth() / 2;
		*/
		mUnit = null; 
	}
}

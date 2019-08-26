package com.mygdx.game;

import com.mygdx.game.screen.GameScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import java.util.Arrays;


public class Tile extends Polygon {
	private PolygonRegion polygonRegion = null;
	// private Color color = Color.RED;
	private Player	mOwner;
	private Unit	mUnit;
	private float 	[]mRenderSpot;

	static Tile highlight = null;

	static public int x, y;

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


	// private Texture dropImage;

	public Tile(float x, float y, Player player) {
		super(getGridShapeVertices());
		setPosition(x, y);
		mOwner = player;

		// this is some constant actually
		float []arr = getTransformedVertices();
		mRenderSpot = new float[]{
			(arr[0] + arr[6]), 
			(arr[1] + arr[7])
		};
		mRenderSpot[0] *= 0.5;
		mRenderSpot[1] *= 0.5;
	}


	public PolygonRegion getPolygonRegion() {
		return polygonRegion;
	}

	public void render(MyGdxGame game) {

		ShapeRenderer sr = game.shapeRenderer;

		sr.begin(ShapeType.Line);

		sr.setColor((highlight == this)? Color.WHITE: getColor());
		sr.polygon(getTransformedVertices());
		sr.end();

		if (mUnit != null)
			renderUnit(game);
	}

	public Color getColor() { return mOwner.getColor(); }

	public Unit getUnit() { return mUnit; }

	// return boolean: setUnit successful.
	public boolean setUnit(Unit toBeDeployed) {
		if (mUnit == toBeDeployed)
			return false;

		if (toBeDeployed.getOwner() != mOwner)
			return false;

		System.out.println("add unit");

		if (!isClear()) {
			mUnit.setTile(null);
			mOwner.removeUnit(mUnit);
			clear();
		}
		if (toBeDeployed.isDeployed())
			toBeDeployed.getTile().clear();
		else
			mOwner.addUnit(toBeDeployed);
		toBeDeployed.setTile(this);

		mUnit = toBeDeployed;

		Sprite unitSprite = mUnit.getIllust();
		mRenderSpot[1] -= unitSprite.getHeight() / 2;
		mRenderSpot[0] -= unitSprite.getWidth() / 2;

		return true;
	}

	/*
	 * chosen tile => chosen tile
	 *    A    B         X    A
	 *    A    X         X    A
	 *    X    A         A    A
	 *    X    X         X    X
	 */

	// return boolean: is touch event handled.
	public boolean handleTouch(Vector3 pos) {
		if (contains(pos.x, pos.y)) {
			// System.out.println("handling");
			highlight = this;

			if (Unit.chosen != null) {
				if (setUnit(Unit.chosen))
					Unit.chosen = null;
				return true;
			} 

			if (!isClear()) {
				System.out.println("select it");
				Unit.chosen = mUnit;
				GameScreen.infoBar.setInformation(mUnit);
			}
			return true;
		}
		return false;
	}

	private boolean isClear() { return mUnit == null; }

	private void clear() { 
		Sprite unitSprite = mUnit.getIllust();

		mRenderSpot[1] += unitSprite.getHeight() / 2;
		mRenderSpot[0] += unitSprite.getWidth() / 2;
		mUnit = null; 
	}

	private void renderUnit(MyGdxGame game) {
		// just render here.
		Sprite unitSprite = mUnit.getIllust();
		BitmapFont font = new BitmapFont();

		game.batch.begin();

		game.batch.draw(unitSprite, mRenderSpot[0], mRenderSpot[1]);
		font.draw(game.batch, "[" + mUnit.getOrder() + "]", 
				mRenderSpot[0], mRenderSpot[1]);

		game.batch.end();
	}
}

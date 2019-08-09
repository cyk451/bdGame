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
	float[]			base;

	static Tile highlight = null;

	static public int x, y;


	// private Texture dropImage;

	public Tile(float[] vertices, float x, float y, Player player) {
		super(vertices);
		setPosition(x, y);
		mOwner = player;

		// this is some constant actually
		float []arr = getTransformedVertices();
		base = new float[]{
			(arr[0] + arr[6]), 
			(arr[1] + arr[7])
		};
		base[0] *= 0.5;
		base[1] *= 0.5;
		// System.out.println("Coord " + base[0] + ", " + base[1]);
		/*
		for (int i = 2; i < arr.length; i += 2) {
			if (arr[i] < base[0])
				base[0] = arr[i];
		}
		for (int i = 3; i < arr.length; i += 2) {
			if (arr[i] < base[1])
				base[1] = arr[i];
		}
		*/
	}


	public PolygonRegion getPolygonRegion() {
		return polygonRegion;
	}

	// this simply give first vertex of polygon
	public void render(MyGdxGame game) {

		ShapeRenderer sr = game.shapeRenderer;

		sr.begin(ShapeType.Line);

		sr.setColor((highlight == this)? Color.WHITE: getColor());
		sr.polygon(getTransformedVertices());
		sr.end();

		renderUnit(game);
	}

	public void renderUnit(MyGdxGame game) {
		if (mUnit == null)
			return;
		// just render here.
		Sprite unitSprite = mUnit.getIllust();
		BitmapFont font = new BitmapFont();

		float []renderSpot = Arrays.copyOf(base, 2);
		renderSpot[0] -= unitSprite.getWidth() / 2;
		renderSpot[1] -= unitSprite.getHeight() / 2;
		game.batch.begin();
		game.batch.draw(unitSprite, 
				renderSpot[0], renderSpot[1]);
		font.draw(game.batch, "[" + mUnit.getOrder() + "]", 
				renderSpot[0], renderSpot[1]);
		game.batch.end();
	}

	private void clear() { mUnit = null; }

	private boolean isClear() { return mUnit == null; }

	public Color getColor() { return mOwner.getColor(); }

	public Unit getUnit() { return mUnit; }

	private void setUnit(Unit toBeDeployed) {
		if (!isClear())
			mOwner.removeUnit(mUnit);
		if (toBeDeployed.isDeployed())
			toBeDeployed.getTile().clear();
		else
			mOwner.addUnit(toBeDeployed);
		toBeDeployed.setTile(this);
	}

	/*
	 * chosen tile => chosen tile
	 *    A    B         X    A
	 *    A    X         X    A
	 *    X    A         A    A
	 *    X    X         X    X
	 */

	public boolean handleTouch(Vector3 pos) {
		if (contains(pos.x, pos.y)) {
			highlight = this;

			if (Unit.chosen != null) {
				setUnit(Unit.chosen);
				Unit.chosen = null;
				return true;
			} 

			if (!isClear()) {
				Unit.chosen = mUnit;
				GameScreen.infoBar.setInformation(mUnit);
			}
			return true;
		}
		return false;
	}
}

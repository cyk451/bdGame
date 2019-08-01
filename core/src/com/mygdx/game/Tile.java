package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
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
	private Player owner = null;
	private Unit unit = null;

	static Tile highlight = null;

	float[] base;

	// private Texture dropImage;

	public Tile(float[] vertices, float x, float y, Player player) {
		super(vertices);
		setPosition(x, y);
		owner = player;

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

	public Color getColor() {
		return owner.getColor();
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
		if (unit == null)
			return;
		// just render here.
		Sprite unitSprite = unit.getIllust();
		BitmapFont font = new BitmapFont();

		float []renderSpot = Arrays.copyOf(base, 2);
		renderSpot[0] -= unitSprite.getWidth() / 2;
		renderSpot[1] -= unitSprite.getHeight() / 2;
		game.batch.begin();
		game.batch.draw(unitSprite, 
				renderSpot[0], renderSpot[1]);
		font.draw(game.batch, "[" + unit.getOrder() + "]", 
				renderSpot[0], renderSpot[1]);
		game.batch.end();
	}

	private void clear() {
		unit = null;
	}

	private boolean isClear() {
		return unit == null;
	}

	public Unit getUnit() {
		return unit;
	}

	private void setUnit(Unit toDeploy) {
		unit = toDeploy;
		unit.deployed = true;
		Unit.chosen = null;
		if (unit.getTile() != null)
			unit.getTile().clear();
		unit.setTile(this);
		unit.getOwner().addUnit(unit);
	}

	public boolean handleTouch(Vector3 pos) {
		if (contains(pos.x, pos.y)) {
			highlight = this;
			if (Unit.chosen != null)
				if (isClear()) {
					setUnit(Unit.chosen);
				} else {
					Unit.chosen = unit;
					GameScreen.infoBar.setInformation(unit);
				}
			return true;
		}
		return false;
	}
}

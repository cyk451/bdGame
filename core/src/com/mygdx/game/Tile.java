package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
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
	private Color color = Color.RED;
	private Unit unit = null;

	static Tile highlight = null;

	float[] base;

	// private Texture dropImage;

	public Tile(float[] vertices, float x, float y, Color c) {
		super(vertices);
		setPosition(x, y);
		color = c;

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
	public float[] getPosition() {
		return base;
	}
	public void render(MyGdxGame game) {

		ShapeRenderer sr = game.shapeRenderer;

		sr.begin(ShapeType.Line);

		sr.setColor((highlight == this)? Color.WHITE: color);
		sr.polygon(getTransformedVertices());
		sr.end();

		renderUnit(game);
	}

	public void renderUnit(MyGdxGame game) {
		if (unit == null)
			return;
		// just render here.
		Sprite unitSprite = unit.getIllust();

		float []pos = getPosition();
		game.batch.begin();
		game.batch.draw(unitSprite, 
				pos[0] - unitSprite.getWidth() / 2, 
				pos[1] - unitSprite.getHeight() / 2);
		game.batch.end();
	}

	public boolean handleTouch(Vector3 pos) {
		if (contains(pos.x, pos.y)) {
			highlight = this;
			if (Unit.chosen != null) {
				unit = Unit.chosen;
				unit.deployed = true;
				Unit.chosen = null;
			}
			return false;
		}
		return true;
	}
}

package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;


/* must create left player first */
class Player {
	Grid		grid;
	Player		opponent;
	Grid.Formation	format;

	Player(float x, float y, Player opponent) {
		Color c = Color.BLUE;

		// we are the bad guys!
		if (opponent != null) {
			x += opponent.grid.width();
			c = Color.RED;
			opponent.opponent = this;
		}

		grid = new Grid(x, y, c);
	}

	public void render(MyGdxGame game) {
		grid.render(game);
	}

	public boolean handleTouch(Vector3 pos) {
		return grid.handleTouch(pos);
	}
}

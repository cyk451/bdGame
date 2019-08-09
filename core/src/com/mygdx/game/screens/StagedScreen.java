package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

class StagedScreen implements Screen {
	protected Stage stage;
	protected Table mainTable;

	StagedScreen() {
	}

	@Override
	public void show() {
		stage = new Stage(new ScreenViewport());
		mainTable = new Table();

		mainTable.align(Align.center);

		stage.addActor(mainTable);
		Gdx.input.setInputProcessor(stage);

	}

	@Override
	public void resize(int width, int height) {
		mainTable.setWidth(width);
		mainTable.setHeight(height);
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void render(float delta) {
	}
}

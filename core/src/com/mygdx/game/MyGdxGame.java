package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;


public class MyGdxGame extends Game{
	public SpriteBatch batch;
	public ShapeRenderer shapeRenderer;
	// Stage stage;
	Skin skin;
	// public Texture img;

	public BitmapFont font;

	@Override
	public void create () {

		Gdx.app.log("Gdx version", com.badlogic.gdx.Version.VERSION);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		// stage = new Stage(new ScreenViewport());
		// img = new Texture("badlogic.jpg");

		skin = new Skin(Gdx.files.internal("uiskin.json"));
		font = new BitmapFont();
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

	public void dispose() {
		batch.dispose();
	}

}

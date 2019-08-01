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
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.utils.Align;



public class MyGdxGame extends Game{
	public BitmapFont		font;
	public ShapeRenderer	shapeRenderer;
	public Skin				skin;
	public SpriteBatch		batch;

	static Array<UnitProperties> unitPropertyList;

	@Override
	public void create () {

		Gdx.app.log("Gdx version", com.badlogic.gdx.Version.VERSION);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		skin = new Skin(Gdx.files.internal("uiskin.json"));
		font = new BitmapFont();

		loadResources();

		setScreen(new MainMenuScreen(this));
	}

	private void loadResources() {

		Json json = new Json();
		FileHandle unitFile = Gdx.files.internal("units.json");
		String text = unitFile.readString();

		JsonValue jsonRoot = new JsonReader().parse(text);
		unitPropertyList = new Array<UnitProperties>();
		for (JsonValue unitJson : jsonRoot.iterator()) {
			UnitProperties up = new UnitProperties(unitJson);
			unitPropertyList.add(up);
		}
	}

	@Override
	public void render () {
		super.render();
	}

	public void dispose() {
		batch.dispose();
	}

}

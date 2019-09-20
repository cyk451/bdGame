package com.mygdx.game;

import com.mygdx.game.screen.MainMenuScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.*;
import java.util.*;

// import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
// import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class MyGdxGame extends Game{
	private Skin					mUiSkin;
	public BitmapFont				mFont;
	public ShapeRenderer				mShapeRenderer;
	public SpriteBatch				mBatch;

	public Array<UnitProperties>			mUnitPropList;
	public Player.Formation mFormation;
	static private HashMap<String, UnitProperties>	sNamedPropMap;


	static public AssetManager mAssetManager;
	public Skin getUiSkin() { return mUiSkin; }

	@Override
	public void create () {

		Gdx.app.log("Gdx version", com.badlogic.gdx.Version.VERSION);
		mBatch = new SpriteBatch();
		mShapeRenderer = new ShapeRenderer();

		mUiSkin = new Skin(Gdx.files.internal("uiskin.json"));
		mFont = new BitmapFont();

		loadResources();

		setScreen(new MainMenuScreen(this));
	}

	private void loadResources() {

		// use resource manager?
		String text = Gdx.files.internal("units.json").readString();

		sNamedPropMap = new HashMap<String, UnitProperties>();

		JsonValue jsonRoot = new JsonReader().parse(text);
		mUnitPropList = new Array<UnitProperties>();
		for (JsonValue unitJson : jsonRoot.iterator()) {
			// Gdx.app.log("loadResources", "loading " + unitJson);
			UnitProperties up = new UnitProperties(unitJson);
			mUnitPropList.add(up);
			// Gdx.app.log("loadResources", "name: " + up.getName());
			sNamedPropMap.put(up.getName(), up);
		}

		text = Gdx.files.internal("test-formation.json").readString();
		jsonRoot = new JsonReader().parse(text);
		mFormation = Player.parseFormation(jsonRoot);
	}

	public static UnitProperties getUnitPropByName(String name) {
		return sNamedPropMap.get(name);
	}


	@Override
	public void render () {
		super.render();
	}

	public void dispose() {
		mBatch.dispose();
		mFont.dispose();
		mUiSkin.dispose();
		mShapeRenderer.dispose();
	}

}

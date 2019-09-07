package com.mygdx.game.screen;

import com.mygdx.game.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.files.*;
import java.util.Iterator;

public class GameScreen implements Screen {
	static final float DEFAULT_SCREEN_HEIGHT	= 480;
	static final float DEFAULT_SCREEN_WIDTH		= 800;
	static final float BOTTON_FRAME_HEIGHT		= 80;
	static final float TOP_FRAME_HEIGHT		= 100;

	final MyGdxGame			mGame;
	private Engine			mEngine;
	public OrthographicCamera	mCamera;
	public Stage			mStage;

	static public UnitSelectBar	sUnitSelectBar;
	static public InformationBar	sInfoBar;
	static Player			[]sPlayers;
	static Array<Unit>		sUnitList;

	public GameScreen(final MyGdxGame g) {
		mGame = g;
	}

	private void loadScene() {
		// load the mStage for enemy player
		//
		sPlayers[1].applyFormation(mGame.mFormation);
	}

	public class InformationBar extends Table {
		private Stage mStage;
		private Label mUnitNameLabel;
		private Label mStatusLabel;
		// private ImageButton pattern;

		InformationBar(Stage parent) {
			super();
			mStage = parent;

			align(Align.topLeft);
			pad(5.0f);

			mUnitNameLabel = new Label("", mGame.getUiSkin());
			mStatusLabel = new Label("", mGame.getUiSkin());
			add(mUnitNameLabel).align(Align.left);
			row();
			add(mStatusLabel);

			mStage.addActor(this);
		}

		void updateGeometry(float w, float h) {
			setWidth(w);
			float height = h * TOP_FRAME_HEIGHT / DEFAULT_SCREEN_HEIGHT;
			setHeight(height);
			setPosition(0, h - height);
		}

		public void setInformation(Unit u) {
			mUnitNameLabel.setText("" + u.getType() + " - " + u.getName());
			mStatusLabel.setText("HP: " + u.getHp()+ " DMG: " + u.getAtk() + "/" + "turn " + u.getRange());
			drawAttackArea();
		}

		public void drawAttackArea() {
		}

	}


	public class UnitSelectBar extends Table {
		private Stage		mStage;
		private HorizontalGroup	mUnitListGroup;

		UnitSelectBar(Stage parent) {
			super();
			mStage = parent;
			mUnitListGroup = new HorizontalGroup();

			align(Align.left);
			pad(5.0f);

			Button startButton = new TextButton("Fight", mGame.getUiSkin());
			startButton.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					System.out.println("Fight starts");
					mEngine.run();
				}
			});

			add(startButton);
			for (final Unit u : sUnitList) {
				mUnitListGroup.addActor(u.asButton());
			}
			ScrollPane sp = new ScrollPane(mUnitListGroup, mGame.getUiSkin());
			// sp.setScrollbarsVisible(false);
			// sp.setScrollbarTouch(false);

			add(sp);
			mStage.addActor(this);
		}

		void updateGeometry(float w, float h) {
			float height = h * BOTTON_FRAME_HEIGHT / DEFAULT_SCREEN_HEIGHT;

			setWidth(w);
			setHeight(height);
			setPosition(0, 0);
		}

		public void addButton(ImageButton button) {
			mUnitListGroup.addActor(button);
		}

		public void removeButton(ImageButton button) {
			mUnitListGroup.removeActor(button);
		}

	}

	private void createUi() {
		mStage		= new Stage(new ScreenViewport());
		sUnitSelectBar	= new UnitSelectBar(mStage);
		sInfoBar	= new InformationBar(mStage);

		Gdx.input.setInputProcessor(mStage);

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mCamera.update();

		mGame.mBatch.setProjectionMatrix(mCamera.combined);
		mGame.mShapeRenderer.setProjectionMatrix(mCamera.combined);

		for (Player player : sPlayers) {
			player.render(mGame);
		}

		mEngine.tick(delta);

		mStage.act(Gdx.graphics.getDeltaTime());
		mStage.draw();
		// mStage.setDebugAll(true);

		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			mCamera.unproject(touchPos);
			for (Player player : sPlayers)
				if (player.handleTouch(touchPos))
					break;
		}
	}

	@Override
	public void resize(int width, int height) {
		if (sInfoBar != null)
			sInfoBar.updateGeometry(width, height);
		if (sUnitSelectBar != null)
			sUnitSelectBar.updateGeometry(width, height);
		mStage.getViewport().update(width, height, true);
	}

	private void spawnPlayers() {
		Player us = new Player(0, BOTTON_FRAME_HEIGHT, null, Color.BLUE).setName("Player");
		Player them = new Player(0, BOTTON_FRAME_HEIGHT, us, Color.RED).setName("Enemy");
		sPlayers = new Player[]{us, them};
	}

	@Override
	public void show() {
		// start the playback of the background music
		// when the screen is shown
		mCamera = new OrthographicCamera();
		mCamera.setToOrtho(false, DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);

		spawnPlayers();

		sUnitList = new Array<Unit>();
		for (UnitProperties up: mGame.mUnitPropList)
			sUnitList.add(new Unit(up, sPlayers[0]));

		mEngine = new Engine(sPlayers);

		createUi();

		loadScene();
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
		mStage.dispose();
	}

	public static Player getControllingPlayer() {
		return sPlayers[0];
	}

}

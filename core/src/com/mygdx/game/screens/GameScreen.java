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
	static final float TOP_FRAME_HEIGHT			= 100;

	final MyGdxGame				mGame;
	private Engine				engine;
	public OrthographicCamera	camera;
	public Stage				stage;

	static Player					[]mPlayers;
	static UnitSelectBar			sUnitSelectBar;
	static Array<Unit>				unitList;
	static public InformationBar	infoBar;

	public GameScreen(final MyGdxGame g) {
		mGame = g;
	}

	private void loadScene() {
		// load the stage for enemy player
		//
		mPlayers[1].applyFormation(mGame.mFormation);
	}

	class UnitButton extends ImageButton {
		Unit unit;
		UnitButton(Unit u) {
			super(new SpriteDrawable(u.getIllust()));
			unit = u;
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
		}
	}

	public class InformationBar extends Table {
		private Stage stage;
		private Label unitName;
		private Label status;
		private ImageButton pattern;

		InformationBar(Stage parent, MyGdxGame game) {
			super();
			stage = parent;

			align(Align.topLeft);
			pad(5.0f);

			unitName = new Label("", mGame.skin);
			status = new Label("", mGame.skin);
			add(unitName).align(Align.left);
			row();
			add(status);

			stage.addActor(this);
		}

		void updateGeometry(float w, float h) {
			setWidth(w);
			float height = h * TOP_FRAME_HEIGHT / DEFAULT_SCREEN_HEIGHT;
			setHeight(height);
			setPosition(0, h - height);
		}

		public void setInformation(Unit u) {
			unitName.setText("" + u.getType() + " - " + u.getName());
			status.setText("HP: " + u.getHp()+ " DMG: " + u.getAtk() + "/" + "turn " + u.getRange());
			drawAttackArea();
		}

		public void drawAttackArea() {
		}

	}

	class UnitSelectBar extends Table {
		private Stage stage;

		UnitSelectBar(Stage parent, MyGdxGame game) {
			super();
			stage = parent;

			align(Align.left);
			pad(5.0f);

			Button startButton = new TextButton("Fight", game.skin);
			startButton.addListener(new ClickListener(){
				@Override 
				public void clicked(InputEvent event, float x, float y){
					// button.setText("You clicked the button");
					// game.setScreen(new GameScreen(game));
					// dispose();
					System.out.println("Ok fight starts");
					// validation check
					engine.run();
				}
			});

			add(startButton);
			HorizontalGroup horizontal = new HorizontalGroup();
			for (final Unit u : unitList) {
				UnitButton ub = new UnitButton(u);
				// ub.setHeight(48);
				horizontal.addActor(ub);
				ub.addListener(new ClickListener(){
					@Override 
					public void clicked(InputEvent event, float x, float y){
						System.out.println("A unit selected...");
						setUnitSelected(u);
					}
				});
			}
			ScrollPane sp = new ScrollPane(horizontal, mGame.skin);
			// sp.setScrollbarsVisible(false);
			// sp.setScrollbarTouch(false);

			add(sp);
			stage.addActor(this);
		}

		void updateGeometry(float w, float h) {
			float height = h * BOTTON_FRAME_HEIGHT / DEFAULT_SCREEN_HEIGHT;

			setWidth(w);
			setHeight(height);
			setPosition(0, 0);
		}

	}

	private void createUi() {
		stage = new Stage(new ScreenViewport());
		sUnitSelectBar = new UnitSelectBar(stage, mGame);
		infoBar = new InformationBar(stage, mGame);
		// table.add(horizontal);

		Gdx.input.setInputProcessor(stage);

	}

	public void setUnitSelected(Unit u) {
		if (!u.isDeployed())
			Unit.chosen = u;
		infoBar.setInformation(u);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		mGame.batch.setProjectionMatrix(camera.combined);
		mGame.shapeRenderer.setProjectionMatrix(camera.combined);

		for (Player player : mPlayers) {
			player.render(mGame);
		}

		engine.tick(delta);

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		stage.setDebugAll(true);

		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			for (Player player : mPlayers)
				if (player.handleTouch(touchPos))
					break;
		}
	}

	@Override
	public void resize(int width, int height) {
		if (infoBar != null)
			infoBar.updateGeometry(width, height);
		if (sUnitSelectBar != null)
			sUnitSelectBar.updateGeometry(width, height);
		stage.getViewport().update(width, height, true);
	}

	private void spawnPlayers() {
		Player us = new Player(0, BOTTON_FRAME_HEIGHT, null, Color.BLUE);
		Player them = new Player(0, BOTTON_FRAME_HEIGHT, us, Color.RED);
		mPlayers = new Player[]{us, them};
	}

	@Override
	public void show() {
		// start the playback of the background music
		// when the screen is shown
		camera = new OrthographicCamera();
		camera.setToOrtho(false, DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);

		spawnPlayers();

		unitList = new Array<Unit>();
		for (UnitProperties up: mGame.mUnitPropList)
			unitList.add(new Unit(up, mPlayers[0]));

		engine = new Engine(mPlayers);

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
		stage.dispose();
	}

}

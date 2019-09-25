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
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.files.*;
import java.util.Iterator;

public class GameScreen implements Screen {
	static final float DEFAULT_SCREEN_HEIGHT	= 480;
	static final float DEFAULT_SCREEN_WIDTH		= 800;
	static final float BOTTOM_FRAME_HEIGHT		= 80;
	static final float TOP_FRAME_HEIGHT		= 100;
	static final float CENTER_FRAME_HEIGHT		= 300;

	final MyGdxGame			mGame;
	private Engine			mEngine;
	public OrthographicCamera	mCamera;
	public Stage			mStage;
	public Table			mRootTable;
	private Viewport		mViewport;

	// static variables
	static public UnitSelectBar	sUnitSelectBar;
	static public InformationBar	sInfoBar;
	static public Button		sOrderChangeButton;
	static public Button		sClearButton;
	static Player			[]sPlayers;
	static Array<Unit>		sUnitList;
	static TopMessage		sTopMessage;

	public GameScreen(final MyGdxGame g) {
		mGame = g;
	}

	public static void setTopMessage(String what, int lastMs) {
		sTopMessage.showMessage(what, lastMs);
	}


	public class DeployButton extends Button {
		Image mInner;
		Unit mUnit;
		DeployButton(Unit u, Skin skin) {
			super(skin, "deploy");
			mUnit = u;
			mInner = new Image(new SpriteDrawable(u.getIllust()));
			add(mInner);

			addListener( new ChangeListener() {
				@Override // click listener
				public void changed(ChangeListener.ChangeEvent e, Actor a) {
					DeployButton btn = (DeployButton)a;
					Gdx.app.log("DeployButton", "checked = " + isChecked());
					if (!btn.isChecked()) {
						Unit.sChosenUnit = null;
						return;
					}
					sPlayers[0].setMode(Player.ActMode.DEPLOY);
					Unit.sChosenUnit = btn.mUnit;
					sInfoBar.setInformation(mUnit);
				}
			}
			);
		}


		@Override // button
		public void draw(Batch batch, float parentAlpha) {
			setDisabled(mUnit.isDeployed());
			super.draw(batch, parentAlpha);
		}

		@Override // button
		public void setDisabled(boolean disabled) {
			super.setDisabled(disabled);
			mInner.setColor(disabled? Color.GRAY: Color.WHITE);
		}
	}

	public class TopMessage extends Table
			implements Engine.EventListener {
		Label mMessageLabel;
		TopMessage() {
			super();

			// align(Align.center);

			mMessageLabel = new Label("", mGame.getUiSkin());
			mMessageLabel.setFontScale(3.0f);
			add(mMessageLabel).expandX();

			// mStage.addActor(this);
		}

		void showMessage(String what, int timeoutMs) {
			mMessageLabel.setText(what);
			if (timeoutMs <= 0) // no timeout
				return ;
			java.util.Timer t = new java.util.Timer();
			t.schedule(new java.util.TimerTask() {
				@Override
				public void run() {
					mMessageLabel.setText("");
				}
			}, timeoutMs);
		}

		// Engine event implementations
		public void onWin() {
			// TODO maybe replace this with an image resource
			// but ok with this until major functions work
			showMessage("You Win :)", 0);
		}
		public void onLose() {
			showMessage("You Lose :(", 0);
		}
		public void onRound(int round) {
			showMessage("Round" + round, Engine.INTERVAL_MS);
		}
	}

	public class InformationBar extends Table {
		private Label mUnitNameLabel;
		private Label mStatusLabel;
		private Image mRangePattern;
		// private ImageButton pattern;

		InformationBar() {
			super(mGame.getUiSkin());

			setBackground("textfield");

			align(Align.topLeft);
			pad(10.0f);
			// pad(5.0f);

			mUnitNameLabel = new Label("", mGame.getUiSkin());
			mStatusLabel = new Label("", mGame.getUiSkin());
			mRangePattern = new Image();
			add(mUnitNameLabel).left();
			row();
			add(mStatusLabel).left();
			row();
			add(mRangePattern).left();
			setVisible(false);

		}

		public void setInformation(Unit u) {
			setVisible(u != null);
			if (u == null)
				return;
			mUnitNameLabel.setText("" + u.getType()
					+ " - " + u.getName());
			mStatusLabel.setText("HP: " + u.getHp()+ " DMG: " +
					u.getAtk() + " " + u.getRange());

			mRangePattern.setDrawable(
					new SpriteDrawable(u.getPattern().asSprite())
					);

			drawAttackArea();
		}

		public void drawAttackArea() {
		}

	}


	public class UnitSelectBar extends Table {
		private HorizontalGroup	mUnitListGroup;
		private ButtonGroup mSelected;

		UnitSelectBar() {
			super(mGame.getUiSkin());
			setBackground("textfield");
			mUnitListGroup = new HorizontalGroup();
			mSelected = new ButtonGroup();
			mSelected.setMaxCheckCount(1);
			mSelected.setMinCheckCount(0);
			mSelected.setUncheckLast(true);

			align(Align.topLeft);
			pad(10.0f);

			Button startButton = new TextButton("Fight", mGame.getUiSkin());
			startButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor act) {
					Gdx.app.log("GameScreen", "Fight starts");
					mEngine.start();
					sUnitSelectBar.setVisible(false);
					sInfoBar.setVisible(false);
				}
			});

			add(startButton).height(52f);


			sOrderChangeButton = new TextButton("Order", mGame.getUiSkin(), "toggle");
			sOrderChangeButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor act) {
					TextButton btn = (TextButton)(act);
					if (btn.isChecked())
						sPlayers[0].setMode(Player.ActMode.ORDER);
				}
			});
			mSelected.add(sOrderChangeButton);

			add(sOrderChangeButton).height(52f);

			sClearButton = new TextButton("X", mGame.getUiSkin(), "toggle");
			sClearButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor act) {
					TextButton btn = (TextButton)(act);
					if (btn.isChecked())
						sPlayers[0].setMode(Player.ActMode.CLEAR);
				}
			});
			mSelected.add(sClearButton);

			add(sClearButton).height(52f);


			Skin skin = mGame.getUiSkin();
			for (final Unit u : sUnitList)
				addButton(new DeployButton(u, skin), u);
			ScrollPane sp = new ScrollPane(mUnitListGroup, mGame.getUiSkin());

			add(sp).left();
		}

		public void addButton(final Button button, final Unit unit) {

			mUnitListGroup.addActor(button);
			mSelected.add(button);
		}

		public void removeButton(Button button, Unit u) {
			mUnitListGroup.removeActor(button);
			mSelected.remove();
		}

	}

	private void createUi() {
		mViewport	= new FitViewport(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT, mCamera);
		mViewport.apply();
		mStage		= new Stage(mViewport);
		mRootTable	= new Table(mGame.getUiSkin());

		mRootTable.setFillParent(true);
		mStage.addActor(mRootTable);

		sUnitSelectBar	= new UnitSelectBar();
		sInfoBar	= new InformationBar();
		sTopMessage	= new TopMessage();

		mRootTable.top();

		mRootTable.add(sInfoBar).left().top().expandX().fillX()
			.height(Value.percentHeight(120f/480, mRootTable));
		mRootTable.row();
		mRootTable.add(sTopMessage).pad(5.0f).expandX()
			.height(Value.percentHeight(260f/480, mRootTable));
		mRootTable.row();
		mRootTable.add(sUnitSelectBar).left().bottom().expand().fill();

		InputMultiplexer multiplexer = new InputMultiplexer();
		final Vector3 tp = new Vector3();
		multiplexer.addProcessor(new InputAdapter() {
			@Override
			public boolean touchDown(int x, int y, int pointer, int button) {
				// Gdx.app.log("Mouse Event","Click at " + x + "," + y);
				Vector3 np = mViewport.unproject(tp.set(x, y, 0));
				// Gdx.app.log("Mouse Event","Click at " + tp.x + "," + tp.y);
				for (Player player : sPlayers)
					if (player.handleDown(np))
						return true;
				return false;
			}

			@Override
			public boolean touchUp(int x, int y, int pointer, int button) {
				Vector3 np = mViewport.unproject(tp.set(x, y, 0));
				for (Player player : sPlayers)
					if (player.handleUp(np))
						return true;
				return false;
			}
		});
		multiplexer.addProcessor(mStage);
		Gdx.input.setInputProcessor(multiplexer);

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mCamera.update();

		mGame.mBatch.setProjectionMatrix(mCamera.combined);
		mGame.mShapeRenderer.setProjectionMatrix(mCamera.combined);

		ShapeRenderer sr = mGame.mShapeRenderer;

		for (Player player : sPlayers) {
			player.render(mGame);
		}

		// mEngine.tick(delta);

		mStage.act(Gdx.graphics.getDeltaTime());
		mStage.draw();
		// mStage.setDebugAll(true);
	}

	@Override
	public void resize(int width, int height) {
		mStage.getViewport().update(width, height, true);
	}

	private void spawnPlayers() {
		Player us = new Player(0, BOTTOM_FRAME_HEIGHT, null, Color.BLUE).setName("Player");
		Player them = new Player(0, BOTTOM_FRAME_HEIGHT, us, Color.RED).setName("Enemy");
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

		createUi();

		loadScene();

		mEngine = new Engine(sPlayers, sTopMessage);
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

	private void loadScene() {
		Player opponent = sPlayers[1];
		opponent.setMode(Player.ActMode.DEPLOY);
		opponent.applyFormation(mGame.mFormation);
		opponent.setMode(Player.ActMode.WAITING);
	}

}

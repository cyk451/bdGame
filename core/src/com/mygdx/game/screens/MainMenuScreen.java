package com.mygdx.game.screen;
// package com.badlogic.drop;

import com.mygdx.game.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MainMenuScreen implements Screen {
	private Skin mSkin;
	static MainMenuScreen instance;

	static final int BUTTON_WIDTH = 400;

	static MyGdxGame mGame;
	private Stage stage;

	private Table mMainTable;
	private MenuButton mStartButton;
	private MenuButton mEditUnitButton;
	private VerticalGroup mMenuButtonList;


	class MenuButton extends TextButton {
		final Screen screen;
		MenuButton(String title, Screen s) {
			super(title, mSkin);
			screen = s;
			setWidth(600);
			addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					MainMenuScreen menu = MainMenuScreen.instance;
					menu.mGame.setScreen(screen);
					menu.dispose();
				}
			});
		}
	}

	public MainMenuScreen(final MyGdxGame game) {
		super();
		instance = this;
		mGame = game;
		mSkin = game.getUiSkin();
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void render(float delta) {

		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glClear(	GL20.GL_COLOR_BUFFER_BIT |
				GL20.GL_DEPTH_BUFFER_BIT |
				(Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));



		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		stage.setDebugAll(true);
	}

	@Override
	public void resize(int width, int height) {
		// super.resize();
		System.out.println("menu resized (" + width + ", " + height + ")");

		mMainTable.setWidth(width);
		mMainTable.setHeight(height);
		stage.getViewport().update(width, height, true);
	}

	private void addButton(MenuButton btn) {
		// mMainTable.add(btn).prefWidth(BUTTON_WIDTH).padBottom(20).align(Align.center);
		mMenuButtonList.addActor(btn);
		// mMainTable.row();
	}

	@Override
	public void show() {
		// super.show();
		System.out.println("menu showed");

		stage = new Stage(new ScreenViewport());
		mMainTable = new Table();
		mMainTable.setFillParent(true);

		HorizontalGroup hGroup = new HorizontalGroup();
		hGroup.expand().fill();
		mMenuButtonList = new VerticalGroup();
		mMenuButtonList.expand().fill();

		hGroup.addActor(mMenuButtonList);
		mMainTable.add(hGroup);


		// mMainTable.align(Align.center);

		mMainTable.pad(50);

		addButton(new MenuButton("New Game", new GameScreen(mGame)));
		addButton(new MenuButton("Edit Unit", new Editor(mGame)));

		stage.addActor(mMainTable);
		Gdx.input.setInputProcessor(stage);
	}
	//...Rest of class omitted for succinctness.

}

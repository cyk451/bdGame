package com.mygdx.game;
// package com.badlogic.drop;

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
	static public Skin skin;
	static public MainMenuScreen instance;

	static final int BUTTON_WIDTH = 400;

	static MyGdxGame game;
	private Stage stage;

	private Table table;
	private MenuButton startButton;
	private MenuButton editUnitButton;


	class MenuButton extends TextButton {
		final Screen screen;
		MenuButton(String title, Screen s) {
			super(title, MainMenuScreen.skin);
			screen = s;
			// setWidth(600);
			addListener(new ClickListener(){
				@Override 
				public void clicked(InputEvent event, float x, float y){
					// button.setText("You clicked the button");
					MainMenuScreen menu = MainMenuScreen.instance;
					menu.game.setScreen(screen);
					menu.dispose();
				}
			});
		}
	}

	public MainMenuScreen(final MyGdxGame game) {
		super();
		instance = this;
		this.game = game;
		skin = game.skin;
	}

	public void addButton(MenuButton btn) {
		table.add(btn).prefWidth(BUTTON_WIDTH).padBottom(20).align(Align.center);

		table.row();
	}

	public void dispose() {
		stage.dispose();
	}

	public void resume() {
	}

	public void hide() {
	}

	public void pause() {
	}

	public void render(float delta) {

		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		stage.setDebugAll(true);
	}

	public void resize(int width, int height) {
		System.out.println("menu resized (" + width + ", " + height + ")");
		
		table.setWidth(width);
		table.setHeight(height);
		stage.getViewport().update(width, height, true);
	}

	public void show() {
		System.out.println("menu showed");

		stage = new Stage(new ScreenViewport());
		table = new Table();

		table.align(Align.center);

		startButton = new MenuButton("New Game", new GameScreen(game));
		editUnitButton = new MenuButton("Edit Unit", new Editor(game));

		table.padTop(50);

		addButton(startButton);
		addButton(editUnitButton);

		stage.addActor(table);
		Gdx.input.setInputProcessor(stage);
	}
	//...Rest of class omitted for succinctness.

}

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

	final MyGdxGame game;
	private Stage stage;

	private Table table;
	private TextButton startButton;
	private TextButton editUnitButton;

	// OrthographicCamera camera;

	public MainMenuScreen(final MyGdxGame game) {
		this.game = game;

		// camera = new OrthographicCamera();
		// camera.setToOrtho(false, 800, 480);

		stage = new Stage(new ScreenViewport());
		Table table = new Table();
		table.setWidth(stage.getWidth());

		table.align(Align.center | Align.top);

		table.setPosition(0, Gdx.graphics.getHeight());

		startButton = new TextButton("New Game", game.skin);
		editUnitButton = new TextButton("Edit Unit", game.skin);

		table.padTop(30);
        startButton.addListener(new ClickListener(){
            @Override 
            public void clicked(InputEvent event, float x, float y){
                // button.setText("You clicked the button");
				game.setScreen(new GameScreen(game));
				dispose();
            }
        });

		table.add(startButton).padBottom(10);

		table.row();
		// table.add(editUnitButton);

		/*
		   HorizontalGroup horizontal = new HorizontalGroup();
		   for (int i = 0; i < 20; ++i) {
		   horizontal.addActor(new ImageButton(new TextureRegionDrawable(new TextureRegion(dropImage))));
		   }
		   ScrollPane sp = new ScrollPane(horizontal, skin);

		   sp.layout();
		// table.add(new ScrollPane(horizontal));
		// table.add(horizontal);
		table.add(sp).expandX().minHeight(100);
		*/

		table.add(editUnitButton);
        editUnitButton.addListener(new ClickListener(){
            @Override 
            public void clicked(InputEvent event, float x, float y){
                // button.setText("You clicked the button");
				game.setScreen(new Editor(game));
				dispose();
            }
        });

		stage.addActor(table);
		Gdx.input.setInputProcessor(stage);

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

	// camera.update();
		/*
		   game.batch.setProjectionMatrix(camera.combined);

		   game.batch.begin();
		   game.font.draw(game.batch, "Welcome to Drop!!! ", 100, 150);
		   game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
		   game.batch.end();

		   if (Gdx.input.isTouched()) {
		   game.setScreen(new GameScreen(game));
		   System.out.println("You pressed, but i have nothing to begin with");
		   dispose();
		   }
		   */
	}

	public void resize(int width, int height) {
	}

	public void show() {
	}
	//...Rest of class omitted for succinctness.

}

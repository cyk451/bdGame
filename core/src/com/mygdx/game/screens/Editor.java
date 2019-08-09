package com.mygdx.game.screen;

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


public class Editor implements Screen {

	final MyGdxGame game;
	Stage stage;

	private Table table;
	private TextField databaseName;
	private Label information;
	private TextButton loadButton;

	// OrthographicCamera camera;

	public Editor(final MyGdxGame game) {
		this.game = game;

/*
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
*/

		stage = new Stage(new ScreenViewport());
		Table table = new Table();
		table.setFillParent(true);
		table.pad(10, 10, 10, 10);
		// table.setWidth(stage.getWidth());

		table.align(Align.topLeft);

		// table.setPosition(0, Gdx.graphics.getHeight());

		databaseName = new TextField("Database", game.skin);

		loadButton = new TextButton("Load", game.skin);
        loadButton.addListener(new ClickListener(){
            @Override 
            public void clicked(InputEvent event, float x, float y){
                // button.setText("You clicked the button");
				// game.setScreen(new GameScreen(game));
				readFile();
            }
        });

		information = new Label("Select a database to edit", game.skin);

		// table.padTop(30);
		table.add(databaseName); // .padBottom(30).expandX();
		table.add(loadButton);

		table.row();
		table.add(information);
		// table.add(quitButton);

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

		Gdx.gl.glClearColor(0.75f, 0.75f, 0.75f, 1);
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
	public void readFile() {
		String dbn = databaseName.getText();
		if (!Gdx.files.internal(dbn).exists()) {
			information.setText("No such file '" + dbn + "' found.");
			return;
		}
		information.setText("File '" + dbn + "' loaded.");
	}
	//...Rest of class omitted for succinctness.

}

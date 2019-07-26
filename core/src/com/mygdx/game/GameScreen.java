package com.mygdx.game;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.Iterator;

public class GameScreen implements Screen {
	final MyGdxGame game;

	Array<Rectangle> raindrops;
	// Array<Grid> grids = new Array<Grid>(2);
	Array<Player> players = new Array<Player>(2);
	// Array<Image>

	// Texture dropImage;
	// Texture bucketImage;
	// Sound dropSound;
	// Music rainMusic;
	OrthographicCamera camera;
	// Rectangle bucket;
	// long lastDropTime;
	// int dropsGathered;

	Stage stage;
	final float BOTTON_FRAME_HEIGHT = 80;

	// private MapRenderer mapRenderer = new HexagonalTiledMapRenderer(new TmxMapLoader().load("test3.tmx"), .2f);

	Array<Unit> unitList;
	// private Unit chosen = null;

	private void loadResources() {
		unitList = new Array<Unit>();
		unitList.add(new Unit(null));
		unitList.add(new Unit(null));
		unitList.add(new Unit(null));
	}

	class UnitButton extends ImageButton {
		Unit unit;
		UnitButton(Unit u) {
			super(new SpriteDrawable(u.chibiSprite));
			unit = u;
		}
	}

	private void createUi() {
		stage = new Stage(new ScreenViewport());
		Table table = new Table();
		table.setWidth(stage.getWidth());
		table.align(Align.topLeft);
		table.setPosition(0, BOTTON_FRAME_HEIGHT);

		Button startButton = new TextButton("Fight", game.skin);

		startButton.addListener(new ClickListener(){
			@Override 
			public void clicked(InputEvent event, float x, float y){
				// button.setText("You clicked the button");
				// game.setScreen(new GameScreen(game));
				// dispose();
				System.out.println("Ok fight starts");
			}
		});

		HorizontalGroup horizontal = new HorizontalGroup();
		/*
		   for (int i = 0; i < 20; ++i) {
		   horizontal.addActor(new ImageButton(new TextureRegionDrawable(new TextureRegion(dropImage))));
		   }
		   */
		ScrollPane sp = new ScrollPane(horizontal, game.skin);

		table.add(sp);
		// table.add(horizontal);

		table.add(startButton);
		stage.addActor(table);
		Gdx.input.setInputProcessor(stage);
	}

	public GameScreen(final MyGdxGame game) {
		this.game = game;

		loadResources();

		// load the images for the droplet and the bucket, 64x64 pixels each
		// dropImage = new Texture(Gdx.files.internal("droplet.png"));
		// bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		// load the drop sound effect and the rain background "music"
		// dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		// rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		// rainMusic.setLooping(true);

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		createUi();
		/*
		// create a Rectangle to logically represent the bucket
		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2; // center the bucket horizontally
		bucket.y = 20; // bottom left corner of the bucket is 20 pixels above
		// the bottom screen edge
		bucket.width = 64;
		bucket.height = 64;
		*/

		// create the raindrops array and spawn the first raindrop
		// raindrops = new Array<Rectangle>();
		// spawnRaindrop();

		// Grid ours	= new Grid(0, 0, Color.BLUE);
		// Grid theirs = new Grid(ours.width(), 0, Color.RED);
		// grids.add(ours);
		// grids.add(theirs);
		Player us = new Player(0, BOTTON_FRAME_HEIGHT, null);
		players.add(us);
		Player them = new Player(0, BOTTON_FRAME_HEIGHT, us);
		players.add(them);

		// chosen = new Unit(null);

		// game.stage();
	}

	public void createUnitList() {
	}

	@Override
	public void render(float delta) {
		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		// tell the camera to update its matrices.
		camera.update();

		// a sound effect as well.

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		game.batch.setProjectionMatrix(camera.combined);
		game.shapeRenderer.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the bucket and
		// all drops
		/*
		   game.batch.begin();
		   game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);
		   game.batch.draw(bucketImage, bucket.x, bucket.y);
		   for (Rectangle raindrop : raindrops) {
		   game.batch.draw(dropImage, raindrop.x, raindrop.y);
		   }
		   game.batch.end();
		   */

		for (Player player : players) {
			player.render(game);
		}

		// process user input
		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			for (Player player : players)
				if (player.handleTouch(touchPos))
					break;
			// bucket.x = touchPos.x - 64 / 2;
		}
		/*
		   if (Gdx.input.isKeyPressed(Keys.LEFT))
		   bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		   if (Gdx.input.isKeyPressed(Keys.RIGHT))
		   bucket.x += 200 * Gdx.graphics.getDeltaTime();

		// make sure the bucket stays within the screen bounds
		if (bucket.x < 0)
		bucket.x = 0;
		if (bucket.x > 800 - 64)
		bucket.x = 800 - 64;

		// check if we need to create a new raindrop
		if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
		spawnRaindrop();

		// move the raindrops, remove any that are beneath the bottom edge of
		// the screen or that hit the bucket. In the later case we play back
		// a sound effect as well.
		Iterator<Rectangle> iter = raindrops.iterator();
		while (iter.hasNext()) {
		Rectangle raindrop = iter.next();
		raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
		if (raindrop.y + 64 < 0)
		iter.remove();
		if (raindrop.overlaps(bucket)) {
		dropsGathered++;
		// dropSound.play();
		iter.remove();
		}
		}
		*/
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// start the playback of the background music
		// when the screen is shown
		// rainMusic.play();
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
		// dropImage.dispose();
		// bucketImage.dispose();
		// dropSound.dispose();
		// rainMusic.dispose();
	}

}

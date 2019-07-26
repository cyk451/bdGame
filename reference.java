public class NetWarGame implements ApplicationListener {

    private static final int MAX_X_CELLS = 5;
    private static final int MAX_Y_CELLS = 4;
    private static final int HEX_CELL_WIDTH = 46;
    private static final int HEX_CELL_HEIGHT = 39;

    private TiledMap map;
    private Stage stage;
    private OrthographicCamera camera;
    private HexagonalTiledMapRenderer renderer;

    private InputMultiplexer inputMultiplexer;

    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        //Camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w / 2, h / 2);
        camera.zoom = 0.6f;
        camera.update();

        //Map
        createTiledMap();
        stage = new TiledMapStage(map);
        stage.getViewport().setCamera(camera);

        //Events
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);

        renderer = new HexagonalTiledMapRenderer(map);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (stage != null) {
            stage.act();
            stage.draw();
        }
        camera.update();
        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        stage.dispose();
        map.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    protected TiledMapTile[] getTiles() {
        TiledMapTile[] tiles = new TiledMapTile[4];
        tiles[0] = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal("hex_1.png"))));
        tiles[1] = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal("hex_2.png"))));
        tiles[2] = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal("hex_3.png"))));
        tiles[3] = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal("hex_template_test.png"))));

        return tiles;
    }

    protected void createTiledMap() {
        map = new TiledMap();

        MapLayers layers = map.getLayers();
        TiledMapTile[] tiles = getTiles();

        TiledMapTileLayer layer = new TiledMapTileLayer(MAX_X_CELLS, MAX_Y_CELLS, HEX_CELL_WIDTH, HEX_CELL_HEIGHT);
        for (int y = 0; y < MAX_X_CELLS; y++) {
            for (int x = 0; x < MAX_Y_CELLS; x++) {
                int id = (int)(Math.random() * 3);

                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(tiles[id]);
                layer.setCell(x, y, cell);
            }
        }
        layers.add(layer);
    }

    public class TiledMapActor extends Actor {

        private TiledMapTileLayer.Cell cell;

        public TiledMapActor(TiledMapTileLayer.Cell cell) {
            this.cell = cell;
        }

        public TiledMapTileLayer.Cell getCell() {
            return cell;
        }
    }

    public class TiledMapClickListener extends ClickListener {

        private TiledMapActor actor;

        public TiledMapClickListener(TiledMapActor actor) {
            this.actor = actor;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (actor != null && actor.getCell() != null) {
                actor.getCell().setTile(getTiles()[3]);
            }
        }
    }

    public class TiledMapStage extends Stage {

        private TiledMap tiledMap;

        public TiledMapStage(TiledMap tiledMap) {
            super(new ScreenViewport());

            this.tiledMap = tiledMap;

            for (MapLayer layer : this.tiledMap.getLayers()) {
                TiledMapTileLayer tiledLayer = (TiledMapTileLayer)layer;
                createActorsForLayer(tiledLayer);
            }
        }

        private void createActorsForLayer(TiledMapTileLayer tiledLayer) {
            for (int x = 0; x <= tiledLayer.getWidth(); x++) {
                for (int y = 0; y < tiledLayer.getHeight(); y++) {

                    TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                    TiledMapActor actor = new TiledMapActor(cell);

                    float offsetX = x * tiledLayer.getTileWidth();
                    float offsetY = y * tiledLayer.getTileHeight();
                    actor.setBounds(offsetX, offsetY, tiledLayer.getTileWidth(), tiledLayer.getTileHeight());

                    EventListener eventListener = new TiledMapClickListener(actor);
                    actor.addListener(eventListener);

                    addActor(actor);
                }
            }
        }
    }
}

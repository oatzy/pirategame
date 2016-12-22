package com.cavillum.pirategame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cavillum.pirategame.helpers.CheatCode;
import com.cavillum.pirategame.helpers.IGoogleServices;
import com.cavillum.pirategame.helpers.LevelBuilder;
import com.cavillum.pirategame.helpers.SaveHelper;
import com.cavillum.pirategame.ui.GameScene;
import com.cavillum.pirategame.ui.LayoutHandler;
import com.cavillum.pirategame.ui.MenuScene;

public class PirateGame extends Game {
	
	public static IGoogleServices googleServices;
	
	// Scenes
	public GameScene gameScene;
	public MenuScene menuScene;
	
	// Camera
	private OrthographicCamera _camera;
	private Viewport _viewport;
	
	// Resizing
	private Vector2 size;
	private int vpX, vpY, vpW, vpH;
	
	// Assets
	private AssetManager _assetManager = null;	
	private SpriteBatch _batch = null;
	
	// Save Helper
	public static SaveHelper save;
	public static LevelBuilder levels;
	public static CheatCode cc;
	
	// layout handler
	public static LayoutHandler layout;
	
	public static boolean isAndroid;
	public static boolean adsEnabled = true;
	
	public PirateGame(IGoogleServices gServices){
		googleServices = gServices;
	}
	
	@Override
	public void create () {
		
		switch(Gdx.app.getType()){
		case Android:
			isAndroid = true;
			break;
		default:
			isAndroid = false;
			break;
		}
		
		// suppress ads on start up
		if (isAndroid) googleServices.showAds(false);

		// Create assets manager
		_assetManager = new AssetManager();
				
		// Sprite batch
		_batch = new SpriteBatch();
		
		// Initialise Save
		save = new SaveHelper();
		levels = new LevelBuilder();
		cc = new CheatCode();
		
		// Initialise Layout
		layout = new LayoutHandler(this, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		// Camera
		_camera = new OrthographicCamera();
		_camera.setToOrtho(false, layout.width, layout.height); //y-up coords
		_viewport = new FitViewport(layout.width, layout.height, _camera);
		
		// Scenes
		menuScene = new MenuScene(this);
		setScreen(menuScene);
		gameScene = new GameScene(this); // maybe this shouldn't be instantiated until needed
		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.945f, 0.918f, 0.608f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
        _camera.update();
        _batch.setProjectionMatrix(_camera.combined);
        
        // Start rendering
        _batch.begin();
		
        super.render();
     		
        _batch.end();
	}
	
	@Override
	public void resize(int width, int height){
		_viewport.update(width, height);
		// for unprojecting
		size = Scaling.fit.apply(layout.width, layout.height, width, height);
	    vpX = (int)(width - size.x) / 2;
	    vpY = (int)(height - size.y) / 2;
	    vpW = (int)size.x;
	    vpH = (int)size.y;
	}
	
	@Override
	public void dispose(){
		gameScene.unload();
		menuScene.unload();
		_assetManager.dispose();
	}
	
	public AssetManager getAssetManager() {
		return _assetManager;
	}
	
	public SpriteBatch getSpriteBatch() {
		return _batch;
	}
	
	public OrthographicCamera getCamera() {
		return _camera;
	}
	
	public Viewport getViewport(){
		return _viewport;
	}
	
	public void unproject(Vector3 pos){
		// to make things tidier in the other files
		_camera.unproject(pos, vpX, vpY, vpW, vpH);
	}
	
}

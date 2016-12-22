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
import com.cavillum.pirategame.ui.GameScene;
import com.cavillum.pirategame.ui.MenuScene;

public class PirateGame extends Game {
	
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
	
	// Constants
	public static final int VIRTUAL_WIDTH = 720;
	public static final int VIRTUAL_HEIGHT = 1280;
	
	@Override
	public void create () {
		
		// Create assets manager
		_assetManager = new AssetManager();
				
		// Sprite batch
		_batch = new SpriteBatch();
		
		// Camera
		_camera = new OrthographicCamera();
		_camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT); //y-up coords
		_viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, _camera);
		
		// Scenes
		gameScene = new GameScene(this);
		menuScene = new MenuScene(this);
		
		setScreen(menuScene); //change to menu later
		
		//Gdx.app.log("Main", "created");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        _camera.update();
        _batch.setProjectionMatrix(_camera.combined);
        
        // Start rendering
        _batch.begin();
		
        super.render();
     		
        _batch.end();
		//Gdx.app.log("Main", "Rendered");
	}
	
	@Override
	public void resize(int width, int height){
		_viewport.update(width, height);
		// for unprojecting
		size = Scaling.fit.apply(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, width, height);
	    vpX = (int)(width - size.x) / 2;
	    vpY = (int)(height - size.y) / 2;
	    vpW = (int)size.x;
	    vpH = (int)size.y;
	    //Gdx.app.log("Main", "resize");
	}
	
	@Override
	public void dispose(){
		gameScene.unload();
		menuScene.unload();
		_assetManager.dispose();
		//Gdx.app.log("Main", "disposed");
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

package com.cavillum.pirategame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cavillum.pirategame.PirateGame;

public class MenuAssetHelper {
	private PirateGame _parent;
	public AssetManager assetManager;
	
	public TextureRegion imgLogo;
	
	private Stage _stage;
	private Skin _skin;
	private Table _table;
	private TextButton _playButton;
	private TextButton _continueButton;
	
	private Image _title;
	
	private boolean _loaded=false;
	
	public MenuAssetHelper(PirateGame game, AssetManager manager){
		_parent = game;
		assetManager = manager;
	}
	
	public void setStage(){
		// Stage
		
		_stage = new Stage(_parent.getViewport());
		//Gdx.input.setInputProcessor(_stage);
		_skin = new Skin(Gdx.files.internal("menuSkin.json"));
		
		_title = new Image(imgLogo);
		_title.setPosition(0, PirateGame.VIRTUAL_HEIGHT-_title.getHeight());
		
		
		_playButton = new TextButton("New Game", _skin);
		_playButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				_parent.gameScene.reset();
				_parent.setScreen(_parent.gameScene);
			}
		});
		
		_table = new Table();//.debug();
		_table.add(_playButton).pad(20);
		_table.row();
		_table.setSize(PirateGame.VIRTUAL_WIDTH, PirateGame.VIRTUAL_HEIGHT-_title.getHeight());
		_table.setPosition(0, 0);
		
		if (_parent.gameScene.canContinue()){
			_continueButton = new TextButton("Continue", _skin);
			_table.add(_continueButton).pad(20).row();
			_continueButton.addListener(new ClickListener(){
				@Override  public void clicked(InputEvent event, float x, float y) {
					_parent.setScreen(_parent.gameScene);
				}
			});
		}
		
		_stage.addActor(_title);
		_stage.addActor(_table);
		
		//Gdx.app.log("AssetHelper", "Stage set");
	}
	
	public Stage getStage(){
		if (_stage == null) setStage();
		return _stage;
	}
	
	public boolean isLoaded(){
		return _loaded;
	}
	
	public void load(){
		// Load Textures
		assetManager.load("menuLogo.png", Texture.class);
		//Gdx.app.log("AssetHelper", "assets loaded");
		
		_loaded = true;
	}
	
	public void unload(){
		if (_loaded){
			imgLogo = null;
			assetManager.unload("menuLogo.png");
			_stage.clear();
			_stage.dispose();
			_skin.dispose();
			//Gdx.app.log("AssetHelper", "Assets unloaded");
			
			_loaded = false;
		}
	}
	
	public void assignResources(){
		
		assetManager.finishLoading();
		
		// Assign
		imgLogo = new TextureRegion(assetManager.get("menuLogo.png", Texture.class));
		
		//Gdx.app.log("AssetHelper", "Assets assigned");
	}

}

package com.cavillum.pirategame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.cavillum.pirategame.PirateGame;

public class MenuScene extends BaseScene{
	
	PirateGame _parent;
	
	// Assets
	private MenuAssetHelper _assetHelper;
	
	private Stage _stage;
	
	// Main
	public MenuScene(PirateGame game){
		super(game);
		_parent = game;
		_stage = null;
		_assetHelper = new MenuAssetHelper(_parent, _parent.getAssetManager());
	}
	
	@Override
	public void show(){
		if (!_assetHelper.isLoaded()) _assetHelper.load();
		if (_stage != null) {
			_stage.clear();
			_assetHelper.setStage();
		}
		Gdx.input.setInputProcessor(_stage);
		Gdx.input.setCatchBackKey(false);
	}
	
	@Override
	public void hide(){
		//unload();
	}
	
	public void unload(){
		_assetHelper.unload();
	}
	
	@Override
	public void handleBackPress(){
		Gdx.app.exit();
	}
	
	@Override
	public void render(float delta){
		if (_parent.getAssetManager().update()) {
			_assetHelper.assignResources();
			_stage = _assetHelper.getStage();
			Gdx.input.setInputProcessor(_stage);
		}
		if (_stage != null) _stage.draw();
	}

}

package com.cavillum.pirategame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.cavillum.pirategame.PirateGame;

public class MenuScene extends BaseScene{
	
	private enum State {Loading, SplashPage, Connecting, MainMenu, PlayerMenu, LevelMenu, ScoreMenu};
	
	PirateGame _parent;
	
	// Assets
	private MenuAssetHelper _assetHelper;
	
	private State _state;
	
	private InputMultiplexer _inputs;
	
	private Vector3 _mousePos = new Vector3();
	private boolean _showSplash = true;
	
	// Main
	public MenuScene(PirateGame game){
		super(game);
		_parent = game;
		_state = State.Loading;
		_assetHelper = new MenuAssetHelper(_parent, _parent.getAssetManager());
		_inputs = new InputMultiplexer();
	}
	
	@Override
	public void show(){
		Gdx.input.setCatchBackKey(true);
		if (!_assetHelper.isLoaded()) _assetHelper.load();
		if (_inputs != null) {
			Gdx.input.setInputProcessor(_inputs);
		} else Gdx.input.setInputProcessor(this);
		if (_state != State.Loading) {
			if (_showSplash) showSplash();
			else showMenu();
		}
		if (!_showSplash && PirateGame.adsEnabled) PirateGame.googleServices.showAds(true);
	}
	
	@Override
	public void hide(){
		if (!_showSplash && PirateGame.adsEnabled) PirateGame.googleServices.showAds(false);
		//unload();
	}
	
	public void unload(){
		_assetHelper.unload();
	}
	
	@Override
	public void handleBackPress(){
		if (_assetHelper.isEditMode()) return;
		if (_state != State.PlayerMenu && _state != State.LevelMenu
				&& _state != State.ScoreMenu) Gdx.app.exit();
		else {
			if ((_state == State.LevelMenu || _state == State.ScoreMenu) && PirateGame.adsEnabled) 
				PirateGame.googleServices.showAds(true);
			showMenu();
		}
	}
	
	public void showSplash(){
		_state = State.SplashPage;
		_assetHelper.setSplash();
	}
	
	public void showLevels(){
		_state = State.LevelMenu;
		_assetHelper.buildLevelSelect();
		if (PirateGame.adsEnabled) PirateGame.googleServices.showAds(false);
	}
	
	public void showHighScores(){
		_state = State.ScoreMenu;
		_assetHelper.buildHighScores();
		if (PirateGame.adsEnabled) PirateGame.googleServices.showAds(false);
	}
	
	public void refreshMenu(){
		if (_state == State.MainMenu) _assetHelper.buildMainMenu();
	}
	
	public void showMenu(){
		_state = State.MainMenu;
		_assetHelper.buildMainMenu();
		if (_showSplash && PirateGame.adsEnabled) PirateGame.googleServices.showAds(true);
		_showSplash = false;
	}
	
	public void onSignIn(){
		PirateGame.googleServices.signIn();
		_state = State.Connecting;
	}
	
	// Player editing actions
	
	public void onPlayerButton(){
		_state = State.PlayerMenu;
		_assetHelper.buildPlayerOptions();
	}
	
	public void onAddPlayer(){
		PirateGame.save.addNextPlayer();
		_assetHelper.buildPlayerOptions();
		PirateGame.save.updatePlayers(true);
	}
	
	public void onRemovePlayer(String name){
		PirateGame.save.removePlayer(name);
		_assetHelper.buildPlayerOptions();
		PirateGame.save.updatePlayers(true);
	}
	
	public void onToggleMode(String name, Boolean mode){
		PirateGame.save.setPlayerMode(name, mode);
		_assetHelper.buildPlayerOptions();
		PirateGame.save.updatePlayers(true);
	}
	
	public void onRenamePlayer(String oldName, String newName){
		// should probably sanitise the new name
		PirateGame.save.renamePlayer(oldName, newName);
		_assetHelper.buildPlayerOptions();
		PirateGame.save.updatePlayers(true);
	}
	
	public void onResetPlayers(){
		PirateGame.save.setDefaultPlayers();
		_assetHelper.buildPlayerOptions();
	}
	
	@Override
	public void render(float delta){
		
		if (_state == State.Loading){
			if (_parent.getAssetManager().update()) {
				
				// Assign Resources
				_assetHelper.assignResources();
				
				// Set Input Handlers
				_inputs.addProcessor(this);
				_inputs.addProcessor(_assetHelper.getStage());
				Gdx.input.setInputProcessor(_inputs);
				
				// Set Display
				if (_showSplash) showSplash();
				else showMenu();
			}
			return;
		}
		
		if (_state == State.MainMenu){
			float a = 140f/(PirateGame.layout.width*PirateGame.layout.width);
			float b = PirateGame.layout.width/2;
			float c = PirateGame.layout.height - _assetHelper.menuBG.getRegionHeight();
			for (int i=0; i<PirateGame.layout.width; i+=_assetHelper.menuBG.getRegionWidth())
				_parent.getSpriteBatch().draw(_assetHelper.menuBG, i, 
						(float) ( a*(i-b)*(i-b) + c));
			drawStats();
		}
		
		if (_state == State.Connecting){
			if (PirateGame.googleServices.isSignedIn()){
				showSplash();
			}
		}
		_parent.getSpriteBatch().end();
		_assetHelper.render();
		_parent.getSpriteBatch().begin();
	}
	
	public void drawStats(){
		SpriteBatch batch = _parent.getSpriteBatch();
		
		int dy = (PirateGame.layout.isTablet()) ? 55 : -50; // shift in y for tablet
		
		batch.draw(_assetHelper.winBG, PirateGame.layout.width/2-310, 
				PirateGame.layout.height-200+dy);
		batch.draw(_assetHelper.scoreBG, PirateGame.layout.width/2+50, 
				PirateGame.layout.height-200+dy);
		
		_assetHelper.getFont().draw(batch, PirateGame.save.getWins()+" Wins", 
				PirateGame.layout.width/2-245, PirateGame.layout.height-137+dy, 
				175, Align.right, false);
		_assetHelper.getFont().draw(batch, PirateGame.save.getTotalPoints()+"", 
				PirateGame.layout.width/2+115, PirateGame.layout.height-137+dy, 
				175, Align.right, false);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		if (pointer == 0){ // Left mouse button clicked
	        _mousePos.x = screenX;
	        _mousePos.y = screenY;
	        _parent.unproject(_mousePos);
	        if (PirateGame.cc.secretMenu((int)_mousePos.x, (int)_mousePos.y)){
	        	//_assetHelper.unlockSecretMenu();
	        }
		}
		return false;
	}

}

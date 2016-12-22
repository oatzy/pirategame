package com.cavillum.pirategame.ui;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cavillum.pirategame.PirateGame;
import com.cavillum.pirategame.TurnHandler;
import com.cavillum.pirategame.helpers.LevelBuilder;
import com.cavillum.pirategame.objects.Grid;
import com.cavillum.pirategame.objects.Player;

public class GameScene extends BaseScene{
	
	public enum State {Loading, ArrangeItems, ItemSwap, CollectItem,
						ShowPopup, Waiting, ChooseNext, GameOver,
						Buried, Select, Reveal};
	
	private static final Vector2 gridOrigin = PirateGame.layout.origin; // location of grid top left corner
	private static final int itemSize = 102; // size of item images
	
	private PirateGame _parent = null;
	private SpriteBatch batch = null;
	private State _state;
	private GameStages _stages;
	
	private InputMultiplexer _inputs;

	private String _message;
	
	// Players
	private Player _player;
	
	private TurnHandler _turnHandler;
	
	// Item Moving
	private int _selectedSquare, _destSquare;
	private Vector3 _mousePos;
	private Vector2 _sqCoord;
	private double _animTime;
	private double _animTotal;
	
	private AnimationHelper _animate;
	
	// Assets
	private GameAssetHelper _assetHelper;
	
	private boolean _started = false;
	private boolean _quit = false;
	
	
	// MAIN //
	
	public GameScene(PirateGame game){
		super(game);
		_parent = game;
		batch = _parent.getSpriteBatch();
		_assetHelper = new GameAssetHelper(_parent.getAssetManager());
		_stages = new GameStages(this);
		_state = State.Loading;
		
		_inputs = new InputMultiplexer();
		
		_message = new String();
		
		
		// Players
		_player = new Player("You");// get rid?
		
		_turnHandler = new TurnHandler(this);
		_turnHandler.setLocalPlayer(_player);
		
		// load AI players from memory
		_turnHandler.loadAiPlayers();
		
		// Item Moving
		_selectedSquare = _destSquare = -1;
		_mousePos = new Vector3();
		_sqCoord = new Vector2();
		_animTime = 0;
		_animTotal = 0.1; // animation duration
		
	}
	
	public void reset(){
		_player.reset();
		_turnHandler.reset();
		if (_animate != null) _animate.reset();
		_stages.getStage().clear();
		_state = State.Loading;
		_message = "";
		_started = false;
		_quit = false;
		PirateGame.layout.showIndicator(false);
	}
	
	@Override
	public void show(){
		if (!_assetHelper.isLoaded()) _assetHelper.load();
		if (_inputs != null) {
			Gdx.input.setInputProcessor(_inputs);
		} else Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
		// on first call to show, ads take time to load
		if (!hasStarted() && PirateGame.adsEnabled) PirateGame.googleServices.showAds(true);
	}
	
	@Override
	public void hide(){
		if (!hasStarted() && PirateGame.adsEnabled) PirateGame.googleServices.showAds(false);
		//unload();
	}
	
	public void unload(){
		_stages.dispose();
		if (_assetHelper.isLoaded()) _assetHelper.unload();
	}
	
	@Override
	public void handleBackPress(){
		// Prevent local from quitting when they are killed
		if (PirateGame.levels.getGameType() == LevelBuilder.GameType.Knockout
				&& _turnHandler.isDead(_player.getID()) && _state != State.GameOver) return;
		//_parent.setScreen(_parent.menuScene);
		if (doShowQuit() && _state != State.GameOver){
			if (_state == State.ShowPopup && _stages.isShown(GameStages.Popup.Quit)) return;
			_stages.showQuitDialog(_state);
			_state = State.ShowPopup;
		}
		else _parent.setScreen(_parent.menuScene);
	}
	
	
	// On Actions //
	
	public void onStart(){
		if (PirateGame.levels.getGameType() == LevelBuilder.GameType.Buried)
			_state = State.Buried;
		else if (PirateGame.levels.getGameType() == LevelBuilder.GameType.Choose)
			_state = State.Select;
		else _state = State.CollectItem;
		_message = "";
		_started = true;
		updateSideBar();
		// TODO - increment plays count (to track quitting)
		// hide ads
		if (PirateGame.adsEnabled) PirateGame.googleServices.showAds(false);
	}
	
	public void onDialogOK(){
		_turnHandler.iterate();
	}
	
	public void onMenu(){ // possible redundancy
		_parent.setScreen(_parent.menuScene);
	}
	
	public void onAttack(String target){
		_turnHandler.localAttack(target);
	}
	
	public void onDefend(Player.dfType defence){
		_turnHandler.localDefence(defence);
	}
	
	public void onGameOver(){
		// Game Over - show score board
		_message = "Game Over!";
		_state = State.GameOver;
		_stages.showScoreBoard(_turnHandler.getScoreBoard());
		if (PirateGame.layout.hasSideBar())
			PirateGame.layout.getSideBar().buildHelp(State.GameOver, true);
		
		// Wins bonus
		if (_turnHandler.localWin()) onWin();
		else onLose();
		
		// High Score (?)
		if (PirateGame.save.isHighScore(_player.getScore()))
			onHighScore();
		
		// Submit to Google
		PirateGame.googleServices.submitScore(_player.getScore());
		PirateGame.googleServices.submitLevelScore(_player.getScore());
		PirateGame.googleServices.updateAchievements(_player.getScore(), _turnHandler.localWin());
	}
	
	public void onHighScore(){
		// TODO - stop these extra points adding to points on screen
		if (PirateGame.save.getPlays()<1) return;
		_player.addBonus(50000);
		_stages.showMessagePopup("New High Score!\nHave a bonus 50,000 points.");
	}
	
	public void onWin(){
		int wins = PirateGame.save.getWins() + 1;
		if (wins % 10 == 0){
			_player.addBonus(25000);
			_stages.showMessagePopup(wins+" wins!\nHave a bonus 25,000 points.");
		}
		else if (wins % 5 == 0){
			_player.addBonus(10000);
			_stages.showMessagePopup(wins+" wins!\nHave a bonus 10,000 points.");
		}
	}
	
	public void onLose(){
		int loses = PirateGame.save.getLoses() + 1;
		if (loses % 10 == 0){
			_player.addBonus(5000);
			_stages.showMessagePopup("Ouch, "+loses+" losses...\nHere's 5,000 sympathy points.");
		}
	}
	
	
	// Show UI Elements //
	
	public void showChooseNext(){
		if (_turnHandler.getQueue().getRound()<48){
			// prevent problems when Choose is the last item
			_state = State.ChooseNext;
			_message = "Tap the item you want next...";
			if (PirateGame.layout.hasSideBar())
				PirateGame.layout.getSideBar().buildHelp(State.ChooseNext, true);
			// TODO - go back to previous SB window
		} else _turnHandler.iterate();
	}
	
	public void showAttackDialog(ArrayList<String> opponents, Grid.sqType type){
		_state  = State.ShowPopup;
		_stages.showAttackDialog(opponents, type);
		updateSideBar();
	}
	
	public void showDefendDialog(String attacker, Grid.sqType type, 
									ArrayList<Player.dfType> defences){
		_state = State.ShowPopup;
		_stages.showDefendDialog(attacker, type, defences);
		updateSideBar();
	}
	
	public void showNotification(String message){
		_state = State.ShowPopup;
		_stages.showDialog(message);
	}
	
	public void showHistory(){
		PirateGame.layout.showIndicator(false);
		if (PirateGame.layout.hasSideBar()){
			PirateGame.layout.getSideBar().buildHistory(_turnHandler.getHistory());
			return;
		}
		_stages.showHistoryDialog(_turnHandler.getHistory(), _state);
		_state = State.ShowPopup;
	}
	
	public void showPlayers(){
		if (PirateGame.layout.hasSideBar()){
			if (PirateGame.levels.getGameType() == LevelBuilder.GameType.Knockout)
				PirateGame.layout.getSideBar()
					.buildPlayers(_turnHandler.getAlive(), _turnHandler.getDeadPlayers());
			else
				PirateGame.layout.getSideBar().buildPlayers(_turnHandler.getLocalOpponents());
			return;
		}
		if (PirateGame.levels.getGameType() == LevelBuilder.GameType.Knockout)
			_stages.showPlayerDialog(_turnHandler.getAlive(), _turnHandler.getDeadPlayers(),
					_state);
		else
			_stages.showPlayerDialog(_turnHandler.getLocalOpponents(), _state);
		_state = State.ShowPopup;
	}
	
	public void showHelp(){
		if (PirateGame.layout.hasSideBar()){
			PirateGame.layout.getSideBar().buildHelp(_state);
			return;
		}
		_stages.showHelpDialog(_state);
		_state = State.ShowPopup;
	}
	
	public void updateSideBar(){
		if (!PirateGame.layout.hasSideBar()) return;
		switch(PirateGame.layout.getSideBarType()){
		case History:
			PirateGame.layout.getSideBar().buildHistory(_turnHandler.getHistory());
			break;
		case Players:
			if (PirateGame.levels.getGameType() == LevelBuilder.GameType.Knockout)
				PirateGame.layout.getSideBar()
					.buildPlayers(_turnHandler.getAlive(), _turnHandler.getDeadPlayers());
			else
				PirateGame.layout.getSideBar().buildPlayers(_turnHandler.getLocalOpponents());
			break;
		case Help:
			PirateGame.layout.getSideBar().buildHelp(_state);
			break;
		default:
			break;
		}
	}
	
	
	// Misc. Helper Functions //
	
	public void changeState(State state){
		_state = state;
		updateSideBar();
		//_message = "";
	}
	
	public String getMessage(){
		return _message;
	}
	
	public void setMessage(String message){
		_message = message;
	}
	
	public boolean hasStarted(){
		// TODO - change for other modes so not started until square is tapped
		return _started;
	}
	
	public boolean doShowQuit(){
		return _quit;
	}
	
	public Player getPlayer(){
		return _player;
	}
	
	public Grid.sqType getCurrent(){
		return _player.getType(_turnHandler.getCurrentSquare());
	}
	
	public GameStages.Popup getTopPopup(){
		return _stages.getTopPopup();
	}
	
	public Viewport getViewport(){
		return _parent.getViewport();
	}
	
	public AnimationHelper getAnimator(){
		return _animate;
	}
	
	public GameAssetHelper getAssetHelper(){
		return _assetHelper;
	}

	
	// Updating & Rendering //
	
	public void update(float delta){
		
		// LOADING STATE
		if (_state == State.Loading) {
			// If loading finished
			if (_parent.getAssetManager().update()) {
				
				// Assign Resources
				_assetHelper.assignResources();
				
				// create animation helper
				_animate = new AnimationHelper(_parent.getSpriteBatch(), _assetHelper.pfont);
				
				// Set Inputs
				_inputs.addProcessor(this);
				_inputs.addProcessor(_stages.getStage());
				Gdx.input.setInputProcessor(_inputs);
				
				// create sidebar (if applicable)
				if (PirateGame.layout.hasSideBar()) 
					PirateGame.layout.createSideBar(_assetHelper);
				
				if (!(PirateGame.levels.getGameType() == LevelBuilder.GameType.Buried
						|| PirateGame.levels.getGameType() == LevelBuilder.GameType.Choose))
								
				// Change to Arrange State
				{
					// Set Stage
					_stages.showArrangeStage();
					_state = State.ArrangeItems;
					//_message = "Drag items to rearrange...";
					_message = "Arrange the items however you like...";
				} 
				
				else onStart();
				
				_stages.showSideBar();
				
			}
			return;
		} // END LOADING
		
		// ITEM SWAP STATE
		if (_state == State.ItemSwap){
			// when the grid is being arranged and two items are swapped (animation)
			if ((_animTime += delta) >= _animTotal){
				// animation finished, reset
				_state = State.ArrangeItems;
				_animTime = 0;
				_selectedSquare = -1;
				_destSquare = -1;
			}
		} // END ITEM SWAP
		
		//if (_state == State.Buried) _message = "Tap a sand mound to dig for treasure...";
		//if (_state == State.Select) _message = "Tap any item to collect it...";
		
	}
	
	@Override
	public void render(float delta){
		update(delta);
		
		// LOADING STATE
		if (_state == State.Loading){
			// don't draw anything or it'll crash
			return;
		}
		
		// UI elements
		PirateGame.layout.drawUiElements(_assetHelper);
		
		// Draw Grid
		drawGrid();
		
		// After Start - Animations
		if (hasStarted()){
			
			// defences
			_animate.drawDefences();
			
			// Floating points add
			_animate.drawFloaters();
		}
		
		// COLLECT ITEM STATE
		if (_state == State.CollectItem){
			
			// show item selector
			_sqCoord = getCoords(_turnHandler.getCurrentSquare());
			batch.draw(_assetHelper.selector, _sqCoord.x, _sqCoord.y);
			
			// Throb current icon
			TextureRegion img = _assetHelper.getItemImage(_player.getType(
					_turnHandler.getCurrentSquare()));
			batch.draw(img, _sqCoord.x, _sqCoord.y, itemSize/2, itemSize/2,
					itemSize, itemSize, (float)_animate.throb(), (float)_animate.throb(), 0);
			
		} // END COLLECT
		
		// Draw Stage (buttons, pop-ups, etc.)
		batch.end(); // grid doesn't draw without this
		
		_stages.render();
		
		batch.begin();
		
	}
	
	public void drawGrid(){
		
		TextureRegion img = null;
		float imgX, imgY;
		double rotate = 0;
		
		if (_state == State.ArrangeItems || _state == State.ChooseNext
				|| _state == State.Select)
				rotate = (float)_animate.wiggle();
		
		// Draw grid items
		for(int i=0; i<49; i++){
			
			// Item position
			_sqCoord = getCoords(i);
			
			// grid squares
			batch.draw(_assetHelper.gridSq, _sqCoord.x, _sqCoord.y);
			
			if (PirateGame.levels.getGameType() == LevelBuilder.GameType.Buried
					&& _state != State.Reveal && !_player.getGrid().isEmpty(i))
				img = _assetHelper.burySq;
			else img = _assetHelper.getItemImage(_player.getType(i));
			
			if (img != null){
				// if the item isn't being moved
				if (!((_state == State.ArrangeItems && i == _selectedSquare) ||
						(_state == State.ItemSwap && i == _destSquare) ||
						(_state == State.CollectItem && i == _turnHandler.getCurrentSquare())
						)){
					
					// Draw wiggler
					if (_state == State.ArrangeItems || _state == State.ChooseNext){
						batch.draw(img, _sqCoord.x, _sqCoord.y, itemSize/2, itemSize/2,
								itemSize, itemSize, 1, 1, (float) rotate);
					}
					// Draw static
					else batch.draw(img, _sqCoord.x, _sqCoord.y);
				}
			} 
			img = null;	
		}
		
		// Moved items - this is separate so they're drawn on top of all others
		
		if (_state == State.ArrangeItems && _selectedSquare != -1){
			// item is being dragged
			img = _assetHelper.getItemImage(_player.getType(_selectedSquare));
			imgX = _mousePos.x-(itemSize/2);
			imgY = _mousePos.y-(itemSize/2);
			batch.draw(img, imgX, imgY);
		} 
		
		else if (_state == State.ItemSwap && _destSquare != -1){
			// animate swapped item
			img = _assetHelper.getItemImage(_player.getType(_destSquare));
			_sqCoord = animateSwap(_selectedSquare, _destSquare, _animTime, _animTotal);
			batch.draw(img, _sqCoord.x, _sqCoord.y);
		}
		img = null;
	}
	
	
	// Coordinate Helper //

	public int getGridIndex(int x, int y){
		// get grid index for coords (x,y)
		if (x>=gridOrigin.x && y <= gridOrigin.y){ // to avoid rounding issues 
			
			int gridX = (int)(x-(gridOrigin.x))/itemSize;
			int gridY = (int)(gridOrigin.y-y)/itemSize;
			
			if (gridX < 7 && gridY < 7) return (7*gridY + gridX);
		}
		return -1;
	}
	
	public Vector2 getCoords(int index){
		float y = gridOrigin.y-itemSize*(1+index/7);
		float x = gridOrigin.x+itemSize*(index%7);
		return new Vector2(x,y);
	}
	
	public Vector2 animateSwap(int orig, int dest, double anim, double animTot){
		Vector2 origCoord = getCoords(orig);
		Vector2 destCoord = getCoords(dest);
		float x = (float) (origCoord.x+anim*(destCoord.x - origCoord.x)/animTot);
		float y = (float) (origCoord.y+anim*(destCoord.y - origCoord.y)/animTot);
		return new Vector2(x, y);
	}
	
	public void setAnimTotal(int index1, int index2){
		int dy = (index1/7)-(index2/7);
		int dx = (index1%7)-(index2%7);
		_animTotal = 0.08*Math.pow((dx*dx + dy*dy), 0.25);
	}

	
	// TOUCH HANDLING //
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (pointer == 0){ // Left mouse button clicked
	        _mousePos.x = screenX;
	        _mousePos.y = screenY;
	        _parent.unproject(_mousePos);
	        
	        int index = getGridIndex((int)_mousePos.x, (int)_mousePos.y);
	        
	        // Konami
	        if (index != -1 && !hasStarted())
	        	if (PirateGame.cc.cheat(index)) {
	        		_message = "Unlimited Lives Unlocked!";
	        		if (PirateGame.layout.hasSideBar() 
	        				&& PirateGame.layout.getSideBarType() == LayoutHandler.SideType.Help)
	        			PirateGame.layout.getSideBar().buildHelp(_state, "Unlimited Lives Unlocked!");
	        		PirateGame.googleServices.unlockKonami();
	        	}
	        
	        // Help
	        if (PirateGame.layout.helpClicked(_mousePos.x, _mousePos.y)
						&& !_stages.isShown(GameStages.Popup.Help)
						&& !_stages.isShown(GameStages.Popup.Quit)) showHelp();
	        
	        // ARRANGE GRID STATE
 			if (_state == State.ArrangeItems){
 				// item clicked
 				if (index != -1){
 					if (_selectedSquare == -1){
 						_selectedSquare = index; // select
 					}
 					if (_player.getGrid().isEmpty(index)){
 						_selectedSquare = -1; // don't allow selecting/dragging empty squares
 					}
 				}
 				return false;
 			} // END ARRANGE GRID
 			
 			// REVEALED STATE
 			if (_state == State.Reveal) {
 				_turnHandler.iterate();
 				return true;
 			}
 			
 			// CHOOSE NEXT STATE
 			if (_state == State.ChooseNext){
 				if (index != -1 && !_player.getGrid().isEmpty(index)){
 					_turnHandler.getQueue().addNext(index);
 					//_message = "";
 					_message = _turnHandler.getLastEvent();
 					_turnHandler.iterate();
 				}
 				return true;
 			} // END CHOOSE NEXT
 			
 			
 			// History, Players
 			if (_state != State.GameOver){
 				
 				if ((PirateGame.layout.notifClicked(_mousePos.x, _mousePos.y) 
 						&& !_stages.isShown(GameStages.Popup.History)
 						&& !_stages.isShown(GameStages.Popup.Quit))) showHistory();
 				
 				if (PirateGame.layout.playersClicked(_mousePos.x, _mousePos.y)
 						&& !_stages.isShown(GameStages.Popup.Players)
 						&& !_stages.isShown(GameStages.Popup.Quit)) showPlayers();
 			}
 			
 			
 			// BURIED STATE
 			if (_state == State.Buried && index != -1){
 				_quit = true;
 				if (!_player.getGrid().isEmpty(index)){
 					_turnHandler.setCurrentSquare(index);
 					changeState(State.CollectItem);
 				}
 				return true;
 			} // END BURIED STATE
 			
 			
 			// SELECT STATE
 			if (_state == State.Select && index != -1){
 				_quit = true;
 				if (!_player.getGrid().isEmpty(index)){
 					_turnHandler.setCurrentSquare(index);
 					
 					// set floating point text (where appt)
					_animate.setPointsText(_player.getType(index), _player.getPoints());
					
					// defences
					if (_player.getType(index) == Grid.sqType.sqShield){
						_animate.addDefence(_assetHelper.imgShield, Player.dfType.dfShield);
					}
					if (_player.getType(index) == Grid.sqType.sqMirror){
						_animate.addDefence(_assetHelper.imgMirror, Player.dfType.dfMirror);
					}
					
					// do item action
					_turnHandler.localCollect();
 				}
 				return true;
 			} // END SELECT STATE
 			
 			
			// COLLECT ITEM STATE
			if (_state == State.CollectItem && index != -1){
				_quit = true;
				if (index == _turnHandler.getCurrentSquare()){
					
					// set floating point text (where appt)
					_animate.setPointsText(_player.getType(index), _player.getPoints());
					
					// defences
					if (_player.getType(index) == Grid.sqType.sqShield){
						_animate.addDefence(_assetHelper.imgShield, Player.dfType.dfShield);
					}
					if (_player.getType(index) == Grid.sqType.sqMirror){
						_animate.addDefence(_assetHelper.imgMirror, Player.dfType.dfMirror);
					}
					
					// do item action
					_turnHandler.localCollect();
				}
				return true;
			} // END COLLECT ITEM
			
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (pointer == 0){ // Left mouse button clicked
	        _mousePos.x = screenX;
	        _mousePos.y = screenY;
	        _parent.unproject(_mousePos);
	        
	        int index = getGridIndex((int)_mousePos.x, (int)_mousePos.y);
	        
	        // ARRANGE GRID STATE
	        if (_state == State.ArrangeItems && index != -1){
	        	if (_selectedSquare != -1 && _selectedSquare != index){
	        		
	        		// Swap items
	        		_player.getGrid().swap(_selectedSquare, index);
	        		
	        		// animate swap
	        		if (_player.getGrid().isEmpty(index)){
	        			
	        			// don't need to animate empty square swap
	        			_selectedSquare = _destSquare = -1;
	        		} 
	        		else{
	        			setAnimTotal(_selectedSquare,index);
		        		_destSquare = _selectedSquare;
		        		_selectedSquare = index;
		        		_state = State.ItemSwap;
	        		}
	        	} else{
	        		// else return to original square
	        		_selectedSquare = -1; // item 'dropped'
	        	}
	        	return false;
	        } // END ARRANGE GRID
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// ARRANGE GRID STATE
		if (_state == State.ArrangeItems){
			// Note - icon sometimes trails behind movement
			_mousePos.x = screenX;
	        _mousePos.y = screenY;
	        _parent.unproject(_mousePos);
	        
	        // Make sure items stay within grid
	        if (_mousePos.x < gridOrigin.x) _mousePos.x = gridOrigin.x;
	        if (_mousePos.y > gridOrigin.y) _mousePos.y = gridOrigin.y;
	        if (_mousePos.x > gridOrigin.x+(7*itemSize)) _mousePos.x = gridOrigin.x+(7*itemSize);
	        if (_mousePos.y < gridOrigin.y-(7*itemSize)) _mousePos.y = gridOrigin.y-(7*itemSize);
		} // END ARRANGE GRID
		return false;
	}

}


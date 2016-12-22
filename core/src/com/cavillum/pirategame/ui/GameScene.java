package com.cavillum.pirategame.ui;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cavillum.pirategame.PirateGame;
import com.cavillum.pirategame.TurnHandler;
import com.cavillum.pirategame.objects.ComputerPlayer;
import com.cavillum.pirategame.objects.Grid;
import com.cavillum.pirategame.objects.Player;

public class GameScene extends BaseScene{
	
	public enum State {Loading, ArrangeItems, ItemSwap, CollectItem,
						ShowPopup, Waiting, ChooseNext, GameOver};
	
	private static final Vector2 gridOrigin = new Vector2(82,1052); // location of grid top left corner
	private static final int itemSize = 85; // size of item images
	
	private PirateGame _parent = null;
	private SpriteBatch batch = null;
	private State _state;
	private GameStages _stages;
	private Stage _stage;
	private Dialog _dialog;
	
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
	
	// Assets
	private GameAssetHelper _assetHelper;	
	
	
	// MAIN //
	
	public GameScene(PirateGame game){
		super(game);
		_parent = game;
		batch = _parent.getSpriteBatch();
		_stages = new GameStages(this);
		_stage = new Stage(_parent.getViewport());
		_state = State.Loading;
		
		_inputs = new InputMultiplexer();
		
		_message = new String();
		
		
		// Players
		_player = new Player("You");// get rid?
		
		_turnHandler = new TurnHandler(this);
		_turnHandler.setLocalPlayer(_player);
		_turnHandler.addComputerPlayer(new ComputerPlayer("Sparrow", true));
		_turnHandler.addComputerPlayer(new ComputerPlayer("Barbosa", true));
		_turnHandler.addComputerPlayer(new ComputerPlayer("Turner"));
		_turnHandler.addComputerPlayer(new ComputerPlayer("Swan"));
		
		// Item Moving
		_selectedSquare = _destSquare = -1;
		_mousePos = new Vector3();
		_sqCoord = new Vector2();
		_animTime = 0;
		_animTotal = 0.1; // animation duration
		
		_assetHelper = new GameAssetHelper(_parent.getAssetManager());
		
		//Gdx.app.log("Game", "created");
	}
	
	public void reset(){
		_player.reset();
		_turnHandler.reset();
		if (_dialog != null) _dialog.clear();
		if (_stage != null) _stage.clear();
		_state = State.Loading;
		_message = "";
	}
	
	@Override
	public void show(){
		if (!_assetHelper.isLoaded()) _assetHelper.load();
		if (_inputs != null) {
			Gdx.input.setInputProcessor(_inputs);
		} else Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
	}
	
	@Override
	public void hide(){
		//unload();
	}
	
	public void unload(){
		_stages.dispose();
		if (_assetHelper.isLoaded()) _assetHelper.unload();
	}
	
	@Override
	public void handleBackPress(){
		_parent.setScreen(_parent.menuScene);
	}
	
	public void changeState(State state){
		_state = state;
		_message = "";
	}
	
	public void setMessage(String message){
		_message = message;
	}
	
	public void onStart(){
		_state = State.CollectItem;
		_stage.clear();
		_message = "";
	}
	
	public void onDialogOK(){
		// may need to alter
		_stage.clear();
		_turnHandler.iterate();
	}
	
	public void onMenu(){ // possible redundancy
		_parent.setScreen(_parent.menuScene);
	}
	
	public void onAttack(String target){
		_stage.clear();
		_turnHandler.localAttack(target);
	}
	
	public void onDefend(Player.dfType defence){
		_stage.clear();
		_turnHandler.localDefence(defence);
	}
	
	public void onGameOver(){
		// Game Over - show score board
		_message = "Game Over!";
		_state = State.GameOver;
		_dialog = _stages.getScoreBoard(_turnHandler.getScoreBoard());
		_dialog.show(_stage);
	}
	
	public boolean hasStarted(){
		return (_state != State.Loading && _state != State.ArrangeItems 
					&& _state != State.ItemSwap);
	}
	
	public boolean canContinue(){
		return (hasStarted() && _state != State.GameOver);
	}
	
	public void showChooseNext(){
		if (_turnHandler.getQueue().getRound()<48){
			_state = State.ChooseNext;
			_message = "Choose next square...";
		// prevent problems when Choose is the last item
		} else _turnHandler.iterate();
	}
	
	public void showAttackDialog(ArrayList<String> opponents, Grid.sqType type){
		_state  = State.ShowPopup;
		_dialog = _stages.getAttackDialog(opponents, type);
		_dialog.show(_stage);
	}
	
	public void showDefendDialog(String attacker, Grid.sqType type, ArrayList<Player.dfType> defences){
		_state = State.ShowPopup;
		_dialog = _stages.getDefendDialog(attacker, type, defences);
		_dialog.show(_stage);
	}
	
	public void showNotification(String message){
		_state = State.ShowPopup;
		_dialog = _stages.getDialog(message);
		_dialog.show(_stage);
	}
	
	public Player getPlayer(){
		return _player;
	}
	
	public Viewport getViewport(){
		return _parent.getViewport();
	}
	
	public void update(float delta){
		
		// LOADING STATE
		if (_state == State.Loading) {
			// If loading finished
			if (_parent.getAssetManager().update()) {
				// Assign Resources
				_assetHelper.assignResources();
				// Set Stage
				_stage = _stages.getArrangeStage();
				// Set Inputs
				_inputs.addProcessor(this);
				_inputs.addProcessor(_stage);
				Gdx.input.setInputProcessor(_inputs);
				// Change to Arrange State
				_state = State.ArrangeItems;
				_message = "Drag items to rearrange...";
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
		//Gdx.app.log("Game", "updated");
	}
	
	@Override
	public void render(float delta){
		update(delta);
		
		// LOADING STATE
		if (_state == State.Loading){
			// display 'loading' message
			_assetHelper.font.draw(batch, "Loading...", (PirateGame.VIRTUAL_WIDTH-420) / 2, 
					(PirateGame.VIRTUAL_HEIGHT-42) / 2);
			return;
		}
		
		// Background image
		if (_assetHelper.imgBoard != null) batch.draw(_assetHelper.imgBoard, 0, 0);
		
		// Draw Grid
		drawGrid();
		
		// After Start - UI Elements
		if (hasStarted()){
			
			// Points
			batch.draw(_assetHelper.pointsBG, 435, 1165);
			_assetHelper.font.draw(batch, ""+_player.getPoints(), 495, 1225);
			
			// Bank
			_assetHelper.font.draw(batch, "Bank", 405, 350);
			batch.draw(_assetHelper.pointsBG, 395, 210);
			_assetHelper.font.draw(batch, ""+_player.getBank(), 460, 270);
			
			// Inventory
			_assetHelper.font.draw(batch, "Inventory", 75, 350);
			batch.draw(_assetHelper.inventoryBG, 65, 200);
			if (_player.hasShield()){
				batch.draw(_assetHelper.imgShield, 90, 208);
				if (_player.hasMirror()) batch.draw(_assetHelper.imgMirror, 190, 208);
			} else if (_player.hasMirror()) batch.draw(_assetHelper.imgMirror, 90, 208);
		}
		
		// COLLECT ITEM STATE
		if (_state == State.CollectItem){
			// show item selector
			_sqCoord = getCoords(_turnHandler.getCurrentSquare());
			batch.draw(_assetHelper.selector, _sqCoord.x, _sqCoord.y);
		} // END COLLECT
		
		// Message Bar
		_assetHelper.font.draw(batch, _message, 20, 65);
		
		// Draw Stage (buttons, pop-ups, etc.)
		batch.end(); // grid doesn't draw without this
		if (_stage != null) _stage.draw();
		batch.begin();
		//Gdx.app.log("Game", "rendered");
	}
	
	public void drawGrid(){
		TextureRegion img = null;
		float imgX, imgY;
		
		// Draw grid items
		for(int i=0; i<49; i++){
			img = _assetHelper.getItemImage(_player.getType(i));
			
			if (img != null){
				// if the item isn't being moved
				if (!((_state == State.ArrangeItems && i == _selectedSquare) ||
						(_state == State.ItemSwap && i == _destSquare) )){
					// Item position
					_sqCoord = getCoords(i);
					// Draw
					batch.draw(img, _sqCoord.x, _sqCoord.y);
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
		} else if (_state == State.ItemSwap && _destSquare != -1){
			// animate swapped item
			img = _assetHelper.getItemImage(_player.getType(_destSquare));
			_sqCoord = animateSwap(_selectedSquare, _destSquare, _animTime, _animTotal);
			batch.draw(img, _sqCoord.x, _sqCoord.y);
		}
		img = null;
		//Gdx.app.log("Game", "Grid Drawn");
	}

	public int getGridIndex(int x, int y){
		// get grid index for coords (x,y)
		if (x>=gridOrigin.x && y <= gridOrigin.y){ // to avoid rounding issues 
			int gridX = (int)(x-gridOrigin.x)/itemSize;
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
	
	
	// TOUCH HANDLING //
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (pointer == 0){ // Left mouse button clicked
	        _mousePos.x = screenX;
	        _mousePos.y = screenY;
	        _parent.unproject(_mousePos);
	        
	        int index = getGridIndex((int)_mousePos.x, (int)_mousePos.y);
	        //Gdx.app.log("Game", ""+index);
	        
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
 			
			// COLLECT ITEM STATE
			if (_state == State.CollectItem){ 
				if (index == _turnHandler.getCurrentSquare()){
					_turnHandler.localCollect();
				}
				return true;
			} // END COLLECT ITEM
			
			// CHOOSE NEXT STATE
			if (_state == State.ChooseNext){
				if (index != -1 && !_player.getGrid().isEmpty(index)){
					_turnHandler.getQueue().addNext(index);
					_message = "";
					_turnHandler.iterate();
				}
				return true;
			} // END CHOOSE NEXT
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
	        		} else{
		        		_destSquare = _selectedSquare;
		        		_selectedSquare = index;
		        		_state = State.ItemSwap;
	        		}
	        	} else{
	        		// Else return to original square
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

package com.cavillum.pirategame.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cavillum.pirategame.PirateGame;
import com.cavillum.pirategame.data.Score;
import com.cavillum.pirategame.objects.Grid;
import com.cavillum.pirategame.objects.Player;

public class GameStages {
	// TODO - pretty stuff up
	
	private GameScene _scene;
	private Skin _skin;
	private Stage _arrangeStage;
	private Table _arrangeTable;
	
	private Dialog _dialog;
	private Dialog _scoreBoard;
	private Dialog _attackDialog;
	private Dialog _defendDialog;
	
	private MessageBuilder _messages;
	
	private boolean _arrangeBuilt = false;
	
	public GameStages(GameScene scene){
		_scene = scene;
		_arrangeStage = new Stage(_scene.getViewport());
		_skin = new Skin(Gdx.files.internal("uiskin.json"));
		_messages = new MessageBuilder();
	}
	
	public void dispose(){
		_arrangeStage.dispose();
		_skin.dispose();
		_arrangeBuilt = false;
	}
	
	
	// ARRANGE STAGE //
	
	public void buildArrangeStage(){
		_arrangeTable = new Table();
		TextButton _shuffleButton = new TextButton("Shuffle", _skin);
		TextButton _startButton = new TextButton("Start", _skin);
		_arrangeTable.add(_shuffleButton).width(250).height(80).pad(10);
		_arrangeTable.add(_startButton).width(250).height(80).pad(10);
		_arrangeTable.setPosition(0, 0);
		_arrangeTable.setSize(PirateGame.VIRTUAL_WIDTH, 400);
		
		_arrangeStage.addActor(_arrangeTable);
		
		_shuffleButton.addListener(new ClickListener(){
			@Override public void clicked(InputEvent event, float x, float y) {
				_scene.getPlayer().getGrid().shuffle();
				//Gdx.app.log("Stages", "Shuffle clicked");
			}
		});
		_startButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				_scene.onStart();
			}
		});
	}
	
	public Stage getArrangeStage(){
		if (!_arrangeBuilt) buildArrangeStage();
		return _arrangeStage;
	}
	
	
	// DIALOG //
	
	public void buildDialog(String message){		
		// create label
		Label _label = new Label(message, _skin);
		_label.setWrap(true);
		
		// create table
		Table _dialogTable = new Table(_skin);
		_dialogTable.defaults().width(300);
		_dialogTable.add(_label).pad(10);
		
		// create dialog
		_dialog = new Dialog("", _skin){
			protected void result(Object object){
				//Gdx.app.log("Dialog", "peek");
				_scene.onDialogOK();
			}
		}.button("OK");
		_dialog.getContentTable().add(_dialogTable).pad(10);
		_dialog.getContentTable().pad(10);
		_dialog.setWidth(400);
		_dialog.pad(10);
	}
	
	public Dialog getDialog(String message){
		if (_dialog != null) _dialog.clear();
		buildDialog(message);
		return _dialog;
	}
	
	
	// SCORE BOARD //
	
	public void buildScoreBoard(List<Score> scores){
		
		// build score table
		// replace with ScrollPane?
		Table _scoreTable = new Table(_skin);
		for (int i=0; i<scores.size(); i++){
			_scoreTable.add(scores.get(i).getName()).pad(10);
			_scoreTable.add(""+scores.get(i).getScore()).pad(10);
			_scoreTable.row();
		}
		
		// build score board dialog 
		_scoreBoard = new Dialog("Score Board", _skin){
			protected void result(Object object){
				if (object == "menu"){
					_scene.onMenu();
				} else if (object == "restart"){
					_scene.reset();
				}
				//Gdx.app.log("Dialog", "score board");
			}
		};
		_scoreTable.setFillParent(true);
		_scoreBoard.getContentTable().add(_scoreTable);
		
		// add buttons
		TextButton _dButton = new TextButton("Menu", _skin);
		_scoreBoard.button(_dButton, "menu");
		_dButton = new TextButton("New Game", _skin);
		_scoreBoard.button(_dButton, "restart");
		_scoreBoard.setSize(400, 300);
		_scoreBoard.pad(10);
	}
	
	public Dialog getScoreBoard(List<Score> scores){
		if (_scoreBoard != null) _scoreBoard.clear();
		buildScoreBoard(scores);
		return _scoreBoard;
	}
	
	
	// ATTACK DIALOG //
	
	public void buildAttackDialog(List<String> players, final Grid.sqType type){
		// create message label
		String message = _messages.buildAttackQuestion(type);
		Label _label = new Label(message, _skin);
		_label.setWrap(true);
		
		// create table
		Table _playerTable = new Table(_skin);
		_playerTable.defaults().width(300);
		_playerTable.add(_label).row();
		
		// create buttons
		for (String p : players){
			final String pTemp = p;
			TextButton _pButton = new TextButton(pTemp, _skin);
			_pButton.addListener(new ClickListener(){
				@Override public void clicked(InputEvent event, float x, float y) {
					_scene.onAttack(pTemp);
					//Gdx.app.log("Attack", type+" "+pTemp.getID());
				}
			});
			_playerTable.add(_pButton).pad(10).expandX();
			_playerTable.row();
		}
		
		// create scrollpane
		ScrollPane _playerList = new ScrollPane(_playerTable, _skin);
		
		// create dialog
		_attackDialog = new Dialog("Attack", _skin){
			protected void result(Object object){
				Gdx.app.log("Dialog", "Attack");
			}
		};
		_attackDialog.getContentTable().add(_playerList);
		_attackDialog.setSize(350, 200);
		_attackDialog.pad(10);
	}
	
	public Dialog getAttackDialog(List<String> players, Grid.sqType type){
		if (_attackDialog != null) _attackDialog.clear();
		buildAttackDialog(players, type);
		return _attackDialog;
	}
	
	
	// DEFENCE DIALOG //
	
	public void buildDefendDialog(String attacker, Grid.sqType type,
			ArrayList<Player.dfType> defences){
		// create message text
		String message = _messages.buildDefenceMessage(attacker, type);
		Label _label = new Label(message, _skin);
		_label.setWrap(true);
		
		// create table
		Table _defendTable = new Table(_skin);
		_defendTable.defaults().width(300);
		_defendTable.add(_label);
		_defendTable.row();
		
		// create buttons
		if (defences.indexOf(Player.dfType.dfShield) != -1){
			TextButton _dButton = new TextButton("Shield", _skin);
			_dButton.addListener(new ClickListener(){
				@Override public void clicked(InputEvent event, float x, float y) {
					_scene.onDefend(Player.dfType.dfShield);
					//Gdx.app.log("defend", "shield");
				}
			});
			_defendTable.add(_dButton).pad(10);
			_defendTable.row();
		}
		if (defences.indexOf(Player.dfType.dfMirror) != -1){
			TextButton _dButton = new TextButton("Mirror", _skin);
			_dButton.addListener(new ClickListener(){
				@Override public void clicked(InputEvent event, float x, float y) {
					_scene.onDefend(Player.dfType.dfMirror);
					//Gdx.app.log("defend", "mirror");
				}
			});
			_defendTable.add(_dButton).pad(10);
			_defendTable.row();
		}
		// Pass Button (if player doesn't want to use defence)
		TextButton _dButton = new TextButton("Pass", _skin);
		_dButton.addListener(new ClickListener(){
			@Override public void clicked(InputEvent event, float x, float y) {
				_scene.onDefend(Player.dfType.dfNone);
				//Gdx.app.log("defend", "pass");
			}
		});
		_defendTable.add(_dButton).pad(10);
		_defendTable.row();
		
		// create dialog
		_defendDialog = new Dialog("defend", _skin);
		_defendDialog.getContentTable().add(_defendTable);
		_defendDialog.setSize(350, 300);
		_defendDialog.pad(10);
	}
	
	public Dialog getDefendDialog(String attacker, Grid.sqType type, ArrayList<Player.dfType> defences){
		if (_defendDialog != null) _defendDialog.clear();
		buildDefendDialog(attacker, type, defences);
		return _defendDialog;
	}

}

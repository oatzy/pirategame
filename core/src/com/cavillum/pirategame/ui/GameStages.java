package com.cavillum.pirategame.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.cavillum.pirategame.PirateGame;
import com.cavillum.pirategame.data.Score;
import com.cavillum.pirategame.helpers.HelpBuilder;
import com.cavillum.pirategame.objects.Grid;
import com.cavillum.pirategame.objects.Player;
import com.cavillum.pirategame.ui.GameScene.State;

public class GameStages {
	// TODO - pretty stuff up
	
	public enum Popup {Notification, ScoreBoard, Attack, Defend,
							History, Players, Help, Quit, None}
	
	private GameScene _scene;
	private Skin _skin;
	private Stage _mainStage;
	private Table _arrangeTable;
	private ArrayList<Popup> _currentPops;
	
	// Dialogs
	private Dialog _dialog;
	private Dialog _scoreBoard;
	private Dialog _attackDialog;
	private Dialog _defendDialog;
	private Dialog _historyDialog;
	private Dialog _playerDialog;
	private Dialog _helpDialog;
	private Dialog _quitDialog;
	
	// Tables
	
	private boolean _arrangeBuilt = false;
	
	// Create
	public GameStages(GameScene scene){
		_scene = scene;
		_mainStage = new Stage(_scene.getViewport());
		_skin = new Skin(Gdx.files.internal("uiskin.json"));
		_currentPops = new ArrayList<Popup>();
		
	}
	
	public Stage getStage(){
		return _mainStage;
	}
	
	public void dispose(){
		_skin.dispose();
		_arrangeBuilt = false;
	}
	
	
	// Popup handling
	
	public ArrayList<Popup> getShownPopups(){
		return _currentPops;
	}
	
	public int popupCount(){
		return _currentPops.size();
	}
	
	public boolean isShown(Popup pop){
		return _currentPops.contains(pop);
	}
	
	public Popup getTopPopup(){
		if (_currentPops.size()>0)
			return _currentPops.get(_currentPops.size()-1);
		return Popup.None;
	}
	
	public void showSideBar(){
		if (PirateGame.layout.hasSideBar())
			//_mainStage.clear();
			_mainStage.addActor(PirateGame.layout.getSideBar().getTable());
	}
	
	
	// ARRANGE STAGE //
	
	public void buildArrangeStage(){
		
		_arrangeTable = new Table();
		
		TextButton _shuffleButton = new TextButton("Shuffle", _skin);
		TextButton _startButton = new TextButton("Start", _skin);
		
		if (PirateGame.layout.isTablet()){
			_arrangeTable.setPosition(0, 0);
			_arrangeTable.setSize(PirateGame.layout.origin.x-20, PirateGame.layout.height);
			
			_startButton.getLabel().setFontScale(1.5f);
			
			_arrangeTable.add(_startButton).width(250).height(100).pad(10).row();
			_arrangeTable.add(_shuffleButton).width(250).height(100).pad(10).padTop(50);
		}
		else {
			_arrangeTable.setPosition(0, 0);
			_arrangeTable.setSize(PirateGame.layout.width, 500);
			
			_arrangeTable.add(_shuffleButton).width(250).height(80).pad(10);
			_arrangeTable.add(_startButton).width(250).height(80).pad(10);
		}
		
		_shuffleButton.addListener(new ClickListener(){
			@Override public void clicked(InputEvent event, float x, float y) {
				_scene.getPlayer().getGrid().shuffle();
			}
		});
		
		_startButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				_scene.onStart();
				_mainStage.clear();
				showSideBar();
			}
		});
		
	}
	
	public void showArrangeStage(){
		if (!_arrangeBuilt) buildArrangeStage();
		_mainStage.clear();
		_mainStage.addActor(_arrangeTable);
	}
	
	
	// DIALOG //
	
	public void buildDialog(String message){		
		// create label
		Label _label = new Label(message, _skin);
		_label.setWrap(true);
		_label.setAlignment(Align.center);
		
		// create table
		Table _dialogTable = new Table(_skin);
		_dialogTable.defaults().width(400).height(150);
		_dialogTable.add(_label).pad(10);
		_dialogTable.setBackground(_skin.getDrawable("pane"));
		
		// create dialog
		_dialog = new Dialog("Notification", _skin){
			protected void result(Object object){
				_dialog.hide();
				_currentPops.remove(Popup.Notification);
				_scene.onDialogOK();
			}
		}.button("Close");
		
		_dialog.getButtonTable().defaults().width(150).pad(0);
		_dialog.getButtonTable().pad(0, 10, 10, 10);
		
		_dialog.getContentTable().add(_dialogTable).pad(50, 10, 0, 10);
		//_dialog.setSize(400, 300);
		_dialog.setWidth(400);
		_dialog.pad(10);
		
		// title
		_dialog.getTitleLabel().setAlignment(Align.center);
		_dialog.getTitleTable().setHeight(50);
		_dialog.getTitleTable().align(Align.top).pad(10);

	}
	
	public void showDialog(String message){
		if (_dialog != null) {
			// To avoid ghost notifs
			_mainStage.clear();
			showSideBar();
			_dialog.clear();
		}
		buildDialog(message);
		_currentPops.add(Popup.Notification);
		popupDialog(_dialog);
	}
	
	
	// SCORE BOARD //
	
	public void buildScoreBoard(List<Score> scores){
		
		// build score table
		// replace with ScrollPane?
		Table _scoreTable = new Table(_skin);
		_scoreTable.defaults().width(200);
		_scoreTable.setBackground(_skin.getDrawable("pane"));
		
		for (int i=0; i<scores.size(); i++){
			Label nLabel = new Label(scores.get(i).getName(), _skin);
			Label sLabel = new Label(""+scores.get(i).getScore(), _skin);
			nLabel.setAlignment(Align.center);
			sLabel.setAlignment(Align.center);
			if (scores.get(i).getName() == "You"){
				nLabel.setFontScale(1.5f);
				sLabel.setFontScale(1.5f);
			}
			_scoreTable.add(nLabel).pad(10);
			_scoreTable.add(sLabel).pad(10);
			_scoreTable.row();
		}
		
		// build score board dialog 
		_scoreBoard = new Dialog("Score Board", _skin){
			protected void result(Object object){
				_scoreBoard.hide();
				_currentPops.remove(Popup.ScoreBoard);
				if (object == "menu"){
					_scene.onMenu();
				} else if (object == "restart"){
					_scene.reset();
					if (PirateGame.adsEnabled) PirateGame.googleServices.showAds(true);
				}
			}
		};
		_scoreBoard.getContentTable().add(_scoreTable).pad(50, 10, 0, 10);
		_scoreBoard.getButtonTable().pad(0, 10, 10, 10);
		
		// add buttons
		TextButton _dButton = new TextButton("Menu", _skin);
		_scoreBoard.button(_dButton, "menu");
		_dButton = new TextButton("Replay", _skin);
		_scoreBoard.button(_dButton, "restart");
		//_scoreBoard.setSize(400, 300);
		_scoreBoard.setWidth(400);
		_scoreBoard.pad(10);
		
		// title
		_scoreBoard.getTitleLabel().setAlignment(Align.center);
		_scoreBoard.getTitleTable().setHeight(50);
		_scoreBoard.getTitleTable().align(Align.top).pad(10);
	}
	
	public void showScoreBoard(List<Score> scores){
		if (_scoreBoard != null) _scoreBoard.clear();
		buildScoreBoard(scores);
		_currentPops.add(Popup.ScoreBoard);
		popupDialog(_scoreBoard);
	}
	
	
	// ATTACK DIALOG //
	
	public void buildAttackDialog(List<String> players, final Grid.sqType type){
			
		// create message label
		Table _labelTable = new Table(_skin);
		_labelTable.defaults().width(400).height(130);
		_labelTable.setBackground(_skin.getDrawable("pane"));
		String message = MessageBuilder.buildAttackQuestion(type);
		Label _label = new Label(message, _skin);
		_label.setWrap(true);
		_label.setAlignment(Align.center);
		_labelTable.add(_label);
		
		// create table
		Table _playerTable = new Table(_skin);
		_playerTable.defaults().width(350);
		
		// create buttons
		for (String p : players){
			final String pTemp = p;
			TextButton _pButton = new TextButton(pTemp, _skin);
			_pButton.addListener(new ClickListener(){
				@Override public void clicked(InputEvent event, float x, float y) {
					_attackDialog.hide();
					_currentPops.remove(Popup.Attack);
					_scene.onAttack(pTemp);
				}
			});
			_playerTable.add(_pButton).padBottom(10).expandX();
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
		
		_attackDialog.getContentTable().add(_labelTable).pad(50, 10, 0, 10).row();
		_attackDialog.getContentTable().add(_playerList)
			.expandX().maxHeight(350);
		_attackDialog.setWidth(400);
		_attackDialog.pad(10);
		
		// title
		_attackDialog.getTitleLabel().setAlignment(Align.center);
		_attackDialog.getTitleTable().setHeight(50);
		_attackDialog.getTitleTable().align(Align.top).pad(10);
	}
	
	public void showAttackDialog(List<String> players, Grid.sqType type){
		if (_attackDialog != null) _attackDialog.clear();
		buildAttackDialog(players, type);
		_currentPops.add(Popup.Attack);
		popupDialog(_attackDialog);
	}
	
	
	// DEFENCE DIALOG //
	
	public void buildDefendDialog(String attacker, Grid.sqType type,
			ArrayList<Player.dfType> defences){
		
		// create table
		Table _defendTable = new Table(_skin);
		_defendTable.defaults().width(350);
		
		// create message text
		Table _labelTable = new Table(_skin);
		_labelTable.defaults().width(400).height(130);
		_labelTable.setBackground(_skin.getDrawable("pane"));
		String message = MessageBuilder.buildDefenceMessage(attacker, type);
		Label _label = new Label(message, _skin);
		_label.setWrap(true);
		_label.setAlignment(Align.center);
		_labelTable.add(_label);
		
		// create buttons
		
		// shield button
		if (defences.indexOf(Player.dfType.dfShield) != -1){
			TextButton _dButton = new TextButton("Shield", _skin);
			_dButton.addListener(new ClickListener(){
				@Override public void clicked(InputEvent event, float x, float y) {
					_defendDialog.hide();
					_scene.onDefend(Player.dfType.dfShield);
				}
			});
			_defendTable.add(_dButton).padBottom(10).row();
		}
		
		// mirror button
		if (defences.indexOf(Player.dfType.dfMirror) != -1){
			TextButton _dButton = new TextButton("Mirror", _skin);
			_dButton.addListener(new ClickListener(){
				@Override public void clicked(InputEvent event, float x, float y) {
					_defendDialog.hide();
					_scene.onDefend(Player.dfType.dfMirror);
				}
			});
			_defendTable.add(_dButton).padBottom(10).row();
		}
		
		// Pass Button (if player doesn't want to use defence)
		TextButton _dButton = new TextButton("Pass", _skin);
		_dButton.addListener(new ClickListener(){
			@Override public void clicked(InputEvent event, float x, float y) {
				_defendDialog.hide();
				_currentPops.remove(Popup.Defend);
				_scene.onDefend(Player.dfType.dfNone);
			}
		});
		_defendTable.add(_dButton).padBottom(10).row();
		
		// create dialog
		_defendDialog = new Dialog("Defend", _skin);
		_defendDialog.getContentTable().add(_labelTable).pad(50, 10, 0, 10).row();
		_defendDialog.getContentTable().add(_defendTable);//.pad(50, 10, 10, 10);
		//_defendDialog.setSize(400, 300);
		_defendDialog.setWidth(400);
		_defendDialog.pad(10);
		
		// title
		_defendDialog.getTitleLabel().setAlignment(Align.center);
		_defendDialog.getTitleTable().setHeight(50);
		_defendDialog.getTitleTable().align(Align.top).pad(10);
		
	}
	
	public void showDefendDialog(String attacker, Grid.sqType type, ArrayList<Player.dfType> defences){
		if (_defendDialog != null) _defendDialog.clear();
		buildDefendDialog(attacker, type, defences);
		_currentPops.add(Popup.Defend);
		popupDialog(_defendDialog);
	}
	
	
	// History Dialog
	
	public void buildHistoryDialog(ArrayList<String> history, final State state){
		
		// create table
		Table _historyTable = new Table(_skin);
		_historyTable.defaults().width(500);
		
		// create labels
		Label _label;
		if (history.size() == 0) {
			_label = new Label("Nothing to see...", _skin);
			_label.setWrap(true);
			_label.setAlignment(Align.center);
			_historyTable.add(_label).pad(10).row();
		}
		else for (int i=history.size()-1; i>=0; i--){
			_label = new Label(history.get(i), _skin);
			_label.setWrap(true);
			_label.setAlignment(Align.center);
			_historyTable.add(_label).pad(15).row();
		}
		
		_historyTable.setBackground(_skin.getDrawable("pane"));
		
		// create dialog
		_historyDialog = new Dialog("History", _skin){
			protected void result(Object object){
				_currentPops.remove(Popup.History);
				_historyDialog.hide();
				_scene.changeState(state);
			}
		}.button("Close");
		
		ScrollPane _historyScroll = new ScrollPane(_historyTable, _skin);
		
		_historyDialog.getContentTable().add(_historyScroll)
			.expandX().maxHeight(300).minHeight(200).pad(50, 10, 0, 10);
		//_historyDialog.setSize(500, 300);
		_historyDialog.setWidth(500);
		_historyDialog.pad(10);
		_historyDialog.getButtonTable().pad(0, 10, 10, 10);
		
		// title
		_historyDialog.getTitleLabel().setAlignment(Align.center);
		_historyDialog.getTitleTable().setHeight(50);
		_historyDialog.getTitleTable().align(Align.top).pad(10);
	}
	
	public void showHistoryDialog(ArrayList<String> history, State state){
		if (_historyDialog != null) _historyDialog.clear();
		buildHistoryDialog(history, state);
		_currentPops.add(Popup.History);
		popupDialog(_historyDialog);
	}
	
	
	// Player Dialog
	
	public void buildPlayerDialog(List<String> players, final State state){
		// TODO - hitlist building
		
		// create table
		Table _playerTable = new Table(_skin);
		_playerTable.defaults().width(400);
		_playerTable.setBackground(_skin.getDrawable("pane"));
		
		// create list
		for (String p : players){
			Label _pLabel = new Label(p, _skin);
			_pLabel.setAlignment(Align.center);
			_playerTable.add(_pLabel).pad(10);//.expandX();
			_playerTable.row();
		}
		
		// create scrollpane
		ScrollPane _playerList = new ScrollPane(_playerTable, _skin);
		
		// create dialog
		_playerDialog = new Dialog("Players", _skin){
			protected void result(Object object){
				_currentPops.remove(Popup.Players);
				_playerDialog.hide();
				_scene.changeState(state);
			}
		}.button("Close");
		
		_playerDialog.getContentTable().add(_playerList)
			.expandX().maxHeight(350).minHeight(200).pad(50, 10, 0, 10);
		_playerDialog.getButtonTable().pad(0, 10, 10, 10);
		//_playerDialog.setSize(400, 300);
		_playerDialog.setWidth(400);
		_playerDialog.pad(10);
		
		// title
		_playerDialog.getTitleLabel().setAlignment(Align.center);
		_playerDialog.getTitleTable().setHeight(50);
		_playerDialog.getTitleTable().align(Align.top).pad(10);
	}
	
	public void buildPlayerDialog(List<String> alive, List<String> dead, final State state){
		
		// create table
		Table _playerTable = new Table(_skin);
		_playerTable.defaults().width(400);
		_playerTable.setBackground(_skin.getDrawable("pane"));
		
		Label _title = new Label("> Alive:", _skin);
		_title.setFontScale(1.2f);
		_title.setAlignment(Align.center);
		_playerTable.add(_title).pad(10).padBottom(10).row();
		
		// create list
		for (String p : alive){
			Label _pLabel = new Label(p, _skin);
			_pLabel.setAlignment(Align.center);
			_playerTable.add(_pLabel).pad(10);//.expandX();
			_playerTable.row();
		}
		
		_title = new Label("> Dead:", _skin);
		_title.setFontScale(1.2f);
		_title.setAlignment(Align.center);
		_playerTable.add(_title).pad(10).padTop(20).padBottom(10).row();
		
		// create list
		for (String p : dead){
			Label _pLabel = new Label(p, _skin);
			_pLabel.setAlignment(Align.left);
			_playerTable.add(_pLabel).pad(10);//.expandX();
			_playerTable.row();
		}
		
		// create scrollpane
		ScrollPane _playerList = new ScrollPane(_playerTable, _skin);
		
		// create dialog
		_playerDialog = new Dialog("Players", _skin){
			protected void result(Object object){
				_currentPops.remove(Popup.Players);
				_playerDialog.hide();
				_scene.changeState(state);
			}
		}.button("Close");
		
		_playerDialog.getContentTable().add(_playerList)
			.expandX().maxHeight(350).minHeight(200).pad(50, 10, 0, 10);
		_playerDialog.getButtonTable().pad(0, 10, 10, 10);
		//_playerDialog.setSize(400, 300);
		_playerDialog.setWidth(400);
		_playerDialog.pad(10);
		
		// title
		_playerDialog.getTitleLabel().setAlignment(Align.center);
		_playerDialog.getTitleTable().setHeight(50);
		_playerDialog.getTitleTable().align(Align.top).pad(10);
	}
	
	public void showPlayerDialog(List<String> players, final State state){
		if (_playerDialog != null) _playerDialog.clear();
		buildPlayerDialog(players, state);
		_currentPops.add(Popup.Players);
		popupDialog(_playerDialog);
	}
	
	public void showPlayerDialog(List<String> alive, List<String> dead, final State state){
		if (_playerDialog != null) _playerDialog.clear();
		buildPlayerDialog(alive, dead, state);
		_currentPops.add(Popup.Players);
		popupDialog(_playerDialog);
	}
	
	
	// Help Dialog
	public void buildHelpDialog(final State state){
		// create table
		Table _helpTable = new Table(_skin);
		_helpTable.defaults().width(500);
		
		// create labels
		Label _label;
		String text;
		
		if (state == State.CollectItem){
			text = HelpBuilder.buildItemHelp(_scene.getCurrent());
			_label = new Label(text, _skin);
			_label.setWrap(true);
			_label.setAlignment(Align.center);
			_helpTable.add(_label).pad(10).padBottom(10).row();
		}
		
		if (state == State.ShowPopup){
			text = HelpBuilder.buildPopupHelp(getTopPopup());
			_label = new Label(text, _skin);
			_label.setWrap(true);
			_label.setAlignment(Align.center);
			_helpTable.add(_label).pad(10).padBottom(10).row();
		}
		
		text = HelpBuilder.buildStateHelp(state);
		if (text!=""){
			_label = new Label(text, _skin);
			_label.setWrap(true);
			_label.setAlignment(Align.center);
			_helpTable.add(_label).pad(10).padBottom(10).row();
		}
		
		_helpTable.setBackground(_skin.getDrawable("pane"));
		
		// create dialog
		_helpDialog = new Dialog("Help", _skin){
			protected void result(Object object){
				_currentPops.remove(Popup.Help);
				_helpDialog.hide();
				_scene.changeState(state);
			}
		}.button("Close");
		
		ScrollPane _helpScroll = new ScrollPane(_helpTable, _skin);
		
		_helpDialog.getContentTable().add(_helpScroll)
			.expandX().maxHeight(400).minHeight(200).pad(50, 10, 0, 10);
		_helpDialog.setWidth(500);
		_helpDialog.pad(10);
		_helpDialog.getButtonTable().pad(0, 10, 10, 10);
		
		// title
		_helpDialog.getTitleLabel().setAlignment(Align.center);
		_helpDialog.getTitleTable().setHeight(50);
		_helpDialog.getTitleTable().align(Align.top).pad(10);
	}
	
	public void showHelpDialog(State state){
		if (_helpDialog != null) _helpDialog.clear();
		buildHelpDialog(state);
		_currentPops.add(Popup.Help);
		popupDialog(_helpDialog);
	}
	
	
	// Quit Dialog
	
	public void buildQuitDialog(final State state){
		
		// create label
		Label _label = new Label("Are you sure you want to quit?", _skin);
		_label.setWrap(true);
		_label.setAlignment(Align.center);
		
		// create table
		Table _quitTable = new Table(_skin);
		_quitTable.defaults().width(400).height(150);
		_quitTable.add(_label).pad(10);
		_quitTable.setBackground(_skin.getDrawable("pane"));
		
		// create dialog
		_quitDialog = new Dialog("Quit", _skin){
			protected void result(Object object){
				_currentPops.remove(Popup.Quit);
				_quitDialog.hide();
				if (object == "yes") {
					// unlock quitter achievement
					PirateGame.save.incrementQuit();
					PirateGame.googleServices.unlockQuits();
					// go to menu
					_scene.onMenu();
				}
				else _scene.changeState(state);
			}
		};

		_quitDialog.getButtonTable().defaults().width(150);

		// add buttons
		TextButton _dButton = new TextButton("Yes", _skin);
		_quitDialog.button(_dButton, "yes");
		_dButton = new TextButton("No", _skin);
		_quitDialog.button(_dButton, "no");
		_quitDialog.getButtonTable().pad(0, 10, 10, 10);
		
		_quitDialog.getContentTable().add(_quitTable).pad(50, 10, 0, 10);
		_quitDialog.setWidth(400);
		_quitDialog.pad(10);
		
		// title
		_quitDialog.getTitleLabel().setAlignment(Align.center);
		_quitDialog.getTitleTable().setHeight(50);
		_quitDialog.getTitleTable().align(Align.top).pad(10);
	}
	
	public void showQuitDialog(State state){
		if (_quitDialog != null) _quitDialog.clear();
		_currentPops.add(Popup.Quit);
		buildQuitDialog(state);
		popupDialog(_quitDialog);
	}
	
	
	// Generic Message Popup //
	
	// Note - use carefully
	public void showMessagePopup(String message){
		// create label
		Label _label = new Label(message, _skin);
		_label.setWrap(true);
		_label.setAlignment(Align.center);

		// create table
		Table _dialogTable = new Table(_skin);
		_dialogTable.defaults().width(400).height(150);
		_dialogTable.add(_label).pad(10);
		_dialogTable.setBackground(_skin.getDrawable("pane"));

		// create dialog
		final Dialog mDialog = new Dialog("Alert", _skin){
			protected void result(Object object){
				this.hide();
			}
		}.button("Close");

		mDialog.getButtonTable().defaults().width(150).pad(0);
		mDialog.getButtonTable().pad(0, 10, 10, 10);

		mDialog.getContentTable().add(_dialogTable).pad(50, 10, 0, 10);
		mDialog.setWidth(400);
		mDialog.pad(10);

		// title
		mDialog.getTitleLabel().setAlignment(Align.center);
		mDialog.getTitleTable().setHeight(50);
		mDialog.getTitleTable().align(Align.top).pad(10);
		
		popupDialog(mDialog);
	}
	
	// TODO - set defaults function?
	
	
	// Pop-up Animation //
	
	public void popupDialog(Dialog dio){
		// Prepare for Animation
		dio.setOrigin(Align.center);
		dio.setPosition((PirateGame.layout.width)/2-50,
				(PirateGame.layout.height-dio.getPrefHeight())/2+150, Align.center);
		dio.setScale(0.75f,  0.75f);
		dio.setColor(1f, 1f, 1f, 0.5f);

		// Animate
		dio.show(_mainStage, Actions.parallel(
				Actions.scaleTo(1f, 1f, 0.3f, Interpolation.swingOut),
				Actions.fadeIn(0.2f, Interpolation.fade)));
	}
	
	
	// RENDER //
	
	public void render(){
		_mainStage.act();
		_mainStage.draw();
	}

}

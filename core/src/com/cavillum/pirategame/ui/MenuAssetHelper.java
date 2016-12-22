package com.cavillum.pirategame.ui;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.cavillum.pirategame.PirateGame;
import com.cavillum.pirategame.helpers.LevelBuilder;

public class MenuAssetHelper {
	// TODO - is there a better way of doing this without repeatedly rebuilding stages?
	// TODO - move players, score board (and achievements) to an options page.
	
	private PirateGame _parent;
	public AssetManager assetManager;
	
	private TextureAtlas _buttons;
	
	// Images
	public TextureRegion imgLogo;
	public TextureRegion menuBG;
	
	public TextureRegion imgGPlus;
	public TextureRegion scoreBG;
	public TextureRegion winBG;
	
	// Stage Objects
	private Stage _stage;
	private Skin _skin;
	
	// Tables
	private Table _splashTable;
	private Table _mainTable;
	private Table _playerTable;
	private Table _levelTable;
	private Table _scoreTable;
	
	// Main Stage Objects
	private Image _title;
	private TextButton _startButton;
	private TextButton _playButton;
	private TextButton _playerButton;
	private TextButton _scoreButton;
	private TextButton _achieveButton;
	private ImageButton _signInButton;
	private TextButton _multiButton; // make this an image?
	
	// Level buttons
	private TextButton _standardButton;
	private TextButton _buriedButton;
	private TextButton _chooseButton;
	private TextButton _knockoutButton;
	
	// Score Buttons
	private TextButton _standardScoreButton;
	private TextButton _buriedScoreButton;
	private TextButton _chooseScoreButton;
	private TextButton _knockoutScoreButton;
	
	// Player Edit Objects
	private TextButton _addButton;
	private TextButton _resetButton;
	
	
	// Other Globals
	private boolean _loaded=false;
	private String _editName = null;
	
	
	// Create
	public MenuAssetHelper(PirateGame game, AssetManager manager){
		_parent = game;
		assetManager = manager;
		
		_buttons = new TextureAtlas(Gdx.files.internal("menu-atlas"));
		_stage = new Stage(_parent.getViewport());
		_skin = new Skin(Gdx.files.internal("uiskin.json"));
		
		_splashTable = new Table();
		_mainTable = new Table();
		_playerTable = new Table();
		//_levelTable = new Table();
		
	}
	
	// Initialise Objects
	public void initialiseMainObjs(){

		// Title Image
		_title = new Image(imgLogo);
		//_title.setPosition(0, PirateGame.layout.height-_title.getHeight());
		_title.setPosition(PirateGame.layout.gutter, PirateGame.layout.height);
		_title.addAction(Actions.moveTo(PirateGame.layout.gutter, PirateGame.layout.height-_title.getHeight(),
				1f, Interpolation.swingOut));
		
		// Start Button
		_startButton = new TextButton("Start", _skin);
		_startButton.getLabel().setFontScale(2f);
		_startButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				_parent.menuScene.showMenu();
			}
		});
		
		// Play Button
		_playButton = new TextButton("New Game", _skin);
		_playButton.getLabel().setFontScale(2f);
		_playButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				//_parent.gameScene.reset();
				//_parent.setScreen(_parent.gameScene);
				_parent.menuScene.showLevels();
			}
		});
		
		// Player Edit Button
		_playerButton = new TextButton("Players", _skin);
		_playerButton.getLabel().setFontScale(1.5f);
		_playerButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				_parent.menuScene.onPlayerButton();
			}
		});
		
		// Score Board Button
		_scoreButton = new TextButton("Score Board", _skin);
		_scoreButton.getLabel().setFontScale(1.5f);
		_scoreButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				//PirateGame.googleServices.showScores();
				_parent.menuScene.showHighScores();
			}
		});
		
		// Achievement Button
		_achieveButton = new TextButton("Achievements", _skin);
		_achieveButton.getLabel().setFontScale(1.5f);
		_achieveButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				PirateGame.googleServices.showAchievements();
			}
		});
		
		// Sign In Button
		_signInButton = new ImageButton(new TextureRegionDrawable(imgGPlus));
		_signInButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				_parent.menuScene.onSignIn();
			}
		});
		
		// Standard level Button
		_standardButton = new TextButton("Play", _skin);
		_standardButton.getLabel().setFontScale(1.2f);
		_standardButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				PirateGame.levels.setGameType(LevelBuilder.GameType.Standard);
				_parent.gameScene.reset();
				_parent.setScreen(_parent.gameScene);
			}
		});
		
		// Buried level Button
		_buriedButton = new TextButton("Play", _skin);
		_buriedButton.getLabel().setFontScale(1.2f);
		_buriedButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				PirateGame.levels.setGameType(LevelBuilder.GameType.Buried);
				_parent.gameScene.reset();
				_parent.setScreen(_parent.gameScene);
			}
		});
		
		// Choose level Button
		_chooseButton = new TextButton("Play", _skin);
		_chooseButton.getLabel().setFontScale(1.2f);
		_chooseButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				PirateGame.levels.setGameType(LevelBuilder.GameType.Choose);
				_parent.gameScene.reset();
				_parent.setScreen(_parent.gameScene);
			}
		});
		
		// Knockout level Button
		_knockoutButton = new TextButton("Play", _skin);
		_knockoutButton.getLabel().setFontScale(1.2f);
		_knockoutButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				PirateGame.levels.setGameType(LevelBuilder.GameType.Knockout);
				_parent.gameScene.reset();
				_parent.setScreen(_parent.gameScene);
			}
		});
		
		_multiButton = new TextButton("Multiplayer", _skin);
		_multiButton.getLabel().setFontScale(1.2f);
		_multiButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				// TODO
			}
		});
		
		// Scoreboard Buttons
		
		_standardScoreButton = new TextButton("Scoreboard", _skin);
		_standardScoreButton.getLabel().setFontScale(1.2f);
		_standardScoreButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				PirateGame.googleServices.showStandardScores();
			}
		});
		
		_buriedScoreButton = new TextButton("Scoreboard", _skin);
		_buriedScoreButton.getLabel().setFontScale(1.2f);
		_buriedScoreButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				PirateGame.googleServices.showBuriedScores();
			}
		});
		
		_knockoutScoreButton = new TextButton("Scoreboard", _skin);
		_knockoutScoreButton.getLabel().setFontScale(1.2f);
		_knockoutScoreButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				PirateGame.googleServices.showKnockoutScores();
			}
		});
		
		_chooseScoreButton = new TextButton("Scoreboard", _skin);
		_chooseScoreButton.getLabel().setFontScale(1.2f);
		_chooseScoreButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				PirateGame.googleServices.showChooseScores();
			}
		});
		
	}
	
	public void initialisePlayerObjs(){
		// Add Button
		_addButton = new TextButton("+add player", _skin);
		_addButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				_parent.menuScene.onAddPlayer();
			}
		});
		
		// Reset Button
		_resetButton = new TextButton("reset", _skin);
		_resetButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				_parent.menuScene.onResetPlayers();
			}
		});
	}
	
	public void unlockSecretMenu(){
		// Levels Button
		_playButton = new TextButton("New Game", _skin);
		_playButton.getLabel().setFontScale(2f);
		_playButton.addListener(new ClickListener(){
			@Override  public void clicked(InputEvent event, float x, float y) {
				_parent.menuScene.showLevels();
			}
		});
	}
	
	
	// Splash Screen //
	
	public void setSplash(){
		_stage.clear();
		_splashTable.clear();
		
		if (PirateGame.layout.isTablet()){
			_splashTable.setSize(PirateGame.layout.width/2, PirateGame.layout.height);
			_splashTable.setPosition(PirateGame.layout.width/2, 0);
		}
		else{ 
			_splashTable.setSize(PirateGame.layout.width, PirateGame.layout.height-_title.getHeight());
			_splashTable.setPosition(0, 0);
		}
		_splashTable.add(_startButton).pad(20).width(250).row();
		
		// If Android
		if (PirateGame.isAndroid) {
			
			// If not signed in, show sign in button
			if (!PirateGame.googleServices.isSignedIn()) 
				_splashTable.add(_signInButton).pad(20).row();
			// TODO - add sign out button?

		}
		
		_stage.addActor(_title);
		_stage.addActor(_splashTable);
		
	}
	
	
	// Main Menu Screen //
	
	public void buildMainMenu(){
		_stage.clear();
		_mainTable.clear();
		
		// Buttons
		
		Table pbTable = new Table(_skin);
		pbTable.add(_playButton);
		pbTable.setSize(PirateGame.layout.width, PirateGame.layout.isTablet()?170:270);
		
		if (PirateGame.layout.isTablet())
			pbTable.setPosition(0, PirateGame.layout.height-410);
		else pbTable.setPosition(0, 575);
		
		_stage.addActor(pbTable);
		
		if (PirateGame.layout.isTablet())
			_mainTable.setSize(PirateGame.layout.width, PirateGame.layout.height-410);
		else _mainTable.setSize(PirateGame.layout.width, 575);
		_mainTable.setPosition(0, 0);
		
		_mainTable.add(_playerButton).pad(20).row();
		
		// If Android
		if (PirateGame.isAndroid) {
			
			// If signed in, show score/achievement buttons
			if (PirateGame.googleServices.isSignedIn()) {
				_mainTable.add(_scoreButton).pad(20).row();
				_mainTable.add(_achieveButton).pad(20).row();
			}

		}
		
		_stage.addActor(_mainTable);
		
	}
	
	
	// Level Select Screen
	
	public void buildLevelSelect(){
		
		if (_levelTable == null){
			_levelTable = new Table();
			
			Table _container = new Table();
			if (!PirateGame.layout.isTablet()){
				ScrollPane _scroll = new ScrollPane(_container, _skin);
				_levelTable.add(_scroll).maxHeight(PirateGame.layout.height);
			}
			else _levelTable.add(_container);
			
			Table _modeTable = new Table();
			Label _title;
			Label _description;
			
			int dwidth = (int) (PirateGame.layout.isTablet() ? 450 : PirateGame.layout.width*0.85);
			int algn = (PirateGame.layout.isTablet() ? Align.left : Align.center);
			
			_modeTable.defaults().width(dwidth);
			
			// Standard Game
			_title = new Label("Normal Game", _skin);
			_title.setFontScale(1.5f);
			_title.setAlignment(Align.center);
			_modeTable.add(_title).pad(10).row();
			_modeTable.add(_standardButton).size(250,80).pad(10).row();
			_description = new Label("The game picks the squares. Use what you get wisely.", _skin);
			_description.setWrap(true);
			_description.setAlignment(algn);
			_modeTable.add(_description).pad(10);
			
			_container.add(_modeTable).pad(30);
			if (!PirateGame.layout.isTablet()) _container.row();
			
			_modeTable = new Table();
			_modeTable.defaults().width(dwidth);
			
			// Free Choice Game
			_title = new Label("Free Choice", _skin);
			_title.setFontScale(1.5f);
			_title.setAlignment(Align.center);
			_modeTable.add(_title).pad(10).row();
			_modeTable.add(_chooseButton).size(250,80).pad(10).row();
			_description = new Label("You choose the squares. So choose carefully.", _skin);
			_description.setWrap(true);
			_description.setAlignment(algn);
			_modeTable.add(_description).pad(10);
			
			_container.add(_modeTable).pad(30);
			_container.row();
			
			_modeTable = new Table();
			_modeTable.defaults().width(dwidth);
			
			// Buried Game
			_title = new Label("Buried Treasure", _skin);
			_title.setFontScale(1.5f);
			_title.setAlignment(Align.center);
			_modeTable.add(_title).pad(10).row();
			_modeTable.add(_buriedButton).size(250,80).pad(10).row();
			_description = new Label("You choose the squares, who knows what you'll get.", _skin);
			_description.setWrap(true);
			_description.setAlignment(algn);
			_modeTable.add(_description).pad(10);
			
			_container.add(_modeTable).pad(30);
			if (!PirateGame.layout.isTablet()) _container.row();
			
			_modeTable = new Table();
			_modeTable.defaults().width(dwidth);
			
			// Knockout Game
			_title = new Label("Knockout Game", _skin);
			_title.setFontScale(1.5f);
			_title.setAlignment(Align.center);
			_modeTable.add(_title).pad(10).row();
			_modeTable.add(_knockoutButton).size(250,80).pad(10).row();
			_description = new Label("If you're killed, you're out of the game. ", _skin);
			_description.setWrap(true);
			_description.setAlignment(algn);
			_modeTable.add(_description).pad(10);
			
			_container.add(_modeTable).pad(30);
			
			//_levelTable.setSize(PirateGame.layout.width, PirateGame.layout.height);
			_levelTable.setFillParent(true);
			_levelTable.setPosition(0, 0);
		
		}
		
		_stage.clear();
		_stage.addActor(_levelTable);
	}
	
	
	// High Scores Screen
	
	public void buildHighScores(){
		
		if (_scoreTable == null){
			_scoreTable = new Table();
			
			Table _container = new Table();
			if (!PirateGame.layout.isTablet()){
				ScrollPane _scroll = new ScrollPane(_container, _skin);
				_scoreTable.add(_scroll).maxHeight(PirateGame.layout.height);
			}
			else _scoreTable.add(_container);
			
			Table _modeTable = new Table();
			Label _title;
			
			int dwidth = (int) (PirateGame.layout.isTablet() ? 450 : PirateGame.layout.width*0.85);
			//int algn = (PirateGame.layout.isTablet() ? Align.left : Align.center);
			
			_modeTable.defaults().width(dwidth);
			
			// Standard Game
			_title = new Label("Normal Game", _skin);
			_title.setFontScale(1.5f);
			_title.setAlignment(Align.center);
			_modeTable.add(_title).pad(10).row();
			_modeTable.add(_standardScoreButton).size(250,80).pad(10).row();
			
			_container.add(_modeTable).pad(30);
			if (!PirateGame.layout.isTablet()) _container.row();
			
			_modeTable = new Table();
			_modeTable.defaults().width(dwidth);
			
			// Free Choice Game
			_title = new Label("Free Choice", _skin);
			_title.setFontScale(1.5f);
			_title.setAlignment(Align.center);
			_modeTable.add(_title).pad(10).row();
			_modeTable.add(_chooseScoreButton).size(250,80).pad(10).row();
			
			_container.add(_modeTable).pad(30);
			_container.row();
			
			_modeTable = new Table();
			_modeTable.defaults().width(dwidth);
			
			// Buried Game
			_title = new Label("Buried Treasure", _skin);
			_title.setFontScale(1.5f);
			_title.setAlignment(Align.center);
			_modeTable.add(_title).pad(10).row();
			_modeTable.add(_buriedScoreButton).size(250,80).pad(10).row();
			
			_container.add(_modeTable).pad(30);
			if (!PirateGame.layout.isTablet()) _container.row();
			
			_modeTable = new Table();
			_modeTable.defaults().width(dwidth);
			
			// Knockout Game
			_title = new Label("Knockout Game", _skin);
			_title.setFontScale(1.5f);
			_title.setAlignment(Align.center);
			_modeTable.add(_title).pad(10).row();
			_modeTable.add(_knockoutScoreButton).size(250,80).pad(10).row();
			
			_container.add(_modeTable).pad(30);
			
			_scoreTable.setFillParent(true);
			_scoreTable.setPosition(0, 0);
		
		}
		
		_stage.clear();
		_stage.addActor(_scoreTable);
	}
	
	
	// Player Edit Screen //
	
	public void buildPlayerOptions(){
		
		_stage.clear();
		_playerTable.clear();
		
		// maybe this should be a global instead of repeated instantiation?
		final HashMap<String, Boolean> players = PirateGame.save.getPlayers();
		final int count = players.size();
		
		for (String name : players.keySet()){
			
			final String nm = name;
			TextButton _editButton;
			final TextField _nameField = new TextField("", _skin);
			
			// Name label/text field
			if (_editName == nm){
				// show text field
				_nameField.setText(name);
				_playerTable.add(_nameField).pad(10).fillX();
				_editButton = new TextButton("OK", _skin);
				_editButton.getLabel().setFontScale(0.8f);
			} else {
				// show label
				Label _label = new Label(name, _skin);
				_playerTable.add(_label).pad(10);
				_editButton = new TextButton("...", _skin);
				_editButton.getLabel().setFontScale(0.8f);
			}
			
			// Edit and Delete Buttons
			TextButton _deleteButton = new TextButton("X", _skin);
			_deleteButton.getLabel().setFontScale(0.8f);
			_playerTable.add(_editButton).pad(10);
			_playerTable.add(_deleteButton).pad(10);
			
			// Edit Listener
			_editButton.addListener(new ClickListener(){
				@Override  public void clicked(InputEvent event, float x, float y) {
					// If text box, save new name, else set _editName to null
					if (_editName == nm){
						String temp = _nameField.getText();
						_editName = null;
						_parent.menuScene.onRenamePlayer(nm, temp);
					} else {
						_editName = nm;
						buildPlayerOptions();
					}
				}
			});
			
			// Delete Listener
			_deleteButton.addListener(new ClickListener(){
				@Override  public void clicked(InputEvent event, float x, float y) {
					if (_editName != null) {
						String temp = _nameField.getText();
						_editName = null;
						_parent.menuScene.onRenamePlayer(nm, temp);
					}
					_parent.menuScene.onRemovePlayer(nm);
				}
			});
			if (count<3) _deleteButton.setTouchable(Touchable.disabled);
			
			_playerTable.row();
			Label intel = new Label("Intelligent:", _skin);
			intel.setFontScale(0.75f);
			_playerTable.add(intel).colspan(2).align(Align.right).padBottom(40);
			
			// Mode check box
			CheckBox _hardmode = new CheckBox("", _skin);
			_hardmode.setChecked(players.get(name));
			_hardmode.getCells().get(0).size(25,25);//50, 50);
			_playerTable.add(_hardmode).padBottom(40);
			
			// Check Listener
			_hardmode.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					_parent.menuScene.onToggleMode(nm, !players.get(nm));
					
				}
			});
			_playerTable.row();
		}
		
		// Add Button
		_playerTable.add(_addButton).pad(20);
		if (count>=7) _addButton.setTouchable(Touchable.disabled);
		else _addButton.setTouchable(Touchable.enabled);
		
		// Reset Button
		_playerTable.add(_resetButton).pad(20).colspan(2);
		
		//_playerTable.setFillParent(true);
		ScrollPane pScroll = new ScrollPane(_playerTable, _skin);
		pScroll.setScrollBarPositions(false, true);
		pScroll.setFadeScrollBars(false);
		
		Table contTable = new Table(_skin);
		contTable.add(pScroll).maxHeight((float) (PirateGame.layout.height*0.8))
			.width((float) (PirateGame.layout.width*0.8));
		contTable.setFillParent(true);
		
		_stage.addActor(contTable);
		
		// Difficulty message
		String message = "Difficulty: " + PirateGame.save.getDifficultyString()
				+" ("+PirateGame.save.getDifficulty()+"/4)";
		Label _difficulty = new Label(message, _skin);
		_difficulty.setFontScale(0.8f);
		_difficulty.setPosition(20+PirateGame.layout.gutter,65);
		_stage.addActor(_difficulty);
		
	}
	
	public boolean isEditMode(){
		// so we can stop backspace changing menu scene
		return _editName != null;
	}
	
	public Stage getStage(){
		return _stage;
	}
	
	public BitmapFont getFont(){
		return _skin.getFont("default-font");
	}
	
	
	// RENDER //
	
	public void render(){
		if (_stage != null) {
			_stage.act();
			_stage.draw();
		}
	}
	
	
	// ASSET FUNCTIONS //
	
	public boolean isLoaded(){
		return _loaded;
	}
	
	public void load(){
		// Load Textures
		assetManager.load("menuLogo.png", Texture.class);
		if (PirateGame.layout.isTablet())
			assetManager.load("tablet-menu-bg.png", Texture.class);
		else assetManager.load("menu-slice.png", Texture.class);
		
		_loaded = true;
	}
	
	public void unload(){
		if (_loaded){
			imgLogo = null;
			imgGPlus = null;
			menuBG = null;
			winBG = null;
			scoreBG = null;
			
			assetManager.unload("menuLogo.png");
			if (PirateGame.layout.isTablet())
				assetManager.unload("tablet-menu-bg.png");
			else assetManager.unload("menu-slice.png");
			
			//_stage.clear();
			_buttons.dispose();
			_stage.dispose();
			_skin.dispose();
			
			_loaded = false;
		}
	}
	
	public void assignResources(){
		
		assetManager.finishLoading();
		
		// Assign
		imgLogo = new TextureRegion(assetManager.get("menuLogo.png", Texture.class));
		if (PirateGame.layout.isTablet())
			menuBG = new TextureRegion(assetManager.get("tablet-menu-bg.png", Texture.class));
		else menuBG = new TextureRegion(assetManager.get("menu-slice.png", Texture.class));
		
		imgGPlus = _buttons.findRegion("gplus-btn");
		winBG = _buttons.findRegion("win-bg");
		scoreBG = _buttons.findRegion("score-bg");
		
		// Initialise Objects
		initialiseMainObjs();
		initialisePlayerObjs();
	
	}

}

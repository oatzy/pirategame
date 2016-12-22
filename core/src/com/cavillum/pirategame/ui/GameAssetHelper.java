package com.cavillum.pirategame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cavillum.pirategame.PirateGame;
import com.cavillum.pirategame.objects.Grid;

public class GameAssetHelper {
	
	public AssetManager assetManager;
	
	private TextureAtlas _itemAtlas;
	private TextureAtlas _specialAtlas;
	private TextureAtlas _uiAtlas;
	
	public Skin skin;
	
	public TextureRegion img1000;
	public TextureRegion img200;
	public TextureRegion img3000;
	public TextureRegion img5000;
	public TextureRegion imgBank;
	public TextureRegion imgBomb;
	public TextureRegion imgChoose;
	public TextureRegion imgDouble;
	public TextureRegion imgGift;
	public TextureRegion imgKill;
	public TextureRegion imgMirror;
	public TextureRegion imgPeek;
	public TextureRegion imgRob;
	public TextureRegion imgShield;
	public TextureRegion imgSwap;
	
	public TextureRegion img10000;
	public TextureRegion imgHalf;
	public TextureRegion imgShell;
	public TextureRegion imgSkull;
	public TextureRegion imgReveal;
	
	public TextureRegion selector;
	public TextureRegion pointsBG;
	public TextureRegion chestBG;
	public TextureRegion defenceBG;
	public NinePatch boxBG;
	public NinePatch sidebarBG; 
	
	public TextureRegion notifBtn;
	public TextureRegion playersBtn;
	public TextureRegion notifInd;
	public TextureRegion helpBtn;
	
	public TextureRegion bgSlice;
	public NinePatch gridBg;
	public TextureRegion gridSq;
	public TextureRegion burySq;
	public TextureRegion messageBg;
	public TextureRegion mBtnBg;
	
	public BitmapFont font;
	public BitmapFont pfont;
	public BitmapFont tfont;
	
	private boolean _loaded=false;
	
	public GameAssetHelper(AssetManager manager){
		assetManager = manager;
	}
	
	public boolean isLoaded(){
		return _loaded;
	}
	
	public void load(){
		// Load Atlas
		_itemAtlas = new TextureAtlas(Gdx.files.internal("itemAtlas"));
		_specialAtlas = new TextureAtlas(Gdx.files.internal("specialAtlas"));
		
		if (PirateGame.layout.isTablet()){
			_uiAtlas = new TextureAtlas(Gdx.files.internal("tablet-atlas"));
			tfont = new BitmapFont(Gdx.files.internal("box-title.fnt"), false);
			assetManager.load("tablet-bg-slice.png", Texture.class);
		}
		
		else {
			_uiAtlas = new TextureAtlas(Gdx.files.internal("phone-atlas"));
		}
		
		assetManager.load("mound.png", Texture.class);
		
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		
		// Load Font
		font = new BitmapFont(Gdx.files.internal("calibri.fnt"), false);
		pfont = new BitmapFont(Gdx.files.internal("points.fnt"), false);
		
		_loaded = true;
		
	}
	
	public void unload(){
		if (_loaded){
			img1000 = null;
			img200 = null;
			img3000 = null;
			img5000 = null;
			imgBank = null;
			imgBomb = null;
			imgChoose = null;
			imgDouble = null;
			imgGift = null;
			imgKill = null;
			imgMirror = null;
			imgPeek = null;
			imgRob = null;
			imgShield = null;
			imgSwap = null;
			
			img10000 = null;
			imgHalf = null;
			imgSkull = null;
			imgShell = null;
			imgReveal = null;
			
			selector = null;
			pointsBG = null;
			chestBG = null;
			defenceBG = null;
			boxBG = null;
			sidebarBG = null;
			
			notifBtn = null;
			playersBtn = null;
			notifInd = null;
			helpBtn = null;
			
			bgSlice = null;
			gridBg = null;
			gridSq = null;
			burySq = null;
			messageBg = null;
			mBtnBg = null;
			
			if (PirateGame.layout.isTablet()){
				assetManager.unload("tablet-bg-slice.png");
				tfont.dispose();
			}
			assetManager.unload("mound.png");
			
			_itemAtlas.dispose();
			_specialAtlas.dispose();
			_uiAtlas.dispose();
			font.dispose();
			pfont.dispose();
			
			//Gdx.app.log("AssetHelper", "Assets unloaded");
			
			_loaded = false;
		}
	}
	
	public void assignResources(){
		
		assetManager.finishLoading();
		
		// Assign
		img1000 = _itemAtlas.findRegion("img1000");
		img200 = _itemAtlas.findRegion("img200");
		img3000 = _itemAtlas.findRegion("img3000");
		img5000 = _itemAtlas.findRegion("img5000");
		imgBank = _itemAtlas.findRegion("imgBank");
		imgBomb = _itemAtlas.findRegion("imgBomb");
		imgChoose = _itemAtlas.findRegion("imgChoose");
		imgDouble = _itemAtlas.findRegion("imgDouble");
		imgGift = _itemAtlas.findRegion("imgGift");
		imgKill = _itemAtlas.findRegion("imgKill");
		imgMirror = _itemAtlas.findRegion("imgMirror");
		imgPeek = _itemAtlas.findRegion("imgPeek");
		imgRob = _itemAtlas.findRegion("imgRob");
		imgShield = _itemAtlas.findRegion("imgShield");
		imgSwap = _itemAtlas.findRegion("imgSwap");
		
		img10000 = _specialAtlas.findRegion("img10000");
		imgHalf = _specialAtlas.findRegion("imgHalf");
		imgSkull = _specialAtlas.findRegion("imgSkull");
		imgShell = _specialAtlas.findRegion("imgShell");
		imgReveal = _specialAtlas.findRegion("imgReveal");
		
		gridSq = _uiAtlas.findRegion("grid-sq");
		selector = _uiAtlas.findRegion("selector");
		notifInd = _uiAtlas.findRegion("notif-indic");
		
		if (PirateGame.layout.getType() == LayoutHandler.Layout.Tablet169){
			notifBtn = _uiAtlas.findRegion("side-bg-1");
			playersBtn = _uiAtlas.findRegion("side-bg-2");
			helpBtn = _uiAtlas.findRegion("side-bg-3");
			sidebarBG = skin.getPatch("rightbox");
		}
		else {
			notifBtn = _uiAtlas.findRegion("notif-btn");
			playersBtn = _uiAtlas.findRegion("player-btn");
			helpBtn = _uiAtlas.findRegion("help-btn");
			mBtnBg = _uiAtlas.findRegion("mbar-btn-bg");
			messageBg = _uiAtlas.findRegion("message-bar");
		}
		
		if (PirateGame.layout.isTablet()){
			bgSlice = new TextureRegion(assetManager.get("tablet-bg-slice.png", Texture.class));
			gridBg = skin.getPatch("grid_bg_patch");
			boxBG = skin.getPatch("leftbox");
		}
		else {
			pointsBG = _uiAtlas.findRegion("points-bg");
			chestBG = _uiAtlas.findRegion("chest-bg");
			defenceBG = _uiAtlas.findRegion("defence-bg");
		}
		
		burySq = new TextureRegion(assetManager.get("mound.png", Texture.class));
		
		//Gdx.app.log("AssetHelper", "Assets assigned");
	}
	
	public TextureRegion getItemImage(Grid.sqType type){
		switch(type){
		case sq200:
			return img200;
		case sq1000:
			return img1000;
		case sq3000:
			return img3000;
		case sq5000:
			return img5000;
		case sqBank:
			return imgBank;
		case sqBomb:
			return imgBomb;
		case sqChoose:
			return imgChoose;
		case sqDouble:
			return imgDouble;
		case sqGift:
			return imgGift;
		case sqKill:
			return imgKill;
		case sqMirror:
			return imgMirror;
		case sqPeek:
			return imgPeek;
		case sqRob:
			return imgRob;
		case sqShield:
			return imgShield;
		case sqSwap:
			return imgSwap;
		
		case sq10000:
			return img10000;
		case sqHalf:
			return imgHalf;
		case sqSkull:
			return imgSkull;
		case sqShell:
			return imgShell;
		case sqReveal:
			return imgReveal;
			
		default:
			return null;
		}
	}

}

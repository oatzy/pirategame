package com.cavillum.pirategame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cavillum.pirategame.objects.Grid;

public class GameAssetHelper {
	
	public AssetManager assetManager;
	
	private TextureAtlas _itemAtlas;
	private TextureAtlas _uiAtlas;
	
	public TextureRegion imgBoard;
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
	
	public TextureRegion selector;
	public TextureRegion pointsBG;
	public TextureRegion inventoryBG;
	
	public BitmapFont font;
	
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
		_uiAtlas = new TextureAtlas(Gdx.files.internal("uiElements"));
		
		// Load Font
		font = new BitmapFont(Gdx.files.internal("verdana.fnt"), false);
		
		// Load Textures
		assetManager.load("board.png", Texture.class);
		//Gdx.app.log("AssetHelper", "assets loaded");
		
		_loaded = true;
		
	}
	
	public void unload(){
		if (_loaded){
			imgBoard = null;
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
			
			selector = null;
			pointsBG = null;
			inventoryBG = null;
			
			assetManager.unload("board.png");
			
			_itemAtlas.dispose();
			_uiAtlas.dispose();
			font.dispose();
			
			//Gdx.app.log("AssetHelper", "Assets unloaded");
			
			_loaded = false;
		}
	}
	
	public void assignResources(){
		
		assetManager.finishLoading();
		
		// Assign
		imgBoard = new TextureRegion(assetManager.get("board.png", Texture.class));
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
		
		selector = _uiAtlas.findRegion("selector");
		pointsBG = _uiAtlas.findRegion("pointBackground");
		inventoryBG = _uiAtlas.findRegion("inventoryBackground");
		
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
		default:
			return null;
		}
	}

}

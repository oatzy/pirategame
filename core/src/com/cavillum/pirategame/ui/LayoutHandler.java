package com.cavillum.pirategame.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.cavillum.pirategame.PirateGame;
import com.cavillum.pirategame.helpers.HelpBuilder;
import com.cavillum.pirategame.ui.GameScene.State;

public class LayoutHandler {
	
	public enum Layout {Phone, Tablet43, Tablet85, Tablet169};
	public enum SideType {History, Players, Help, Null};
	
	private PirateGame _parent;
	private SpriteBatch batch;
	
	public int width;
	public int height;
	public int gutter;
	public int ygutter = 0; //not needed in most cases
	public Vector2 origin;
	
	public SideBar _sidebar;
	
	private Layout _layout;
	private SideType _stype;
	
	private boolean _nIndicator;
	
	public LayoutHandler(PirateGame game, int swidth, int sheight){
		_parent = game;
		batch = game.getSpriteBatch();
		
		_stype = SideType.Null;
		
		if (3*swidth < 4*sheight){
			// phone/portrait
			_layout = Layout.Phone;
			height = 1280;
			width = swidth*1280/sheight;
			if (width < 720){
				width = 720;
				gutter = 0;
			} else gutter = (width-720)/2;
			origin = new Vector2(3+gutter,1105);
		} 
		
		else if (5*swidth < 8*sheight){
			// 4:3
			_layout = Layout.Tablet43;
			height = 880;
			width = swidth*880/sheight;
			if (width < 1173){
				width = 1173;
				gutter = 0;
			} else gutter = (width-1173)/2;
			origin = new Vector2(365+gutter,840);
		} 
		
		else if (9*swidth < 16*sheight){
			// 8:5
			_layout = Layout.Tablet169;
			_stype = SideType.Help;
			gutter = 0;
			width = 1421;
			height = 1421*sheight/swidth;
			if (height < 800){
				height = 800;
				ygutter = 0;
			} else ygutter = (height-800)/2;
			origin = new Vector2(315,760+ygutter);
		}
		
		else {
			// 16:9
			_layout = Layout.Tablet169;
			_stype = SideType.Help;
			height = 800;
			width = swidth*800/sheight;
			if (width < 1421){
				width = 1421;
				gutter = 0;
			} else gutter = (width-1421)/2;
			origin = new Vector2(315,760);
		}
	}
	
	public void showIndicator(boolean show){
		if (show && _stype == SideType.History) return;
		_nIndicator = show;
	}
	
	public Layout getType(){
		return _layout;
	}
	
	public SideType getSideBarType(){
		return _stype;
	}
	
	public SideBar getSideBar(){
		return _sidebar;
	}
	
	public void createSideBar(GameAssetHelper assets){
		_sidebar = new SideBar(assets);
	}
	
	public boolean isTablet(){
		return (_layout == Layout.Tablet169 || _layout == Layout.Tablet85
				|| _layout == Layout.Tablet43);
	}
	
	public boolean hasMessageBar(){
		return (_layout == Layout.Phone || _layout == Layout.Tablet43);
	}
	
	public boolean hasSideBar(){
		return (_layout == Layout.Tablet169 || _layout == Layout.Tablet85);
	}
	
	
	// Renderers //
	
	public void drawUiElements(GameAssetHelper assets){
		if (_layout == Layout.Phone)
			drawPortrait(assets);
		if (_layout == Layout.Tablet43)
			drawTablet43(assets);
		if (_layout == Layout.Tablet169)
			drawTablet169(assets);
	}
	
	public void drawPortrait(GameAssetHelper assets){
		
		if (_parent.gameScene.hasStarted()) {
			
			// Points
			batch.draw(assets.pointsBG, 410+gutter, 1130);
			assets.pfont.draw(batch, ""+_parent.gameScene.getPlayer().getPoints(), 
					425+gutter, 1195, 230, Align.right, false);
			
			// Bank
			batch.draw(assets.chestBG, 410+gutter, 170);
			assets.pfont.draw(batch, ""+_parent.gameScene.getPlayer().getBank(), 
					430+gutter, 240, 230, Align.right, false);
			
			// Defences
			batch.draw(assets.defenceBG, 45+gutter, 155);
			
			// Top Buttons
			batch.draw(assets.notifBtn, 35+gutter, 1140);
			batch.draw(assets.playersBtn, 150+gutter, 1140);
			
			// Notif Indicator
			if (_nIndicator) batch.draw(assets.notifInd, 85+gutter, 1185);
		
		}
		
		// Message bar
		batch.draw(assets.messageBg, 0, 0, 0, 0, width, 
				assets.messageBg.getRegionHeight(), 1, 1, 0);
		assets.font.draw(batch, _parent.gameScene.getMessage(), 50,65);
		// Button BG
		batch.draw(assets.mBtnBg, width-assets.mBtnBg.getRegionWidth(), 0);
		// Help Btn
		batch.draw(assets.helpBtn, width-86, 13);
		
	}
	
	public void drawTablet43(GameAssetHelper assets){
		
		// Board Background
		tileBackground(assets.bgSlice);
		
		// Grid BG
		assets.gridBg.draw(batch, 340+gutter, 102, 764, 764);
		
		if (_parent.gameScene.hasStarted()){
		
			// Points
			assets.boxBG.draw(batch, 0, 630, 275, 200);
			assets.tfont.draw(batch, "POINTS", 0, 805, 235, Align.right, false);
			assets.pfont.draw(batch, ""+_parent.gameScene.getPlayer().getPoints(), 
					0, 725, 220, Align.right, false);
			
			// Bank
			assets.boxBG.draw(batch, 0, 400, 275, 200);
			assets.tfont.draw(batch, "CHEST", 0, 575, 235, Align.right, false);
			assets.pfont.draw(batch, ""+_parent.gameScene.getPlayer().getBank(), 
					0, 495, 220, Align.right, false);
			
			// Defences
			assets.boxBG.draw(batch, 0, 130, 275, 240);
			assets.tfont.draw(batch, "DEFENCE", 0, 345, 235, Align.right, false);
		
		}
		
		// Message bar
		batch.draw(assets.messageBg, 0, 0, 0, 0, width, 
				assets.messageBg.getRegionHeight(), 1, 1, 0);
		assets.font.draw(batch, _parent.gameScene.getMessage(), 50, 55);
		// Button BG
		batch.draw(assets.mBtnBg, width-assets.mBtnBg.getRegionWidth(), 0);
		
		// bottom Buttons
		batch.draw(assets.notifBtn, width-320, 10);
		batch.draw(assets.playersBtn, width-205, 10);
		batch.draw(assets.helpBtn, width-100, 7);
		
		// Notif Indicator
		if (_nIndicator) batch.draw(assets.notifInd, width-280, 45);
		
	}
	
	public void drawTablet169(GameAssetHelper assets){
		
		// Board Background
		tileBackground(assets.bgSlice);

		// Grid BG
		assets.gridBg.draw(batch, 290+gutter, 22+ygutter, 764, 764);
		
		// Draw Sidebar
		_sidebar.draw();

		if (_parent.gameScene.hasStarted()){

			// Points
			assets.boxBG.draw(batch, 0, 550+ygutter, 275, 200);
			assets.tfont.draw(batch, "POINTS", 0, 725+ygutter, 235, Align.right, false);
			assets.pfont.draw(batch, ""+_parent.gameScene.getPlayer().getPoints(), 
					0, 645+ygutter, 220, Align.right, false);

			// Bank
			assets.boxBG.draw(batch, 0, 320+ygutter, 275, 200);
			assets.tfont.draw(batch, "CHEST", 0, 495+ygutter, 235, Align.right, false);
			assets.pfont.draw(batch, ""+_parent.gameScene.getPlayer().getBank(), 
					0, 415+ygutter, 220, Align.right, false);

			// Defences
			assets.boxBG.draw(batch, 0, 50+ygutter, 275, 240);
			assets.tfont.draw(batch, "DEFENCE", 0, 265+ygutter, 235, Align.right, false);
			
			// Notif Indicator
			if (_nIndicator) batch.draw(assets.notifInd, width-270, 85+ygutter);

		}
		
	}
	
	
	private void tileBackground(TextureRegion bgSlice){
		float a = 240f/(width*width);
		float b = width/2;
		float c = height-bgSlice.getRegionHeight();
		for (int i=0; i<width; i+=bgSlice.getRegionWidth())
			batch.draw(bgSlice, i, (float) ( a*(i-b)*(i-b) + c));
	}
	
	
	// Coordinate getters
	
	public Vector2 getPointFloat(){
		if (_layout == Layout.Phone)
			return new Vector2(425+gutter, 1230);
		if (_layout == Layout.Tablet43)
			return new Vector2(0, 760);
		if (_layout == Layout.Tablet169)
			return new Vector2(0, 675+ygutter);
		return null;
	}
	
	public Vector2 getBankFloat(){
		if (_layout == Layout.Phone)
			return new Vector2(430+gutter, 275);
		if (_layout == Layout.Tablet43)
			return new Vector2(0, 530);
		if (_layout == Layout.Tablet169)
			return new Vector2(0, 450+ygutter);
		return null;
	}
	
	public Vector2 getDefenceOrigin(){
		if (_layout == Layout.Phone)
			return new Vector2(75+gutter, 175);
		if (_layout == Layout.Tablet43)
			return new Vector2(15,175);
		if (_layout == Layout.Tablet169)
			return new Vector2(15,95+ygutter);
		return null;
	}
	
	
	// Click Checkers
	
	public boolean notifClicked(float x, float y){
		if (_layout == Layout.Phone)
			return (x>=35+gutter && x<=110+gutter && y>=1140 && y<=1220);
		if (_layout == Layout.Tablet43)
			return (x>=width-340 && x<=width-240 && y<=80);
		if (_layout == Layout.Tablet169)
			return (x>=width-345 && x<=width-235 && y>=22+ygutter && y<=110+ygutter);
		return false;
	}
	
	public boolean playersClicked(float x, float y){
		if (_layout == Layout.Phone)
			return (x>=150+gutter && x<=225+gutter && y>=1140 && y<=1220);
		if (_layout == Layout.Tablet43)
			return (x>=width-230 && x<=width-130 && y<=80);
		if (_layout == Layout.Tablet169)
			return (x>=width-225 && x<=width-125 && y>=22+ygutter && y<=110+ygutter);
		return false;
	}
	
	public boolean helpClicked(float x, float y){
		if (_layout == Layout.Phone)
			return (x>=width-100 && y<=80);
		if (_layout == Layout.Tablet43)
			return (x>=width-120 && y<=80);
		if (_layout == Layout.Tablet169)
			return (x>=width-115 && y>=22+ygutter && y<=110+ygutter);
		return false;
	}
	
	
	// Sidebar Class
	
	public class SideBar{
		
		private GameAssetHelper assets;
		private Table _container;
		private Table _textTable;
		private ScrollPane _scroll;
		
		public SideBar(GameAssetHelper assets){
			this.assets = assets;
			
			_container = new Table(assets.skin);
			_textTable = new Table(assets.skin);
			_scroll = new ScrollPane(_textTable, assets.skin);
			_container.add(_scroll).maxHeight(550);
			
			_textTable.defaults().width(295).pad(10);
			_container.top();
			_container.setSize(325, 565);
			_container.setPosition(width-320, 140+ygutter);
			
			buildHelp(State.ArrangeItems);
		}
		
		public Table getTable(){
			return _container;
		}
		
		
		// Draw UI elements
		
		public void draw(){
			if (_stype == SideType.History) drawHistory();
			if (_stype == SideType.Players) drawPlayers();
			if (_stype == SideType.Help) drawHelp();
		}
		
		public void drawHistory(){
			// bottom Buttons
			batch.draw(assets.helpBtn, width-125, 22+ygutter);
			batch.draw(assets.playersBtn, width-235, 22+ygutter);
			
			// Side Bar
			assets.sidebarBG.draw(batch, width-345, 110+ygutter, 345, 675);
			batch.draw(assets.notifBtn, width-345, 22+ygutter);
			
			// Title
			assets.tfont.draw(batch, "HISTORY", width-300, 755+ygutter, 300, Align.left, false);
			
		}
		
		public void drawPlayers(){
			// bottom Buttons
			batch.draw(assets.notifBtn, width-345, 22+ygutter);
			batch.draw(assets.helpBtn, width-125, 22+ygutter);
			
			// Side Bar
			assets.sidebarBG.draw(batch, width-345, 110+ygutter, 345, 675);
			batch.draw(assets.playersBtn, width-235, 22+ygutter);
			
			//Title
			assets.tfont.draw(batch, "PLAYERS", width-300, 755+ygutter, 300, Align.left, false);
		}
		
		public void drawHelp(){
			
			// bottom Buttons
			batch.draw(assets.notifBtn, width-345, 22+ygutter);
			batch.draw(assets.playersBtn, width-235, 22+ygutter);
			
			// Side Bar
			assets.sidebarBG.draw(batch, width-345, 110+ygutter, 345, 675);
			batch.draw(assets.helpBtn, width-125, 22+ygutter);

			// Title
			assets.tfont.draw(batch, "HELP", width-300, 755+ygutter, 300, Align.left, false);
		}
		
		// Build text content (table)
		
		public void buildHistory(ArrayList<String> history, boolean temp){
			if (!temp) _stype = SideType.History;
			
			_textTable.clear();
			
			// create labels
			Label _label;
			if (history.size() == 0) {
				_label = new Label("Nothing to see...", assets.skin);
				_label.setWrap(true);
				_label.setAlignment(Align.left);
				_textTable.add(_label).pad(10).row();
			}
			else for (int i=history.size()-1; i>=0; i--){
				_label = new Label(history.get(i), assets.skin);
				_label.setWrap(true);
				_label.setAlignment(Align.left);
				_textTable.add(_label).pad(10).padBottom(20).row();
			}
			
		}
		
		public void buildHistory(ArrayList<String> history){
			buildHistory(history, false);
		}
		
		public void buildPlayers(List<String> players, boolean temp){
			if (!temp) _stype = SideType.Players;
			
			_textTable.clear();
			
			for (String p : players){
				Label _pLabel = new Label(p, assets.skin);
				_pLabel.setAlignment(Align.left);
				_textTable.add(_pLabel).pad(10).padBottom(20);//.expandX();
				_textTable.row();
			}
		}
		
		public void buildPlayers(List<String> players){
			buildPlayers(players, false);
		}
		
		public void buildPlayers(List<String> alive, List<String> dead){
			_stype = SideType.Players;
			
			_textTable.clear();
			
			Label _title = new Label("> Alive:", assets.skin);
			_title.setFontScale(1.2f);
			_title.setAlignment(Align.left);
			_textTable.add(_title).pad(10).padBottom(20).row();
			
			for (String p : alive){
				Label _pLabel = new Label("    "+p, assets.skin);
				_pLabel.setAlignment(Align.left);
				_textTable.add(_pLabel).pad(10).padBottom(20);//.expandX();
				_textTable.row();
			}
			
			_title = new Label("> Dead:", assets.skin);
			_title.setFontScale(1.2f);
			_title.setAlignment(Align.left);
			_textTable.add(_title).pad(10).padTop(30).padBottom(20).row();
			
			for (String p : dead){
				Label _pLabel = new Label("    "+p, assets.skin);
				_pLabel.setAlignment(Align.left);
				_textTable.add(_pLabel).pad(10).padBottom(20);//.expandX();
				_textTable.row();
			}
		}
		
		public void buildHelp(State state, String message, boolean temp){
			if (!temp) _stype = SideType.Help;
			
			_textTable.clear();
			
			// create labels
			Label _label;
			String text;
			
			if (message != ""){
				_label = new Label(message, assets.skin);
				_label.setWrap(true);
				_label.setAlignment(Align.left);
				_textTable.add(_label).pad(10).padBottom(20).row();
			}
			
			if (state == State.CollectItem){
				text = HelpBuilder.buildItemHelp(_parent.gameScene.getCurrent());
				_label = new Label(text, assets.skin);
				_label.setWrap(true);
				_label.setAlignment(Align.left);
				_textTable.add(_label).pad(10).padBottom(20).row();
			}
			
			if (state == State.ShowPopup){
				text = HelpBuilder.buildPopupHelp(_parent.gameScene.getTopPopup());
				_label = new Label(text, assets.skin);
				_label.setWrap(true);
				_label.setAlignment(Align.left);
				_textTable.add(_label).pad(10).padBottom(20).row();
			}
			
			text = HelpBuilder.buildStateHelp(state);
			if (text!=""){
				_label = new Label(text, assets.skin);
				_label.setWrap(true);
				_label.setAlignment(Align.left);
				_textTable.add(_label).pad(10).padBottom(20).row();
			}
			
		}
		
		public void buildHelp(State state, String message){
			buildHelp(state, message, false);
		}
		
		public void buildHelp(State state, boolean temp){
			buildHelp(state, "", temp);
		}
		
		public void buildHelp(State state){
			buildHelp(state, false);
		}

	}

}

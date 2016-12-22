package com.cavillum.pirategame.ui;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.cavillum.pirategame.PirateGame;
import com.cavillum.pirategame.data.InteractionData;
import com.cavillum.pirategame.objects.Grid;
import com.cavillum.pirategame.objects.Player;

public class AnimationHelper {
	private SpriteBatch _batch;
	
	private double _time = 0.8;
	private double _duration = 1.8;
	
	private double _dduration = 0.3;
	private ArrayList<Defence> _defences;
	
	private BitmapFont _font;
	
	private double _fduration = 0.4;
	private double _fdelay = 0.3;
	private ArrayList<FloatingPoints> _floaters;
	
	private Vector2 pfPos = PirateGame.layout.getPointFloat();
	private Vector2 bfPos = PirateGame.layout.getBankFloat();
	private Vector2 dPos = PirateGame.layout.getDefenceOrigin();
	
	public AnimationHelper(SpriteBatch batch, BitmapFont font){
		_batch = batch;
		_font = font;
		_floaters = new ArrayList<FloatingPoints>();
		_defences = new ArrayList<Defence>();
	}
	
	public AnimationHelper(){
		_batch = null;
		_font = null;
	}
	
	
	public void reset(){
		_time = 1.;
		_floaters.clear();
		_defences.clear();
	}
	
	
	// Item Animations
	
	public double wiggle(){
		_duration = 2;
		_time = (_time+Gdx.graphics.getDeltaTime())%_duration;
		if (_time < _duration/3.){
			return -12*(1-1.25*_time)*Math.sin(_time*12.*Math.PI/_duration);
		}
		return 0.;
	}
	
	public double throb(){
		_duration = 1.8;
		_time = (_time+Gdx.graphics.getDeltaTime())%_duration;
		if (_time < _duration/3.){
			return 1+0.25*(Math.sin(_time*3.*Math.PI/_duration));
		}
		return 1.;
	}
	
	
	// Defence Animations
	
	public class Defence{
		private TextureRegion _img;
		private Player.dfType _type;
		private double _dtime;
		private double _scale;
		private int _gain = -1;
		
		public Defence(TextureRegion img, Player.dfType type){
			if (type == Player.dfType.dfNone || img == null) return;
			_img = img;
			_type = type;
			_dtime = 0;
			_scale = 1.5;
		}
		
		public boolean update(){
			_dtime = _dtime + Gdx.graphics.getDeltaTime();
			isAdded();
			return isRemoved();
		}
		
		public boolean isAdded(){
			if (_gain == -1 && _dtime>_dduration){
				_gain = 0;
				return true;
			} return false;
		}
		
		public boolean isRemoved(){
			return (_gain >= 1 && _dtime>_dduration);
		}
		
		public void remove(){
			_dtime = 0;
			_gain = 1;
		}
		
		public boolean isType(Player.dfType type){
			return type == _type;
		}
		
		public void draw(int xoff, int yoff){
			int x = (int) dPos.x+xoff;
			int y = (int) dPos.y+yoff;
			_scale += _gain*0.5*Gdx.graphics.getDeltaTime()/_dduration;
			_batch.draw(_img, x, y, _img.getRegionWidth()/2, _img.getRegionHeight()/2,
					_img.getRegionWidth(), _img.getRegionHeight(), 
					(float)_scale, (float)_scale, 1);
		}
		
		public void draw(){
			draw(0,0);
		}
	}
	
	public void updateDefences(){
		for (int i=0; i<_defences.size(); i++){
			if (_defences.get(i).update()){
				_defences.remove(i);
				i--;
			}
		}
	}
	
	public void drawDefences(){
		updateDefences();
		if (_defences.size() == 0){
			_font.setColor(0.1f, 0.1f, 0.1f, 0.1f);
			_font.draw(_batch, "(NONE)", dPos.x+25, dPos.y+70);//30,75
			_font.setColor(0f, 0f, 0f, 1f);
		}
		if (_defences.size()>0) _defences.get(0).draw();
		if (_defences.size()>1) _defences.get(1).draw(125, 0);
	}
	
	public void addDefence(TextureRegion img, Player.dfType type){
		_defences.add(new Defence(img, type));
	}
	
	public void removeDefence(Player.dfType type){
		for (int i=0; i<_defences.size(); i++){
			if (_defences.get(i).isType(type)){
				_defences.get(i).remove();
				return;
			}
		}
	}
	
	
	// Point Animations
	
	public class FloatingPoints{
		private String _text;
		private boolean _direction;
		private double x, y;
		private double _ftime = 0;
	
		public FloatingPoints(String text){
			_text = text;
			_direction = true; // up
			x = pfPos.x;
			y = pfPos.y;
		}
		
		public FloatingPoints(String text, boolean direction){
			this(text);
			_direction = direction;
			if (!direction) y -= 70;
		}
		
		public FloatingPoints(String text, int x, int y){
			this(text);
			this.x = x;
			this.y = y;
		}
		
		public FloatingPoints(int points){
			this("+"+points);
		}
		
		public FloatingPoints(int points, boolean direction){
			this((direction ? "+" : "-")+points, direction);
		}
		
		public FloatingPoints(int points, int x, int y){
			this("+"+points, x, y);
		}
		
		public boolean isDone(){
			_ftime += Gdx.graphics.getDeltaTime();
			return _ftime > _fduration;
		}
		
		public boolean delay(){
			return (_ftime < _fdelay);
		}
		
		public void draw(){
			y = y + ((_direction) ? 1 : -1)*25*Gdx.graphics.getDeltaTime()/_fduration;
			int w = (PirateGame.layout.isTablet()) ? 220 : 230;
			_font.draw(_batch, _text, (int) x, (int) y, w, Align.right, false);
		}
	}
	
	public void setPointsText(Grid.sqType type, int points){
		switch (type){
		case sq200:
			_floaters.add(new FloatingPoints("+200"));
			break;
		case sq1000:
			_floaters.add(new FloatingPoints("+1000"));
			break;
		case sq3000:
			_floaters.add(new FloatingPoints("+3000"));
			break;
		case sq5000:
			_floaters.add(new FloatingPoints("+5000"));
			break;
		case sqDouble:
			_floaters.add(new FloatingPoints("x2"));
			break;
		case sqBomb:
			_floaters.add(new FloatingPoints(points, false));
			break;
		case sqBank:
			_floaters.add(new FloatingPoints(points, (int)bfPos.x, (int)bfPos.y));
			break;
		case sqHalf:
			_floaters.add(new FloatingPoints("x 1/2", false));
			break;
		case sq10000:
			_floaters.add(new FloatingPoints("+10000"));
		default:
			break;
		}
	}
	
	public void setAttackPoints(InteractionData data){
		if (data.getDefenceType() == Player.dfType.dfShield) return;
		switch (data.getType()){
		case sqSwap:
			if (data.getDefenceType() == Player.dfType.dfNone){
				_floaters.add(new FloatingPoints(data.getSourcePoints(), false));
				_floaters.add(new FloatingPoints(data.getTargetPoints()));
			}
			break;
		case sqRob:
			if (data.getDefenceType() == Player.dfType.dfNone){
				_floaters.add(new FloatingPoints(data.getTargetPoints()));
			}
		case sqKill:
			if (data.getDefenceType() == Player.dfType.dfMirror){
				_floaters.add(new FloatingPoints(data.getSourcePoints(), false));
			}
		default:
			break;
		}
	}
	
	public void setDefendPoints(InteractionData data){
		
		if (data.getDefenceType() == Player.dfType.dfShield){
			removeDefence(Player.dfType.dfShield);
			return;
		}
		if (data.getDefenceType() == Player.dfType.dfMirror){
			removeDefence(Player.dfType.dfMirror);
		}
		
		if (data.getType() == Grid.sqType.sqGift){
			_floaters.add(new FloatingPoints("+1000"));
			return;
		}
		
		switch (data.getType()){
		case sqSwap:
			if (data.getDefenceType() == Player.dfType.dfNone){
				_floaters.add(new FloatingPoints(data.getTargetPoints(), false));
				_floaters.add(new FloatingPoints(data.getSourcePoints()));
			}
			break;
		case sqRob:
			if (data.getDefenceType() == Player.dfType.dfMirror){
				_floaters.add(new FloatingPoints(data.getSourcePoints()));
			}
		case sqKill:
			if (data.getDefenceType() == Player.dfType.dfNone){
				_floaters.add(new FloatingPoints(data.getTargetPoints(), false));
			}
		default:
			break;
		}
	}
	
	public void drawFloaters(){
		if (_floaters.isEmpty()) return;
		for (int i=0; i<_floaters.size(); i++){
			if (!_floaters.get(i).isDone()) {
				_floaters.get(i).draw();
				if (_floaters.get(i).delay()) return;
			}
			else {
				_floaters.remove(i);
				i--;
			}
		}
	}
}

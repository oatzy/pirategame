package com.cavillum.pirategame.objects;

import java.util.ArrayList;

import com.cavillum.pirategame.data.InteractionData;

//import com.badlogic.gdx.Gdx;

public class Player{
	
	public enum dfType {dfShield, dfMirror, dfNone};
	
	private String _id;
	
	private Grid _grid;
	private int _points, _bank;
	private boolean _mirror, _shield; // inventory/defences
	private int _mCount, _sCount; // number of mirrors, shields. Future feature...
	
	public Player(String uid){
		_grid = new Grid();
		_id = uid;
		reset();
	}
	
	public Player(){
		_grid = new Grid();
		_id = "You";
		reset();
	}
	
	public String getID(){
		return _id;
	}
	
	public void setID(String name){
		_id = name;
	}
	
	public Grid getGrid(){
		return _grid;
	}
	
	public int getPoints(){
		return _points;
	}
	
	public void setPoints(int num){
		_points = num;
	}
	
	public void addPoints(int num){
		_points += num;
	}
	
	public int getBank(){
		return _bank;
	}
	
	public void bankPoints(){
		_bank = _points;
		_points = 0;
	}
	
	public void addBonus(int bonus){
		_points += bonus;
	}
	
	public int getScore(){
		return _points + _bank;
	}
	
	public Grid.sqType getType(int index){
		return _grid.getType(index);
	}
	
	public int getShieldCount(){
		return _sCount;
	}
	
	public int getMirrorCount(){
		return _mCount;
	}
	
	public void reset(){
		_grid.generate();
		_points = 0;
		_bank = 0;
		_mirror = false;
		_shield = false;
		_mCount = 0;
		_sCount = 0;
	}
	
	public boolean hasMirror(){
		return _mirror;
	}
	
	public boolean hasShield(){
		return _shield;
	}
	
	public boolean hasDefence(){
		return (_shield || _mirror);
	}
	
	public void useShield(){
		if (_sCount > 0) _sCount--;
		if (_sCount > 0) _shield = true;
		else _shield = false;
	}
	
	public void useMirror(){
		if (_mCount > 0) _mCount--;
		if (_mCount > 0) _mirror = true;
		else _mirror = false;
	}
	
	public void useDefence(Player.dfType defence){
		if (defence == Player.dfType.dfShield) useShield();
		if (defence == Player.dfType.dfMirror) useMirror();
	}
	
	public boolean canDefend(int index){
		return canDefend(_grid.getType(index));
	}
	
	public boolean canDefend(Grid.sqType type){
		if (Grid.isAttack(type)){
			if (_shield) return true;
			if (_mirror && type != Grid.sqType.sqSwap) return true;
		}
		return false;
	}
	
	public boolean canMirror(Grid.sqType type){
		return (_mirror && Grid.isAttack(type) && type != Grid.sqType.sqSwap);
	}
	
	public boolean canMirror(int index){
		return canMirror(_grid.getType(index));
	}
	
	public ArrayList<dfType> getDefences(){
		ArrayList<dfType> temp = new ArrayList<dfType>();
		if (_shield) temp.add(dfType.dfShield);
		if (_mirror) temp.add(dfType.dfMirror);
		if (temp.size() == 0) temp.add(dfType.dfNone);
		return temp;
	}
	
	public ArrayList<dfType> getDefences(Grid.sqType type){
		ArrayList<dfType> temp = new ArrayList<dfType>();
		if (Grid.isAttack(type)){
			if (_shield) temp.add(dfType.dfShield);
			if (_mirror && type != Grid.sqType.sqSwap) temp.add(dfType.dfMirror);
		}
		if (temp.size() == 0) temp.add(dfType.dfNone);
		return temp;
	}
	
	public ArrayList<dfType> getDefences(int index){
		return getDefences(_grid.getType(index));
	}
	
	public void collect(int index){
		collect(_grid.getType(index));
		_grid.empty(index);
	}
	
	public void collect(Grid.sqType item){
		switch(item){
		case sq200:
			_points += 200;
			break;
		case sq1000:
			_points += 1000;
			break;
		case sq3000:
			_points += 3000;
			break;
		case sq5000:
			_points += 5000;
			break;
		case sqBank:
			_bank = _points;
			_points = 0;
			break;
		case sqBomb:
			_points = 0;
			break;
		case sqDouble:
			_points = 2*_points;
			break;
		case sqMirror:
			_mirror = true;
			_mCount++;
			break;
		case sqShield:
			_shield = true;
			_sCount++;
			break;
		// Specials
		case sq10000:
			_points += 10000;
			break;
		case sqHalf:
			_points = _points/2;
			break;
		default:
			break;
		}
	}
	
	public void completeAttack(int points, Grid.sqType type, Player.dfType defence){
		// Perform the target's side of an attack
		
		// Use shield
		if (defence == Player.dfType.dfShield) {
			useShield();
			return;
		}
		
		// Use mirror
		if (defence == Player.dfType.dfMirror){
			if (type == Grid.sqType.sqRob) addPoints(points);
			useMirror();
			return;
		}
		
		// Don't defend
		switch(type){
		case sqRob:
		case sqKill:
			setPoints(0);
			break;
		case sqSwap:
			setPoints(points);
			break;
		case sqGift:
			addPoints(1000);
			break;
		default:
			break;
		}
	}
	
	public void completeAttack(InteractionData data){
		completeAttack(data.getSourcePoints(), data.getType(), data.getDefenceType());
	}
	
	public void completeResponse(int points, Grid.sqType type, Player.dfType defence){
		// Perform attacker's side of attack (after target has chosen defence)
		
		// Target used shield
		if (defence == Player.dfType.dfShield) return;
		
		// Target used mirror
		if (defence == Player.dfType.dfMirror) {
			if (type == Grid.sqType.sqRob || type == Grid.sqType.sqKill) setPoints(0);
			return;
		}
		
		// Target didn't defend
		switch(type){
		case sqRob:
			addPoints(points);
			break;
		case sqSwap:
			setPoints(points);
			break;
		default:
			return;
		}
	}
	
	public void completeResponse(InteractionData response){
		completeResponse(response.getTargetPoints(), response.getType(), 
				response.getDefenceType());
	}
	
}
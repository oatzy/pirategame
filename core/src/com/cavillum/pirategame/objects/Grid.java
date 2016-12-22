package com.cavillum.pirategame.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.cavillum.pirategame.PirateGame;

public class Grid {
	public enum sqType {sqEmpty, sqRob, sqKill, sqGift, sqSwap, sqChoose, 
		sqShield, sqMirror, sqBomb, sqDouble, sqBank, sqPeek, 
		sq200, sq1000, sq3000, sq5000, 
		sq10000, sqSkull, sqHalf, sqReveal, sqShell};
		
	private static List<sqType> _special = new ArrayList<sqType>();
	
	private sqType[] _grid;
	
	public Grid(){
		_grid = new sqType[49];
		
		_special.add(sqType.sq10000);
		_special.add(sqType.sqSkull);
		_special.add(sqType.sqHalf);
	}
	
	public sqType getType(int index){
		if (inRange(index)){
			return _grid[index];
		}
		else return null;
	}
	
	public void setType(int index, sqType type){
		if (inRange(index)) _grid[index] = type;
	}
	
	public int indexOf(sqType type){
		for (int i=0; i<49; i++){
			if (_grid[i] == type) return i;
		}
		return -1;
	}
	
	public void empty(int index){
		if (inRange(index)) _grid[index] = sqType.sqEmpty;
	}
	
	public void empty(){
		for (int i=0; i<49; i++) _grid[i] = sqType.sqEmpty;
	}
	
	public boolean isEmpty(int index){
		if (inRange(index)) return _grid[index] == sqType.sqEmpty;
		return false;
	}
	
	public boolean isEmpty(){
		for (int i=0; i<49; i++){
			if (!isEmpty(i)) return false; 
		}
		return true;
	}
	
	public int itemsLeft(){
		int temp = 0;
		for (int i=0; i<49; i++){
			if (_grid[i] != sqType.sqEmpty) temp++;
		}
		return temp;
	}
	
	public boolean inRange(int index){
		return (index>=0 && index<49);
	}
	
	public boolean isAttack(int index){
		return isInteraction(index) && _grid[index] != sqType.sqGift;
	}
	
	public static boolean isAttack(Grid.sqType type){
		return isInteraction(type) && type != sqType.sqGift;
	}
	
	public boolean isInteraction(int index){
		if (inRange(index)) return isInteraction(_grid[index]);
		return false;
	}
	
	public static boolean isInteraction(Grid.sqType type){
		return (type == sqType.sqGift) || (type == sqType.sqKill) || (type == sqType.sqRob) ||
				(type == sqType.sqSwap) || (type == sqType.sqPeek);
	}
	
	public static boolean isSpecial(Grid.sqType type){
		return _special.contains(type);
	}
	
	public static List<Grid.sqType> getSpecialItems(){
		return _special;
	}
	
	public static sqType getRandomSpecial(){
		return _special.get(MathUtils.random(_special.size()-1));
	}
	
	public int getRandomIndex(){
		if (isEmpty()) return -1; // safety check
		int index;
		do {
			index = MathUtils.random(48);
		} while (isEmpty(index));
		return index;
	}
	
	public void swap(int index1, int index2){
		if (inRange(index1) && inRange(index2)){
			sqType temp = _grid[index1];
			_grid[index1] = _grid[index2];
			_grid[index2] = temp;
		}
	}
	
	public void shuffle(){
		// Shuffle the boards (Fisher-Yates)
		
		int index;
		Random random = new Random();
		for (int i = 48; i > 0; i--)
		{
			index = random.nextInt(i + 1);
			swap(i, index);
		}
	}
	
	public void generate(){
		
		// Populate the board
		
		/*for (int i=0; i<25; i++){
			_grid[i] = sqType.sq200;
		}
		for (int i=0; i<10; i++){
			_grid[i+25] = sqType.sq1000;
		}
		_grid[35] = sqType.sq3000;
		_grid[36] = sqType.sq3000;
		_grid[37] = sqType.sq5000;
		int i = 37;
		for (sqType s : sqType.values()){
			if (i>37 && i<49) _grid[i] = s;
			i++;
		}*/
		
		_grid = PirateGame.levels.buildGrid();
		
		shuffle(); //randomise the board
	}
	
	public void generate(boolean isAI){
		if (isAI){
			_grid = PirateGame.levels.buildAiGrid();
			shuffle();
		} else generate();
	}
}

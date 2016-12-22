package com.cavillum.pirategame.objects;

import java.util.Random;

import com.badlogic.gdx.math.MathUtils;

public class Grid {
	public enum sqType {sqEmpty, sqRob, sqKill, sqGift, sqSwap, sqChoose, 
		sqShield, sqMirror, sqBomb, sqDouble, sqBank, sqPeek, 
		sq200, sq1000, sq3000, sq5000};
	
	private sqType[] _grid;
	
	public Grid(){
		_grid = new sqType[49];
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
	
	public boolean isAttack(Grid.sqType type){
		return isInteraction(type) && type != sqType.sqGift;
	}
	
	public boolean isInteraction(int index){
		if (inRange(index)) return isInteraction(_grid[index]);
		return false;
	}
	
	public boolean isInteraction(Grid.sqType type){
		return (type == sqType.sqGift) || (type == sqType.sqKill) || (type == sqType.sqRob) ||
				(type == sqType.sqSwap) || (type == sqType.sqPeek);
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
		
		for (int i=0; i<25; i++){
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
		}
		
		shuffle(); //randomise the board
	}
}

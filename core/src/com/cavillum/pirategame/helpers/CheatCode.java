package com.cavillum.pirategame.helpers;

import com.cavillum.pirategame.PirateGame;

public class CheatCode {
	
	private int index = 0;
	private Integer[] _sequence = {3,3,45,45,21,27,21,27};
	private int _menu = 0;
	
	public boolean cheat(int press){
		if (press == _sequence[index]) index++;
		else if (index == 2 && press == _sequence[1]) return false;
		else index = 0;
		if (index == _sequence.length){
			index = 0;
			return true;
		}
		return false;
	}
	
	public boolean isMenuUnlocked(){
		return (_menu == 2);
	}
	
	public boolean secretMenu(int x, int y){
		if (isWinsClicked(x, y) && _menu < 2) _menu = 1;
		if (isPointsClicked(x,y) && _menu == 1) _menu = 2;
		return (_menu == 2);
	}
	
	public boolean isWinsClicked(int x, int y){
		int x0 = PirateGame.layout.width/2-310;
		int y0 = PirateGame.layout.height-200+(PirateGame.layout.isTablet() ? 55 : 0);
		return (x>x0 && x<x0+175 && y>y0 && y<y0+80);
	}
	
	public boolean isPointsClicked(int x, int y){
		int x0 = PirateGame.layout.width/2+50;
		int y0 = PirateGame.layout.height-200+(PirateGame.layout.isTablet() ? 55 : 0);
		return (x>x0 && x<x0+175 && y>y0 && y<y0+80);
	}
	
}

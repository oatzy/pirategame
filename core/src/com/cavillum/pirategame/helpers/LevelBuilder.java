package com.cavillum.pirategame.helpers;

import com.badlogic.gdx.math.MathUtils;
import com.cavillum.pirategame.objects.Grid;
import com.cavillum.pirategame.objects.Grid.sqType;

public class LevelBuilder {
	
	public enum GameType {Standard, Buried, Knockout, Choose, Omniscient, Multiplayer};
	
	private GameType _type = GameType.Standard;
	
	public LevelBuilder(){
		
	}
	
	public GameType getGameType(){
		return _type;
	}
	
	public void setGameType(GameType type){
		_type = type;
	}
	
	public boolean isStandard(){
		return _type == GameType.Standard;
	}
	
	public boolean isBuried(){
		return _type == GameType.Buried;
	}
	
	public boolean isKnockout(){
		return _type == GameType.Knockout;
	}
	
	public boolean isChoose(){
		return _type == GameType.Choose;
	}
	
	public boolean isMultiplayer(){
		return _type == GameType.Multiplayer;
	}
	
	public Grid.sqType[] buildGrid(){
		if (_type == GameType.Buried)
			return buildBuriedGrid();
		if (_type == GameType.Choose)
			return buildChooseGrid();
		return buildStandardGrid();
	}
	
	public Grid.sqType[] buildAiGrid() {
		if (_type == GameType.Buried)
			return buildAiBuriedGrid();
		else return buildGrid();
	}
	
	public Grid.sqType[] buildStandardGrid(){
		Grid.sqType[] grid = new Grid.sqType[49];
		for (int i=0; i<25; i++){
			grid[i] = sqType.sq200;
		}
		for (int i=0; i<10; i++){
			grid[i+25] = sqType.sq1000;
		}
		grid[35] = sqType.sq3000;
		grid[36] = sqType.sq3000;
		grid[37] = sqType.sq5000;
		int i = 37;
		for (sqType s : sqType.values()){
			if (i>37 && i<49) grid[i] = s;
			i++;
		}
		return grid;
	}
	
	public Grid.sqType[] buildBuriedGrid(){
		// TODO - add mystery items
		Grid.sqType[] grid = new Grid.sqType[49];
		for (int i=0; i<25; i++){
			grid[i] = sqType.sq200;
		}
		for (int i=0; i<10; i++){
			grid[i+25] = sqType.sq1000;
		}
		grid[35] = sqType.sq3000;
		grid[36] = sqType.sq3000;
		grid[37] = sqType.sq5000;
		int i = 37;
		for (sqType s : sqType.values()){
			if (s == sqType.sqChoose) {
				if (MathUtils.random()<0.3)	s = sqType.sq3000;
				else s = Grid.getRandomSpecial();
			}
			if (i>37 && i<49) grid[i] = s;
			i++;
		}
		return grid;
	}
	
	public Grid.sqType[] buildAiBuriedGrid(){
		Grid.sqType[] grid = new Grid.sqType[49];
		for (int i=0; i<25; i++){
			grid[i] = sqType.sq200;
		}
		for (int i=0; i<10; i++){
			grid[i+25] = sqType.sq1000;
		}
		grid[35] = sqType.sq3000;
		grid[36] = sqType.sq3000;
		grid[37] = sqType.sq5000;
		int i = 37;
		for (sqType s : sqType.values()){
			if (s == sqType.sqChoose) s = sqType.sq3000;
			if (i>37 && i<49) grid[i] = s;
			i++;
		}
		return grid;
	}
	
	public Grid.sqType[] buildChooseGrid(){
		Grid.sqType[] grid = new Grid.sqType[49];
		for (int i=0; i<25; i++){
			grid[i] = sqType.sq200;
		}
		for (int i=0; i<10; i++){
			grid[i+25] = sqType.sq1000;
		}
		grid[35] = sqType.sq3000;
		grid[36] = sqType.sq3000;
		grid[37] = sqType.sq5000;
		int i = 37;
		for (sqType s : sqType.values()){
			if (s == sqType.sqChoose || s == sqType.sqBomb) s = sqType.sq3000;
			if (i>37 && i<49) grid[i] = s;
			i++;
		}
		return grid;
	}

}

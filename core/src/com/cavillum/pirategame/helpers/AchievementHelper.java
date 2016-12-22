package com.cavillum.pirategame.helpers;

import java.util.ArrayList;

import com.cavillum.pirategame.PirateGame;
import com.cavillum.pirategame.data.InteractionData;
import com.cavillum.pirategame.objects.Grid;
import com.cavillum.pirategame.objects.Player;

public class AchievementHelper {
	
	private String _local;
	private ArrayList<String> _gifters;
	
	public AchievementHelper(String local){
		_local = local;
		_gifters = new ArrayList<String>();
	}
	
	public void reset(){
		_gifters.clear();
	}
	
	public void update(InteractionData data){
		
		// Backstabber Achievement
		if (data.getTarget() == _local && data.getType()==Grid.sqType.sqGift){
			addGifter(data.getSource());
		}
		if (data.getSource() == _local && _gifters.contains(data.getTarget()) &&
				(data.getType()==Grid.sqType.sqRob || data.getType() == Grid.sqType.sqKill)){
			PirateGame.googleServices.unlockBackstabber();
		}
		
		// Rob Achievements
		if (data.getType() == Grid.sqType.sqRob){
			if (data.getSource() == _local && data.getDefenceType() == Player.dfType.dfNone)
				PirateGame.googleServices.incrementRobbed(data.getTargetPoints());
			else if (data.getTarget() == _local && data.getDefenceType() == Player.dfType.dfMirror)
				PirateGame.googleServices.incrementRobbed(data.getSourcePoints());
		}
		
		// Blue Shell Achievement
		if ((data.getType() == Grid.sqType.sqRob || data.getType() == Grid.sqType.sqKill)
				&& ((data.getSource() == _local && data.getDefenceType() == Player.dfType.dfMirror
				&& data.getSourcePoints() > 20000)
				|| (data.getTarget() == _local && data.getDefenceType() == Player.dfType.dfNone
				&& data.getTargetPoints() > 20000))){
			PirateGame.googleServices.unlockBlueShell();
		}
		
		// Switcheroo
		if(data.getType() == Grid.sqType.sqSwap && data.getDefenceType() == Player.dfType.dfNone && (
				(data.getSource() == _local && data.getSourcePoints() == 0 
				&& data.getTargetPoints() > 25000)
				|| (data.getTarget() == _local && data.getTargetPoints() == 0
				&& data.getSourcePoints() > 25000))) PirateGame.googleServices.unlockSwitcheroo();
		
		// Kills
		if(data.getType() == Grid.sqType.sqKill && (
				(data.getSource() == _local && data.getDefenceType() == Player.dfType.dfNone)
				|| (data.getTarget() == _local && data.getDefenceType() == Player.dfType.dfMirror))){
			PirateGame.googleServices.incrementKills();
			PirateGame.save.incrementKills();
		}
		
		// Played Type
		PirateGame.save.setPlayed(PirateGame.levels.getGameType());
	}
	
	public void addGifter(String player){
		_gifters.add(player);
	}
	
	

}

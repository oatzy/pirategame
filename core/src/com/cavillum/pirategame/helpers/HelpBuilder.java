package com.cavillum.pirategame.helpers;

import com.cavillum.pirategame.PirateGame;
import com.cavillum.pirategame.objects.Grid;
import com.cavillum.pirategame.objects.Grid.sqType;
import com.cavillum.pirategame.ui.GameScene.State;
import com.cavillum.pirategame.ui.GameStages;

public class HelpBuilder {
	
	public static String buildStateHelp(State state){
		switch(state){
		case ArrangeItems:
			return "Arrange the items however you like by dragging and dropping."
					+ " When you're happy with your layout, tap 'Start' to play the game.";
		case CollectItem:
			if (PirateGame.levels.getGameType() == LevelBuilder.GameType.Buried)
				return "Tap the highlighted item to collect it.";
			return "The game has picked a random item, highlighted in blue and pulsing. "
					+ "Tap the item to collect it.";
		case Buried:
			return "Tap a sand mound to dig for treasure."; 
		case Select:
			return "Tap any item to collect it.";
		case ChooseNext:
			return "Tap the item that you want in the next round.";
		case Reveal:
			return "Tap anywhere to continue.";
		case GameOver:
			return "Game Over!";
		default:
			return "";
		}
	}
	
	public static String buildPopupHelp(GameStages.Popup type){
		switch(type){
		case History:
			return "History shows you the last 3 notifications.";
		case Players:
			return "Players shows you a list of the other players in the current game.";
		case Attack:
			return "Tap the name of the player you want to use your attack on.";
		case Defend:
			return "Another player is trying to attack you. Tap the defensive item you want to use"
					+ " or tap pass to save your defences.";
		case Notification:
			return "Notifications tell you what other players are doing.";
		default:
			return "";
		}
	}
	
	public static String buildItemHelp(Grid.sqType item){
		switch(item){
		case sq200:
			return "Bronze '200' Coin - adds 200 to your POINTS.";
		case sq1000:
			return "Silver '1000' Coin - adds 1,000 to your POINTS.";
		case sq3000:
			return "Gold '3000' Coin - adds 3,000 to your POINTS.";
		case sq5000:
			return "Ruby - adds 5,000 to your POINTS.";
		case sqDouble:
			return "x2 - doubles your POINTS";
		case sqBomb:
			return "Bomb - blows you up, setting your POINTS to 0. The CHEST is unaffected.";
		case sqBank:
			return "Treasure Chest - moves any POINTS you have to the 'CHEST'.";
		case sqChoose:
			return "?? - lets you choose the item you want next, from those still in your grid.";
		case sqShield:
			return "Shield - lets you protect yourself from an attack";
		case sqMirror:
			return "Mirror - lets you reflect an attack back on the attacker.";
		case sqKill:
			return "Cannon - lets you kill another player. This sets their POINTS to 0.";
		case sqRob:
			return "Dagger - lets you rob another player. This gives you their POINTS.";
		case sqSwap:
			return "Arrows - lets you swap POINTS with another player.";
		case sqPeek:
			return "Spyglass - lets you peak at another player's POINTS.";
		case sqGift:
			return "Gift - lets you give a gift of 1,000 points to any other player.";
		// Specials
		case sq10000:
			return "Gold Crown - adds 10,000 to your POINTS.";
		case sqHalf:
			return "1/2 - Halves your POINTS.";
		case sqSkull:
			return "Skull and Crossbones - kills all other players.";
		case sqReveal:
			return "Treasure Map - lets you (temporarily) see what's buried.";
		case sqShell:
			return "Blue Shell - kills whoever is in the lead (even if it's you).";
		default:
			return "";
		}
	}
	
	public static String allItemsHelp(){
		String temp = "";
		for (sqType type : sqType.values())
			if (type != sqType.sqEmpty && !Grid.isSpecial(type)) 
				temp += buildItemHelp(type)+"\n";
		return temp;
	}
	
	public static String buildHints(GameStages.Popup pop){
		if (pop == GameStages.Popup.Attack){
			return "Check the notification history (icon in the top left corner). "
					+ "This might give you an idea of who has lots of points.";
		}
		return "";
	}

}

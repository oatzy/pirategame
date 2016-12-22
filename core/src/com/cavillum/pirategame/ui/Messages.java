package com.cavillum.pirategame.ui;

import com.cavillum.pirategame.data.InteractionData;
import com.cavillum.pirategame.objects.Grid;
import com.cavillum.pirategame.objects.Player;

public class Messages {
	
	public static String build(String attacker, String defender, int apoints, int dpoints, Grid.sqType type){
		// when a (non-local) player attacks another
		switch(type){
		case sqKill:
			return attacker+" killed "+defender;
		case sqGift:
			if (defender == "You")
				return attacker+" gifted 1000 points to "+defender;
			else return "";
		case sqRob:
			return attacker+" robbed "+dpoints+" points from "+defender;
		case sqSwap:
			return attacker+" swapped "+apoints+" points for "+defender+"'s "+dpoints;
		case sqPeek:
			if (attacker == "You") return defender+" has "+dpoints+" points";
			else return "";
		case sqSkull:
			return "You killed everyone!";
		case sqShell:
			return defender+" was killed by the blue shell";
		default:
			return "";
		}
	}
	
	public static String build(String attacker, String defender, int apoints, int dpoints,
			Grid.sqType type, Player.dfType defence){
		// When an non-local player attacks a target who uses a defence
		switch(defence){
		case dfNone:
			return build(attacker, defender, apoints, dpoints, type);
		case dfShield:
			return shield(attacker, defender, type);
		case dfMirror:
			return mirror(attacker, defender, apoints, type);
		default:
			return "";
		}
	}
	
	public static String build(InteractionData data){
		return build(data.getSource(), data.getTarget(), data.getSourcePoints(), data.getTargetPoints(),
				data.getType(), data.getDefenceType());
	}
	
	private static String shield(String attacker, String defender, Grid.sqType type){
		// when a non-local player attacks a player who uses a shield
		String temp = attacker+" tried to ";
		switch(type){
		case sqRob:
			return temp+"rob "+defender;
		case sqKill:
			return temp+"kill "+defender;
		case sqSwap:
			return temp+"swap points with "+defender;
		case sqPeek:
			return temp+"peek at "+defender+"'s points";
		default:
			return "";
		}
	}
	
	private static String mirror(String attacker, String defender, int apoints, Grid.sqType type){
		// when a non-local player attacks a play who uses a mirror
		String temp = defender+" used their mirror to ";
		switch(type){
		case sqRob:
			return temp+"rob "+apoints+" from "+attacker;
		case sqKill:
			return temp+"kill "+attacker;
		case sqPeek:
			return temp+"peek at "+defender+"'s points";
		default:
			return "";
		}
	}
	
	public static String buildLocalAttack(String attacker, int apoints, int dpoints, Grid.sqType type){
		// Notification for when a player successfully attacks the local player
		switch(type){
		case sqKill:
			return attacker+" killed you";
		case sqGift:
			return attacker+" gifted 1000 points to you";
		case sqRob:
			return attacker+" robbed "+dpoints+" points from you";
		case sqSwap:
			return attacker+" swapped "+apoints+" points for your "+dpoints;
		case sqShell:
			return "You were killed by the blue shell";
		default:
			return "";
		}
	}
	
	public static String buildLocalDefence(InteractionData data){
		// when a player attacks the local player, who uses a defence
		switch(data.getDefenceType()){
		case dfNone:
			return buildLocalAttack(data.getSource(), data.getSourcePoints(), 
					data.getTargetPoints(), data.getType());
		case dfMirror:
			return build("You", data.getSource(), data.getTargetPoints(),
					data.getSourcePoints(), data.getType());
		case dfShield:
		default:
			return "";
		}
	}
	
	public static String buildOpponentDefence(String defender, int apoints, int dpoints, Grid.sqType type, Player.dfType defence){
		// when local player attacks a player who uses a defence
		switch(defence){
		case dfShield:
			return defender+" used their shield";
		case dfMirror:
			return defender+" used their mirror";
		case dfNone:
			// disable this condition for more notifications
			if (type == Grid.sqType.sqPeek || type == Grid.sqType.sqShell 
					|| type == Grid.sqType.sqSkull)	
				return build("You", defender, apoints, dpoints, type);
		default:
			return "";
		}
	}
	
	public static String buildOpponentDefence(InteractionData data){
		return buildOpponentDefence(data.getTarget(), data.getSourcePoints(), 
				data.getTargetPoints(), data.getType(), data.getDefenceType());
	}
	
	public static String buildDefenceMessage(String attacker, Grid.sqType type){
		// Notification text is defence selector
		switch(type){
		case sqKill:
			return attacker+" is trying to kill you!";
		case sqRob:
			return attacker+" is trying to rob you!";
		case sqSwap:
			return attacker+" is trying to swap points with you!";
		case sqPeek:
			return attacker+" is trying to peek at your points!";
		default:
			return "";
		}
	}
	
	public static String buildAttackQuestion(Grid.sqType type){
		// Notfication text is attack target selector
		String message = "Who do you want to";
		switch(type){
		case sqKill:
			return message+" kill?";
		case sqRob:
			return message+" rob?";
		case sqSwap:
			return message+" swap points with?";
		case sqGift:
			return message+" gift 1000 points to?";
		case sqPeek:
			return "Whose points do you want to peek at?";
		default:
			return "";
		}
	}

}

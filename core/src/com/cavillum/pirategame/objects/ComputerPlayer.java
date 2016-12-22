package com.cavillum.pirategame.objects;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.cavillum.pirategame.data.InteractionData;

public class ComputerPlayer extends Player{
	
	// TODO - take round into account in AI
	
	private boolean _hardMode = false;
	private ArrayList<String> _hitlist;
	private ArrayList<String> _avoidList;
	
	public ComputerPlayer(){
		super();
		_hitlist = new ArrayList<String>();
		_avoidList = new ArrayList<String>();
	}
	
	public ComputerPlayer(String id){
		super(id);
		_hitlist = new ArrayList<String>();
		_avoidList = new ArrayList<String>();
	}
	
	public ComputerPlayer(String id, boolean hard){
		this(id);
		_hardMode = hard;
	}
	
	public void reset(){
		super.reset();
		getGrid().generate(true);
		if (_hitlist != null) _hitlist.clear();
		if (_avoidList != null) _avoidList.clear();
	}
	
	public boolean isHardMode(){
		return _hardMode;
	}
	
	public InteractionData attack(ArrayList<String> opponents, Grid.sqType type){
		String target = new String();
		
		if (_hardMode){
			// Hard Mode - choose more strategically
			target = chooseTarget(opponents, type);
		}
		
		else {
			// Normal Mode - choose random target
			target = opponents.get(MathUtils.random(opponents.size()-1));
		}
		
		return new InteractionData(getID(), target, getPoints(), type);
	}
	
	public InteractionData defend(InteractionData data){
		// pick defence type
		ArrayList<Player.dfType> defences = getDefences(data.getType());
		defences.add(Player.dfType.dfNone); // test - so random players can opt to not defend
		Player.dfType defence = Player.dfType.dfNone;
		
		if (_hardMode && defences.get(0) != Player.dfType.dfNone){
			// Hard Mode - choose more strategically
			defence = chooseDefence(data.getSource(), data.getType(), defences);
		}
		else {
			// Normal Mode - chose random defence
			defence = defences.get(MathUtils.random(defences.size()-1));
		}
		
		// update data object
		data.setTargetData(getID(), getPoints(), defence);
		
		// complete processing
		completeAttack(data.getSourcePoints(), data.getType(), defence);
		
		// return updated data
		return data;
	}
	
	public InteractionData doSquareAction(ArrayList<String> opponents, Grid.sqType type){
		
		// Interaction
		if (Grid.isInteraction(type)) return attack(opponents, type);
		
		// Choose next - crafty work around for return value
		if (type == Grid.sqType.sqChoose) {
			if (_hardMode) {
				return new InteractionData(getID(), null, chooseNext(), type);
			}
			return new InteractionData(getID(), null, getGrid().getRandomIndex(), type);
		}
		
		// collect
		collect(type);
		return new InteractionData();
	}
	
	public InteractionData doSquareAction(ArrayList<String> opponents, int index){
		Grid.sqType type = getType(index);
		getGrid().empty(index);
		return doSquareAction(opponents, type);
	}
	
	public String chooseTarget(ArrayList<String> opponents, Grid.sqType type){
		
		if (type == Grid.sqType.sqGift){
			// give points to someone with fewer points (less competition for top)
			if (_avoidList.size() != 0){
				return _avoidList.get(MathUtils.random(_avoidList.size()-1));
			}
		}
		
		String target = opponents.get(MathUtils.random(opponents.size()-1));
		
		if (type == Grid.sqType.sqPeek){
			if (_hitlist.size()+_avoidList.size() < opponents.size()){
				do {
					target = opponents.get(MathUtils.random(opponents.size()-1));
				} while (_hitlist.contains(target) || _avoidList.contains(target));
				return target;
			}
		}
		
		if (_hitlist.size() != 0){
			// Choose target from hitlist
			return _hitlist.get(MathUtils.random(_hitlist.size()-1));
		}
		
		// hopefully this prevents infinite loops
		if (_avoidList.size() != 0 && _avoidList.size() < opponents.size()){
			// Choose random not on avoid list
			do {
				target = opponents.get(MathUtils.random(opponents.size()-1));
			} while (_avoidList.indexOf(target) != -1);
			return target;
		}
		
		return target;
	}
	
	public Player.dfType chooseDefence(String attacker, Grid.sqType type, 
			ArrayList<Player.dfType> defences){
		
		// If attacker is on hitlist
		if (_hitlist.indexOf(attacker) != -1){
			switch(type){
			case sqSwap:
				// if have lots of points - defend
				if (getPoints() > 30000) return Player.dfType.dfShield;
				
				// else will hopefully get the better end of a swap
				return Player.dfType.dfNone;
				
			case sqRob:	
				// Prefer mirror if possible
				if (hasMirror()) return Player.dfType.dfMirror;
				
				// don't shield if not many points
				if (getPoints()<6000) return Player.dfType.dfNone;
				
				// if in this function and doesn't have mirror, necessarily has shield
				return Player.dfType.dfShield;
				
			case sqKill:
				// Prefer shield if possible
				if (hasShield()) {
					if (getPoints() > 8000) return Player.dfType.dfShield;
				}
				
				if (hasMirror()) return Player.dfType.dfMirror;
				
				return Player.dfType.dfNone;
			
			case sqPeek:
				// not worth mirroring hitlist - shield if have lots of points
				if (hasShield() && getPoints() > 15000) return Player.dfType.dfShield;
				
				return Player.dfType.dfNone;

			default:
				break;
			}
		}
		
		// Attacker not on hitlist and not many points - not worth defending 
		if (getPoints() < 10000) return Player.dfType.dfNone;
		
		// Else - pick random and hope for the best
		return defences.get(MathUtils.random(defences.size()-1));
	}
	
	public int chooseNext(){
		int index = -1;
		if (getPoints() > 10000){
			if ((index = getGrid().indexOf(Grid.sqType.sqBank)) != -1) return index;
			if ((index = getGrid().indexOf(Grid.sqType.sqShield)) != -1) return index;
			if ((index = getGrid().indexOf(Grid.sqType.sqMirror)) != -1) return index;
		}
		if (getPoints() < 5000){
			if ((index = getGrid().indexOf(Grid.sqType.sqBomb)) != -1) return index;
			if ((index = getGrid().indexOf(Grid.sqType.sqSwap)) != -1) return index;
			if ((index = getGrid().indexOf(Grid.sqType.sqRob)) != -1) return index;
			if ((index = getGrid().indexOf(Grid.sqType.sqKill)) != -1) return index;
		}
		return getGrid().getRandomIndex();
	}
	
	public void processEvent(InteractionData data){
		// take data from opponent interactions and build _hitlist
		// must only use data that would be available to a human player(!)
		// TODO - consider when a player has used their defences
		
		if (!Grid.isInteraction(data.getType())) return;
		if (data.getDefenceType() == Player.dfType.dfShield) return;
		
		ArrayList<String> hit = new ArrayList<String>();
		ArrayList<String> avoid = new ArrayList<String>();
		
		if (data.getDefenceType() == Player.dfType.dfMirror){
			// swap source and target
			// TODO - this is problematic - alters the actually turn data!!
			// temp fix by creating clone function
			String source = data.getTarget();
			int spoints = data.getTargetPoints();
			data.setTarget(data.getSource());
			data.setTargetPoints(data.getSourcePoints());
			data.setSource(source);
			data.setSourcePoints(spoints);
		}
		
		switch(data.getType()){
		case sqPeek:
			if (data.getSource() == getID()){ // only the attacker can see the peeked points
				if (data.getTargetPoints() > 10000) hit.add(data.getTarget());
				if (data.getTargetPoints() < 2000) avoid.add(data.getTarget());
			}
			break;
		case sqKill:
			avoid.add(data.getTarget());
			break;
		case sqRob:
			avoid.add(data.getTarget());
			if (data.getTargetPoints() > 5000) hit.add(data.getSource());
			break;
		case sqSwap:
			if (data.getTargetPoints() > 10000) hit.add(data.getSource());
			if (data.getTargetPoints() < 2000) avoid.add(data.getSource());
			if (data.getSourcePoints() > 10000) hit.add(data.getTarget());
			if (data.getSourcePoints() < 2000) avoid.add(data.getTarget());
			break;
		case sqGift:
			// optional - be nice to people who give gifts
			if (data.getTarget() == getID()) avoid.add(data.getSource());
			break;
		default:
			break;
		}
		
		// Clean up
		sanitiseHitlist(hit, avoid);
	}	
	
	public void sanitiseHitlist(ArrayList<String> hit, ArrayList<String> avoid){
		// hopefully there are no overlaps
		
		// update hits
		for (String p : hit){
			if (_hitlist.indexOf(p) == -1 && p != getID()){
				_hitlist.add(p);
			}
			if (_avoidList.indexOf(p) != -1) _avoidList.remove(p);
		}
		// update avoids
		for (String p : avoid){
			if (_avoidList.indexOf(p) == -1 && p != getID()){
				_avoidList.add(p);
			}
			if (_hitlist.indexOf(p) != -1) _hitlist.remove(p);
		}
	}
	
	// Special
	public void instantKill(){
		// TODO - allow defence?
		setPoints(0);
	}
}

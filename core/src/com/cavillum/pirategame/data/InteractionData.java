package com.cavillum.pirategame.data;

import com.cavillum.pirategame.objects.Grid;
import com.cavillum.pirategame.objects.Player;
import com.cavillum.pirategame.objects.Grid.sqType;
import com.cavillum.pirategame.objects.Player.dfType;

public class InteractionData {
	
	// TODO - change to use Score object?
	
	private String _source;
	private String _target;
	private Grid.sqType _type;
	private int _sourcePoints;
	private int _targetPoints;
	private Player.dfType _defence;
	
	public InteractionData(String source, String target, int spoints, int tpoints, 
			sqType type, dfType defence){
		_source = source;
		_target = target;
		_sourcePoints = spoints;
		_targetPoints = tpoints;
		_type = type;
		_defence = defence;
	}
	
	public InteractionData(String source, String target, int spoints, sqType type){
		_source = source;
		_target = target;
		_sourcePoints = spoints;
		_type = type;
		_defence = dfType.dfNone;
	}
	
	public InteractionData(String target, int points, sqType type, dfType defence){
		_source = null;
		_target = target;
		_sourcePoints = points;
		_targetPoints = 0;
		_type = type;
		_defence = defence;
	}
	
	public InteractionData(){
		clear();
	}
	
	public void clear(){
		_source = null;
		_target = null;
		_sourcePoints = 0;
		_targetPoints = 0;
		_type = sqType.sqEmpty;
		_defence = dfType.dfNone;
	}
	
	public InteractionData clone(){
		return new InteractionData(_source, _target, _sourcePoints, _targetPoints,
				_type, _defence);
	}
	
	public void setSourceData(String source, int spoints, sqType type){
		_source = source;
		_sourcePoints = spoints;
		_type = type;
	}
	
	public void setTargetData(String target, int tpoints){
		_target = target;
		_targetPoints = tpoints;
	}
	
	public void setTargetData(String target, int tpoints, dfType defence){
		_target = target;
		_targetPoints = tpoints;
		_defence = defence;
	}
	
	public String getSource(){
		return _source;
	}
	
	public String getTarget(){
		return _target;
	}
	
	public int getSourcePoints(){
		return _sourcePoints;
	}
	
	public int getTargetPoints(){
		return _targetPoints;
	}
	
	public sqType getType(){
		return _type;
	}
	
	public dfType getDefenceType(){
		return _defence;
	}
	
	public void setSource(String source){
		_source = source;
	}
	
	public void setTarget(String target){
		_target = target;
	}
	
	public void setSourcePoints(int points){
		_sourcePoints = points;
	}
	
	public void setTargetPoints(int points){
		_targetPoints = points;
	}
	
	public void setType(sqType type){
		_type = type;
	}
	
	public void setType(String type){
		for (sqType s : sqType.values()){
			if (s.toString() == type) {
				_type = s;
				return;
			}
		}
	}
	
	public void setDefence(dfType defence){
		_defence = defence;
	}
	
	public void setDefence(String defence){
		for (dfType d : dfType.values()){
			if (d.toString() == defence) _defence = d;
		}
	}
	
}

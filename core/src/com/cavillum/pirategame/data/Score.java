package com.cavillum.pirategame.data;

public class Score implements Comparable<Score>{
	private int _score;
	private String _name;
	
	public Score(String name, int score){
		_name = name;
		_score = score;
	}
	
	public int getScore(){
		return _score;
	}
	
	public String getName(){
		return _name;
	}
	
	@Override
	public int compareTo(Score o) {
		return _score < o.getScore() ? 1 : _score > o.getScore() ? -1 : 0;
	}

}

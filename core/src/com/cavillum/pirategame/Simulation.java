package com.cavillum.pirategame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cavillum.pirategame.data.InteractionData;
import com.cavillum.pirategame.data.Score;
import com.cavillum.pirategame.objects.ComputerPlayer;
import com.cavillum.pirategame.objects.Grid;
import com.cavillum.pirategame.objects.Queue;

public class Simulation {
	
	private int _turn;
	private int _roundSize;
	private Queue _queue;
	
	private ArrayList<String> _players;
	private ArrayList<ComputerPlayer> _aiPlayers;
	
	private InteractionData _turnData;
	
	private ArrayList<Integer> wins;
	
	List<Score> _scores;
	
	public Simulation(){
		_turn = -1;
		_aiPlayers = new ArrayList<ComputerPlayer>();
		_players = new ArrayList<String>();
		_roundSize = 0;
		_queue = new Queue();
		_queue.generate();
		_turnData = new InteractionData();
		wins = new ArrayList<Integer>();
	}
	
	public void reset(){
		_turn = -1;
		_queue.reset();
		for (ComputerPlayer p : _aiPlayers) p.reset();
		_roundSize = _players.size();
		_turnData.clear();
		_scores = null;
	}
	
	public void addComputerPlayer(ComputerPlayer player){
		_aiPlayers.add(player);
		_players.add(player.getID());
		_roundSize++;
		wins.add(0);
	}
	
	public int getCurrentTurn(){
		return _turn;
	}
	
	public int getCurrentSquare(){
		return _queue.getCurrent();
	}
	
	public int getPlayerCount(){
		return _players.size();
	}
	
	public ArrayList<String> getPlayers(){
		return _players;
	}
	
	public List<Score> getScoreBoard(){
		return _scores;
	}
	
	public InteractionData getData(){
		return _turnData;
	}
	
	public ArrayList<String> getOpponents(String playerID){
		ArrayList<String> temp = new ArrayList<String>();
		for (String p : _players){
			if (p != playerID) temp.add(p);
		}
		return temp;
	}
	
	public ArrayList<String> getCurrentOpponents(){
		return getOpponents(getCurrentPlayer());
	}
	
	public Queue getQueue(){
		return _queue;
	}
	
	public ComputerPlayer getAiPlayerById(String playerID){
		for (ComputerPlayer p : _aiPlayers){
			if (p.getID() == playerID) return p;
		}
		return null;
	}
	
	public String getCurrentPlayer(){
		return _players.get(_turn);
	}
	
	public ComputerPlayer getCurrentAiPlayer(){
		return getAiPlayerById(getCurrentPlayer());
	}
	
	public boolean isCurrentPlayer(String playerID){
		return _players.get(_turn) == playerID;
	}
	
	public boolean isComputerPlayer(String player){
		for (ComputerPlayer p : _aiPlayers){
			if (p.getID() == player) return true;
		}
		return false;
	}
	
	public void endTurn(){
		// tell all players what happened in round
		for (String p : getPlayers()){
			getAiPlayerById(p).processEvent(_turnData.clone());
		}
		iterate();
	}
	
	public void iterate(){
		_turnData.clear();
		_turn++;
		if (_turn >= _roundSize){
			if (_queue.isEnd()) {
				buildScoreBoard();
				return;
			}
			_turn = 0;
			_queue.iterate();
		}
		//Gdx.app.log("Current Player", getCurrentPlayer());
		computerCollect();
	}
	
	public void buildScoreBoard(){
		// create board
		_scores = new ArrayList<Score>();
		for (ComputerPlayer p : _aiPlayers){
			addToScoreBoard(p.getID(), p.getScore());
		}
		// send score to all other players ...
	}
	
	public void addToScoreBoard(Score score){
		// if board not created
		if (_scores == null){
			buildScoreBoard();
		}
		
		// add new score
		_scores.add(score);
		
		// if board is complete, sort and display
		if (_scores.size() == _players.size()){
			Collections.sort(_scores);
			onGameOver();
		}
	}
	
	public void addToScoreBoard(String id, int score){
		addToScoreBoard(new Score(id, score));
	}
	
	public void onGameOver(){
		//System.out.println("Winner "+_scores.get(0).getName());
		//System.out.println("Winning Points "+_scores.get(0).getScore());
	}
	
	public void loop(int loops){
		for (int j=0; j<loops; j++){
			iterate();
			for (int i=0; i<getPlayerCount(); i++){
				if (_players.get(i) == _scores.get(0).getName()) wins.set(i, wins.get(i)+1);
			}
			reset();
		}
		for (int j=0; j<getPlayerCount(); j++){
			System.out.println(_players.get(j)+" : "+wins.get(j));
		}
	}

	
	// Computer Move //
	
	public void computerCollect(){
		if (getCurrentAiPlayer() == null) return;
		
		_turnData = getCurrentAiPlayer().doSquareAction(getCurrentOpponents(),
				getCurrentSquare());
		
		// Collect item
		// is there a better way to handle this?
		if (_turnData.getSource() == null) {
			iterate();
			return;
		}
		
		// Choose Next
		if (_turnData.getType() == Grid.sqType.sqChoose){
			if (_turnData.getSourcePoints() == -1){
				// last move - no squares to choose
				iterate();
			} else {
				// Note - crafty work around, not actually points
				_queue.addNext(_turnData.getSourcePoints());
				endTurn();
			}
			return;
		}
			
		// Attack other (computer) player

		_turnData = getAiPlayerById(_turnData.getTarget()).defend(_turnData);
		getCurrentAiPlayer().completeResponse(_turnData);
		
		endTurn();
	}
	
	// MAIN //
	
	public static void main (String[] arg) {
		Simulation sim = new Simulation();
		sim.addComputerPlayer(new ComputerPlayer("Player 1", true));
		sim.addComputerPlayer(new ComputerPlayer("Player 2"));
		sim.addComputerPlayer(new ComputerPlayer("Player 3"));
		sim.addComputerPlayer(new ComputerPlayer("Player 4"));
		sim.addComputerPlayer(new ComputerPlayer("Player 5"));
		sim.addComputerPlayer(new ComputerPlayer("Player 6"));
		sim.addComputerPlayer(new ComputerPlayer("Player 7"));
		sim.addComputerPlayer(new ComputerPlayer("Player 8"));
		sim.loop(10000);
	}

}

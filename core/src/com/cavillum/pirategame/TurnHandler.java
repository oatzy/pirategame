package com.cavillum.pirategame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cavillum.pirategame.data.InteractionData;
import com.cavillum.pirategame.data.Score;
import com.cavillum.pirategame.objects.ComputerPlayer;
import com.cavillum.pirategame.objects.Grid;
import com.cavillum.pirategame.objects.Player;
import com.cavillum.pirategame.objects.Queue;
import com.cavillum.pirategame.ui.GameScene;
import com.cavillum.pirategame.ui.MessageBuilder;
import com.cavillum.pirategame.ui.GameScene.State;

public class TurnHandler {
	// TODO - make sure the only times a non-local player is explicitly called
	// 		  is in a computer move function
	
	private GameScene _parent;
	
	private int _turn;
	private int _roundSize;
	private Queue _queue;
	
	private Player _localPlayer;
	private String _localID;
	private ArrayList<String> _players;
	private ArrayList<ComputerPlayer> _aiPlayers;
	
	private InteractionData _turnData;
	
	List<Score> _scores;
	
	
	MessageBuilder _messages = new MessageBuilder();
	
	public TurnHandler(GameScene game){
		_parent = game;
		_turn = 0;
		_aiPlayers = new ArrayList<ComputerPlayer>();
		_players = new ArrayList<String>();
		_roundSize = 0;
		_localPlayer = new Player();
		_localID = new String();
		_queue = new Queue();
		_queue.generate();
		_turnData = new InteractionData();
	}
	
	public TurnHandler(GameScene game, Player local, ArrayList<String> players){
		_parent = game;
		_turn = 0;
		_players = players;
		_aiPlayers = new ArrayList<ComputerPlayer>();
		_roundSize = _players.size();
		_localPlayer = local;
		_localID = local.getID();
		_queue = new Queue();
		_queue.generate();
		_turnData = new InteractionData();
	}
	
	public void reset(){
		_turn = 0;
		_queue.reset();
		for (ComputerPlayer p : _aiPlayers){
			p.reset();
		}
		_localPlayer.reset();
		_roundSize = _players.size();
		_turnData.clear();
		_scores = null;
	}
	
	public void addComputerPlayer(ComputerPlayer player){
		_aiPlayers.add(player);
		_players.add(player.getID());
		_roundSize++;
	}
	
	public void setLocalPlayer(Player player){
		_localPlayer = player;
		_localID = player.getID();
		_players.add(_localID);
		_roundSize++;
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
	
	public ArrayList<String> getLocalOpponents(){
		return getOpponents(_localID);
	}
	
	public ArrayList<String> getCurrentOpponents(){
		return getOpponents(getCurrentPlayer());
	}
	
	public Queue getQueue(){
		return _queue;
	}
	
	public Player getLocalPlayer(){
		return _localPlayer;
	}
	
	public String getLocalPlayerId(){
		return _localID;
	}
	
	public boolean isLocalPlayer(String player){
		return player == _localID;
	}
	
	public boolean localIsCurrent(){
		return isCurrentPlayer(_localID);
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
		for (String p : getLocalOpponents()){
			if (isComputerPlayer(p)){
				getAiPlayerById(p).processEvent(_turnData.clone());//problematic, memory waste?
			} else {
				// Network Player ...
				// if not source or target, send update
			}
		}
		
		// show notification (where relevant)
		notification();
	}
	
	public void notification(){
		String message = "";
		
		// local player attacked
		if (_turnData.getSource() == _localID){
			message = _messages.buildOpponentDefence(_turnData);
		} 
		
		// local player defended
		else if (_turnData.getTarget() == _localID){
			message = _messages.buildLocalDefence(_turnData);
		} 
		
		// other players interact
		else {
			message = _messages.build(_turnData);
		}
		
		// show notification
		if (message != ""){
			_parent.showNotification(message); // iterates on OK click
		} else iterate();
	}
	
	public void iterate(){
		_turnData.clear();
		_turn++;
		if (_turn >= _roundSize){
			_turn = 0;
			if (_queue.isEnd()) {
				//_parent.onGameOver();
				buildScoreBoard();
				return;
			}
			_queue.iterate();
		}
		if (localIsCurrent()) _parent.changeState(State.CollectItem);
		else {
			_parent.changeState(State.Waiting);
			//_parent.setMessage(getCurrentPlayer()+"'s move");
			computerCollect();
		}
	}
	
	public void buildScoreBoard(){
		// create board
		_scores = new ArrayList<Score>();
		// add local player
		_scores.add(new Score(_localPlayer.getID(), _localPlayer.getScore()));
		// add scores for local computer players
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
			_parent.onGameOver();
		}
	}
	
	public void addToScoreBoard(String id, int score){
		addToScoreBoard(new Score(id, score));
	}
	
	
	// Local Player Move //
	
	public void localCollect(){
		// On Local Player Item Click
		
		// Update _turnData
		_turnData.setSourceData(_localID, _localPlayer.getPoints(), 
				_localPlayer.getType(getCurrentSquare()));
		
		// Choose next
		if (_turnData.getType() == Grid.sqType.sqChoose) {
			_parent.showChooseNext();
		}
		
		// Interaction - show attack dialog
		else if (_localPlayer.getGrid().isInteraction(getCurrentSquare())){
			_parent.showAttackDialog(getLocalOpponents(), _turnData.getType());
		} 
		
		// Collect item
		else {
			_localPlayer.collect(getCurrentSquare());
			// Note - square emptying is handled by collect
			iterate();
			return;
		}
		// Empty square
		getLocalPlayer().getGrid().empty(getCurrentSquare());
	}
	
	public void localAttack(String targetID){
		// After local player picks attack target
		
		InteractionData response;
		
		// send data to opponent
		if (isComputerPlayer(targetID)){
			// Local Computer Player
			response = getAiPlayerById(targetID).defend(_turnData);
			processResponse(response);
		}
		else {
			// Network Player ...
		}
		
	}
	
	public void processResponse(InteractionData response){
		// complete attack after target sends back response
		// TODO - trigger from endTurn() ?
		
		_turnData = response;
		
		getLocalPlayer().completeResponse(_turnData);
		
		endTurn();
		
	}
	
	public void processAttack(InteractionData attackData){
		// when the local player is attacked
		
		_turnData = attackData;
		
		// Local player can defend - show defend dialog
		if (_localPlayer.canDefend(_turnData.getType())){
			_parent.showDefendDialog(_turnData.getSource(), _turnData.getType(), 
					_localPlayer.getDefences(_turnData.getType()));
		} 
		
		// Local player can't defend - finish attack processing
		else localDefence(Player.dfType.dfNone);
	}
	
	public void localDefence(Player.dfType defence){
		// After local player picks defence type

		_turnData.setTargetData(_localID, _localPlayer.getPoints(), defence);
		
		// Finish processing attack
		_localPlayer.completeAttack(_turnData);
		
		// Send data back to attacker (all players?)
		if (isComputerPlayer(_turnData.getSource())){
			// Local Computer Player
			getAiPlayerById(_turnData.getSource()).completeResponse(_turnData);
		}
		else {
			// Network Player ...
			// TODO - send data in endTurn() ?
		}
		
		endTurn();
		
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
		
		// Interactions (where necessary)
		
		// Attack local player
		if (_turnData.getTarget() == getLocalPlayerId()){
			processAttack(_turnData);
			
		// Attack other (computer) player
		} else {
			_turnData = getAiPlayerById(_turnData.getTarget()).defend(_turnData);
			getCurrentAiPlayer().completeResponse(_turnData);
			
			endTurn();
		}
	}
	
}

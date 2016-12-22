package com.cavillum.pirategame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.cavillum.pirategame.data.InteractionData;
import com.cavillum.pirategame.data.Score;
import com.cavillum.pirategame.helpers.AchievementHelper;
import com.cavillum.pirategame.helpers.LevelBuilder;
import com.cavillum.pirategame.objects.ComputerPlayer;
import com.cavillum.pirategame.objects.Grid;
import com.cavillum.pirategame.objects.Player;
import com.cavillum.pirategame.objects.Queue;
import com.cavillum.pirategame.ui.GameScene;
import com.cavillum.pirategame.ui.LayoutHandler;
import com.cavillum.pirategame.ui.MessageBuilder;
import com.cavillum.pirategame.ui.Messages;
import com.cavillum.pirategame.ui.GameScene.State;

public class TurnHandler {
	
	private GameScene _parent;
	
	private int _turn;
	private int _roundSize;
	private Queue _queue;
	private int _current = -1; // for buried, choose
	
	private Player _localPlayer;
	private String _localID;
	private ArrayList<String> _players;
	private ArrayList<ComputerPlayer> _aiPlayers;
	
	private ArrayList<String> _dead; // for knockout
	
	private AchievementHelper _achievements;
	
	private ArrayList<String> _history;
	
	private InteractionData _turnData;
	
	List<Score> _scores;

	
	public TurnHandler(GameScene game){
		_parent = game;
		_turn = 0;
		_aiPlayers = new ArrayList<ComputerPlayer>();
		_players = new ArrayList<String>();
		_dead = new ArrayList<String>();
		_roundSize = 0;
		_localPlayer = new Player();
		_localID = new String();
		_queue = new Queue();
		_queue.generate();
		_history = new ArrayList<String>();
		_turnData = new InteractionData();
	}
	
	public TurnHandler(GameScene game, Player local, ArrayList<String> players){
		_parent = game;
		_turn = 0;
		_players = players;
		_dead = new ArrayList<String>();
		_aiPlayers = new ArrayList<ComputerPlayer>();
		_roundSize = _players.size();
		_localPlayer = local;
		_localID = local.getID();
		_queue = new Queue();
		_queue.generate();
		_history = new ArrayList<String>();
		_turnData = new InteractionData();
		_achievements = new AchievementHelper(_localID);
	}
	
	public void reset(){
		_turn = 0;
		_queue.reset();
		if (PirateGame.save.playersChanged()) loadAiPlayers();
		else for (ComputerPlayer p : _aiPlayers) p.reset();
		_dead.clear();
		_localPlayer.reset();
		_roundSize = _players.size();
		_turnData.clear();
		_history.clear();
		_achievements.reset();
		_scores = null;
	}
	
	public void addComputerPlayer(ComputerPlayer player){
		_aiPlayers.add(player);
		_players.add(player.getID());
		_roundSize++;
	}
	
	public void loadAiPlayers(){
		// TODO - this could use tidying, some redundancy
		// reset arrays
		_aiPlayers.clear();
		_players.clear();
		_players.add(_localID);
		_roundSize = 1;
		
		// add computer players from memory 
		HashMap<String, Boolean> players = PirateGame.save.getPlayers();
		
		for (String name : players.keySet()){
			addComputerPlayer(new ComputerPlayer(name, players.get(name)));
		}
		PirateGame.save.updatePlayers(false);
	}
	
	public void setLocalPlayer(Player player){
		_localPlayer = player;
		_localID = player.getID();
		_players.add(_localID);
		_achievements = new AchievementHelper(_localID);
		_roundSize++;
	}
	
	public int getCurrentTurn(){
		return _turn;
	}
	
	public int getCurrentSquare(){
		if ((PirateGame.levels.getGameType() == LevelBuilder.GameType.Buried
				|| PirateGame.levels.getGameType() == LevelBuilder.GameType.Choose)
				&& localIsCurrent())
			return _current;
		return _queue.getCurrent();
	}
	
	public void setCurrentSquare(int current){
		_current = current;
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
		if (PirateGame.levels.getGameType() == LevelBuilder.GameType.Knockout)
			return getAliveOpponents(playerID);
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
	
	private Player getPlayerById(String id) {
		if (id == _localPlayer.getID()) return _localPlayer;
		return getAiPlayerById(id);
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
	
	
	// KnockOut Functions
	
	public ArrayList<String> getDeadPlayers(){
		return _dead;
	}
	
	public int getDeadCount(){
		return _dead.size();
	}
	
	public int getAliveCount(){
		return _players.size() - _dead.size();
	}
	
	public void killPlayer(String player){
		_dead.add(player);
	}
	
	public void processKill(){
		// Knockout
		if (!(_turnData.getType()==Grid.sqType.sqKill))
			return;
		if (_turnData.getDefenceType() == Player.dfType.dfNone)
			killPlayer(_turnData.getTarget());
		else if (_turnData.getDefenceType() == Player.dfType.dfMirror)
			killPlayer(_turnData.getSource());
	}
	
	public boolean isDead(String player){
		return _dead.contains(player);
	}
	
	public ArrayList<String> getAlive(){
		ArrayList<String> temp = new ArrayList<String>();
		for (String p : _players){
			if (!isDead(p)) temp.add(p);
		}
		return temp;
	}
	
	public ArrayList<String> getAliveOpponents(String playerID){
		ArrayList<String> temp = new ArrayList<String>();
		for (String p : _players){
			if (p != playerID && !isDead(p)) temp.add(p);
		}
		return temp;
	}
	
	
	// Event Functions
	
	public boolean localWin(){
		if (_scores != null){
			return _scores.get(0).getName() == _localID;
		}
		return false;
	}
	
	public ArrayList<String> getHistory(){
		return _history;
	}
	
	public void updateHistory(String notif){
		if (notif == "") return;
		if (_history.size()==3) _history.remove(0);
		_history.add(notif);
	}
	
	public String getLastEvent(){
		if (_history.size() == 0) return "";
		String message = _history.get(_history.size()-1);
		if (message.length() > 40) return message.substring(0, 40)+"...";
		return message;
	}
	
	
	// End of turn Functions
	
	public void endTurn(){
		
		// Knockout
		if (PirateGame.levels.getGameType() == LevelBuilder.GameType.Knockout
				&& _turnData.getType() == Grid.sqType.sqKill)
			processKill();
		
		// tell all players what happened in round
		for (String p : getLocalOpponents()){
			if (isComputerPlayer(p)){
				getAiPlayerById(p).processEvent(_turnData.clone());//problematic, memory waste?
			} else {
				// Network Player ...
				// if not source or target, send update
			}
		}
		
		// Check for achievements
		_achievements.update(_turnData);
		
		// show notification (where relevant)
		notification();
	}
	
	public void notification(){
		String message = "";
		String notif = "";
		
		// local player attacked
		if (_turnData.getSource() == _localID){
			message = Messages.buildOpponentDefence(_turnData);
			notif = MessageBuilder.buildOpponentDefence(_turnData);
			_parent.getAnimator().setAttackPoints(_turnData);
		} 
		
		// local player defended
		else if (_turnData.getTarget() == _localID){
			message = Messages.buildLocalDefence(_turnData);
			notif = MessageBuilder.buildLocalDefence(_turnData);
			_parent.getAnimator().setDefendPoints(_turnData);
		} 
		
		// other players interact
		else {
			message = Messages.build(_turnData);
			notif = MessageBuilder.build(_turnData);
		}
		
		// Update Notification Bar
		if (notif != ""){
			updateHistory(notif);
			_parent.setMessage(getLastEvent());
			if (PirateGame.layout.getSideBarType() == LayoutHandler.SideType.History)
				_parent.updateSideBar();
			// show indicator
			if (message == "") PirateGame.layout.showIndicator(true);
		}
		
		// show notification
		if (message != ""){
			_parent.showNotification(message); // iterates on OK click
		} else iterate();
	}
	
	public void iterate(){
		_turnData.clear();
		
		// Knock out
		if (PirateGame.levels.getGameType() == LevelBuilder.GameType.Knockout
				&& getAliveCount() <= 1) {
			buildKnockoutBoard();
			return;
		}
		
		do {
			_turn++;
			if (_turn >= _roundSize){
				if (_queue.isEnd()) {
					if (PirateGame.levels.getGameType() == LevelBuilder.GameType.Knockout)
						buildKnockoutBoard();
					else buildScoreBoard();
					return;
				}
				_turn = 0;
				_queue.iterate();
			}
		} while(isDead(getCurrentPlayer()));
		
		if (localIsCurrent()) {
			if (PirateGame.levels.getGameType() == LevelBuilder.GameType.Buried)
				_parent.changeState(State.Buried);
			else if (PirateGame.levels.getGameType() == LevelBuilder.GameType.Choose)
				_parent.changeState(State.Select);
			else _parent.changeState(State.CollectItem);
		}
		
		else {
			_parent.changeState(State.Waiting);
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
	
	public void buildKnockoutBoard(){
		// TODO - network version?
		// create board
		_scores = new ArrayList<Score>();
		// add alive players
		if (!isDead(_localID)) 
			_scores.add(new Score(_localPlayer.getID(), _localPlayer.getScore()));
		// add alive ai
		for (ComputerPlayer p : _aiPlayers){
			if (!isDead(p.getID())) addToScoreBoard(p.getID(), p.getScore());
		}
		// sort alive players
		Collections.sort(_scores);
		// add dead players
		String p;
		for (int i=_dead.size()-1; i>=0; i--){
			p = _dead.get(i);
			if (p == _localID) 
				_scores.add(new Score(_localPlayer.getID(), _localPlayer.getScore()));
			else 
				_scores.add(new Score(p, getAiPlayerById(p).getScore()));
		}
		// show scoreboard
		_parent.onGameOver();
	}
	
	
	// Process incoming messages
	
	public void processIncoming(InteractionData data){
		// Local is being attacked
		if (data.getTarget() == _localID) processAttack(data);
		// Response from a local attack
		else if (data.getSource() == _localID) processResponse(data);
		// End of another player's turn (no interaction)
		else {
			_turnData = data;
			endTurn();
		}
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
		
		
		// Specials 
		
		else if (_localPlayer.getType(getCurrentSquare()) == Grid.sqType.sqSkull){
			for (String p : getLocalOpponents()){
				if (isComputerPlayer(p)) getAiPlayerById(p).instantKill();
			}
			endTurn();
		}
		
		else if (_localPlayer.getType(getCurrentSquare()) == Grid.sqType.sqShell){
			// create board
			ArrayList<Score> temp = new ArrayList<Score>();
			// add local player
			temp.add(new Score(_localID, _localPlayer.getPoints()));
			// add scores for local computer players
			for (ComputerPlayer p : _aiPlayers)
				temp.add(new Score(p.getID(), p.getPoints()));
			Collections.sort(temp);
			getPlayerById(temp.get(0).getName()).setPoints(0);
			//_turnData.setType(Grid.sqType.sqShell);
			_turnData.setTarget(temp.get(0).getName());
			endTurn();
		}
		
		else if (_localPlayer.getType(getCurrentSquare()) == Grid.sqType.sqReveal){
			_parent.changeState(State.Reveal);
			_parent.setMessage("Tap anywhere to continue...");
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

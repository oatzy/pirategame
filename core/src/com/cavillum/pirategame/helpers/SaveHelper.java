package com.cavillum.pirategame.helpers;

import java.lang.reflect.Array;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

public class SaveHelper {
	
	// TODO - see what I can do about hashmap getting player order mixed up
	
	private Preferences _aiPlayers;
	private Preferences _achievements;
	
	private String[] _oldNames = {"Blackbeard", "Calico Jack", "Cpt. Kidd", "Black Bart",
			"Davy Jones", "Anne Bonny", "Mary Read", "John Silvers", "Jack Sparrow",
			"Cpt. Barbossa", "Cpt. Morgan", "Cpt. Hook", "Cpt. Pugwash"};
	
	//private String[] _names = {"Hackbeard", "Cpt. Modem", "A/V Jones", 
	//		"Unicode Jack",	"Cpt. CADD", "Long J Cyber", "Droid Bonny", 
	//		"Mary ROM",	"Black Scart", "Cpt. Plug", "Dread Robots"};
	
	private String[] _names = {"Mark", "Jonny", "Adam", "Katie", 
			"George", "Chris", "Lizzy", "James", "Clare", "Robert", "Rachel"};
	
	public SaveHelper(){
		
		_aiPlayers = Gdx.app.getPreferences("AiPlayers");
		_achievements = Gdx.app.getPreferences("Achievements");
		
		// Defaults
		if (!_achievements.contains("highScore")) setDefaultAchievements();
		
		// temporarily disabled for testing
		if (!_aiPlayers.contains("count")) setDefaultPlayers();
		if (_aiPlayers.getBoolean("rename", true)) updateNames();
	}
	
	public void setDefaultAchievements(){
		_achievements.clear();
		_achievements.putInteger("highScore", 0);
		_achievements.putInteger("wins", 0);
		_achievements.putInteger("loses", 0);
		_achievements.putInteger("totalPoints", 0);
		_achievements.putInteger("pointsRobbed", 0);
		_achievements.putInteger("kills", 0);
		_achievements.putInteger("quits", 0);
		
		_achievements.putBoolean("standard", false);
		_achievements.putBoolean("buried", false);
		_achievements.putBoolean("knockout", false);
		_achievements.putBoolean("choose", false);
		_achievements.flush();
	}
	
	public void setDefaultPlayers(){
		_aiPlayers.clear();
		_aiPlayers.putInteger("count", 0);
		_aiPlayers.putBoolean("update", true);
		_aiPlayers.putFloat("difficulty", 0.5f);
		_aiPlayers.flush();
		addPlayer("Mark", false);
		addPlayer("Jonny", true);
		addPlayer("Adam", false);
		addPlayer("Katie", true);
	}
	
	
	// Player Functions //
	
	public void updateNames(){
		int count = _aiPlayers.getInteger("count");
		String old = new String();
		
		for (int i=0; i<count; i++){
			old = _aiPlayers.getString("player"+(i+1));
			for (int j=0; j<_oldNames.length; j++){
				if (old.equals(_oldNames[j])) _aiPlayers.putString("player"+(i+1), _names[i]);
			}
		}
		_aiPlayers.putBoolean("rename", false);
		_aiPlayers.flush();
	}
	
	public void resetPlayers(){
		_aiPlayers.clear();
		setDefaultPlayers();
	}
	
	public boolean playersChanged(){
		return _aiPlayers.getBoolean("update", true);
	}
	
	public void updatePlayers(boolean change){
		_aiPlayers.putBoolean("update", change);
		_aiPlayers.flush();
	}
	
	public int getPlayerCount(){
		return _aiPlayers.getInteger("count");
	}
	
	public void setPlayerMode(String name, boolean mode){
		int count = _aiPlayers.getInteger("count");
		for (int i=0; i<count; i++){
			if (_aiPlayers.getString("player"+(i+1)).equals(name)) {
				_aiPlayers.putBoolean("mode"+(i+1), mode);
				_aiPlayers.putFloat("difficulty", _aiPlayers.getFloat("difficulty")+
						(mode ? 0.25f : -0.25f));
				_aiPlayers.flush();
				return;
			}
		}
	}
	
	public void renamePlayer(String oldName, String newName){
		if (newName.length() == 0 || isNameUsed(newName)) return;
		int count = _aiPlayers.getInteger("count");
		for (int i=0; i<count; i++){
			if (_aiPlayers.getString("player"+(i+1)).equals(oldName)) {
				_aiPlayers.putString("player"+(i+1), newName);
				_aiPlayers.flush();
				return;
			}
		}
	}
	
	public HashMap<String, Boolean> getPlayers(){
		
		HashMap<String, Boolean> players = new HashMap<String, Boolean>();
		int count = _aiPlayers.getInteger("count");
		
		for (int i=0; i<count; i++){
			players.put(_aiPlayers.getString("player"+(i+1)), _aiPlayers.getBoolean("mode"+(i+1)));
		}
		return players;
	}
	
	public boolean isNameUsed(String name){
		int count = _aiPlayers.getInteger("count");
		for (int i=0; i<count; i++){
			// for future reference, use 'equals' NOT '==' or things won't work
			if (_aiPlayers.getString("player"+(i+1)).equals(name)) return true;
		}
		return false;
	}
	
	public void addPlayer(String name, Boolean hard){
		int index = _aiPlayers.getInteger("count") + 1;
		_aiPlayers.putString("player"+index, name);
		_aiPlayers.putBoolean("mode"+index, hard);
		_aiPlayers.putInteger("count", index);
		_aiPlayers.putFloat("difficulty", _aiPlayers.getFloat("difficulty")+
				(hard ? 0.5f : 0.25f));
		_aiPlayers.flush();
	}
	
	public void addPlayer(String name){
		addPlayer(name, MathUtils.randomBoolean());
	}
	
	public void addPlayers(HashMap<String, Boolean> players){
		for (String name : players.keySet()){
			addPlayer(name, players.get(name));
		}
	}
	
	public void addNextPlayer(){
		for (int i=0; i<Array.getLength(_names); i++){
			if (!isNameUsed(_names[i])){
				if (_names[i].equals("Chris") || _names[i].equals("Jonny"))
					addPlayer(_names[i], true);
				else addPlayer(_names[i]);
				return;
			}
		}
	}
	
	public void removePlayer(String name){
		HashMap<String, Boolean> players = getPlayers();
		if (players.containsKey(name)){
			_aiPlayers.clear();
			_aiPlayers.putInteger("count", 0);
			_aiPlayers.putFloat("difficulty", 0.5f);
			_aiPlayers.flush();
			for (String n : players.keySet()){
				if (n != name) addPlayer(n, players.get(n));
			}
		}
	}
	
	public float recalculateDifficulty(){
		int count = _aiPlayers.getInteger("count");
		float temp = 0.5f;
		for (int i=0; i<count; i++){
			if (_aiPlayers.getBoolean("mode"+(i+1))) temp += 0.5f;
			else temp += 0.25f;
		}
		_aiPlayers.putFloat("difficulty", temp);
		_aiPlayers.flush();
		return temp;
	}
	
	public float getDifficulty(){
		float d = _aiPlayers.getFloat("difficulty");
		if (d < 1) {
			return recalculateDifficulty();
		}
		return d;
	}
	
	public String getDifficultyString(){
		float d = getDifficulty();
		if (d < 2) return "Easy";
		if (d < 3) return "Medium";
		return "Hard";
	}
	
	public int getDifficultyInt(){
		float d = getDifficulty();
		if (d < 2) return 1;
		if (d < 3) return 2;
		return 3;
	}
	
	
	// Data Functions //
	
	public int getHighScore(){
		return _achievements.getInteger("highScore");
	}
	
	public void setHighScore(int score){
		_achievements.putInteger("highScore", score);
		_achievements.flush();
	}
	
	public int getTotalPoints(){
		return _achievements.getInteger("totalPoints");
	}
	
	public void updateTotalPoints(int points){
		int temp = _achievements.getInteger("totalPoints");
		_achievements.putInteger("totalPoints", temp+points);
		_achievements.flush();
	}
	
	public void setTotalPoints(int points){
		_achievements.putInteger("totalPoints", points);
		_achievements.flush();
	}
	
	public int getWins(){
		return _achievements.getInteger("wins");
	}
	
	public void setWins(int wins){
		_achievements.putInteger("wins", wins);
		_achievements.flush();
	}
	
	public void updateWins(int wins){
		int temp = _achievements.getInteger("wins");
		_achievements.putInteger("wins", temp+1);
		_achievements.flush();
	}
	
	public int getLoses(){
		return _achievements.getInteger("loses");
	}
	
	public void setLoses(int loses){
		_achievements.putInteger("loses", loses);
		_achievements.flush();
	}
	
	public void updateLoses(int loses){
		int temp = _achievements.getInteger("loses");
		_achievements.putInteger("loses", temp+1);
		_achievements.flush();
	}
	
	public int getPlays(){
		return (_achievements.getInteger("loses")+_achievements.getInteger("wins"));
	}
	
	public int getQuits(){
		return _achievements.getInteger("quits");
	}
	
	public void incrementQuit(){
		int temp = _achievements.getInteger("quits");
		_achievements.putInteger("quits", temp+1);
		_achievements.flush();
	}
	
	public int getKills(){
		return _achievements.getInteger("kills");
	}
	
	public void incrementKills(){
		int temp = _achievements.getInteger("kills");
		_achievements.putInteger("kills", temp+1);
		_achievements.flush();
	}
	
	public int getRobbed(){
		if (!_achievements.contains("pointsRobbed")){
			_achievements.putInteger("pointsRobbed", 0);
			_achievements.flush();
		}
		return _achievements.getInteger("pointsRobbed",0);
	}
	
	public void updateRobbed(int points){
		int temp = _achievements.getInteger("pointsRobbed");
		_achievements.putInteger("pointsRobbed", temp+points);
		_achievements.flush();
	}
	
	public void updateAchievements(int score, boolean win){
		
		int temp;
		
		if (win){
			temp = _achievements.getInteger("wins");
			_achievements.putInteger("wins", temp+1);
		} else{
			temp = _achievements.getInteger("loses");
			_achievements.putInteger("loses", temp+1);
		}
		
		temp = _achievements.getInteger("totalPoints");
		_achievements.putInteger("totalPoints", temp+score);
		
		temp = _achievements.getInteger("highScore");
		if (score > temp) _achievements.putInteger("highScore", score);
		
		_achievements.flush();
	}
	
	public boolean isHighScore(int score){
		return score > _achievements.getInteger("highScore");
	}
	
	public void setPlayed(LevelBuilder.GameType type){
		switch(type){
		case Standard:
			_achievements.putBoolean("standard", true);
			break;
		case Buried:
			_achievements.putBoolean("buried", true);
			break;
		case Knockout:
			_achievements.putBoolean("knockout", true);
			break;
		case Choose:
			_achievements.putBoolean("choose", true);
			break;
		default:
			return;
		}
		_achievements.flush();
	}
	
	public boolean hasPlayed(LevelBuilder.GameType type){
		switch(type){
		case Standard:
			return _achievements.getBoolean("standard");
		case Buried:
			return _achievements.getBoolean("buried");
		case Knockout:
			return _achievements.getBoolean("knockout");
		case Choose:
			return _achievements.getBoolean("choose");
		default:
			return false;
		}
	}
	
	public boolean hasPlayedAll(){
		for (LevelBuilder.GameType type : LevelBuilder.GameType.values()){
			if (!hasPlayed(type)) return false;
		}
		return true;
	}
	
	public boolean isSynced(){
		return _achievements.getBoolean("synced", false);
	}
	
	public void setSynced(boolean synced){
		_achievements.putBoolean("synced", synced);
		_achievements.flush();
	}
	
	public boolean submitPast(){
		if (_achievements.getInteger("submitted", 0)<6){
			_achievements.putInteger("submitted", 6); // version code
			_achievements.flush();
			return true;
		} return false;
	}
}

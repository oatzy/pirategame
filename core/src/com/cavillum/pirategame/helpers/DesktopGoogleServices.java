package com.cavillum.pirategame.helpers;

import com.badlogic.gdx.Gdx;
import com.cavillum.pirategame.PirateGame;

public class DesktopGoogleServices implements IGoogleServices{

	@Override
	public void signIn() {
		Gdx.app.log("Desktop Google", "Sign In");
	}

	@Override
	public void signOut() {
		Gdx.app.log("Desktop Google", "Sign Out");
	}

	@Override
	public boolean isSignedIn() {
		Gdx.app.log("Desktop Google", "Is Signed In");
		return false;
	}

	@Override
	public void submitScore(int score) {
		Gdx.app.log("Desktop Google", "Submit Score");
	}

	@Override
	public void unlockAchievement(String achievementID) {
		Gdx.app.log("Desktop Google", "Unlock Achievement");		
	}

	@Override
	public void showScores() {
		Gdx.app.log("Desktop Google", "Show Scores");
	}

	@Override
	public void showAchievements() {
		Gdx.app.log("Desktop Google", "Show Achievement");
	}

	@Override
	public void showAds(boolean show) {
		Gdx.app.log("Desktop Google", (show ? "Show" : "Hide")+" Ads");
	}

	@Override
	public void incrementAchievement(String achievementID, int increment) {
		Gdx.app.log("Desktop Google", "Unlock Increment Achievement");		
	}

	@Override
	public void updateAchievements(int score, boolean win) {
		if (win) incrementWins();
		incrementTotal(score);
		scoreAchievements(score);
		PirateGame.save.updateAchievements(score, win);
		Gdx.app.log("Total Plays", ""+PirateGame.save.getPlays());
		Gdx.app.log("Desktop Google", "Update Achievements");
	}
	
	public void incrementWins(){
		Gdx.app.log("increment wins", ""+(PirateGame.save.getWins()+1));
	}
	
	public void incrementTotal(int score){
		int old = PirateGame.save.getTotalPoints();
		int steps = (old+score)/10000 - old/10000;
		Gdx.app.log("Incriment total", steps+" new steps");
		Gdx.app.log("Total points", ""+(old+score));
	}
	
	public void scoreAchievements(int score){
		if (score >= 50000) Gdx.app.log("Score Achievement", "50,000 points");
		if (score >= 100000) Gdx.app.log("Score Achievement", "100,000 points");
		if (score == 0) Gdx.app.log("Score Achievement", "pipped at the post");
	}

	@Override
	public void incrementEvent(String eventID, int increment) {
		Gdx.app.log("Incriment event", "");
	}

	@Override
	public void unlockQuits() {
		Gdx.app.log("Quitter", ""+PirateGame.save.getQuits());
	}

	@Override
	public void incrementRobbed(int points) {
		int old = PirateGame.save.getRobbed();
		PirateGame.save.updateRobbed(points);
		Gdx.app.log("Incriment Robbed", ((old+points)/10000 - old/10000)+" steps");
		Gdx.app.log("Total robbed", (old+points)+"");
	}

	@Override
	public void unlockBackstabber() {
		Gdx.app.log("Achievement unlocked", "Backstabber");
	}

	@Override
	public void unlockKonami() {
		Gdx.app.log("Achievement unlocked", "Unlimited Lives!");
		
	}

	@Override
	public void unlockBlueShell() {
		Gdx.app.log("Achievement unlocked", "Blue Shell!!!");
		
	}

	@Override
	public void unlockSwitcheroo() {
		Gdx.app.log("Achievement unlocked", "Switcheroo unlocked");

	}

	@Override
	public void incrementKills() {
		Gdx.app.log("Achievement unlocked", "Increment kills: "+PirateGame.save.getKills());
	}

	@Override
	public void showStandardScores() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showBuriedScores() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showKnockoutScores() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showChooseScores() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void submitLevelScore(int score) {
		// TODO Auto-generated method stub
		
	}

}

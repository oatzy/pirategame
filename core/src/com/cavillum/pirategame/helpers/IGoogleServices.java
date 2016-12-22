package com.cavillum.pirategame.helpers;

public interface IGoogleServices {
	public void signIn();
	public void signOut();
	public boolean isSignedIn();
	public void submitScore(int score);
	public void unlockAchievement(String achievementID);
	public void incrementAchievement(String achievementID, int increment);
	public void showScores();
	public void showAchievements();
	public void updateAchievements(int score, boolean win);
	public void showAds(boolean show);
	public void incrementEvent(String eventID, int increment);
	public void unlockQuits();
	public void incrementRobbed(int points);
	public void unlockBackstabber();
	public void unlockKonami();
	public void unlockBlueShell();
	public void unlockSwitcheroo();
	public void incrementKills();
	public void showStandardScores();
	public void showBuriedScores();
	public void showKnockoutScores();
	public void showChooseScores();
	public void submitLevelScore(int score);
}

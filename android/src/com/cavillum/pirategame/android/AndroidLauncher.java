package com.cavillum.pirategame.android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.math.MathUtils;
import com.cavillum.pirategame.PirateGame;
import com.cavillum.pirategame.helpers.IGoogleServices;
import com.cavillum.pirategame.helpers.LevelBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class AndroidLauncher extends AndroidApplication 
					implements IGoogleServices, GameHelperListener{
	
	private static final String AD_UNIT_ID = "ca-app-pub-3725066873640938/8707177000";
	
	private GameHelper _gameHelper;
	private PirateGame _game;
	
	private AdView adView;
	
	// Ad Handler
    private final int SHOW_ADS = 1;
    private final int HIDE_ADS = 0;

    @SuppressLint("HandlerLeak") // this bothers me
	protected Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SHOW_ADS:
                {
                    adView.setVisibility(View.VISIBLE);
                    break;
                }
                case HIDE_ADS:
                {
                    adView.setVisibility(View.GONE);
                    break;
                }
            }
        }
    };
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); // this needs to go first for some reason
		super.onCreate(savedInstanceState);
		
		// set tablet (landscape) mode
		if (isTablet()) setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		// Create Game object
		_game = new PirateGame(this);
		
		// Create the GameHelper.
		_gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		_gameHelper.enableDebugLog(false);

		GameHelperListener gameHelperListener = new GameHelper.GameHelperListener(){
			
			@Override
			public void onSignInSucceeded(){
				
			}

			@Override
			public void onSignInFailed(){
				
			}
		};
		
		// prevent sign-in on app start-up
		_gameHelper.setMaxAutoSignInAttempts(0);
		_gameHelper.setup(gameHelperListener);
		
		//initialize(new PirateGame(this), config);
		
		// Setup Game View
		
		RelativeLayout layout = new RelativeLayout(this);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		View gameView = initializeForView(_game, config);
		layout.addView(gameView);

		// Add the AdMob view 
		adView = new AdView(this);
		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, 
						RelativeLayout.LayoutParams.WRAP_CONTENT); 
		
		if (isTablet()) {
			adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); //tablet mode
			adView.setAdSize(AdSize.BANNER);
		}
		else {
			adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			adParams.addRule(RelativeLayout.CENTER_HORIZONTAL); // portrait
			adView.setAdSize(AdSize.SMART_BANNER);
		}
		
		adView.setAdUnitId(AD_UNIT_ID);
		startAdvertising();
		layout.addView(adView, adParams);
		
		setContentView(layout);
		
	}
	
	public boolean isTablet(){
		DisplayMetrics metrics = new DisplayMetrics();
		//getActivity().
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		float yInches= metrics.heightPixels/metrics.ydpi;
		float xInches= metrics.widthPixels/metrics.xdpi;
		double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
		if (diagonalInches>=6.5){
		    // 6.5inch device or bigger
			return true;
		}else{
		    // smaller device
			return false;
		}
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		_gameHelper.onStart(this);
		
	}

	@Override
	protected void onStop(){
		super.onStop();
		_gameHelper.onStop();
	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		_gameHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void signIn() {
		try {
			runOnUiThread(new Runnable() {
				//@Override
				public void run() {
					_gameHelper.beginUserInitiatedSignIn();
				}
			});
		}
		catch (Exception e)	{
			Gdx.app.log("Android Launcher", "Log in failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void signOut() {
		try	{
			runOnUiThread(new Runnable() {
				//@Override
				public void run() {
					_gameHelper.signOut();
				}
			});
		}
		catch (Exception e) {
			Gdx.app.log("Android Launcher", "Log out failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public boolean isSignedIn() {
		return _gameHelper.isSignedIn();
	}

	@Override
	public void submitScore(int score) {
		submitScore(score, PirateGame.save.getDifficultyInt());
		/*if (isSignedIn()) {
			Games.Leaderboards.submitScore(_gameHelper.getApiClient(),
					getString(R.string.leaderboard_high_scores), score);
		}*/
	} 
	
	@Override
	public void showScores() {
		showScores(PirateGame.save.getDifficultyInt());
		/*if (isSignedIn() == true){ 
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent
					(_gameHelper.getApiClient(), getString(R.string.leaderboard_high_scores)), 9002);
		}*/
	}
	
	@Override
	public void showStandardScores() {
		if (isSignedIn() == true){ 
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent
					(_gameHelper.getApiClient(), getString(R.string.leaderboard_normal)), 9002);
		}
	}
	
	@Override
	public void showBuriedScores() {
		if (isSignedIn() == true){ 
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent
					(_gameHelper.getApiClient(), getString(R.string.leaderboard_burial)), 9002);
		}
	}
	
	@Override
	public void showKnockoutScores() {
		if (isSignedIn() == true){ 
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent
					(_gameHelper.getApiClient(), getString(R.string.leaderboard_knockout)), 9002);
		}
	}
	
	@Override
	public void showChooseScores() {
		if (isSignedIn() == true){ 
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent
					(_gameHelper.getApiClient(), getString(R.string.leaderboard_choose)), 9002);
		}
	}
	
	@Override
	public void unlockAchievement(String achievementID) {
		if (isSignedIn()){
			Games.Achievements.unlock(_gameHelper.getApiClient(), achievementID);
		} 
	}
	
	@Override
	public void incrementAchievement(String achievementID, int increment) {
		if (isSignedIn()){
			Games.Achievements.increment(_gameHelper.getApiClient(), 
					achievementID, increment);
		} 
		
	}
	
	@Override
	public void showAchievements() {
		if (isSignedIn()) {
			startActivityForResult(Games.Achievements.getAchievementsIntent
					(_gameHelper.getApiClient()),9002);
		} 
	}
	
	@Override
	public void incrementEvent(String eventID, int increment){
		if (isSignedIn()){
			Games.Events.increment(_gameHelper.getApiClient(), eventID, increment);
		}
	}

	@Override
	public void onSignInFailed() {
		Gdx.app.log("Android Launcher", "Signin Failed");
	}

	@Override
	public void onSignInSucceeded() {
		Gdx.app.log("Android Launcher", "Signin Succeeded");		
	}
	
	private void startAdvertising() {
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest); 
	}

	@Override
	public void showAds(boolean show) {
		handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
	}
	
	
	// Leader Boards
	
	public void submitScore(int score, int level){
		if (isSignedIn()) {
			String board = new String();
			if (level == 1) board = getString(R.string.leaderboard_high_score_easy);
			else if (level == 2) board = getString(R.string.leaderboard_high_score_medium);
			else if (level == 3) board = getString(R.string.leaderboard_high_score_hard);
			else return;
			Games.Leaderboards.submitScore(_gameHelper.getApiClient(), board, score);
		}
	}
	
	@Override
	public void submitLevelScore(int score){
		if (isSignedIn()) {
			String board = new String();
			LevelBuilder.GameType type = PirateGame.levels.getGameType();
			if (type == LevelBuilder.GameType.Standard)
				board = getString(R.string.leaderboard_normal);
			else if (type == LevelBuilder.GameType.Buried)
				board = getString(R.string.leaderboard_burial);
			else if (type == LevelBuilder.GameType.Choose)
				board = getString(R.string.leaderboard_choose);
			else if (type == LevelBuilder.GameType.Knockout)
				board = getString(R.string.leaderboard_knockout);
			else return;
			Games.Leaderboards.submitScore(_gameHelper.getApiClient(), board, score);
		}
	}
	
	public void showScores(int level){
		if (isSignedIn()) {
			String board = new String();
			if (level == 1) board = getString(R.string.leaderboard_high_score_easy);
			else if (level == 2) board = getString(R.string.leaderboard_high_score_medium);
			else if (level == 3) board = getString(R.string.leaderboard_high_score_hard);
			else return;
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent
					(_gameHelper.getApiClient(), board), 9002);
		}	
	}
	
	
	// Achievements
	
	@Override
	public void updateAchievements(int score, boolean win){
		
		// unlock and increment stuff
		if (win) incrementWins();
		else incrementAchievement(getString(R.string.achievement_50_lost), 1);
		
		incrementTotal(score);
		scoreAchievements(score);
		
		// Save to local file
		PirateGame.save.updateAchievements(score, win);
		
		// unlock others
		unlockPlays();
		unlockBots();
		if (MathUtils.random()<0.05)
			unlockAchievement(getString(R.string.achievement_random_love));
		
		// update events
		if (score > 0)
			incrementEvent(getString(R.string.event_points), score);
		if (win) incrementEvent(getString(R.string.event_wins), 1);
		else incrementEvent(getString(R.string.event_lost), 1);
		
		if (PirateGame.save.hasPlayedAll()){
			unlockAchievement(getString(R.string.achievement_played_all));
		}
	}
	
	public void incrementWins(){
		unlockAchievement(getString(R.string.achievement_1_win));
		incrementAchievement(getString(R.string.achievement_5_wins), 1);
		incrementAchievement(getString(R.string.achievement_10_wins), 1);
		incrementAchievement(getString(R.string.achievement_25_wins), 1);
		incrementAchievement(getString(R.string.achievement_50_wins), 1);
		if (PirateGame.levels.isBuried())
			unlockAchievement(getString(R.string.achievement_buried_win));
		if (PirateGame.levels.isChoose())
			unlockAchievement(getString(R.string.achievement_choose_win));
		if (PirateGame.levels.isKnockout())
			unlockAchievement(getString(R.string.achievement_knockout_win));
	}
	
	public void incrementTotal(int score){
		int old = PirateGame.save.getTotalPoints();
		int steps = (old+score)/1000 - old/1000;
		if (steps > 0){
			incrementAchievement(getString(R.string.achievement_10000_total), steps);
			incrementAchievement(getString(R.string.achievement_20000_total), steps);
			incrementAchievement(getString(R.string.achievement_50000_total), steps);
		}
		steps = (old+score)/10000 - old/10000;
		if (steps > 0) {
			incrementAchievement(getString(R.string.achievement_100000_total), steps);
			incrementAchievement(getString(R.string.achievement_200000_total), steps);
			incrementAchievement(getString(R.string.achievement_500000_total), steps);
			incrementAchievement(getString(R.string.achievement_750000_total), steps);
			incrementAchievement(getString(R.string.achievement_1000000_total), steps);
			incrementAchievement(getString(R.string.achievement_2000000_total), steps);
		}
		if (old+score > 9000) unlockAchievement(getString(R.string.achievement_over_9000));
	}
	
	public void scoreAchievements(int score){
		if (score >= 25000) unlockAchievement(getString(R.string.achievement_25000_points));
		if (score >= 50000) unlockAchievement(getString(R.string.achievement_50000_points));
		if (score >= 100000) unlockAchievement(getString(R.string.achievement_100000_points));
		if (score == 0) unlockAchievement(getString(R.string.achievement_0_points));
	}
	
	@Override
	public void incrementRobbed(int points){
		int old = PirateGame.save.getRobbed();
		int steps = (old+points)/10000 - old/10000;
		if (steps > 0){
			incrementAchievement(getString(R.string.achievement_250000_robbed), steps);
			incrementAchievement(getString(R.string.achievement_500000_robbed), steps);
		}
		steps = (old+points)/50000 - old/50000; // different step cause I'm dumb
		if (steps > 0)
			incrementAchievement(getString(R.string.achievement_1000000_robbed), steps);
		PirateGame.save.updateRobbed(points);
	}
	
	@Override
	public void incrementKills(){
		incrementAchievement(getString(R.string.achievement_10_kill), 1);
		incrementAchievement(getString(R.string.achievement_25_kill), 1);
	}
	
	public void unlockPlays(){
		int plays = PirateGame.save.getPlays();
		if (plays >= 100) unlockAchievement(getString(R.string.achievement_100_plays));
	}
	
	public void unlockBots(){
		int wins = PirateGame.save.getWins();
		int loses = PirateGame.save.getLoses();
		if (wins+loses > 20){
			if (loses > wins) unlockAchievement(getString(R.string.achievement_bots_1));
			if (loses > 2*wins) unlockAchievement(getString(R.string.achievement_bots_2));
		}
	}
	
	@Override
	public void unlockQuits(){
		int quits = PirateGame.save.getQuits();
		if (quits >= 10) unlockAchievement(getString(R.string.achievement_10_quits));
	}
	
	@Override
	public void unlockBackstabber(){
		unlockAchievement(getString(R.string.achievement_gift_kill));
	}
	
	@Override
	public void unlockKonami(){
		unlockAchievement(getString(R.string.achievement_konami));
	}
	
	@Override
	public void unlockBlueShell(){
		unlockAchievement(getString(R.string.achievement_blue_shell));
	}
	
	@Override
	public void unlockSwitcheroo(){
		unlockAchievement(getString(R.string.achievement_25000_swap));
	}
	

	// Achievement House Keeping
	public void unlockPast(){
		
		// Total Points
		int points = PirateGame.save.getTotalPoints();
		int steps = points/1000;
		if (steps > 0){
			incrementAchievement(getString(R.string.achievement_10000_total), steps);
			incrementAchievement(getString(R.string.achievement_20000_total), steps);
			incrementAchievement(getString(R.string.achievement_50000_total), steps);
		}
		steps = points/10000;
		if (steps > 0){
			incrementAchievement(getString(R.string.achievement_100000_total), steps);
			incrementAchievement(getString(R.string.achievement_200000_total), steps);
			incrementAchievement(getString(R.string.achievement_500000_total), steps);
			incrementAchievement(getString(R.string.achievement_750000_total), steps);
			//incrementAchievement(getString(R.string.achievement_1000000_total), steps);
			incrementAchievement(getString(R.string.achievement_2000000_total), steps);
		}
		
		// Wins
		int wins = PirateGame.save.getWins();
		if (wins > 0){
			unlockAchievement(getString(R.string.achievement_1_win));
			incrementAchievement(getString(R.string.achievement_5_wins), wins);
			//incrementAchievement(getString(R.string.achievement_10_wins), wins);
			//incrementAchievement(getString(R.string.achievement_25_wins), wins);
			incrementAchievement(getString(R.string.achievement_50_wins), wins);
		}
		
		// Loses
		int loses = PirateGame.save.getLoses();
		if (loses > 0) incrementAchievement(getString(R.string.achievement_50_lost), loses);
		
		// Single game points (using high score)
		int high = PirateGame.save.getHighScore();
		if (high!=0) scoreAchievements(high);
		
		// Others
		unlockPlays();
		unlockQuits();
	}
	
}

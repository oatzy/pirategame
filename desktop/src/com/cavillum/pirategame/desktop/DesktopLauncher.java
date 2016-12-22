package com.cavillum.pirategame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cavillum.pirategame.PirateGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = 720;
		config.height = 1280;
		config.title = "The Pirate Game";
		
		new LwjglApplication(new PirateGame(), config);
	}
}

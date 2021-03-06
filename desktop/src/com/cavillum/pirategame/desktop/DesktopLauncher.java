package com.cavillum.pirategame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cavillum.pirategame.PirateGame;
import com.cavillum.pirategame.helpers.DesktopGoogleServices;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = 960;//1067;//800;//540;//
		config.height = 600;//720;//
		config.title = "The Pirate Game";
		
		new LwjglApplication(new PirateGame(new DesktopGoogleServices()), config);
	}
}

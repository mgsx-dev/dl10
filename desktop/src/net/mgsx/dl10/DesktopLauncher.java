package net.mgsx.dl10;

import java.awt.SplashScreen;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = DL10Game.WIDTH;
		config.height = DL10Game.HEIGHT;
		
		new LwjglApplication(new DL10Game(){
			@Override
			public void create() {
				SplashScreen splashScreen = SplashScreen.getSplashScreen();
				if(splashScreen != null){
					splashScreen.close();
				}
				super.create();
			}
		}, config);
	}
}

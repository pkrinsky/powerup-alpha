package powerup.engine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	private static int debugLevel;
	
	public static void log(String s) {
		log(s,0);
	}
	
	public static void log(String s, int level) {
		if (debugLevel >= level) {
			String date = new SimpleDateFormat("HH:mm:ss:S").format(new Date())+"   ";
			System.out.println(date.substring(0, 13)+" "+s);
		}
	}

	public static int getDebugLevel() {
		return debugLevel;
	}

	public static void setDebugLevel(int debugLevel) {
		Util.debugLevel = debugLevel;
	}
	
	

}

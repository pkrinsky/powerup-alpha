package powerup.engine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	
	public static void log(String s) {
		String date = new SimpleDateFormat("HH:mm:ss:S").format(new Date());;
		System.out.println(date+" "+s);
	}

}

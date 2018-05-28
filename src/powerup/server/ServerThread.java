package powerup.server;

import java.io.BufferedReader;
import java.io.PrintWriter;

import powerup.engine.Util;

public class ServerThread extends Thread {
	
	private BufferedReader in;
	private PrintWriter out;
	private GameServer server;
	private boolean running = true;
	private String name;
	
	
	public ServerThread(BufferedReader in, PrintWriter out, String name, GameServer server) {
		super();
		this.in = in;
		this.out = out;
		this.name = name;
		this.server = server;
	}

	
	public void shutdown() {
		running = false;
	}
	
    public void run() {
		String line ="";
		try {
			while (running) {
				Util.log("ServerThread.run waiting for client "+name);
				line = in.readLine();
				String outString = server.executeCommand(name,line);
				out.println(outString);
			}
		} catch (Exception e) {
			Util.log(e);
		}
	}    

}

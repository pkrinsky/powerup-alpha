package powerup.network;

import java.io.BufferedReader;
import java.io.PrintWriter;

import powerup.engine.Util;
import powerup.field.Robot;

public class ServerThread extends Thread {
	
	private BufferedReader in;
	private PrintWriter out;
	private GameServer server;
	private boolean running = true;
	private Robot robot;
	
	
	public ServerThread(BufferedReader in, PrintWriter out, Robot robot, GameServer server) {
		super();
		this.in = in;
		this.out = out;
		this.robot = robot;
		this.server = server;
	}

	
	public void shutdown() {
		running = false;
	}
	
    public void run() {
		String line ="";
		try {
			while (running) {
				Util.log("ServerThread.run waiting for robot "+robot.getName());
				line = in.readLine();
				String outString = server.execute(robot.getName(),line);
				out.println(outString);
			}
		} catch (Exception e) {
			Util.log(e.getMessage());
		}
	}    

}

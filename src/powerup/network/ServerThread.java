package powerup.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import powerup.engine.Util;
import powerup.field.Robot;

public class ServerThread extends Thread {
	
	private BufferedReader in;
	private PrintWriter out;
	private GameServer server;
	private boolean running = true;
	private Robot robot;
	
	public ServerThread(String name) {
		super(name);
	}

	public void setup(GameServer server, Socket clientSocket, Robot robot) throws IOException {
		this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		this.out = new PrintWriter(clientSocket.getOutputStream(),true);
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

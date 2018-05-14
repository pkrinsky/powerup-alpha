package powerup.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
		
		List<String> fieldList = new ArrayList<String>();
		try {
			while (running) {
				Util.log("ServerThread.run waiting for robot "+robot.getName());
				line = in.readLine();
				Util.log("ServerThread.run received line:["+line+"]");
					
				StringTokenizer fieldTokens = new StringTokenizer(line, GameClient.DELIM);
				fieldList.clear();
				while (fieldTokens.hasMoreTokens()) {
					fieldList.add(fieldTokens.nextToken());
				}
				
				String command = fieldList.get(0);
				//Util.log("ServerThread.run command:["+command+"]");
				
				if (GameServer.COMMAND_EXIT.equals(command)) {
					//Util.log("ServerThread.run exit:"+fieldList.get(1));
					running = false;
				}
				
				if (GameServer.COMMAND_MOVE.equals(command)) {
					//Util.log("ServerThread.run move:"+fieldList.get(1));
					String c = fieldList.get(1);
					int i = new Integer(fieldList.get(2));
					server.move(c,i);
				}
				
				if (GameServer.COMMAND_REGISTER.equals(command)) {
					//Util.log("ServerThread.run register robot:"+fieldList.get(1));
					robot.setName(fieldList.get(1));
					robot.setHasCube(true);
					server.setup(robot);	
				}				
				
				if (GameServer.COMMAND_GET_FIELD.equals(command)) {
					String f = server.getFieldAsString(fieldList.get(1));
					//Util.log("ServerThread.run println fieldString:"+f);
					out.println(f);
				}
			}
			Util.log("ServerThread.run done");
		} catch (Exception e) {
			Util.log(e.getMessage());
		}
	}    

}

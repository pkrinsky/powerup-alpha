package powerup.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import powerup.engine.Util;
import powerup.field.Cube;
import powerup.field.Field;
import powerup.field.Robot;
import powerup.field.Scale;
import powerup.robot.Autobot;

public class GameServer {
	
	public static final String COMMAND_GET_FIELD = "GET_FIELD";
	public static final String COMMAND_MOVE = "MOVE";
	public static final String COMMAND_REGISTER = "REGISTER";
	public static final String COMMAND_EXIT = "EXIT";
	public static final String COMMAND_START = "START";
	
	private long lastScoreSecs = 0;
	private String gamedata = "LRL";
	private boolean running = false;
	
	private Field field = new Field();
	
	public synchronized String executeCommand(String name, String request) {
		String returnString = "";
		
		Util.log("GameServer.executeCommand robot:"+name+" recv:"+request);
		
		List<String> fieldList = new ArrayList<String>();
		StringTokenizer fieldTokens = new StringTokenizer(request, GameClient.DELIM);
		fieldList.clear();
		while (fieldTokens.hasMoreTokens()) {
			fieldList.add(fieldTokens.nextToken());
		}
		
		String command = fieldList.get(0);
		
		if (GameServer.COMMAND_EXIT.equals(command)) {
			Util.log("ServerThread.run exit:"+fieldList.get(1));
			running = false;
		}
		
		if (GameServer.COMMAND_MOVE.equals(command)) {
			//Util.log("ServerThread.run move:"+fieldList.get(1));
			String c = fieldList.get(1);
			int i = new Integer(fieldList.get(2));
			if (running) {
				move(c,i);
			} else {
				Util.log("ServerThread.execute no moves while game not running");
			}
		}
		
		if (GameServer.COMMAND_REGISTER.equals(command)) {
			if (!running) {
				Util.log("GameServer.execute register robot:"+fieldList.get(1)+" position:"+fieldList.get(2));
				int position = new Integer(fieldList.get(2));
				Robot robot = null;
				switch (position) {
					case 1:
						robot = new Robot(fieldList.get(1),Robot.BLUE,gamedata,Field.LEFT);
						break;
					case 2:
						robot = new Robot(fieldList.get(1),Robot.RED,gamedata,Field.LEFT);
						break;
					case 3:
						robot = new Autobot("993",Robot.BLUE,gamedata,Field.MIDDLE);
						robot.setAi(true);
						break;
					case 4:
						robot = new Autobot("994",Robot.RED,gamedata,Field.MIDDLE);
						robot.setAi(true);
						break;
					case 5:
						robot = new Autobot("995",Robot.BLUE,gamedata,Field.RIGHT);
						robot.setAi(true);
						break;
					case 6:
						robot = new Autobot("996",Robot.RED,gamedata,Field.RIGHT);
						robot.setAi(true);
						break;
				}
				setup(robot);
				returnString = robot.getName();
			}
		}				
		
		if (GameServer.COMMAND_GET_FIELD.equals(command)) {
			String f = getFieldAsString(fieldList.get(1));
			returnString = f;
		}
		
		if (GameServer.COMMAND_START.equals(command)) {
			Util.log("ServerThread.execute start");
			running = true;
		}
		
		//Util.log("GameServer.execute robot:"+name+" response:["+returnString+"]");
		
		return returnString;

	}
	
	private void run(int listenPort) throws SocketException {
		boolean listen = true;
		ServerSocket serverSocket;
		List<ServerThread> threadList = new ArrayList<ServerThread>();
		try {
			serverSocket = new ServerSocket(listenPort);
			Socket clientSocket = null;

			while (listen) {
				Util.log("GameServer.run Waiting for client on "+listenPort);
				clientSocket = serverSocket.accept();
				Util.log("GameServer.run Client connection from "+clientSocket.getInetAddress().getHostAddress()+" "+clientSocket.getPort());
				
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
				String request = in.readLine();
				out.println("");
				List<String> fieldList = new ArrayList<String>();
				StringTokenizer fieldTokens = new StringTokenizer(request, GameClient.DELIM);
				fieldList.clear();
				while (fieldTokens.hasMoreTokens()) {
					fieldList.add(fieldTokens.nextToken());
				}
				String clientName = fieldList.get(1);
				ServerThread thread = new ServerThread(in,out,clientName,this);
				threadList.add(thread);
				thread.start();
			}
			for (ServerThread t:threadList) {
				t.shutdown();
			}
			serverSocket.close();
		} catch (Exception e) {
			Util.log(e.getMessage());
		}
	}	

	private Field setupField() {
		Util.log("GameServer.setupField");
		
		field = Field.getStaticField();

		
		// need to randomize this
		// [close switch][scale][far switch]
		
		
		//robotList.add(new Autobot("101",Robot.BLUE,gamedata,Field.LEFT));
		//robotList.add(new Autobot("102",Robot.BLUE,gamedata,Field.MIDDLE));
		//robotList.add(new Autobot("103",Robot.BLUE,gamedata,Field.RIGHT));
		//robotList.add(new Autobot("106",Robot.RED,gamedata,Field.LEFT));
		//robotList.add(new Autobot("105",Robot.RED,gamedata,Field.MIDDLE));
		//robotList.add(new Autobot("104",Robot.RED,gamedata,Field.RIGHT));
		
		
		for (int i=0;i<5;i++) {
			field.set(Field.COL1+1,5+i,new Cube());
			field.set(Field.COL1-1,5+i,new Cube());
			field.set(Field.COL3+1,5+i,new Cube());
			field.set(Field.COL3-1,5+i,new Cube());
		}
		
		
		//field.print();

		return field;
	}	
	
	
	public static void main(String[] args) {
		GameServer server = new GameServer();
		server.startGame();
		int listenPort = 9001;
		try {
			server.run(listenPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void calcScore() {
		Scale r = (Scale) field.find("RS");
		Scale b = (Scale) field.find("BS");
		if (r.getNumCubes() > b.getNumCubes()) {
			field.increaseRedScore(1);
		}
		if (b.getNumCubes() > r.getNumCubes()) {
			field.increaseBlueScore(1);
		}
		r = (Scale) field.find("RFS");
		b = (Scale) field.find("BNS");
		if (b.getNumCubes() > r.getNumCubes()) {
			field.increaseBlueScore(1);
		}
		r = (Scale) field.find("RNS");
		b = (Scale) field.find("BFS");
		if (r.getNumCubes() > b.getNumCubes()) {
			field.increaseRedScore(1);
		}
	}	
	

	
	private Field getField(String name) {
		//Util.log("GameServer.getField name:"+name+" secs:"+field.getGameSecs());
		if (running) {
		
			// move the ai
			int move = Robot.STOP;
			for (Robot r:field.getRobotList()) {
				if (r.isAi()) {
					Util.log("GameServer.move ai "+r.getName());
					move = r.move(field);
					field.move(r.getName(),move);
				}
			}
			
			if (field.getGameSecs() == 0) {
				running = false;
			} else {
				field.decreaseGameSecs(1);
			}
			
			if (field.getGameSecs() != lastScoreSecs) {
				calcScore();
				lastScoreSecs = field.getGameSecs();
			}
			
		}
		return field;
	}
	
	private String getFieldAsString(String name) {
		return getField(name).save();
	}
	
	private void move(String name, int command) {
		field.move(name, command);
	}
	
	private void setup(Robot robot) {
		field.setup(robot);;
	}
	
	public void startGame() {
		Util.log("GameServer.startGame");
		setupField();
	}
	


}

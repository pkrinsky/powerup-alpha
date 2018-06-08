package powerup.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import powerup.engine.Util;
import powerup.field.Cube;
import powerup.field.Field;
import powerup.field.FieldLayout;
import powerup.field.FieldObject;
import powerup.field.Robot;
import powerup.field.Scale;
import powerup.robot.Autobot;
import powerup.robot.PaulBot;

public class GameServer {
	
	public static final String COMMAND_GET_FIELD = "GET_FIELD";
	public static final String COMMAND_MOVE = "MOVE";
	public static final String COMMAND_REGISTER = "REGISTER";
	public static final String COMMAND_EXIT = "EXIT";
	public static final String COMMAND_START = "START";
	public static final String COMMAND_RESTART = "RESTART";
	public static final String COMMAND_PAUSE = "PAUSE";
	public static final String COMMAND_AI_FASTER = "AI_FASTER";
	
	private long lastScoreSecs = 0;
	//private long nextAICheck = 0;
	private int turn = 0;
	private long nextAIMove = 0;

	private String gameData = "LRL";
	private boolean running = false;
	
	private Field field = new Field();
	private Random random = new Random();
	
	private Robot newRobot(String name, String alliance, String gameData, char startingPosition) {
		return new PaulBot(name,alliance,gameData,startingPosition);
	}
	
	private Robot newAutobot(String name, String alliance, String gameData, char startingPosition) {
		return new Autobot(name,alliance,gameData,startingPosition);
	}
	
	public synchronized String executeCommand(String name, String request) {
		String returnString = "";
		
		Util.log("GameServer.executeCommand robot:"+name+" recv:"+request,10);
		
		List<String> fieldList = new ArrayList<String>();
		StringTokenizer fieldTokens = new StringTokenizer(request, GameClient.DELIM);
		fieldList.clear();
		while (fieldTokens.hasMoreTokens()) {
			fieldList.add(fieldTokens.nextToken());
		}
		
		String command = fieldList.get(0);
		
		if (GameServer.COMMAND_EXIT.equals(command)) {
			Util.log("GameServer.run exit:"+fieldList.get(1));
			running = false;
		}
		
		if (GameServer.COMMAND_MOVE.equals(command)) {
			//Util.log("ServerThread.run move:"+fieldList.get(1));
			String c = fieldList.get(1);
			int i = new Integer(fieldList.get(2));
			if (running && field.getCountDown() == 0) {
				move(c,i);
			} else {
				Util.log("GameServer.execute no moves while game not running or in countdown running:"+running+ " countdown:"+field.getCountDown());
			}
		}
		
		if (GameServer.COMMAND_AI_FASTER.equals(command)) {
			Util.log("GameServer.execute AI_FASTER");
			field.increaseRobotLevel();
		}
		
		if (GameServer.COMMAND_REGISTER.equals(command)) {
			if (!running) {
				Util.log("GameServer.execute register robot:"+fieldList.get(1)+" position:"+fieldList.get(2));
				int position = new Integer(fieldList.get(2));
				Robot robot = null;
				switch (position) {
					case 0:
						robot = newRobot("PK1",Robot.RED,gameData,Field.RIGHT);
						robot.setAi(true);
						setup(robot);
						robot = newRobot("PK2",Robot.RED,gameData,Field.MIDDLE);
						robot.setAi(true);
						setup(robot);
						robot = newRobot("PK3",Robot.RED,gameData,Field.LEFT);
						robot.setAi(true);
						setup(robot);
						robot = newAutobot("MK1",Robot.BLUE,gameData,Field.RIGHT);
						robot.setAi(true);
						setup(robot);
						robot = newAutobot("MK2",Robot.BLUE,gameData,Field.MIDDLE);
						robot.setAi(true);
						setup(robot);
						robot = newAutobot("MK3",Robot.BLUE,gameData,Field.LEFT);
						robot.setAi(true);
						setup(robot);
						break;
					case 1:
						robot = newRobot(fieldList.get(1),Robot.BLUE,gameData,Field.RIGHT);
						setup(robot);
						break;
					case 2:
						robot = newRobot(fieldList.get(1),Robot.BLUE,gameData,Field.MIDDLE);
						setup(robot);
						break;
					case 3:
						robot = newRobot(fieldList.get(1),Robot.BLUE,gameData,Field.LEFT);
						setup(robot);
						break;
					case 4:
						robot = newRobot(fieldList.get(1),Robot.RED,gameData,Field.RIGHT);
						setup(robot);
						break;
					case 5:
						robot = newRobot(fieldList.get(1),Robot.RED,gameData,Field.MIDDLE);
						setup(robot);
						break;
					case 6:
						robot = newRobot(fieldList.get(1),Robot.RED,gameData,Field.LEFT);
						setup(robot);
						break;
					case 7:
						int players = field.getRobotList().size();
						robot = newRobot("AI1",Robot.RED,gameData,Field.RIGHT);
						robot.setAi(true);
						setup(robot);
						if (players >1) {
							robot = newRobot("AI2",Robot.RED,gameData,Field.MIDDLE);
							robot.setAi(true);
							setup(robot);
						}
						if (players >2) {
							robot = newRobot("AI3",Robot.RED,gameData,Field.LEFT);
							robot.setAi(true);
							setup(robot);
						}
						break;
				}
				returnString = robot.getName();
			}
		}				
		
		if (GameServer.COMMAND_GET_FIELD.equals(command)) {
			String f = getField(fieldList.get(1)).save();
			returnString = f;
		}
		
		if (GameServer.COMMAND_START.equals(command)) {
			Util.log("ServerThread.execute start");
			running = true;
			field.setGameSecs(Field.GAME_SECS);
			field.setCountDown(Field.COUNTDOWN);
		}
		
		if (GameServer.COMMAND_RESTART.equals(command)) {
			
			if (!running) {
				Util.log("ServerThread.run RESTART");
				setupGame();
			} else {
				Util.log("ServerThread.run cant RESTART while game running");	
			}
		}
		
		if (GameServer.COMMAND_PAUSE.equals(command)) {
			Util.log("ServerThread.execute pause");
			if (running) {
				running = false;
			} else {
				running = true;
			}
		}
		
		//Util.log("GameServer.execute robot:"+name+" response:["+returnString+"]");
		
		return returnString;

	}
	
	private void run(int listenPort) throws SocketException {
		Util.log("GameServer.run listenPort:"+listenPort);
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
			Util.log(e);
		}
	}	

	private Field setupField() {
		Util.log("GameServer.setupField");
		
		field = FieldLayout.getStaticField();
		
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
		server.setupGame();
		int listenPort = 9001;
		if (args.length > 0)
			listenPort = new Integer(args[0]);
		try {
			server.run(listenPort);
		} catch (SocketException e) {
			Util.log(e);
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
	
	private void spawnCornerCubes() {
		FieldObject fo;
		int r = 0;
		
		List<Cube> cubeList = field.getCubeList();
		
		if (cubeList == null) 
			r = 1;
		else if (cubeList.size() < 5)
			r = random.nextInt(2);
		
		if (r == 1) {
			r = random.nextInt(4);
			
			switch (r) {
				case 1:
					fo = field.getFieldObject(Field.COLS-1,Field.ROWS-1);
					if (fo == null) {
						field.set(Field.COLS-1,Field.ROWS-1,new Cube());
					}
					break;
				case 2:					
					fo = field.getFieldObject(0,Field.ROWS-1);
					if (fo == null) {
						field.set(0,Field.ROWS-1,new Cube());
					}
					break;
				case 3:					
					fo = field.getFieldObject(0,0);
					if (fo == null) {
						field.set(0,0,new Cube());
					}
					break;
				case 0:					
					fo = field.getFieldObject(Field.COLS-1,0);
					if (fo == null) {
						field.set(Field.COLS-1,0,new Cube());
					}
					break;
			}
		
		}
		
	}
	

	
	private Field getField(String name) {
		// boolean blueHasAI = false;
		// boolean redHasAI = false;
		
		Util.log("GameServer.getField name:"+name+" secs:"+field.getGameSecs()+" turn:"+turn,10);
		
		if (running) {
			
			if (running && field.getCountDown() == 0) {
				turn++;
				// check to see if we should spawn more cubes
				spawnCornerCubes();
				
				
				// move the ai
				int move = Robot.STOP;
				if (System.currentTimeMillis() > nextAIMove ) {
					for (Robot r:field.getRobotList()) {
						if (r.isAi()) {
							
							//if (Robot.BLUE.equals(r.getAlliance())) blueHasAI = true; 
							//else redHasAI = true;
							
							try {
								Util.log("GameServer.move ai "+r.getName());
								move = r.getMove(field);
								field.move(r.getName(),move);
							} catch (Exception e) {
								Util.log(e);
							}
						}
					}
					nextAIMove = System.currentTimeMillis() + (525-(field.getRobotLevel()*50));
					
					/*
					if (System.currentTimeMillis() > nextAICheck && field.getRobotLevel() <= 6) {
						if ((blueHasAI == true && redHasAI == false && field.getRedScore()-field.getBlueScore() > 2) 
								|| (blueHasAI== false && redHasAI == true && field.getBlueScore()-field.getRedScore() > 2)) 
						{
							field.increaseRobotLevel();
							Util.log("\n\n\n\n\nIncreasing AI level to "+field.getRobotLevel());
						}
						nextAICheck = System.currentTimeMillis() + 5000;
						//Util.log("\n\n"+blueHasAI+" "+redHasAI+ " next "+nextAICheck+" diff "+Math.abs(field.getBlueScore()-field.getRedScore()));
					}
					*/
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
	

	
	private void move(String name, int command) {
		field.move(name, command);
	}
	
	private void setup(Robot robot) {
		field.setup(robot);;
	}
	
	public void setupGame() {
		Util.log("GameServer.setupGame");
		setupField();
	}
	


}

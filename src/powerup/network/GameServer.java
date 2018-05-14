package powerup.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import powerup.engine.Util;
import powerup.field.Cube;
import powerup.field.Field;
import powerup.field.Robot;
import powerup.field.Scale;

public class GameServer {
	
	public static final String COMMAND_GET_FIELD = "GET_FIELD";
	public static final String COMMAND_MOVE = "MOVE";
	public static final String COMMAND_REGISTER = "REGISTER";
	public static final String COMMAND_EXIT = "EXIT";
	
	private long lastScoreSecs = 0;
	private String gamedata = "LRL";

	
	private Field field = new Field();
	private List<Robot> robotList = new ArrayList<Robot>();

	public Field setupField() {
		Util.log("GameServer.setupField");
		
		field = Field.getStaticField();

		
		// need to randomize this
		// [close switch][scale][far switch]
		
		
		//robotList.add(new Robot("001",Robot.BLUE,gamedata,Field.MIDDLE));
		//robotList.add(new Autobot("004",Robot.RED,gamedata,Field.LEFT));
		//robotList.add(new Autobot("005",Robot.RED,gamedata,Field.MIDDLE));
		//robotList.add(new Autobot("006",Robot.RED,gamedata,Field.RIGHT));
		
		
		//for (Robot r:robotList) {
		//	r.setHasCube(true);
		//	field.setup(r);	
		//}
		
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
		Util.log("GameServer.getField name:"+name+" secs:"+field.getGameSecs());
		if (field.getGameSecs() > 0) {
		
			field.decreaseGameSecs(1);
			
			if (field.getGameSecs() != lastScoreSecs) {
				calcScore();
				lastScoreSecs = field.getGameSecs();
			}
			
			// move the ai
			int move = Robot.STOP;
			for (Robot r:robotList) {
				// ask the ai if they want to move
				move = r.move(field);
				field.move(r.getName(),move);
			}
		}
		return field;
	}
	
	public String getFieldAsString(String name) {
		return getField(name).save();
	}
	
	
	public void move(String name, int command) {
		field.move(name, command);
	}
	
	public void setup(Robot robot) {
		field.setup(robot);;
	}
	
	public void startGame() {
		Util.log("GameServer.startGame");
		setupField();
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
				String robotName = "000";
				Robot robot = null;
				switch (threadList.size()) {
					case 0:
						robot = new Robot(robotName,Robot.BLUE,gamedata,Field.MIDDLE);
						break;
					case 1:					
						robot = new Robot(robotName,Robot.RED,gamedata,Field.MIDDLE);
						break;
				}
				Util.log("GameServer.run setup robot "+robot.getName());
				robotList.add(robot);
				ServerThread thread = new ServerThread(robotName);
				threadList.add(thread);
				thread.setup(this, clientSocket, robot);
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
	

	

}

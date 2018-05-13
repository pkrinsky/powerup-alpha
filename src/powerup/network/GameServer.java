package powerup.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
	public static final String COMMAND_EXIT = "EXIT";
	
	private long lastScoreSecs = 0;
	
	private Field field = new Field();
	private List<GameClient> connectionList = new ArrayList<GameClient>();
	private List<Robot> robotList = new ArrayList<Robot>();

	public Field setupField() {
		Util.log("GameServer.setupField");
		
		field = Field.getStaticField();

		
		// need to randomize this
		// [close switch][scale][far switch]
		
		String gamedata = "LRL";
		
		robotList.add(new Robot("001",Robot.BLUE,gamedata,Field.MIDDLE));
		robotList.add(new Autobot("004",Robot.RED,gamedata,Field.LEFT));
		robotList.add(new Autobot("005",Robot.RED,gamedata,Field.MIDDLE));
		robotList.add(new Autobot("006",Robot.RED,gamedata,Field.RIGHT));
		
		
		for (Robot r:robotList) {
			r.setHasCube(true);
			field.setup(r);	
		}
		
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
			server.start(listenPort);
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
	
	public void addClient(GameClient client) {
		Util.log("GameServer.addClient");
		connectionList.add(client);
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
	
	public void startGame() {
		Util.log("GameServer.startGame");
		setupField();
	}
	
	private void start(int listenPort) throws SocketException {
		boolean listen = true;
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(listenPort);
			Socket clientSocket = null;
			BufferedReader in;
			PrintWriter out;
			
			while (listen) {
				Util.log("Waiting for client on "+listenPort);
				clientSocket = serverSocket.accept();
				Util.log("Client connection");
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				out = new PrintWriter(clientSocket.getOutputStream(),true);
				processClient(in,out);
			}
			clientSocket.close();
			Util.log("Client socket closed");
			serverSocket.close();
		} catch (Exception e) {
			Util.log(e.getMessage());
		}
	}
	
	private void processClient(BufferedReader in, PrintWriter out) {
		String line ="";
		boolean done = false;
		List<String> fieldList = new ArrayList<String>();
		try {
			while (!done) {
				Util.log("GameServer.processClient waiting for client");
				line = in.readLine();
				Util.log("GameServer.processClient received line:["+line+"]");
					
				StringTokenizer fieldTokens = new StringTokenizer(line, GameClient.DELIM);
				fieldList.clear();
				while (fieldTokens.hasMoreTokens()) {
					fieldList.add(fieldTokens.nextToken());
				}
				String command = fieldList.get(0);
				Util.log("Client command:["+command+"]");
				
				if (COMMAND_EXIT.equals(command)) {
					Util.log("GameServer.processClient move:"+fieldList.get(1));
					done = true;
				}
				
				if (COMMAND_MOVE.equals(command)) {
					Util.log("GameServer.processClient move:"+fieldList.get(1));
					String c = fieldList.get(1);
					int i = new Integer(fieldList.get(2));
					move(c,i);
				}
				
				if (COMMAND_GET_FIELD.equals(command)) {
					String f = getFieldAsString(fieldList.get(1));
					//Util.log("GameServer.processClient println fieldString:"+f);
					out.println(f);
				}
			}
			Util.log("GameServer.prcessClient done");
		} catch (Exception e) {
			Util.log(e.getMessage());
		}
	}
	

}

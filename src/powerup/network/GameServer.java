package powerup.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
		
		robotList.add(new Autobot("001",Robot.BLUE,gamedata,Field.MIDDLE));
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
		//Util.log("GameServer.getField name:"+name+" secs:"+field.getGameSecs());
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
	
	public void move(String s) {
		String name = null;
		int command = -1;
		
		StringTokenizer fieldTokens = new StringTokenizer(s, GameClient.DELIM);
		List<String> fieldList = new ArrayList<String>();
		while (fieldTokens.hasMoreTokens()) {
			fieldList.add(fieldTokens.nextToken());
		}
		
		name=fieldList.get(0);
		command = new Integer(fieldList.get(1));
		
		move(name,command);
	}
	
	private void move(String name, int command) {
		field.move(name, command);
	}	
	
	public void startGame() {
		Util.log("GameServer.startGame");
		setupField();
	}
	
	
	private void start(int listenPort) throws SocketException {
		boolean listen = true;
		
		DatagramSocket socket = new DatagramSocket(listenPort);
		System.out.println("GameServer.start listening on "+listenPort);
		
		while (listen) {
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
				String received = new String(packet.getData(), 0, packet.getLength());
				Util.log("GameServer.start received packet s:"+received);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//InetAddress address = packet.getAddress();
			//int port = packet.getPort();
			//packet = new DatagramPacket(buf, buf.length, address, port);
			//socket.send(packet);
			
		}
		socket.close();
	}
}

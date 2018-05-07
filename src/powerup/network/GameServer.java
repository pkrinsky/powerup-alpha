package powerup.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import powerup.engine.Util;
import powerup.field.Cube;
import powerup.field.Field;
import powerup.field.Robot;
import powerup.field.Scale;
import powerup.field.Wall;
import powerup.robot.Autobot;

public class GameServer {
	
	private long lastScoreSecs = 0;
	
	private Field field = new Field();
	private List<GameClient> connectionList = new ArrayList<GameClient>();
	private List<Robot> robotList = new ArrayList<Robot>();

	public Field setupField() {
		Util.log("GameServer.setupField");
		
		field = new Field();

		
		// need to randomize this
		// [close switch][scale][far switch]
		
		String gamedata = "LRL";
		
		int col2 = Field.COL2;
		int col1 = Field.COL1;
		int col3 = Field.COL3;
				
		robotList.add(new Autobot("004",Robot.RED,gamedata,Field.LEFT));
		robotList.add(new Autobot("005",Robot.RED,gamedata,Field.MIDDLE));
		robotList.add(new Autobot("006",Robot.RED,gamedata,Field.RIGHT));
		
		
		for (GameClient gc:connectionList) {
			field.setup(gc.getController().getRobot());	
		}
		
		for (Robot r:robotList) {
			field.setup(r);	
		}
		
		
		field.setup(col2,3,new Scale("RS",Robot.RED)); 
		field.setup(col2,4,new Wall());
		field.setup(col2,5,new Wall());
		field.setup(col2,6,new Wall());
		field.setup(col2,7,new Wall());
		field.setup(col2,8,new Wall());
		field.setup(col2,9,new Wall());
		field.setup(col2,10,new Wall());
		field.setup(col2,11,new Scale("BS",Robot.BLUE));

		field.setup(col1,4,new Scale("BNS",Robot.BLUE));
		field.setup(col1,5,new Wall());
		field.setup(col1,6,new Wall());
		field.setup(col1,7,new Wall());
		field.setup(col1,8,new Wall());
		field.setup(col1,9,new Wall());
		field.setup(col1,10,new Scale("RFS",Robot.RED));

		field.setup(col3,4,new Scale("BFS",Robot.BLUE));
		field.setup(col3,5,new Wall());
		field.setup(col3,6,new Wall());
		field.setup(col3,7,new Wall());
		field.setup(col3,8,new Wall());
		field.setup(col3,9,new Wall());
		field.setup(col3,10,new Scale("RNS",Robot.RED));
		
		
		for (int i=0;i<5;i++) {
			field.setup(col1+1,5+i,new Cube());
			field.setup(col1-1,5+i,new Cube());
			field.setup(col3+1,5+i,new Cube());
			field.setup(col3-1,5+i,new Cube());
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
	
	public void move(String name, int command) {
		field.move(name, command);
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
	
	
	
	public Field getField(String name) {
		Util.log("GameServer.getField name:"+name);
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

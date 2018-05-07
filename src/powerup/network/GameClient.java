package powerup.network;

import powerup.engine.RobotController;
import powerup.engine.Util;
import powerup.field.Field;
import powerup.field.Robot;
import powerup.robot.RobotRex;

public class GameClient {
	
	//private List<RobotController> robotControllerList= new ArrayList<RobotController>();
	private RobotController controller = null;
	//private String serverAddress = "localhost";
	//private int serverPort = 9001;
	private GameServer server = null;
	
	public static void main(String[] args) {
		GameClient client = new GameClient();
		client.setup();
		client.gameLoop();
		//client.send(-1);
	}
	
	public void setup() {
		Util.log("GameClient.setup");
		String gamedata = "LRL";
		server = new GameServer();
		
		controller = new RobotController(new RobotRex("001",Robot.BLUE,gamedata,Field.MIDDLE));
		controller.setup();
		server.addClient(this);
		
		server.startGame();
		gameLoop();
		
	}
	
	private void gameLoop() {
		Util.log("GameClient.gameLoop");
		boolean gameRunning = true;
		Field field = null;
		String name = controller.getRobot().getName();
			
		while (gameRunning) {
			if (field == null || field.getGameSecs() > 0) {
				field = server.getField(name);
				int move = controller.move(field);
				if (move != Robot.STOP) {
					server.move(name,move);
					field = server.getField(name);
				}
				controller.drawField(field);
			}

			// wait for a little then start again
			try { Thread.sleep(250); } catch (Exception e) {}
		}
	}
	
	public void move(Field field) {
		Util.log("GameClient.move");
		controller.move(field);
	}

	/*
	
	private void send(int command) {
		// if there is a local server use it otherwise send it across the network
		if (server == null) {
			String s = ""+command;
			byte[] buf = s.getBytes();
			
			try {
				DatagramSocket socket = new DatagramSocket();
				InetAddress address = InetAddress.getByName(serverAddress);
				DatagramPacket packet = new DatagramPacket(buf, buf.length, address, serverPort);
				socket.send(packet);
				packet = new DatagramPacket(buf, buf.length, address, serverPort);
				Util.log("GameClient.sending s:"+s);
				socket.send(packet);
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		} else {
			server.move(command);
		}
	}
		*/
	
	public void key(char key) {
		controller.key(key);
	}

	public RobotController getController() {
		return controller;
	}

	public void setController(RobotController controller) {
		this.controller = controller;
	}
	
	
}

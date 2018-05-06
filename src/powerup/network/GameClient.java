package powerup.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import powerup.engine.Util;
import powerup.field.Field;
import powerup.field.Robot;
import powerup.robot.RobotRex;

public class GameClient {
	
	//private List<RobotController> robotControllerList= new ArrayList<RobotController>();
	private Robot robot = null;
	private String serverAddress = "localhost";
	private int serverPort = 9001;
	private GameServer server = null;
	
	public static void main(String[] args) {
		GameClient client = new GameClient();
		client.send(-1);
	}
	
	public void setup() {
		String gamedata = "LRL";
		server = new GameServer();
		
		robot = new RobotRex("001",Robot.BLUE,gamedata,Field.MIDDLE);
		
		//robotControllerList.add(new RobotController(new RobotRex("001",Robot.BLUE,gamedata,Field.MIDDLE)));
		//robotControllerList.add(new RobotController(new Autobot("002",Robot.BLUE,gamedata,Field.LEFT)));
		//robotControllerList.add(new RobotController(new Autobot("003",Robot.BLUE,gamedata,Field.RIGHT)));
		//robotControllerList.add(new RobotController(new Autobot("004",Robot.RED,gamedata,Field.LEFT)));
		//robotControllerList.add(new RobotController(new Autobot("005",Robot.RED,gamedata,Field.MIDDLE)));
		//robotControllerList.add(new RobotController(new Autobot("006",Robot.RED,gamedata,Field.RIGHT)));
		//myController = robotControllerList.get(0);
		
		//for (RobotController rc:robotControllerList) {
		//	field.setup(rc.getRobot());	
		//}
		
	}
	
	public void move(Field field) {
		int m = robot.move(field);
		send(m);
		// move the robots
		//for (RobotController r:robotControllerList) {
			//r.move(field);
		//}
	}
	
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
	
	public void key(char key) {
		robot.key(key);
	}
}

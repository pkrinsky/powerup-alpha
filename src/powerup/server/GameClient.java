package powerup.server;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;

import powerup.engine.GraphicsController;
import powerup.engine.Util;
import powerup.field.Field;
import powerup.field.FieldLayout;
import powerup.field.Robot;
import powerup.robot.BasicAutoBot;

public class GameClient {
	public static final String DELIM="|";
	public static final String ROW_DELIM="~";
	public static final int DELAY = 200;
	
	private GraphicsController controller = null;
	private String serverAddress = null;
	private int serverPort = 0;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private GameServer server = null;
	private String name = "000";
	private Robot robot = null;
	boolean clientRunning = false;
	boolean gameRunning = false;
	boolean autonomous = false;

	public boolean isAutonomous() {
		return autonomous;
	}
	
	protected Robot newRobot() {
		return new BasicAutoBot();
	}

	public static void main(String[] args) {
		GameClient client = new GameClient();
		String name = null;
		String debug = null;
		String serverAddress = null;
		String serverPort = null;
		
		if (args.length >= 1) {
			name = args[0];
		}
		if (args.length >= 2) {
			debug = args[1];
			Util.setDebugLevel(new Integer(debug));
		}
		if (args.length >= 3) {
			serverAddress = args[2];
		}
		if (args.length >= 4) {
			serverPort = args[3];
		}
			
		client.setup(serverAddress, serverPort, name);
		client.gameLoop();
	}
	
	public void keyEvent(KeyEvent e) {
		Util.log("GameClient.keyEvent keyChar:"+e.getKeyChar()+" code:"+e.getKeyCode());

		if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
		
		if (e.getKeyChar() == '1') {
			sendRegister(1);
		} else if (e.getKeyChar() == '2') {
			sendRegister(2);
		} else if (e.getKeyChar() == '3') {
			sendRegister(3);
		} else if (e.getKeyChar() == '4') {
			sendRegister(4);
		} else if (e.getKeyChar() == '5') {
			sendRegister(5);
		} else if (e.getKeyChar() == '6') {
			sendRegister(6);
		} else if (e.getKeyChar() == '7') {
			sendRegister(7);
		} else if (e.getKeyChar() == 't') {
			sendRegister(0);
		} else if (e.getKeyChar() == '8') {
			sendAIHard();
		} else if (e.getKeyChar() == '9') {
			sendStart();
		} else if (e.getKeyChar() == '0') {
			sendRestart();
		} else if (e.getKeyChar() == 'p') {
			sendPause();
		} else if (clientRunning && !autonomous) {
				robot.handleKey(e);
		} else {
			Util.log("GameClient.keyEvent ignored in autonomous mode");
		}
			
	}
	
	private Robot getRobot(Field field, String robotName) {
		Robot robot = (Robot) field.find(robotName);
		return robot;
	}
	
	private int getMove(Field field) {
		int move = Robot.STOP;
		
		// get the next move
		move = robot.move(field);
		
		// send to the server if needed
		if (move != Robot.STOP) {
			Util.log("GameClient.move "+name+" move:"+Robot.getCommandName(move));
		}
		
		return move;
	}	
	
	public void setup(String serverAddress, String serverPort, String name) {
		robot = newRobot();
		
		if (name != null) {
			this.name = name;
			robot.setName(name);
		}
		
		if (serverAddress == null) {
			Util.log("GameClient.setup local server");
			server = new GameServer();
			server.setupGame();	
		} else {
			Util.log("GameClient.setup network server "+serverAddress+" "+serverPort);
			this.serverAddress = serverAddress;
			this.serverPort = new Integer(serverPort).intValue();
			
			try {
				socket = new Socket(this.serverAddress,this.serverPort);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(),true);
				
			} catch (UnknownHostException e) {
				Util.log(e);
			} catch (ConnectException e) {
				Util.log("GameClient.setup could not connect to server");
				Util.log("GameClient.setup exiting");
				System.exit(1);
			} catch (IOException e) {
				Util.log(e);
			}

		}

	}	
	
	private String executeCommand(String request) {
		String returnString = null;
		Util.log("GameClient.executeCommand robot:"+name+" sent:"+request,10);
		if (server == null) {
			try {
				out.println(request);
				returnString = in.readLine();
			} catch (UnknownHostException e) {
				Util.log(e);
			} catch (ConnectException e) {
				Util.log("GameClient.setup could not connect to server");
				Util.log("GameClient.setup exiting");
				System.exit(1);
			} catch (IOException e) {
				Util.log(e);
			}			
		} else {
			returnString = server.executeCommand(name, request);
		}
		if (returnString != null)
			Util.log("GameClient.executeCommand received:"+returnString.replace(ROW_DELIM.charAt(0), '\n'),5);
		return returnString;
	}
	
	private void getFieldData(Field field) {
		String s = null;
		//Util.log("GameClient.getFieldData requesting from server");
		StringBuffer sb = new StringBuffer();
		sb.append(GameServer.COMMAND_GET_FIELD);
		sb.append(DELIM);
		sb.append(name);
		sb.append(DELIM);	
		s = executeCommand(sb.toString());
		//Util.log("GameClient.getFieldData returned "+s);
				
		if (s!= null) field.load(s);
	}
	
	private void sendMove(int command) {
		Util.log("GameClient.sendMove "+command);
		
		StringBuffer sb = new StringBuffer();
		sb.append(GameServer.COMMAND_MOVE);
		sb.append(DELIM);
		sb.append(name);
		sb.append(DELIM);
		sb.append(command);
		sb.append(DELIM);
		executeCommand(sb.toString());
	}	
	
	private void sendRegister(int position) {
		Util.log("GameClient.sendRegister position:"+position);
		
		StringBuffer sb = new StringBuffer();
		sb.append(GameServer.COMMAND_REGISTER);
		sb.append(DELIM);
		sb.append(name);
		sb.append(DELIM);
		sb.append(position);
		sb.append(DELIM);
		executeCommand(sb.toString());
	}
	
	private void sendStart() {
		Util.log("GameClient.sendStart");
		
		StringBuffer sb = new StringBuffer();
		sb.append(GameServer.COMMAND_START);
		sb.append(DELIM);
		sb.append(name);
		sb.append(DELIM);
		executeCommand(sb.toString());
	}
	
	private void sendRestart() {
		Util.log("GameClient.sendRestart");
		
		StringBuffer sb = new StringBuffer();
		sb.append(GameServer.COMMAND_RESTART);
		sb.append(DELIM);
		sb.append(name);
		sb.append(DELIM);
		executeCommand(sb.toString());
	}
	
	private void sendAIHard() {
		Util.log("GameClient.sendAIHard");
		
		StringBuffer sb = new StringBuffer();
		sb.append(GameServer.COMMAND_AI_FASTER);
		sb.append(DELIM);
		sb.append(name);
		sb.append(DELIM);
		executeCommand(sb.toString());
	}
	
	private void sendPause() {
		Util.log("GameClient.sendPause");
		
		StringBuffer sb = new StringBuffer();
		sb.append(GameServer.COMMAND_PAUSE);
		sb.append(DELIM);
		sb.append(name);
		sb.append(DELIM);
		executeCommand(sb.toString());
	}		
	
	public void gameLoop() {
		int delay = DELAY;
		Integer command = Robot.STOP;
		long nextMove = 0;
		Queue<Integer> commandList = null;
		
		
		Field field = FieldLayout.getStaticField();
		getFieldData(field);
		
		controller = new GraphicsController();
		controller.setup(this);
		
		Util.log("GameClient.gameLoop starting");
		clientRunning = true;
		
		while (clientRunning) {
			// get the latest field data from the server
			getFieldData(field);
			
			controller.drawField(field);
			
			if (field.getGameSecs() > 0) {
				gameRunning = true;
				if (commandList == null) commandList = robot.getAutonomousCommands();
				if (field.getGameSecs() <= (Field.GAME_SECS - Field.AUTONOMOUS)) {
					autonomous = false;
				} else {
					autonomous = true;
					//Util.log("GameClient.gameLoop autonomous:"+autonomous+ " secs:"+field.getGameSecs());
				}
			} else {
				gameRunning = false;
				autonomous = false;
			}
			
			Util.log("GameClient.gameLoop autonomous:"+autonomous+ " running:"+gameRunning+" secs:"+field.getGameSecs(),10);
			

			// if the robot is on the field get the latest data
			Robot fieldRobot = getRobot(field, name);
			if (fieldRobot != null) {
				robot.update(fieldRobot);
			}

			// ask the robot what the next command is
			command = Robot.STOP;
			if (gameRunning && field.getCountDown() == 0) {
				if (autonomous) {
					if (System.currentTimeMillis() > nextMove) {
						command = commandList.poll();
						if (command == null) command = Robot.STOP;
						nextMove = System.currentTimeMillis()+500;
						Util.log("GameClient.gameLoop autonomous command:"+command+" queue:"+commandList.size());
					}
				} else {
					command = getMove(field);	
				}
			}
			
			// send the move to the server
			if (command != Robot.STOP) {
				Util.log("GameClient.gameLoop command:"+Robot.getCommandName(command));
				sendMove(command);
			}

			// wait for a little then start again
			try { Thread.sleep(delay); } catch (Exception e) {Util.log(e);}
		}
	}


	
}

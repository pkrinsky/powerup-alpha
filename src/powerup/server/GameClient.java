package powerup.server;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import powerup.engine.GraphicsController;
import powerup.engine.Util;
import powerup.field.Field;
import powerup.field.Robot;

public class GameClient {
	public static final String DELIM="|";
	public static final String ROW_DELIM="~";
	public static final int DELAY = 100;
	
	private GraphicsController controller = null;
	private String serverAddress = null;
	private int serverPort = 0;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private GameServer server = null;
	private String name = "000";
	private Robot robot = new Robot();
	boolean clientRunning = false;
	boolean gameRunning = false;
	boolean autonomous = false;


	
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
		
		// disable key events during autonomous mode
		if (clientRunning && !autonomous) {
			robot.handleKey(e);
		} else {
			Util.log("GameClient.keyEvent keys ignored in autonomous mode");
		}
	}
	
	public Robot getRobot(Field field, String robotName) {
		Robot robot = (Robot) field.find(robotName);
		return robot;
	}
	
	public int getMove(Field field) {
		int move = Robot.STOP;
		Robot fieldRobot = getRobot(field, name);

		// if the robot is on the field get the latest data
		if (fieldRobot != null) {
			robot.update(fieldRobot);
		}
		
		// get the next move
		move = robot.move(field);
		
		// send to the server if needed
		if (move != Robot.STOP) {
			Util.log("GameClient.move "+name+" move:"+Robot.getCommandName(move));
		}
		
		return move;
	}	
	
	public void setup(String serverAddress, String serverPort, String name) {
		if (name != null) this.name = name;
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
	
	public String executeCommand(String request) {
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
	
	private void gameLoop() {
		int delay = DELAY;
		
		Field field = Field.getStaticField();
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
				if (field.getGameSecs() < (Field.GAME_SECS - 15)) {
					autonomous = false;
				} else {
					//autonomous = true;
				}
			} else {
				gameRunning = false;
				autonomous = false;
			}
			
			//Util.log("GameClient.gameLoop autonomous:"+autonomous+ " running:"+gameRunning);
			
			// see if the robot wants to make a move
			int command = getMove(field);
			
			// send the move to the server
			if (command != Robot.STOP) {
				Util.log("GameClient.gameLoop command:"+Robot.getCommandName(command));
				if (Robot.PLAYER_1 == command) {
					sendRegister(1);
				} else if (Robot.PLAYER_2 == command) {
					sendRegister(2);
				} else if (Robot.PLAYER_3 == command) {
					sendRegister(3);
				} else if (Robot.PLAYER_4 == command) {
					sendRegister(4);
				} else if (Robot.PLAYER_5 == command) {
					sendRegister(5);
				} else if (Robot.PLAYER_6 == command) {
					sendRegister(6);
				} else if (Robot.ADD_AI == command) {
					sendRegister(7);
				} else if (Robot.INCREASE_AI_SPEED == command) {
					sendAIHard();
				} else if (Robot.START == command) {
					sendStart();
				} else if (Robot.RESTART == command) {
					sendRestart();
				} else if (Robot.PAUSE == command) {
					sendPause();
				} else {
					sendMove(command);
				}
			}

			// wait for a little then start again
			try { Thread.sleep(delay); } catch (Exception e) {Util.log(e);}
		}
	}

	public GraphicsController getController() {
		return controller;
	}

	public void setController(GraphicsController controller) {
		this.controller = controller;
	}
	
}

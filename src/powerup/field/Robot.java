package powerup.field;

import java.util.LinkedList;
import java.util.Queue;

import powerup.engine.Util;

public class Robot extends FieldObject  {
	
	public static final int STOP = 0;
	public static final int NORTH = 1;
	public static final int EAST = 2;
	public static final int SOUTH = 3;
	public static final int WEST = 4;	
	public static final int PICKUP = 5;
	public static final int SHOOT = 6;
	public static final int START = 9;
	public static final int PAUSE = 10;
	public static final int PLAYER_1 = 11;
	public static final int PLAYER_2 = 12;
	public static final int PLAYER_3 = 13;
	public static final int PLAYER_4 = 14;
	public static final int PLAYER_5 = 15;
	public static final int PLAYER_6 = 16;
	public static final int ADD_AI = 17;
	public static final int INCREASE_AI_SPEED = 18;
	public static final int RESTART = 19;
	
	public static final String BLUE = "B";
	public static final String RED = "R";
	
	protected String alliance; 
	protected boolean hasCube = true;
	private boolean ai = false;
	protected String gameData = null;
	protected char startPosition;
	protected int shotsMade = 0;
	protected int command = Robot.STOP;
	
	public Robot() {
	}
	
	public Robot(String name, String alliance, String gameData, char start) {
		this.name = name;
		this.alliance = alliance;
		this.gameData = gameData;
		this.startPosition = start;
	}	
	
	public int getMove(Field field) {
		int thismove = command;
		
		// once the move has completed STOP and wait for next command
		command = Robot.STOP;
		
		return thismove;		
	}
	
	public static String getCommandName(int c) {
		String name = "unknown";
		switch (c) {
			case Robot.NORTH:
				name = "NORTH";
				break;
			case Robot.SOUTH:
				name = "SOUTH";
				break;
			case Robot.EAST:
				name = "EAST";
				break;
			case Robot.WEST:
				name = "WEST";
				break;
			case Robot.STOP:
				name = "STOP";
				break;
			case Robot.PICKUP:
				name = "PICKUP";
				break;
			case Robot.SHOOT:
				name = "SHOOT";
				break;
		}
		return name;
	}

	public void handleKey(char keyChar, int keyCode) {
		Util.log("Robot.handleKey "+name+" key:"+keyChar+" code:"+keyCode);
		
		
		if (keyChar == 'd' || keyCode == 39) {
			command = Robot.EAST;
		}		
		if (keyChar == 'a' || keyCode == 37) {
			command = Robot.WEST;
		}		
		if (keyChar == 'w' || keyCode == 38) {
			command = Robot.NORTH;
		}		
		if (keyChar == 's' || keyCode == 40) {
			command = Robot.SOUTH;
		}		
		if (keyChar == ' ') {
			if (hasCube()) {
				command = Robot.SHOOT;
			} else {
				command = Robot.PICKUP;
			}
		}	
		Util.log("Robot.handleKey "+name+" command:"+command+ " "+getCommandName(command));
	}

	public String getAlliance() {
		return alliance;
	}

	public boolean getHasCube() {
		return hasCube;
	}

	public void setAlliance(String alliance) {
		this.alliance = alliance;
	}

	public void setGameData(String gameData) {
		this.gameData = gameData;
	}

	public void setStartPosition(char startPosition) {
		this.startPosition = startPosition;
	}

	public void setShotsMade(int shotsMade) {
		this.shotsMade = shotsMade;
	}

	public char getStartPosition() {
		return startPosition;
	}
	
	public void shotMade() {
		shotsMade++;
	}
	
	public int getShotsMade() {
		return shotsMade;
	}

	public String getGameData() {
		return gameData;
	}
	public boolean hasCube() {
		return hasCube;
	}

	public void setHasCube(boolean hasCube) {
		this.hasCube = hasCube;
	}
	
	public boolean isAi() {
		return ai;
	}

	public void setAi(boolean ai) {
		this.ai = ai;
	}

	public void update(Robot fieldRobot) {
		setCol(fieldRobot.getCol());
		setRow(fieldRobot.getRow());
		setAlliance(fieldRobot.getAlliance());
		setGameData(fieldRobot.getGameData());
		setStartPosition(fieldRobot.getStartPosition());
		setHasCube(fieldRobot.getHasCube());
		setShotsMade(fieldRobot.getShotsMade());
	}
	
	public Queue<Integer> getAutonomousCommands() {
		Queue<Integer> commandList = new LinkedList<Integer>();
		return commandList;
	}
	

}

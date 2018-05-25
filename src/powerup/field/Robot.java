package powerup.field;

import java.awt.event.KeyEvent;

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
	
	public static final String BLUE = "B";
	public static final String RED = "R";
	
	protected String alliance; 
	protected boolean hasCube = true;
	private boolean ai = false;
	protected String gamedata = null;
	protected char startPosition;
	protected int shotsMade = 0;
	protected int command = Robot.STOP;
	
	public Robot() {
	}
	
	public Robot(String name, String alliance, String gamedata, char start) {
		this.name = name;
		this.alliance = alliance;
		this.gamedata = gamedata;
		this.startPosition = start;
	}	
	
	public int move(Field info) {
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
			case Robot.PAUSE:
				name = "PAUSE";
				break;
			case Robot.PICKUP:
				name = "PICKUP";
				break;
			case Robot.SHOOT:
				name = "SHOOT";
				break;
			case Robot.PLAYER_1:
				name = "PLAYER_1";
				break;
			case Robot.PLAYER_2:
				name = "PLAYER_2";
				break;
		}
		return name;
	}

	public void handleKey(KeyEvent e) {
		Util.log("Robot.handleKey "+name+" key:"+e.getKeyChar());
		
		if (e.getKeyChar() == 'd' || e.getKeyCode() == 39) {
			command = Robot.EAST;
		}		
		if (e.getKeyChar() == 'a' || e.getKeyCode() == 37) {
			command = Robot.WEST;
		}		
		if (e.getKeyChar() == 'w' || e.getKeyCode() == 38) {
			command = Robot.NORTH;
		}		
		if (e.getKeyChar() == 's' || e.getKeyCode() == 40) {
			command = Robot.SOUTH;
		}		
		if (e.getKeyChar() == '1') {
			command = Robot.PLAYER_1;
		}		
		if (e.getKeyChar() == '2') {
			command = Robot.PLAYER_2;
		}		
		if (e.getKeyChar() == '3') {
			command = Robot.PLAYER_3;
		}		
		if (e.getKeyChar() == '4') {
			command = Robot.PLAYER_4;
		}		
		if (e.getKeyChar() == '5') {
			command = Robot.PLAYER_5;
		}		
		if (e.getKeyChar() == '6') {
			command = Robot.PLAYER_6;
		}		
		if (e.getKeyChar() == ' ') {
			if (hasCube()) {
				command = Robot.SHOOT;
			} else {
				command = Robot.PICKUP;
			}
		}	
		if (e.getKeyChar() == '9') {
			command = Robot.START;
		}	
		if (e.getKeyChar() == 'p') {
			command = Robot.PAUSE;
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

	public void setGamedata(String gamedata) {
		this.gamedata = gamedata;
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

	public String getGamedata() {
		return gamedata;
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
		setGamedata(fieldRobot.getGamedata());
		setStartPosition(fieldRobot.getStartPosition());
		setHasCube(fieldRobot.getHasCube());
		setShotsMade(fieldRobot.getShotsMade());
	}
	

}

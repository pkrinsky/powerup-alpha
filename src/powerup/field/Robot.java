package powerup.field;

import powerup.engine.Util;

public class Robot extends FieldObject  {
	
	public static final int STOP = 0;
	public static final int NORTH = 1;
	public static final int EAST = 2;
	public static final int SOUTH = 3;
	public static final int WEST = 4;	
	public static final int PICKUP = 5;
	public static final int SHOOT = 6;
	
	public static final String BLUE = "B";
	public static final String RED = "R";
	
	protected String alliance; 
	protected boolean hasCube = false;
	protected String gamedata = null;
	protected char startPosition;
	protected int shotsMade = 0;
	protected int command = Robot.STOP;
	
	public Robot(String name, String alliance, String gamedata, char start) {
		this.name = name;
		this.alliance = alliance;
		this.gamedata = gamedata;
		this.startPosition = start;
	}	
	
	public boolean hasCube() {
		return hasCube;
	}

	public void setHasCube(boolean hasCube) {
		this.hasCube = hasCube;
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
			case Robot.PICKUP:
				name = "PICKUP";
				break;
			case Robot.SHOOT:
				name = "SHOOT";
				break;
		}
		return name;
	}

	public void key(char key) {
		Util.log("Robot.key "+name+" key:"+key);
		
		if (key == 'd') {
			command = Robot.EAST;
		}		
		if (key == 'a') {
			command = Robot.WEST;
		}		
		if (key == 'w') {
			command = Robot.NORTH;
		}		
		if (key == 's') {
			command = Robot.SOUTH;
		}		
		if (key == 'r') {
			command = Robot.PICKUP;
		}		
		if (key == ' ') {
			command = Robot.SHOOT;
		}	
		
		Util.log("Robot.key "+name+" command:"+command+ " "+getCommandName(command));
				
	}

	public String getAlliance() {
		return alliance;
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


	

}

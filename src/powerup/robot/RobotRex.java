package powerup.robot;

import powerup.field.Field;
import powerup.field.Robot;

public class RobotRex extends Robot {
	
	
	
	public RobotRex(String name, String alliance, String gameData, char startPosition) {
		super(name, alliance, gameData, startPosition);
		// TODO Auto-generated constructor stub
	}

	public int move(Field info) {
		int thismove = command;
		//move = RobotController.STOP;
		//int targetx = info.getMyScaleX();
		//int targety = info.getMyScaleY();
		//int x = info.getRobotX();
		//int y = info.getRobotY();

		/*
		// check to see if we have arrived
		if(x == targetx && y == targety) {
			// if so stop
		} else {
			// if not arrived then move towards target
			if (x > targetx) {
				move = WEST;
			} else if (x < targetx) {
				move = EAST;
			}

			if (y > targety) {
				move = NORTH;
			} else if (y < targety) {
				move = SOUTH;
			}
		}
		
		info.printField();
		info.printRobot();
		*/
		
		// once the move has completed STOP and wait for next command
		command = Robot.STOP;
		return thismove;
	}

}

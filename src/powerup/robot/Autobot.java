package powerup.robot;

import powerup.field.Field;
import powerup.field.FieldObject;
import powerup.field.Robot;

public class Autobot extends Robot {
	
	public Autobot(String name, String alliance, String gameData, char startPosition) {
		super(name, alliance, gameData, startPosition);
	}

	public int move(Field field) {
		int thismove = Robot.STOP;
		
		// if we have a cube put it on the switch
		if (hasCube() && Field.LEFT == startPosition) {
			FieldObject target = field.find(alliance+"NS");
			System.out.println("Autobot.move "+name+" target new switch:"+target.getCol()+" "+target.getRow());
			
			if (target.getCol()+1 < getCol()) {
				thismove = Robot.WEST;
			}
			if (target.getCol()+1 > getCol()) {
				thismove = Robot.EAST;
			}
			if (thismove == Robot.STOP) {
				thismove = Robot.SHOOT;
			}
			if (target.getRow() < getRow()) {
				thismove = Robot.SOUTH;
			}
			if (target.getRow() < getRow()) {
				thismove = Robot.NORTH;
			}
		}
		
		if (hasCube() && Field.RIGHT == startPosition) {
			FieldObject target = field.find(alliance+"S");
			System.out.println("Autobot.move "+name+" target scale:"+target.getCol()+" "+target.getRow());
			
			if (target.getCol() < getCol()) {
				thismove = Robot.WEST;
			}
			if (target.getCol() > getCol()) {
				thismove = Robot.EAST;
			}
			if (thismove == Robot.STOP) {
				thismove = Robot.SHOOT;
			}
			if (target.getRow()+1 < getRow()) {
				thismove = Robot.SOUTH;
			}
			if (target.getRow()+1 < getRow()) {
				thismove = Robot.NORTH;
			}
				
		}
		
		// once the move has complete wait for next command
		command = Robot.STOP;
		return thismove;
	}

}

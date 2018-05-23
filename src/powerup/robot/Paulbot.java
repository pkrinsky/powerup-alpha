package powerup.robot;

import java.util.Random;

import powerup.engine.Util;
import powerup.field.Cube;
import powerup.field.Field;
import powerup.field.FieldObject;
import powerup.field.Robot;

public class Paulbot extends Robot {

	public Paulbot(String name, String alliance, String gamedata, char startPosition) {
		super(name, alliance, gamedata, startPosition);
	}

	private FieldObject findCube(Field field) {
		for (int c=0; c<Field.COLS; c++) {
			for(int r=0; r<Field.ROWS; r++) {
				if (field.getFieldObject(c, r) != null && field.getFieldObject(c, r) instanceof Cube){
					return field.getFieldObject(c, r);
				}
			}
		}
		return null;

	}
	
	private int chooseDirection(Field field, int c, int r) {
		int thismove = Robot.STOP;
		Random random = new Random();

		Util.log("Paulbot.chooseDirection "+name+" robat at c:"+getCol()+" r:"+getRow());
		
		if(thismove == Robot.STOP && c > getCol()) {
			Util.log("Paulbot.chooseDir target to right go east");
			thismove = Robot.EAST;
		}
		if (thismove == Robot.STOP &&c < getCol()) {
			Util.log("Paulbot.chooseDir target to left go west");
			thismove = Robot.WEST;
		}
		if (thismove == Robot.STOP && r < getRow()) {
			Util.log("Paulbot.chooseDir target above go north");
			thismove = Robot.NORTH;
		}
		if (thismove == Robot.STOP && r > getRow()) {
			Util.log("Paulbot.chooseDir target below go south");
			thismove = Robot.SOUTH;
		}
		
		boolean blocked = true;
		
		while (blocked) {
			if (thismove == Robot.WEST && checkWest(field)) blocked = false;
			if (thismove == Robot.EAST && checkEast(field)) blocked = false;
			if (thismove == Robot.SOUTH && checkSouth(field)) blocked = false;
			if (thismove == Robot.NORTH && checkNorth(field)) blocked = false;
			
			if (blocked) {
				int ri = random.nextInt(2) + 1;
				if (thismove == Robot.NORTH || thismove == Robot.SOUTH) {
					switch (ri) {
						case 1:	thismove = Robot.EAST;	break;
						case 2:	thismove = Robot.WEST;	break;
					}
				} else {
					switch (ri) {
						case 1:	thismove = Robot.NORTH;	break;
						case 2:	thismove = Robot.SOUTH;	break;
					}
				}
				Util.log("Paulbot.chooseDir blocked switching to "+thismove);
			}
			
		}

		
		
		return thismove;
	}
	
	private boolean checkField(Field field, int c, int r) {
		boolean blocked = true;
		if (c>=0 && r>=0 && c <= Field.COLS && r<=Field.ROWS)
			blocked = field.getFieldObject(c,r) == null ? true : false;
		return blocked;
	}
	
	private boolean checkWest(Field field) {
		return checkField(field,getCol()-1,getRow());
	}
	private boolean checkEast(Field field) {
		return checkField(field,getCol()+1,getRow());
	}
	private boolean checkNorth(Field field) {
		return checkField(field,getCol(),getRow()-1);
	}
	private boolean checkSouth(Field field) {
		return checkField(field,getCol(),getRow()+1);
	}

	public int move(Field field) {
		Util.log("Paulbot.move "+name);
		int thismove = Robot.STOP;
		FieldObject target = null;

		// if we have a cube put it on the switch
		if (hasCube()) {
			if(Field.LEFT == startPosition){
				target = field.find(alliance+"FS");
			}

			if(Field.MIDDLE == startPosition){
				target = field.find(alliance+"NS");
			}
			
			if(Field.RIGHT == startPosition){
				target = field.find(alliance+"S");
			}

		} else {
			target = findCube(field);
		}
		
		if (target == null) {
			Util.log("No more cubes");
		} else {
			Util.log("Target is c:"+target.getCol()+" r:"+target.getRow());
			if (getCol() != target.getCol() && getRow() > 3 && getRow() <= 5 ) {
				Util.log("Paulbot.move move to the top before moving E/W");
				thismove = chooseDirection(field,getCol(),1);
			} else if (getCol() != target.getCol() && getRow() >= 6 && getRow() < 10) {
				Util.log("Paulbot.move move to the bottom before moving E/W");
				thismove = chooseDirection(field,getCol(),11);
			} else {
				Util.log("Paulbot.move move to the target");
				thismove = chooseDirection(field,target.getCol(),target.getRow());
			}
		
			if (hasCube()) {
				if (target.getCol() == getCol() && target.getRow() == getRow()+1) {
					thismove = Robot.SHOOT;
				} 
				if (target.getCol() == getCol() && target.getRow() == getRow()-1) {
					thismove = Robot.SHOOT;
				}
				
				if (target.getCol() == getCol()+1 && target.getRow() == getRow()) {
					thismove = Robot.SHOOT;
				} 
				if (target.getCol() == getCol()-1 && target.getRow() == getRow()) {
					thismove = Robot.SHOOT;
				} 
			} else {
				if (field.getFieldObject(getCol(), getRow()+1) instanceof Cube) {
					thismove = Robot.PICKUP;
				} 
				if (field.getFieldObject(getCol(), getRow()-1) instanceof Cube) {
					thismove = Robot.PICKUP;
				}
				if (field.getFieldObject(getCol()+1, getRow()) instanceof Cube) {
					thismove = Robot.PICKUP;
				} 
				if (field.getFieldObject(getCol()-1, getRow()) instanceof Cube) {
					thismove = Robot.PICKUP;
				}
			}
		}

		// once the move has complete wait for next command
		command = Robot.STOP;
		return thismove;
	}

}

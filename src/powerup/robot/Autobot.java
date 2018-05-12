package powerup.robot;

import powerup.field.Cube;
import powerup.field.Field;
import powerup.field.FieldObject;
import powerup.field.Robot;

public class Autobot extends Robot {

	public Autobot(String name, String alliance, String gamedata, char startPosition) {
		super(name, alliance, gamedata, startPosition);
	}

	private FieldObject findCube(Field field) {
		FieldObject[][] grid = field.getGrid();
		for (int c=0; c<Field.COLS; c++) {
			for(int r=0; r<Field.ROWS; r++) {
				if (grid[c][r] != null && grid[c][r] instanceof Cube){
					return grid[c][r];
				}
			}
		}
		return null;

	}

	public int move(Field field) {
		int thismove = Robot.STOP;
		FieldObject target = null;

		// if we have a cube put it on the switch
		if (hasCube()) {

			if(Field.LEFT == startPosition){
				FieldObject[][] grid = field.getGrid();
				if( gamedata == "LRL" ) {

					target = field.find(alliance+"NS");
					System.out.println("Autobot.move "+name+" target scale:"+target.getCol()+" "+target.getRow());
				} else {
					target = field.find(alliance+"NS");
					System.out.println("Autobot.move "+name+" target switch:"+target.getCol()+" "+target.getRow());	
				}
				if (target.getRow() > getRow()) {
					thismove = Robot.SOUTH;
				}
				if (target.getRow() < getRow()) {
					thismove = Robot.NORTH;
				}
				if (thismove == Robot.STOP) {
					thismove = Robot.SHOOT;
				} 
				if(getCol() >0) {
				if (target.getCol()+1 < getCol() && grid[getCol()-1][getRow()] ==null || grid[getCol()-1][getRow()] instanceof Cube) {
					thismove = Robot.WEST;
				}else {
					if(grid[getCol() -1][getRow()] != null && target.getCol()+1 != getCol()) {
						thismove = Robot.NORTH;
					}	
				}
				}
				if (target.getCol()-1 > getCol()) {
					thismove = Robot.EAST;
				}

			}

			if(Field.RIGHT == startPosition){
				if( gamedata == "RLR") {
					target = field.find(alliance+"S");

					System.out.println("Autobot.move "+name+" target scale:"+target.getCol()+" "+target.getRow());

				} else if(alliance == BLUE){
					target = field.find(alliance+"S");	
					System.out.println("Autobot.move "+name+" target switch:"+target.getCol()+" "+target.getRow());

				} else {
					target = field.find(alliance+"S");
					System.out.println("Autobot.move "+name+" target switch:"+target.getCol()+" "+target.getRow());

				}
				if (thismove == Robot.STOP) {
					thismove = Robot.SHOOT;
				}
				if (target.getCol()+1 < getCol()) {
					thismove = Robot.WEST;
				}
				if (target.getCol()-1 > getCol() ) {
					thismove = Robot.EAST;
					// if not empty then go north
					//System.out.println(getCol()+ getRow());
				}

				if (target.getRow() > getRow()) {
					thismove = Robot.SOUTH;
				}
				if (target.getRow() < getRow()) {
					thismove = Robot.NORTH;
				}
			}

		} else {

			FieldObject cube = findCube(field);
			System.out.println("Getting cube at c:"+cube.getCol()+" r:"+cube.getRow());
			FieldObject[][] grid = field.getGrid();
			if (cube.getRow()-1 > getRow() && cube.getCol() == getCol()) {
				thismove = Robot.SOUTH;
			}
			if (cube.getRow()+1 < getRow()) {
				thismove = Robot.NORTH;
			}
			if (cube.getCol()+1 <= getCol() && grid[getCol()-1][getRow()] ==null) {
				thismove = Robot.WEST;
			}else if (grid[getCol()-1][getRow()] != null && cube.getCol() != getCol()) {
				thismove = Robot.NORTH;
			}
			if (cube.getCol()-1 >= getCol() && grid[getCol()+1][getRow()] == null) {
				thismove = Robot.EAST;
			}else if (grid[getCol()+1][getRow()] != null && cube.getCol() != getCol()) {
				thismove = Robot.NORTH;
			}
			if (thismove == Robot.STOP) {
				thismove = Robot.PICKUP;
			}




		}

		// once the move has complete wait for next command
		command = Robot.STOP;
		return thismove;
	}

}

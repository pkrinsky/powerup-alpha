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
			FieldObject[][] grid = field.getGrid();


			if( gamedata == "RLR") {
				if(Field.RIGHT == startPosition){
					target = field.find(alliance+"NS");

					System.out.println("Autobot.move "+name+" target scale:"+target.getCol()+" "+target.getRow());

				} else {
					target = field.find(alliance+"S");
				}
			}else {
				if(Field.RIGHT == startPosition) {
					target = field.find(alliance+"S");
				}else {
					target = field.find(alliance+"NS");
				}
			}


			if (thismove == Robot.STOP) {
				thismove = Robot.SHOOT;
			} 
			if(getCol() >0) {
				if (target.getCol()+1 < getCol() && grid[getCol()-1][getRow()] ==null) {
					thismove = Robot.WEST;
				}else {
					if(grid[getCol() -1][getRow()] != null && target.getCol()+1 != getCol()) {
						thismove = Robot.NORTH;
					}	
				}
			}
			if(getCol() <22) {
				if (target.getCol()-1 > getCol() && grid[getCol()+1][getRow()] == null) {
					thismove = Robot.EAST;

				}else {
					if(grid[getCol()+1][getRow()] != null && target.getCol()-1 != getCol()) {
						thismove = Robot.NORTH;
					}
						if (target.getRow() < getRow() && grid[getCol()][getRow()-1] == null ) {
							thismove = Robot.NORTH;
						} else if (target.getRow() > getRow() && grid[getCol()+1][getRow()]== null) {
							thismove = Robot.EAST;
						} else if (target.getRow() >getRow() && grid[getCol()-1][getRow()]== null) {
							thismove = Robot.WEST;
						}
						if (target.getRow() > getRow() && grid[getCol()][getRow()+1] == null) {
							thismove = Robot.SOUTH;
						} else if (target.getRow() > getRow() && grid[getCol()+1][getRow()]== null) {
							thismove = Robot.EAST;
						} else if (target.getRow() >getRow() && grid[getCol()-1][getRow()]== null) {
							thismove = Robot.WEST;
						}

					

				}

			}



		} else {
			FieldObject[][] grid = field.getGrid();
			FieldObject cube = findCube(field);
			System.out.println("Getting cube at c:"+cube.getCol()+" r:"+cube.getRow());

			if (cube.getRow()-1 > getRow() && cube.getCol() == getCol()) {
				thismove = Robot.SOUTH;
				if(grid[getCol()][getRow()+1] != null) {
					thismove = Robot.WEST;
				}
			}
			if (cube.getRow()+1 < getRow()) {
				thismove = Robot.NORTH;
				if(grid[getCol()][getRow()+1] != null) {
					thismove = Robot.WEST;		
				}else {
					thismove = Robot.EAST;
				}
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
			if(getCol() != 0 && getRow() != 0) {
				if(grid[getCol()+1][getRow()] instanceof Cube) {
					thismove = Robot.PICKUP;
				}
				if(grid[getCol()-1][getRow()] instanceof Cube) {
					thismove = Robot.PICKUP;
				}
				if(grid[getCol()][getRow()+1] instanceof Cube) {
					thismove = Robot.PICKUP;
				}
				if(grid[getCol()][getRow()-1] instanceof Cube) {
					thismove = Robot.PICKUP;
				}
			}





		}

		// once the move has complete wait for next command
		command = Robot.STOP;
		return thismove;
	}

}

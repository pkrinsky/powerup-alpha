package powerup.robot;

import powerup.engine.Util;
import powerup.field.Cube;
import powerup.field.Field;
import powerup.field.FieldObject;
import powerup.field.Robot;

public class Autobot extends Robot {

	public Autobot(String name, String alliance, String gameData, char startPosition) {
		super(name, alliance, gameData, startPosition);
	}

	private FieldObject findCube(Field field) {
		FieldObject fo = null;
		for (int c=0; c<Field.COLS; c++) {
			for(int r=0; r<Field.ROWS; r++) {
				fo = field.getFieldObject(c, r);
				if (fo != null && fo instanceof Cube){
					return fo;
				}
			}
		}
		return null;

	}

	public int move(Field field) {
		Util.log("Autobot.move "+name);
		int thismove = Robot.STOP;
		FieldObject target = null;


		// if we have a cube put it on the switch
		if (hasCube()) {



			if( gameData == "RLR") {
				if(Field.RIGHT == startPosition){
					target = field.find(alliance+"NS");

					Util.log("Autobot.move "+name+" target scale:"+target.getCol()+" "+target.getRow());

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

		} else {
			FieldObject cube = findCube(field);
			Util.log("Getting cube at c:"+cube.getCol()+" r:"+cube.getRow());
			target = cube;


			if(getCol() != 0 && getRow() != 0) {
				if(field.getFieldObject(getCol()+1,getRow()) instanceof Cube) {
					thismove = Robot.PICKUP;
				}
				if(field.getFieldObject(getCol()-1,getRow()) instanceof Cube) {
					thismove = Robot.PICKUP;
				}
				if(field.getFieldObject(getCol(),getRow()+1) instanceof Cube) {
					thismove = Robot.PICKUP;
				}
				if(field.getFieldObject(getCol(),getRow()-1) instanceof Cube) {
					thismove = Robot.PICKUP;
				}
			}



		}
		int loops = Field.ROWS* Field.COLS;
		while (loops-- >0) {
			for (int r=0;r<Field.ROWS;r++) {
				for (int c=0;c<Field.COLS;c++) {
					if (field.getFieldObject(c,r) == target) {
						if (r-1>=0 && field.getFieldObject(c,r-1) == null) {
							//grid[c][r-1] = 1;
						}


					}
				}

			}
		}
		int[][] distance = new int[Field.COLS][Field.ROWS];


		for(int r = 0 ; r< Field.ROWS; r++) {
			for(int c = 0 ; c <Field.COLS; c++) {
				distance[c][r] =-1;
			}
		}


		distance[target.getCol()][target.getRow()] = 0;

		print(distance);

		int loopies = Field.COLS*Field.ROWS;

		while (loopies-- > 0)	{
			for(int r = 0 ; r< Field.ROWS; r++) {
				for(int c = 0 ; c <Field.COLS; c++) {
					if(distance[c][r] != -1 ) {

						// north
						if (r-1 >=0 && distance[c][r-1] == -1 && field.getFieldObject(c,r-1) == null) {
							distance[c][r-1] = distance[c][r] + 1;
						}
						// east
						if (c+1 <Field.COLS && distance[c+1][r] == -1 && field.getFieldObject(c+1,r)== null) {
							distance[c+1][r] = distance[c][r] + 1;
						}
						// south
						if (r+1 <Field.ROWS && distance[c][r+1] == -1 && field.getFieldObject(c,r+1) == null) {
							distance[c][r+1] = distance[c][r] + 1;
						}
						// west
						if (c-1 >=0 && distance[c-1][r] == -1 && field.getFieldObject(c-1,r) == null) {
							distance[c-1][r] = distance[c][r] + 1;
						}
					}
				}
			}
			print(distance);
		}

		//		System.exit(1);
		int smallest = 10000;
		if(getCol() < 22) {
			if(distance[getCol()+1][getRow()] < smallest && distance[getCol()+1][getRow()] != -1 ) {
				smallest = distance[getCol()+1][getRow()];
				thismove = Robot.EAST;
			}
		}
		if(getCol()>1) {
			if(distance[getCol()-1][getRow()] < smallest && distance[getCol()-1][getRow()] != -1) {
				smallest = distance[getCol()-1][getRow()];
				thismove = Robot.WEST;
			}
		}
		if(distance[getCol()][getRow()+1] < smallest && distance[getCol()][getRow()+1] != -1) {
			smallest = distance[getCol()][getRow()+1];
			thismove = Robot.SOUTH;
		}
		if(distance[getCol()][getRow()-1] < smallest && distance[getCol()][getRow()-1] != -1) {
			smallest = distance[getCol()][getRow()-1];
			thismove = Robot.NORTH;
		}
		if(smallest == 0 && hasCube == true) {
			thismove = Robot.SHOOT;
		}
		if(smallest == 0 && hasCube ==false) {
			thismove = Robot.PICKUP;
		}

		Util.log("Autobot.move "+name+" complete");
		// once the move has complete wait for next command
		return thismove;
	}


	private void print(int[][] distance) {
		for(int r = 0 ; r< Field.ROWS; r++) {
			for(int c = 0 ; c <Field.COLS; c++) {
				Util.log(((distance[c][r]+" ").substring(0, 2)+ " "),10);
			}
			Util.log("",10);
		}
		Util.log("Autobot.print done",10);

	}


}

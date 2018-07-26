package powerup.robot;

import powerup.engine.Util;
import powerup.field.Cube;
import powerup.field.Field;
import powerup.field.FieldObject;
import powerup.field.Robot;
import powerup.field.Scale;

public class Autobot extends Robot {
	int[][] distance = new int[Field.COLS][Field.ROWS];
	public Autobot(String name, String alliance, String gameData, char startPosition) {
		super(name, alliance, gameData, startPosition);
	}

	private FieldObject findCube(Field field) {
		int[][] distance = new int[Field.COLS][Field.ROWS];

		FieldObject cube = null;
		for(int r = 0 ; r< Field.ROWS; r++) {
			for(int c = 0 ; c <Field.COLS; c++) {
				distance[c][r] =-1;
			}
		}
		int loops = 10000;
		distance[getCol()][getRow()] = 0;
		boolean found = false;

		while (found == false) {
			while(loops-- > 0) {

				for(int r = 1 ; r< Field.ROWS; r++) {
					for(int c = 1 ; c <Field.COLS; c++) {
						if(distance[c][r] != -1) {
							if(field.getFieldObject(c-1, r) instanceof Cube && c>1 && found == false) {
								found = true;
								cube = field.getFieldObject(c-1,r);
							}else if(c>1){
								distance[c-1][r]=1;
							}
							if(field.getFieldObject(c+1,r) instanceof Cube && c< Field.COLS && found == false) {
								found = true;
								cube = field.getFieldObject(c+1,r);
							}else if(c+1< Field.COLS){
								distance[c+1][r]=1;
							}
							if(field.getFieldObject(c, r+1) instanceof Cube && r<Field.ROWS && found == false) {
								found = true;
								cube = field.getFieldObject(c, r+1);
							}else if(r+1<Field.ROWS){
								distance[c][r+1]=1;
							}
							if(field.getFieldObject(c,r-1) instanceof Cube && r>1 && found == false) {
								found = true;
								cube = field.getFieldObject(c, r-1);
							}else if (r>1){
								distance[c][r-1]= 1;

							}

						}
					}
				}
			}
		}

		System.out.println(found);

		return cube;



	}

	public int move(Field field) {
		Util.log("Autobot.move "+name);
		int thismove = Robot.STOP;
		FieldObject target = null;


		// if we have a cube put it on the switch
		if (hasCube()) {

			String otherAlliance = Robot.RED.equals(alliance) ? Robot.BLUE : Robot.RED;
			Scale me = (Scale) field.find(alliance+"FS");
			Scale them = (Scale) field.find(otherAlliance+"NS");

			if ( me.getNumCubes() <= them.getNumCubes() ) {
				target = field.find(alliance+"FS");
			}
			me = (Scale) field.find(alliance+"NS");
			them = (Scale) field.find(otherAlliance+"FS");

			if ( me.getNumCubes() <= them.getNumCubes() ) {
				target = field.find(alliance+"NS");
			}

			me = (Scale) field.find(alliance+"S");
			them = (Scale) field.find(otherAlliance+"S");

			if ( me.getNumCubes() <= them.getNumCubes() ) {
				target = field.find(alliance+"S");
			}
			them = (Scale) field.find(otherAlliance+"FS");
			if(them.getNumCubes() < 1) {
				target = field.find(otherAlliance+"FS"); 
			}
			
			me = (Scale) field.find(alliance+"NS");
			them = (Scale) field.find(otherAlliance+"FS");

			if ( me.getNumCubes() <= them.getNumCubes() ) {
				target = field.find(alliance+"NS");
			}




			if (thismove == Robot.STOP) {
				thismove = Robot.SHOOT;
			} 

		} else {
			FieldObject cube = findCube(field);
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
		if( getCol()+1< Field.COLS &&distance[getCol()+1][getRow()] < smallest) {
			if(distance[getCol()+1][getRow()] != -1 ) {
				smallest = distance[getCol()+1][getRow()];
				thismove = Robot.EAST;
			}
		}
		if( getCol() > 0 && distance[getCol()-1][getRow()] < smallest) {
			if(distance[getCol()-1][getRow()] != -1) {
				smallest = distance[getCol()-1][getRow()];
				thismove = Robot.WEST;
			}
		}
		if( getRow()+1<Field.ROWS && distance[getCol()][getRow()+1] < smallest) {
			if(distance[getCol()][getRow()+1] != -1) {
				smallest = distance[getCol()][getRow()+1];
				thismove = Robot.SOUTH;
			}
		}
		if( getRow()>0  && distance[getCol()][getRow()-1] < smallest){

			if(distance[getCol()][getRow()-1] != -1) {
				smallest = distance[getCol()][getRow()-1];
				thismove = Robot.NORTH;
			}
		}
		if(smallest == 0 && hasCube == true) {
			thismove = Robot.SHOOT;
		}
		if(smallest == 0 && hasCube ==false) {
			thismove = Robot.PICKUP;
		}
		if(getCol() != 0 && getRow() != 0) {
			if(field.getFieldObject(getCol()+1,getRow()) instanceof Cube && hasCube() == false) {
				thismove = Robot.PICKUP;
			}
			if(field.getFieldObject(getCol()-1,getRow()) instanceof Cube && hasCube() == false) {
				thismove = Robot.PICKUP;
			}
			if(field.getFieldObject(getCol(),getRow()+1) instanceof Cube && hasCube() == false) {
				thismove = Robot.PICKUP;
			}
			if(field.getFieldObject(getCol(),getRow()-1) instanceof Cube && hasCube() == false) {
				thismove = Robot.PICKUP;
			}
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

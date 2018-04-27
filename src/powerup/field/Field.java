package powerup.field;

public class Field {
	public static final int COLS=23;
	public static final int ROWS=15;
	public static final int COL2 = 12;
	public static final int COL1 = COL2-6;
	public static final int COL3 = COL2+6;
	public static final int ROW2 = 8;
	public static final int ROW1 = ROW2-6;
	public static final int ROW3 = ROW2+6;
	public static final char LEFT = 'L';
	public static final char MIDDLE = 'M';
	public static final char RIGHT = 'R';

	
	private FieldObject[][] grid= new FieldObject[COLS][ROWS];

	public FieldObject[][] getGrid() {
		return grid;
	}
	
	public FieldObject getFieldObject(int c, int r) {
		return grid[c][r];
	}

	public void setGrid(FieldObject[][] grid) {
		this.grid = grid;
	}
	
	public FieldObject find(String name) {
		FieldObject fo = null;
		for (int r=0;r<Field.ROWS;r++) {
			for (int c=0;c<Field.COLS;c++) {
				//System.out.println("Field.find checking col:"+c+" r:"+r);
				if (grid[c][r] != null 
						&& grid[c][r].getName() != null 
						&& grid[c][r].getName().equalsIgnoreCase(name)) 
				{
					fo = grid[c][r];
					fo.setCol(c);
					fo.setRow(r);
					//System.out.println("Field.find found "+fo.name+" col:"+fo.getCol()+" r:"+fo.getRow());
					break;
				}
			}
		}
		
		if (fo == null) {
			System.out.println("Field.find Cannot find field object with name:"+name);
		}
		return fo;
		
	}
	
	
	public void print() {
		for (int r=0;r<Field.ROWS;r++) {
			for (int c=0;c<Field.COLS;c++) {
				if (grid[c][r] != null) {
					System.out.println("Field.print "+c+" "+r+" "+grid[c][r].name);
				} else {
					//System.out.println(c+" "+r+" Empty");
				}
			}
		}
		
	}
	
	public void setup(int col, int row, FieldObject fo) {
		System.out.println("Field.setup "+fo.name+" at col:"+col+" r:"+row);
		fo.setCol(col);
		fo.setRow(row);
		if (grid[col][row] == null) {
			grid[col][row] = fo;
		} else {
			throw new RuntimeException("Field.setup position already taken by "+grid[col][row].getName()); 
		}
	}
	
	public void setup(Robot r) {
		int row = 0;
		int col = 0;
		
		if (LEFT == r.getStartPosition()) {
			if (Robot.BLUE.equals(r.getAlliance())) {
				row = ROW1;
			} else {
				row = ROW3;
			}
		}
		if (MIDDLE == r.getStartPosition()) {
			row = ROW2;
		}
		if (RIGHT == r.getStartPosition()) {
			if (Robot.BLUE.equals(r.getAlliance())) {
				row = ROW3;
			} else {
				row = ROW1;
			}
		}
		
		if (Robot.BLUE.equals(r.getAlliance())) {
			col = 0;
		} else {
			col = COLS-1;
		}
		
		setup(col,row,r);
	}
	
	public void remove(int col, int row) {
		grid[col][row].setDeleted(true);
		grid[col][row] = null;
		System.out.println("Field.remove is now empty col:"+col+" r:"+row);
	}
	
	public void move(FieldObject fo, int col, int row) {
		System.out.println("Field.move "+fo.name+" from col:"+fo.getCol()+" r:"+fo.getRow()+" to col:"+col+" r:"+row);
		
		// make sure target is on the field
		if (row >= 0 && col >= 0 && row < ROWS && col < COLS) {
			// make sure target is empty
			if(grid[col][row] == null ) {
				int oldr = fo.getRow();
				int oldc = fo.getCol();
				fo.setCol(col);
				fo.setRow(row);
				grid[col][row] = fo;
				System.out.println("Field.move was successful new position for "+fo.name+" col:"+col+" r:"+row);
				grid[oldc][oldr] = null;
				System.out.println("Field.move is now empty "+" col:"+oldc+" r:"+oldr);
			} else {
				System.out.println("Field.move target is occupied "+fo.name+" col:"+col+" r:"+row);
			}
		} else {
			System.out.println("Field.move off the field "+fo.name+" col:"+col+" r:"+row);
		}
		//print();
	}



	
	
	

}

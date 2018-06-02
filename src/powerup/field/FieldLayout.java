package powerup.field;

import powerup.engine.Util;

public class FieldLayout {
	
	public static Field getStaticField() {
		Util.log("Field.getStaticField");
		
		Field field = new Field();
		
		int col2 = Field.COL2;
		int col1 = Field.COL1;
		int col3 = Field.COL3;
				
		field.set(col2,3,new Scale("RS",Robot.RED)); 
		field.set(col2,4,new Wall());
		field.set(col2,5,new Wall());
		field.set(col2,6,new Wall());
		field.set(col2,7,new Wall());
		field.set(col2,8,new Wall());
		field.set(col2,9,new Wall());
		field.set(col2,10,new Wall());
		field.set(col2,11,new Scale("BS",Robot.BLUE));

		field.set(col1,4,new Scale("BNS",Robot.BLUE));
		field.set(col1,5,new Wall());
		field.set(col1,6,new Wall());
		field.set(col1,7,new Wall());
		field.set(col1,8,new Wall());
		field.set(col1,9,new Wall());
		field.set(col1,10,new Scale("RFS",Robot.RED));

		field.set(col3,4,new Scale("BFS",Robot.BLUE));
		field.set(col3,5,new Wall());
		field.set(col3,6,new Wall());
		field.set(col3,7,new Wall());
		field.set(col3,8,new Wall());
		field.set(col3,9,new Wall());
		field.set(col3,10,new Scale("RNS",Robot.RED));
		
		return field;
	}
	
	public static Scale getScale(Field field, Wall wall) {
		Scale scale = null;
		if (wall.getCol() == Field.COL2 && (wall.getRow() == 4 || wall.getRow() == 5))
			scale = (Scale) field.find("RS");
		if (wall.getCol() == Field.COL2 && (wall.getRow() == 9 || wall.getRow() == 10))
			scale = (Scale) field.find("BS");
		return scale;
	}
	
	public static Scale getOtherScale(Field field, Scale scale) {
		Scale otherScale = null;
		
		if (Robot.BLUE == scale.getAlliance()) 
			otherScale = (Scale) field.find("RS");
		else 
			otherScale = (Scale) field.find("BS");
		
		return otherScale;
	}
	
	public static int getImageIndex(Field field, FieldObject fo) {
		int r = 0;

		if (fo instanceof Wall && (field.getGameSecs() % 2 == 0)) {

			// scale
			Scale bs = (Scale) field.find("BS");
			Scale rs = (Scale) field.find("RS");
			if (fo.getCol() == Field.COL2 && (fo.getRow() == 4 
					|| fo.getRow() == 5 
					|| fo.getRow() == 6 
					|| fo.getRow() == 7 
					|| fo.getRow() == 8
					|| fo.getRow() == 9
					|| fo.getRow() == 10)
				) 
			{
				if (rs.getNumCubes() > bs.getNumCubes()) r = 1;
				if (bs.getNumCubes() > rs.getNumCubes()) r = 2;
			}

			bs = (Scale) field.find("BNS");
			rs = (Scale) field.find("RFS");
			if (fo.getCol() == Field.COL1 && (fo.getRow() == 5 
					|| fo.getRow() == 6 
					|| fo.getRow() == 7 
					|| fo.getRow() == 8
					|| fo.getRow() == 9)
				) 
			{
				if (rs.getNumCubes() > bs.getNumCubes()) r = 1;
				if (bs.getNumCubes() > rs.getNumCubes()) r = 2;
			}
			
			bs = (Scale) field.find("BFS");
			rs = (Scale) field.find("RNS");
			if (fo.getCol() == Field.COL3 && (fo.getRow() == 5 
					|| fo.getRow() == 6 
					|| fo.getRow() == 7 
					|| fo.getRow() == 8
					|| fo.getRow() == 9)
				) 
			{
				if (rs.getNumCubes() > bs.getNumCubes()) r = 1;
				if (bs.getNumCubes() > rs.getNumCubes()) r = 2;
			}
		}
		
		return r;
	}
	
}

package powerup.field;

public class Wall extends FieldObject {
	
	@Override
	public String getName() {
		return "wall-"+getCol()+"-"+getRow();
	}

}

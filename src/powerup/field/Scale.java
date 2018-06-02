package powerup.field;

public class Scale extends FieldObject {
	private String alliance;
	private int numCubes = 0;

	
	public Scale(String name, String alliance) {
		this.name = name;
		this.alliance = alliance;
	}	
	
	public int getNumCubes() {
		return numCubes;
	}

	public void setNumCubes(int numCubes) {
		this.numCubes = numCubes;
	}

	public String getAlliance() {
		return alliance;
	}

	public void setAlliance(String alliance) {
		this.alliance = alliance;
	}

}

package powerup.engine;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import powerup.field.FieldObject;
import powerup.field.Robot;
import powerup.field.Wall;

public class Block {
	
	public static int BLOCKSIZE = 50;
	
	private Image image;
	private BufferedImage[] imageArray;
	protected String name;
	private FieldObject fieldObject;
	private int imageIndex = 0;
	
	public Image setImage(BufferedImage sourceImage) {
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image image = gc.createCompatibleImage(sourceImage.getWidth(),sourceImage.getHeight(),Transparency.BITMASK);
		image.getGraphics().drawImage(sourceImage,0,0,null);
		return image;
	}	

	public Block(BufferedImage[] images, FieldObject fo) {
		this.imageArray = images;
		this.image = setImage(images[imageIndex]);
		this.fieldObject = fo;
	}
	
	public void draw(Graphics g) {
		//update x and y from field object
		int x=(fieldObject.getCol())*BLOCKSIZE;
		int y=(fieldObject.getRow())*BLOCKSIZE;
		
		
		// change image for block depending upon state of object
		if (fieldObject != null && fieldObject instanceof Robot) {
			Robot r = (Robot) fieldObject;
			if (r.hasCube()) {
				this.image = setImage(imageArray[0]);
			} else {
				this.image = setImage(imageArray[1]);
			}
		} 

		if (fieldObject != null && fieldObject instanceof Wall) {
			this.image = setImage(imageArray[imageIndex]);
		}
		
		g.drawImage(image,x,y,null);
		//System.out.println("draw "+fieldObject.getName()+" x:"+x+" y:"+y);
	}

	
	public FieldObject getFieldObject() {
		return fieldObject;
	}
	
	public void setFieldObject(FieldObject fieldObject) {
		this.fieldObject = fieldObject;
	}

	public int getImageIndex() {
		return imageIndex;
	}

	public void setImageIndex(int imageIndex) {
		this.imageIndex = imageIndex;
	}

	

}

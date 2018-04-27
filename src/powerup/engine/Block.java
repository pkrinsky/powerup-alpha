package powerup.engine;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import powerup.field.FieldObject;

public class Block {
	
	public static int BLOCKSIZE = 50;
	
	private Image image;
	protected int height=0,width=0;
	protected String name;
	private FieldObject fieldObject;
	
	public int getHeight() {return height;}
	public int getWidth() {return width;}
		
	
	public Image getImage(String ref) {
		
		BufferedImage sourceImage = null;
		
		try {
			URL url = this.getClass().getClassLoader().getResource(ref);
			
			if (url == null) {
				throw new RuntimeException("Can't find ref: "+ref);
			}
			
			sourceImage = ImageIO.read(url);
			height = sourceImage.getHeight();
			width = sourceImage.getWidth();
			System.out.println("read image "+ref+" width:"+width+" height:"+height);
			
		} catch (IOException e) {
			throw new RuntimeException("Failed to load: "+ref);
		}
		
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image image = gc.createCompatibleImage(width,height,Transparency.BITMASK);
		image.getGraphics().drawImage(sourceImage,0,0,null);
		
		
		return image;
	}	
	
	public Block(String ref, FieldObject fo) {
		this.image = getImage(ref);
		this.fieldObject = fo;
	}
	
	public void draw(Graphics g) {
		//update x and y from field object
		int x=(fieldObject.getCol())*BLOCKSIZE;
		int y=(fieldObject.getRow())*BLOCKSIZE;
		g.drawImage(image,x,y,null);
		//System.out.println("draw "+fieldObject.getName()+" x:"+x+" y:"+y);
	}
	
	
	public FieldObject getFieldObject() {
		return fieldObject;
	}
	
	public void setFieldObject(FieldObject fieldObject) {
		this.fieldObject = fieldObject;
	}

	

}

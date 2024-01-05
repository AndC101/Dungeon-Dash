import java.awt.*;
import java.awt.image.*;
import java.io.*;


public class Portal extends Block {
	
	//CAPITALIZE THESE
	public static final int width = 60;
	public static final int height = 75;
	public BufferedImage i;
	
	public Portal(int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(x,y,l,w,i);
	}
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		super.draw(g);
	}

	public String toString () {
		return "Portal " + super.toString();
	}

}

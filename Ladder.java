import java.awt.*;
import java.awt.image.*;
import java.io.*;


public class Ladder extends Block {
	
	public static final int width = 20;
	public static final int height = 40;
	
	public BufferedImage i;
	
	public Ladder(int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(x,y,l,w,i);
	}
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		super.draw(g);
	}
	
	public String toString () {
		return "Ladder " + super.toString();
	}

}

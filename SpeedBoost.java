import java.awt.*;
import java.awt.image.*;
import java.io.*;


public class SpeedBoost extends Block {
	
	public static int width = 45;
	public static int height = 45;
	
	public BufferedImage i;
	
	public SpeedBoost(int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(x,y,l,w,i);
	}
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		super.draw(g);
	}
	
	public String toString () {
		return "SpeedBoost " + super.toString();
	}

}

import java.awt.*;
import java.awt.image.*;
import java.io.*;


public class CrackedStone extends Block {
	
	public static final int width = 60;
	public static final int height = 40;
	public static final int breakTime = 3000;
	
	public BufferedImage i;
	
	public CrackedStone(int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(x,y,l,w,i);
	}
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		super.draw(g);
	}
	
	public String toString () {
		return "CrackedStone " + super.toString();
	}

}
 
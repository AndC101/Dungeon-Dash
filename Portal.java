import java.awt.*;
import java.awt.image.*;
import java.io.*;


public class Portal extends Block {
	
	int diffX, diffY;
	public BufferedImage i;
	
	public Portal(int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(x,y,l,w,i);
	}
	
	//draw the image from the block class
	public void draw(Graphics g) {
		super.draw(g);
	}

}
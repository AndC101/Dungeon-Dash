import java.awt.*;
import java.awt.image.*;
import java.io.*;


public class Stone extends Block {
	
	public static final int width = 40;
	public static final int height = 40;
	
	
	int diffX, diffY;
	public BufferedImage i;
	
	public Stone(int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(x,y,l,w,i);
	}
	
	//draw the image from the block class
	public void draw(Graphics g) {
		super.draw(g);
	}

}
 
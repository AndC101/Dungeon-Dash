import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;


public class Chest extends Block {
	
	int diffX, diffY;
	boolean opened;
	public BufferedImage i;
	
	public Chest(int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(x,y,l,w,i);
		opened = false;
	}
	
	public void openChest() throws IOException {
		opened = true;
		i = ImageIO.read(new File("Images/OpenChest.png"));
	}
	
	//draw the image from the block class
	public void draw(Graphics g) {
		super.draw(g);
	}
	
	

}

/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * Chest is the victory block that you touch to "open" and win the game
 */

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;


public class Chest extends Block {
	
	public boolean opened; //checks if chest is opened already
	public BufferedImage i; //image for chest
	
	public Chest(int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(x,y,l,w,i); // block constructor
		opened = false;
	}
	
	//called when user touches chest
	public void openChest() throws IOException {
		opened = true;
		i = ImageIO.read(new File("Images/OpenChest.png"));
	}
	
	//draw the image from the block class

	public void draw(Graphics2D g) {
		super.draw(g);
	}

	
	

}

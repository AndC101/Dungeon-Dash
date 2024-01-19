/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * Adds a heart to the knight
 */

import java.awt.*;
import java.awt.image.*;
import java.io.*;


public class OneUp extends Block {
	
	public static int width = 60;
	public static int height = 60;
	
	public BufferedImage i; //image for the powerup
	
	public OneUp(GamePanel gp, int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(gp, x,y,l,w,i); //block constructor
	}
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		super.draw(g);
	}
	
	//returns name and the constructor info
	public String toString () {
		return "OneUp " + super.toString();
	}

}
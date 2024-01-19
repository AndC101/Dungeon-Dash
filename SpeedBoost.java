/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * Speed increase powerup
 */

import java.awt.*;
import java.awt.image.*;
import java.io.*;


public class SpeedBoost extends Block {
	
	public static int width = 45;
	public static int height = 45;
	
	public BufferedImage i; //image for the powerup
	
	public SpeedBoost(GamePanel gp, int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(gp,x,y,l,w,i); //block constructor
	}
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		super.draw(g);
	}
	
	//returns name and constructor info
	public String toString () {
		return "SpeedBoost " + super.toString();
	}

}

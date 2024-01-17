/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * ladder is a block that allows knight to climb it 
 */

import java.awt.*;
import java.awt.image.*;
import java.io.*;


public class Chest extends Block {
	
	public static int width = 60;
	public static int height = 40;
	
	public BufferedImage i; //image for ladder
	
	public Chest(int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(x,y,l,w,i); //block constructor
	}
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		super.draw(g);
	}
	
	//returns name and constructor info
	public String toString () {
		return "Chest " + super.toString();
	}

}

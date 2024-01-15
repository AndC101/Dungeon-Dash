/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * Ice class is the main class to run the game
 */


import java.awt.*;
import java.awt.image.*;
import java.io.*;


public class Ice extends Block {
	
	public static int width = 60;
	public static int height = 40;

	public BufferedImage i; // ice image
	
	public Ice(int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(x,y,l,w,i); //block constructor
	}
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		super.draw(g);
	}

	//return block name and constructor info
	public String toString() {
		return "Ice " + super.toString();
	}
}

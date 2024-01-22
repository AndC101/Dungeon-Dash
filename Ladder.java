/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * ladder is a block that allows knight to climb it 
 */

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.HashSet;


public class Ladder extends Block {
	
	public static int width = 20;
	public static int height = 40;
	public int yVelocity;
	public BufferedImage i; //image for ladder
	public HashSet<Character> keysPressed = new HashSet<Character>();

	public Ladder(int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(x,y,l,w,i); //block constructor
	}

	//draw the image from the block class
	public void draw(Graphics2D g) {
		super.draw(g);
	}
	

	//returns name and constructor info
	public String toString () {
		return "Ladder " + super.toString();
	}

}

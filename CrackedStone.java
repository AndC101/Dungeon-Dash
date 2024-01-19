/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * CrackedStone disappears after a breakTime
 */

import java.awt.*;
import java.awt.image.*;
import java.io.*;


public class CrackedStone extends Block {
	
	public static int width = 60;
	public static int height = 40;
	public static final int breakTime = 1000;
	public long startBreak = -1;
	
	public BufferedImage i; //image for crackedstone
	
	public CrackedStone(int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(x,y,l,w,i); //block constructor 
	}
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		super.draw(g);
	}
	
	//name and constructor info
	public String toString () {
		return "CrackedStone " + super.toString();
	}

}
 
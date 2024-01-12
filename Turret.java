
/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * Turret shoots projectiles across the screen
 */

import java.awt.*;
import java.awt.image.*;
import java.io.*;


public class Turret extends Block {
	
	public static int width = 60;
	public static int height = 60;
	public BufferedImage i; //img for turret
	
	public Turret(int x, int y, int l, int w, BufferedImage i) throws IOException{
		super(x,y,l,w,i); //block constructor
	}
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		super.draw(g);
	}

	//returns name and constructor info
	public String toString () {
		return "Turret " + super.toString();
	}

}

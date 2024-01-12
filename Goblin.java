/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * goblin is a walking enemy
 */

import java.awt.*;
import java.io.*;


public class Goblin extends Block {
	
	
	public static int height = 50;
	public static int width = 50;
	Image animation; //animation image
	
	public Goblin(int x, int y, int l, int w, Image i) throws IOException{
		super(x,y,l,w);
		animation = i;
	}
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		g.drawImage(animation, x, y, null);
	}
	
		
	public void move() {
		x += xVelocity;
	}
	
	//returns name and the constructor info
	public String toString () {
		return "Goblin " + super.toString();
	}

}
 
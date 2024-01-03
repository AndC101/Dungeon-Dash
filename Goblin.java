import java.awt.*;
import java.io.*;


public class Goblin extends Block {
	
	
	public int yVelocity = -1;
	public int xVelocity = 0;
	public int moved = 0;
	public static int height = 50;
	public static int width = 50;
	Image animation;
	
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
		y += yVelocity;
	}
	
	public String toString () {
		return "Goblin " + super.toString();
	}

}
 
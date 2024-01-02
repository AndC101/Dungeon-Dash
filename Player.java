import java.awt.*;
import java.io.*;



import javax.swing.ImageIcon;



public class Player extends Rectangle {
	
	
	public int yVelocity = -1;
	public int xVelocity = 0;
	public int moved = 0;
	Image curAnimation;
	
	public Player(int x, int y, int l, int w) throws IOException{
		super(x,y,l,w);
	}
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		g.drawImage(curAnimation, x, y, null);
	}
	
	
	public void move() {
		x += xVelocity;
		y += yVelocity;
	}

}
 
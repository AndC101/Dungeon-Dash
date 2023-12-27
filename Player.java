import java.awt.*;
import java.io.*;

import javax.swing.ImageIcon;


public class Player extends Rectangle {
	
	Image afkAnimation = new ImageIcon("Images/KnightAfk.gif").getImage();
	
	public Player(int x, int y, int l, int w) throws IOException{
		super(x,y,l,w);
	}
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		g.drawImage(afkAnimation, x, y, null);
	}

}
 
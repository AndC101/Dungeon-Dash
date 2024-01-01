import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;


public class Block extends Rectangle {
	
	int diffX, diffY;
	boolean drag = false;
	BufferedImage img;
	
	public Block(int x, int y, int l, int w, BufferedImage i){
		super(x,y,l,w);
		img = i;
	}
	



	public void keyListener(MouseEvent e) {
		
	}
	
	public void mousePressed(MouseEvent e) {
		diffX = e.getX() - x;
        diffY = e.getY() - y;
        drag = true;
	}
	
	public void mouseReleased(MouseEvent e) {
		drag = false;
		
	}
	
	public void mouseDragged(MouseEvent e) {
		x = e.getX() - diffX; y = e.getY() - diffY;
	}

	public void draw(Graphics2D g) {
		g.drawImage(img, x, y, width, height, null);
	}

	public String toString() {
		return x + " " + y + " " + width + " " + height;
	}
}

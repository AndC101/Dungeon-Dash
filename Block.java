import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Block extends Rectangle {
	
	int diffX, diffY;
	
	public Block(int x, int y, int l, int w){
		super(x,y,l,w);
	}
	

	public void keyListener(MouseEvent e) {
		
	}
	
	public void mousePressed(MouseEvent e) {
		diffX = e.getX() - x;
        diffY = e.getY() - y;
	}
	
	public void mouseDragged(MouseEvent e) {
		x = e.getX() - diffX; y = e.getY() - diffY;
	}

	public void draw(Graphics g) {
		g.fillRect(x, y, width, height);
	}

}

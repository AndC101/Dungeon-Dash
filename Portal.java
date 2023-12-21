import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;


public class Portal extends Block {
	
	int diffX, diffY;
	public BufferedImage image; 
	
	public Portal(int x, int y, int l, int w) throws IOException{
		super(x,y,l,w);
		image = ImageIO.read(new File("Images/Start_Portal.png"));
	}
	
	public void draw(Graphics g) {
		g.drawImage(image, x, y, width, height, null);
	}

}

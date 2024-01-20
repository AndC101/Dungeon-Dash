import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class MainPanel extends JPanel {

    private Image img;
  
    public MainPanel(String img) {
      this(new ImageIcon(img).getImage());
    }
  
    public MainPanel(Image img) {
      this.img = img;
    }
  
    public void paintComponent(Graphics g) {
      g.drawImage(img, getX(), getY(), null);
    }
  
  }
  
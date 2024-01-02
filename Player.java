import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.ImageIcon;

public class Player extends Rectangle implements KeyListener {

    private Image afkAnimation = new ImageIcon("Images/KnightAfk.gif").getImage();
    private Image leftAnimation = new ImageIcon("Images/KnightRunLeft.gif").getImage();
    private Image rightAnimation = new ImageIcon("Images/KnightRunRight.gif").getImage();

    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;

    private int moveSpeed = 5;
    private int currentSpeed = 0;

    public Player(int x, int y, int l, int w) throws IOException {
        super(x, y, l, w);
    }

    public void draw(Graphics2D g) {
        if (isMovingLeft) {
            g.drawImage(leftAnimation, x, y, null);
        } else if (isMovingRight) {
            g.drawImage(rightAnimation, x, y, null);
        } else {
            g.drawImage(afkAnimation, x, y, null);
        }
    }

    public void update() {
        // Update the position based on the current speed
        x -= currentSpeed;

        // Gradually decrease the speed (simulate slowing down)
        if (currentSpeed > 0) {
            currentSpeed--;
        } else if (currentSpeed < 0) {
            currentSpeed++;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed for this example
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Handle left key press
        if (key == KeyEvent.VK_LEFT) {
            isMovingLeft = true;
            currentSpeed = moveSpeed;
        }
        // Handle right key press
        else if (key == KeyEvent.VK_RIGHT) {
            isMovingRight = true;
            currentSpeed = moveSpeed;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();


        // Reset animation when the keys are released
        if (key == KeyEvent.VK_LEFT) {
            isMovingLeft = false;
            currentSpeed = 0;
        } else if (key == KeyEvent.VK_RIGHT) {
            isMovingRight = false;
            currentSpeed = 0;
        }
    }
}

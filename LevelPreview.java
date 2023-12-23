import java.awt.*;

public class LevelPreview extends Rectangle {

    private int levelNum = 1;
    private static int width = 600;
    private static int height = 50;
    private static final int arcWidth = 10;
    private static final int arcLength = 10;

    public LevelPreview (int x, int y) {
        super(x,y,width,height);
        
    }

    public void draw (Graphics g) {
        g.setColor(Color.blue);
        g.drawRoundRect(x, y, width, height, arcWidth, arcLength);
        g.drawString("Level " + levelNum + ": ", x+10, y+20);
        
        levelNum++;
    }

}


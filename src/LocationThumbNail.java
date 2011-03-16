import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/16/11
 * Time: 5:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class LocationThumbnail extends JPanel {

    private final BufferedImage image;
    private Point focus;


    public LocationThumbnail(int xResolution, int yResolution){
        image = new MandelCanvasFactory(xResolution, yResolution).getHome().getAsBufferedImage();
        focus = new Point(xResolution/2, yResolution/2);
    }

    public void setFocus(Point newFocus){ focus = newFocus; }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // todo: increase opacity
        g.drawImage(image, 0, 0, null);

        // todo: draw box around zoom region if it would be visible
        g.setColor(Color.WHITE);
        final int x = (int) focus.getX();
        final int y = (int) focus.getY();
        g.drawLine(x, 0, x, this.getHeight());
        g.drawLine(0, y, this.getWidth(), y);
    }
}
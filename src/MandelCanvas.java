import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: David
 * Date: Feb 13, 2011
 * Time: 7:44:16 PM
 * To change this template use File | Settings | File Templates.
 */

/*
 * Class describes a region of the Mandelbrot set (zoomed out view initially),
 * the number of pixels used to represent that area and an equal number of
 * MandelPoints, and the BufferedImage entailed by mapping those pixels to
 * colors with Palette.java.
 *
 * Most of the associated GUI code for this picture is in MandelJPanel.java.
 */
class MandelCanvas {

    // describes the region of the Mandelbrot set to be displayed
    // todo: improve region program initially renders
    private double realMinimum = -2.5;
    private double imaginaryMaximum = 1.25;
    private double realMaximum = 1.0;
    private double imaginaryMinimum; //todo: set imaginaryMinimum

    // distance between pixels
    // if the aspect ratio of the logical picture does not match that of
    // the rendered (on screen), then you need two separate deltas:
    // realDelta and imaginaryDelta
    private double delta;

    private final int countOfXPixels;
    private final int countOfYPixels;

    private final MandelPoint[][] mandelPoints;

    // todo: increase iterationMax as picture is zoomed
    // todo: give user control over iterationMax
    private int iterationMax = 100;

    /**
     * Constructs a MandelCanvas appropriate for the input resolution.
     *
     * @param xRes number of horizontal pixels (real axis)
     * @param yRes number of vertical pixels (imaginary axis)
     */
    public MandelCanvas(final int xRes, final int yRes){
        delta = (realMaximum - realMinimum)/xRes;
        imaginaryMinimum = imaginaryMaximum - yRes * delta;
        countOfXPixels = xRes;
        countOfYPixels = yRes;
        mandelPoints = new MandelPoint[xRes][yRes];

        for(int x = 0; x < xRes; ++x)
            for(int y = 0; y < yRes; ++y)
                mandelPoints[x][y] = new MandelPoint(realMinimum + x * delta, imaginaryMaximum - y * delta);
    }

    /**
     * Maps a pixel to a MandelPoint to a Color (per Palette.java); ensures
     * the pixel has been iterated before returning result.
     *
     * @param x horizontal offset of pixel from top-left (0,0)
     * @param y vertical offset of pixel from top-left (0,0)
     * @return the color at input pixel
     */
    Color getColorAtPoint(final int x, final int y){
        final MandelPoint m = mandelPoints[x][y];
        m.iterate(iterationMax, 2.0);
        return Palette.getColor(m);
    }

    /**
     * Alters this object to describe a different area of the Mandelbrot set
     * per the region described by the input pixels (mouse clicks).
     *
     * todo: construct a new object instead of recalculating everything
     * todo: navigation history
     * todo: spawn new threads to do this (here might not be the best place)
     * todo: fix recalculating region to maintain aspect ratio
     * todo: refactor param names
     * todo: remove println, use status bar, describe new region
     *
     * @param upperLeftCorner first click of user (may not actually be upperleftcorner)
     * @param lowerRightCorner second click of user
     */
    public void doZoom(Point upperLeftCorner, Point lowerRightCorner){

        // todo: this method could use cleaning up & some unit tests

        // swap the click points if user clicked lower right corner before upper left corner
        if(upperLeftCorner.getX() > lowerRightCorner.getX() || upperLeftCorner.getY() > lowerRightCorner.getY()){
            Point tmp = lowerRightCorner;
            lowerRightCorner = upperLeftCorner;
            upperLeftCorner = tmp;
        }
        // translate first click into a complex number
        realMinimum = realMinimum + upperLeftCorner.getX() * delta;
        imaginaryMaximum = imaginaryMaximum - upperLeftCorner.getY() * delta;
        // translate second click into a complex number
        realMaximum = realMinimum + lowerRightCorner.getX() * delta;
        imaginaryMinimum = imaginaryMaximum - lowerRightCorner.getY() * delta;
        // delta - the complex distance between pixels - must be recalculated because we're zooming in
        // having real & imaginary axis share a delta keeps the aspect ratio correct
        delta = ((realMaximum - realMinimum)/countOfXPixels + (imaginaryMaximum - imaginaryMinimum)/countOfYPixels)/2.0;
        System.out.println("delta: " + delta);
        // update the array of complex points based on the new corners & new delta
        for(int x = 0; x < countOfXPixels; ++x)
            for(int y = 0; y < countOfYPixels; ++y)
               mandelPoints[x][y] = new MandelPoint(realMinimum + x * delta, imaginaryMaximum - y * delta);
    }

    /**
     * get an image with pixel data based on the MandelPoint's contained in the
     * described region
     *
     * it might be best to cache the buffered image but the JPanel that uses
     * this to display to the screen already (i think) caches it so it really
     * only affects saving the file
     *
     * @return the mandelbrot data, colored per Palette.java, as far as
     *  currently calculated
     */
    public BufferedImage getAsBufferedImage(){
        final BufferedImage img = new BufferedImage(countOfXPixels, countOfYPixels, BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < countOfXPixels; ++x)
            for(int y = 0; y < countOfYPixels; ++y)
                img.setRGB(x, y, getColorAtPoint(x, y).getRGB());
        return img;
    }

    /**
     * todo: recalculate the MandelPoint s to match new iteration limit
     * todo: tie this to the gui somewhere
     *
     * @param increase the number to add to the current iterationLimit (the
     *  bailout number of iterations for determining if a point is a member of
     *  the prisoner set).
     */
    public void increaseIterationMax(int increase){
        iterationMax += increase;
    }
}

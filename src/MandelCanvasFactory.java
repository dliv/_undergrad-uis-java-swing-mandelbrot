import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/15/11
 * Time: 4:44 AM
 * To change this template use File | Settings | File Templates.
 *
 * Assists in creating MandelCanvas objects.
 *
 * Current functionality:
 *      - creating the 'home' (zoomed out) starting MandelCanvas
 *      - reconstructing MandelCanvas objects from files of their serialized state
 *
 * Requirement 1.0.0 Display fractal image
 * Start up is different than zooming because you don't have the zoom inputs to
 * specify the fractal to be rendered. This class's method for calculating the
 * 'home' image (zoomed out view of Mandelbrot set) supports displaying the
 * initial image.
 *
 * Requirement 1.1.9 Open State File
 * This class provides a method for getting a MandelCanvas object from a state
 * file saved to disk (this file is a serialized SaveableState object, not a
 * serialized MandelCanvas object). // todo: avoid confusion, make MandelCanvas not implement serializable interface
 */
class MandelCanvasFactory {

    private final static double defaultRealMinimum = -3.5;
    private final static double defaultRealMaximum = 1.0;
    private final static double defaultImaginaryMaximum = 1.25;

    private final MandelCanvas home;

    /**
     * Constructor is pointless.
     *
     * TODO: static method which returns the 'home' view of MandelCanvas for input image sizes
     *
     * @param logicalImageSize
     * @param displayImageSize
     */
    public MandelCanvasFactory(final ImageSize logicalImageSize, final ImageSize displayImageSize){
        double defaultDelta = (defaultRealMaximum - defaultRealMinimum) / logicalImageSize.getWidth();
        double defaultImaginaryMinimum = defaultImaginaryMaximum - logicalImageSize.getHeight() * defaultDelta;
        int defaultIterationMax = 128;
        home = new MandelCanvas(
            new ComplexRegion(
                new ComplexNumber(defaultRealMinimum, defaultImaginaryMaximum),
                new ComplexNumber(defaultRealMaximum, defaultImaginaryMinimum)
            ),
            logicalImageSize,
            displayImageSize,
                defaultIterationMax,
            new PaletteSet().getDefault()
        );
        home.setAsSaved();
    }

    public MandelCanvas getHome(){
        return home;
    }

    /**
     * Retrieves MandelCanvas from a file containing serialized SaveableState
     * version of the MandelCanvas.
     *
     * Not very user friendly, presumably the GUI
     * will only call this method on files with the correct extension but if
     * an improper file is selected an error message rather than an exception
     * would be nicer.
     *
     * Uses a SaveableState object rather than the default serialization of
     * MandelCanvas because it is less likely to break the save/open state
     * feature everytime a program change is made.
     *
     * @param serialized
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static MandelCanvas unmarshallFromSaveableState(File serialized) throws IOException, ClassNotFoundException {
        assert null != serialized;
        final FileInputStream fis = new FileInputStream(serialized);
        final ObjectInputStream in = new ObjectInputStream(fis);
        final SaveableState ss = (SaveableState) in.readObject();
        final MandelCanvas out = ss.toMandelCanvas();
        out.calcLightWeightAttributes();
        return out;
    }
}

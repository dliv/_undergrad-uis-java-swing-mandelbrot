import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: David
 * Date: Feb 13, 2011
 * Time: 7:57:37 PM
 * To change this template use File | Settings | File Templates.
 *
 * Container window for every other GUI element.
 *
 * Requirement 1.1.0 GUI
 * Requirement 1.1.1 MenuBar
 * Requirement 1.1.2 Status Window
 * Requirement 1.1.3 Thumbnail Window
 */
class MainWindow extends JFrame {

    /**
     * Constructs the GUI which has the effect of launching the program.
     *
     * @param initialLogicalImageSize
     * @param fileToOpen saved state file which the program is being launched to examine; if null, program
     *  is being launched normally and should begin at the home screen (zoomed out view of the Mandelbrot
     *  set).
     */
    public MainWindow(final ImageSize initialLogicalImageSize, final File fileToOpen) {

        ImageSize logicalImageSize = initialLogicalImageSize;

        final int frameHeightAddition = 25;
        final int frameWidthAddition = 10;

        // this is somewhat naive b/c it gets the resolution of the primary display & doesn't consider multiple monitors
        final ImageSize monitorResolution = ImageSize.fromDimension(Toolkit.getDefaultToolkit().getScreenSize());
        // give from for title bar, menu bar, and possible bottom task bar
        final ImageSize mainWindow = new ImageSize(monitorResolution.getHeight() - 4 * frameHeightAddition, monitorResolution.getWidth());

        final int thumbNailSizeDivisor = 6;
        final ImageSize thumbNailImageSize = new ImageSize(ImageSize.REAL_HD.getHeight()/thumbNailSizeDivisor, ImageSize.REAL_HD.getWidth()/thumbNailSizeDivisor);
        final ImageSize thumbNailFrameSize = new ImageSize(thumbNailImageSize.getHeight() + frameHeightAddition, thumbNailImageSize.getWidth() + frameWidthAddition);

        final int statsTableAttributeColumnWidth = 50;
        final int statsTableValueColumnWidth = 225;
        final ImageSize renderStatsTableSize = new ImageSize(350, statsTableAttributeColumnWidth + statsTableValueColumnWidth);

        final int widthAvailable = mainWindow.getWidth() - (10 + renderStatsTableSize.getWidth());
        final int matchingHeight = (int)(widthAvailable * ((double) logicalImageSize.getHeight())/ logicalImageSize.getWidth());
        ImageSize displayedImageSize = new ImageSize(matchingHeight, widthAvailable);
        if(displayedImageSize.largerThan(logicalImageSize))
            displayedImageSize = logicalImageSize;
        final ImageSize renderWindowSize = new ImageSize(displayedImageSize.getHeight() + frameHeightAddition, displayedImageSize.getWidth() + frameWidthAddition);
        assert 0.001 > Math.abs(logicalImageSize.heightToWidth() - displayedImageSize.heightToWidth());

        final Pixel upperLeftCornerThumbNailWindow = new Pixel(mainWindow.getWidth() - thumbNailFrameSize.getWidth(), mainWindow.getHeight() - thumbNailFrameSize.getHeight());
        final Pixel upperLeftCornerStatsTable = new Pixel(mainWindow.getWidth() - renderStatsTableSize.getWidth(), 0);
        final Pixel upperLeftCornerRenderWindow = new Pixel(0, 0);

        // SETUP DISPLAY OF MAIN RENDER WINDOW
        // JInternalFrame (for JDesktopPane) + JPanel
        // ----------------------------------------------------
        final MandelJPanel mJPanel = new MandelJPanel(logicalImageSize, displayedImageSize, fileToOpen);
        // JInternalFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable)
        final JInternalFrame renderInternalFrame = new JInternalFrame("Render Window", true, false, true, true);
        renderInternalFrame.add(mJPanel);
        renderInternalFrame.setLocation(upperLeftCornerRenderWindow.asPoint());
        renderInternalFrame.setSize(renderWindowSize.asDimension());
        renderInternalFrame.setVisible(true);

        // SETUP DISPLAY OF LOCATION THUMBNAIL
        // -----------------------------------------------
        // JInternalFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable)
        final JInternalFrame locationThumbnailInternalFrame = new JInternalFrame("Zoom Location", false, false, false, true);
        final LocationThumbnail locationThumbnail = new LocationThumbnail(thumbNailImageSize, locationThumbnailInternalFrame);
        locationThumbnailInternalFrame.setVisible(false);
        locationThumbnailInternalFrame.setSize(thumbNailFrameSize.asDimension());
        locationThumbnailInternalFrame.add(locationThumbnail);
        locationThumbnailInternalFrame.setLocation(upperLeftCornerThumbNailWindow.asPoint());
        mJPanel.associateThumbnail(locationThumbnailInternalFrame);
        mJPanel.associateThumbnail(locationThumbnail);

        // SETUP DISPLAY OF RENDER STATISTICS TABLE
        // -----------------------------------------------
        // JInternalFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable)
        JInternalFrame attributeTableInternalFrame = new JInternalFrame("Attribute Values", true, false, false, true);
        attributeTableInternalFrame.setVisible(true);
        attributeTableInternalFrame.setSize(renderStatsTableSize.getWidth(), renderStatsTableSize.getHeight());
        String[] columnNames = { "Attribute", "Value" };
        Object[][] data = mJPanel.getAttributeValues();
        final JTable renderStats = new JTable(data, columnNames);
        renderStats.getColumnModel().getColumn(0).setWidth(statsTableAttributeColumnWidth);
        renderStats.getColumnModel().getColumn(1).setWidth(statsTableValueColumnWidth);
        attributeTableInternalFrame.setLayout(new BorderLayout());
        attributeTableInternalFrame.add(renderStats.getTableHeader(), BorderLayout.PAGE_START);
        attributeTableInternalFrame.add(renderStats, BorderLayout.CENTER);
        attributeTableInternalFrame.setLocation(upperLeftCornerStatsTable.asPoint());
        mJPanel.associateRenderStats(renderStats);

        // SETUP DESKTOP PANE WHICH HOLDS ALL INTERIOR WINDOWS
        // -------------------------------------------------------
        final JDesktopPane desktop = new JDesktopPane();
        desktop.add(renderInternalFrame);
        desktop.setVisible(true);
        desktop.add(locationThumbnailInternalFrame);
        desktop.add(attributeTableInternalFrame);

        // SETUP MAIN WINDOW WHICH CONTAINS DESKTOP PANE & MENUBAR
        // -------------------------------
        setTitle(VersionInfo.getTitle());
        final MenuBar menuBar = new MenuBar(mJPanel, this);
        setJMenuBar(menuBar);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // set an initial size so when we drag the window somewhere by the
        // menubar it doesn't return to a non-maximized size of (0x0)
        // todo: make returning to non-maximized size also resize interior windows
        setSize(displayedImageSize.asDimension());

        // http://stackoverflow.com/questions/479523/java-swing-maximize-window
        // best voted answer on SO, not sure why the bitwise OR is necessary though
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setContentPane(desktop);

        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(mJPanel.getNavigationHistory().getCurrent().needsSaving()){
                    if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(mJPanel, "Save before exiting?", "There is unsaved work...", JOptionPane.YES_NO_OPTION))
                        menuBar.saveState();
                }
            }
        });
    }
}

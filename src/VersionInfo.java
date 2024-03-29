/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/3/11
 * Time: 11:38 AM
 * To change this template use File | Settings | File Templates.
 *
 * Does not meet a specific requirement.
 *
 * Provides a way to source control the version number and debugging mode status.
 */
class VersionInfo {

    // set to true for developer builds
    // in regular builds, user can enable assertions to activate debug mode
    private final static boolean forceDebugMode = false;
    public final static boolean DEBUG = forceDebugMode || SystemInfo.areAssertionsEnabled();

    // incremented each time a build is released to be tested
    private static final int serialID = 8;

    // incremented after adding a major feature
    private static final int majorVersion = 5;
    // significant changes or bug fix, new minor feature
    private static final char minorVersion = 'B';
    // a new commit (if i remember) but not significant enough to incr minorVersion
    private static final int buildVersion = 0;

    public static String getVersion(){
        return serialID +
            "[" + majorVersion + "." +
            minorVersion + "." +
            buildVersion + "]" +
            (DEBUG ? "D" : "")
        ;
    }

    public static String getTitle(){
        return "HD-MMV: High Definition Multithreaded Mandelbrot Viewer \t \t v" +
            getVersion() + " \t \t " +
            (DEBUG ? "--DEBUG MODE--" : "")
        ;
    }
}

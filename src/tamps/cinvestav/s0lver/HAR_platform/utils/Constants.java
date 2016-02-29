package tamps.cinvestav.s0lver.HAR_platform.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/***
 * Constants employed overall the platform
 */
public class Constants {
    /***
     * I.e., one second in milliseconds
     */
    public static final int ONE_SECOND = 1000;

    /***
     * Standard date format for output of data (for instance, in file names).
     */
    public static final String DATE_FORMAT = "dd-MM-yyyy_HH-mm-ss";

    /***
     * Date format object for formating dates and keeping them uniform across the system
     */
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);

    /***
     * Unique classes recognized by the platform (currently 3)
     */
    public final static int UNIQUE_CLASES = 3;

    /***
     * Total dimensions employed by the trainer - classifier
     */
    public static final int TOTAL_DIMENSIONS = 2;

    /***
     * Position of the standard deviation dimension in the files
     */
    public static final int STD_DEV_DIMENSION = 0;

    /***
     * Position of the mean dimension in the files
     */
    public static final int MEAN_DIMENSION = 1;

    /***
     * Length of the accelerometer data window to be analyzed
     */
    public static final int WINDOW_LENGTH = 5 * ONE_SECOND;
}

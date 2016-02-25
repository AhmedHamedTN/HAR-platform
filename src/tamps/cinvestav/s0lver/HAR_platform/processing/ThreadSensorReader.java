package tamps.cinvestav.s0lver.HAR_platform.processing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.entities.AccelerometerReading;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/***
 * Reads data from accelerometer and triggers the processing of data
 */
public class ThreadSensorReader implements SensorEventListener{
    private static final int ONE_SECOND = 1000;
    private SensorManager sensorManager;
    private Sensor sensor;
    private int currentRun;
    private int sizeOfWindow;
    private int sizeOfAveragedSamples;
    private Timer timerReadings;
    private TimerTask timerTaskReading;
    private String activityType;
    private Date startTime;

    private ArrayList<AccelerometerReading> buffer;

    /***
     *
     * @param context Context for accessing the Sensor-Sensing system service
     * @param sizeOfWindow The size of the window on which data will be allocated, this is in millisecods
     * @param sizeOfAveragedSamples The size of the subsample to be averaged
     */
    public ThreadSensorReader(Context context, String activityType, int sizeOfWindow, int sizeOfAveragedSamples) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.activityType = activityType;
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        this.sizeOfWindow = sizeOfWindow;
        this.sizeOfAveragedSamples = sizeOfAveragedSamples;
        this.currentRun = 1;
        this.startTime = new Date(System.currentTimeMillis());
    }

    /***
     * Triggers the readings according to parameters specified in constructor
     */
    public void startReadings(){
        Log.i(this.getClass().getSimpleName(), "Starting readings");
        buffer = new ArrayList<>();
        scheduleTimer();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /***
     * Schedules the timer that will <b>tick</b> once a window sampling is completed
     */
    private void scheduleTimer() {
        Log.i(this.getClass().getSimpleName(), "Scheduling timer");
        timerReadings = new Timer("timerReadings");
        timerTaskReading = new TimerTaskReadings();
        timerReadings.scheduleAtFixedRate(timerTaskReading, sizeOfWindow, sizeOfWindow);
    }

    /***
     * Stop readings and the associated timers and processing components
     */
    public void stopReadings(){
        Log.i(this.getClass().getSimpleName(), "Stopping readings");
        sensorManager.unregisterListener(this, sensor);
        cancelTimer();
    }

    /***
     * Cancels the timer (if there are any pending ticks)
     */
    private void cancelTimer() {
        Log.i(this.getClass().getSimpleName(), "Cancelling timer");
        timerTaskReading.cancel();
        timerReadings.cancel();
        timerReadings.purge();
    }

    /***
     * Called when a window sampling is completed.
     * Data is packed and processed in one of these windows.
     */
    private void timeoutReached() {
        Log.i(this.getClass().getSimpleName(), "Timeout reached, window filled");
        String filePrefix = activityType + "_" + (sizeOfWindow/ONE_SECOND) + "_secs_" + sizeOfAveragedSamples + "_fused_";
        ThreadDataProcessor threadDataProcessor = new ThreadDataProcessor(startTime, currentRun++, filePrefix, buffer, sizeOfAveragedSamples);
        Thread thread = new Thread(threadDataProcessor);
        thread.start();
        buffer = new ArrayList<>();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        AccelerometerReading accReading = new AccelerometerReading(values[0], values[1], values[2], System.currentTimeMillis());
        buffer.add(accReading);
//        Log.i(this.getClass().getSimpleName(), "Got: " + accReading);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {    }

    /***
     * Inner class for detecting each time a sampling window is filled.
     */
    class TimerTaskReadings extends TimerTask {
        @Override
        public void run() {
            timeoutReached();
        }
    }
}

package qut.wearable_remake.sensors;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandIOException;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.SampleRate;


import qut.wearable_remake.SpecialEventListener;
import qut.wearable_remake.band.ProjectClient;

public class AccSensor implements SensorInterface {
    private static final long BOUNCE_TIME = 750;
    private static final double ACCELERATION_THRESHOLD = 0.3;

    private final ProjectClient projectClient;
    private final BandAccelerometerEventListener listener;

    private long lastMovement;
    private boolean moving;
    private float offset;

    public AccSensor(final ProjectClient pc, final SpecialEventListener specialEvent) {
        projectClient = pc;

        listener = new BandAccelerometerEventListener() {
            @Override
            public void onBandAccelerometerChanged(BandAccelerometerEvent bandEvent) {
                float x = bandEvent.getAccelerationX();
                float y = bandEvent.getAccelerationY();
                float z = bandEvent.getAccelerationZ();
                long time = bandEvent.getTimestamp();

                /** Detect Orientation **/
//                if (time > lastOrientation + BOUNCE_TIME) {
//                    lastOrientation = time;
//                    if (y < -ORIENTATION_THRESHOLD) {
//                        orientation = "Tilt Right";
//                    } else if (z > ORIENTATION_THRESHOLD) {
//                        orientation = "Flat";
//                    } else if (y > ORIENTATION_THRESHOLD) {
//                        orientation = "Tilt Left";
//                    } else if (z < -ORIENTATION_THRESHOLD) {
//                        orientation = "Upside Down";
//                    } else if (x < -ORIENTATION_THRESHOLD) {
//                        orientation = "Vertical Up";
//                    } else {
//                        orientation = "Vertical Down";
//                    }
//                }

                /** Count Movements **/
                float sum = x + y + z;
                if (!moving && (sum - offset > ACCELERATION_THRESHOLD || sum - offset < -ACCELERATION_THRESHOLD)) {
                    moving = true;
                } else {
                    if (moving && time > lastMovement + BOUNCE_TIME) {
                        lastMovement = time;
                        specialEvent.onMoveCountChanged();
                    }
                    moving = false;
                    offset = sum;
                }

                /** Write Data to File **/
                if (projectClient.getProjectContact().getWorn()) {
                    specialEvent.onAccChanged(time, sum);
                }
            }
        };
    }

    /**
     * Registers the sensor's event listener.
     *
     * @param bandClient The BandClient whose event listener is registered.
     */
    @Override
    public void registerListener(BandClient bandClient) {
        try {
            bandClient.getSensorManager().registerAccelerometerEventListener(listener, SampleRate.MS128);
        } catch (BandIOException ex) {
            ex.printStackTrace();
        }
    } // end registerListener()

    /**
     * Unregisters the sensor's event listener.
     *
     * @param bandClient The BandClient whose event listener is unregistered.
     */
    @Override
    public void unregisterListener(BandClient bandClient) {
        try {
            bandClient.getSensorManager().unregisterAccelerometerEventListener(listener);
        } catch (BandIOException ex) {
            ex.printStackTrace();
        }
    } // end unregisterListener()
}
package qut.wearable_remake.sensors;

import com.microsoft.band.BandClient;

public interface SensorInterface {

    /**
     * Registers the sensor's event listener.
     *
     * @param bandClient The BandClient whose event listener is registered.
     */
    void registerListener(BandClient bandClient);

    /**
     * Unregisters the sensor's event listener.
     *
     * @param bandClient The BandClient whose event listener is unregistered.
     */
    void unregisterListener(BandClient bandClient);
}

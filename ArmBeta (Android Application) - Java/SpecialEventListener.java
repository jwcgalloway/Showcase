package qut.wearable_remake;

import com.microsoft.band.BandClient;

public interface SpecialEventListener {

    /**
     * Called after the device has connected to the Band.
     * Checks to see if a previous setup exists on the device and/or Band.  If a previous setup is
     * found, the existing data is loaded otherwise a fresh setup is undertaken.
     *
     * @param bandClients The Band client returned from the ConnectAsync task
     */
    void onConnectDone(BandClient bandClients);

    /**
     * Called after the app has been fully setup or fully loaded.
     * Initialises all save-dependent page data, such as graphs.
     */
    void onSetupDone();

    /**
     * Called when the moveCount variable has been updated.
     * Writes the new count to the 'move_count' local file and updates the value displayed
     * on the device.
     */
    void onMoveCountChanged();

    /**
     * Called when the accelerometer data has changed.
     * Writes the accelerometer data to the 'acc_data' local file, sets the orientation text view to
     * the given string, and refreshes the graph, provided live graphing is checked.
     *
     * @param timestamp The timestamp that the accelerometer data was taken at.
     * @param accData The accelerometer data.
     */
    void onAccChanged(long timestamp, float accData);


}
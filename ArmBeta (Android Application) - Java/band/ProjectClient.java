package qut.wearable_remake.band;

import android.app.Activity;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandException;
import com.microsoft.band.notifications.VibrationType;
import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.WrappedTextBlockData;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import qut.wearable_remake.HelperMethods;
import qut.wearable_remake.SpecialEventListener;
import qut.wearable_remake.sensors.AccSensor;
import qut.wearable_remake.sensors.ContactSensor;
import qut.wearable_remake.sensors.SensorInterface;

/**
 * Contains functions related to the communication of the Band with the user's device.
 */
public class ProjectClient {
    private final BandClient bandClient;
    private final ContactSensor projectContact;
    private final AccSensor projectAcc;

    private UUID tileId, pageId;

    public ProjectClient(BandClient c, SpecialEventListener e) {
        bandClient = c;
        //pageId = UUID.randomUUID();

        projectAcc = new AccSensor(this, e);
        projectContact = new ContactSensor();
    }

    /**
     * Sends a dialog to the Band.
     *
     * @param title The title of the dialog.
     * @param msg The content of the dialog.
     */
    public void sendDialog(String title, String msg) {
        try {
            bandClient.getNotificationManager().showDialog(tileId, title, msg).await();
        } catch (InterruptedException | BandException ex) {
            ex.printStackTrace();
        }
    } // end sendDialog()

    /**
     * Sends a haptic (vibration) to the Band.
     */
    public void sendHaptic() {
        try {
            bandClient.getNotificationManager().vibrate(VibrationType.NOTIFICATION_ONE_TONE);
        } catch (BandException e) {
            e.printStackTrace();
        }
    } // end sendHaptic()

    /**
     * Removes the project tile from the Band.
     */
    public void removeTile() {
        try {
            bandClient.getTileManager().removeTile(tileId).await();
        } catch (BandException | InterruptedException ex) {
            ex.printStackTrace();
        }
    } // end removeTile()

    /**
     * Sets the initial content of the pages on the Band.
     */
    public void setMovePageData(int moveCount) {
        WrappedTextBlockData headingData = new WrappedTextBlockData(1, "Movements:");

        WrappedTextBlockData contentData =
                new WrappedTextBlockData(2, String.format(Locale.getDefault(), "%d", moveCount));

        PageData data = new PageData(pageId, 0)
                .update(headingData).update(contentData);

        try {
            bandClient.getTileManager().setPages(tileId, data).await();
        } catch (InterruptedException | BandException e) {
            e.printStackTrace();
        }
    } // end setMovePageData()


    public void setTileId(UUID id) { tileId = id; }
    public void setPageId(UUID id) { pageId = id; }

    public BandClient getBandClient() { return bandClient; }
    public ContactSensor getProjectContact() { return projectContact; }
    public SensorInterface[] getSensors() { return new SensorInterface[]{projectContact, projectAcc}; }
}

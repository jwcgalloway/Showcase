package qut.wearable_remake.band;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;

import qut.wearable_remake.SpecialEventListener;

/**
 * Class used solely to asynchronously connect the device to any paired Bands.
 */
public class ConnectAsync extends AsyncTask<Void, Void, Boolean> {
    private ProgressDialog connectDialog;
    private BandClient bandClient;
    private final Activity activity;
    private final SpecialEventListener listener;

    public ConnectAsync(Activity a, SpecialEventListener l) {
        super();
        activity = a;
        listener = l;
    }

    /**
     * Start the progress dialog while the connection takes place.
     */
    @Override
    protected void onPreExecute() {
        connectDialog = ProgressDialog.show(activity, "", "Connecting...", true);
    } // end onPreExecute()

    /**
     * Attempt to connect to Band.
     */
    @Override
    protected Boolean doInBackground(Void...params) {
        BandInfo[] pairedBands = BandClientManager.getInstance().getPairedBands();

        try {
            bandClient = BandClientManager.getInstance().create(activity, pairedBands[0]);
            BandPendingResult<ConnectionState> pendingResult = bandClient.connect();
            ConnectionState state = pendingResult.await();
            if (state == ConnectionState.CONNECTED) {
                return true;
            }
        } catch (IndexOutOfBoundsException | InterruptedException | BandException e) {
            e.printStackTrace();
        }
        return false;
    } // end doInBackground()

    /**
     * Dismiss progress dialog upon completion and return the Band client through the callback.
     */
    @Override
    protected void onPostExecute(Boolean connected) {
        if (!connected) {
            Toast.makeText(activity, "Connection Failed.", Toast.LENGTH_LONG).show();
        }
        listener.onConnectDone(bandClient);
        connectDialog.dismiss();
    } // end onPostExecute()
} // end ConnectAsync

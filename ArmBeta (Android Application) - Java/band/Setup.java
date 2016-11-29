package qut.wearable_remake.band;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.microsoft.band.BandException;
import com.microsoft.band.tiles.BandIcon;
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.pages.FlowPanelOrientation;
import com.microsoft.band.tiles.pages.PageLayout;
import com.microsoft.band.tiles.pages.PageRect;
import com.microsoft.band.tiles.pages.ScrollFlowPanel;
import com.microsoft.band.tiles.pages.WrappedTextBlock;
import com.microsoft.band.tiles.pages.WrappedTextBlockFont;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.UUID;

import qut.wearable_remake.HelperMethods;
import qut.wearable_remake.SpecialEventListener;

/**
 * Group of static methods relating to the installation and setup of the app on both the device and
 * Band.
 */
public class Setup extends AsyncTask<Void, Void, Boolean> {
    private final Activity activity;
    private final ProjectClient projectClient;
    private final UUID tileId, pageId;
    private final SpecialEventListener listener;
    private ProgressDialog setupDialog;

    public Setup(Activity a, ProjectClient pc, SpecialEventListener l) {
        activity = a;
        projectClient = pc;
        tileId = UUID.randomUUID();
        pageId = UUID.randomUUID();
        listener = l;

        String str = tileId.toString() + "," + pageId.toString();
        HelperMethods.writeToFile("app_id", str, a);
    }

    /**
     * Start the progress dialog while the connection takes place.
     */
    @Override
    protected void onPreExecute() {
        setupDialog = ProgressDialog.show(activity, "", "Installing...", true);
    } // end onPreExecute()

    /**
     * Attempt to initialise and install the relevant files on the device and Band.  Band tile ID
     * and page ID are set upon success.
     *
     * @return True if all actions were successful, otherwise false.
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        boolean setup = createLocalFile("acc_data") && createLocalFile("move_count") && createTile();
        if (setup) {
            // Write default values to save files
            HelperMethods.writeToFile("acc_data", "0,0", activity);

            String[] splitDate = HelperMethods.getCurrentDate().split(":");
            for (int i = 1; i <= 24; i++) {

                // Fake move counts for testing & demonstration
                Random rand = new Random();
                int fake;
                if (i < 7) {
                    fake = rand.nextInt(100) + 1;
                } else if (i < 18) {
                    fake = rand.nextInt(500) + 100;
                } else {
                    fake = 0;
                }

                HelperMethods.writeToFile("move_count", splitDate[0] + ":" + Integer.toString(i) +
                        "," + Integer.toString(fake) + "\n", activity);
                // End Fake

                //HelperMethods.writeToFile("move_count", splitDate[0] + ":" + Integer.toString(i) + ",0\n", activity);
            }

            projectClient.setTileId(tileId);
            projectClient.setPageId(pageId);
            projectClient.sendDialog("Wearable", "Tap to continue...");
            return true;
        }
        return false;
    } // end doInBackground()

    /**
     * Dismiss progress dialog upon completion and display error message upon failure.
     */
    @Override
    protected void onPostExecute(Boolean success) {
        if (!success) {
            Toast.makeText(activity, "Setup Failed.", Toast.LENGTH_LONG).show();
        }
        setupDialog.dismiss();
        listener.onSetupDone();
    } // end onPostExecute()

    /**
     * Private method to create local save file for given data upon startup.
     *
     * @param filename the filename of the file to be created.
     */
    private boolean createLocalFile(String filename) {
        try {
            FileOutputStream fos = activity.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.close();
            return true;
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    } // end createLocalFile()

    /**
     * Creates the project tile on the Band.
     *
     * @return True if the tile was successfully created, otherwise false.
     */
    private boolean createTile() {
        BandIcon largeIcon = BandIcon.toBandIcon(getIconFromAssets()); // TODO Comes out as a big ol' square for some reason
        PageLayout layout = createLayout();

        BandTile tile = new BandTile.Builder(tileId, "Wearable", largeIcon)
                .setPageLayouts(layout)
                .build();

        try {
            if (projectClient.getBandClient().getTileManager().addTile(activity, tile).await()) {
                return true;
            }
        } catch (InterruptedException | BandException e) {
            e.printStackTrace();
        }
        return false;
    } // end createTile()

    /**
     * Creates the layout of the pages on the Band.
     *
     * @return Layout of the pages.
     */
    private PageLayout createLayout() {
        // Scrollable Vertical Panel
        ScrollFlowPanel panel = new ScrollFlowPanel(new PageRect(0, 0, 258, 128));
        panel.setFlowPanelOrientation(FlowPanelOrientation.VERTICAL);

        // Heading
        WrappedTextBlock heading =
                new WrappedTextBlock(new PageRect(0, 0, 258, 128), WrappedTextBlockFont.MEDIUM);
        heading.setId(1);
        heading.setColor(Color.argb(0, 184, 255, 29)); // Light Green

        // Content
        WrappedTextBlock content =
                new WrappedTextBlock(new PageRect(0, 0, 258, 128), WrappedTextBlockFont.MEDIUM);
        content.setId(2);

        panel.addElements(heading, content);
        return new PageLayout(panel);
    } // end createLayout()

    /**
     * Reads the icon image from the assets file into a bitmap.
     *
     * @return The bitmap with the icon image read into it.
     */
    private Bitmap getIconFromAssets() {
        try {
            InputStream is = activity.getAssets().open("icon.bmp");
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    } // end getIconFromAssets()
} // end Setup

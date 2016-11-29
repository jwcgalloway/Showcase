package qut.wearable_remake;

import android.app.Activity;
import android.content.Context;

import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class HelperMethods {

    /**
     * Checks to see if there is an existing instance of the application by checking if the internal
     * save files already exist.
     *
     * @param context Context of the current page.
     * @param fileName Name of internally saved file to be checked if exists.
     * @return True is installed, otherwise false.
     */
    static boolean isInstalled(Context context, String fileName) {
        String filePath = context.getFilesDir().toString() + String.format("/%s", fileName);
        File file = new File(filePath);
        return file.exists();
    } // end isInstalled()

    /**
     * Writes a given string to a given file.
     *
     * @param filename The filepath of the file to be edited.
     * @param content The string that will be written.
     * @param activity The activity used to obtain the filepath.
     */
    public static void writeToFile(String filename, String content, Activity activity) {
        try {
            FileOutputStream fos = activity.openFileOutput(filename, Context.MODE_APPEND);
            fos.write(content.getBytes());
            fos.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    } // end writeToFile()

    /**
     * Converts the contents of an input stream to a string.
     *
     * @param stream The input stream to be converted.
     * @return The contents of the input stream as a string.
     * @throws IOException If line could not be read.
     */
    private static String streamToString(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        @SuppressWarnings("UnusedAssignment")
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    } // end streamToString()

    /**
     * Reads a given file and returns its contents as a string.  The contents of the file are then
     * deleted.
     *
     * @param fileName The name of the file to be read.
     * @param  context Context of the current activity.
     * @return The string contents of the file.
     * @throws IOException If file is not found.
     */
    public static String getDataFromFile(String fileName, Context context) throws IOException {
        String filePath = context.getFilesDir().toString() + String.format("/%s", fileName);
        File file = new File(filePath);
        FileInputStream stream = new FileInputStream(file);
        String str = streamToString(stream);
        stream.close();

        PrintWriter pw = new PrintWriter(filePath);
        pw.close();

        return str;
    } // end getDataFromFile()

    /**
     * Reads the saved UUID data and returns an array of 2 consisting the UUID and pageID.
     *
     * @param a Activity for the read file function.
     * @return The array of type UUID of the app's saved UUID and pageID
     */
    public static List<UUID> getUUID (Activity a) {
        String uuid_str = null;
        try {
            uuid_str = HelperMethods.getDataFromFile("app_id", a);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> strings = Arrays.asList(uuid_str.split(","));
        List<UUID> uuid_list = new ArrayList<>();
        for (String i:strings) {
            UUID val = UUID.fromString(i);
            uuid_list.add(val);
        }
        return uuid_list;
    }

    /**
     * Gets the current date & time and returns it in string form using the following format:
     * dd.MM.yyyy:HH
     *
     * @return The string representation of the current date & time
     */
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy:HH", Locale.getDefault());
        Date currentDate = Calendar.getInstance().getTime();
        return dateFormat.format(currentDate);
    } // end getCurrentDate()

    /**
     * Separates the hour segment from a date string and converts and returns it as an int.
     * The hour string must be the last segment, preceded by a colon, eg: dd.MM.yyyy:HH.
     *
     * @param date The string representation of the date.
     * @return The hour segment in int form.
     */
    public static int getHourFromDate(String date) {
        return Integer.parseInt(date.split(":")[1]);
    } // end getHourFromDate


}

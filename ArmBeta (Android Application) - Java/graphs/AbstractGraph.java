package qut.wearable_remake.graphs;

import android.app.Activity;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.DataSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import qut.wearable_remake.HelperMethods;

abstract class AbstractGraph {
    private final Chart chart;
    private final String saveFilename;
    private final Activity activity;

    AbstractGraph(Chart c, Activity a, String sfn) {
        chart = c;
        activity = a;
        saveFilename = sfn;
    }

    /**
     * Gets, splits and returns the value pairs (X,Y) from the graph's data file.
     *
     * @return An array list containing each value pair.
     */
    ArrayList<String[]> getDataFromFile() {
        String dataStr = "";
        try {
            dataStr = HelperMethods.getDataFromFile(saveFilename, activity);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String[]> strValuePairs = new ArrayList<>();

        Scanner scanner = new Scanner(dataStr);
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            String[] splitVals = nextLine.split(",");

            strValuePairs.add(splitVals);
        }
        scanner.close();

        return strValuePairs;
    } // end getDataFromFile()

    /**
     * Updates the graphical display of the graph with the provided data.
     */
    public void updateDisplay() {
        chart.notifyDataSetChanged();
        chart.invalidate();
    } // end updateDisplay()

    Activity getActivity() { return activity; }
}
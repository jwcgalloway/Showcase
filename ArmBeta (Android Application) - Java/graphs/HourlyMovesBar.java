package qut.wearable_remake.graphs;

import android.app.Activity;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

import qut.wearable_remake.HelperMethods;
import qut.wearable_remake.WearableApplication;

public class HourlyMovesBar extends AbstractGraph {
    private final BarDataSet dataSet;
    private final BarChart chart;

    public HourlyMovesBar(BarChart bc, Activity a) {
        super(bc, a, "move_count");

        chart = bc;
        dataSet = loadSavedData();
        dataSet.setDrawValues(false);

        ArrayList<Integer> colourScheme = new ArrayList<>();
        colourScheme.add(Color.argb(255, 51, 188, 161));
        colourScheme.add(Color.argb(235, 51, 188, 161));
        colourScheme.add(Color.argb(215, 51, 188, 161));
        colourScheme.add(Color.argb(195, 51, 188, 161));
        colourScheme.add(Color.argb(175, 51, 188, 161));
        colourScheme.add(Color.argb(165, 51, 188, 161));
        dataSet.setColors(colourScheme);

        BarData graphData = new BarData(dataSet);
        graphData.setBarWidth(0.95f);
        bc.setData(graphData);
        bc.setDescription("");
        bc.setDrawValueAboveBar(true);
        bc.getLegend().setEnabled(false);
        bc.getAxisRight().setDrawLabels(false);
        bc.setBorderColor(Color.GRAY);
        bc.setBorderWidth(0.5f);
        bc.setHighlightPerTapEnabled(false);

        XAxis xAxis = bc.getXAxis();
        xAxis.setValueFormatter(new DateFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);

        YAxis yAxisLeft = bc.getAxisLeft();
        yAxisLeft.setGranularity(10);

        this.updateDisplay();
    }

    /**
     * Increments the move count at a particular key in the chart's data.
     *
     * @param x The X value of the data point.
     */
    public void incrementDataSet(String x) {
        int hour = HelperMethods.getHourFromDate(x);
        BarEntry entry = dataSet.getEntryForXPos(hour);

        if (entry == null) {
            dataSet.addEntry(new BarEntry(hour, 1));
        } else {
            entry.setY(entry.getY() + 1);
        }

        dataSet.notifyDataSetChanged();
        BarData graphData = new BarData(dataSet);
        graphData.setBarWidth(0.95f);
        chart.setData(graphData);
    } // end incrementDataSet()

    /**
     * Reads the data saved in the chart's save file, parses and returns it in Chart Data form.
     */
    private BarDataSet loadSavedData() {
        int totalMoves = 0;
        ArrayList<BarEntry> loadedEntries = new ArrayList<>();

        for (String[] pair : this.getDataFromFile()) {
            int xVal = HelperMethods.getHourFromDate(pair[0]);
            int yVal = Integer.parseInt(pair[1]);

            totalMoves = totalMoves + yVal;
            loadedEntries.add(new BarEntry(xVal, yVal));
        }

        ((WearableApplication) this.getActivity().getApplication()).setTotalMovesToday(totalMoves);
        return new BarDataSet(loadedEntries, "");
    } // end loadSavedData()

    /**
     * Saves the current state of the chart's data to the save file.
     */
    private void saveData() {
        String date = HelperMethods.getCurrentDate().split(":")[0];
        for (int i = 0; i < dataSet.getEntryCount(); i++) {
            BarEntry entry = dataSet.getEntryForIndex(i);
            String content = date + ":" + Float.toString(entry.getX()) + "," + Float.toString(entry.getY());
            HelperMethods.writeToFile("move_count", content, this.getActivity());
        }
    } // end saveData()
}
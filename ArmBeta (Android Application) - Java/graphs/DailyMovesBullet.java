package qut.wearable_remake.graphs;

import android.app.Activity;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

import qut.wearable_remake.WearableApplication;

public class DailyMovesBullet extends AbstractGraph {
    private int moveGoal;
    private int moveCount;
    private BarDataSet dataSet;
    private LimitLine limitLine;
    private BarChart hc;
    public DailyMovesBullet(HorizontalBarChart hc, Activity a) {
        super(hc, a, "some_thing");
        this.hc = hc;
        hc.setDescription("");
        hc.getLegend().setEnabled(false);
        hc.getAxisLeft().setDrawLabels(false);

        YAxis yAxisRight = hc.getAxisRight();
        yAxisRight.setEnabled(true);


        XAxis xAxis = hc.getXAxis();
        xAxis.setGranularity(10);
        xAxis.setDrawLabels(false);

        moveGoal = ((WearableApplication) this.getActivity().getApplication()).getMoveGoal();

        int segmentSize = moveGoal / 5;
        ArrayList<BarEntry> defaultEntries = new ArrayList<>();
        defaultEntries.add(new BarEntry(1, segmentSize * 6));
        defaultEntries.add(new BarEntry(1, segmentSize * 5));
        defaultEntries.add(new BarEntry(1, segmentSize * 4));
        defaultEntries.add(new BarEntry(1, segmentSize * 3));
        defaultEntries.add(new BarEntry(1, segmentSize * 2));
        defaultEntries.add(new BarEntry(1, segmentSize));

        moveCount = ((WearableApplication) this.getActivity().getApplication()).getTotalMovesToday();
        defaultEntries.add(new BarEntry(1, moveCount));
        dataSet = new BarDataSet(defaultEntries, "");

        ArrayList<Integer> colourScheme = new ArrayList<>();
        colourScheme.add(Color.argb(75, 51, 188, 161));
        colourScheme.add(Color.argb(100, 51, 188, 161));
        colourScheme.add(Color.argb(125, 51, 188, 161));
        colourScheme.add(Color.argb(150, 51, 188, 161));
        colourScheme.add(Color.argb(175, 51, 188, 161));
        colourScheme.add(Color.argb(255, 51, 188, 161));
        colourScheme.add(Color.BLACK);

        dataSet.setColors(colourScheme);
        dataSet.setDrawValues(false);

        BarData graphData = new BarData(dataSet);
        hc.setData(graphData);

        limitLine = new LimitLine(moveGoal, "");
        limitLine.setLineWidth(1);
        limitLine.setLineColor(Color.rgb(124, 124, 124));
        yAxisRight.addLimitLine(limitLine);

        dataSet.notifyDataSetChanged();
        this.updateDisplay();
    }

    //called after update move goal
    private void adjustSegments(){
        int segmentSize = moveGoal / 5;
        for(int i = 0; i < 6; i++){
            BarEntry entry = dataSet.getEntryForIndex(i);
            entry.setY(segmentSize *(6-i));
        }
        dataSet.notifyDataSetChanged();
        hc.setData(new BarData(dataSet));

    }
    private void updateMoveGoal(){
        //Todo
        moveGoal = ((WearableApplication) this.getActivity().getApplication()).getMoveGoal();

    }
    private void updateMoveCount(){
         moveCount = ((WearableApplication) this.getActivity().getApplication()).getTotalMovesToday();
    }
    public void updateMoveGoalLine(){
        //remove limitLines
        updateMoveGoal();
        adjustSegments();
        hc.getAxisRight().removeAllLimitLines();
        limitLine = new LimitLine(moveGoal, "");
        limitLine.setLineWidth(1);
        limitLine.setLineColor(Color.BLACK);
        hc.getAxisRight().addLimitLine(limitLine);
        this.updateDisplay();
    }

    public void updateDataSet(){
        updateMoveCount();
        BarEntry entry = dataSet.getEntryForIndex(6);
        entry.setY(entry.getY()+1);
        dataSet.notifyDataSetChanged();
        this.updateDisplay();
    }
}

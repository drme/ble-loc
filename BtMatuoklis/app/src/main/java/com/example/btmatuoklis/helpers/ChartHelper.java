package com.example.btmatuoklis.helpers;

import android.graphics.Color;

import com.example.btmatuoklis.classes.Beacon;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

public class ChartHelper {

    ValueDependentColor<DataPoint> outsideCursor, insideCursor;

    public ChartHelper(){
        this.setOutsideCursorColor();
        this.setInsideCursorColor();
    }

    public void setFullChart(GraphView graph, Beacon beacon){
        ArrayList<Byte> uniqueRSSIs = beacon.getUniqueRSSIs();
        ArrayList<Byte> frequnecies = beacon.countRSSIFrequencies();
        int size = uniqueRSSIs.size();
        DataPoint[] data = new DataPoint[size];
        for (int i = 0; i < size; i++){
            data[i] = new DataPoint(uniqueRSSIs.get(i), frequnecies.get(i));
        }
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(beacon.getRSSIMin());
        graph.getViewport().setMaxX(beacon.getRSSIMax());
        graph.getViewport().setMaxY(Collections.max(frequnecies));
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(data);
        series.setSpacing(5);
        graph.addSeries(series);
    }

    public void setFullSpacedChart(GraphView graph, Beacon beacon){
        ArrayList<Byte> uniqueRSSIs = beacon.getSpacedRSSIs();
        ArrayList<Byte> frequnecies = beacon.countSpacedRSSIFrequencies();
        int size = uniqueRSSIs.size();
        DataPoint[] data = new DataPoint[size];
        for (int i = 0; i < size; i++){
            data[i] = new DataPoint(uniqueRSSIs.get(i), frequnecies.get(i));
        }
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(beacon.getRSSIMin());
        graph.getViewport().setMaxX(beacon.getRSSIMax());
        graph.getViewport().setMaxY(Collections.max(frequnecies));
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(data);
        series.setSpacing(5);
        graph.addSeries(series);
    }

    public void setScanChart(GraphView graph, Byte rssiMin){
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setNumVerticalLabels(2);
        graph.getGridLabelRenderer().setNumHorizontalLabels(10);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(-90);
        graph.getViewport().setMaxX(0);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(1);
    }

    public void updateScanChart(GraphView graph, Byte rssiMin, Beacon beacon){
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>( new DataPoint[]{
                new DataPoint(0, 0),
                new DataPoint(rssiMin, 0)
        });
        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<DataPoint>( new DataPoint[]{
                new DataPoint(beacon.getRSSIAverage(), 1)
        });
        series1.setThickness(10);
        series2.setSpacing(100);
        if (beacon.getRSSIAverage() < rssiMin){ series2.setValueDependentColor(getOutsideCursor()); }
        if (!graph.getSeries().isEmpty()){ graph.getSeries().set(0, series1); }
        else { graph.addSeries(series1); }
        if (graph.getSeries().size() > 1){ graph.getSeries().set(1, series2); }
        else { graph.addSeries(series2); }
    }

    public void updateScanChart_Lollipop(GraphView graph, Byte rssiMin, Beacon beacon){
        graph.removeAllSeries();
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>( new DataPoint[]{
                new DataPoint(0, 0),
                new DataPoint(rssiMin, 0)
        });
        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<DataPoint>( new DataPoint[]{
                new DataPoint(beacon.getRSSIAverage(), 1)
        });
        series1.setThickness(10);
        series2.setSpacing(100);
        if (beacon.getRSSIAverage() < rssiMin){ series2.setValueDependentColor(getOutsideCursor()); }
        graph.addSeries(series1);
        graph.addSeries(series2);
    }

    private void setOutsideCursorColor(){
         this.outsideCursor = new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) { return Color.rgb(255, 0, 0); }};
    }

    private void setInsideCursorColor(){
        this.insideCursor = new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) { return Color.rgb(0, 0, 255); }};
    }

    private ValueDependentColor<DataPoint> getOutsideCursor(){ return this.outsideCursor; }

    private ValueDependentColor<DataPoint> getInsideCursor() { return this.insideCursor; }
}

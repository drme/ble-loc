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

    public ChartHelper(){}

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
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>( new DataPoint[]{
                new DataPoint(0, 0),
                new DataPoint(rssiMin, 0)
        });
        series.setThickness(10);
        graph.addSeries(series);
    }

    public void updateScanChart(GraphView graph, Byte rssiMin, Beacon beacon){
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>( new DataPoint[]{
                new DataPoint(beacon.getRSSIAverage(), 1)
        });
        series.setSpacing(100);
        if (beacon.getRSSIAverage() < rssiMin){
            series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb(255, 0, 0);
                }
            });
        }
        graph.getSeries().set(0, series);
    }
}

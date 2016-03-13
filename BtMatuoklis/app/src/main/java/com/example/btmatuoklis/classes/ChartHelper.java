package com.example.btmatuoklis.classes;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

import lt.monarch.chart.android.AndroidChart;
import lt.monarch.chart.android.stubs.java.awt.BasicStroke;
import lt.monarch.chart.chart2D.Chart2D;
import lt.monarch.chart.chart2D.Grid;
import lt.monarch.chart.chart2D.axis.Axis2DX;
import lt.monarch.chart.chart2D.axis.Axis2DY;
import lt.monarch.chart.chart2D.engine.PlaneMapper2D;
import lt.monarch.chart.chart2D.series.BarSeries;
import lt.monarch.chart.chart2D.series.BarStrategies;
import lt.monarch.chart.engine.ChartObject;
import lt.monarch.chart.mapper.LabelAxisMapper;
import lt.monarch.chart.mapper.MathAxisMapper;
import lt.monarch.chart.models.ChartDataModel;
import lt.monarch.chart.view.LabeledChart;

public class ChartHelper {

    public ChartHelper(){}

    public void setFullChart(Activity activity, Beacon beacon, int id)
    {
        View view = fullChart(activity, beacon);
        replaceView(activity, id, view);
        view.setId(id);
    }

    public void setFullSpacedChart(Activity activity, Beacon beacon, int id)
    {
        View view = fullSpacedChart(activity, beacon);
        replaceView(activity, id, view);
        view.setId(id);
    }

    public void setRangeChart(Activity activity, Beacon beacon, int id)
    {
        View view = rangeChart(activity, beacon);
        replaceView(activity, id, view);
        view.setId(id);
    }

    protected lt.monarch.chart.android.AndroidChart fullChart(Context context, Beacon beacon) {
        LabelAxisMapper xMapper = new LabelAxisMapper();
        ChartDataModel RSSIdata = new ChartDataModel();
        ArrayList<Byte> uniqueRSSIs = beacon.getUniqueRSSIs();
        ArrayList<Byte> frequnecies = beacon.countRSSIFrequencies();
        for (int i = 0; i < uniqueRSSIs.size(); i++){
            xMapper.registerKey(uniqueRSSIs.get(i));
            RSSIdata.add(new Object[]{uniqueRSSIs.get(i), frequnecies.get(i)});
        }
        MathAxisMapper yMapper = new MathAxisMapper(0d, Collections.max(frequnecies)+1);
        Axis2DX xAxis = new Axis2DX(xMapper);
        xAxis.setTitle("RSSI");
        Axis2DY yAxis = new Axis2DY(yMapper);
        yAxis.setTitle("Pasikartojimų kiekis");
        BarSeries connectedSeries1 = new BarSeries(RSSIdata, xMapper, yMapper);
        connectedSeries1.setName("RSSI");
        connectedSeries1.setStrategy(BarStrategies.BAR_STRATEGY);
        connectedSeries1.getPaintStyle().setForeground(new lt.monarch.chart.android.stubs.java.awt.Color(219, 67, 47));
        connectedSeries1.getPaintStyle().setStroke(new BasicStroke(2));
        Grid grid = new Grid(new PlaneMapper2D(), null, yMapper);
        Chart2D chart = new Chart2D();
        chart.setObjects(new ChartObject[] { grid, connectedSeries1});
        xAxis.setAxisPosition(yAxis, 0);
        chart.setXAxis(xAxis);
        chart.setYAxis(yAxis);
        LabeledChart m_chart = new LabeledChart(chart);
        return new AndroidChart(m_chart, context);
    }

    protected lt.monarch.chart.android.AndroidChart fullSpacedChart(Context context, Beacon beacon) {
        LabelAxisMapper xMapper = new LabelAxisMapper();
        ChartDataModel RSSIdata = new ChartDataModel();
        ArrayList<Byte> uniqueRSSIs = beacon.getSpacedRSSIs();
        ArrayList<Byte> frequnecies = beacon.countSpacedRSSIFrequencies();
        for (int i = 0; i < uniqueRSSIs.size(); i++){
            xMapper.registerKey(uniqueRSSIs.get(i));
            RSSIdata.add(new Object[]{uniqueRSSIs.get(i), frequnecies.get(i)});
        }
        MathAxisMapper yMapper = new MathAxisMapper(0d, Collections.max(frequnecies)+1);
        Axis2DX xAxis = new Axis2DX(xMapper);
        xAxis.setTitle("RSSI");
        Axis2DY yAxis = new Axis2DY(yMapper);
        yAxis.setTitle("Pasikartojimų kiekis");
        BarSeries connectedSeries1 = new BarSeries(RSSIdata, xMapper, yMapper);
        connectedSeries1.setName("RSSI");
        connectedSeries1.setStrategy(BarStrategies.BAR_STRATEGY);
        connectedSeries1.getPaintStyle().setForeground(new lt.monarch.chart.android.stubs.java.awt.Color(219, 67, 47));
        connectedSeries1.getPaintStyle().setStroke(new BasicStroke(2));
        Grid grid = new Grid(new PlaneMapper2D(), null, yMapper);
        Chart2D chart = new Chart2D();
        chart.setObjects(new ChartObject[] { grid, connectedSeries1});
        xAxis.setAxisPosition(yAxis, 0);
        chart.setXAxis(xAxis);
        chart.setYAxis(yAxis);
        LabeledChart m_chart = new LabeledChart(chart);
        return new AndroidChart(m_chart, context);
    }

    protected lt.monarch.chart.android.AndroidChart rangeChart(Context context, Beacon beacon) {
        int step = 10;
        ArrayList<Byte> rssiArray = beacon.getFullRSSI();
        LabelAxisMapper xMapper = new LabelAxisMapper();
        ChartDataModel RSSIdata = new ChartDataModel();
        ArrayList<Byte> countArray = new ArrayList<Byte>();
        byte minRSSI = beacon.getRSSIMin();
        byte maxRSSI = beacon.getRSSIMax();
        String Key;
        for (int i = maxRSSI; i > minRSSI; i=i-step){
            Byte count = 0;
            for (int j = 0; j < rssiArray.size(); j++ ){
                if ( (i-step < rssiArray.get(j) ) && (rssiArray.get(j) <= i) ){
                    count++;
                }
            }
            countArray.add(count);
            int endStep = i - step;
            if (step > 1) {
                Key = "[" + i + ", " + endStep + "]";
            }
            else{
                Key = String.valueOf(i);
            }
            xMapper.registerKey(Key);
            RSSIdata.add(new Object[]{Key, count});
        }

        //Temporary fix
        int maxFrequency;
        if (countArray.size() != 0){ maxFrequency = Collections.max(countArray); }
        else { maxFrequency = 0; }

        MathAxisMapper yMapper = new MathAxisMapper(0d, maxFrequency+1);
        Axis2DX xAxis = new Axis2DX(xMapper);
        xAxis.setTitle("RSSI");
        Axis2DY yAxis = new Axis2DY(yMapper);
        yAxis.setTitle("Pasikartojimų kiekis");
        BarSeries connectedSeries1 = new BarSeries(RSSIdata, xMapper, yMapper);
        connectedSeries1.setName("RSSI");
        connectedSeries1.setStrategy(BarStrategies.BAR_STRATEGY);
        connectedSeries1.getPaintStyle().setForeground(new lt.monarch.chart.android.stubs.java.awt.Color(219, 67, 47));
        connectedSeries1.getPaintStyle().setStroke(new BasicStroke(2));
        Grid grid = new Grid(new PlaneMapper2D(), null, yMapper);
        Chart2D chart = new Chart2D();
        chart.setObjects(new ChartObject[] { grid, connectedSeries1});
        xAxis.setAxisPosition(yAxis, 0);
        chart.setXAxis(xAxis);
        chart.setYAxis(yAxis);
        return new AndroidChart(new LabeledChart(chart), context);
    }

    public static void replaceView(Activity activity, int id, View replacement)
    {
        View originalView = activity.findViewById(id);
        ViewGroup parent = (ViewGroup) originalView.getParent();
        int viewIndex = parent.indexOfChild(originalView);
        ViewGroup.LayoutParams layoutParams = originalView.getLayoutParams();
        parent.removeView(originalView);
        parent.addView(replacement, viewIndex);
        replacement.setLayoutParams(layoutParams);
    }
}

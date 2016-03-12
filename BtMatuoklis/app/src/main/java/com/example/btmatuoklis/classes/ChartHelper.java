package com.example.btmatuoklis.classes;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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

    public void setDashboardContent(Activity activity, Beacon beacon, int id)
    {
        View view = Chart(activity, beacon);
        replaceView(activity, id, view);
        view.setId(id);
    }

    protected lt.monarch.chart.android.AndroidChart Chart(Context context, Beacon beacon) {

        int step = 10;
        ArrayList<Byte> rssiArray;

        LabelAxisMapper xMapper = new LabelAxisMapper();
        ChartDataModel RSSIdata = new ChartDataModel();

        ArrayList<Byte> countArray = new ArrayList<Byte>();
        rssiArray = beacon.getFullRSSI();
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
                Key = " " + i + "  " + endStep;
            }
            else{
                Key = ""+i;
            }

            xMapper.registerKey(Key);
            RSSIdata.add(new Object[]{Key, count});
            Log.d("addRoom", count.toString());
        }

        int maxFrequency = Collections.max(countArray);
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

        LabeledChart m_chart = new LabeledChart(chart);
        return new AndroidChart(m_chart, context);
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

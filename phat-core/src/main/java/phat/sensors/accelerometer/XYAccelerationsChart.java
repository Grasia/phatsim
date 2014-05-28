/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.sensors.accelerometer;

import phat.sensors.accelerometer.AccelerationData;
import phat.sensors.Sensor;
import phat.sensors.SensorData;
import phat.sensors.SensorListener;
import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * An accelerometer listener that plots the x,y,z accelerations of the
 * sensor that is listening.
 * 
 * @author Pablo
 */
public class XYAccelerationsChart extends ApplicationFrame implements SensorListener {

    XYSeries xAcceleration;
    XYSeries yAcceleration;
    XYSeries zAcceleration;
    
    XYSeriesCollection dataset;    
    JFreeChart chart;
    XYPlot plot;
    
    ChartPanel chartPanel;
    
    float acumulativeTime = 0f;
    
    /**
     * Creates a new graphic to plot accelerations.
     *
     * @param title  the frame title.
     */
    public XYAccelerationsChart(final String windowstitle, final String chartTitle,
            final String domainAxisLabel, final String rangeAxisLabel) {

        super(windowstitle);

        //Object[][][] data = new Object[3][50][2];
        xAcceleration = new XYSeries("x acc.");        
        yAcceleration = new XYSeries("y acc.");
        zAcceleration = new XYSeries("z acc.");

        dataset = new XYSeriesCollection();
        dataset.addSeries(xAcceleration);
        dataset.addSeries(yAcceleration);
        dataset.addSeries(zAcceleration);

        chart = ChartFactory.createXYLineChart(
                chartTitle, // chart title
                domainAxisLabel, // domain axis label
                rangeAxisLabel, // range axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true,
                false);

        plot = chart.getXYPlot();
        /*final NumberAxis domainAxis = new NumberAxis("x");
        final NumberAxis rangeAxis = new LogarithmicAxis("Log(y)");
        plot.setDomainAxis(domainAxis);
        plot.setRangeAxis(rangeAxis);*/
        chart.setBackgroundPaint(Color.white);
        plot.setOutlinePaint(Color.black);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }
    
    public void showWindow() {
        pack();        
        RefineryUtilities.centerFrameOnScreen(this);
        setVisible(true);
    }

    @Override
    public void update(Sensor source, SensorData sd) {
        if(sd instanceof AccelerationData) {
            AccelerationData ad = (AccelerationData) sd;
            acumulativeTime += ad.getInterval();        
            xAcceleration.add(acumulativeTime, ad.getX());
            yAcceleration.add(acumulativeTime, ad.getY());
            zAcceleration.add(acumulativeTime, ad.getZ());        
            chartPanel.updateUI();
        }
    }

    public void stop() {
    }

    @Override
    public void cleanUp() {
        setVisible(false);
        xAcceleration = null;
        yAcceleration = null;
        zAcceleration = null;
    
        dataset = null;    
        chart = null;
        plot = null;
    
        chartPanel = null;
    }
}
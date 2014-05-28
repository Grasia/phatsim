/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.audio.listeners;

import phat.sensors.Sensor;
import phat.sensors.SensorData;
import phat.sensors.SensorListener;
import phat.sensors.microphone.MicrophoneData;
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
import org.tritonus.share.sampled.FloatSampleTools;

/**
 *
 * @author Pablo
 */
public class XYRMSAudioChart extends ApplicationFrame implements SensorListener {

    XYSeries values;
    XYSeriesCollection dataset;
    JFreeChart chart;
    XYPlot plot;
    ChartPanel chartPanel;
    float acumulativeTime = 0f;

    /**
     * Creates a new demo.
     *
     * @param title the frame title.
     */
    public XYRMSAudioChart(final String title) {

        super(title);

        //Object[][][] data = new Object[3][50][2];
        values = new XYSeries("data");

        dataset = new XYSeriesCollection();
        dataset.addSeries(values);

        chart = ChartFactory.createXYLineChart(
                title, // chart title
                "Time (m)", // domain axis label
                "RMS (dB)", // range axis label
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
    public synchronized void update(Sensor source, SensorData sd) {
        if (sd instanceof MicrophoneData) {
            MicrophoneData md = (MicrophoneData) sd;
            byte[] data = md.getData();
            acumulativeTime += (1.0f / md.getAudioFormat().getSampleRate()) * (data.length / 2);
            //values.add(acumulativeTime, volumeRMS(data, 0, data.length));
            values.add(acumulativeTime, getMax(md));
            /*int sample = 0;
             for (int i = 0; i < data.length; i += 2) {
                acumulativeTime += (1.0f / md.getAudioFormat().getSampleRate());
                sample += Math.abs((short) (data[i] | data[i + 1]));
                values.add(acumulativeTime, sample / (data.length / 2));
             }*/
        }
    }

    public float getMax(MicrophoneData md) {
        int numSamples = md.getData().length;
        float[] out = new float[numSamples];
        FloatSampleTools.
                byte2floatInterleaved(
                md.getData(), 0, out, 0, numSamples / md.getAudioFormat().getFrameSize(), md.getAudioFormat());

        float max = Float.NEGATIVE_INFINITY;
        float value = 0;
        for (float f : out) {
            value = Math.abs(f);
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public double mean(MicrophoneData md) {
        byte [] raw = md.getData();
        int size = md.getData().length;
        
        double sum = 0d;
        if (raw.length == 0) {
            return sum;
        } else {
            for (int ii = 0; ii < size; ii += 2) {
                sum += (raw[ii] | raw[ii + 1])*(raw[ii] | raw[ii + 1]);
            }
        }
        return sum / (size*2);
    }
    
    public double volumeRMS(MicrophoneData md) {
        byte [] raw = md.getData();
        int size = md.getData().length;
        
        double sum = 0d;
        if (raw.length == 0) {
            return sum;
        } else {
            for (int ii = 0; ii < size; ii += 2) {
                sum += (raw[ii] | raw[ii + 1]);
            }
        }
        double average = sum / (size*2);

        double sumMeanSquare = 0d;
        for (int ii = 0; ii < size; ii += 2) {
            sumMeanSquare += Math.pow((raw[ii] | raw[ii + 1]) - average, 2d);
        }
        double averageMeanSquare = sumMeanSquare / (size*2);
        double rootMeanSquare = Math.pow(averageMeanSquare, 0.5d);

        return rootMeanSquare;
    }

    @Override
    public void cleanUp() {
        setVisible(false);
        this.values = null;
        this.dataset = null;
        this.chart = null;
        this.chartPanel = null;
        this.plot = null;
    }
}
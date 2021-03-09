package ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class PlotUtils {
    public static void drawSystem(DoubleUnaryOperator[] sysPlot, String f1, String f2,
                                  double rootX, double rootY) {
        List<XYSeries> series = new ArrayList<>(4);
        series.add(new XYSeries(f1)); //series 0
        series.add(new XYSeries("f1Comp")); //series 1
        series.add(new XYSeries(f2)); //series 2
        series.add(new XYSeries("f2Comp")); //series 3

        for (double j = -100; j < 100; j += 0.1) {
            for (int i = 0; i < sysPlot.length; i++) {
                DoubleUnaryOperator f = sysPlot[i];
                if (f != null) {
                    series.get(i).add(j, f.applyAsDouble(j));
                }
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();

        for (XYSeries s: series) {
            dataset.addSeries(s);
        }

        final XYSeries root = new XYSeries("Root");
        root.add(rootX, rootY); //series 4
        dataset.addSeries(root);


        final JFreeChart chart = ChartFactory.createXYLineChart(
                "System Graph",
                "X",                        // x axis label
                "Y",                        // y axis label
                null,                        // data
                PlotOrientation.VERTICAL,
                true,                        // include legend
                false,                       // tooltips
                false                        // urls
        );

        chart.setBackgroundPaint(Color.WHITE);

        final XYPlot plot = chart.getXYPlot();

        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.getDomainAxis().setRange(rootX-10, rootX+10);
        plot.getRangeAxis().setRange(rootY-10, rootY+10);
        plot.setDomainZeroBaselineVisible(true);
        plot.setRangeZeroBaselineVisible(true);

        XYSplineRenderer r = new XYSplineRenderer();
        r.setSeriesShapesVisible(0, false);
        r.setSeriesShapesVisible(1, false);
        r.setSeriesShapesVisible(2, false);
        r.setSeriesShapesVisible(3, false);

        r.setSeriesLinesVisible(0, true);
        r.setSeriesLinesVisible(1, true);
        r.setSeriesLinesVisible(2, true);
        r.setSeriesLinesVisible(3, true);

        r.setSeriesVisibleInLegend(1, false);
        r.setSeriesVisibleInLegend(3, false);
        r.setSeriesVisibleInLegend(4, false);

        r.setSeriesPaint(0, Color.RED);
        r.setSeriesPaint(1, Color.RED);

        r.setSeriesPaint(2, Color.BLUE);
        r.setSeriesPaint(3, Color.BLUE);

        r.setSeriesPaint(4, Color.GREEN);


        plot.setDataset(0, dataset);
        plot.setRenderer(r);

        JFrame jf = new JFrame();
        jf.setContentPane(new ChartPanel(chart));
        jf.pack();
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }

    public static void drawPlot(DoubleUnaryOperator f, String fStr, double left, double right, double rootXBisec, double rootXChords) {
        final XYSeries series1 = new XYSeries(fStr);
        for (double i = left - 50; i < right + 50; i += 0.1) {
            series1.add(i, f.applyAsDouble(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(series1);

        if (Double.isFinite(rootXBisec)) {
            final XYSeries pBisec = new XYSeries("Bisection");
            pBisec.add(rootXBisec, 0);
            dataset.addSeries(pBisec);
        }

        if (Double.isFinite(rootXChords)) {
            final XYSeries pChord = new XYSeries("Chords");
            pChord.add(rootXChords, 0);
            dataset.addSeries(pChord);
        }

        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Equation Graph",
                "X",                        // x axis label
                "Y",                        // y axis label
                null,                        // data
                PlotOrientation.VERTICAL,
                true,                        // include legend
                false,                       // tooltips
                false                        // urls
        );

        chart.setBackgroundPaint(Color.WHITE);

        final XYPlot plot = chart.getXYPlot();

        plot.setBackgroundPaint(Color.LIGHT_GRAY);

        plot.getDomainAxis().setRange(left-5, right+5);
        plot.getDomainAxis().setAutoRange(false);
        plot.setDomainZeroBaselineVisible(true);
        plot.setRangeZeroBaselineVisible(true);

        XYSplineRenderer r0 = new XYSplineRenderer();
        r0.setSeriesShapesVisible(0, false);

        r0.setSeriesLinesVisible(1, false);
        r0.setSeriesPaint(1, Color.BLUE);
        r0.setSeriesLinesVisible(2, false);
        r0.setSeriesPaint(2, Color.GREEN);

        plot.setDataset(0, dataset);

        plot.setRenderer(r0);

        JFrame jf = new JFrame();
        jf.setContentPane(new ChartPanel(chart));
        jf.pack();
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
}

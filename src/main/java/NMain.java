import nlequations.NonLinearEquations;
import nlequations.Result;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ui.ConsoleUserInterface;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public class NMain {
    public static void main(String[] args) {
        ConsoleUserInterface ui = new ConsoleUserInterface(new BufferedReader(new InputStreamReader(System.in)),
                new OutputStreamWriter(System.out));

        Function<Double, Double> f = null;
        ArrayList<Function<double[], Double>> sysFuncs = new ArrayList<>();
        ArrayList<ArrayList<Function<double[], Double>>> df = new ArrayList<>();

        ArrayList<DoubleUnaryOperator> sysPlot = new ArrayList<>();

        int option = ui.readChoice(new String[]{
                "SYSTEM",
                "EQUATION",
        }, "Choose an option");

        if (option == 1) {
            int eq = ui.readChoice(new String[]{
                    "ax^3 + bx^2 + cx + d",
                    "a^x + bx^2 + c",
                    "a*sin(x) + b*cos(x)"
            }, "Choose an option");
            ui.writeln("Enter coefficients");
            switch (eq) {
                case 0:
                    double[] coefs0 = ui.readCoefRow(4);
                    f = x -> coefs0[0] * Math.pow(x, 3) +
                        coefs0[1] * Math.pow(x, 2) +
                        coefs0[2] * x +
                        coefs0[3];
                    break;
                case 1:
                    double[] coefs1 = ui.readCoefRow(3);
                    f = x -> Math.pow(coefs1[0], x) +
                            coefs1[1] * Math.pow(x, 2) +
                            coefs1[2];
                    break;
                case 2:
                    double[] coefs2 = ui.readCoefRow(2);
                    f = x -> coefs2[0]*Math.sin(x)+coefs2[1]*Math.cos(x);
                    break;
            }
        }
        else {
            int sys = ui.readChoice(new String[]{
                    "{ y^2 = x^3 + 0.5\n" +
                    "{ y^2 + x^2 = 1",
                    ""
            }, "Choose an option");

            if (sys==0) {
                sysPlot.add(x-> Math.sqrt(Math.pow(x, 3)+0.5));
                sysPlot.add(x-> -Math.sqrt(Math.pow(x, 3)+0.5));
                sysPlot.add(x-> -Math.sqrt(1-Math.pow(x,2)));
                sysPlot.add(x-> Math.sqrt(1-Math.pow(x,2)));

                sysFuncs.add(x -> Math.pow(x[1],2)-Math.pow(x[0],3)-0.5);
                sysFuncs.add(x -> Math.pow(x[0],2)+Math.pow(x[1],2)-1);

                ArrayList<Function<double[], Double>> eq1d = new ArrayList<>();
                ArrayList<Function<double[], Double>> eq2d = new ArrayList<>();
                eq1d.add(x -> -3*Math.pow(x[0],2));
                eq1d.add(y -> 2*y[1]);

                eq2d.add(x -> 2*x[0]);
                eq2d.add(y -> 2*y[1]);

                df.add(eq1d);
                df.add(eq2d);
            }
            else if (sys==1){
                sysFuncs.add(x -> Math.pow(x[1],2)-Math.pow(x[0],3)-0.5);
                sysFuncs.add(x -> Math.pow(x[0],2)+Math.pow(x[1],2)-1);

                ArrayList<Function<double[], Double>> eq1d = new ArrayList<>();
                ArrayList<Function<double[], Double>> eq2d = new ArrayList<>();
                eq1d.add(x -> -3*Math.pow(x[0],2));
                eq1d.add(y -> 2*y[1]);

                eq2d.add(x -> 2*x[0]);
                eq2d.add(y -> 2*y[1]);

                df.add(eq1d);
                df.add(eq2d);
            }
        }

        if (f != null) {

            double left = ui.readDouble("Enter left border");
            double right = ui.readDouble("Enter right border");
            double eps = ui.readDouble("Enter epsilon");

            final XYSeries series1 = new XYSeries("Series 1");
            for (double i = left-50; i < right+50; i += 0.1) {
                series1.add(i, f.apply(i));
            }

            XYSeriesCollection dataset = new XYSeriesCollection();

            dataset.addSeries(series1);

            Result rBisec = NonLinearEquations.rootByBisection(left, right, eps, f);
            Result rChord = NonLinearEquations.rootByChords(left, right, eps, f);

            if (rBisec.isValid()) {

                ui.writeln("Bisection: "+ rBisec.getRes());
                ui.writeln("Chords: "+ rChord.getRes());

                final XYSeries pBisec = new XYSeries("Bisection");
                pBisec.add(rBisec.getRes(), 0);

                final XYSeries pChord = new XYSeries("Chords");
                pChord.add(rChord.getRes(), 0);

                dataset.addSeries(pBisec);
                dataset.addSeries(pChord);
            }

            else {
                ui.writeln("No roots on your segment");
            }


            final JFreeChart chart = ChartFactory.createXYLineChart(
                    "Equation Graph",
                    null,                        // x axis label
                    null,                        // y axis label
                    null,                        // data
                    PlotOrientation.VERTICAL,
                    true,                        // include legend
                    false,                       // tooltips
                    false                        // urls
            );

            chart.setBackgroundPaint(Color.WHITE);

            final XYPlot plot = chart.getXYPlot();

            plot.setBackgroundPaint(Color.LIGHT_GRAY);

            plot.getDomainAxis().setRange(left, right);
            plot.getRangeAxis().setRange(left, right);
            plot.setDomainZeroBaselineVisible(true);
            plot.setRangeZeroBaselineVisible(true);

            XYSplineRenderer r0 = new XYSplineRenderer();
            r0.setSeriesShapesVisible(0, false);

            r0.setSeriesLinesVisible(1, false);
            r0.setSeriesPaint(1, Color.BLUE);
            r0.setSeriesLinesVisible(2, false);
            r0.setSeriesPaint(2, Color.GREEN);

            plot.setDataset(0, dataset);

            // Подключение Spline Renderer к наборам данных
            plot.setRenderer(r0);

            JFrame jf = new JFrame();
            jf.setContentPane(new ChartPanel(chart));
            jf.pack();
            jf.setVisible(true);
        }
        else {
            ui.writeln(Double.toString(NonLinearEquations.systemRootsByNewton(0.5, 0.001, sysFuncs, df)[0]));
            ui.writeln(Double.toString(NonLinearEquations.systemRootsByNewton(0.5, 0.001, sysFuncs, df)[1]));


            final XYSeries series0 = new XYSeries("Series 1");
            for (double j = -100; j < 100; j += 0.1) {
                series0.add(j, sysPlot.get(0).applyAsDouble(j));
            }

            final XYSeries series1 = new XYSeries("Series 2");
            for (double j = -100; j < 100; j += 0.1) {
                series0.add(j, sysPlot.get(1).applyAsDouble(j));
            }
            final XYSeries series2 = new XYSeries("Series 3");
            for (double j = -100; j < 100; j += 0.1) {
                series0.add(j, sysPlot.get(2).applyAsDouble(j));
            }
            final XYSeries series3 = new XYSeries("Series 4");
            for (double j = -100; j < 100; j += 0.1) {
                series0.add(j, sysPlot.get(3).applyAsDouble(j));
            }

            XYSeriesCollection dataset = new XYSeriesCollection();

            dataset.addSeries(series0);
            dataset.addSeries(series1);
            dataset.addSeries(series2);
            dataset.addSeries(series3);

            final JFreeChart chart = ChartFactory.createXYLineChart(
                    "System Graph",
                    null,                        // x axis label
                    null,                        // y axis label
                    null,                        // data
                    PlotOrientation.VERTICAL,
                    true,                        // include legend
                    false,                       // tooltips
                    false                        // urls
            );

            chart.setBackgroundPaint(Color.WHITE);

            final XYPlot plot = chart.getXYPlot();

            plot.setBackgroundPaint(Color.LIGHT_GRAY);
            plot.getDomainAxis().setRange(-50, 50);
            plot.getRangeAxis().setRange(-50, 50);
            plot.setDomainZeroBaselineVisible(true);
            plot.setRangeZeroBaselineVisible(true);

            XYSplineRenderer r0 = new XYSplineRenderer();
            r0.setSeriesLinesVisible();

            plot.setDataset(0, dataset);

            // Подключение Spline Renderer к наборам данных
            plot.setRenderer(r0);

            JFrame jf = new JFrame();
            jf.setContentPane(new ChartPanel(chart));
            jf.pack();
            jf.setVisible(true);
        }
    }
}

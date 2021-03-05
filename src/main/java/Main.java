import equations.LinearEquationSystem;
import equations.NumberOfRoots;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ui.ConsoleUserInterface;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {

        ConsoleUserInterface ui = new ConsoleUserInterface(new BufferedReader(new InputStreamReader(System.in)),
                new OutputStreamWriter(System.out));

        LinearEquationSystem m = ui.readEqSystem();
        LinearEquationSystem tm = m.toTriangularMatrixGaussian();
        double determinant = tm.calcDetForTriangular();
        ui.writeln("Determinant:");
        ui.getWriter().printf(Locale.ENGLISH, "%.6f%n", determinant);
        ui.writeln("");
        ui.writeln("Triangular system:");
        printSystem(ui, tm);
        ui.writeln("");
        NumberOfRoots numberOfRoots = tm.getNumberOfRootsForTriangular();
        if (numberOfRoots == NumberOfRoots.SINGLE) {
            double[] roots = tm.calcRoots();
            double[] residuals = m.calcResiduals(roots);
            ui.writeln("Roots:");
            printVector(ui, roots, "x");
            ui.writeln("");
            ui.writeln("Residuals:");
            printVector(ui, residuals, "r");
            ui.writeln("");
        }
        else if (tm.getNumberOfRootsForTriangular() == NumberOfRoots.INFINITE) {
            ui.writeln("THE SYSTEM HAS INFINITE NUMBER OF ROOTS");
        }
        else ui.writeln("THE SYSTEM HAS NO ROOTS");
    }

    private static void printSystem(ConsoleUserInterface ui, LinearEquationSystem system){
        double[][] coef = system.getCoefficients();
        double[] terms = system.getConstantTerms();
        for (int i = 0; i < system.getDim(); i++) {
            for (int j = 0; j < system.getDim(); j++) {
                ui.getWriter().printf(Locale.ENGLISH, "%20.6f", coef[i][j]);
            }
            ui.getWriter().printf("%5s", "|");
            ui.getWriter().printf(Locale.ENGLISH, "%20.6f", terms[i]);
            ui.writeln("");
        }
    }

    private static void printVector(ConsoleUserInterface ui, double[] vector, String id) {
        for (int i = 0; i < vector.length; i++) {
            ui.writeln(id + i +": " + vector[i]);
        }
    }

}

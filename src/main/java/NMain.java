import nlequations.NonLinearEquations;
import nlequations.Result;
import ui.ConsoleUserInterface;
import ui.PlotUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public class NMain {
    public static void main(String[] args) {
        ConsoleUserInterface ui = new ConsoleUserInterface(new BufferedReader(new InputStreamReader(System.in)),
                new OutputStreamWriter(System.out));

        DoubleUnaryOperator f = null;
        String fStr = null;
        List<Function<double[], Double>> sysFuncs = new ArrayList<>();
        List<List<Function<double[], Double>>> df = new ArrayList<>();

        DoubleUnaryOperator[] sysPlot = new DoubleUnaryOperator[4];

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
                    fStr = coefs0[0] + "x^3 + " + coefs0[1] + "x^2 + " + coefs0[2] + "x + " + coefs0[3];
                    break;
                case 1:
                    double[] coefs1 = ui.readCoefRow(3);
                    f = x -> Math.pow(coefs1[0], x) +
                            coefs1[1] * Math.pow(x, 2) +
                            coefs1[2];
                    fStr = coefs1[0] + "^x + " + coefs1[1] + "x^2 + " + coefs1[2];
                    break;
                case 2:
                    double[] coefs2 = ui.readCoefRow(2);
                    f = x -> coefs2[0] * Math.sin(x) + coefs2[1] * Math.cos(x);
                    fStr = coefs2[0] + "*sin(x) + " + coefs2[1] + "*cos(x)";
                    break;
            }
        } else {
            int sys = ui.readChoice(new String[]{
                    "{ 1. y = x^2 + 3x + 1\n" +
                            "{ 2. 2y = x + 5x^2\n",
                    "{ 1. sin(x) = yx\n" +
                            "{ 2. x^2 + y^3 = 4\n",
                    "{ 1. x^2 + y^2 + z^2 = 1\n" +
                            "{ 2. 2x^2 + y^2 = 4z\n" +
                            "{ 3. 3x^2 -4y = -z^2\n"
            }, "Choose an option");

            double eps = ui.readDouble("Enter epsilon:");

            if (sys == 0) {
                sysPlot[0] = x -> Math.pow(x, 2) + 3 * x + 1;
                sysPlot[2] = x -> x / 2 + 5 * Math.pow(x, 2) / 2;

                sysFuncs.add(x -> Math.pow(x[0], 2) + 3 * x[0] + 1 - x[1]);
                sysFuncs.add(x -> x[0] + 5 * Math.pow(x[0], 2) - 2 * x[1]);

                ArrayList<Function<double[], Double>> eq1d = new ArrayList<>();
                ArrayList<Function<double[], Double>> eq2d = new ArrayList<>();
                eq1d.add(x -> 2 * x[0] + 3);
                eq1d.add(y -> -1d);

                eq2d.add(x -> 1 + 10 * x[0]);
                eq2d.add(y -> -2d);

                df.add(eq1d);
                df.add(eq2d);

                double[] root = NonLinearEquations.systemRootsByNewton(0.5, eps, sysFuncs, df);
                double rootX = root[0];
                double rootY = root[1];

                ui.writeln("x = " + rootX);
                ui.writeln("y = " + rootY);
                PlotUtils.drawSystem(sysPlot, "y = x^2 + 3x + 1", "2y = x + 5x^2", rootX, rootY);

            } else if (sys == 1) {
                sysPlot[0] = x -> 10 * Math.asin(x + 0.2) - 10 * x;
                sysPlot[2] = x -> Math.cbrt(4 - Math.pow(x, 2));

                sysFuncs.add(x -> Math.sin(x[0] + 0.1 * x[1]) - x[0] - 0.2);
                sysFuncs.add(x -> Math.pow(x[0], 2) + Math.pow(x[1], 3) - 4);

                ArrayList<Function<double[], Double>> eq1d = new ArrayList<>();
                ArrayList<Function<double[], Double>> eq2d = new ArrayList<>();
                eq1d.add(x -> -1 + Math.cos(x[0] + x[1] / 10));
                eq1d.add(y -> Math.cos(y[0] + y[1] / 10) / 10);

                eq2d.add(x -> 2 * x[0]);
                eq2d.add(y -> 3 * Math.pow(y[1], 2));

                df.add(eq1d);
                df.add(eq2d);

                double[] root = NonLinearEquations.systemRootsByNewton(0.5, eps, sysFuncs, df);
                double rootX = root[0];
                double rootY = root[1];

                ui.writeln("x = " + rootX);
                ui.writeln("y = " + rootY);
                PlotUtils.drawSystem(sysPlot, "sin(x+0.1y) - x = 0.2", "x^2 + y^3 = 4", rootX, rootY);

            } else if (sys == 2) {
                sysFuncs.add(x -> Math.pow(x[0], 2) + Math.pow(x[1], 2) + Math.pow(x[2], 2) - 1);
                sysFuncs.add(x -> 2 * Math.pow(x[0], 2) + Math.pow(x[1], 2) - 4 * x[2]);
                sysFuncs.add(x -> 3 * Math.pow(x[0], 2) - 4 * x[1] + Math.pow(x[2], 2));

                List<Function<double[], Double>> eq1d = new ArrayList<>();
                List<Function<double[], Double>> eq2d = new ArrayList<>();
                List<Function<double[], Double>> eq3d = new ArrayList<>();
                eq1d.add(x -> 2 * x[0]);
                eq1d.add(y -> 2 * y[1]);
                eq1d.add(z -> 2 * z[2]);

                eq2d.add(x -> 4 * x[0]);
                eq2d.add(y -> 2 * y[1]);
                eq2d.add(z -> -4d);

                eq3d.add(x -> 6 * x[0]);
                eq3d.add(y -> -4d);
                eq3d.add(z -> 2 * z[2]);

                df.add(eq1d);
                df.add(eq2d);
                df.add(eq3d);

                double[] root = NonLinearEquations.systemRootsByNewton(0.5, eps, sysFuncs, df);
                double rootX = root[0];
                double rootY = root[1];
                double rootZ = root[2];

                ui.writeln("x = " + rootX);
                ui.writeln("y = " + rootY);
                ui.writeln("z = " + rootZ);
            }
        }

        if (f != null) {

            double left = ui.readDouble("Enter left border");
            double right = ui.readDouble("Enter right border");
            double eps = ui.readDouble("Enter epsilon");

            Result rBisec = NonLinearEquations.rootByBisection(f, left, right, eps);
            Result rChord = NonLinearEquations.rootByChords(f, left, right, eps);

            if (rBisec.isValid()) {
                ui.writeln("Bisection\n" +
                        "Root: " + rBisec.getRes() + "\n" +
                        "Iterations: " + rBisec.getIters() + "\n");
                ui.writeln("Chords\n" +
                        "Root: " + rChord.getRes() + "\n" +
                        "Iterations: " + rChord.getIters() + "\n");
                ui.writeln("The difference between roots is " + Math.abs(rBisec.getRes() - rChord.getRes()));
            } else {
                ui.writeln("No roots on your segment");
            }

            PlotUtils.drawPlot(f, fStr, left, right, rBisec.getRes(), rChord.getRes());

        }
    }
}

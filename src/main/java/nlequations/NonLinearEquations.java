package nlequations;

import equations.LinearEquationSystem;

import java.util.ArrayList;
import java.util.function.Function;

public class NonLinearEquations {
    public static Result rootByBisection(double left, double right, double eps, Function<Double, Double> f) {
        Result r = new Result();
        r.setEps(eps);

        int iter = 0;
        double rightInit = right;

        if (f.apply(left) * f.apply(right) > 0) {
            right = left;

            while (f.apply(left) * f.apply(right) > 0) {
                right += 0.5;
                if (right >= rightInit) {
                    r.setValid(false);
                    return r;
                }
            }
        }

        r.setLeft(left);
        r.setRight(right);

        while (Math.abs(left - right) >= eps) {

            double x = (left + right) / 2;

            if (f.apply(left) * f.apply(x) > 0) {
                left = x;
            } else right = x;
            iter++;
        }

        r.setIters(iter);
        r.setRes((left + right) / 2);
        r.setValid(true);

        return r;
    }

    public static Result rootByChords(double left, double right, double eps, Function<Double, Double> f) {
        Result r = new Result();
        r.setEps(eps);

        int iter = 0;
        double rightInit = right;

        if (f.apply(left) * f.apply(right) > 0) {
            right = left;
        }

        while (f.apply(left) * f.apply(right) > 0) {
            right += 0.5;
            if (right >= rightInit) {
                r.setValid(false);
                return r;
            }
        }

        double xPrev = left;
        double xCur = right;

        while (Math.abs(xCur - xPrev) > eps) {
            xPrev = xCur;
            double x = left - (right - left) / (f.apply(right) - f.apply(left)) * f.apply(left);
            xCur = x;

            if (f.apply(left) * f.apply(x) > 0) {
                left = x;
            } else right = x;
            iter++;
        }

        r.setIters(iter);
        r.setRes(xCur);

        return r;
    }

    public static double[] systemRootsByNewton(double start, double eps,
                                               ArrayList<Function<double[], Double>> f,
                                               ArrayList<ArrayList<Function<double[], Double>>> fDer) {
        double[] x = new double[f.size()];
        double[] fx = new double[f.size()];
        double[] xPrev;
        double[][] jacobian = new double[f.size()][f.size()];

        for (int i = 0; i < f.size(); i++) {
            x[i] = start;
        }

        while (true) {

            xPrev = x.clone();

            for (int i = 0; i < f.size(); i++) {

                fx[i] = f.get(i).apply(x);

                for (int j = 0; j < f.size(); j++) {
                    jacobian[i][j] = fDer.get(i).get(j).apply(x);
                }
            }

            LinearEquationSystem linearEqSys = new LinearEquationSystem(jacobian, fx);
            LinearEquationSystem trEqSys = linearEqSys.toTriangularMatrixGaussian();
            double[] dx = trEqSys.calcRoots();
            for (int i = 0; i < dx.length; i++) {
                x[i] = x[i] - dx[i];
            }

            boolean allLwrEps = true;
            for (int i = 0; i < f.size(); i++) {
                if (Math.abs(xPrev[i] - x[i]) > eps) {
                    allLwrEps = false;
                    break;
                }
            }
            if (allLwrEps) break;
        }
        return x;
    }
}

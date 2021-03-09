package nlequations;

import equations.LinearEquationSystem;

import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public class NonLinearEquations {
    public static Result rootByBisection(DoubleUnaryOperator f, double left, double right, double eps) {
        Result r = new Result();
        r.setEps(eps);

        int iter = 0;
        double rightInit = right;

        if (f.applyAsDouble(left) * f.applyAsDouble(right) > 0) {
            right = left;

            while (f.applyAsDouble(left) * f.applyAsDouble(right) > 0) {
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

            if (f.applyAsDouble(left) * f.applyAsDouble(x) > 0) {
                left = x;
            } else right = x;
            iter++;
        }

        r.setIters(iter);
        r.setRes((left + right) / 2);
        r.setValid(true);

        return r;
    }

    public static Result rootByChords(DoubleUnaryOperator f, double left, double right, double eps) {
        Result r = new Result();
        r.setEps(eps);

        int iter = 0;
        double rightInit = right;

        if (f.applyAsDouble(left) * f.applyAsDouble(right) > 0) {
            right = left;
        }

        while (f.applyAsDouble(left) * f.applyAsDouble(right) > 0) {
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
            double x = left - (right - left) / (f.applyAsDouble(right) - f.applyAsDouble(left)) * f.applyAsDouble(left);
            xCur = x;

            if (f.applyAsDouble(left) * f.applyAsDouble(x) > 0) {
                left = x;
            } else right = x;
            iter++;
        }

        r.setIters(iter);
        r.setRes(xCur);

        return r;
    }

    public static double[] systemRootsByNewton(double start, double eps,
                                               List<Function<double[], Double>> f,
                                               List<List<Function<double[], Double>>> fDer) {
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

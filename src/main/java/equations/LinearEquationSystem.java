package equations;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LinearEquationSystem {
    private double[][] coefficients;
    private double[] constantTerms;
    private int dim;

    public LinearEquationSystem(double[][] coefficients, double[] constantTerms) {
        this.coefficients = coefficients;
        this.constantTerms = constantTerms;
        this.dim = coefficients.length; //FIXME
    }

    public double[][] getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(double[][] coefficients) {
        this.coefficients = coefficients;
    }

    public double[] getConstantTerms() {
        return constantTerms;
    }

    public void setConstantTerms(double[] constantTerms) {
        this.constantTerms = constantTerms;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public LinearEquationSystem toTriangleMatrix() {

        LinearEquationSystem triangularMatrix = new LinearEquationSystem(MatrixUtils.copyMatrix(coefficients), constantTerms.clone());

        double[][] newCoefficients = triangularMatrix.getCoefficients();
        double[] newConstantTerms = triangularMatrix.getConstantTerms();

        for (int i = 0; i < dim - 1; i++) {
            if (newCoefficients[i][i] == 0) {
                int indexNotZero = findNextNotZero(i);
                if (indexNotZero != -1) {
                    triangularMatrix.swapRows(i, indexNotZero);
                } else continue;
            }

            for (int k = i + 1; k < dim; k++) {
                final double multiplier = newCoefficients[k][i] / newCoefficients[i][i];

                for (int j = i; j < dim; j++) {
                    newCoefficients[k][j] = newCoefficients[k][j] - multiplier * newCoefficients[i][j];
                }
                newConstantTerms[k] = newConstantTerms[k] - multiplier * newConstantTerms[i];
            }
        }

        return triangularMatrix;
    }

    public double calcDetForTriangular() {
        return IntStream.range(0, dim).mapToDouble(i -> coefficients[i][i]).reduce(1, (a, b) -> a * b);
    }

    public double[] calcRoots() {
        double[] roots = new double[dim];
        for (int i = dim-1; i >= 0; i--) {
            double s = 0;
            for (int j = i+1; j < dim; j++) {
                s += coefficients[i][j] * roots[j];
            }
            roots[i] = (constantTerms[i] - s) / coefficients[i][i];
        }
        return roots;
    }

    public double[] calcResiduals(double[] roots) {
        double[] residuals = new double[dim];
        for (int i = 0; i < dim; i++) {
            double cur = 0;
            for (int j = 0; j < dim; j++) {
                cur +=residuals[i] = coefficients[i][j] * roots[j];
            }
            residuals[i] = constantTerms[i] - cur;
        }
        return residuals;
    }

    private void swapRows(int i, int indexNotZero) {
        double[] tmpCoef = coefficients[i];
        coefficients[i] = coefficients[indexNotZero];
        coefficients[indexNotZero] = tmpCoef;

        double tmpTerm = constantTerms[i];
        constantTerms[i] = constantTerms[indexNotZero];
        constantTerms[indexNotZero] = tmpTerm;
    }

    private int findNextNotZero(int i) {
        for (int k = i + 1; k < dim; k++) {
            if (coefficients[k][i] != 0) return k;
        }
        return -1;
    }


}

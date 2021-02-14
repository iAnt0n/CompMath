package equations;

import java.util.Arrays;

public class MatrixUtils {
    public static double[][] copyMatrix(double [][] matrix) {
        double[][] newMatrix = new double[matrix.length][];
        for(int i = 0; i < matrix.length; i++)
            newMatrix[i] = matrix[i].clone();
        return newMatrix;
    }

    public static LinearEquationSystem extMatrixToEqSys(double[][] extendedMatrix) {
        final int numOfVars = extendedMatrix.length;

        double[][] coefMatrix = new double[numOfVars][numOfVars];
        double[] constantTerms = new double[numOfVars];
        for (int i = 0; i < numOfVars; i++) {
            coefMatrix[i] = Arrays.copyOfRange(extendedMatrix[i], 0, numOfVars);
            constantTerms[i] = extendedMatrix[i][extendedMatrix[i].length-1];
        }
        return new LinearEquationSystem(coefMatrix, constantTerms);
    }
}

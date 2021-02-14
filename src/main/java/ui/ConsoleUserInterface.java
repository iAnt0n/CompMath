package ui;

import equations.LinearEquationSystem;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

import static equations.MatrixUtils.extMatrixToEqSys;

public class ConsoleUserInterface {
    private Writer writer;
    private Scanner scanner;

    public ConsoleUserInterface(Reader reader, Writer writer) {
        this.writer = writer;
        this.scanner = new Scanner(reader);
    }

    private void writeln(String s) {
        try {
            writer.write(s + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int readChoice(String[] options, String prompt) {
        writeln(prompt);
        for (int i = 0; i < options.length; i++) {
            writeln(i + ": " + options[i]);
        }

        while (true) {
            try {
                final int option = Integer.parseInt(scanner.nextLine().trim());
                if (option >= 0 && option < options.length) {
                    return option;
                } else writeln("Choose a valid option");
            } catch (NumberFormatException e) {
                writeln("Enter a number");
            }
        }
    }

    public LinearEquationSystem readEqSystem() {
        int option = readChoice(new String[]{
                "READ FROM CONSOLE",
                "READ FROM FILE",
                "RANDOM MATRIX"
        }, "Choose an option");

        LinearEquationSystem eqSys;
        switch (option) {
            case 0:
                eqSys = readEqSystemFromConsole();
                break;
            case 1:
                eqSys = readEqSystemFromFile();
                break;
            case 2:
                eqSys = readEqSystemRandomMatrix();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + option);
        }
        return eqSys;
    }

    private LinearEquationSystem readEqSystemRandomMatrix() {
        double[][] extendedMatrix = readRandomMatrix();
        return extMatrixToEqSys(extendedMatrix);
    }

    private LinearEquationSystem readEqSystemFromFile() {
        double[][] extendedMatrix = readMatrixFromFile();
        return extMatrixToEqSys(extendedMatrix);
    }

    private LinearEquationSystem readEqSystemFromConsole() {
        double[][] extendedMatrix = readMatrixFromConsole();
        return extMatrixToEqSys(extendedMatrix);
    }

    private double[][] readRandomMatrix() {
        final int coefMatrixSize = readMatrixSize();
        final int extMatrixSize = coefMatrixSize+1;
        double[][] matrix = new double[coefMatrixSize][extMatrixSize];
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < coefMatrixSize; i++) {
            for (int j = 0; j < extMatrixSize; j++) {
                matrix[i][j] = -1000 + rand.nextInt(2000);
            }
        }
        return matrix;
    }

    private double[][] readMatrixFromFile() {
        return null;
    }

    private double[][] readMatrixFromConsole() {
        final int coefMatrixSize = readMatrixSize();
        final int extMatrixSize = coefMatrixSize+1;
        double[][] matrix = new double[coefMatrixSize][extMatrixSize];
        writeln("Enter a system as\n" +
                "a1 a2 ... an b1\n" +
                "...\n" +
                "x1 x2 ... xn bn");
        for (int i = 0; i < coefMatrixSize; i++) {
            double[] row = null;
            while (row == null) {
                String[] inputAsStringList = scanner.nextLine().trim().split("\\s+");
                if (inputAsStringList.length != extMatrixSize) {
                    writeln("Wrong number of coefficients");
                    continue;
                }
                try {
                    row = Stream.of(inputAsStringList).mapToDouble(Double::parseDouble).toArray();
                }
                catch (NumberFormatException e) {
                    row = null;
                    writeln("Coefficients must be floating point numbers");
                }
            }
            matrix[i] = row;
        }
        return matrix;
    }

    private int readMatrixSize() {
        return readInt(1, 20, "Enter a number of variables");
    }

    private int readInt(int min, int max, String prompt) {
        writeln(prompt);
        while (true) {
            try {
                final int input = Integer.parseInt(scanner.nextLine().trim());
                if (input >= min && input <= max) {
                    return input;
                } else writeln("Enter an integer from " + min + " to " + max);
            } catch (NumberFormatException e) {
                writeln("Enter an integer from " + min + " to " + max);
            }
        }
    }
}

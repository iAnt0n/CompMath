package ui;

import equations.LinearEquationSystem;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

import static equations.MatrixUtils.extMatrixToEqSys;

public class ConsoleUserInterface {
    private PrintWriter writer;
    private Scanner scanner;

    public ConsoleUserInterface(Reader reader, Writer writer) {
        this.writer = new PrintWriter(writer);
        this.scanner = new Scanner(reader);
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void writeln(String s) {
        writer.write(s + "\n");
        writer.flush();
    }

    public int readChoice(String[] options, String prompt) {
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
                "RANDOM MATRIX",
                "EXIT"
        }, "Choose an option");

        LinearEquationSystem eqSys = null;
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
            case 3:
                System.exit(0);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + option);
        }

        if (eqSys == null) {
            return readEqSystem();
        }
        return eqSys;
    }

    private LinearEquationSystem readEqSystemRandomMatrix() {
        double[][] extendedMatrix = readRandomMatrix();
        return extMatrixToEqSys(extendedMatrix);
    }

    private LinearEquationSystem readEqSystemFromFile() {
        double[][] extendedMatrix = readMatrixFromFile();
        if (extendedMatrix != null) {
            return extMatrixToEqSys(extendedMatrix);
        } else return null;
    }

    private LinearEquationSystem readEqSystemFromConsole() {
        double[][] extendedMatrix = readMatrixFromConsole();
        return extMatrixToEqSys(extendedMatrix);
    }

    private double[][] readRandomMatrix() {
        final int coefMatrixSize = readMatrixSize();
        final int extMatrixSize = coefMatrixSize + 1;
        double[][] matrix = new double[coefMatrixSize][extMatrixSize];
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < coefMatrixSize; i++) {
            for (int j = 0; j < extMatrixSize; j++) {
                matrix[i][j] = -100 + rand.nextInt(100);
            }
        }
        return matrix;
    }

    private double[][] readMatrixFromFile() {
        writeln("Enter filename");
        Path path = Paths.get(scanner.nextLine());
        final int coefMatrixSize;
        try (Scanner sc = new Scanner(new FileReader(path.toFile()))) {
            try {
                if (sc.hasNextLine()) {
                    coefMatrixSize = Integer.parseInt(sc.nextLine());
                    if (coefMatrixSize < 1 || coefMatrixSize > 20) {
                        writeln("Invalid matrix size. Must be an integer from 1 to 20");
                        return null;
                    }
                }
                else {
                    writeln("Matrix size not provided");
                    return null;
                }
            } catch (NumberFormatException e) {
                writeln("Invalid matrix size. Must be an integer from 1 to 20");
                return null;
            }
            final int extMatrixSize = coefMatrixSize + 1;
            double[][] matrix = new double[coefMatrixSize][extMatrixSize];
            for (int i = 0; i < coefMatrixSize; i++) {
                if (sc.hasNextLine()) {
                    String[] inputAsStringList = sc.nextLine().trim().split("\\s+");
                    if (inputAsStringList.length != extMatrixSize) {
                        writeln("Wrong number of coefficients in equation " + (i + 1));
                        return null;
                    }
                    try {
                        matrix[i] = Stream.of(inputAsStringList).mapToDouble(Double::parseDouble).toArray();
                    } catch (NumberFormatException e) {
                        writeln("Coefficients must be floating point numbers. Line " + (i + 1));
                        return null;
                    }
                }
                else {
                    writeln("Number of equations is incorrect. Expected "+coefMatrixSize);
                    return null;
                }
            }
            return matrix;
        } catch (FileNotFoundException e) {
            writeln("File does not exist");
            return null;
        }
    }

    private double[][] readMatrixFromConsole() {
        final int coefMatrixSize = readMatrixSize();
        final int extMatrixSize = coefMatrixSize + 1;
        double[][] matrix = new double[coefMatrixSize][extMatrixSize];
        writeln("Enter a system as\n" +
                "a_11 a_12 ... a_1n b_1\n" +
                "...\n" +
                "a_n1 a_n2 ... a_nn b_n");
        for (int i = 0; i < coefMatrixSize; i++) {
            matrix[i] = readCoefRow(extMatrixSize);
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

    public double[] readCoefRow(int n) {
        double[] row = null;
        while (row == null) {
            String[] inputAsStringList = scanner.nextLine().trim().split("\\s+");
            if (inputAsStringList.length != n) {
                writeln("Wrong number of coefficients");
                continue;
            }
            try {
                row = Stream.of(inputAsStringList).mapToDouble(Double::parseDouble).toArray();
            } catch (NumberFormatException e) {
                row = null;
                writeln("Coefficients must be floating point numbers");
            }
        }
        return row;
    }

    public double readDouble(String prompt) {
        writeln(prompt);
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                writeln("Enter double");
            }
        }
    }
}

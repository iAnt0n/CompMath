import nlequations.NonLinearEquations;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NLEqTest extends Assert {

    @Test
    public void testBisec() {
        assertEquals(-3,
                NonLinearEquations.rootByBisection(x -> Math.pow(x, 2) - 9, -100, 100, 0.001).getRes(),
                0.001);
        assertEquals(-0.729,
                NonLinearEquations.rootByBisection(x -> 5 * Math.pow(x, 3) +
                        4 * Math.pow(x, 2) +
                        3 * x +
                        2, -100, 100, 0.001)
                        .getRes(), 0.001);
        assertEquals(-1.933,
                NonLinearEquations.rootByBisection(x -> Math.pow(2, x) + Math.pow(x, 2) - 4, -10, 10, 0.001)
                        .getRes(), 0.001);
    }

    @Test
    public void testChords() {
        assertEquals(-3,
                NonLinearEquations.rootByChords(x -> Math.pow(x, 2) - 9, -100, 100, 0.001)
                        .getRes(), 0.1);
        assertEquals(-0.729,
                NonLinearEquations.rootByChords(x -> 5 * Math.pow(x, 3) +
                        4 * Math.pow(x, 2) +
                        3 * x +
                        2, -100, 100, 0.001)
                        .getRes(), 0.1);
        assertEquals(-1.933,
                NonLinearEquations.rootByChords(x -> Math.pow(2, x) +
                        Math.pow(x, 2) - 4, -10, 10, 0.001)
                        .getRes(), 0.1);
    }

    @Test
    public void testSystem3() {
        List<Function<double[], Double>> f = new ArrayList<>();
        f.add(x -> Math.pow(x[0], 2) + Math.pow(x[1], 2) + Math.pow(x[2], 2) - 1);
        f.add(x -> 2 * Math.pow(x[0], 2) + Math.pow(x[1], 2) - 4 * x[2]);
        f.add(x -> 3 * Math.pow(x[0], 2) - 4 * x[1] + Math.pow(x[2], 2));

        List<List<Function<double[], Double>>> df = new ArrayList<>();
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
        eq3d.add(y -> -4D);
        eq3d.add(z -> 2 * z[2]);
        df.add(eq1d);
        df.add(eq2d);
        df.add(eq3d);

        assertEquals(0.7852,
                NonLinearEquations.systemRootsByNewton(0.5, 0.01, f, df)[0], 0.01);
        assertEquals(0.4966,
                NonLinearEquations.systemRootsByNewton(0.5, 0.01, f, df)[1], 0.01);
        assertEquals(0.3699,
                NonLinearEquations.systemRootsByNewton(0.5, 0.01, f, df)[2], 0.01);
    }

    @Test
    public void testSystem2() {
        List<Function<double[], Double>> f = new ArrayList<>();
        f.add(x -> Math.sin(2 * x[0] - x[1]) - 1.2 * x[0] - 0.4);
        f.add(x -> 0.8 * Math.pow(x[0], 2) + 1.5 * Math.pow(x[1], 2) - 1);

        List<List<Function<double[], Double>>> df = new ArrayList<>();
        List<Function<double[], Double>> eq1d = new ArrayList<>();
        List<Function<double[], Double>> eq2d = new ArrayList<>();
        eq1d.add(x -> 2 * Math.cos(2 * x[0] - x[1]) - 1.2);
        eq1d.add(y -> -Math.cos(2 * y[0] - y[1]));

        eq2d.add(x -> 1.6 * x[0]);
        eq2d.add(y -> 3 * y[1]);

        df.add(eq1d);
        df.add(eq2d);

        assertEquals(-1.09,
                NonLinearEquations.systemRootsByNewton(0.5, 0.01, f, df)[0], 0.01);
        assertEquals(-0.17,
                NonLinearEquations.systemRootsByNewton(0.5, 0.01, f, df)[1], 0.01);
    }
}

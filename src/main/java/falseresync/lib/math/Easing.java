package falseresync.lib.math;

/**
 * @author <a href="https://github.com/ai/easings.net">Easings.net</a>
 */
public class Easing {
    public static double easeInOutBack(double x) {
        final double c1 = 1.70158;
        final double c2 = c1 * 1.525;

        return x < 0.5
                ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
    }

    public static double easeInOutCubic(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;
    }

    public static double easeInOutQuart(double x) {
        return x < 0.5 ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2;
    }

    public static double easeOutCirc(double x) {
        return Math.sqrt(1 - Math.pow(x - 1, 2));
    }
}

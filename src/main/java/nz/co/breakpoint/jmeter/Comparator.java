package nz.co.breakpoint.jmeter;

public enum Comparator {
    UNDEFINED(""),
    LT("<") {
        @Override
        public boolean compare(double actual, double threshold) {
            return actual < threshold;
        }
    },
    LE("<=") {
        @Override
        public boolean compare(double actual, double threshold) {
            return actual <= threshold;
        }
    },
    GT(">") {
        @Override
        public boolean compare(double actual, double threshold) {
            return actual > threshold;
        }
    },
    GE(">=") {
        @Override
        public boolean compare(double actual, double threshold) {
            return actual >= threshold;
        }
    };

    private final String symbol;

    Comparator(String symbol) {
        this.symbol = symbol;
    }

    public boolean compare(double actual, double threshold) {
        return true;
    }

    public static Comparator fromString(String symbol) {
        for (Comparator c : Comparator.values()) {
            if (c.symbol.equalsIgnoreCase(symbol)) {
                return c;
            }
        }
        return null;
    }
}

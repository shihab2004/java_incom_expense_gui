package com.example.incomeexpense.ml;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.ArrayList;
import java.util.List;

public final class SimpleLedgerValuePredictor {
    private SimpleLedgerValuePredictor() {
    }

    /**
     * Predicts the next values for a 1D time series.
     *
     * <p>Implementation: least-squares linear regression over the historical values (Apache Commons Math).
     * If there is insufficient history (0 or 1 values), it falls back to repeating the last value.
     */
    public static List<Long> predictNextCents(List<Long> historyCents, int horizon) {
        if (horizon <= 0) {
            return List.of();
        }
        if (historyCents == null || historyCents.isEmpty()) {
            return List.of();
        }

        int n = historyCents.size();
        if (n == 1) {
            List<Long> out = new ArrayList<>(horizon);
            for (int i = 0; i < horizon; i++) {
                out.add(historyCents.get(0));
            }
            return out;
        }

        SimpleRegression regression = new SimpleRegression(true);
        for (int i = 0; i < n; i++) {
            regression.addData(i, historyCents.get(i));
        }

        double slope = regression.getSlope();
        double intercept = regression.getIntercept();

        List<Long> out = new ArrayList<>(horizon);
        for (int step = 0; step < horizon; step++) {
            double x = n + step;
            double predicted = intercept + slope * x;
            out.add(Math.round(predicted));
        }
        return out;
    }
}

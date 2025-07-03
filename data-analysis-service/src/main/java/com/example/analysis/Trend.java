package com.example.analysis;

public class Trend {
    private double slope;
    private double intercept;

    public Trend(double slope, double intercept) {
        this.slope = slope;
        this.intercept = intercept;
    }

    public double getSlope() { return slope; }
    public double getIntercept() { return intercept; }
}

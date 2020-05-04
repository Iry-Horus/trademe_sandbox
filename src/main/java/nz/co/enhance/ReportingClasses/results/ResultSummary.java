package nz.co.enhance.ReportingClasses.results;

public class ResultSummary {
    ResultValue features = new ResultValue();
    ResultValue scenarios = new ResultValue();
    ResultValue steps = new ResultValue();

    public ResultValue getFeatures() {
        return features;
    }

    public ResultValue getScenarios() {
        return scenarios;
    }

    public ResultValue getSteps() {
        return steps;
    }
}

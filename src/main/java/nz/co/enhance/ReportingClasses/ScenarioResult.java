package nz.co.enhance.ReportingClasses;

import gherkin.formatter.model.Scenario;
import java.util.ArrayList;
import java.util.List;

public class ScenarioResult {
    Scenario scenario;
    List<StepResult> steps = new ArrayList<>();

    public ScenarioResult(Scenario scenario) {
        this.scenario = scenario;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public List<StepResult> getSteps() {
        return steps;
    }

    public void setSteps(List<StepResult> steps) {
        this.steps = steps;
    }
}

package nz.co.enhance.ReportingClasses;

import gherkin.formatter.model.ScenarioOutline;

import java.util.ArrayList;
import java.util.List;

public class ScenarioOutlineResult {
    ScenarioOutline scenarioOutline;
    List<StepResult> steps = new ArrayList<>();

    public ScenarioOutlineResult(ScenarioOutline scenario) {
        this.scenarioOutline = scenario;
    }

    public ScenarioOutline getScenario() {
        return scenarioOutline;
    }

    public void setScenario(ScenarioOutline scenario) {
        this.scenarioOutline = scenario;
    }

    public List<StepResult> getSteps() {
        return steps;
    }

    public void setSteps(List<StepResult> steps) {
        this.steps = steps;
    }
}

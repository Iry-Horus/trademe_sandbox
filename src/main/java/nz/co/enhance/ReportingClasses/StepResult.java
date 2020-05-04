package nz.co.enhance.ReportingClasses;

import gherkin.formatter.model.Result;
import gherkin.formatter.model.Step;

public class StepResult {
    Step step;
    Result result;

    public StepResult(Step step) {
        this.step = step;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}

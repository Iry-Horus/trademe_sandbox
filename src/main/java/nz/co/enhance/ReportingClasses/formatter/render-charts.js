google.charts.load('current', {packages: ['corechart', 'table']});
google.charts.setOnLoadCallback(drawCharts);

var projectVersion = '0.1.17';

$(window).on("throttledresize", function (event) {
    drawCharts();
});

function drawCharts() {
	drawPieChart(getScenarioData(), 'scenarioPieChart', 'Scenario Results (' + getScenarioCount() + ' in total)');
	drawStackedBarChart(getFeatureScenarioBreakdownData(), getTotalFeatureCount(), 'featureScenarioBarChart', 'Feature Results (' + getTotalFeatureCount() + ' in total)');
    drawTableChart(getFeatureScenarioBreakdownData(), 'featureScenarioTableChart');
    drawLineChart(getScenarioDurationData(), 'scenarioPerformanceLineChart', 'Scenario duration (Total duration ' + getTotalDurationText() + ')', 'Duration in seconds');
    writeBreakdown('scenarioSummaryBreakdown');
}

function getTotalFeatureCount() {
    return formatterHighLevelSummary.features.passed + formatterHighLevelSummary.features.failed + formatterHighLevelSummary.features.undefined + formatterHighLevelSummary.features.pending;
}

function getScenarioCount() {
    return formatterHighLevelSummary.scenarios.passed + formatterHighLevelSummary.scenarios.failed + formatterHighLevelSummary.scenarios.undefined + formatterHighLevelSummary.scenarios.pending;
}

function getStepCount() {
    return formatterHighLevelSummary.steps.passed + formatterHighLevelSummary.steps.failed + formatterHighLevelSummary.steps.undefined + formatterHighLevelSummary.steps.pending + formatterHighLevelSummary.steps.skipped;
}

function getTotalDurationText() {
	return formatterSummaryDuration;
}

function getStepData() {
	    var data = new google.visualization.DataTable();
	    data.addColumn('string', 'Scenario');
	    data.addColumn('number', 'Scenarios');

	    var resultPassed = 0;
	    var resultFailed = 0;
	    var resultUndefined = 0;
	    var resultPending = 0;
	    var resultSkipped = 0;

	    for(var feature = 0; feature < formatterSummary.length; feature++) {
	        for(var scenario = 0; scenario < formatterSummary[feature].scenarios.length; scenario++) {
				for(var step = 0; step < formatterSummary[feature].scenarios[scenario].steps.length; step++) {
					if (formatterSummary[feature].scenarios[scenario].steps[step].result != null) {
						switch(formatterSummary[feature].scenarios[scenario].steps[step].result.status) {
							case "passed":
								resultPassed++;
								break;
							case "failed":
								resultFailed++;
								break;
							case "pending":
							    resultPending++;
							    break;
							case "undefined":
								resultUndefined++;
								break;
							case "skipped":
								resultSkipped++;
								break;
						}
					}
				}
			}
		}

	    data.addRows([
	    ['Passed', resultPassed],
	    ['Failed', resultFailed],
	    ['Undefined', resultUndefined],
	    ['Pending', resultPending],
	    ['Skipped', resultSkipped]
    ]);
    return data;
}

function getScenarioData() {
	    var data = new google.visualization.DataTable();
	    data.addColumn('string', 'Scenario');
	    data.addColumn('number', 'Scenarios');

	    var resultPassed = 0;
	    var resultFailed = 0;
	    var resultUndefined = 0;
	    var resultPending = 0;

	    for(var feature = 0; feature < formatterSummary.length; feature++) {
	        for(var scenario = 0; scenario < formatterSummary[feature].scenarios.length; scenario++) {
				var scenarioResult = null;
				for(var step = 0; step < formatterSummary[feature].scenarios[scenario].steps.length; step++) {
					if (formatterSummary[feature].scenarios[scenario].steps[step].result != null) {
						switch(formatterSummary[feature].scenarios[scenario].steps[step].result.status) {
							case "passed":
								if (scenarioResult == null) {
									scenarioResult = "passed";
								}
								break;
							case "failed":
								scenarioResult = "failed";
								break;
							case "undefined":
								if (scenarioResult == null || scenarioResult != "failed") {
									scenarioResult = "undefined";
								}
								break;
							case "pending":
								if (scenarioResult == null || scenarioResult == "passed") {
									scenarioResult = "pending";
								}
								break;
						}
					}
				}
				switch(scenarioResult) {
					case "passed":
						resultPassed++;
						break;
					case "failed":
						resultFailed++;
						break;
					case "undefined":
						resultUndefined++;
						break;
					case "pending":
						resultPending++;
						break;
				}
			}
		}

	    data.addRows([
	    ['Passed', resultPassed],
	    ['Failed', resultFailed],
	    ['Undefined', resultUndefined],
	    ['Pending', resultPending]
    ]);
    return data;
}

function getFeatureData() {
	    var data = new google.visualization.DataTable();
	    data.addColumn('string', 'Scenario');
	    data.addColumn('number', 'Scenarios');

	    var resultPassed = 0;
	    var resultFailed = 0;
	    var resultUndefined = 0;
	    var resultPending = 0;

	    for(var feature = 0; feature < formatterSummary.length; feature++) {
			var featureResult = null;
	        for(var scenario = 0; scenario < formatterSummary[feature].scenarios.length; scenario++) {
				for(var step = 0; step < formatterSummary[feature].scenarios[scenario].steps.length; step++) {
					if (formatterSummary[feature].scenarios[scenario].steps[step].result != null) {
						switch(formatterSummary[feature].scenarios[scenario].steps[step].result.status) {
							case "passed":
								if (featureResult == null) {
									featureResult = "passed";
								}
								break;
							case "failed":
								featureResult = "failed";
								break;
							case "undefined":
								if (featureResult == null || featureResult != "failed") {
									featureResult = "undefined";
								}
								break;
							case "pending":
								if (featureResult == null || featureResult == "passed") {
									featureResult = "pending";
								}
								break;
						}
					}
				}
			}
			switch(featureResult) {
				case "passed":
					resultPassed++;
					break;
				case "failed":
					resultFailed++;
					break;
				case "undefined":
					resultUndefined++;
					break;
				case "pending":
					resultPending++;
					break;
			}
		}

	    data.addRows([
	    ['Passed', resultPassed],
	    ['Failed', resultFailed],
	    ['Undefined', resultUndefined],
	    ['Pending', resultPending]
    ]);
    return data;
}

function getFeatureScenarioBreakdownData() {
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Feature');
	data.addColumn('number', 'Passed');
	data.addColumn('number', 'Failed');
	if (formatterHighLevelSummary.scenarios.undefined > 0 || formatterHighLevelSummary.scenarios.pending > 0) {
		data.addColumn('number', 'Undefined');
		data.addColumn('number', 'Pending');
	}

	for(var feature = 0; feature < formatterSummary.length; feature++) {
		var resultPassed = 0;
		var resultFailed = 0;
		var resultUndefined = 0;
		var resultPending = 0;
		for(var scenario = 0; scenario < formatterSummary[feature].scenarios.length; scenario++) {
			var scenarioResult = null;
			for(var step = 0; step < formatterSummary[feature].scenarios[scenario].steps.length; step++) {
				if (formatterSummary[feature].scenarios[scenario].steps[step].result != null) {
					switch(formatterSummary[feature].scenarios[scenario].steps[step].result.status) {
						case "passed":
							if (scenarioResult == null) {
								scenarioResult = "passed";
							}
							break;
						case "failed":
							scenarioResult = "failed";
							break;
						case "undefined":
							if (scenarioResult == null || scenarioResult != "failed") {
								scenarioResult = "undefined";
							}
							break;
						case "pending":
							if (scenarioResult == null || scenarioResult == "passed") {
								scenarioResult = "pending";
							}
							break;
					}
				}
			}
			switch(scenarioResult) {
				case "passed":
					resultPassed++;
					break;
				case "failed":
					resultFailed++;
					break;
				case "undefined":
					resultUndefined++;
					break;
				case "pending":
					resultPending++;
					break;
			}
		}
		if (formatterHighLevelSummary.scenarios.undefined > 0 || formatterHighLevelSummary.scenarios.pending > 0) {
			data.addRow([formatterSummary[feature].feature.name, resultPassed == 0 ? null : resultPassed, resultFailed == 0 ? null : resultFailed, resultUndefined == 0 ? null : resultUndefined, resultPending == 0 ? null : resultPending]);
		} else {
			data.addRow([formatterSummary[feature].feature.name, resultPassed == 0 ? null : resultPassed, resultFailed == 0 ? null : resultFailed]);
		}
	}
    return data;
}

function getScenarioDurationData() {
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Scenario');
    data.addColumn('number', 'Scenario duration');

	var scenarioNumber = 0;
    for(var feature = 0; feature < formatterSummary.length; feature++) {
        for(var scenario = 0; scenario < formatterSummary[feature].scenarios.length; scenario++) {
			var scenarioDuration = 0;
			scenarioNumber++;
			for(var step = 0; step < formatterSummary[feature].scenarios[scenario].steps.length; step++) {
				if (formatterSummary[feature].scenarios[scenario].steps[step].result != null) {
					if (null != formatterSummary[feature].scenarios[scenario].steps[step].result.duration) {
                        scenarioDuration += formatterSummary[feature].scenarios[scenario].steps[step].result.duration;
                    }
                }
            }
       	    data.addRow([formatterSummary[feature].scenarios[scenario].scenario.name + " - (" + formatterSummary[feature].scenarios[scenario].steps.length + " steps)", (Math.round((scenarioDuration / 1000000) + 0.00001) * 1000) / 1000000]);
        }
    }
    return data;
}

function drawPieChart(data, elementId, title) {
    var options = {
        title: title,
        pieSliceText: 'label',
        width: "100%",
        height: "100%",
        axisTitlesPosition: 'out',
        chartArea: {
            left: "25%",
            top: "25%",
            bottom: "15%",
            height: "100%",
            width: "100%"
        },
        slices: {
            0: { color: '#12948b' },
            1: { color: '#ed1c24' },
            2: { color: '#617e8c' },
            3: { color: '#282828' }
        },
        titleTextStyle: {
            fontSize: '18',
            bold: true
        }
    };

    new google.visualization.PieChart(document.getElementById(elementId)).draw(data, options);
}

function drawStackedBarChart(data, total, elementId, title) {
	var options = {
        title: title,
        width: "100%",
        height: "100%",
        axisTitlesPosition: 'inAndOut',
        chartArea: {
            left: "25%",
            top: "25%",
            bottom: "15%",
            right: "2%",
            height: "100%",
            width: "100%"
        },
        colors: ['#12948b',
            '#ed1c24',
            '#617e8c',
            '#282828']
        ,
        hAxis: {format: '0', viewWindowMode: 'pretty'},
        vAxis: {showTextEvery: 1},
        bars: 'horizontal',
        legend: { position: 'top', maxLines: 3 },
        bar: { groupWidth: '90%' },
        isStacked: true,
        titleTextStyle: {
            fontSize: '18',
            bold: true
        }
    };
    new google.visualization.BarChart(document.getElementById(elementId)).draw(data, options);
}


function drawLineChart(data, elementId, title, vAxisTitle) {
	var options = {
        title: title,
        width: "100%",
        height: "100%",
		vAxis: {
            title: vAxisTitle
	    },
	    hAxis: { textPosition: 'none' },
        legend: { position: 'none' },
        titleTextStyle: {
            fontSize: '18',
            bold: true
        }
    };
    new google.visualization.LineChart(document.getElementById(elementId)).draw(data, options);
}

function drawTableChart(data, elementId) {
    var cssClassNames = {
    'headerRow': 'headerRow',
    'tableRow': 'tableRow',
    'oddTableRow': 'tableRow',
    'selectedTableRow': 'selectedTableRow',
    'hoverTableRow': 'hoverTableRow',
    'headerCell': 'headerCell',
    'tableCell': 'tableCell',
    'rowNumberCell': 'rowNumberCell'};
	var options = {
        width: "100%",
        height: "100%",
        chartArea: {
            height: "100%",
            width: "100%"
        },
        'cssClassNames': cssClassNames
    };
    new google.visualization.Table(document.getElementById(elementId)).draw(data, options);
}

function writeBreakdown(elementId) {
	var element = document.getElementById(elementId);
	element.innerHTML = "<div class=\"breakdownHeading chartHeading\">Breakdown</div>";
	element.innerHTML += "<div class=\"breakdownRow\">" + getTotalFeatureCount() + " <b>Features</b> (<span class=\"passed\">" + formatterHighLevelSummary.features.passed + " passed,</span>	 <span class=\"failed\">" + formatterHighLevelSummary.features.failed + " failed,</span> <span class=\"undefined\">" + formatterHighLevelSummary.features.undefined + " undefined,</span> <span class=\"pending\">" + formatterHighLevelSummary.features.pending + " pending</span>)</div>";
	element.innerHTML += "<div class=\"breakdownRow\">" + getScenarioCount() + " <b>Scenarios</b> (<span class=\"passed\">" + formatterHighLevelSummary.scenarios.passed + " passed,</span> <span class=\"failed\">" + formatterHighLevelSummary.scenarios.failed + " failed,</span> <span class=\"undefined\">" + formatterHighLevelSummary.scenarios.undefined + " undefined,</span> <span class=\"pending\">" + formatterHighLevelSummary.scenarios.skipped + " skipped</span>)</div>";
	element.innerHTML += "<div class=\"breakdownRow\">" + getStepCount() + " <b>Steps</b> (<span class=\"passed\">" + formatterHighLevelSummary.steps.passed + " passed,</span> <span class=\"failed\">" + formatterHighLevelSummary.steps.failed + " failed,</span> <span class=\"undefined\">" + formatterHighLevelSummary.steps.undefined + " undefined,</span> <span class=\"pending\">" + formatterHighLevelSummary.steps.pending + " pending,</span> <span class=\"skipped\">" + formatterHighLevelSummary.steps.skipped + " skipped</span>)</div>";
}
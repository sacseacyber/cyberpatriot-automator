package client.scoringreport;

import client.config.CPConfig;

public class ScoringReportDisplay {
	private CPConfig cpconfig;
	private ScoringReport report;

	public ScoringReportDisplay(CPConfig cpconfig) {
		this.cpconfig = cpconfig;
		this.report = new ScoringReport(cpconfig);
	}
}

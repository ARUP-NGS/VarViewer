package varviewer.shared.bcrabl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BCRABLReport implements Serializable {

	String message;
	String sampleAccession;
	String analysisDate;
	List<String> reportText = new ArrayList<String>();
	
	boolean passedQualityCheck = false;
	String qualityMessage = null;
	
	public BCRABLReport() {
		//blah blah
	}

	
	
	public boolean isPassedQualityCheck() {
		return passedQualityCheck;
	}



	public void setPassedQualityCheck(boolean passedQualityCheck) {
		this.passedQualityCheck = passedQualityCheck;
	}



	public String getQualityMessage() {
		return qualityMessage;
	}



	public void setQualityMessage(String qualityMessage) {
		this.qualityMessage = qualityMessage;
	}



	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSampleAccession() {
		return sampleAccession;
	}

	public void setSampleAccession(String sampleAccession) {
		this.sampleAccession = sampleAccession;
	}

	public String getAnalysisDate() {
		return analysisDate;
	}

	public void setAnalysisDate(String analysisDate) {
		this.analysisDate = analysisDate;
	}

	public List<String> getReportText() {
		return reportText;
	}

	public void addReportTextLine(String line) {
		this.reportText.add(line);
	}
	
	
}

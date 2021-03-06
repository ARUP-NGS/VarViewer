package varviewer.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Stores more detailed information about a gene
 * @author brendan
 *
 */
public class GeneInfo implements Serializable, IsSerializable {

	String summary;
	String[] omimDiseaseIDs;
	String[] omimInheritance;
	String[] hgmdDiseases;
	String dbNSFPDisease;
	String[] hgmdVars;
	String[] omimPhenos;
	String fullName;
	
	
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String[] getOmimPhenos() {
		return omimPhenos;
	}

	public void setOmimPhenos(String[] omimPhenos) {
		this.omimPhenos = omimPhenos;
	}

	public GeneInfo() {
		//must have no-arg constructor for serialization
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String[] getOmimDiseaseIDs() {
		return omimDiseaseIDs;
	}

	public void setOmimDiseaseIDs(String[] omimDiseaseIDs) {
		this.omimDiseaseIDs = omimDiseaseIDs;
	}

	public String[] getOmimInheritance() {
		return omimInheritance;
	}

	public void setOmimInheritance(String[] omimInheritance) {
		this.omimInheritance = omimInheritance;
	}

	public String[] getHgmdDiseases() {
		return hgmdDiseases;
	}

	public void setHgmdDiseases(String[] hgmdDiseases) {
		this.hgmdDiseases = hgmdDiseases;
	}

	public String getDbNSFPDisease() {
		return dbNSFPDisease;
	}

	public void setDbNSFPDisease(String dbNSFPDisease) {
		this.dbNSFPDisease = dbNSFPDisease;
	}

	public String[] getHgmdVars() {
		return hgmdVars;
	}

	public void setHgmdVars(String[] hgmdVars) {
		this.hgmdVars = hgmdVars;
	}
	
	
	
	
}

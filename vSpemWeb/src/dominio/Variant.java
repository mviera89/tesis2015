package dominio;

import java.util.ArrayList;
import java.util.List;

public class Variant {

	private String ID;
	private String name;
	private String IDVarPoint;
	private boolean isInclusive;
	private String varType;
	private List<String> inclusivas;
	private List<String> exclusivas;
	
	public Variant(String ID, String name, String IDVarPoint, boolean isInclusive, String varType){
		this.ID = ID;
		this.name = name;
		this.IDVarPoint = IDVarPoint;
		this.isInclusive = isInclusive;
		this.varType = varType;
		this.inclusivas = new ArrayList<String>();
		this.exclusivas = new ArrayList<String>();
		
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIDVarPoint() {
		return IDVarPoint;
	}

	public void setIDVarPoint(String iDVarPoint) {
		IDVarPoint = iDVarPoint;
	}

	public boolean isInclusive() {
		return isInclusive;
	}

	public void setInclusive(boolean isInclusive) {
		this.isInclusive = isInclusive;
	}

	public String getVarType() {
		return varType;
	}

	public void setVarType(String varType) {
		this.varType = varType;
	}

	public List<String> getInclusivas() {
		return inclusivas;
	}

	public void setInclusivas(List<String> inclusivas) {
		this.inclusivas = inclusivas;
	}

	public List<String> getExclusivas() {
		return exclusivas;
	}

	public void setExclusivas(List<String> exclusivas) {
		this.exclusivas = exclusivas;
	}
	
	
	
		
}

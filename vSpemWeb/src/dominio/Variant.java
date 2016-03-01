package dominio;

import java.util.ArrayList;
import java.util.List;

public class Variant {

	private String ID;
	private String name;
    private String presentationName;
	private String description;
	private String briefDescription;
	private String IDVarPoint;
	private boolean isInclusive;
	private String varType;
	private List<String> inclusivas;
	private List<String> exclusivas;
	private List<Struct> hijos;
    private Boolean estaExpandido;
    private String processComponentId;
	private String processComponentName;
	private String presentationId;
	private String elementIDExtends;
	
	public Variant(String ID, String name, String presentationName, String IDVarPoint, boolean isInclusive, String varType, String processComponentId, String processComponentName, String presentationId, String elementIDExtends){
		this.ID = ID;
		this.name = name;
		this.presentationName = presentationName;
		this.IDVarPoint = IDVarPoint;
		this.isInclusive = isInclusive;
		this.varType = varType;
		this.inclusivas = new ArrayList<String>();
		this.exclusivas = new ArrayList<String>();
		this.hijos = new ArrayList<Struct>();
		this.estaExpandido = false;
		this.processComponentId = processComponentId;
		this.processComponentName = processComponentName;
		this.presentationId = presentationId;
		this.elementIDExtends = elementIDExtends;
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
	
	public String getPresentationName() {
		return presentationName;
	}

	public void setPresentationName(String presentationName) {
		this.presentationName = presentationName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBriefDescription() {
		return briefDescription;
	}

	public void setBriefDescription(String briefDescription) {
		this.briefDescription = briefDescription;
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

	public List<Struct> getHijos() {
		return hijos;
	}

	public void setHijos(List<Struct> hijos) {
		this.hijos = hijos;
	}

	public Boolean getEstaExpandido() {
		return estaExpandido;
	}

	public void setEstaExpandido(Boolean estaExpandido) {
		this.estaExpandido = estaExpandido;
	}

	public String getProcessComponentId() {
		return processComponentId;
	}

	public void setProcessComponentId(String processComponentId) {
		this.processComponentId = processComponentId;
	}

	public String getProcessComponentName() {
		return processComponentName;
	}

	public void setProcessComponentName(String processComponentName) {
		this.processComponentName = processComponentName;
	}

	public String getPresentationId() {
		return presentationId;
	}

	public void setPresentationId(String presentationId) {
		this.presentationId = presentationId;
	}

	public String getElementIDExtends() {
		return elementIDExtends;
	}

	public void setElementIDExtends(String elementIDExtends) {
		this.elementIDExtends = elementIDExtends;
	}

}

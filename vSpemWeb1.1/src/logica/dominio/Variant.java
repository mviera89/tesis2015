package logica.dominio;

import java.util.ArrayList;
import java.util.List;

import logica.dataTypes.TipoMethodElementProperty;
import logica.dataTypes.TipoSection;

public class Variant {

	private String ID;
	private String name;
    private String presentationName;
	private String guid;
	private String isPlanned;
	private String superActivities;
	private String isOptional;
	private String variabilityType;
	private String isSynchronizedWithSource;
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
	private String processComponentPresentationName;
	private String presentationId;
	private String elementIDExtends;
	private String idTask;
	private String idRole;
	private String idWorkProduct;
    private List<TipoSection> steps;
    private List<TipoMethodElementProperty> methodElementProperties;
    private String diagramURI;
    
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
		this.steps = new ArrayList<TipoSection>();
		this.methodElementProperties = new ArrayList<TipoMethodElementProperty>();
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

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getIsPlanned() {
		return isPlanned;
	}

	public void setIsPlanned(String isPlanned) {
		this.isPlanned = isPlanned;
	}

	public String getSuperActivities() {
		return superActivities;
	}

	public void setSuperActivities(String superActivities) {
		this.superActivities = superActivities;
	}

	public String getIsOptional() {
		return isOptional;
	}

	public void setIsOptional(String isOptional) {
		this.isOptional = isOptional;
	}

	public String getVariabilityType() {
		return variabilityType;
	}

	public void setVariabilityType(String variabilityType) {
		this.variabilityType = variabilityType;
	}

	public String getIsSynchronizedWithSource() {
		return isSynchronizedWithSource;
	}

	public void setIsSynchronizedWithSource(String isSynchronizedWithSource) {
		this.isSynchronizedWithSource = isSynchronizedWithSource;
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

	public String getProcessComponentPresentationName() {
		return processComponentPresentationName;
	}

	public void setProcessComponentPresentationName(
			String processComponentPresentationName) {
		this.processComponentPresentationName = processComponentPresentationName;
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

	public String getIdTask() {
		return idTask;
	}

	public void setIdTask(String idTask) {
		this.idTask = idTask;
	}

	public String getIdRole() {
		return idRole;
	}

	public void setIdRole(String idRole) {
		this.idRole = idRole;
	}

	public String getIdWorkProduct() {
		return idWorkProduct;
	}

	public void setIdWorkProduct(String idWorkProduct) {
		this.idWorkProduct = idWorkProduct;
	}

	public List<TipoSection> getSteps() {
		return steps;
	}

	public void setSteps(List<TipoSection> steps) {
		this.steps = steps;
	}

	public List<TipoMethodElementProperty> getMethodElementProperties() {
		return methodElementProperties;
	}

	public void setMethodElementProperties(
			List<TipoMethodElementProperty> methodElementProperties) {
		this.methodElementProperties = methodElementProperties;
	}

	public String getDiagramURI() {
		return diagramURI;
	}

	public void setDiagramURI(String diagramURI) {
		this.diagramURI = diagramURI;
	}

}

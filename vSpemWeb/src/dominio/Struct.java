package dominio;

import java.util.ArrayList;
import java.util.List;

import config.Constantes;
import dataTypes.TipoElemento;

public class Struct {
	
	private String elementID;
	private String nombre;
	private TipoElemento type;
	private List<Variant> variantes;
	private List<Struct> hijos;
	private int min;
	private int max;

    private String color;
    private String imagen;
    private Boolean esPV;
    private Boolean estaExpandido;
    private String etiqueta; // Si es opcional, obligatorio, etc.

	private String description;
	private String briefDescription;
    private String presentationName;
    private String performedPrimaryBy;
    private List<String> performedAditionallyBy;
    private List<String> mandatoryInputs;
    private List<String> optionalInputs;
    private List<String> externalInputs;
    private List<String> outputs;
    private List<String> responsableDe;
    private List<String> modifica;
    private List<String> linkToPredecessor;
    private List<String> sucesores;
    private String processComponentId;
	private String processComponentName;
	private String presentationId;
	private String elementIDExtends;

    private String idTask;
    private String idWorkProduct;
    
	public Struct(String ID, String nombre, TipoElemento type, int min, int max, String imagen, String processComponentId, String processComponentName, String presentationId, String elementIDExtends){
		this.elementID = ID;
		this.nombre = nombre;
		this.type = type;
		this.min = min;
		this.max = max;
        this.imagen = imagen;
        this.esPV = (type == TipoElemento.VP_ACTIVITY ||
        			 type == TipoElemento.VP_TASK ||
        			 type == TipoElemento.VP_PHASE ||
        			 type == TipoElemento.VP_ITERATION ||
        			 type == TipoElemento.VP_ROLE ||
        			 type == TipoElemento.VP_MILESTONE ||
        			 type == TipoElemento.VP_WORK_PRODUCT);
        this.estaExpandido = false;
        this.etiqueta = "";
        this.color = this.esPV ? Constantes.colorVarPoint : "black";
		this.variantes = new ArrayList<Variant>();
		this.hijos = new ArrayList<Struct>();
		this.description = "";
		this.presentationName = "";
		this.performedPrimaryBy = "";
		this.performedAditionallyBy = null;
		this.mandatoryInputs = null;
		this.optionalInputs = null;
		this.externalInputs = null;
		this.outputs = null;
		this.responsableDe = null;
		this.modifica = null;
		this.linkToPredecessor = null;
		this.sucesores = null;
		this.processComponentId = processComponentId;
		this.processComponentName = processComponentName;
		this.presentationId = presentationId;
		this.elementIDExtends = elementIDExtends;
	}

	public String getElementID() {
		return elementID;
	}

	public void setElementID(String elementID) {
		this.elementID = elementID;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Variant> getVariantes() {
		return variantes;
	}

	public void setVariantes(ArrayList<Variant> variantes) {
		this.variantes = variantes;
	}

	public TipoElemento getType() {
		return type;
	}

	public void setType(TipoElemento type) {
		this.type = type;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

    public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

	public Boolean getEsPV() {
		return esPV;
	}

	public void setEsPV(Boolean esPV) {
		this.esPV = esPV;
	}
    
    public Boolean getEstaExpandido() {
		return estaExpandido;
	}

	public void setEstaExpandido(Boolean estaExpandido) {
		this.estaExpandido = estaExpandido;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public List<Struct> getHijos() {
		return hijos;
	}

	public void setHijos(List<Struct> hijos) {
		this.hijos = hijos;
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

	public String getPresentationName() {
		return presentationName;
	}

	public void setPresentationName(String presentationName) {
		this.presentationName = presentationName;
	}

	public String getPerformedPrimaryBy() {
		return performedPrimaryBy;
	}

	public void setPerformedPrimaryBy(String performedPrimaryBy) {
		this.performedPrimaryBy = performedPrimaryBy;
	}

	public List<String> getPerformedAditionallyBy() {
		return performedAditionallyBy;
	}

	public void setPerformedAditionallyBy(List<String> performedAditionallyBy) {
		this.performedAditionallyBy = performedAditionallyBy;
	}

	public List<String> getMandatoryInputs() {
		return mandatoryInputs;
	}

	public void setMandatoryInputs(List<String> mandatoryInputs) {
		this.mandatoryInputs = mandatoryInputs;
	}

	public List<String> getOptionalInputs() {
		return optionalInputs;
	}

	public void setOptionalInputs(List<String> optionalInputs) {
		this.optionalInputs = optionalInputs;
	}

	public List<String> getExternalInputs() {
		return externalInputs;
	}

	public void setExternalInputs(List<String> externalInputs) {
		this.externalInputs = externalInputs;
	}

	public List<String> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<String> outputs) {
		this.outputs = outputs;
	}

	public List<String> getResponsableDe() {
		return responsableDe;
	}

	public void setResponsableDe(List<String> responsableDe) {
		this.responsableDe = responsableDe;
	}

	public List<String> getModifica() {
		return modifica;
	}

	public void setModifica(List<String> modifica) {
		this.modifica = modifica;
	}

	public List<String> getLinkToPredecessor() {
		return linkToPredecessor;
	}

	public void setLinkToPredecessor(List<String> linkToPredecessor) {
		this.linkToPredecessor = linkToPredecessor;
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

	public List<String> getSucesores() {
		return sucesores;
	}

	public void setSucesores(List<String> sucesores) {
		this.sucesores = sucesores;
	}

	public String getIdTask() {
		return idTask;
	}

	public void setIdTask(String idTask) {
		this.idTask = idTask;
	}

	public String getIdWorkProduct() {
		return idWorkProduct;
	}

	public void setIdWorkProduct(String idWorkProduct) {
		this.idWorkProduct = idWorkProduct;
	}

	@Override
	public String toString() {
		return "Struct [elementID=" + elementID + ", nombre=" + nombre
				+ ", type=" + type + ", variantes=" + variantes + ", hijos="
				+ hijos + ", min=" + min + ", max=" + max + ", color=" + color
				+ ", imagen=" + imagen + ", esPV=" + esPV + ", estaExpandido="
				+ estaExpandido + ", etiqueta=" + etiqueta + ", description="
				+ description + ", briefDescription=" + briefDescription
				+ ", presentationName=" + presentationName
				+ ", performedPrimaryBy=" + performedPrimaryBy
				+ ", performedAditionallyBy=" + performedAditionallyBy
				+ ", mandatoryInputs=" + mandatoryInputs + ", optionalInputs="
				+ optionalInputs + ", externalInputs=" + externalInputs
				+ ", outputs=" + outputs + ", responsableDe=" + responsableDe
				+ ", modifica=" + modifica + ", linkToPredecessor="
				+ linkToPredecessor + ", sucesores=" + sucesores
				+ ", processComponentId=" + processComponentId
				+ ", processComponentName=" + processComponentName
				+ ", presentationId=" + presentationId + ", elementIDExtends="
				+ elementIDExtends + ", idTask=" + idTask + "]";
	}

}

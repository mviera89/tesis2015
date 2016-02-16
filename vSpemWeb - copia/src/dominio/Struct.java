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
    private String presentationName;
    private String performedPrimaryBy;
    private List<String> performedAditionallyBy;
    private List<String> mandatoryInputs;
    private List<String> optionalInputs;
    private List<String> externalInputs;
    private List<String> outputs;
   
    

	public Struct(String ID, String nombre, TipoElemento type, int min, int max, String imagen){
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
	
	

}

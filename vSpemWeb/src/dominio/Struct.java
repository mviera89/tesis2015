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
        			 type == TipoElemento.VP_ITERATION);
        this.color = this.esPV ? Constantes.colorVarPoint : "black";
		this.variantes = new ArrayList<Variant>();
		this.hijos = new ArrayList<Struct>();
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

	public List<Struct> getHijos() {
		return hijos;
	}

	public void setHijos(List<Struct> hijos) {
		this.hijos = hijos;
	}

}

package dominio;

import java.util.ArrayList;
import java.util.List;

import dataTypes.TipoElemento;

public class Struct {
	
	private String elementID;
	private String nombre;
	private TipoElemento type;
	private List<Variant> hijos;
	
	public Struct(String ID, String nombre, TipoElemento type){
		this.elementID = ID;
		this.nombre = nombre;
		this.type = type;
		this.hijos = new ArrayList<Variant>();
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

	public List<Variant> getHijos() {
		return hijos;
	}

	public void setHijos(ArrayList<Variant> hijos) {
		this.hijos = hijos;
	}
	
	
	public TipoElemento getType() {
		return type;
	}

	public void setType(TipoElemento type) {
		type = type;
	}
	
}

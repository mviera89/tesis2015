package dominio;

import java.util.ArrayList;
import java.util.List;

public class Struct {
	
	private String elementID;
	
	private String nombre;
	
	private String Type;
	
	private List<Variant> hijos;
	
		
	public Struct(String ID, String nombre, String type){
		this.elementID = ID;
		this.nombre = nombre;
		this.Type = type;
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
	
	
	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}
	

}

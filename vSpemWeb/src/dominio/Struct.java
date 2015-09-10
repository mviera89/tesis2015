package dominio;

import java.util.ArrayList;

public class Struct {
	
	private String elementID;
	
	private String Type;
	
	private ArrayList<String> hijos;
	
		
	public Struct(String ID, String type){
		this.elementID = ID;
		this.Type = type;
		this.hijos = new ArrayList<String>();
		
	}

	public String getElementID() {
		return elementID;
	}

	public void setElementID(String elementID) {
		this.elementID = elementID;
	}

	public ArrayList<String> getHijos() {
		return hijos;
	}

	public void setHijos(ArrayList<String> hijos) {
		this.hijos = hijos;
	}
	
	
	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}
	

}

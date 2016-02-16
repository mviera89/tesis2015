package dataTypes;

import java.util.List;

public class WorkProduct {
	
	private String tipo;
	private List<String> workProducts;
	
	public WorkProduct(String tipo, List<String> wp){
		this.tipo = tipo;
		this.workProducts = wp;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public List<String> getWorkProducts() {
		return workProducts;
	}

	public void setWorkProducts(List<String> workProducts) {
		this.workProducts = workProducts;
	}
	
	

}

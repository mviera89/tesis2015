package managedBeans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="VistaBean")
@ViewScoped
public class VistaBean {
	
	private int indiceActivo = 0;
	
	public int getIndiceActivo() {
		return indiceActivo;
	}
	
	public void setIndiceActivo(int indiceActivo) {
		this.indiceActivo = indiceActivo;
	}
	
	public void actualizarIndiceActivo(int indice){
		setIndiceActivo(indice);
	}
	
}

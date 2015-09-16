package managedBeans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.TreeNode;

@ManagedBean(name="VistaBean")
@SessionScoped
public class VistaBean {

	private int indiceActivo = 0;
	private String nombreArchivo = "";

	public int getIndiceActivo() {
		return indiceActivo;
	}

	public void setIndiceActivo(int indiceActivo) {
		this.indiceActivo = indiceActivo;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public void actualizarIndiceActivo(int indice){
		setIndiceActivo(indice);
	}
	

}

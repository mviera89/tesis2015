package managedBeans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.TreeNode;
import org.primefaces.model.diagram.DefaultDiagramModel;

@ManagedBean(name="VistaBean")
@SessionScoped
public class VistaBean {

	private int indiceActivo = 0;
	private String nombreArchivo = "";
	//private DefaultDiagramModel modeloAdaptado = null;
	private boolean finModelado = false;
	
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

	/*public DefaultDiagramModel getModeloAdaptado() {
		return modeloAdaptado;
	}

	public void setModeloAdaptado(DefaultDiagramModel modeloAdaptado) {
		this.modeloAdaptado = modeloAdaptado;
	}*/

	public boolean isFinModelado() {
		return finModelado;
	}

	public void setFinModelado(boolean finModelado) {
		this.finModelado = finModelado;
	}

	public void actualizarIndiceActivo(int indice){
		setIndiceActivo(indice);
	}
	
	public boolean deshabilitar(int indice){
		boolean res = true;
		switch (indice) {
			case 0:
				res = false;
				break;
			case 1:
				res = (nombreArchivo == "");
				break;
			case 2:
				/*res = (modeloAdaptado == null);
				break;
			case 3:
				res = !finModelado;*/
				res = /*((modeloAdaptado == null) ||*/ !finModelado/*)*/;
				break;
			default:
				break;
		}
		return res;
	}

}

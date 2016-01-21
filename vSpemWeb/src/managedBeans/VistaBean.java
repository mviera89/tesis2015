package managedBeans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="VistaBean")
@SessionScoped
public class VistaBean {

	private int indiceActivo = 0;
	private String nombreArchivo = "";
	private List<String> capabilityPatterns = null;
	private boolean finModelado = false;
	private String repositorio = "";

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

	public List<String> getCapabilityPatterns() {
		return capabilityPatterns;
	}

	public void setCapabilityPatterns(List<String> capabilityPatterns) {
		this.capabilityPatterns = capabilityPatterns;
	}

	public boolean isFinModelado() {
		return finModelado;
	}

	public void setFinModelado(boolean finModelado) {
		this.finModelado = finModelado;
	}
	
	public String getRepositorio() {
		return repositorio;
	}

	public void setRepositorio(String repositorio) {
		this.repositorio = repositorio;
	}

	public void actualizarIndiceActivo(int indice){
		setIndiceActivo(indice);
		
		if (indice == 1){
			FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
    		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
			AdaptarModeloBean ab = (AdaptarModeloBean) session.getAttribute("adaptarModeloBean");
	        if (ab != null){
	        	ab.init();
	        }
	        this.setFinModelado(false);
		}
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
				res = !finModelado;
				break;
			default:
				break;
		}
		return res;
	}

	public void addCapabilityPattern(String nombreArchivoCP){
		if (capabilityPatterns == null){
			capabilityPatterns = new ArrayList<String>();
		}
		capabilityPatterns.add(nombreArchivoCP);
	}
}

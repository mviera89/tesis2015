package managedBeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import dataTypes.TipoContentCategory;
import dataTypes.TipoContentDescription;
import dataTypes.TipoContentPackage;
import dataTypes.TipoLibrary;
import dataTypes.TipoMethodConfiguration;
import dataTypes.TipoMethodPackage;
import dataTypes.TipoPlugin;
import dataTypes.TipoTask;

@ManagedBean(name="VistaBean")
@SessionScoped
public class VistaBean {

	private int indiceActivo = 0;
	private String nombreArchivo = "";
	private List<String> capabilityPatterns = null;
	private boolean finModelado = false;
	private String repositorio = "";
	private TipoLibrary library = null;
	private TipoPlugin plugin = null;
	private TipoContentDescription contentDescription = null;
	private TipoContentCategory contentCategory = null;
	private TipoMethodConfiguration methodConfiguration = null;
	private Map<String, TipoContentCategory> categorizedElements = null;
	private List<TipoMethodPackage> processPackages = null;
	private Map<String, TipoTask> tasks = null;
	private List<TipoContentPackage> contentPackages = null;
	
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

	public TipoLibrary getLibrary() {
		return library;
	}

	public void setLibrary(TipoLibrary library) {
		this.library = library;
	}

	public TipoPlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(TipoPlugin plugin) {
		this.plugin = plugin;
	}

	public TipoContentDescription getContentDescription() {
		return contentDescription;
	}

	public void setContentDescription(TipoContentDescription contentDescription) {
		this.contentDescription = contentDescription;
	}

	public TipoContentCategory getContentCategory() {
		return contentCategory;
	}

	public void setContentCategory(TipoContentCategory contentCategory) {
		this.contentCategory = contentCategory;
	}

	public TipoMethodConfiguration getMethodConfiguration() {
		return methodConfiguration;
	}

	public void setMethodConfiguration(TipoMethodConfiguration methodConfiguration) {
		this.methodConfiguration = methodConfiguration;
	}

	public Map<String, TipoContentCategory> getCategorizedElements() {
		return categorizedElements;
	}

	public void setCategorizedElements(
			Map<String, TipoContentCategory> categorizedElements) {
		this.categorizedElements = categorizedElements;
	}

	public List<TipoMethodPackage> getProcessPackages() {
		return processPackages;
	}

	public void setProcessPackages(List<TipoMethodPackage> processPackages) {
		this.processPackages = processPackages;
	}

	public Map<String, TipoTask> getTasks() {
		return tasks;
	}

	public void setTasks(Map<String, TipoTask> tasks) {
		this.tasks = tasks;
	}

	public List<TipoContentPackage> getContentPackages() {
		return contentPackages;
	}

	public void setContentPackages(List<TipoContentPackage> contentPackages) {
		this.contentPackages = contentPackages;
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

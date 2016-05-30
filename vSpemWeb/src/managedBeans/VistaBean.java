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
import dataTypes.TipoContentElement;
import dataTypes.TipoContentPackage;
import dataTypes.TipoLibrary;
import dataTypes.TipoMethodConfiguration;
import dataTypes.TipoMethodPackage;
import dataTypes.TipoPlugin;

@ManagedBean(name="VistaBean")
@SessionScoped
public class VistaBean {

	private int indiceActivo = 0;
	private String dirPlugin = "";
	private String nombreArchivo = "";
	private String directorioArchivo = "";
	private List<String> capabilityPatterns = null;
	private boolean finModelado = false;
	private String repositorio = "";
	private TipoLibrary library = null;
	private TipoPlugin plugin = null;
	private TipoContentCategory contentCategory = null;
	private TipoMethodConfiguration methodConfiguration = null;
	private Map<String, TipoContentCategory> categorizedElements = null;
	private List<TipoMethodPackage> processPackages = null;
	private List<TipoContentPackage> contentPackages = null;
	private Map<String, TipoContentElement> templates = null;
	private Map<String, List<TipoContentElement>> roles = null;
	private List<String> diagrams = null;
	
	public int getIndiceActivo() {
		return indiceActivo;
	}

	public void setIndiceActivo(int indiceActivo) {
		this.indiceActivo = indiceActivo;
	}

	public String getDirPlugin() {
		return dirPlugin;
	}

	public void setDirPlugin(String dirPlugin) {
		this.dirPlugin = dirPlugin;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public String getDirectorioArchivo() {
		return directorioArchivo;
	}

	public void setDirectorioArchivo(String directorioArchivo) {
		this.directorioArchivo = directorioArchivo;
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

	public List<TipoContentPackage> getContentPackages() {
		return contentPackages;
	}

	public void setContentPackages(List<TipoContentPackage> contentPackages) {
		this.contentPackages = contentPackages;
	}

	public Map<String, TipoContentElement> getTemplates() {
		return templates;
	}

	public void setTemplates(Map<String, TipoContentElement> templates) {
		this.templates = templates;
	}

	public Map<String, List<TipoContentElement>> getRoles() {
		return roles;
	}

	public void setRoles(Map<String, List<TipoContentElement>> roles) {
		this.roles = roles;
	}

	public List<String> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<String> diagrams) {
		this.diagrams = diagrams;
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

	public void addDiagram(String dirDiagram){
		if (diagrams == null){
			diagrams = new ArrayList<String>();
		}
		if (!diagrams.contains(dirDiagram)){
			diagrams.add(dirDiagram);
		}
	}
	
}

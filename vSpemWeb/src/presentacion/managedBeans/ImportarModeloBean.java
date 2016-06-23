package presentacion.managedBeans;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.servlet.http.HttpSession;

import logica.dataTypes.TipoContentCategory;
import logica.dataTypes.TipoContentElement;
import logica.dataTypes.TipoContentPackage;
import logica.dataTypes.TipoLibrary;
import logica.dataTypes.TipoMethodConfiguration;
import logica.dataTypes.TipoMethodPackage;
import logica.dataTypes.TipoPlugin;
import logica.negocio.IImportarManager;

import org.primefaces.event.TabChangeEvent;

import com.sun.mail.iap.ConnectionException;

import config.Constantes;
import config.ReadProperties;

@ManagedBean
@SessionScoped
public class ImportarModeloBean {

	IImportarManager iim;
	
	private String mensajeAyudaRepositorio = Constantes.mensjaeAyudaRepositorio;
	private String repositorioIngresado = ReadProperties.getProperty("URL_GITHUB_DEFAULT");
	private String directorioLocalIngresado = ReadProperties.getProperty("DIRECTORIO_LOCAL_DEFAULT");
	private Boolean desdeRepositorio = true;
	private String repositorio = "";
	private String nombreArchivo = "";
	private String nombreArchivoRepo = "";
	private String nombreArchivoLocal = "";
	private List<String> archivosDisponibles = new ArrayList<String>();
	private List<String> archivosDisponiblesLocal = new ArrayList<String>();


	public ImportarModeloBean() {
		try{
			this.iim = InitialContext.doLookup("java:module/ImportarManager");
		}
		catch (Exception e){
			e.printStackTrace();		
		}
	}

	public String getMensajeAyudaRepositorio() {
		return mensajeAyudaRepositorio;
	}

	public void setMensajeAyudaRepositorio(String mensajeAyudaRepositorio) {
		this.mensajeAyudaRepositorio = mensajeAyudaRepositorio;
	}

	public String getRepositorioIngresado() {
		return repositorioIngresado;
	}

	public void setRepositorioIngresado(String repositorioIngresado) {
		this.repositorioIngresado = repositorioIngresado;
	}

	public String getDirectorioLocalIngresado() {
		return directorioLocalIngresado;
	}

	public void setDirectorioLocalIngresado(String directorioLocalIngresado) {
		this.directorioLocalIngresado = directorioLocalIngresado;
	}

	public String getRepositorio() {
		return repositorio;
	}

	public void setRepositorio(String repositorio) {
		this.repositorio = repositorio;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public String getNombreArchivoRepo() {
		return nombreArchivoRepo;
	}

	public void setNombreArchivoRepo(String nombreArchivoRepo) {
		this.nombreArchivoRepo = nombreArchivoRepo;
	}

	public List<String> getArchivosDisponiblesLocal() {
		return archivosDisponiblesLocal;
	}

	public void setArchivosDisponiblesLocal(List<String> archivosDisponiblesLocal) {
		this.archivosDisponiblesLocal = archivosDisponiblesLocal;
	}

	public String getNombreArchivoLocal() {
		return nombreArchivoLocal;
	}

	public void setNombreArchivoLocal(String nombreArchivoLocal) {
		this.nombreArchivoLocal = nombreArchivoLocal;
	}

	public List<String> getArchivosDisponibles() {
		return archivosDisponibles;
	}

	public void setArchivosDisponibles(List<String> archivosDisponibles) {
		this.archivosDisponibles = archivosDisponibles;
	}

	/*** CARGA DE ARCHIVOS ***/

	public void leerArchivos(boolean esDesdeRepositorio) throws Exception {
		try{
			desdeRepositorio = esDesdeRepositorio;
			repositorio = (esDesdeRepositorio) ? repositorioIngresado : directorioLocalIngresado;
			if (!repositorio.equals("")){
				// Si no termina con '/' se la agrego
				int n = repositorio.length();
				String s = repositorio.substring(n - 1, n);
				if (!s.equals("/")){
					repositorio = repositorio + "/";
				}
				archivosDisponibles.clear();
				archivosDisponiblesLocal.clear();
				
				repositorio = iim.cargarArchivos(desdeRepositorio, repositorio, archivosDisponibles, archivosDisponiblesLocal);
				
				if ((desdeRepositorio && (archivosDisponibles.size() == 0)) || (!desdeRepositorio && (archivosDisponiblesLocal.size() == 0))){
					FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN, "", Constantes.MENSAJE_ARCHIVOS_NO_ENCONTRADOS);
		        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
				}
			}
			else{
				FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN, "", Constantes.MENSAJE_URL_NULL);
	        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
			}
		}
		catch (FileNotFoundException e){
		    System.out.println("No se encontró la URL: " + e.getMessage() + ".");
		    FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", Constantes.MENSAJE_URL_NO_ACCESIBLE + "'" + Constantes.URL_GITHUB + repositorioIngresado + "'.");
	        FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
		catch(Exception e){
			System.out.println("No se pudo conectar a la URL: " + e.getMessage() + ".");
		    FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", Constantes.MENSAJE_URL_NO_ACCESIBLE + "'" + Constantes.URL_GITHUB + repositorioIngresado + "'.");
	        FacesContext.getCurrentInstance().addMessage(null, mensaje);
			e.printStackTrace();
		}
	}

	public void cargarArchivo(){
		try{
			System.out.println("Comenzando la descarga...");
			nombreArchivo = (desdeRepositorio) ? nombreArchivoRepo : nombreArchivoLocal;
			if (!nombreArchivo.equals("")){
				Object[] res = cargarArchivoInicial(); // [String, TipoLibrary]
				String urlPlugin = ((res != null) && (res.length > 0)) ? (String) res[0] : null;
				TipoLibrary library = ((res != null) && (res.length > 1)) ? (TipoLibrary) res[1] : null;
				if (urlPlugin != null){
					// Cargo la configuración
					TipoMethodConfiguration methodConfiguration = cargarArchivoConfigurationRepositorio();
					
					String archivoPlugin = urlPlugin;
					// archivoPlugin puede ser de la forma: dir1/dir2/.../nombre
					int indexDiv = archivoPlugin.indexOf("/");
					String dirPlugin = "";
					String dir = "";
					while (indexDiv != -1){
						dir = archivoPlugin.substring(0, indexDiv);
						archivoPlugin = archivoPlugin.substring(indexDiv + 1, archivoPlugin.length());
						indexDiv = archivoPlugin.indexOf("/");
						dirPlugin += dir + "/";
					}
					
					try{
						cargarTodoDeRepositorio(dirPlugin);
						TipoPlugin plugin = iim.cargarDeRepositorio(desdeRepositorio, repositorio, dirPlugin, archivoPlugin, archivoPlugin);
						List<TipoMethodPackage> processPackages = iim.cargarProcessPackageRepositorio(dirPlugin + archivoPlugin);
						
						// Cargo los datos del method plugin 
						FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
						HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
						VistaBean vb = (VistaBean) session.getAttribute("VistaBean");
						vb.setLibrary(library);
						vb.setPlugin(plugin);
				        vb.setMethodConfiguration(methodConfiguration);
				        vb.setProcessPackages(processPackages);
				        vb.setDirPlugin(dirPlugin);
				        parsearDatosPlugin(dirPlugin, "", plugin, archivoPlugin);
				        
				        FacesMessage mensaje = new FacesMessage("", "El archivo ha sido cargado correctamente.");
			            FacesContext.getCurrentInstance().addMessage(null, mensaje);
						System.out.println("Fin de la descarga.");
					}
					catch (Exception e) {
						FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Ha ocurrido un error durante la carga de los archivos.");
				        FacesContext.getCurrentInstance().addMessage(null, mensaje);
				        System.out.println(e.getMessage());
					}
				}
				else{
					FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN, "", Constantes.MENSAJE_ARCHIVO_INCORRECTO);
			        FacesContext.getCurrentInstance().addMessage(null, mensaje);
					System.out.println(Constantes.MENSAJE_ARCHIVO_INCORRECTO);
				}
			}
			else{
				FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN, "", Constantes.MENSAJE_ARCHIVO_NULL);
		        FacesContext.getCurrentInstance().addMessage(null, mensaje);
				System.out.println(Constantes.MENSAJE_ARCHIVO_NULL);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cargarTodoDeRepositorio(String dirPlugin){
		try {
			iim.cargarTodoDeRepositorio(desdeRepositorio, repositorio, dirPlugin);
		}
		catch (ConnectionException e) {
			FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Falló la conexión con el repositorio.");
        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
			e.printStackTrace();
		}
		catch (Exception e) {
			FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Falló la carga de archivos.");
        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
			e.printStackTrace();
		}
	}

	public Object[] cargarArchivoInicial() {
		try{
			return iim.cargarArchivoInicial(desdeRepositorio, repositorio, nombreArchivo);
		}
		catch (MalformedURLException e) {
			FacesMessage mensaje = null;
			if (desdeRepositorio){
				mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Error al intentar acceder a la URL.");
			}
			else{
				mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Error al intentar acceder al directorio local.");
			}
			FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
		catch (FileNotFoundException e){
			FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "No se encontró el archivo '" + nombreArchivo + "'.");
        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
		catch (IOException e){
			FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Error al establecer la conexión con el repositorio.");
        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public TipoMethodConfiguration cargarArchivoConfigurationRepositorio(){
		try{
			TipoMethodConfiguration tmc = iim.cargarArchivoConfigurationRepositorio(desdeRepositorio, repositorio);
			if (tmc == null){
				FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN, "", "No se encontró el archivo de configuración.");
	        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
			}
			return tmc;
		}
		catch (FileNotFoundException e){
			FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "No se encontró el archivo de configuración.");
        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
		catch (IOException e){
			FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Error al establecer la conexión con el repositorio.");
        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void descargarArchivos(String dir, String archivo, String archivoFinal) throws Exception {
		try{
			iim.descargarArchivos(desdeRepositorio, repositorio, dir, archivo, archivoFinal);
		}
		catch (FileNotFoundException e){
			FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "No se encontró el archivo '" + archivo + "'.");
        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
		catch (IOException e){
			FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Error al establecer la conexión con el repositorio.");
        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void parsearDatosPlugin(String dirPlugin, String dirLineProcess, TipoPlugin plugin, String archivoPlugin) throws Exception{
		if (plugin != null){
			FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
			VistaBean vb = (VistaBean) session.getAttribute("VistaBean");
			
			Map<String, Object> resParser = iim.parsearDatosPlugin(desdeRepositorio, repositorio, dirPlugin, dirLineProcess, plugin, archivoPlugin, vb.getMethodConfiguration().getAddedCategory());
			if (resParser.containsKey("nombreArchivo")){
				nombreArchivo = (String) resParser.get("nombreArchivo");
			}
			if (resParser.containsKey("vbNombreArchivo")){
				vb.setNombreArchivo((String) resParser.get("vbNombreArchivo"));
			}
			if (resParser.containsKey("vbDirectorioArchivo")){
				vb.setDirectorioArchivo((String) resParser.get("vbDirectorioArchivo"));
			}
			if (resParser.containsKey("vbRepositorio")){
				vb.setRepositorio((String) resParser.get("vbRepositorio"));
			}
			if (resParser.containsKey("vbContentCategory")){
				vb.setContentCategory((TipoContentCategory) resParser.get("vbContentCategory"));
			}
			if (resParser.containsKey("vbCategorizedElements")){
				vb.setCategorizedElements((Map<String, TipoContentCategory>) resParser.get("vbCategorizedElements"));
			}
			if (resParser.containsKey("vbTemplates")){
				vb.setTemplates((Map<String, TipoContentElement>) resParser.get("vbTemplates"));
			}
			if (resParser.containsKey("vbContentPackages")){
				vb.setContentPackages((List<TipoContentPackage>) resParser.get("vbContentPackages"));
			}
			if (resParser.containsKey("vbRoles")){
				vb.setRoles((Map<String, List<TipoContentElement>>) resParser.get("vbRoles"));
			}
		}
		else{
			FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error al procesar los datos.");
	        FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
	}

	/*** EVENTOS ***/

	public void onTabChange(TabChangeEvent event) {
		nombreArchivo = "";
    }

}

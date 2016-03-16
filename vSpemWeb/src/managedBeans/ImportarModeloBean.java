package managedBeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import logica.XMIParser;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.UploadedFile;

import config.Constantes;
import dataTypes.TipoContentCategory;
import dataTypes.TipoContentDescription;
import dataTypes.TipoContentPackage;
import dataTypes.TipoLibrary;
import dataTypes.TipoMethodConfiguration;
import dataTypes.TipoMethodPackage;
import dataTypes.TipoPlugin;
import dataTypes.TipoTask;

@ManagedBean
@ViewScoped
public class ImportarModeloBean {

	private String mensajeAyudaRepositorio = Constantes.mensjaeAyudaRepositorio;
	private String repositorioIngresado = Constantes.URL_GITHUB_DEFAULT;
	private String directorioLocalIngresado = Constantes.DIRECTORIO_LOCAL_DEFAULT;
	private Boolean desdeRepositorio = true;
	private String repositorio = "";
	private String nombreArchivo = "";
	private List<String> archivosDisponibles = new ArrayList<String>();

	@PostConstruct
	public void init(){
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

	public List<String> getArchivosDisponibles() {
		return archivosDisponibles;
	}

	public void setArchivosDisponibles(List<String> archivosDisponibles) {
		this.archivosDisponibles = archivosDisponibles;
	}

	/*** CARGA REMOTA DE ARCHIVOS ***/

	public void leerArchivos(boolean esDesdeRepositorio) throws Exception {
		try{
			init();
			if (!repositorioIngresado.equals("")){
				desdeRepositorio = esDesdeRepositorio;
				repositorio = (esDesdeRepositorio) ? repositorioIngresado : directorioLocalIngresado;
				// Si no termina con '/' se la agrego
				int n = repositorio.length();
				String s = repositorio.substring(n - 1, n);
				if (!s.equals("/")){
					repositorio = repositorio + "/";
				}
				archivosDisponibles.clear();
				
				// Carga desde el repositorio
				if (desdeRepositorio){
					// Si en la url del repositorio existe el string "tree" => Lo sustituyo por "blob".
					int index = repositorio.indexOf("tree");
					if (index != -1){
						repositorio = repositorio.replace("tree", "blob");
					}
					System.out.println("Carga: " + Constantes.URL_GITHUB + repositorio);
					
					URL url = new URL(Constantes.URL_GITHUB + repositorio);
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					
					String linea;
					while ((linea = in.readLine()) != null){
						// <a href="/repositorio/.../nomArchivo"
						String strBuscado = "<a href=\"/" + repositorio;
						int indexIni = linea.indexOf(strBuscado);
						if (indexIni != -1){
							String archivo = linea.substring(indexIni + strBuscado.length());
							archivo = archivo.substring(0, archivo.indexOf("\""));
							int indexExtension = archivo.indexOf(".");
							if (indexExtension != -1){
								String nomArchivo = archivo.substring(0, indexExtension);
								String extArchivo = archivo.substring(indexExtension + 1, archivo.length());
								// Solo cargo archivos xmi
								if (extArchivo.equals("xmi")){
									// nomArchivo puede ser de la forma: dir1/dir2/.../nombre
									int indexDiv = nomArchivo.indexOf("/");
									while (indexDiv != -1){
										String dir = nomArchivo.substring(0, indexDiv);
										nomArchivo = nomArchivo.substring(indexDiv + 1, nomArchivo.length());
										indexDiv = nomArchivo.indexOf("/");
										repositorio += dir + "/";
									}
									archivosDisponibles.add(nomArchivo + "." + extArchivo);
								}
							}
						}
					}
					
					in.close();
				}
				// Carga desde directorio local
				else{
					File directorio = new File(repositorio);
					if (directorio.exists()){
						File[] archivos = directorio.listFiles();
						int nArchivos = archivos.length;
						for (int i = 0; i < nArchivos; i++){
							String archivo = archivos[i].getName();
							int indexExtension = archivo.indexOf(".");
							if (indexExtension != -1){
								String nomArchivo = archivo.substring(0, indexExtension);
								String extArchivo = archivo.substring(indexExtension + 1, archivo.length());
								// Solo cargo archivos xmi
								if (extArchivo.equals("xmi")){
									archivosDisponibles.add(nomArchivo + "." + extArchivo);
								}
							}
						}
					}
				}
				
				if (archivosDisponibles.size() == 0){
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
			System.out.println(e.getMessage());
		}
	}

	public void cargarArchivo(){
		try{
			if (!nombreArchivo.equals("")){
				Object[] res = cargarArchivoInicialRepositorio(); // [String, TipoLibrary]
				String urlPlugin = (String) res[0];
				TipoLibrary library = (TipoLibrary) res[1];
				if (urlPlugin != null){
					// Cargo la configuración
					TipoMethodConfiguration methodConfiguration = cargarArchivoConfigurationRepositorio();
					
					String archivoPlugin = urlPlugin;
					// archivoPlugin puede ser de la forma: dir1/dir2/.../nombre
					int indexDiv = archivoPlugin.indexOf("/");
					String dirPlugin = "";
					while (indexDiv != -1){
						String dir = archivoPlugin.substring(0, indexDiv);
						archivoPlugin = archivoPlugin.substring(indexDiv + 1, archivoPlugin.length());
						indexDiv = archivoPlugin.indexOf("/");
						dirPlugin += dir + "/";
					}
					TipoPlugin plugin = cargarDeRepositorio(dirPlugin, archivoPlugin, archivoPlugin);
					List<TipoMethodPackage> processPackages = cargarProcessPackageRepositorio(archivoPlugin);
					
					// Cargo los datos del method plugin 
					FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
					HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
					VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
					vb.setLibrary(library);
					vb.setPlugin(plugin);
			        vb.setMethodConfiguration(methodConfiguration);
			        vb.setProcessPackages(processPackages);
			        parsearDatosPlugin(dirPlugin, "", plugin, archivoPlugin);
			        
			        FacesMessage mensaje = new FacesMessage("", "El archivo ha sido cargado correctamente.");
		            FacesContext.getCurrentInstance().addMessage(null, mensaje);
				}
			}
			else{
				FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN, "", Constantes.MENSAJE_ARCHIVO_NULL);
		        FacesContext.getCurrentInstance().addMessage(null, mensaje);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object[] cargarArchivoInicialRepositorio() {
		String archivoExport = nombreArchivo;
		
		// Si en la url del repositorio existe el string "blob/" => Lo sustituyo por "", sino, le agrego el string "master/"
		int index = repositorio.indexOf("blob/");
		String urlDescargar = (index != -1) ? repositorio.replace("blob/", "") : repositorio.concat("master/");
		System.out.println("Descarga: " + Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + archivoExport);
		
		try{
			URL url = new URL(Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + archivoExport);
			URLConnection urlCon = url.openConnection();
			
			InputStream is = urlCon.getInputStream();
			FileOutputStream fos = new FileOutputStream(Constantes.destinoDescargas + archivoExport);
			byte [] array = new byte[1000];
			int leido = is.read(array);
			while (leido > 0) {
			   fos.write(array, 0, leido);
			   leido = is.read(array);
			}
			is.close();
			fos.close();
			
			return XMIParser.getElementsXMIResource(Constantes.destinoDescargas + archivoExport); // [String, TipoLibrary]
		}
		catch (FileNotFoundException e){
			FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "No se encontró el archivo '" + archivoExport + "'.");
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
		List<String> archivosXML = new ArrayList<String>(); 
		try{
			// Si en la url del repositorio existe el string "blob/" => Lo sustituyo por "", sino, le agrego el string "master/"
			int index = repositorio.indexOf("blob/");
			URL url = new URL(Constantes.URL_GITHUB + repositorio + "configurations/");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			
			String linea;
			while ((linea = in.readLine()) != null){
				// <a href="/repositorio/.../nomArchivo"
				String strBuscado = "<a href=\"/" + repositorio + "configurations/";
				int indexIni = linea.indexOf(strBuscado);
				if (indexIni != -1){
					String archivo = linea.substring(indexIni + strBuscado.length());
					archivo = archivo.substring(0, archivo.indexOf("\""));
					int indexExtension = archivo.indexOf(".");
					if (indexExtension != -1){
						String nomArchivo = archivo.substring(0, indexExtension);
						String extArchivo = archivo.substring(indexExtension + 1, archivo.length());
						// Solo cargo archivos xmi
						if (extArchivo.equals("xmi")){
							// nomArchivo puede ser de la forma: dir1/dir2/.../nombre
							int indexDiv = nomArchivo.indexOf("/");
							while (indexDiv != -1){
								nomArchivo = nomArchivo.substring(indexDiv + 1, nomArchivo.length());
								indexDiv = nomArchivo.indexOf("/");
							}
							archivosXML.add(nomArchivo + "." + extArchivo);
						}
					}
				}
			}
			
			in.close();
			
			if (archivosXML.size() == 0){
				FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN, "", "No se encontró el archivo de configuración.");
	        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
			}
			else{
				// Si lo encontré, lo parseo. Asumo que hay un único achivo dentro de la carpeta 'configurations'.
				String archivoConfig = archivosXML.get(0);
				String urlDescargar = (index != -1) ? repositorio.replace("blob/", "") : repositorio.concat("master/");
				System.out.println("Descarga: " + Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + "configurations/" + archivoConfig);
				
				try{
					url = new URL(Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + "configurations/" + archivoConfig);
					URLConnection urlCon = url.openConnection();
					
					InputStream is = urlCon.getInputStream();
					FileOutputStream fos = new FileOutputStream(Constantes.destinoDescargas + archivoConfig);
					byte [] array = new byte[1000];
					int leido = is.read(array);
					while (leido > 0) {
					   fos.write(array, 0, leido);
					   leido = is.read(array);
					}
					is.close();
					fos.close();
					
					return XMIParser.getElementsXMIConfigurations(Constantes.destinoDescargas + archivoConfig); // [String, TipoLibrary]
				}
				catch (FileNotFoundException e){
					FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "No se encontró el archivo '" + archivoConfig + "'.");
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public TipoPlugin cargarDeRepositorio(String dir, String archivo, String archivoFinal) throws Exception {
		// Si en la url del repositorio existe el string "blob/" => Lo sustituyo por "", sino, le agrego el string "master/"
		int index = repositorio.indexOf("blob/");
		String urlDescargar = (index != -1) ? repositorio.replace("blob/", "") : repositorio.concat("master/");
		System.out.println("Descarga: " + Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + dir + archivo);
		
		try{
			URL url = new URL(Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + dir + archivo);
			URLConnection urlCon = url.openConnection();
			
			InputStream is = urlCon.getInputStream();
			FileOutputStream fos = new FileOutputStream(Constantes.destinoDescargas + archivoFinal);
			byte [] array = new byte[1000];
			int leido = is.read(array);
			while (leido > 0) {
			   fos.write(array, 0, leido);
			   leido = is.read(array);
			}
			is.close();
			fos.close();
			
			return XMIParser.getElementsXMIPlugin(Constantes.destinoDescargas + archivoFinal);
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
		return null;
	}

	public List<TipoMethodPackage> cargarProcessPackageRepositorio(String archivoPlugin){
		File f = new File(Constantes.destinoDescargas + archivoPlugin);
		if (f.isFile()){
			return XMIParser.getElementsXMIProcessPackage(Constantes.destinoDescargas + archivoPlugin);	
		}
		return null;		
	}

	public void parsearDatosPlugin(String dirPlugin, String dirLineProcess, TipoPlugin plugin, String archivoPlugin) throws Exception{
		if (plugin != null){
			int indexDiv = 0;

			FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
			VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
			
			// Parseo la línea de proceso
			String lineProcessDir = plugin.getLineProcessDir();
			if (lineProcessDir != null){
				String[] dirRes = separarDireccion(lineProcessDir);
				String dirLP = dirRes[0];
				String archivoLP = dirRes[1];
				TipoPlugin modelLineProcess = cargarDeRepositorio(dirPlugin + dirLP, archivoLP, archivoLP);
				parsearDatosPlugin(dirPlugin, dirLP, modelLineProcess, archivoLP);
			}
			
			// Parseo el delieryProcess
			String deliveryProcessDir = plugin.getDeliveryProcessDir();
			if (deliveryProcessDir != null){
				String[] dirRes = separarDireccion(deliveryProcessDir);
				String dirDeliveryProcess = dirRes[0];
				String archivoDP = dirRes[1];
				nombreArchivo = archivoDP;
		        vb.setNombreArchivo(archivoDP);
				cargarDeRepositorio(dirPlugin + dirLineProcess + dirDeliveryProcess, archivoDP, archivoDP);// Cargo los Capability Patterns
				// Para que se seteen todos los hijos
				XMIParser.getElementXMI(Constantes.destinoDescargas + archivoDP);
		        vb.setRepositorio(repositorio);
			}
			
			// Parseo los capabilityPatterns
			List<String> capabilityPatternsDir = plugin.getCapabilityPatternsDir();
			Iterator<String> itCP = capabilityPatternsDir.iterator();
			while (itCP.hasNext()){
				String archivoCP = itCP.next();
				// itCP.next() puede ser de la forma: dir1/dir2/.../nombre
				indexDiv = archivoCP.indexOf("/");
				String dirCapabilityPattern = "";
				String nameCapabilityPattern = "";
				while (indexDiv != -1){
					nameCapabilityPattern = archivoCP.substring(0, indexDiv);
					archivoCP = archivoCP.substring(indexDiv + 1, archivoCP.length());
					indexDiv = archivoCP.indexOf("/");
					dirCapabilityPattern += nameCapabilityPattern + "/";
				}
				String archivoCPFinal = archivoCP.substring(0, nombreArchivo.length() - 4)  + "_" + nameCapabilityPattern.replace("%20", "_") + ".xmi";
				cargarDeRepositorio(dirPlugin + dirLineProcess + dirCapabilityPattern, archivoCP, archivoCPFinal);
			}
			
			// Parseo las customCategories
			String customCategoriesDir = plugin.getCustomCategoriesDir();
			if (customCategoriesDir != null){
				String[] dirRes = separarDireccion(customCategoriesDir);
				String dirCustomCategories = dirRes[0];
				String archivoCC = dirRes[1];
				cargarDeRepositorio(dirPlugin + dirLineProcess + dirCustomCategories, archivoCC, archivoCC);
				TipoContentCategory contentCategory = cargarCustomCategoriesRepositorio(archivoCC, archivoPlugin);
				
		        vb.setContentCategory(contentCategory);
				
				if (contentCategory != null){
					Map<String, TipoContentCategory> categorizedElements = cargarCategorizedElementsRepositorio(archivoPlugin, contentCategory.getCategorizedElements());
			        vb.setCategorizedElements(categorizedElements);
				}
			}
			
			// Parseo las tasks
			List<String> tasksDir = plugin.getTasksDir();
			Iterator<String> itTasks = tasksDir.iterator();
			Map<String, TipoTask> lstTask = new HashMap<String, TipoTask>();
			while (itTasks.hasNext()){
				String taskDir = itTasks.next();
				String[] dirRes = separarDireccion(taskDir);
				String dirTask = dirRes[0];
				String archivoTask = dirRes[1];
				cargarDeRepositorio(dirPlugin + dirLineProcess + dirTask, archivoTask, "task_" + archivoTask);
				TipoTask task = cargarTasksRepositorio("task_" + archivoTask);
				if (task != null){
					lstTask.put(task.getId(), task);
				}
			}
			vb.setTasks(lstTask);

			List<TipoContentPackage> contentPackages = new ArrayList<TipoContentPackage>();
			List<String> cpAgregados = new ArrayList<String>();
			Iterator<Entry<String, TipoTask>> itTask = lstTask.entrySet().iterator();
			while (itTask.hasNext()){
				TipoTask task = itTask.next().getValue();
				String idTask = task.getId();
				TipoContentCategory contentElement = XMIParser.getElementsXMIPadreTipoId(Constantes.destinoDescargas + archivoPlugin, "contentElements", idTask);
				if (contentElement != null){
					TipoContentCategory childPackage = XMIParser.getElementsXMIPadreTipoId(Constantes.destinoDescargas + archivoPlugin, "childPackages", contentElement.getId());
					if ((childPackage != null) && (!cpAgregados.contains(childPackage.getId()))){
						cpAgregados.add(childPackage.getId());
						/*contentPackages.add(childPackage);
						tasksCP.add(task);*/
						TipoContentPackage tcp = new TipoContentPackage();
						tcp.setContentPackages(childPackage);
						tcp.getTasksCP().add(task);
						contentPackages.add(tcp);
					}
					else{
						int i = 0;
						int n = contentPackages.size();
						boolean fin = false;
						while ((i < n) && (!fin)){
							TipoContentPackage tcp = contentPackages.get(i);
							if (tcp.getContentPackages().getId().equals(childPackage.getId())){
								tcp.getTasksCP().add(task);
								fin = true;
							}
							i++;
						}
					}
				}
			}
			vb.setContentPackages(contentPackages);
			// Parseo los workproducts
			// Parseo las guidances
		}
		else{
			/*****************/
			/*FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", Constantes.MENSAJE_URL_NO_ACCESIBLE + "'" + Constantes.URL_GITHUB + repositorioIngresado + "'.");
	        FacesContext.getCurrentInstance().addMessage(null, mensaje);*/
			/*****************/
		}
	}

	public String[] separarDireccion(String archivo){
		// archivo puede ser de la forma: dir1/dir2/.../nombre
		int indexDiv = archivo.indexOf("/");
		String dirArchivo = "";
		while (indexDiv != -1){
			String dir = archivo.substring(0, indexDiv);
			archivo = archivo.substring(indexDiv + 1, archivo.length());
			dirArchivo += dir + "/";
			indexDiv = archivo.indexOf("/");
		}
		String[] res = {dirArchivo, archivo};
		return res;
	}

	public TipoContentCategory cargarCustomCategoriesRepositorio(String archivoCC, String archivoPlugin){
		TipoContentDescription contentDescription = XMIParser.getElementsXMICustomCategories(Constantes.destinoDescargas + archivoCC);
		TipoContentCategory contentCategory = XMIParser.getElementsXMIPadreTipoId(Constantes.destinoDescargas + archivoPlugin, "contentElements", contentDescription.getId());
		contentCategory.setContentDescription(contentDescription);
		return contentCategory;
	}
	
	public Map<String, TipoContentCategory> cargarCategorizedElementsRepositorio(String archivoPlugin, String categorizedElements){
		File f = new File(Constantes.destinoDescargas + archivoPlugin);
		if (f.isFile()){
			String[] categorizedElementsArray = categorizedElements.split(" ");
			return XMIParser.getElementsXMICategorizedElements(Constantes.destinoDescargas + archivoPlugin, categorizedElementsArray);	
		}
		return null;
	}
	
	public TipoTask cargarTasksRepositorio(String archivoTask){
		File f = new File(Constantes.destinoDescargas + archivoTask);
		if (f.isFile()){
			return XMIParser.getElementsXMITasks(Constantes.destinoDescargas + archivoTask);	
		}
		return null;
	}

	/*** CARGA LOCAL DE ARCHIVOS ***/

	public void cargarArchivoLocal(FileUploadEvent event) {
		UploadedFile archivo = event.getFile();
        if (archivo != null) {
    		nombreArchivo = archivo.getFileName();
            FacesMessage mensaje = new FacesMessage("", "El archivo " + archivo.getFileName() + " ha sido cargado correctamente.");
            FacesContext.getCurrentInstance().addMessage(null, mensaje);
            try {
            	copiarArchivoLocal(archivo.getFileName(), archivo.getInputstream());
                
                FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        		VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
                vb.setNombreArchivo(nombreArchivo);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	public void copiarArchivoLocal(String nombreArchivo, InputStream in) {
		try {
			OutputStream out = new FileOutputStream(new File(Constantes.destinoDescargas + nombreArchivo));
			int leer = 0;
			byte[] bytes = new byte[1024];
			while ((leer = in.read(bytes)) != -1) {
				out.write(bytes, 0, leer);
			}
			in.close();
			out.flush();
			out.close();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void cargarCapabilityPatternsLocal(FileUploadEvent event){
		UploadedFile archivo = event.getFile();
        if (archivo != null) {
    		nombreArchivo = archivo.getFileName();
            FacesMessage mensaje = new FacesMessage("", "El archivo " + archivo.getFileName() + " ha sido cargado correctamente.");
            FacesContext.getCurrentInstance().addMessage(null, mensaje);
            try {
            	copiarArchivoLocal(archivo.getFileName(), archivo.getInputstream());
                
                FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        		VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
                
                String nombreProceso = XMIParser.getNombreProcesoXMI(Constantes.destinoDescargas + nombreArchivo);
                vb.addCapabilityPattern(nombreProceso);
                
                File fichero = new File(Constantes.destinoDescargas + nombreArchivo);
                if (!fichero.exists()) {
                    System.out.println("Error: El archivo \"" + Constantes.destinoDescargas + nombreArchivo + "\" no existe.");
                    return;
                }
                
                String nombreDP = vb.getNombreArchivo().substring(0, vb.getNombreArchivo().length() - 4);
                String nombreCP = nombreProceso.replace(" ", "_");
                File fichero2 = new File(Constantes.destinoDescargas + nombreDP + "_" + nombreCP + ".xmi");
                
                boolean success = fichero.renameTo(fichero2);
                if (!success) {
                    System.out.println("Error intentando cambiar el nombre del archivo.");
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

	/*** EVENTOS ***/

	public void onTabChange(TabChangeEvent event) {
		nombreArchivo = "";
		init();
    }

}

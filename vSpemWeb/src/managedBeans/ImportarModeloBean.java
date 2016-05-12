package managedBeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
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
import dataTypes.TipoContentElement;
import dataTypes.TipoContentPackage;
import dataTypes.TipoElemento;
import dataTypes.TipoLibrary;
import dataTypes.TipoMethodConfiguration;
import dataTypes.TipoMethodPackage;
import dataTypes.TipoPlugin;
import dataTypes.TipoTag;
import dataTypes.TipoView;

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
			System.out.println("No se pudo conectar a la URL: " + e.getMessage() + ".");
		    FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", Constantes.MENSAJE_URL_NO_ACCESIBLE + "'" + Constantes.URL_GITHUB + repositorioIngresado + "'.");
	        FacesContext.getCurrentInstance().addMessage(null, mensaje);
			e.printStackTrace();
		}
	}

	public void cargarArchivo(){
		try{
			System.out.println("Comenzando la descarga...");
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
					String dir = "";
					while (indexDiv != -1){
						dir = archivoPlugin.substring(0, indexDiv);
						archivoPlugin = archivoPlugin.substring(indexDiv + 1, archivoPlugin.length());
						indexDiv = archivoPlugin.indexOf("/");
						dirPlugin += dir + "/";
					}
					
					cargarTodoDeRepositorio(dirPlugin);
					TipoPlugin plugin = cargarDeRepositorio(dirPlugin, archivoPlugin, archivoPlugin);
					List<TipoMethodPackage> processPackages = cargarProcessPackageRepositorio(dirPlugin + archivoPlugin);
					
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
					System.out.println("Fin de la descarga.");
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
			URL url = new URL(Constantes.URL_GITHUB + repositorio + dirPlugin.replace(" ", "%20"));
			
			// Listo los directorios
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String linea;
			while ((linea = in.readLine()) != null){
				String dir = repositorio;
				int index = dir.indexOf("blob");
				if (index != -1){
					dir = dir.replace("blob", "tree");
				}
				dir = dir + dirPlugin;
				// <a href="/repositorio/.../nomArchivo"
				String strBuscado = "<a href=\"/" + dir;
				int indexIni = linea.indexOf(strBuscado);
				if (indexIni != -1){
					String archivo = linea.substring(indexIni + strBuscado.length());
					archivo = archivo.substring(0, archivo.indexOf("\"")).replace("%20", " ");
					File f = new File(Constantes.destinoDescargas + dirPlugin + archivo);
					f.mkdirs();
					cargarTodoDeRepositorio(dirPlugin + archivo + "/");
				}
			}
			in.close();
			
			// Listo los archivos
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			while ((linea = in.readLine()) != null){
				// <a href="/repositorio/.../nomArchivo"
				String strBuscado = "<a href=\"/" + repositorio + dirPlugin.replace(" ", "%20");
				int indexIni = linea.indexOf(strBuscado);
				if (indexIni != -1){
					String archivo = linea.substring(indexIni + strBuscado.length());
					archivo = archivo.substring(0, archivo.indexOf("\"")).replace("%20", " ");
					descargarArchivos(dirPlugin, archivo, archivo);
				}
			}
			
			in.close();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Object[] cargarArchivoInicialRepositorio() {
		String archivoExport = nombreArchivo;
		
		// Si en la url del repositorio existe el string "blob/" => Lo sustituyo por "", sino, le agrego el string "master/"
		int index = repositorio.indexOf("blob/");
		String urlDescargar = (index != -1) ? repositorio.replace("blob/", "") : repositorio.concat("master/");
		//System.out.println("Descarga: " + Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + archivoExport);
		
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
				//System.out.println("Descarga: " + Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + "configurations/" + archivoConfig);
				
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
					
					return XMIParser.getElementsXMIConfigurations(Constantes.destinoDescargas + archivoConfig);
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

	public void descargarArchivos(String dir, String archivo, String archivoFinal) throws Exception {
		int index = repositorio.indexOf("blob/");
		String urlDescargar = (index != -1) ? repositorio.replace("blob/", "") : repositorio.concat("master/");
		//System.out.println("Descarga: " + Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + dir.replace(" ", "%20") + archivo);
		
		try{
			URL url = new URL(Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + dir.replace(" ", "%20") + archivo);
			URLConnection urlCon = url.openConnection();
			
			InputStream is = urlCon.getInputStream();
			dir = dir.replace("%20", " ");
			File directorio = new File(Constantes.destinoDescargas + dir);
			directorio.mkdirs();
			FileOutputStream fos = new FileOutputStream(directorio + "/" + archivoFinal);
			byte [] array = new byte[1000];
			int leido = is.read(array);
			while (leido > 0) {
			   fos.write(array, 0, leido);
			   leido = is.read(array);
			}
			is.close();
			fos.close();
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
	
	public TipoPlugin cargarDeRepositorio(String dir, String archivo, String archivoFinal){
		try{
			File f = new File(Constantes.destinoDescargas + dir.replace("%20", " ") + "/" + archivoFinal);
			if (!f.isFile()){
				descargarArchivos(dir, archivo, archivoFinal);
			}
			dir = dir.replace("%20", " ");
			File directorio = new File(Constantes.destinoDescargas + dir);
			return XMIParser.getElementsXMIPlugin(directorio + "/" + archivoFinal);
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
				parsearDatosPlugin(dirPlugin, dirLP, modelLineProcess, dirLP + archivoLP);
			}
			
			// Parseo el delieryProcess
			String deliveryProcessDir = plugin.getDeliveryProcessDir();
			if (deliveryProcessDir != null){
				String[] dirRes = separarDireccion(deliveryProcessDir);
				String dirDeliveryProcess = dirRes[0];
				String archivoDP = dirRes[1];
				nombreArchivo = archivoDP;
		        vb.setNombreArchivo(archivoDP);
		        vb.setDirectorioArchivo(dirPlugin + dirLineProcess);
				cargarDeRepositorio(dirPlugin + dirLineProcess + dirDeliveryProcess, archivoDP, archivoDP);// Cargo los Capability Patterns
				// Para que se seteen todos los hijos
				XMIParser.getElementXMI(Constantes.destinoDescargas + dirPlugin + dirLineProcess + dirDeliveryProcess + archivoDP);
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
				cargarDeRepositorio(dirPlugin + dirLineProcess + dirCapabilityPattern, archivoCP, archivoCP);
			}
			
			// Parseo las customCategories
			String customCategoriesDir = plugin.getCustomCategoriesDir();
			if (customCategoriesDir != null){
				String[] dirRes = separarDireccion(customCategoriesDir);
				String dirCustomCategories = dirRes[0];
				String archivoCC = dirRes[1];
				cargarDeRepositorio(dirPlugin + dirCustomCategories, archivoCC, archivoCC);
				TipoContentCategory contentCategory = cargarCustomCategoriesRepositorio(dirPlugin, dirCustomCategories, archivoCC, archivoPlugin);
		        vb.setContentCategory(contentCategory);
				if (contentCategory != null){
					String elements = "";
					List<TipoView> categories = vb.getMethodConfiguration().getAddedCategory();
					if (categories != null){
						Iterator<TipoView> it = categories.iterator();
						while (it.hasNext()){
							TipoView category = it.next();
							String href = category.getHref(); // href="uma://_HtQ6kOMfEd6OoK0l17K4LA#_X38tkOMsEd6OoK0l17K4LA"
							if ((href != null) && (!href.equals(""))){
								String[] elems = href.split("#");
								if (elems.length > 1){
									elements += elems[1] + " ";
								}
							}
						}
					}
					
					String[] categorizedElems = contentCategory.getCategorizedElements().split(" ");
					int n = categorizedElems.length;
					for (int i = 0; i < n; i++){
						String elem = categorizedElems[i];
						if (!elements.contains(elem)){
							elements += elem + " ";
						}
					}
					
					Map<String, TipoContentCategory> categorizedElements = cargarCategorizedElementsRepositorio(dirPlugin, archivoPlugin, elements);
			        vb.setCategorizedElements(categorizedElements);
				}
			}
			
			// Parseo las tasks
			Map<String, TipoContentElement> lstTask = new HashMap<String, TipoContentElement>();
			List<TipoContentElement> tceTasks = XMIParser.getElementsXMIPlugin(Constantes.destinoDescargas + dirPlugin + archivoPlugin, TipoTag.TASK.toString());
			Iterator<TipoContentElement> itTceTasks = tceTasks.iterator();
			while (itTceTasks.hasNext()){
				TipoContentElement tce = itTceTasks.next();
				tce.setTipoElemento(TipoElemento.TASK);
				lstTask.put(tce.getId(), tce);
			}
			List<String> tasksDir = plugin.getTasksDir();
			Iterator<String> itTasks = tasksDir.iterator();
			while (itTasks.hasNext()){
				String taskDir = itTasks.next();
				String[] dirRes = separarDireccion(taskDir);
				String dirTask = dirRes[0];
				String archivoTask = dirRes[1];
				cargarDeRepositorio(dirPlugin + dirTask, archivoTask, archivoTask);
				TipoContentElement task = cargarContentElementsRepositorio(dirPlugin + dirTask, archivoTask, TipoTag.TASK_DESCRIPTION.toString());
				if (task != null){
					String taskName = task.getName();
					String[] res = taskName.split(",");
					String taskId = res.length > 1 ? res[1] : "";
					if (!taskId.equals("")){
						TipoContentElement wp = lstTask.get(taskId);
						wp.setContentDescription(task);
					}
				}
			}
			
			// Parseo los workproducts
			Map<String, TipoContentElement> lstWorkproduct = new HashMap<String, TipoContentElement>();
			List<TipoContentElement> lstWP = XMIParser.getElementsXMIPlugin(Constantes.destinoDescargas + dirPlugin + archivoPlugin, TipoTag.ARTIFACT.toString());
			Iterator<TipoContentElement> itWP = lstWP.iterator();
			while (itWP.hasNext()){
				TipoContentElement wp = itWP.next();
				wp.setTipoElemento(TipoElemento.WORK_PRODUCT);
				lstWorkproduct.put(wp.getId(), wp);
			}
			List<String> workproductsDir = plugin.getWorkproductsDir();
			Iterator<String> itWorkproducts = workproductsDir.iterator();
			while (itWorkproducts.hasNext()){
				String workproductDir = itWorkproducts.next();
				String[] dirRes = separarDireccion(workproductDir);
				String dirWorkproduct = dirRes[0];
				String archivoWorkproduct = dirRes[1];
				cargarDeRepositorio(dirPlugin + dirWorkproduct, archivoWorkproduct, archivoWorkproduct);
				TipoContentElement workproduct = cargarContentElementsRepositorio(dirPlugin + dirWorkproduct, archivoWorkproduct, TipoTag.ARTIFACT_DESCRIPTION.toString());
				if (workproduct != null){
					String workproductName = workproduct.getName();
					String[] res = workproductName.split(",");
					String workproductId = res.length > 1 ? res[1] : "";
					if (!workproductId.equals("")){
						TipoContentElement wp = lstWorkproduct.get(workproductId);
						wp.setContentDescription(workproduct);
					}
				}
			}
			
			// Parseo las guidances
			List<String> guidancesDir = plugin.getGuidancesDir();
			Iterator<String> itGuidances = guidancesDir.iterator();
			Map<String, TipoContentElement> lstGuidances = new HashMap<String, TipoContentElement>();
			while (itGuidances.hasNext()){
				String guidanceDir = itGuidances.next();
				String[] dirRes = separarDireccion(guidanceDir);
				String dirGuidance = dirRes[0];
				String archivoGuidance = dirRes[1];
				cargarDeRepositorio(dirPlugin + dirGuidance, archivoGuidance, archivoGuidance);
				String tag = (dirGuidance.contains("supportingmaterials")) ? TipoTag.SUPPORTING_MATERIAL_DESCRIPTION.toString() : TipoTag.GUIDANCE_DESCRIPTION.toString();
				TipoContentElement guidance = cargarContentElementsRepositorio(dirPlugin + dirGuidance, archivoGuidance, tag);
				if (guidance != null){
					String guidanceName = guidance.getName();
					String[] res = guidanceName.split(",");
					String guidanceId = res.length > 1 ? res[1] : "";
					lstGuidances.put(guidanceId, guidance);
				}
			}
			List<TipoContentElement> lstTemplates = cargarTemplateRepositorio(dirPlugin + archivoPlugin, TipoTag.GUIDANCE.toString());
			List<TipoContentElement> lstTemplates2 = cargarTemplateRepositorio(dirPlugin + archivoPlugin, TipoTag.SUPPORTING_MATERIAL.toString());
			lstTemplates.addAll(lstTemplates2);
			Map<String, TipoContentElement> mapTemplates = new HashMap<String, TipoContentElement>();
			Iterator<TipoContentElement> itTceTemplates = lstTemplates.iterator();
			while (itTceTemplates.hasNext()){
				TipoContentElement tce = itTceTemplates.next();
				String idTce = tce.getId();
				TipoContentElement guidance = lstGuidances.get(idTce);
				tce.setGuidance(guidance);
				mapTemplates.put(idTce, tce);
			}
			vb.setTemplates(mapTemplates);
			
			// Cargo en el contentPackage
			Map<String, TipoContentElement> mapRoles = new HashMap<String, TipoContentElement>();
			List<TipoContentElement> lstRoles = XMIParser.getElementsXMIPlugin(Constantes.destinoDescargas + dirPlugin + archivoPlugin, TipoTag.ROLE.toString());
			Iterator<TipoContentElement> itRoles = lstRoles.iterator();
			while (itRoles.hasNext()){
				TipoContentElement rol = itRoles.next();
				rol.setTipoElemento(TipoElemento.ROLE);
				mapRoles.put(rol.getId(), rol);
			}
			Map<String, List<TipoContentElement>> mapCCRol = new HashMap<String, List<TipoContentElement>>();
			List<TipoContentPackage> contentPackages = new ArrayList<TipoContentPackage>();
			Map<String, TipoContentElement> lstCE = new HashMap<String, TipoContentElement>();
			lstCE.putAll(lstTask);
			lstCE.putAll(lstWorkproduct);
			lstCE.putAll(mapTemplates);
			lstCE.putAll(mapRoles);
			List<String> cpAgregados = new ArrayList<String>();
			Iterator<Entry<String, TipoContentElement>> itCE = lstCE.entrySet().iterator();
			while (itCE.hasNext()){
				Entry<String, TipoContentElement> entry = itCE.next();
				TipoContentElement tce = entry.getValue();
				String idTce = entry.getKey();
				TipoContentCategory contentElement = XMIParser.getElementsXMITipoId(dirPlugin, Constantes.destinoDescargas + dirPlugin + archivoPlugin, "contentElements", idTce, false);
				if (contentElement != null){
					TipoContentCategory childPackage = XMIParser.getElementsXMITipoId(dirPlugin, Constantes.destinoDescargas + dirPlugin + archivoPlugin, "childPackages", contentElement.getId(), true);
					if (childPackage != null){
						if (!cpAgregados.contains(childPackage.getId())){
							cpAgregados.add(childPackage.getId());
							TipoContentPackage tcp = new TipoContentPackage();
							tcp.setContentPackages(childPackage);
							
							if (tce.getTipoElemento() == TipoElemento.WORK_PRODUCT){
								tcp.getWorkproductsCP().add(tce);
							}
							else if ((tce.getTipoElemento() == TipoElemento.GUIDANCE) || (tce.getTipoElemento() == TipoElemento.SUPPORTING_MATERIAL)){
								tcp.getGuidancesCP().add(tce);
							}
							else if (tce.getTipoElemento() == TipoElemento.TASK){
								tcp.getTasksCP().add(tce);
							}
							contentPackages.add(tcp);
						}
						else{
							int i = 0;
							int n = contentPackages.size();
							boolean fin = false;
							while ((i < n) && (!fin)){
								TipoContentPackage tcp = contentPackages.get(i);
								if (tcp.getContentPackages().getId().equals(childPackage.getId())){
									if (tce.getTipoElemento() == TipoElemento.WORK_PRODUCT){
										tcp.getWorkproductsCP().add(tce);
									}
									else if ((tce.getTipoElemento() == TipoElemento.GUIDANCE) || (tce.getTipoElemento() == TipoElemento.SUPPORTING_MATERIAL)){
										tcp.getGuidancesCP().add(tce);
									}
									else if (tce.getTipoElemento() == TipoElemento.TASK){
										tcp.getTasksCP().add(tce);
									}
									fin = true;
								}
								i++;
							}
						}
						
						if (tce.getTipoElemento() == TipoElemento.ROLE){
							List<TipoContentElement> lstTCE = mapCCRol.get(childPackage.getId());
							if (lstTCE == null){
								lstTCE = new ArrayList<TipoContentElement>();
							}
							lstTCE.add(tce);
							mapCCRol.put(childPackage.getId(), lstTCE);
						}
					}
				}
			}
			vb.setContentPackages(contentPackages);
			vb.setRoles(mapCCRol);
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

	public TipoContentCategory cargarCustomCategoriesRepositorio(String dirPlugin, String dirCustomCategory, String archivoCC, String archivoPlugin){
		TipoContentDescription contentDescription = XMIParser.getElementsXMICustomCategories(dirPlugin + dirCustomCategory, Constantes.destinoDescargas + dirPlugin + dirCustomCategory + archivoCC);
		TipoContentCategory contentCategory = XMIParser.getElementsXMITipoId(dirPlugin, Constantes.destinoDescargas + dirPlugin + archivoPlugin, "contentElements", contentDescription.getId(), true);
		if (contentCategory != null){
			contentCategory.setContentDescription(contentDescription);
		}
		return contentCategory;
	}
	
	public Map<String, TipoContentCategory> cargarCategorizedElementsRepositorio(String dirPrevia, String archivoPlugin, String categorizedElements){
		File f = new File(Constantes.destinoDescargas + dirPrevia + archivoPlugin);
		if (f.isFile()){
			String[] categorizedElementsArray = categorizedElements.split(" ");
			return XMIParser.getElementsXMICategorizedElements(dirPrevia, Constantes.destinoDescargas + dirPrevia + archivoPlugin, categorizedElementsArray);	
		}
		return null;
	}
	
	public TipoContentElement cargarContentElementsRepositorio(String dirPrevia, String archivo, String tag){
		File f = new File(Constantes.destinoDescargas + dirPrevia + archivo);
		if (f.isFile()){
			return XMIParser.getElementsXMIContentElement(dirPrevia, Constantes.destinoDescargas + dirPrevia + archivo, tag);	
		}
		return null;
	}
	
	public List<TipoContentElement> cargarTemplateRepositorio(String archivoPlugin, String tag){
		File f = new File(Constantes.destinoDescargas + archivoPlugin);
		if (f.isFile()){
			return XMIParser.getElementsXMIPlugin(Constantes.destinoDescargas + archivoPlugin, tag);	
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

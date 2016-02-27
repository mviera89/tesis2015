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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import dataTypes.TipoElemento;
import dataTypes.TipoLibrary;
import dataTypes.TipoMethodConfiguration;
import dataTypes.TipoMethodPackage;
import dataTypes.TipoPlugin;
import dominio.Struct;

@ManagedBean
@ViewScoped
public class ImportarModeloBean {

	private String mensajeAyudaRepositorio = Constantes.mensjaeAyudaRepositorio;
	private String repositorioIngresado = Constantes.URL_GITHUB_DEFAULT;
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

	public void leerArchivosRepositorio() throws Exception {
		try{
			init();
			if (!repositorioIngresado.equals("")){
				repositorio = repositorioIngresado;
				// Si no termina con '/' se la agrego
				int n = repositorio.length();
				String s = repositorio.substring(n - 1, n);
				if (!s.equals("/")){
					repositorio = repositorio + "/";
				}
				archivosDisponibles.clear();
				
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

	public void cargarArchivoRepositorio(){
		try{
			if (!nombreArchivo.equals("")){
				Object[] res = cargarArchivoExportRepositorio(); // [String, TipoLibrary]
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
					TipoPlugin plugin = cargarArchivoPluginRepositorio(dirPlugin, archivoPlugin);
					List<TipoMethodPackage> processPackages = cargarProcessPackageRepositorio(archivoPlugin);
					
					// Cargo los datos del method plugin en vistaBean 
					FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
					HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
					VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
					vb.setLibrary(library);
					vb.setPlugin(plugin);
			        vb.setMethodConfiguration(methodConfiguration);
			        vb.setProcessPackages(processPackages);
			        
//			        parsearDatosPlugin(plugin, archivoPlugin);
			        
					if (plugin != null){
						// Si hay una linea de procesos definida => La parseo
						String lineProcessDir = plugin.getLineProcessDir();
						if (lineProcessDir != null){
							String archivoLP = lineProcessDir;
							// lineProcessDir puede ser de la forma: dir1/dir2/.../nombre
							indexDiv = archivoLP.indexOf("/");
							String dirLineProcess = "";
							while (indexDiv != -1){
								String dir = archivoLP.substring(0, indexDiv);
								archivoLP = archivoLP.substring(indexDiv + 1, archivoLP.length());
								indexDiv = archivoLP.indexOf("/");
								dirLineProcess += dir + "/";
							}
							TipoPlugin modelLineProcess = cargarLineProcessRepositorio(dirPlugin, dirLineProcess, archivoLP);
							parsearDatosPlugin(dirPlugin, dirLineProcess, modelLineProcess, archivoLP);
						}
						String deliveryProcessDir = plugin.getDeliveryProcessDir();
						if (deliveryProcessDir != null){
							String archivoDP = deliveryProcessDir;
							// deliveryProcessDir puede ser de la forma: dir1/dir2/.../nombre
							indexDiv = archivoDP.indexOf("/");
							String dirDeliveryProcess = "";
							while (indexDiv != -1){
								String dir = archivoDP.substring(0, indexDiv);
								archivoDP = archivoDP.substring(indexDiv + 1, archivoDP.length());
								indexDiv = archivoDP.indexOf("/");
								dirDeliveryProcess += dir + "/";
							}
							cargarDeliveryProcessRepositorio(dirPlugin, null, dirDeliveryProcess, archivoDP);
							
							String customCategoriesDir = plugin.getCustomCategoriesDir();
							if (customCategoriesDir != null){
								String archivoCC = customCategoriesDir;
								// customCategoriesDir puede ser de la forma: dir1/dir2/.../nombre
								indexDiv = archivoCC.indexOf("/");
								String dirCustomCategories = "";
								while (indexDiv != -1){
									String dir = archivoCC.substring(0, indexDiv);
									archivoCC = archivoCC.substring(indexDiv + 1, archivoCC.length());
									indexDiv = archivoCC.indexOf("/");
									dirCustomCategories += dir + "/";
								}
								TipoContentCategory contentCategory = cargarCustomCategoriesRepositorio(dirPlugin, dirCustomCategories, archivoCC, archivoPlugin);
								
								Map<String, TipoContentCategory> categorizedElements = null;
								if (contentCategory != null){
									categorizedElements = cargarCategorizedElementsRepositorio(archivoPlugin, contentCategory.getCategorizedElements());
								}
								
						        vb.setContentCategory(contentCategory);
						        vb.setCategorizedElements(categorizedElements);
							}
							else{
								/*****************/
								/*FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", Constantes.MENSAJE_URL_NO_ACCESIBLE + "'" + Constantes.URL_GITHUB + repositorioIngresado + "'.");
						        FacesContext.getCurrentInstance().addMessage(null, mensaje);*/
								/*****************/
							}
						}
						else{
							/*****************/
							/*FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", Constantes.MENSAJE_URL_NO_ACCESIBLE + "'" + Constantes.URL_GITHUB + repositorioIngresado + "'.");
					        FacesContext.getCurrentInstance().addMessage(null, mensaje);*/
							/*****************/
						}
					}
					else{
						/*****************/
						/*FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", Constantes.MENSAJE_URL_NO_ACCESIBLE + "'" + Constantes.URL_GITHUB + repositorioIngresado + "'.");
				        FacesContext.getCurrentInstance().addMessage(null, mensaje);*/
						/*****************/
					}
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

	public void parsearDatosPlugin(String dirPlugin, String dirLineProcess, TipoPlugin plugin, String archivoPlugin) throws Exception{
		if (plugin != null){
			int indexDiv = 0;
			// Si hay una linea de procesos definida => La parseo
			String lineProcessDir = plugin.getLineProcessDir();
			if (lineProcessDir != null){
				String archivoLP = lineProcessDir;
				// lineProcessDir puede ser de la forma: dir1/dir2/.../nombre
				indexDiv = archivoLP.indexOf("/");
				while (indexDiv != -1){
					String dir = archivoLP.substring(0, indexDiv);
					archivoLP = archivoLP.substring(indexDiv + 1, archivoLP.length());
					indexDiv = archivoLP.indexOf("/");
					dirLineProcess += dir + "/";
				}
				TipoPlugin modelLineProcess = cargarLineProcessRepositorio(dirPlugin, dirLineProcess, archivoLP);
				parsearDatosPlugin(dirPlugin, dirLineProcess, modelLineProcess, archivoLP);
			}
			String deliveryProcessDir = plugin.getDeliveryProcessDir();
			if (deliveryProcessDir != null){
				String archivoDP = deliveryProcessDir;
				// deliveryProcessDir puede ser de la forma: dir1/dir2/.../nombre
				indexDiv = archivoDP.indexOf("/");
				String dirDeliveryProcess = "";
				while (indexDiv != -1){
					String dir = archivoDP.substring(0, indexDiv);
					archivoDP = archivoDP.substring(indexDiv + 1, archivoDP.length());
					indexDiv = archivoDP.indexOf("/");
					dirDeliveryProcess += dir + "/";
				}
				cargarDeliveryProcessRepositorio(dirPlugin, dirLineProcess, dirDeliveryProcess, archivoDP);
			}
			String customCategoriesDir = plugin.getCustomCategoriesDir();
			TipoContentCategory contentCategory = null;
			Map<String, TipoContentCategory> categorizedElements = null;
			if (customCategoriesDir != null){
				String archivoCC = customCategoriesDir;
				// customCategoriesDir puede ser de la forma: dir1/dir2/.../nombre
				indexDiv = archivoCC.indexOf("/");
				String dirCustomCategories = "";
				while (indexDiv != -1){
					String dir = archivoCC.substring(0, indexDiv);
					archivoCC = archivoCC.substring(indexDiv + 1, archivoCC.length());
					indexDiv = archivoCC.indexOf("/");
					dirCustomCategories += dir + "/";
				}
				contentCategory = cargarCustomCategoriesRepositorio(dirPlugin, dirLineProcess + dirCustomCategories, archivoCC, archivoPlugin);
				
				if (contentCategory != null){
					categorizedElements = cargarCategorizedElementsRepositorio(archivoPlugin, contentCategory.getCategorizedElements());
				}
			}
		}
		else{
		}
	}

	public Object[] cargarArchivoExportRepositorio() {
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
	
	public TipoPlugin cargarArchivoPluginRepositorio(String dirPlugin, String archivoPlugin){
		// Si en la url del repositorio existe el string "blob/" => Lo sustituyo por "", sino, le agrego el string "master/"
		int index = repositorio.indexOf("blob/");
		String urlDescargar = (index != -1) ? repositorio.replace("blob/", "") : repositorio.concat("master/");
		System.out.println("Descarga: " + Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + dirPlugin + archivoPlugin);
		
		try{
			URL url = new URL(Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + dirPlugin + archivoPlugin);
			URLConnection urlCon = url.openConnection();
			
			InputStream is = urlCon.getInputStream();
			FileOutputStream fos = new FileOutputStream(Constantes.destinoDescargas + archivoPlugin);
			byte [] array = new byte[1000];
			int leido = is.read(array);
			while (leido > 0) {
			   fos.write(array, 0, leido);
			   leido = is.read(array);
			}
			is.close();
			
			return XMIParser.getElementsXMIPlugin(Constantes.destinoDescargas + archivoPlugin);
		}
		catch (FileNotFoundException e){
			FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "No se encontró el archivo '" + archivoPlugin + "'.");
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

	public TipoPlugin cargarLineProcessRepositorio(String dirPlugin, String dirLineProcess, String archivoLP) throws Exception {
		// Si en la url del repositorio existe el string "blob/" => Lo sustituyo por "", sino, le agrego el string "master/"
		int index = repositorio.indexOf("blob/");
		String urlDescargar = (index != -1) ? repositorio.replace("blob/", "") : repositorio.concat("master/");
		System.out.println("Descarga: " + Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + dirPlugin + dirLineProcess + archivoLP);
		
		try{
			URL url = new URL(Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + dirPlugin + dirLineProcess + archivoLP);
			URLConnection urlCon = url.openConnection();
			
			InputStream is = urlCon.getInputStream();
			FileOutputStream fos = new FileOutputStream(Constantes.destinoDescargas + archivoLP);
			byte [] array = new byte[1000];
			int leido = is.read(array);
			while (leido > 0) {
			   fos.write(array, 0, leido);
			   leido = is.read(array);
			}
			is.close();
			
			return XMIParser.getElementsXMIPlugin(Constantes.destinoDescargas + archivoLP);
		}
		catch (FileNotFoundException e){
			FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "No se encontró el archivo '" + archivoLP + "'.");
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

	public void cargarDeliveryProcessRepositorio(String dirPlugin, String dirLineProcess, String dirDP, String archivoDP) throws Exception {
		nombreArchivo = archivoDP;
		// Si en la url del repositorio existe el string "blob/" => Lo sustituyo por "", sino, le agrego el string "master/"
		int index = repositorio.indexOf("blob/");
		String urlDescargar = (index != -1) ? repositorio.replace("blob/", "") : repositorio.concat("master/");
		System.out.println("Descarga: " + Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + dirPlugin + (dirLineProcess != null ? dirLineProcess + dirDP : dirDP) + archivoDP);
		
		URL url = new URL(Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + dirPlugin + (dirLineProcess != null ? dirLineProcess + dirDP : dirDP) + archivoDP);
		URLConnection urlCon = url.openConnection();
		
		InputStream is = urlCon.getInputStream();
		FileOutputStream fos = new FileOutputStream(Constantes.destinoDescargas + archivoDP);
		byte [] array = new byte[1000];
		int leido = is.read(array);
		while (leido > 0) {
		   fos.write(array, 0, leido);
		   leido = is.read(array);
		}
		
		is.close();
		
		// Cargo los Capability Patterns
		List<Struct> nodos = XMIParser.getElementXMI(Constantes.destinoDescargas + archivoDP);
		Iterator<Struct> it = nodos.iterator();
		while (it.hasNext()){
			Struct nodo = it.next();
			cargarCapabilityPatternsRepositorio(dirPlugin, dirLineProcess, archivoDP, nodo);
		}
		
		fos.close();
		
		FacesMessage mensaje = new FacesMessage("", "El archivo " + archivoDP + " ha sido cargado correctamente.");
        FacesContext.getCurrentInstance().addMessage(null, mensaje);
        
		FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
        vb.setNombreArchivo(archivoDP);
        vb.setRepositorio(repositorio);
	}
	
	public TipoContentCategory cargarCustomCategoriesRepositorio(String dirPlugin, String dirCC, String archivoCC, String archivoPlugin){
		// Si en la url del repositorio existe el string "blob/" => Lo sustituyo por "", sino, le agrego el string "master/"
		int index = repositorio.indexOf("blob/");
		String urlDescargar = (index != -1) ? repositorio.replace("blob/", "") : repositorio.concat("master/");
		System.out.println("Descarga: " + Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + dirPlugin + dirCC + archivoCC);
		
		try{
			URL url = new URL(Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + dirPlugin + dirCC + archivoCC);
			URLConnection urlCon = url.openConnection();
			
			InputStream is = urlCon.getInputStream();
			FileOutputStream fos = new FileOutputStream(Constantes.destinoDescargas + archivoCC);
			byte [] array = new byte[1000];
			int leido = is.read(array);
			while (leido > 0) {
			   fos.write(array, 0, leido);
			   leido = is.read(array);
			}
			is.close();
			
			TipoContentDescription contentDescription = XMIParser.getElementsXMICustomCategories(Constantes.destinoDescargas + archivoCC);
			TipoContentCategory contentCategory = XMIParser.getElementsXMIContentCategory(Constantes.destinoDescargas + archivoPlugin, contentDescription.getId());
			contentCategory.setContentDescription(contentDescription);
			return contentCategory;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Map<String, TipoContentCategory> cargarCategorizedElementsRepositorio(String archivoPlugin, String categorizedElements){
		File f = new File(Constantes.destinoDescargas + archivoPlugin);
		if (f.isFile()){
			String[] categorizedElementsArray = categorizedElements.split(" ");
			return XMIParser.getElementsXMICategorizedElements(Constantes.destinoDescargas + archivoPlugin, categorizedElementsArray);	
		}
		return null;
	}

	public void cargarCapabilityPatternsRepositorio(String dirPlugin, String dirLineProcess, String nombreArchivo, Struct nodo) throws Exception {
		if (nodo.getType() == TipoElemento.CAPABILITY_PATTERN){
			String fileCapabilityPattern = Constantes.nomArchivoDownload; // model.xmi
			String nombreArchivoCapabilityPattern = nombreArchivo.substring(0, nombreArchivo.length() - 4) + "_" + nodo.getNombre().replace(" ", "_") + ".xmi";
			String nameCapabilityPatterns = nodo.getNombre().replace(" ", "%20");
			String repoCapabilityPatterns = repositorio + dirPlugin + (dirLineProcess != null ? dirLineProcess : "") + "capabilitypatterns/" + nameCapabilityPatterns + "/";
			
			int index = repoCapabilityPatterns.indexOf("blob/");
			String urlDescargar = (index != -1) ? repoCapabilityPatterns.replace("blob/", "") : repoCapabilityPatterns.concat("master/");
			System.out.println("Descarga: " + Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + fileCapabilityPattern);
			
			URL url = new URL(Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + fileCapabilityPattern);
			URLConnection urlCon = url.openConnection();
			
			InputStream is = urlCon.getInputStream();
			FileOutputStream fos = new FileOutputStream(Constantes.destinoDescargas + nombreArchivoCapabilityPattern);
			byte [] array = new byte[1000];
			int leido = is.read(array);
			while (leido > 0) {
			   fos.write(array, 0, leido);
			   leido = is.read(array);
			}
			is.close();
			fos.close();
		}
		Iterator<Struct> it = nodo.getHijos().iterator();
		while (it.hasNext()){
			cargarCapabilityPatternsRepositorio(dirPlugin, dirLineProcess, nombreArchivo, it.next());
		}
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

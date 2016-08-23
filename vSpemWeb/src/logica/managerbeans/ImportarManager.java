package logica.managerbeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;

import logica.dataTypes.TipoContentCategory;
import logica.dataTypes.TipoContentDescription;
import logica.dataTypes.TipoContentElement;
import logica.dataTypes.TipoContentPackage;
import logica.dataTypes.TipoMethodConfiguration;
import logica.dataTypes.TipoMethodPackage;
import logica.dataTypes.TipoPlugin;
import logica.dataTypes.TipoView;
import logica.enumerados.TipoElemento;
import logica.enumerados.TipoTag;
import logica.interfaces.IImportarManager;
import logica.managerbeans.ImportarManager;
import logica.utils.Utils;
import logica.utils.XMIParser;

import com.sun.mail.iap.ConnectionException;

import config.Constantes;
import config.ReadProperties;

@Stateless
public class ImportarManager implements IImportarManager{

	public String cargarArchivos(boolean desdeRepositorio, String repositorio, List<String> archivosDisponibles, List<String> archivosDisponiblesLocal) throws IOException{
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
							archivosDisponiblesLocal.add(nomArchivo + "." + extArchivo);
						}
					}
				}
			}
		}
		return repositorio;
	}

	public void cargarTodoDeRepositorio(boolean desdeRepositorio, String repositorio, String dirPlugin) throws ConnectionException, IOException{
		if (desdeRepositorio){
			URL url = new URL(Constantes.URL_GITHUB + repositorio + dirPlugin.replace(" ", "%20"));
		
			// Directorios
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
					File f = new File(ReadProperties.getProperty("destinoDescargas") + dirPlugin + archivo);
					f.mkdirs();
					cargarTodoDeRepositorio(desdeRepositorio, repositorio, dirPlugin + archivo + "/");
				}
			}
			in.close();
			
			// Archivos
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			while ((linea = in.readLine()) != null){
				// <a href="/repositorio/.../nomArchivo"
				String strBuscado = "<a href=\"/" + repositorio + dirPlugin.replace(" ", "%20");
				int indexIni = linea.indexOf(strBuscado);
				if (indexIni != -1){
					String archivo = linea.substring(indexIni + strBuscado.length());
					archivo = archivo.substring(0, archivo.indexOf("\"")).replace("%20", " ");
					descargarArchivos(desdeRepositorio, repositorio, dirPlugin, archivo, archivo);
				}
			}
			
			in.close();

		}
		else{
			File directorio = new File(repositorio + dirPlugin);
			
			if (directorio.exists()){
				File[] archivos = directorio.listFiles();
				int nArchivos = archivos.length;
				for (int i = 0; i < nArchivos; i++){
					String archivo = archivos[i].getName();
					int indexExtension = archivo.indexOf(".");
					// Si no tiene extensión => es un directorio
					if (indexExtension == -1){
						File f = new File(ReadProperties.getProperty("destinoDescargas") + dirPlugin + archivo);
						f.mkdirs();
						cargarTodoDeRepositorio(desdeRepositorio, repositorio, dirPlugin + archivo + "/");
					}
					else{
						descargarArchivos(desdeRepositorio, repositorio, dirPlugin, archivo, archivo);
					}
				}
			}
		}
	}

	public void descargarArchivos(boolean desdeRepositorio, String repositorio, String dir, String archivo, String archivoFinal) throws FileNotFoundException, IOException, MalformedURLException{
		URL url = null;
		if (desdeRepositorio){
			int index = repositorio.indexOf("blob/");
			String urlDescargar = (index != -1) ? repositorio.replace("blob/", "") : repositorio.concat("master/");
			url = new URL(Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + dir.replace(" ", "%20") + archivo);
		}
		else{
			File f = new File(repositorio + dir.replace(" ", "%20") + archivo);
			url = f.toURL();
		}
		URLConnection urlCon = url.openConnection();
		
		InputStream is = urlCon.getInputStream();
		dir = dir.replace("%20", " ");
		File directorio = new File(ReadProperties.getProperty("destinoDescargas") + dir);
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

	public Object[] cargarArchivoInicial(boolean desdeRepositorio, String repositorio, String nombreArchivo) throws MalformedURLException, FileNotFoundException, IOException{
		URL url  = null;
		
		if (desdeRepositorio){
			// Si en la url del repositorio existe el string "blob/" => Lo sustituyo por "", sino, le agrego el string "master/"
			int index = repositorio.indexOf("blob/");
			String urlDescargar = (index != -1) ? repositorio.replace("blob/", "") : repositorio.concat("master/");
			url = new URL(Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + nombreArchivo);
		}
		else{
			File f = new File(repositorio + nombreArchivo);
			url =  f.toURL();
		}
		
		if (url != null){
			URLConnection urlCon = url.openConnection();
			
			InputStream is = urlCon.getInputStream();
			FileOutputStream fos = new FileOutputStream(ReadProperties.getProperty("destinoDescargas") + nombreArchivo);
			byte [] array = new byte[1000];
			int leido = is.read(array);
			while (leido > 0) {
			   fos.write(array, 0, leido);
			   leido = is.read(array);
			}
			is.close();
			fos.close();
			
			return XMIParser.getElementsXMIResource(ReadProperties.getProperty("destinoDescargas") + nombreArchivo); // [String, TipoLibrary]
		}
		return null;
	}

	public TipoMethodConfiguration cargarArchivoConfigurationRepositorio(boolean desdeRepositorio, String repositorio) throws FileNotFoundException, IOException{
		List<String> archivosXML = new ArrayList<String>(); 
		int index = 0;
		if (desdeRepositorio){
			// Si en la url del repositorio existe el string "blob/" => Lo sustituyo por "", sino, le agrego el string "master/"
			index = repositorio.indexOf("blob/");
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
		}
		else{
			File directorio = new File(repositorio + "configurations/");
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
							archivosXML.add(nomArchivo + "." + extArchivo);
						}
					}
				}
			}
		}
		
		if (archivosXML.size() != 0){
			// Si lo encontré, lo parseo. Asumo que hay un único achivo dentro de la carpeta 'configurations'.
			String archivoConfig =  "configurations/" + archivosXML.get(0);
			String urlDescargar = "";
			if (desdeRepositorio){
				urlDescargar = (index != -1) ? repositorio.replace("blob/", "") : repositorio.concat("master/");
			}
			else{
				urlDescargar = repositorio;
			}
			
			File directorio = new File(ReadProperties.getProperty("destinoDescargas") + "configurations");
			directorio.mkdirs();
			URL url = null;
			if (desdeRepositorio){
				url = new URL(Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + archivoConfig);
			}
			else{
				File f = new File(urlDescargar + archivoConfig);
				url = f.toURL();
			}
			URLConnection urlCon = url.openConnection();
			
			InputStream is = urlCon.getInputStream();
			FileOutputStream fos = new FileOutputStream(ReadProperties.getProperty("destinoDescargas") + archivoConfig);
			byte [] array = new byte[1000];
			int leido = is.read(array);
			while (leido > 0) {
			   fos.write(array, 0, leido);
			   leido = is.read(array);
			}
			is.close();
			fos.close();
			
			return XMIParser.getElementsXMIConfigurations(ReadProperties.getProperty("destinoDescargas") + archivoConfig);
		}
		return null;
	}

	public TipoPlugin cargarDeRepositorio(boolean desdeRepositorio, String repositorio, String dir, String archivo, String archivoFinal){
		try{
			File f = new File(ReadProperties.getProperty("destinoDescargas") + dir.replace("%20", " ") + "/" + archivoFinal);
			if (!f.isFile()){
				descargarArchivos(desdeRepositorio, repositorio, dir, archivo, archivoFinal);
			}
			dir = dir.replace("%20", " ");
			File directorio = new File(ReadProperties.getProperty("destinoDescargas") + dir);
			return XMIParser.getElementsXMIPlugin(directorio + "/" + archivoFinal);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<TipoMethodPackage> cargarProcessPackageRepositorio(String archivoPlugin){
		File f = new File(ReadProperties.getProperty("destinoDescargas") + archivoPlugin);
		if (f.isFile()){
			return XMIParser.getElementsXMIProcessPackage(ReadProperties.getProperty("destinoDescargas") + archivoPlugin);	
		}
		return null;		
	}

	public Map<String, Object> parsearDatosPlugin(boolean desdeRepositorio, String repositorio, String dirPlugin, String dirLineProcess, TipoPlugin plugin, String archivoPlugin, List<TipoView> addedCategory){
		Map<String, Object> resParser = new HashMap<String, Object>();
		int indexDiv = 0;
		
		// Parseo la línea de proceso
		String lineProcessDir = plugin.getLineProcessDir();
		if (lineProcessDir != null){
			String[] dirRes = Utils.separarDireccion(lineProcessDir);
			String dirLP = dirRes[0];
			String archivoLP = dirRes[1];
			TipoPlugin modelLineProcess = cargarDeRepositorio(desdeRepositorio, repositorio, dirPlugin + dirLP, archivoLP, archivoLP);
			parsearDatosPlugin(desdeRepositorio, repositorio, dirPlugin, dirLP, modelLineProcess, dirLP + archivoLP, addedCategory);
		}
		
		// Parseo el delieryProcess
		String deliveryProcessDir = plugin.getDeliveryProcessDir();
		if (deliveryProcessDir != null){
			String[] dirRes = Utils.separarDireccion(deliveryProcessDir);
			String dirDeliveryProcess = dirRes[0];
			String archivoDP = dirRes[1];
			resParser.put("nombreArchivo", archivoDP);
			resParser.put("vbNombreArchivo", archivoDP);
			resParser.put("vbDirectorioArchivo", dirPlugin + dirLineProcess);
			cargarDeRepositorio(desdeRepositorio, repositorio, dirPlugin + dirLineProcess + dirDeliveryProcess, archivoDP, archivoDP);// Cargo los Capability Patterns
			// Para que se seteen todos los hijos
			XMIParser.getElementXMI(ReadProperties.getProperty("destinoDescargas") + dirPlugin + dirLineProcess + dirDeliveryProcess + archivoDP);
			resParser.put("vbRepositorio", repositorio);
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
			cargarDeRepositorio(desdeRepositorio, repositorio, dirPlugin + dirLineProcess + dirCapabilityPattern, archivoCP, archivoCP);
		}
		
		// Parseo las customCategories
		String customCategoriesDir = plugin.getCustomCategoriesDir();
		if (customCategoriesDir != null){
			String[] dirRes = Utils.separarDireccion(customCategoriesDir);
			String dirCustomCategories = dirRes[0];
			String archivoCC = dirRes[1];
			cargarDeRepositorio(desdeRepositorio, repositorio, dirPlugin + dirCustomCategories, archivoCC, archivoCC);
			TipoContentCategory contentCategory = cargarCustomCategoriesRepositorio(dirPlugin, dirCustomCategories, archivoCC, archivoPlugin);
			resParser.put("vbContentCategory", contentCategory);
			if (contentCategory != null){
				String elements = "";
				List<TipoView> categories = addedCategory;
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
				resParser.put("vbCategorizedElements", categorizedElements);
			}
		}
		
		// Parseo las tasks
		Map<String, TipoContentElement> lstTask = new HashMap<String, TipoContentElement>();
		List<TipoContentElement> tceTasks = XMIParser.getElementsXMIPlugin(ReadProperties.getProperty("destinoDescargas") + dirPlugin + archivoPlugin, TipoTag.TASK.toString());
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
			String[] dirRes = Utils.separarDireccion(taskDir);
			String dirTask = dirRes[0];
			String archivoTask = dirRes[1];
			cargarDeRepositorio(desdeRepositorio, repositorio, dirPlugin + dirTask, archivoTask, archivoTask);
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
		List<TipoContentElement> lstWP = XMIParser.getElementsXMIPlugin(ReadProperties.getProperty("destinoDescargas") + dirPlugin + archivoPlugin, TipoTag.ARTIFACT.toString());
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
			String[] dirRes = Utils.separarDireccion(workproductDir);
			String dirWorkproduct = dirRes[0];
			String archivoWorkproduct = dirRes[1];
			cargarDeRepositorio(desdeRepositorio, repositorio, dirPlugin + dirWorkproduct, archivoWorkproduct, archivoWorkproduct);
			TipoContentElement workproduct = cargarContentElementsRepositorio(dirPlugin + dirWorkproduct, archivoWorkproduct, TipoTag.ARTIFACT_DESCRIPTION.toString());
			if (workproduct != null){
				String workproductName = workproduct.getName();
				String[] res = workproductName.split(",");
				String workproductId = res.length > 1 ? res[1] : "";
				if (!workproductId.equals("")){
					TipoContentElement wp = lstWorkproduct.get(workproductId);
					if (wp != null){
						wp.setContentDescription(workproduct);
					}
				}
			}
		}
		
		// Parseo las guidances
		List<String> guidancesDir = plugin.getGuidancesDir();
		Iterator<String> itGuidances = guidancesDir.iterator();
		Map<String, TipoContentElement> lstGuidances = new HashMap<String, TipoContentElement>();
		while (itGuidances.hasNext()){
			String guidanceDir = itGuidances.next();
			String[] dirRes = Utils.separarDireccion(guidanceDir);
			String dirGuidance = dirRes[0];
			String archivoGuidance = dirRes[1];
			cargarDeRepositorio(desdeRepositorio, repositorio, dirPlugin + dirGuidance, archivoGuidance, archivoGuidance);
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
		resParser.put("vbTemplates", mapTemplates);
		
		// Cargo en el contentPackage
		Map<String, TipoContentElement> mapRoles = new HashMap<String, TipoContentElement>();
		List<TipoContentElement> lstRoles = XMIParser.getElementsXMIPlugin(ReadProperties.getProperty("destinoDescargas") + dirPlugin + archivoPlugin, TipoTag.ROLE.toString());
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
			TipoContentCategory contentElement = XMIParser.getElementsXMITipoId(dirPlugin, ReadProperties.getProperty("destinoDescargas") + dirPlugin + archivoPlugin, "contentElements", idTce, false);
			if (contentElement != null){
				TipoContentCategory childPackage = XMIParser.getElementsXMITipoId(dirPlugin, ReadProperties.getProperty("destinoDescargas") + dirPlugin + archivoPlugin, "childPackages", contentElement.getId(), true);
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
		resParser.put("vbContentPackages", contentPackages);
		resParser.put("vbRoles", mapCCRol);
		
		return resParser;
	}

	public TipoContentCategory cargarCustomCategoriesRepositorio(String dirPlugin, String dirCustomCategory, String archivoCC, String archivoPlugin){
		TipoContentDescription contentDescription = XMIParser.getElementsXMICustomCategories(dirPlugin + dirCustomCategory, ReadProperties.getProperty("destinoDescargas") + dirPlugin + dirCustomCategory + archivoCC);
		TipoContentCategory contentCategory = XMIParser.getElementsXMITipoId(dirPlugin, ReadProperties.getProperty("destinoDescargas") + dirPlugin + archivoPlugin, "contentElements", contentDescription.getId(), true);
		if (contentCategory != null){
			contentCategory.setContentDescription(contentDescription);
		}
		return contentCategory;
	}

	public Map<String, TipoContentCategory> cargarCategorizedElementsRepositorio(String dirPrevia, String archivoPlugin, String categorizedElements){
		File f = new File(ReadProperties.getProperty("destinoDescargas") + dirPrevia + archivoPlugin);
		if (f.isFile()){
			String[] categorizedElementsArray = categorizedElements.split(" ");
			return XMIParser.getElementsXMICategorizedElements(dirPrevia, ReadProperties.getProperty("destinoDescargas") + dirPrevia + archivoPlugin, categorizedElementsArray);	
		}
		return null;
	}

	public TipoContentElement cargarContentElementsRepositorio(String dirPrevia, String archivo, String tag){
		File f = new File(ReadProperties.getProperty("destinoDescargas") + dirPrevia + archivo);
		if (f.isFile()){
			return XMIParser.getElementsXMIContentElement(dirPrevia, ReadProperties.getProperty("destinoDescargas") + dirPrevia + archivo, tag);	
		}
		return null;
	}

	public List<TipoContentElement> cargarTemplateRepositorio(String archivoPlugin, String tag){
		File f = new File(ReadProperties.getProperty("destinoDescargas") + archivoPlugin);
		if (f.isFile()){
			return XMIParser.getElementsXMIPlugin(ReadProperties.getProperty("destinoDescargas") + archivoPlugin, tag);	
		}
		return null;
	}

}

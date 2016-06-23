package logica.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import logica.dataTypes.TipoContentCategory;
import logica.dataTypes.TipoContentDescription;
import logica.dataTypes.TipoContentElement;
import logica.dataTypes.TipoContentPackage;
import logica.dataTypes.TipoLibrary;
import logica.dataTypes.TipoMethodConfiguration;
import logica.dataTypes.TipoMethodElementProperty;
import logica.dataTypes.TipoMethodPackage;
import logica.dataTypes.TipoPlugin;
import logica.dataTypes.TipoSection;
import logica.dataTypes.TipoView;
import logica.dataTypes.WorkProduct;
import logica.dominio.Struct;
import logica.dominio.Variant;
import logica.enumerados.TipoElemento;
import logica.enumerados.TipoTag;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import presentacion.managedBeans.VistaBean;
import config.Constantes;
import config.ReadProperties;

public class XMIParser {
	
	public static Object[] getElementsXMIResource(String nomFile){
		String uriPlugin = null;
		TipoLibrary library = null;
		File inputFile = new File(nomFile);
		try{
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();
	        
	        // Obtengo la ruta donde se encuentra plugin.xmi
	        NodeList nodos = doc.getElementsByTagName("org.eclipse.epf.uma.resourcemanager:ResourceManager");
        	if (nodos.getLength() > 0){
        		int temp = 0;
				Node nodo = nodos.item(temp);
				if (nodo.getNodeType() == Node.ELEMENT_NODE) {
					NodeList childNodes = nodo.getChildNodes();
					for (int i = 0; i < childNodes.getLength(); i++){
						Node child = childNodes.item(i);
						if (child.getNodeType() == Node.ELEMENT_NODE) {
							Element eChild = (Element) child;
							if (eChild.hasAttribute("uri")){
								String uri = eChild.getAttribute("uri");
								if ((uri != null) && (!uri.equals(""))){
									// uri puede ser de la forma: dir1/dir2/.../nombre
									String nombre = uri;
									int indexDiv = nombre.indexOf("/");
									while (indexDiv != -1){
										nombre = nombre.substring(indexDiv + 1, nombre.length());
										indexDiv = nombre.indexOf("/");
									}
									if (nombre.equals("plugin.xmi")){
										uriPlugin = uri;
									}
								}
							}
						}
					}
				}
        	}
        	
        	// Obtengo los datos de la librería
        	nodos = doc.getElementsByTagName("org.eclipse.epf.uma:MethodLibrary");
        	if (nodos.getLength() > 0){
        		int temp = 0;
				Node nodo = nodos.item(temp);
				Element eNodo = (Element) nodo;
				String guid = "";
				String name = "";
				String id = "";
				if (eNodo.hasAttribute("guid")){
					guid = eNodo.getAttribute("guid");
				}
				if (eNodo.hasAttribute("name")){
					name = eNodo.getAttribute("name");
				}
				if (eNodo.hasAttribute("xmi:id")){
					id = eNodo.getAttribute("xmi:id");
				}
				library = new TipoLibrary(guid, name, id);
        	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Object[] res = {uriPlugin, library};
		return res;
		 
	}
	
	public static TipoMethodConfiguration getElementsXMIConfigurations(String nomFile){
		File inputFile = new File(nomFile);
		try{
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();
	        
	        // Obtengo la ruta donde se encuentra plugin.xmi
	        NodeList nodos = doc.getElementsByTagName("org.eclipse.epf.uma:MethodConfiguration");
        	if (nodos.getLength() > 0){
        		int temp = 0;
				Node nodo = nodos.item(temp);
				if (nodo.getNodeType() == Node.ELEMENT_NODE) {
					Element eNodo = (Element) nodo;
					String xmiVersion = "";
					String xmlnsXmi = "";
					String xmlnsXsi = "";
					String uma = "";
					String epf = "";
					String epfVersion = "";
					String id = "";
					String name = "";
					String guid = "";
					String briefDescription = "";
					List<TipoMethodElementProperty> methodElementProperty = new ArrayList<TipoMethodElementProperty>();
					List<TipoView> processViews = new ArrayList<TipoView>();
					TipoView defaultView = null;
					List<TipoView> addedCategory = new ArrayList<TipoView>();
					
					if (eNodo.hasAttribute("xmi:version")){
						xmiVersion = eNodo.getAttribute("xmi:version");
					}
					if (eNodo.hasAttribute("xmlns:xmi")){
						xmlnsXmi = eNodo.getAttribute("xmlns:xmi");
					}
					if (eNodo.hasAttribute("xmlns:xsi")){
						xmlnsXsi = eNodo.getAttribute("xmlns:xsi");
					}
					if (eNodo.hasAttribute("xmlns:org.eclipse.epf.uma")){
						uma = eNodo.getAttribute("xmlns:org.eclipse.epf.uma");
					}
					if (eNodo.hasAttribute("xmlns:epf")){
						epf = eNodo.getAttribute("xmlns:epf");
					}
					if (eNodo.hasAttribute("epf:version")){
						epfVersion = eNodo.getAttribute("epf:version");
					}
					if (eNodo.hasAttribute("xmi:id")){
						id = eNodo.getAttribute("xmi:id");
					}
					if (eNodo.hasAttribute("name")){
						name = eNodo.getAttribute("name");
					}
					if (eNodo.hasAttribute("guid")){
						guid = eNodo.getAttribute("guid");
					}
					if (eNodo.hasAttribute("briefDescription")){
						briefDescription = eNodo.getAttribute("briefDescription");
						briefDescription = briefDescription.replaceAll("\"", "'");
					}
					
					// Cargo methodElementProperty
					NodeList childNodes = nodo.getChildNodes();
					int j = 0;
					while (j < childNodes.getLength()){
						Node child = childNodes.item(j);
						String nodeName = child.getNodeName();
						if (nodeName.equals("methodElementProperty")){
							Element eChild = (Element) child;
							String eId = "";
							String eName = "";
							String eValue = "";
							if (eChild.hasAttribute("xmi:id")){
								eId = eChild.getAttribute("xmi:id");
							}
							if (eChild.hasAttribute("name")){
								eName = eChild.getAttribute("name");							
							}
							if (eChild.hasAttribute("value")){
								eValue = eChild.getAttribute("value");
							}
							TipoMethodElementProperty elementProperty = new TipoMethodElementProperty(eId, eName, eValue);
							methodElementProperty.add(elementProperty);
						}
						else if ((nodeName.equals("processViews")) || (nodeName.equals("defaultView")) || (nodeName.equals("addedCategory"))){
							Element eChild = (Element) child;
							String eType = "";
							String eHref = "";
							if (eChild.hasAttribute("xsi:type")){
								eType = eChild.getAttribute("xsi:type");
							}
							if (eChild.hasAttribute("href")){
								eHref = eChild.getAttribute("href");							
							}
							TipoView view = new TipoView(eType, eHref);
							if (nodeName.equals("processViews")){
								processViews.add(view);
							}
							else if (nodeName.equals("defaultView")){
								defaultView = view;
							}
							else{
								addedCategory.add(view);
							}
						}
						j++;
					}
					return new TipoMethodConfiguration(xmiVersion, xmlnsXmi, xmlnsXsi, uma, epf, epfVersion, id, name, guid, briefDescription, 
													   methodElementProperty, processViews, defaultView, addedCategory);
				}
        	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static TipoPlugin getElementsXMIPlugin(String nomFile){
		try{
			File inputFile = new File(nomFile);
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();

	        String lineProcessDir = null;
	        String deliveryProcessDir = null;
	        List<String> capabilityPatternsDir = new ArrayList<String>();
	        List<String> tasksDir = new ArrayList<String>();
	        List<String> workproductsDir = new ArrayList<String>();
	        List<String> guidancesDir = new ArrayList<String>();
	        String customCategoriesDir = null;
	        NodeList nodosResource = doc.getElementsByTagName("org.eclipse.epf.uma.resourcemanager:ResourceManager");
	        if (nodosResource.getLength() > 0){
				Node nodo = nodosResource.item(0);
				if (nodo.getNodeType() == Node.ELEMENT_NODE) {
					NodeList childNodes = nodo.getChildNodes();
					int i = 0;
					while (i < childNodes.getLength()){
						Node child = childNodes.item(i);
						if (child.getNodeType() == Node.ELEMENT_NODE) {
							if (child.getNodeName().equals("resourceDescriptors")){
								Element eChild = (Element) child;
								if (eChild.hasAttribute("uri")){
									String uri = eChild.getAttribute("uri");
									if (uri != null){
										int indexDiv = uri.indexOf("/");
										if (indexDiv != -1){
											String dir = uri.substring(0, indexDiv);
											if (dir.equals("lineprocess")){
												lineProcessDir = uri;
											}
											else if (dir.equals("deliveryprocesses")){
												deliveryProcessDir = uri;
											}
											else if (dir.equals("capabilitypatterns")){
												capabilityPatternsDir.add(uri);
											}
											else if (dir.equals("customcategories")){
												customCategoriesDir = uri;
											}
											else if (dir.equals("tasks")){
												tasksDir.add(uri);
											}
											else if (dir.equals("workproducts")){
												workproductsDir.add(uri);
											}
											else if (dir.equals("guidances")){
												guidancesDir.add(uri);
											}
										}
									}
								}
							}
						}
						i++;
					}
				}
	        }
	        
			String id = "";
			String name = "";
			String guid = "";
			String briefDescription = "";
			String authors = ""; 
			String changeDate = "";
			String changeDescription = "";
			String version = "";
	        NodeList nodos = doc.getElementsByTagName("org.eclipse.epf.uma:MethodPlugin");
        	if (nodos.getLength() > 0){
        		int temp = 0;
				Node nodo = nodos.item(temp);
				Element eHijo = (Element) nodo;
				if (eHijo.hasAttribute("xmi:id")){
					id = eHijo.getAttribute("xmi:id");
				}
				if (eHijo.hasAttribute("name")){
					name = eHijo.getAttribute("name");
				}
				if (eHijo.hasAttribute("guid")){
					guid = eHijo.getAttribute("guid");
				}
				if (eHijo.hasAttribute("briefDescription")){
					briefDescription = eHijo.getAttribute("briefDescription");
					briefDescription = briefDescription.replaceAll("\"", "'");
				}
				if (eHijo.hasAttribute("authors")){
					authors = eHijo.getAttribute("authors");
				}
				if (eHijo.hasAttribute("changeDate")){
					changeDate = eHijo.getAttribute("changeDate"); // Es de la forma 2016-02-13T20:59:36.516-0300
					int index = changeDate.indexOf(".");
					changeDate = changeDate.substring(0, index);
				}
				if (eHijo.hasAttribute("changeDescription")){
					changeDescription = eHijo.getAttribute("changeDescription");
				}
				if (eHijo.hasAttribute("version")){
					version = eHijo.getAttribute("version");
				}
        	}
        	
        	
        	if (lineProcessDir != null){
        		String[] dirRes = separarDireccion(nomFile);
        		String dirPlugin = dirRes[0];
        		
        		dirRes = separarDireccion(lineProcessDir);
        		String dirLP = dirRes[0];
				String archivoPL = dirRes[1];
        		dirPlugin += dirLP;
				TipoPlugin pt = getElementsXMIPlugin(dirPlugin + archivoPL);
				deliveryProcessDir = dirLP + pt.getDeliveryProcessDir();
				Iterator<String> itCp = pt.getCapabilityPatternsDir().iterator();
				while (itCp.hasNext()){
					capabilityPatternsDir.add(dirLP + itCp.next());	
				}
        	}
        	
        	
        	return new TipoPlugin(id, name, guid, briefDescription, authors, changeDate, changeDescription, version,
        						  lineProcessDir, deliveryProcessDir, capabilityPatternsDir, customCategoriesDir, tasksDir, workproductsDir, guidancesDir);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String[] separarDireccion(String archivo){
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
	
	public static TipoContentCategory getElementsXMITipoId(String dirPrevia, String nomFile, String tipo, String id, boolean buscarPadre){
		try{
			File inputFile = new File(nomFile);
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();
	        
	        NodeList nodos = doc.getElementsByTagName("org.eclipse.epf.uma:MethodPlugin");
        	if (nodos.getLength() > 0){
        		int temp = 0;
				Node nodo = nodos.item(temp);
				Node resNode = buscarPadre ? obtenerPadreTipoId(nodo, tipo, id) : obtenerNodoTipoId(nodo, tipo, id);
				
				// Si encontré el id que buscaba
				if (resNode != null){
					Element e = (Element) resNode;
					String typeCC = "";
					String idCC = "";
					String nameCC = "";
					String guidCC = "";
					String presentationNameCC = "";
					String briefDescriptionCC = "";
					String categorizedElementsCC = "";
					String shapeiconCC = "";
					String nodeiconCC = "";
					String tasksCC = "";
					String workProductsCC = "";
					TipoMethodElementProperty methodElementProperty = null;
					
					if (e.hasAttribute("xsi:type")){
						typeCC = e.getAttribute("xsi:type");
					}
					if (e.hasAttribute("xmi:id")){
						idCC = e.getAttribute("xmi:id");
					}
					if (e.hasAttribute("name")){
						nameCC = e.getAttribute("name");
					}
					if (e.hasAttribute("guid")){
						guidCC = e.getAttribute("guid");
					}
					if (e.hasAttribute("presentationName")){
						presentationNameCC = e.getAttribute("presentationName");
					}
					if (e.hasAttribute("briefDescription")){
						briefDescriptionCC = e.getAttribute("briefDescription");
						briefDescriptionCC = briefDescriptionCC.replaceAll("\"", "'");
					}
					if (e.hasAttribute("categorizedElements")){
						categorizedElementsCC = e.getAttribute("categorizedElements");
					}
					if (e.hasAttribute("shapeicon")){
						shapeiconCC = dirPrevia + e.getAttribute("shapeicon");
					}
					if (e.hasAttribute("nodeicon")){
						nodeiconCC = dirPrevia + e.getAttribute("nodeicon");
					}
					if (e.hasAttribute("tasks")){
						tasksCC = e.getAttribute("tasks");
					}
					if (e.hasAttribute("workProducts")){
						workProductsCC = e.getAttribute("workProducts");
					}
					
					NodeList childNodes = resNode.getChildNodes();
					int j = 0;
					while ((j < childNodes.getLength()) && (methodElementProperty == null)){
						Node child = childNodes.item(j);
						if (child.getNodeName().equals("methodElementProperty")){
							Element eChild = (Element) child;
							String eId = "";
							String eName = "";
							String eValue = "";
							if (eChild.hasAttribute("xmi:id")){
								eId = eChild.getAttribute("xmi:id");
							}
							if (eChild.hasAttribute("name")){
								eName = eChild.getAttribute("name");							
							}
							if (eChild.hasAttribute("value")){
								eValue = eChild.getAttribute("value");
							}
							methodElementProperty = new TipoMethodElementProperty(eId, eName, eValue);
						}
						j++;
					}
					
					return new TipoContentCategory(typeCC, idCC, nameCC, guidCC, presentationNameCC, briefDescriptionCC, categorizedElementsCC, shapeiconCC, nodeiconCC, tasksCC, workProductsCC, methodElementProperty);
				}
        	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Obtiene el nodo de tipo 'tipo' que es "padre" del elemento con id 'xmiId' 
	public static Node obtenerPadreTipoId(Node nodo, String tipo, String xmiId){
		if (nodo.getNodeType() == Node.ELEMENT_NODE) {
			if (nodo.getNodeName().equals(tipo)){
				NodeList nodes = nodo.getChildNodes();
				int j = 0;
				while (j < nodes.getLength()){
					Node n = nodes.item(j);
					if (n.getNodeType() == Node.ELEMENT_NODE){
						Element eNode = (Element) n;
						if ((eNode.hasAttribute("xmi:id")) && (eNode.getAttribute("xmi:id").equals(xmiId))){
							return nodo;
						}
					}
					j++;
				}
			}
		}
		NodeList childNodes = nodo.getChildNodes();
		int i = 0;
		while (i < childNodes.getLength()){
			Node child = childNodes.item(i);
			Node res = obtenerPadreTipoId(child, tipo, xmiId);
			if (res != null){
				return res;
			}
			i++;
		}
		return null;
	}
	
	// Obtiene el nodo de tipo 'tipo' con id 'xmiId' 
	public static Node obtenerNodoTipoId(Node nodo, String tipo, String xmiId){
		if (nodo.getNodeType() == Node.ELEMENT_NODE) {
			if (nodo.getNodeName().equals(tipo)){
				Element eNode = (Element) nodo;
				if ((eNode.hasAttribute("xmi:id")) && (eNode.getAttribute("xmi:id").equals(xmiId))){
					return nodo;
				}
			}
		}
		NodeList childNodes = nodo.getChildNodes();
		int i = 0;
		int n = childNodes.getLength();
		while (i < n){
			Node child = childNodes.item(i);
			Node res = obtenerNodoTipoId(child, tipo, xmiId);
			if (res != null){
				return res;
			}
			i++;
		}
		return null;
	}
	
	public static Node obtenerNodoId(Node nodo, String xmiId){
		if (nodo.getNodeType() == Node.ELEMENT_NODE) {
			Element eNodo = (Element) nodo;
			if ((eNodo.hasAttribute("xmi:id")) && (eNodo.getAttribute("xmi:id").equals(xmiId))){
				return nodo;
			}
		}
		NodeList childNodes = nodo.getChildNodes();
		int i = 0;
		while (i < childNodes.getLength()){
			Node child = childNodes.item(i);
			Node res = obtenerNodoId(child, xmiId);
			if (res != null){
				return res;
			}
			i++;
		}
		return null;
	}
	
	public static List<Node> obtenerNodosType(Node nodo, String xsiType){
		List<Node> res = new ArrayList<Node>();
		if (nodo.getNodeType() == Node.ELEMENT_NODE) {
			Element eNodo = (Element) nodo;
			if ((eNodo.hasAttribute("xsi:type")) && (eNodo.getAttribute("xsi:type").equals(xsiType))){
				res.add(nodo);
			}
		}
		NodeList childNodes = nodo.getChildNodes();
		int i = 0;
		while (i < childNodes.getLength()){
			Node child = childNodes.item(i);
			List<Node> resNode = obtenerNodosType(child, xsiType);
			res.addAll(resNode);
			i++;
		}
		return res;
	}
	
	public static List<Node> obtenerHijosNodoType(Node nodo, String xsiType){
		List<Node> res = new ArrayList<Node>();
		NodeList childNodes = nodo.getChildNodes();
		int i = 0;
		while (i < childNodes.getLength()){
			Node child = childNodes.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE){
				Element eChild = (Element) child;
				if ((eChild.hasAttribute("xsi:type")) && (eChild.getAttribute("xsi:type").equals(xsiType))){
					res.add(child);
				}
			}
			i++;
		}
		return res;
	}
	
	public static TipoContentDescription getElementsXMICustomCategories(String dirPrevia, String nomFile){
		File inputFile = new File(nomFile);
		try{
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();
	        
	        NodeList nodos = doc.getElementsByTagName("org.eclipse.epf.uma:ContentDescription");
        	if (nodos.getLength() > 0){
        		int temp = 0;
				Node nodo = nodos.item(temp);
				if (nodo.getNodeType() == Node.ELEMENT_NODE) {
					Element eHijo = (Element) nodo;
					String xmiVersion = "";
					String xmi = "";
					String uma = "";
					String epf = "";
					String epfVersion = "";
					String id = "";
					String name = "";
					String guid = "";
					String authors = "";
					String changeDate = "";
					String changeDescription = "";
					String version = "";
					String mainDescription = "";
					String keyConsiderations = "";
					
					if (eHijo.hasAttribute("xmi:version")){
						xmiVersion = eHijo.getAttribute("xmi:version");
					}
					if (eHijo.hasAttribute("xmlns:xmi")){
						xmi = eHijo.getAttribute("xmlns:xmi");
					}
					if (eHijo.hasAttribute("xmlns:org.eclipse.epf.uma")){
						uma = eHijo.getAttribute("xmlns:org.eclipse.epf.uma");
					}
					if (eHijo.hasAttribute("xmlns:epf")){
						epf = eHijo.getAttribute("xmlns:epf");
					}
					if (eHijo.hasAttribute("epf:version")){
						epfVersion = eHijo.getAttribute("epf:version");
					}
					if (eHijo.hasAttribute("xmi:id")){
						id = eHijo.getAttribute("xmi:id");
					}
					if (eHijo.hasAttribute("name")){
						name = eHijo.getAttribute("name");
					}
					if (eHijo.hasAttribute("guid")){
						guid = eHijo.getAttribute("guid");
					}
					if (eHijo.hasAttribute("authors")){
						authors = eHijo.getAttribute("authors");
					}
					if (eHijo.hasAttribute("changeDate")){
						changeDate = eHijo.getAttribute("changeDate"); // Es de la forma 2016-02-13T20:59:36.516-0300
						int index = changeDate.indexOf(".");
						changeDate = changeDate.substring(0, index);
					}
					if (eHijo.hasAttribute("changeDescription")){
						changeDescription = eHijo.getAttribute("changeDescription");
					}
					if (eHijo.hasAttribute("version")){
						version = eHijo.getAttribute("version");
					}
					
					NodeList childNodes = nodo.getChildNodes();
					int i = 0;
					while ((i < childNodes.getLength()) && (mainDescription.equals("") || (keyConsiderations.equals("")))){
						Node child = childNodes.item(i);
						if (child.getNodeName().equals("mainDescription")){
							mainDescription = child.getFirstChild().getNodeValue();
							mainDescription = mainDescription.replace("src=\"./", "src=\"" + dirPrevia);
						}
						else if (child.getNodeName().equals("keyConsiderations")){
							keyConsiderations = child.getFirstChild().getNodeValue();
						}
						i++;
					}
					
					return new TipoContentDescription(xmiVersion, xmi, uma, epf, epfVersion, id, name, guid, authors, changeDate, changeDescription, version, mainDescription, keyConsiderations);
				}
        	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Map<String, TipoContentCategory> getElementsXMICategorizedElements(String dirPrevia, String nomFile, String[] categorizedElementsArray){
		Map<String, TipoContentCategory> res = new HashMap<String, TipoContentCategory>();
		File inputFile = new File(nomFile);
		try{
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();

	        NodeList nodos = doc.getElementsByTagName("org.eclipse.epf.uma:MethodPlugin");
        	if (nodos.getLength() > 0){
        		int temp = 0;
				Node nodo = nodos.item(temp);
				int n = categorizedElementsArray.length;
				for (int i = 0; i < n; i++){
					String id = categorizedElementsArray[i];
					Node resNode = obtenerNodoId(nodo, id);
					
					// Si encontré el id que buscaba
					if (resNode != null){
						Element e = (Element) resNode;
						String typeCC = "";
						if (e.hasAttribute("xsi:type")){
							typeCC = e.getAttribute("xsi:type");
						}
						if ((typeCC.equals("org.eclipse.epf.uma:CustomCategory")) ||
								(typeCC.equals("org.eclipse.epf.uma:Discipline")) ||
								(typeCC.equals("org.eclipse.epf.uma:SupportingMaterial"))){
							String idCC = "";
							String nameCC = "";
							String guidCC = "";
							String presentationNameCC = "";
							String briefDescriptionCC = "";
							String categorizedElementsCC = "";
							String shapeiconCC = "";
							String nodeiconCC = "";
							String tasksCC = "";
							String workProductsCC = "";
							TipoMethodElementProperty methodElementProperty = null;
							
							if (e.hasAttribute("xmi:id")){
								idCC = e.getAttribute("xmi:id");
							}
							if (e.hasAttribute("name")){
								nameCC = e.getAttribute("name");
							}
							if (e.hasAttribute("guid")){
								guidCC = e.getAttribute("guid");
							}
							if (e.hasAttribute("presentationName")){
								presentationNameCC = e.getAttribute("presentationName");
							}
							if (e.hasAttribute("briefDescription")){
								briefDescriptionCC = e.getAttribute("briefDescription");
								briefDescriptionCC = briefDescriptionCC.replaceAll("\"", "'");
							}
							if (e.hasAttribute("categorizedElements")){
								categorizedElementsCC = e.getAttribute("categorizedElements");
							}
							if (e.hasAttribute("shapeicon")){
								shapeiconCC = dirPrevia + e.getAttribute("shapeicon");
							}
							if (e.hasAttribute("nodeicon")){
								nodeiconCC = dirPrevia + e.getAttribute("nodeicon");
							}
							if (e.hasAttribute("tasks")){
								tasksCC = e.getAttribute("tasks");
							}
							if (e.hasAttribute("workProducts")){
								workProductsCC = e.getAttribute("workProducts");
							}
							
							NodeList childNodes = resNode.getChildNodes();
							int j = 0;
							while ((j < childNodes.getLength()) && (methodElementProperty == null)){
								Node child = childNodes.item(j);
								if (child.getNodeName().equals("methodElementProperty")){
									Element eChild = (Element) child;
									String eId = "";
									String eName = "";
									String eValue = "";
									if (eChild.hasAttribute("xmi:id")){
										eId = eChild.getAttribute("xmi:id");
									}
									if (eChild.hasAttribute("name")){
										eName = eChild.getAttribute("name");							
									}
									if (eChild.hasAttribute("value")){
										eValue = eChild.getAttribute("value");
									}
									methodElementProperty = new TipoMethodElementProperty(eId, eName, eValue);
								}
								j++;
							}
							res.put(id, new TipoContentCategory(typeCC, idCC, nameCC, guidCC, presentationNameCC, briefDescriptionCC, categorizedElementsCC, shapeiconCC, nodeiconCC, tasksCC, workProductsCC, methodElementProperty));
						}
					}
				}
        	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static TipoContentElement getElementsXMIContentElement(String dirPrevia, String nomFile, String tag){
		File inputFile = new File(nomFile);
		try{
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();
	        
	        NodeList nodos = doc.getElementsByTagName(tag);
        	if (nodos.getLength() > 0){
        		int temp = 0;
				Node nodo = nodos.item(temp);
				if (nodo.getNodeType() == Node.ELEMENT_NODE) {
					Element eHijo = (Element) nodo;
					String xmiVersion = "";
					String xmi = "";
					String uma = "";
					String epf = "";
					String epfVersion = "";
					String id = "";
					String name = "";
					String guid = "";
					String presentationName = "";
					String authors = "";
					String changeDate = "";
					String version = "";
					String mainDescription = "";
					String keyConsiderations = "";
					List<TipoSection> sections = new ArrayList<TipoSection>();
					String purpose = "";
					String alternatives = "";
					String attachments = "";
					String briefDescription = "";
					String performedBy = "";
					String mandatoryInput = "";
					String optionalInput = "";
					String output = "";
					String additionallyPerformedBy = "";
					String responsibleFor = "";
					
					if (eHijo.hasAttribute("xmi:version")){
						xmiVersion = eHijo.getAttribute("xmi:version");
					}
					if (eHijo.hasAttribute("xmlns:xmi")){
						xmi = eHijo.getAttribute("xmlns:xmi");
					}
					if (eHijo.hasAttribute("xmlns:org.eclipse.epf.uma")){
						uma = eHijo.getAttribute("xmlns:org.eclipse.epf.uma");
					}
					if (eHijo.hasAttribute("xmlns:epf")){
						epf = eHijo.getAttribute("xmlns:epf");
					}
					if (eHijo.hasAttribute("epf:version")){
						epfVersion = eHijo.getAttribute("epf:version");
					}
					if (eHijo.hasAttribute("xmi:id")){
						id = eHijo.getAttribute("xmi:id");
					}
					if (eHijo.hasAttribute("name")){
						name = eHijo.getAttribute("name");
					}
					if (eHijo.hasAttribute("guid")){
						guid = eHijo.getAttribute("guid");
					}
					if (eHijo.hasAttribute("presentationName")){
						presentationName = eHijo.getAttribute("presentationName");
					}
					if (eHijo.hasAttribute("authors")){
						authors = eHijo.getAttribute("authors");
					}
					if (eHijo.hasAttribute("changeDate")){
						changeDate = eHijo.getAttribute("changeDate"); // Es de la forma 2016-02-13T20:59:36.516-0300
						int index = changeDate.indexOf(".");
						changeDate = changeDate.substring(0, index);
					}
					if (eHijo.hasAttribute("version")){
						version = eHijo.getAttribute("version");
					}
					if (eHijo.hasAttribute("briefDescription")){
						briefDescription = eHijo.getAttribute("briefDescription");
						briefDescription = briefDescription.replaceAll("\"", "'");
					}
					if (eHijo.hasAttribute("performedBy")){
						performedBy = eHijo.getAttribute("performedBy");
					}
					if (eHijo.hasAttribute("mandatoryInput")){
						mandatoryInput = eHijo.getAttribute("mandatoryInput");
					}
					if (eHijo.hasAttribute("optionalInput")){
						optionalInput = eHijo.getAttribute("optionalInput");
					}
					if (eHijo.hasAttribute("output")){
						output = eHijo.getAttribute("output");
					}
					if (eHijo.hasAttribute("additionallyPerformedBy")){
						additionallyPerformedBy = eHijo.getAttribute("additionallyPerformedBy");
					}
					if (eHijo.hasAttribute("responsibleFor")){
						responsibleFor = eHijo.getAttribute("responsibleFor");
					}
				              
					NodeList childNodes = nodo.getChildNodes();
					int i = 0;
					while ((i < childNodes.getLength()) && 
						   (mainDescription.equals("") || keyConsiderations.equals("") || (sections.size() == 0) || 
							purpose.equals("") || alternatives.equals("") || attachments.equals(""))){
						Node child = childNodes.item(i);
						if (child.getNodeName().equals("mainDescription")){
							mainDescription = child.getFirstChild().getNodeValue();
							mainDescription = mainDescription.replace("src=\"./", "src=\"");
							mainDescription = mainDescription.replace("src=\"", "src=\"" + dirPrevia);
						}
						else if (child.getNodeName().equals("keyConsiderations")){
							keyConsiderations = child.getFirstChild().getNodeValue();
						}
						else if (child.getNodeName().equals("sections")){
							Element eChild = (Element) child;
							String xmiIdSection = "";
							String nameSection = "";
							String guidSection = "";
							if (eChild.hasAttribute("xmi:id")){
								xmiIdSection = eChild.getAttribute("xmi:id");
							}
							if (eChild.hasAttribute("name")){
								nameSection = eChild.getAttribute("name");
								nameSection = nameSection.replaceAll("\"", "'");
							}
							if (eChild.hasAttribute("guid")){
								guidSection = eChild.getAttribute("guid");
							}
							TipoSection s = new TipoSection(xmiIdSection, nameSection, guidSection);
							sections.add(s);
						}
						else if (child.getNodeName().equals("purpose")){
							purpose = child.getFirstChild().getNodeValue();
						}
						else if (child.getNodeName().equals("alternatives")){
							alternatives = child.getFirstChild().getNodeValue();
						}
						else if (child.getNodeName().equals("attachments")){
							attachments = dirPrevia + child.getFirstChild().getNodeValue();
						}
						i++;
					}
					
					TipoElemento tipoElemento = tag.equals(TipoTag.TASK_DESCRIPTION.toString()) ? TipoElemento.TASK :
												tag.equals(TipoTag.ARTIFACT_DESCRIPTION.toString()) ? TipoElemento.WORK_PRODUCT : 
												tag.equals(TipoTag.GUIDANCE_DESCRIPTION.toString()) ? TipoElemento.GUIDANCE : 
												tag.equals(TipoTag.SUPPORTING_MATERIAL_DESCRIPTION.toString()) ? TipoElemento.SUPPORTING_MATERIAL : null;
					
					return new TipoContentElement(tipoElemento, xmiVersion, xmi, uma, epf, epfVersion, id, name, guid, presentationName, authors, changeDate, version, 
												  briefDescription, performedBy, mandatoryInput, optionalInput, output, additionallyPerformedBy, responsibleFor,
												  mainDescription, keyConsiderations, sections, purpose, alternatives, attachments);
				}
        	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Retorna todos los elementos del archivo plugin que tengan el tag pasado como parámetro
	public static List<TipoContentElement> getElementsXMIPlugin(String nomFile, String tag){
		List<TipoContentElement> res = new ArrayList<TipoContentElement>();
		try{
			File inputFile = new File(nomFile);
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();

	        NodeList nodos = doc.getElementsByTagName("org.eclipse.epf.uma:MethodPlugin");
        	if (nodos.getLength() > 0){
        		int temp = 0;
				Node nodo = nodos.item(temp);
				List<Node> resNode = obtenerNodosType(nodo, tag);
				Iterator<Node> it = resNode.iterator();
				while (it.hasNext()){
					Node n = it.next();
					if (n.getNodeType() == Node.ELEMENT_NODE) {
						Element eHijo = (Element) n;
						String xmiVersion = "";
						String xmi = "";
						String uma = "";
						String epf = "";
						String epfVersion = "";
						String id = "";
						String name = "";
						String guid = "";
						String presentationName = "";
						String authors = "";
						String changeDate = "";
						String version = "";
						String mainDescription = "";
						String keyConsiderations = "";
						List<TipoSection> sections = new ArrayList<TipoSection>();
						String purpose = "";
						String alternatives = "";
						String attachments = "";
						String briefDescription = "";
						String performedBy = "";
						String mandatoryInput = "";
						String optionalInput = "";
						String output = "";
						String additionallyPerformedBy = "";
						String responsibleFor = "";
						
						if (eHijo.hasAttribute("xmi:version")){
							xmiVersion = eHijo.getAttribute("xmi:version");
						}
						if (eHijo.hasAttribute("xmlns:xmi")){
							xmi = eHijo.getAttribute("xmlns:xmi");
						}
						if (eHijo.hasAttribute("xmlns:org.eclipse.epf.uma")){
							uma = eHijo.getAttribute("xmlns:org.eclipse.epf.uma");
						}
						if (eHijo.hasAttribute("xmlns:epf")){
							epf = eHijo.getAttribute("xmlns:epf");
						}
						if (eHijo.hasAttribute("epf:version")){
							epfVersion = eHijo.getAttribute("epf:version");
						}
						if (eHijo.hasAttribute("xmi:id")){
							id = eHijo.getAttribute("xmi:id");
						}
						if (eHijo.hasAttribute("name")){
							name = eHijo.getAttribute("name");
						}
						if (eHijo.hasAttribute("guid")){
							guid = eHijo.getAttribute("guid");
						}
						if (eHijo.hasAttribute("presentationName")){
							presentationName = eHijo.getAttribute("presentationName");
						}
						if (eHijo.hasAttribute("authors")){
							authors = eHijo.getAttribute("authors");
						}
						if (eHijo.hasAttribute("changeDate")){
							changeDate = eHijo.getAttribute("changeDate"); // Es de la forma 2016-02-13T20:59:36.516-0300
							int index = changeDate.indexOf(".");
							changeDate = changeDate.substring(0, index);
						}
						if (eHijo.hasAttribute("version")){
							version = eHijo.getAttribute("version");
						}
						if (eHijo.hasAttribute("briefDescription")){
							briefDescription = eHijo.getAttribute("briefDescription");
							briefDescription = briefDescription.replaceAll("\"", "'");
						}
						if (eHijo.hasAttribute("performedBy")){
							performedBy = eHijo.getAttribute("performedBy");
						}
						if (eHijo.hasAttribute("mandatoryInput")){
							mandatoryInput = eHijo.getAttribute("mandatoryInput");
						}
						if (eHijo.hasAttribute("optionalInput")){
							optionalInput = eHijo.getAttribute("optionalInput");
						}
						if (eHijo.hasAttribute("output")){
							output = eHijo.getAttribute("output");
						}
						if (eHijo.hasAttribute("additionallyPerformedBy")){
							additionallyPerformedBy = eHijo.getAttribute("additionallyPerformedBy");
						}
						if (eHijo.hasAttribute("responsibleFor")){
							responsibleFor = eHijo.getAttribute("responsibleFor");
						}
						
						NodeList childNodes = n.getChildNodes();
						int i = 0;
						while ((i < childNodes.getLength()) && 
							   (mainDescription.equals("") || keyConsiderations.equals("") || (sections.size() == 0) || 
								purpose.equals("") || alternatives.equals("") || attachments.equals(""))){
							Node child = childNodes.item(i);
							if (child.getNodeName().equals("mainDescription")){
								mainDescription = child.getFirstChild().getNodeValue();
							}
							else if (child.getNodeName().equals("keyConsiderations")){
								keyConsiderations = child.getFirstChild().getNodeValue();
							}
							else if (child.getNodeName().equals("sections")){
								Element eChild = (Element) child;
								String xmiIdSection = "";
								String nameSection = "";
								String guidSection = "";
								if (eChild.hasAttribute("xmi:id")){
									xmiIdSection = eChild.getAttribute("xmi:id");
								}
								if (eChild.hasAttribute("name")){
									nameSection = eChild.getAttribute("name");
									nameSection = nameSection.replaceAll("\"", "'");
								}
								if (eChild.hasAttribute("guid")){
									guidSection = eChild.getAttribute("guid");
								}
								TipoSection s = new TipoSection(xmiIdSection, nameSection, guidSection);
								sections.add(s);
							}
							else if (child.getNodeName().equals("purpose")){
								purpose = child.getFirstChild().getNodeValue();
							}
							else if (child.getNodeName().equals("alternatives")){
								alternatives = child.getFirstChild().getNodeValue();
							}
							else if (child.getNodeName().equals("attachments")){
								attachments = child.getFirstChild().getNodeValue();
							}
							i++;
						}
						
						TipoElemento tipoElemento = tag.equals(TipoTag.TASK_DESCRIPTION.toString()) ? TipoElemento.TASK :
													tag.equals(TipoTag.ARTIFACT_DESCRIPTION.toString()) ? TipoElemento.WORK_PRODUCT : 
													(tag.equals(TipoTag.GUIDANCE_DESCRIPTION.toString()) || 
														tag.equals(TipoTag.GUIDANCE.toString())) ? TipoElemento.GUIDANCE : 
													(tag.equals(TipoTag.SUPPORTING_MATERIAL_DESCRIPTION.toString()) || 
														tag.equals(TipoTag.SUPPORTING_MATERIAL.toString())) ? TipoElemento.SUPPORTING_MATERIAL : 
													null;
						
						TipoContentElement tce = new TipoContentElement(tipoElemento, xmiVersion, xmi, uma, epf, epfVersion, id, name, guid, presentationName, authors, changeDate, version,
													briefDescription, performedBy, mandatoryInput, optionalInput, output, additionallyPerformedBy, responsibleFor,
												 	mainDescription, keyConsiderations, sections, purpose, alternatives, attachments);
						res.add(tce);
					}
				}
        	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;	
	}
	
	public static List<TipoMethodPackage> getElementsXMIProcessPackage(String nomFile){
		List<TipoMethodPackage> res = new ArrayList<TipoMethodPackage>();
		try{
			File inputFile = new File(nomFile);
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();

	        NodeList nodos = doc.getElementsByTagName("org.eclipse.epf.uma:MethodPlugin");
        	if (nodos.getLength() > 0){
        		int temp = 0;
				Node nodo = nodos.item(temp);
				List<Node> resNode = obtenerNodosType(nodo, "org.eclipse.epf.uma:ProcessPackage");
				Iterator<Node> it = resNode.iterator();
				while (it.hasNext()){
					Node n = it.next();
					Element e = (Element) n;
					String type = "";
					String id = "";
					String name = "";
					String guid = "";
					List<String> processComponentChild = new ArrayList<String>();
					if (e.hasAttribute("xsi:type")){
						type = e.getAttribute("xsi:type");
					}
					if (e.hasAttribute("xmi:id")){
						id = e.getAttribute("xmi:id");
					}
					if (e.hasAttribute("name")){
						name = e.getAttribute("name");
					}
					if (e.hasAttribute("guid")){
						guid = e.getAttribute("guid");
					}
					List<Node> child = obtenerHijosNodoType(n, "org.eclipse.epf.uma:ProcessComponent");
					Iterator<Node> itChild = child.iterator();
					while (itChild.hasNext()){
						Element eChild = (Element) itChild.next();
						if (eChild.hasAttribute("xmi:id")){
							processComponentChild.add(eChild.getAttribute("xmi:id"));
						}
					}
					TipoMethodPackage methodPackage = new TipoMethodPackage(type, id, name, guid);
					methodPackage.setProcessComponentChild(processComponentChild);
					res.add(methodPackage);
				}
        	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static List<Struct> getElementXMI(String nomFile){
		List<Struct> result = new ArrayList<Struct>(); 
		
		try {
			File inputFile = new File(nomFile);
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        
	        List<Variant> registroVar = new ArrayList<Variant>();
	        Map<String, List<Struct>> registroHijos = new HashMap<String,List<Struct>>();
	        Map<String,List<String>> vpToVar = new HashMap<String,List<String>>();
	        Map<Struct,String> performedPrimaryBy = new HashMap<Struct, String>();
	        Map<Struct,List<String>> performedAdditionallyBy = new HashMap<Struct, List<String>>();
	        Map<Struct,List<WorkProduct>> workProducts = new HashMap<Struct,List<WorkProduct>>();
	        Map<String, String[]> predecesores = new HashMap<String,String[]>();
			Map<String, String> dataProcess = new HashMap<String, String>();
	        doc.getDocumentElement().normalize();
	        NodeList nList = doc.getElementsByTagName("org.eclipse.epf.uma:ProcessComponent");
	        getNodos(nomFile, nList, result, registroVar, vpToVar, registroHijos, performedPrimaryBy, performedAdditionallyBy,workProducts, predecesores, dataProcess);
	        
	        // Seteo todos los elementos como hijos del elemento raiz (Delivery Process)
	        Iterator<Struct> itResult = result.iterator();
	        while (itResult.hasNext()){
	        	Struct s = itResult.next();
	        	if (s.getType() == TipoElemento.DELIVERY_PROCESS){
	        		List<Struct> hijos = registroHijos.get(s.getElementID());
	        		s.setHijos(hijos);
	        	}
	        }
	        
	        Iterator<Entry<String, List<Struct>>> iter = registroHijos.entrySet().iterator();
	        while (iter.hasNext()){
	        	Entry<String, List<Struct>> e = iter.next();
	        	String padre = e.getKey();
	        	List<Struct> l = e.getValue();
	        	boolean esHijoDeVar = false;
	        	Iterator<Variant> itV = registroVar.iterator();
		    	while (itV.hasNext()){
		    		Variant v = itV.next();
    		    	if (v.getID().equals(padre)){
    		    		v.getHijos().addAll(l);
    		    		esHijoDeVar = true;
    		    	}
    		    	else{
    		    		// Busco también en hijos de var
    		    		Iterator<Struct> itH = v.getHijos().iterator();
    		    		while (itH.hasNext()){
    		    			Struct s = itH.next();
    		    			Struct nodo = buscoEnHijos(s, padre);
    	    				if (nodo != null){
    	    					nodo.getHijos().addAll(l);
    	    					esHijoDeVar = true;
    	    				}
    		    			
    		    		}
    		    	}
    		    }
    		    if (!esHijoDeVar){
    		    	// Busco en result
    		    	result.addAll(l);
    		    }
	        }
	        
	        // Seteo variantes en varPoints
	        Iterator<Entry<String, List<Struct>>> iterH = registroHijos.entrySet().iterator();
	        while (iterH.hasNext()){
	        	Entry<String, List<Struct>> e = iterH.next();
	        	List<Struct> l = e.getValue();
	        	Iterator<Struct> it = l.iterator();
	        	while (it.hasNext()){
	        		Struct s = it.next();
	        		seteoVariantes(s,registroVar,vpToVar);
	        	}
	        	
	        }
	        
		    // Recorro performedPrimaryBy
		    Iterator<Entry<Struct,String>> iterator = performedPrimaryBy.entrySet().iterator();
		    while (iterator.hasNext()){
		    	Entry<Struct, String> e = iterator.next();
		        Struct tarea = e.getKey();
		        String role = e.getValue();
		        Iterator<Struct> it1 = result.iterator();
		        boolean encontre = false;
		        while (it1.hasNext() && !encontre){
		        	Struct s = it1.next();
		        	if(s.getElementID().equals(role)){
		        		tarea.getHijos().add(s);
		        		result.remove(s);
		        		encontre = true;
		        	}
		        }
		    }
		    
		    // Recorro performedAdditionallyBy
			Iterator<Entry<Struct,List<String>>> itera = performedAdditionallyBy.entrySet().iterator();
			while (itera.hasNext()){
				Entry<Struct, List<String>> e = itera.next();
				Struct tarea = e.getKey();
				List<String> roles = e.getValue();
				Iterator<String> itRoles = roles.iterator();
				while (itRoles.hasNext()){
					String rol  = itRoles.next();
			    	Iterator<Struct> it2 = result.iterator();
			    	boolean encontre = false;
			    	while (it2.hasNext() && !encontre){
			    		Struct s = it2.next();
			    		if(s.getElementID().equals(rol)){
			    			tarea.getHijos().add(s);
			    			result.remove(s);
			    			encontre = true;
			    		}
			    	}
				}
			}
		    
		    // Recorro workProducts
		    Iterator<Entry<Struct,List<WorkProduct>>> iterat = workProducts.entrySet().iterator();
			while (iterat.hasNext()){
				Entry<Struct, List<WorkProduct>> e = iterat.next();
				Struct tarea = e.getKey();
				Iterator<WorkProduct> itwp = e.getValue().iterator();
				while (itwp.hasNext()){
					WorkProduct workProd = itwp.next();
			    	Iterator<String> itWP = workProd.getWorkProducts().iterator();
			    	while (itWP.hasNext()){
			    		String wp  = itWP.next();
			    		Iterator<Struct> it3 = result.iterator();
			        	boolean encontre = false;
			        	while (it3.hasNext() && !encontre){
			        		Struct s = it3.next();
			        		if(s.getElementID().equals(wp)){
			        			tarea.getHijos().add(s);
			        			encontre = true;
			        		}
			        	}
			    	}
				}
			}
			
			// Ordeno result segun predecesores
			result = ordenoNodos(result,predecesores);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
    }

	public static void getNodos(String nomFile, NodeList nodos, List<Struct> result,List<Variant> registroVar, Map<String,List<String>> vpToVar, Map<String,List<Struct>> registroHijos, Map<Struct,String> performedPrimaryBy, Map<Struct,List<String>> performedAditionallyBy, Map<Struct,List<WorkProduct>> workProducts, Map<String,String[]> predecesores, Map<String, String> dataProcess){
		for (int temp = 0; temp < nodos.getLength(); temp++){
			Node nodo = nodos.item(temp);
			if (nodo.getNodeType() == Node.ELEMENT_NODE) {
				Element eHijo = (Element) nodo;
				if (nodo.getNodeName().equals("org.eclipse.epf.uma:ProcessComponent")){
					dataProcess.clear();
					dataProcess.put("processComponentId", eHijo.getAttribute("xmi:id"));
					dataProcess.put("processComponentName", eHijo.getAttribute("name"));
				}
				else if (nodo.getNodeName().equals("process")){
					String nameHijo = "";
					String type = "";
					String id = "";
					String description = "";
					String presentationName = "";
					String guid = "";
					String isPlanned = "true";
					String superActivities = "";
					String isOptional = "false";
					String variabilityType = "na";
					String isSynchronizedWithSource = "true";
					
					if (eHijo.hasAttribute("name")){
						nameHijo = eHijo.getAttribute("name");
					}
					if (eHijo.hasAttribute("xsi:type")){
						type = eHijo.getAttribute("xsi:type").substring(20);
					}
					if (eHijo.hasAttribute("xmi:id")){
						id = eHijo.getAttribute("xmi:id");
					}
					if (eHijo.hasAttribute("briefDescription")){
						description = eHijo.getAttribute("briefDescription");
						description = description.replaceAll("\"", "'");
					}
					if (eHijo.hasAttribute("presentationName")){
						presentationName = eHijo.getAttribute("presentationName");
					}
					if (eHijo.hasAttribute("guid")){
						guid = eHijo.getAttribute("guid");
					}
					if (eHijo.hasAttribute("isPlanned")){
						isPlanned = eHijo.getAttribute("isPlanned");
					}
					if (eHijo.hasAttribute("superActivities")){
						superActivities = eHijo.getAttribute("superActivities");
					}
					if (eHijo.hasAttribute("isOptional")){
						isOptional = eHijo.getAttribute("isOptional");
					}
					if (eHijo.hasAttribute("variabilityType")){
						variabilityType = eHijo.getAttribute("variabilityType");
					}
					if (eHijo.hasAttribute("isSynchronizedWithSource")){
						isSynchronizedWithSource = eHijo.getAttribute("isSynchronizedWithSource");
					}
					
					NodeList hijos = nodo.getChildNodes();
					int i = 0;
					while ((i < hijos.getLength()) && (!dataProcess.containsKey("presentationId"))){
						Node n = hijos.item(i);
						if ((n.getNodeType() == Node.ELEMENT_NODE) && (n.getNodeName().equals("presentation"))){
							dataProcess.put("presentationId", ((Element) n).getAttribute("xmi:id"));
						}
						i++;
					}
					
					TipoElemento tipo = obtenerTipoElemento(type);
					
					FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
		  			HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		  			VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
		     		
					String processComponentId = (dataProcess != null) ? dataProcess.get("processComponentId") : null;
					String processComponentName = (dataProcess != null) ? dataProcess.get("processComponentName") : null;
					String presentationId = (dataProcess != null) ? dataProcess.get("presentationId") : null;
					Struct h = new Struct(id, nameHijo, tipo,-1,-1, obtenerIconoPorTipo(tipo), processComponentId, processComponentName, presentationId, null);
					h.setDescription(description);
					h.setPresentationName(presentationName);
	      		    h.setGuid(guid);
	      		    h.setIsPlanned(isPlanned);
	      		    h.setSuperActivities(superActivities);
	      		    h.setIsOptional(isOptional);
	      			h.setVariabilityType(variabilityType);
	      			h.setIsSynchronizedWithSource(isSynchronizedWithSource);
	      			if ((tipo == TipoElemento.DELIVERY_PROCESS) || (tipo == TipoElemento.CAPABILITY_PATTERN)){
		      			String[] res = separarDireccion(nomFile);
		      			String nameDir = res[0];
		      			File f = new File(nameDir + "diagram.xmi");
		      			if (f.exists()){
			      			int index = nameDir.indexOf(ReadProperties.getProperty("destinoDescargas"));
			      			if (index != -1){
			      				nameDir = nameDir.substring(ReadProperties.getProperty("destinoDescargas").length(), nameDir.length());
			      				// nameDir = 'dirPlugin/lineProcess/lineProcessName/...' o 'dirPlugin/...'
			      				String strBuscado = "lineprocess";
			      				index = nameDir.indexOf(strBuscado);
				      			if (index != -1){
				      				nameDir = nameDir.substring(index + 1 + strBuscado.length(), nameDir.length());
				      				index = nameDir.indexOf("/");
				      				if (index != -1){
				      					nameDir = vb.getDirPlugin() + nameDir.substring(index + 1, nameDir.length());
				      					copiarArchivo(f, nameDir, "diagram.xmi");
						      			vb.addDiagram(ReadProperties.getProperty("destinoDescargas") + nameDir + "diagram.xmi");
				      				}
				      			}
				      			h.setDiagramURI(nameDir + "diagram.xmi");
			      			}
		      			}
		      			else{
		      				h.setDiagramURI("");
		      			}
	      			}
					result.add(h);
				}
				else if (nodo.getNodeName().equals("processElements")){
					String nameHijo = "";
					String type = "";
					String id = "";
					String description = "";
					String presentationName = "";
					String perfPrimaryBy = "";
					String perfAdditionallyBy = "";
					String mandatoryInputs = "";
					String optionalInputs = "";
					String externalInputs = "";
					String outputs = "";
					String responsableDe = "";
					String modifica = "";
					String[] predecesoresList = {"", ""};
					List<WorkProduct> lwp = new ArrayList<WorkProduct>();
					String pred = "";
					String guid = "";
					String isPlanned = "true";
					String superActivities = "";
					String isOptional = "false";
					String variabilityType = "na";
					String isSynchronizedWithSource = "true";
					
					if (eHijo.hasAttribute("name")){
						nameHijo = eHijo.getAttribute("name");
					}
					if (eHijo.hasAttribute("xsi:type")){
						type = eHijo.getAttribute("xsi:type").substring(20);
					}
					if (eHijo.hasAttribute("xmi:id")){
						id = eHijo.getAttribute("xmi:id");
					}
					if (eHijo.hasAttribute("briefDescription")){
						description = eHijo.getAttribute("briefDescription");
						description = description.replaceAll("\"", "'");
					}
					if (eHijo.hasAttribute("presentationName")){
						presentationName = eHijo.getAttribute("presentationName");
					}
					if (eHijo.hasAttribute("guid")){
						guid = eHijo.getAttribute("guid");
					}
					if (eHijo.hasAttribute("isPlanned")){
						isPlanned = eHijo.getAttribute("isPlanned");
					}
					if (eHijo.hasAttribute("superActivities")){
						superActivities = eHijo.getAttribute("superActivities");
					}
					if (eHijo.hasAttribute("isOptional")){
						isOptional = eHijo.getAttribute("isOptional");
					}
					if (eHijo.hasAttribute("variabilityType")){
						variabilityType = eHijo.getAttribute("variabilityType");
					}
					if (eHijo.hasAttribute("isSynchronizedWithSource")){
						isSynchronizedWithSource = eHijo.getAttribute("isSynchronizedWithSource");
					}
					
	      		    int min = -1;
	      		    int max = -1;
	      		    
	      		    TipoElemento tipo = obtenerTipoElemento(type);
	      		    Struct h = null;
	      		    
		      		FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
		  			HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		  			VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
		  			Iterator<String> itCapabilityPatterns = vb.getPlugin().getCapabilityPatternsDir().iterator();
		  			boolean fin = false;
	  				String dirArchivo = "";
	  				String nomArchivo = "";
		  			while ((itCapabilityPatterns.hasNext()) && (!fin)){
		  				String directorioCapabilityPattern = itCapabilityPatterns.next();
		  				int indexDiv = directorioCapabilityPattern.indexOf("/");
		  				dirArchivo = "";
		  				while (indexDiv != -1){
		  					String dir = directorioCapabilityPattern.substring(0, indexDiv);
		  					dir = dir.replace("%20", " ");
		  					directorioCapabilityPattern = directorioCapabilityPattern.substring(indexDiv + 1, directorioCapabilityPattern.length());
		  					dirArchivo += dir + "/";
		  					nomArchivo = directorioCapabilityPattern;
		  					indexDiv = directorioCapabilityPattern.indexOf("/");
		  					fin = dir.equals(nameHijo);
		  				}
		  			}
		  			String nombreArchivoCapabilityPattern = "";
		  			if (fin){
		  				nombreArchivoCapabilityPattern = ReadProperties.getProperty("destinoDescargas") + vb.getDirectorioArchivo() + dirArchivo + nomArchivo;
		  			}
      		    	File inputFile = new File(nombreArchivoCapabilityPattern);
      		    	// Si es un capability pattern y está el archivo descargado => Lo parseo
	      		    if ((tipo == TipoElemento.CAPABILITY_PATTERN) && (inputFile.isFile())){
	      		    	List<Struct> nodosCP = XMIParser.getElementXMI(nombreArchivoCapabilityPattern);
	      		    	if (nodosCP.size() > 0){
	      		    		h = nodosCP.get(0);
	      		    		int n = nodosCP.size();
	      		    		List<Struct> hijosH = new ArrayList<Struct>();
	      		    		for (int i = 1; i < n; i++){
	      		    			hijosH.add(nodosCP.get(i));
	      		    		}
	      		    		h.setHijos(hijosH);
	      		    		h.setElementIDExtends(id);
	      		    		h.setProcessComponentPresentationName(presentationName);
			      		    h.setSuperActivities(superActivities);
			      		    
			      		    // Cargo otros datos del capability pattern
			      		    NodeList hijos = nodo.getChildNodes();
							int i = 0;
							int nHijios = hijos.getLength();
							List<TipoMethodElementProperty> methodElementProperties = new ArrayList<TipoMethodElementProperty>(); 
							while (i < nHijios){
								Node nodoHijo= hijos.item(i);
								if (nodoHijo.getNodeType() == Node.ELEMENT_NODE) {
									if (nodoHijo.getNodeName().equals("methodElementProperty")){
										Element eNodoHijo = (Element) nodoHijo;
										String xmiId = "";
										String name = "";
										String value = "";
										if (eNodoHijo.hasAttribute("xmi:id")){
											xmiId = eNodoHijo.getAttribute("xmi:id");
										}
										if (eNodoHijo.hasAttribute("name")){
											name = eNodoHijo.getAttribute("name");
										}
										if (eNodoHijo.hasAttribute("value")){
											value = eNodoHijo.getAttribute("value");
										}
										TipoMethodElementProperty elementProperty = new TipoMethodElementProperty(xmiId, name, value);
										methodElementProperties.add(elementProperty);
									}
								}
								i++;
							}
							h.setMethodElementProperties(methodElementProperties);
	      		    	}
	      		    }
	      		    else{
	      		    	String processComponentId = (dataProcess != null) ? dataProcess.get("processComponentId") : null;
						String processComponentName = (dataProcess != null) ? dataProcess.get("processComponentName") : null;
						String presentationId = (dataProcess != null) ? dataProcess.get("presentationId") : null;
		      		    h = new Struct(id, nameHijo, tipo,min,max, obtenerIconoPorTipo(tipo), processComponentId, processComponentName, presentationId, null);
		      		    h.setDescription(description);
		      		    h.setPresentationName(presentationName);
		      		    h.setGuid(guid);
		      		    h.setIsPlanned(isPlanned);
		      		    h.setSuperActivities(superActivities);
		      		    h.setIsOptional(isOptional);
		      			h.setVariabilityType(variabilityType);
		      			h.setIsSynchronizedWithSource(isSynchronizedWithSource);
		      			if ((tipo != TipoElemento.DELIVERY_PROCESS) && (tipo != TipoElemento.CAPABILITY_PATTERN)){
		      				h.setDiagramURI("");
		      			}
		      			
						if (eHijo.hasAttribute("linkToPredecessor")){
							List<String> list = new ArrayList<String>();
							pred = eHijo.getAttribute("linkToPredecessor");
							list = Arrays.asList(pred.split("\\s"));
							h.setLinkToPredecessor(list);
						}
		      		    
						if (tipo == TipoElemento.TASK || tipo == TipoElemento.VP_TASK){
							if (eHijo.hasAttribute("performedPrimarilyBy")){
								perfPrimaryBy = eHijo.getAttribute("performedPrimarilyBy");
								h.setPerformedPrimaryBy(perfPrimaryBy);
								performedPrimaryBy.put(h, perfPrimaryBy);
							}
							if (eHijo.hasAttribute("additionallyPerformedBy")){
								List<String> list = new ArrayList<String>();
								perfAdditionallyBy = eHijo.getAttribute("additionallyPerformedBy");
								list = Arrays.asList(perfAdditionallyBy.split("\\s"));
								
								h.setPerformedAditionallyBy(list);
								performedAditionallyBy.put(h, list);
							}
							if (eHijo.hasAttribute("mandatoryInput")){
								List<String> list = new ArrayList<String>();
								mandatoryInputs = eHijo.getAttribute("mandatoryInput");
								list = Arrays.asList(mandatoryInputs.split("\\s"));
								h.setMandatoryInputs(list);
								WorkProduct wp = new WorkProduct("mandatoryInput", list);
								lwp.add(wp);
							}
							if (eHijo.hasAttribute("optionalInput")){
								List<String> list = new ArrayList<String>();
								optionalInputs = eHijo.getAttribute("optionalInput");
								list = Arrays.asList(optionalInputs.split("\\s"));
								h.setOptionalInputs(list);
								WorkProduct wp = new WorkProduct("optionalInput", list);
								lwp.add(wp);
							}
							if (eHijo.hasAttribute("externalInput")){
								List<String> list = new ArrayList<String>();
								externalInputs = eHijo.getAttribute("externalInput");
								list = Arrays.asList(externalInputs.split("\\s"));
								h.setExternalInputs(list);
								WorkProduct wp = new WorkProduct("externalInput", list);
								lwp.add(wp);
							}
							if (eHijo.hasAttribute("output")){
								List<String> list = new ArrayList<String>();
								outputs = eHijo.getAttribute("output");
								list = Arrays.asList(outputs.split("\\s"));
								h.setOutputs(list);
								WorkProduct wp = new WorkProduct("outputs", list);
								lwp.add(wp);
							}
							
							workProducts.put(h,lwp);
							
							// Cargo otros datos de la task
							boolean salir = false;
							NodeList hijos = nodo.getChildNodes();
							List<TipoSection> steps = null;
							int i = 0;
							while ((i < hijos.getLength()) && !salir){
								Node nodoHijo= hijos.item(i);
								if (nodoHijo.getNodeType() == Node.ELEMENT_NODE) {
									if (nodoHijo.getNodeName().equals("Task")){
										Element eNodoHijo = (Element) nodoHijo;
										String href = "";
										if (eNodoHijo.hasAttribute("href")){
											href = eNodoHijo.getAttribute("href");
											int index = href.indexOf("#");
											href = href.substring(index + 1, href.length());
										}
										h.setIdTask(href);
										
										List<TipoContentPackage> contentPackages = vb.getContentPackages();
										if (contentPackages != null){
											Iterator<TipoContentPackage> iter = contentPackages.iterator();
											fin = false; 
											while (iter.hasNext() && (!fin)){
												TipoContentPackage tcp = iter.next();
												Iterator<TipoContentElement> iterTcp = tcp.getTasksCP().iterator();
												while (iterTcp.hasNext() && (!fin)){
													TipoContentElement tce = iterTcp.next();
													if (tce.getId().equals(href)){
														TipoContentElement content = tce.getContentDescription();
														steps = (content != null) ? content.getSections() : null;
														fin = true;
													}
												}
											}
										}
									}
									else if (nodoHijo.getNodeName().equals("selectedSteps")){
										Element eNodoHijo = (Element) nodoHijo;
										String href = "";
										if (eNodoHijo.hasAttribute("href")){
											href = eNodoHijo.getAttribute("href");
											int index = href.indexOf("#");
											href = href.substring(index + 1, href.length());
										}

										String nameStep = "";
										String guidStep = "";
										if (steps != null){
											Iterator<TipoSection> iterSteps = steps.iterator();
											while (iterSteps.hasNext() && (nameStep.equals("") && (guidStep.equals("")))){
												TipoSection step = iterSteps.next();
												if (step.getXmiId().equals(href)){
													nameStep = step.getName();
													guidStep = step.getGuid();
												}
											}
										}
										
										TipoSection ts = new TipoSection(href, nameStep, guidStep);
										h.getSteps().add(ts);
									}
								}
								i++;
							}
						}
						else if (tipo == TipoElemento.ROLE || tipo == TipoElemento.VP_ROLE){
							if (eHijo.hasAttribute("responsibleFor")){
								List<String> list = new ArrayList<String>();
								responsableDe = eHijo.getAttribute("responsibleFor");
								list = Arrays.asList(responsableDe.split("\\s"));
								h.setResponsableDe(list);
							}
							if (eHijo.hasAttribute("modifies")){
								List<String> list = new ArrayList<String>();
								modifica = eHijo.getAttribute("modifies");
								list = Arrays.asList(modifica.split("\\s"));
								h.setModifica(list);
							}
							boolean salir = false;
							NodeList hijos = nodo.getChildNodes();
							int i = 0;
							while ((i < hijos.getLength()) && !salir){
								Node nodoHijo= hijos.item(i);
								if (nodoHijo.getNodeType() == Node.ELEMENT_NODE) {
									if (nodoHijo.getNodeName().equals("Role")){
										Element eNodoHijo = (Element) nodoHijo;
										
										String href = "";
										if (eNodoHijo.hasAttribute("href")){
											href = eNodoHijo.getAttribute("href");
											int index = href.indexOf("#");
											href = href.substring(index + 1, href.length());
										}
										h.setIdRole(href);
										salir = true;
									}
								}
								i++;
							}
						}
						else if (tipo == TipoElemento.WORK_PRODUCT || tipo == TipoElemento.VP_WORK_PRODUCT){
							boolean salir = false;
							NodeList hijos = nodo.getChildNodes();
							int i = 0;
							while ((i < hijos.getLength()) && !salir){
								Node nodoHijo= hijos.item(i);
								if (nodoHijo.getNodeType() == Node.ELEMENT_NODE) {
									if (nodoHijo.getNodeName().equals("WorkProduct")){
										Element eNodoHijo = (Element) nodoHijo;
										
										String href = "";
										if (eNodoHijo.hasAttribute("href")){
											href = eNodoHijo.getAttribute("href");
											int index = href.indexOf("#");
											href = href.substring(index + 1, href.length());
										}
										h.setIdWorkProduct(href);
										salir = true;
									}
								}
								i++;
							}
						}
					}
					
	      		    if (h != null){
						boolean tienePadre = false;
						if (eHijo.hasAttribute("superActivities") &&
							!(type.equals(TipoElemento.VAR_ACTIVITY.toString()) &&
							  !(type.equals("WorkOrder")) ||
							  type.equals(TipoElemento.VAR_PHASE.toString())	   ||
							  type.equals(TipoElemento.VAR_ITERATION.toString()) ||
							  type.equals(TipoElemento.VAR_TASK.toString())      ||
							  type.equals(TipoElemento.VAR_ROLE.toString())      ||
							  type.equals(TipoElemento.VAR_MILESTONE.toString()) ||
							  type.equals(TipoElemento.VAR_WORK_PRODUCT.toString()) )) {
							// Me fijo si es hijo de alguien
							tienePadre = true;
							String padre = eHijo.getAttribute("superActivities");
							
							boolean esHijoDeVar = false;
							// busco en las variantes y en sus hijos
							Iterator<Variant> itV = registroVar.iterator();
							while (itV.hasNext()){
								Variant v = itV.next();
								if (v.getID().equals(padre)){
									v.getHijos().add(h);
									esHijoDeVar = true;
								}
								else{
									List<Struct> hijos = v.getHijos();
					    			Iterator<Struct> it = hijos.iterator();
					    			while (it.hasNext()){
					    				Struct s = it.next();
					    				if (s.getElementID().equals(padre)){
					    					s.getHijos().add(h);
					    					esHijoDeVar = true;
					    				}
					    				else{
						    				Struct n = buscoEnHijos(s, padre);
						    				if (n != null){
						    					n.getHijos().add(h);
						    					esHijoDeVar = true;
						    				}
					    				}
					    			}
								}
							}
							if (!esHijoDeVar){
								boolean elPadreEsHijo = false;
								// Busco el padre en result
								Struct padreS = buscoPadre(padre,result);
								if (padreS != null){
									padreS.getHijos().add(h);
									tienePadre = true;
								}
								// Veo si el padre ya no esta en registroHijos como hijo de alguien
				  		    	else {
				  		    		Struct s = buscoPadre (registroHijos, padre);
				  		    		if (s != null){
				  		    			s.getHijos().add(h);
				  		    			elPadreEsHijo = true;
				  		    		}
				  		    	}
								
							  	if(!elPadreEsHijo){
				      		    	if (!registroHijos.containsKey(padre)){
				      		    		List<Struct> l = new ArrayList<Struct>();
				      		    		l.add(h);
				      		    		registroHijos.put(padre, l);
				      		    	}
				      		    	else {
				      		    		registroHijos.get(padre).add(h);
				      		    	}
				  		    	} 
					    	}
							
					  	}
						
						// Veo si es Padre de alguien
		      		    List<Struct> hijosS = new ArrayList<Struct>();
		      		    if (registroHijos.containsKey(id)){
		      		    	hijosS = registroHijos.get(id);
		      		    	registroHijos.remove(id);
		      		    }
		      		    if ((hijosS != null)){
							h.getHijos().addAll(hijosS);
						}
						
		      		    if (type.equals(TipoElemento.VP_ACTIVITY.toString()) ||
	          				type.equals(TipoElemento.VP_PHASE.toString()) ||
	          				type.equals(TipoElemento.VP_ITERATION.toString()) ||
	          				type.equals(TipoElemento.VP_TASK.toString()) ||
	          				type.equals(TipoElemento.VP_ROLE.toString()) ||
	          				type.equals(TipoElemento.VP_MILESTONE.toString()) ||
	          				type.equals(TipoElemento.VP_WORK_PRODUCT.toString()) ){
		      		    	if (eHijo.hasAttribute("min")) {
		      		    		min = Integer.parseInt(eHijo.getAttribute("min"));
		      		    		h.setMin(min);
		      		    	}
	      		    		if (eHijo.hasAttribute("max")) {
	  		    				max = Integer.parseInt(eHijo.getAttribute("max"));
	  		    				h.setMax(max);
	  		    			}
	            			
	      			   		List<String> variantes = new ArrayList<String>();
	      			   		// Obtengo lista de elementos hijos del nodo
	      			   		NodeList nHijosVar = nodo.getChildNodes();
	      			   		int length = nHijosVar.getLength();
	      			   		for (int temp2 = 0; temp2 < length; temp2++) {
	  			   				Node nHijoVar = nHijosVar.item(temp2);
	  			   				if ((nHijoVar.getNodeType() == Node.ELEMENT_NODE) && (nHijoVar.getNodeName().equals("client"))){
	  			   					Element eHijoVar = (Element) nHijoVar;
	  			   					if  (eHijoVar.getAttribute("xsi:type").substring(20).equals("varp2variant")){
			   							String iDVariant = eHijoVar.getAttribute("supplier");
			   							variantes.add(iDVariant);
	  			   					}
	  			   				}
	      			   		}
	      			   		vpToVar.put(id, variantes);
		      		    }
		      		    
		      		    if (type.equals(TipoElemento.VAR_ACTIVITY.toString())  ||
	          				type.equals(TipoElemento.VAR_PHASE.toString())	   ||
	          				type.equals(TipoElemento.VAR_ITERATION.toString()) ||
	          				type.equals(TipoElemento.VAR_TASK.toString()) ||
	          				type.equals(TipoElemento.VAR_ROLE.toString()) ||
	          				type.equals(TipoElemento.VAR_MILESTONE.toString()) ||
	          				type.equals(TipoElemento.VAR_WORK_PRODUCT.toString())){
	      		    		
		      		    	String processComponentId = (dataProcess != null) ? dataProcess.get("processComponentId") : null;
							String processComponentName = (dataProcess != null) ? dataProcess.get("processComponentName") : null;
							String presentationId = (dataProcess != null) ? dataProcess.get("presentationId") : null;
		      		    	Variant var = new Variant(id, nameHijo, presentationName, "", true, type, processComponentId, processComponentName, presentationId, null);
		      		    	var.getHijos().addAll(hijosS);
	            			registroVar.add(var);
	            			var.setIsPlanned(isPlanned);
	            			var.setIsOptional(isOptional);
	            			var.setVariabilityType(variabilityType);
	 		      		    var.setSuperActivities(superActivities);
	            			var.setDescription(description);
	 		      		    var.setGuid(guid);
	 		      			var.setIsSynchronizedWithSource(isSynchronizedWithSource);
	 		      			var.setPresentationName(presentationName);
		      		    	
		      		    	NodeList nHijosVar = nodo.getChildNodes();
	                    	for (int temp3 = 0; temp3 < nHijosVar.getLength(); temp3++) {
	                    		Node nHijoVar = nHijosVar.item(temp3);
	                    		
	                    		if ((nHijoVar.getNodeType() == Node.ELEMENT_NODE) && (nHijoVar.getNodeName().equals("client"))){
	                    			Element eHijoVar = (Element) nHijoVar;
	                				if  (eHijoVar.getAttribute("xsi:type").substring(20).equals("variant2variant")){
	                					if (eHijoVar.hasAttribute("isInclusive")){
	                						String iDVariantInclusiva = eHijoVar.getAttribute("supplier");
	                						var.getInclusivas().add(iDVariantInclusiva);
	                					}
	                					else {
	                						String iDVariantExclusiva = eHijoVar.getAttribute("supplier");
	                						var.getExclusivas().add(iDVariantExclusiva);
	                					}
	                				}
	                    		}
	                    	}
		      		    }
		      		    else if(type.equals("WorkOrder")){
		      		    	if (eHijo.hasAttribute("pred")){
								predecesoresList[0] = eHijo.getAttribute("pred");
								predecesores.put(id, predecesoresList);
							}
		      		    	else{
			      		    	NodeList nHijosVar = nodo.getChildNodes();
		                    	for (int temp3 = 0; temp3 < nHijosVar.getLength(); temp3++) {
		                    		Node nHijoVar = nHijosVar.item(temp3);
		                    		
		                    		if (nHijoVar.getNodeType() == Node.ELEMENT_NODE){
		                    			Element eHijoVar = (Element) nHijoVar;
	                    				predecesoresList = predecesores.get(id);
	                    				if (predecesoresList == null){
	                    					predecesoresList = new String[]{"", ""};
	                    				}
		                    			if (nHijoVar.getNodeName().equals("pred")){
											if (eHijoVar.hasAttribute("href")){
												String href = eHijoVar.getAttribute("href");
			                    				String[] res = href.split("#");
			                    				predecesoresList[0] = (res.length > 0) ? res[1] : "";
												predecesores.put(id, predecesoresList);
											}
		                    			}
		                    			if (nHijoVar.getNodeName().equals("methodElementProperty")){
		        							String eName = "";
		        							String eValue = "";
		        							if (eHijoVar.hasAttribute("name")){
		        								eName = eHijoVar.getAttribute("name");							
		        							}
		        							if (eHijoVar.hasAttribute("value")){
		        								eValue = eHijoVar.getAttribute("value");
		        							}
		        							predecesoresList[1] = "name=" + eName + "&#xA;value=" + eValue + "&#xA;";
		        							predecesores.put(id, predecesoresList);
		                    			}
		                    		}
		                    	}
		      		    	}
		      		    }
		      		    else if(id != null && nameHijo != null && type != null && !tienePadre){
		      		    	if (hijosS != null ){
		      		    		h.getHijos().addAll(hijosS);
		      		    	}
		      		    	result.add(h);
		      		    }
	      		    }
				}
				NodeList hijos = nodo.getChildNodes();
				getNodos(nomFile, hijos, result, registroVar, vpToVar, registroHijos, performedPrimaryBy, performedAditionallyBy, workProducts, predecesores, dataProcess);
			}
		}
	}

	public static String getNombreProcesoXMI(String nomFile){
		try {
			File inputFile = new File(nomFile);
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();
	        NodeList nList = doc.getElementsByTagName("org.eclipse.epf.uma:ProcessComponent");
	        int i = 0;
	        int n = nList.getLength();
	        while (i < n){
	        	Node nodo = nList.item(i);
	        	if (nodo.getNodeType() == Node.ELEMENT_NODE){
					Element elem = (Element) nodo;
					if (elem.hasAttribute("name")){
						return elem.getAttribute("name");
					}
	        	}
	        	i++;
	        }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "";
    }

	public static TipoElemento obtenerTipoElemento(String t){
		TipoElemento type = (t.equals(TipoElemento.PROCESS_PACKAGE.toString()))    ? TipoElemento.PROCESS_PACKAGE 	 :
    				   		(t.equals(TipoElemento.ACTIVITY.toString()))	       ? TipoElemento.ACTIVITY		   	 :
			   			 	(t.equals(TipoElemento.VP_ACTIVITY.toString()))        ? TipoElemento.VP_ACTIVITY	   	 :
		   			 		(t.equals(TipoElemento.VAR_ACTIVITY.toString()))       ? TipoElemento.VAR_ACTIVITY	   	 :
		   			 		(t.equals(TipoElemento.TASK.toString()))    		   ? TipoElemento.TASK			   	 :
		   			 		(t.equals(TipoElemento.VP_TASK.toString()))    		   ? TipoElemento.VP_TASK		   	 :
		   			 		(t.equals(TipoElemento.VAR_TASK.toString()))    	   ? TipoElemento.VAR_TASK		   	 :
		   			 		(t.equals(TipoElemento.ITERATION.toString()))    	   ? TipoElemento.ITERATION	   		 :
		   			 		(t.equals(TipoElemento.VP_ITERATION.toString()))       ? TipoElemento.VP_ITERATION	   	 :
		   			 		(t.equals(TipoElemento.VAR_ITERATION.toString()))      ? TipoElemento.VAR_ITERATION   	 :
		   			 		(t.equals(TipoElemento.PHASE.toString()))    	   	   ? TipoElemento.PHASE		   		 :
		   			 		(t.equals(TipoElemento.VP_PHASE.toString()))    	   ? TipoElemento.VP_PHASE		   	 :
		   			 		(t.equals(TipoElemento.VAR_PHASE.toString()))  		   ? TipoElemento.VAR_PHASE	   		 :
			   			 	(t.equals(TipoElemento.CAPABILITY_PATTERN.toString())) ? TipoElemento.CAPABILITY_PATTERN :
			   			 	(t.equals(TipoElemento.DELIVERY_PROCESS.toString()))   ? TipoElemento.DELIVERY_PROCESS 	 :
				   			(t.equals(TipoElemento.MILESTONE.toString()))		   ? TipoElemento.MILESTONE 		 :
				   			(t.equals(TipoElemento.VP_MILESTONE.toString()))	   ? TipoElemento.VP_MILESTONE 		 :
				   			(t.equals(TipoElemento.VAR_MILESTONE.toString()))	   ? TipoElemento.VAR_MILESTONE 	 :
				   			(t.equals(TipoElemento.ROLE.toString()))			   ? TipoElemento.ROLE 				 :
				   			(t.equals(TipoElemento.VP_ROLE.toString()))			   ? TipoElemento.VP_ROLE 			 :
				   			(t.equals(TipoElemento.VAR_ROLE.toString()))		   ? TipoElemento.VAR_ROLE 			 :
				   			(t.equals(TipoElemento.WORK_PRODUCT.toString()))	   ? TipoElemento.WORK_PRODUCT 		 :
				   			(t.equals(TipoElemento.VP_WORK_PRODUCT.toString()))	   ? TipoElemento.VP_WORK_PRODUCT    :
				   			(t.equals(TipoElemento.VAR_WORK_PRODUCT.toString()))   ? TipoElemento.VAR_WORK_PRODUCT 	 :
	   			 			null;
    	return type;
    }

	public static String obtenerIconoPorTipo(TipoElemento tipo){
    	String icono = (tipo == TipoElemento.PROCESS_PACKAGE) 	 ? TipoElemento.PROCESS_PACKAGE.getImagen()    :
    				   (tipo == TipoElemento.ACTIVITY)	      	 ? TipoElemento.ACTIVITY.getImagen()		   :
    				   (tipo == TipoElemento.VP_ACTIVITY)     	 ? TipoElemento.VP_ACTIVITY.getImagen()	 	   :
    				   (tipo == TipoElemento.VAR_ACTIVITY)    	 ? TipoElemento.VAR_ACTIVITY.getImagen()	   :
    				   (tipo == TipoElemento.TASK)     	      	 ? TipoElemento.TASK.getImagen()	 		   :
    				   (tipo == TipoElemento.VP_TASK)     	  	 ? TipoElemento.VP_TASK.getImagen()	 	 	   :
    				   (tipo == TipoElemento.VAR_TASK)        	 ? TipoElemento.VAR_TASK.getImagen()		   :
    				   (tipo == TipoElemento.ITERATION)    	  	 ? TipoElemento.ITERATION.getImagen()	   	   :
   		   			   (tipo == TipoElemento.VP_ITERATION)   	 ? TipoElemento.VP_ITERATION.getImagen()	   :
   		   			   (tipo == TipoElemento.VAR_ITERATION)   	 ? TipoElemento.VAR_ITERATION.getImagen()      :
   		   			   (tipo == TipoElemento.PHASE)    		  	 ? TipoElemento.PHASE.getImagen()		       :
   		   			   (tipo == TipoElemento.VP_PHASE)   	  	 ? TipoElemento.VP_PHASE.getImagen()		   :
   		   			   (tipo == TipoElemento.VAR_PHASE)	      	 ? TipoElemento.VAR_PHASE.getImagen()	       :
   		   			   (tipo == TipoElemento.CAPABILITY_PATTERN) ? TipoElemento.CAPABILITY_PATTERN.getImagen() :
   		   			   (tipo == TipoElemento.DELIVERY_PROCESS)	 ? TipoElemento.DELIVERY_PROCESS.getImagen()   :
   		   			   (tipo == TipoElemento.MILESTONE)		  	 ? TipoElemento.MILESTONE.getImagen() 		   :
				   	   (tipo == TipoElemento.VP_MILESTONE)	  	 ? TipoElemento.VP_MILESTONE.getImagen() 	   :
				   	   (tipo == TipoElemento.VAR_MILESTONE)   	 ? TipoElemento.VAR_MILESTONE.getImagen() 	   :
				   	   (tipo == TipoElemento.ROLE)			  	 ? TipoElemento.ROLE.getImagen() 			   :
				   	   (tipo == TipoElemento.VP_ROLE)		  	 ? TipoElemento.VP_ROLE.getImagen() 		   :
				   	   (tipo == TipoElemento.VAR_ROLE)		  	 ? TipoElemento.VAR_ROLE.getImagen() 		   :
				   	   (tipo == TipoElemento.WORK_PRODUCT)	  	 ? TipoElemento.WORK_PRODUCT.getImagen() 	   :
				   	   (tipo == TipoElemento.VP_WORK_PRODUCT) 	 ? TipoElemento.VP_WORK_PRODUCT.getImagen()    :
				   	   (tipo == TipoElemento.VAR_WORK_PRODUCT)	 ? TipoElemento.VAR_WORK_PRODUCT.getImagen()   :
    				   "";
    	return icono;
    }
    
    public static Struct buscoPadre(String id, List<Struct> lista){
    	Struct padre = null;
    	Iterator<Struct> it = lista.iterator();
    	while (it.hasNext() && padre == null){
    		Struct s = it.next();
    		if( s.getElementID().equals(id)){
    			padre = s;
    		}
    		else {
    			padre = buscoPadre(id, s.getHijos());
    		}
    	}
    	
    	return padre;
    }
    
    public static List<Struct> buscoHermanos(String id, List<Struct> lista){
    	List<Struct> hermanos = null;
    	Iterator<Struct> it = lista.iterator();
    	while (it.hasNext() && hermanos == null){
    		Struct s = it.next();
    		if( s.getElementID().equals(id)){
    			hermanos = lista;
    		}
    		else {
    			hermanos = buscoHermanos(id, s.getHijos());
    		}
    	}
    	
    	return hermanos;
    }
    
    public static void seteoVariantes(Struct s,List<Variant> registroVar, Map<String,List<String>> vpToVar){
        if (s.getType() == TipoElemento.VP_ACTIVITY ||
    		s.getType() == TipoElemento.VP_TASK		||
         	s.getType() == TipoElemento.VP_PHASE	||
         	s.getType() == TipoElemento.VP_ITERATION ||
         	s.getType() == TipoElemento.VP_MILESTONE ||
         	s.getType() == TipoElemento.VP_ROLE	||
         	s.getType() == TipoElemento.VP_WORK_PRODUCT){
    		Iterator<Variant> itaux = registroVar.iterator();
         	while (itaux.hasNext()){
         		Variant v = itaux.next();
         		if (vpToVar.get(s.getElementID())!= null){
	     			if (vpToVar.get(s.getElementID()).contains(v.getID())){
	     				v.setIDVarPoint(s.getElementID());
	 					s.getVariantes().add(v);
	 					//si las variantes tienen hijos vp
	 					if (v.getHijos()!= null){
	 						Iterator<Struct> itv = v.getHijos().iterator();
	 			        	while (itv.hasNext()){
	 			        		Struct hijo = itv.next();
	 			        		seteoVariantes(hijo,registroVar,vpToVar);
	 			        	}
	 						
	 					}
	     			}
         		}
         	}
         	
    	}
        if (s.getHijos() != null){
        	Iterator<Struct> it = s.getHijos().iterator();
        	while (it.hasNext()){
        		Struct hijo = it.next();
        		seteoVariantes(hijo,registroVar,vpToVar);
        	}
        	
        }
    }
    
    public static Struct buscoEnHijos(Struct s, String padre){
		Iterator<Struct> it = s.getHijos().iterator();
		while (it.hasNext()){
			Struct hijo = it.next();
			if(hijo.getElementID().equals(padre)){
				return hijo;
        	}
			else {
				return buscoEnHijos(hijo,padre);
				
			}
		}
		return null;
    }
    
    public static Struct buscoPadre(Map<String,List<Struct>> registroHijos, String padre){
    	Struct result = null;
    	Iterator<Entry<String, List<Struct>>> iter = registroHijos.entrySet().iterator();
  		while (iter.hasNext()){
  			Entry<String, List<Struct>> e = iter.next();
        	List<Struct> l = e.getValue();
        	Iterator<Struct> itList = l.iterator();
        	while (itList.hasNext()){
        		Struct s = itList.next();
        		if(s.getElementID().equals(padre)){
        			result = s;
        			return result;
        		}
        		else {
          			result = buscoEnHijos(s,padre);
          		}
        	}
        }
  		return result;
    }
    
    public static List<Struct> ordenoNodos(List<Struct> list, Map<String,String[]> predecesores){
    	List<Struct> result = new ArrayList<Struct>();
    	Iterator<Struct> it = list.iterator();
    	
    	while (it.hasNext()){
    		Struct s = it.next();
    		if (s.getLinkToPredecessor() != null){
    			Iterator<String> itLinks = s.getLinkToPredecessor().iterator();
    			while (itLinks.hasNext()){
    				String idLink = itLinks.next();
    				if(predecesores.containsKey(idLink)){
    					String[] pred = predecesores.get(idLink);
    					// Busco pred
    					Struct predS = buscoPadre(pred[0],list);
    					if(predS != null){
    						if (s.getPredecesores() != null){
    							String[] array = {predS.getElementID(), pred[1]};
    							s.getPredecesores().put(idLink, array);
    						}
    						else {
    							Map<String,String[]> lista = new HashMap<String,String[]>();
    							String[] array = {predS.getElementID(), pred[1]};
    							lista.put(idLink, array);
    							s.setPredecesores(lista);
    						}
    						if (!result.contains(predS)){
    							if (!result.contains(s)){
    								result.add(predS);
    							}
    							else{
    								result.add(result.indexOf(s),predS);
								}
							}
						}
					}
				}
    			
    			if(!result.contains(s)){
    				result.add(s);
    			}
			}
    		else{
    			if (!result.contains(s)){
    				result.add(s);
    			}
    			/***********************************************************************/
    			Iterator<Entry<String, String[]>> itPred = predecesores.entrySet().iterator();
    			while (itPred.hasNext()){
    				Entry<String, String[]> entry = itPred.next();
    				String idLink = entry.getKey();
    				String[] pred = entry.getValue();
    				if (pred[0].equals(s.getElementID()) && (!pred[1].equals(""))){
    					Map<String, String[]> mapPred = s.getPredecesores();
    					if (mapPred == null){
    						mapPred = new HashMap<String, String[]>();
    					}
    					String[] array = {s.getElementID(), pred[1]};
    					mapPred.put(idLink, array);
    					s.setPredecesores(mapPred);
    				}
    			}
    			/***********************************************************************/
    		}
    		List<Struct> resHijos = ordenoNodos(s.getHijos(), predecesores);
    		s.setHijos(resHijos);
    	}
    	return result;
    }

    public static void copiarArchivo(File origen, String nomDestino, String nomArchivo){
		if (origen.isFile()){
			try{
				URL url = origen.toURL();	
				URLConnection urlCon = url.openConnection();
				
				InputStream is = urlCon.getInputStream();
				File destino = new File(ReadProperties.getProperty("destinoDescargas") + nomDestino);
				destino.mkdirs();
				FileOutputStream fos = new FileOutputStream(destino + "/" + nomArchivo);
				byte [] array = new byte[1000];
				int leido = is.read(array);
				while (leido > 0) {
				   fos.write(array, 0, leido);
				   leido = is.read(array);
				}
				is.close();
				fos.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    
    /*public static void actualizarDiagram(String dirDiagram, List<Struct> nodos, DefaultDiagramModel modeloAdaptado){
		File inputFile = new File(dirDiagram);
		try{
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();

	        Map<String, Node> changeNodes = new HashMap<String, Node>(); // <id variante, nodo>
	        Map<String, Node> deleteNodes = new HashMap<String, Node>(); // <id varpoint, nodo>
	        Map<String, List<String>> vpVar = new HashMap<String, List<String>>(); // <id varpoint, [idVariantes]>
	        NodeList nodes = doc.getElementsByTagName("node");
	        for (int temp = 0; temp < nodes.getLength(); temp ++){
				Node node = nodes.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					NodeList eAnotationsNodes = node.getChildNodes();
					for (int i = 0; i < eAnotationsNodes.getLength(); i++){
						Node eAnotation = eAnotationsNodes.item(i);
						if (eAnotation.getNodeType() == Node.ELEMENT_NODE) {
							NodeList detailsNodes = eAnotation.getChildNodes();
							for (int j = 0; j < detailsNodes.getLength(); j++){
								Node detail = detailsNodes.item(j);
								if (detail.getNodeType() == Node.ELEMENT_NODE) {
									Element eDetail = (Element) detail;
									if (eDetail.hasAttribute("key")){
										String key = eDetail.getAttribute("key");
										if ((key != null) && (key.equals("uri")) && (eDetail.hasAttribute("value"))){
											String value = eDetail.getAttribute("value");
											String[] res = value.split("#"); // value = "uma://_A_znMCXQEeaZI6JbOHEuYA#_CtOLQSXQEeaZI6JbOHEuYA"
											String id = (res.length > 1) ? res[1] : "";
											if (!id.equals("")){
												Struct sModelo = Utils.buscarElemento(id, nodos, "");
												// Si no está en el modelo o es un punto de variación => lo borro.
												if (sModelo == null){ // Posiblemente sea una variante
													Variant var = Utils.buscarVariante(nodos, id);
													if (var != null){ // Es una variante
														Struct s = Utils.buscarElementoEnModelo(id, modeloAdaptado, "");
														if (s == null){ // No está en el modelo final => La borro
															deleteNodes.put(id, node);
														}
														else{ // Sino, la modifico
															changeNodes.put(id, node);
														}
													}
												}
												else if (Utils.esPuntoDeVariacion(sModelo.getType())){ // Si es un punto de variación => lo borro.
													deleteNodes.put(id, node);
													// Guardo los PV mapeados con las variantes seleccionadas
													List<String> variantes = new ArrayList<String>();
													Iterator<Variant> itVar = sModelo.getVariantes().iterator();
													while (itVar.hasNext()){
														String idVar = itVar.next().getID();
														if ((Utils.buscarVariante(nodos, idVar) != null) && (Utils.buscarElementoEnModelo(idVar, modeloAdaptado, "") != null)){
															variantes.add(idVar);
														}
													}
													vpVar.put(id, variantes);
												}
											}
										}
									}
								}
							}
						}
					}
				}
        	}
	        
	        // Actualizo los nodos correspondientes a variantes
	        Iterator<Entry<String, Node>> itChange = changeNodes.entrySet().iterator();
	        int i = 1;
	        while (itChange.hasNext()){
	        	Entry<String, Node> entry = itChange.next();
	        	String idVarModel = entry.getKey();
	        	Node nodeVar = entry.getValue();
	        	String idVP = null;
	        	Iterator<Entry<String, List<String>>> itVpVar = vpVar.entrySet().iterator();
	        	while ((itVpVar.hasNext()) && (idVP == null)){
	        		Entry<String, List<String>> entryVpVar = itVpVar.next();
	        		List<String> vars = entryVpVar.getValue();
	        		if (vars.contains(idVarModel)){
	        			idVP = entryVpVar.getKey(); 
	        		}
	        	}
	        	if (idVP != null){
        			Element eNodeVar = null;
	        		Node nodeVP = deleteNodes.get(idVP);
        			String idNodeVP = null;
	        		// Modifico los atributos incoming y outgoing con el valor de la variante seleccionada
	        		if ((nodeVar.getNodeType() == Node.ELEMENT_NODE) && (nodeVP.getNodeType() == Node.ELEMENT_NODE)){
	        			eNodeVar = (Element) nodeVar;
	        			Element eNodeVP = (Element) nodeVP;
	        			if (eNodeVP.hasAttribute("incoming")){
	        				eNodeVar.setAttribute("incoming", eNodeVP.getAttribute("incoming") + i);
	        			}
	        			if (eNodeVP.hasAttribute("outgoing")){
	        				eNodeVar.setAttribute("outgoing", eNodeVP.getAttribute("outgoing") + i);
	        			}
	        			if (eNodeVP.hasAttribute("xmi:id")){
	        				idNodeVP = eNodeVP.getAttribute("xmi:id");
	        			}
	        		}
	        		i++;
	        	}
	        }
	        
	        // Si hay más de una variante seleccionada para el mismo PV => Duplico la edge del PV
	        Map<Node, Node>insertNodes = new HashMap<Node, Node>(); 
			Iterator<Entry<String, List<String>>> iter = vpVar.entrySet().iterator();
			while (iter.hasNext()){
				Entry<String, List<String>> entry = iter.next();
				String pV = entry.getKey();
				List<String> vars = entry.getValue();
				int cantVar = vars.size();
				String idPV = ((Element) deleteNodes.get(pV)).getAttribute("xmi:id");
				// Busco la edge que contiene el PV
				NodeList edges = doc.getElementsByTagName("edge");
    	        for (int temp = 0; temp < edges.getLength(); temp ++){
    				Node edge = edges.item(temp);
    				if (edge.getNodeType() == Node.ELEMENT_NODE){
    					Element eEdge = (Element) edge;
    					String source = "";
    					String target = "";
    					if (eEdge.hasAttribute("source")){
    						source = eEdge.getAttribute("source");
    					}
    					if (eEdge.hasAttribute("target")){
    						target = eEdge.getAttribute("target");
    					}
    					if (source.equals(idPV) || target.equals(idPV)){
    						List<Element> eEdgeModify = new ArrayList<Element>(); 
    						for (int j = 2; j < cantVar + 1; j++){
	    						Node newChild = edge.cloneNode(true);
	    						Element eNewChild = (Element) newChild;
	    						eNewChild.setAttribute("xmi:id", eNewChild.getAttribute("xmi:id") + j);
	    						insertNodes.put(newChild, edge);
	    						eEdgeModify.add(eNewChild);
    						}
    						eEdge.setAttribute("xmi:id", eEdge.getAttribute("xmi:id") + 1);
    						eEdgeModify.add(eEdge);
    						
    						Iterator<String> itVars = vars.iterator();
    						while (itVars.hasNext()){
    							String idVar = itVars.next();
    							Node nodeVar = changeNodes.get(idVar);
    							Element eNodeVar = (Element) nodeVar;
    							String idENodeVar = ((Element) nodeVar).getAttribute("xmi:id");
    							if (source.equals(idPV)){
    								Iterator<Element> itEEdgeModify = eEdgeModify.iterator();
    								boolean fin = false;
    								while (itEEdgeModify.hasNext() && !fin){
    									Element eModify = itEEdgeModify.next();
    									if (eNodeVar.getAttribute("incoming").equals(eModify.getAttribute("xmi:id"))){
    										eModify.setAttribute("source", idENodeVar);
    										fin = true;
    									}
    								}
		    					}
    							else{
    								Iterator<Element> itEEdgeModify = eEdgeModify.iterator();
    								boolean fin = false;
    								while (itEEdgeModify.hasNext() && !fin){
    									Element eModify = itEEdgeModify.next();
    									if (eNodeVar.getAttribute("incoming").equals(eModify.getAttribute("xmi:id"))){
    										eModify.setAttribute("target", idENodeVar);
    										fin = true;
    									}
    								}
	    						}
    						}
    					}
    				}
    	        }
				
			}
			Iterator<Entry<Node, Node>> itInsert = insertNodes.entrySet().iterator();
			while (itInsert.hasNext()){
				Entry<Node, Node> entry = itInsert.next();
				Node edge = entry.getValue();
				edge.getParentNode().insertBefore(entry.getKey(), edge);
			}
	        
	        // Elimino los nodos de puntos de variación y variantes que no se usan
	        Iterator<Entry<String, Node>> itDelete = deleteNodes.entrySet().iterator();
	        while (itDelete.hasNext()){
	        	Entry<String, Node> entry = itDelete.next();
	        	Node node = entry.getValue();
	        	node.getParentNode().removeChild(node);
	        }
	        
	        // Elimino los children de puntos de variación y variantes que no se usan
	        NodeList childrens = doc.getElementsByTagName("children");
	        for (int temp = 0; temp < childrens.getLength(); temp ++){
				Node children = childrens.item(temp);
				if (children.getNodeType() == Node.ELEMENT_NODE){
					Element eChildren = (Element) children;
					if (eChildren.hasAttribute("element")){
    					String element =  eChildren.getAttribute("element");
    					Iterator<Entry<String, Node>> it = deleteNodes.entrySet().iterator();
    					boolean fin = false;
    					while (it.hasNext() && !fin){
    						Entry<String, Node> entry = it.next();
    						Node node = entry.getValue();
    						if (node.getNodeType() == Node.ELEMENT_NODE){
    							Element eNode = (Element) node;
    							if (eNode.hasAttribute("xmi:id")){
    								String id = eNode.getAttribute("xmi:id");
    								if (element.equals(id)){
    									fin = true;
    									children.getParentNode().removeChild(children);
    								}
    							}
    						}
    					}
					}
				}
	        }
	        
	        doc.normalize();
	        Transformer tf = TransformerFactory.newInstance().newTransformer();
	        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	        tf.setOutputProperty(OutputKeys.INDENT, "yes");
	        Writer out = new StringWriter();
	        tf.transform(new DOMSource(doc), new StreamResult(out));
	        File tmpFile = new File(dirDiagram.substring(0, dirDiagram.length() - 4) + "_tmp.xmi");
    		PrintWriter pw = new PrintWriter(new FileWriter(tmpFile));
		    pw.println(out.toString());
		    pw.flush();
		    pw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }*/
    
}

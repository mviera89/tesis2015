package logica;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dataTypes.TipoContentCategory;
import dataTypes.TipoContentDescription;
import dataTypes.TipoElemento;
import dataTypes.TipoLibrary;
import dataTypes.TipoMethodConfiguration;
import dataTypes.TipoPlugin;
import dataTypes.WorkProduct;
import dominio.Struct;
import dominio.Variant;

public class XMIParser {
	
	public static Object[] getElementsXMIResource(String nomFile){
		String uriPlugin = null;
		TipoLibrary library = null;
		try{
			File inputFile = new File(nomFile);
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
								if (uri != null){
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
		try{
			File inputFile = new File(nomFile);
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
					String id = "";
					String name = "";
					String briefDescription = "";
					if (eNodo.hasAttribute("xmi:id")){
						id = eNodo.getAttribute("xmi:id");
					}
					if (eNodo.hasAttribute("name")){
						name = eNodo.getAttribute("name");
					}
					if (eNodo.hasAttribute("briefDescription")){
						briefDescription = eNodo.getAttribute("briefDescription");
					}
					return new TipoMethodConfiguration(id, name, briefDescription);
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

	        String deliveryProcessDir = null;
	        String customCategoriesDir = null;
	        NodeList nodosResource = doc.getElementsByTagName("org.eclipse.epf.uma.resourcemanager:ResourceManager");
	        if (nodosResource.getLength() > 0){
				Node nodo = nodosResource.item(0);
				if (nodo.getNodeType() == Node.ELEMENT_NODE) {
					NodeList childNodes = nodo.getChildNodes();
					int i = 0;
					while ((i < childNodes.getLength()) && ((deliveryProcessDir == null) || (customCategoriesDir == null))){
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
											if (dir.equals("deliveryprocesses")){
												deliveryProcessDir = uri;
											}
											else if (dir.equals("customcategories")){
												customCategoriesDir = uri;
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
	        
	        
	        NodeList nodos = doc.getElementsByTagName("org.eclipse.epf.uma:MethodPlugin");
        	if (nodos.getLength() > 0){
        		int temp = 0;
				Node nodo = nodos.item(temp);
				Element eHijo = (Element) nodo;
				String id = "";
				String name = "";
				String guid = "";
				String briefDescription = "";
				String authors = ""; 
				String changeDate = "";
				String changeDescription = "";
				String version = "";
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
				
				return new TipoPlugin(id, name, guid, briefDescription, authors, changeDate, changeDescription, version, deliveryProcessDir, customCategoriesDir);
        	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static TipoContentCategory getElementsXMIContentCategory(String nomFile, String id){
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
				Node resNode = obtenerContentCategoryId(nodo, id);
				
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
					}
					if (e.hasAttribute("categorizedElements")){
						categorizedElementsCC = e.getAttribute("categorizedElements");
					}
					
					return new TipoContentCategory(typeCC, idCC, nameCC, guidCC, presentationNameCC, briefDescriptionCC, categorizedElementsCC);
				}
        	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Node obtenerContentCategoryId(Node nodo, String xmiId){
		if (nodo.getNodeType() == Node.ELEMENT_NODE) {
			if (nodo.getNodeName().equals("contentElements")){
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
		Node resNode = null;
		while ((i < childNodes.getLength()) && (resNode == null)){
			Node child = childNodes.item(i);
			Node res = obtenerContentCategoryId(child, xmiId);
			if (res != null){
				return res;
			}
			i++;
		}
		return null;
	}
	
	public static TipoContentDescription getElementsXMICustomCategories(String nomFile){
		try{
			File inputFile = new File(nomFile);
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
							mainDescription = child.getFirstChild().getTextContent();
						}
						else if (child.getNodeName().equals("keyConsiderations")){
							keyConsiderations = child.getFirstChild().getTextContent();
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
	        Map<String, String> predecesores = new HashMap<String,String>();
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

	public static void getNodos(String nomFile, NodeList nodos, List<Struct> result,List<Variant> registroVar, Map<String,List<String>> vpToVar, Map<String,List<Struct>> registroHijos, Map<Struct,String> performedPrimaryBy, Map<Struct,List<String>> performedAditionallyBy, Map<Struct,List<WorkProduct>> workProducts, Map<String,String> predecesores, Map<String, String> dataProcess){
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
					
					if (eHijo.hasAttribute("name")){
						nameHijo = eHijo.getAttribute("name");
						System.out.println("Nombre del proceso: " + nameHijo);
					}
					
					if (eHijo.hasAttribute("xsi:type")){
						type = eHijo.getAttribute("xsi:type").substring(20);
					}
					
					if (eHijo.hasAttribute("xmi:id")){
						id = eHijo.getAttribute("xmi:id");
					}
					
					if (eHijo.hasAttribute("briefDescription")){
						description = eHijo.getAttribute("briefDescription");
					}
					
					if (eHijo.hasAttribute("presentationName")){
						presentationName = eHijo.getAttribute("presentationName");
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
		     		
					String processComponentId = (dataProcess != null) ? dataProcess.get("processComponentId") : null;
					String processComponentName = (dataProcess != null) ? dataProcess.get("processComponentName") : null;
					String presentationId = (dataProcess != null) ? dataProcess.get("presentationId") : null;
					Struct h = new Struct(id, nameHijo, tipo,-1,-1, obtenerIconoPorTipo(tipo), processComponentId, processComponentName, presentationId, null);
					h.setDescription(description);
					h.setPresentationName(presentationName);
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
					String predecesoresList = "";
					List<WorkProduct> lwp = new ArrayList<WorkProduct>();
					String pred = "";
					
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
					}
					
					if (eHijo.hasAttribute("presentationName")){
						presentationName = eHijo.getAttribute("presentationName");
					}
					
	      		    int min = -1;
	      		    int max = -1;
	      		    
	      		    TipoElemento tipo = obtenerTipoElemento(type);
	      		    Struct h = null;
      		    	String nombreArchivoCapabilityPattern = nomFile.substring(0, nomFile.length() - 4) + "_" + nameHijo.replace(" ", "_") + ".xmi";
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
	      		    	}
	      		    }
	      		    else{
	      		    	String processComponentId = (dataProcess != null) ? dataProcess.get("processComponentId") : null;
						String processComponentName = (dataProcess != null) ? dataProcess.get("processComponentName") : null;
						String presentationId = (dataProcess != null) ? dataProcess.get("presentationId") : null;
		      		    h = new Struct(id, nameHijo, tipo,min,max, obtenerIconoPorTipo(tipo), processComponentId, processComponentName, presentationId, null);
		      		    h.setDescription(description);
		      		    h.setPresentationName(presentationName);
		      		   
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
								predecesoresList = eHijo.getAttribute("pred");
								if (!predecesores.containsKey(id)){
									predecesores.put(id, predecesoresList);
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
     			if (vpToVar.get(s.getElementID()).contains(v.getID())){
     				v.setIDVarPoint(s.getElementID());
 					s.getVariantes().add(v);
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
    
    public static Struct buscoPadre (Map<String,List<Struct>> registroHijos, String padre){
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
    
    public static List<Struct> ordenoNodos(List<Struct> list, Map<String,String> predecesores){
    	List<Struct> result = new ArrayList<Struct>();
    	Iterator<Struct> it = list.iterator();
    	while (it.hasNext()){
    		Struct s = it.next();
    		
    		if (s.getLinkToPredecessor() != null){
    			Iterator<String> itLinks = s.getLinkToPredecessor().iterator();
    			while (itLinks.hasNext()){
    				String idLink = itLinks.next();
    				if(predecesores.containsKey(idLink)){
    					String pred = predecesores.get(idLink);
    					// Busco pred
    					Struct predS = buscoPadre(pred,list);
    					if(predS != null){
    						if (predS.getSucesores() != null){
    						predS.getSucesores().add(s.getElementID());
    						}
    						else {
    							List<String> lista = new ArrayList<String>();
    							lista.add(s.getElementID());
    							predS.setSucesores(lista);    							
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
    		else if (!result.contains(s)){
    			result.add(s);
    		}
    		List<Struct> resHijos = ordenoNodos(s.getHijos(), predecesores);
    		s.setHijos(resHijos);
    	}
    	return result;
    }

}

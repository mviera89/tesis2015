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

import dataTypes.TipoElemento;
import dataTypes.WorkProduct;
import dominio.Struct;
import dominio.Variant;

public class XMIParser {

	
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
	        
	        doc.getDocumentElement().normalize();
	        NodeList nList = doc.getElementsByTagName("org.eclipse.epf.uma:ProcessComponent");
	        getNodos(nList, result, registroVar, vpToVar, registroHijos, performedPrimaryBy, performedAdditionallyBy,workProducts);
	        
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
    		    	}
    		    if (!esHijoDeVar){
    		    	 
   	        	 result.addAll(l);
    		    }        	
	                	 
	         }
	         
	         
	         // Recorro result, para cada var point busco las variantes y se ponen en la lista de hijos
		        Iterator<Struct> it = result.iterator();
		        while (it.hasNext()){
		        	Struct s = it.next();
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
		        }
		        
		        //Recorro performedPrimaryBy 
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
		         
		         //Recorro performedAdditionallyBy 
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
		         
		         
		       //Recorro workProducts 
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
				        			 //result.remove(s);
				        			 encontre = true;
				        		 }
				        	 }
				        	 
			        	 }
		        	 }
		         }
	
		         
	
		}
		catch (Exception e) {
			e.printStackTrace();
		}
        return result;
    }

	public static void getNodos(NodeList nodos, List<Struct> result,List<Variant> registroVar, Map<String,List<String>> vpToVar, Map<String,List<Struct>> registroHijos, Map<Struct,String> performedPrimaryBy, Map<Struct,List<String>> performedAditionallyBy, Map<Struct,List<WorkProduct>> workProducts ){
		for (int temp = 0; temp < nodos.getLength(); temp++){
			Node nodo = nodos.item(temp);
			if (nodo.getNodeType() == Node.ELEMENT_NODE) {
				Element eHijo = (Element) nodo;
				if (nodo.getNodeName().equals("process")){
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
						
					TipoElemento tipo = obtenerTipoElemento(type);
		     		    	
					Struct h = new Struct(id, nameHijo, tipo,-1,-1, obtenerIconoPorTipo(tipo));
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
					List<WorkProduct> lwp = new ArrayList<WorkProduct>();
					
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
	      		    
	      		//Veo si es Padre de alguien
	      		  List<Struct> hijosS = new ArrayList<Struct>();
	      		    if(registroHijos.containsKey(id)){
	      		    	hijosS = registroHijos.get(id);
	      		    	registroHijos.remove(id);
	      		    }
	      		  TipoElemento tipo = obtenerTipoElemento(type);
     		    	
     		    Struct h = new Struct(id, nameHijo, tipo,min,max, obtenerIconoPorTipo(tipo));
     		    h.setDescription(description);
				h.setPresentationName(presentationName);
				
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
				
				if (tipo == TipoElemento.ROLE || tipo == TipoElemento.VP_ROLE){
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
				
	      		    
	      		  	      		    
	      		  boolean tienePadre =false;
	      		  if (eHijo.hasAttribute("superActivities") &&  !(type.equals(TipoElemento.VAR_ACTIVITY.toString())
	      				  && !(type.equals("WorkOrder")) ||
	                  				type.equals(TipoElemento.VAR_PHASE.toString())	   ||
	                  				type.equals(TipoElemento.VAR_ITERATION.toString()) ||
	                  				type.equals(TipoElemento.VAR_TASK.toString())      ||
	                  				type.equals(TipoElemento.VAR_ROLE.toString())      ||
	                  				type.equals(TipoElemento.VAR_MILESTONE.toString()) ||
	                  				type.equals(TipoElemento.VAR_WORK_PRODUCT.toString()) )) {
	      		    	 // Me fijo si es hijo de alguien
	      			  		tienePadre = true;
	      			  		String padre = eHijo.getAttribute("superActivities");
	 	      		    	
	 	      		    	if (hijosS != null){
	 	      		    		h.getHijos().addAll(hijosS);
	 	      		    	}
	 	      		    	boolean esHijoDeVar = false;
	 	      		    	//busco en las variantes
	 	      		    	Iterator<Variant> itV = registroVar.iterator();
	 	      		    	while (itV.hasNext()){
	 	      		    		Variant v = itV.next();
	 	      		    		if (v.getID().equals(padre)){
	 	      		    			v.getHijos().add(h);
	 	      		    			esHijoDeVar = true;
	 	      		    		}
	 	      		    	}
	 	      		    	if (!esHijoDeVar){
		 	      		    	boolean elPadreEsHijo = false;
		 	      		    	//busco el padre en result
		 	      		    	Struct padreS = buscoPadre(padre,result);
		 	      		    	if (padreS != null){
		 	      		    		
		 	      		    		padreS.getHijos().add(h);
		 	      		    		tienePadre = true;
		 	      		    		
		 	      		    	}
		 	      		    	//veo si el padre ya no esta en registroHijos como hijo de alguien
		 	      		    	else {
		 	      		    	 Iterator<Entry<String, List<Struct>>> iter = registroHijos.entrySet().iterator();
		 	      		         while (iter.hasNext()){
		 	      		        	 Entry<String, List<Struct>> e = iter.next();
		 	      		        	 List<Struct> l = e.getValue();
		 	      		        	 Iterator<Struct> itList = l.iterator();
		 	      		        	 while (itList.hasNext()){
		 	      		        		 Struct s = itList.next();
		 	      		        		 if(s.getElementID().equals(padre)){
		 	      		        			 s.getHijos().add(h);
		 	      		        			 elPadreEsHijo = true;
		 	      		        		
		 	      		        		 } 
		 	      		        	 }
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
          			   //	System.out.println("\t\tMin : " + min);
          			   	//System.out.println("\t\tMax : " + max);
            			
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
      		    		
	      		    	Variant var = new Variant(id, nameHijo, presentationName, "", true, type);
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
                						//System.out.println("Inclusiva: " + iDVariantInclusiva);
                						
                					}
                					else {
                						String iDVariantExclusiva = eHijoVar.getAttribute("supplier");
                						var.getExclusivas().add(iDVariantExclusiva);
                						//System.out.println("Exclusiva: " + iDVariantExclusiva);
                						
                					}
                				}
                    		}
                    	}
	      		    }
	      		   
	      		    else if(id != null && nameHijo != null && type != null && !tienePadre && !(type.equals("WorkOrder"))){
	      		    	if (hijosS != null ){
	      		    		h.getHijos().addAll(hijosS);
	      		    	}
	      		    	result.add(h);
	      		    }
				}
				NodeList hijos = nodo.getChildNodes();
				getNodos(hijos, result, registroVar, vpToVar, registroHijos, performedPrimaryBy, performedAditionallyBy, workProducts);
			
		}
		}
	}

	public static TipoElemento obtenerTipoElemento(String t){
		TipoElemento type = (t.equals(TipoElemento.PROCESS_PACKAGE.toString())) ? TipoElemento.PROCESS_PACKAGE :
    				   		(t.equals(TipoElemento.ACTIVITY.toString()))	    ? TipoElemento.ACTIVITY		   :
			   			 	(t.equals(TipoElemento.VP_ACTIVITY.toString()))     ? TipoElemento.VP_ACTIVITY	   :
		   			 		(t.equals(TipoElemento.VAR_ACTIVITY.toString()))    ? TipoElemento.VAR_ACTIVITY	   :
		   			 		(t.equals(TipoElemento.TASK.toString()))    		? TipoElemento.TASK			   :
		   			 		(t.equals(TipoElemento.VP_TASK.toString()))    		? TipoElemento.VP_TASK		   :
		   			 		(t.equals(TipoElemento.VAR_TASK.toString()))    	? TipoElemento.VAR_TASK		   :
		   			 		(t.equals(TipoElemento.ITERATION.toString()))    	? TipoElemento.ITERATION	   :
		   			 		(t.equals(TipoElemento.VP_ITERATION.toString()))    ? TipoElemento.VP_ITERATION	   :
		   			 		(t.equals(TipoElemento.VAR_ITERATION.toString()))   ? TipoElemento.VAR_ITERATION   :
		   			 		(t.equals(TipoElemento.PHASE.toString()))    		? TipoElemento.PHASE		   :
		   			 		(t.equals(TipoElemento.VP_PHASE.toString()))    	? TipoElemento.VP_PHASE		   :
		   			 		(t.equals(TipoElemento.VAR_PHASE.toString()))  		? TipoElemento.VAR_PHASE	   :
			   			 	(t.equals(TipoElemento.CAPABILITY_PATTERN.toString()))? TipoElemento.CAPABILITY_PATTERN :
			   			 	(t.equals(TipoElemento.DELIVERY_PROCESS.toString()))? TipoElemento.DELIVERY_PROCESS :
				   			(t.equals(TipoElemento.MILESTONE.toString()))		? TipoElemento.MILESTONE :
				   			(t.equals(TipoElemento.VP_MILESTONE.toString()))? TipoElemento.VP_MILESTONE :
				   			(t.equals(TipoElemento.VAR_MILESTONE.toString()))? TipoElemento.VAR_MILESTONE :
				   			(t.equals(TipoElemento.ROLE.toString()))? TipoElemento.ROLE :
				   			(t.equals(TipoElemento.VP_ROLE.toString()))? TipoElemento.VP_ROLE :
				   			(t.equals(TipoElemento.VAR_ROLE.toString()))? TipoElemento.VAR_ROLE :
				   			(t.equals(TipoElemento.WORK_PRODUCT.toString()))? TipoElemento.WORK_PRODUCT :
				   			(t.equals(TipoElemento.VP_WORK_PRODUCT.toString()))? TipoElemento.VP_WORK_PRODUCT :
				   			(t.equals(TipoElemento.VAR_WORK_PRODUCT.toString()))? TipoElemento.VAR_WORK_PRODUCT :
	   			 			null;
    	return type;
    }

    public static String obtenerIconoPorTipo(TipoElemento tipo){
    	String icono = (tipo == TipoElemento.PROCESS_PACKAGE) ? TipoElemento.PROCESS_PACKAGE.getImagen() :
    				   (tipo == TipoElemento.ACTIVITY)	      ? TipoElemento.ACTIVITY.getImagen()		 :
    				   (tipo == TipoElemento.VP_ACTIVITY)     ? TipoElemento.VP_ACTIVITY.getImagen()	 :
    				   (tipo == TipoElemento.VAR_ACTIVITY)    ? TipoElemento.VAR_ACTIVITY.getImagen()	 :
    				   (tipo == TipoElemento.TASK)     	      ? TipoElemento.TASK.getImagen()	 		 :
    				   (tipo == TipoElemento.VP_TASK)     	  ? TipoElemento.VP_TASK.getImagen()	 	 :
    				   (tipo == TipoElemento.VAR_TASK)        ? TipoElemento.VAR_TASK.getImagen()		 :
    				   (tipo == TipoElemento.ITERATION)    	  ? TipoElemento.ITERATION.getImagen()	   	 :
   		   			   (tipo == TipoElemento.VP_ITERATION)    ? TipoElemento.VP_ITERATION.getImagen()	 :
   		   			   (tipo == TipoElemento.VAR_ITERATION)   ? TipoElemento.VAR_ITERATION.getImagen()   :
   		   			   (tipo == TipoElemento.PHASE)    		  ? TipoElemento.PHASE.getImagen()		     :
   		   			   (tipo == TipoElemento.VP_PHASE)   	  ? TipoElemento.VP_PHASE.getImagen()		 :
   		   			   (tipo == TipoElemento.VAR_PHASE)	      ? TipoElemento.VAR_PHASE.getImagen()	     :
   		   			   (tipo == TipoElemento.CAPABILITY_PATTERN)? TipoElemento.CAPABILITY_PATTERN.getImagen() :
   		   			   (tipo == TipoElemento.DELIVERY_PROCESS)? TipoElemento.DELIVERY_PROCESS.getImagen() :
   		   			   (tipo == TipoElemento.MILESTONE)		  ? TipoElemento.MILESTONE.getImagen() :
				   	   (tipo == TipoElemento.VP_MILESTONE)	  ? TipoElemento.VP_MILESTONE.getImagen() :
				   	   (tipo == TipoElemento.VAR_MILESTONE)   ? TipoElemento.VAR_MILESTONE.getImagen() :
				   	   (tipo == TipoElemento.ROLE)			  ? TipoElemento.ROLE.getImagen() :
				   	   (tipo == TipoElemento.VP_ROLE)		  ? TipoElemento.VP_ROLE.getImagen() :
				   	   (tipo == TipoElemento.VAR_ROLE)		  ? TipoElemento.VAR_ROLE.getImagen() :
				   	   (tipo == TipoElemento.WORK_PRODUCT)	  ? TipoElemento.WORK_PRODUCT.getImagen() :
				   	   (tipo == TipoElemento.VP_WORK_PRODUCT) ? TipoElemento.VP_WORK_PRODUCT.getImagen() :
				   	   (tipo == TipoElemento.VAR_WORK_PRODUCT)? TipoElemento.VAR_WORK_PRODUCT.getImagen() :   		   				   
    				   "";
    	return icono;
    }
    
    static Struct buscoPadre(String id, List<Struct> lista){
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
    

}

		
	
	

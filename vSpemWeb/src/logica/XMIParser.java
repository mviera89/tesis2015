package logica;

import java.io.File;
import java.util.ArrayList;
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
	        
	        doc.getDocumentElement().normalize();
	        NodeList nList = doc.getElementsByTagName("org.eclipse.epf.uma:ProcessComponent");
	        getNodos(nList, result, registroVar, vpToVar, registroHijos);
	        
	
	        
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
		         		s.getType() == TipoElemento.VP_ITERATION){
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
	
		}
		catch (Exception e) {
			e.printStackTrace();
		}
        return result;
    }

	public static void getNodos(NodeList nodos, List<Struct> result,List<Variant> registroVar, Map<String,List<String>> vpToVar, Map<String,List<Struct>> registroHijos ){
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
					
					if (eHijo.hasAttribute("name")){
						nameHijo = eHijo.getAttribute("name");
					}
					if (eHijo.hasAttribute("xsi:type")){
						type = eHijo.getAttribute("xsi:type").substring(20);
					}
					if (eHijo.hasAttribute("xmi:id")){
						id = eHijo.getAttribute("xmi:id");
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
	      		    
	      		  	      		    
	      		  boolean tienePadre =false;
	      		  if (eHijo.hasAttribute("superActivities") &&  !(type.equals(TipoElemento.VAR_ACTIVITY.toString())  ||
	                  				type.equals(TipoElemento.VAR_PHASE.toString())	   ||
	                  				type.equals(TipoElemento.VAR_ITERATION.toString()) ||
	                  				type.equals(TipoElemento.VAR_TASK.toString()))) {
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
          				type.equals(TipoElemento.VP_TASK.toString()) ){
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
          				type.equals(TipoElemento.VAR_TASK.toString())){
      		    		
	      		    	Variant var = new Variant(id,nameHijo,"",true,type);
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
	      		   
	      		    else if(id != null && nameHijo != null && type != null && !tienePadre){
	      		    	if (hijosS != null ){
	      		    		h.getHijos().addAll(hijosS);
	      		    	}
	      		    	result.add(h);
	      		    }
				}
				NodeList hijos = nodo.getChildNodes();
				getNodos(hijos, result, registroVar, vpToVar, registroHijos);
			
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

		
	
	

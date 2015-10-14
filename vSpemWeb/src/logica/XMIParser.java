package logica;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	         DocumentBuilderFactory dbFactory 
	            = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.parse(inputFile);
	         
	         List<Variant> registroVar = new ArrayList<Variant>();
	         Map<String,List<String>> vpToVar = new HashMap<String,List<String>>();
	         	                  
	         doc.getDocumentElement().normalize();
	                  
	         NodeList nList = doc.getElementsByTagName("org.eclipse.epf.uma:ProcessComponent");      
	         	         
	         //Recorro la lista
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	            Node nNode = nList.item(temp);
	            	            
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            	
	               //Obtengo lista de elementos hijos del nodo
	               NodeList nHijos = nNode.getChildNodes();
	               	for (int temp1 = 0; temp1 < nHijos.getLength(); temp1++) {
	               		
	               		Node nHijo = nHijos.item(temp1);
	               		
	               		if (nHijo.getNodeName().equals("childPackages")){
	               				               			
	               			NodeList hijos = nHijo.getChildNodes();
	               			for(int aux = 0; aux <hijos.getLength();aux++){
	               				Node hijo =hijos.item(aux);
	               				if (hijo.getNodeType() == Node.ELEMENT_NODE){
	     	            		   Element eHijo = (Element) hijo;
	     	            		   
	     	            		   String nameHijo = eHijo.getAttribute("name");
	     	            		   String type = eHijo.getAttribute("xsi:type").substring(20);
	     	            		   String id = eHijo.getAttribute("xmi:id");
	     	            		   int min = -1;
	     	            		   int max = -1;
	     	            		   
	     	            		   //////////////////////////////////////////////
	     	            		  if (type.equals(TipoElemento.VP_ACTIVITY.toString()) || 
	   	            				   type.equals(TipoElemento.VP_PHASE.toString()) || 
	   	            				   type.equals(TipoElemento.VP_ITERATION.toString()) ){
	     	            			 if (eHijo.hasAttribute("min")) {
	   	            				  min = Integer.parseInt(eHijo.getAttribute("min"));
	     	            			 }
	     	            			 if (eHijo.hasAttribute("max")) {
	   	            				  max = Integer.parseInt(eHijo.getAttribute("max"));
	     	            			 }
	   	            			   	System.out.println("\t\tMin : " + min);
	   	            			   	System.out.println("\t\tMax : " + max);
	     	            			
	   		               		   List<String> variantes = new ArrayList<String>();
	   		               		        //Obtengo lista de elementos hijos del nodo
	   		                           NodeList nHijosVar = hijo.getChildNodes();
	   		                           	for (int temp2 = 0; temp2 < nHijosVar.getLength(); temp2++) {
	   		                           		
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
	   	            		   
	   	            		   if (type.equals(TipoElemento.VAR_ACTIVITY.toString()) 
	   	            				   || type.equals(TipoElemento.VAR_PHASE.toString())
	   	            				|| type.equals(TipoElemento.VAR_ITERATION.toString())){
	   	            			               		   
	   	            			    Variant var = new Variant(id,nameHijo,"",true,type);
	   	                        	registroVar.add(var);
	   	                        		
	   	            		     }
	   	            		   else if (!type.equals("RoleDescriptor")){
	   	            			   Struct nodo = new Struct(id, nameHijo, obtenerTipoElemento(type),min,max);
	   		            		   result.add(nodo);
	   	            		   }
	   	               		  	               
	   	               	
	     	            		   
	     	           ////////////////////////////////////////////////
	               				}
	               				
	               			}
	 	               			
	               		}
	               		else if(nHijo.getNodeName().equals("processElements")){
	               		
	               		if (nHijo.getNodeType() == Node.ELEMENT_NODE){
	            		   Element eHijo = (Element) nHijo;
	            		   
	            		   String nameHijo = eHijo.getAttribute("name");
	            		   String type = eHijo.getAttribute("xsi:type").substring(20);
	            		   String id = eHijo.getAttribute("xmi:id");
	            		   int min = -1;
 	            		   int max = -1;
	            		  
	            		   if (type.equals(TipoElemento.VP_TASK.toString())){
	            			  if (eHijo.hasAttribute("min")) {
	            				  min = Integer.parseInt(eHijo.getAttribute("min"));
	            			  }
	            			  if (eHijo.hasAttribute("max")) {
	            				  max = Integer.parseInt(eHijo.getAttribute("max"));
	            			  }
	            			   System.out.println("\t\tMin : " + min);
	            			   System.out.println("\t\tMax : " + max);
		               		   List<String> variantes = new ArrayList<String>();
		               		        //Obtengo lista de elementos hijos del nodo
		                           NodeList nHijosVar = nHijo.getChildNodes();
		                           	for (int temp2 = 0; temp2 < nHijosVar.getLength(); temp2++) {
		                           		
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
	            		   
	            		   if (type.equals(TipoElemento.VAR_TASK.toString())){
	            				
	                           		
	                        			Variant var = new Variant(id,nameHijo,"",true,type);
	                        			registroVar.add(var);
	                        		
	                           	
	            		   }
	            		   else if (!type.equals("RoleDescriptor")){
	            			   Struct nodo = new Struct(id, nameHijo, obtenerTipoElemento(type),min,max);
		            		   result.add(nodo);
	            		   }
	               		}
	               
	               	}
	            }
	           }
	         }
	         
	         //Recorro result, para cada var point busco las variantes y se ponen en la lista de hijos
	         Iterator<Struct> it = result.iterator();
	         while (it.hasNext()){
	         	Struct s = it.next();
	         	if (s.getType() == TipoElemento.VP_ACTIVITY 
	         			|| s.getType() == TipoElemento.VP_TASK
	         			|| s.getType() == TipoElemento.VP_PHASE
	         			|| s.getType() == TipoElemento.VP_ITERATION){
	         		Iterator<Variant> itaux = registroVar.iterator();
	         		while (itaux.hasNext()){
	         			Variant v = itaux.next();
	         			if (vpToVar.get(s.getElementID()).contains(v.getID())){
	         				v.setIDVarPoint(s.getElementID());
	         				s.getHijos().add(v);
	         			}
	         		}
	         		
	         		
	         	}
	         }
	         
	      
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
        return result;
     
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
		   			 		

	   			 			null;
    	return type;
    }

}
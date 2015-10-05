package logica;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	         
	         //HashMap<String, String> registroVP = new HashMap<String, String>();
	         List<Variant> registroVar = new ArrayList<Variant>();
	         //HashMap<String, String> registroVar = new HashMap<String, String>();
	                  
	         doc.getDocumentElement().normalize();
	                  
	         NodeList nList = doc.getElementsByTagName("childPackages");
	         	         
	         //Recorro la lista de childPackages
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	            Node nNode = nList.item(temp);
	            	            
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            	
	               //Obtengo lista de elementos hijos del nodo
	               NodeList nHijos = nNode.getChildNodes();
	               	for (int temp1 = 0; temp1 < nHijos.getLength(); temp1++) {
	               		
	               		Node nHijo = nHijos.item(temp1);
	               		
	               		if (nHijo.getNodeType() == Node.ELEMENT_NODE){
	            		   Element eHijo = (Element) nHijo;
	            		   
	            		   String nameHijo = eHijo.getAttribute("name");
	            		   String type = eHijo.getAttribute("xsi:type").substring(20);
	            		   String id = eHijo.getAttribute("xmi:id");
	            		   
	            		   //if (type.equals("vpActivity")){
	            			   //System.out.println("\t\tMin : " +eHijo.getAttribute("min"));
	            			   //System.out.println("\t\tMax : " +eHijo.getAttribute("max"));
	            			   //registroVP.put(id, nameHijo);
	            		   //}
	            		   
	            		   if (type.equals(TipoElemento.VAR_ACTIVITY.toString())){
	            			               		   
	            			 //Obtengo lista de elementos hijos del nodo
	                           NodeList nHijosVar = nHijo.getChildNodes();
	                           	for (int temp2 = 0; temp2 < nHijosVar.getLength(); temp2++) {
	                           		
	                           		Node nHijoVar = nHijosVar.item(temp2);
	                           		                           		
	                           		if ((nHijoVar.getNodeType() == Node.ELEMENT_NODE) && (nHijoVar.getNodeName().equals("client"))){
	                        		   Element eHijoVar = (Element) nHijoVar;
	                        		                           		   
	                        		   if  (eHijoVar.getAttribute("xsi:type").substring(20).equals("variant2varP")){
	                        			   		//if (!registroVP.isEmpty()){
	                        			   			String iDVarPoint = eHijoVar.getAttribute("supplier");
	                        			   			//registroVar.put(id, nameHijo);
	                        			   			//System.out.println("\t\tCorresponde al VarPoint:  " + varPoint);
	                        			   			String isInclusive = eHijoVar.getAttribute("isInclusive");
	                        			   			boolean inclusiva = false;
	                        			   			if (isInclusive.equals("true")){
	                        			   				inclusiva = false;
	                        			   			}
	                        			   			Variant var = new Variant(id,nameHijo,iDVarPoint,inclusiva);
	                        			   			registroVar.add(var);
	                        			   		//}
	                        		   }
	                           		}
	            		        }
	            		   }
	            		   else if (!type.equals("RoleDescriptor")){
	            			   Struct nodo = new Struct(id, nameHijo, obtenerTipoElemento(type));
		            		   result.add(nodo);
	            		   }
	               		}
	               
	               	}
	            }
	         }
	         
	         //Recorro result, para cada var point busco las variantes y se ponen en la lista de hijos
	         Iterator<Struct> it = result.iterator();
	         while (it.hasNext()){
	         	Struct s = it.next();
	         	if (s.getType() == TipoElemento.VP_ACTIVITY){
	         		Iterator<Variant> itaux = registroVar.iterator();
	         		while (itaux.hasNext()){
	         			Variant v = itaux.next();
	         			if (v.getIDVarPoint().equals(s.getElementID())){
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
	   			 			null;
    	return type;
    }

}
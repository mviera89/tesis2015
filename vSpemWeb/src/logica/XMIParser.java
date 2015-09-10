package logica;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dominio.Struct;

public class XMIParser {
	
	public static List<Struct> getElementXMI(String nomFile){
		List<Struct> result = new ArrayList<Struct>();
		try {
			
			 
	         File inputFile = new File(nomFile);
	         DocumentBuilderFactory dbFactory 
	            = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.parse(inputFile);
	         
	         HashMap<String, String> registroVP = new HashMap<String, String>();
	                  
	         doc.getDocumentElement().normalize();
	         //System.out.println("Element :" 
	           // + doc.getDocumentElement().getNodeName());
	         
	         NodeList nList = doc.getElementsByTagName("childPackages");
	         //System.out.println("----------------------------");
	         
	         //Recorro la lista de childPackages
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	            Node nNode = nList.item(temp);
	            //String name = nNode.getNodeName();
	            //System.out.println("Element : " + nNode.getNodeName());
	            
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            	
	               //Element eElement = (Element) nNode;
	               //System.out.println("Name : " + eElement.getAttribute("name"));
	               
	               //Obtengo lista de elementos hijos del nodo
	               NodeList nHijos = nNode.getChildNodes();
	               	for (int temp1 = 0; temp1 < nHijos.getLength(); temp1++) {
	               		
	               		Node nHijo = nHijos.item(temp1);
	               		
	               		if (nHijo.getNodeType() == Node.ELEMENT_NODE){
	            		   Element eHijo = (Element) nHijo;
	            		   
	            		   //System.out.println("\tElement: " 
	             	         //      + nHijo.getNodeName());
	            		   
	            		   String nameHijo = eHijo.getAttribute("name");
	            		   //System.out.println("\t\tName: " + name );
	            		   String type = eHijo.getAttribute("xsi:type").substring(20);
	            		   //System.out.println("\t\tType : " +type);
	            		   
	            		   Struct nodo = new Struct(nameHijo,type);
	            		   result.add(nodo);
	            		   String id = eHijo.getAttribute("xmi:id");
	            		   
	            		   if (type.equals("vpActivity")){
	            			   //System.out.println("\t\tMin : " +eHijo.getAttribute("min"));
	            			   //System.out.println("\t\tMax : " +eHijo.getAttribute("max"));
	            			   registroVP.put(id, nameHijo);
	            		   }
	            		   
	            		   if (type.equals("VarActivity")){
	            		   
	            			 //Obtengo lista de elementos hijos del nodo
	                           NodeList nHijosVar = nHijo.getChildNodes();
	                           	for (int temp2 = 0; temp2 < nHijosVar.getLength(); temp2++) {
	                           		
	                           		Node nHijoVar = nHijosVar.item(temp2);
	                           		                           		
	                           		if ((nHijoVar.getNodeType() == Node.ELEMENT_NODE) && (nHijoVar.getNodeName().equals("client"))){
	                        		   Element eHijoVar = (Element) nHijoVar;
	                        		                           		   
	                        		   if  (eHijoVar.getAttribute("xsi:type").substring(20).equals("variant2varP")){
	                        			   		if (!registroVP.isEmpty()){
	                        			   			String varPoint = registroVP.get(eHijoVar.getAttribute("supplier"));
	                        			   			//System.out.println("\t\tCorresponde al VarPoint:  " + varPoint);
	                        			   			//System.out.println("\t\tEs Inclusiva:  " + eHijoVar.getAttribute("isInclusive"));
	                        			   		}
	                        		   }
	            		   }
	            		   
	            		   
	               		}
	            	 
	               }
	               	//System.out.println();
	              }
	               
	            }
	           }
	         }
	      
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
        return result;
     
     }

}
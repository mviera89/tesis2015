package vSpemLogica;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class XMLParser {
   public static void main(String[] args){

      try {	
         File inputFile = new File("model.xmi");
         DocumentBuilderFactory dbFactory 
            = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.parse(inputFile);
         doc.getDocumentElement().normalize();
         System.out.println("Element :" 
            + doc.getDocumentElement().getNodeName());
         
         NodeList nList = doc.getElementsByTagName("childPackages");
         System.out.println("----------------------------");
         
         //Recorro la lista de childPackages
         for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            
            System.out.println("Element :" 
               + nNode.getNodeName());
            
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;
               System.out.println("Name : " 
                  + eElement.getAttribute("name"));
               
               //Obtengo lista de elementos hijos del nodo
               NodeList nHijos = nNode.getChildNodes();
               	for (int temp1 = 0; temp1 < nHijos.getLength(); temp1++) {
               		
               		Node nHijo = nHijos.item(temp1);
               		
               		if (nHijo.getNodeType() == Node.ELEMENT_NODE){
            		   Element eHijo = (Element) nHijo;
            		   
            		   System.out.println("\tProcess Element: " 
             	               + nHijo.getNodeName());
            		   
            		   System.out.println("\tName: " + eHijo.getAttribute("name"));
            		   String type = eHijo.getAttribute("xsi:type").substring(20);
            		   System.out.println("\tType : " +type+ "\n");
            		   
            		   if (type.equals("VarActivity")){
            		   //ver a cual punto de variacion pertenece
            		   
            		   }
            		   
            		   
               		}
            	 
               }
               	System.out.println();
            }
               
              // System.out.println("First Name : " 
                //  + eElement
                  //.getElementsByTagName("processElements"));
                  //.item(0)
                  //.getTextContent());  
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}


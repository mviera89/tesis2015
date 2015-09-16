package managedBeans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.model.TreeNode;

import config.Constantes;
import dominio.Document;
import dominio.Struct;
 
@ManagedBean
public class ExportarModeloBean {
	
	public void exportarModelo(TreeNode treeAdaptado){
		try{
			if (treeAdaptado != null){
				File archivo = new File(Constantes.destinoExport + Constantes.nomArchivoExport);
				OutputStream out = new FileOutputStream(archivo);
				
				String texto =
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" +
					"<uma:MethodLibrary xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:uma=\"http://www.eclipse.org/epf/uma/1.0.3\" name=\"LibraryPublish\" briefDescription=\"\" id=\"_ot_IIFyXEeWvU7GfTaR-Wg\" orderingGuide=\"\" suppressed=\"false\" authors=\"\" changeDescription=\"\" version=\"\" tool=\"epf=1.2.0\">" + "\n" +
					  "<MethodElementProperty value=\"0\"/>" + "\n" +
					  "<MethodElementProperty value=\"_rZz1MFyXEeWvU7GfTaR-Wg\"/>" + "\n" +
					  "<MethodElementProperty value=\"EjemploPublish1\"/>" + "\n" +
					  "<MethodPlugin name=\"EjemploPublish1\" briefDescription=\"\" id=\"_rZz1MFyXEeWvU7GfTaR-Wg\" orderingGuide=\"\" suppressed=\"false\" authors=\"\" changeDescription=\"\" version=\"\" userChangeable=\"true\">" + "\n" +
					    "<MethodPackage xsi:type=\"uma:ContentCategoryPackage\" name=\"ContentCategories\" id=\"_m9EDcFyfEeWvU7GfTaR-Wg\">" + "\n" +
					      "<ContentCategory xsi:type=\"uma:CustomCategory\" name=\"EjemploPublish1CC\" briefDescription=\"\" id=\"_BoAG8FyeEeWvU7GfTaR-Wg\" orderingGuide=\"\" suppressed=\"false\" presentationName=\"EjemploPublish1CC\" variabilityType=\"na\">" + "\n" +
					        "<CategorizedElement>_19pnYVyXEeWvU7GfTaR-Wg</CategorizedElement>" + "\n" +
					      "</ContentCategory>" + "\n" +
					    "</MethodPackage>" + "\n" +
					    "<MethodPackage xsi:type=\"uma:ProcessComponent\" name=\"EjemploPublish1DP\" briefDescription=\"\" id=\"_19pnYFyXEeWvU7GfTaR-Wg\" orderingGuide=\"\" suppressed=\"false\" global=\"false\" authors=\"\" changeDescription=\"\" version=\"\">" + "\n" +
					      "<Process xsi:type=\"uma:DeliveryProcess\" name=\"EjemploPublish1DP\" briefDescription=\"\" id=\"_19pnYVyXEeWvU7GfTaR-Wg\" orderingGuide=\"\" suppressed=\"false\" presentationName=\"EjemploPublish1DP\" hasMultipleOccurrences=\"false\" isOptional=\"false\" isPlanned=\"true\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" IsEnactable=\"false\" variabilityType=\"na\">" + "\n" +
					        "<Presentation xsi:type=\"uma:DeliveryProcessDescription\" name=\"EjemploPublish1DP,_19pnYVyXEeWvU7GfTaR-Wg\" briefDescription=\"\" id=\"-CZu7P3BJkdsHiMFqE6f-zA\" orderingGuide=\"\" suppressed=\"false\" authors=\"\" changeDescription=\"\" version=\"\" externalId=\"\" usageGuidance=\"\">" + "\n" +
					          "<MainDescription></MainDescription>" + "\n" +
					          "<KeyConsiderations></KeyConsiderations>" + "\n" +
					          "<Alternatives></Alternatives>" + "\n" +
					          "<HowToStaff></HowToStaff>" + "\n" +
					          "<Purpose></Purpose>" + "\n" +
					          "<Scope></Scope>" + "\n" +
					          "<UsageNotes></UsageNotes>" + "\n" +
					          "<Scale></Scale>" + "\n" +
					          "<ProjectCharacteristics></ProjectCharacteristics>" + "\n" +
					          "<RiskLevel></RiskLevel>" + "\n" +
					          "<EstimatingTechnique></EstimatingTechnique>" + "\n" +
					          "<ProjectMemberExpertise></ProjectMemberExpertise>" + "\n" +
					          "<TypeOfContract></TypeOfContract>" + "\n" +
					        "</Presentation>" + "\n";
				
				List<TreeNode> nodos = treeAdaptado.getChildren();
				Iterator<TreeNode> it = nodos.iterator();
				while (it.hasNext()){
					Document nodo = (Document) it.next().getData();
					String nombre = nodo.getName();
					String nombrePresentacion = nombre;
					String id = nodo.getElementID();
					texto += 
							"<BreakdownElement xsi:type=\"uma:Activity\" name=\"" + nombre + "\" briefDescription=\"\" id=\"" + id + "\" orderingGuide=\"\" suppressed=\"false\" presentationName=\"" + nombrePresentacion + "\" hasMultipleOccurrences=\"false\" isOptional=\"false\" isPlanned=\"true\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" IsEnactable=\"false\" variabilityType=\"na\">" + "\n" +
							"<SuperActivity>_19pnYVyXEeWvU7GfTaR-Wg</SuperActivity>" + "\n" +
							"</BreakdownElement>" + "\n";
					       /* "<BreakdownElement xsi:type=\"uma:Activity\" name=\"Relevamiento de requerimientos\" briefDescription=\"\" id=\"_3gq5UFyXEeWvU7GfTaR-Wg\" orderingGuide=\"\" suppressed=\"false\" presentationName=\"Relevamiento de requerimientos\" hasMultipleOccurrences=\"false\" isOptional=\"false\" isPlanned=\"true\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" IsEnactable=\"false\" variabilityType=\"na\">" + "\n" +
					          "<SuperActivity>_19pnYVyXEeWvU7GfTaR-Wg</SuperActivity>" + "\n" +
					        "</BreakdownElement>" + "\n" +
					        "<BreakdownElement xsi:type=\"uma:Activity\" name=\"Analisis y Disenio\" briefDescription=\"\" id=\"_42wigFyXEeWvU7GfTaR-Wg\" orderingGuide=\"\" suppressed=\"false\" presentationName=\"Analisis y Disenio\" hasMultipleOccurrences=\"false\" isOptional=\"false\" isPlanned=\"true\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" IsEnactable=\"false\" variabilityType=\"na\">" + "\n" +
					          "<SuperActivity>_19pnYVyXEeWvU7GfTaR-Wg</SuperActivity>" + "\n" +
					        "</BreakdownElement>" + "\n" +
					        "<BreakdownElement xsi:type=\"uma:Activity\" name=\"Implementacion\" briefDescription=\"\" id=\"_590RMFyXEeWvU7GfTaR-Wg\" orderingGuide=\"\" suppressed=\"false\" presentationName=\"Implementacion\" hasMultipleOccurrences=\"false\" isOptional=\"false\" isPlanned=\"true\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" IsEnactable=\"false\" variabilityType=\"na\">" + "\n" +
					          "<SuperActivity>_19pnYVyXEeWvU7GfTaR-Wg</SuperActivity>" + "\n" +
					        "</BreakdownElement>" + "\n" +
					        "<BreakdownElement xsi:type=\"uma:Activity\" name=\"Testing\" briefDescription=\"\" id=\"_7Bk0QFyXEeWvU7GfTaR-Wg\" orderingGuide=\"\" suppressed=\"false\" presentationName=\"Testing\" hasMultipleOccurrences=\"false\" isOptional=\"false\" isPlanned=\"true\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" IsEnactable=\"false\" variabilityType=\"na\">" + "\n" +
					          "<SuperActivity>_19pnYVyXEeWvU7GfTaR-Wg</SuperActivity>" + "\n" +
					        "</BreakdownElement>" + "\n" +*/
				}
				
				texto +=
					        "<DefaultContext>_wN52UFyXEeWvU7GfTaR-Wg</DefaultContext>" + "\n" +
					        "<ValidContext>_wN52UFyXEeWvU7GfTaR-Wg</ValidContext>" + "\n" +
					      "</Process>" + "\n" +
					    "</MethodPackage>" + "\n" +
					  "</MethodPlugin>" + "\n" +
					  "<MethodConfiguration name=\"ConfiguracionPublish\" briefDescription=\"\" id=\"_wN52UFyXEeWvU7GfTaR-Wg\" orderingGuide=\"\" suppressed=\"false\" authors=\"\" changeDescription=\"\" version=\"\">" + "\n" +
					    "<MethodPluginSelection>_rZz1MFyXEeWvU7GfTaR-Wg</MethodPluginSelection>" + "\n" +
					    "<MethodPackageSelection>_rZz1PlyXEeWvU7GfTaR-Wg</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_rZz1P1yXEeWvU7GfTaR-Wg</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_rZz1QVyXEeWvU7GfTaR-Wg</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_rZz1MVyXEeWvU7GfTaR-Wg</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_rZz1PVyXEeWvU7GfTaR-Wg</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_m9EDcFyfEeWvU7GfTaR-Wg</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_rZz1MlyXEeWvU7GfTaR-Wg</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_rZz1QFyXEeWvU7GfTaR-Wg</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_rZz1OVyXEeWvU7GfTaR-Wg</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_rZz1M1yXEeWvU7GfTaR-Wg</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_rZz1O1yXEeWvU7GfTaR-Wg</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_19pnYFyXEeWvU7GfTaR-Wg</MethodPackageSelection>" + "\n" +
					    "<ProcessView>_BoAG8FyeEeWvU7GfTaR-Wg</ProcessView>" + "\n" +
					  "</MethodConfiguration>" + "\n" +
					"</uma:MethodLibrary>";
				
		        byte bytes[] = texto.getBytes();
		        out.write(bytes);
			    
			    out.flush();
			    out.close();
			    
			    FacesMessage mensaje = new FacesMessage("", "El modelo ha sido exportado correctamente.");
	            FacesContext.getCurrentInstance().addMessage(null, mensaje);
	            
	            FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
	    		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
	    		VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
	    		vb.setFinModelado(true);
			}
			else{
				System.out.println("##### exportarModelo - treeAdaptado: null");
			}
		}
    	catch (IOException e) {
    		System.out.println(e.getMessage());
		}
	}
	
}
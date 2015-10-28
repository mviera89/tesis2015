package managedBeans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.model.TreeNode;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;

import config.Constantes;
import dataTypes.TipoElemento;
import dominio.Document;
import dominio.Struct;
 
@ManagedBean
public class ExportarModeloBean {
	
	public void exportarModelo(DefaultDiagramModel modeloAdaptado){
		try{
			if (modeloAdaptado != null){
				File archivo = new File(Constantes.destinoExport + Constantes.nomArchivoExport);
				OutputStream out = new FileOutputStream(archivo);
				
				/*** Para vEPF ***/
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
				
				List<String> idsAgregados = new ArrayList<String>();
				List<Element> elementos = modeloAdaptado.getElements();
				Iterator<Element> it = elementos.iterator();
				while (it.hasNext()){
					Struct s = (Struct) it.next().getData();
					String nombre = s.getNombre();
					String nombrePresentacion = nombre;
					String id = s.getElementID();
					TipoElemento tipo = s.getType();
					if ((tipo != TipoElemento.PROCESS_PACKAGE) && (!idsAgregados.contains(id))){
						idsAgregados.add(id);
						
						String superactivity = "_19pnYVyXEeWvU7GfTaR-Wg";
						if (tipo == TipoElemento.ACTIVITY){
							texto += 
									"<BreakdownElement xsi:type=\"uma:Activity\" name=\"" + nombre + "\" briefDescription=\"\" id=\"" + id + "\" orderingGuide=\"\" suppressed=\"false\" presentationName=\"" + nombrePresentacion + "\" hasMultipleOccurrences=\"false\" isOptional=\"false\" isPlanned=\"true\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" IsEnactable=\"false\" variabilityType=\"na\">" + "\n" +
											"<SuperActivity>" + superactivity + "</SuperActivity>" + "\n" +
									"</BreakdownElement>" + "\n";
						}
						else if (tipo == TipoElemento.ITERATION){
							texto += 
									"<BreakdownElement xsi:type=\"uma:Iteration\" name=\"" + nombre + "\" briefDescription=\"\" id=\"" + id + "\" orderingGuide=\"\" suppressed=\"false\" presentationName=\"" + nombrePresentacion + "\" hasMultipleOccurrences=\"false\" isOptional=\"false\" isPlanned=\"true\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" IsEnactable=\"false\" variabilityType=\"na\">" + "\n" +
											"<SuperActivity>" + superactivity + "</SuperActivity>" + "\n" +
									"</BreakdownElement>" + "\n";
						}
						else if (tipo == TipoElemento.PHASE){
							texto += 
									"<BreakdownElement xsi:type=\"uma:Phase\" name=\"" + nombre + "\" briefDescription=\"\" id=\"" + id + "\" orderingGuide=\"\" suppressed=\"false\" presentationName=\"" + nombrePresentacion + "\" hasMultipleOccurrences=\"false\" isOptional=\"false\" isPlanned=\"true\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" IsEnactable=\"false\" variabilityType=\"na\">" + "\n" +
											"<SuperActivity>" + superactivity + "</SuperActivity>" + "\n" +
									"</BreakdownElement>" + "\n";
						}
						else if (tipo == TipoElemento.TASK){
							texto += 
									"<BreakdownElement xsi:type=\"uma:TaskDescriptor\" name=\"" + nombre + "\" briefDescription=\"\" id=\"" + id + "\" orderingGuide=\"\" suppressed=\"false\" presentationName=\"" + nombrePresentacion + "\" hasMultipleOccurrences=\"false\" isOptional=\"false\" isPlanned=\"false\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" isSynchronizedWithSource=\"true\">" + "\n"  +
											"<SuperActivity>" + superactivity + "</SuperActivity>" +
					          		"</BreakdownElement>" + "\n";
						}
						
					}
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

				/*** Para EPF-C ***/
				/*String texto = 
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" +
						"<uma:MethodLibrary xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:uma=\"http://www.eclipse.org/epf/uma/1.0.6\" name=\"Library1\" briefDescription=\"\" id=\"_4voVEFy-EeWgQtxNTihl_w\" orderingGuide=\"\" presentationName=\"\" suppressed=\"false\" authors=\"\" changeDescription=\"\" version=\"\" tool=\"epf=1.5.0\">" + "\n" +
						  "<MethodElementProperty name=\"library_synFree\" value=\"true\"/>" + "\n" +
						  "<MethodPlugin name=\"new_plug-in\" briefDescription=\"\" id=\"_OIDEIFy_EeWgQtxNTihl_w\" orderingGuide=\"\" presentationName=\"\" suppressed=\"false\" authors=\"\" changeDescription=\"\" version=\"\" supporting=\"false\" userChangeable=\"true\">" + "\n" +
						    "<MethodElementProperty name=\"plugin_synFree\" value=\"true\"/>" + "\n" +
						    "<MethodPackage xsi:type=\"uma:ContentCategoryPackage\" name=\"ContentCategories\" id=\"_G90coFzAEeWgQtxNTihl_w\">" + "\n" +
						      "<ContentCategory xsi:type=\"uma:CustomCategory\" name=\"new_custom_category\" briefDescription=\"\" id=\"_ULZtsFy_EeWgQtxNTihl_w\" orderingGuide=\"\" presentationName=\"new_custom_category\" suppressed=\"false\" isAbstract=\"false\" variabilityType=\"na\">" + "\n" +
						        "<MethodElementProperty name=\"me_edited\" value=\"true\"/>" + "\n" +
						        "<CategorizedElement>_Pyl6YVy_EeWgQtxNTihl_w</CategorizedElement>" + "\n" +
						      "</ContentCategory>" + "\n" +
						    "</MethodPackage>" + "\n" +
						    "<MethodPackage xsi:type=\"uma:ProcessComponent\" name=\"dp1\" briefDescription=\"\" id=\"_Pyl6YFy_EeWgQtxNTihl_w\" orderingGuide=\"\" presentationName=\"\" suppressed=\"false\" global=\"false\" authors=\"\" changeDescription=\"\" version=\"\">" + "\n" +
						      "<MethodElementProperty name=\"pkg_loadCheck\" value=\"true\"/>" + "\n" +
						      "<MethodElementProperty name=\"me_edited\" value=\"true\"/>" + "\n" +
						      "<Process xsi:type=\"uma:DeliveryProcess\" name=\"dp1\" briefDescription=\"\" id=\"_Pyl6YVy_EeWgQtxNTihl_w\" orderingGuide=\"\" presentationName=\"dp1\" suppressed=\"false\" isAbstract=\"false\" hasMultipleOccurrences=\"false\" isOptional=\"false\" isPlanned=\"true\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" variabilityType=\"na\">" + "\n" +
						        "<Presentation xsi:type=\"uma:DeliveryProcessDescription\" name=\"dp1,_Pyl6YVy_EeWgQtxNTihl_w\" briefDescription=\"\" id=\"-NylF8m6y-xXRpyezC4dSOw\" orderingGuide=\"\" presentationName=\"\" suppressed=\"false\" authors=\"\" changeDescription=\"\" version=\"\" externalId=\"\" usageGuidance=\"\">" + "\n" +
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
							"<SuperActivity>_Pyl6YVy_EeWgQtxNTihl_w</SuperActivity>" + "\n" +
							"</BreakdownElement>" + "\n";
				}
				
				texto += "</Process>" + "\n" +
					    "</MethodPackage>" + "\n" +
					  "</MethodPlugin>" + "\n" +
					  "<MethodConfiguration name=\"new_config\" briefDescription=\"\" id=\"_necEoFy_EeWgQtxNTihl_w\" orderingGuide=\"\" presentationName=\"\" suppressed=\"false\" authors=\"\" changeDescription=\"\" version=\"\">" + "\n" +
					    "<MethodElementProperty name=\"TouchedByConfigEditor\" value=\"true\"/>" + "\n" +
					    "<MethodElementProperty name=\"Config_doneLoadCheckPkgs\" value=\"_Pyl6YFy_EeWgQtxNTihl_w\"/>" + "\n" +
					    "<MethodElementProperty name=\"me_edited\" value=\"true\"/>" + "\n" +
					    "<MethodPluginSelection>_OIDEIFy_EeWgQtxNTihl_w</MethodPluginSelection>" + "\n" +
					    "<MethodPackageSelection>_OIDrOFy_EeWgQtxNTihl_w</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_Pyl6YFy_EeWgQtxNTihl_w</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_OIDrN1y_EeWgQtxNTihl_w</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_R9baQFy_EeWgQtxNTihl_w</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_OIDEIVy_EeWgQtxNTihl_w</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_OIDrNly_EeWgQtxNTihl_w</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_G90coFzAEeWgQtxNTihl_w</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_OIDEIly_EeWgQtxNTihl_w</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_OIDrMly_EeWgQtxNTihl_w</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>_OIDrNFy_EeWgQtxNTihl_w</MethodPackageSelection>" + "\n" +
					    "<ProcessView>_ULZtsFy_EeWgQtxNTihl_w</ProcessView>" + "\n" +
					  "</MethodConfiguration>" + "\n" +
					"</uma:MethodLibrary>";
				*/
				
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
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

import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;

import config.Constantes;
import dataTypes.TipoElemento;
import dominio.Struct;
 
@ManagedBean
public class ExportarModeloBean {
	
	public void exportarModelo(DefaultDiagramModel modeloAdaptado){
		try{
			if (modeloAdaptado != null){
				FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
				HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
				VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
		        String nomArchivo = vb.getNombreArchivo();
		        nomArchivo = nomArchivo.substring(0, nomArchivo.length() - 4); // Para quitar la extensi�n
				
				File archivo = new File(Constantes.destinoExport + nomArchivo + "_" + Constantes.nomArchivoExport);
				OutputStream out = new FileOutputStream(archivo);

				String versionXML = "1.0";
				String encodingXML = "UTF-8";
				String xmlns_xsi = "http://www.w3.org/2001/XMLSchema-instance";
				String xmlns_uma = "http://www.eclipse.org/epf/uma/1.0.3";
				
				String methodLibraryName = "LibraryPublish";
				String methodLibraryId = "_ot_IIFyXEeWvU7GfTaR-Wg";
				String methodLibraryBriefDescription = "";
				String methodLibraryOrderingGuide = "";
				String methodLibrarySuppressed = "false";
				String methodLibraryAuthors = "";
				String methodLibraryChangeDescription = "";
				String methodLibraryVersion = "";
				String methodLibraryTool = "epf=1.2.0";
				
				String methodPluginSelectionId = "_rZz1MFyXEeWvU7GfTaR-Wg";
				String methodPluginSelectionName = "EjemploPublish1";
				String methodPluginSelectionBriefDescription = "";
				String methodPluginSelectionOrderingGuide = "";
				String methodPluginSelectionSuppressed = "false";
				String methodPluginSelectionAuthors = "";
				String methodPluginSelectionChangeDescription = "";
				String methodPluginSelectionVersion = "";
				String methodPluginSelectionUserChangeable = "true";
				
				String customCategoryId = "_m9EDcFyfEeWvU7GfTaR-Wg";
				String customCategoryName = "EjemploPublish1CC";
				String customCategoryBriefDescription = "";
				String idProcessView = "_BoAG8FyeEeWvU7GfTaR-Wg";
				String customCategoryOrderingGuide = "";
				String customCategorySuppressed = "false";
				String customCategoryPresentationName = "EjemploPublish1CC";
				String customCategoryVariabilityType = "na";
				String categorizedElement = "_19pnYVyXEeWvU7GfTaR-Wg";
				
				String processName = "EjemploPublish1DP";
				String processId = "_19pnYFyXEeWvU7GfTaR-Wg";
				String processBriefDescription = "";
				String processOrderingGuide = "";
				String processSuppressed = "false";
				String processGlobal = "false";
				String processAuthors = "";
				String processChangeDescription = "";
				String processVersion = "";
				String processPresentationName = "EjemploPublish1DP";
				String processHasMultipleOccurrences = "false";
				String processIsOptional = "false";
				String processIsPlanned = "true";
				String processPrefix = "";
				String processIsEventDriven = "false";
				String processIsOngoing = "false";
				String processIsRepeatable = "false";
				String processIsEnactable = "false";
				String processVariabilityType = "na";
				String deliveryProcessDescriptionId = "-CZu7P3BJkdsHiMFqE6f-zA";
				String processExternalId = "";
				String processUsageGuidance = "";
				
				String methodConfigurationName = "ConfiguracionPublish";
				String methodConfigurationId = "_wN52UFyXEeWvU7GfTaR-Wg";
				String methodConfigurationBriefDescription = "";
				String methodConfigurationOrderingGuide = "";
				String methodConfigurationSuppressed = "false";
				String methodConfigurationAuthors = "";
				String methodConfigurationChangeDescription = "";
				String methodConfigurationVersion = "";
				
				
				/*** Para vEPF ***/
				String texto =
					"<?xml version=\"" + versionXML + "\" encoding=\"" + encodingXML + "\"?>" + "\n" +
					"<uma:MethodLibrary xmlns:xsi=\"" + xmlns_xsi + "\" xmlns:uma=\"" + xmlns_uma + "\" name=\"" + methodLibraryName + "\" briefDescription=\"" + methodLibraryBriefDescription + "\" id=\"" + methodLibraryId + "\" orderingGuide=\"" + methodLibraryOrderingGuide + "\" suppressed=\"" + methodLibrarySuppressed + "\" authors=\"" + methodLibraryAuthors + "\" changeDescription=\"" + methodLibraryChangeDescription + "\" version=\"" + methodLibraryVersion + "\" tool=\"" + methodLibraryTool + "\">" + "\n" +
					  "<MethodElementProperty value=\"0\"/>" + "\n" +
					  "<MethodElementProperty value=\"" + methodPluginSelectionId + "\"/>" + "\n" +
					  "<MethodElementProperty value=\"" + methodPluginSelectionName + "\"/>" + "\n" +
					  "<MethodPlugin name=\"" + methodPluginSelectionName + "\" briefDescription=\"" + methodPluginSelectionBriefDescription + "\" id=\"" + methodPluginSelectionId + "\" orderingGuide=\"" + methodPluginSelectionOrderingGuide + "\" suppressed=\"" + methodPluginSelectionSuppressed + "\" authors=\"" + methodPluginSelectionAuthors + "\" changeDescription=\"" + methodPluginSelectionChangeDescription + "\" version=\"" + methodPluginSelectionVersion + "\" userChangeable=\"" + methodPluginSelectionUserChangeable + "\">" + "\n" +
					    
					  	// CustomCategory
					  	"<MethodPackage xsi:type=\"uma:ContentCategoryPackage\" name=\"ContentCategories\" id=\"" + customCategoryId + "\">" + "\n" +
					      "<ContentCategory xsi:type=\"uma:CustomCategory\" name=\"" + customCategoryName + "\" briefDescription=\"" + customCategoryBriefDescription + "\" id=\"" + idProcessView + "\" orderingGuide=\"" + customCategoryOrderingGuide + "\" suppressed=\"" + customCategorySuppressed + "\" presentationName=\"" + customCategoryPresentationName + "\" variabilityType=\"" + customCategoryVariabilityType + "\">" + "\n" +
					        "<CategorizedElement>" + categorizedElement + "</CategorizedElement>" + "\n" +
					      "</ContentCategory>" + "\n" +
					    "</MethodPackage>" + "\n" +
					    
					    // DeliveryProcess
					    "<MethodPackage xsi:type=\"uma:ProcessComponent\" name=\"" + processName + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + processId + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" global=\"" + processGlobal + "\" authors=\"" + processAuthors + "\" changeDescription=\"" + processChangeDescription + "\" version=\"" + processVersion + "\">" + "\n" +
					      "<Process xsi:type=\"uma:DeliveryProcess\" name=\"" + processName + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + categorizedElement + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" presentationName=\"" + processPresentationName + "\" hasMultipleOccurrences=\"" + processHasMultipleOccurrences + "\" isOptional=\"" + processIsOptional + "\" isPlanned=\"" + processIsPlanned + "\" prefix=\"" + processPrefix + "\" isEventDriven=\"" + processIsEventDriven + "\" isOngoing=\"" + processIsOngoing + "\" isRepeatable=\"" + processIsRepeatable + "\" IsEnactable=\"" + processIsEnactable + "\" variabilityType=\"" + processVariabilityType + "\">" + "\n" +
					        "<Presentation xsi:type=\"uma:DeliveryProcessDescription\" name=\"" + processName + "," + categorizedElement + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + deliveryProcessDescriptionId + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" authors=\"" + processAuthors + "\" changeDescription=\"" + processChangeDescription + "\" version=\"" + processVersion + "\" externalId=\"" + processExternalId + "\" usageGuidance=\"" + processUsageGuidance + "\">" + "\n" +
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
					String id = s.getElementID();
					TipoElemento tipo = s.getType();
					if (tipo != null){
						if ((tipo == TipoElemento.CAPABILITY_PATTERN) || (tipo == TipoElemento.DELIVERY_PROCESS)){
							methodConfigurationName = s.getNombre();
							methodConfigurationBriefDescription = s.getDescription();
							processPresentationName = s.getPresentationName();
						}
						else if ((tipo != TipoElemento.PROCESS_PACKAGE) && (!idsAgregados.contains(id))){
							idsAgregados.add(id);
							texto += agregarElementoAxml(s, categorizedElement);
						}
					}
				}
				
				texto +=
					        "<DefaultContext>" + methodConfigurationId + "</DefaultContext>" + "\n" +
					        "<ValidContext>" + methodConfigurationId + "</ValidContext>" + "\n" +
					      "</Process>" + "\n" +
					    "</MethodPackage>" + "\n" +
					  "</MethodPlugin>" + "\n" +
					  "<MethodConfiguration name=\"" + methodConfigurationName + "\" briefDescription=\"" + methodConfigurationBriefDescription + "Holaaaaaa" /*+"\" presentationName=\"" + processPresentationName*/ + "\" id=\"" + methodConfigurationId + "\" orderingGuide=\"" + methodConfigurationOrderingGuide + "\" suppressed=\"" + methodConfigurationSuppressed + "\" authors=\"" + methodConfigurationAuthors + "\" changeDescription=\"" + methodConfigurationChangeDescription + "\" version=\"" + methodConfigurationVersion + "\">" + "\n" +
					    "<MethodPluginSelection>" + methodPluginSelectionId + "</MethodPluginSelection>" + "\n" +
					    "<MethodPackageSelection>" + customCategoryId + "</MethodPackageSelection>" + "\n" +
					    "<MethodPackageSelection>" + processId + "</MethodPackageSelection>" + "\n" +
					    "<ProcessView>" + idProcessView + "</ProcessView>" + "\n" +
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
	
	public String agregarElementoAxml(Struct s, String superactivity){
		String texto = "";
		String nombre = s.getNombre();
		String nombrePresentacion = nombre;
		String id = s.getElementID();
		TipoElemento tipo = s.getType();
		
		if (tipo == TipoElemento.ACTIVITY){
			texto += "<BreakdownElement xsi:type=\"uma:Activity\" name=\"" + nombre + "\" briefDescription=\"\" id=\"" + id + "\" orderingGuide=\"\" " +
					 "suppressed=\"false\" presentationName=\"" + nombrePresentacion + "\" hasMultipleOccurrences=\"false\" isOptional=\"false\" " +
					 "isPlanned=\"true\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" IsEnactable=\"false\" variabilityType=\"na\">" + "\n";
		}
		else if (tipo == TipoElemento.ITERATION){
			texto += "<BreakdownElement xsi:type=\"uma:Iteration\" name=\"" + nombre + "\" briefDescription=\"\" id=\"" + id + "\" orderingGuide=\"\" " +
					 "suppressed=\"false\" presentationName=\"" + nombrePresentacion + "\" hasMultipleOccurrences=\"false\" isOptional=\"false\" " +
					 "isPlanned=\"true\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" IsEnactable=\"false\" variabilityType=\"na\">" + "\n";
		}
		else if (tipo == TipoElemento.PHASE){
			texto += "<BreakdownElement xsi:type=\"uma:Phase\" name=\"" + nombre + "\" briefDescription=\"\" id=\"" + id + "\" orderingGuide=\"\" " +
					 "suppressed=\"false\" presentationName=\"" + nombrePresentacion + "\" hasMultipleOccurrences=\"false\" isOptional=\"false\" " +
					 "isPlanned=\"true\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" IsEnactable=\"false\" variabilityType=\"na\">" + "\n";
		}
		else if (tipo == TipoElemento.TASK){
			texto += "<BreakdownElement xsi:type=\"uma:TaskDescriptor\" name=\"" + nombre + "\" briefDescription=\"\" id=\"" + id + "\" orderingGuide=\"\" " +
					 "suppressed=\"false\" presentationName=\"" + nombrePresentacion + "\" hasMultipleOccurrences=\"false\" isOptional=\"false\" " +
					 "isPlanned=\"false\" prefix=\"\" isEventDriven=\"false\" isOngoing=\"false\" isRepeatable=\"false\" isSynchronizedWithSource=\"true\">" + "\n";
		}
		
		if (!texto.equals("")){
			texto += "<SuperActivity>" + superactivity + "</SuperActivity>" + "\n";
			
			// Agrego los hijos
			Iterator<Struct> it = s.getHijos().iterator();
			while (it.hasNext()){
				Struct hijo = it.next();
				texto += agregarElementoAxml(hijo, id);
			}
			
			texto += "</BreakdownElement>" + "\n";
		}
		
		return texto;
	}
	
}
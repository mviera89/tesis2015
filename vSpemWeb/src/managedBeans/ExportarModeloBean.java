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
import dataTypes.TipoContentCategory;
import dataTypes.TipoContentDescription;
import dataTypes.TipoElemento;
import dataTypes.TipoLibrary;
import dataTypes.TipoMethodConfiguration;
import dataTypes.TipoPlugin;
import dominio.Struct;
 
@ManagedBean
public class ExportarModeloBean {

	private List<String> idCapabilityPatterns = new ArrayList<String>();
	private List<String> idsAgregados = new ArrayList<String>();
	private List<String> processIds = new ArrayList<String>();
	private String textoCapabilityPattern = "";
	
	public void exportarModelo(DefaultDiagramModel modeloAdaptado){
		try{
			if (modeloAdaptado != null){
				FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
				HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
				VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
		        String nomArchivo = vb.getNombreArchivo();
		        nomArchivo = nomArchivo.substring(0, nomArchivo.length() - 4); // Para quitar la extensión
				
				File archivo = new File(Constantes.destinoExport + nomArchivo + "_" + Constantes.nomArchivoExport);
				OutputStream out = new FileOutputStream(archivo);

				TipoLibrary library = vb.getLibrary();
				TipoPlugin plugin = vb.getPlugin();
				TipoContentCategory contentCategory = vb.getContentCategory();
				TipoContentDescription contentDescription = contentCategory.getContentDescription();
				TipoMethodConfiguration methodConfiguration = vb.getMethodConfiguration();
				
				// <?xml... ?>
				String versionXML = "1.0";
				String encodingXML = "UTF-8";
				
				// <uma:MethodLibrary... />
				String xmlns_xsi = "http://www.w3.org/2001/XMLSchema-instance";
				String xmlns_uma = "http://www.eclipse.org/epf/uma/1.0.3";
				String methodLibraryName = library.getName();
				String methodLibraryId = library.getId();
				String methodLibraryBriefDescription = "";
				String methodLibraryOrderingGuide = "";
				String methodLibrarySuppressed = "false";
				String methodLibraryAuthors = "";
				String methodLibraryChangeDescription = "";
				String methodLibraryVersion = "";
				String methodLibraryTool = "epf=1.2.0";
				
				// <MethodElementProperty... />
				/*String methodElementPropertyId = "_rZz1MFyXEeWvU7GfTaR-Wg";
				String methodElementPropertyName = "EjemploPublish1";*/
				
				// <MethodPlugin... />
				String methodPluginSelectionId = plugin.getId();
				String methodPluginSelectionName = plugin.getName();
				String methodPluginSelectionAuthors = plugin.getAuthors();
				String methodPluginSelectionBriefDescription = plugin.getBriefDescription();
				String methodPluginSelectionVersion = plugin.getVersion();
				String methodPluginSelectionChangeDate = plugin.getChangeDate();
				String methodPluginSelectionChangeDescription = plugin.getChangeDescription();
				String methodPluginSelectionOrderingGuide = "";
				String methodPluginSelectionSuppressed = "false";
				String methodPluginSelectionUserChangeable = "true";
				
				// <MethodPackage... />
				String contentCategoryPackageName = "ContentCategories";
				String contentCategoryId = "_J3AlUNK2EeWyCYG0_iINEw";
				String contentCategoryName =  contentCategory.getName();
				String contentCategoryBriefDescription = contentCategory.getBriefDescription();
				String idProcessView = contentCategory.getId();
				String contentCategoryPresentationName = contentCategory.getPresentationName();
				String contentDescriptionName = contentDescription.getName();
				String contentDescriptionId = contentDescription.getId();
				String contentDescriptionAutors = contentDescription.getAuthors();
				String contentDescriptionChangeDate = contentDescription.getChangeDate();
				String contentDescriptionChangeDescription = contentDescription.getChangeDescription();
				String contentDescriptionVersion = contentDescription.getVersion();
				String contentDescriptionMainDescription = contentDescription.getMainDescription();
				String contentDescriptionKeyConsiderations = contentDescription.getKeyConsiderations();
				String contentDescriptionOrderingGuide = "";
				String contentDescriptionBriefDescription = "";
				String contentDescriptionSuppressed = "false";
				String contentDescriptionExternalId = "";
				String contentCategoryOrderingGuide = "";
				String contentCategorySuppressed = "false";
				String contentCategoryVariabilityType = "na";
				
				// <Process... />
				String processBriefDescription = "";
				String processOrderingGuide = "";
				String processSuppressed = "false";
				String processGlobal = "false";
				String processAuthors = "";
				String processChangeDescription = "";
				String processVersion = "";
				String processHasMultipleOccurrences = "false";
				String processIsOptional = "false";
				String processIsPlanned = "true";
				String processPrefix = "";
				String processIsEventDriven = "false";
				String processIsOngoing = "false";
				String processIsRepeatable = "false";
				String processIsEnactable = "false";
				String processVariabilityType = "na";
				String processExternalId = "";
				String processUsageGuidance = "";
				
				// <MethodConfiguration... />
				String methodConfigurationName = methodConfiguration.getName();
				String methodConfigurationId = methodConfiguration.getId();
				String methodConfigurationBriefDescription = methodConfiguration.getBriefDescription();
				String methodConfigurationOrderingGuide = "";
				String methodConfigurationSuppressed = "false";
				String methodConfigurationAuthors = "";
				String methodConfigurationChangeDescription = "";
				String methodConfigurationVersion = "";
				
				
				/*** Para vEPF ***/
				String textoContentCategory = "";
				String textoDeliveryProcess = "";
				String idDeliveryProcess = "";
				String texto =
					"<?xml version=\"" + versionXML + "\" encoding=\"" + encodingXML + "\"?>" + "\n" +
					"<uma:MethodLibrary xmlns:xsi=\"" + xmlns_xsi + "\" xmlns:uma=\"" + xmlns_uma + "\" name=\"" + methodLibraryName + "\" briefDescription=\"" + methodLibraryBriefDescription + "\" id=\"" + methodLibraryId + "\" orderingGuide=\"" + methodLibraryOrderingGuide + "\" suppressed=\"" + methodLibrarySuppressed + "\" authors=\"" + methodLibraryAuthors + "\" changeDescription=\"" + methodLibraryChangeDescription + "\" version=\"" + methodLibraryVersion + "\" tool=\"" + methodLibraryTool + "\">" + "\n" +
						//"\t<MethodElementProperty value=\"0\"/>" + "\n" +
						//"\t<MethodElementProperty value=\"" + methodElementPropertyId + "\"/>" + "\n" +
						//"\t<MethodElementProperty value=\"" + methodElementPropertyName + "\"/>" + "\n" +
						"\t<MethodPlugin name=\"" + methodPluginSelectionName + "\" briefDescription=\"" + methodPluginSelectionBriefDescription + "\" id=\"" + methodPluginSelectionId + "\" orderingGuide=\"" + methodPluginSelectionOrderingGuide + "\" suppressed=\"" + methodPluginSelectionSuppressed + "\" authors=\"" + methodPluginSelectionAuthors + "\" changeDate=\"" + methodPluginSelectionChangeDate + "\" changeDescription=\"" + methodPluginSelectionChangeDescription + "\" version=\"" + methodPluginSelectionVersion + "\" userChangeable=\"" + methodPluginSelectionUserChangeable + "\">" + "\n";
				
				List<Element> elementos = modeloAdaptado.getElements();
				Iterator<Element> it = elementos.iterator();
				while (it.hasNext()){
					Struct s = (Struct) it.next().getData();
					TipoElemento tipo = s.getType();
					if (tipo != null){
						String processName = s.getProcessComponentName();
						String processPresentationName = s.getPresentationName();
						String processComponentId = s.getProcessComponentId();
						String processId = s.getElementID();
						String processDescriptionId = s.getPresentationId();
						
						if (tipo == TipoElemento.DELIVERY_PROCESS){
					  		// ContentCategory
							idDeliveryProcess = processId;
							processIds.add(processComponentId);
							textoContentCategory +=
				    		"\t\t<MethodPackage xsi:type=\"uma:ContentCategoryPackage\" name=\"" + contentCategoryPackageName + "\" id=\"" + contentCategoryId + "\">" + "\n" +
					  			"\t\t\t<ContentCategory xsi:type=\"uma:CustomCategory\" name=\"" + contentCategoryName + "\" briefDescription=\"" + contentCategoryBriefDescription + "\" id=\"" + idProcessView + "\" orderingGuide=\"" + contentCategoryOrderingGuide + "\" suppressed=\"" + contentCategorySuppressed + "\" presentationName=\"" + contentCategoryPresentationName + "\" variabilityType=\"" + contentCategoryVariabilityType + "\">" + "\n" +
					  				// "\t\t\t\t<MethodElementProperty name=\"PUBLISH_CATEGORY\" value=\"true\"/>" + "\n" +
					  				"\t\t\t\t<Presentation name=\"" + contentDescriptionName + "\" briefDescription=\"" + contentDescriptionBriefDescription + "\" id=\"" + contentDescriptionId + "\" orderingGuide=\"" + contentDescriptionOrderingGuide + "\" suppressed=\"" + contentDescriptionSuppressed + "\" authors=\"" + contentDescriptionAutors + "\" changeDate=\"" + contentDescriptionChangeDate + "\" changeDescription=\"" + contentDescriptionChangeDescription + "\" version=\"" + contentDescriptionVersion + "\" externalId=\"" + contentDescriptionExternalId + "\">" + "\n" +
					  					"\t\t\t\t\t<MainDescription><![CDATA[" + contentDescriptionMainDescription + "]]></MainDescription>" + "\n" +
					  					"\t\t\t\t\t<KeyConsiderations><![CDATA[" + contentDescriptionKeyConsiderations + "]]></KeyConsiderations>" + "\n" +
					  				"\t\t\t\t</Presentation>" + "\n";
							
							textoDeliveryProcess +=
		    				"\t\t<MethodPackage xsi:type=\"uma:ProcessComponent\" name=\"" + processName + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + processComponentId + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" global=\"" + processGlobal + "\" authors=\"" + processAuthors + "\" changeDescription=\"" + processChangeDescription + "\" version=\"" + processVersion + "\">" + "\n" +
	    						"\t\t\t<Process xsi:type=\"uma:" + tipo.toString() + "\" name=\"" + processName + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + processId + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" presentationName=\"" + processPresentationName + "\" hasMultipleOccurrences=\"" + processHasMultipleOccurrences + "\" isOptional=\"" + processIsOptional + "\" isPlanned=\"" + processIsPlanned + "\" prefix=\"" + processPrefix + "\" isEventDriven=\"" + processIsEventDriven + "\" isOngoing=\"" + processIsOngoing + "\" isRepeatable=\"" + processIsRepeatable + "\" IsEnactable=\"" + processIsEnactable + "\" variabilityType=\"" + processVariabilityType + "\">" + "\n" +
	    							
						    		"\t\t\t\t<Presentation xsi:type=\"uma:DeliveryProcessDescription\" name=\"" + processName + "," + processId + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + processDescriptionId + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" authors=\"" + processAuthors + "\" changeDescription=\"" + processChangeDescription + "\" version=\"" + processVersion + "\" externalId=\"" + processExternalId + "\" usageGuidance=\"" + processUsageGuidance + "\">" + "\n" +
					    				"\t\t\t\t\t<MainDescription></MainDescription>" + "\n" +
					    				"\t\t\t\t\t<KeyConsiderations></KeyConsiderations>" + "\n" +
					    				"\t\t\t\t\t<Alternatives></Alternatives>" + "\n" +
					    				"\t\t\t\t\t<HowToStaff></HowToStaff>" + "\n" +
					    				"\t\t\t\t\t<Purpose></Purpose>" + "\n" +
					    				"\t\t\t\t\t<Scope></Scope>" + "\n" +
					    				"\t\t\t\t\t<UsageNotes></UsageNotes>" + "\n" +
					    				"\t\t\t\t\t<Scale></Scale>" + "\n" +
					    				"\t\t\t\t\t<ProjectCharacteristics></ProjectCharacteristics>" + "\n" +
					    				"\t\t\t\t\t<RiskLevel></RiskLevel>" + "\n" +
					    				"\t\t\t\t\t<EstimatingTechnique></EstimatingTechnique>" + "\n" +
					    				"\t\t\t\t\t<ProjectMemberExpertise></ProjectMemberExpertise>" + "\n" +
					    				"\t\t\t\t\t<TypeOfContract></TypeOfContract>" + "\n" +
				    				"\t\t\t\t</Presentation>" + "\n";
							
							List<Struct> hijos = s.getHijos();
							Iterator<Struct> itHijos = hijos.iterator();
							while (itHijos.hasNext()){
								Struct hijo = itHijos.next(); 
								textoDeliveryProcess += agregarElementoAxml(hijo, processId);
							}
						}
						else if (tipo == TipoElemento.CAPABILITY_PATTERN){
							idCapabilityPatterns.add(processId);
							textoCapabilityPattern +=
		    				"\t\t<MethodPackage xsi:type=\"uma:ProcessComponent\" name=\"" + processName + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + processComponentId + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" global=\"" + processGlobal + "\" authors=\"" + processAuthors + "\" changeDescription=\"" + processChangeDescription + "\" version=\"" + processVersion + "\">" + "\n" +
								"\t\t\t<Process xsi:type=\"uma:" + tipo.toString() + "\" name=\"" + processName + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + processId + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" presentationName=\"" + processPresentationName + "\" hasMultipleOccurrences=\"" + processHasMultipleOccurrences + "\" isOptional=\"" + processIsOptional + "\" isPlanned=\"" + processIsPlanned + "\" prefix=\"" + processPrefix + "\" isEventDriven=\"" + processIsEventDriven + "\" isOngoing=\"" + processIsOngoing + "\" isRepeatable=\"" + processIsRepeatable + "\" IsEnactable=\"" + processIsEnactable + "\" variabilityType=\"" + processVariabilityType + "\">" + "\n" +
									
									"\t\t\t\t<Presentation xsi:type=\"uma:ProcessDescription\" name=\"" + processName + "," + processId + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + processDescriptionId + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" authors=\"" + processAuthors + "\" changeDescription=\"" + processChangeDescription + "\" version=\"" + processVersion + "\" externalId=\"" + processExternalId + "\" usageGuidance=\"" + processUsageGuidance + "\">" + "\n" +
				    					"\t\t\t\t\t<MainDescription></MainDescription>" + "\n" +
				    					"\t\t\t\t\t<KeyConsiderations></KeyConsiderations>" + "\n" +
				    					"\t\t\t\t\t<Alternatives></Alternatives>" + "\n" +
				    					"\t\t\t\t\t<HowToStaff></HowToStaff>" + "\n" +
				    					"\t\t\t\t\t<Purpose></Purpose>" + "\n" +
				    					"\t\t\t\t\t<Scope></Scope>" + "\n" +
				    					"\t\t\t\t\t<UsageNotes></UsageNotes>" + "\n" +
			    					"\t\t\t\t</Presentation>" + "\n";
							
							List<Struct> hijos = s.getHijos();
							Iterator<Struct> itHijos = hijos.iterator();
							while (itHijos.hasNext()){
								Struct hijo = itHijos.next(); 
								textoCapabilityPattern += agregarElementoAxml(hijo, processId);
							}
							
							textoCapabilityPattern +=
									"\t\t\t\t<DefaultContext>" + methodConfigurationId + "</DefaultContext>" + "\n" +
			        				"\t\t\t\t<ValidContext>" + methodConfigurationId + "</ValidContext>" + "\n" +
		        				"\t\t\t</Process>" + "\n" +
		    				"\t\t</MethodPackage>" + "\n";
						}
					}
				}
				
				texto += textoContentCategory +
									"\t\t\t\t<CategorizedElement>" + idDeliveryProcess + "</CategorizedElement>" + "\n" +
								"\t\t\t</ContentCategory>" + "\n" +
			  					//\t\t\t<ContentCategory ...
	  						"\t\t</MethodPackage>" + "\n";
			
				if (textoCapabilityPattern != ""){
					texto += textoCapabilityPattern;
				}
				
				if (textoDeliveryProcess != ""){
					texto += textoDeliveryProcess;
					if (textoCapabilityPattern != ""){
						for (String id: idCapabilityPatterns){
							texto += 
									"\t\t\t\t<IncludesPattern>" + id + "</IncludesPattern>" + "\n";
						}
					}
					texto +=
								"\t\t\t\t<DefaultContext>" + methodConfigurationId + "</DefaultContext>" + "\n" +
								"\t\t\t\t<ValidContext>" + methodConfigurationId + "</ValidContext>" + "\n";
					
					texto += 
								"\t\t\t</Process>" + "\n" +
							"\t\t</MethodPackage>" + "\n";
				}
				
				texto +=
						"\t</MethodPlugin>" + "\n" +
						"\t<MethodConfiguration name=\"" + methodConfigurationName + "\" briefDescription=\"" + methodConfigurationBriefDescription + "\" id=\"" + methodConfigurationId + "\" orderingGuide=\"" + methodConfigurationOrderingGuide + "\" suppressed=\"" + methodConfigurationSuppressed + "\" authors=\"" + methodConfigurationAuthors + "\" changeDescription=\"" + methodConfigurationChangeDescription + "\" version=\"" + methodConfigurationVersion + "\">" + "\n" +
							"\t\t<MethodPluginSelection>" + methodPluginSelectionId + "</MethodPluginSelection>" + "\n" +
							"\t\t<MethodPackageSelection>" + contentCategoryId + "</MethodPackageSelection>" + "\n";
				
				for (String pId: processIds){
					texto +=
							"\t\t<MethodPackageSelection>" + pId + "</MethodPackageSelection>" + "\n";
				}
				
				texto +=			
							"\t\t<ProcessView>" + idProcessView + "</ProcessView>" + "\n" +
						"\t</MethodConfiguration>" + "\n" +
					"</uma:MethodLibrary>";
				
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
		String id = s.getElementID();
		if (!idsAgregados.contains(id)){
			idsAgregados.add(id);
			String nombre = s.getNombre();
			String nombrePresentacion = s.getPresentationName();
			TipoElemento tipo = s.getType();
			
			String briefDescription = "";
			String orderingGuide = "";
			String suppressed = "false";
			String hasMultipleOccurrences = "false";
			String isOptional = "false";
			String isPlanned = "true";
			String prefix = "";
			String isEventDriven = "false";
			String isOngoing = "false";
			String isRepeatable = "false";
			String isEnactable = "false";
			String variabilityType = "na";
			String isSynchronizedWithSource = "true";
			String activityEntryState = "";
			String activityExitState = "";
			
			if (tipo == TipoElemento.ACTIVITY){
				texto += "\t\t\t\t<BreakdownElement xsi:type=\"uma:Activity\" name=\"" + nombre + "\" briefDescription=\"" + briefDescription + "\" id=\"" + id + 
						"\" orderingGuide=\"" + orderingGuide + "\" " + "suppressed=\"" + suppressed + "\" presentationName=\"" + nombrePresentacion + 
						"\" hasMultipleOccurrences=\"" + hasMultipleOccurrences + "\" isOptional=\"" + isOptional + "\" " + "isPlanned=\"" + isPlanned + 
						"\" prefix=\"" + prefix + "\" isEventDriven=\"" + isEventDriven + "\" isOngoing=\"" + isOngoing + "\" isRepeatable=\"" + isRepeatable + 
						"\" IsEnactable=\"" + isEnactable + "\" variabilityType=\"" + variabilityType + "\">" + "\n";
			}
			else if (tipo == TipoElemento.ITERATION){
				texto += "\t\t\t\t<BreakdownElement xsi:type=\"uma:Iteration\" name=\"" + nombre + "\" briefDescription=\"" + briefDescription + "\" id=\"" + id + 
						"\" orderingGuide=\"" + orderingGuide + "\" " + "suppressed=\"" + suppressed + "\" presentationName=\"" + nombrePresentacion + 
						"\" hasMultipleOccurrences=\"" + hasMultipleOccurrences + "\" isOptional=\"" + isOptional + "\" " + "isPlanned=\"" + isPlanned + 
						"\" prefix=\"" + prefix + "\" isEventDriven=\"" + isEventDriven + "\" isOngoing=\"" + isOngoing + "\" isRepeatable=\"" + isRepeatable + 
						"\" IsEnactable=\"" + isEnactable + "\" variabilityType=\"" + variabilityType + "\">" + "\n";
			}
			else if (tipo == TipoElemento.PHASE){
				texto += "\t\t\t\t<BreakdownElement xsi:type=\"uma:Phase\" name=\"" + nombre + "\" briefDescription=\"" + briefDescription + "\" id=\"" + id + 
						"\" orderingGuide=\"" + orderingGuide + "\" " + "suppressed=\"" + suppressed + "\" presentationName=\"" + nombrePresentacion + 
						"\" hasMultipleOccurrences=\"" + hasMultipleOccurrences + "\" isOptional=\"" + isOptional + "\" " + "isPlanned=\"" + isPlanned + 
						"\" prefix=\"" + prefix + "\" isEventDriven=\"" + isEventDriven + "\" isOngoing=\"" + isOngoing + "\" isRepeatable=\"" + isRepeatable + 
						"\" IsEnactable=\"" + isEnactable + "\" variabilityType=\"" + variabilityType + "\">" + "\n";
			}
			else if (tipo == TipoElemento.TASK){
				texto += "\t\t\t\t<BreakdownElement xsi:type=\"uma:TaskDescriptor\" name=\"" + nombre + "\" briefDescription=\"" + briefDescription + "\" id=\"" + id + 
						"\" orderingGuide=\"" + orderingGuide + "\" " + "suppressed=\"" + suppressed + "\" presentationName=\"" + nombrePresentacion + 
						"\" hasMultipleOccurrences=\"" + hasMultipleOccurrences + "\" isOptional=\"" + isOptional + "\" " + "isPlanned=\"" + isPlanned + 
						"\" prefix=\"" + prefix + "\" isEventDriven=\"" + isEventDriven + "\" isOngoing=\"" + isOngoing + "\" isRepeatable=\"" + isRepeatable +
						"\" isSynchronizedWithSource=\"" + isSynchronizedWithSource + "\">" + "\n";
			}
			else if (tipo == TipoElemento.ROLE){
				texto += "\t\t\t\t<BreakdownElement xsi:type=\"uma:RoleDescriptor\" name=\"" + nombre + "\" briefDescription=\"" + briefDescription + "\" id=\"" + id + 
						"\" orderingGuide=\"" + orderingGuide + "\" " + "suppressed=\"" + suppressed + "\" presentationName=\"" + nombrePresentacion + 
						"\" hasMultipleOccurrences=\"" + hasMultipleOccurrences + "\" isOptional=\"" + isOptional + "\" " + "isPlanned=\"" + isPlanned + 
						"\" prefix=\"" + prefix + "\" isSynchronizedWithSource=\"" + isSynchronizedWithSource + "\">" + "\n";
			}
			else if (tipo == TipoElemento.WORK_PRODUCT){
				texto += "\t\t\t\t<BreakdownElement xsi:type=\"uma:WorkProductDescriptor\" name=\"" + nombre + "\" briefDescription=\"" + briefDescription + "\" id=\"" + id + 
						"\" orderingGuide=\"" + orderingGuide + "\" " + "suppressed=\"" + suppressed + "\" presentationName=\"" + nombrePresentacion + 
						"\" hasMultipleOccurrences=\"" + hasMultipleOccurrences + "\" isOptional=\"" + isOptional + "\" " + "isPlanned=\"" + isPlanned + 
						"\" prefix=\"" + prefix + "\" isSynchronizedWithSource=\"" + isSynchronizedWithSource + "\" activityEntryState=\"" + activityEntryState + 
						"\" activityExitState=\"" + activityExitState + "\">" + "\n";
			}
			else if (tipo == TipoElemento.MILESTONE){
				texto += "\t\t\t\t<BreakdownElement xsi:type=\"uma:Milestone\" name=\"" + nombre + "\" briefDescription=\"" + briefDescription + "\" id=\"" + id + 
						"\" orderingGuide=\"" + orderingGuide + "\" " + "suppressed=\"" + suppressed + "\" presentationName=\"" + nombrePresentacion + 
						"\" hasMultipleOccurrences=\"" + hasMultipleOccurrences + "\" isOptional=\"" + isOptional + "\" " + "isPlanned=\"" + isPlanned + 
						"\" prefix=\"" + prefix + "\" isEventDriven=\"" + isEventDriven + "\" isOngoing=\"" + isOngoing + "\" isRepeatable=\"" + isRepeatable + "\">\n";
			}
			else if (tipo == TipoElemento.CAPABILITY_PATTERN){
				String processId = s.getProcessComponentId();
				processIds.add(processId);
				String categorizedElement = s.getElementID();
				String idExtends = s.getElementIDExtends();
				variabilityType = "extends";
				
				texto += "\t\t\t\t<BreakdownElement xsi:type=\"uma:CapabilityPattern\" name=\"" + nombre + "\" briefDescription=\"" + briefDescription + "\" id=\"" + idExtends + 
						"\" orderingGuide=\"" + orderingGuide + "\" " + "suppressed=\"" + suppressed + "\" presentationName=\"" + nombrePresentacion + 
						"\" hasMultipleOccurrences=\"" + hasMultipleOccurrences + "\" isOptional=\"" + isOptional + "\" " + "isPlanned=\"" + isPlanned + 
						"\" prefix=\"" + prefix + "\" isEventDriven=\"" + isEventDriven + "\" isOngoing=\"" + isOngoing + "\" isRepeatable=\"" + isRepeatable +
						"\" IsEnactable=\"" + isEnactable + "\" variabilityBasedOnElement=\"" + categorizedElement + "\" variabilityType=\"" + variabilityType + "\">" + "\n" +
							"\t\t\t\t\t<SuperActivity>" + superactivity + "</SuperActivity>" + "\n" +
						"\t\t\t\t</BreakdownElement>" + "\n";
			}
			
			if ((!texto.equals("")) && (tipo != TipoElemento.CAPABILITY_PATTERN)){
				texto += "\t\t\t\t\t<SuperActivity>" + superactivity + "</SuperActivity>" + "\n";
				
				//Agrego sucesores
				List<String> sucesores = s.getSucesores();
				if ((sucesores != null) && (sucesores.size() > 0)){
					Iterator<String> it = sucesores.iterator();
					while (it.hasNext()){
						texto += "\t\t\t\t\t<Predecessor id=\"_xaj2g0u2Ed-vjd_3xeIJwQ\" linkType=\"finishToStart\" properties=\"name=successor&#xA;value=" + it.next() + "#xA;scope=_ab5yQUesEd-iTvGQFwAspw>" + id + "</Predecessor>" + "\n";
						//<Predecessor id="_xaj2g0u2Ed-vjd_3xeIJwQ" linkType="finishToStart" properties="name=successor&#xA;value=_oMNV4EerEd-iTvGQFwAspw&#xA;scope=_ab5yQUesEd-iTvGQFwAspw">_3iR0EEfDEd-iNY7TQq4TSw</Predecessor>
						
					}
				}
				
				// Si tiene asignado un rol principal, se lo agrego
				String performedPrimaryBy = s.getPerformedPrimaryBy();
				if ((performedPrimaryBy != null) && (!performedPrimaryBy.equals(""))){
					texto += "\t\t\t\t\t<PerformedPrimarilyBy>" + performedPrimaryBy + "</PerformedPrimarilyBy>" + "\n";
				}
				
				// Si tiene asignado un rol adicional, se lo agrego
				List<String> performedAditionallyBy = s.getPerformedAditionallyBy();
				if ((performedAditionallyBy != null) && (performedAditionallyBy.size() > 0)){
					Iterator<String> it = performedAditionallyBy.iterator();
					while (it.hasNext()){
						texto += "\t\t\t\t\t<AdditionallyPerformedBy>" + it.next() + "</AdditionallyPerformedBy>" + "\n";
					}
				}
				
				// Si tiene workProduct asignados, los agrego
				List<String> mandatoryInputs = s.getMandatoryInputs();
				if ((mandatoryInputs != null) && (mandatoryInputs.size() > 0)){
					Iterator<String> it = mandatoryInputs.iterator();
					while (it.hasNext()){
						texto += "\t\t\t\t\t<MandatoryInput>" + it.next() + "</MandatoryInput>" + "\n";
					}
				}
				
				List<String> optionalInputs = s.getOptionalInputs();
				if ((optionalInputs != null) && (optionalInputs.size() > 0)){
					Iterator<String> it = optionalInputs.iterator();
					while (it.hasNext()){
						texto += "\t\t\t\t\t<OptionalInput>" + it.next() + "</OptionalInput>" + "\n";
					}
				}
				
				List<String> externalInputs = s.getExternalInputs();
				if ((externalInputs != null) && (externalInputs.size() > 0)){
					Iterator<String> it = externalInputs.iterator();
					while (it.hasNext()){
						texto += "\t\t\t\t\t<ExternalInput>" + it.next() + "</ExternalInput>" + "\n";
					}
				}
				
				List<String> outputs = s.getOutputs();
				if ((outputs != null) && (outputs.size() > 0)){
					Iterator<String> it = outputs.iterator();
					while (it.hasNext()){
						texto += "\t\t\t\t\t<Output>" + it.next() + "</Output>" + "\n";
					}
				}
				
				// Agrego los hijos
				List<Struct> roles = new ArrayList<Struct>();
				List<Struct> workProduct = new ArrayList<Struct>();
				Iterator<Struct> it = s.getHijos().iterator();
				while (it.hasNext()){
					Struct hijo = it.next();
					if ((hijo.getType() != TipoElemento.ROLE) && (hijo.getType() != TipoElemento.WORK_PRODUCT)){
						texto += agregarElementoAxml(hijo, id);
					}
					else if (hijo.getType() == TipoElemento.ROLE){
						roles.add(hijo);
					}
					else{
						workProduct.add(hijo);
					}
				}
				
				texto += "\t\t\t\t</BreakdownElement>" + "\n";
				
				// Agrego los roles
				if (roles.size() > 0){
					it = roles.iterator();
					while (it.hasNext()){
						texto += agregarElementoAxml(it.next(), superactivity);
					}
				}
				
				// Agrego los workProduct
				if (workProduct.size() > 0){
					it = workProduct.iterator();
					while (it.hasNext()){
						texto += agregarElementoAxml(it.next(), superactivity);
					}
				}
			}
		}
		return texto;
	}
	
}
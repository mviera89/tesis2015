package managedBeans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.mail.FolderClosedException;
import javax.servlet.http.HttpSession;

import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;

import config.Constantes;
import dataTypes.TipoContentCategory;
import dataTypes.TipoContentDescription;
import dataTypes.TipoContentPackage;
import dataTypes.TipoElemento;
import dataTypes.TipoLibrary;
import dataTypes.TipoMethodConfiguration;
import dataTypes.TipoMethodPackage;
import dataTypes.TipoPlugin;
import dataTypes.TipoSection;
import dataTypes.TipoTask;
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
				TipoContentDescription contentDescription = (contentCategory != null) ? contentCategory.getContentDescription() : null;
				TipoMethodConfiguration methodConfiguration = vb.getMethodConfiguration();
				Map<String, TipoContentCategory> categorizedElementsContent = vb.getCategorizedElements();
				List<TipoContentPackage> contentPackages = vb.getContentPackages();
				
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
				String contentCategoryName =  (contentCategory != null) ? contentCategory.getName() : null;
				String contentCategoryBriefDescription = (contentCategory != null) ? contentCategory.getBriefDescription() : null;
				String idProcessView = (contentCategory != null) ? contentCategory.getId() : null;
				String contentCategoryPresentationName = (contentCategory != null) ? contentCategory.getPresentationName() : null;
				String categorizedElements = (contentCategory != null) ? contentCategory.getCategorizedElements() : null;
				String contentCategoryNodeicon = (contentCategory != null) ? contentCategory.getNodeicon() : null;
				String contentCategoryShapeicon = (contentCategory != null) ? contentCategory.getShapeicon() : null;
				String contentDescriptionName = (contentDescription != null) ? contentDescription.getName() : null;
				String contentDescriptionId = (contentDescription != null) ? contentDescription.getId() : null;
				String contentDescriptionAutors = (contentDescription != null) ? contentDescription.getAuthors() : null;
				String contentDescriptionChangeDate = (contentDescription != null) ? contentDescription.getChangeDate() : null;
				String contentDescriptionChangeDescription = (contentDescription != null) ? contentDescription.getChangeDescription() : null;
				String contentDescriptionVersion = (contentDescription != null) ? contentDescription.getVersion() : null;
				String contentDescriptionMainDescription = (contentDescription != null) ? contentDescription.getMainDescription() : null;
				String contentDescriptionKeyConsiderations = (contentDescription != null) ? contentDescription.getKeyConsiderations() : null;
				String contentDescriptionOrderingGuide = "";
				String contentDescriptionBriefDescription = "";
				String contentDescriptionSuppressed = "false";
				String contentDescriptionExternalId = "";
				String contentCategoryOrderingGuide = "";
				String contentCategorySuppressed = "false";
				String contentCategoryVariabilityType = "na";
				String methodPackageOrderingGuide = "";
				String methodPackageSuppressed = "false";
				String methodPackageGlobal = "false";
				String contentElementOrderingGuide = "";
				String contentElementSuppressed = "false";
				String contentElementVariabilityType = "na";
				
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
				String idCapPattern = "";
				String texto =
					"<?xml version=\"" + versionXML + "\" encoding=\"" + encodingXML + "\"?>" + "\n" +
					"<uma:MethodLibrary xmlns:xsi=\"" + xmlns_xsi + "\" xmlns:uma=\"" + xmlns_uma + "\" name=\"" + methodLibraryName + "\" briefDescription=\"" + methodLibraryBriefDescription + "\" id=\"" + methodLibraryId + "\" orderingGuide=\"" + methodLibraryOrderingGuide + "\" suppressed=\"" + methodLibrarySuppressed + "\" authors=\"" + methodLibraryAuthors + "\" changeDescription=\"" + methodLibraryChangeDescription + "\" version=\"" + methodLibraryVersion + "\" tool=\"" + methodLibraryTool + "\">" + "\n" +
						//"\t<MethodElementProperty value=\"0\"/>" + "\n" +
						//"\t<MethodElementProperty value=\"" + methodElementPropertyId + "\"/>" + "\n" +
						//"\t<MethodElementProperty value=\"" + methodElementPropertyName + "\"/>" + "\n" +
						"\t<MethodPlugin name=\"" + methodPluginSelectionName + "\" briefDescription=\"" + methodPluginSelectionBriefDescription + "\" id=\"" + methodPluginSelectionId + "\" orderingGuide=\"" + methodPluginSelectionOrderingGuide + "\" suppressed=\"" + methodPluginSelectionSuppressed + "\" authors=\"" + methodPluginSelectionAuthors + (!methodPluginSelectionChangeDate.equals("") ? "\" changeDate=\"" + methodPluginSelectionChangeDate : "" ) + "\" changeDescription=\"" + methodPluginSelectionChangeDescription + "\" version=\"" + methodPluginSelectionVersion + "\" userChangeable=\"" + methodPluginSelectionUserChangeable + "\">" + "\n";
				
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
							if ((contentDescriptionKeyConsiderations != null) && (!contentDescriptionKeyConsiderations.equals(""))) {
								contentDescriptionKeyConsiderations = "<![CDATA[" + contentDescriptionKeyConsiderations + "]]>";
							}
							
							textoContentCategory +=
				    		"\t\t<MethodPackage xsi:type=\"uma:ContentCategoryPackage\" name=\"" + contentCategoryPackageName + "\" id=\"" + contentCategoryId + "\">" + "\n";
							if (contentCategory != null){
								textoContentCategory +=
					  			"\t\t\t<ContentCategory xsi:type=\"uma:CustomCategory\" name=\"" + contentCategoryName + "\" briefDescription=\"" + contentCategoryBriefDescription + "\" id=\"" + idProcessView + "\" orderingGuide=\"" + contentCategoryOrderingGuide + "\" suppressed=\"" + contentCategorySuppressed + "\" presentationName=\"" + contentCategoryPresentationName + "\" nodeicon=\"" + contentCategoryNodeicon + "\" shapeicon=\"" + contentCategoryShapeicon + "\" variabilityType=\"" + contentCategoryVariabilityType + "\">" + "\n";
								// "\t\t\t\t<MethodElementProperty name=\"PUBLISH_CATEGORY\" value=\"true\"/>" + "\n" +
							}
					  		if (contentDescription != null){
					  			textoContentCategory +=
					  				"\t\t\t\t<Presentation name=\"" + contentDescriptionName + "\" briefDescription=\"" + contentDescriptionBriefDescription + "\" id=\"" + contentDescriptionId + "\" orderingGuide=\"" + contentDescriptionOrderingGuide + "\" suppressed=\"" + contentDescriptionSuppressed + "\" authors=\"" + contentDescriptionAutors + "\" changeDate=\"" + contentDescriptionChangeDate + "\" changeDescription=\"" + contentDescriptionChangeDescription + "\" version=\"" + contentDescriptionVersion + "\" externalId=\"" + contentDescriptionExternalId + "\">" + "\n" +
					  					"\t\t\t\t\t<MainDescription><![CDATA[" + contentDescriptionMainDescription + "]]></MainDescription>" + "\n" +
					  					"\t\t\t\t\t<KeyConsiderations>" + contentDescriptionKeyConsiderations + "</KeyConsiderations>" + "\n" +
					  				"\t\t\t\t</Presentation>" + "\n";
					  		}
							
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
							idCapPattern = (idCapPattern.equals("")) ? processComponentId : idCapPattern;
							idCapabilityPatterns.add(processId);
							textoCapabilityPattern +=
		    				"\t\t<MethodPackage xsi:type=\"uma:ProcessComponent\" name=\"" + processName + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + processComponentId + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" global=\"" + processGlobal + "\" authors=\"" + processAuthors + "\" changeDescription=\"" + processChangeDescription + "\" version=\"" + processVersion + "\">" + "\n" +
								"\t\t\t\t<Process xsi:type=\"uma:" + tipo.toString() + "\" name=\"" + processName + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + processId + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" presentationName=\"" + processPresentationName + "\" hasMultipleOccurrences=\"" + processHasMultipleOccurrences + "\" isOptional=\"" + processIsOptional + "\" isPlanned=\"" + processIsPlanned + "\" prefix=\"" + processPrefix + "\" isEventDriven=\"" + processIsEventDriven + "\" isOngoing=\"" + processIsOngoing + "\" isRepeatable=\"" + processIsRepeatable + "\" IsEnactable=\"" + processIsEnactable + "\" variabilityType=\"" + processVariabilityType + "\">" + "\n" +
									
									"\t\t\t\t\t<Presentation xsi:type=\"uma:ProcessDescription\" name=\"" + processName + "," + processId + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + processDescriptionId + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" authors=\"" + processAuthors + "\" changeDescription=\"" + processChangeDescription + "\" version=\"" + processVersion + "\" externalId=\"" + processExternalId + "\" usageGuidance=\"" + processUsageGuidance + "\">" + "\n" +
				    					"\t\t\t\t\t\t<MainDescription></MainDescription>" + "\n" +
				    					"\t\t\t\t\t\t<KeyConsiderations></KeyConsiderations>" + "\n" +
				    					"\t\t\t\t\t\t<Alternatives></Alternatives>" + "\n" +
				    					"\t\t\t\t\t\t<HowToStaff></HowToStaff>" + "\n" +
				    					"\t\t\t\t\t\t<Purpose></Purpose>" + "\n" +
				    					"\t\t\t\t\t\t<Scope></Scope>" + "\n" +
				    					"\t\t\t\t\t\t<UsageNotes></UsageNotes>" + "\n" +
			    					"\t\t\t\t\t</Presentation>" + "\n";
							
							List<Struct> hijos = s.getHijos();
							Iterator<Struct> itHijos = hijos.iterator();
							while (itHijos.hasNext()){
								Struct hijo = itHijos.next(); 
								textoCapabilityPattern += agregarElementoAxml(hijo, processId);
							}
							
							textoCapabilityPattern +=
									"\t\t\t\t\t<DefaultContext>" + methodConfigurationId + "</DefaultContext>" + "\n" +
			        				"\t\t\t\t\t<ValidContext>" + methodConfigurationId + "</ValidContext>" + "\n" +
		        				"\t\t\t\t</Process>" + "\n" +
		    				"\t\t\t</MethodPackage>" + "\n";
						}
					}
				}
				
				texto += textoContentCategory;
				if (contentCategory != null){
					int n = 0;
					String[] categorizedElementsArray = null;
					if (categorizedElements.equals("")){
						texto += 		"\t\t\t\t<CategorizedElement>" + idDeliveryProcess + "</CategorizedElement>" + "\n";
					}
					else{
						categorizedElementsArray =	categorizedElements.split(" ");
						n = categorizedElementsArray.length;
						for (int i = 0; i < n; i++){
							texto += 		"\t\t\t\t<CategorizedElement>" + categorizedElementsArray[i] + "</CategorizedElement>" + "\n";
						}
					}
					texto +=	"\t\t\t</ContentCategory>" + "\n";
					
					for (int i = 0; i < n; i++){
						String key = categorizedElementsArray[i];
						TipoContentCategory element = categorizedElementsContent.get(key);
						if (element != null){
							texto +=	"\t\t\t<ContentCategory xsi:type=\"uma:CustomCategory\" name=\"" + element.getName() + "\" briefDescription=\"" + element.getBriefDescription() + "\" id=\"" + element.getId() + "\" orderingGuide=\"" + contentCategoryOrderingGuide + "\" suppressed=\"" + contentCategorySuppressed + "\" presentationName=\"" + element.getPresentationName() + "\" nodeicon=\"" + element.getNodeicon() + "\" shapeicon=\"" + element.getShapeicon() + "\" variabilityType=\"" + contentCategoryVariabilityType + "\">" + "\n";
							String categorizedElementsE = element.getCategorizedElements();
							if (categorizedElementsE.equals("")){
								texto += 		"\t\t\t\t<CategorizedElement>" + idDeliveryProcess + "</CategorizedElement>" + "\n";
							}
							else{
								String[] categorizedElementsEA = categorizedElementsE.split(" ");
								int nEA = categorizedElementsEA.length;
								for (int j = 0; j < nEA; j++){
									texto += 	"\t\t\t\t<CategorizedElement>" + categorizedElementsEA[j] + "</CategorizedElement>" + "\n";
								}
							}
							texto +=	"\t\t\t</ContentCategory>" + "\n";
						}
					}
				}
				
				texto += 	"\t\t</MethodPackage>" + "\n";
			
				Iterator<TipoContentPackage> itCP = contentPackages.iterator();
				while (itCP.hasNext()){
					TipoContentPackage tcp = itCP.next();
					TipoContentCategory cp = tcp.getContentPackages();
					texto += "\t\t<MethodPackage xsi:type=\"uma:ContentPackage\" name=\"" + cp.getName() + "\" briefDescription=\"" + cp.getBriefDescription() + "\" id=\"" + cp.getId() + "\" orderingGuide=\"" + methodPackageOrderingGuide + "\" presentationName=\"" + cp.getPresentationName() + "\" suppressed=\"" + methodPackageSuppressed + "\" global=\"" + methodPackageGlobal + "\">" + "\n";
					Iterator<TipoTask> itCE = tcp.getTasksCP().iterator();
					while (itCE.hasNext()){
						TipoTask tt = itCE.next();
						String name = tt.getName();
						String[] resSplit = name.split(",");
						String id = resSplit.length > 1 ? resSplit[1] : "";
						Struct elem = buscarElementoEnModelo(id, modeloAdaptado);
						String ttBriefDescription = ((elem != null) && (elem.getBriefDescription() != null)) ? elem.getBriefDescription() : "";
						String ttPresentationName = ((elem != null) && (elem.getPresentationName() != null)) ? elem.getPresentationName() : "";
						String ttBriefDescriptionPresentation = "";
						String ttOrderingGuide = "";
						String ttPresentationNamePresentation = "";
						String ttSuppressed = "false";
						String ttChangeDescription = "";
						String ttExternalId = "";
						texto += "\t\t\t<ContentElement xsi:type=\"uma:Task\" name=\"" + elem.getNombre() + "\" briefDescription=\"" + ttBriefDescription + "\" id=\"" + elem.getIdTask() + "\" orderingGuide=\"" + contentElementOrderingGuide + "\" presentationName=\"" + ttPresentationName + "\" suppressed=\"" + contentElementSuppressed + "\" variabilityType=\"" + contentElementVariabilityType + "\">" + "\n" +
						
									"\t\t\t\t<Presentation xsi:type=\"uma:TaskDescription\" name=\"" + tt.getName() + "\" briefDescription=\"" + ttBriefDescriptionPresentation + "\" id=\"" + tt.getId() + "\" orderingGuide=\"" + ttOrderingGuide + "\" presentationName=\"" + ttPresentationNamePresentation + "\" suppressed=\"" + ttSuppressed + "\" authors=\"" + tt.getAuthors() + "\" changeDate=\"" + tt.getChangeDate() + "\" changeDescription=\"" + ttChangeDescription + "\" version=\"" + tt.getVersion() + "\" externalId=\"" + ttExternalId + "\">" + "\n" +
				    					"\t\t\t\t\t<MainDescription>" + ((tt.getMainDescription() != null) ? "<![CDATA[" + tt.getMainDescription() + "]]>" : "") + "</MainDescription>" + "\n" +
				    					"\t\t\t\t\t<KeyConsiderations></KeyConsiderations>" + "\n";
						
						if (tt.getSections() != null){
							Iterator<TipoSection> itSections = tt.getSections().iterator();
							String sectionBriefDescription = "";
							String sectionOrderingGuide = "";
							String sectionPresentationName = "";
							String sectionSuppressed = "false";
							String sectionSectionName = "";
							String sectionVariabilityType = "na";
							while (itSections.hasNext()){
								TipoSection section = itSections.next();
								texto +=
										"\t\t\t\t\t<Section name=\"" + section.getName() + "\" briefDescription=\"" + sectionBriefDescription + "\" id=\"" + section.getXmiId() + "\" orderingGuide=\"" + sectionOrderingGuide + "\" presentationName=\"" + sectionPresentationName + "\" suppressed=\"" + sectionSuppressed +"\" sectionName=\"" + sectionSectionName + "\" variabilityType=\"" + sectionVariabilityType + "\">" + "\n" +
											"\t\t\t\t\t\t<Description></Description>" + "\n" +
										"\t\t\t\t\t</Section>" + "\n";
							}
						}
						
						texto +=
				    					"\t\t\t\t\t<Alternatives></Alternatives>" + "\n" +
				    					//"\t\t\t\t\t<HowToStaff></HowToStaff>" + "\n" +
				    					"\t\t\t\t\t<Purpose>" + ((tt.getPurpose() != null) ? "<![CDATA[" + tt.getPurpose() + "]]>" : "") + "</Purpose>" + "\n" +
				    					//"\t\t\t\t\t<Scope></Scope>" + "\n" +
				    					//"\t\t\t\t\t<UsageNotes></UsageNotes>" + "\n" +
									"\t\t\t\t</Presentation>" + "\n";
						
						String performedPrimaryBy = elem.getPerformedPrimaryBy();
				    	if (performedPrimaryBy != null){
				    		texto +=
				    				"\t\t\t\t<PerformedBy>" + performedPrimaryBy + "</PerformedBy>" + "\n";
				    	}
				    	if (elem.getPerformedAditionallyBy() != null){
					    	Iterator<String> itAdditionallyPerformedBy = elem.getPerformedAditionallyBy().iterator();
					    	while (itAdditionallyPerformedBy.hasNext()){
					    		texto +=
					    				"\t\t\t\t<AdditionallyPerformedBy>" + itAdditionallyPerformedBy.next() + "</AdditionallyPerformedBy>" + "\n";
					    	}
				    	}
				    	
				    	if (elem.getMandatoryInputs() != null){
					    	Iterator<String> itMandatoryInputs = elem.getMandatoryInputs().iterator();
					    	while (itMandatoryInputs.hasNext()){
					    		texto +=
					    				"\t\t\t\t<MandatoryInput>" + itMandatoryInputs.next() + "</MandatoryInput>" + "\n";
					    	}
				    	}
				    	if (elem.getOptionalInputs() != null){
					    	Iterator<String> itOptionalInputs = elem.getOptionalInputs().iterator();
					    	while (itOptionalInputs.hasNext()){
					    		texto +=
					    				"\t\t\t\t<OptionalInput>" + itOptionalInputs.next() + "</OptionalInput>" + "\n";
					    	}
				    	}
				    	if (elem.getOutputs() != null){
					    	Iterator<String> itOutput = elem.getOutputs().iterator();
					    	while (itOutput.hasNext()){
					    		texto +=
					    				"\t\t\t\t<Output>" + itOutput.next() + "</Output>" + "\n";
					    	}
				    	}
						
						texto += "\t\t\t</ContentElement>" + "\n";
					}
					texto += "\t\t</MethodPackage>" + "\n";
					/*
					 <MethodPackage xsi:type="uma:ContentPackage" name="implementation_package" briefDescription="" id="_QK85wOM5Ed6OoK0l17K4LA" orderingGuide="" presentationName="Services Implementation" suppressed="false" global="false">
      <ContentElement xsi:type="uma:Role" name="developer" briefDescription="" id="_x4Y9YOM8Ed6OoK0l17K4LA" orderingGuide="" presentationName="Developer" suppressed="false" isAbstract="false" variabilityType="na">
        <ResponsibleFor>_SDch4OM4Ed6OoK0l17K4LA</ResponsibleFor>
      </ContentElement>
      <ContentElement xsi:type="uma:Artifact" name="service_implemented" briefDescription="" id="_o6dUQEexEd-iTvGQFwAspw" orderingGuide="" presentationName="Service Implemented" suppressed="false" isAbstract="false" variabilityType="na"/>
      <ContentElement xsi:type="uma:Task" name="implement_services" briefDescription="" id="_hdEQcEfDEd-iNY7TQq4TSw" orderingGuide="" presentationName="I1 - Implement services" suppressed="false" isAbstract="false" variabilityType="na">
        <Presentation xsi:type="uma:TaskDescription" name="implement_services,_hdEQcEfDEd-iNY7TQq4TSw" briefDescription="" id="-0k89H-5aWSSJjGjhjQJctQ" orderingGuide="" presentationName="" suppressed="false" authors="Andrea Delgado " changeDate="2010-04-15T10:13:33" changeDescription="" version="1.0" externalId="">
          <MainDescription><![CDATA[To implement the described services&nbsp;taking into account the type of service, the designed interfaces, the interaction
with other services (with or without a repository of services, binding in development or execution time).]]></MainDescription>
          <KeyConsiderations></KeyConsiderations>
          <Section name="For each service recall the defined interfaces with its operations, parameters, pre and post conditions from the services document " briefDescription="" id="_2JK88EiQEd-6ten92sIKgg" orderingGuide="" presentationName="" suppressed="false" sectionName="" variabilityType="na">
            <Description></Description>
          </Section>
          <Section name="Implement the service defining if invocations to other services will be through a services repository or not, and the time of binding for the invocation (development, execution)" briefDescription="" id="_99qM8EiQEd-6ten92sIKgg" orderingGuide="" presentationName="" suppressed="false" sectionName="" variabilityType="na">
            <Description></Description>
          </Section>
          <Section name="Implement the service defining how invocations to other software pieces will be handled, and the time of binding for the invocation (development, execution)" briefDescription="" id="_XqA2YEiREd-6ten92sIKgg" orderingGuide="" presentationName="" suppressed="false" sectionName="" variabilityType="na">
            <Description></Description>
          </Section>
          <Alternatives></Alternatives>
          <Purpose><![CDATA[To implement the described services.]]></Purpose>
        </Presentation>
        <PerformedBy>_x4Y9YOM8Ed6OoK0l17K4LA</PerformedBy>
        <MandatoryInput>_N4bXIOMxEd6OoK0l17K4LA</MandatoryInput>
        <MandatoryInput>_P1dnYOM4Ed6OoK0l17K4LA</MandatoryInput>
        <MandatoryInput>_SDch4OM4Ed6OoK0l17K4LA</MandatoryInput>
        <Output>_o6dUQEexEd-iTvGQFwAspw</Output>
      </ContentElement>
    </MethodPackage>
					 */
				}
				
				if (textoCapabilityPattern != ""){
					List<TipoMethodPackage> processPackages = vb.getProcessPackages();
					// Busco el "padre" de los capabilityPatterns, si los hay
					Iterator<TipoMethodPackage> iter = processPackages.iterator();
					TipoMethodPackage pack = null;
					while (iter.hasNext() && (pack == null)){
						TipoMethodPackage processPackage = iter.next();
						if (processPackage.getProcessComponentChild().contains(idCapPattern)){
							pack = processPackage; 
						}
					}
					if (pack != null){
						texto += "\t\t<MethodPackage xsi:type=\"uma:ProcessPackage\" name=\"" + pack.getName() + "\" briefDescription=\"\" id=\"" + pack.getId() + "\" orderingGuide=\"\" suppressed=\"false\" global=\"false\">" + "\n";
						processIds.add(pack.getId());
					}
					texto += textoCapabilityPattern;
					if (pack != null){
						texto += "\t\t</MethodPackage>" + "\n";
					}
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
				
				if (contentCategory != null){
					texto +=			
							"\t\t<ProcessView>" + idProcessView + "</ProcessView>" + "\n";
				}
				texto +=
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
			String briefDescription = s.getBriefDescription() != null ? s.getBriefDescription() : "";
			
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
						String next = it.next();
						texto += "\t\t\t\t\t<Predecessor id=\"_xaj2g0u2Ed-vjd_3xeIJwQ\" linkType=\"finishToStart\">" + id + "</Predecessor>" + "\n";
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
	
	public Struct buscarElementoEnModelo(String id, DefaultDiagramModel modelo){
		System.out.println("###### id: " + id);
		Iterator<Element> it = modelo.getElements().iterator();
		while (it.hasNext()){
			Struct s = (Struct) it.next().getData();
			System.out.println("######### s: " + s.toString());
			if ((s.getIdTask() != null) && (s.getIdTask().equals(id))){
				return s;
			}
		}
		return null;
	}
	
}
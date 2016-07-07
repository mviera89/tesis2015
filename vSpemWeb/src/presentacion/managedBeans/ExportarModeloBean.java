package presentacion.managedBeans;

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
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.servlet.http.HttpSession;

import logica.dataTypes.TipoContentCategory;
import logica.dataTypes.TipoContentDescription;
import logica.dataTypes.TipoContentElement;
import logica.dataTypes.TipoContentPackage;
import logica.dataTypes.TipoLibrary;
import logica.dataTypes.TipoMethodConfiguration;
import logica.dataTypes.TipoMethodElementProperty;
import logica.dataTypes.TipoMethodPackage;
import logica.dataTypes.TipoPlugin;
import logica.dataTypes.TipoRolesWorkProducts;
import logica.dataTypes.TipoSection;
import logica.dataTypes.TipoView;
import logica.dominio.Struct;
import logica.dominio.Variant;
import logica.enumerados.TipoElemento;
import logica.negocio.IExportarManager;
import logica.utils.Utils;

import org.eclipse.jgit.api.errors.TransportException;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;

import config.Constantes;
import config.ReadProperties;
 
@ManagedBean
@SessionScoped
public class ExportarModeloBean {

	private IExportarManager iem;
	
	private boolean exportar;
	private String repositorioExport;
	private String comentarioRepositorioExport;
	private String userRepositorioExport;
	private String passRepositorioExport;
	private String mensajeAyudaRepositorioExport;
	
	private List<String> idCapabilityPatterns;
	private List<String> idsAgregados;
	private List<String> processIds;
	private String textoCapabilityPattern;
	
	public ExportarModeloBean(){
		try{
			this.iem = InitialContext.doLookup("java:module/ExportarManager");
		}
		catch (Exception e){
			e.printStackTrace();		
		}
		exportar = false;
		repositorioExport = ReadProperties.getProperty("URL_GITHUB_EXPORT_DEFAULT");
		comentarioRepositorioExport = "";
		userRepositorioExport = "";
		passRepositorioExport = "";
		mensajeAyudaRepositorioExport = Constantes.mensjaeAyudaRepositorioExport;
		
		idCapabilityPatterns = new ArrayList<String>();
		idsAgregados = new ArrayList<String>();
		processIds = new ArrayList<String>();
		textoCapabilityPattern = "";
	}

	public boolean isExportar() {
		return exportar;
	}

	public void setExportar(boolean exportar) {
		this.exportar = exportar;
	}

	public String getRepositorioExport() {
		return repositorioExport;
	}

	public void setRepositorioExport(String repositorioExport) {
		this.repositorioExport = repositorioExport;
	}

	public String getComentarioRepositorioExport() {
		return comentarioRepositorioExport;
	}


	public void setComentarioRepositorioExport(String comentarioRepositorioExport) {
		this.comentarioRepositorioExport = comentarioRepositorioExport;
	}

	public String getUserRepositorioExport() {
		return userRepositorioExport;
	}

	public void setUserRepositorioExport(String userRepositorioExport) {
		this.userRepositorioExport = userRepositorioExport;
	}

	public String getPassRepositorioExport() {
		return passRepositorioExport;
	}

	public void setPassRepositorioExport(String passRepositorioExport) {
		this.passRepositorioExport = passRepositorioExport;
	}

	public String getMensajeAyudaRepositorioExport() {
		return mensajeAyudaRepositorioExport;
	}

	public void setMensajeAyudaRepositorioExport(String mensajeAyudaRepositorioExport) {
		this.mensajeAyudaRepositorioExport = mensajeAyudaRepositorioExport;
	}

	public void exportarModelo(DefaultDiagramModel modeloAdaptado, List<TipoRolesWorkProducts> modeloRolesWP, DefaultDiagramModel modelo, List<Struct> nodos){
		try{
			if (modeloAdaptado != null){
				FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
				HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
				VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
				
		        String nomArchivo = vb.getNombreArchivo();
		        nomArchivo = nomArchivo.substring(0, nomArchivo.length() - 4); // Para quitar la extensión
				
				File archivo = new File(ReadProperties.getProperty("destinoExport") + nomArchivo + "_" + Constantes.nomArchivoExport);
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
				String processPrefix = "";
				String processIsEventDriven = "false";
				String processIsOngoing = "false";
				String processIsRepeatable = "false";
				String processExternalId = "";
				String processUsageGuidance = "";
				
				// <MethodConfiguration... />
				String methodConfigurationName = (methodConfiguration != null) ? methodConfiguration.getName() : "";
				String methodConfigurationId = (methodConfiguration != null) ? methodConfiguration.getId() : "";
				String methodConfigurationBriefDescription = (methodConfiguration != null) ? methodConfiguration.getBriefDescription() : "";
				String methodConfigurationOrderingGuide = "";
				String methodConfigurationSuppressed = "false";
				String methodConfigurationAuthors = "";
				String methodConfigurationChangeDescription = "";
				String methodConfigurationVersion = "";
				String idDefaultView = ((methodConfiguration != null) && (methodConfiguration.getDefaultView() != null)) ? methodConfiguration.getDefaultView().getHref() : "";
				if (!idDefaultView.equals("")){
					String[] res = idDefaultView.split("#");
					idDefaultView = (res.length > 0) ? res[1] : idDefaultView;
				}
				List<TipoView> addedCategories = (methodConfiguration != null) ? methodConfiguration.getAddedCategory() : new ArrayList<TipoView>();
				
				String textoContentCategory = "";
				String textoDeliveryProcess = "";
				String idDeliveryProcess = "";
				String idCapPattern = "";
				String texto =
					"<?xml version=\"" + versionXML + "\" encoding=\"" + encodingXML + "\"?>" + "\n" +
					"<uma:MethodLibrary xmlns:xsi=\"" + xmlns_xsi + "\" xmlns:uma=\"" + xmlns_uma + "\" name=\"" + methodLibraryName + "\" briefDescription=\"" + methodLibraryBriefDescription + "\" id=\"" + methodLibraryId + "\" orderingGuide=\"" + methodLibraryOrderingGuide + "\" suppressed=\"" + methodLibrarySuppressed + "\" authors=\"" + methodLibraryAuthors + "\" changeDescription=\"" + methodLibraryChangeDescription + "\" version=\"" + methodLibraryVersion + "\" tool=\"" + methodLibraryTool + "\">" + "\n" +
						"\t<MethodPlugin name=\"" + methodPluginSelectionName + "\" briefDescription=\"" + methodPluginSelectionBriefDescription + "\" id=\"" + methodPluginSelectionId + "\" orderingGuide=\"" + methodPluginSelectionOrderingGuide + "\" suppressed=\"" + methodPluginSelectionSuppressed + "\" authors=\"" + methodPluginSelectionAuthors + (!methodPluginSelectionChangeDate.equals("") ? "\" changeDate=\"" + methodPluginSelectionChangeDate : "" ) + "\" changeDescription=\"" + methodPluginSelectionChangeDescription + "\" version=\"" + methodPluginSelectionVersion + "\" userChangeable=\"" + methodPluginSelectionUserChangeable + "\">" + "\n";
				
				//actualizo predecesores
				iem.actualizarPredecesoresModelo(modeloAdaptado, modelo);
				
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
						String processIsPlanned = s.getIsPlanned();
						String processIsOptional = s.getIsOptional();
						String processVariabilityType = s.getVariabilityType();
						String diagramURI = s.getDiagramURI(); 
						
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
					  			"\t\t\t<ContentCategory xsi:type=\"uma:CustomCategory\" name=\"" + contentCategoryName + "\" briefDescription=\"" + contentCategoryBriefDescription + "\" id=\"" + idProcessView + "\" orderingGuide=\"" + contentCategoryOrderingGuide + "\" suppressed=\"" + contentCategorySuppressed + "\" presentationName=\"" + contentCategoryPresentationName + (((contentCategoryNodeicon != null) && (!contentCategoryNodeicon.equals(""))) ? ("\" nodeicon=\"" + contentCategoryNodeicon) : "") + (((contentCategoryShapeicon != null) && (!contentCategoryShapeicon.equals(""))) ? ("\" shapeicon=\"" + contentCategoryShapeicon) : "") + "\" variabilityType=\"" + contentCategoryVariabilityType + "\">" + "\n";
								TipoMethodElementProperty methodElementProperty = contentCategory.getMethodElementProperty();
								if (methodElementProperty != null){
									textoContentCategory += "\t\t\t\t<MethodElementProperty name=\"" + methodElementProperty.getName() + "\" value=\"" + methodElementProperty.getValue() + "\"/>" + "\n";
								}
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
	    						"\t\t\t<Process xsi:type=\"uma:" + tipo.toString() + "\" name=\"" + processName + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + processId + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" presentationName=\"" + processPresentationName + "\" hasMultipleOccurrences=\"" + processHasMultipleOccurrences + "\" isOptional=\"" + processIsOptional + "\" isPlanned=\"" + processIsPlanned + "\" prefix=\"" + processPrefix + "\" isEventDriven=\"" + processIsEventDriven + "\" isOngoing=\"" + processIsOngoing + "\" isRepeatable=\"" + processIsRepeatable + /*"\" IsEnactable=\"" + processIsEnactable +*/ "\" variabilityType=\"" + processVariabilityType + (diagramURI != null && !diagramURI.equals("") ? "\" diagramURI=\"" + diagramURI : "") + "\">" + "\n" +
	    							
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
								textoDeliveryProcess += agregarElementoAxml(hijo, modeloAdaptado);
							}
						}
						else if (tipo == TipoElemento.CAPABILITY_PATTERN){
							idCapPattern = (idCapPattern.equals("")) ? processComponentId : idCapPattern;
							idCapabilityPatterns.add(processId);
							textoCapabilityPattern +=
		    				"\t\t<MethodPackage xsi:type=\"uma:ProcessComponent\" name=\"" + processName + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + processComponentId + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" global=\"" + processGlobal + "\" authors=\"" + processAuthors + "\" changeDescription=\"" + processChangeDescription + "\" version=\"" + processVersion + "\">" + "\n" +
								"\t\t\t\t<Process xsi:type=\"uma:" + tipo.toString() + "\" name=\"" + processName + "\" briefDescription=\"" + processBriefDescription + "\" id=\"" + processId + "\" orderingGuide=\"" + processOrderingGuide + "\" suppressed=\"" + processSuppressed + "\" presentationName=\"" + processPresentationName + "\" hasMultipleOccurrences=\"" + processHasMultipleOccurrences + "\" isOptional=\"" + processIsOptional + "\" isPlanned=\"" + processIsPlanned + "\" prefix=\"" + processPrefix + "\" isEventDriven=\"" + processIsEventDriven + "\" isOngoing=\"" + processIsOngoing + "\" isRepeatable=\"" + processIsRepeatable + /*"\" IsEnactable=\"" + processIsEnactable +*/ "\" variabilityType=\"" + processVariabilityType + (diagramURI != null && !diagramURI.equals("") ? "\" diagramURI=\"" + diagramURI : "") + "\">" + "\n" +
									
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
								textoCapabilityPattern += agregarElementoAxml(hijo, modeloAdaptado);
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
					
					Iterator<Entry<String, TipoContentCategory>> iter = categorizedElementsContent.entrySet().iterator();
					while (iter.hasNext()){
						Entry<String, TipoContentCategory> entry = iter.next();
						TipoContentCategory element = entry.getValue();
						if ((element != null) && (!element.getId().equals(idProcessView)) && (!element.getCategorizedElements().contains(idProcessView))){
							String type = element.getType().equals("org.eclipse.epf.uma:CustomCategory") ? "uma:CustomCategory" :
										  element.getType().equals("org.eclipse.epf.uma:Discipline") ? "uma:Discipline" : 
										  element.getType().equals("org.eclipse.epf.uma:WorkProductType") ? "uma:WorkProductType" : "";
							if (!type.equals("")){
								String nodeIcon = element.getNodeicon();
								String shapeIcon = element.getShapeicon();
								texto +=	"\t\t\t<ContentCategory xsi:type=\"" + type + "\" name=\"" + element.getName() + "\" briefDescription=\"" + element.getBriefDescription() + "\" id=\"" + element.getId() + "\" orderingGuide=\"" + contentCategoryOrderingGuide + "\" presentationName=\"" + element.getPresentationName() + "\" suppressed=\"" + contentCategorySuppressed + (((nodeIcon != null) && (!nodeIcon.equals(""))) ? ("\" nodeicon=\"" + nodeIcon) : "") + (((shapeIcon != null) && (!shapeIcon.equals(""))) ? ("\" shapeicon=\"" + shapeIcon) : "") + "\" variabilityType=\"" + contentCategoryVariabilityType + "\">" + "\n";
								TipoMethodElementProperty methodElementProperty = element.getMethodElementProperty();
								if (methodElementProperty != null){
									texto += 		"\t\t\t\t<MethodElementProperty name=\"" + methodElementProperty.getName() + "\" value=\"" + methodElementProperty.getValue() + "\"/>" + "\n";
								}
								String categorizedElementsE = element.getCategorizedElements();
								if (type.equals("uma:Discipline")){
									String[] tasks = element.getTasks().split(" ");
									int nTasks = tasks.length;
									for (int j = 0; j < nTasks; j++){
										texto += "\t\t\t\t<Task>" + tasks[j] + "</Task>" + "\n";
									}
								}
								else if (type.equals("uma:WorkProductType")){
									String[] workProducts = element.getWorkProducts().split(" ");
									int nWorkProducts = workProducts.length;
									for (int j = 0; j < nWorkProducts; j++){
										texto += "\t\t\t\t<WorkProduct>" + workProducts[j] + "</WorkProduct>" + "\n";
									}
								}
								else{
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
								}
								texto +=	"\t\t\t</ContentCategory>" + "\n";
							}
						}
					}
				}
				
				texto += 	"\t\t</MethodPackage>" + "\n";
			
				Iterator<TipoContentPackage> itCP = contentPackages.iterator();
				while (itCP.hasNext()){
					TipoContentPackage tcp = itCP.next();
					TipoContentCategory cp = tcp.getContentPackages();
					texto += "\t\t<MethodPackage xsi:type=\"uma:ContentPackage\" name=\"" + cp.getName() + "\" briefDescription=\"" + cp.getBriefDescription() + "\" id=\"" + cp.getId() + "\" orderingGuide=\"" + methodPackageOrderingGuide + "\" suppressed=\"" + methodPackageSuppressed + "\" global=\"" + methodPackageGlobal + "\">" + "\n";
					List<TipoContentElement> lstCE = new ArrayList<TipoContentElement>(); 
					lstCE.addAll(tcp.getTasksCP());
					lstCE.addAll(tcp.getWorkproductsCP());
					lstCE.addAll(tcp.getGuidancesCP());
					
					Iterator<TipoContentElement> itCE = lstCE.iterator();
					while (itCE.hasNext()){
						TipoContentElement element = itCE.next();
						String name = element.getName();
						String[] resSplit = name.split(",");
						String id = resSplit.length > 1 ? resSplit[1] : "";
						
						if ((element.getTipoElemento() == TipoElemento.WORK_PRODUCT) || (element.getTipoElemento() == TipoElemento.TASK)){ 
							name = ((element.getContentDescription() != null) ? element.getContentDescription().getName() : "");
							if (name.equals("")){
								name = element.getName();
							}
							resSplit = name.split(",");
							id = resSplit.length > 1 ? resSplit[1] : "";
						}
						
						TipoElemento typeStruct = element.getTipoElemento();
						String briefDescriptionStruct = (element.getBriefDescription() != null) ? element.getBriefDescription() : "";
						String presentationNameStruct = (element.getPresentationName() != null) ? element.getPresentationName() : "";
						String orderingGuideStruct = "";
						String suppressedStruct = "false";
						String changeDescriptionStruct = "";
						String externalIdStruct = "";
						
						String type = (typeStruct == TipoElemento.TASK) ? "uma:Task" : 
									  (typeStruct == TipoElemento.WORK_PRODUCT) ? "uma:Artifact" : 
									  (typeStruct == TipoElemento.GUIDANCE) ? "uma:Template" : 
									  (typeStruct == TipoElemento.SUPPORTING_MATERIAL) ? "uma:SupportingMaterial" : "";
						
						String typeDescription = (typeStruct == TipoElemento.TASK) ? "uma:TaskDescription" : 
							  					 (typeStruct == TipoElemento.WORK_PRODUCT) ? "uma:ArtifactDescription" : 
							  					 (typeStruct == TipoElemento.GUIDANCE) ? "uma:GuidanceDescription" : "";
						
						String idStruct = element.getId();
						
						texto += "\t\t\t<ContentElement xsi:type=\"" + type + "\" name=\"" + element.getName() + "\" briefDescription=\"" + briefDescriptionStruct + "\" id=\"" + idStruct + "\" orderingGuide=\"" + contentElementOrderingGuide + "\" presentationName=\"" + presentationNameStruct + "\" suppressed=\"" + contentElementSuppressed + "\" variabilityType=\"" + contentElementVariabilityType + "\"";

						TipoContentElement elementDescription = ((typeStruct == TipoElemento.WORK_PRODUCT) || (typeStruct == TipoElemento.TASK)) ? element.getContentDescription() : element;
						TipoContentElement guidance = null;
						String idPresentation = "";
						if (elementDescription != null){
							guidance = elementDescription.getGuidance();
							idPresentation = ((typeStruct == TipoElemento.GUIDANCE) || (typeStruct == TipoElemento.SUPPORTING_MATERIAL)) ? 
												((guidance != null) ? guidance.getId() : "") :
												elementDescription.getId();
						}
						if ((idPresentation != null) && (!idPresentation.equals(""))){

							String namePresentation = ((typeStruct == TipoElemento.GUIDANCE) || (typeStruct == TipoElemento.SUPPORTING_MATERIAL)) ? 
														((guidance != null) ? guidance.getName() : "") :
														elementDescription.getName();
							String briefDescriptionPresentationStruct = ((typeStruct == TipoElemento.GUIDANCE) || (typeStruct == TipoElemento.SUPPORTING_MATERIAL)) ?
																			((guidance != null) ? guidance.getBriefDescription() : "") :
																			elementDescription.getBriefDescription();
							String mainDescription = ((typeStruct == TipoElemento.GUIDANCE) || (typeStruct == TipoElemento.SUPPORTING_MATERIAL)) ? 
														(((guidance != null) && (guidance.getMainDescription() != null) && (!guidance.getMainDescription().equals(""))) ? "<![CDATA[" + guidance.getMainDescription() + "]]>" : "") :
														(((elementDescription.getMainDescription() != null) && (!elementDescription.getMainDescription().equals(""))) ? "<![CDATA[" + elementDescription.getMainDescription() + "]]>" : "");
							
							String keyConsiderations = ((typeStruct == TipoElemento.GUIDANCE) || (typeStruct == TipoElemento.SUPPORTING_MATERIAL)) ?
														(((guidance != null) && (guidance.getKeyConsiderations() != null) && (!guidance.getKeyConsiderations().equals(""))) ? "<![CDATA[" + guidance.getKeyConsiderations() + "]]>" : "") :
														(((elementDescription.getKeyConsiderations() != null) && (!elementDescription.getKeyConsiderations().equals(""))) ? "<![CDATA[" + elementDescription.getKeyConsiderations() + "]]>" : "");
							
							String typeDescriptionStr = (!typeDescription.equals("")) ? ("xsi:type=\"" + typeDescription + "\" ") : "";
							String autors = ((typeStruct == TipoElemento.GUIDANCE) || (typeStruct == TipoElemento.SUPPORTING_MATERIAL)) ?
												((guidance != null) ? guidance.getAuthors() : "") :
												elementDescription.getAuthors();
							String date = ((typeStruct == TipoElemento.GUIDANCE) || (typeStruct == TipoElemento.SUPPORTING_MATERIAL)) ? 
												((guidance != null) ? guidance.getChangeDate() : "") :
												elementDescription.getChangeDate();
							String changeDate = ((date != null) && (!date.equals(""))) ? ("\" changeDate=\"" + date) : "";
							String version = ((typeStruct == TipoElemento.GUIDANCE) || (typeStruct == TipoElemento.SUPPORTING_MATERIAL)) ? 
												((guidance != null) ? guidance.getVersion() : "") :
												elementDescription.getVersion();
							List<TipoSection> sections = elementDescription.getSections();
							texto += ">" + "\n" +
									"\t\t\t\t<Presentation " + typeDescriptionStr + "name=\"" + namePresentation + "\" briefDescription=\"" + briefDescriptionPresentationStruct + "\" id=\"" + idPresentation + "\" orderingGuide=\"" + orderingGuideStruct + "\" suppressed=\"" + suppressedStruct + "\" authors=\"" + autors + changeDate + "\" changeDescription=\"" + changeDescriptionStruct + "\" version=\"" + version + "\" externalId=\"" + externalIdStruct + "\">" + "\n" +
				    					"\t\t\t\t\t<MainDescription>" + mainDescription + "</MainDescription>" + "\n" +
				    					"\t\t\t\t\t<KeyConsiderations>" + keyConsiderations + "</KeyConsiderations>" + "\n";
						
							boolean haySections = ((typeStruct == TipoElemento.GUIDANCE) || (typeStruct == TipoElemento.SUPPORTING_MATERIAL)) ? 
													((guidance != null) ? (guidance.getSections() != null) : false) : 
													(elementDescription.getSections() != null);
							if (haySections){
								Iterator<TipoSection> itSections = ((typeStruct == TipoElemento.GUIDANCE) || (typeStruct == TipoElemento.SUPPORTING_MATERIAL)) ? 
																		guidance.getSections().iterator() : 
																		sections.iterator();
								String sectionBriefDescription = "";
								String sectionOrderingGuide = "";
								String sectionSuppressed = "false";
								String sectionSectionName = "";
								String sectionVariabilityType = "na";
								while (itSections.hasNext()){
									TipoSection section = itSections.next();
									texto +=
											"\t\t\t\t\t<Section name=\"" + section.getName() + "\" briefDescription=\"" + sectionBriefDescription + "\" id=\"" + section.getXmiId() + "\" orderingGuide=\"" + sectionOrderingGuide + "\" suppressed=\"" + sectionSuppressed +"\" sectionName=\"" + sectionSectionName + "\" variabilityType=\"" + sectionVariabilityType + "\">" + "\n" +
												"\t\t\t\t\t\t<Description></Description>" + "\n" +
											"\t\t\t\t\t</Section>" + "\n";
								}
							}
							
							if ((typeStruct != TipoElemento.WORK_PRODUCT) && (typeStruct != TipoElemento.GUIDANCE) && (typeStruct != TipoElemento.SUPPORTING_MATERIAL)){
								String alt = elementDescription.getAlternatives();
								String alternatives = ((alt != null) && (!alt.equals(""))) ? "<![CDATA[" + alt + "]]>" : "";
								texto +=
											"\t\t\t\t\t<Alternatives>" + alternatives + "</Alternatives>" + "\n";
							}
							
							if ((typeStruct != TipoElemento.GUIDANCE) && (typeStruct != TipoElemento.SUPPORTING_MATERIAL)){
								String pur = elementDescription.getPurpose();
								String purpose = ((pur != null) && (!pur.equals(""))) ? "<![CDATA[" + pur + "]]>" : "";
								texto +=
					    					"\t\t\t\t\t<Purpose>" + purpose + "</Purpose>" + "\n";
							}
							
							if (typeStruct == TipoElemento.WORK_PRODUCT){
								texto +=
				    						"\t\t\t\t\t<ImpactOfNotHaving></ImpactOfNotHaving>" + "\n" +
					    					"\t\t\t\t\t<ReasonsForNotNeeding></ReasonsForNotNeeding>" + "\n" +
					    					"\t\t\t\t\t<BriefOutline></BriefOutline>" + "\n" +
					    					"\t\t\t\t\t<RepresentationOptions></RepresentationOptions>" + "\n" +
					    					"\t\t\t\t\t<Representation></Representation>" + "\n" +
					    					"\t\t\t\t\t<Notation></Notation>" + "\n";
							}
							else if (typeStruct == TipoElemento.GUIDANCE){
								String attachment = ((guidance != null) && (guidance.getAttachments() != null)) ? guidance.getAttachments() : "";
								texto +=
										"\t\t\t\t\t<Attachment>" + attachment + "</Attachment>" + "\n";
							}
						
							texto +=
		    							"\t\t\t\t</Presentation>" + "\n";
							
				    		String performedPrimaryBy = element.getPerformedBy();
				    		if ((performedPrimaryBy != null) && (!performedPrimaryBy.equals(""))){
				    			String[] performedBy = performedPrimaryBy.split(" ");
				    			int n = performedBy.length;
				    			for (int i = 0; i < n; i++){
				    				texto +=
					    					"\t\t\t\t<PerformedBy>" + performedBy[i] + "</PerformedBy>" + "\n";
				    			}
				    		}
				    		
				    		String additionallyPerformedBy = element.getAdditionallyPerformedBy();
				    		if ((additionallyPerformedBy != null) && (!additionallyPerformedBy.equals(""))){
				    			String[] performedBy =  additionallyPerformedBy.split(" ");
				    			int n = performedBy.length;
				    			for (int i = 0; i < n; i++){
				    				texto +=
					    					"\t\t\t\t<AdditionallyPerformedBy>" + performedBy[i] + "</AdditionallyPerformedBy>" + "\n";
				    			}
				    		}
					    	
				    		String mandatoryInput = element.getMandatoryInput();
				    		if ((mandatoryInput != null) && (!mandatoryInput.equals(""))){
				    			String[] input =  mandatoryInput.split(" ");
				    			int n = input.length;
				    			for (int i = 0; i < n; i++){
				    				texto +=
					    					"\t\t\t\t<MandatoryInput>" + input[i] + "</MandatoryInput>" + "\n";
				    			}
				    		}
				    		
				    		String optionalInput = element.getOptionalInput();
				    		if ((optionalInput != null) && (!optionalInput.equals(""))){
				    			String[] input =  optionalInput.split(" ");
				    			int n = input.length;
				    			for (int i = 0; i < n; i++){
				    				texto +=
					    					"\t\t\t\t<OptionalInput>" + input[i] + "</OptionalInput>" + "\n";
				    			}
				    		}
				    		
				    		String output = element.getOutput();
				    		if ((output != null) && (!output.equals(""))){
				    			String[] outStr =  output.split(" ");
				    			int n = outStr.length;
				    			for (int i = 0; i < n; i++){
				    				texto +=
					    					"\t\t\t\t<Output>" + outStr[i] + "</Output>" + "\n";
				    			}
				    		}
							texto += "\t\t\t</ContentElement>" + "\n";
						}
						else{
							texto += "/>" + "\n";
						}
					}
					
					Map<String, List<TipoContentElement>> roles = vb.getRoles();
					if (roles != null){
						String orderingGuide = "";
						String suppressed = "false";
						String variabilityType = "na";
						
						List<TipoContentElement> lstTce = roles.get(cp.getId());
						if (lstTce != null){
							Iterator<TipoContentElement> itTCE = lstTce.iterator();
							while (itTCE.hasNext()){
								TipoContentElement tce = itTCE.next();
								texto += 
										"\t\t\t<ContentElement xsi:type=\"uma:Role\" name=\"" + tce.getName() + "\" briefDescription=\"" + tce.getBriefDescription() + "\" id=\"" + tce.getId() + "\" orderingGuide=\"" + orderingGuide + "\" presentationName=\"" + tce.getPresentationName() + "\" suppressed=\"" + suppressed + "\" variabilityType=\"" + variabilityType + "\">" + "\n";
								
								String[] responsableDe = tce.getResponsibleFor().split(" ");
								int n = responsableDe.length;
								for (int i = 0; i < n; i++){
									String responsable = responsableDe[i];
									if (!responsable.equals("")){
										texto += 
											"\t\t\t\t<ResponsibleFor>" + responsableDe[i] + "</ResponsibleFor>" + "\n";
									}
								}
								
								texto +=
										"\t\t\t</ContentElement>" + "\n";
							}
						}
					}
					
					texto += "\t\t</MethodPackage>" + "\n";
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
				
				Iterator<TipoContentPackage> itcontentPackages = contentPackages.iterator();
				while (itcontentPackages.hasNext()){
					TipoContentPackage tcp = itcontentPackages.next();
					TipoContentCategory cp = tcp.getContentPackages();
					texto +=
							"\t\t<MethodPackageSelection>" + cp.getId() + "</MethodPackageSelection>" + "\n";
				}

				texto +=
						"\t\t<DefaultView>" + idDefaultView + "</DefaultView>" + "\n";
				
				if (contentCategory != null){
					texto +=
							"\t\t<ProcessView>" + idProcessView + "</ProcessView>" + "\n";
				}
				
				Iterator<TipoView> itAddCat = addedCategories.iterator();
				while (itAddCat.hasNext()){
					String addCat = itAddCat.next().getHref();
					String[] resCat = addCat.split("#");
					addCat = (resCat.length > 0) ? resCat[1] : addCat;
					texto +=
							"\t\t<AddedCategory>" + addCat + "</AddedCategory>" + "\n";
				}
				
				texto +=
						"\t</MethodConfiguration>" + "\n" +
					"</uma:MethodLibrary>";
				
		        byte bytes[] = texto.getBytes();
		        out.write(bytes);
			    
			    out.flush();
			    out.close();
			    
			    boolean ok = true;
			    if (exportar){
			    	ok = exportarModeloARepositorio();
			    }
			    
			    if (ok){
			    	FacesMessage mensaje = new FacesMessage("", "El modelo ha sido exportado correctamente.");
			    	FacesContext.getCurrentInstance().addMessage(null, mensaje);
			    	
			    	vb.setFinModelado(true);
			    }
			    else{
			    	FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha fallado la carga al repositorio.");
			    	FacesContext.getCurrentInstance().addMessage(null, mensaje);
			    }
			}
			else{
				System.out.println("##### exportarModelo - treeAdaptado: null");
			}
		}
    	catch (IOException e) {
    		System.out.println(e.getMessage());
		}
	}
	
	public String agregarElementoAxml(Struct s, DefaultDiagramModel modeloAdaptado){
		String texto = "";
		String id = s.getElementID();
		if (!idsAgregados.contains(id) && (Utils.buscarElementoEnModeloSinHijos(id, modeloAdaptado) != null)){
			idsAgregados.add(id);
			String nombre = s.getNombre();
			String nombrePresentacion = s.getPresentationName();
			TipoElemento tipo = s.getType();
			String briefDescription = s.getBriefDescription() != null ? s.getBriefDescription() : "";
			
			String isPlanned = s.getIsPlanned();
			String superactivity = s.getSuperActivities();
			String isOptional = s.getIsOptional();
			String variabilityType = s.getVariabilityType();
			String isSynchronizedWithSource = s.getIsSynchronizedWithSource();
			
			String orderingGuide = "";
			String suppressed = "false";
			String hasMultipleOccurrences = "false";
			String prefix = "";
			String isEventDriven = "false";
			String isOngoing = "false";
			String isRepeatable = "false";
			//String isEnactable = "false";
			String activityEntryState = "";
			String activityExitState = "";
			
			if (tipo == TipoElemento.ACTIVITY){
				texto += "\t\t\t\t<BreakdownElement xsi:type=\"uma:Activity\" name=\"" + nombre + "\" briefDescription=\"" + briefDescription + "\" id=\"" + id +
						"\" orderingGuide=\"" + orderingGuide + "\" " + "suppressed=\"" + suppressed + "\" presentationName=\"" + nombrePresentacion +
						"\" hasMultipleOccurrences=\"" + hasMultipleOccurrences + "\" isOptional=\"" + isOptional + "\" " + "isPlanned=\"" + isPlanned +
						"\" prefix=\"" + prefix + "\" isEventDriven=\"" + isEventDriven + "\" isOngoing=\"" + isOngoing + "\" isRepeatable=\"" + isRepeatable +
						"\" variabilityType=\"" + variabilityType + "\">" + "\n";
			}
			else if (tipo == TipoElemento.ITERATION){
				texto += "\t\t\t\t<BreakdownElement xsi:type=\"uma:Iteration\" name=\"" + nombre + "\" briefDescription=\"" + briefDescription + "\" id=\"" + id +
						"\" orderingGuide=\"" + orderingGuide + "\" " + "suppressed=\"" + suppressed + "\" presentationName=\"" + nombrePresentacion +
						"\" hasMultipleOccurrences=\"" + hasMultipleOccurrences + "\" isOptional=\"" + isOptional + "\" " + "isPlanned=\"" + isPlanned +
						"\" prefix=\"" + prefix + "\" isEventDriven=\"" + isEventDriven + "\" isOngoing=\"" + isOngoing + "\" isRepeatable=\"" + isRepeatable +
						"\" variabilityType=\"" + variabilityType + "\">" + "\n";
			}
			else if (tipo == TipoElemento.PHASE){
				texto += "\t\t\t\t<BreakdownElement xsi:type=\"uma:Phase\" name=\"" + nombre + "\" briefDescription=\"" + briefDescription + "\" id=\"" + id +
						"\" orderingGuide=\"" + orderingGuide + "\" " + "suppressed=\"" + suppressed + "\" presentationName=\"" + nombrePresentacion +
						"\" hasMultipleOccurrences=\"" + hasMultipleOccurrences + "\" isOptional=\"" + isOptional + "\" " + "isPlanned=\"" + isPlanned +
						"\" prefix=\"" + prefix + "\" isEventDriven=\"" + isEventDriven + "\" isOngoing=\"" + isOngoing + "\" isRepeatable=\"" + isRepeatable +
						"\" variabilityType=\"" + variabilityType + "\">" + "\n";
			}
			else if (tipo == TipoElemento.TASK){
				texto += "\t\t\t\t<BreakdownElement xsi:type=\"uma:TaskDescriptor\" name=\"" + nombre + "\" briefDescription=\"" + briefDescription + "\" id=\"" + id +
						"\" orderingGuide=\"" + orderingGuide + "\" " + "suppressed=\"" + suppressed + "\" presentationName=\"" + nombrePresentacion +
						"\" hasMultipleOccurrences=\"" + hasMultipleOccurrences + "\" isOptional=\"" + isOptional + "\" " + "isPlanned=\"" + isPlanned +
						"\" prefix=\"" + prefix + "\" isEventDriven=\"" + isEventDriven + "\" isOngoing=\"" + isOngoing + "\" isRepeatable=\"" + isRepeatable +
						"\" isSynchronizedWithSource=\"" + isSynchronizedWithSource + "\">" + "\n";
			}
			else if ((tipo == TipoElemento.ROLE) || (tipo == TipoElemento.VAR_ROLE)){
				texto += "\t\t\t\t<BreakdownElement xsi:type=\"uma:RoleDescriptor\" name=\"" + nombre + "\" briefDescription=\"" + briefDescription + "\" id=\"" + id +
						"\" orderingGuide=\"" + orderingGuide + "\" " + "suppressed=\"" + suppressed + "\" presentationName=\"" + nombrePresentacion +
						"\" hasMultipleOccurrences=\"" + hasMultipleOccurrences + "\" isOptional=\"" + isOptional + "\" " + "isPlanned=\"" + isPlanned +
						"\" prefix=\"" + prefix + "\" isSynchronizedWithSource=\"" + isSynchronizedWithSource + "\">" + "\n";
			}
			else if ((tipo == TipoElemento.WORK_PRODUCT) || (tipo == TipoElemento.VAR_WORK_PRODUCT)){
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
				String diagramURI = "";
				nombrePresentacion = s.getProcessComponentPresentationName();
				List<TipoMethodElementProperty> methodElementProperties = s.getMethodElementProperties();
				
				texto += "\t\t\t\t<BreakdownElement xsi:type=\"uma:CapabilityPattern\" name=\"" + nombre + "\" briefDescription=\"" + briefDescription + "\" id=\"" + idExtends +
						"\" orderingGuide=\"" + orderingGuide + "\" " + "suppressed=\"" + suppressed + "\" presentationName=\"" + nombrePresentacion +
						"\" hasMultipleOccurrences=\"" + hasMultipleOccurrences + "\" isOptional=\"" + isOptional + "\" " + "isPlanned=\"" + isPlanned +
						"\" prefix=\"" + prefix + "\" isEventDriven=\"" + isEventDriven + "\" isOngoing=\"" + isOngoing + "\" isRepeatable=\"" + isRepeatable +
						"\" variabilityBasedOnElement=\"" + categorizedElement + "\" variabilityType=\"" + variabilityType + (diagramURI != null && !diagramURI.equals("") ? "\" diagramURI=\"" + diagramURI : "") + "\">" + "\n";
				if (methodElementProperties.size() > 0){
					Iterator<TipoMethodElementProperty> itProperties = methodElementProperties.iterator();
					while (itProperties.hasNext()){
						TipoMethodElementProperty property = itProperties.next();
						texto += 
							"\t\t\t\t\t<MethodElementProperty name=\"" + property.getName() + "\" value=\"" + property.getValue() + "\"/>" + "\n";
					}
				}
				texto += 
							"\t\t\t\t\t<SuperActivity>" + superactivity + "</SuperActivity>" + "\n" +
						"\t\t\t\t</BreakdownElement>" + "\n";
			}
			
			if ((!texto.equals("")) && (tipo != TipoElemento.CAPABILITY_PATTERN)){
				texto += "\t\t\t\t\t<SuperActivity>" + superactivity + "</SuperActivity>" + "\n";
				
				//Agrego sucesores
				Map<String,String[]> sucesores = s.getPredecesores();
				if (sucesores != null){
					Iterator<Entry<String, String[]>> iter = sucesores.entrySet().iterator();
					while (iter.hasNext()){
						Entry<String, String[]> e = iter.next();
						String idLink = e.getKey();
				    	String sucesor = e.getValue()[0];
				    	texto += "\t\t\t\t\t<Predecessor id=\"" + idLink + "\"" +" linkType=\"finishToStart\"" + ">" + sucesor + "</Predecessor>" + "\n";
					}
				}

				// Si es un rol => Agrego el rol
				String idRole = s.getIdRole();
				if ((idRole != null) && (!idRole.equals(""))){
					texto += "\t\t\t\t\t<Role>" + idRole + "</Role>" + "\n";
				}
				
				// Si es una task => Agrego la task
				String idTask = s.getIdTask();
				if ((idTask != null) && (!idTask.equals(""))){
					texto += "\t\t\t\t\t<Task>" + idTask + "</Task>" + "\n";
				}

				// Si es un workproduct => Agrego el workproduct
				String idWp = s.getIdWorkProduct();
				if ((idWp != null) && (!idWp.equals(""))){
					texto += "\t\t\t\t\t<WorkProduct>" + idWp + "</WorkProduct>" + "\n"; 
				}
				
				// Si es responsable de algo => Lo agrego
				List<String> responsable = s.getResponsableDe();
				if ((responsable != null) && (responsable.size() > 0)){
					Iterator<String> itResp = responsable.iterator();
					while (itResp.hasNext()){
						texto += "\t\t\t\t\t<ResponsibleFor>" + itResp.next() + "</ResponsibleFor>" + "\n";
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
				
				List<TipoSection> steps = s.getSteps();
				if (steps != null){
					Iterator<TipoSection> it = steps.iterator();
					while (it.hasNext()){
						TipoSection step = it.next();
						String briefDescriptionStep = "";
						String orderingGuideStep = "";
						String suppressedStep = "false";
						String sectionNameStep = "";
						String variabilityTypeStep = "na";
						texto += "\t\t\t\t\t<Step name=\"" + step.getName() + "\" briefDescription=\""+ briefDescriptionStep + "\" id=\"" + step.getXmiId() + "\" orderingGuide=\"" + orderingGuideStep + /*"\" presentationName=\"" + presentationNameStep +*/ "\" suppressed=\"" + suppressedStep + "\" sectionName=\"" + sectionNameStep + "\" variabilityType=\"" + variabilityTypeStep + "\">" + "\n" +
									"\t\t\t\t\t<Description></Description>" + "\n" +
								 "\t\t\t\t\t</Step>" + "\n";
					}
				}
				
				// Agrego los hijos
				List<Struct> roles = new ArrayList<Struct>();
				List<Struct> workProduct = new ArrayList<Struct>();
				Iterator<Struct> it = s.getHijos().iterator();
				while (it.hasNext()){
					Struct hijo = it.next();
					if ((hijo.getType() != TipoElemento.ROLE) && (hijo.getType() != TipoElemento.WORK_PRODUCT) && (!Utils.esPuntoDeVariacion(hijo.getType()))){
						texto += agregarElementoAxml(hijo, modeloAdaptado);
					}
					else if (Utils.esPuntoDeVariacion(hijo.getType())){
						Iterator<Variant> itVars = hijo.getVariantes().iterator();
						while (itVars.hasNext()){
							Struct var = Utils.buscarElementoEnModelo(itVars.next().getID(), modeloAdaptado, "");
							if (var != null){
								var.setSuperActivities(hijo.getSuperActivities());
								if (var.getType() == TipoElemento.VAR_ROLE){
									roles.add(var);
								}
								else if (var.getType() == TipoElemento.VAR_WORK_PRODUCT){
									workProduct.add(var);
								}
								else{
									texto += agregarElementoAxml(var, modeloAdaptado);
								}
							}
						}
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
						texto += agregarElementoAxml(it.next(), modeloAdaptado);
					}
				}
				
				// Agrego los workProduct
				if (workProduct.size() > 0){
					it = workProduct.iterator();
					while (it.hasNext()){
						texto += agregarElementoAxml(it.next(), modeloAdaptado);
					}
				}
			}
		}
		return texto;
	}

	public boolean exportarModeloARepositorio(){
		FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
        String nomArchivo = vb.getNombreArchivo();
        String dirPlugin = vb.getDirPlugin();
		File dirLocal = null;
        try{
        	dirLocal = iem.exportarModelo(nomArchivo, dirPlugin, repositorioExport, userRepositorioExport, passRepositorioExport, comentarioRepositorioExport);
        }
    	catch (TransportException e){
    		FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Verifique que el usuario y contraseña sean correctos.");
        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
			e.printStackTrace();
			iem.borrarDirectorio(dirLocal);
			return false;
    	}
    	catch (Exception e){
			FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Verifique que la dirección del repositorio ingresado sea correcta.");
        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
			e.printStackTrace();
			iem.borrarDirectorio(dirLocal);
			return false;
    	}
        
        exportar = false;
		repositorioExport = ReadProperties.getProperty("URL_GITHUB_EXPORT_DEFAULT");
        comentarioRepositorioExport = "";
        userRepositorioExport = "";
        passRepositorioExport = "";
        return true;
	}
	
}
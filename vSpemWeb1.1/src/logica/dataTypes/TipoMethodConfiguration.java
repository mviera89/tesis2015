package logica.dataTypes;

import java.util.List;

public class TipoMethodConfiguration {

	private String xmiVersion;
	private String xmlnsXmi;
	private String xmlnsXsi;
	private String uma;
	private String epf;
	private String epfVersion;
	private String id;
	private String name;
	private String guid;
	private String briefDescription;
	private List<TipoMethodElementProperty> methodElementProperty;
	private List<TipoView> processViews;
	private TipoView defaultView;
	private List<TipoView> addedCategory;
	
	public TipoMethodConfiguration(String xmiVersion, String xmlnsXmi, String xmlnsXsi, String uma, String epf, String epfVersion, String id, String name, String guid, 
								   String briefDescription, List<TipoMethodElementProperty> methodElementProperty, List<TipoView> processViews, TipoView defaultView, 
								   List<TipoView> addedCategory) {
		this.xmiVersion = xmiVersion;
		this.xmlnsXmi = xmlnsXmi;
		this.xmlnsXsi = xmlnsXsi;
		this.uma = uma;
		this.epf = epf;
		this.epfVersion = epfVersion;
		this.id = id;
		this.name = name;
		this.guid = guid;
		this.briefDescription = briefDescription;
		this.methodElementProperty = methodElementProperty;
		this.processViews = processViews;
		this.defaultView = defaultView;
		this.addedCategory = addedCategory;
	}

	public String getXmiVersion() {
		return xmiVersion;
	}

	public void setXmiVersion(String xmiVersion) {
		this.xmiVersion = xmiVersion;
	}

	public String getXmlnsXmi() {
		return xmlnsXmi;
	}

	public void setXmlnsXmi(String xmlnsXmi) {
		this.xmlnsXmi = xmlnsXmi;
	}

	public String getXmlnsXsi() {
		return xmlnsXsi;
	}

	public void setXmlnsXsi(String xmlnsXsi) {
		this.xmlnsXsi = xmlnsXsi;
	}

	public String getUma() {
		return uma;
	}

	public void setUma(String uma) {
		this.uma = uma;
	}

	public String getEpf() {
		return epf;
	}

	public void setEpf(String epf) {
		this.epf = epf;
	}

	public String getEpfVersion() {
		return epfVersion;
	}

	public void setEpfVersion(String epfVersion) {
		this.epfVersion = epfVersion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getBriefDescription() {
		return briefDescription;
	}

	public void setBriefDescription(String briefDescription) {
		this.briefDescription = briefDescription;
	}

	public List<TipoMethodElementProperty> getMethodElementProperty() {
		return methodElementProperty;
	}

	public void setMethodElementProperty(
			List<TipoMethodElementProperty> methodElementProperty) {
		this.methodElementProperty = methodElementProperty;
	}

	public List<TipoView> getProcessViews() {
		return processViews;
	}

	public void setProcessViews(List<TipoView> processViews) {
		this.processViews = processViews;
	}

	public TipoView getDefaultView() {
		return defaultView;
	}

	public void setDefaultView(TipoView defaultView) {
		this.defaultView = defaultView;
	}

	public List<TipoView> getAddedCategory() {
		return addedCategory;
	}

	public void setAddedCategory(List<TipoView> addedCategory) {
		this.addedCategory = addedCategory;
	}

}

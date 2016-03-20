package dataTypes;

import java.util.List;

public class TipoContentElement {

	private TipoElemento tipoElemento;
	private String xmiVersion;
	private String xmi;
	private String uma;
	private String epf;
	private String epfVersion;
	private String id;
	private String name;
	private String guid;
	private String authors;
	private String changeDate;
	private String version;
	private String mainDescription;
	private List<TipoSection> sections;
	private String purpose;

	public TipoContentElement(TipoElemento tipoElemento, String xmiVersion, String xmi, String uma, String epf, String epfVersion, String id, String name, String guid,
			String authors, String changeDate, String version, String mainDescription, List<TipoSection> sections, String purpose) {
		this.tipoElemento = tipoElemento;
		this.xmiVersion = xmiVersion;
		this.xmi = xmi;
		this.uma = uma;
		this.epf = epf;
		this.epfVersion = epfVersion;
		this.id = id;
		this.name = name;
		this.guid = guid;
		this.authors = authors;
		this.changeDate = changeDate;
		this.version = version;
		this.mainDescription = mainDescription;
		this.sections = sections;
		this.purpose = purpose;
	}

	public TipoElemento getTipoElemento() {
		return tipoElemento;
	}

	public void setTipoElemento(TipoElemento tipoElemento) {
		this.tipoElemento = tipoElemento;
	}

	public String getXmiVersion() {
		return xmiVersion;
	}

	public void setXmiVersion(String xmiVersion) {
		this.xmiVersion = xmiVersion;
	}

	public String getXmi() {
		return xmi;
	}

	public void setXmi(String xmi) {
		this.xmi = xmi;
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

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(String changeDate) {
		this.changeDate = changeDate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMainDescription() {
		return mainDescription;
	}

	public void setMainDescription(String mainDescription) {
		this.mainDescription = mainDescription;
	}

	public List<TipoSection> getSections() {
		return sections;
	}

	public void setSections(List<TipoSection> sections) {
		this.sections = sections;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

}

package dataTypes;

public class TipoPlugin {

	private String id;
	private String name;
	private String guid;
	private String briefDescription;
	private String authors;
	private String changeDate;
	private String changeDescription;
	private String version;
	private String lineProcessDir;
	private String deliveryProcessDir;
	private String customCategoriesDir;

	public TipoPlugin(String id, String name, String guid, String briefDescription, String authors, String changeDate, String changeDescription, String version, 
					  String lineProcessDir, String deliveryProcessDir, String customCategoriesDir) {
		this.id = id;
		this.name = name;
		this.guid = guid;
		this.briefDescription = briefDescription;
		this.authors = authors;
		this.changeDate = changeDate;
		this.changeDescription = changeDescription;
		this.version = version;
		this.lineProcessDir = lineProcessDir;
		this.deliveryProcessDir = deliveryProcessDir;
		this.customCategoriesDir = customCategoriesDir;
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

	public String getChangeDescription() {
		return changeDescription;
	}

	public void setChangeDescription(String changeDescription) {
		this.changeDescription = changeDescription;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLineProcessDir() {
		return lineProcessDir;
	}

	public void setLineProcessDir(String lineProcessDir) {
		this.lineProcessDir = lineProcessDir;
	}

	public String getDeliveryProcessDir() {
		return deliveryProcessDir;
	}

	public void setDeliveryProcessDir(String deliveryProcessDir) {
		this.deliveryProcessDir = deliveryProcessDir;
	}

	public String getCustomCategoriesDir() {
		return customCategoriesDir;
	}

	public void setCustomCategoriesDir(String customCategoriesDir) {
		this.customCategoriesDir = customCategoriesDir;
	}

}

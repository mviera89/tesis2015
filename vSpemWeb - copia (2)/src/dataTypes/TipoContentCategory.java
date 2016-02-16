package dataTypes;

public class TipoContentCategory {

	private String type;
	private String id;
	private String name;
	private String guid;
	private String presentationName;
	private String briefDescription;
	private String categorizedElements;
	private TipoContentDescription contentDescription;
	private String shapeicon;
	private String nodeicon;

	public TipoContentCategory(String type, String id, String name,	String guid, String presentationName, String briefDescription, String categorizedElements,
							   String shapeicon, String nodeicon) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.guid = guid;
		this.presentationName = presentationName;
		this.briefDescription = briefDescription;
		this.categorizedElements = categorizedElements;
		this.shapeicon = shapeicon;
		this.nodeicon = nodeicon;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getPresentationName() {
		return presentationName;
	}

	public void setPresentationName(String presentationName) {
		this.presentationName = presentationName;
	}

	public String getBriefDescription() {
		return briefDescription;
	}

	public void setBriefDescription(String briefDescription) {
		this.briefDescription = briefDescription;
	}

	public String getCategorizedElements() {
		return categorizedElements;
	}

	public void setCategorizedElements(String categorizedElements) {
		this.categorizedElements = categorizedElements;
	}

	public TipoContentDescription getContentDescription() {
		return contentDescription;
	}

	public void setContentDescription(TipoContentDescription contentDescription) {
		this.contentDescription = contentDescription;
	}

	public String getShapeicon() {
		return shapeicon;
	}

	public void setShapeicon(String shapeicon) {
		this.shapeicon = shapeicon;
	}

	public String getNodeicon() {
		return nodeicon;
	}

	public void setNodeicon(String nodeicon) {
		this.nodeicon = nodeicon;
	}

}

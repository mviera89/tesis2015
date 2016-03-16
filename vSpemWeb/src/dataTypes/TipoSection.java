package dataTypes;

public class TipoSection {

	private String xmiId;
	private String name;
	private String guid;

	public TipoSection(String xmiId, String name, String guid) {
		super();
		this.xmiId = xmiId;
		this.name = name;
		this.guid = guid;
	}

	public String getXmiId() {
		return xmiId;
	}

	public void setXmiId(String xmiId) {
		this.xmiId = xmiId;
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

}

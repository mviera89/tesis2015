package dataTypes;

public class TipoMethodConfiguration {

	private String id;
	private String name;
	private String briefDescription;

	public TipoMethodConfiguration(String id, String name, String briefDescription) {
		this.id = id;
		this.name = name;
		this.briefDescription = briefDescription;
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

	public String getBriefDescription() {
		return briefDescription;
	}

	public void setBriefDescription(String briefDescription) {
		this.briefDescription = briefDescription;
	}

}

package dataTypes;

public class TipoLibrary {

	private String guid;
	private String name;
	private String id;

	public TipoLibrary(String guid, String name, String id) {
		this.guid = guid;
		this.name = name;
		this.id = id;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}

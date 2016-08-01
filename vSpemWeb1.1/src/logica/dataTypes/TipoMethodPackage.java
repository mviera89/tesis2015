package logica.dataTypes;

import java.util.List;

public class TipoMethodPackage {

	private String type;
	private String id;
	private String name;
	private String guid;
	private List<String> processComponentChild;
	
	public TipoMethodPackage(String type, String id, String name, String guid) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.guid = guid;
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

	public List<String> getProcessComponentChild() {
		return processComponentChild;
	}

	public void setProcessComponentChild(List<String> processComponentChild) {
		this.processComponentChild = processComponentChild;
	}

	@Override
	public String toString() {
		return "TipoMethodPackage [type=" + type + ", id=" + id + ", name="
				+ name + ", guid=" + guid + ", processComponentChild="
				+ processComponentChild + "]";
	}

}

package dominio;

public class Variant {

	private String ID;
	
	private String name;
	
	private String IDVarPoint;
	
	private boolean isInclusive;
	
	public Variant(String ID, String name, String IDVarPoint, boolean isInclusive){
		this.ID = ID;
		this.name = name;
		this.IDVarPoint = IDVarPoint;
		this.isInclusive = isInclusive;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIDVarPoint() {
		return IDVarPoint;
	}

	public void setIDVarPoint(String iDVarPoint) {
		IDVarPoint = iDVarPoint;
	}

	public boolean isInclusive() {
		return isInclusive;
	}

	public void setInclusive(boolean isInclusive) {
		this.isInclusive = isInclusive;
	}
	
	
}

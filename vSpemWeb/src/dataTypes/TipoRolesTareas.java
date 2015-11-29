package dataTypes;

import org.primefaces.model.diagram.DefaultDiagramModel;

import dominio.Struct;

public class TipoRolesTareas {

	private Struct rol;
	private DefaultDiagramModel primary;
	private DefaultDiagramModel additionally;

	public Struct getRol() {
		return rol;
	}

	public void setRol(Struct rol) {
		this.rol = rol;
	}

	public DefaultDiagramModel getPrimary() {
		return primary;
	}

	public void setPrimary(DefaultDiagramModel primary) {
		this.primary = primary;
	}

	public DefaultDiagramModel getAdditionally() {
		return additionally;
	}

	public void setAdditionally(DefaultDiagramModel additionally) {
		this.additionally = additionally;
	}

}

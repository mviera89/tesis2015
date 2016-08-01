package logica.dataTypes;

import java.util.List;

import logica.dominio.Struct;

import org.primefaces.model.diagram.DefaultDiagramModel;

public class TipoRolesTareas {

	private DefaultDiagramModel rol;
	private List<Struct> primary;
	private List<Struct> additionally;

	public DefaultDiagramModel getRol() {
		return rol;
	}

	public void setRol(DefaultDiagramModel rol) {
		this.rol = rol;
	}

	public List<Struct> getPrimary() {
		return primary;
	}

	public void setPrimary(List<Struct> primary) {
		this.primary = primary;
	}

	public List<Struct> getAdditionally() {
		return additionally;
	}

	public void setAdditionally(List<Struct> additionally) {
		this.additionally = additionally;
	}

}

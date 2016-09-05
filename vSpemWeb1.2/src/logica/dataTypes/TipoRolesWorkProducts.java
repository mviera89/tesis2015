package logica.dataTypes;

import logica.dominio.Struct;

import org.primefaces.model.diagram.DefaultDiagramModel;

public class TipoRolesWorkProducts {

	private Struct rol;
	private DefaultDiagramModel responsableDe;
	private DefaultDiagramModel modifica;

	public Struct getRol() {
		return rol;
	}

	public void setRol(Struct rol) {
		this.rol = rol;
	}

	public DefaultDiagramModel getResponsableDe() {
		return responsableDe;
	}

	public void setResponsableDe(DefaultDiagramModel responsableDe) {
		this.responsableDe = responsableDe;
	}

	public DefaultDiagramModel getModifica() {
		return modifica;
	}

	public void setModifica(DefaultDiagramModel modifica) {
		this.modifica = modifica;
	}

}

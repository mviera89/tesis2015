package logica.dataTypes;

import java.util.List;

import logica.dominio.Struct;

import org.primefaces.model.diagram.DefaultDiagramModel;

public class TipoRolesWorkProducts {

	private Struct rol;
	private List<DefaultDiagramModel> responsableDe;
	private List<DefaultDiagramModel> modifica;

	public Struct getRol() {
		return rol;
	}

	public void setRol(Struct rol) {
		this.rol = rol;
	}

	public List<DefaultDiagramModel> getResponsableDe() {
		return responsableDe;
	}

	public void setResponsableDe(List<DefaultDiagramModel> responsableDe) {
		this.responsableDe = responsableDe;
	}

	public List<DefaultDiagramModel> getModifica() {
		return modifica;
	}

	public void setModifica(List<DefaultDiagramModel> modifica) {
		this.modifica = modifica;
	}

}

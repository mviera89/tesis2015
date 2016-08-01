package logica.dataTypes;

import java.util.List;

import logica.dominio.Struct;

public class TipoTareasWorkProducts {
	private Struct tarea;
	private List<Struct> mandatoryInputs;
	private List<Struct> optionalInputs;
	private List<Struct> externalInputs;
	private List<Struct> Outputs;
	public Struct getTarea() {
		return tarea;
	}
	public void setTarea(Struct tarea) {
		this.tarea = tarea;
	}
	public List<Struct> getMandatoryInputs() {
		return mandatoryInputs;
	}
	public void setMandatoryInputs(List<Struct> mandatoryInputs) {
		this.mandatoryInputs = mandatoryInputs;
	}
	public List<Struct> getOptionalInputs() {
		return optionalInputs;
	}
	public void setOptionalInputs(List<Struct> optionalInputs) {
		this.optionalInputs = optionalInputs;
	}
	public List<Struct> getExternalInputs() {
		return externalInputs;
	}
	public void setExternalInputs(List<Struct> externalInputs) {
		this.externalInputs = externalInputs;
	}
	public List<Struct> getOutputs() {
		return Outputs;
	}
	public void setOutputs(List<Struct> outputs) {
		Outputs = outputs;
	}
	
}

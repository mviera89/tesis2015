package dataTypes;

import java.util.ArrayList;
import java.util.List;

public class TipoContentPackage {

	private TipoContentCategory contentPackages;
	private List<TipoContentElement> tasksCP;
	private List<TipoContentElement> workproductsCP;

	public TipoContentPackage() {
		tasksCP = new ArrayList<TipoContentElement>();
		workproductsCP = new ArrayList<TipoContentElement>();
	}

	public TipoContentPackage(TipoContentCategory contentPackages, List<TipoContentElement> tasksCP) {
		this.contentPackages = contentPackages;
		this.tasksCP = tasksCP;
	}

	public TipoContentCategory getContentPackages() {
		return contentPackages;
	}

	public void setContentPackages(TipoContentCategory contentPackages) {
		this.contentPackages = contentPackages;
	}

	public List<TipoContentElement> getTasksCP() {
		return tasksCP;
	}

	public void setTasksCP(List<TipoContentElement> tasksCP) {
		this.tasksCP = tasksCP;
	}

	public List<TipoContentElement> getWorkproductsCP() {
		return workproductsCP;
	}

	public void setWorkproductsCP(List<TipoContentElement> workproductsCP) {
		this.workproductsCP = workproductsCP;
	}

}

package dataTypes;

import java.util.ArrayList;
import java.util.List;

public class TipoContentPackage {

	private TipoContentCategory contentPackages;
	private List<TipoTask> tasksCP;

	public TipoContentPackage() {
		tasksCP = new ArrayList<TipoTask>();
	}

	public TipoContentPackage(TipoContentCategory contentPackages, List<TipoTask> tasksCP) {
		this.contentPackages = contentPackages;
		this.tasksCP = tasksCP;
	}

	public TipoContentCategory getContentPackages() {
		return contentPackages;
	}

	public void setContentPackages(TipoContentCategory contentPackages) {
		this.contentPackages = contentPackages;
	}

	public List<TipoTask> getTasksCP() {
		return tasksCP;
	}

	public void setTasksCP(List<TipoTask> tasksCP) {
		this.tasksCP = tasksCP;
	}

}

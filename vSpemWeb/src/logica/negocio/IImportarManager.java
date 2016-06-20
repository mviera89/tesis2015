package logica.negocio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import logica.dataTypes.TipoContentCategory;
import logica.dataTypes.TipoContentElement;
import logica.dataTypes.TipoMethodConfiguration;
import logica.dataTypes.TipoMethodPackage;
import logica.dataTypes.TipoPlugin;
import logica.dataTypes.TipoView;

import com.sun.mail.iap.ConnectionException;

@Local
public interface IImportarManager {

	String cargarArchivos(boolean desdeRepositorio, String repositorio, List<String> archivosDisponibles, List<String> archivosDisponiblesLocal) throws IOException;
	void cargarTodoDeRepositorio(boolean desdeRepositorio, String repositorio, String dirPlugin) throws ConnectionException, IOException;
	void descargarArchivos(boolean desdeRepositorio, String repositorio, String dir, String archivo, String archivoFinal) throws FileNotFoundException, IOException, MalformedURLException;
	Object[] cargarArchivoInicial(boolean desdeRepositorio, String repositorio, String nombreArchivo) throws MalformedURLException, FileNotFoundException, IOException;
	TipoMethodConfiguration cargarArchivoConfigurationRepositorio(boolean desdeRepositorio, String repositorio) throws FileNotFoundException, IOException;
	TipoPlugin cargarDeRepositorio(boolean desdeRepositorio, String repositorio, String dir, String archivo, String archivoFinal);
	List<TipoMethodPackage> cargarProcessPackageRepositorio(String archivoPlugin);
	Map<String, Object> parsearDatosPlugin(boolean desdeRepositorio, String repositorio, String dirPlugin, String dirLineProcess, TipoPlugin plugin, String archivoPlugin, List<TipoView> addedCategory);
	TipoContentCategory cargarCustomCategoriesRepositorio(String dirPlugin, String dirCustomCategory, String archivoCC, String archivoPlugin);
	Map<String, TipoContentCategory> cargarCategorizedElementsRepositorio(String dirPrevia, String archivoPlugin, String categorizedElements);
	TipoContentElement cargarContentElementsRepositorio(String dirPrevia, String archivo, String tag);
	List<TipoContentElement> cargarTemplateRepositorio(String archivoPlugin, String tag);

}

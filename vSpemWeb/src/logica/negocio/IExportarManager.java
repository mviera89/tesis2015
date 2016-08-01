package logica.negocio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import java.io.File;
import java.io.IOException;

import logica.dominio.Struct;
import logica.negocio.IExportarManager;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.primefaces.model.diagram.DefaultDiagramModel;

@Local
public interface IExportarManager {

	void actualizarPredecesoresModelo(DefaultDiagramModel modelo, HashMap<String, String[]> puntosDeVariacion);
	boolean elementoPerteneceAModelo(String id, List<Struct> lista, Map<String, String[]> predecesores);
	void actualizarPredecesor(String idVP, String idVariant, List<Struct> list, int num);
	void quitarPredecessor(String Id, List<Struct> elementos);
	File exportarModelo(String nomArchivo, String dirPlugin, String repositorioExport, String userRepositorioExport, String passRepositorioExport, String comentarioRepositorioExport) throws TransportException, IOException, GitAPIException;
	void copiarArchivos(File origen, File destino);
	void copiarDirectorio(File origen, File destino);
	void borrarDirectorio(File dir);
	
}

package logica.managerbeans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;

import logica.dominio.Struct;
import logica.dominio.Variant;
import logica.enumerados.TipoElemento;
import logica.negocio.IExportarManager;
import logica.utils.GitControl;
import logica.utils.Utils;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;

import config.Constantes;
import config.ReadProperties;

@Stateless
public class ExportarManager implements IExportarManager{

	private void actualizar (Struct s, DefaultDiagramModel modelo, HashMap<String, String[]> puntosDeVariacion){
		TipoElemento tipo = s.getType();
		if (Utils.esPuntoDeVariacion(tipo)){
			List<Variant> variants = s.getVariantes();
			String idVarPoint = s.getElementID();
			String[] variantesParaPVArray = puntosDeVariacion.get(idVarPoint);
			Map<String, String[]> predecesores = s.getPredecesores();
			int num = 0;
			// Para cada variante
			Iterator<Variant> itV = variants.iterator();
			while (itV.hasNext()){
				num ++;
				Variant v = itV.next();
				String idV = v.getID();
				boolean pertenece = false;
				// Veo si v fue seleccionada para el PV
				if ((variantesParaPVArray != null) && (variantesParaPVArray.length > 0)){
					List<String> variantesParaPV = Arrays.asList(variantesParaPVArray);
					if (variantesParaPV.contains(idV)){
						Struct st = Utils.buscarElementoEnModelo(idV, modelo, "");
						if ((st != null) && (predecesores != null)){
							pertenece = true;
							Map<String, String[]> predecesores2 = new HashMap<String, String[]>();
							Iterator<Entry<String, String[]>> itPred = predecesores.entrySet().iterator();
							while (itPred.hasNext()){
								Entry<String, String[]> entry = itPred.next();
								String idLink = entry.getKey() + num;
								predecesores2.put(idLink, entry.getValue());
							}
							st.setPredecesores(predecesores2);
						}
					}
					else{
						pertenece = elementoPerteneceAModelo(idV, s.getHijos(), predecesores);
					}
				}
				if (pertenece){
					// Buscar quien tiene este id como predecessor
					Iterator<Element> itModeloA = modelo.getElements().iterator();
					boolean encontre = false;
					while (itModeloA.hasNext() && !encontre){
						Element ele = itModeloA.next();
						Struct str = (Struct) ele.getData();
						if (str.getPredecesores() != null){
							Iterator<Entry<String, String[]>> iter = str.getPredecesores().entrySet().iterator();
							String link = "";
							String properties = "";
							while (iter.hasNext()){
								Entry<String, String[]> elem = iter.next();
								String idLink = elem.getKey();
								String predecesor = elem.getValue()[0];
								if (predecesor.equals(idVarPoint)){
									encontre = true;
									link = idLink;
									properties = elem.getValue()[1];
								}
							}
							if (!link.equals("")){
								String[] array = {idV, properties};
								str.getPredecesores().put(link + num, array);
							}
						}
						if (!encontre){
							actualizarPredecesor(idVarPoint, idV, str.getHijos(), num);
						}
					}
				}
			}

			// Sacar el varpoint de quien lo tiene como predecesor
			Iterator<Element> itModeloA = modelo.getElements().iterator();
			boolean encontre = false;
			while (itModeloA.hasNext() && !encontre){
				Element ele = itModeloA.next();
				Struct str = (Struct) ele.getData();
				if (str.getPredecesores() != null){
					Iterator<Entry<String, String[]>> iter = str.getPredecesores().entrySet().iterator();
					String link = "";
					while (iter.hasNext() && !encontre){
						Entry<String, String[]> elem = iter.next();
						String idLink = elem.getKey();
						String predecesor = elem.getValue()[0];
						if (predecesor.equals(idVarPoint)){
							encontre = true;
							link = idLink;
							str.getPredecesores().remove(link);
							// Si al sacar el id del VP de la lista de predecesores esta queda vacía, es porque no se seleccionó ninguna variante.
							if ((str.getPredecesores() == null) || (str.getPredecesores().size() == 0)){
								str.setLinkToPredecessor(s.getLinkToPredecessor());
								str.setPredecesores(s.getPredecesores());
							}
						}
					}
				}
				if (!encontre){
					quitarPredecessor(idVarPoint, str.getHijos());
				}
			}
		}
		List<Struct> hijos = s.getHijos();
		if (hijos != null){
			Iterator<Struct> itHijos = hijos.iterator();
			while (itHijos.hasNext()){
				Struct h = itHijos.next();
				actualizar(h, modelo, puntosDeVariacion);
			}
		}
	}

	public void actualizarPredecesoresModelo(DefaultDiagramModel modelo, HashMap<String, String[]> puntosDeVariacion){
		// Recorro modelo
		// Para cada variante busco si el id esta en modelo adapatado (fue seleccionada)
		// Si esta busco el id del var point con este id busco en los predecesores, si alguien lo tiene se lo quito y se agrega la variante como predecesor
		// Para cada varPoint veo sus predecesores, luego a cada variante elegida se le setean los mismos
		Iterator<Element> itModelo = modelo.getElements().iterator();
		while (itModelo.hasNext()){
			Element e = itModelo.next();
			Struct s = (Struct) e.getData();
			actualizar(s, modelo, puntosDeVariacion);
		}
	}

	public boolean elementoPerteneceAModelo(String id, List<Struct> lista, Map<String, String[]> predecesores){
		Iterator<Struct> it = lista.iterator();
		while (it.hasNext()){
			Struct s = it.next();
			if (s.getElementID().equals(id)){
				s.setPredecesores(predecesores);
				return true;
			}
			else{
				return elementoPerteneceAModelo(id, s.getHijos(),predecesores);
			}
		}
		return false;
	}

	public void actualizarPredecesor(String idVP, String idVariant, List<Struct> list, int num){
		// Quien tenga idVP como predecesor, se agrega idVariant como predecesor
		if (list != null){
			Iterator<Struct> it = list.iterator();
			while (it.hasNext()){
				Struct s = it.next();
				Map<String,String[]> predecesores = s.getPredecesores();

				if (predecesores != null) {
					Iterator<Entry<String, String[]>> iter = predecesores.entrySet().iterator();
					boolean encontre = false;
					String link = "";
					String properties = "";
					while (iter.hasNext() && !encontre){
						Entry<String, String[]> e = iter.next();
						String idLink = e.getKey();
						String predecesor = e.getValue()[0];
						if (predecesor.equals(idVP)){
							encontre = true;
							link = idLink;
							properties = e.getValue()[1];
						}
					}
					if (encontre && !link.equals("")){
						String[] array = {idVariant, properties};
						s.getPredecesores().put(link + num, array);
					}
					else {
						List<Struct> hijos = s.getHijos();
						Iterator<Struct> ite = hijos.iterator();
						while (ite.hasNext()){
							Struct st = ite.next();
							actualizarPredecesor(idVP, idVariant, st.getHijos(), num);
						}
					}
				}
			}
		}
	}

	public void quitarPredecessor(String Id, List<Struct> elementos){
		Iterator<Struct> it = elementos.iterator();
		boolean encontre = false;
		while (it.hasNext() && !encontre){
			Struct str = it.next();

			Map<String,String[]> predecesores = str.getPredecesores();
			if (predecesores != null) {
				Iterator<Entry<String, String[]>> iter = predecesores.entrySet().iterator();
				String link = "";
				while (iter.hasNext() && !encontre){
					Entry<String, String[]> e = iter.next();
					String idLink = e.getKey();
					String predecesor = e.getValue()[0];
					if (predecesor.equals(Id)){
						encontre = true;
						link = idLink;
					}
				}
				if (encontre){
					str.getPredecesores().remove(link);
					// Si al sacar el id del VP de la lista de predecesores esta queda vacía, es porque no se seleccionó ninguna variante. 
					if ((str.getPredecesores() == null) || (str.getPredecesores().size() == 0)){
						Struct s = Utils.buscarElemento(Id, elementos, "");
						if (s != null){
							str.setLinkToPredecessor(s.getLinkToPredecessor());
							str.setPredecesores(s.getPredecesores());
						}
					}
				}
				else {
					List<Struct> hijos = str.getHijos();
					Iterator<Struct> ite = hijos.iterator();
					while (ite.hasNext()){
						Struct s = ite.next();
						quitarPredecessor(Id, s.getHijos());
					}
				}
			}
		}
	}

	public File exportarModelo(String nomArchivo, String dirPlugin, String repositorioExport, String userRepositorioExport, String passRepositorioExport, String comentarioRepositorioExport) throws IOException, NoFilepatternException, GitAPIException{
		// archivo puede ser de la forma: dir1/dir2/.../nombre
		String repo = repositorioExport;
		int indexDiv = repo.indexOf("/");
		String dirRepo = "";
		while (indexDiv != -1){
			dirRepo = repo.substring(0, indexDiv);
			repo = repo.substring(indexDiv + 1, repo.length());
			indexDiv = repo.indexOf("/");
		}

		String dir = "gitExport_" + dirRepo;
		String localPath = ReadProperties.getProperty("destinoExport") + dir;
		String remotePath = Constantes.URL_GITHUB + repositorioExport;
		GitControl gc = new GitControl(localPath, remotePath, userRepositorioExport, passRepositorioExport);

		// Clonar repositorio a local si no existe; sino, hacer pull
		File dirLocal  = new File(localPath);
		if (!dirLocal.isDirectory()){
			gc.cloneRepo();
		}
		else{
			gc.pullFromRepo();
		}

		nomArchivo = nomArchivo.substring(0, nomArchivo.length() - 4); // Para quitar la extensión

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		String fecha = sdf.format(new Date());

		String nombre = nomArchivo + "_" + Constantes.nomArchivoExport;
		File origen  = new File(ReadProperties.getProperty("destinoExport") + nombre);

		// Creo la ruta destinoExport/dir/fecha/
		File destino = new File(ReadProperties.getProperty("destinoExport") + dir + "/Export" + fecha);
		destino.mkdirs();

		// Creo en esa ruta el archivo 'nombre'
		copiarArchivos(origen, new File(ReadProperties.getProperty("destinoExport") + dir + "/Export" + fecha + "/" + nombre));

		// Copiar archivos de imagenes al localPath
		origen  = new File(ReadProperties.getProperty("destinoExport") + dirPlugin);
		copiarDirectorio(origen, new File(destino + "/" + dirPlugin));

		// Hacer el add, commit y push
		gc.addToRepo();
		gc.commitToRepo(comentarioRepositorioExport);
		gc.pushToRepo();

		return dirLocal;
	}

	public void copiarArchivos(File origen, File destino){
		try{
			InputStream in = new FileInputStream(origen);
			OutputStream out = new FileOutputStream(destino);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	public void copiarDirectorio(File origen, File destino){
		File[] archivos = origen.listFiles();
		int n = archivos.length;
		for (int i = 0; i < n; i++){
			File archivo = archivos[i];
			if (archivo.isDirectory()) {
				File newDestino = new File(destino + "/" + archivo.getName());
				newDestino.mkdirs();
				copiarDirectorio(archivo, newDestino);
				// Si el directorio está vacío => Lo borro
				if (newDestino.listFiles().length == 0){
					newDestino.delete();
				}
			}
			else{
				String nombreExt = archivo.getName();
				int indexExtension = nombreExt.indexOf(".");
				if (indexExtension != -1){
					String extArchivo = nombreExt.substring(indexExtension + 1, nombreExt.length());
					// Solo cargo archivos que no son xmi, excepto los 'diagram.xmi'
					if ((!extArchivo.equals("xmi")) || (nombreExt.equals("diagram.xmi"))){
						copiarArchivos(archivo, new File(destino + "/" + archivo.getName()));
					}
				}
			}
		}
	}

	public void borrarDirectorio(File dir){
		if (dir != null){
			File[] archivos = dir.listFiles();
			int n = archivos.length;
			for (int i = 0; i < n; i++){
				if (archivos[i].isDirectory()) {
					borrarDirectorio(archivos[i]);
				}
				archivos[i].delete();
			}
			dir.delete();
		}
	}

}

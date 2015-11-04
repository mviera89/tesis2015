package managedBeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import config.Constantes;
import dominio.Struct;

@ManagedBean
@ViewScoped
public class ImportarModeloBean {

	private String repositorioIngresado = "mviera89/tesis2015/tree/master/upload/";
	private String repositorio = "";
	private String nombreArchivo = "";
	private List<String> archivosDisponibles = new ArrayList<String>();

	public String getRepositorioIngresado() {
		return repositorioIngresado;
	}

	public void setRepositorioIngresado(String repositorioIngresado) {
		this.repositorioIngresado = repositorioIngresado;
	}
	
	public String getRepositorio() {
		return repositorio;
	}

	public void setRepositorio(String repositorio) {
		this.repositorio = repositorio;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public List<String> getArchivosDisponibles() {
		return archivosDisponibles;
	}

	public void setArchivosDisponibles(List<String> archivosDisponibles) {
		this.archivosDisponibles = archivosDisponibles;
	}

	public void leerArchivos() throws Exception {
		// repositorio	= "mviera89/tesis2015/tree/master/upload/";
		// repositorio* = "mviera89/tesis2015/blob/master/upload/";
		repositorio = repositorioIngresado;
		int index = repositorio.indexOf("tree");
		if (index != -1){
			repositorio = repositorio.replace("tree", "blob");
		}
		System.out.println("Carga: https://github.com/" + repositorio);
		
		URL url = new URL("https://github.com/" + repositorio);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		archivosDisponibles.clear();
		String linea;
		while ((linea = in.readLine()) != null){
			// <a href="/repositorio/.../nomArchivo"
			String strBuscado = "<a href=\"/" + repositorio;
			int indexIni = linea.indexOf(strBuscado);
			if (indexIni != -1){
				String archivo = linea.substring(indexIni + strBuscado.length());
				archivo = archivo.substring(0, archivo.indexOf("\""));
				int indexExtension = archivo.indexOf(".");
				if (indexExtension != -1){
					String nomArchivo = archivo.substring(0, indexExtension);
					String extArchivo = archivo.substring(indexExtension + 1, archivo.length());
					// Solo cargo archivos xmi
					if (extArchivo.equals("xmi")){
						// nomArchivo puede ser de la forma: dir1/dir2/.../nombre
						int indexDiv = nomArchivo.indexOf("/");
						while (indexDiv != -1){
							String dir = nomArchivo.substring(0, indexDiv);
							nomArchivo = nomArchivo.substring(indexDiv + 1, nomArchivo.length());
							indexDiv = nomArchivo.indexOf("/");
							repositorio += dir + "/";
						}
						archivosDisponibles.add(nomArchivo + "." + extArchivo);
					}
				}
			}
		}
		
		in.close();
	}
	public void cargarArchivo() throws Exception {
		if (!nombreArchivo.equals("")){
			// repositorio	= "mviera89/tesis2015/blob/master/upload/";
			// urlDescargar = "mviera89/tesis2015/master/upload/";
			int index = repositorio.indexOf("blob/");
			String urlDescargar = (index != -1) ? repositorio.replace("blob/", "") : repositorio.concat("master/");
			System.out.println("Descarga: https://raw.githubusercontent.com/" + urlDescargar + nombreArchivo);
			
			URL url = new URL("https://raw.githubusercontent.com/" + urlDescargar + nombreArchivo);
			URLConnection urlCon = url.openConnection();
			
			InputStream is = urlCon.getInputStream();
			FileOutputStream fos = new FileOutputStream(Constantes.destinoDescargas + nombreArchivo);
			
			byte [] array = new byte[1000];
			int leido = is.read(array);
			while (leido > 0) {
			   fos.write(array, 0, leido);
			   leido = is.read(array);
			}
			
			is.close();
			fos.close();
			
			FacesMessage mensaje = new FacesMessage("", "El archivo " + nombreArchivo + " ha sido cargado correctamente.");
	        FacesContext.getCurrentInstance().addMessage(null, mensaje);
	        
			FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
			VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
	        vb.setNombreArchivo(nombreArchivo);
	        
	        AdaptarModeloBean ab = (AdaptarModeloBean) session.getAttribute("adaptarModeloBean");
	        if (ab != null){
	        	ab.init();
	        }
	        vb.setFinModelado(false);
		}
	}

}
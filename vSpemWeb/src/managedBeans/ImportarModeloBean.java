package managedBeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.UploadedFile;

import config.Constantes;

@ManagedBean
@ViewScoped
public class ImportarModeloBean {

	private String mensajeAyudaRepositorio = Constantes.mensjaeAyudaRepositorio;
	private String repositorioIngresado = Constantes.URL_GITHUB_DEFAULT;
	private String repositorio = "";
	private String nombreArchivo = "";
	private List<String> archivosDisponibles = new ArrayList<String>();

	public String getMensajeAyudaRepositorio() {
		return mensajeAyudaRepositorio;
	}

	public void setMensajeAyudaRepositorio(String mensajeAyudaRepositorio) {
		this.mensajeAyudaRepositorio = mensajeAyudaRepositorio;
	}

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

	/*** CARGA REMOTA DE ARCHIVOS ***/

	public void leerArchivosRepositorio() throws Exception {
		try{
			nombreArchivo = "";
			if (!repositorioIngresado.equals("")){
				repositorio = repositorioIngresado;
				archivosDisponibles.clear();
				
				// Si en la url del repositorio existe el string "tree" => Lo sustituyo por "blob".
				int index = repositorio.indexOf("tree");
				if (index != -1){
					repositorio = repositorio.replace("tree", "blob");
				}
				System.out.println("Carga: " + Constantes.URL_GITHUB + repositorio);
				
				URL url = new URL(Constantes.URL_GITHUB + repositorio);
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				
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
				
				if (archivosDisponibles.size() == 0){
					FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN, "", Constantes.MENSAJE_ARCHIVOS_NO_ENCONTRADOS);
		        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
				}
			}
			else{
				FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN, "", Constantes.MENSAJE_URL_NULL);
	        	FacesContext.getCurrentInstance().addMessage(null, mensaje);
			}
		}
		catch (FileNotFoundException e){
		    System.out.println("No se encontró la URL: " + e.getMessage() + ".");
		    FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", Constantes.MENSAJE_URL_NO_ACCESIBLE + "'" + Constantes.URL_GITHUB + repositorioIngresado + "'.");
	        FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	public void cargarArchivoRepositorio() throws Exception {
		if (!nombreArchivo.equals("")){
			// Si en la url del repositorio existe el string "blob/" => Lo sustituyo por "", sino, le agrego el string "master/"
			int index = repositorio.indexOf("blob/");
			String urlDescargar = (index != -1) ? repositorio.replace("blob/", "") : repositorio.concat("master/");
			System.out.println("Descarga: " + Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + nombreArchivo);
			
			URL url = new URL(Constantes.URL_GITHUB_DOWNLOAD + urlDescargar + nombreArchivo);
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
		else{
			FacesMessage mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN, "", Constantes.MENSAJE_ARCHIVO_NULL);
	        FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
	}

	/*** CARGA LOCAL DE ARCHIVOS ***/

	public void cargarArchivoLocal(FileUploadEvent event) {
		UploadedFile archivo = event.getFile();
        if (archivo != null) {
    		nombreArchivo = archivo.getFileName();
            FacesMessage mensaje = new FacesMessage("", "El archivo " + archivo.getFileName() + " ha sido cargado correctamente.");
            FacesContext.getCurrentInstance().addMessage(null, mensaje);
            try {
            	copiarArchivoLocal(archivo.getFileName(), archivo.getInputstream());
                
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
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	public void copiarArchivoLocal(String nombreArchivo, InputStream in) {
		try {
			OutputStream out = new FileOutputStream(new File(Constantes.destinoDescargas + nombreArchivo));
			int leer = 0;
			byte[] bytes = new byte[1024];
			
			while ((leer = in.read(bytes)) != -1) {
				out.write(bytes, 0, leer);
			}
			
			in.close();
			out.flush();
			out.close();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/*** EVENTOS ***/

	public void onTabChange(TabChangeEvent event) {
		repositorioIngresado = Constantes.URL_GITHUB_DEFAULT;
		repositorio = "";
		nombreArchivo = "";
		archivosDisponibles.clear();
    }

}

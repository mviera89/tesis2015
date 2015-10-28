package managedBeans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import config.Constantes;

@ManagedBean
@ViewScoped
public class ImportarModeloBean {

	private String nombreArchivo = "";

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public void cargarArchivo(FileUploadEvent event) {
		UploadedFile archivo = event.getFile();
        if (archivo != null) {
    		nombreArchivo = archivo.getFileName();
            FacesMessage mensaje = new FacesMessage("", "El archivo " + archivo.getFileName() + " ha sido cargado correctamente.");
            FacesContext.getCurrentInstance().addMessage(null, mensaje);
            try {
                copiarArchivo(archivo.getFileName(), archivo.getInputstream());
                
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

    public void copiarArchivo(String nombreArchivo, InputStream in) {
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
    
}
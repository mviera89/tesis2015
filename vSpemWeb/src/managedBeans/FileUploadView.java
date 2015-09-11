package managedBeans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
  
@ManagedBean(name="fileUploadView")
public class FileUploadView {
	
	private UploadedFile file;
	private String destination="C:\\download\\";
	 
    public UploadedFile getFile() {
        return file;
    }
 
    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
    public void upload(FileUploadEvent event) {
        FacesMessage msg = new FacesMessage("Success! ", event.getFile().getFileName() + " is uploaded.");  
        FacesContext.getCurrentInstance().addMessage(null, msg);
        // Do what you want with the file        
        try {
            copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
            VistaBean vb = (VistaBean) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("VistaBean");
            //vb.actualizarIndiceActivo(1);

            /*SelectionView sv = (SelectionView) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("SelectionView");
            sv.setNomFile(event.getFile().getFileName());*/
            
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }  
 
    public void copyFile(String fileName, InputStream in) {
    	try {
		    // write the inputStream to a FileOutputStream
		    OutputStream out = new FileOutputStream(new File(destination + fileName));
		    int read = 0;
		    byte[] bytes = new byte[1024];
		  
		    while ((read = in.read(bytes)) != -1) {
		    	out.write(bytes, 0, read);
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
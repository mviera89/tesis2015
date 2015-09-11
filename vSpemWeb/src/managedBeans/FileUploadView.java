package managedBeans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
  
@ManagedBean(name="fileUploadView")
public class FileUploadView {
   private String destination="C:\\download\\";
 
    public void upload(FileUploadEvent event) {
    	System.out.println("##### upload");
        FacesMessage msg = new FacesMessage("Success! ", event.getFile().getFileName() + " is uploaded.");  
        FacesContext.getCurrentInstance().addMessage(null, msg);
        System.out.println("Success! " + event.getFile().getFileName() + " is uploaded.");
        // Do what you want with the file        
        try {
            copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }  
 
    public void copyFile(String fileName, InputStream in) {
    	try {
    		System.out.println("##### copyFile");
		  
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
		  
		    System.out.println("New file created!");
		} 
    	catch (IOException e) {
    		System.out.println(e.getMessage());
		}
    }
    
}
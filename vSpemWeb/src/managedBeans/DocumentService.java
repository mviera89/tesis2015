package managedBeans;

import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import dominio.Variant;
import dominio.Document;
import dominio.Struct;
 
@ManagedBean(name = "documentService")
@ViewScoped
public class DocumentService {
     
   public TreeNode createTree(List<Struct> nodos) {
	   
        TreeNode root = new CheckboxTreeNode(new Document("-", "Inicio", "-", "Folder"), null);
        root.setExpanded(true);
        root.setSelectable(false);
        
        Iterator<Struct> it = nodos.iterator();
        while (it.hasNext()){
        	Struct s = it.next();
        	TreeNode padre = new CheckboxTreeNode(new Document(s.getElementID(), s.getNombre(), "-", "Folder"), root);
        	padre.setExpanded(true);
        	padre.setSelectable(false);
        	
        	
        	Iterator<Variant> it1 = s.getVariantes().iterator();
        	while (it1.hasNext()){
        		Variant v = it1.next();
        		TreeNode hijo = new CheckboxTreeNode(new Document(v.getID(), v.getName(), "-", "Folder"), padre);
        		hijo.setExpanded(true);
        		
        	}
        }
              
        return root;
    }
}
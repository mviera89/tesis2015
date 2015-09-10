package managedBeans;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import dominio.Variant;

import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import dominio.Document;
import dominio.Struct;
 
@ManagedBean(name = "documentService")
@ApplicationScoped
public class DocumentService {
     
   public TreeNode createTree(List<Struct> nodos) {
        TreeNode root = new DefaultTreeNode(new Document("Inicio", "-", "Folder"), null);
        root.setExpanded(true);
        root.setSelectable(false);
         
        Iterator<Struct> it = nodos.iterator();
        while (it.hasNext()){
        	Struct s = it.next();
        	TreeNode padre = new DefaultTreeNode(new Document(s.getNombre(), "-", "Folder"), root);
        	padre.setExpanded(true);
        	padre.setSelectable(false);
        	
        	Iterator<Variant> it1 = s.getHijos().iterator();
        	while (it1.hasNext()){
        		Variant v = it1.next();
        		TreeNode hijo = new CheckboxTreeNode(new Document(v.getName(), "-", "Folder"), padre);
        		hijo.setExpanded(true);
        		
        	}
        }
              
        return root;
    }
}
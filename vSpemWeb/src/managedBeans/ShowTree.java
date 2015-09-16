package managedBeans;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import dominio.Document;

@ManagedBean(name="showtree")
public class ShowTree implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TreeNode treeAdaptado = null;
	
	
	public TreeNode getTreeAdaptado() {
		return treeAdaptado;
	}




	public void setTreeAdaptado(TreeNode treeAdaptado) {
		this.treeAdaptado = treeAdaptado;
	}




	public void newTreeAdaptado(TreeNode tree, TreeNode[] selecionados) {
		
		FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		SelectionView sv =(SelectionView) session.getAttribute("SelectionView");
		if (sv != null){
			System.out.println("Bean selectionview no es null");
			sv.displaySelectedMultiple(selecionados);
		}

		if (tree != null){
			
			treeAdaptado =  new CheckboxTreeNode(new Document(((Document)tree.getData()).getElementID(),((Document)tree.getData()).getName(),"-", "Folder"),null);
			treeAdaptado.setExpanded(true);
			List<TreeNode> nodosNivel2 = tree.getChildren();
			
			for (int temp = 0; temp < nodosNivel2.size(); temp++){
				TreeNode hijoNivel2 = nodosNivel2.get(temp);
				List<TreeNode> nodosNivel3 = hijoNivel2.getChildren();
				if(nodosNivel3.size()>0){
					for (int temp1 = 0;temp1 < nodosNivel3.size(); temp1++){
						TreeNode hijoNivel3 = nodosNivel3.get(temp1);
						
						 if (hijoNivel3.isSelected()){
							 TreeNode nuevoHijoNivel3 = new CheckboxTreeNode(new Document(((Document)hijoNivel3.getData()).getElementID(),((Document)hijoNivel3.getData()).getName(),"-", "Folder"),treeAdaptado);
							 nuevoHijoNivel3.setExpanded(true);
							}
					}
				} else {
					TreeNode nuevoHijoNivel2 = new CheckboxTreeNode(new Document(((Document)hijoNivel2.getData()).getElementID(),((Document)hijoNivel2.getData()).getName(),"-", "Folder"),treeAdaptado);
					nuevoHijoNivel2.setExpanded(true);	
				}
			 }
		  }
		  VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
		  vb.setTreeAdaptado(treeAdaptado);
		
	}
}



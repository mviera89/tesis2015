package managedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import logica.XMIParser;

import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import dominio.Struct;

@ManagedBean(name="showtree")
public class ShowTree implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TreeNode treeAdaptado;
	
	
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
			System.out.println("Bean selectionview es null");
			sv.displaySelectedMultiple(selecionados);
		}
		treeAdaptado = tree;
		if (!(treeAdaptado == null)){
		System.out.println("Hijos del nodo ################" + treeAdaptado.getChildCount());
		List<TreeNode> nodosNivel2 = treeAdaptado.getChildren();
		
		for (int temp = 0; temp < nodosNivel2.size(); temp++){
			TreeNode hijoNivel2 = nodosNivel2.get(temp);
			TreeNode padreNivel1 = hijoNivel2.getParent();
			List<TreeNode> nodosNivel3 = hijoNivel2.getChildren();
			System.out.println("Hijos del nodo nivel 2 ################" + nodosNivel3.size());
			for (int temp1 = 0;temp1 < nodosNivel3.size(); temp1++){
				TreeNode hijoNivel3 = nodosNivel3.get(temp1);
				TreeNode padreNivel2 = hijoNivel3.getParent();
				System.out.println("Hijo 3 ################" + hijoNivel3.toString());
				System.out.println("Hijo 3 ################" + hijoNivel3.isSelected());
				//if (hijoNivel3.isSelectable()){
				 if (hijoNivel3.isSelected()){
					 hijoNivel3.setParent(padreNivel1);
					 System.out.println("Padre es:" + hijoNivel3.getParent().toString());
					 System.out.println("ESta seleccionadoooooo" + hijoNivel3.toString());
					}
				//}
			}
		}
			
	}
		VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
		vb.setTreeAdaptado(treeAdaptado);
		
	}
}



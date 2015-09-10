package managedBeans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import logica.XMIParser;

import org.primefaces.model.TreeNode;

import dominio.Struct;
 
@ManagedBean(name="treeSelectionView")
@ViewScoped
public class SelectionView implements Serializable {
    
	private static final long serialVersionUID = 1L;
	private TreeNode root1;
    private TreeNode root2;
    private TreeNode root3;
    private TreeNode selectedNode;
    private TreeNode[] selectedNodes1;
    private TreeNode[] selectedNodes2;
     
    @ManagedProperty("#{documentService}")
    private DocumentService service;
     
    @PostConstruct
    public void init() {
    	String nomFile = "C:\\model.xmi"; 
    	List<Struct> nodos = XMIParser.getElementXMI(nomFile);
        root3 = service.createTree(nodos);
    }
 
    public TreeNode getRoot1() {
        return root1;
    }
 
    public TreeNode getRoot2() {
        return root2;
    }
 
    public TreeNode getRoot3() {
        return root3;
    }
 
    public TreeNode getSelectedNode() {
        return selectedNode;
    }
 
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }
 
    public TreeNode[] getSelectedNodes1() {
        return selectedNodes1;
    }
 
    public void setSelectedNodes1(TreeNode[] selectedNodes1) {
        this.selectedNodes1 = selectedNodes1;
    }
 
    public TreeNode[] getSelectedNodes2() {
        return selectedNodes2;
    }
 
    public void setSelectedNodes2(TreeNode[] selectedNodes2) {
        this.selectedNodes2 = selectedNodes2;
    }
 
    public void setService(DocumentService service) {
        this.service = service;
    }
 
    public void displaySelectedSingle() {
        if(selectedNode != null) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", selectedNode.getData().toString());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
     
    public void displaySelectedMultiple(TreeNode[] nodes) {
        if(nodes != null && nodes.length > 0) {
            StringBuilder builder = new StringBuilder();
 
            for(TreeNode node : nodes) {
                builder.append(node.getData().toString());
                builder.append("<br />");
            }
 
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", builder.toString());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
}
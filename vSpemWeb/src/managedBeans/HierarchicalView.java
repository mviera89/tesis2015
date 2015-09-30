package managedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.FlowChartConnector;
import org.primefaces.model.diagram.endpoint.BlankEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.overlay.ArrowOverlay;

import config.Constantes;
import dominio.Struct;
import dominio.Variant;
import logica.XMIParser;
 
@ManagedBean(name = "diagramHierarchicalView")
@ViewScoped
public class HierarchicalView implements Serializable {
     
    private DefaultDiagramModel model;
	private List<Variant> variantes = new ArrayList<Variant>();
	private List<Struct> nodos = new ArrayList<Struct>();
	private Variant[] variantesSeleccionadas = null;
	
	public void redibujarModelo() {
    	FacesContext fc = FacesContext.getCurrentInstance();
        String nombre =  fc.getExternalContext().getRequestParameterMap().get("name"); 
		
        List<Element> elements = model.getElements();
        Iterator<Element> iterator = elements.iterator();
        while (iterator.hasNext()){
			Element element = (Element) iterator.next();
			NetworkElement data = (NetworkElement) element.getData();
			if (data.getType().equals("vpActivity") && data.getName().equals(nombre)){
				cargarVariantesDelPunto(nombre);
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('variantesDialog').show();");
			}
		}
    }
    
	public void cargarVariantesDelPunto(String nombrePV){
		variantes.clear();
		Struct s = null;
		boolean fin = false;
		
		Iterator<Struct> it = this.nodos.iterator();
        while (it.hasNext() && !fin){
        	s = it.next();
        	fin = (s.getType().equals("vpActivity") && s.getNombre().equals(nombrePV));
        }
        if (s != null){
	        Iterator<Variant> it1 = s.getHijos().iterator();
	    	while (it1.hasNext()){
	    		Variant v = it1.next();
	    		variantes.add(v);
	    	}
        }
	}
	
    @PostConstruct
    public void init() {
        model = new DefaultDiagramModel();
        model.setMaxConnections(-1);
        createModel();
    }
    
    public String obtenerIcono(String tipo){
    	String icono = (tipo.equals("Activity"))    ? "activity.png"   :
    				   (tipo.equals("vpActivity"))  ? "vpActivity.png" :
    				   //(tipo.equals("VarActivity")) ? "vActivity.png"  : 
    				   "";
    	return icono;
    }
    
    public void createModel(){
    	FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
		if ((vb != null) && (!vb.getNombreArchivo().isEmpty())){
			String nomFile = Constantes.destinoDescargas + vb.getNombreArchivo();
		   	this.nodos = XMIParser.getElementXMI(nomFile);
		   	
		   	/*** Creo el modelo con los nodos obtenidos del archivo ***/
	    	FlowChartConnector connector = new FlowChartConnector();
	        connector.setPaintStyle("{strokeStyle:'#404a4e', lineWidth:2}");
	        connector.setHoverPaintStyle("{strokeStyle:'#20282b'}");
	        connector.setAlwaysRespectStubs(true);
	        model.setDefaultConnector(connector);
	        
	        Element root = new Element(new NetworkElement("Inicio", "processPackage.png", ""), "30em", "0em");
	        EndPoint endPointR = createEndPoint(EndPointAnchor.BOTTOM);
	        root.addEndPoint(endPointR);
        	model.addElement(root);
	        
        	int i = 0;
	        Iterator<Struct> it = this.nodos.iterator();
	        while (it.hasNext()){
	        	i++;
	        	Struct s = it.next();
				
	        	Element padre = new Element(new NetworkElement(s.getNombre(), obtenerIcono(s.getType()), s.getType()), 15 * i + "em", "10em");
		        EndPoint endPointP1_T = createEndPoint(EndPointAnchor.TOP);
		        EndPoint endPointP2_B = createEndPoint(EndPointAnchor.BOTTOM);
		        padre.addEndPoint(endPointP1_T);
		        padre.addEndPoint(endPointP2_B);
		        model.addElement(padre);
		        model.connect(createConnection(endPointR, endPointP1_T));
		        
		       /* // Variantes
		        int j = 0;
		        Iterator<Variant> it1 = s.getHijos().iterator();
	        	while (it1.hasNext()){
	        		j++;
	        		Variant v = it1.next();
	        		Element hijo = new Element(new NetworkElement(v.getName(), "vActivity.png", "VarActivity"), 10 * j + "em", "20em");
	        		EndPoint endPointH1 = createEndPoint(EndPointAnchor.TOP);
	        		hijo.addEndPoint(endPointH1);
			        model.addElement(hijo);
			        model.connect(createConnection(endPointP2_B, endPointH1));
	        	}*/
	        }
		}
    }
    
    public DiagramModel getModel() {
        return model;
    }

    public void setModel(DefaultDiagramModel model) {
		this.model = model;
	}

    public List<Variant> getVariantes() {
		return variantes;
	}

	public void setVariantes(List<Variant> variantes) {
		this.variantes = variantes;
	}

	public List<Struct> getNodos() {
		return nodos;
	}

	public void setNodos(List<Struct> nodos) {
		this.nodos = nodos;
	}
    
	public Variant[] getVariantesSeleccionadas() {
		return variantesSeleccionadas;
	}

	public void setVariantesSeleccionadas(Variant[] variantesSeleccionadas) {
		this.variantesSeleccionadas = variantesSeleccionadas;
	}

    private EndPoint createEndPoint(EndPointAnchor anchor) {
    	BlankEndPoint endPoint = new BlankEndPoint(anchor);
        return endPoint;
    }
    
    private Connection createConnection(EndPoint from, EndPoint to) {
        Connection conn = new Connection(from, to);
        conn.getOverlays().add(new ArrowOverlay(8, 8, 1, 1));
        return conn;
    }
     
    public class NetworkElement implements Serializable {
         
        private String name;
        private String image;
        private String type;

		public NetworkElement() {
        }
 
        public NetworkElement(String name, String image, String type) {
            this.name = name;
            this.image = image;
            this.type = type;
        }
 
        public String getName() {
            return name;
        }
 
        public void setName(String name) {
            this.name = name;
        }
 
        public String getImage() {
            return image;
        }
 
        public void setImage(String image) {
            this.image = image;
        }
 
        public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
 
        @Override
        public String toString() {
            return name;
        }
         
    }
}
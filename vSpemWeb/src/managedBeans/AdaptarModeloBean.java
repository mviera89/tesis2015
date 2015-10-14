package managedBeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
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
import dataTypes.TipoElemento;
import dominio.ElementoModelo;
import dominio.Struct;
import dominio.Variant;
import logica.XMIParser;
 
@ManagedBean
@SessionScoped
public class AdaptarModeloBean {
     
    private DefaultDiagramModel modelo;
	private int y;
	private List<Struct> nodos;
	private EndPoint endPointRoot;
	private Element puntoVariacionAdaptado;
	private List<SelectItem> variantes;
	private String[] variantesSeleccionadas;
	private HashMap<String, String[]> puntosDeVariacion; // <Id del PV, Lista de Variantes elegidas>

	@PostConstruct
    public void init() {
    	//System.out.println("### AdaptarModeloBean - init() ###");
    	nodos = new ArrayList<Struct>();
    	variantes = new ArrayList<SelectItem>();
    	variantesSeleccionadas = null;
    	this.puntosDeVariacion = new HashMap<String, String[]>();
    	this.y = Constantes.yInicial;
        crearModelo();
    }

    /*** Getters y Setters ***/
    
    public DiagramModel getModelo() {
        return modelo;
    }

    public void setModelo(DefaultDiagramModel modelo) {
		this.modelo = modelo;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public List<Struct> getNodos() {
		return nodos;
	}

	public void setNodos(List<Struct> nodos) {
		this.nodos = nodos;
	}
	
	public EndPoint getEndPointRoot() {
		return endPointRoot;
	}

	public void setEndPointRoot(EndPoint endPointRoot) {
		this.endPointRoot = endPointRoot;
	}

	public Element getPuntoVariacionAdaptado() {
		return puntoVariacionAdaptado;
	}

	public void setPuntoVariacionAdaptado(Element puntoVariacionAdaptado) {
		this.puntoVariacionAdaptado = puntoVariacionAdaptado;
	}

    public List<SelectItem> getVariantes() {
		return variantes;
	}

	public void setVariantes(List<SelectItem> variantes) {
		this.variantes = variantes;
	}
    
	public String[] getVariantesSeleccionadas() {
		return variantesSeleccionadas;
	}

	public void setVariantesSeleccionadas(String[] variantesSeleccionadas) {
		String clave = ((ElementoModelo) this.puntoVariacionAdaptado.getData()).getId();
		String[] variantesParaPV = this.puntosDeVariacion.get(clave);
		if (variantesParaPV != null){
			eliminarVariantesSeleccionadas(variantesParaPV);
		}
		this.variantesSeleccionadas = variantesSeleccionadas;
		actualizarVariantesParaPV();
		this.redibujarModelo(); 
	}

    public HashMap<String, String[]> getPuntosDeVariacion() {
		return puntosDeVariacion;
	}

	public void setPuntosDeVariacion(HashMap<String, String[]> puntosDeVariacion) {
		this.puntosDeVariacion = puntosDeVariacion;
	}
	
	/*************************/
	
    public void crearModelo(){
        modelo = new DefaultDiagramModel();
        modelo.setMaxConnections(-1);
    	FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
		
		if ((vb != null) && (!vb.getNombreArchivo().isEmpty())){
			String nomArchivo = Constantes.destinoDescargas + vb.getNombreArchivo();
		   	this.nodos = XMIParser.getElementXMI(nomArchivo);
		   	
		   	/*** Creo el modelo con los nodos obtenidos del archivo ***/
		   	
	    	FlowChartConnector conector = new FlowChartConnector();
	    	conector.setPaintStyle("{strokeStyle:'#404a4e', lineWidth:2}");
	    	conector.setHoverPaintStyle("{strokeStyle:'#20282b'}");
	    	conector.setAlwaysRespectStubs(true);
	        modelo.setDefaultConnector(conector);
	        
	        Element root = new Element(new ElementoModelo("", "Inicio", obtenerIconoPorTipo(TipoElemento.PROCESS_PACKAGE), TipoElemento.PROCESS_PACKAGE));
	        root.setY(this.y + "em");
	        endPointRoot = crearEndPoint(EndPointAnchor.BOTTOM);
	        root.addEndPoint(endPointRoot);
        	modelo.addElement(root);
	        
        	this.y += Constantes.distanciaEntreNiveles;
        	int x = 0;
	        Iterator<Struct> it = this.nodos.iterator();
	        while (it.hasNext()){
	        	Struct s = it.next();
				
	        	Element padre = new Element(new ElementoModelo(s.getElementID(), s.getNombre(), obtenerIconoPorTipo(s.getType()), s.getType()), x + "em", this.y + "em");
		        EndPoint endPointP1_T = crearEndPoint(EndPointAnchor.TOP);
		        padre.addEndPoint(endPointP1_T);
		        modelo.addElement(padre);
		        modelo.connect(crearConexion(endPointRoot, endPointP1_T));
	        	x += 2 * s.getNombre().length() / 3;
	        }
	        root.setX(x/2 + "em");
	        this.y += Constantes.distanciaEntreNiveles;
		}
    }
    
    public String obtenerIconoPorTipo(TipoElemento tipo){
    	String icono = (tipo == TipoElemento.PROCESS_PACKAGE) ? TipoElemento.PROCESS_PACKAGE.getImagen() :
    				   (tipo == TipoElemento.ACTIVITY)	      ? TipoElemento.ACTIVITY.getImagen()		 :
    				   (tipo == TipoElemento.VP_ACTIVITY)     ? TipoElemento.VP_ACTIVITY.getImagen()	 :
    				   (tipo == TipoElemento.VAR_ACTIVITY)    ? TipoElemento.VAR_ACTIVITY.getImagen()	 :
    				   (tipo == TipoElemento.TASK)     	      ? TipoElemento.TASK.getImagen()	 		 :
    				   (tipo == TipoElemento.VP_TASK)     	  ? TipoElemento.VP_TASK.getImagen()	 	 :
    				   (tipo == TipoElemento.VAR_TASK)        ? TipoElemento.VAR_TASK.getImagen()		 :
    				   (tipo == TipoElemento.ITERATION)    	  ? TipoElemento.ITERATION.getImagen()	   	 :
   		   			   (tipo == TipoElemento.VP_ITERATION)    ? TipoElemento.VP_ITERATION.getImagen()	 :
   		   			   (tipo == TipoElemento.VAR_ITERATION)   ? TipoElemento.VAR_ITERATION.getImagen()   :
   		   			   (tipo == TipoElemento.PHASE)    		  ? TipoElemento.PHASE.getImagen()		     :
   		   			   (tipo == TipoElemento.VP_PHASE)   	  ? TipoElemento.VP_PHASE.getImagen()		 :
   		   			   (tipo == TipoElemento.VAR_PHASE)	      ? TipoElemento.VAR_PHASE.getImagen()	     :
    				   "";
    	return icono;
    }

    private EndPoint crearEndPoint(EndPointAnchor anchor) {
    	BlankEndPoint endPoint = new BlankEndPoint(anchor);
        return endPoint;
    }
    
    private Connection crearConexion(EndPoint desde, EndPoint hasta) {
        Connection con = new Connection(desde, hasta);
        con.getOverlays().add(new ArrowOverlay(8, 8, 1, 1));
        return con;
    }

	public Element obtenerElemento(String idElemento){
		Iterator<Element> it = modelo.getElements().iterator();
		while (it.hasNext()){
			Element e = it.next();
			String id = ((ElementoModelo) e.getData()).getId();
			if (id.equals(idElemento)){
				return e;
			}
		}
		
		return null;
	}
	
	public void seleccionarVariantes(){
		FacesContext fc = FacesContext.getCurrentInstance();
        String idElemSeleccionado =  fc.getExternalContext().getRequestParameterMap().get("elemSeleccionado"); 
		
        Element elemento = obtenerElemento(idElemSeleccionado);
        ElementoModelo e = (ElementoModelo) elemento.getData();
        if (e.getType() == TipoElemento.VP_ACTIVITY 
        		|| e.getType() == TipoElemento.VP_TASK
        		|| e.getType() == TipoElemento.VP_PHASE
        		|| e.getType() == TipoElemento.VP_ITERATION){
			puntoVariacionAdaptado = elemento;
			cargarVariantesDelPunto(idElemSeleccionado);
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('variantesDialog').show()");
		}
	}
    
	public void cargarVariantesDelPunto(String idElemSeleccionado){
		variantes.clear();
		Struct s = null;
		boolean fin = false;
		
		Iterator<Struct> iterator = this.nodos.iterator();
        while (iterator.hasNext() && !fin){
        	s = iterator.next();
        	fin = ((s.getType() == TipoElemento.VP_ACTIVITY 
        			|| s.getType() == TipoElemento.VP_TASK
        			|| s.getType() == TipoElemento.VP_PHASE
        			|| s.getType() == TipoElemento.VP_ITERATION)
        			&& (s.getElementID().equals(idElemSeleccionado)));
        }
        if (fin){
	        Iterator<Variant> it = s.getHijos().iterator();
	    	while (it.hasNext()){
	    		Variant v = it.next();
	    		variantes.add(new SelectItem(v.getID(), v.getName()));
	    	}
        }
	}

	public void eliminarVariantesSeleccionadas(String[] variantesParaPV){
    	int i = 0;
    	int cantVariantes = variantesParaPV.length;
		for (i = 0; i < cantVariantes; i++){
			Element e = obtenerElemento(variantesParaPV[i]);
			if (e != null){
				modelo.removeElement(e);
			}
    	}
	}
	
	public void actualizarVariantesParaPV(){
		String clave = ((ElementoModelo) this.puntoVariacionAdaptado.getData()).getId();
		this.puntosDeVariacion.put(clave, this.variantesSeleccionadas);
	}
	
	public void redibujarModelo() {
    	int cantVariantes = this.variantesSeleccionadas.length;
    	String xStr = this.puntoVariacionAdaptado.getX();
		int xIni = Integer.valueOf(xStr.substring(0, xStr.length() - 2));
		int x = (cantVariantes > 1) ? xIni - (xIni / cantVariantes) : xIni;
    	for (int i = 0; i < cantVariantes; i++){
    		// Creo la variante
    		String nombreVariante = "";
    		Iterator<SelectItem> it = this.variantes.iterator();
    		while (it.hasNext() && nombreVariante.equals("")){
    			SelectItem si = it.next();
    			if (si.getValue().equals(this.variantesSeleccionadas[i])){
    				nombreVariante = (String) si.getLabel();
    			}
    		}
			Element hijo = new Element(new ElementoModelo(this.variantesSeleccionadas[i], nombreVariante, obtenerIconoPorTipo(TipoElemento.VAR_ACTIVITY), TipoElemento.VAR_ACTIVITY), x + "em", this.y + "em");
    		EndPoint endPointH1 = crearEndPoint(EndPointAnchor.TOP);
    		hijo.addEndPoint(endPointH1);
	        modelo.addElement(hijo);
	        
	        // Creo el endPoint del punto de variación
	        EndPoint endPointPV_B = crearEndPoint(EndPointAnchor.BOTTOM);
	        this.puntoVariacionAdaptado.addEndPoint(endPointPV_B);
	        
	        // Conecto el punto de variación con la variante
	        modelo.connect(crearConexion(endPointPV_B, endPointH1));
        	
	        x += 2 * nombreVariante.length() / 3;
    	}
    }
	
}
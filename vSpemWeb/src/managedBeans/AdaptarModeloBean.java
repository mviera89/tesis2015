package managedBeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
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
import dominio.Struct;
import dominio.Variant;
import logica.XMIParser;
 
@ManagedBean
@SessionScoped
public class AdaptarModeloBean {
     
    private DefaultDiagramModel modelo;
	private int y;
	private List<Struct> nodos;
	private Element puntoVariacionAdaptado;
	private List<SelectItem> variantes;
	private String[] variantesSeleccionadas;
	private HashMap<String, String[]> puntosDeVariacion; // <Id del PV, Lista de Variantes elegidas>

	@PostConstruct
    public void init() {
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
		Struct pv = ((Struct) this.puntoVariacionAdaptado.getData());
		String error = this.validarSeleccion(variantesSeleccionadas.length, pv.getMin(), pv.getMax());
		if (error.isEmpty()){
			String clave = pv.getElementID();
			String[] variantesParaPV = this.puntosDeVariacion.get(clave);
			if (variantesParaPV != null){
				eliminarVariantesSeleccionadas(variantesParaPV);
			}
			this.variantesSeleccionadas = variantesSeleccionadas;
			actualizarVariantesParaPV();
			this.redibujarModelo();
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('variantesDialog').hide()");
			context.update("panel_principal");
		}
		else{
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, error, "");
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}
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
	        
	        Element root = new Element(new Struct("", "Inicio", TipoElemento.PROCESS_PACKAGE, Constantes.min_default, Constantes.max_default, XMIParser.obtenerIconoPorTipo(TipoElemento.PROCESS_PACKAGE)));
	        root.setY(this.y + "em");
	        EndPoint endPointRoot = crearEndPoint(EndPointAnchor.BOTTOM);
	        root.addEndPoint(endPointRoot);
        	modelo.addElement(root);
	        
        	this.y += Constantes.distanciaEntreNiveles;
        	float x = 0;
	        Iterator<Struct> it = this.nodos.iterator();
	        while (it.hasNext()){
	        	Struct s = it.next();
				
	        	Element padre = new Element(new Struct(s.getElementID(), s.getNombre(), s.getType(), s.getMin(), s.getMax(), s.getImagen()), x + "em", this.y + "em");
		        EndPoint endPointP1_T = crearEndPoint(EndPointAnchor.TOP);
		        padre.addEndPoint(endPointP1_T);
		        modelo.addElement(padre);
		        modelo.connect(crearConexion(endPointRoot, endPointP1_T));
	        	x += s.getNombre().length() / 2.0 + Constantes.distanciaEntreElemsMismoNivel;
	        }
	        root.setX(x/2 + "em");
	        this.y += Constantes.distanciaEntreNiveles;
		}
    }

/*public TipoElemento getTipoElemento(String t){
		
	TipoElemento type = (t.equals(TipoElemento.PROCESS_PACKAGE.toString())) ? TipoElemento.PROCESS_PACKAGE :
   		(t.equals(TipoElemento.ACTIVITY.toString()))	    ? TipoElemento.ACTIVITY		   :
		 	(t.equals(TipoElemento.VP_ACTIVITY.toString()))     ? TipoElemento.VP_ACTIVITY	   :
	 		(t.equals(TipoElemento.VAR_ACTIVITY.toString()))    ? TipoElemento.VAR_ACTIVITY	   :
	 		(t.equals(TipoElemento.TASK.toString()))    		? TipoElemento.TASK			   :
	 		(t.equals(TipoElemento.VP_TASK.toString()))    		? TipoElemento.VP_TASK		   :
	 		(t.equals(TipoElemento.VAR_TASK.toString()))    	? TipoElemento.VAR_TASK		   :
	 		(t.equals(TipoElemento.ITERATION.toString()))    	? TipoElemento.ITERATION	   :
	 		(t.equals(TipoElemento.VP_ITERATION.toString()))    ? TipoElemento.VP_ITERATION	   :
	 		(t.equals(TipoElemento.VAR_ITERATION.toString()))   ? TipoElemento.VAR_ITERATION   :
	 		(t.equals(TipoElemento.PHASE.toString()))    		? TipoElemento.PHASE		   :
	 		(t.equals(TipoElemento.VP_PHASE.toString()))    	? TipoElemento.VP_PHASE		   :
	 		(t.equals(TipoElemento.VAR_PHASE.toString()))  		? TipoElemento.VAR_PHASE	   :
	 		

			null;
return type;
	}*/

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
			String id = ((Struct) e.getData()).getElementID();
			if (id.equals(idElemento)){
				return e;
			}
		}
		
		return null;
	}

	public void seleccionarVariantes(){
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext c = fc.getExternalContext();
        String idElemSeleccionado =  c.getRequestParameterMap().get("elemSeleccionado");
		
		if (idElemSeleccionado != null){
	        Element elemento = obtenerElemento(idElemSeleccionado);
	        Struct s = (Struct) elemento.getData();
	        if (s.getEsPV()){
				puntoVariacionAdaptado = elemento;
				cargarVariantesDelPunto(idElemSeleccionado);
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('variantesDialog').show()");
			}
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
	        Iterator<Variant> it = s.getVariantes().iterator();
	    	while (it.hasNext()){
	    		Variant v = it.next();
	    		variantes.add(new SelectItem(v.getID(), v.getName(),v.getVarType()));
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
		String clave = ((Struct) this.puntoVariacionAdaptado.getData()).getElementID();
		this.puntosDeVariacion.put(clave, this.variantesSeleccionadas);
	}
	
	public void redibujarModelo() {
    	int cantVariantes = this.variantesSeleccionadas.length;
    	String xStr = this.puntoVariacionAdaptado.getX();
		float xIni = Float.valueOf(xStr.substring(0, xStr.length() - 2));
		float x = (cantVariantes > 1) ? xIni - (xIni / cantVariantes) : xIni;
    	for (int i = 0; i < cantVariantes; i++){
    		// Creo la variante
    		String nombreVariante = "";
    		String tipoVariante = "";
    		Iterator<SelectItem> it = this.variantes.iterator();
    		while (it.hasNext() && nombreVariante.equals("") && tipoVariante.equals("")){
    			SelectItem si = it.next();
    			if (si.getValue().equals(this.variantesSeleccionadas[i])){
    				nombreVariante = (String) si.getLabel();
    				tipoVariante = si.getDescription();
    			}
    		}
    		TipoElemento tipo = XMIParser.obtenerTipoElemento(tipoVariante);
			Element hijo = new Element(new Struct(this.variantesSeleccionadas[i], nombreVariante, tipo, Constantes.min_default, Constantes.max_default, XMIParser.obtenerIconoPorTipo(tipo)), x + "em", this.y + "em");
    		EndPoint endPointH1 = crearEndPoint(EndPointAnchor.TOP);
    		hijo.addEndPoint(endPointH1);
	        modelo.addElement(hijo);
	        
	        // Creo el endPoint del punto de variación
	        EndPoint endPointPV_B = crearEndPoint(EndPointAnchor.BOTTOM);
	        this.puntoVariacionAdaptado.addEndPoint(endPointPV_B);
	        
	        // Conecto el punto de variación con la variante
	        modelo.connect(crearConexion(endPointPV_B, endPointH1));
        	
	        x +=  nombreVariante.length() / 2.0 + Constantes.distanciaEntreElemsMismoNivel;
    	}
    }
	
	public String validarSeleccion(int cantVariantesSelec, int min, int max){
		String res = "";
		if (cantVariantesSelec < min){
			res = "Debe seleccionar al menos " + min + " variante" + (min > 1 ? "s." : ".");
		}
		else if ((max != -1) && (cantVariantesSelec > max)){
			res = "Debe seleccionar a lo sumo " + max + " variante" + (max > 1 ? "s." : ".");
		}
		return res;
	}
	
}
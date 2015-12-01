package managedBeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
import dataTypes.TipoEtiqueta;
import dataTypes.TipoRolesTareas;
import dominio.Struct;
import dominio.Variant;
import logica.XMIParser;

@ManagedBean
@SessionScoped
public class AdaptarModeloBean {
    
    private DefaultDiagramModel modelo;
	private DefaultDiagramModel modeloAdaptado;
	private DefaultDiagramModel modeloRolesTareas;
	private int y;
	private List<Struct> nodos;
	private Element puntoVariacionAdaptado;
	private List<SelectItem> variantes;
	private String[] variantesSeleccionadas;

	private HashMap<String, String[]> puntosDeVariacion; // <Id del PV, Lista de Variantes elegidas>
	private HashMap<String, String> restriccionesPV;	 // <Id del PV, Booleano que indica si cumple restricciones del PV>
	private List<String[]> erroresModeloFinal; 			 // Lista de parejas de string {[Nombre del PV, Texto del error]}
	private HashMap<String, List<Struct>> rolesTareasPrimary;
	private HashMap<String, List<Struct>> rolesTareasAdditionally;


	/**************************/
	
	private List<TipoRolesTareas> rolesTareas;
	public void Prueba(){
		rolesTareas = new ArrayList<TipoRolesTareas>();
		
		Element r1 = new Element(new Struct("R1", "Rol 1", TipoElemento.ROLE, -1, -1, XMIParser.obtenerIconoPorTipo(TipoElemento.ROLE)));
		Element e1 = new Element(new Struct("S1", "Struct 1", TipoElemento.TASK, -1, -1, XMIParser.obtenerIconoPorTipo(TipoElemento.TASK)), "10em", "0em");
		Element e2 = new Element(new Struct("S2", "Struct 2", TipoElemento.TASK, -1, -1, XMIParser.obtenerIconoPorTipo(TipoElemento.TASK)), "20em", "0em");
		Element e3 = new Element(new Struct("S3", "Struct 3", TipoElemento.TASK, -1, -1, XMIParser.obtenerIconoPorTipo(TipoElemento.TASK)), "10em", "0em");
		Element e4 = new Element(new Struct("S4", "Struct 4", TipoElemento.TASK, -1, -1, XMIParser.obtenerIconoPorTipo(TipoElemento.TASK)), "20em", "0em");
		
		DefaultDiagramModel primary1 = new DefaultDiagramModel();
		DefaultDiagramModel additionally1 = new DefaultDiagramModel();
		
		primary1.addElement(r1);
		primary1.addElement(e1);
		primary1.addElement(e2);
		additionally1.addElement(r1);
		additionally1.addElement(e3);
		additionally1.addElement(e4);
		
		TipoRolesTareas trt1 = new TipoRolesTareas();
		trt1.setRol((Struct) r1.getData());
		trt1.setPrimary(primary1);
		trt1.setAdditionally(additionally1);
		
		/*** Segundo rol con las tareas asignadas ***/
		
		Element r2 = new Element(new Struct("R2", "Rol 2", TipoElemento.ROLE, -1, -1, XMIParser.obtenerIconoPorTipo(TipoElemento.ROLE)));
		Element e5 = new Element(new Struct("S5", "Struct 5", TipoElemento.TASK, -1, -1, XMIParser.obtenerIconoPorTipo(TipoElemento.TASK)), "10em", "0em");
		Element e6 = new Element(new Struct("S6", "Struct 6", TipoElemento.TASK, -1, -1, XMIParser.obtenerIconoPorTipo(TipoElemento.TASK)), "20em", "0em");
		Element e7 = new Element(new Struct("S7", "Struct 7", TipoElemento.TASK, -1, -1, XMIParser.obtenerIconoPorTipo(TipoElemento.TASK)), "10em", "0em");
		Element e8 = new Element(new Struct("S8", "Struct 8", TipoElemento.TASK, -1, -1, XMIParser.obtenerIconoPorTipo(TipoElemento.TASK)), "20em", "0em");
		
		DefaultDiagramModel primary2 = new DefaultDiagramModel();
		DefaultDiagramModel additionally2 = new DefaultDiagramModel();

		primary2.addElement(r2);
		primary2.addElement(e5);
		primary2.addElement(e6);
		additionally2.addElement(r2);
		additionally2.addElement(e7);
		additionally2.addElement(e8);
		
		TipoRolesTareas trt2 = new TipoRolesTareas();
		trt2.setRol((Struct) r2.getData());
		trt2.setPrimary(primary2);
		trt2.setAdditionally(additionally2);
		
		/*** Agrego los hash a la lista ***/
		rolesTareas.add(trt1);
		rolesTareas.add(trt2);
		
	}
	public List<TipoRolesTareas> getRolesTareas() {
        return rolesTareas;
    }
	/**************************/
	
	@PostConstruct
    public void init() {
		this.Prueba(); /*********************/
    	nodos = new ArrayList<Struct>();
    	variantes = new ArrayList<SelectItem>();
    	variantesSeleccionadas = null;
    	this.puntosDeVariacion = new HashMap<String, String[]>();

    	this.rolesTareasPrimary = new HashMap<String, List<Struct>>();
    	this.rolesTareasAdditionally = new HashMap<String, List<Struct>>();
    	this.restriccionesPV = new HashMap<String, String>();
    	erroresModeloFinal = new ArrayList<String[]>();
    	this.y = Constantes.yInicial;
        crearModelo();
        crearModeloRolesTareas();
    }

    /*** Getters y Setters ***/

    public DiagramModel getModelo() {
        return modelo;
    }

    public void setModelo(DefaultDiagramModel modelo) {
		this.modelo = modelo;
	}

	public DefaultDiagramModel getModeloAdaptado() {
		return modeloAdaptado;
	}

	public void setModeloAdaptado(DefaultDiagramModel modelo) {
		this.modeloAdaptado = modelo;
	}

	public DefaultDiagramModel getModeloRolesTareas() {
		return modeloRolesTareas;
	}

	public void setModeloRolesTareas(DefaultDiagramModel modelo) {
		this.modeloRolesTareas = modelo;
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
		String error = this.validarSeleccion(variantesSeleccionadas, pv);
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

	public HashMap<String, String> getRestriccionesPV() {
		return restriccionesPV;
	}

	public void setRestriccionesPV(HashMap<String, String> restriccionesPV) {
		this.restriccionesPV = restriccionesPV;
	}

	public List<String[]> getErroresModeloFinal() {
		erroresModeloFinal.clear();
		Iterator<Entry<String, String>> it = this.restriccionesPV.entrySet().iterator();
		while (it.hasNext()){
			Entry<String, String> entry = it.next();
			String error = entry.getValue();
			if (!error.isEmpty()){
				String id = entry.getKey();
				Element e = obtenerElemento(id);
				if (e != null){
					String[] errorPV = {((Struct) e.getData()).getNombre(), error};
					erroresModeloFinal.add(errorPV);
				}
			}
		} 
		return erroresModeloFinal;
	}

	public void setErroresModeloFinal(List<String[]> erroresModeloFinal) {
		this.erroresModeloFinal = erroresModeloFinal;
	}

	/*** Crear modelo ***/

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
	        
	        // Busco el elemento raiz en nodos
	        Iterator<Struct> itn = nodos.iterator();
	        Struct raiz = null;
	        TipoElemento t = null;
	        while (itn.hasNext()){
	        	Struct s = itn.next();
	        	if(s.getType()!= null){
		        	if (s.getType().equals(TipoElemento.CAPABILITY_PATTERN) || s.getType().equals(TipoElemento.DELIVERY_PROCESS)){
		        		raiz = s;
		        		t = s.getType();
		        		itn.remove();
		        		
		        	}
	        	}
	        }
	        
	        // Si no encuentro el elemento raíz => Modelo inválido
	        if (raiz == null){
	        	// Si llegó acá, hay cargado un mensaje de que el archivo fue cargado correctamente, pero no quiero que se muestre.
	        	Iterator<FacesMessage> it = FacesContext.getCurrentInstance().getMessages();
	        	while ( it.hasNext() ) {
	        	    it.next();
	        	    it.remove();
	        	}
	        	// Cargo el mensaje de error
	        	FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, Constantes.MENSAJE_ARCHIVO_INCORRECTO, "");
		        FacesContext.getCurrentInstance().addMessage(null, message);
	        	return;
	        }
	        
	        Struct r = new Struct(raiz.getElementID(), raiz.getNombre(), t, Constantes.min_default, Constantes.max_default, XMIParser.obtenerIconoPorTipo(t));
	        r.setDescription(raiz.getDescription());
	        r.setPresentationName(raiz.getPresentationName());
	        Element root = new Element(r);
	        
	        root.setY(this.y + "em");
	        EndPoint endPointRoot = crearEndPoint(EndPointAnchor.BOTTOM);
	        root.addEndPoint(endPointRoot);
	        root.setDraggable(false);
        	modelo.addElement(root);
	        
        	this.y += Constantes.distanciaEntreNiveles;
        	float x = 0;
	        Iterator<Struct> it = this.nodos.iterator();
	        while (it.hasNext()){
	        	Struct s = it.next();
	        	TipoElemento tipo = s.getType();
	        	// Si es un punto de variación => Lo agrego al hash restriccionesPV
	        	if (esPuntoDeVariacion(tipo)){
	        		this.restriccionesPV.put(s.getElementID(), validarSeleccion(null, s));
	        	}
	        	
	        	// Este modelo NO muestra roles ni workproducts
				if ((tipo != TipoElemento.ROLE) && (tipo != TipoElemento.VP_ROLE) && (tipo != TipoElemento.WORK_PRODUCT) && (tipo != TipoElemento.VP_WORK_PRODUCT)){ 
		        	Element padre = new Element(s, x + "em", this.y + "em");
			        EndPoint endPointP1_T = crearEndPoint(EndPointAnchor.TOP);
			        padre.addEndPoint(endPointP1_T);
			        padre.setDraggable(false);
			        modelo.addElement(padre); 
			        modelo.connect(crearConexion(endPointRoot, endPointP1_T));
			        String etiqueta = obtenerEtiquetaParaModelo(r, s);
			        s.setEtiqueta(etiqueta);
		        	x += s.getNombre().length() / 2.0 + Constantes.distanciaEntreElemsMismoNivel;
		        	if (tipo == TipoElemento.TASK || tipo == TipoElemento.VP_TASK){
		        		if (!s.getPerformedPrimaryBy().equals("")){
		        			if (rolesTareasPrimary.containsKey(s.getPerformedPrimaryBy())){
		        				rolesTareasPrimary.get(s.getPerformedPrimaryBy()).add(s);
		        			}
		        			else{
		        				List<Struct> list = new ArrayList<Struct>();
		        				list.add(s);
		        				rolesTareasPrimary.put(s.getPerformedPrimaryBy(), list);
		        			}
		        		}
		        		if (s.getPerformedAditionallyBy() != null){
		        			Iterator<String> it1 = s.getPerformedAditionallyBy().iterator();
		        			while(it1.hasNext()){
		        				String rol = it1.next();
			        			if (rolesTareasAdditionally.containsKey(rol)){
			        				rolesTareasAdditionally.get(rol).add(s);
			        			}
			        			else{
			        				List<Struct> list = new ArrayList<Struct>();
			        				list.add(s);
			        				rolesTareasAdditionally.put(rol, list);
			        			}
		        			}
		        		}
		        	}
				}
	        }
	        root.setX(x/2 + "em");
	        this.y += Constantes.distanciaEntreNiveles;
		}
		
		if (modelo.getElements().size() > 0){
    		this.crearModeloFinal(this.modelo);
    	}
    }

    public static EndPoint crearEndPoint(EndPointAnchor anchor) {
    	BlankEndPoint endPoint = new BlankEndPoint(anchor);
        return endPoint;
    }

    public static Connection crearConexion(EndPoint desde, EndPoint hasta) {
        Connection con = new Connection(desde, hasta);
        con.getOverlays().add(new ArrowOverlay(8, 8, 1, 1));
        return con;
    }

	public void redibujarModelo() {
		try{
	    	int cantVariantes = this.variantesSeleccionadas.length;
	    	String xStr = this.puntoVariacionAdaptado.getX();
	    	String yStr = this.puntoVariacionAdaptado.getY();
			float xIni = Float.valueOf(xStr.substring(0, xStr.length() - 2));
			float x = (cantVariantes > 1) ? xIni - (xIni / cantVariantes) : xIni;
			float y = Float.valueOf(yStr.substring(0, yStr.length() - 2)) + Constantes.distanciaEntreNiveles;
	    	for (int i = 0; i < cantVariantes; i++){
	    		// Creo la variante
	
	    		Variant v = buscarVariante(nodos, this.variantesSeleccionadas[i]);
	    		String nombreVariante = v.getName();
				String tipoVariante = v.getVarType();
	    		String idVariante = this.variantesSeleccionadas[i];
	    		List<Struct> hijos = v.getHijos();
	    		
	    		TipoElemento tipo = XMIParser.obtenerTipoElemento(tipoVariante);
	    		String iconoVariante = XMIParser.obtenerIconoPorTipo(tipo);
				Element hijo = new Element(new Struct(idVariante, nombreVariante, tipo, Constantes.min_default, Constantes.max_default, iconoVariante), x + "em", y + "em");
				Struct s = (Struct) hijo.getData();
				s.setHijos(hijos);
	    		EndPoint endPointH1 = crearEndPoint(EndPointAnchor.TOP);
	    		hijo.addEndPoint(endPointH1);
	    		hijo.setDraggable(false);
		        modelo.addElement(hijo);
		        
		        // Creo el endPoint del punto de variaci�n
		        EndPoint endPointPV_B = crearEndPoint(EndPointAnchor.BOTTOM);
		        this.puntoVariacionAdaptado.addEndPoint(endPointPV_B);
		        
		        // Conecto el punto de variaci�n con la variante
		        modelo.connect(crearConexion(endPointPV_B, endPointH1));
		        s.setEtiqueta(((Struct) this.puntoVariacionAdaptado.getData()).getEtiqueta());
		        
		        x +=  nombreVariante.length() / 2.0 + Constantes.distanciaEntreElemsMismoNivel;
	    	}
	    	
	    	if (cantVariantes > 0){
	    		this.crearModeloFinal(this.modelo);
	    	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }

	public void redibujarHijos(){
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext c = fc.getExternalContext();
        String idElemSeleccionado =  c.getRequestParameterMap().get("elemSeleccionado");
		
		if (idElemSeleccionado != null){
	        Element elemento = obtenerElemento(idElemSeleccionado);
	        Struct s = (Struct) elemento.getData();
	        if (!s.getEstaExpandido()){
	        	s.setEstaExpandido(true);
	        	mostrarHijos(elemento, modelo, false);
	        }
	        else{
	        	s.setEstaExpandido(false);
	        	ocultarHijos(elemento, modelo);
	        }
		}
	}

    public void mostrarHijos(Element padre, DefaultDiagramModel modelo, boolean esVistaPrevia){
    	Struct p = (Struct) padre.getData();
    	List<Struct> hijos = p.getHijos();
    	
    	if (hijos.size() > 0){
	    	String xStr = padre.getX();
	    	String yStr = padre.getY();
			float x = Float.valueOf(xStr.substring(0, xStr.length() - 2));
			float y = Float.valueOf(yStr.substring(0, yStr.length() - 2)) + Constantes.distanciaEntreNiveles;
			
			EndPoint endPointPadre = crearEndPoint(EndPointAnchor.BOTTOM);
			padre.addEndPoint(endPointPadre);
	        
	        Iterator<Struct> it = hijos.iterator();
	        while (it.hasNext()){
	        	Struct s = it.next();
	        	Element hijo = null;
	        	TipoElemento tipo = s.getType();
	        	// Si es un punto de variación => Lo agrego al hash restriccionesPV
	        	if (esPuntoDeVariacion(tipo)){
	        		this.restriccionesPV.put(s.getElementID(), validarSeleccion(null, s));
	        	}
	        	
	        	if ((tipo != TipoElemento.ROLE) && (tipo != TipoElemento.VP_ROLE) && (tipo != TipoElemento.WORK_PRODUCT) && (tipo != TipoElemento.VP_WORK_PRODUCT)){

	        		if (tipo == TipoElemento.TASK || tipo == TipoElemento.VP_TASK){
		        		if (!s.getPerformedPrimaryBy().equals("")){
		        			if (rolesTareasPrimary.containsKey(s.getPerformedPrimaryBy())){
		        				rolesTareasPrimary.get(s.getPerformedPrimaryBy()).add(s);
		        			}
		        			else{
		        				List<Struct> list = new ArrayList<Struct>();
		        				list.add(s);
		        				rolesTareasPrimary.put(s.getPerformedPrimaryBy(), list);
		        			}
		        		}
		        		if (s.getPerformedAditionallyBy() != null){
		        			Iterator<String> it1 = s.getPerformedAditionallyBy().iterator();
		        			while(it1.hasNext()){
		        				String rol = it1.next();
			        			if (rolesTareasAdditionally.containsKey(rol)){
			        				rolesTareasAdditionally.get(rol).add(s);
			        			}
			        			else{
			        				List<Struct> list = new ArrayList<Struct>();
			        				list.add(s);
			        				rolesTareasAdditionally.put(rol, list);
			        			}
		        			}
		        		}
		        	}
	        		
	        		// Si NO es para la vista previa o si NO es un punto de variaci�n, lo agrego al modelo
		        	if ((!esVistaPrevia) || (s.getVariantes().size() == 0)){
			        	hijo = new Element(s, x + "em", y + "em");
				        EndPoint endPointHijo = crearEndPoint(EndPointAnchor.TOP);
				        hijo.addEndPoint(endPointHijo);
				        hijo.setDraggable(false);
				        modelo.addElement(hijo);
				        modelo.connect(crearConexion(endPointPadre, endPointHijo));
		        	}
			        String etiqueta = obtenerEtiquetaParaModelo(p, s);
			        s.setEtiqueta(etiqueta);
		        	
		        	// Si se trata de un punto de variación y tiene variantes seleccionadas, las muestro
		        	String[] variantesSeleccionadasParaPV = this.puntosDeVariacion.get(s.getElementID());
		        	int cantVariantesSeleccionadasParaPV = (variantesSeleccionadasParaPV != null) ? variantesSeleccionadasParaPV.length : 0;
		        	if ((s.getVariantes().size() > 0) && (cantVariantesSeleccionadasParaPV > 0)){
		        		int i = 0;
		        		float xAnt = x;
		        		if (hijo != null){ 
		        			y += Constantes.distanciaEntreNiveles;
		        		}
		        		while (i < cantVariantesSeleccionadasParaPV){
		        			
		        			Variant var = buscarVariante(this.nodos, variantesSeleccionadasParaPV[i]);
		        			if (var != null){
		        				TipoElemento tipoVar = getElementoParaVarPoint(XMIParser.obtenerTipoElemento(var.getVarType()));
			    	    		String iconoVar = XMIParser.obtenerIconoPorTipo(tipoVar);
			        			Struct st = new Struct(var.getID(), var.getName(), tipoVar, Constantes.min_default, Constantes.max_default, iconoVar);
			        			Element e = new Element(st, x + "em", y + "em");
			        			
			        			EndPoint endPointVar = crearEndPoint(EndPointAnchor.TOP);
			    		        e.addEndPoint(endPointVar);
			    		        e.setDraggable(false);
			    		        modelo.addElement(e);
			    		        
			    		        EndPoint endPointHijoB = crearEndPoint(EndPointAnchor.BOTTOM);
			    		        if (hijo != null){
			    		        	hijo.addEndPoint(endPointHijoB);
			    		        }
			    		        else{
			    		        	padre.addEndPoint(endPointHijoB);
			    		        }
			    		        
			    		        modelo.connect(crearConexion(endPointHijoB, endPointVar));
			    		        st.setEtiqueta(etiqueta); // La variante tiene la misma etiqueta que el punto de variación
			    	        	x += st.getNombre().length() / 2.0 + Constantes.distanciaEntreElemsMismoNivel;
			    	        	
			        			mostrarHijos(e, modelo, esVistaPrevia);
		        			}
		        			
		        			i++;
		        		}
		        		if (hijo != null){ 
		        			y -= Constantes.distanciaEntreNiveles;
		        		}
		        		x = xAnt;
		        	}
	
		        	x += s.getNombre().length() / 2.0 + Constantes.distanciaEntreElemsMismoNivel;
		        	
		        	if (esVistaPrevia && (hijo != null)){
		        		mostrarHijos(hijo, modelo, esVistaPrevia);
		        	}
	        	}
	        }
    	}
    }

    public void ocultarHijos(Element padre, DefaultDiagramModel modelo){
    	List<Struct> hijos = ((Struct) padre.getData()).getHijos();
    	Iterator<Struct> it = hijos.iterator();
	    while (it.hasNext()){
        	Struct s = it.next();
	    	Element e = obtenerElemento(s.getElementID());
	    	if (e != null){
	    		if (s.getHijos().size() > 0){
	    			ocultarHijos(e, modelo);
	    		}
	    		else if (s.getVariantes().size() > 0){ // Es un punto de variación
	    			ocultarVariantes(e, modelo);
	    		}
	    		modelo.removeElement(e);
	    	}
    		s.setEstaExpandido(false);
        }
    }

    public void ocultarVariantes(Element padre, DefaultDiagramModel modelo){
    	List<Variant> variantes = ((Struct) padre.getData()).getVariantes();
    	Iterator<Variant> it = variantes.iterator();
    	while (it.hasNext()){
    		Variant v = it.next();
    		Element e = obtenerElemento(v.getID());
    		if (e != null){
    			if (v.getHijos().size() > 0){
    				ocultarHijos(e, modelo);
    			}
    			modelo.removeElement(e);
    		}
    		v.setEstaExpandido(false);
    	}
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
		Struct s = buscarPV(idElemSeleccionado,this.nodos);
		if (s != null){
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
		this.restriccionesPV.replace(clave, ""); // Si estoy agregando las variantes es porque cumple las restricciones
	}

    /*** Modelo final ***/

	public void crearModeloFinal(DefaultDiagramModel modelo) {
		modeloAdaptado = new DefaultDiagramModel();
		modeloAdaptado.setMaxConnections(-1);
		
		FlowChartConnector conector = new FlowChartConnector();
    	conector.setPaintStyle("{strokeStyle:'#404a4e', lineWidth:2}");
    	conector.setHoverPaintStyle("{strokeStyle:'#20282b'}");
    	conector.setAlwaysRespectStubs(true);
    	modeloAdaptado.setDefaultConnector(conector);
        
		Iterator<Element> it = modelo.getElements().iterator();
		EndPoint endPointRoot = null;

		float xElement = 0;
		float yElement = Constantes.yInicial;
		Element root = null;
		
		while (it.hasNext()){
			Element e = it.next();
			Struct s = (Struct) e.getData();
			
			TipoElemento type = s.getType();
			if (type != null){
				
				// Si es el elemento de inicio, obtengo el un único endPoint que tiene y lo agrego al modelo final
				if (type == TipoElemento.CAPABILITY_PATTERN || type == TipoElemento.DELIVERY_PROCESS){
					Struct newS = new Struct(s.getElementID(), s.getNombre(), s.getType(), s.getMin(), s.getMax(), s.getImagen());
					newS.setDescription(s.getDescription());
					newS.setPresentationName(s.getPresentationName());
					Element newE = new Element(newS, e.getX(), e.getY());
					root = newE;
					
					endPointRoot = AdaptarModeloBean.crearEndPoint(EndPointAnchor.BOTTOM);
					newE.setDraggable(false);
					newE.addEndPoint(endPointRoot);
					
					modeloAdaptado.addElement(newE);
					yElement += Constantes.distanciaEntreNiveles;
				}
				else{
					// Si es un punto de variación, recorro las variantes y agrego las que están en el modelo
					// Lo hago así porque sino se incluyen al final
					if (esPuntoDeVariacion(type)){
						
						// Esto lo agrego para evitar que las variantes que van en niveles inferiores no se incluyan en los superiores
						Iterator<Variant> itVar = s.getVariantes().iterator();
						while (itVar.hasNext()){
							Variant v = itVar.next();
							if (elementoPerteneceAModelo(v.getID(), modelo)){
								TipoElemento newType = getElementoParaVarPoint(XMIParser.obtenerTipoElemento(v.getVarType()));
								Struct newS = new Struct(v.getID(), v.getName(), newType, Constantes.min_default, Constantes.max_default, XMIParser.obtenerIconoPorTipo(newType));
								newS.setHijos(v.getHijos());
								Element newE = new Element(newS, xElement + "em", yElement + "em");
								newE.setDraggable(false);
								xElement = agregarElementoModeloFinal(newE, endPointRoot, xElement, "");
							}
						}
					}
					
					// Sino, si no es variante la incluyo (las variantes ya se incluyeron el el if)
					else if ((type != TipoElemento.VAR_ACTIVITY) &&
			  				 (type != TipoElemento.VAR_PHASE)     &&
			  				 (type != TipoElemento.VAR_ITERATION) &&
			  				 (type != TipoElemento.VAR_TASK)	&&
			  				 (type != TipoElemento.VAR_ROLE)	&&
			  				 (type != TipoElemento.VAR_MILESTONE)	&&
			  				 (type != TipoElemento.VAR_WORK_PRODUCT)){
						
						// Si ya no se agregó al modelo (lo agrego porque sino los hijos se incluyen 2 veces)
						if (!elementoPerteneceAModelo(s.getElementID(), modeloAdaptado)){
							Struct newS = crearCopiaStruct(s);
							Element newE = new Element(newS, xElement + "em", yElement + "em");
							newE.setDraggable(false);
							String etiqueta = obtenerEtiquetaParaModelo((Struct) root.getData(), newS);
							xElement = agregarElementoModeloFinal(newE, endPointRoot, xElement, etiqueta);
						}
					}
					
				}
			}
		}
		
		if (root != null){
			root.setX(xElement/2 + "em");
		}
		
	}

	public float agregarElementoModeloFinal(Element e, EndPoint superior, float x, String etiqueta){
		// Agrego el elemento al modelo final y lo conecto con el nodo superior
		if (e != null){
			EndPoint endPointP1_T = AdaptarModeloBean.crearEndPoint(EndPointAnchor.TOP);
			e.addEndPoint(endPointP1_T);
			
			modeloAdaptado.addElement(e);
			modeloAdaptado.connect(AdaptarModeloBean.crearConexion(superior, endPointP1_T));
			
			Struct s = (Struct) e.getData();
			x += s.getNombre().length() / 2.0 + Constantes.distanciaEntreElemsMismoNivel;
			s.setEtiqueta(etiqueta);
			
			// Dibujo los hijos en el modelo final
			mostrarHijos(e, modeloAdaptado, true);
		}
		return x;
	}

	public void mostrarVistaPrevia(){
		RequestContext c = RequestContext.getCurrentInstance();
		c.execute("PF('vistaPreviaDialog').show()");
	}

	/*** Copiar elementos ***/

	public Struct crearCopiaStruct(Struct s){
		Struct newS = new Struct(s.getElementID(), s.getNombre(), s.getType(), s.getMin(), s.getMax(), s.getImagen());
		
		// Seteo los hijos
		List<Struct> lstHijos = s.getHijos();
		if (lstHijos.size() > 0){
			List<Struct> hijosNewS = new ArrayList<Struct>();
			Iterator<Struct> itHijos = lstHijos.iterator();
			while (itHijos.hasNext()){
				Struct hijo = itHijos.next();
				Struct newHijo = crearCopiaStruct(hijo);
				hijosNewS.add(newHijo);
			}
			newS.setHijos(hijosNewS);
		}
		
		newS.setPerformedPrimaryBy(s.getPerformedPrimaryBy());
		newS.setPerformedAditionallyBy(s.getPerformedAditionallyBy());
		newS.setMandatoryInputs(s.getMandatoryInputs());
		newS.setOptionalInputs(s.getOptionalInputs());
		newS.setExternalInputs(s.getExternalInputs());
		newS.setOutputs(s.getOutputs());
		
		// Seteo las variantes
		List<Variant> lstVariantes = s.getVariantes();
		if (lstVariantes.size() > 0){
			ArrayList<Variant> variantesNewS = new ArrayList<Variant>();
			Iterator<Variant> itVariantes = lstVariantes.iterator();
			while (itVariantes.hasNext()){
				Variant v = itVariantes.next();
				Variant newVariant = crearCopiaVariante(v);
				variantesNewS.add(newVariant);
			}
			newS.setVariantes(variantesNewS);
		}
		
		return newS;
	}

	public Variant crearCopiaVariante(Variant v){
		Variant newV = new Variant(v.getID(), v.getName(), v.getIDVarPoint(), v.isInclusive(), v.getVarType());
		
		// Seteo los hijos
		List<Struct> lstHijos = v.getHijos();
		if (lstHijos.size() > 0){
			List<Struct> hijosNewV = new ArrayList<Struct>();
			Iterator<Struct> itHijos = lstHijos.iterator();
			while (itHijos.hasNext()){
				Struct hijo = itHijos.next();
				Struct newHijo = crearCopiaStruct(hijo);
				hijosNewV.add(newHijo);
			}
			newV.setHijos(hijosNewV);
		}
		
		// Seteo las variantes inclusivas y exclusivas
		newV.setInclusivas(v.getInclusivas());
		newV.setExclusivas(v.getExclusivas());
		
		return newV;
	}

	/*** Funciones booleanas ***/

    // Retorna true si tiene algún hijo DISTINTO de ROLE, VP_ROLE, WORK_PRODUCT o VP_WORK_PRODUCT.
    public boolean elementoTieneHijos(String id){
        if (id != null){
	        Element e = obtenerElemento(id);
	        if (e != null){
		        Struct s = (Struct) e.getData();
		    	Iterator<Struct> it = s.getHijos().iterator();
		    	while (it.hasNext()){
		    		TipoElemento t = it.next().getType();
		    		if ((t != TipoElemento.ROLE) && (t != TipoElemento.VP_ROLE) && (t != TipoElemento.WORK_PRODUCT) && (t != TipoElemento.VP_WORK_PRODUCT)){
		    			return true;
		    		}
		    	}
	        }
        }
    	return false;
    }

	public boolean elementoPerteneceAModelo(String id, DefaultDiagramModel modelo){
		Iterator<Element> it = modelo.getElements().iterator();
		while (it.hasNext()){
			Struct s = (Struct) it.next().getData();
			if (s.getElementID().equals(id)){
				return true;
			}
		}
		return false;
	}

	public boolean esPuntoDeVariacion(TipoElemento tipo){
		return (tipo == TipoElemento.VP_ACTIVITY  ||
        		tipo == TipoElemento.VP_TASK 	  ||
        		tipo == TipoElemento.VP_PHASE 	  ||
        		tipo == TipoElemento.VP_ITERATION ||
        		tipo == TipoElemento.VP_ROLE 	  ||
        		tipo == TipoElemento.VP_MILESTONE ||
        		tipo == TipoElemento.VP_WORK_PRODUCT);
	}

	/*** Buscar elementos ***/

    public String obtenerEtiquetaParaModelo(Struct padre, Struct hijo){
    	String etiqueta = "";
    	
    	String idHijo = hijo.getElementID();
    	if ((hijo.getType() == TipoElemento.ROLE) || (hijo.getType() == TipoElemento.VP_ROLE)){
    		etiqueta = (padre.getPerformedPrimaryBy() != null && padre.getPerformedPrimaryBy().equals(idHijo))
    						? TipoEtiqueta.PRIMARY_PERFORMER.toString()
    						: (padre.getPerformedAditionallyBy() != null && padre.getPerformedAditionallyBy().contains(idHijo)) 
    							? TipoEtiqueta.ADDITIONAL_PERFORMER.toString()
    					   		: "";
    	}
    	else if ((hijo.getType() == TipoElemento.WORK_PRODUCT) || (hijo.getType() == TipoElemento.VP_WORK_PRODUCT)){
    		etiqueta = (padre.getMandatoryInputs() != null && padre.getMandatoryInputs().contains(idHijo)) 
    						? TipoEtiqueta.MANDATORY_INPUT.toString()
    						:(padre.getOptionalInputs() != null && padre.getOptionalInputs().contains(idHijo)) 
    	    						? TipoEtiqueta.OPTIONAL_INPUT.toString()
    	    				:(padre.getExternalInputs() != null && padre.getExternalInputs().contains(idHijo)) 
    	    	    				? TipoEtiqueta.EXTERNAL_INPUT.toString()
    	    	    		:(padre.getOutputs() != null && padre.getOutputs().contains(idHijo)) 
    	    	    				? TipoEtiqueta.OUTPUT.toString()
    						: "";
    	}
    	
    	return etiqueta;
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

	public Struct buscarPV (String idElemSeleccionado, List<Struct> list) {
		Iterator<Struct> iterator = list.iterator();
		Struct res = null;
		
        while (iterator.hasNext() && (res == null)){
        	Struct s = iterator.next();
        	if((esPuntoDeVariacion(s.getType())) && (s.getElementID().equals(idElemSeleccionado))){
        		res = s;
        	}
        	else if (s.getHijos().size() > 0){
        		res = buscarPV(idElemSeleccionado, s.getHijos());
        	}
        }
        
        return res;
	}

	public Variant obtenerVarianteParaPV(Struct pv, String idVar){
		Iterator<Variant> it = pv.getVariantes().iterator();
		while (it.hasNext()){
			Variant v = it.next();
			if (v.getID().equals(idVar)){
				return v;
			}
		}
		return null;
	}

	public Variant buscarVariante(List<Struct> nodos, String Id){
		Iterator<Struct> it = nodos.iterator();
		Variant res = null;
		
		while (it.hasNext() && (res == null)){
			Struct s = it.next();
			TipoElemento type = s.getType();
			if (esPuntoDeVariacion(type)){
				List<Variant> variantes = s.getVariantes();
				Iterator<Variant> itH = variantes.iterator();
				while (itH.hasNext() && (res == null)){
					Variant v = itH.next();
					if (v.getID().equals(Id)){
						res = v;
					}
				}
			}
			else if (s.getHijos().size() > 0){
				res = buscarVariante(s.getHijos(),Id);
			}
		}
		
		return res;
	}

	public TipoElemento getElementoParaVarPoint(TipoElemento type){
		if (type == TipoElemento.VAR_ACTIVITY){
			return TipoElemento.ACTIVITY;
		}
		if (type == TipoElemento.VAR_PHASE){
			return TipoElemento.PHASE;
		}
		if (type == TipoElemento.VAR_ITERATION){
			return TipoElemento.ITERATION;
		}
		if (type == TipoElemento.VAR_TASK){
			return TipoElemento.TASK;
		}
		if (type == TipoElemento.VAR_ROLE){
			return TipoElemento.ROLE;
		}
		if (type == TipoElemento.VAR_MILESTONE){
			return TipoElemento.MILESTONE;
		}
		if (type == TipoElemento.VAR_WORK_PRODUCT){
			return TipoElemento.WORK_PRODUCT;
		}
		return null;
	}

	/*** Validar ****/

	public String validarSeleccion(String[] variantesSeleccionadas, Struct pv){
		String res = "";
		
		/*** Chequeo máximos y mínimos ***/
		int cantVariantesSelec = (variantesSeleccionadas != null) ? variantesSeleccionadas.length : 0;
		int min = pv.getMin();
		int max = pv.getMax();
		if (cantVariantesSelec < min){
			res = "Debe seleccionar al menos " + min + " variante" + (min > 1 ? "s." : ".");
		}
		else if ((max != -1) && (cantVariantesSelec > max)){
			res = "Debe seleccionar a lo sumo " + max + " variante" + (max > 1 ? "s." : ".");
		}
		/*** Chequeo variantes inclusivas y exclusivas ***/
		else{
			int i = 0;
			while ((i < cantVariantesSelec) && (res.isEmpty())){
				// Para cada variante seleccionada obtengo las variantes exlusivas e inclusivas
				Variant v = obtenerVarianteParaPV(pv, variantesSeleccionadas[i]);
				List<String> varExclusivas = v.getExclusivas();
				List<String> varInclusivas = v.getInclusivas();
				
				// Si las otras variantes seleccionadas están en las variantes exlusivas => Error
				if (varExclusivas != null){
					int j = 0;
					while ((j < cantVariantesSelec) && (res.isEmpty())){
						if ((j != i) && (varExclusivas.contains(variantesSeleccionadas[j]))){
							res = "No es posible seleccionar las variantes '" + obtenerVarianteParaPV(pv, variantesSeleccionadas[i]).getName() + "' y '" + obtenerVarianteParaPV(pv, variantesSeleccionadas[j]).getName() + "' a la vez.";
						}
						j++;
					}
				}
				
				// Si entre las variantes seleccionadas NO están todas las variantes inclusivas => Error
				if ((res.isEmpty()) && (varInclusivas != null)){
					Iterator<String> itInclusivas = varInclusivas.iterator();
					while (itInclusivas.hasNext()){
						String var = itInclusivas.next();
						if (!Arrays.asList(variantesSeleccionadas).contains(var)){
							res = "Debe seleccionar la variante '" + obtenerVarianteParaPV(pv, var).getName() + "'.";
						}
					}
				}
				
				i++;
			}
		}
		
		return res;
	}
	
	public Struct buscarRol (String idElemSeleccionado, List<Struct> list) {
		Iterator<Struct> iterator = list.iterator();
		Struct res = null;
		
        while (iterator.hasNext() && (res == null)){
        	Struct s = iterator.next();
        	if((s.getType() == TipoElemento.VP_ROLE ||
        			s.getType() == TipoElemento.ROLE) &&
        	   (s.getElementID().equals(idElemSeleccionado))){
       
        		res = s;
        	}
        	else {
        		if (s.getHijos().size() > 0){
        			res = buscarRol(idElemSeleccionado, s.getHijos());
        		}
        	}
        }
        return res;
	}

	
	public void crearModeloRolesTareas(){
		modeloRolesTareas = new DefaultDiagramModel();
		modeloRolesTareas.setMaxConnections(-1);
		

		FlowChartConnector conector = new FlowChartConnector();
    	conector.setPaintStyle("{strokeStyle:'#404a4e', lineWidth:2}");
    	conector.setHoverPaintStyle("{strokeStyle:'#20282b'}");
    	conector.setAlwaysRespectStubs(true);
    	modeloRolesTareas.setDefaultConnector(conector);
    	float y = 0;
    	//obtengo roles primarios
        Iterator<Entry<String,List<Struct>>> itera = rolesTareasPrimary.entrySet().iterator();
        while (itera.hasNext()){
       	 Entry<String, List<Struct>> e = itera.next();
       	 String rol = e.getKey();
       	 List<Struct> tareas = e.getValue();
       	 float x = 0;
       	
       	 //buscar rol
       	 if (rol != null){
	       	 Struct rolS = buscarRol(rol, nodos);
	       	 if (rolS != null){
		       	 //si es un vp
	       		 if ( rolS.getType().equals(TipoElemento.VP_ROLE)){
	       			 //busco variantes
	       		 }
		       	 //crear nodo
			     Element rolE = new Element(rolS);
				 rolE.setY(y + "em");
				 EndPoint endPointRoot = crearEndPoint(EndPointAnchor.RIGHT);
				 rolE.addEndPoint(endPointRoot);
				 rolE.setDraggable(false);
				 modeloRolesTareas.addElement(rolE);
				 x += rolS.getNombre().length();
		       	 
		       	 Iterator<Struct> iter = tareas.iterator();
		    	 Struct tareaInicial = iter.next();
	       		 //crear nodo y conexion
		       	 Element tareaAsociada = new Element(tareaInicial, x + "em", y + "em");
			     EndPoint endPointP1_T = crearEndPoint(EndPointAnchor.LEFT);
			     tareaAsociada.addEndPoint(endPointP1_T);
			     tareaAsociada.setDraggable(false);
			     modeloRolesTareas.addElement(tareaAsociada);
			     modeloRolesTareas.connect(crearConexion(endPointRoot, endPointP1_T));
			     x += tareaInicial.getNombre().length() + 2;
		      // 	 Struct t = iter.next();
		       	 while (iter.hasNext()){
		       		Struct tarea = iter.next();
		       		 //crear nodo
		            Element tareaAsoc = new Element(tarea, x + "em", y + "em");
			        modeloRolesTareas.addElement(tareaAsoc);
			        x += tarea.getNombre().length() + 2;	 
		       	}
		       	 //busco si el rol tmb tiene tareas adicionales
		         //agrego tareas adicionales
		         	 if (rolesTareasAdditionally.containsKey(rol)){
		         		x = rolS.getNombre().length();
		         		y += Constantes.distanciaEntreNivelesMismoRol;
		         		List<Struct> tareasAd = rolesTareasAdditionally.get(rol);
		         	 
		         		Iterator<Struct> iteraAd = tareasAd.iterator();
		         		Struct tareaInicialAd = iteraAd.next();
		   	       		 //crear nodo y conexion
		   		       	 Element tareaAsociadaAd = new Element(tareaInicialAd, x + "em", y + "em");
		   			     EndPoint endPointP1_TAd = crearEndPoint(EndPointAnchor.LEFT);
		   			     tareaAsociadaAd.addEndPoint(endPointP1_TAd);
		   			     tareaAsociadaAd.setDraggable(false);
		   			     modeloRolesTareas.addElement(tareaAsociadaAd);
		   			     modeloRolesTareas.connect(crearConexion(endPointRoot, endPointP1_TAd));
			   			 x += tareaInicialAd.getNombre().length() + 2;
		   			     
		         		while (iteraAd.hasNext()){
		         			Struct tarea = iteraAd.next();
		   		            Element tareaAsoc = new Element(tarea, x + "em", y + "em");
		   			        modeloRolesTareas.addElement(tareaAsoc);
		   			        x += tarea.getNombre().length() + 2;
		         		}
		   	       	 }
		         	rolesTareasAdditionally.remove(rol);
		         	
	       	 }
	       	y += Constantes.distanciaEntreNiveles;
		    }
       	 
        }	
        
        //recorro roles adicionales restantes
        Iterator<Entry<String,List<Struct>>> iteraAd = rolesTareasAdditionally.entrySet().iterator();
        while (iteraAd.hasNext()){
       	 Entry<String, List<Struct>> e = iteraAd.next();
       	 String rol = e.getKey();
       	 List<Struct> tareas = e.getValue();
       	 float x = 0;
       	
       	 //buscar rol
       	 if (rol != null){
	       	 Struct rolS = buscarRol(rol, nodos);
	       	 if (rolS != null){
	       		if ( rolS.getType().equals(TipoElemento.VP_ROLE)){
	       			 //busco variantes
	       		 }
			     Element rolE = new Element(rolS);
				 rolE.setY(y + "em");
				 EndPoint endPointRoot = crearEndPoint(EndPointAnchor.RIGHT);
				 rolE.addEndPoint(endPointRoot);
				 rolE.setDraggable(false);
				 modeloRolesTareas.addElement(rolE);
				 x += rolS.getNombre().length();
		       	 
		       	 Iterator<Struct> iter = tareas.iterator();
		    	 Struct tareaInicial = iter.next();
	       		 //crear nodo y conexion
		       	 Element tareaAsociada = new Element(tareaInicial, x + "em", y + "em");
			     EndPoint endPointP1_T = crearEndPoint(EndPointAnchor.LEFT);
			     tareaAsociada.addEndPoint(endPointP1_T);
			     tareaAsociada.setDraggable(false);
			     modeloRolesTareas.addElement(tareaAsociada);
			     modeloRolesTareas.connect(crearConexion(endPointRoot, endPointP1_T));
			     x += tareaInicial.getNombre().length();
					     
		  		 while (iter.hasNext()){
		  			 	Struct tarea = iter.next();
			            Element tareaAsoc = new Element(tarea, x + "em", y + "em");
				        modeloRolesTareas.addElement(tareaAsoc);
				        x += tarea.getNombre().length();
		  		}
		   }
       	 }
       	 y += Constantes.distanciaEntreNiveles;
      }
	}

}

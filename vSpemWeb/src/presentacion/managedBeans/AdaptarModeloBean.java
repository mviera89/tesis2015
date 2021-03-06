package presentacion.managedBeans;

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
import javax.naming.InitialContext;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;
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
import config.ReadProperties;
import logica.dataTypes.TipoRolesTareas;
import logica.dataTypes.TipoRolesWorkProducts;
import logica.dataTypes.TipoTareasWorkProducts;
import logica.dominio.Struct;
import logica.dominio.Variant;
import logica.enumerados.TipoElemento;
import logica.interfaces.IAdaptarManager;
import logica.utils.Utils;

@ManagedBean
@SessionScoped
public class AdaptarModeloBean {
    
	private IAdaptarManager iam;
	
    private DefaultDiagramModel modelo;
	private DefaultDiagramModel modeloAdaptado;
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
	private HashMap<String, List<String>> rolesWPResponsable;
	private HashMap<String, List<String>> rolesWPModifica;
	private HashMap<String,List<String>> tareasWPMandatoryInputs;
	private HashMap<String,List<String>> tareasWPOptionalInputs;
	private HashMap<String,List<String>> tareasWPExternalInputs;
	private HashMap<String,List<String>> tareasWPOutputs;
	
	private String idTab;
	private List<TipoRolesTareas> rolesTareas;
	private List<TipoRolesWorkProducts> rolesWP;
	private List<TipoTareasWorkProducts> tareasWP;

	public AdaptarModeloBean() {
		try{
			this.iam = InitialContext.doLookup("java:module/AdaptarManager");
		}
		catch (Exception e){
			e.printStackTrace();		
		}
	}

	@PostConstruct
    public void init() {
    	this.y = Constantes.yInicial;
    	this.nodos = new ArrayList<Struct>();
    	this.puntoVariacionAdaptado = null;
    	this.variantes = new ArrayList<SelectItem>();
    	this.variantesSeleccionadas = null;
    	
    	this.puntosDeVariacion = new HashMap<String, String[]>();
    	this.restriccionesPV = new HashMap<String, String>();
    	this.erroresModeloFinal = new ArrayList<String[]>();
    	this.rolesTareasPrimary = new HashMap<String, List<Struct>>();
    	this.rolesTareasAdditionally = new HashMap<String, List<Struct>>();
    	this.rolesWPResponsable = new HashMap<String, List<String>>();
    	this.rolesWPModifica = new HashMap<String, List<String>>();
    	this.tareasWPMandatoryInputs = new HashMap<String, List<String>>();
    	this.tareasWPOptionalInputs = new HashMap<String, List<String>>();
    	this.tareasWPExternalInputs = new HashMap<String, List<String>>();
    	this.tareasWPOutputs = new HashMap<String, List<String>>();
    	
    	this.idTab = "tab1";
    	
        crearModelo();
        cargarDatos(nodos);
        crearModeloRolesTareas();
        crearModeloRolesWorkProducts();
        crearModeloTareasWPS();
    }

	public void onTabChange(TabChangeEvent event) {
		idTab = event.getTab().getId();
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
		String error = iam.seleccionarVariantesParaPV(puntoVariacionAdaptado, variantesSeleccionadas);
		if (error.isEmpty()){
			Struct pv = ((Struct) this.puntoVariacionAdaptado.getData());
			eliminarRestricciones(pv.getElementID());
			String clave = pv.getElementID();
			String[] variantesParaPV = this.puntosDeVariacion.get(clave);
			if (variantesParaPV != null){
				ocultarVariantes(this.puntoVariacionAdaptado, modelo);
				eliminarVariantesSeleccionadas(variantesParaPV, idTab);
			}
			this.variantesSeleccionadas = variantesSeleccionadas;
			actualizarVariantesParaPV();
			if (idTab.equals("tab1")){
				this.redibujarModelo();
			}
			else if (idTab.equals("tab2")){
				this.redibujarModeloRoles();
			}
			else if (idTab.equals("tab3")){
				this.redibujarModeloWPS();
			}
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
				Struct s = iam.buscarElemento(id, nodos, "");
				if (s != null){
					String[] errorPV = {s.getNombre(), error};
					erroresModeloFinal.add(errorPV);
				}
			}
		}
		return erroresModeloFinal;
	}

	public void setErroresModeloFinal(List<String[]> erroresModeloFinal) {
		this.erroresModeloFinal = erroresModeloFinal;
	}

	public String getIdTab() {
		return idTab;
	}

	public void setIdTab(String idTab) {
		this.idTab = idTab;
	}

	public List<TipoRolesTareas> getRolesTareas() {
        return rolesTareas;
    }

	public List<TipoRolesWorkProducts> getRolesWP() {
		return rolesWP;
	}

	public void setRolesWP(List<TipoRolesWorkProducts> rolesWP) {
		this.rolesWP = rolesWP;
	}

	public List<TipoTareasWorkProducts> getTareasWP() {
		return tareasWP;
	}

	public void setTareasWP(List<TipoTareasWorkProducts> tareasWP) {
		this.tareasWP = tareasWP;
	}

	/*** Crear modelo ***/

    public void crearModelo(){
        modelo = new DefaultDiagramModel();
        modelo.setMaxConnections(-1);
    	FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		VistaBean vb =(VistaBean) session.getAttribute("VistaBean");
		
		if ((vb != null) && (!vb.getNombreArchivo().isEmpty())){
			String nomArchivo = ReadProperties.getProperty("destinoDescargas") + vb.getDirectorioArchivo() + vb.getPlugin().getDeliveryProcessDir();
		   	
		   	/*** Creo el modelo con los nodos obtenidos del archivo ***/
		   	
	    	FlowChartConnector conector = new FlowChartConnector();
	    	conector.setPaintStyle("{strokeStyle:'#404a4e', lineWidth:2}");
	    	conector.setHoverPaintStyle("{strokeStyle:'#20282b'}");
	    	conector.setAlwaysRespectStubs(true);
	        modelo.setDefaultConnector(conector);

		   	this.nodos = iam.cargarNodos(nomArchivo);
		   	
		   	Struct r = iam.crearElementoRaiz(nodos);
	        // Si no encuentro el elemento raíz => Modelo inválido
	        if (r == null){
	        	// Si llegó acá, hay cargado un mensaje de que el archivo fue cargado correctamente, pero no quiero que se muestre.
	        	Iterator<FacesMessage> it = FacesContext.getCurrentInstance().getMessages();
	        	while (it.hasNext()){
	        	    it.next();
	        	    it.remove();
	        	}
	        	// Cargo el mensaje de error
	        	FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, Constantes.MENSAJE_ARCHIVO_INCORRECTO, "");
		        FacesContext.getCurrentInstance().addMessage(null, message);
	        	return;
	        }
	        
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
	        	if (iam.esPuntoDeVariacion(tipo)){
	        		cargarRestricciones(s);
	        	}
	        	
	        	// Este modelo NO muestra roles ni workproducts
	        	if ((tipo != TipoElemento.WORK_PRODUCT) && (tipo != TipoElemento.VP_WORK_PRODUCT)){
		        	Element padre = new Element(s, x + "em", this.y + "em");
		        	
			        EndPoint endPointP1_T = crearEndPoint(EndPointAnchor.TOP);
			        padre.addEndPoint(endPointP1_T);
			        padre.setDraggable(false);
			        modelo.addElement(padre);
			        
			        modelo.connect(crearConexion(endPointRoot, endPointP1_T));
			        
			        String etiqueta = iam.obtenerEtiquetaParaModelo(r, s);
			        iam.modificarEtiqueta(s, etiqueta);
			        
		        	x += Constantes.distanciaEntreElemsMismoNivel;
		        	
		        	buscoVPRolesEnHijos(s.getHijos());
				}
	        }
	        root.setX((x - Constantes.distanciaEntreElemsMismoNivel)/2 + "em");
	        this.y += Constantes.distanciaEntreNiveles;
		}
		
		if (modelo.getElements().size() > 0){
    		this.crearModeloFinal(this.modelo, null, null);
    	}
    }

    public void cargarRestricciones(Struct s){
    	TipoElemento tipo = s.getType();
    	if (iam.esPuntoDeVariacion(tipo)){
    		String[] variantesPV = puntosDeVariacion.get(s.getElementID()); 
    		this.restriccionesPV.put(s.getElementID(), iam.validarSeleccion(variantesPV, s));
    		Iterator<Variant> itVar = s.getVariantes().iterator();
    		while (itVar.hasNext()){
    			Variant var = itVar.next();
    			if ((variantesPV != null) && (variantesPV.length > 0)){ // Si está en el modelo final => Cargo las restricciones
    				List<String> lst = Arrays.asList(variantesPV);
    				if (lst.contains(var.getID())){
    					cargarRestriccionesVar(var);
    				}
    			}
    		}
    	}
    	else{
			Iterator<Struct> itHijos = s.getHijos().iterator();
			while (itHijos.hasNext()){
				cargarRestricciones(itHijos.next());
			}
    	}
    }
    
    public void cargarRestriccionesVar(Variant v){
    	Iterator<Struct> itHijos = v.getHijos().iterator();
    	while (itHijos.hasNext()){
    		cargarRestricciones(itHijos.next());
    	}
    }
    
    public void buscoVPRolesEnHijos(List<Struct> list){
    	Iterator<Struct> it = list.iterator();
    	while (it.hasNext()){
    		Struct s = it.next();
    		if(s.getType() == TipoElemento.VP_ROLE){
    			if (s.getResponsableDe() != null){
        			Iterator<String> it1 = s.getResponsableDe().iterator();
        			while(it1.hasNext()){
        				String wp = it1.next();
	        			if (rolesWPResponsable.containsKey(s.getNombre())){
	        				List<String> responsableDe = rolesWPResponsable.get(s.getNombre());
	        				// Si el wp no está => Lo agrego
	        				if (!responsableDe.contains(wp)){
	        					responsableDe.add(wp);
	        				}
	        				rolesWPResponsable.get(s.getNombre()).add(wp);
	        			}
	        			else{
	        				List<String> list2 = new ArrayList<String>();
	        				list2.add(wp);
	        				rolesWPResponsable.put(s.getNombre(), list2);
	        			}
        			}
        		}
        		
        		if (s.getModifica() != null){
        			Iterator<String> it1 = s.getModifica().iterator();
        			while(it1.hasNext()){
        				String wp = it1.next();
	        			if (rolesWPModifica.containsKey(s.getNombre())){
	        				rolesWPModifica.get(s.getNombre()).add(wp);
	        			}
	        			else{
	        				List<String> list2 = new ArrayList<String>();
	        				list2.add(wp);
	        				rolesWPModifica.put(s.getNombre(), list2);
	        			}
        			}
        		}
    		}
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
	    		Element hijo = iam.crearVariante(nodos, (Struct) puntoVariacionAdaptado.getData(), this.variantesSeleccionadas[i], x, y);
	    		if (hijo != null){
		    		EndPoint endPointH1 = crearEndPoint(EndPointAnchor.TOP);
		    		hijo.addEndPoint(endPointH1);
		    		hijo.setDraggable(false);
			        modelo.addElement(hijo);
			        
			        // Creo el endPoint del punto de variación
			        EndPoint endPointPV_B = crearEndPoint(EndPointAnchor.BOTTOM);
			        this.puntoVariacionAdaptado.addEndPoint(endPointPV_B);
			        
			        // Conecto el punto de variación con la variante
			        modelo.connect(crearConexion(endPointPV_B, endPointH1));
			        
			        x +=  Constantes.distanciaEntreElemsMismoNivel;
	    		}
	    	}
	    	
	    	if (cantVariantes > 0){
	    		this.crearModeloFinal(this.modelo, null, null);
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
	        	iam.modificarExpandido(s, true);
	        	if (!iam.esPuntoDeVariacion(s.getType())){
	        		mostrarHijos(elemento, modelo, false);
	        	}
	        	else{
	        		mostrarVariantes(elemento, modelo);
	        	}
	        }
	        else{
	        	iam.modificarExpandido(s, false);
	        	if (!iam.esPuntoDeVariacion(s.getType())){
	        		ocultarHijos(elemento, modelo);
	        	}
	        	else{
	        		ocultarVariantes(elemento, modelo);
	        	}
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
	        	if (iam.esPuntoDeVariacion(tipo)){
	        		cargarRestricciones(s);
	        	}
	        	
	        	if ((tipo != TipoElemento.WORK_PRODUCT) && (tipo != TipoElemento.VP_WORK_PRODUCT)){
	        		// Si NO es para la vista previa o si NO es un punto de variación, lo agrego al modelo
		        	if ((!esVistaPrevia) || (s.getVariantes().size() == 0)){
			        	hijo = new Element(s, x + "em", y + "em");
				        EndPoint endPointHijo = crearEndPoint(EndPointAnchor.TOP);
				        hijo.addEndPoint(endPointHijo);
				        hijo.setDraggable(false);
				        modelo.addElement(hijo);
				        modelo.connect(crearConexion(endPointPadre, endPointHijo));
		        	}
		        	
			        String etiqueta = iam.obtenerEtiquetaParaModelo(p, s);
			        iam.modificarEtiqueta(s, etiqueta);
		        	
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
		        			Element e = iam.crearVariante(nodos, s, variantesSeleccionadasParaPV[i], x, y);
		        			if (e != null){
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
			    	        	x += Constantes.distanciaEntreElemsMismoNivel;
			    	        	
			        			mostrarHijos(e, modelo, esVistaPrevia);
		        			}
		        			
		        			i++;
		        		}
		        		if (hijo != null){
		        			y -= Constantes.distanciaEntreNiveles;
		        		}
		        		x = xAnt;
		        	}
		        	
		        	x += Constantes.distanciaEntreElemsMismoNivel;
		        	
		        	if (esVistaPrevia && (hijo != null)){
		        		mostrarHijos(hijo, modelo, esVistaPrevia);
		        	}
	        	}
	        }
    	}
    }
    
    public void mostrarVariantes(Element padre, DefaultDiagramModel modelo){
    	Struct p = (Struct) padre.getData();
    	String xStr = padre.getX();
    	String yStr = padre.getY();
		float x = Float.valueOf(xStr.substring(0, xStr.length() - 2));
		float y = Float.valueOf(yStr.substring(0, yStr.length() - 2)) + Constantes.distanciaEntreNiveles;
		
    	String[] variantesSeleccionadasParaPV = this.puntosDeVariacion.get(p.getElementID());
    	int cantVariantesSeleccionadasParaPV = (variantesSeleccionadasParaPV != null) ? variantesSeleccionadasParaPV.length : 0;
    	if ((p.getVariantes().size() > 0) && (cantVariantesSeleccionadasParaPV > 0)){
    		int i = 0;
    		while (i < cantVariantesSeleccionadasParaPV){
    			Element e = iam.crearVariante(nodos, p, variantesSeleccionadasParaPV[i], x, y);
    			if (e != null){
        			EndPoint endPointVar = crearEndPoint(EndPointAnchor.TOP);
    		        e.addEndPoint(endPointVar);
    		        e.setDraggable(false);
    		        modelo.addElement(e);
    		        
    		        EndPoint endPointHijoB = crearEndPoint(EndPointAnchor.BOTTOM);
    		        padre.addEndPoint(endPointHijoB);
    		        
    		        modelo.connect(crearConexion(endPointHijoB, endPointVar));
    	        	x += Constantes.distanciaEntreElemsMismoNivel;
    	        	
        			mostrarHijos(e, modelo, false);
    			}
    			
    			i++;
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
	    	iam.modificarExpandido(s, false);
        }
    }

    public void ocultarVariantes(Element padre, DefaultDiagramModel modelo){
    	Struct pv = (Struct) padre.getData();
    	List<Variant> variantes = pv.getVariantes();
    	Iterator<Variant> it = variantes.iterator();
    	while (it.hasNext()){
    		Variant v = it.next();
    		Element e = obtenerElemento(v.getID());
    		if (e != null){
    			if (v.getHijos().size() > 0){
    				ocultarHijos(e, modelo);
    			}
    			// Si el elemento que estoy eliminando es hijo del DeliveryProcess => Lo elimino de la lista de hijos
				Struct s = (Struct) e.getData();
				Struct p = Utils.buscarElementoEnModelo(((Struct) padre.getData()).getSuperActivities(), modelo, "");
				if (p.getType() == TipoElemento.DELIVERY_PROCESS){
					List<Struct> hijosDP = p.getHijos();
					if (hijosDP != null){
						Iterator<Struct> itHijosDP = hijosDP.iterator();
						while (itHijosDP.hasNext()){
							Struct hijo = itHijosDP.next();
							if (hijo.getElementID().equals(s.getElementID())){
								itHijosDP.remove();
							}
						}
					}
				}
				Iterator<Element> itModel = modelo.getElements().iterator();
				while (itModel.hasNext()){
					Element eTask = itModel.next();
					Struct st = (Struct) eTask.getData();
					actualizarRoles(pv, v, st);
					actualizarWP(pv, v, st);
				}
				
				// Lo elimino del modelo
    			modelo.removeElement(e);
    		}
    		iam.modificarExpandido(v, false);
    	}
    }

    public boolean actualizarRoles(Struct pv, Variant v, Struct task){
    	boolean encontre = false;
    	if ((task.getType() == TipoElemento.TASK) || (task.getType() == TipoElemento.VAR_TASK)){
			if (task.getPerformedPrimaryBy().equals(v.getID())){
				encontre = true;
				task.setPerformedPrimaryBy(pv.getElementID());
			}
			if (task.getPerformedAditionallyBy() != null){
				Iterator<String> itRemove = task.getPerformedAditionallyBy().iterator();
				while (itRemove.hasNext() && !encontre){
					String remove = itRemove.next();
					if (remove.equals(v.getID())){
						encontre = true;
						itRemove.remove();
					}
				}
				if (encontre){
					List<String> performedAditionallyBy = (task.getPerformedAditionallyBy() == null) ? new ArrayList<String>() : task.getPerformedAditionallyBy();
					performedAditionallyBy.add(pv.getElementID());
					task.setPerformedAditionallyBy(performedAditionallyBy);
				}
			}
		}
    	if (!encontre){
	    	Iterator<Struct> it = task.getHijos().iterator();
	    	while (it.hasNext() && !encontre){
	    		Struct hijo = it.next();
	    		encontre = actualizarRoles(pv, v, hijo);
	    	}
    	}
    	return encontre;
    }
    
    public boolean actualizarWP(Struct pv, Variant v, Struct role){
    	boolean encontre = false;
    	if (role.getType() == TipoElemento.VP_ROLE){
    		if (role.getModifica() != null){
				Iterator<String> itRemove = role.getModifica().iterator();
				while (itRemove.hasNext() && !encontre){
					String remove = itRemove.next();
					if (remove.equals(v.getID())){
						encontre = true;
						itRemove.remove();
					}
				}
				if (encontre && ((role.getModifica() == null) || (role.getModifica().size() == 0))){
					List<String> modifica = new ArrayList<String>();
					modifica.add(pv.getElementID());
					role.setModifica(modifica);
				}
			}
			if (role.getResponsableDe() != null){
				Iterator<String> itRemove = role.getResponsableDe().iterator();
				while (itRemove.hasNext() && !encontre){
					String remove = itRemove.next();
					if (remove.equals(v.getID())){
						encontre = true;
						itRemove.remove();
					}
				}
				if (encontre){
					List<String> responsableDe = (role.getResponsableDe() == null) ? new ArrayList<String>() : role.getResponsableDe();
					responsableDe.add(pv.getElementID());
					role.setResponsableDe(responsableDe);
				}
			}
		}
    	if (!encontre){
	    	Iterator<Struct> it = role.getHijos().iterator();
	    	while (it.hasNext() && !encontre){
	    		Struct hijo = it.next();
	    		encontre = actualizarWP(pv, v, hijo);
	    	}
    	}
    	return encontre;
    }

	public void seleccionarVariantes(){
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext c = fc.getExternalContext();
        String idElemSeleccionado =  c.getRequestParameterMap().get("elemSeleccionado");
		
		if (idElemSeleccionado != null){
	        Element elemento = obtenerElemento(idElemSeleccionado);
	        if (elemento != null){
		        Struct s = (Struct) elemento.getData();
		        if (s.getEsPV()){
					puntoVariacionAdaptado = elemento;
					cargarVariantesDelPunto(idElemSeleccionado);
					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("PF('variantesDialog').show()");
				}
	        }
		}
	}

	public void cargarVariantesDelPunto(String idElemSeleccionado){
		variantes.clear();
		variantesSeleccionadas = null;
		Struct s = buscarPV(idElemSeleccionado,this.nodos);
		if (s != null){
	        Iterator<Variant> it = s.getVariantes().iterator();
	    	while (it.hasNext()){
	    		Variant v = it.next();
	    		variantes.add(new SelectItem(v.getID(), v.getName(),v.getVarType()));
	    	}
	    	
	    	// Si había seleccionado alguna variante => La muestro marcada
			String[] variantesParaPV = this.puntosDeVariacion.get(s.getElementID());
			if (variantesParaPV != null){
				int n = variantesParaPV.length;
				variantesSeleccionadas = new String[n];
				for (int i = 0; i < n; i++){
					variantesSeleccionadas[i] = variantesParaPV[i];
				}
			}
        }
	}

	public void eliminarRestricciones(String id){
		Struct s = iam.buscarElemento(id, nodos, "");
		if (s != null){
			if (iam.esPuntoDeVariacion(s.getType())){
				this.restriccionesPV.put(id, ""); // Si voy a eliminar el elemento => No tengo que considerar las restricciones que este pueda tener
				String[] variantesPV = this.puntosDeVariacion.get(id);
				if (variantesPV != null){
					int cantVariantesPV = variantesPV.length;
					for (int i = 0; i < cantVariantesPV; i++){
						eliminarRestricciones(variantesPV[i]);
					}
				}
			}
			else{
				Iterator<Struct> itHijos = s.getHijos().iterator();
				while (itHijos.hasNext()){
					eliminarRestricciones(itHijos.next().getElementID());
				}
			}
		}
		else{ // Sino, es una variante
			Variant var = iam.buscarVariante(nodos, id);
			if (var != null){
				Iterator<Struct> itHijos = var.getHijos().iterator();
				while (itHijos.hasNext()){
					eliminarRestricciones(itHijos.next().getElementID());
				}
			}
		}
	}
	
	public void eliminarVariantesSeleccionadas(String[] variantesParaPV, String idTab){
    	int i = 0;
    	int cantVariantes = variantesParaPV.length;
		for (i = 0; i < cantVariantes; i++){
			Element e = obtenerElemento(variantesParaPV[i]);
			if (e != null){
				if (idTab.equals("tab1")){
					modelo.removeElement(e);
				}
				else if (idTab.equals("tab2")){
					// Busco el modelo en el que debo agregarlo
		    		DefaultDiagramModel modeloRolesTareas = null;
					String idPuntoVariacionAdaptado = ((Struct) this.puntoVariacionAdaptado.getData()).getElementID();
					Iterator<TipoRolesTareas> it = rolesTareas.iterator();
					while ((it.hasNext()) && (modeloRolesTareas == null)){
						TipoRolesTareas trt = it.next();
						Element elem = trt.getRol().getElements().get(0); // El PV es siempre el primer elemento de la lista
						Struct s = (Struct) elem.getData();
						if (s.getElementID().equals(idPuntoVariacionAdaptado)){
							modeloRolesTareas = trt.getRol();
						}
					}
					modeloRolesTareas.removeElement(e);
				}
				else if (idTab.equals("tab3")){
					// Busco el modelo en el que debo agregarlo
		    		DefaultDiagramModel modeloRolesWP = null;
					String idPuntoVariacionAdaptado = ((Struct) this.puntoVariacionAdaptado.getData()).getElementID();
					Iterator<TipoRolesWorkProducts> it = rolesWP.iterator();
					while ((it.hasNext())){
						TipoRolesWorkProducts trt = it.next();
						if (trt.getResponsableDe() != null){
							Iterator<DefaultDiagramModel> iterResp = trt.getResponsableDe().iterator();
							while (iterResp.hasNext() && (modeloRolesWP == null)){
								DefaultDiagramModel diagram = iterResp.next();
								Iterator<Element> iterRespD = diagram.getElements().iterator();
								while (iterRespD.hasNext()){
									Element el = iterRespD.next();
									String id = ((Struct) el.getData()).getElementID();
									if (id.equals(idPuntoVariacionAdaptado)){
										modeloRolesWP = diagram;
									}
								}
							}
						}
						if ((modeloRolesWP == null) && (trt.getModifica() != null)){
							Iterator<DefaultDiagramModel> iterMod = trt.getModifica().iterator();
							while (iterMod.hasNext() && (modeloRolesWP == null)){
								DefaultDiagramModel diagram = iterMod.next();
								Iterator<Element> iterModD = diagram.getElements().iterator();
								while (iterModD.hasNext()){
									Element el = iterModD.next();
									String id = ((Struct) el.getData()).getElementID();
									if (id.equals(idPuntoVariacionAdaptado)){
										modeloRolesWP = diagram;
									}
								}
							}
						}
						if (modeloRolesWP != null) {
							modeloRolesWP.removeElement(e);
						}
					}
				}
			}
    	}
	}

	public void actualizarVariantesParaPV(){
		String clave = ((Struct) this.puntoVariacionAdaptado.getData()).getElementID();
		this.puntosDeVariacion.put(clave, this.variantesSeleccionadas);
		this.restriccionesPV.put(clave, ""); // Si estoy agregando las variantes es porque cumple las restricciones
	}

	/*** Modelo roles-tareas ***/

	public void crearModeloRolesTareas(){
		rolesTareas = new ArrayList<TipoRolesTareas>();
		List<String> rolesAgregados = new ArrayList<String>();
		
		// Agrego las tareas primarias
		Iterator<Entry<String, List<Struct>>> it = rolesTareasPrimary.entrySet().iterator();
		while (it.hasNext()){
			Entry<String, List<Struct>> entry = it.next();
			String idRol = entry.getKey();
			List<Struct> tareas = entry.getValue();
			
			if ((idRol != null) && (!idRol.equals(""))){
				Struct rol = iam.buscarRolPorId(nodos, idRol);
		       	if (rol != null){
		       		if (!rolesAgregados.contains(rol.getNombre())){
		       			rolesAgregados.add(rol.getNombre());
			    		TipoRolesTareas trt = new TipoRolesTareas();
			    		DefaultDiagramModel rolModel = crearModeloParaRol(rol);
			       		trt.setRol(rolModel);
			       		trt.setPrimary(tareas);
			       		rolesTareas.add(trt);
		       		}
		       		else{
		       			Iterator<TipoRolesTareas> iter = rolesTareas.iterator();
		       			while (iter.hasNext()){
		       				TipoRolesTareas trt = iter.next();
		       				Struct s = (Struct) trt.getRol().getElements().get(0).getData();
		       				if (s.getNombre().equals(rol.getNombre())){
		       					List<Struct> prim = trt.getPrimary();
		       					Iterator<Struct> itTareas = tareas.iterator();
		       					// Agrego todas las tareas que no están
		       					while (itTareas.hasNext()){
		       						Struct tarea = itTareas.next();
		       						boolean fin = false;
		       						int i = 0;
		       						int n = prim.size();
			       					while ((i < n) && !fin){
			       						if (prim.get(i).getNombre().equals(tarea.getNombre())){
			       							fin = true;
			       						}
			       						i++;
			       					}
			       					if (!fin){
			       						prim.add(tarea);
			       					}
		       					}
		       				}
		       			}
		       		}
		       	}
			}
		}
		
		// Agrego las tareas adicionales
		it = rolesTareasAdditionally.entrySet().iterator();
		while (it.hasNext()){
			Entry<String, List<Struct>> entry = it.next();
			String idRol = entry.getKey();
			List<Struct> tareas = entry.getValue();
			if ((idRol != null) && (!idRol.equals(""))){
				Struct rol = iam.buscarRolPorId(nodos, idRol);
		       	if (rol != null){
		       		if (!rolesAgregados.contains(rol.getNombre())){
			       		rolesAgregados.add(rol.getNombre());
			    		TipoRolesTareas trt = new TipoRolesTareas();
			    		DefaultDiagramModel rolModel = crearModeloParaRol(rol);
			       		trt.setRol(rolModel);
			       		trt.setPrimary(tareas);
			       		rolesTareas.add(trt);
		       		}
		       		else{
		       			Iterator<TipoRolesTareas> iter = rolesTareas.iterator();
		       			while (iter.hasNext()){
		       				TipoRolesTareas trt = iter.next();
		       				Struct s = (Struct) trt.getRol().getElements().get(0).getData();
		       				if (s.getNombre().equals(rol.getNombre())){
		       					List<Struct> add = trt.getAdditionally();
		       					if (add == null){
		       						trt.setAdditionally(tareas);
		       					}
		       					else{
			       					Iterator<Struct> itTareas = tareas.iterator();
			       					// Agrego todas las tareas que no están
			       					while (itTareas.hasNext()){
			       						Struct tarea = itTareas.next();
			       						boolean fin = false;
			       						int i = 0;
			       						int n = add.size();
				       					while ((i < n) && !fin){
				       						if (add.get(i).getNombre().equals(tarea.getNombre())){
				       							fin = true;
				       						}
				       						i++;
				       					}
				       					if (!fin){
				       						add.add(tarea);
				       					}
			       					}
		       					}
		       				}
		       			}
		       		}
		       	}
			}
		}
	}

	public DefaultDiagramModel crearModeloParaRol(Struct rol){
		DefaultDiagramModel modeloRol = new DefaultDiagramModel();
        modeloRol.setMaxConnections(-1);
	   	
    	FlowChartConnector conector = new FlowChartConnector();
    	conector.setPaintStyle("{strokeStyle:'#404a4e', lineWidth:2}");
    	conector.setHoverPaintStyle("{strokeStyle:'#20282b'}");
    	conector.setAlwaysRespectStubs(true);
        modeloRol.setDefaultConnector(conector);
        
        // Agrego el rol como elemento raiz
        Element root = new Element(rol);
        root.setDraggable(false);
    	modeloRol.addElement(root);
    	
    	return modeloRol;
	}

	public void redibujarModeloRoles() {
		try{
	    	int cantVariantes = this.variantesSeleccionadas.length;
	    	String xStr = this.puntoVariacionAdaptado.getX();
	    	if (xStr == null) {
	    		xStr = "0em";
	    	}
	    	String yStr = this.puntoVariacionAdaptado.getY();
	    	if (yStr == null) {
	    		yStr = "0em";
	    	}
			float xIni = Float.valueOf(xStr.substring(0, xStr.length() - 2));
			float x = (cantVariantes > 1) ? xIni - (xIni / cantVariantes) : xIni;
			float y = Float.valueOf(yStr.substring(0, yStr.length() - 2)) + Constantes.distanciaEntreNiveles;
			
    		// Busco el modelo en el que debo agregarlo
    		DefaultDiagramModel modeloRolesTareas = null;
			String idPuntoVariacionAdaptado = ((Struct) this.puntoVariacionAdaptado.getData()).getElementID();
			Iterator<TipoRolesTareas> it = rolesTareas.iterator();
			while ((it.hasNext()) && (modeloRolesTareas == null)){
				TipoRolesTareas trt = it.next();
				Element e = trt.getRol().getElements().get(0); // El PV es siempre el primer elemento de la lista
				Struct s = (Struct) e.getData();
				if (s.getElementID().equals(idPuntoVariacionAdaptado)){
					modeloRolesTareas = trt.getRol();
				}
			}
    		
			if (modeloRolesTareas != null){
		    	for (int i = 0; i < cantVariantes; i++){
		    		Element hijo = iam.crearVariante(nodos, (Struct) puntoVariacionAdaptado.getData(), this.variantesSeleccionadas[i], x, y);
		    		if (hijo != null){
			    		EndPoint endPointH1 = crearEndPoint(EndPointAnchor.TOP);
			    		hijo.addEndPoint(endPointH1);
			    		hijo.setDraggable(false);
				        modeloRolesTareas.addElement(hijo);
			    		
				        // Creo el endPoint del punto de variación
				        EndPoint endPointPV_B = crearEndPoint(EndPointAnchor.BOTTOM);
				        this.puntoVariacionAdaptado.addEndPoint(endPointPV_B);
				        
				        // Conecto el punto de variación con la variante
				        modeloRolesTareas.connect(crearConexion(endPointPV_B, endPointH1));
				        
				        x +=  Constantes.distanciaEntreElemsMismoNivel;
			    	}
		    	}
			}
			
			if (cantVariantes > 0){
	    		this.crearModeloFinal(modelo, modeloRolesTareas, null);
	    	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }

	/*** Modelo Roles WorkProducts ***/
	
	public List<DefaultDiagramModel> crearModeloParaWPS(List<String> wps){
    	List<DefaultDiagramModel> res = new ArrayList<DefaultDiagramModel>();
    	// Para cada wp lo busco y lo agrego
    	Iterator<String> itWP = wps.iterator();
    	List<String> wpAgregados = new ArrayList<String>();
    	while (itWP.hasNext()){
    		String wp = itWP.next();
    		Struct wpS = iam.buscarWP(nodos, wp);
    		if (wpS != null){
    			if (!wpAgregados.contains(wpS.getNombre())){

    				DefaultDiagramModel modeloWps = new DefaultDiagramModel();
    				modeloWps.setMaxConnections(-1);
    			   	
    		    	FlowChartConnector conector = new FlowChartConnector();
    		    	conector.setPaintStyle("{strokeStyle:'#404a4e', lineWidth:2}");
    		    	conector.setHoverPaintStyle("{strokeStyle:'#20282b'}");
    		    	conector.setAlwaysRespectStubs(true);
    		    	modeloWps.setDefaultConnector(conector);
    		    	
    				wpAgregados.add(wpS.getNombre());
			        Element root = new Element(wpS);
			        root.setDraggable(false);
			        modeloWps.addElement(root);
			        
			        res.add(modeloWps);
    			}
    		}
    	}
    	return res;
	}
	
	public void crearModeloRolesWorkProducts(){
		rolesWP = new ArrayList<TipoRolesWorkProducts>();
		List<String> rolesAgregados = new ArrayList<String>();
		
		// Agrego los wp que es responsable
		Iterator<Entry<String, List<String>>> it = rolesWPResponsable.entrySet().iterator();
		while (it.hasNext()){
			Entry<String, List<String>> entry = it.next();
			String nomRol = entry.getKey();
			List<String> wps = entry.getValue();
			if ((nomRol != null) && (!nomRol.equals(""))){
				List<Struct> roles = iam.buscarRolPorNombre(nodos, nomRol);
				Iterator<Struct> itRoles = roles.iterator();
		       	while (itRoles.hasNext()){
		       		Struct rol = itRoles.next();
		       		// Crear modelo para wps
		       		List<DefaultDiagramModel> wpsModel = crearModeloParaWPS(wps);
		       		if (!rolesAgregados.contains(rol.getNombre())){
		       			rolesAgregados.add(rol.getNombre());
		       			TipoRolesWorkProducts trt = new TipoRolesWorkProducts();
			       		trt.setRol(rol);
			       		trt.setResponsableDe(wpsModel);
			       		rolesWP.add(trt);
		       		}
		       		else{
						Iterator<TipoRolesWorkProducts> iter = rolesWP.iterator();
						while (iter.hasNext()){
							TipoRolesWorkProducts trt = iter.next();
							Struct s = trt.getRol();
							if (s.getNombre().equals(rol.getNombre())){
								List<DefaultDiagramModel> responsable = trt.getResponsableDe();
								if (responsable != null){
									List<DefaultDiagramModel> res = new ArrayList<DefaultDiagramModel>();
									Iterator<DefaultDiagramModel> itResp = responsable.iterator();
									while (itResp.hasNext()){
										DefaultDiagramModel model = itResp.next();
				       					List<Element> resp = model.getElements();
				       					Iterator<DefaultDiagramModel> itLstWs = wpsModel.iterator();
				       					while (itLstWs.hasNext()){
				       						DefaultDiagramModel modelWs = itLstWs.next();
											Iterator<Element> itWs = modelWs.getElements().iterator();
											// Agrego todos los wp que no están
											while (itWs.hasNext()){
												Element wp = itWs.next();
												boolean fin = false;
												int i = 0;
												int n = resp.size();
												while ((i < n) && !fin){
													String nombreResp = ((Struct) resp.get(i).getData()).getNombre();
													String nombreWp = ((Struct) wp.getData()).getNombre();
													if (nombreResp.equals(nombreWp)){
														fin = true;
													}
													i++;
												}
												if (!fin){
													res.add(model);
												}
											}
				       					}
									}
								}
								else{
									trt.setResponsableDe(wpsModel);
			       				}
							}
						}
					}
		       	}
			}
		}
		
		// Agrego los wp que modifica
		it = rolesWPModifica.entrySet().iterator();
		while (it.hasNext()){
			Entry<String, List<String>> entry = it.next();
			String nomRol = entry.getKey();
			List<String> wps = entry.getValue();
			if ((nomRol != null) && (!nomRol.equals(""))){
				List<Struct> roles = iam.buscarRolPorNombre(nodos, nomRol);
				Iterator<Struct> itRoles = roles.iterator();
		       	while (itRoles.hasNext()){
		       		Struct rol = itRoles.next();
		       		// crear modelo para wps
		       		List<DefaultDiagramModel> wpsModel = crearModeloParaWPS(wps);
		       		if (!rolesAgregados.contains(rol.getNombre())){
			       		rolesAgregados.add(rol.getNombre());
			       		TipoRolesWorkProducts trt = new TipoRolesWorkProducts();
			       		trt.setRol(rol);
			       		trt.setModifica(wpsModel);
			       		rolesWP.add(trt);
		       		}
		       		else{
		       			Iterator<TipoRolesWorkProducts> iter = rolesWP.iterator();
		       			while (iter.hasNext()){
		       				TipoRolesWorkProducts trt = iter.next();
		       				Struct s = trt.getRol();
		       				if (s.getNombre().equals(rol.getNombre())){
		       					List<DefaultDiagramModel> modifica = trt.getModifica();
								if (modifica != null){
									List<DefaultDiagramModel> res = new ArrayList<DefaultDiagramModel>();
									Iterator<DefaultDiagramModel> itMod = modifica.iterator();
									while (itMod.hasNext()){
										DefaultDiagramModel model = itMod.next();
				       					List<Element> modif = model.getElements();
				       					Iterator<DefaultDiagramModel> itLstWs = wpsModel.iterator();
				       					while (itLstWs.hasNext()){
				       						DefaultDiagramModel modelWs = itLstWs.next();
											Iterator<Element> itWs = modelWs.getElements().iterator();
											// Agrego todos los wp que no están
											while (itWs.hasNext()){
												Element wp = itWs.next();
												boolean fin = false;
												int i = 0;
												int n = modif.size();
												while ((i < n) && !fin){
													String nombreModif = ((Struct) modif.get(i).getData()).getNombre();
													String nombreWp = ((Struct) wp.getData()).getNombre();
													if (nombreModif.equals(nombreWp)){
														fin = true;
													}
													i++;
												}
												if (!fin){
													res.add(model);
												}
											}
				       					}
									}
								}
								else{
									trt.setModifica(wpsModel);
			       				}
		       				}
		       			}
		       		}
		       	}
			}
		}
	}

	public void redibujarModeloWPS() {
		try{
	    	int cantVariantes = this.variantesSeleccionadas.length;
	    	String xStr = this.puntoVariacionAdaptado.getX();
	    	if (xStr == null) {
	    		xStr = "0em";
	    	}
	    	String yStr = this.puntoVariacionAdaptado.getY();
	    	if (yStr == null) {
	    		yStr = "0em";
	    	}
			float xIni = Float.valueOf(xStr.substring(0, xStr.length() - 2));
			float x = (cantVariantes > 1) ? xIni - (xIni / cantVariantes) : xIni;
			float y = Float.valueOf(yStr.substring(0, yStr.length() - 2)) + Constantes.distanciaEntreNiveles;
			
    		// Busco el modelo en el que debo agregarlo
    		DefaultDiagramModel modeloRolesWPS = null;
			String idPuntoVariacionAdaptado = ((Struct) this.puntoVariacionAdaptado.getData()).getElementID();
			Iterator<TipoRolesWorkProducts> it = rolesWP.iterator();
			boolean estaVP = false;
			while (it.hasNext() && !estaVP){
				TipoRolesWorkProducts trt = it.next();
				List<DefaultDiagramModel> responsable = trt.getResponsableDe();
				List<DefaultDiagramModel> modifica = trt.getModifica();
				// busco idPuntoVariacionAdaptado en los modelos
				boolean encontre = false;
				if (responsable != null){
					Iterator<DefaultDiagramModel> resp = responsable.iterator();
					while (resp.hasNext()){
						DefaultDiagramModel model = resp.next();
						Iterator<Element> itResp = model.getElements().iterator();
						while(itResp.hasNext() && !encontre){
							Element e = itResp.next();
							Struct s = (Struct) e.getData();
							if (s.getElementID().equals(idPuntoVariacionAdaptado)){
								encontre = true;
								modeloRolesWPS = model;
								estaVP = true;
							}
						}
					}
				}
				if (!encontre){
					if (modifica != null){
						Iterator<DefaultDiagramModel> mod = modifica.iterator();
						while (mod.hasNext()){
							DefaultDiagramModel model = mod.next();
							Iterator<Element> itMod = model.getElements().iterator();
							while(itMod.hasNext() && !encontre){
								Element e = itMod.next();
								Struct s = (Struct) e.getData();
								if (s.getElementID().equals(idPuntoVariacionAdaptado)){
									encontre = true;
									modeloRolesWPS = model;
									estaVP = true;
								}
							}
						}
					}
				}
			}
			
			if (modeloRolesWPS != null){
		    	for (int i = 0; i < cantVariantes; i++){
		    		Element hijo = iam.crearVariante(nodos, (Struct) puntoVariacionAdaptado.getData(), this.variantesSeleccionadas[i], x, y);
		    		if (hijo != null){
			    		EndPoint endPointH1 = crearEndPoint(EndPointAnchor.TOP);
			    		hijo.addEndPoint(endPointH1);
			    		hijo.setDraggable(false);
			    		modeloRolesWPS.addElement(hijo);
			    		
				        // Creo el endPoint del punto de variación
				        EndPoint endPointPV_B = crearEndPoint(EndPointAnchor.BOTTOM);
				        this.puntoVariacionAdaptado.addEndPoint(endPointPV_B);
				        
				        // Conecto el punto de variación con la variante
				        modeloRolesWPS.connect(crearConexion(endPointPV_B, endPointH1));
				        
				        x +=  Constantes.distanciaEntreElemsMismoNivel;
		    		}
		    	}
			}
			
			if (cantVariantes > 0){
	    		this.crearModeloFinal(modelo, null, modeloRolesWPS);
	    	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }

	/*** Modelo tareas ***/

	public void crearModeloTareasWPS(){
		tareasWP = new ArrayList<TipoTareasWorkProducts>();
		List<String> tareasAgregadas = new ArrayList<String>();
		
		// Agrego los workproducts mandatoryInputs
		Iterator<Entry<String, List<String>>> it = tareasWPMandatoryInputs.entrySet().iterator();
		while (it.hasNext()){
			Entry<String, List<String>> entry = it.next();
			String nomTarea = entry.getKey();
			List<String> wps = entry.getValue();
			Iterator<String> itWP = wps.iterator();
			List<Struct> mandatoryInputs = new ArrayList<Struct>();
			List<String> wpAgregados = new ArrayList<String>();
			while (itWP.hasNext()){
				String wp = itWP.next();
				Struct wpS = iam.buscarWP(nodos, wp);
				if ((wps != null) && (!wpAgregados.contains(wpS.getNombre()))){
					wpAgregados.add(wpS.getNombre());
					mandatoryInputs.add(wpS);
				}
			}
			if ((nomTarea != null) && (!nomTarea.equals(""))){
				List<Struct> tareas = iam.buscarTareaPorNombre(nodos, nomTarea);
				Iterator<Struct> itTareas = tareas.iterator();
		       	while (itTareas.hasNext()){
		       		Struct tarea = itTareas.next();
		       		if (!tareasAgregadas.contains(nomTarea)){
			       		tareasAgregadas.add(nomTarea);
			    		TipoTareasWorkProducts trt = new TipoTareasWorkProducts();
			       		trt.setTarea(tarea);
			       		trt.setMandatoryInputs(mandatoryInputs);
			       		tareasWP.add(trt);
		       		}
		       		else{
		       			Iterator<TipoTareasWorkProducts> iter = tareasWP.iterator();
		       			while (iter.hasNext()){
		       				TipoTareasWorkProducts trt = iter.next();
		       				Struct s = (Struct) trt.getTarea();
		       				if (s.getNombre().equals(nomTarea)){
		       					trt.setMandatoryInputs(mandatoryInputs);
		       				}
		       			}
		       		}
		       	}
			}
		}
		
		// Agrego los wps optionalInputs
		it = tareasWPOptionalInputs.entrySet().iterator();
		while (it.hasNext()){
			Entry<String, List<String>> entry = it.next();
			String nomTarea = entry.getKey();
			List<String> wps = entry.getValue();
			Iterator<String> itWP = wps.iterator();
			List<Struct> optionalInputs = new ArrayList<Struct>();
			List<String> wpAgregados = new ArrayList<String>();
			while (itWP.hasNext()){
				String wp = itWP.next();
				Struct wpS = iam.buscarWP(nodos, wp);
				if ((wps != null) && (!wpAgregados.contains(wpS.getNombre()))){
					wpAgregados.add(wpS.getNombre());
					optionalInputs.add(wpS);
				}
			}
			if ((nomTarea != null) && (!nomTarea.equals(""))){
				List<Struct> tareas = iam.buscarTareaPorNombre(nodos, nomTarea);
				Iterator<Struct> itTareas = tareas.iterator();
		       	while (itTareas.hasNext()){
		       		Struct tarea = itTareas.next();
		       		if (!tareasAgregadas.contains(nomTarea)){
		       			tareasAgregadas.add(nomTarea);
		       			TipoTareasWorkProducts trt = new TipoTareasWorkProducts();
			       		trt.setTarea(tarea);
			       		trt.setOptionalInputs(optionalInputs);
			       		tareasWP.add(trt);
		       		}
		       		else{
		       			Iterator<TipoTareasWorkProducts> iter = tareasWP.iterator();
		       			while (iter.hasNext()){
		       				TipoTareasWorkProducts trt = iter.next();
		       				Struct s = (Struct) trt.getTarea();
		       				if (s.getNombre().equals(nomTarea)){
		       					trt.setOptionalInputs(optionalInputs);
		       				}
		       			}
		       		}
		       	}
			}
		}
		
		// Agrego los wps externalInputs
		it = tareasWPExternalInputs.entrySet().iterator();
		while (it.hasNext()){
			Entry<String, List<String>> entry = it.next();
			String nomTarea = entry.getKey();
			List<String> wps = entry.getValue();
			Iterator<String> itWP = wps.iterator();
			List<Struct> externalInputs = new ArrayList<Struct>();
			List<String> wpAgregados = new ArrayList<String>();
			while (itWP.hasNext()){
				String wp = itWP.next();
				Struct wpS = iam.buscarWP(nodos, wp);
				if ((wps != null) && (!wpAgregados.contains(wpS.getNombre()))){
					wpAgregados.add(wpS.getNombre());
					externalInputs.add(wpS);
				}
			}
			if ((nomTarea != null) && (!nomTarea.equals(""))){
				List<Struct> tareas = iam.buscarTareaPorNombre(nodos, nomTarea);
				Iterator<Struct> itTareas = tareas.iterator();
		       	while (itTareas.hasNext()){
		       		Struct tarea = itTareas.next();
		       		if (!tareasAgregadas.contains(nomTarea)){
		       			tareasAgregadas.add(nomTarea);
		       			TipoTareasWorkProducts trt = new TipoTareasWorkProducts();
			       		trt.setTarea(tarea);
			       		trt.setExternalInputs(externalInputs);
			       		tareasWP.add(trt);
		       		}
		       		else{
		       			Iterator<TipoTareasWorkProducts> iter = tareasWP.iterator();
		       			while (iter.hasNext()){
		       				TipoTareasWorkProducts trt = iter.next();
		       				Struct s = (Struct) trt.getTarea();
		       				if (s.getNombre().equals(nomTarea)){
		       					trt.setExternalInputs(externalInputs);
		       				}
		       			}
		       		}
		       	}
			}
		}
		
		// Agrego los wps outputs
		it = tareasWPOutputs.entrySet().iterator();
		while (it.hasNext()){
			Entry<String, List<String>> entry = it.next();
			String nomTarea = entry.getKey();
			List<String> wps = entry.getValue();
			Iterator<String> itWP = wps.iterator();
			List<Struct> outputs = new ArrayList<Struct>();
			List<String> wpAgregados = new ArrayList<String>();
			while (itWP.hasNext()){
				String wp = itWP.next();
				Struct wpS = iam.buscarWP(nodos, wp);
				if ((wps != null) && (!wpAgregados.contains(wpS.getNombre()))){
					wpAgregados.add(wpS.getNombre());
					outputs.add(wpS);
				}
			}
			if ((nomTarea != null) && (!nomTarea.equals(""))){
				List<Struct> tareas = iam.buscarTareaPorNombre(nodos, nomTarea);
				Iterator<Struct> itTareas = tareas.iterator();
		       	while (itTareas.hasNext()){
		       		Struct tarea = itTareas.next();
		       		if (!tareasAgregadas.contains(nomTarea)){
		       			tareasAgregadas.add(nomTarea);
		       			TipoTareasWorkProducts trt = new TipoTareasWorkProducts();
			       		trt.setTarea(tarea);
			       		trt.setOutputs(outputs);
			       		tareasWP.add(trt);
		       		}
		       		else{
		       			Iterator<TipoTareasWorkProducts> iter = tareasWP.iterator();
		       			while (iter.hasNext()){
		       				TipoTareasWorkProducts trt = iter.next();
		       				Struct s = (Struct) trt.getTarea();
		       				if (s.getNombre().equals(nomTarea)){
		       					trt.setOutputs(outputs);
		       				}
		       			}
		       		}
		       	}
			}
		}
	}

    /*** Modelo final ***/

	public void crearModeloFinal(DefaultDiagramModel modelo, DefaultDiagramModel modeloRoles, DefaultDiagramModel modeloRolesWPS) {
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
					Struct newS = Utils.crearStruct(s);
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
					if (iam.esPuntoDeVariacion(type)){
						// busco padre del varpoint
						Struct padre = null;
						Iterator<Element> iter = modelo.getElements().iterator();
						while (iter.hasNext() && (padre == null)){
							Element el = iter.next();
							Struct st = (Struct) el.getData();
							List<Struct> hijos = st.getHijos();
							if (hijos != null){
								Iterator<Struct> itera = hijos.iterator();
								while (itera.hasNext() && (padre == null)){
									Struct h = itera.next();
									if (h.getElementID().equals(s.getElementID())){
										padre = st;
									}
									else {
										padre = buscarPadre(s.getElementID(), h.getHijos());
									}
								}
							}
						}
						
						// Esto lo agrego para evitar que las variantes que van en niveles inferiores no se incluyan en los superiores
						Iterator<Variant> itVar = s.getVariantes().iterator();
						while (itVar.hasNext()){
							Variant v = itVar.next();
							if ((this.puntosDeVariacion != null) && (this.puntosDeVariacion.size() > 0)){
								String[] variantesParaPV = this.puntosDeVariacion.get(s.getElementID());
								if (variantesParaPV != null){
									List<String> varSeleccionadas = Arrays.asList(variantesParaPV);
									if (varSeleccionadas.contains(v.getID())){
										Struct newS = Utils.crearStruct(v, s);
										if (padre != null){
											padre.getHijos().add(newS);
										}
										Element newE = new Element(newS, xElement + "em", yElement + "em");
										newE.setDraggable(false);
										xElement = agregarElementoModeloFinal(newE, endPointRoot, xElement, "");
									}
								}
							}
						}
					}
					
					// Sino, si no es variante la incluyo (las variantes ya se incluyeron el el if)
					else if (!iam.esVariante(type)){
						// Si ya no se agregó al modelo (lo agrego porque sino los hijos se incluyen 2 veces)
						if (!elementoPerteneceAModelo(s.getElementID(), modeloAdaptado)){
							Struct newS = Utils.crearCopiaStruct(s);
							Element newE = new Element(newS, xElement + "em", yElement + "em");
							newE.setDraggable(false);
							String etiqueta = iam.obtenerEtiquetaParaModelo((Struct) root.getData(), newS);
							xElement = agregarElementoModeloFinal(newE, endPointRoot, xElement, etiqueta);
						}
					}	
				}
			}
		}
		
		actualizarRolesEnModeloFinal(modeloRoles);
		actualizarWorkProductsEnModeloFinal(modeloRolesWPS);
		
		if (root != null){
			root.setX(xElement/2 + "em");
		}
		
	}

	public void actualizarRolesEnModeloFinal(DefaultDiagramModel modeloRoles){
		if (modeloRoles != null){
			Struct s = (modeloRoles.getElements().size() > 0) ? (Struct) modeloRoles.getElements().get(0).getData() : null;
			if ((s != null) && (s.getType().equals(TipoElemento.VP_ROLE))){
				Iterator<Element> itElems = modeloAdaptado.getElements().iterator();
				while (itElems.hasNext()){
					Element eTask  = itElems.next();
					Struct sTask = (Struct) eTask.getData();
					if ((this.puntosDeVariacion != null) && (this.puntosDeVariacion.size() > 0)){
						List<String> varSeleccionadas = Arrays.asList(this.puntosDeVariacion.get(s.getElementID()));
						if (sTask.getType() == TipoElemento.TASK){
							if (sTask.getPerformedPrimaryBy().equals(s.getElementID())){
								boolean fin = false;
								Iterator<Variant> itVar = s.getVariantes().iterator();
								while (itVar.hasNext() && !fin){
									Variant v = itVar.next();
									if (varSeleccionadas.contains(v.getID())){
										sTask.setPerformedPrimaryBy(v.getID());
										fin = true;
									}
								}
							}
							else if (sTask.getPerformedAditionallyBy() != null){
								String idVarAdd = null;
								Iterator<String> itAdd = sTask.getPerformedAditionallyBy().iterator();
								while (itAdd.hasNext()){
									String add = itAdd.next();
									if (add.equals(s.getElementID())){
										Iterator<Variant> itVar = s.getVariantes().iterator();
										while (itVar.hasNext() && (idVarAdd == null)){
											Variant v = itVar.next();
											if (varSeleccionadas.contains(v.getID())){
												itAdd.remove();
												idVarAdd = v.getID();
											}
										}
									}
								}
								if (idVarAdd != null){
									sTask.getPerformedAditionallyBy().add(idVarAdd);
								}
							}
						}
					}
				}
			}
		}
	}

	public void actualizarWorkProductsEnModeloFinal(DefaultDiagramModel modeloRolesWPS){
		if (modeloRolesWPS != null){
			if (modeloRolesWPS.getElements().size() > 0){
				Iterator<Element> itRolesWS = modeloRolesWPS.getElements().iterator();
				while (itRolesWS.hasNext()){
					Struct s = (Struct) itRolesWS.next().getData();
					if ((s != null) && (s.getType().equals(TipoElemento.VP_WORK_PRODUCT))){
						Iterator<Element> itElems = modeloAdaptado.getElements().iterator();
						while (itElems.hasNext()){
							Element eTask  = itElems.next();
							Struct sRole = (Struct) eTask.getData();
							if ((this.puntosDeVariacion != null) && (this.puntosDeVariacion.size() > 0)){
								List<String> varSeleccionadas = Arrays.asList(this.puntosDeVariacion.get(s.getElementID()));
								if (sRole.getType() == TipoElemento.VAR_ROLE){
									if (sRole.getModifica() != null){
										String idVarAdd = null;
										Iterator<String> itModifica = sRole.getModifica().iterator();
										while (itModifica.hasNext()){
											String modifica = itModifica.next();
											if (modifica.equals(s.getElementID())){
												Iterator<Variant> itVar = s.getVariantes().iterator();
												while (itVar.hasNext() && (idVarAdd == null)){
													Variant v = itVar.next();
													if (varSeleccionadas.contains(v.getID())){
														itModifica.remove();
														idVarAdd = v.getID();
													}
												}
											}
										}
										if (idVarAdd != null){
											sRole.getModifica().add(idVarAdd);
										}
									}
									else if (sRole.getResponsableDe() != null){
										String idVarAdd = null;
										List<String> responsableDe = sRole.getResponsableDe();
										Iterator<String> itResp = responsableDe.iterator();
										while ((itResp.hasNext()) && (idVarAdd == null)){
											String responsable = itResp.next();
											if (responsable.equals(s.getElementID())){
												Iterator<Variant> itVar = s.getVariantes().iterator();
												while (itVar.hasNext() && (idVarAdd == null)){
													Variant v = itVar.next();
													if (varSeleccionadas.contains(v.getID())){
														itResp.remove();
														idVarAdd = v.getID();
													}
												}
											}
										}
										if (idVarAdd != null){
											responsableDe.add(idVarAdd);
											sRole.setResponsableDe(responsableDe);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public float agregarElementoModeloFinal(Element e, EndPoint superior, float x, String etiqueta){
		// Agrego el elemento al modelo final y lo conecto con el nodo superior
		if (e != null){
			EndPoint endPointP1_T = crearEndPoint(EndPointAnchor.TOP);
			e.addEndPoint(endPointP1_T);

			modeloAdaptado.addElement(e);
			modeloAdaptado.connect(crearConexion(superior, endPointP1_T));
			
			Struct s = (Struct) e.getData();
			x += Constantes.distanciaEntreElemsMismoNivel;
			iam.modificarEtiqueta(s, etiqueta);
			
			// Dibujo los hijos en el modelo final
			mostrarHijos(e, modeloAdaptado, true);
		}
		return x;
	}

	/*** Funciones booleanas ***/
	
    // Retorna true si tiene algún hijo DISTINTO de ROLE, VP_ROLE, WORK_PRODUCT o VP_WORK_PRODUCT, o si es un punto de variación y tiene variantes seleccionadas
    public boolean elementoTieneHijos(String id){
        if (id != null){
	        Element e = obtenerElemento(id);
	        if (e != null){
		        Struct s = (Struct) e.getData();
		        if ((s.getType() != TipoElemento.DELIVERY_PROCESS) && !Utils.esPuntoDeVariacion(s.getType())){
			    	Iterator<Struct> it = s.getHijos().iterator();
			    	while (it.hasNext()){
			    		TipoElemento t = it.next().getType();
			    		if ((t != TipoElemento.ROLE) && (t != TipoElemento.VP_ROLE) && (t != TipoElemento.WORK_PRODUCT) && (t != TipoElemento.VP_WORK_PRODUCT)){
			    			return true;
			    		}
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

	/*** Buscar elementos ***/

	public Element obtenerElemento(String idElemento){
		if (idTab.equals("tab1")){
			Iterator<Element> it = modelo.getElements().iterator();
			while (it.hasNext()){
				Element e = it.next();
				String id = ((Struct) e.getData()).getElementID();
				if (id.equals(idElemento)){
					return e;
				}
			}
		}
		else if (idTab.equals("tab2")){
			// Busca roles
			Iterator<TipoRolesTareas> it = rolesTareas.iterator();
			while (it.hasNext()){
				TipoRolesTareas trt = it.next();
				Iterator<Element> iter = trt.getRol().getElements().iterator();
				while (iter.hasNext()){
					Element e = iter.next();
					String id = ((Struct) e.getData()).getElementID();
					if (id.equals(idElemento)){
						return e;
					}
				}
			}
		}
		else if (idTab.equals("tab3")){
			// Busca work product
			Iterator<TipoRolesWorkProducts> it = rolesWP.iterator();
			while (it.hasNext()){
				boolean encontre = false;
				TipoRolesWorkProducts trt = it.next();
				if (trt.getResponsableDe() != null){
					Iterator<DefaultDiagramModel> iterR = trt.getResponsableDe().iterator();
					while (iterR.hasNext()){
						DefaultDiagramModel resp = iterR.next();
						Iterator<Element> iterResp = resp.getElements().iterator();
						while (iterResp.hasNext() && !encontre){
							Element e = iterResp.next();
							String id = ((Struct) e.getData()).getElementID();
							if (id.equals(idElemento)){
								encontre = true;
								return e;
							}
						}
					}
				}
				if (trt.getModifica() != null){
					Iterator<DefaultDiagramModel> iterM = trt.getModifica().iterator();
					while (iterM.hasNext()){
						DefaultDiagramModel mod = iterM.next();
						Iterator<Element> iterMod = mod.getElements().iterator();
						while (iterMod.hasNext() && !encontre){
							Element e = iterMod.next();
							String id = ((Struct) e.getData()).getElementID();
							if (id.equals(idElemento)){
								encontre = true;
								return e;
							}
						}
					}
				}
			}
		}
		
		return null;
	}

	public Struct buscarPV (String idElemSeleccionado, List<Struct> list) {
		Iterator<Struct> iterator = list.iterator();
		Struct res = null;
		
        while (iterator.hasNext() && (res == null)){
        	Struct s = iterator.next();
        	if((iam.esPuntoDeVariacion(s.getType())) && (s.getElementID().equals(idElemSeleccionado))){
        		res = s;
        	}
        	else 
        	{
        		if (s.getHijos().size() > 0){
        			res = buscarPV(idElemSeleccionado, s.getHijos());
        		}
        		if (res == null){
	        		if (s.getVariantes().size() > 0){
	        			Iterator<Variant> itv = s.getVariantes().iterator();
	        			while (itv.hasNext() && (res==null)){
	        				Variant v = itv.next();
	        				if (v.getHijos() != null){
	        					res = buscarPV(idElemSeleccionado, v.getHijos());
	        				}
	        			}
	        			
	        		}
        		}
        		
        	}
        }
        
        return res;
	}
	
	public Struct buscarPadre (String idHijo, List<Struct> list) {
		Iterator<Struct> iterator = list.iterator();
		Struct res = null;
		
        while (iterator.hasNext() && (res == null)){
        	Struct s = iterator.next();
        	if((s.getElementID().equals(idHijo))){
        		res = s;
        	}
        	else if (s.getHijos().size() > 0){
        		res = buscarPadre(idHijo, s.getHijos());
        	}
        }
        
        return res;
	}

	public void cargarDatos(List<Struct> lista){
		Iterator<Struct> it = lista.iterator();
	    while (it.hasNext()){
        	Struct s = it.next();
        	TipoElemento tipo = s.getType();
    		if ((tipo == TipoElemento.TASK) || (tipo == TipoElemento.VP_TASK)){
    			if (!s.getPerformedPrimaryBy().equals("")){
        			if (rolesTareasPrimary.containsKey(s.getPerformedPrimaryBy())){
        				Iterator<Struct> itStruct = rolesTareasPrimary.get(s.getPerformedPrimaryBy()).iterator();
        				boolean fin = false;
        				while (itStruct.hasNext() && !fin){
        					Struct st = (Struct) itStruct.next();
        					fin = st.getNombre().equals(s.getNombre());
        				}
        				if (!fin){ // La tarea no está => La agrego
        					rolesTareasPrimary.get(s.getPerformedPrimaryBy()).add(s);
        				}
        			}
        			else{
        				List<Struct> list = new ArrayList<Struct>();
        				list.add(s);
        				rolesTareasPrimary.put(s.getPerformedPrimaryBy(), list);
        			}
        		}
        		if (s.getPerformedAditionallyBy() != null){
        			Iterator<String> it1 = s.getPerformedAditionallyBy().iterator();
        			while (it1.hasNext()){
        				String rol = it1.next();
	        			if (rolesTareasAdditionally.containsKey(rol)){
	        				Iterator<Struct> itStruct = rolesTareasAdditionally.get(rol).iterator();
	        				boolean fin = false;
	        				while (itStruct.hasNext() && !fin){
	        					Struct st = (Struct) itStruct.next();
	        					fin = st.getNombre().equals(s.getNombre());
	        				}
	        				if (!fin){ // La tarea no está => La agrego
	        					rolesTareasAdditionally.get(rol).add(s);
	        				}
	        			}
	        			else{
	        				List<Struct> list = new ArrayList<Struct>();
	        				list.add(s);
	        				rolesTareasAdditionally.put(rol, list);
	        			}
        			}
        		}
        		if (s.getMandatoryInputs() != null){
        			Iterator<String> it1 = s.getMandatoryInputs().iterator();
        			while (it1.hasNext()){
        				String wp = it1.next();
	        			if (tareasWPMandatoryInputs.containsKey(s.getNombre())){
	        				List<String> idsWps = tareasWPMandatoryInputs.get(s.getNombre());
	        				// Si el wp no está => Lo agrego
	        				if (!idsWps.contains(wp)){
	        					idsWps.add(wp);
	        				}
	        			}
	        			else{
	        				List<String> list = new ArrayList<String>();
	        				list.add(wp);
	        				tareasWPMandatoryInputs.put(s.getNombre(), list);
	        			}
        			}
        			
        		}
        		if (s.getOptionalInputs() != null){
        			Iterator<String> it1 = s.getOptionalInputs().iterator();
        			while (it1.hasNext()){
        				String wp = it1.next();
	        			if (tareasWPOptionalInputs.containsKey(s.getNombre())){
	        				List<String> idsWps = tareasWPOptionalInputs.get(s.getNombre());
	        				// Si el wp no está => Lo agrego
	        				if (!idsWps.contains(wp)){
	        					idsWps.add(wp);
	        				}
	        			}
	        			else{
	        				List<String> list = new ArrayList<String>();
	        				list.add(wp);
	        				tareasWPOptionalInputs.put(s.getNombre(), list);
	        			}
        			}
        		}
        		if (s.getExternalInputs() != null){
        			Iterator<String> it1 = s.getExternalInputs().iterator();
        			while (it1.hasNext()){
        				String wp = it1.next();
	        			if (tareasWPExternalInputs.containsKey(s.getNombre())){
	        				List<String> idsWps = tareasWPExternalInputs.get(s.getNombre());
	        				// Si el wp no está => Lo agrego
	        				if (!idsWps.contains(wp)){
	        					idsWps.add(wp);
	        				}
	        			}
	        			else{
	        				List<String> list = new ArrayList<String>();
	        				list.add(wp);
	        				tareasWPExternalInputs.put(s.getNombre(), list);
	        			}
        			}
        		}
        		if (s.getOutputs() != null){
        			Iterator<String> it1 = s.getOutputs().iterator();
        			while (it1.hasNext()){
        				String wp = it1.next();
	        			if (tareasWPOutputs.containsKey(s.getNombre())){
	        				List<String> idsWps = tareasWPOutputs.get(s.getNombre());
	        				// Si el wp no está => Lo agrego
	        				if (!idsWps.contains(wp)){
	        					idsWps.add(wp);
	        				}
	        			}
	        			else{
	        				List<String> list = new ArrayList<String>();
	        				list.add(wp);
	        				tareasWPOutputs.put(s.getNombre(), list);
	        			}
        			}
        		}
        	}
        	if ((tipo == TipoElemento.ROLE) || (tipo == TipoElemento.VP_ROLE)){
        		if (s.getResponsableDe() != null){
        			Iterator<String> it1 = s.getResponsableDe().iterator();
        			while (it1.hasNext()){
        				String wp = it1.next();
	        			if (rolesWPResponsable.containsKey(s.getNombre())){
	        				List<String> responsableDe = rolesWPResponsable.get(s.getNombre());
	        				// Si el wp no está => Lo agrego
	        				if (!responsableDe.contains(wp)){
	        					responsableDe.add(wp);
	        				}
	        			}
	        			else{
	        				List<String> list = new ArrayList<String>();
	        				list.add(wp);
	        				rolesWPResponsable.put(s.getNombre(), list);
	        			}
        			}
        		}
        		
        		if (s.getModifica() != null){
        			Iterator<String> it1 = s.getModifica().iterator();
        			while (it1.hasNext()){
        				String wp = it1.next();
	        			if (rolesWPModifica.containsKey(s.getNombre())){
	        				rolesWPModifica.get(s.getNombre()).add(wp);
	        			}
	        			else{
	        				List<String> list = new ArrayList<String>();
	        				list.add(wp);
	        				rolesWPModifica.put(s.getNombre(), list);
	        			}
        			}
        		}
        	}
        	if (s.getHijos()!= null){
        		cargarDatos (s.getHijos());
        	}
        	
        	if (iam.esPuntoDeVariacion(tipo)){
        		 List<Variant> variantes = s.getVariantes();
        		 Iterator<Variant> itv = variantes.iterator();
        		 while (itv.hasNext()){
        			 Variant v = itv.next();
        			 if (v.getHijos() != null){
        				 cargarDatos(v.getHijos());
        			 }
        		 }
        	}
    	}
	}

}

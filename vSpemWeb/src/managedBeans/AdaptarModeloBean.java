package managedBeans;

import java.io.File;
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
import dataTypes.TipoElemento;
import dataTypes.TipoEtiqueta;
import dataTypes.TipoRolesTareas;
import dataTypes.TipoRolesWorkProducts;
import dataTypes.TipoTareasWorkProducts;
import dominio.Struct;
import dominio.Variant;
import logica.XMIParser;

@ManagedBean
@SessionScoped
public class AdaptarModeloBean {
    
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
	
	String idTab = "tab1";
	private List<TipoRolesTareas> rolesTareas;
	private List<TipoRolesWorkProducts> rolesWP;
	private List<TipoTareasWorkProducts> tareasWP;

	@PostConstruct
    public void init() {
    	nodos = new ArrayList<Struct>();
    	variantes = new ArrayList<SelectItem>();
    	variantesSeleccionadas = null;
    	this.puntosDeVariacion = new HashMap<String, String[]>();

    	this.rolesTareasPrimary = new HashMap<String, List<Struct>>();
    	this.rolesTareasAdditionally = new HashMap<String, List<Struct>>();
    	this.rolesWPResponsable = new HashMap<String, List<String>>();
    	this.rolesWPModifica = new HashMap<String, List<String>>();
    	this.tareasWPMandatoryInputs = new HashMap<String, List<String>>();
    	this.tareasWPOptionalInputs = new HashMap<String, List<String>>();
    	this.tareasWPExternalInputs = new HashMap<String, List<String>>();
    	this.tareasWPOutputs = new HashMap<String, List<String>>();
    	
    	this.restriccionesPV = new HashMap<String, String>();
    	erroresModeloFinal = new ArrayList<String[]>();
    	this.y = Constantes.yInicial;
        crearModelo();
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

	public DefaultDiagramModel getModeloAdaptado() {
		return modeloAdaptado;
	}

	public void setModeloAdaptado(DefaultDiagramModel modelo) {
		this.modeloAdaptado = modelo;
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
			else if (idTab.equals("tab4")){
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
			String nomArchivo = Constantes.destinoDescargas + vb.getNombreArchivo();
		   	this.nodos = XMIParser.getElementXMI(nomArchivo);
		   	// Luego de parsear el proceso elimino el archivo
		   	File f = new File(nomArchivo);
		   	if (f.isFile()){
		   		f.delete();
		   	}
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
	        while (itn.hasNext() && (raiz == null)){
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
	        
	        Struct r = new Struct(raiz.getElementID(), raiz.getNombre(), t, Constantes.min_default, Constantes.max_default, XMIParser.obtenerIconoPorTipo(t), raiz.getProcessComponentId(), raiz.getProcessComponentName(), raiz.getPresentationId(), raiz.getElementIDExtends());
	        r.setDescription(raiz.getDescription());
	        r.setBriefDescription(raiz.getBriefDescription());
	        r.setPresentationName(raiz.getPresentationName());
	        r.setIdTask(raiz.getIdTask());
	        r.setHijos(raiz.getHijos());
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
	        	// Si en un wp me fijo en los roles
	        	if ((tipo == TipoElemento.ROLE) || (tipo == TipoElemento.VP_ROLE)){
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
	        			while(it1.hasNext()){
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
	        	else if ((tipo != TipoElemento.WORK_PRODUCT) && (tipo != TipoElemento.VP_WORK_PRODUCT)){
		        	Element padre = new Element(s, x + "em", this.y + "em");
			        EndPoint endPointP1_T = crearEndPoint(EndPointAnchor.TOP);
			        padre.addEndPoint(endPointP1_T);
			        padre.setDraggable(false);
			        modelo.addElement(padre);
			        modelo.connect(crearConexion(endPointRoot, endPointP1_T));
			        String etiqueta = obtenerEtiquetaParaModelo(r, s);
			        s.setEtiqueta(etiqueta);
		        	x += s.getNombre().length() / 2.0 + Constantes.distanciaEntreElemsMismoNivel;
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
		        			while(it1.hasNext()){
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
		        			while(it1.hasNext()){
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
		        	buscoVPRolesEnHijos(s.getHijos());
				}
	        }
	        root.setX(x/2 + "em");
	        this.y += Constantes.distanciaEntreNiveles;
		}
		
		if (modelo.getElements().size() > 0){
    		this.crearModeloFinal(this.modelo);
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
	    		// Creo la variante
	    		Variant v = buscarVariante(nodos, this.variantesSeleccionadas[i]);
	    		String nombreVariante = v.getName();
				String tipoVariante = v.getVarType();
	    		String idVariante = this.variantesSeleccionadas[i];
	    		List<Struct> hijos = v.getHijos();
	    		String presentationName = v.getPresentationName();
	    		String processComponentId = v.getProcessComponentId();
	    		String processComponentName = v.getProcessComponentName();
	    		String presentationId = v.getPresentationId();
	    		String idExtends = v.getElementIDExtends();
	    		
	    		TipoElemento tipo = XMIParser.obtenerTipoElemento(tipoVariante);
	    		String iconoVariante = XMIParser.obtenerIconoPorTipo(tipo);
				Element hijo = new Element(new Struct(idVariante, nombreVariante, tipo, Constantes.min_default, Constantes.max_default, iconoVariante, processComponentId, processComponentName, presentationId, idExtends), x + "em", y + "em");
				Struct s = (Struct) hijo.getData();
				s.setHijos(hijos);
				s.setPresentationName(presentationName);
				s.setDescription(v.getDescription());
				s.setBriefDescription(v.getBriefDescription());
				s.setIdTask(v.getIdTask());
				
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
	        	
	        	if ((tipo != TipoElemento.WORK_PRODUCT) && (tipo != TipoElemento.VP_WORK_PRODUCT)){
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
	        		
	        		// Si NO es para la vista previa o si NO es un punto de variación, lo agrego al modelo
		        	else if ((!esVistaPrevia) || (s.getVariantes().size() == 0)){
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
			        			Struct st = new Struct(var.getID(), var.getName(), tipoVar, Constantes.min_default, Constantes.max_default, iconoVar, var.getProcessComponentId(), var.getProcessComponentName(), var.getPresentationId(), var.getElementIDExtends());
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
			    	        	
			    	        	st.setDescription(var.getDescription());
			    	        	st.setBriefDescription(var.getBriefDescription());
			    	        	st.setIdTask(var.getIdTask());
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
				}
				else if (idTab.equals("tab4")){
				}
			}
    	}
	}

	public void actualizarVariantesParaPV(){
		String clave = ((Struct) this.puntoVariacionAdaptado.getData()).getElementID();
		this.puntosDeVariacion.put(clave, this.variantesSeleccionadas);
		this.restriccionesPV.put(clave, ""); //.replace(clave, ""); // Si estoy agregando las variantes es porque cumple las restricciones
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
				Struct rol = buscarRolPorId(idRol, nodos);
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
				Struct rol = buscarRolPorId(idRol, nodos);
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
		    		// Creo la variante
		    		Variant v = buscarVariante(nodos, this.variantesSeleccionadas[i]);
		    		if (v != null){
			    		String nombreVariante = v.getName();
						String tipoVariante = v.getVarType();
			    		String idVariante = this.variantesSeleccionadas[i];
			    		List<Struct> hijos = v.getHijos();
			    		String processComponentId = v.getProcessComponentId();
			    		String processComponentName = v.getProcessComponentName();
			    		String presentationId = v.getPresentationId();
			    		String idExtends = v.getElementIDExtends();
			    		
			    		TipoElemento tipo = XMIParser.obtenerTipoElemento(tipoVariante);
			    		String iconoVariante = XMIParser.obtenerIconoPorTipo(tipo);
						Element hijo = new Element(new Struct(idVariante, nombreVariante, tipo, Constantes.min_default, Constantes.max_default, iconoVariante, processComponentId, processComponentName, presentationId, idExtends), x + "em", y + "em");
						Struct s = (Struct) hijo.getData();
						s.setHijos(hijos);
			    		EndPoint endPointH1 = crearEndPoint(EndPointAnchor.TOP);
			    		hijo.addEndPoint(endPointH1);
			    		hijo.setDraggable(false);
			    		
				        modeloRolesTareas.addElement(hijo);
				        
				        // Creo el endPoint del punto de variación
				        EndPoint endPointPV_B = crearEndPoint(EndPointAnchor.BOTTOM);
				        this.puntoVariacionAdaptado.addEndPoint(endPointPV_B);
				        
				        // Conecto el punto de variación con la variante
				        modeloRolesTareas.connect(crearConexion(endPointPV_B, endPointH1));
				        s.setEtiqueta(((Struct) this.puntoVariacionAdaptado.getData()).getEtiqueta());
				        
				        s.setDescription(v.getDescription());
				        s.setBriefDescription(v.getBriefDescription());
				        s.setIdTask(v.getIdTask());
				        
				        x +=  nombreVariante.length() / 2.0 + Constantes.distanciaEntreElemsMismoNivel;
			    	}
		    	}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }

	/*** Modelo Roles WorkProducts ***/
	
	public DefaultDiagramModel crearModeloParaWPS(List<String> wps){
		DefaultDiagramModel modeloWps = new DefaultDiagramModel();
		modeloWps.setMaxConnections(-1);
	   	
    	FlowChartConnector conector = new FlowChartConnector();
    	conector.setPaintStyle("{strokeStyle:'#404a4e', lineWidth:2}");
    	conector.setHoverPaintStyle("{strokeStyle:'#20282b'}");
    	conector.setAlwaysRespectStubs(true);
    	modeloWps.setDefaultConnector(conector);
    	float x = 0;
    	float y = 0;
    	// Para cada wp lo busco y lo agrego
    	Iterator<String> itWP = wps.iterator();
    	List<String> wpAgregados = new ArrayList<String>();
    	while (itWP.hasNext()){
    		String wp = itWP.next();
    		Struct wpS = buscarWP(wp, nodos);
    		if (wpS != null){
    			if (!wpAgregados.contains(wpS.getNombre())){
    				wpAgregados.add(wpS.getNombre());
			        Element root = new Element(wpS, x + "em", y + "em");
			        root.setDraggable(false);
			        modeloWps.addElement(root);
			        x += wpS.getNombre().length();
    			}
    		}
    	}
    	return modeloWps;
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
				List<Struct> roles = buscarRolPorNombre(nomRol, nodos);
				Iterator<Struct> itRoles = roles.iterator();
		       	while (itRoles.hasNext()){
		       		Struct rol = itRoles.next();
		       		// Crear modelo para wps
		       		DefaultDiagramModel wpsModel = crearModeloParaWPS(wps);
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
								DefaultDiagramModel responsable = trt.getResponsableDe();
								if (responsable != null){
			       					List<Element> resp = responsable.getElements();
									Iterator<Element> itWs = wpsModel.getElements().iterator();
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
											resp.add(wp);
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
				//Struct rol = buscarRolPorId(idRol, nodos);
				List<Struct> roles = buscarRolPorNombre(nomRol, nodos);
				Iterator<Struct> itRoles = roles.iterator();
		       	while (itRoles.hasNext()){
		       		Struct rol = itRoles.next();
		       		// crear modelo para wps
		       		DefaultDiagramModel wpsModel = crearModeloParaWPS(wps);
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
			       				DefaultDiagramModel modifica = trt.getModifica();
			       				if (modifica != null) {
			       					List<Element> modif = modifica.getElements();
									Iterator<Element> itWs = wpsModel.getElements().iterator();
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
											modif.add(wp);
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
				DefaultDiagramModel responsable = trt.getResponsableDe();
				DefaultDiagramModel modifica = trt.getModifica();
				// busco idPuntoVariacionAdaptado en los modelos
				boolean encontre = false;
				if (responsable != null){
					Iterator<Element> itResp = responsable.getElements().iterator();
					while(itResp.hasNext() && !encontre){
						Element e = itResp.next();
						Struct s = (Struct) e.getData();
						if (s.getElementID().equals(idPuntoVariacionAdaptado)){
							encontre = true;
							modeloRolesWPS = responsable;
							estaVP = true;
						}
					}
				}
				if (!encontre){
					if (modifica != null){
						Iterator<Element> itMod = modifica.getElements().iterator();
						while(itMod.hasNext()){
							Element e = itMod.next();
							Struct s = (Struct) e.getData();
							if (s.getElementID().equals(idPuntoVariacionAdaptado)){
								modeloRolesWPS = modifica;
								estaVP = true;
							}
						}
					}
				}
			}
			
			if (modeloRolesWPS != null){
		    	for (int i = 0; i < cantVariantes; i++){
		    		// Creo la variante
		    		Variant v = buscarVariante(nodos, this.variantesSeleccionadas[i]);
		    		String nombreVariante = v.getName();
					String tipoVariante = v.getVarType();
		    		String idVariante = this.variantesSeleccionadas[i];
		    		List<Struct> hijos = v.getHijos();
		    		String processComponentId = v.getProcessComponentId();
		    		String processComponentName = v.getProcessComponentName();
		    		String presentationId = v.getPresentationId();
		    		String idExtends = v.getElementIDExtends();
		    		
		    		TipoElemento tipo = XMIParser.obtenerTipoElemento(tipoVariante);
		    		String iconoVariante = XMIParser.obtenerIconoPorTipo(tipo);
					Element hijo = new Element(new Struct(idVariante, nombreVariante, tipo, Constantes.min_default, Constantes.max_default, iconoVariante, processComponentId, processComponentName, presentationId, idExtends), x + "em", y + "em");
					Struct s = (Struct) hijo.getData();
					s.setHijos(hijos);
					s.setDescription(v.getDescription());
					s.setBriefDescription(v.getBriefDescription());
					s.setIdTask(v.getIdTask());
					
		    		EndPoint endPointH1 = crearEndPoint(EndPointAnchor.TOP);
		    		hijo.addEndPoint(endPointH1);
		    		hijo.setDraggable(false);
		    		
		    		modeloRolesWPS.addElement(hijo);
			        
			        // Creo el endPoint del punto de variación
			        EndPoint endPointPV_B = crearEndPoint(EndPointAnchor.BOTTOM);
			        this.puntoVariacionAdaptado.addEndPoint(endPointPV_B);
			        
			        // Conecto el punto de variación con la variante
			        modeloRolesWPS.connect(crearConexion(endPointPV_B, endPointH1));
			        
			        x +=  nombreVariante.length() / 2.0 + Constantes.distanciaEntreElemsMismoNivel;
		    	}
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
				Struct wpS = buscarWP(wp, nodos);
				if ((wps != null) && (!wpAgregados.contains(wpS.getNombre()))){
					wpAgregados.add(wpS.getNombre());
					mandatoryInputs.add(wpS);
				}
			}
			if ((nomTarea != null) && (!nomTarea.equals(""))){
				List<Struct> tareas = buscarTareaPorNombre(nomTarea, nodos);
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
				Struct wpS = buscarWP(wp, nodos);
				if ((wps != null) && (!wpAgregados.contains(wpS.getNombre()))){
					wpAgregados.add(wpS.getNombre());
					optionalInputs.add(wpS);
				}
			}
			if ((nomTarea != null) && (!nomTarea.equals(""))){
				List<Struct> tareas = buscarTareaPorNombre(nomTarea, nodos);
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
				Struct wpS = buscarWP(wp, nodos);
				if ((wps != null) && (!wpAgregados.contains(wpS.getNombre()))){
					wpAgregados.add(wpS.getNombre());
					externalInputs.add(wpS);
				}
			}
			if ((nomTarea != null) && (!nomTarea.equals(""))){
				List<Struct> tareas = buscarTareaPorNombre(nomTarea, nodos);
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
				Struct wpS = buscarWP(wp, nodos);
				if ((wps != null) && (!wpAgregados.contains(wpS.getNombre()))){
					wpAgregados.add(wpS.getNombre());
					outputs.add(wpS);
				}
			}
			if ((nomTarea != null) && (!nomTarea.equals(""))){
				List<Struct> tareas = buscarTareaPorNombre(nomTarea, nodos);
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
					Struct newS = new Struct(s.getElementID(), s.getNombre(), s.getType(), s.getMin(), s.getMax(), s.getImagen(), s.getProcessComponentId(), s.getProcessComponentName(), s.getPresentationId(), s.getElementIDExtends());
					newS.setDescription(s.getDescription());
					newS.setBriefDescription(s.getBriefDescription());
					newS.setPresentationName(s.getPresentationName());
					newS.setIdTask(s.getIdTask());
					newS.setHijos(s.getHijos());
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
								Struct newS = new Struct(v.getID(), v.getName(), newType, Constantes.min_default, Constantes.max_default, XMIParser.obtenerIconoPorTipo(newType), v.getProcessComponentId(), v.getProcessComponentName(), v.getPresentationId(), v.getElementIDExtends());
								newS.setHijos(v.getHijos());
								newS.setDescription(v.getDescription());
								newS.setBriefDescription(v.getBriefDescription());
								newS.setPresentationName(v.getPresentationName());
								newS.setIdTask(v.getIdTask());
								///
								// Seteo las variantes
								List<Variant> lstVariantes = s.getVariantes();
								if (lstVariantes.size() > 0){
									ArrayList<Variant> variantesNewS = new ArrayList<Variant>();
									Iterator<Variant> itVariantes = lstVariantes.iterator();
									while (itVariantes.hasNext()){
										Variant vrt = itVariantes.next();
										Variant newVariant = crearCopiaVariante(vrt);
										variantesNewS.add(newVariant);
									}
									newS.setVariantes(variantesNewS);
								}
								//
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
		Struct newS = new Struct(s.getElementID(), s.getNombre(), s.getType(), s.getMin(), s.getMax(), s.getImagen(), s.getProcessComponentId(), s.getProcessComponentName(), s.getPresentationId(), s.getElementIDExtends());
		
		newS.setDescription(s.getDescription());
		newS.setBriefDescription(s.getBriefDescription());
		newS.setPresentationName(s.getPresentationName());
		newS.setIdTask(s.getIdTask());
		
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
		
		newS.setResponsableDe(s.getResponsableDe());
		newS.setModifica(s.getModifica());
		
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
		Variant newV = new Variant(v.getID(), v.getName(), v.getPresentationName(), v.getIDVarPoint(), v.isInclusive(), v.getVarType(), v.getProcessComponentId(), v.getProcessComponentName(), v.getPresentationId(), v.getElementIDExtends());
		
		newV.setDescription(v.getDescription());
		newV.setBriefDescription(v.getBriefDescription());
		newV.setIdTask(v.getIdTask());
		
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
		        if (s.getType() != TipoElemento.DELIVERY_PROCESS){
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
					Iterator<Element> iterResp = trt.getResponsableDe().getElements().iterator();
					while (iterResp.hasNext() && !encontre){
						Element e = iterResp.next();
						String id = ((Struct) e.getData()).getElementID();
						if (id.equals(idElemento)){
							encontre = true;
							return e;
						}
					}
				}
				
				if (trt.getModifica() != null){
					if (!encontre){ //busco en el modelo modifica
						Iterator<Element> iterMod = trt.getModifica().getElements().iterator();
						while (iterMod.hasNext() && !encontre){
							Element e = iterMod.next();
							String id = ((Struct) e.getData()).getElementID();
							if (id.equals(idElemento)){
								return e;
							}
						}
					}
				}
			}
		}
		else if (idTab.equals("tab4")){
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

	public Struct buscarRolPorId (String idElemSeleccionado, List<Struct> list) {
		Iterator<Struct> iterator = list.iterator();
		Struct res = null;
		
        while (iterator.hasNext() && (res == null)){
        	Struct s = iterator.next();
        	if((s.getType() == TipoElemento.VP_ROLE || s.getType() == TipoElemento.ROLE) &&
        	   (s.getElementID().equals(idElemSeleccionado))){
        		res = s;
        	}
        	else if (s.getHijos().size() > 0){
        		res = buscarRolPorId(idElemSeleccionado, s.getHijos());
        	}
        }
        return res;
	}
	
	public List<Struct> buscarRolPorNombre (String nombreElemSeleccionado, List<Struct> list) {
		Iterator<Struct> iterator = list.iterator();
		List<Struct> res = new ArrayList<Struct>();
		
        while (iterator.hasNext()){
        	Struct s = iterator.next();
        	if((s.getType() == TipoElemento.VP_ROLE || s.getType() == TipoElemento.ROLE) &&
        	   (s.getNombre().equals(nombreElemSeleccionado))){
        		res.add(s);
        	}
        	else if (s.getHijos().size() > 0){
        		List<Struct> resHijos = buscarRolPorNombre(nombreElemSeleccionado, s.getHijos());
        		res.addAll(resHijos);
        	}
        }
        return res;
	}

	public Struct buscarTareaPorId (String idElemSeleccionado, List<Struct> list) {
		Iterator<Struct> iterator = list.iterator();
		Struct res = null;
		
        while (iterator.hasNext() && (res == null)){
        	Struct s = iterator.next();
        	if ((s.getType() == TipoElemento.VP_TASK || s.getType() == TipoElemento.TASK) &&
        	   (s.getElementID().equals(idElemSeleccionado))){
        		res = s;
        	}
        	else if (s.getHijos().size() > 0){
        		res = buscarTareaPorId(idElemSeleccionado, s.getHijos());
        	}
        }
        return res;
	}

	public List<Struct> buscarTareaPorNombre (String nombreElemSeleccionado, List<Struct> list) {
		Iterator<Struct> iterator = list.iterator();
		List<Struct> res = new ArrayList<Struct>();
		
        while (iterator.hasNext()){
        	Struct s = iterator.next();
        	if ((s.getType() == TipoElemento.VP_TASK || s.getType() == TipoElemento.TASK) &&
        	   (s.getNombre().equals(nombreElemSeleccionado))){
        		res.add(s);
        	}
        	else if (s.getHijos().size() > 0){
        		List<Struct> resHijos = buscarTareaPorNombre(nombreElemSeleccionado, s.getHijos());
        		res.addAll(resHijos);
        	}
        }
        return res;
	}

	public Struct buscarWP (String idElemSeleccionado, List<Struct> list) {
		Iterator<Struct> iterator = list.iterator();
		Struct res = null;
		
        while (iterator.hasNext() && (res == null)){
        	Struct s = iterator.next();
        	if((s.getType() == TipoElemento.VP_WORK_PRODUCT || s.getType() == TipoElemento.WORK_PRODUCT) &&
        	   (s.getElementID().equals(idElemSeleccionado))){
        		res = s;
        	}
        	else if (s.getHijos().size() > 0){
        		res = buscarWP(idElemSeleccionado, s.getHijos());
        	}
        }
        return res;
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

}

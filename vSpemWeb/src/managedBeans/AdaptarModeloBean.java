package managedBeans;

import java.util.ArrayList;
import java.util.Arrays;
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
import dataTypes.TipoEtiqueta;
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
	
	public DefaultDiagramModel getModeloAdaptado() {
		return modeloAdaptado;
	}

	public void setModeloAdaptado(DefaultDiagramModel modeloAdaptado) {
		this.modeloAdaptado = modeloAdaptado;
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
	        
	        Struct r = new Struct(raiz.getElementID(), raiz.getNombre(), t, Constantes.min_default, Constantes.max_default, XMIParser.obtenerIconoPorTipo(t));
	        r.setDescription(raiz.getDescription());
	        r.setPresentationName(raiz.getPresentationName());
	        Element root = new Element(r);
	        
	        root.setY(this.y + "em");
	        EndPoint endPointRoot = crearEndPoint(EndPointAnchor.BOTTOM);
	        root.addEndPoint(endPointRoot);
        	modelo.addElement(root);
	        
        	this.y += Constantes.distanciaEntreNiveles;
        	float x = 0;
	        Iterator<Struct> it = this.nodos.iterator();
	        while (it.hasNext()){
	        	Struct s = it.next();
				
	        	Element padre = new Element(s, x + "em", this.y + "em");
		        EndPoint endPointP1_T = crearEndPoint(EndPointAnchor.TOP);
		        padre.addEndPoint(endPointP1_T);
		        modelo.addElement(padre);
		        modelo.connect(crearConexion(endPointRoot, endPointP1_T));
		        String etiqueta = obtenerEtiquetaParaModelo(r, s);
		        s.setEtiqueta(etiqueta);
	        	x += s.getNombre().length() / 2.0 + Constantes.distanciaEntreElemsMismoNivel;
	        	
	        	//mostrarHijos(padre, modelo);
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
				
	        	Element hijo = new Element(s, x + "em", y + "em");
		        EndPoint endPointHijo = crearEndPoint(EndPointAnchor.TOP);
		        hijo.addEndPoint(endPointHijo);
		        modelo.addElement(hijo);
		        modelo.connect(crearConexion(endPointPadre, endPointHijo));
		        String etiqueta = obtenerEtiquetaParaModelo(p, s);
		        s.setEtiqueta(etiqueta);
	        	x += s.getNombre().length() / 2.0 + Constantes.distanciaEntreElemsMismoNivel;
	        	
	        	if (esVistaPrevia){
	        		mostrarHijos(hijo, modelo, esVistaPrevia);
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
	    		ocultarHijos(e, modelo);
	    		modelo.removeElement(e);
	    	}
    		s.setEstaExpandido(false);
        }
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
		Struct s = buscarVP(idElemSeleccionado,this.nodos);
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
	}
	
	public void redibujarModelo() {
		try{
	    	int cantVariantes = this.variantesSeleccionadas.length;
	    	String xStr = this.puntoVariacionAdaptado.getX();
			float xIni = Float.valueOf(xStr.substring(0, xStr.length() - 2));
			float x = (cantVariantes > 1) ? xIni - (xIni / cantVariantes) : xIni;
	    	for (int i = 0; i < cantVariantes; i++){
	    		// Creo la variante
	
	    		Variant v = buscarVariante(nodos, this.variantesSeleccionadas[i]);
	    		String nombreVariante = v.getName();
				String tipoVariante = v.getVarType();
	    		String idVariante = this.variantesSeleccionadas[i];
	    		List<Struct> hijos = v.getHijos();
	    		
	    		TipoElemento tipo = XMIParser.obtenerTipoElemento(tipoVariante);
	    		String iconoVariante = XMIParser.obtenerIconoPorTipo(tipo);
				Element hijo = new Element(new Struct(idVariante, nombreVariante, tipo, Constantes.min_default, Constantes.max_default, iconoVariante), x + "em", this.y + "em");
				Struct s = (Struct) hijo.getData();
				s.setHijos(hijos);
	    		EndPoint endPointH1 = crearEndPoint(EndPointAnchor.TOP);
	    		hijo.addEndPoint(endPointH1);
		        modelo.addElement(hijo);
		        
		        // Creo el endPoint del punto de variación
		        EndPoint endPointPV_B = crearEndPoint(EndPointAnchor.BOTTOM);
		        this.puntoVariacionAdaptado.addEndPoint(endPointPV_B);
		        
		        // Conecto el punto de variación con la variante
		        modelo.connect(crearConexion(endPointPV_B, endPointH1));
		        String etiqueta = obtenerEtiquetaParaModelo((Struct) this.puntoVariacionAdaptado.getData(), s);
		        s.setEtiqueta(etiqueta);
	        	
		        x +=  nombreVariante.length() / 2.0 + Constantes.distanciaEntreElemsMismoNivel;
		        
		        // Dibujo los hijos de la variante
		        // mostrarHijos(hijo, modelo);
	    	}
	    	
	    	if (cantVariantes > 0){
	    		this.crearModeloFinal(this.modelo);
	    	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public String validarSeleccion(String[] variantesSeleccionadas, Struct pv){
		String res = "";
		
		/*** Chequeo máximos y mínimos ***/
		int cantVariantesSelec = variantesSeleccionadas.length;
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

	public void vistaPrevia(){
		RequestContext c = RequestContext.getCurrentInstance();
		c.execute("PF('vistaPreviaDialog').show()");
	}

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
					newE.addEndPoint(endPointRoot);
					
					modeloAdaptado.addElement(newE);
					yElement += Constantes.distanciaEntreNiveles;
				}
				else{
					// Si es un punto de variación, recorro las variantes y agrego las que están en el modelo
					// Lo hago así porque sino se incluyen al final
					if ((type == TipoElemento.VP_ACTIVITY)  ||
		  				(type == TipoElemento.VP_PHASE)     ||
		  				(type == TipoElemento.VP_ITERATION) ||
		  				(type == TipoElemento.VP_TASK)		||
		  				(type == TipoElemento.VP_ROLE)		||
		  				(type == TipoElemento.VP_MILESTONE)		||
		  				(type == TipoElemento.VP_WORK_PRODUCT)	){
						
						Iterator<Variant> itVar = s.getVariantes().iterator();
						while (itVar.hasNext()){
							Variant v = itVar.next();
							if (variantePerteneceAModelo(v.getID(), modelo)){
								TipoElemento newType = getElementoParaVarPoint(XMIParser.obtenerTipoElemento(v.getVarType()));
								Struct newS = new Struct(v.getID(), v.getName(), newType, Constantes.min_default, Constantes.max_default, XMIParser.obtenerIconoPorTipo(newType));
								newS.setHijos(v.getHijos());
								Element newE = new Element(newS, xElement + "em", yElement + "em");
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
						if (!variantePerteneceAModelo(s.getElementID(), modeloAdaptado)){
							Struct newS = crearCopiaStruct(s);
							Element newE = new Element(newS, xElement + "em", yElement + "em");
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

	public boolean variantePerteneceAModelo(String idVar, DefaultDiagramModel modelo){
		Iterator<Element> it = modelo.getElements().iterator();
		while (it.hasNext()){
			Struct s = (Struct) it.next().getData();
			if (s.getElementID().equals(idVar)){
				return true;
			}
		}
		return false;
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
		
		return newS;
	}

	public Variant buscarVariante(List<Struct> nodos, String Id){
		Iterator<Struct> it = nodos.iterator();
		Variant res = null;
		
		while (it.hasNext() && (res == null)){
			Struct s = it.next();
			TipoElemento type = s.getType();
			if ((type == TipoElemento.VP_ACTIVITY)  ||
				(type == TipoElemento.VP_PHASE)     ||
				(type == TipoElemento.VP_ITERATION) ||
				(type == TipoElemento.VP_TASK)		||
				(type == TipoElemento.VP_MILESTONE)	||
				(type == TipoElemento.VP_ROLE)		||
				(type == TipoElemento.VP_WORK_PRODUCT)
			   ){
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

	public Struct buscarVP (String idElemSeleccionado, List<Struct> list) {
		Iterator<Struct> iterator = list.iterator();
		Struct res = null;
		
        while (iterator.hasNext() && (res == null)){
        	Struct s = iterator.next();
        	if((s.getType() == TipoElemento.VP_ACTIVITY ||
        		s.getType() == TipoElemento.VP_TASK ||
        		s.getType() == TipoElemento.VP_PHASE ||
        		s.getType() == TipoElemento.VP_ITERATION ||
        		s.getType() == TipoElemento.VP_ROLE ||
        		s.getType() == TipoElemento.VP_MILESTONE ||
        		s.getType() == TipoElemento.VP_WORK_PRODUCT) &&
        	   (s.getElementID().equals(idElemSeleccionado))
        	  ){
        		res = s;
        	}
        	else {
        		if (s.getHijos().size() > 0){
        			res = buscarVP(idElemSeleccionado, s.getHijos());
        		}
        	}
        }
        return res;
	}

}
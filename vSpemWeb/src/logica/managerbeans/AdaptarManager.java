package logica.managerbeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;

import org.primefaces.model.diagram.Element;

import config.Constantes;
import logica.dominio.Struct;
import logica.dominio.Variant;
import logica.enumerados.TipoElemento;
import logica.enumerados.TipoEtiqueta;
import logica.negocio.IAdaptarManager;
import logica.utils.Utils;
import logica.utils.XMIParser;

@Stateless
public class AdaptarManager implements IAdaptarManager{

	public String seleccionarVariantesParaPV(Element puntoVariacionAdaptado, String[] variantesSeleccionadas){
		Struct pv = ((Struct) puntoVariacionAdaptado.getData());
		//pv.setEstaExpandido(variantesSeleccionadas.length > 0);
		modificarExpandido(pv, variantesSeleccionadas.length > 0);
		return this.validarSeleccion(variantesSeleccionadas, pv);
	}

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
				Variant v = Utils.obtenerVarianteParaPV(pv, variantesSeleccionadas[i]);
				List<String> varExclusivas = v.getExclusivas();
				List<String> varInclusivas = v.getInclusivas();
				
				// Si las otras variantes seleccionadas están en las variantes exlusivas => Error
				if (varExclusivas != null){
					int j = 0;
					while ((j < cantVariantesSelec) && (res.isEmpty())){
						if ((j != i) && (varExclusivas.contains(variantesSeleccionadas[j]))){
							res = "No es posible seleccionar las variantes '" + Utils.obtenerVarianteParaPV(pv, variantesSeleccionadas[i]).getName() + "' y '" + Utils.obtenerVarianteParaPV(pv, variantesSeleccionadas[j]).getName() + "' a la vez.";
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
							res = "Debe seleccionar la variante '" + Utils.obtenerVarianteParaPV(pv, var).getName() + "'.";
						}
					}
				}
				
				i++;
			}
		}
		
		return res;
	}

	public Struct buscarElemento(String id, List<Struct> elementos, String buscarPor){
		return Utils.buscarElemento(id, elementos, buscarPor);
	}

	public Variant buscarVariante(List<Struct> nodos, String id){
		return Utils.buscarVariante(nodos, id);
	}

	public boolean esPuntoDeVariacion(TipoElemento tipo){
		return Utils.esPuntoDeVariacion(tipo);
	}

	public boolean esVariante(TipoElemento tipo){
		return Utils.esVariante(tipo);
	}

	public void modificarExpandido(Struct s, boolean expandido){
		s.setEstaExpandido(expandido);
	}
		
	public void modificarExpandido(Variant v, boolean expandido){
		v.setEstaExpandido(expandido);
	}

	public void modificarEtiqueta(Struct s, String etiqueta){
		s.setEtiqueta(etiqueta);
	}

	public Element crearVariante(List<Struct> nodos, Struct pv, String varianteSeleccionada, float x, float y){
		Variant v = Utils.buscarVariante(nodos, varianteSeleccionada);
		if (v != null){
			String nombreVariante = v.getName();
			String tipoVariante = v.getVarType();
			String idVariante = varianteSeleccionada;
			List<Struct> hijos = v.getHijos();
			String presentationName = v.getPresentationName();
			String processComponentId = v.getProcessComponentId();
			String processComponentName = v.getProcessComponentName();
			String presentationId = v.getPresentationId();
			String idExtends = v.getElementIDExtends();
			
			TipoElemento tipo = XMIParser.obtenerTipoElemento(tipoVariante);
			String iconoVariante = XMIParser.obtenerIconoPorTipo(tipo);
			Element elem = new Element(new Struct(idVariante, nombreVariante, tipo, Constantes.min_default, Constantes.max_default, iconoVariante, processComponentId, processComponentName, presentationId, idExtends), x + "em", y + "em");
			Struct s = (Struct) elem.getData();
			s.setHijos(hijos);
			s.setPresentationName(presentationName);
	        s.setProcessComponentPresentationName(v.getProcessComponentPresentationName());
			s.setGuid(v.getGuid());
			s.setIsPlanned(v.getIsPlanned());
			s.setSuperActivities(v.getSuperActivities());
			s.setIsOptional(v.getIsOptional());
			s.setVariabilityType(v.getVariabilityType());
			s.setIsSynchronizedWithSource(v.getIsSynchronizedWithSource());
			s.setDescription(v.getDescription());
			s.setBriefDescription(v.getBriefDescription());
			s.setIdTask(v.getIdTask());
			s.setIdRole(v.getIdRole());
			s.setIdWorkProduct(v.getIdWorkProduct());
	        s.setSteps(v.getSteps());
	        s.setMethodElementProperties(v.getMethodElementProperties());
	        s.setDiagramURI(v.getDiagramURI());
	        s.setEtiqueta(pv.getEtiqueta());
	        	        
    		s.setModifica(pv.getModifica());
    		s.setResponsableDe(pv.getResponsableDe());
    		s.setExternalInputs(pv.getExternalInputs());
    		s.setMandatoryInputs(pv.getMandatoryInputs());
    		s.setOptionalInputs(pv.getOptionalInputs());
    		s.setOutputs(pv.getOutputs());
    		
	        return elem;
		}
		return null;
	}

	public Struct crearStruct(Struct s){
		Struct newS = new Struct(s.getElementID(), s.getNombre(), s.getType(), s.getMin(), s.getMax(), s.getImagen(), s.getProcessComponentId(), s.getProcessComponentName(), s.getPresentationId(), s.getElementIDExtends());
		newS.setDescription(s.getDescription());
		newS.setBriefDescription(s.getBriefDescription());
		newS.setPresentationName(s.getPresentationName());
        newS.setProcessComponentPresentationName(s.getProcessComponentPresentationName());
	    newS.setGuid(s.getGuid());
	    newS.setIsPlanned(s.getIsPlanned());
	    newS.setSuperActivities(s.getSuperActivities());
	    newS.setIsOptional(s.getIsOptional());
		newS.setVariabilityType(s.getVariabilityType());
		newS.setIsSynchronizedWithSource(s.getIsSynchronizedWithSource());
		newS.setIdTask(s.getIdTask());
		newS.setIdRole(s.getIdRole());
		newS.setIdWorkProduct(s.getIdWorkProduct());
		newS.setHijos(s.getHijos());
        newS.setSteps(s.getSteps());
        newS.setMethodElementProperties(s.getMethodElementProperties());
        newS.setPredecesores(s.getPredecesores());
        newS.setDiagramURI(s.getDiagramURI());
        return newS;
	}
	
	public Struct crearStruct(Variant v, List<Variant> lstVariantes){
		TipoElemento newType = getElementoParaVarPoint(XMIParser.obtenerTipoElemento(v.getVarType()));
		Struct newS = new Struct(v.getID(), v.getName(), newType, Constantes.min_default, Constantes.max_default, XMIParser.obtenerIconoPorTipo(newType), v.getProcessComponentId(), v.getProcessComponentName(), v.getPresentationId(), v.getElementIDExtends());
		newS.setHijos(v.getHijos());
		newS.setDescription(v.getDescription());
		newS.setBriefDescription(v.getBriefDescription());
		newS.setPresentationName(v.getPresentationName());
        newS.setProcessComponentPresentationName(v.getProcessComponentPresentationName());
		newS.setGuid(v.getGuid());
		newS.setIsPlanned(v.getIsPlanned());
		newS.setSuperActivities(v.getSuperActivities());
		newS.setIsOptional(v.getIsOptional());
		newS.setVariabilityType(v.getVariabilityType());
		newS.setIsSynchronizedWithSource(v.getIsSynchronizedWithSource());
		newS.setIdTask(v.getIdTask());
		newS.setIdRole(v.getIdRole());
		newS.setIdWorkProduct(v.getIdWorkProduct());
        newS.setSteps(v.getSteps());
        newS.setMethodElementProperties(v.getMethodElementProperties());
        newS.setDiagramURI(v.getDiagramURI());
        
		// Seteo las variantes
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
		return newS;
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

	public Struct crearCopiaStruct(Struct s){
		Struct newS = new Struct(s.getElementID(), s.getNombre(), s.getType(), s.getMin(), s.getMax(), s.getImagen(), s.getProcessComponentId(), s.getProcessComponentName(), s.getPresentationId(), s.getElementIDExtends());
		
		newS.setDescription(s.getDescription());
		newS.setBriefDescription(s.getBriefDescription());
		newS.setPresentationName(s.getPresentationName());
        newS.setProcessComponentPresentationName(s.getProcessComponentPresentationName());
	    newS.setGuid(s.getGuid());
	    newS.setIsPlanned(s.getIsPlanned());
	    newS.setSuperActivities(s.getSuperActivities());
		newS.setIsOptional(s.getIsOptional());
		newS.setVariabilityType(s.getVariabilityType());
		newS.setIsSynchronizedWithSource(s.getIsSynchronizedWithSource());
		newS.setIdTask(s.getIdTask());
		newS.setIdRole(s.getIdRole());
		newS.setIdWorkProduct(s.getIdWorkProduct());
        newS.setSteps(s.getSteps());
        newS.setMethodElementProperties(s.getMethodElementProperties());
        newS.setPredecesores(s.getPredecesores());
        newS.setDiagramURI(s.getDiagramURI());
		
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
		newV.setIdRole(v.getIdRole());
		newV.setIdWorkProduct(v.getIdWorkProduct());
		
		
		newV.setIsPlanned(v.getIsPlanned());
		newV.setIsOptional(v.getIsOptional());
		newV.setVariabilityType(v.getVariabilityType());
		newV.setSuperActivities(v.getSuperActivities());
		newV.setDescription(v.getDescription());
		newV.setGuid(v.getGuid());
		newV.setIsSynchronizedWithSource(v.getIsSynchronizedWithSource());
			
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

	public List<Struct> cargarNodos(String nomArchivo){
		return XMIParser.getElementXMI(nomArchivo);
	}

	public Struct crearElementoRaiz(List<Struct> nodos){
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
        
        if (raiz != null){
	        Struct r = new Struct(raiz.getElementID(), raiz.getNombre(), t, Constantes.min_default, Constantes.max_default, XMIParser.obtenerIconoPorTipo(t), raiz.getProcessComponentId(), raiz.getProcessComponentName(), raiz.getPresentationId(), raiz.getElementIDExtends());
	        r.setDescription(raiz.getDescription());
	        r.setBriefDescription(raiz.getBriefDescription());
	        r.setPresentationName(raiz.getPresentationName());
	        r.setProcessComponentPresentationName(raiz.getProcessComponentPresentationName());
		    r.setGuid(raiz.getGuid());
		    r.setIsPlanned(raiz.getIsPlanned());
		    r.setSuperActivities(raiz.getSuperActivities());
		    r.setIsOptional(raiz.getIsOptional());
			r.setVariabilityType(raiz.getVariabilityType());
			r.setIsSynchronizedWithSource(raiz.getIsSynchronizedWithSource());
	        r.setIdTask(raiz.getIdTask());
			r.setIdRole(raiz.getIdRole());
			r.setIdWorkProduct(raiz.getIdWorkProduct());
	        r.setHijos(raiz.getHijos());
	        r.setSteps(raiz.getSteps());
	        r.setMethodElementProperties(raiz.getMethodElementProperties());
	        r.setPredecesores(raiz.getPredecesores());
	        r.setDiagramURI(raiz.getDiagramURI());
	        return r;
        }
        
        return null;
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

    public Struct buscarRolPorId(List<Struct> nodos, String idRol){
    	return Utils.buscarRolPorId(idRol, nodos);
    }
    
    public List<Struct> buscarRolPorNombre(List<Struct> nodos, String nomRol){
    	return Utils.buscarRolPorNombre(nomRol, nodos);
    }
    
    public Struct buscarWP(List<Struct> nodos, String wp){
    	return Utils.buscarWP(wp, nodos);
    }

    public List<Struct> buscarTareaPorNombre(List<Struct> nodos, String nomTarea){
    	return Utils.buscarTareaPorNombre(nomTarea, nodos);
    }

}

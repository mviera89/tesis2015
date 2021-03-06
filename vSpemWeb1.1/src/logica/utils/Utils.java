package logica.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import logica.dominio.Struct;
import logica.dominio.Variant;
import logica.enumerados.TipoElemento;

import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;

import config.Constantes;

public class Utils {

	public static boolean esPuntoDeVariacion(TipoElemento tipo){
		return (tipo == TipoElemento.VP_ACTIVITY  ||
        		tipo == TipoElemento.VP_TASK 	  ||
        		tipo == TipoElemento.VP_PHASE 	  ||
        		tipo == TipoElemento.VP_ITERATION ||
        		tipo == TipoElemento.VP_ROLE 	  ||
        		tipo == TipoElemento.VP_MILESTONE ||
        		tipo == TipoElemento.VP_WORK_PRODUCT);
	}

	public static boolean esVariante(TipoElemento tipo){
		return (tipo == TipoElemento.VAR_ACTIVITY  ||
        		tipo == TipoElemento.VAR_TASK 	  ||
        		tipo == TipoElemento.VAR_PHASE 	  ||
        		tipo == TipoElemento.VAR_ITERATION ||
        		tipo == TipoElemento.VAR_ROLE 	  ||
        		tipo == TipoElemento.VAR_MILESTONE ||
        		tipo == TipoElemento.VAR_WORK_PRODUCT);
	}

	public static Struct buscarElemento(String id, List<Struct> elementos, String buscarPor){
		if (elementos != null){
			Iterator<Struct> it = elementos.iterator();
			while (it.hasNext()){
				Struct s = it.next();
				String sId = (buscarPor.equals("idTask")) ? s.getIdTask() :
					 		 (buscarPor.equals("idWorkProduct")) ? s.getIdWorkProduct() :
			 			 	 (buscarPor.equals("idRole")) ? s.getIdRole() :
		 			 		 s.getElementID();
					 
				if ((sId != null) && (sId.equals(id))){
					return s;
				}
				Struct hijo = buscarElemento(id, s.getHijos(), buscarPor);
				if (hijo != null){
					return hijo;
				}
				Iterator<Variant> itVar = s.getVariantes().iterator();
				while (itVar.hasNext()){
					Variant var = itVar.next();
					String varId = (buscarPor.equals("idTask")) ? var.getIdTask() :
				 		 		   (buscarPor.equals("idWorkProduct")) ? var.getIdWorkProduct() :
				 		 		   (buscarPor.equals("idRole")) ? var.getIdRole() :
				 		 		   var.getID();
				 	if ((varId != null) && (varId.equals(id))){
				 		TipoElemento tipo = XMIParser.obtenerTipoElemento(var.getVarType());
			    		String iconoVariante = XMIParser.obtenerIconoPorTipo(tipo);
						Struct sVar = new Struct(var.getID(), var.getName(), tipo, Constantes.min_default, Constantes.max_default, iconoVariante, var.getProcessComponentId(), var.getProcessComponentName(), var.getPresentationId(), var.getElementIDExtends());
						sVar.setHijos(var.getHijos());
						sVar.setPresentationName(var.getPresentationName());
						sVar.setProcessComponentPresentationName(var.getProcessComponentPresentationName());
						sVar.setGuid(var.getGuid());
						sVar.setIsPlanned(var.getIsPlanned());
						sVar.setSuperActivities(var.getSuperActivities());
						sVar.setIsOptional(var.getIsOptional());
						sVar.setVariabilityType(var.getVariabilityType());
						sVar.setIsSynchronizedWithSource(var.getIsSynchronizedWithSource());
						sVar.setDescription(var.getDescription());
						sVar.setBriefDescription(var.getBriefDescription());
						sVar.setIdTask(var.getIdTask());
						sVar.setIdRole(var.getIdRole());
						sVar.setIdWorkProduct(var.getIdWorkProduct());
						sVar.setSteps(var.getSteps());
						sVar.setMethodElementProperties(var.getMethodElementProperties());
						sVar.setDiagramURI(var.getDiagramURI());
						
						sVar.setModifica(s.getModifica());
						sVar.setResponsableDe(s.getResponsableDe());
						sVar.setExternalInputs(s.getExternalInputs());
						sVar.setMandatoryInputs(s.getMandatoryInputs());
						sVar.setOptionalInputs(s.getOptionalInputs());
						sVar.setOutputs(s.getOutputs());
			    		
				 		return sVar;
				 	}
				 	Struct hijoVar = buscarElemento(id, var.getHijos(), buscarPor);
					if (hijoVar != null){
						return hijoVar;
					}
				}
			}
		}
		return null;
	}

	public static Struct crearStruct(Struct s){
		Struct newS = new Struct(s.getElementID(), s.getNombre(), s.getType(), s.getMin(), s.getMax(), s.getImagen(), s.getProcessComponentId(), s.getProcessComponentName(), s.getPresentationId(), s.getElementIDExtends());
		newS.setBriefDescription(s.getBriefDescription());
		newS.setDescription(s.getDescription());
        newS.setDiagramURI(s.getDiagramURI());
        newS.setExternalInputs(s.getExternalInputs());
	    newS.setGuid(s.getGuid());
		newS.setHijos(s.getHijos());
		newS.setIdRole(s.getIdRole());
		newS.setIdTask(s.getIdTask());
		newS.setIdWorkProduct(s.getIdWorkProduct());
	    newS.setIsOptional(s.getIsOptional());
	    newS.setIsPlanned(s.getIsPlanned());
		newS.setIsSynchronizedWithSource(s.getIsSynchronizedWithSource());
        newS.setLinkToPredecessor(s.getLinkToPredecessor());
        newS.setMandatoryInputs(s.getMandatoryInputs());
        newS.setMethodElementProperties(s.getMethodElementProperties());
        newS.setModifica(s.getModifica());
        newS.setOptionalInputs(s.getOptionalInputs());
        newS.setOutputs(s.getOutputs());
        newS.setPerformedAditionallyBy(s.getPerformedAditionallyBy());
        newS.setPerformedPrimaryBy(s.getPerformedPrimaryBy());
        newS.setPredecesores(s.getPredecesores());
		newS.setPresentationName(s.getPresentationName());
        newS.setProcessComponentPresentationName(s.getProcessComponentPresentationName());
        newS.setResponsableDe(s.getResponsableDe());
        newS.setSteps(s.getSteps());
	    newS.setSuperActivities(s.getSuperActivities());
		newS.setVariabilityType(s.getVariabilityType());
        newS.setVariantes(s.getVariantes());
        
        return newS;
	}
	
	public static Struct crearStruct(Variant v, Struct pv){
		TipoElemento newType = getElementoParaVarPoint(XMIParser.obtenerTipoElemento(v.getVarType()));
		Struct newS = new Struct(v.getID(), v.getName(), newType, Constantes.min_default, Constantes.max_default, XMIParser.obtenerIconoPorTipo(newType), v.getProcessComponentId(), v.getProcessComponentName(), v.getPresentationId(), v.getElementIDExtends());
		newS.setBriefDescription(v.getBriefDescription());
		newS.setDescription(v.getDescription());
		newS.setDiagramURI(v.getDiagramURI());
		newS.setGuid(v.getGuid());
		newS.setHijos(v.getHijos());
		newS.setIdRole(v.getIdRole());
		newS.setIdTask(v.getIdTask());
		newS.setIdWorkProduct(v.getIdWorkProduct());
		newS.setIsOptional(v.getIsOptional());
		newS.setIsPlanned(v.getIsPlanned());
		newS.setIsSynchronizedWithSource(v.getIsSynchronizedWithSource());
        newS.setMethodElementProperties(v.getMethodElementProperties());
		newS.setPresentationName(v.getPresentationName());
        newS.setProcessComponentPresentationName(v.getProcessComponentPresentationName());
        newS.setSteps(v.getSteps());
		newS.setSuperActivities(v.getSuperActivities());
		newS.setVariabilityType(v.getVariabilityType());
		
		// Agrego datos del punto de variación
		newS.setLinkToPredecessor(pv.getLinkToPredecessor());
		newS.setMandatoryInputs(pv.getMandatoryInputs());
		newS.setModifica(pv.getModifica());
		newS.setOptionalInputs(pv.getOptionalInputs());
		newS.setOutputs(pv.getOutputs());
		newS.setPerformedAditionallyBy(pv.getPerformedAditionallyBy());
		newS.setPerformedPrimaryBy(pv.getPerformedPrimaryBy());
		newS.setPredecesores(pv.getPredecesores());
		newS.setResponsableDe(pv.getResponsableDe());
		newS.setSteps(pv.getSteps());
		newS.setSuperActivities(pv.getSuperActivities());
		
		return newS;
	}

	public static TipoElemento getElementoParaVarPoint(TipoElemento type){
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

	public static Struct crearCopiaStruct(Struct s){
		Struct newS = new Struct(s.getElementID(), s.getNombre(), s.getType(), s.getMin(), s.getMax(), s.getImagen(), s.getProcessComponentId(), s.getProcessComponentName(), s.getPresentationId(), s.getElementIDExtends());

		newS.setBriefDescription(s.getBriefDescription());
		newS.setDescription(s.getDescription());
        newS.setDiagramURI(s.getDiagramURI());
        newS.setEtiqueta(s.getEtiqueta());
		newS.setExternalInputs(s.getExternalInputs());
	    newS.setGuid(s.getGuid());

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

		newS.setIdRole(s.getIdRole());
		newS.setIdTask(s.getIdTask());
		newS.setIdWorkProduct(s.getIdWorkProduct());
		newS.setIsOptional(s.getIsOptional());
	    newS.setIsPlanned(s.getIsPlanned());
		newS.setIsSynchronizedWithSource(s.getIsSynchronizedWithSource());
		newS.setLinkToPredecessor(s.getLinkToPredecessor());
		newS.setMandatoryInputs(s.getMandatoryInputs());
        newS.setMethodElementProperties(s.getMethodElementProperties());
		newS.setModifica(s.getModifica());
		newS.setOptionalInputs(s.getOptionalInputs());
		newS.setOutputs(s.getOutputs());
		newS.setPerformedAditionallyBy(s.getPerformedAditionallyBy());
		newS.setPerformedPrimaryBy(s.getPerformedPrimaryBy());
        newS.setPredecesores(s.getPredecesores());
		newS.setPresentationName(s.getPresentationName());
        newS.setProcessComponentPresentationName(s.getProcessComponentPresentationName());
		newS.setResponsableDe(s.getResponsableDe());
        newS.setSteps(s.getSteps());
	    newS.setSuperActivities(s.getSuperActivities());
		newS.setVariabilityType(s.getVariabilityType());
		
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

	public static Variant crearCopiaVariante(Variant v){
		Variant newV = new Variant(v.getID(), v.getName(), v.getPresentationName(), v.getIDVarPoint(), v.isInclusive(), v.getVarType(), v.getProcessComponentId(), v.getProcessComponentName(), v.getPresentationId(), v.getElementIDExtends());

		newV.setBriefDescription(v.getBriefDescription());
		newV.setDescription(v.getDescription());
		newV.setDiagramURI(v.getDiagramURI());
		newV.setExclusivas(v.getExclusivas());
		newV.setGuid(v.getGuid());
		
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

		newV.setIdRole(v.getIdRole());
		newV.setIdTask(v.getIdTask());
		newV.setIdWorkProduct(v.getIdWorkProduct());
		newV.setInclusivas(v.getInclusivas());
		newV.setIsOptional(v.getIsOptional());
		newV.setIsPlanned(v.getIsPlanned());
		newV.setIsSynchronizedWithSource(v.getIsSynchronizedWithSource());
		newV.setMethodElementProperties(v.getMethodElementProperties());
		newV.setProcessComponentPresentationName(v.getProcessComponentPresentationName());
		newV.setSteps(v.getSteps());
		newV.setSuperActivities(v.getSuperActivities());
		newV.setVariabilityType(v.getVariabilityType());
		
		return newV;
	}

	public static Struct buscarElementoPorId (String id, List<Struct> list) {
		Iterator<Struct> iterator = list.iterator();
		Struct res = null;
		
        while (iterator.hasNext() && (res == null)){
        	Struct s = iterator.next();
        	if(s.getElementID().equals(id)){
        		res = s;
        	}
        	else { 
        		
        		if (s.getHijos().size() > 0){
        			res = buscarElementoPorId(id, s.getHijos());
        		}
        		if ((res == null) && (s.getVariantes().size()>0)){
        			Iterator<Variant> iter = s.getVariantes().iterator();
        			while (iter.hasNext()){
        				Variant v = iter.next();
        				if (v.getHijos()!=null){
        					res = buscarElementoPorId(id,v.getHijos());
        				}
        			}
        			
        		}
        	}
        }
        return res;
	}

	public static Struct buscarElementoEnModelo(String id, DefaultDiagramModel modelo, String buscarPor){
		if ((modelo != null) && (modelo.getElements() != null)){
			Iterator<Element> it = modelo.getElements().iterator();
			while (it.hasNext()){
				Struct s = (Struct) it.next().getData();
				String sId = (buscarPor.equals("idTask")) ? s.getIdTask() :
							 (buscarPor.equals("idWorkProduct")) ? s.getIdWorkProduct() :
							 (buscarPor.equals("idRole")) ? s.getIdRole() :
							 s.getElementID();
				if ((sId != null) && (sId.equals(id))){
					return s;
				}
				Struct hijo = buscarElemento(id, s.getHijos(), buscarPor);
				if (hijo != null){
					return hijo;
				}
			}
		}
		return null;
	}
	
	public static Struct buscarElementoEnModeloSinHijos(String id, DefaultDiagramModel modelo){
		if ((modelo != null) && (modelo.getElements() != null)){
			Iterator<Element> it = modelo.getElements().iterator();
			while (it.hasNext()){
				Struct s = (Struct) it.next().getData();
				String sId = s.getElementID();
				if ((sId != null) && (sId.equals(id))){
					return s;
				}
			}
		}
		return null;
	}

	public static Variant buscarVariante(List<Struct> nodos, String Id){
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
					else {
						res = buscarVariante(v.getHijos(),Id);
					}
				}
			}
			else { 
				if (s.getHijos().size() > 0){
					res = buscarVariante(s.getHijos(),Id);
				}
			}
		}
		
		return res;
	}

	public static Struct buscarRolPorId (String idElemSeleccionado, List<Struct> list) {
		Iterator<Struct> iterator = list.iterator();
		Struct res = null;
		
        while (iterator.hasNext() && (res == null)){
        	Struct s = iterator.next();
        	if(((s.getType() == TipoElemento.VP_ROLE || s.getType() == TipoElemento.ROLE)) &&
        	   (s.getElementID().equals(idElemSeleccionado))){
        			res = s;
        	}
        	else {
        		
        		if (s.getVariantes() != null){
        			Iterator<Variant> iterv = s.getVariantes().iterator();
        			while (iterv.hasNext() && res == null){
        				Variant v = iterv.next();
        				if (v.getHijos() != null){
        					res = buscarRolPorId(idElemSeleccionado, v.getHijos());
        				}
        			}      			
        		}
        		if (s.getHijos().size() > 0 && res == null){
    	      		res = buscarRolPorId(idElemSeleccionado, s.getHijos());
        		}
        	}
        }
        return res;
	}

	public static List<Struct> buscarRolPorNombre (String nombreElemSeleccionado, List<Struct> list) {
		Iterator<Struct> iterator = list.iterator();
		List<Struct> res = new ArrayList<Struct>();
		
        while (iterator.hasNext()){
        	Struct s = iterator.next();
        	if((s.getType() == TipoElemento.VP_ROLE || s.getType() == TipoElemento.ROLE) &&
        	   (s.getNombre().equals(nombreElemSeleccionado))){
        		res.add(s);
        	}
        	else {
        		if (s.getHijos().size() > 0){
	        		List<Struct> resHijos = buscarRolPorNombre(nombreElemSeleccionado, s.getHijos());
	        		res.addAll(resHijos);
        		}
        		
        		if (s.getVariantes() != null){
        			Iterator<Variant> iterv = s.getVariantes().iterator();
        			while (iterv.hasNext()){
        				Variant v = iterv.next();
        				if (v.getHijos() != null){
        					List<Struct> resHijos = buscarRolPorNombre(nombreElemSeleccionado, v.getHijos());
        					res.addAll(resHijos);
        				}
        			}      			
        		}
        	}
        }
        return res;
	}

	public static Struct buscarTareaPorId (String idElemSeleccionado, List<Struct> list) {
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

	public static List<Struct> buscarTareaPorNombre (String nombreElemSeleccionado, List<Struct> list) {
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

	public static Struct buscarWP (String idElemSeleccionado, List<Struct> list) {
		Iterator<Struct> iterator = list.iterator();
		Struct res = null;
		
        while (iterator.hasNext() && (res == null)){
        	Struct s = iterator.next();
        	if((s.getType() == TipoElemento.VP_WORK_PRODUCT || s.getType() == TipoElemento.WORK_PRODUCT) &&
        	   (s.getElementID().equals(idElemSeleccionado))){
        		res = s;
        	}
        	else {
        		if (s.getVariantes() != null){
        			Iterator<Variant> iterv = s.getVariantes().iterator();
        			while (iterv.hasNext() && res == null){
        				Variant v = iterv.next();
        				if (v.getHijos() != null){
        					res = buscarWP(idElemSeleccionado, v.getHijos());
        				}
        			}      			
        		}
        		if (s.getHijos().size() > 0 && res == null){
    	      		res = buscarWP(idElemSeleccionado, s.getHijos());
        		}
        	}
        	
        }
        return res;
	}

	public static String[] separarDireccion(String archivo){
		// archivo puede ser de la forma: dir1/dir2/.../nombre
		int indexDiv = archivo.indexOf("/");
		String dirArchivo = "";
		while (indexDiv != -1){
			String dir = archivo.substring(0, indexDiv);
			archivo = archivo.substring(indexDiv + 1, archivo.length());
			dirArchivo += dir + "/";
			indexDiv = archivo.indexOf("/");
		}
		String[] res = {dirArchivo, archivo};
		return res;
	}

	public static Variant obtenerVarianteParaPV(Struct pv, String idVar){
		Iterator<Variant> it = pv.getVariantes().iterator();
		while (it.hasNext()){
			Variant v = it.next();
			if (v.getID().equals(idVar)){
				return v;
			}
		}
		return null;
	}

	public static List<String> asList(String[] array){
		List<String> res = new ArrayList<String>();
		int n = array.length;
		for (int i = 0; i < n; i++){
			res.add(array[i]);
		}
		return res;
	}
}

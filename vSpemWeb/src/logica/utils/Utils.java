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
	
	public static Struct crearStruct(Variant v, List<Variant> lstVariantes){
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

	public static Variant crearCopiaVariante(Variant v){
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

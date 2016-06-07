package logica;

import java.util.Iterator;
import java.util.List;

import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;

import config.Constantes;
import dataTypes.TipoElemento;
import dominio.Struct;
import dominio.Variant;

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


	public static Variant buscarVariante(List<Struct> nodos, String Id){
		Iterator<Struct> it = nodos.iterator();
		Variant res = null;
		
		while (it.hasNext() && (res == null)){
			Struct s = it.next();
			TipoElemento type = s.getType();
			if (Utils.esPuntoDeVariacion(type)){
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
}

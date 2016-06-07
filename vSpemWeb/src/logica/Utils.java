package logica;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;

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
}

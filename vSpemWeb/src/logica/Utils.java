package logica;

import java.util.Iterator;
import java.util.List;

import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;

import dataTypes.TipoElemento;
import dominio.Struct;

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
	
}

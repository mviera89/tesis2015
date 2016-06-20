package logica.negocio;

import java.util.List;

import javax.ejb.Local;

import logica.dominio.Struct;
import logica.dominio.Variant;
import logica.enumerados.TipoElemento;

import org.primefaces.model.diagram.Element;

@Local
public interface IAdaptarManager {

	String seleccionarVariantesParaPV(Element puntoVariacionAdaptado, String[] variantesSeleccionadas);
	String validarSeleccion(String[] variantesSeleccionadas, Struct pv);
	Struct buscarElemento(String id, List<Struct> elementos, String buscarPor);
	Variant buscarVariante(List<Struct> nodos, String id);
	boolean esPuntoDeVariacion(TipoElemento tipo);
	boolean esVariante(TipoElemento tipo);
	void modificarExpandido(Struct s, boolean expandido);
	void modificarExpandido(Variant v, boolean expandido);
	void modificarEtiqueta(Struct s, String etiqueta);
	Element crearVariante(List<Struct> nodos, String etiqueta, String varianteSeleccionada, float x, float y);
	Struct crearStruct(Struct s);
	Struct crearStruct(Variant v, List<Variant> lstVariantes);
	TipoElemento getElementoParaVarPoint(TipoElemento type);
	Struct crearCopiaStruct(Struct s);
	Variant crearCopiaVariante(Variant v);
	List<Struct> cargarNodos(String nomArchivo);
	Struct crearElementoRaiz(List<Struct> nodos);
	String obtenerEtiquetaParaModelo(Struct padre, Struct hijo);
	Struct buscarRolPorId(List<Struct> nodos, String idRol);
	List<Struct> buscarRolPorNombre(List<Struct> nodos, String nomRol);
	Struct buscarWP(List<Struct> nodos, String wp);
	List<Struct> buscarTareaPorNombre(List<Struct> nodos, String nomTarea);
	
}

package config;

public class Constantes {
	
	// Constantes de archivos
	public static final String destinoDescargas = "C:\\download\\";
	public static final String destinoExport = "C:\\download\\";
	public static final String nomArchivoExport = "export.xml";
	
	// Constantes de Github
	public static final String URL_GITHUB = "https://github.com/";
	public static final String URL_GITHUB_DEFAULT = "mviera89/tesis2015/tree/master/upload/";
	public static final String URL_GITHUB_DOWNLOAD = "https://raw.githubusercontent.com/";
	
	// Constantes para la representaci�n del modelo
	public static final int min_default = -1; // Valor por defecto asignado al m�nimo de variantes que se pueden seleccionar para un punto de variaci�n
	public static final int max_default = -1; // Valor por defecto asignado al m�ximo de variantes que se pueden seleccionar para un punto de variaci�n
	public static final int distanciaEntreNiveles = 10;
	public static final int yInicial = 0;
	public static final float distanciaEntreElemsMismoNivel = (float) 2.0;
	public static final String colorVarPoint = "mediumblue";
	
	// Constantes para los mensjaes
	public static final String mensjaeAyudaRepositorio = "Ingrese la URL correspondiente al repositorio github donde se encuentra el XMI a importar, sin incluir el prefijo 'https://github.com/'.";
	public static final String MENSAJE_ARCHIVOS_NO_ENCONTRADOS = "No se han encontrado archivos XMI en el repositorio indicado.";
	public static final String MENSAJE_URL_NULL = "Debe ingresar la URL correspondiente al repositorio github donde se encuentra el XMI a importar.";
	public static final String MENSAJE_URL_NO_ACCESIBLE = "No se pudo acceder a la URL ";
	public static final String MENSAJE_ARCHIVO_NULL = "Debe seleccionar el archivo que desea cargar.";

}

package dominio;

import config.Constantes;
import dataTypes.TipoElemento;

public class ElementoModelo {
    
	private String id;
	private String nombre;
    private String imagen;
    private TipoElemento tipo;
    private Boolean esPV;
    private String color;
	private int min;
	private int max;

	public ElementoModelo() {
    }

    public ElementoModelo(String id, String nombre, String imagen, TipoElemento tipo, int min, int max) {
    	this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.tipo = tipo;
        this.esPV = (tipo == TipoElemento.VP_ACTIVITY || 
        			 tipo == TipoElemento.VP_TASK ||
        			 tipo == TipoElemento.VP_PHASE ||
        			 tipo == TipoElemento.VP_ITERATION);
        this.color = this.esPV ? Constantes.colorVarPoint : "black";
        this.min = min;
        this.max = max;
    }
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public TipoElemento getTipo() {
		return tipo;
	}

	public void setTipo(TipoElemento tipo) {
		this.tipo = tipo;
	}

	public Boolean getEsPV() {
		return esPV;
	}

	public void setEsPV(Boolean esPV) {
		this.esPV = esPV;
	}

    public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

    @Override
    public String toString() {
        return nombre;
    }
     
}

package dominio;

import dataTypes.TipoElemento;

public class ElementoModelo {
    
	private String id;
	private String nombre;
    private String imagen;
    private TipoElemento type;

	public ElementoModelo() {
    }

    public ElementoModelo(String id, String nombre, String imagen, TipoElemento type) {
    	this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.type = type;
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

    public TipoElemento getType() {
		return type;
	}

	public void setType(TipoElemento type) {
		this.type = type;
	}

    @Override
    public String toString() {
        return nombre;
    }
     
}

package dataTypes;

public enum TipoElemento {
	PROCESS_PACKAGE, ACTIVITY, VP_ACTIVITY, VAR_ACTIVITY;
	
	public String getImagen() {
        String imagen = "";
        switch (ordinal()) {
        	case 0: // PROCESS_PACKAGE
        		imagen = "processPackage.png";
        		break;
	        case 1: // ACTIVITY
	        	imagen = "activity.png";
	            break;
	        case 2: // VP_ACTIVITY
	        	imagen = "vpActivity.png";
	            break;
	        case 3: // VAR_ACTIVITY
	        	imagen = "varActivity.png";
	            break;
	        default:
	        	imagen = "";
	            break;
        }
        return imagen;
    }
	
	@Override
    public String toString() {
        String valor = "";
        switch (ordinal()) {
	    	case 0: // PROCESS_PACKAGE
	    		valor = "ProcessPackage";
	    		break;
	        case 1: // ACTIVITY
	        	valor = "Activity";
	            break;
	        case 2: // VP_ACTIVITY
	        	valor = "vpActivity";
	            break;
	        case 3: // VAR_ACTIVITY
	        	valor = "VarActivity";
	            break;
	        default:
	        	valor = "";
	            break;
	    }
        return valor;
    }
	
}

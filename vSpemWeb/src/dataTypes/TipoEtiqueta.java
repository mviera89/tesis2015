package dataTypes;

public enum TipoEtiqueta {
	PRIMARY_PERFORMER, ADDITIONAL_PERFORMER, 
	MANDATORY_INPUT, OPTIONAL_INPUT, 
	MANDATORY_OUTPUT, OPTIONAL_OUTPUT, EXTERNAL_INPUT, OUTPUT;

	@Override
    public String toString() {
        String valor = "";
        switch (ordinal()) {
	    	case 0: // PRIMARY_PERFORMER
	    		valor = "PRI";
	    		break;
	        case 1: // ADDITIONAL_PERFORMER
	        	valor = "ADD";
	            break;
	        case 2: // MANDATORY_INPUT
	        	valor = "MDT_IN";
	            break;
	        case 3: // OPTIONAL_INPUT
	        	valor = "OPT_IN";
	        	break;
	        case 4: // MANDATORY_OUTPUT
	        	valor = "MDT_OUT";
	            break;
	        case 5: // OPTIONAL_OUTPUT
	        	valor = "OPT_OUT";
	        	break;
	        case 6: // EXTERNAL_INPUT
	        	valor = "EXT_IN";
	        	break;
	        case 7: // OUTPUT
	        	valor = "OUT";
	        	break;
	        default:
	        	valor = "";
	            break;
	    }
        return valor;
    }

}

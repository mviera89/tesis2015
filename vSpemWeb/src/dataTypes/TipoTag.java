package dataTypes;

public enum TipoTag {
	TASK_DESCRIPTION, ARTIFACT_DESCRIPTION;

	@Override
    public String toString() {
        String valor = "";
        switch (ordinal()) {
	    	case 0: // TASK_DESCRIPTION
	    		valor = "org.eclipse.epf.uma:TaskDescription";
	    		break;
	        case 1: // ARTIFACT_DESCRIPTION
	        	valor = "org.eclipse.epf.uma:ArtifactDescription";
	            break;
	        default:
	        	valor = "";
	            break;
	    }
        return valor;
    }
	
}

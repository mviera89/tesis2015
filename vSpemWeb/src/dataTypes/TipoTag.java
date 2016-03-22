package dataTypes;

public enum TipoTag {
	TASK_DESCRIPTION, ARTIFACT_DESCRIPTION, GUIDANCE, GUIDANCE_DESCRIPTION;

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
	        case 2: // GUIDANCE
	        	valor = "org.eclipse.epf.uma:Template";
	        	break;
	        case 3: // GUIDANCE_DESCRIPTION
	        	valor = "org.eclipse.epf.uma:GuidanceDescription";
	        	break;
	        default:
	        	valor = "";
	            break;
	    }
        return valor;
    }
	
}

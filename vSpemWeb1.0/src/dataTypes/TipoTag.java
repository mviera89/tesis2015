package dataTypes;

public enum TipoTag {
	TASK, TASK_DESCRIPTION, ARTIFACT, ARTIFACT_DESCRIPTION, GUIDANCE, GUIDANCE_DESCRIPTION, ROLE, SUPPORTING_MATERIAL, SUPPORTING_MATERIAL_DESCRIPTION;

	@Override
    public String toString() {
        String valor = "";
        switch (ordinal()) {
        	case 0: // TASK
        		valor = "org.eclipse.epf.uma:Task";
        		break;
	    	case 1: // TASK_DESCRIPTION
	    		valor = "org.eclipse.epf.uma:TaskDescription";
	    		break;
	    	case 2: // ARTIFACT
	    		valor = "org.eclipse.epf.uma:Artifact";
	    		break;
	        case 3: // ARTIFACT_DESCRIPTION
	        	valor = "org.eclipse.epf.uma:ArtifactDescription";
	            break;
	        case 4: // GUIDANCE
	        	valor = "org.eclipse.epf.uma:Template";
	        	break;
	        case 5: // GUIDANCE_DESCRIPTION
	        	valor = "org.eclipse.epf.uma:GuidanceDescription";
	        	break;
	        case 6: // ROLE
	        	valor = "org.eclipse.epf.uma:Role";
	        	break;
	        case 7: // SUPPORTING_MATERIAL
	        	valor = "org.eclipse.epf.uma:SupportingMaterial";
	        	break;
	        case 8: // SUPPORTING_MATERIAL_DESCRIPTION
	        	valor = "org.eclipse.epf.uma:ContentDescription";
	        	break;
	        default:
	        	valor = "";
	            break;
	    }
        return valor;
    }
	
}

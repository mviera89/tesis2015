package dataTypes;

public enum TipoElemento {
	PROCESS_PACKAGE, ACTIVITY, VP_ACTIVITY, VAR_ACTIVITY, TASK, VP_TASK, 
	VAR_TASK, ITERATION, VP_ITERATION, VAR_ITERATION, PHASE, VP_PHASE, 
	VAR_PHASE, CAPABILITY_PATTERN, DELIVERY_PROCESS, MILESTONE, VP_MILESTONE, VAR_MILESTONE, 
	ROLE, VP_ROLE, VAR_ROLE, WORK_PRODUCT, VP_WORK_PRODUCT, VAR_WORK_PRODUCT, GUIDANCE;

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
	        case 4: // TASK
	        	imagen = "taskDescriptor.png";
	            break;
	        case 5: // VP_TASK
	        	imagen = "vpTaskDescriptor.png";
	            break;
	        case 6: // VAR_TASK
	        	imagen = "varTaskDescriptor.png";
	            break;
	        case 7: // ITERATION
	        	imagen = "iteration.png";
	            break;
	        case 8: // VP_ITERATION
	        	imagen = "vpIteration.png";
	            break;
	        case 9: // VAR_ITERATION
	        	imagen = "varIteration.png";
	            break;
	        case 10: // PHASE
	        	imagen = "phase.png";
	            break;
	        case 11: // VP_PHASE
	        	imagen = "vpPhase.png";
	            break;
	        case 12: // VAR_PHASE
	        	imagen = "varPhase.png";
	            break;
	        case 13: // CAPABILITY_PATTERN
	        	imagen = "capabilityPattern.png";
	            break;
	        case 14: // DELIVERY_PROCESS
	        	imagen = "deliveryProcess.png";
	            break;
	        case 15: // MILESTONE
	        	imagen = "milestone.png";
	            break;
	        case 16: // VP_MILESTONE
	        	imagen = "vpMilestone.png";
	            break;
	        case 17: // VAR_MILESTONE
	        	imagen = "varMilestone.png";
	            break;
	        case 18: // ROLE
	        	imagen = "roleDescriptor.png";
	            break;
	        case 19: // VP_ROLE
	        	imagen = "vpRoleDescriptor.png";
	            break;
	        case 20: // VAR_ROLE
	        	imagen = "varRoleDescriptor.png";
	            break;
	        case 21: // WORK_PRODUCT
	        	imagen = "workproduct.png";
	            break;
	        case 22: // VP_WORK_PRODUCT
	        	imagen = "vpWorkproduct.png";
	            break;
	        case 23: // VAR_WORK_PRODUCT
	        	imagen = "varWorkproduct.png";
	            break;
	        case 24: // GUIDANCE
	        	imagen = "";
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
	        case 4: // TASK
	        	valor = "TaskDescriptor";
	            break;
	        case 5: // VP_TASK
	        	valor = "vpTaskDescriptor";
	            break;
	        case 6: // VAR_TASK
	        	valor = "VarTaskDescriptor";
	            break;
	        case 7: // ITERATION
	        	valor = "Iteration";
	            break;
	        case 8: // VP_ITERATION
	        	valor = "vpIteration";
	            break;
	        case 9: // VAR_ITERATION
	        	valor = "VarIteration";
	            break;
	        case 10: // PHASE
	        	valor = "Phase";
	            break;
	        case 11: // VP_PHASE
	        	valor = "vpPhase";
	            break;
	        case 12: // VAR_PHASE
	        	valor = "VarPhase";
	            break;
	        case 13: // CAPABILITY_PATTERN
	        	valor= "CapabilityPattern";
	            break;
	        case 14: // DELIVERY_PROCESS
	        	valor = "DeliveryProcess";
	            break;
	        case 15: // MILESTONE
	        	valor = "Milestone";
	            break;
	        case 16: // VP_MILESTONE
	        	valor = "vpMilestone";
	            break;
	        case 17: // VAR_MILESTONE
	        	valor = "VarMilestone";
	            break;
	        case 18: // ROLE
	        	valor = "RoleDescriptor";
	            break;
	        case 19: // VP_ROLE
	        	valor = "vpRoleDescriptor";
	            break;
	        case 20: // VAR_ROLE
	        	valor = "VarRoleDescriptor";
	            break;
	        case 21: // WORK_PRODUCT
	        	valor = "WorkProductDescriptor";
	            break;
	        case 22: // VP_WORK_PRODUCT
	        	valor = "vpWorkProductDescriptor";
	            break;
	        case 23: // VAR_WORK_PRODUCT
	        	valor = "VarWorkProductDescriptor";
	            break;
	        case 24: // GUIDANCE
	        	valor = "Guidance";
	        	break;
	        default:
	        	valor = "";
	            break;
	    }
        return valor;
    }

}

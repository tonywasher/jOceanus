package uk.co.tolcroft.models.data;

/**
 * Enumeration of edit states of DataItem and DataList objects in a view
 */
public enum EditState {
	/**
	 * No changes made
	 */
	CLEAN,
	
	/**
	 *  Non-validated changes made 
	 */
	DIRTY,
	
	/**
	 * Only valid changes made
	 */
	VALID,
	
	/**
	 * Object is in error
	 */
	ERROR;

	/**
	 * Determine the precedence for a {@link EditState} value.
	 * 
	 * @param pTest The EditState 
	 * @return the precedence 
	 */	
	private static int editOrder(EditState pTest) {
		switch (pTest) {
			case ERROR: return 3;
			case DIRTY: return 2;
			case VALID: return 1;
			default:    return 0;
		}
	}

	/**
	 * Combine With another edit state
	 * @param pState edit state to combine with 
	 */
	public EditState combineState(EditState pState) {	
		if (editOrder(this) > editOrder(pState))
			return(this);
		else 
			return(pState);
	}
}

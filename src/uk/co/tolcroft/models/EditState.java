package uk.co.tolcroft.models;

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
}

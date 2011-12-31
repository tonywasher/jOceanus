package uk.co.tolcroft.models.data;

/**
 * Enumeration of states of DataItem and DataList objects
 */
public enum DataState {
	/**
	 * No known state
	 */
	NOSTATE,
	
	/**
	 * New object
	 */
	NEW,
	
	/**
	 * Clean object with no changes
	 */
	CLEAN,
	
	/**
	 * Changed object with history
	 */
	CHANGED,
	
	/**
	 * Deleted Clean object
	 */
	DELETED,
	
	/**
	 * Deleted New object
	 */
	DELNEW,
	
	/**
	 * Deleted Changed object
	 */
	DELCHG,
	
	/**
	 * Recovered deleted object
	 */
	RECOVERED;
}

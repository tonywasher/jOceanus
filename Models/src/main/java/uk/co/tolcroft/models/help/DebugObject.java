package uk.co.tolcroft.models.help;

import uk.co.tolcroft.models.help.DebugManager.DebugEntry;

/**
 * Represents an HTML dump-able object
 */
public interface DebugObject {
	/**
	 * Create a string form of the object suitable for inclusion in an Debug output document
	 * @param pDetail the Debug detail
	 * @return the formatted string
	 */
	public StringBuilder buildDebugDetail(DebugDetail pDetail);	

	/**
	 * Add child entries for the debug object
	 * @param pManager the debug manager
	 * @param pParent the parent debug entry
	 */
	public void addChildEntries(DebugManager 	pManager,
								DebugEntry		pParent);	
}

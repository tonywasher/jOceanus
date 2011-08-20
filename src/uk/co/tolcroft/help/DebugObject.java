package uk.co.tolcroft.help;

import uk.co.tolcroft.help.DebugManager.DebugEntry;

/**
 * Represents an HTML dump-able object
 */
public interface DebugObject {
	/**
	 * Create a string form of the object suitable for inclusion in an HTML document
	 * @return the formatted string
	 */
	public StringBuilder toHTMLString();	

	/**
	 * Add child entries for the debug object
	 * @param pManager the debug manager
	 * @param pParent the parent debug entry
	 */
	public void addChildEntries(DebugManager 	pManager,
								DebugEntry		pParent);	
}

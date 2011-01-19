package uk.co.tolcroft.models;

/**
 * Represents an HTML dump-able object
 */
public interface htmlDumpable {
	/**
	 * Create a string form of the object suitable for inclusion in an HTML document
	 * @return the formatted string
	 */
	public StringBuilder toHTMLString();	
}

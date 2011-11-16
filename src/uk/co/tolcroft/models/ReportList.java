package uk.co.tolcroft.models;

import uk.co.tolcroft.models.help.DebugDetail;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;
import uk.co.tolcroft.models.help.DebugObject;

public abstract class ReportList<T extends ReportItem<T>> extends SortedList<T> 
												 		  implements DebugObject {
	/**
	 * Construct a top-level List
	 */
	public ReportList(Class<T> pClass) { super(pClass); }

	/**
	 * Construct a top-level List
	 */
	public ReportList(Class<T> pClass, boolean fromStart) { super(pClass, fromStart); }

	/**
	 * Obtain the type of the list
	 * @return the type of the list
	 */
	abstract public String itemType();
	
	/**
	 * Stub for extensions to add their own fields
	 * @param pDetail the debug detail
	 * @param pBuffer the string buffer 
	 */
	public void addHTMLFields(DebugDetail pDetail, StringBuilder pBuffer) {}
	
	/**
	 * Provide a string representation of this object
	 * @param pDetail the Debug Detail
	 * @return formatted string
	 */
	public StringBuilder buildDebugDetail(DebugDetail pDetail) {
		/* Local variables */
		StringBuilder	myString = new StringBuilder(10000);
			
		/* Format the table headers */
		myString.append("<table border=\"1\" width=\"90%\" align=\"center\">");
		myString.append("<thead><th>");
		myString.append(itemType());
		myString.append("List</th>");
		myString.append("<th>Property</th><th>Value</th></thead><tbody>");
			
		/* Start the status section */
		myString.append("<tr><th rowspan=\"2\">Status</th></tr>");
		myString.append("<tr><td>ListSize</td><td>"); 
		myString.append(sizeAll()); 
		myString.append("</td></tr>"); 

		/* Add any extra detail */
		addHTMLFields(pDetail, myString);
		
		/* Return the string */
		return myString;
	}
		
	/**
	 * Add child entries for the debug object
	 * @param pManager the debug manager
	 * @param pParent the parent debug entry
	 */
	public void addChildEntries(DebugManager 	pManager,
								DebugEntry		pParent) { }	
}

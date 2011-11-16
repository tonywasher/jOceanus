package uk.co.tolcroft.models;

import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.help.DebugDetail;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;
import uk.co.tolcroft.models.help.DebugObject;

public abstract class ReportItem<T extends ReportItem<T>> extends SortedItem<T> 
														  implements DebugObject {
	/**
	 * The item that this Report is based upon 
	 */
	private DataItem<?>	theBase	= null;
	
	/**
	 * Construct a new item
	 * @param pCtl the list that this item is associated with
	 * @param uId the Id of the new item (or 0 if not yet known)
	 */
	public ReportItem(SortedList<T> pList) {
		super(pList);
	}
	
	/**
	 * Get the base item for this item
	 * @return the Base item or <code>null</code>
	 */
	public DataItem<?>				getBase()      	{ return theBase; }

	/**
	 * Set the base item for this item
	 * @param pBase the Base item
	 */
	public void						setBase(DataItem<?> pBase) { theBase = pBase; }
	
	/**
	 * Determine the field name for a particular field
	 * This method is the underlying method called when the id is unknown 
	 * @return the field name
	 */
	public static String			fieldName(int fieldId)	{
		return "Unknown";
	}

	/**
	 * Format the value of a particular field as a table row
	 * @param pDetail the debug detail
	 * @param iField the field number
	 * @return the formatted field
	 */
	public String 					formatField(DebugDetail 		pDetail, 
												int 				iField) {
		return "";
	}
							
	/**
	 *  Test an element for equality
	 *  @param that the object to test against
	 */
	public abstract boolean equals(Object that);
	
	/**
	 *  Compare an element for sort order
	 *  @param that the object to test against
	 */
	public abstract int compareTo(Object that);
	
	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	abstract public String 			itemType();
	
	/**
	 * Determine the field name for a particular field
	 * This method is always overridden but is used to supply the default field name 
	 * @return the field name
	 */
	public abstract String			getFieldName(int fieldId);

	/**
	 * Obtain the number of fields for an item
	 * @return the number of fields
	 */
	public int						numFields() { return 0; }
	
	/**
	 * Format this item to a string
	 * @param pDetail the Debug Detail
	 * @return the formatted item
	 */
	public StringBuilder 			buildDebugDetail(DebugDetail pDetail) {
		StringBuilder	myString = new StringBuilder(2000);
		int     iField;
		int		iNumFields = numFields();
		
		/* Initialise the string with an item name */
		myString.append("<table border=\"1\" width=\"90%\" align=\"center\">");
		myString.append("<thead><th>");
		myString.append(itemType());
		myString.append("</th>");
		myString.append("<th>Field</th><th>Value</th></thead><tbody>");
		
		/* Add Link Debug */
		addLinkDebug(myString);
		
		/* Start the values section */
		myString.append("<tr><th rowspan=\"");
		myString.append(iNumFields+1);
		myString.append("\">Values</th></tr>");
		
		/* Loop through the fields */
		for (iField = 0;
			 iField < iNumFields;
			 iField++) {
			if (iField != 0) myString.append("<tr>"); 
			myString.append("<td>"); 
			myString.append(getFieldName(iField)); 
			myString.append("</td><td>"); 
			myString.append(formatField(pDetail, iField));
			myString.append("</td></tr>");
		}

		/* Add any extra fields */
		addHTMLFields(pDetail, myString);

		/* If we have an underlying object */
		if (getBase() != null) {
			/* Format the Underlying object */
			myString.append("<tr><th>Underlying</th><td colspan=\"2\">");
			myString.append(pDetail.addDebugLink(getBase(), getBase().itemType()));
			myString.append("</td></tr>");
		}
		
		/* Terminate the table */
		myString.append("</tbody></table>");
		
		/* Return the formatted item */
		return myString;
	}
	
	/**
	 * Stub for extensions to add their own fields
	 * @param pDetail the debug detail
	 * @param pBuffer the string buffer 
	 */
	public void addHTMLFields(DebugDetail pDetail, StringBuilder pBuffer) {}

	/**
	 * Add child entries for the debug object
	 * @param pManager the debug manager
	 * @param pParent the parent debug entry
	 */
	public void addChildEntries(DebugManager 	pManager,
								DebugEntry		pParent) { }	
}

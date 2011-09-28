package uk.co.tolcroft.models.data;


public class ValidationControl<T extends DataItem<T>> {
	/**
	 * The item to which this Validation Control belongs
	 */
	private T			 	theItem = null;

	/**
	 * The item to which this Validation Control belongs
	 */
	private DataList<?,T>	theList = null;

	/**
	 * The first error in the list
	 */
	private errorElement 	theTop  = null;

	/**
	 * The last error in the list
	 */
	private errorElement 	theEnd  = null;
	
	/**
	 * The number of errors in this buffer
	 */
	private int			 	theNumErrors = 0;
	
	/**
	 * Constructor
	 * @pItem the item to which this validation control belongs
	 */
	protected ValidationControl(T pItem) {
		/* Store details */
		theItem = pItem;
		theList = theItem.getList();
	}
	
	/**
	 * Get the first error in the list
	 * @return the first error or <code>null</code>
	 */
	public errorElement getFirst() { return theTop; }
	
	/**
	 *  add error to the list 
	 *  @param pText the text for the error
	 *  @param iFieldId the field id for the error
	 */
	protected void addError(String pText,
			                int    iFieldId) {
		/* Create a new error element */
		errorElement myEl = new errorElement(pText, iFieldId);
		
		/* Add to the end of the list */
		if (theEnd != null) theEnd.theNext = myEl; 
		if (theTop == null) theTop = myEl;
		theEnd = myEl;
		
		/* Increment the error count */
		theNumErrors++;
		
		/* Note that the list has errors */
		theList.setEditState(EditState.ERROR);
	}
	
	/**
	 *  Determine whether there are any errors for a particular field
	 *  @param iField - the field number to check
	 *  @return <code>true</code> if there are any errors <code>false</code> otherwise
	 */
	protected boolean hasErrors(int iField) {
		errorElement myCurr;
		for (myCurr = theTop;
		     myCurr != null;
		     myCurr = myCurr.getNext()) {
			if (myCurr.getField() == iField) return true;
		}
		return false;
	}
	
	/**
	 *  Get the first actual error for a particular field
	 *  @param iField - the field number to check
	 *  @return the error text
	 */
	protected String getFieldErrors(int iField) {
		errorElement 	myCurr;
		String			myErrors = null;
		
		/* Loop through the errors */
		for (myCurr = theTop;
		     myCurr != null;
		     myCurr = myCurr.getNext()) {
			/* If the field matches */
			if (myCurr.getField() == iField) {					
				/* Add the error */
				myErrors = addErrorText(myErrors, myCurr.getError());
			}
		}
		return (myErrors == null) ? null : myErrors + "</html>";
	}
	
	/**
	 * Get the error text for fields outside a set of fields
	 * @param iFields the set of fields
	 * @return the error text
	 */
	protected String	getFieldErrors(int[] iFields) {
		errorElement 	myCurr;
		boolean 		bFound;
		int 			myField;
		String			myErrors = null;
		
		/* Loop through the errors */
		for (myCurr = theTop;
		     myCurr != null;
		     myCurr = myCurr.getNext()) {
			/* Assume that field is not in set */
			myField	= myCurr.getField();
			bFound 	= false;
			
			/* Search the field set */
			for(int i : iFields) {
				/* If we have found the field note it and break loop */
				if (i == myField) { bFound = true; break; }
			}
			
			/* Skip error if the field was found */
			if (bFound) continue;
			
			/* Add the error */
			myErrors = addErrorText(myErrors, myCurr.getError());
		}
		
		/* Return errors */
		return (myErrors == null) ? null : myErrors + "</html>";
	}
	
	/**
	 * Add error text
	 * @param pCurrent existing error text
	 * @param pError new error text
	 */
	private String addErrorText(String pCurrent, String pError) {
		/* Return text if current is null */
		if (pCurrent == null) return "<html>" + pError;
		
		/* return with error appended */
		return pCurrent + "<br>" + pError;
	}
	
	/**
	 *  Clear errors
	 */
	protected void clearErrors() {
		/* Remove all errors */
		theTop       = null;
		theEnd       = null;
		theNumErrors = 0;
	}
	
	/**
	 * Format the errors in this list
	 * @return the formatted changes
	 */
	protected StringBuilder toHTMLString() {
		errorElement	myCurr;
		StringBuilder	myString = new StringBuilder(1000);
		
		/* Start the values section */
		myString.append("<tr><th rowspan=\"");
		myString.append(theNumErrors+1);
		myString.append("\">Errors</th></tr>");
		
		/* Loop through the elements */
		for (myCurr  = theTop;
			 myCurr != null;
			 myCurr  = myCurr.theNext) {
			/* Format the error element */
			myString.append(myCurr.toHTMLString());
		}
		
		/* Return the formatted string */
		return myString;
	}
	
	/**
	 * represents an instance of an error for an object
	 */
	public class errorElement {
		/**
		 * The next error in the list
		 */
		private errorElement theNext   = null;
		
		/**
		 * The text of the error
		 */
		private String       theError  = null;
		
		/**
		 * The field id for the error
		 */
		private int          theField  = -1;
		
		/**
		 * Get the text for the error
		 * @return the text
		 */
		public String       getError() { return theError; }
		
		/**
		 * Get the fieldId for the error
		 * @return the fieldId
		 */
		public int          getField() { return theField; }
		
		/**
		 * Get the next error
		 * @return the next error (or <code>null</code>)
		 */
		public errorElement getNext()  { return theNext; }
		
		/**
		 * Constructor for the error
		 * @param pError the error text
		 * @param iField the field id
		 */
		private errorElement(String pError,
				             int    iField) {
			theError = pError;
			theField = iField;
		}
		
		/**
		 * Format the error represented by this element
		 * @return the formatted changes
		 */
		public StringBuilder toHTMLString() {
			StringBuilder	myString = new StringBuilder(100);
			
			/* Format the details */
			myString.append("<tr><td>");
			myString.append(theItem.getFieldName(theField));
			myString.append("</td><td>");
			myString.append(theError);
			myString.append("</td</tr>");
			
			/* Return the string */
			return myString;
		}
	}
}

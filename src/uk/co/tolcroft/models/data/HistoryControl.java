package uk.co.tolcroft.models.data;


/**
 * Provides the implementation of a history buffer for a DataItem
 * Each element represents a changed set of values and refers to a {@link histObject} object
 * which is the set of changeable values for the object. 
 * @see histElement
 */
public class HistoryControl<T extends DataItem<T>> {
	/**
	 * The item to which this Validation Control belongs
	 */
	private T						theItem 	= null;

	/**
	 * The item on which this item is based
	 */
	private DataItem<?>				theBase 	= null;

	/**
	 * The current set of values for this object
	 */
	private HistoryValues<T>        theCurr  	= null;
	
	/**
	 * The most recent change in the set of past values
	 */
	private histElement 			theTop    	= null;
	
	/**
	 * The original set of values if any changes have been made
	 */
	private histElement 			theOriginal	= null;
	
	/**
	 * The restore cursor indicating where the restore cursor is in the set of changes
	 */
	private HistoryControl<?>.histElement 	theCursor 	= null;
	
	/**
	 * Constructor
	 * @param pItem the item to which this validation control belongs
	 */
	protected HistoryControl(T pItem) {
		/* Store details */
		theItem = pItem;
	}
	
	/**
	 * Initialise the current values
	 * @param pValues the current values
	 */
	protected void setValues(HistoryValues<T> pValues) {
		/* Store details */
		theCurr = pValues;
	}
	
	/**
	 * Initialise the base item
	 * @param pValues the current values
	 */
	protected void setBase(DataItem<?> pBase) {
		/* Store details */
		theBase = pBase;
	}
	
	/**
	 * Get the changeable values object for this item 
	 * @return the object
	 */
	public HistoryValues<T>		getCurrentValues()	{ return theCurr; }	

	/**
	 *  Get original values
	 *  @return original values
	 */
	protected HistoryValues<T> 	getOriginalValues() {
		return (theOriginal != null) ? theOriginal.theSnapShot : theCurr;
	}
	
	/**
	 * Get the base item for this item
	 * @return the Base item or <code>null</code>
	 */
	public DataItem<?>	getBase()      	{ return theBase; }

	/**
	 *  Push Item to the history
	 */
	protected void pushHistory() {
		/* Create a new history element */
		histElement myEl = new histElement();
		
		/* Add to the start of the list */
		myEl.theNext = theTop;
		if (theTop      != null) theTop.thePrev = myEl;
		if (theOriginal == null) theOriginal    = myEl;
		theTop       = myEl; 
	}
	
	/**
	 *  popItem from the history and remove from history
	 *  @return last history item
	 */
	protected void popTheHistory() {
		histElement myItem;
		
		/* Remove top-most element from the list */
		myItem = theTop;
		
		/* Reset the cursor */
		theCursor = null;
		
		/* If there is an item */
		if (myItem != null) {
			/* Remove it from the list */
			theTop = myItem.theNext;
			if (theTop != null) theTop.thePrev = null;
			else theOriginal = null;
			
			/* Store object as the current values */
			theCurr = myItem.theSnapShot;
		}
	}
	
	/**
	 *  popItem from the history if equal to current
	 *  @param isCursor is this a cursor related change 
	 *  @return <code>true/false</code> was history retained?
	 */
	protected boolean maybePopHistory(boolean isCursor) {
		/* If there is no change */
		if (theCurr.histEquals(theTop.theSnapShot)) {
			/* Just pop the history */
			popTheHistory();
			return false;
		}
		
		/* If this is not a cursor change, reset the cursor */
		if (!isCursor) theCursor = null;
		
		/* Return that we have made a change */
		return true;
	}
	
	/**
	 *  peek the next item from the history
	 *  @return the next item
	 */
	protected void peekFurther() {
		HistoryControl<?> myControl = theBase.getHistory();
		
		/* If we have no cursor */
		if (theCursor == null) {
			/* Point to start of history */
			theCursor = myControl.theTop;
			
			/* If we have a cursor */
			if (theCursor != null) {
				/* Build from cursor object */
				pushHistory();
				theCurr.copyFrom(theCursor.theSnapShot);
				maybePopHistory(true);
			}
		}
		
		/* Else if we have another element */
		else if (theCursor.theNext != null) {
			/* Shift the cursor */
			theCursor = theCursor.theNext;

			/* Build from cursor object */
			pushHistory();
			theCurr.copyFrom(theCursor.theSnapShot);
			maybePopHistory(true);
		}
	}
	
	/**
	 *  peek the previous item from the history
	 *  @return the previous item
	 */
	protected void peekPrevious() {
		HistoryControl<?> myControl = theBase.getHistory();
		
		/* If we have a cursor */
		if (theCursor != null) {
			/* Shift cursor backwards */
			theCursor = theCursor.thePrev;
			
			/* If we still have a cursor */
			if (theCursor != null) {
				/* Build from cursor object */
				pushHistory();
				theCurr.copyFrom(theCursor.theSnapShot);
				maybePopHistory(true);
			}
			
			/* else we should restore original values */
			else {
				/* Copy from the underlying value */
				theCurr.copyFrom(myControl.theCurr);
				
				/* Clear history */
				clearHistory();
				
				/* Set clean status */
				theItem.setState(DataState.CLEAN);
			}
		}
	}
	
	/**
	 *  Is there any history
	 *  @return whether there are entries in the history list
	 */
	protected boolean hasHistory() {
		return (theTop != null);
	}
	
	/**
	 *  Is there any valid history for this table
	 *  @param pTable the table to test against
	 *  @return whether the top entry in the the history list is valid for the table
	 */
	protected boolean hasValidHistory(HistoryCheck<T> pTable) {
		histElement myShot = null;
		
		/* If we have potential valid history */
		if (theTop != null)
			myShot = theTop;
		
		/* Handle validity for table */
		if ((myShot != null) &&
		    (!pTable.isValidHistory(theItem, myShot.theSnapShot)))
			myShot = null;
		
		/* Return details */
		return (myShot != null);
	}
	
	/**
	 *  Is there any previous history from the cursor for this table 
	 *  @param pObj the current values  
	 *  @param pTable the table to test against
	 *  @return whether there is further history
	 */
	protected boolean hasFurther(HistoryCheck<T> pTable) {
		HistoryControl<?> 				myControl;
		HistoryControl<?>.histElement 	myShot = null;
		DataState						myState;
		
		/* Need a base element to have valid further items */
		if (theBase == null)	return false;
		
		/* Access  the State */
		myState = theItem.getState();
		
		/* If we are clean, test for valid history */
		if (myState == DataState.CLEAN)	{
			/* Access the base control */
			myControl = theBase.getHistory();
			
			/* If we have potential valid history */
			if (myControl.theTop != null)
				myShot = myControl.theTop;
			
			/* Handle validity for table */
			if ((myShot != null) &&
			    (!pTable.isValidHistory(theItem, myShot.theSnapShot)))
				myShot = null;			
		}
		
		/* If we are changed, check the cursor */
		else if (myState == DataState.CHANGED) {
			/* Access potential further valid history from a valid cursor */
			if ((theCursor != null) &&
				(theCursor.theNext != null))
				myShot = theCursor.theNext;
		
			/* Handle validity for table */
			if ((myShot != null) &&
				(!pTable.isValidHistory(theItem, myShot.theSnapShot)))
				myShot = null;
		}
		
		/* Return details */
		return (myShot != null);
	}
	
	/**
	 *  Is there any previous history from the cursor 
	 *  @param pObj the current values  
	 *  @return whether there are entries in the history list
	 */
	protected boolean hasPrevious() {
		DataState	myState;

		/* Need a base element to have valid previous items */
		if (theBase == null)	return false;
		
		/* Access  the State */
		myState = theItem.getState();
		
		/* If we are not changed, return false */
		if (myState != DataState.CHANGED) return false;

		/* We have previous peek-ability if we have a cursor */
		return (theCursor != null);
	}
	
	/**
	 *  Clear history
	 */
	protected void clearHistory() {
		/* Retain the current values */
		HistoryValues<T> myCurr = theCurr;
		
		/* Remove all history */
		resetHistory();
		
		/* Restore the current values */
		theCurr = myCurr;
	}
	
	/**
	 *  Reset history
	 */
	protected void resetHistory() {
		/* Remove all history */
		while (theTop != null) popTheHistory();
	}
	
	/**
	 *  Set history explicitly
	 */
	protected void setHistory() {
		HistoryControl<?> 				myControl = theBase.getHistory();
		HistoryControl<?>.histElement 	myOriginal;
		HistoryValues<?>				myBaseValues;
		
		/* Access the original values for the base */
		myOriginal   = myControl.theOriginal;
		myBaseValues = (myOriginal != null) ? myOriginal.theSnapShot
										    : myControl.theCurr;
		
		/* Create a new history element */
		histElement myEl = new histElement();
		
		/* Add to the end of the list */
		myEl.thePrev = theOriginal;
		if (theOriginal != null) theOriginal.theNext = myEl;
		if (theTop      == null) theTop = myEl; 
		theOriginal	= myEl;
		
		/* Store the values correctly */
		theOriginal.theSnapShot.copyFrom(myBaseValues);
	}
	
	/**
	 *  Determines whether a particular field has changed
	 *  @param fieldNo the field identifier
	 */
	protected boolean fieldChanged(int fieldNo) {
		/* Handle case where there are no changes at all */
		if (theOriginal == null) return false;
		
		/* Call the function from the interface */
		return theCurr.fieldChanged(fieldNo, theOriginal.theSnapShot);
	}
	
	/**
	 * Format the historical changes in this list
	 * @return the formatted changes
	 */
	public StringBuilder toHTMLString() {
		histElement		myCurr;
		StringBuilder	myString = new StringBuilder(1000);
		
		/* Loop through the elements */
		for (myCurr  = theTop;
			 myCurr != null;
			 myCurr  = myCurr.theNext) {
			/* Format the historical element */
			myString.append(myCurr.toHTMLString());
		}
		
		/* Return the formatted string */
		return myString;
	}
	
	/**
	 * The history element class 
	 */
	private class histElement {
		/**
		 * The next element in the list
		 */
		private histElement 		theNext 	= null;
		
		/**
		 * The previous element in the list
		 */
		private histElement 		thePrev 	= null;
		
		/**
		 * The snapshot of values referred to by this element
		 */
		private HistoryValues<T>  	theSnapShot	= null;
		
		/**
		 * Constructor for element
		 */
		public histElement() {
			/* Take a snapshot of the current values */
			theSnapShot = theCurr.copySelf();
		}
		
		/**
		 * Format the changes represented by this element
		 * @return the formatted changes
		 */
		public StringBuilder toHTMLString() {
			HistoryValues<T> 	myObj;
			int 				fieldId;
			int					myCount;
			StringBuilder		myString = new StringBuilder(100);
			
			/* Determine the previous that we will compare against */
			myObj = (thePrev != null) ? thePrev.theSnapShot : theCurr;
			
			/* Loop through the fields */
			for (fieldId = 0, myCount = 0;
				 fieldId < theItem.numFields();
				 fieldId++) {
				/* Note if the field has changed */
				if (theSnapShot.fieldChanged(fieldId, myObj))
					myCount++;
			}
			
			/* Start the values section */
			myString.append("<tr><th rowspan=\"");
			myString.append(myCount+1);
			myString.append("\">Changes</th></tr>");
			
			/* Loop through the fields */
			for (fieldId = 0;
				 fieldId < theItem.numFields();
				 fieldId++) {
				/* If the field has changed */
				if (theSnapShot.fieldChanged(fieldId, myObj)) {
					/* Format the field */
					myString.append("<tr><td>"); 
					myString.append(theItem.getFieldName(fieldId)); 
					myString.append("</td><td>"); 
					myString.append(theItem.formatField(fieldId, theSnapShot));
					myString.append("</td></tr>");
				}
			}
			
			/* Return the formatted string */
			return myString;
		}
	}
}

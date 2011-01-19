package finance;

import finance.finObject.State;
import finance.finObject.EditState;

/**
 * Provides generic underlying object classes for use by the finance package.
 * In particular this class provides 
 * <ul>
 * <li>list management
 * <li>error management
 * <li>state management
 * <li>change management
 * </ul> 
 * 
 * Two basic object classes are provided with other internal utility classes used to 
 * provide various functions
 *   
 * @author 	Tony Washer
 * @version 1.0
 * 
 * @see finLink.itemCtl
 * @see finLink.itemElement
 */
public class finLink {
	/**
	 * Interface for link-able objects allowing comparison functions
	 */
	public static interface linkObject {
		/**
		 * Compares two link-able objects to determine sort order
		 * @param pCompare the comparison object
		 * @return (-1,0,1) depending on whether this object is before, equal or after the comparison object
		 */
		int     linkCompareTo(linkObject pCompare);
	}
	
	/**
	 * Interface for Value objects allowing history functions
	 */
	public static interface histObject {
		/**
		 * Is this object identical to the comparison object
		 * @param pCompare the comparison object
		 * @return <code>true</code> if the objects are equal <code>false</code> otherwise
		 */
		boolean    histEquals(histObject pCompare);
		
		/**
		 * Initialises the object with values from another object
		 * @param pSource the object to copy values from
		 */
		void       copyFrom(histObject pSource);
		
		/**
		 * Provides a cloned object of its own values
		 * @return the cloned object
		 */
		histObject copySelf();
		
		/**
		 * Determines whether the indicated field has changed from the original
		 * @param fieldNo the field to check
		 * @param pOriginal the original values
		 * @return <code>true</code> if the field has changed <code>false</code> otherwise
		 */
		boolean	   fieldChanged(int fieldNo, histObject pOriginal);
	}
	
	/**
	 * Provides the implementation of a list in the finance package
	 * The list is doubly linked and the elements are nested objects in the list
	 * Each element contains a reference to a {@link finLink.linkObject} which is the object that is 
	 * truly in the list  
	 * @see finLink.linkCtl.linkElement
	 */
	protected static class linkCtl {
		/**
		 * The first element in the list
		 */
		private linkElement theFirst        = null;

		/**
		 * The last element in the list
		 */
		private linkElement theLast         = null;

		/**
		 * Self reference to allow nested classes access to methods
		 */
		private linkCtl     theLink         = this;
		
		/**
		 * Is the search for insert point conducted from the start or end of the list
		 */
		private boolean     insertFromStart = true;
		
		/**
		 * Access the object that is first in the list
		 * @return the first object in the list or <code>null</code>
		 */
		protected linkObject  getFirst()   { 
			return (theFirst == null) ? null : theFirst.theObj; }
		
		/**
		 * Access the object that is last in the list
		 * @return the last object in the list or <code>null</code>
		 */
		protected linkObject  getLast()    { 
			return (theLast == null)  ? null : theLast.theObj; }
		
		/**
		 *  Construct a list
		 *  @param fromStart - should inserts be attempted from start/end of list
		 */
		protected linkCtl(boolean fromStart) { insertFromStart = fromStart; }
		
		/**
		 *  add an object to the list  
		 *  @param pItem - item to add to the list
		 */
		private void addItem(linkElement pItem) {
			/* Add in the appropriate fashion */
			if (insertFromStart) 
				addItemFromStart(pItem);
			else
				addItemFromEnd(pItem);
		}
		
		/**
		 *  add Item to the list searching from the start
		 *  @param pItem - item to add to the list
		 */
		private void addItemFromStart(linkElement pItem) {
			linkElement myCurr;
		   
		   	/* Loop through the current items */
		    for(myCurr = theFirst;
		        myCurr != null;
		        myCurr = myCurr.getNextEl())
		    {
		    	/* Break if we have found an element that should be later */
		    	if (myCurr.compareTo(pItem) >= 0) break;
		    }
		       
		    /* If we found an insert point */
		    if (myCurr != null) {
		    	/* Set values for the new item */
		        pItem.thePrev = myCurr.thePrev;
		        pItem.theNext = myCurr;
		    	    
		        /* Add to the list */
		        myCurr.thePrev = pItem;
		        if (pItem.thePrev != null)
		        	pItem.thePrev.theNext = pItem;
		        else 
		        	theFirst = pItem;
		    }
		       	
		    /* else we need to add to the end of the list */
		    else {
		    	/* Set values for the new item */
		    	pItem.thePrev = theLast;
		    	pItem.theNext = null;
		    	
		        /* Add to the list */
	    	    theLast = pItem;   
		    	if (pItem.thePrev != null)
		    		pItem.thePrev.theNext = pItem;
		    	else 
		    		theFirst = pItem;
		    }
		}
		
		/**
		 *  add Item to the list searching from the end
		 *  @param pItem - item to add to the list
		 */
		private void addItemFromEnd(linkElement pItem) {
			linkElement myCurr;
		   
		   	/* Loop backwards through the current items */
		    for(myCurr = theLast;
		        myCurr != null;
		        myCurr = myCurr.getPrevEl())
		    {
		    	/* Break if we have found an element that should be earlier */
		    	if (myCurr.compareTo(pItem) <= 0) break;
		    }
		       
		    /* If we found an insert point */
		    if (myCurr != null) {
		    	/* Set values for the new item */
		        pItem.theNext = myCurr.theNext;
		        pItem.thePrev = myCurr;
		    	    
		        /* Add to the list */
		        myCurr.theNext = pItem;
		        if (pItem.theNext != null)
		        	pItem.theNext.thePrev = pItem;
		        else 
		        	theLast = pItem;
		    }
		       	
		    /* else we need to add to the beginning of the list */
		    else {
		    	/* Set values for the new item */
		    	pItem.theNext = theFirst;
		    	pItem.thePrev = null;
		    	
		        /* Add to the list */
	    	    theFirst = pItem;   
		    	if (pItem.theNext != null)
		    		pItem.theNext.thePrev = pItem;
		    	else 
		    		theLast = pItem;
		    }
		}
		
		/**
		 *  Remove item from list
		 *  
		 *  @param pItem - item to remove from list
		 */
		private void unLink(linkElement pItem) {
			/* Adjust pointers to skip this element */
			if (pItem.thePrev != null)
				pItem.thePrev.theNext = pItem.theNext;
			else 
				theFirst = pItem.theNext;
			if (pItem.theNext != null)
				pItem.theNext.thePrev = pItem.thePrev;
			else
				theLast = pItem.thePrev;
				
			/* clean our links */
			pItem.theNext = null;
			pItem.thePrev = null;
		}
		
		/**
		 * Purge all list items
		 */
		private void purge() {
			/* Loop unlinking the first item */
			while (theFirst != null) unLink(theFirst);
		}
		
		/**
		 * Represents an element of the list
		 */
		protected class linkElement {
			/**
			 * The element that is after this in the list 
			 * or <code>null</code> if this is last in the list
			 */
			private linkElement theNext = null;
			
			/**
			 * The element that is prior to this in the list 
			 * or <code>null</code> if this is first in the list
			 */
			private linkElement thePrev = null;
			
			/**
			 * The actual object that is referenced
			 */
			private linkObject  theObj  = null;
			
			/**
			 * Get the element following this one
			 * @return the next element
			 */
			private linkElement getNextEl()  { return theNext; }

			/**
			 * Get the element prior to this one
			 * @return the previous element
			 */
			private linkElement getPrevEl()  { return thePrev; }

			/**
			 * Get the object following this one
			 * @return the next object
			 */
			protected  linkObject  getNext()    { 
				return (theNext == null) ? null : theNext.theObj; }

			/**
			 * Get the object prior to this one
			 * @return the previous object
			 */
			protected  linkObject  getPrev()    { 
				return (thePrev == null) ? null : thePrev.theObj; }
			
			/**
			 * Construct a new element for this list
			 * @param pObj the referenced object
			 */
			protected linkElement(linkObject pObj) {
				theObj = pObj;
			}
			
			/**
			 * Compare this link element to another to determine sort order
			 * @return (-1,0,1)
			 */
			private int compareTo(linkElement pItem) {
			   return theObj.linkCompareTo(pItem.theObj);
			}
			
			/**
			 * Add item to the list
			 */
			protected void addToList() {
				theLink.addItem(this);
			}
			
			/**
			 * Remove item from list
			 */
			protected void unLink() {
				theLink.unLink(this);
			}
			
			/**
			 * Re-link an item whose data has changed
			 */
			private void reSort() {
			
				/* Remove the item from the list */
				unLink();
			
				/* Add the item again */
				addToList();
			}
		}
	}
	
	/**
	 * Provides the implementation of a history buffer in the finance package
	 * Each element represents a changed set of values and refers to a {@link finLink.histObject} object
	 * which is the set of changeable values for the object. 
	 * @see finLink.historyCtl.histElement
	 */
	private static class historyCtl {
		/**
		 * The most recent change in the set of past values
		 */
		private histElement theTop    = null;
		
		/**
		 * The original set of values if any changes have been made
		 */
		private histElement theBase   = null;
		
		/**
		 * The restore cursor indicating where the restore cursor is in the set of changes
		 */
		private histElement theCursor = null;
		
		/**
		 * The item containing the history buffer
		 */
		private itemElement theItem	  = null;
				
		/**
		 *  Get original values
		 *  @return original values
		 */
		private histObject getBase() {
			return (theBase != null) ? theBase.theObj : theItem.getObj();
		}
		
		/**
		 *  Constructor for a history buffer
		 *  @param pItem the item owning the history buffer
		 */
		private historyCtl(itemElement pItem) { theItem = pItem; }
		
		/**
		 *  Push Item to the history
		 *  @param pItem - item to add to the history
		 */
		private void pushHistory(histObject pItem) {
			/* Create a new history element */
			histElement myEl = new histElement(pItem);
			
			/* Add to the start of the list */
			myEl.theNext = theTop;
			if (theTop != null) theTop.thePrev = myEl;
			if (theBase == null) theBase = myEl;
			theTop       = myEl; 
		}
		
		/**
		 *  popItem from the history and remove from history
		 *  @return last history item
		 */
		private histObject popTheHistory() {
			histElement myItem;
			
			/* Remove top-most element from the list */
			myItem = theTop;
			
			/* Reset the cursor */
			theCursor = null;
			
			/* If there is an item return its object */
			if (myItem != null) {
				theTop = myItem.theNext;
				if (theTop != null) theTop.thePrev = null;
				else theBase = null;
				return myItem.theObj;
				
			/* else return null */
			} else return null;
		}
		
		/**
		 *  popItem from the history if equal to current
		 *  @return <code>true/false</code> was history retained?
		 */
		private boolean maybePopHistory(histObject pCurr) {
			/* Pop element if identical */
			if (pCurr.histEquals(theTop.theObj)) {
				popTheHistory();
				return false;
			}
			return true;
		}
		
		/**
		 *  peek the next item from the history
		 *  @return the next item
		 */
		private histElement peekFurther() {
			/* If we have no cursor, point to start of history */
			if (theCursor == null) {
				theCursor = theTop;
				return theCursor;
			}
			
			/* Shift cursor */
			if (theCursor.theNext != null) {
				theCursor = theCursor.theNext;
				return theCursor;
			}
			
			/* No item to return */
			return null;
		}
		
		/**
		 *  peek the previous item from the history
		 *  @return the previous item
		 */
		private histElement peekPrevious() {
			/* If we have a cursor return its previous value */
			if (theCursor != null) {
				theCursor = theCursor.thePrev;
				return theCursor;
			}
			
			/* No item to return */
			return null;
		}
		
		/**
		 *  Is there any history
		 *  @return whether there are entries in the history list
		 */
		private boolean hasHistory() {
			return (theTop != null);
		}
		
		/**
		 *  Is there any valid history for this table
		 *  @param pTable the table to test against
		 *  @return whether the top entry in the the history list is valid for the table
		 */
		private boolean hasValidHistory(finSwing.financeTable pTable) {
			histElement myObj = null;
			
			/* If we have potential valid history */
			if (theTop != null)
				myObj = theTop;
			
			/* Handle validity for table */
			if ((myObj != null) &&
			    (!pTable.isValidObj(theItem, myObj.theObj)))
				myObj = null;
			
			/* Return details */
			return (myObj != null);
		}
		
		/**
		 *  Is there any previous history from the cursor for this table 
		 *  @param pObj the current values  
		 *  @param pTable the table to test against
		 *  @return whether there is further history
		 */
		private boolean hasValidFurther(histObject pObj, finSwing.financeTable pTable) {
			histElement myObj = null;
			
			/* Reset the cursor if we are no longer using it */
			if ((theCursor != null) && (!theCursor.theObj.histEquals(pObj)))
				theCursor = null;
		
			/* Access potential further valid history from a valid cursor */
			if ((theCursor != null) &&
				(theCursor.theNext != null))
				myObj = theCursor.theNext;
			
			/* Handle validity for table */
			if ((myObj != null) &&
				(!pTable.isValidObj(theItem, myObj.theObj)))
				myObj = null;
			
			/* Return details */
			return (myObj != null);
		}
		
		/**
		 *  Is there any previous history from the cursor 
		 *  @return whether there are entries in the history list
		 */
		private boolean hasPrevious(histObject pObj) {
			/* Reset the cursor if we are no longer using it */
			if ((theCursor != null) && (!theCursor.theObj.histEquals(pObj)))
				theCursor = null;
		
			/* We have previous peek-ability if we have a cursor */
			return (theCursor != null);
		}
		
		/**
		 *  Clear history
		 */
		private void clearHistory() {
			/* Remove all history */
			while (theTop != null) popTheHistory();
		}
		
		/**
		 *  Reset history
		 */
		private histObject resetHistory() {
			histObject myLast = null;
			
			/* Remove all history */
			while (theTop != null) myLast = popTheHistory();
			return myLast;
		}
		
		/**
		 *  Set history explicitly
		 *  @param pObj the historic values
		 */
		private void setHistory(histObject pObj) {
			clearHistory();
			pushHistory(pObj);
		}
		
		/**
		 *  Determines whether a particular field has changed
		 *  @param fieldNo the field identifier
		 *  @param pCurrent the current set of values 
		 */
		private boolean fieldChanged(int fieldNo, histObject pCurrent) {
			/* Handle case where there are no changes at all */
			if (theBase == null) return false;
			
			/* Call the function from the interface */
			return pCurrent.fieldChanged(fieldNo, theBase.theObj);
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
			private histElement theNext = null;
			
			/**
			 * The previous element in the list
			 */
			private histElement thePrev = null;
			
			/**
			 * The values referred to by this element
			 */
			private histObject  theObj  = null;
			
			/**
			 * Constructor for element
			 * @param pObj the values for this element
			 */
			public histElement(histObject pObj) {
				theObj = pObj;
			}
			
			/**
			 * Format the changes represented by this element
			 * @return the formatted changes
			 */
			public StringBuilder toHTMLString() {
				histObject 		myObj;
				int 			fieldId;
				int				myCount;
				StringBuilder	myString = new StringBuilder(100);
				
				/* Determine the previous that we will compare against */
				myObj = (thePrev != null) ? thePrev.theObj : theItem.getObj();
				
				/* Loop through the fields */
				for (fieldId = 0, myCount = 0;
					 fieldId < theItem.numFields();
					 fieldId++) {
					/* If the field has changed */
					if (theObj.fieldChanged(fieldId, myObj))
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
					if (theObj.fieldChanged(fieldId, myObj))
						/* Format the field */
						myString.append(theItem.formatField(fieldId, theObj));
				}
				
				/* Return the formatted string */
				return myString;
			}
		}
	}
	
	/**
	 * Provides the validation control for a finance object holding the list of errors for
	 * an object.
	 * @see finLink.validationCtl.errorElement
	 */
	protected static class validationCtl {
		/**
		 * The first error in the list
		 */
		private errorElement theTop  = null;

		/**
		 * The last error in the list
		 */
		private errorElement theEnd  = null;
		
		/**
		 * Reference to the list control for this object allowing quick setting of error status
		 */
		private itemCtl      theCtl  = null;
				
		/**
		 * Reference to the owner of this object
		 */
		private itemElement  theItem = null;
				
		/**
		 * The number of errors in this buffer
		 */
		private int			 theNumErrors = 0;
		/**
		 * Get the first error in the list
		 * @return the first error or <code>null</code>
		 */
		private errorElement getFirst() { return theTop; }
		
		/**
		 *  Construct a new validation control 
		 *  @param pItem the owner of this object
		 */
		private validationCtl(itemElement pItem) {
			theItem = pItem;
			theCtl  = pItem.getCtl();
		}
		
		/**
		 *  add error to the list 
		 *  @param pText the text for the error
		 *  @param iFieldId the field id for the error
		 */
		private void addError(String pText,
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
			theCtl.setEditState(EditState.ERROR);
		}
		
		/**
		 *  Determine whether there are any errors for a particular field
		 *  @param iField - the field number to check
		 *  @return <code>true</code> if there are any errors <code>false</code> otherwise
		 */
		private boolean hasErrors(int iField) {
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
		private String getFieldError(int iField) {
			errorElement myCurr;
			for (myCurr = theTop;
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				if (myCurr.getField() == iField) return myCurr.getError();
			}
			return null;
		}
		
		/**
		 *  Clear errors
		 */
		private void clearErrors() {
			/* Remove all errors */
			theTop       = null;
			theEnd       = null;
			theNumErrors = 0;
		}
		
		/**
		 * Format the errors in this list
		 * @return the formatted changes
		 */
		public StringBuilder toHTMLString() {
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
				myString.append(theItem.fieldName(theField));
				myString.append("</td><td>");
				myString.append(theError);
				myString.append("</td</tr>");
				
				/* Return the string */
				return myString;
			}
		}
	}
	
	/**
	 * Provides the abstract itemCtl class as the basis for finance project lists
	 * @see finLink.linkCtl
	 * @see finLink.itemElement
	 */
	protected static abstract class itemCtl implements finObject.htmlDumpable {
		/**
		 * The underlying list class
		 */
		private linkCtl   theList     = null;

		/**
		 * The style of the list
		 */
		private ListStyle theStyle    = ListStyle.CORE;

		/**
		 * Should deleted items be counted or ignored
		 */
		private boolean   showDeleted = false;
		
		/**
		 * The edit state of the list
		 */
		private EditState theEdit	  = EditState.CLEAN;
		
		/**
		 * Get the first element of the list
		 * @return the first element (or <code>null</code>)
		 */
		protected itemElement    getFirst()    { return (itemElement)theList.getFirst(); }

		/**
		 * Get the last element of the list
		 * @return the last element (or <code>null</code>)
		 */
		protected itemElement    getLast()     { return (itemElement)theList.getLast(); }
		
		/**
		 * Get the list control
		 * @return the list control
		 */
		private   linkCtl        getList()     { return theList; }

		/**
		 * Get the style of the list
		 * @return the list style
		 */
		protected ListStyle      getStyle()    { return theStyle; }

		/**
		 * Get the EditState of the list
		 * @return the Edit State
		 */
		public    EditState      getEditState(){ return theEdit; }

		/**
		 * Determine whether the list got any errors
		 * @return <code>true/false</code>
		 */
		public	  boolean        hasErrors()   { return (theEdit == EditState.ERROR); }

		/**
		 * Determine whether the list got any changes
		 * @return <code>true/false</code>
		 */
		public	  boolean        hasChanges()  { return (theEdit != EditState.CLEAN); }

		/**
		 * Determine whether the list is valid (or are there errors/non-validated changes)
		 * @return <code>true/false</code>
		 */
		public	  boolean        isValid()     { return ((theEdit == EditState.CLEAN) ||
				                                         (theEdit == EditState.VALID)); }

		/**
		 * Determine whether the list is Locked (overwritten as required)
		 * @return <code>true/false</code>
		 */
		public 	  boolean		 isLocked()    { return false; }

		/**
		 * Determine whether we should count deleted items as present
		 * @return <code>true/false</code>
		 */
		public 	  boolean        getShowDeleted() { return showDeleted; }
		
		/**
		 * Set whether we should count deleted items as present
		 * @param bShow <code>true/false</code>
		 */
		public	  void           setShowDeleted(boolean bShow) {
			showDeleted = bShow; }

		/**
		 * Construct a new object
		 * @param pStyle the new {@link finLink.itemCtl.ListStyle}
		 * @param fromStart - should inserts be attempted from start/end of list
		 */
		protected itemCtl(ListStyle pStyle,
				          boolean   fromStart) {
			theStyle = pStyle;
			theList  = new linkCtl(fromStart);
		}
		
		/**
		 * Construct an update/edit/core extract of an itemCtl list
		 * 
		 * @param pList      The list to extract from
		 * @param pStyle	 the Style of the list  
		 */
		protected itemCtl(itemCtl pList, ListStyle pStyle) {
			/* Local variables */
			itemElement myCurr;
			itemElement myItem;
			
			/* Make this list the correct style */
			theStyle = pStyle;
			theList  = new linkCtl(false);
			
			/* Note that this list should show deleted items on UPDATE */
			if (pStyle == ListStyle.UPDATE) setShowDeleted(true);
			
			/* Loop through the list */
			for (myCurr = pList.getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* If this item is not CLEAN or this is not an update extract */
				if ((pStyle != ListStyle.UPDATE) ||
				    (myCurr.getState() != State.CLEAN)) {
					/* Copy the item */
					myItem = addNewItem(myCurr);
					
					/* If the item is a changed object */
					if ((pStyle == ListStyle.UPDATE) &&
						(myItem.getState() == State.CHANGED)) {
						/* Ensure that we record the correct history */
						if (myItem.getObj() != null) myItem.setHistory();
					}
				}
			}
		}
	
		/**
		 * Construct a difference extract between two itemCtl lists.
		 * The difference extract will only have items that differ between the two lists.
		 * Items that are in the new list, but not in the old list will be viewed as inserted.
		 * Items that are in the old list but not in the new list will be viewed as deleted.
		 * Items that are in both list but differ will be viewed as changed 
		 * 
		 * @param pNew The new list to extract from 
		 * @param pOld The old list to extract from 
		 */
		protected itemCtl(itemCtl pNew, itemCtl pOld) {
			/* Local variables */
			itemElement myCurr;
			itemElement myItem;
			itemElement myNew;
			itemCtl		myOld;
			
			/* Make this list the correct style */
			theStyle = ListStyle.DIFFER;
			theList  = new linkCtl(false);
			
			/* Create a clone of the old list */
			myOld = pOld.cloneIt();
			
			/* Note that this list should show deleted items */
			setShowDeleted(true);
			
			/* Loop through the new list */
			for (myCurr  = pNew.getFirst();
			     myCurr != null;
			     myCurr  = myCurr.getNext()) {
				/* Locate the item in the old list */
				myItem = myOld.searchFor(myCurr.getId());
				
				/* If the item does not exist */
				if (myItem == null) {
					/* Insert a new item */
					myItem = addNewItem(myCurr);
					myItem.setBase(null);
					myItem.setState(State.NEW);
				}
				
				/* else the item exists in the old list */
				else { 
					/* If the item has changed */
					if (!myCurr.equals(myItem)) {
						/* Copy the item */
						myNew = addNewItem(myCurr);
						myNew.setBase(myItem);
						myNew.setState(State.CHANGED);
					
						/* Ensure that we record the correct history */
						if (myNew.getObj() != null) myNew.setHistory();
					}
					
					/* Unlink the old item to improve search speed */
					myItem.unLink();
				}
			}
			
			/* Loop through the remaining items in the old list */
			for (myCurr = myOld.getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* Insert a new item */
				myItem = addNewItem(myCurr);
				myItem.setBase(null);
				myItem.setState(State.DELETED);
			}
		}
	
		/**
		 * Re-base the list against a database image.
		 * This method is used to re-synchronise between two sources.
		 * Items that are in this list, but not in the base list will be viewed as inserted.
		 * Items that are in the base list but not in this list list will be viewed as deleted.
		 * Items that are in both list but differ will be viewed as changed 
		 * 
		 * @param pBase The base list to re-base on 
		 */
		protected void reBase(itemCtl pBase) {
			/* Local variables */
			itemElement myCurr;
			itemElement myItem;
			itemCtl		myBase;
			
			/* Create a clone of the base list */
			myBase = pBase.cloneIt();
			
			/* Loop through this list */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* Locate the item in the base list */
				myItem = myBase.searchFor(myCurr.getId());
				
				/* If the item does not exist */
				if (myItem == null) {
					/* Mark this as a new item with no history or base */
					myCurr.setBase(null);
					myCurr.clearHistory();
					myCurr.clearErrors();
					myCurr.setState(State.NEW);
				}
				
				/* else the item exists in the old list */
				else { 
					/* if it has changed */
					if (!myCurr.equals(myItem)) {					
						/* Mark this as a changed item */
						myCurr.setBase(myItem);
						myCurr.clearHistory();
						myCurr.clearErrors();
						myCurr.setState(State.CHANGED);
					
						/* Set correct history */
						if (myCurr.getObj() != null) myCurr.setHistory();
						myCurr.setBase(null);
					}
				
					/* else it is identical */
					else {
						/* Mark this as a clean item with no history or base */
						myCurr.setBase(null);
						myCurr.clearHistory();
						myCurr.clearErrors();
						myCurr.setState(State.CLEAN);					
					}
				
					/* Unlink the old item to improve search speed */
					myItem.unLink();
				}
			}
			
			/* Loop through the remaining items in the base list */
			for (myCurr = myBase.getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* Insert a new item */
				myItem = addNewItem(myCurr);
				myItem.setBase(null);
				myItem.setState(State.DELETED);
			}
		}
	
		/**
		 * Obtain a clone of the list
		 * @return the clone of  the list
		 */
		abstract protected itemCtl cloneIt();
		
		/**
		 * Obtain the type of the list
		 * @return the type of the list
		 */
		abstract public String itemType();
		
		/**
		 * Compare this list to another to establish equality.
		 * 
		 * @param that The list to compare to
		 * @return <code>true</code> if the event lists are identical,
		 *  <code>false</code> otherwise
		 */
		protected boolean equals(itemCtl that) {
			/* Local variables */
			itemElement myCurr;
			itemElement myOther;
			
			/* Loop through the list */
			for (myCurr = getFirst(), myOther = that.getFirst();
			     (myCurr != null) || (myOther != null);
			     myCurr = myCurr.getNext(), myOther = myOther.getNext()) {
				/* If either entry is null then we differ */
				if ((myCurr == null) || (myOther == null)) return false;
				
				/* If the entries differ then the lists differ */
				if (!myCurr.equals(myOther)) return false;
			}
			
			/* We are identical */
			return true;
		}

		/**
		 * Provide a string representation of this object
		 * @return formatted string
		 */
		public StringBuilder toHTMLString() {
			/* Local variables */
			itemElement 	myCurr;	
			StringBuilder	myString = new StringBuilder(10000);
			
			/* Format the table headers */
			myString.append("<table border=\"1\" width=\"75%\" align=\"center\">");
			myString.append("<thead><th>");
			myString.append(itemType());
			myString.append("List</th>");
			myString.append("<th>Property</th><th>Value</th></thead><tbody>");
			
			/* Start the status section */
			myString.append("<tr><th rowspan=\"");
			myString.append((showDeleted) ? 4 : 3);
			myString.append("\">Status</th></tr>");
			
			/* Format the listStyle and editState */
			myString.append("<tr><td>ListStyle</td><td>"); 
			myString.append(theStyle); 
			myString.append("</td></tr>"); 
			myString.append("<tr><td>EditState</td><td>"); 
			myString.append(theEdit); 
			myString.append("</td></tr>"); 
			if (showDeleted) myString.append("<tr><td>showDeleted</td><td>true</td></tr>");
			myString.append("</tbody></table>"); 
			
			/* Loop through the list */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* Format the Item */
				myString.append("<p>");
				myString.append(myCurr.toHTMLString());
			}
			
			/* Return the string */
			return myString;
		}
		
		/**
		 * Set the EditState for the list (forcible on error/change)
		 * @param pState the new {@link finObject.EditState} (only ERROR/DIRTY)
		 */
		public	  void           setEditState(EditState pState) {
			switch (pState) {
				case ERROR: theEdit = pState; break;
				case DIRTY: if (theEdit != EditState.ERROR)
								theEdit = pState;
							break;
			}
		}
		
		/**
		 * Calculate the Edit State for the list
		 */
		public void findEditState() {
			itemElement myCurr;
			boolean		isDirty = false;
			boolean		isError = false;
			boolean		isValid	= false;
			
			/* Loop through the items */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* If the item is deleted */
				if (myCurr.isDeleted()) {
					/* If the base element is not deleted we have a valid change */
					if (!myCurr.isCoreDeleted()) isValid = true;
				}
				
				/* Else the item is active */
				else
				{
					switch (myCurr.theEdit) {
						case CLEAN: 				break;
						case DIRTY: isDirty = true; break;
						case VALID: isValid = true; break;
						case ERROR: isError = true; break;
					}
				}
			}
			
			/* Set state */
			if 		(isError) theEdit = EditState.ERROR;
			else if (isDirty) theEdit = EditState.DIRTY;
			else if (isValid) theEdit = EditState.VALID;
			else 			  theEdit = EditState.CLEAN;
		}
		
		/**
		 * Search for a particular item by Id
		 * @param uId Id of Item
		 * @return The Item if present (or <code>null</code>)
		 */
		protected itemElement searchFor(long uId) {
			itemElement myCurr;
			
			/* Loop through the items to find the match */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				if (myCurr.getId() == uId) break;
			}
			
			/* Return result */
			return myCurr;
		}
		
		/**
		 * Search for a particular item by index
		 * @param uIndex Index of item
		 * @return The Item if present (or <code>null</code>)
		 */
		public itemElement extractItemAt(long uIndex) {
			itemElement myCurr;
			long        uItem;
			
			/* If negative row return null */
			if (uIndex < 0) return null;
			
			/* Loop through the items to find the match */
			for (myCurr = getFirst(), uItem = -1;
			     (myCurr != null) && (uItem < uIndex);
			     myCurr = myCurr.getNext()) {
				/* Ignore deleted items if desired */
				if ((!showDeleted) && (myCurr.isDeleted())) continue;
				
				/* Increment item and break loop if found */
				if (++uItem == uIndex) break;
			}
			
			/* Return result */
			return myCurr;
		}
		
		/**
		 * Count how many items exist in the list
		 * @return The count of items
		 */
		public int countItems() {
			itemElement myCurr;
			int         uItem;
			
			/* Loop through the items to find the match */
			for (myCurr = getFirst(), uItem = 0;
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* Ignore deleted items if desired */
				if ((!showDeleted) && (myCurr.isDeleted())) continue;
				
				/* Increment item count */
				++uItem;
			}
			
			/* Return result */
			return uItem;
		}
		
		/**
		 * Determine whether the list have active members
		 * @return <code>true/false</code>
		 */
		public boolean   hasMembers()   {
			itemElement myCurr;
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				if ((showDeleted) || 
					(!myCurr.isDeleted())) return true;
			}
			return false;
		} 
		
		/**
		 * Validate the events
		 */
		public void validate() {
			itemElement myCurr;

			/* Clear the errors */
			clearErrors();
			
			/* Loop through the list */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				/* Skip deleted items */
				if (myCurr.isDeleted()) continue;
				
				/* Validate the item */
				myCurr.validate();
			}
			
			/* Determine the Edit State */
			findEditState();
		}
		
		/* Check whether we have updates */
		public boolean hasUpdates() {
			itemElement myCurr;
				
			/* Loop through the Lines */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
		
				/* Ignore clean items */
				if (myCurr.getState() == State.CLEAN)
					continue;
					
				/* We have an update */
				return true;
			}
				
			/* We have no updates */
			return false;
		}
		
		/**
		 *  Clear errors
		 */
		public void clearErrors() {
			itemElement myCurr;
			
			/* Loop through items clearing validation errors */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myCurr.getNext()) {
				myCurr.clearErrors();
			}
		}
		
		/**
		 * Purge all list items
		 */
		public void purge() { theList.purge(); }
		
		/**
		 * Create a new element in the core list from an edit session (to be over-written)
		 * @param pElement - element to base new item on
		 */
		public abstract itemElement addNewItem(itemElement pElement);
		
		/** 
		 * Apply changes in an edit view back into the core data
		 * @param pCtl - edit view with changes to apply
		 */
		public void applyChanges(itemCtl pCtl) {
			itemElement myCurr;
			itemElement myItem;
			itemElement myNext;
			
			/* Loop through the elements */
			for (myCurr = pCtl.getFirst();
			     myCurr != null;
			     myCurr = myNext) {
				/* Determine the next element */
				myNext = myCurr.getNext();
				
				/* Switch on the state */
				switch (myCurr.getState()) {
					/* Ignore the item if it is clean */
					case CLEAN:
						break;
						
					/* Delete the item from the list if it is a deleted new item */
					case DELNEW:
						myCurr.unLink();
						break;
						
					/* If this is a new item, add it to the list */
					case NEW:
						/* Link this item to the new item */
						myCurr.setBase(addNewItem(myCurr));
						
						/* Clear history and set as a clean item */
						myCurr.clearHistory();
						myCurr.setState(State.CLEAN);
						break;
						
					/* If this is a deleted or deleted-changed item */
					case DELETED:
					case DELCHG:
						/* Access the underlying item and mark as deleted */
						myItem = myCurr.getBase();					
						myItem.setState(State.DELETED);
						
						/* Clear history and set as a clean item */
						myCurr.clearHistory();
						myCurr.setState(State.CLEAN);
						break;
						
					/* If this is a recovered item */
					case RECOVERED:
						/* Access the underlying item and mark as restored */
						myItem = myCurr.getBase();					
						myItem.setState(State.RECOVERED);
						
						/* Clear history and set as a clean item */
						myCurr.clearHistory();
						myCurr.setState(State.CLEAN);
						break;
						
					/* If this is a changed item */
					case CHANGED:
						/* Access underlying item, apply changes and mark as changed */
						myItem = myCurr.getBase();					
						myItem.applyChanges(myCurr);
						myItem.setState(State.CHANGED);
						
						/* Re-sort the item */
						myItem.reSort();
						
						/* Clear history and set as a clean item */
						myCurr.clearHistory();
						myCurr.setState(State.CLEAN);
						break;
				}
			}
		}
		
		/** 
		 * Reset changes in an edit view
		 */
		public void resetChanges() {
			itemElement myCurr;
			itemElement myNext;
			
			/* Loop through the elements */
			for (myCurr = getFirst();
			     myCurr != null;
			     myCurr = myNext) {
				/* Determine the next element */
				myNext = myCurr.getNext();
				
				/* Switch on the state */
				switch (myCurr.getState()) {
					/* Delete the item if it is new or a deleted new item */
					case NEW:
					case DELNEW:
						myCurr.unLink();
						break;
						
					/* If this is a clean item, just ignore */
					case CLEAN:
						break;
						
					/* If this is a deleted or recovered item */
					case DELETED:
					case RECOVERED:				
						/* Mark the item as clean */
						myCurr.setState(State.CLEAN);
						break;
					
					/* If this is a changed or DELCHG item */
					case CHANGED:
					case DELCHG:
						/* Clear changes and mark as clean */
						myCurr.resetHistory();
						myCurr.setState(State.CLEAN);
						break;
				}
			}
		}
		
		/**
		 *  ListStyles
		 */
		public enum ListStyle {
			/**
			 * Core list holding the true version of the data
			 */
			CORE,
			
			/**
			 * Partial extract of the data for the purposes of editing
			 */
			EDIT,
			
			/**
			 * Special editing view for SpotPrices
			 */
			SPOT,
			
			/**
			 * List of changes to be applied to database
			 */
			UPDATE,
			
			/**
			 * Temporary View for validation purposes 
			 */
			VIEW,
			
			/**
			 * List of differences
			 */
			DIFFER;
		}
	}
		
	/**
	 * Provides the abstract itemElement class as the basis for finance project items
	 * @see finLink.itemCtl
	 * @see finLink.historyCtl
	 * @see finLink.validationCtl
	 * @see finLink.linkCtl
	 * @see finLink.linkCtl.linkElement
	 * @see finLink.linkObject
	 */
	public static abstract class itemElement implements linkObject, 
														finObject.htmlDumpable {
		/**
		 * The list to which this item belongs
		 */
		private itemCtl             theCtl     = null;
		
		/**
		 * The linking element for this item
		 */
		private linkCtl.linkElement theLink    = null;
		
		/**
		 * The changeable values for this item
		 */
		private histObject          theObj     = null;
		
		/**
		 * The Change state of this item {@link finObject.State}
		 */
	    private State               theState   = State.NOSTATE;
	    
	    
		/**
		 * The Edit state of this item {@link finObject.EditState}
		 */
	    private EditState           theEdit    = EditState.CLEAN;

	    /**
		 * The base item to which this item is linked. This will be <code>null>/code> in CORE list
		 * and for new items etc.
		 */
	    private itemElement         theBase    = null;

	    /**
		 * Is the item visible to standard searches
		 */
	    private boolean             isDeleted  = false;

	    /**
		 * The id number of the item
		 */
		private long 	            theId 	   = 0;

		/**
		 * The history control {@link finLink.historyCtl}
		 */
		private historyCtl          theHistory = null;

		/**
		 * The validation control {@link finLink.validationCtl}
		 */
		private validationCtl       theErrors  = null;
		
		/**
		 * Get the list control for this item
		 * @return the list control
		 */
		public itemCtl     getCtl()       	{ return theCtl; }
		
		/**
		 * Get the changeable values object for this item 
		 * @return the object
		 */
		public histObject  getObj()         { return theObj; }	

		/**
		 * Get the Id for this item
		 * @return the Id
		 */
		public long        getId()        	{ return theId; }

		/**
		 * Get the EditState for this item
		 * @return the EditState
		 */
		public EditState   getEditState()	{ return theEdit; }

		/**
		 * Get the State for this item
		 * @return the State
		 */
		public State       getState()     	{ return theState; }
		
		/**
		 * Get the base item for this item
		 * @return the Base item or <code>null</code>
		 */
		public itemElement getBase()      	{ return theBase; }

		/**
		 * Determine whether the item is visible to standard searches
		 * @return <code>true/false</code>
		 */
		public boolean     isDeleted()    	{ return isDeleted; }

		/**
		 * Determine whether the underlying base item is deleted
		 * @return <code>true/false</code>
		 */
		public boolean     isCoreDeleted()  { return (theBase != null) &&
													 (theBase.isDeleted); }
		/**
		 * Set the id of the item
		 * @param id of the item
		 */
		public void        setId(long id) 	{ theId = id; }
		
		/**
		 * Set the base item for this item
		 * @param pBase the Base item
		 */
		public void        setBase(itemElement pBase) { theBase = pBase; }
		
		/**
		 * Set the changeable values object for this item
		 * @param pObj the changeable object 
		 */
		public void        setObj(histObject pObj)    { theObj  = pObj; }
		
		/**
		 * Mark this item as invisible to standard searches 
		 */
		public void 	   setInVisible()	{ isDeleted = true; }
		
		/**
		 * Determine whether the item is locked (overridden if required( 
		 * @return <code>true/false</code>
		 */
		public boolean	   isLocked() 	  	{ return false; }

		/**
		 * Determine whether the list is locked (overridden if required( 
		 * @return <code>true/false</code>
		 */
		public boolean	   isListLocked() 	{ return false; }

		/**
		 * Determine the field name for a particular field
		 * This method is always overridden but is used to supply the default field name 
		 * @return the field name
		 */
		public String	   fieldName(int fieldId)	{ return "Unknown"; }
		
		/**
		 * Format this item to a string
		 * @return the formatted item
		 */
		public StringBuilder toHTMLString() {
			StringBuilder	myString = new StringBuilder(2000);
			String	myTemp;
			int     iField;
			int		iNumFields = numFields();
			
			/* Initialise the string with an item name */
			myString.append("<table border=\"1\" width=\"75%\" align=\"center\">");
			myString.append("<thead><th>");
			myString.append(itemType());
			myString.append("</th>");
			myString.append("<th>Field</th><th>Value</th></thead><tbody>");
			
			/* Start the status section */
			myString.append("<tr><th rowspan=\"");
			myString.append((isDeleted) ? 4 : 3);
			myString.append("\">Status</th></tr>");
			
			/* Format the State and edit State */
			myString.append("<tr><td>State</td><td>");
			myString.append(theState);
			myString.append("</td></tr>");
			myString.append("<tr><td>EditState</td><td>");
			myString.append(theEdit);
			myString.append("</td></tr>");
			if (isDeleted) 
				myString.append("<tr><td>Deleted</td><td>true</td></tr>");
			
			/* Start the values section */
			myString.append("<tr><th rowspan=\"");
			myString.append(iNumFields+1);
			myString.append("\">Values</th></tr>");
			
			/* Loop through the fields */
			for (iField = 0;
				 iField < iNumFields;
				 iField++) {
				/* Format the field */
				myTemp = formatField(iField, theObj);
				if (iField == 0)  myTemp = myTemp.substring(4);
				myString.append(myTemp);
			}

			/* If errors exist */
			if (hasErrors()) {
				/* Add details of the errors */
				myString.append(theErrors.toHTMLString());
			}
			
			/* If changes exist */
			if (hasHistory()) {
				/* Add details of the errors */
				myString.append(theHistory.toHTMLString());
			}
			
			/* Terminate the table */
			myString.append("</tbody></table>");
			
			/* Return the formatted item */
			return myString;
		}
		
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		abstract public String itemType();
		
		/**
		 * Obtain the number of fields for an item
		 * @return the number of fields
		 */
		abstract public int	numFields();
		
		/**
		 * Format the value of a particular field
		 * @param iField the field number
		 * @param pObj the values to use
		 * @return the formatted field
		 */
		abstract public String formatField(int iField, histObject pObj);
		
		/**
		 * Get the next item in the list 
		 * @return the next item or <code>null</code>
		 */
		public itemElement getNext()    { return (itemElement)theLink.getNext(); }

		/**
		 * Get the previous item in the list 
		 * @return the previous item or <code>null</code>
		 */
		public itemElement getPrev()    { return (itemElement)theLink.getPrev();  }

		/**
		 * Add the item to the list 
		 */
		public void        addToList()  { theLink.addToList(); }
		
		/**
		 * Unlink the item from the list
		 */
		public void        unLink()     { theLink.unLink(); }
		
		/**
		 * Re-sort the item in the list
		 */
		public void        reSort()     { theLink.reSort(); }
		
		/**
		 * Determine whether the item has changes
		 * @return <code>true/false</code>
		 */
		public boolean     hasHistory()    { return theHistory.hasHistory(); }
		
		/**
		 * Clear the history for the item (leaving current values) 
		 */
		protected void        clearHistory()  { theHistory.clearHistory(); }
		
		/**
		 * Reset the history for the item (restoring original values)
		 */
		public 	  void        resetHistory()  { 
			histObject myObj = theHistory.resetHistory();
			if (myObj != null) theObj = myObj;
		}
		
		/**
		 * Set Change history for an update list so that the first and only entry in the change
		 * list is the original values of the base
		 */
		public 	  void   	  setHistory()  { 
			itemElement myItem = getBase();
			histObject  myObj  = myItem.theHistory.getBase();
			theHistory.setHistory(myObj);
		}
		
		/**
		 * Return the base history object
		 * @return the original values for this object
		 */
		public 	  histObject  getBaseObj()  { 
			return theHistory.getBase();
		}
		
		/**
		 * Check to see whether any changes were made. 
		 * If no changes were made remove last saved history since it is not needed
		 * @return <code>true</code> if changes were made, <code>false</code> otherwise
		 */
		protected boolean     checkForHistory() {
			return theHistory.maybePopHistory(theObj);	}
		
		/**
		 * Push current values into history buffer ready for changes to be made
		 */
		protected void        pushHistory() { 
			theHistory.pushHistory(theObj.copySelf()); }
		
		/**
		 * Remove the last changes for the history buffer and restore values from it
		 */
		public void        popHistory() {
			histObject myVals = theHistory.popTheHistory();
			if (myVals != null) setObj(myVals);
		}
		
		/**
		 * Determine whether a particular field has changed in this edit view
		 * @param fieldNo the field to test
		 * @return <code>true/false</code>
		 */
		public boolean     fieldChanged(int fieldNo) {
			return theHistory.fieldChanged(fieldNo, theObj);
		}
		
		/**
		 * Determine whether there is restore-able history for an item 
		 * @param pTable the table to which data would be restored
		 * @return <code>true/false</code>
		 */
		protected boolean     hasValidHistory(finSwing.financeTable pTable)    { 
			return theHistory.hasValidHistory(pTable); }

		/**
		 * Determine whether there is further history on a CORE list item to peek 
		 * @param pTable the table to which data would be restored
		 * @return <code>true/false</code>
		 */
		public boolean     hasFurther(finSwing.financeTable pTable)    {
			if (theBase == null) 
				return false;
			if (theState == State.CLEAN) 
				return theBase.hasValidHistory(pTable);
			if (theState == State.CHANGED)
				return theBase.theHistory.hasValidFurther(theObj, pTable);
			return false;
		}

		/**
		 * Determine whether there is previous history on a CORE list item to peek 
		 * @return <code>true/false</code>
		 */
		public boolean     hasPrevious()    {
			return ((theState == State.CHANGED) &&
					(theBase!= null) &&
					(theBase.theHistory.hasPrevious(theObj)));
		}

		/**
		 * Restore values from a further history item beyond the current cursor 
		 */
		public void peekFurther()    {
			historyCtl.histElement myElement;
			myElement = theBase.theHistory.peekFurther();
			if (myElement != null) {
				pushHistory();
				theObj.copyFrom(myElement.theObj);
				setState(State.CHANGED);
			}
		}
		
		/**
		 * Restore values from a previous history item beyond the current cursor 
		 */
		public void peekPrevious()    {
			historyCtl.histElement myElement;
			myElement = theBase.theHistory.peekPrevious();
			if (myElement != null) {
				pushHistory();
				theObj.copyFrom(myElement.theObj);
				theHistory.maybePopHistory(theObj);
			}
			else {
				/* Copy values back from base and set to clean*/
				theObj.copyFrom(theBase.theObj);
				theHistory.maybePopHistory(theObj);
				if (!hasHistory()) setState(State.CLEAN);				
			}
		}
		
		/**
		 * Determine whether the item has Errors
		 * @return <code>true/false</code>
		 */
		public	  boolean        hasErrors()   { return (theEdit == EditState.ERROR); }

		/**
		 * Determine whether the item has Changes
		 * @return <code>true/false</code>
		 */
		public	  boolean        hasChanges()  { return (theEdit != EditState.CLEAN); }

		/**
		 * Determine whether the item is Valid
		 * @return <code>true/false</code>
		 */
		public	  boolean        isValid()     { return ((theEdit == EditState.CLEAN) ||
				                                         (theEdit == EditState.VALID)); }

		/**
		 * Determine whether a particular field has Errors
		 * @param iField the particular field
		 * @return <code>true/false</code>
		 */
		public	  boolean      hasErrors(int iField) { 
			return theErrors.hasErrors(iField); }

		/**
		 * Note that this item has been validated 
		 */
		protected  void setValidEdit() {
			if (isCoreDeleted())
				theEdit = (isDeleted) ? EditState.CLEAN : EditState.VALID;
			else if (isDeleted) 
				theEdit = EditState.VALID;
			else
				theEdit = ((hasHistory()) || (getBase() == null)) 
								? EditState.VALID : EditState.CLEAN;
		}

		/**
		 * Clear all errors for this item
		 */
		public	  void         clearErrors()  { theErrors.clearErrors(); }

		/**
		 * Add an error for this item
		 * @param pError the error text
		 * @param iField the associated field
		 */
		protected void         addError(String pError, int iField) {
			theEdit = EditState.ERROR;
			theErrors.addError(pError, iField);	}

		/**
		 * Get the error text for a field
		 * @param iField the associated field
		 * @return the error text
		 */
		public 	  String getFieldError(int iField) {
			return theErrors.getFieldError(iField);	}

		/**
		 * Get the first error element for an item
		 * @return the first error (or <code>null</code>)
		 */
		protected validationCtl.errorElement getFirstError() {
			return theErrors.getFirst();	}

		/**
		 * Construct a new item
		 * @param pCtl the list that this item is associated with
		 * @param uId the Id of the new item (or 0 if not yet known)
		 */
		public itemElement(itemCtl pCtl, long uId) {
			theId      = uId;
			theCtl     = pCtl;
			theLink    = pCtl.getList().new linkElement(this);
			theHistory = new historyCtl(this);
			theErrors  = new validationCtl(this);
		}
		
		/**
		 *  Get the state of the underlying record
		 *  @return the underlying state
		 */
		protected State getBaseState() {
			return (theBase == null) ? State.NOSTATE : theBase.getState();
		}

		/**
		 * Determine index of element within the list
		 * @return The index
		 */
		public int indexOfItem() {
			itemElement myCurr;
			int	        uItem;
			int			uFound = -1;
			
			/* Loop through the items to find this one */
			for (myCurr = theCtl.getFirst(), uItem = 0;
			     (myCurr != null) && (myCurr != this);
			     myCurr = myCurr.getNext()) {
				/* Ignore deleted items if desired */
				if ((!theCtl.showDeleted) && (myCurr.isDeleted())) continue;
				
				/* Increment item */
				++uItem;
			}
			
			/* If we found the item set its index */
			if (myCurr != null) uFound = uItem;
			
			/* Return result */
			return uFound;
		}
		
		/**
		 *  Apply changes to the item from a changed version
		 *  Overwritten by objects that have changes
		 *  @param pElement the changed element
		 */
		public void applyChanges(itemElement pElement){};
		
		/**
		 *  Validate the element
		 *  Overwritten by objects that have changes
		 */
		protected void validate() {};
		
		/**
		 *  Test an element for equality
		 *  @param that the object to test against
		 */
		public abstract boolean equals(itemElement that);
		
		/**
		 * State Management algorithm
		 * 
		 * In a Core list we generally have three states
		 * NEW     - Newly created but not added to DB
		 * CLEAN   - In sync with DB
		 * CHANGED - Changed from DB
		 * 
		 * In addition we have the Delete States
		 * DELETED - DELETED from CLEAN
		 * DELNEW  - DELETED from NEW 
		 * DELCHG  - DELETED from CHANGED
		 * 
		 * The reason for holding the DELETE states as three separate states is 
		 * a) To allow a restore to the correct state
		 * b) To ensure that re-synchronisation to DB does not attempt to 
		 * Delete a DELNEW record which does not exist anyway
		 * 
		 * When changes are made to a NEW record it remains NEW
		 * When changes are made to a CLEAN/CHANGED record it becomes CHANGED
		 * No changes can be made to a DELETED etc record
		 * 
		 * In an Update list we stick to the three states
		 * NEW     - Record needing an insert
		 * CHANGED - Record needing an update
		 * DELETED - Record requiring deletion
		 * 
		 * The underlying Delete state is held in CoreState, allowing proper 
		 * handling of DELNEW records
		 * 
		 * In Edit views, we start off with everything in CLEAN state which means 
		 * that it is unchanged with respect to the core.
		 * New additions to the Edit view become NEW, and changes and deletes are 
		 * handled in the same fashion as for core
		 * 
		 * For restore operations deletes are handled as follows
		 * DELETED -> CLEAN
		 * DELNEW  -> NEW
		 * DELCHG  -> CHANGED
		 * CLEAN(Underlying delete state) -> RESTORED
		 * 
		 * A RESTORED record can now be handled as a special case of CLEAN. It is
		 * necessary to have this extra case to indicate that the underlying record
		 * is to be restored, whereas CLEAN would imply no change. If subsequent
		 * changes are made to a restored record, the restore is still implied since
		 * it can never return to the CLEAN state
		 * 
		 * Undo operations are currently simplistic with the only change points
		 * that can be recovered being the current underlying core state and the 
		 * original core state. However this algorithm holds even if we implement 
		 * multiple change history with NEW being treated the same as CHANGED
		 * 
		 * Edit Undo operations are performed on CHANGED state records only
		 * No history is kept for NEW records in Edit view
		 * Undo restores the record to the values in the underlying core record
		 * The State is changed to CLEAN or RESTORED depending on whether the 
		 * underlying record is deleted or not
		 * If the current state is CLEAN and the underlying state is CHANGED
		 * then the values are reset to the original core state and the Edit status
		 * is set to CHANGED. No other CLEAN state is possible to Undo since NEW has
		 * no history, CLEAN has no changes and DELETED records are unavailable
		 * If the current value is RESTORED then if the underlying status is DELCHG
		 * then we can restore changes as for CLEAN and set the status to CHANGED.
		 * Other underlying deleted value are invalid (DELNEW has no history, 
		 * DELETED has no changes)
		 * 
		 * Applying Edit changes is performed as follows
		 * NEW -> insert record with status of NEW into CORE
		 * DELNEW -> Discard
		 * CLEAN -> Discard
		 * DELETED/DELCHG - NEW -> DELNEW (no changes copied down)
		 * 		   		  - CHANGED -> DELCHG (no changes copied down)
		 * 				  - CLEAN -> DELETED (no changes copied down)
		 * 				  - DEL* -> No change to status (no changes copied down)
		 * RECOVERED- DELNEW -> NEW
		 *          - DELETED -> CLEAN
		 *          - DELCHG -> CHANGED
		 * CHANGED  - NEW -> NEW (changes copied down)
		 * 			- CHANGED -> CHANGED (changes copied down)
		 * 			- CLEAN -> CHANGED (changes copied down)
		 * 			- DELNEW -> NEW (changes copied down)
		 * 			- DELCHG -> CHANGED (changes copied down)
		 * 			- DELETED -> CHANGED (changes copied down) 
		 *
		 * A Spot list has some minor changes to the algorithm in that there are 
		 * no NEW or DELETED states, leaving just CLEAN and CHANGED. The isDeleted
		 * flags is changed in usage to an isVisible flag
		 */
		
		/**
		 * Set the state of the item
		 * @param newState the new state to set
		 */
		public void setState(State newState) {
			/* If this is a Spot list */
			if (theCtl.getStyle() == itemCtl.ListStyle.SPOT) {
				/* Handle as special case */
				switch (newState) {
					case CLEAN:
						theState  = newState;
						theEdit   = EditState.CLEAN;
						isDeleted = false;
						break;
					case CHANGED:
						theState  = newState;
						theEdit   = EditState.DIRTY;
						isDeleted = false;
						break;
				}
				/* Return having completed processing */
				return;
			}
			
			/* Police the action */
			switch (newState) {
				case NEW:
					theState  = newState;
					isDeleted = false;
					theEdit = EditState.DIRTY;
					break;
				case CLEAN:
					theState  = newState;
					theEdit   = EditState.CLEAN;
					switch (getBaseState()) {
						case NOSTATE:
							if (theCtl.getStyle() == itemCtl.ListStyle.EDIT) {
								theState  = State.NEW;
								theEdit   = EditState.DIRTY;
							}
							isDeleted = false;
							break;
						case DELETED:
						case DELNEW:
						case DELCHG:
							isDeleted = true;
							break;
						default:
							isDeleted = false;
							break;
					}
					break;
				case RECOVERED:
					theEdit = EditState.DIRTY;
					isDeleted = false;
					switch (theState) {
						case DELETED:
							theState = State.CLEAN;
							break;
						case DELNEW:
							theState = State.NEW;
							break;
						case DELCHG:
							theState = State.CHANGED;
							break;
						case CLEAN:
							theState = newState;
							break;
					}
					break;
				case DELCHG:
				case DELNEW:
					theState  = State.DELETED;
					isDeleted = true;
					setValidEdit();
					break;
				case CHANGED:
					theCtl.setEditState(EditState.DIRTY);
					theEdit = EditState.DIRTY;
					isDeleted = false;
					switch (theState) {
						case NEW:
						case DELNEW:
							theState = State.NEW;
							break;
						case CHANGED:
						case CLEAN:
						case RECOVERED:
						case DELETED:
						case DELCHG:
						case NOSTATE:
							theState = newState;
							break;
					}
					break;
				case DELETED:
					isDeleted = true;
					setValidEdit();
					switch (theState) {
						case NEW:
							theState  = State.DELNEW;
							break;
						case CHANGED:
							theState  = State.DELCHG;
							break;
						case CLEAN:
						case RECOVERED:
						case NOSTATE:
							theState  = newState;
							break;
						case DELETED:
						case DELNEW:
						case DELCHG:
							break;
					}
					break;
			}
		}		
	}		
}
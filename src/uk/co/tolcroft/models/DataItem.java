package uk.co.tolcroft.models;

import uk.co.tolcroft.models.DataList;
import uk.co.tolcroft.models.DataList.ListStyle;
import uk.co.tolcroft.models.DataState;
import uk.co.tolcroft.models.EditState;

/**
 * Provides the abstract DataItem class as the basis for finance project items. The implementation of the 
 * {@link SortedList.linkObject} interface means that this object can only be held in one list at a time
 * @see uk.co.tolcroft.models.DataList
 */
public abstract class DataItem implements SortedList.linkObject, htmlDumpable {
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
	 * Interface for checking whether history objects are valid in a table view
	 */
	public static interface tableHistory {
		/**
		 * Is this object valid for this item in the table view
		 * @param pItem the item to check
		 * @param pObj the history values to check
		 * @return <code>true</code> if the history object is valid <code>false</code> otherwise
		 */
		boolean    isValidObj(DataItem pItem, histObject pObj);
	}
	
	/**
	 * The list to which this item belongs
	 */
	private DataList<DataItem>	theList		= null;
	
	/**
	 * Self reference
	 */
	private DataItem			theItem		= this;
	
	/**
	 * The changeable values for this item
	 */
	private histObject          theObj     	= null;
	
	/**
	 * The Change state of this item {@link DataState}
	 */
    private DataState			theState	= DataState.NOSTATE;
    
	/**
	 * The Edit state of this item {@link EditState}
	 */
    private EditState           theEdit		= EditState.CLEAN;

    /**
	 * The base item to which this item is linked. This will be <code>null</code> in CORE list
	 * and for new items etc.
	 */
    private DataItem         	theBase		= null;

    /**
	 * Is the item visible to standard searches
	 */
    private boolean             isDeleted	= false;

    /**
	 * Is the item in the process of being changed
	 */
    private boolean             isChangeing	= false;

    /**
	 * Is the item in the process of being restored
	 */
    private boolean             isRestoring	= false;

    /**
	 * Storage for the List Node
	 */
    private Object				theLink		= null;

    /**
	 * The id number of the item
	 */
	private int 	            theId 	   = 0;

	/**
	 * The history control {@link historyCtl}
	 */
	private historyCtl          theHistory = null;

	/**
	 * The validation control {@link validationCtl}
	 */
	private validationCtl       theErrors  = null;
	
	/**
	 * Get the list control for this item
	 * @return the list control
	 */
	public DataList<? extends DataItem>   getList()  	{ return theList; }
	
	/**
	 * Get the changeable values object for this item 
	 * @return the object
	 */
	public histObject	getObj()		{ return theObj; }	

	/**
	 * Get the Id for this item
	 * @return the Id
	 */
	public int			getId()        	{ return theId; }

	/**
	 * Get the EditState for this item
	 * @return the EditState
	 */
	public EditState	getEditState()	{ return theEdit; }

	/**
	 * Get the State for this item
	 * @return the State
	 */
	public DataState	getState()     	{ return theState; }
	
	/**
	 * Get the base item for this item
	 * @return the Base item or <code>null</code>
	 */
	public DataItem		getBase()      	{ return theBase; }

	/**
	 * Get the link node for this item
	 * @return the Link node or <code>null</code>
	 */
	public Object		getLinkNode(Object pList)	{ return theLink; }

	/**
	 * Get the link node for this item
	 * @return the Link node or <code>null</code>
	 */
	public void			setLinkNode(Object l, Object o)	{ theLink = o; }

	/**
	 * Determine whether the item is visible to standard searches
	 * @param isDeleted <code>true/false</code>
	 */
	private void		setDeleted(boolean bDeleted) {
		isDeleted = bDeleted;
		theList.setHidden(this, isDeleted);
	}

	/**
	 * Determine whether the item is in the process of being changed
	 * @param isChangeing <code>true/false</code>
	 */
	protected void	setChangeing(boolean bChangeing) {
		isChangeing = bChangeing;
	}

	/**
	 * Determine whether the item is in the process of being restored
	 * @param isRestoring <code>true/false</code>
	 */
	protected void	setRestoring(boolean bRestoring) {
		isRestoring = bRestoring;
	}

	/**
	 * Determine whether the item is visible to standard searches
	 * @return <code>true/false</code>
	 */
	public void			setHidden()    	{ setDeleted(true); }

	/**
	 * Determine whether the item is visible to standard searches
	 * @return <code>true/false</code>
	 */
	public boolean		isDeleted()    	{ return isDeleted; }

	/**
	 * Determine whether the item is in the process of being changed
	 * @return <code>true/false</code>
	 */
	protected boolean	isChangeing()   { return isChangeing; }

	/**
	 * Determine whether the item is in the process of being restored
	 * @return <code>true/false</code>
	 */
	protected boolean	isRestoring()   { return isRestoring; }

	/**
	 * Determine whether the item is visible to standard searches
	 * @return <code>true/false</code>
	 */
	public boolean		isHidden()    	{ return isDeleted; }

	/**
	 * Determine whether the underlying base item is deleted
	 * @return <code>true/false</code>
	 */
	public boolean		isCoreDeleted() { return (theBase != null) &&
											     (theBase.isDeleted); }
	/**
	 * Set the id of the item
	 * @param id of the item
	 */
	public void			setId(int id) 	{ theId = id; }
	
	/**
	 * Set the base item for this item
	 * @param pBase the Base item
	 */
	public void			setBase(DataItem pBase) { theBase = pBase; }
	
	/**
	 * Set the changeable values object for this item
	 * @param pObj the changeable object 
	 */
	public void			setObj(histObject pObj)    { theObj  = pObj; }
	
	/**
	 * Determine whether the item is locked (overridden if required( 
	 * @return <code>true/false</code>
	 */
	public boolean		isLocked() 	  	{ return false; }

	/**
	 * Determine whether the list is locked (overridden if required( 
	 * @return <code>true/false</code>
	 */
	public boolean		isListLocked() 	{ return false; }

	/**
	 * Determine the field name for a particular field
	 * This method is always overridden but is used to supply the default field name 
	 * @return the field name
	 */
	public String		fieldName(int fieldId)	{ return "Unknown"; }
	
	/**
	 * Stub for extensions to add their own fields
	 * @param pBuffer the string buffer 
	 */
	public void addHTMLFields(StringBuilder pBuffer) {}
	
	/**
	 * Format this item to a string
	 * @return the formatted item
	 */
	public StringBuilder toHTMLString() {
		StringBuilder	myString = new StringBuilder(2000);
		int     iField;
		int		iNumFields = numFields();
		
		/* Initialise the string with an item name */
		myString.append("<table border=\"1\" width=\"90%\" align=\"center\">");
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
			if (iField != 0) myString.append("<tr>"); 
			myString.append("<td>"); 
			myString.append(fieldName(iField)); 
			myString.append("</td><td>"); 
			myString.append(formatField(iField, theObj));
			myString.append("</td></tr>");
		}

		/* Add any additional HTML Fields */
		addHTMLFields(myString);
		
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
		
		/* If we have an underlying object */
		if (theBase != null) {
			/* Format the Underlying object */
			myString.append("<tr><th>Underlying</th><td colspan=\"2\">");
			myString.append(theBase.toHTMLString());
			myString.append("</td></tr>");
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
	public int	numFields() { return 0; }
	
	/**
	 * Format the value of a particular field
	 * @param iField the field number
	 * @param pObj the values to use
	 * @return the formatted field
	 */
	abstract public String formatField(int iField, histObject pObj);
	
	/**
	 * Add the item to the list 
	 */
	public void			addToList()  { theList.add(this); }
	
	/**
	 * Unlink the item from the list
	 */
	public void			unLink()     { theList.remove(this); }
	
	/**
	 * Re-sort the item in the list
	 */
	public void			reSort()     { theList.reSort(this); }
	
	/**
	 * Determine whether the item has changes
	 * @return <code>true/false</code>
	 */
	public boolean		hasHistory()    { return theHistory.hasHistory(); }
	
	/**
	 * Clear the history for the item (leaving current values) 
	 */
	public void			clearHistory()  { theHistory.clearHistory(); }
	
	/**
	 * Reset the history for the item (restoring original values)
	 */
	public 	  void		resetHistory()  { 
		histObject myObj = theHistory.resetHistory();
		if (myObj != null) theObj = myObj;
	}
	
	/**
	 * Set Change history for an update list so that the first and only entry in the change
	 * list is the original values of the base
	 */
	public 	  void		setHistory()  { 
		DataItem myItem = getBase();
		histObject  myObj  = myItem.theHistory.getBase();
		theHistory.setHistory(myObj);
	}
	
	/**
	 * Return the base history object
	 * @return the original values for this object
	 */
	public 	  histObject	getBaseObj()  { 
		return theHistory.getBase();
	}
	
	/**
	 * Check to see whether any changes were made. 
	 * If no changes were made remove last saved history since it is not needed
	 * @return <code>true</code> if changes were made, <code>false</code> otherwise
	 */
	public boolean		checkForHistory() {
		return theHistory.maybePopHistory(theObj);	}
	
	/**
	 * Push current values into history buffer ready for changes to be made
	 */
	public void			pushHistory() { 
		theHistory.pushHistory(theObj.copySelf()); }
	
	/**
	 * Remove the last changes for the history buffer and restore values from it
	 */
	public void				popHistory() {
		histObject myVals = theHistory.popTheHistory();
		if (myVals != null) setObj(myVals);
	}
	
	/**
	 * Determine whether a particular field has changed in this edit view
	 * @param fieldNo the field to test
	 * @return <code>true/false</code>
	 */
	public boolean			fieldChanged(int fieldNo) {
		return theHistory.fieldChanged(fieldNo, theObj);
	}
	
	/**
	 * Determine whether there is restore-able history for an item 
	 * @param pTable the table to which data would be restored
	 * @return <code>true/false</code>
	 */
	protected boolean		hasValidHistory(tableHistory pTable)    { 
		return theHistory.hasValidHistory(pTable); }

	/**
	 * Determine whether there is further history on a CORE list item to peek 
	 * @param pTable the table to which data would be restored
	 * @return <code>true/false</code>
	 */
	public boolean			hasFurther(tableHistory pTable)    {
		if (theBase == null) 
			return false;
		if (theState == DataState.CLEAN) 
			return theBase.hasValidHistory(pTable);
		if (theState == DataState.CHANGED)
			return theBase.theHistory.hasValidFurther(theObj, pTable);
		return false;
	}

	/**
	 * Determine whether there is previous history on a CORE list item to peek 
	 * @return <code>true/false</code>
	 */
	public boolean			hasPrevious()    {
		return ((theState == DataState.CHANGED) &&
				(theBase!= null) &&
				(theBase.theHistory.hasPrevious(theObj)));
	}

	/**
	 * Restore values from a further history item beyond the current cursor 
	 */
	public void				peekFurther()    {
		historyCtl.histElement myElement;
		myElement = theBase.theHistory.peekFurther();
		if (myElement != null) {
			pushHistory();
			theObj.copyFrom(myElement.theObj);
			setState(DataState.CHANGED);
		}
	}
	
	/**
	 * Restore values from a previous history item beyond the current cursor 
	 */
	public void				peekPrevious()    {
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
			if (!hasHistory()) setState(DataState.CLEAN);				
		}
	}
	
	/**
	 * Determine whether the item has Errors
	 * @return <code>true/false</code>
	 */
	public	  boolean		hasErrors()   { return (theEdit == EditState.ERROR); }

	/**
	 * Determine whether the item has Changes
	 * @return <code>true/false</code>
	 */
	public	  boolean		hasChanges()  { return (theEdit != EditState.CLEAN); }

	/**
	 * Determine whether the item is Valid
	 * @return <code>true/false</code>
	 */
	public	  boolean		isValid()     { return ((theEdit == EditState.CLEAN) ||
			                                        (theEdit == EditState.VALID)); }

	/**
	 * Determine whether a particular field has Errors
	 * @param iField the particular field
	 * @return <code>true/false</code>
	 */
	public	  boolean		hasErrors(int iField) { 
		return theErrors.hasErrors(iField); }

	/**
	 * Note that this item has been validated 
	 */
	public	  void			setValidEdit() {
		switch (theList.getStyle()) {
			case CORE:
				if (theState == DataState.CLEAN) 
					theEdit = EditState.CLEAN;
				else
					theEdit = EditState.DIRTY;
				break;
			default:
				if (isCoreDeleted())
					theEdit = (isDeleted) ? EditState.CLEAN : EditState.VALID;
				else if (isDeleted) 
					theEdit = EditState.VALID;
				else
					theEdit = ((hasHistory()) || (getBase() == null)) 
									? EditState.VALID : EditState.CLEAN;
				break;
		}
	}

	/**
	 * Clear all errors for this item
	 */
	public	  void			clearErrors()  {
		theEdit = EditState.CLEAN;
		theErrors.clearErrors();
	}

	/**
	 * Add an error for this item
	 * @param pError the error text
	 * @param iField the associated field
	 */
	protected void			addError(String pError, int iField) {
		theEdit = EditState.ERROR;
		theErrors.addError(pError, iField);	}

	/**
	 * Get the error text for a field
	 * @param iField the associated field
	 * @return the error text
	 */
	public 	  String		getFieldErrors(int iField) {
		return theErrors.getFieldErrors(iField);	}

	/**
	 * Get the error text for a set of fields
	 * @param iFields the set of fields
	 * @return the error text
	 */
	public 	  String		getFieldErrors(int[] iFields) {
		return theErrors.getFieldErrors(iFields);	}
	
	/**
	 * Get the first error element for an item
	 * @return the first error (or <code>null</code>)
	 */
	public validationCtl.errorElement getFirstError() {
		return theErrors.getFirst();	}

	/**
	 * Construct a new item
	 * @param pCtl the list that this item is associated with
	 * @param uId the Id of the new item (or 0 if not yet known)
	 */
	@SuppressWarnings("unchecked")
	public DataItem(DataList<? extends DataItem> pList, int uId) {
		theId      = uId;
		theList    = (DataList<DataItem>)pList;
		theHistory = new historyCtl();
		theErrors  = new validationCtl();
	}
	
	/**
	 *  Get the state of the underlying record
	 *  @return the underlying state
	 */
	protected DataState getBaseState() {
		return (theBase == null) ? DataState.NOSTATE : theBase.getState();
	}

	/**
	 * Determine index of element within the list
	 * @return The index
	 */
	public int indexOf() {
		/* Return index */
		return theList.indexOf(this);
	}
	
	/**
	 *  Apply changes to the item from a changed version
	 *  Overwritten by objects that have changes
	 *  @param pElement the changed element
	 *  @return were changes made
	 */
	public boolean applyChanges(DataItem pElement) { return false; };
	
	/**
	 *  Validate the element
	 *  Overwritten by objects that have changes
	 */
	public void validate() {};
	
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
	public void setState(DataState newState) {
		/* If this is a Spot list */
		if (theList.getStyle() == ListStyle.SPOT) {
			/* Handle as special case */
			switch (newState) {
				case CLEAN:
					theState  = newState;
					theEdit   = EditState.CLEAN;
					break;
				case CHANGED:
					theState  = newState;
					theEdit   = EditState.DIRTY;
					break;
			}
			/* Return having completed processing */
			return;
		}
		
		/* Police the action */
		switch (newState) {
			case NEW:
				theState  = newState;
				setDeleted(false);
				theEdit = EditState.DIRTY;
				break;
			case CLEAN:
				theState  = newState;
				theEdit   = EditState.CLEAN;
				switch (getBaseState()) {
					case NOSTATE:
						if (theList.getStyle() == ListStyle.EDIT) {
							theState  = DataState.NEW;
							theEdit   = EditState.DIRTY;
						}
						setDeleted(false);
						break;
					case DELETED:
					case DELNEW:
					case DELCHG:
						setDeleted(true);
						break;
					default:
						setDeleted(false);
						break;
				}
				break;
			case RECOVERED:
				theEdit = EditState.DIRTY;
				setDeleted(false);
				switch (theState) {
					case DELETED:
						theState = DataState.CLEAN;
						break;
					case DELNEW:
						theState = DataState.NEW;
						break;
					case DELCHG:
						theState = DataState.CHANGED;
						break;
					case CLEAN:
						theState = newState;
						break;
				}
				break;
			case DELCHG:
			case DELNEW:
				theState  = DataState.DELETED;
				setDeleted(true);
				setValidEdit();
				break;
			case CHANGED:
				theList.setEditState(EditState.DIRTY);
				theEdit = EditState.DIRTY;
				setDeleted(false);
				switch (theState) {
					case NEW:
					case DELNEW:
						theState = DataState.NEW;
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
				setDeleted(true);
				setValidEdit();
				switch (theState) {
					case NEW:
						theState  = DataState.DELNEW;
						break;
					case CHANGED:
						theState  = DataState.DELCHG;
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
	
	/**
	 * Provides the implementation of a history buffer for a DataItem
	 * Each element represents a changed set of values and refers to a {@link finLink.histObject} object
	 * which is the set of changeable values for the object. 
	 * @see historyCtl.histElement
	 */
	private class historyCtl {
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
		 *  Get original values
		 *  @return original values
		 */
		private histObject getBase() {
			return (theBase != null) ? theBase.theObj : getObj();
		}
		
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
		private boolean hasValidHistory(tableHistory pTable) {
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
		private boolean hasValidFurther(histObject pObj, tableHistory pTable) {
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
			theCursor = null;
		}
		
		/**
		 *  Reset history
		 */
		private histObject resetHistory() {
			histObject myLast = null;
			
			/* Remove all history */
			while (theTop != null) myLast = popTheHistory();
			theCursor = null;
			return myLast;
		}
		
		/**
		 *  Set history explicitly
		 *  @param pObj the historic values
		 */
		private void setHistory(histObject pObj) {
			/* Create a new history element */
			histElement myEl = new histElement(pObj);
			
			/* Add to the end of the list */
			myEl.thePrev = theBase;
			if (theBase != null) theBase.theNext = myEl;
			if (theTop == null)  theTop = myEl; 
			theBase      = myEl;
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
				myObj = (thePrev != null) ? thePrev.theObj : getObj();
				
				/* Loop through the fields */
				for (fieldId = 0, myCount = 0;
					 fieldId < numFields();
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
					 fieldId < numFields();
					 fieldId++) {
					/* If the field has changed */
					if (theObj.fieldChanged(fieldId, myObj)) {
						/* Format the field */
						myString.append("<tr><td>"); 
						myString.append(fieldName(fieldId)); 
						myString.append("</td><td>"); 
						myString.append(formatField(fieldId, theObj));
						myString.append("</td></tr>");
					}
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
	public class validationCtl {
		/**
		 * The first error in the list
		 */
		private errorElement theTop  = null;

		/**
		 * The last error in the list
		 */
		private errorElement theEnd  = null;
		
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
			theList.setEditState(EditState.ERROR);
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
		private String getFieldErrors(int iField) {
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
		private	String	getFieldErrors(int[] iFields) {
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
				myString.append(fieldName(theField));
				myString.append("</td><td>");
				myString.append(theError);
				myString.append("</td</tr>");
				
				/* Return the string */
				return myString;
			}
		}
	}	
}

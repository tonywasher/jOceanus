/*******************************************************************************
 * Copyright 2012 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package uk.co.tolcroft.models.data;

import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.help.DebugDetail;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugObject;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;
import uk.co.tolcroft.models.Difference;
import uk.co.tolcroft.models.LinkObject;
import uk.co.tolcroft.models.ReportItem;

/**
 * Provides the abstract DataItem class as the basis for data items. The implementation of the 
 * {@link LinkObject} interface means that this object can only be held in one list at a time and is unique within that list
 * @see uk.co.tolcroft.models.data.DataList
 */
public abstract class DataItem<T extends DataItem<T>> 
								extends ReportItem<T> 
								implements DebugObject {
	/**
	 * The list to which this item belongs
	 */
	private DataList<?,T>			theList		= null;
	
	/**
	 * Self reference (built as cast during constructor)
	 */
	private T						theItem;
	
	/**
	 * The Change state of this item {@link DataState}
	 */
    private DataState				theState	= DataState.NOSTATE;
    
	/**
	 * The Edit state of this item {@link EditState}
	 */
    private EditState           	theEdit		= EditState.CLEAN;

    /**
	 * Is the item visible to standard searches
	 */
    private boolean             	isDeleted	= false;

    /**
	 * Is the item in the process of being changed
	 */
    private boolean             	isChangeing	= false;

    /**
	 * Is the item in the process of being restored
	 */
    private boolean             	isRestoring	= false;

    /**
	 * The id number of the item
	 */
	private int 	            	theId 	   	= 0;

	/**
	 * The history control {@link historyCtl}
	 */
	private HistoryControl<T>      	theHistory 	= null;

	/**
	 * The validation control {@link validationCtl}
	 */
	private ValidationControl<T>	theErrors  	= null;
	
	/**
	 * Is the item active 
	 */
	private	boolean					isActive	= false;
		
	@Override
	public DataList<?,T>   			getList()  		{ return theList; }
	
	/**
	 * Get the list control for this item
	 * @return the list control
	 */
	public ListStyle   				getStyle()  	{ return theList.getStyle(); }
	
	/**
	 * Get the changeable values object for this item 
	 * @return the object
	 */
	public HistoryValues<T>			getCurrentValues()		{ return theHistory.getCurrentValues(); }	

	/**
	 * Get the Id for this item
	 * @return the Id
	 */
	public int						getId()     	{ return theId; }

	/**
	 * Get the EditState for this item
	 * @return the EditState
	 */
	public EditState				getEditState()	{ return theEdit; }

	/**
	 * Get the State for this item
	 * @return the State
	 */
	public DataState				getState()     	{ return theState; }
	
	@Override
	public DataItem<?>				getBase()      	{ return theHistory.getBase(); }

	/**
	 * Get the Generation
	 * @return the Generation
	 */
	public int		getGeneration()	{ return theList.getGeneration(); }

	/**
	 * Get the base item for this item
	 * @return the Base item or <code>null</code>
	 */
	protected HistoryControl<T>		getHistory()	{ return theHistory; }

	/**
	 * Determine whether the item is visible to standard searches
	 * @param isDeleted <code>true/false</code>
	 */
	private void					setDeleted(boolean bDeleted) {
		isDeleted = bDeleted;
		theList.setHidden(theItem, isDeleted);
	}

	/**
	 * Determine whether the item is in the process of being changed
	 * @param isChangeing <code>true/false</code>
	 */
	protected void					setChangeing(boolean bChangeing) {
		isChangeing = bChangeing;
	}

	/**
	 * Determine whether the item is in the process of being restored
	 * @param isRestoring <code>true/false</code>
	 */
	protected void					setRestoring(boolean bRestoring) {
		isRestoring = bRestoring;
	}

	/**
	 * Set the Data State
	 * @param pState the Data Status
	 */
	protected void					setDataState(DataState pState) {
		theState = pState;
	}

	/**
	 * Set the Edit State
	 * @param pState the Edit Status
	 */
	protected void					setEditState(EditState pState) {
		theEdit = pState;
	}

	/**
	 * Set the item as hidden to standard searches
	 */
	public void						setHidden()    	{ setDeleted(true); }

	/**
	 * Determine whether the item is visible to standard searches
	 * @return <code>true/false</code>
	 */
	public boolean					isDeleted()    	{ return isDeleted; }

	/**
	 * Determine whether the item is in the process of being changed
	 * @return <code>true/false</code>
	 */
	protected boolean				isChangeing()   { return isChangeing; }

	/**
	 * Determine whether the item is in the process of being restored
	 * @return <code>true/false</code>
	 */
	protected boolean				isRestoring()   { return isRestoring; }

	@Override
	public boolean					isHidden()    	{ return isDeleted; }

	/**
	 * Determine whether the underlying base item is deleted
	 * @return <code>true/false</code>
	 */
	public boolean					isCoreDeleted() {
		DataItem<?> myBase = getBase();
		return (myBase != null) && (myBase.isDeleted);
	}
	
	/**
	 * Set the id of the item
	 * @param id of the item
	 */
	public void						setId(int id) 	{ theId = id; }
	
	@Override
	public void						setBase(DataItem<?> pBase) { theHistory.setBase(pBase); }
	
	/**
	 * Determine whether the item is locked (overridden if required( 
	 * @return <code>true/false</code>
	 */
	public boolean					isLocked() 	  	{ return false; }

	/**
	 * Determine whether the list is locked (overridden if required( 
	 * @return <code>true/false</code>
	 */
	public boolean					isListLocked() 	{ return false; }

	/**
	 * De-Register any infoSet links 
	 */
	public void						deRegister() 	{ }

	/**
	 * Is the Item Active
	 * @return <code>true/false</code>
	 */
	public		boolean		isActive() { return isActive; }
	
	/**
	 * Clear the Item Active flag
	 */
	protected	void		clearActive() { isActive = false; }
	
	/**
	 * Touch the item
	 * @param pObject object that references the item
	 */
	public		void		touchItem(DataItem<?> pObject) { isActive = true; }
	
	/**
	 * Obtain properly cast reference to self
	 * @return self reference
	 */
	public T						getItem()		{ return theItem; }
	
	/**
	 * Determine the field name for a particular field
	 * This method is the underlying method called when the id is unknown 
	 * @return the field name
	 */
	public static String			fieldName(int fieldId)	{
		switch (fieldId) {
			case FIELD_ID:		return NAME_ID;
			case FIELD_ACTIVE:	return "IsActive";
			default: 			return "Unknown";
		}
	}
	
	/**
	 * Format the value of a particular field as a table row
	 * @param pDetail the debug detail
	 * @param iField the field number
	 * @param pValues the values to use
	 * @return the formatted field
	 */
	public String 					formatField(DebugDetail 		pDetail, 
												int 				iField, 
												HistoryValues<T> 	pValues) {
		String 	myString = "";
		switch (iField) {
			case FIELD_ID: 		
				myString += getId(); 
				break;
			case FIELD_ACTIVE: 		
				myString += isActive() ? true : false; 
				break;
		}
		return myString;
	}
							
	@Override
	public abstract String			getFieldName(int fieldId);
	
	/**
	 * Stub for extensions to add their own fields
	 * @param pDetail the debug detail
	 * @param pBuffer the string buffer 
	 */
	@Override
	public void 					addHTMLFields(DebugDetail pDetail, StringBuilder pBuffer) {}

	/**
	 * Id for standard field
	 */
	public static final int 	FIELD_ID		= 0;
	public static final int 	FIELD_ACTIVE	= 1;
	public static final int 	NUMFIELDS		= FIELD_ACTIVE+1; 
	public static final String	NAME_ID			= "Id";
	
	@Override
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
		
		/* Start the status section */
		myString.append("<tr><th rowspan=\"");
		myString.append((isDeleted) ? 6 : 5);
		myString.append("\">Status</th></tr>");
		
		/* Format the State and edit State */
		myString.append("<tr><td>Generation</td><td>");
		myString.append(getGeneration());
		myString.append("</td></tr>");
		myString.append("<tr><td>Style</td><td>");
		myString.append(getStyle());
		myString.append("</td></tr>");
		myString.append("<tr><td>State</td><td>");
		myString.append(theState);
		myString.append("</td></tr>");
		myString.append("<tr><td>EditState</td><td>");
		myString.append(theEdit);
		myString.append("</td></tr>");
		if (isDeleted) 
			myString.append("<tr><td>Deleted</td><td>true</td></tr>");
		
		/* Add Link Debug */
		addLinkDebug(myString);
		
		/* Start the values section */
		myString.append("<tr><th rowspan=\"");
		myString.append(iNumFields+2);
		myString.append("\">Values</th></tr>");
		myString.append("<td>DerivedState</td><td>"); 
		myString.append(theHistory.determineState());
		myString.append("</td></tr>");
		
		/* Loop through the fields */
		HistoryValues<T> myValues = theHistory.getCurrentValues();
		for (iField = 0;
			 iField < iNumFields;
			 iField++) {
			//if (iField != 0)
			myString.append("<tr>"); 
			myString.append("<td>"); 
			myString.append(getFieldName(iField)); 
			myString.append("</td><td>"); 
			myString.append(formatField(pDetail, iField, myValues));
			myString.append("</td></tr>");
		}

		/* Add any additional HTML Fields */
		addHTMLFields(pDetail, myString);
		
		/* If errors exist */
		if (hasErrors()) {
			/* Add details of the errors */
			myString.append(theErrors.buildDebugDetail());
		}
		
		/* If changes exist */
		if (hasHistory()) {
			/* Add details of the errors */
			myString.append(theHistory.buildDebugDetail(pDetail));
		}
		
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
	
	@Override
	public void addChildEntries(DebugManager 	pManager,
								DebugEntry		pParent) { }	

	@Override
	abstract public String 			itemType();
	
	@Override
	public int						numFields() { return 0; }
	
	/**
	 * Unlink the item from the list
	 */
	public void						unLink()     { theList.remove(this); }
	
	/**
	 * Determine whether the item has changes
	 * @return <code>true/false</code>
	 */
	public boolean					hasHistory()    { return (theHistory != null) &&
															 (theHistory.hasHistory()); }
	
	/**
	 * Clear the history for the item (leaving current values) 
	 */
	public void						clearHistory()  { theHistory.clearHistory(); }
	
	/**
	 * Reset the history for the item (restoring original values)
	 */
	public 	  void					resetHistory()  { theHistory.resetHistory(); }
	
	/**
	 * Set Change history for an update list so that the first and only entry in the change
	 * list is the original values of the base
	 */
	public 	  void					setHistory()	{ theHistory.setHistory(); }
	
	/**
	 * Return the base history object
	 * @return the original values for this object
	 */
	public 	  HistoryValues<T>		getOriginalValues()  { return theHistory.getOriginalValues(); }
	
	/**
	 * Check to see whether any changes were made. 
	 * If no changes were made remove last saved history since it is not needed
	 * @return <code>true</code> if changes were made, <code>false</code> otherwise
	 */
	public boolean					checkForHistory() { return theHistory.maybePopHistory(false);	}
	
	/**
	 * Push current values into history buffer ready for changes to be made
	 */
	public void						pushHistory() {	theHistory.pushHistory(); }
	
	/**
	 * Remove the last changes for the history buffer and restore values from it
	 */
	public void						popHistory() { theHistory.popTheHistory();	}
	
	/**
	 * Determine whether a particular field has changed in this edit view
	 * @param fieldNo the field to test
	 * @return <code>true/false</code>
	 */
	public Difference				fieldChanged(int fieldNo) {	return theHistory.fieldChanged(fieldNo); }
	
	/**
	 * Determine whether there is restore-able history for an item 
	 * @param pTable the table to which data would be restored
	 * @return <code>true/false</code>
	 */
	protected boolean				hasValidHistory(HistoryCheck<T> pTable)    { return theHistory.hasValidHistory(pTable); }

	/**
	 * Determine whether there is further history on a CORE list item to peek 
	 * @param pTable the table to which data would be restored
	 * @return <code>true/false</code>
	 */
	public boolean					hasFurther(HistoryCheck<T> pTable)    { return theHistory.hasFurther(pTable); }

	/**
	 * Determine whether there is previous history on a CORE list item to peek 
	 * @return <code>true/false</code>
	 */
	public boolean					hasPrevious()   { return theHistory.hasPrevious(); }

	/**
	 * Restore values from a further history item beyond the current cursor 
	 */
	public void						peekFurther()   { theHistory.peekFurther(); }
	
	/**
	 * Restore values from a previous history item beyond the current cursor 
	 */
	public void						peekPrevious()	{ theHistory.peekPrevious(); }
	
	/**
	 * Determine whether the item has Errors
	 * @return <code>true/false</code>
	 */
	public	  boolean				hasErrors()   { return (theEdit == EditState.ERROR); }

	/**
	 * Determine whether the item has Changes
	 * @return <code>true/false</code>
	 */
	public	  boolean				hasChanges()  { return (theEdit != EditState.CLEAN); }

	/**
	 * Determine whether the item is Valid
	 * @return <code>true/false</code>
	 */
	public	  boolean				isValid()     { return ((theEdit == EditState.CLEAN) ||
			                                        		(theEdit == EditState.VALID)); }

	/**
	 * Determine whether a particular field has Errors
	 * @param iField the particular field
	 * @return <code>true/false</code>
	 */
	public	  boolean				hasErrors(int iField) {	return theErrors.hasErrors(iField); }

	/**
	 * Note that this item has been validated 
	 */
	public	  void					setValidEdit() {
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
	public	  void					clearErrors()  {
		theEdit = EditState.CLEAN;
		theErrors.clearErrors();
	}

	/**
	 * Add an error for this item
	 * @param pError the error text
	 * @param iField the associated field
	 */
	protected void					addError(String pError, int iField) {
		theEdit = EditState.ERROR;
		theErrors.addError(pError, iField);	}

	/**
	 * Get the error text for a field
	 * @param iField the associated field
	 * @return the error text
	 */
	public 	  String				getFieldErrors(int iField) {
		return theErrors.getFieldErrors(iField);	}

	/**
	 * Get the error text for a set of fields
	 * @param iFields the set of fields
	 * @return the error text
	 */
	public 	  String				getFieldErrors(int[] iFields) {
		return theErrors.getFieldErrors(iFields);	}
	
	/**
	 * Get the first error element for an item
	 * @return the first error (or <code>null</code>)
	 */
	public ValidationControl<T>.errorElement getFirstError() {
		return theErrors.getFirst();	}

	/**
	 * Determine whether two DataItem objects differ.
	 * @param pCurr The current data 
	 * @param pNew The new Data
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static Difference differs(DataItem<?> pCurr, DataItem<?> pNew) {
		/* Handle case where current value is null */
		if  (pCurr == null) return (pNew != null) ? Difference.Different 
												  : Difference.Identical;
		
		/* Handle case where new value is null */
		if  (pNew == null) return Difference.Different;
		
		/* Handle case where class differs */
		if  (pCurr.getClass() != pNew.getClass()) return Difference.Different;
		
		/* Compare the two objects */
		return pCurr.differences(pNew);
	}

	/**
	 * Determine whether two DataItem objects differ. 
	 * @param pNew The new Data
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	private Difference differences(DataItem<?> pNew) {
		/* Access the Number of fields */
		int			iNumFields 	= numFields();
		Difference 	myDiff		= Difference.Identical;
		T			myThat		= getList().getBaseClass().cast(pNew);
		
		/* Access the Values */
		HistoryValues<T> myCurr = getCurrentValues();
		HistoryValues<T> myNew	= myThat.getCurrentValues();
		
		/* Loop through the fields */
		for (int i=0; i<iNumFields; i++) {
			/* Check for differences */
			Difference myTest = myCurr.fieldChanged(i, myNew);
			myDiff = myDiff.combine(myTest);
		}
		
		/* Return difference */
		return myDiff;
	}

	/**
	 * Build History
	 */
	protected void buildHistory() {
		/* Create history and validation control */ 
		theErrors  = new ValidationControl<T>(theItem);
		
		/* Build an initial set of values */
		theHistory.setValues(getNewValues());
	}
	
	/**
	 * Get an initial set of values 
	 * @return an initial set of values 
	 */
	protected HistoryValues<T> getNewValues() { return null; }
	
	/**
	 * Copy flags 
	 * @param pItem the original item 
	 */
	protected void copyFlags(T pItem) { isActive = pItem.isActive(); }
	
	/**
	 * Construct a new item
	 * @param pList the list that this item is associated with
	 * @param uId the Id of the new item (or 0 if not yet known)
	 */
	public DataItem(DataList<?,T> pList, int uId) {
		super(pList);
		theId      = uId;
		theList    = pList;
		theItem	   = pList.getBaseClass().cast(this);
		theHistory = new HistoryControl<T>(theItem);
		buildHistory();
	}
	
	/**
	 *  Get the state of the underlying record
	 *  @return the underlying state
	 */
	protected DataState getBaseState() {
		DataItem<?> myBase = getBase();
		return (myBase == null) ? DataState.NOSTATE : myBase.getState();
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
	public boolean applyChanges(DataItem<?> pElement) { return false; };
	
	/**
	 *  Validate the element
	 *  Dirty items become valid
	 */
	public void validate() { 
		if (getEditState() == EditState.DIRTY)
			setEditState(EditState.VALID);
	}
	
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
	 */
	
	/**
	 * Set the state of the item
	 * @param newState the new state to set
	 */
	public void setState(DataState newState) {
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
						if (getStyle() == ListStyle.EDIT) {
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
}

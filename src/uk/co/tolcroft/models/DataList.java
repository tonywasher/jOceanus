package uk.co.tolcroft.models;

/**
 * Generic implementation of a DataList for DataItems Stack
 * @author Tony Washer
 */
public abstract class DataList<T extends DataItem> extends SortedList<T>{
	/**
	 * The style of the list
	 */
	private ListStyle 		theStyle    = ListStyle.CORE;

	/**
	 * The edit state of the list
	 */
	private EditState 		theEdit	  	= EditState.CLEAN;
		
	/**
	 * The edit state of the list
	 */
	private IdManager<T>	theMgr	  	= null;
		
	/**
	 * Get the style of the list
	 * @return the list style
	 */
	public ListStyle		getStyle()	{ return theStyle; }

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
	public 	  boolean        getShowDeleted() { return !getSkipHidden(); }
		
	/**
	 * Set whether we should count deleted items as present
	 * @param bShow <code>true/false</code>
	 */
	public	  void           setShowDeleted(boolean bShow) { setSkipHidden(!bShow); }

	/**
	 * Construct a new object
	 * @param pStyle the new {@link finLink.itemCtl.ListStyle}
	 * @param fromStart - should inserts be attempted from start/end of list
	 */
	protected DataList(ListStyle pStyle,
			           boolean   fromStart) {
		super(fromStart);
		theStyle = pStyle;
		theMgr	 = new IdManager<T>();
	}
		
	/**
	 * Construct an update/edit/core extract of an itemCtl list
	 * @param pList      The list to extract from
	 * @param pStyle	 the Style of the list  
	 */
	protected DataList(DataList<T> pList, ListStyle pStyle) {
		/* Make this list the correct style */
		super(false);
		theStyle = pStyle;
		theMgr	 = new IdManager<T>();
			
		/* Local variables */
		ListIterator 	myIterator;
		DataItem		myCurr;
		DataItem		myItem;
			
		/* Note that this list should show deleted items on UPDATE */
		if (pStyle == ListStyle.UPDATE) setShowDeleted(true);
			
		/* Create an iterator for all items in the source list */
		myIterator = pList.listIterator(true);
		
		/* Loop through the list */
		while ((myCurr = myIterator.next()) != null)  { 
			/* If this item is not CLEAN or this is not an update extract */
			if ((pStyle != ListStyle.UPDATE) ||
			    (myCurr.getState() != DataState.CLEAN)) {
				/* Copy the item */
				myItem = addNewItem(myCurr);
					
				/* If the item is a changed object */
				if ((pStyle == ListStyle.UPDATE) &&
					(myItem.getState() == DataState.CHANGED)) {
					/* Ensure that we record the correct history */
					if (myItem.getObj() != null) myItem.setHistory();
				}
			}
		}
	}
	
	/**
	 * Construct a difference extract between two DataLists.
	 * The difference extract will only have items that differ between the two lists.
	 * Items that are in the new list, but not in the old list will be viewed as inserted.
	 * Items that are in the old list but not in the new list will be viewed as deleted.
	 * Items that are in both list but differ will be viewed as changed 
	 * 
	 * @param pNew The new list to extract from 
	 * @param pOld The old list to extract from 
	 */
	protected DataList(DataList<T> pNew, DataList<T> pOld) {
		/* Make this list the correct style */
		super(false);
		theStyle = ListStyle.DIFFER;
		theMgr	 = new IdManager<T>();
			
		/* Local variables */
		ListIterator 	myIterator;
		DataItem		myCurr;
		DataItem		myItem;
		DataItem		myNew;
		DataList<T>		myOld;
			
		/* Create a clone of the old list */
		myOld = pOld.cloneIt();
			
		/* Note that this list should show deleted items */
		setShowDeleted(true);
			
		/* Create an iterator for all items in the source new list */
		myIterator = pNew.listIterator(true);
		
		/* Loop through the new list */
		while ((myCurr = myIterator.next()) != null) {
			/* Locate the item in the old list */
			myItem = myOld.searchFor(myCurr.getId());
				
			/* If the item does not exist */
			if (myItem == null) {
				/* Insert a new item */
				myItem = addNewItem(myCurr);
				myItem.setBase(null);
				myItem.setState(DataState.NEW);
			}
				
			/* else the item exists in the old list */
			else { 
				/* If the item has changed */
				if (!myCurr.equals(myItem)) {
					/* Copy the item */
					myNew = addNewItem(myCurr);
					myNew.setBase(myItem);
					myNew.setState(DataState.CHANGED);
					
					/* Ensure that we record the correct history */
					if (myNew.getObj() != null) myNew.setHistory();
				}
					
				/* Unlink the old item to improve search speed */
				myOld.remove(myItem);
			}
		}
	
		/* Create an iterator for all items in the source old list */
		myIterator = myOld.listIterator(true);
	
		/* Loop through the remaining items in the old list */
		while ((myCurr = myIterator.next()) != null) {
			/* Insert a new item */
			myItem = addNewItem(myCurr);
			myItem.setBase(null);
			myItem.setState(DataState.DELETED);
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
	public void reBase(DataList<T> pBase) {
		/* Local variables */
		ListIterator 	myIterator;
		DataItem		myCurr;
		DataItem		myItem;
		DataList<T>		myBase;
			
		/* Create a clone of the base list */
		myBase = pBase.cloneIt();
			
		/* Create an iterator for our new list */
		myIterator = listIterator(true);
		
		/* Loop through this list */
		while ((myCurr = myIterator.next()) != null) {
			/* Locate the item in the base list */
			myItem = myBase.searchFor(myCurr.getId());
				
			/* If the underlying item does not exist */
			if (myItem == null) {
				/* Mark this as a new item */
				myCurr.setBase(null);
				myCurr.setState(myCurr.isDeleted() ? DataState.DELNEW : DataState.NEW);
			}
				
			/* else the item exists in the old list */
			else { 
				/* if it has changed */
				if (!myCurr.equals(myItem)) {					
					/* Mark this as a changed item (go via CLEAN to remove NEW indication) */
					myCurr.setBase(myItem);
					myCurr.setState(DataState.CLEAN);
					myCurr.setState(myCurr.isDeleted() ? DataState.DELCHG : DataState.CHANGED);
					
					/* Set correct history */
					myCurr.setHistory();
					myCurr.setBase(null);
				}
				
				/* else it is identical */
				else {
					/* Mark this as a clean item */
					myCurr.setBase(null);
					myCurr.setState(myCurr.isDeleted() ? DataState.DELETED : DataState.CLEAN);					
				}
				
				/* Unlink the old item to improve search speed */
				myBase.remove(myItem);
			}
		}
			
		/* Create an iterator for the source base list */
		myIterator = myBase.listIterator(true);
	
		/* Loop through the remaining items in the base list */
		while ((myCurr = myIterator.next()) != null) {
			/* Insert a new item */
			myItem = addNewItem(myCurr);
			myItem.setBase(null);
			myItem.setState(DataState.DELETED);
		}
	}
	
	/**
	 *  add an object to the list  
	 *  @param pItem - item to add to the list
	 *  @return <code>true</code>
	 */
	public boolean add(T pItem) {
		/* Add the item to the underlying list */
		boolean bSuccess = super.add(pItem);
		
		/* Declare to the id Manager */
		if (bSuccess) theMgr.setItem(pItem.getId(), pItem);
		
		/* Return to caller */
		return bSuccess;
	}
	
	/**
	 * remove the specified item 
	 * @param o the item to remove
	 * @return <code>true/false</code> was the item removed
	 */
	@SuppressWarnings("unchecked")
	public boolean remove(Object o) {
		/* Remove the underlying item */
		boolean bSuccess = super.remove(o);
				
		/* Access the object */
		T myItem = (T)o;
		
		/* Declare to the id Manager */
		if (bSuccess) theMgr.setItem(myItem.getId(), null);
		
		/* Return to caller */
		return bSuccess;
	}
	
	/**
	 * remove the item at the specified index
	 * @param iIndex index of item
	 * @return the removed item
	 */
	public T remove(int iIndex) {
		/* Remove the underlying item */
		T myItem = super.remove(iIndex);
		
		/* Declare to the id Manager */
		theMgr.setItem(myItem.getId(), null);
		
		/* Return to caller */
		return myItem;
	}
	
	/**
	 * obtain an Iterator for this list
	 * @return <code>true/false</code>
	 */
	public java.util.Iterator<T> iterator() {
		/* Return a new iterator */
		return new ListIterator();
	}
	
	/**
	 * obtain a list Iterator for this list
	 * @return List iterator
	 */
	public ListIterator listIterator() {
		/* Return a new iterator */
		return new ListIterator();
	}
	
	/**
	 * obtain a list Iterator for this list
	 * @param bShowAll show all items in the list
	 * @return List iterator
	 */
	public ListIterator listIterator(boolean bShowAll) {
		/* Return a new iterator */
		return new ListIterator(bShowAll);
	}
	
	/**
	 * obtain a list Iterator for this list
	 * obtain a list Iterator for this list initialised to an index
	 * @param iIndex the index to initialise to
	 * @return List iterator
	 */
	public ListIterator listIterator(int iIndex) {
		/* Throw exception */
		throw new java.lang.UnsupportedOperationException();
	}
	
	/**
	 * remove All list items
	 */
	public void clear() {
		/* Clear the underlying list */
		super.clear();
		
		/* Reset the id manager */
		theMgr.clear();
	}
	
	/**
	 * Is the Id unique in this list
	 * @param uId the Id to check
	 * @return Whether the id is unique <code>true/false</code>
	 */
	public boolean isIdUnique(long uId) {
		/* Ask the Id Manager for the answer */
		return theMgr.isIdUnique(uId);
	}
	
	/**
	 * Generate/Record new id for the item
	 * @param pItem the new item
	 */
	public void setNewId(T pItem) {
		/* Ask the Id Manager to manage the request */
		theMgr.setNewId(pItem);		
	}
	
	/**
	 * Obtain a clone of the list
	 * @return the clone of  the list
	 */
	abstract protected DataList<T> cloneIt();
		
	/**
	 * Obtain the type of the list
	 * @return the type of the list
	 */
	abstract public String itemType();
		
	/**
	 * Provide a string representation of this object
	 * @return formatted string
	 */
	public StringBuilder toHTMLString() {
		/* Local variables */
		ListIterator 	myIterator;
		T 				myCurr;	
		StringBuilder	myString = new StringBuilder(10000);
		boolean			showDeleted;
			
		/* Access showDeleted */
		showDeleted = !getSkipHidden();
			
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
			
		/* Create an iterator for the source base list */
		myIterator = listIterator(true);
		
		/* Loop through the list */
		while ((myCurr = myIterator.next()) != null) {
			/* Format the Item */
			myString.append("<p>");
			myString.append(myCurr.toHTMLString());
		}
			
		/* Return the string */
		return myString;
	}
		
	/**
	 * Set the EditState for the list (forcible on error/change)
	 * @param pState the new {@link EditState} (only ERROR/DIRTY)
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
		boolean			isDirty = false;
		boolean			isError = false;
		boolean			isValid	= false;
		ListIterator 	myIterator;
		T 				myCurr;
			
		/* Create an iterator for the list */
		myIterator = listIterator(true);
			
		/* Loop through the items to find the match */
		while ((myCurr = myIterator.next()) != null) {		
			/* If the item is deleted */
			if (myCurr.isDeleted()) {
				/* If the base element is not deleted we have a valid change */
				if (!myCurr.isCoreDeleted()) isValid = true;
			}
				
			/* Else the item is active */
			else
			{
				switch (myCurr.getEditState()) {
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
	public T searchFor(long uId) {		
		/* Access the item from the IdManager */
		T myItem = theMgr.getItem(uId);
			
		/* Return result */
		return myItem;
	}
		
	/**
	 * Validate the events
	 */
	public void validate() {
		ListIterator 	myIterator;
		T				myCurr;
		
		/* Clear the errors */
		clearErrors();
			
		/* Create an iterator for the list */
		myIterator = listIterator(true);
			
		/* Loop through the items */
		while ((myCurr = myIterator.next()) != null) {
			/* Clear Errors */
			myCurr.clearErrors();
			
			/* Skip deleted items */
			if (myCurr.isDeleted()) continue;
				
			/* Validate the item */
			myCurr.validate();
		}
			
		/* Determine the Edit State */
		findEditState();
	}
		
	/**
	 * Check whether we have updates 
	 * @return <code>true/false</code>
	 */
	public boolean hasUpdates() {
		ListIterator 	myIterator;
		T 				myCurr;
			
		/* Create an iterator for the list */
		myIterator = listIterator(true);
			
		/* Loop through the items */
		while ((myCurr = myIterator.next()) != null) {		
			/* Ignore clean items */
			if (myCurr.getState() == DataState.CLEAN)
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
		ListIterator 	myIterator;
		T 				myCurr;
			
		/* Create an iterator for the list */
		myIterator = listIterator(true);
		
		/* Loop through items clearing validation errors */
		while ((myCurr = myIterator.next()) != null) {
			myCurr.clearErrors();
		}
	}
		
	/**
	 * Create a new element in the core list from an edit session (to be over-written)
	 * @param pElement - element to base new item on
	 */
	public abstract DataItem addNewItem(DataItem pElement);
		
	/**
	 * Create a new empty element in the edit list (to be over-written)
	 * @param isCredit - is the item a credit or debit
	 */
	public abstract void addNewItem(boolean isCredit);
		
	/** 
	 * Apply changes in an edit view back into the core data
	 * @param pChanges - edit view with changes to apply
	 */
	@SuppressWarnings("unchecked")
	public void applyChanges(DataList<?> pChanges) {
		DataList<?>.ListIterator 	myIterator;
		DataItem					myCurr;
		T							myItem;
			
		/* Create an iterator for the changes list */
		myIterator = pChanges.listIterator(true);
			
		/* Loop through the elements */
		while ((myCurr = myIterator.next()) != null) {		
			/* Switch on the state */
			switch (myCurr.getState()) {
				/* Ignore the item if it is clean */
				case CLEAN:
					break;
						
				/* Delete the item from the list if it is a deleted new item */
				case DELNEW:
					myIterator.remove();
					break;
					
				/* If this is a new item, add it to the list */
				case NEW:
					/* Link this item to the new item */
					myCurr.setBase(addNewItem(myCurr));
						
					/* Clear history and set as a clean item */
					myCurr.clearHistory();
					myCurr.setState(DataState.CLEAN);
					break;
						
				/* If this is a deleted or deleted-changed item */
				case DELETED:
				case DELCHG:
					/* Access the underlying item and mark as deleted */
					myItem = (T)myCurr.getBase();					
					myItem.setState(DataState.DELETED);
						
					/* Clear history and set as a clean item */
					myCurr.clearHistory();
					myCurr.setState(DataState.CLEAN);
					break;
						
				/* If this is a recovered item */
				case RECOVERED:
					/* Access the underlying item and mark as restored */
					myItem = (T)myCurr.getBase();					
					myItem.setState(DataState.RECOVERED);
						
					/* Clear history and set as a clean item */
					myCurr.clearHistory();
					myCurr.setState(DataState.CLEAN);
					break;
						
				/* If this is a changed item */
				case CHANGED:
					/* Access underlying item, apply changes and mark as changed */
					myItem = (T)myCurr.getBase();					
					myItem.applyChanges(myCurr);
					myItem.setState(DataState.CHANGED);
						
					/* Re-sort the item */
					reSort(myItem);
						
					/* Clear history and set as a clean item */
					myCurr.clearHistory();
					myCurr.setState(DataState.CLEAN);
					break;
			}
		}
	}
		
	/** 
	 * Reset changes in an edit view
	 */
	public void resetChanges() {
		ListIterator 	myIterator;
		T 				myCurr;
			
		/* Create an iterator for the list */
		myIterator = listIterator(true);
			
		/* Loop through the elements */
		while ((myCurr = myIterator.next()) != null) {		
			/* Switch on the state */
			switch (myCurr.getState()) {
				/* Delete the item if it is new or a deleted new item */
				case NEW:
				case DELNEW:
					myIterator.remove();
					break;
						
				/* If this is a clean item, just ignore */
				case CLEAN:
					break;
						
				/* If this is a deleted or recovered item */
				case DELETED:
				case RECOVERED:				
					/* Mark the item as clean */
					myCurr.setState(DataState.CLEAN);
					break;
					
				/* If this is a changed or DELCHG item */
				case CHANGED:
				case DELCHG:
					/* Clear changes and mark as clean */
					myCurr.resetHistory();
					myCurr.setState(DataState.CLEAN);
					break;
			}
		}
	}
		
	/**
	 * ListIterator class for this list
	 */
	public class ListIterator extends SortedList<T>.ListIterator {
		/**
		 * Constructor for standard iterator 
		 */
		private ListIterator() { this(false); }
		
		/**
		 * Constructor for iterator that can show all elements 
		 * @param bShowAll show all items in the list
		 */
		private ListIterator(boolean bShowAll) { super(bShowAll); }		

		/**
		 * Remove the last referenced item.
		 */
		public void remove() {
			/* Remove the last Item */
			T myItem = super.removeLastItem();

			/* Declare to the id Manager */
			theMgr.setItem(myItem.getId(), null);
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

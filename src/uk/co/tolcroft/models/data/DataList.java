package uk.co.tolcroft.models.data;

import uk.co.tolcroft.models.SortedList;
import uk.co.tolcroft.models.help.DebugDetail;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugObject;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;

/**
 * Generic implementation of a DataList for DataItems Stack
 * @author Tony Washer
 */
public abstract class DataList<L extends DataList<L,T>,
							   T extends DataItem<T>> 	extends SortedList<T> 
												   		implements DebugObject {
	/**
	 * The style of the list
	 */
	private ListStyle 		theStyle    	= ListStyle.CORE;

	/**
	 * The edit state of the list
	 */
	private EditState 		theEdit	  		= EditState.CLEAN;
		
	/**
	 * The id manager 
	 */
	private	IdManager<T>	theMgr	  		= null;
		
	/**
	 * The class 
	 */
	private	Class<L>		theClass  		= null;
		
	/**
	 * The class 
	 */
	private	L				theList  		= null;
		
	/**
	 * The base list (for extracts) 
	 */
	private	DataList<?,?>	theBase	  		= null;
		
	/**
	 * The generation 
	 */
	private	int				theGeneration  	= 0;
		
	/**
	 * Get the style of the list
	 * @return the list style
	 */
	public 	ListStyle		getStyle()	{ return theStyle; }

	/**
	 * Get the style of the list
	 * @return the list style
	 */
	protected 	void		setStyle(ListStyle pStyle)	{ theStyle = pStyle; }

	/**
	 * Get the EditState of the list
	 * @return the Edit State
	 */
	public  EditState		getEditState() { return theEdit; }

	/**
	 * Get the Generation of the list
	 * @return the Generation
	 */
	public  int				getGeneration() { return theGeneration; }

	/**
	 * Get the Generation of the list
	 * @return the Generation
	 */
	protected void			setGeneration(int pGeneration) { theGeneration = pGeneration; }

	/**
	 * Determine whether the list got any errors
	 * @return <code>true/false</code>
	 */
	public	boolean 		hasErrors()   { return (theEdit == EditState.ERROR); }

	/**
	 * Determine whether the list got any changes
	 * @return <code>true/false</code>
	 */
	public	boolean   		hasChanges()  { return (theEdit != EditState.CLEAN); }

	/**
	 * Determine whether the list is valid (or are there errors/non-validated changes)
	 * @return <code>true/false</code>
	 */
	public	boolean			isValid()     { return ((theEdit == EditState.CLEAN) ||
			                                        (theEdit == EditState.VALID)); }

	/**
	 * Determine whether the list is Locked (overwritten as required)
	 * @return <code>true/false</code>
	 */
	public	boolean			isLocked()    { return false; }

	/**
	 * Determine whether we should count deleted items as present
	 * @return <code>true/false</code>
	 */
	public	boolean 		getShowDeleted() { return !getSkipHidden(); }
		
	/**
	 * Set whether we should count deleted items as present
	 * @param bShow <code>true/false</code>
	 */
	public	void			setShowDeleted(boolean bShow) { setSkipHidden(!bShow); }

	/**
	 * Set the base DataList
	 * @param pBase the list that this list is based upon
	 */
	protected	void		setBase(DataList<?,?> pBase) { theBase = pBase; }

	/**
	 * Get List
	 * @return the List
	 */
	public		L			getList() 	{ return theList; }
	
	/**
	 * Get Max Id
	 * @return the Maximum Id
	 */
	public		int			getMaxId() 	{ return theMgr.getMaxId(); }
	
	/**
	 * Set Max Id
	 * @param uMaxId the Maximum Id
	 */
	public		void		setMaxId(int uMaxId) { theMgr.setMaxId(uMaxId); }
	
	/**
	 * Construct a new object
	 * @param pClass the class
	 * @param pBaseClass the class of the underlying object
	 * @param pStyle the new {@link ListStyle}
	 * @param fromStart - should inserts be attempted from start/end of list
	 */
	protected DataList(Class<L>		pClass,
					   Class<T>	 	pBaseClass,
					   ListStyle 	pStyle,
			           boolean   	fromStart) {
		super(pBaseClass, fromStart);
		theClass = pClass;
		theList	 = pClass.cast(this);
		theStyle = pStyle;
		theMgr	 = new IdManager<T>();
	}
		
	/**
	 * Construct a clone object
	 * @param pSource the list to clone
	 */
	protected DataList(L pSource) {
		super(pSource.getBaseClass(), false);
		theStyle 		= ListStyle.VIEW;
		theClass 		= pSource.theClass;
		theList	 		= theClass.cast(this);
		theMgr	 		= new IdManager<T>();
		theBase  		= pSource;
		theGeneration 	= pSource.getGeneration();
	}
		
	/**
	 * Construct an update extract for a DataList.
	 * @return the update extract (or null if not core data list) 
	 */
	abstract protected L getUpdateList();
	
	/**
	 * Construct an edit extract for a DataList.
	 * @return the edit extract (or null if not edit-able list) 
	 */
	abstract public L getEditList();
	
	/**
	 * Obtain a clone of the list
	 * @return the clone of  the list
	 */
	abstract protected L getDeepCopy(DataSet<?> pDataSet);
		
	/**
	 * Obtain a copy of the list
	 * @return the copy of  the list
	 */
	abstract protected L getShallowCopy();
		
	/**
	 * Construct an difference extract for a DataList.
	 * @return the difference extract (or null if not differ-able list) 
	 */
	abstract protected L getDifferences(L pOld);
	
	/**
	 * Populate an Extract List
	 * @param pStyle the Style of the extract 
	 */
	protected void populateList(ListStyle pStyle) {
		/* Make this list the correct style */
		theStyle = pStyle;
			
		/* Local variables */
		DataList<?,?>.ListIterator 	myIterator;
		DataItem<?>					myCurr;
		DataItem<T>					myItem;
			
		/* Note that this list should show deleted items on UPDATE */
		if (pStyle == ListStyle.UPDATE) setShowDeleted(true);
			
		/* Create an iterator for all items in the source list */
		myIterator = theBase.listIterator(true);
		
		/* Loop through the list */
		while ((myCurr = myIterator.next()) != null)  { 
			/* Ignore this item for UPDATE lists if it is clean */
			if ((pStyle == ListStyle.UPDATE) &&
			    (myCurr.getState() == DataState.CLEAN))
				continue;
			
			/* Copy the item */
			myItem = addNewItem(myCurr);
					
			/* If the item is a changed object */
			if ((pStyle == ListStyle.UPDATE) &&
				(myItem.getState() == DataState.CHANGED)) {
				/* Ensure that we record the correct history */
				if (myItem.getCurrentValues() != null)
					myItem.setHistory();
			}
		}
		
		/* For Clone lists remove base reference */
		if (theStyle == ListStyle.CLONE) theBase = null;
	}
	
	/**
	 * Construct a difference extract between two DataLists.
	 * The difference extract will only have items that differ between the two lists.
	 * Items that are in the new list, but not in the old list will be viewed as inserted.
	 * Items that are in the old list but not in the new list will be viewed as deleted.
	 * Items that are in both list but differ will be viewed as changed 
	 * @param pNew The new list to compare 
	 * @param pOld The old list to compare 
	 */
	protected void getDifferenceList(L pNew, L pOld) {
		/* Make this list the correct style */
		theStyle = ListStyle.DIFFER;
			
		/* Local variables */
		ListIterator 	myIterator;
		DataItem<T>		myCurr;
		DataItem<T>		myItem;
		DataItem<T>		myNew;
		L				myOld;
			
		/* Create a shallow copy of the old list */
		myOld = pOld.getShallowCopy();
			
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
					if (myNew.getCurrentValues() != null) myNew.setHistory();
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
	public void reBase(L pBase) {
		/* Local variables */
		ListIterator 	myIterator;
		T				myCurr;
		T				myItem;
		L				myBase;
			
		/* Create a shallow copy of the base list */
		myBase = pBase.getShallowCopy();
			
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
	public boolean remove(Object o) {
		/* Make sure that the object is the same data class */
		if (o.getClass() != getBaseClass()) return false;
		
		/* Remove the underlying item */
		boolean bSuccess = super.remove(o);
				
		/* Access the object */
		DataItem<T> myItem = getBaseClass().cast(o);
		
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
	public boolean isIdUnique(int uId) {
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
	 * Obtain the type of the list
	 * @return the type of the list
	 */
	abstract public String itemType();
	
	/**
	 * Stub for extensions to add their own fields
	 * @param pBuffer the string buffer 
	 */
	public void addHTMLFields(StringBuilder pBuffer) {}
	
	/**
	 * Provide a string representation of this object
	 * @param pDetail the Debug Detail
	 * @return formatted string
	 */
	public StringBuilder buildDebugDetail(DebugDetail pDetail) {
		/* Local variables */
		StringBuilder	myString = new StringBuilder(10000);
		boolean			showDeleted;
			
		/* Access showDeleted */
		showDeleted = !getSkipHidden();
			
		/* Format the table headers */
		myString.append("<table border=\"1\" width=\"90%\" align=\"center\">");
		myString.append("<thead><th>");
		myString.append(itemType());
		myString.append("List</th>");
		myString.append("<th>Property</th><th>Value</th></thead><tbody>");
			
		/* Start the status section */
		myString.append("<tr><th rowspan=\"");
		myString.append((showDeleted) ? 6 : 5);
		myString.append("\">Status</th></tr>");
			
		/* Format the listSize */
		myString.append("<tr><td>Generation</td><td>");
		myString.append(getGeneration());
		myString.append("</td></tr>");
		myString.append("<tr><td>ListSize</td><td>"); 
		myString.append(sizeAll()); 
		myString.append("</td></tr>"); 

		/* Format the listStyle and editState */
		myString.append("<tr><td>ListStyle</td><td>"); 
		myString.append(theStyle); 
		myString.append("</td></tr>"); 
		myString.append("<tr><td>EditState</td><td>"); 
		myString.append(theEdit); 
		myString.append("</td></tr>"); 
		if (showDeleted) myString.append("<tr><td>showDeleted</td><td>true</td></tr>");
		addHTMLFields(myString);
		myString.append("</tbody></table>"); 
			
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

	/**
	 * Set the EditState for the list (forcible on error/change)
	 * @param pState the new {@link EditState} (only ERROR/DIRTY)
	 */
	public	  void           setEditState(EditState pState) {
		switch (pState) {
			case CLEAN: 
			case VALID: 
			case ERROR: 
				theEdit = pState;
				break;
			case DIRTY: 
				if (theEdit != EditState.ERROR)
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
	public T searchFor(int uId) {		
		/* Access the item from the IdManager */
		T myItem = theMgr.getItem(uId);
			
		/* Return result */
		return myItem;
	}
		
	/**
	 * Validate the data items
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
	 *  Reset active
	 */
	public void clearActive() {
		ListIterator 	myIterator;
		T 				myCurr;
			
		/* Create an iterator for the list */
		myIterator = listIterator(true);
		
		/* Loop through items clearing active flag */
		while ((myCurr = myIterator.next()) != null) {
			myCurr.clearActive();
		}
	}
		
	/**
	 * Create a new element in the core list from an edit session (to be over-written)
	 * @param pElement - element to base new item on
	 */
	public abstract T addNewItem(DataItem<?> pElement);
		
	/**
	 * Create a new empty element in the edit list (to be over-written)
	 */
	public abstract T addNewItem();
		
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
						
				/* If this is a changed or DELCHG item */
				case CHANGED:
				case DELCHG:
					/* Clear changes and fall through */
					myCurr.resetHistory();

				/* If this is a deleted or recovered item */
				case DELETED:
				case RECOVERED:				
					/* Clear errors and mark the item as clean */
					myCurr.clearErrors();
					myCurr.setState(DataState.CLEAN);
					break;
			}
		}
	}

	/** 
	 * Prepare changes in an edit view back into the core data
	 * @param pChanges - edit view with changes to apply
	 */
	public void prepareChanges() {
		ListIterator 	myIterator;
		DataItem<T>		myCurr;
		DataItem<?>		myBase;
			
		/* Create an iterator for the changes list */
		myIterator = listIterator(true);
			
		/* Loop through the elements */
		while ((myCurr = myIterator.next()) != null) {		
			/* Switch on the state */
			switch (myCurr.getState()) {
				/* Ignore the item if it is clean or DELNEW */
				case CLEAN:
				case DELNEW:
					break;
					
				/* If this is a new item, add it to the list */
				case NEW:
					/* Link this item to the new item */
					myCurr.setBase(theBase.addNewItem(myCurr));
					break;
						
				/* If this is a deleted or deleted-changed item */
				case DELETED:
				case DELCHG:
					/* Access the underlying item and mark as deleted */
					myBase = myCurr.getBase();					
					myBase.setState(DataState.DELETED);
					break;
						
				/* If this is a recovered item */
				case RECOVERED:
					/* Access the underlying item and mark as restored */
					myBase = myCurr.getBase();					
					myBase.setState(DataState.RECOVERED);
					myBase.setRestoring(true);
					break;
						
				/* If this is a changed item */
				case CHANGED:
					/* Access underlying item */
					myBase = myCurr.getBase();
					
					/* Apply changes and note if history has been applied */
					if (myBase.applyChanges(myCurr))
						myBase.setChangeing(true);
					
					/* Note if we are restoring an item */
					if (myBase.isDeleted())	
						myBase.setRestoring(true);
					
					/* Set new state */
					myBase.setState(DataState.CHANGED);
						
					/* Re-sort the item */
					theBase.reSort(myBase);
					break;
			}
		}
	}
		
	/** 
	 * RollBack changes in an edit view that have been applied to core data
	 */
	public void rollBackChanges() {
		ListIterator 	myIterator;
		DataItem<T>		myCurr;
		DataItem<?>		myBase;
			
		/* Create an iterator for this list */
		myIterator = listIterator(true);
			
		/* Loop through the elements */
		while ((myCurr = myIterator.next()) != null) {		
			/* Switch on the state */
			switch (myCurr.getState()) {
				/* Ignore the item if it is clean or DelNew */
				case CLEAN:
				case DELNEW:
					break;
					
				/* If this is a new item, remove the base item */
				case NEW:
					/* Remove the base item and its reference */
					theBase.remove(myCurr.getBase());	
					myCurr.setBase(null);
					break;
						
				/* If this is a deleted or deleted-changed item */
				case DELETED:
				case DELCHG:
					/* Access the underlying item and mark as not deleted */
					myBase = myCurr.getBase();
					myBase.setState(DataState.RECOVERED);
					break;
						
				/* If this is a recovered item */
				case RECOVERED:
					/* Access the underlying item and mark as deleted */
					myBase = myCurr.getBase();					
					myBase.setState(DataState.DELETED);
					myBase.setRestoring(false);
					break;
						
				/* If this is a changed item */
				case CHANGED:
					/* Access underlying item */
					myBase = myCurr.getBase();
					
					/* If we were changing pop the changes */
					if (myBase.isChangeing())
						myBase.popHistory();
					
					/* If we were restoring */
					if (myBase.isRestoring()) {
						/* Set the item to be deleted again */
						myBase.setState(DataState.DELETED);
						myBase.setRestoring(false);
					}
					
					/* If the item is now clean */
					else if (!myBase.hasHistory()) {
						/* Set the new status */
						myBase.setState(DataState.CLEAN);
					}
						
					/* Re-sort the item */
					theBase.reSort(myBase);
					break;
			}
		}
	}
		
	/** 
	 * Commit changes in an edit view that have been applied to the core data
	 */
	public void commitChanges() {
		ListIterator 	myIterator;
		DataItem<T>		myCurr;
			
		/* Create an iterator for this list */
		myIterator = listIterator(true);
			
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
					
				/* All other states clear history and, convert it to Clean */
				case NEW:
				case DELETED:
				case DELCHG:
				case RECOVERED:
				case CHANGED:
					/* Clear history and set as a clean item */
					myCurr.clearHistory();
					myCurr.setRestoring(false);
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
		 * Deep Copy clone for security updates
		 */
		CLONE,
			
		/**
		 * Shallow Copy list for comparison purposes
		 */
		COPY,
			
		/**
		 * Partial extract of the data for the purposes of editing
		 */
		EDIT,
			
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

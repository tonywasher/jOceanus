package uk.co.tolcroft.models.data;

import java.util.EnumMap;
import java.util.Map;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.data.EncryptedItem.EncryptedList;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugObject;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;
import uk.co.tolcroft.models.security.SecureManager;
import uk.co.tolcroft.models.security.SecurityControl;
import uk.co.tolcroft.models.threads.DataControl;

public abstract class DataSet<T extends Enum<T>> implements DebugObject {
	private SecureManager			theSecurity   	= null;
	private ControlKey.List			theControlKeys  = null;
	private DataKey.List			theDataKeys		= null;
	private ControlData.List		theControlData 	= null;
	private Class<T>				theClass 		= null;

	/**
	 * The DataList Map
	 */
	private Map<T, DataList<?>>		theMap			= null;

    /* Access methods */
	protected SecureManager		getSecurity() 		{ return theSecurity; }
	public ControlKey.List 		getControlKeys() 	{ return theControlKeys; }
	public DataKey.List 		getDataKeys() 		{ return theDataKeys; }
	public ControlData.List 	getControlData() 	{ return theControlData; }

	/**
	 *  Constructor for new empty DataSet
	 *  @param pSecurity the secure manager
	 *  @param pClass the class of the item types
	 */ 
	protected DataSet(SecureManager 	pSecurity,
				   	  Class<T>			pClass) {
		/* Store the security manager and class */
		theSecurity   	= pSecurity;
		theClass		= pClass;
		
		/* Create the empty security lists */
		theControlKeys 	= new ControlKey.List(this);
		theDataKeys    	= new DataKey.List(this);
		theControlData 	= new ControlData.List(this);
		
		/* Create the map of additional lists */
		theMap			= new EnumMap<T, DataList<?>>(pClass);
	}
	
	/**
	 * Constructor for a cloned DataSet
	 * @param pSource the source DataSet
	 */
	protected DataSet(DataSet<T> pSource) {
		/* Store the security manager and class */
		theSecurity   	= pSource.theSecurity;
		theClass		= pSource.theClass;
		
		/* Create the map of additional lists */
		theMap			= new EnumMap<T, DataList<?>>(theClass);		
	}
	
	/**
	 * Construct an update extract for a DataSet.
	 * @param pExtract the extract to build 
	 * @return the update DataSet 
	 */
	protected void getUpdateSet(DataSet<T> 	pExtract) {
		/* Build the static differences */
		pExtract.theControlKeys = new ControlKey.List(theControlKeys, 	ListStyle.UPDATE);
		pExtract.theDataKeys   	= new DataKey.List(theDataKeys, 		ListStyle.UPDATE);
		pExtract.theControlData = new ControlData.List(theControlData, 	ListStyle.UPDATE);
	}
	
	/**
	 * Construct a difference extract between two DataSets.
	 * The difference extract will only contain items that differ between the two DataSets.
	 * Items that are in the new list, but not in the old list will be viewed as inserted.
	 * Items that are in the old list but not in the new list will be viewed as deleted.
	 * Items that are in both list but differ will be viewed as changed 
	 * @param pOld The old list to extract from 
	 */
	public abstract DataSet<?> getDifferenceSet(DataSet<?> pOld) throws Exception;

	/**
	 * Construct a difference extract between two DataSets.
	 * The difference extract will only contain items that differ between the two DataSets.
	 * Items that are in the new list, but not in the old list will be viewed as inserted.
	 * Items that are in the old list but not in the new list will be viewed as deleted.
	 * Items that are in both list but differ will be viewed as changed 
	 * @param pNew The new list to extract from 
	 * @param pOld The old list to extract from 
	 */
	protected void getDifferenceSet(DataSet<T> pDifferences, 
									DataSet<T> pOld) throws Exception {
		/* Build the security differences */
		pDifferences.theControlKeys = new ControlKey.List(theControlKeys, 	pOld.getControlKeys());
		pDifferences.theDataKeys	= new DataKey.List(theDataKeys, 		pOld.getDataKeys());
		pDifferences.theControlData	= new ControlData.List(theControlData, 	pOld.getControlData());
	}
	
	/**
	 * ReBase this data set against an earlier version.
	 * @param pOld The old data to reBase against 
	 */
	public void reBase(DataSet<?> pOld) throws Exception {
		/* ReBase the security items */
		theControlKeys.reBase(pOld.getControlKeys());
		theDataKeys.reBase(pOld.getDataKeys());
		theControlData.reBase(pOld.getControlData());
	}
	
	/**
	 * Add DataList to list of lists 
	 * @param pItemType the type of the item to add
	 * @param pList the list to add
	 */
	protected void addList(T pItemType, DataList<?> pList) {
		/* Add the list to the map */
		theMap.put(pItemType, pList);
	}
	
	/**
	 * Obtain DataList for an item type
	 * @param pItemType the type of items
	 * @return the list of items
	 */
	abstract public DataList<?> getDataList(Enum<?> pItemType);
	
	/**
	 * Analyse the DataSet
	 * @param pControl The DataControl 
	 */
	public abstract void analyseData(DataControl<?> pControl) throws Exception;

	/**
	 * Compare this data-set to another to establish equality.
	 * 
	 * @param pThat The Data-set to compare to
	 * @return <code>true</code> if the data-sets are identical, 
	 * <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a DataSet */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a DataSet */
		DataSet<?> myThat = (DataSet<?>)pThat;
		
		/* Compare class */
		if (theClass != myThat.theClass) return false;
		
		/* Compare security data */
		if (!theControlKeys.equals(myThat.getControlKeys())) return false;
		if (!theDataKeys.equals(myThat.getDataKeys())) return false;
		if (!theControlData.equals(myThat.getControlData())) return false;
		
		/* Loop through the Enum values */
		for (T myType: theClass.getEnumConstants()) {
			/* Access the lists */
			DataList<?> myThisList = theMap.get(myType);
			DataList<?> myThatList = myThat.theMap.get(myType);
			
			/* Handle trivial cases */
			if (myThisList == myThatList) 	continue;
			if (myThisList == null)	 		return false;
			
			/* Compare list */
			if (!myThisList.equals(myThatList)) return false;
		}

		/* We are identical */
		return true;
	}

	/**
	 * Provide a string representation of this object
	 * @return formatted string
	 */
	public StringBuilder toHTMLString() { return null; }
	
	/**
	 * Add child entries for the debug object
	 * @param pManager the debug manager
	 * @param pParent the parent debug entry
	 */
	public void addChildEntries(DebugManager 	pManager,
								DebugEntry		pParent) { 
		/* Add the security lists */
		addChildEntry(pManager, pParent, theControlKeys);
		addChildEntry(pManager, pParent, theDataKeys);
		addChildEntry(pManager, pParent, theControlData);
		
		/* Loop through the Enum values */
		for (T myType: theClass.getEnumConstants()) {
			/* Access the lists */
			DataList<?> myList = theMap.get(myType);
			
			/* Add the child entry */
			addChildEntry(pManager, pParent, myList);
		}
	}	

	/**
	 * Add child entry for the list
	 * @return the first dump-able child object
	 */
	private void addChildEntry(DebugManager 	pManager,
							   DebugEntry		pParent,
							   DataList<?>		pList) {
		/* Don't add difference/update lists that are empty */
		if (((pList.getStyle() != ListStyle.DIFFER) &&
		     (pList.getStyle() != ListStyle.UPDATE)) ||
			(pList.sizeAll() > 0))
			pManager.addChildEntry(pParent, pList.itemType(), pList);
	}	

	/**
	 * Determine whether a DataSet has entries
	 * @return <code>true</code> if the DataSet has entries
	 */
	public boolean isEmpty() {
		/* Determine whether the security data is empty */
		if (!theControlKeys.isEmpty() 	||
			!theDataKeys.isEmpty()		||
			!theControlData.isEmpty())
			return false;

		/* Loop through the Enum values */
		for (T myType: theClass.getEnumConstants()) {
			/* Access the lists */
			DataList<?> myList = theMap.get(myType);
			
			/* Determine whether the list is empty */
			if (!myList.isEmpty()) return false;
		}
		
		/* Return the indication */
		return true;
	}
	
	/**
	 * Determine whether the Data-set has updates
	 * @return <code>true</code> if the Data-set has updates, <code>false</code> if not
	 */
	public boolean hasUpdates() {		
		/* Determine whether we have updates */
		if (theControlKeys.hasUpdates()) return true;
		if (theDataKeys.hasUpdates()) return true;
		if (theControlData.hasUpdates()) return true;
		
		/* Loop through the Enum values */
		for (T myType: theClass.getEnumConstants()) {
			/* Access the lists */
			DataList<?> myList = theMap.get(myType);
			
			/* Determine whether the list has updates */
			if (myList.hasUpdates()) return true;
		}
		
		/* We have no updates */
		return false;
	}
	
	/**
	 * Get the control record
	 * @return the control record 
	 */
	public ControlData getControl() {
		/* Set the control */
		return getControlData().getControl();		
	}
	
	/**
	 * Get the active control key
	 * @return the control key 
	 */
	public ControlKey getControlKey() {
		/* Access the control element from the database */
		ControlData myControl = getControl();
		ControlKey  myKey		= null;
		
		/* Access control key from control data */
		if (myControl != null) myKey = myControl.getControlKey();
		
		/* Return the key */
		return myKey;
	}
	
	/**
	 * Initialise Security from database (if present) 
	 * @param pBase the database data
	 */
	public void initialiseSecurity(DataSet<?> pBase) throws Exception {
		/* Initialise Security */
		theControlKeys.initialiseSecurity(pBase);		

		/* Access the control key */
		ControlKey myControl = getControlKey();
		
		/* Loop through the Enum values */
		for (Enum<?> myType: theClass.getEnumConstants()) {
			/* Access the lists */
			DataList<?> myList = theMap.get(myType);
			DataList<?> myBase = pBase.getDataList(myType);
			
			/* If the list is an encrypted list */
			if (myList instanceof EncryptedList) {
				/* Adopt the security */
				EncryptedList<?> myEncrypted = (EncryptedList<?>)myList;
				myEncrypted.adoptSecurity(myControl, (EncryptedList<?>) myBase);
			}
		}
	}
	
	/**
	 * Renew Security 
	 */
	public void renewSecurity() throws Exception {
		/* Create a new ControlKey */
		ControlKey myKey = theControlKeys.addItem();
				
		/* Declare the New Control Key */
		getControl().setControlKey(myKey);
		
		/* Update Security */
		updateSecurity();
	}
	
	/**
	 * Update Security 
	 */
	public void updateSecurity() throws Exception {
		/* Access the control key */
		ControlKey myControl = getControlKey();
		
		/* Loop through the Enum values */
		for (T myType: theClass.getEnumConstants()) {
			/* Access the lists */
			DataList<?> myList = theMap.get(myType);
			
			/* If the list is an encrypted list */
			if (myList instanceof EncryptedList) {
				/* Update the security */
				EncryptedList<?> myEncrypted = (EncryptedList<?>)myList;
				myEncrypted.updateSecurity(myControl);
			}
		}
		
		/* Delete old ControlSets */
		theControlKeys.purgeOldControlKeys();
	}
	
	/**
	 * Get the Security control 
	 * @return the security control 
	 */
	public SecurityControl getSecurityControl() throws Exception {
		/* Access the active control key */
		ControlKey  myKey	= getControlKey();
		
		/* Set the control */
		return (myKey == null) ? null : myKey.getSecurityControl();		
	}
	
	/**
	 * Update data with a new password
	 * @param pSource the source of the data
	 * @return was the password changed <code>true/false</code>
	 */
	public boolean updateSecurityControl(String pSource) throws Exception {
		/* Update the security control */
		boolean isChanged = theSecurity.updateSecurityControl(getSecurityControl(), pSource);
		
		/* If we changed the password */
		if (isChanged) {
			/* Update the control details */
			getControlKey().updateSecurityControl();
		}
		
		/* Return to the caller */
		return isChanged;
	}
}

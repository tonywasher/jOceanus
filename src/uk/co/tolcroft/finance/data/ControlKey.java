package uk.co.tolcroft.finance.data;

import java.util.EnumMap;
import java.util.Map;

import uk.co.tolcroft.models.DataItem;
import uk.co.tolcroft.models.DataList;
import uk.co.tolcroft.models.DataState;
import uk.co.tolcroft.models.EncryptedPair;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.security.AsymmetricKey;
import uk.co.tolcroft.security.SecureManager;
import uk.co.tolcroft.security.SecurityControl;
import uk.co.tolcroft.security.SymmetricKey.SymKeyType;

public class ControlKey extends DataItem {
	/**
	 * The name of the object
	 */
	public static final String objName = "ControlKey";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";

	/**
	 * Control Key Length
	 */
	public final static int CTLLEN 		= AsymmetricKey.IDSIZE;

	/**
	 * The DataKey Map
	 */
	private Map<SymKeyType, DataKey>	theMap	= null;
	
	/* Local values */
	private SecurityControl	theControl	= null;
	
	/* Access methods */
	public  SecurityControl	getSecurityControl()	{ return theControl; }
	public  String 			getSecurityKey()  		{ return getObj().getSecurityKey(); }

	/* Linking methods */
	public ControlKey	getBase() { return (ControlKey)super.getBase(); }
	public Values  		getObj()  { return (Values)super.getObj(); }	
	
	/* Field IDs */
	public static final int FIELD_KEY	   = 1;
	public static final int NUMFIELDS	   = 2; 

	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }
	
	/**
	 * Obtain the number of fields for an item
	 * @return the number of fields
	 */
	public int	numFields() { return NUMFIELDS; }
	
	/**
	 * Determine the field name for a particular field
	 * @return the field name
	 */
	public static String	fieldName(int iField) {
		switch (iField) {
			case FIELD_ID:			return NAME_ID;
			case FIELD_KEY:			return "ControlKey";
			default:		  		return DataItem.fieldName(iField);
		}
	}
				
	/**
	 * Determine the field name in a non-static fashion 
	 */
	public String getFieldName(int iField) { return fieldName(iField); }
	
	/**
	 * Format the value of a particular field as a table row
	 * @param iField the field number
	 * @param pObj the values to use
	 * @return the formatted field
	 */
	public String formatField(int iField, histObject pObj) {
		String 	myString = "";
		switch (iField) {
			case FIELD_ID: 		
				myString += getId(); 
				break;
			case FIELD_KEY:
				myString += getSecurityKey(); 
				break;
		}
		return myString;
	}
							
	/**
 	 * Construct a copy of a ControlKey
 	 * @param pKey The Key to copy 
 	 */
	protected ControlKey(List pList, ControlKey pKey) {
		/* Set standard values */
		super(pList, pKey.getId());
		Values myObj = new Values(pKey.getObj());
		setObj(myObj);
		theControl		= pKey.getSecurityControl();

		/* Switch on the LinkStyle */
		switch (pList.getStyle()) {
			case CORE:
				pList.setNewId(this);				
				break;
			case EDIT:
				setBase(pKey);
				setState(DataState.CLEAN);
				break;
			case UPDATE:
				setBase(pKey);
				setState(pKey.getState());
				break;
		}
	}

	/**
	 * Constructor for loading an encrypted ControlKey 
	 * @param pList the list to which to add the key to 
	 * @param uId the id of the ControlKey
	 * @param pSecurityKey the encrypted string for the key
	 */
	public ControlKey(List     	pList,
				   	  int		uId,
				   	  String	pSecurityKey) throws Exception {
		/* Initialise the item */
		super(pList, uId);
		Values myObj = new Values();
		setObj(myObj);

		/* Store the key */
		myObj.setSecurityKey(pSecurityKey);

		/* Access the Security manager */
		DataSet 		myData 		= pList.getData();
		SecureManager 	mySecurity 	= myData.getSecurity();
		
		/* Obtain the required security control */
		theControl = mySecurity.getSecurityControl(pSecurityKey, "Database");
		
		/* Create the DataKey Map */
		theMap = new EnumMap<SymKeyType,DataKey>(SymKeyType.class);
		
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/**
	 * Constructor for a new ControlKey. This will create a set of DataKeys 
	 * @param pList the list to which to add the key to 
	 */
	public ControlKey(List 	pList) throws Exception {
		/* Initialise the item */
		super(pList, 0);
		Values myObj = new Values();
		setObj(myObj);
				
		/* Access the Security manager */
		DataSet 		myData 		= pList.getData();
		SecureManager 	mySecurity 	= myData.getSecurity();
		EncryptedPair	myPairs		= myData.getEncryptedPairs();
		
		/* Obtain the required security control */
		theControl = mySecurity.getSecurityControl(null, "Database");
		myObj.setSecurityKey(theControl.getSecurityKey());
		
		/* Create the DataKey Map */
		theMap = new EnumMap<SymKeyType,DataKey>(SymKeyType.class);
		
		/* Allocate the id */
		pList.setNewId(this);
				
		/* Allocate the DataKeys */
		allocateDataKeys(pList.getData());
		
		/* TEMP Obtain the AES key */
		DataKey myKey = theMap.get(SymKeyType.AES);
		myPairs.setEncryptionDtl(myKey.getDataKey(), myKey.getInitVector());
	}

	/**
	 * Compare this ControlKey to another to establish equality.
	 * @param pThat The ControlKey to compare to
	 * @return <code>true</code> if the ControlKey is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a ControlKey */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a ControlKey */
		ControlKey myKey = (ControlKey)pThat;
		
		/* Check for equality */
		if (getId() != myKey.getId()) 		return false;
		if (Utils.differs(getSecurityKey(), myKey.getSecurityKey())) return false;
		return true;
	}

	/**
	 * Compare this ControlKey to another to establish sort order. 
	 * @param pThat The ControlKey to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		int iDiff;
		
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a ControlKey */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as a ControlKey */
		ControlKey myThat = (ControlKey)pThat;

		/* Compare the IDs */
		iDiff =(int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}

	/**
	 * Allocate a new set of DataKeys 
	 * @param pData the DataSet
	 */
	private void allocateDataKeys(DataSet pData) throws Exception {
		/* Access the DataKey List */
		DataKey.List myKeys = pData.getDataKeys();
		
		/* Loop through the SymKeyType values */
		for (SymKeyType myType: SymKeyType.values()) {
			/* Create a new DataKey for this ControlKey */
			DataKey myKey = myKeys.addItem(this, myType);
			
			/* Store the DataKey into the map */
			theMap.put(myType, myKey);
		}
	}
	
	/**
	 * Register DataKey 
	 * @param pKey the DataKey to register
	 */
	protected void registerDataKey(DataKey pKey) throws Exception {
		/* Store the DataKey into the map */
		theMap.put(pKey.getKeyType(), pKey);

		/* TEMP Obtain the AES key */
		if (pKey.getKeyType() == SymKeyType.AES) {
			EncryptedPair myPairs = ((List)getList()).theData.getEncryptedPairs();
			myPairs.setEncryptionDtl(pKey.getDataKey(), pKey.getInitVector());
		}
	}
	
	/**
	 * Determine whether two {@link ControlKey} objects differ.
	 * @param pCurr The current ControlKey 
	 * @param pNew The new ControlKey
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(ControlKey pCurr, ControlKey pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (pCurr.compareTo(pNew) != 0))));
	}
	
	/**
	 * DataKey List
	 */
	public static class List  extends DataList<ControlKey> {
		/* Members */
		private DataSet		theData		= null;
		public 	DataSet 	getData()	{ return theData; }

		/** 
		 * Construct an empty CORE static list
	 	 * @param pData the DataSet for the list
		 */
		protected List(DataSet pData) { 
			super(ListStyle.CORE, false);
			theData = pData;
		}

		/** 
		 * Construct an empty generic static list
	 	 * @param pData the DataSet for the list
		 * @param pStyle the style of the list 
		 */
		protected List(DataSet pData, ListStyle pStyle) {
			super(pStyle, false);
			theData = pData;
		}

		/** 
		 * Construct a generic Static list
		 * @param pList the source static list 
		 * @param pStyle the style of the list 
		 */
		public List(List pList, ListStyle pStyle) { 
			super(pList, pStyle);
			theData = pList.theData;
		}

		/** 
		 * Construct a difference static list
		 * @param pNew the new Static list 
		 * @param pOld the old Static list 
		 */
		protected List(List pNew, List pOld) { 
			super(pNew, pOld);
			theData = pNew.theData;
		}

		/** 
		 * 	Clone a Price list
		 * @return the cloned list
		 */
		protected List cloneIt() {return new List(this, ListStyle.CORE); }

		/**
		 * Add a new item to the core list
		 * @param pItem item
		 * @return the newly added item
		 */
		public ControlKey addNewItem(DataItem pItem) { 
			ControlKey myKey = new ControlKey(this, (ControlKey)pItem);
			myKey.addToList();
			return myKey; 
		}

		/**
		 * Add a new item to the edit list
		 * @param isCredit - ignored
		 * @return the newly added item
		 */
		public ControlKey addNewItem(boolean isCredit) { return null; }

		/**
		 * 	Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }

		/**
		 *  Add a ControlKey item from a Database/Backup
		 */
		public ControlKey addItem(int  		uId,
							      String	pSecurityKey) throws Exception {
			ControlKey     	myKey;
			
			/* Create the ControlKey */
			myKey = new ControlKey(this, 
								   uId,
							       pSecurityKey);
			
			/* Check that this KeyId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
									myKey,
									"Duplicate ControlKeyId <" + uId + ">");
			 
			/* Add to the list */
			myKey.addToList();
			return myKey;
		}			

		/**
		 *  Add a new ControlKey (with associated DataKeys)
		 */
		public ControlKey addItem() throws Exception {
			ControlKey     	myKey;
			
			/* Create the key */
			myKey = new ControlKey(this);
			
			/* Add to the list */
			myKey.addToList();
			return myKey;
		}			

		/**
		 * Initialise Security from a DataBase 
		 * @param pControlKey the ControlKey to clone (or null)
		 */
		protected void initialiseSecurity(ControlKey pControlKey) throws Exception {
			ControlKey myKey;
			
			/* If we have an existing security key */
			if (pControlKey != null) {
				/* Clone the Control Key and its DataKeys */
				myKey = cloneControlKey(pControlKey);
			}
			
			/* else create a new security set */
			else {
				/* Create the new security set */
				myKey = addItem();
			}
			
			/* Declare the Control Key */
			theData.getControl().setControlKey(myKey);
			
			/* Ensure the encryption */
			theData.ensureEncryption();
		}
		
		/**
		 * Clone Security from a DataBase 
		 * @param pControlKey the ControlKey to clone
		 */
		private ControlKey cloneControlKey(ControlKey pControlKey) throws Exception {
			/* Clone the control key */
			ControlKey myControl = addItem(pControlKey.getId(),
										   pControlKey.getSecurityKey());
			
			/* Access the DataKey List */
			DataKey.List myKeys = theData.getDataKeys();
			
			/* Loop through the SymKeyType values */
			for (SymKeyType myType: SymKeyType.values()) {
				/* Access the source Data key */
				DataKey mySrcKey = pControlKey.theMap.get(myType);
				
				/* Create a new DataKey for this ControlKey */
				DataKey myKey = myKeys.addItem(myControl,
											   mySrcKey);
				
				/* Store the DataKey into the map */
				myControl.theMap.put(myType, myKey);

				/* TEMP Obtain the AES key */
				if (myType == SymKeyType.AES) {
					EncryptedPair myPairs = theData.getEncryptedPairs();
					myPairs.setEncryptionDtl(myKey.getDataKey(), myKey.getInitVector());
				}
			}
			
			/* return the cloned key */
			return myControl;
		}
	}

	/**
	 * Values for a static 
	 */
	public class Values implements histObject {
		private String			theSecurityKey	= null;
		
		/* Access methods */
		public  String 			getSecurityKey()  	{ return theSecurityKey; }
		
		public void setSecurityKey(String pValue) {
			theSecurityKey = pValue; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theSecurityKey	= pValues.getSecurityKey();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (Utils.differs(theSecurityKey,   pValues.theSecurityKey))   	return false;
			return true;
		}
		
		/* Copy values */
		public void    copyFrom(histObject pSource) {
			Values myValues = (Values)pSource;
			copyFrom(myValues);
		}
		public histObject copySelf() {
			return new Values(this);
		}
		public void    copyFrom(Values pValues) {
			theSecurityKey	= pValues.getSecurityKey();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_KEY:
					bResult = (Utils.differs(theSecurityKey,	pValues.theSecurityKey));
					break;
			}
			return bResult;
		}
	}
}

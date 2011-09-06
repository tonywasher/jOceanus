package uk.co.tolcroft.models.data;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.security.DataCipher;
import uk.co.tolcroft.models.security.SecurityControl;
import uk.co.tolcroft.models.security.SymmetricKey;
import uk.co.tolcroft.models.security.SymmetricKey.SymKeyType;
import uk.co.tolcroft.models.Utils;

public class DataKey extends DataItem<DataKey> {
	/**
	 * The name of the object
	 */
	public static final String objName = "DataKey";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";

	/**
	 * Symmetric Key Length
	 */
	public final static int KEYLEN 		= SymmetricKey.IDSIZE;

	/* Local values */
	private SecurityControl	theControl		= null;
	private DataCipher		theCipher		= null;
	private SymmetricKey	theKey			= null;
	private int 			theControlId 	= -1;
	private int 			theKeyTypeId 	= -1;
	
	/* Access methods */
	public  byte[] 			getSecuredKeyDef()  { return getValues().getSecuredKeyDef(); }
	public  DataCipher		getCipher()  		{ return theCipher; }
	public  SymKeyType		getKeyType()  		{ return getValues().getKeyType(); }
	public  ControlKey		getControlKey()		{ return getValues().getControlKey(); }
	protected SymmetricKey	getDataKey()		{ return theKey; }

	/* Linking methods */
	public DataKey	getBase() 	{ return (DataKey)super.getBase(); }
	public Values  	getValues() { return (Values)super.getCurrentValues(); }	
	
	/* Field IDs */
	public static final int FIELD_CONTROL  = DataItem.NUMFIELDS;
	public static final int FIELD_KEYTYPE  = DataItem.NUMFIELDS+1;
	public static final int FIELD_KEY	   = DataItem.NUMFIELDS+2;
	public static final int NUMFIELDS	   = DataItem.NUMFIELDS+3; 

	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }
	
	/**
	 * Obtain the number of fields for an item
	 * @return the number of fields
	 */
	public int	numFields() {return NUMFIELDS; }
	
	/**
	 * Determine the field name for a particular field
	 * @return the field name
	 */
	public static String	fieldName(int iField) {
		switch (iField) {
			case FIELD_CONTROL:		return "ControlId";
			case FIELD_KEYTYPE:		return "KeyType";
			case FIELD_KEY:			return "DataKey";
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
	 * @param pValues the values to use
	 * @return the formatted field
	 */
	public String formatField(int iField, HistoryValues<DataKey> pValues) {
		Values	myValues = (Values)pValues;
		String 	myString = "";
		switch (iField) {
			case FIELD_CONTROL: 		
				if (myValues.getControlKey() != null)
					myString += myValues.getControlKey().getId();
				else 
					myString += "Id=" + theControlId;					
				break;
			case FIELD_KEYTYPE: 		
				myString += (getKeyType() == null) ? ("Id=" + theKeyTypeId) : getKeyType().toString(); 
				break;
			case FIELD_KEY:
				myString += Utils.HexStringFromBytes(myValues.getSecuredKeyDef()); 
				break;
			default: 		
				myString += super.formatField(iField, pValues); 
				break;
		}
		return myString;
	}
							
	/**
 	 * Construct a copy of a DataKey
	 * @param pList the list to add to
 	 * @param pKey The Key to copy 
 	 */
	protected DataKey(List pList, DataKey pKey) {
		/* Set standard values */
		super(pList, pKey.getId());
		Values myValues = new Values(pKey.getValues());
		setValues(myValues);
		theControlId	= pKey.theControlId;
		theControl		= pKey.theControl;
		theCipher		= pKey.getCipher();
		theKey			= pKey.getDataKey();

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
 	 * Construct a DataKey from Database/Backup
	 * @param pList the list to add to
 	 * @param uId the id of the DataKey
 	 * @param uControlId the id of the ControlKey
 	 * @param uKeyTypeId the id of the KeyType
	 * @param pSecurityKey the encrypted symmetric key
	 */
	public DataKey(List     	pList,
				   int			uId,
				   int			uControlId,
				   int			uKeyTypeId,
				   byte[]		pSecurityKey) throws Exception {
		/* Initialise the item */
		super(pList, uId);
		Values myValues = new Values();
		setValues(myValues);

		/* Record the IDs */
		theControlId	= uControlId;
		theKeyTypeId	= uKeyTypeId;

		/* Look up the ControlKey */
		ControlKey myControlKey = pList.theData.getControlKeys().searchFor(uControlId);
		if (myControlKey == null) 
			throw new Exception(ExceptionClass.DATA,
		                        this,
					            "Invalid ControlKey Id");
		
		/* Determine the SymKeyType */
		try { myValues.setKeyType(SymKeyType.fromId(uKeyTypeId)); }
		catch (Exception e) {
			throw new Exception(ExceptionClass.DATA,
								this,
	            				"Invalid KeyType Id " + uKeyTypeId);
		}
		
		/* Store the keys */
		myValues.setControlKey(myControlKey);
		myValues.setSecuredKeyDef(pSecurityKey);

		/* Create the Symmetric Key from the wrapped data */
		theControl = myControlKey.getSecurityControl();
		theKey	   = theControl.getSymmetricKey(pSecurityKey, getKeyType()); 
		
		/* Access the Cipher */
		theCipher = theKey.initDataCipher();
		
		/* Register the DataKey */
		myControlKey.registerDataKey(this);
		
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/**
	 * Constructor for a new DataKey in a new ControlKey set
	 * @param pList the list to add to
	 * @param pControlKey the ControlKey to which this key belongs
	 * @param pKeyType the Key type of the new key
	 */
	public DataKey(List 		pList,
				   ControlKey 	pControlKey,
				   SymKeyType	pKeyType) throws Exception {
		/* Initialise the item */
		super(pList, 0);
		Values myValues = new Values();
		setValues(myValues);
		
		/* Store the Control Details */
		theControlId	= pControlKey.getId();
		
		/* Create the new key */
		theControl = pControlKey.getSecurityControl();
		theKey	   = theControl.getSymmetricKey(pKeyType);
		
		/* Access the Cipher */
		theCipher = theKey.initDataCipher();
		
		/* Store its secured keyDef */
		myValues.setControlKey(pControlKey);
		myValues.setKeyType(pKeyType);
		myValues.setSecuredKeyDef(theControl.getSecuredKeyDef(theKey));			
	
		/* Register the DataKey */
		pControlKey.registerDataKey(this);
		
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/**
	 * Constructor for a cloned DataKey in a new ControlKey set
	 * @param pList the list to add to
	 * @param pControlKey the ControlKey to which this key belongs
	 * @param pDataKey the DataKey to clone
	 */
	public DataKey(List 		pList,
				   ControlKey 	pControlKey,
				   DataKey		pDataKey) throws Exception {
		/* Initialise the item */
		super(pList, 0);
		Values myValues = new Values();
		setValues(myValues);
		
		/* Store the Control details */
		theControlId	= pControlKey.getId();
		theControl		= pControlKey.getSecurityControl();
		
		/* Copy the key details */
		theKey	  = pDataKey.getDataKey();
		theCipher = pDataKey.getCipher();
		
		/* Store its secured keyDef */
		myValues.setControlKey(pDataKey.getControlKey());
		myValues.setKeyType(pDataKey.getKeyType());
		myValues.setSecuredKeyDef(theControl.getSecuredKeyDef(theKey));			
		
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/**
	 * Compare this DataKey to another to establish equality.
	 * 
	 * @param pThat The DataKey to compare to
	 * @return <code>true</code> if the DataKey is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a DataKey */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a DataKey */
		DataKey myThat = (DataKey)pThat;
		
		/* Check for equality */
		if (getId() != myThat.getId()) 		 		return false;
		
		/* Compare the changeable values */
		return getValues().histEquals(myThat.getValues());
	}

	/**
	 * Compare this DataKey to another to establish sort order. 
	 * @param pThat The DataKey to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		int iDiff;
		
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a DataKey */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as a DataKey */
		DataKey myThat = (DataKey)pThat;

		/* Compare the IDs */
		iDiff =(int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}

	/**
	 * Update security control 
	 */
	protected void updateSecurityControl() throws Exception {
		/* Store the current detail into history */
		pushHistory();

		/* Update the Security Control Key and obtain the new secured KeyDef */
		theControl = getControlKey().getSecurityControl();
		getValues().setSecuredKeyDef(theControl.getSecuredKeyDef(theKey));			
		
		/* Check for changes */
		if (checkForHistory()) setState(DataState.CHANGED);
	}
	
	/**
	 * DataKey List
	 */
	public static class List  extends DataList<DataKey> {
		/* Members */
		private DataSet<?>	theData		= null;
		public 	DataSet<?> 	getData()	{ return theData; }

		/** 
		 * Construct an empty CORE DataKey list
	 	 * @param pData the DataSet for the list
		 */
		protected List(DataSet<?> pData) { 
			super(DataKey.class, ListStyle.CORE, false);
			theData = pData;
		}

		/** 
		 * Construct an empty generic DataKey list
	 	 * @param pData the DataSet for the list
		 * @param pStyle the style of the list 
		 */
		protected List(DataSet<?> pData, ListStyle pStyle) {
			super(DataKey.class, pStyle, false);
			theData = pData;
		}

		/**
		 * Constructor for a cloned List
		 * @param pSource the source List
		 */
		private List(List pSource) { 
			super(pSource);
			theData = pSource.theData;
		}
		
		/**
		 * Construct an update extract for the List.
		 * @return the update Extract
		 */
		private List getExtractList(ListStyle pStyle) {
			/* Build an empty Extract List */
			List myList = new List(this);
			
			/* Obtain underlying updates */
			myList.populateList(pStyle);
			
			/* Return the list */
			return myList;
		}

		/* Obtain extract lists. */
		public List getUpdateList() { return getExtractList(ListStyle.UPDATE); }
		public List getEditList() 	{ return null; }
		public List getClonedList() { return getExtractList(ListStyle.CORE); }

		/** 
		 * Construct a difference ControlData list
		 * @param pNew the new ControlData list 
		 * @param pOld the old ControlData list 
		 */
		protected List getDifferences(DataList<DataKey> pOld) { 
			/* Build an empty Difference List */
			List myList = new List(this);
			
			/* Calculate the differences */
			myList.getDifferenceList(this, pOld);
			
			/* Return the list */
			return myList;
		}

		/**
		 * Add a new item to the core list
		 * @param pItem item
		 * @return the newly added item
		 */
		public DataKey addNewItem(DataItem<?> pItem) { 
			DataKey myKey = new DataKey(this, (DataKey)pItem);
			add(myKey);
			return myKey; 
		}

		/**
		 * Add a new item to the edit list
		 * @param isCredit - ignored
		 * @return the newly added item
		 */
		public DataKey addNewItem(boolean isCredit) { return null; }

		/**
		 * 	Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }

		/**
	 	 * Add a DataKey from Database/Backup
	 	 * @param uId the id of the DataKey
	 	 * @param uControlId the id of the ControlKey
	 	 * @param uKeyTypeId the id of the KeyType
		 * @param pSecurityKey the encrypted symmetric key
	 	 */
		public DataKey addItem(int  	uId,
							   int		uControlId,
							   int		uKeyTypeId,
	            			   byte[]	pSecurityKey) throws Exception {
			DataKey     	myKey;
			
			/* Create the DataKey */
			myKey = new DataKey(this, 
								uId,
								uControlId,
								uKeyTypeId,
							    pSecurityKey);
			
			/* Check that this KeyId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
									myKey,
									"Duplicate DataKeyId (" + uId + ")");
			 
			/* Add to the list */
			add(myKey);
			return myKey;
		}			

		/**
		 *  Add a new DataKey for the passed ControlKey
		 *  @param pControlKey the ControlKey
		 *  @param pKeyType the KeyType
		 *  @return the new DataKey
		 */
		public DataKey addItem(ControlKey 	pControlKey,
							   SymKeyType	pKeyType) throws Exception {
			DataKey     	myKey;
			
			/* Create the key */
			myKey = new DataKey(this, 
								pControlKey,
								pKeyType);
			
			/* Add to the list */
			add(myKey);
			return myKey;
		}			

		/**
		 *  Add a clone of the passed DataKey for the passed ControlKey
		 *  @param pControlKey the ControlKey
		 *  @param pDataKey the DataKey
		 *  @return the new DataKey
		 */
		public DataKey addItem(ControlKey 	pControlKey,
							   DataKey		pDataKey) throws Exception {
			DataKey     	myKey;
			
			/* Create the key */
			myKey = new DataKey(this, 
								pControlKey,
								pDataKey);
			
			/* Add to the list */
			add(myKey);
			return myKey;
		}			
	}

	/**
	 * Values for a DataKey 
	 */
	public class Values implements HistoryValues<DataKey> {
		private ControlKey		theControlKey		= null;
		private byte[]			theSecuredKeyDef	= null;
		private SymKeyType		theKeyType			= null;
		
		/* Access methods */
		public  ControlKey		getControlKey()  	{ return theControlKey; }
		public  byte[] 			getSecuredKeyDef()  { return theSecuredKeyDef; }
		public  SymKeyType		getKeyType()  		{ return theKeyType; }
		
		public void setControlKey(ControlKey pValue) {
			theControlKey  = pValue; }
		public void setSecuredKeyDef(byte[] pValue) {
			theSecuredKeyDef = pValue; }
		public void setKeyType(SymKeyType pValue) {
			theKeyType = pValue; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) { copyFrom(pValues); }
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(HistoryValues<DataKey> pCompare) {
			Values myValues = (Values)pCompare;
			if (ControlKey.differs(theControlKey, myValues.theControlKey))   	return false;
			if (Utils.differs(theSecuredKeyDef,   myValues.theSecuredKeyDef))   return false;
			if (!theKeyType.equals(myValues.theKeyType)) 						return false;
			return true;
		}
		
		/* Copy values */
		public HistoryValues<DataKey> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			Values myValues 	= (Values)pSource;
			theControlKey		= myValues.getControlKey();
			theSecuredKeyDef	= myValues.getSecuredKeyDef();
			theKeyType			= myValues.getKeyType();
		}
		public boolean	fieldChanged(int fieldNo, HistoryValues<DataKey> pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_CONTROL:
					bResult = (ControlKey.differs(theControlKey, pValues.theControlKey));
					break;
				case FIELD_KEYTYPE:
					bResult = (!theKeyType.equals(pValues.theKeyType));
					break;
				case FIELD_KEY:
					bResult = (Utils.differs(theSecuredKeyDef,	pValues.theSecuredKeyDef));
					break;
			}
			return bResult;
		}
	}	
}

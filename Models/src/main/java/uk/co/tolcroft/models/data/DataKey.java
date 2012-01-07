package uk.co.tolcroft.models.data;

import uk.co.tolcroft.models.Difference;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.help.DebugDetail;
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
	 * Encrypted Symmetric Key Length
	 */
	public final static int KEYLEN 		= SymmetricKey.IDSIZE;

	/* Access methods */
	public  byte[] 			getSecuredKeyDef()  { return getValues().getSecuredKeyDef(); }
	public  DataCipher		getCipher()  		{ return getValues().getCipher(); }
	public  SymKeyType		getKeyType()  		{ return getValues().getKeyType(); }
	public  ControlKey		getControlKey()		{ return getValues().getControlKey(); }
	protected SymmetricKey	getDataKey()		{ return getValues().getKey(); }

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
	 * @param pDetail the debug detail
	 * @param iField the field number
	 * @param pValues the values to use
	 * @return the formatted field
	 */
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<DataKey> pValues) {
		Values	myValues = (Values)pValues;
		String 	myString = "";
		switch (iField) {
			case FIELD_CONTROL: 		
				myString = "Id=" + myValues.theControlId;					
				if (myValues.getControlKey() != null)
					myString = pDetail.addDebugLink(myValues.getControlKey(), myString);
				break;
			case FIELD_KEYTYPE: 		
				myString += (myValues.getKeyType() == null) ? ("Id=" + myValues.theKeyTypeId)  
															: myValues.getKeyType().toString(); 
				break;
			case FIELD_KEY:
				myString += Utils.HexStringFromBytes(myValues.getSecuredKeyDef()); 
				break;
			default: 		
				myString += super.formatField(pDetail, iField, pValues); 
				break;
		}
		return myString;
	}
							
	/**
	 * Get an initial set of values 
	 * @return an initial set of values 
	 */
	protected HistoryValues<DataKey> getNewValues() { return new Values(); }
	
	/**
 	 * Construct a copy of a DataKey
	 * @param pList the list to add to
 	 * @param pKey The Key to copy 
 	 */
	protected DataKey(List pList, DataKey pKey) {
		/* Set standard values */
		super(pList, pKey.getId());
		Values myValues = getValues();
		myValues.copyFrom(pKey.getValues());

		/* Switch on the LinkStyle */
		switch (getStyle()) {
			case CLONE:
				isolateCopy(pList.getData());
			case CORE:
			case COPY:
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
	private DataKey(List     	pList,
				    int			uId,
				    int			uControlId,
				    int			uKeyTypeId,
				    byte[]		pSecurityKey) throws ModelException {
		/* Initialise the item */
		super(pList, uId);
		Values myValues = getValues();

		/* Record the IDs */
		myValues.setControlId(uControlId);
		myValues.setKeyTypeId(uKeyTypeId);
		myValues.setSecuredKeyDef(pSecurityKey);

		/* Determine the SymKeyType */
		try { myValues.setKeyType(SymKeyType.fromId(uKeyTypeId)); }
		catch (ModelException e) {
			throw new ModelException(ExceptionClass.DATA,
								this,
	            				"Invalid KeyType Id " + uKeyTypeId);
		}
		
		/* Look up the ControlKey */
		ControlKey myControlKey = pList.theData.getControlKeys().searchFor(uControlId);
		if (myControlKey == null) 
			throw new ModelException(ExceptionClass.DATA,
		                        this,
					            "Invalid ControlKey Id");
		
		/* Store the keys */
		myValues.setControlKey(myControlKey);

		/* Create the Symmetric Key from the wrapped data */
		SecurityControl myControl = myControlKey.getSecurityControl();
		SymmetricKey myKey = myControl.getSymmetricKey(pSecurityKey, getKeyType());
		myValues.setKey(myKey);
		myValues.setSecurityControl(myControl);
		
		/* Access the Cipher */
		myValues.setCipher(myKey.initDataCipher());
		
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
	private DataKey(List 		pList,
				    ControlKey 	pControlKey,
				    SymKeyType	pKeyType) throws ModelException {
		/* Initialise the item */
		super(pList, 0);
		Values myValues = getValues();
		
		/* Store the Details */
		myValues.setControlKey(pControlKey);
		myValues.setKeyType(pKeyType);
		
		/* Create the new key */
		SecurityControl myControl = pControlKey.getSecurityControl();
		SymmetricKey myKey  = myControl.getSymmetricKey(pKeyType);
		myValues.setKey(myKey);
		
		/* Access the Cipher */
		myValues.setCipher(myKey.initDataCipher());
		
		/* Store its secured keyDef */
		myValues.setSecurityControl(myControl);
		myValues.setSecuredKeyDef(myControl.getSecuredKeyDef(myKey));			
	
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
	private DataKey(List 		pList,
				    ControlKey 	pControlKey,
				    DataKey		pDataKey) throws ModelException {
		/* Initialise the item */
		super(pList, 0);
		Values myValues = getValues();
		
		/* Store the Control details */
		myValues.setControlKey(pControlKey);
		
		/* Copy the key details */
		myValues.setKey(pDataKey.getDataKey());
		myValues.setCipher(pDataKey.getCipher());
		
		/* Access Security Control */
		SecurityControl myControl = pControlKey.getSecurityControl();

		/* Store its secured keyDef */
		myValues.setSecurityControl(myControl);
		myValues.setKeyType(pDataKey.getKeyType());
		myValues.setSecuredKeyDef(myControl.getSecuredKeyDef(myValues.getKey()));			
		
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
		return getValues().histEquals(myThat.getValues()).isIdentical();
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
	 * Isolate Data Copy
	 * @param pData the DataSet
	 */
	private void isolateCopy(DataSet<?> pData) {
		ControlKey.List myKeys = pData.getControlKeys();
		
		/* Update to use the local copy of the ControlKeys */
		Values 		myValues   	= getValues();
		ControlKey 	myKey 		= myValues.getControlKey();
		ControlKey 	myNewKey 	= myKeys.searchFor(myKey.getId());
		myValues.setControlKey(myNewKey);
		
		/* Register the Key */
		myNewKey.registerDataKey(this);
	}

	/**
	 * Update security control 
	 */
	protected void updateSecurityControl() throws ModelException {
		/* Store the current detail into history */
		pushHistory();

		/* Update the Security Control Key and obtain the new secured KeyDef */
		Values 			myValues   		= getValues();
		ControlKey		myControlKey	= getControlKey();
		SecurityControl	myControl		= myControlKey.getSecurityControl();
		myValues.setSecurityControl(myControl);
		myValues.setSecuredKeyDef(myControl.getSecuredKeyDef(myValues.getKey()));			
		
		/* Check for changes */
		if (checkForHistory()) setState(DataState.CHANGED);
	}
	
	/**
	 * DataKey List
	 */
	public static class List  extends DataList<List, DataKey> {
		/* Members */
		private DataSet<?>	theData		= null;
		public 	DataSet<?> 	getData()	{ return theData; }

		/** 
		 * Construct an empty CORE DataKey list
	 	 * @param pData the DataSet for the list
		 */
		protected List(DataSet<?> pData) { 
			super(List.class, DataKey.class, ListStyle.CORE, false);
			theData = pData;
		}

		/** 
		 * Construct an empty generic DataKey list
	 	 * @param pData the DataSet for the list
		 * @param pStyle the style of the list 
		 */
		protected List(DataSet<?> pData, ListStyle pStyle) {
			super(List.class, DataKey.class, pStyle, false);
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
		public List getUpdateList() 	{ return getExtractList(ListStyle.UPDATE); }
		public List getEditList() 		{ return null; }
		public List getShallowCopy() 	{ return getExtractList(ListStyle.COPY); }
		public List getDeepCopy(DataSet<?> pDataSet)	{ 
			/* Build an empty Extract List */
			List myList = new List(this);
			myList.theData = pDataSet;
			
			/* Obtain underlying clones */
			myList.populateList(ListStyle.CLONE);
			myList.setStyle(ListStyle.CORE);
			
			/* Return the list */
			return myList;
		}

		/** 
		 * Construct a difference ControlData list
		 * @param pNew the new ControlData list 
		 * @param pOld the old ControlData list 
		 */
		protected List getDifferences(List pOld) { 
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
		 * @return the newly added item
		 */
		public DataKey addNewItem() { return null; }

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
	            			   byte[]	pSecurityKey) throws ModelException {
			DataKey     	myKey;
			
			/* Create the DataKey */
			myKey = new DataKey(this, 
								uId,
								uControlId,
								uKeyTypeId,
							    pSecurityKey);
			
			/* Check that this KeyId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new ModelException(ExceptionClass.DATA,
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
							   SymKeyType	pKeyType) throws ModelException {
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
							   DataKey		pDataKey) throws ModelException {
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
	public class Values extends HistoryValues<DataKey> {
		private SecurityControl	theControl			= null;
		private ControlKey		theControlKey		= null;
		private byte[]			theSecuredKeyDef	= null;
		private SymKeyType		theKeyType			= null;
		private int 			theControlId 		= -1;
		private int 			theKeyTypeId 		= -1;
		private DataCipher		theCipher			= null;
		private SymmetricKey	theKey				= null;
		
		/* Access methods */
		public  SecurityControl	getSecurityControl()	{ return theControl; }
		public  ControlKey		getControlKey()  		{ return theControlKey; }
		public  byte[] 			getSecuredKeyDef()  	{ return theSecuredKeyDef; }
		public  SymKeyType		getKeyType()  			{ return theKeyType; }
		public  int				getControlId()  		{ return theControlId; }
		public  int				getKeyTypeId()  		{ return theKeyTypeId; }
		public  DataCipher		getCipher()  			{ return theCipher; }
		public  SymmetricKey	getKey()  				{ return theKey; }
		
		private void setSecurityControl(SecurityControl pControl) {
			theControl			= pControl; }
		private void setControlKey(ControlKey pValue) {
			theControlKey		= pValue; 
			theControlId 		= (pValue == null) ? -1 : pValue.getId(); }
		private void setSecuredKeyDef(byte[] pValue) {
			theSecuredKeyDef	= pValue; }
		private void setKeyType(SymKeyType pValue) {
			theKeyType			= pValue; 
			theKeyTypeId 		= (pValue == null) ? -1 : pValue.getId(); }
		private void setKeyTypeId(int pValue) {
			theKeyTypeId		= pValue; }
		private void setControlId(int pValue) {
			theControlId		= pValue; }
		private void setCipher(DataCipher pValue) {
			theCipher			= pValue; }
		private void setKey(SymmetricKey pValue) {
			theKey				= pValue; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) { copyFrom(pValues); }
		
		/* Check whether this object is equal to that passed */
		public Difference histEquals(HistoryValues<DataKey> pCompare) {
			/* Make sure that the object is the same class */
			if (pCompare.getClass() != this.getClass()) return Difference.Different;
			
			/* Cast correctly */
			Values 		myValues 		= (Values)pCompare;
			Difference 	myDifference 	= Difference.Identical;
			
			/* Test the standard fields */
			if ((!theKeyType.equals(myValues.theKeyType)) ||
				(Utils.differs(theSecuredKeyDef,   myValues.theSecuredKeyDef).isDifferent()))
				myDifference = Difference.Different;
			
			/* Test underlying item differences */
			if (myDifference.isIdentical()) 
				myDifference = ControlKey.differs(theControlKey, myValues.theControlKey);
			
			/* Return the differences */
			return myDifference;
		}
		
		/* Copy values */
		public HistoryValues<DataKey> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			Values myValues 	= (Values)pSource;
			theControl			= myValues.getSecurityControl();
			theControlKey		= myValues.getControlKey();
			theSecuredKeyDef	= myValues.getSecuredKeyDef();
			theKeyType			= myValues.getKeyType();
			theKeyTypeId		= myValues.getKeyTypeId();
			theControlId		= myValues.getControlId();
			theCipher			= myValues.getCipher();
			theKey				= myValues.getKey();
		}
		public Difference	fieldChanged(int fieldNo, HistoryValues<DataKey> pOriginal) {
			Values 	pValues = (Values)pOriginal;
			Difference	bResult = Difference.Identical;
			switch (fieldNo) {
				case FIELD_CONTROL:
					bResult = (ControlKey.differs(theControlKey, pValues.theControlKey));
					break;
				case FIELD_KEYTYPE:
					bResult = (theKeyType != pValues.theKeyType) ? Difference.Different
							 									 : Difference.Identical;
					break;
				case FIELD_KEY:
					bResult = (Utils.differs(theSecuredKeyDef,	pValues.theSecuredKeyDef));
					break;
			}
			return bResult;
		}
	}	
}
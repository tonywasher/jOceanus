package uk.co.tolcroft.models.data;

import java.security.SecureRandom;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import uk.co.tolcroft.models.Difference;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.help.DebugDetail;
import uk.co.tolcroft.models.security.AsymmetricKey;
import uk.co.tolcroft.models.security.CipherSet;
import uk.co.tolcroft.models.security.PasswordHash;
import uk.co.tolcroft.models.security.SecureManager;
import uk.co.tolcroft.models.security.SecurityControl;
import uk.co.tolcroft.models.security.SecurityMode;
import uk.co.tolcroft.models.security.SecuritySignature;
import uk.co.tolcroft.models.security.SymmetricKey.SymKeyType;

public class ControlKey extends DataItem<ControlKey> {
	/**
	 * The name of the object
	 */
	public static final String objName = "ControlKey";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";

	/**
	 * PasswordHash Length
	 */
	public final static int HASHLEN 	= PasswordHash.HASHSIZE;

	/**
	 * PublicKey Length
	 */
	public final static int PUBLICLEN 	= AsymmetricKey.PUBLICSIZE;

	/**
	 * PrivateKey Length
	 */
	public final static int PRIVATELEN 	= AsymmetricKey.PRIVATESIZE;

	/**
	 * The DataKey Map
	 */
	private Map<SymKeyType, DataKey>	theMap			= null;

	/**
	 * The Encryption CipherSet
	 */
	private CipherSet					theCipherSet	= null;
	
	/* Access methods */
	public  SecurityControl	getSecurityControl()	{ return getValues().getSecurityControl(); }
	public  byte[] 			getPublicKey()  		{ return getValues().getPublicKey(); }
	public  byte[] 			getPrivateKey()  		{ return getValues().getPrivateKey(); }
	public  byte[] 			getPasswordHash()  		{ return getValues().getPasswordHash(); }
	public  SecurityMode	getKeyMode()  			{ return getValues().getKeyMode(); }
	private SecureRandom	getRandom()  			{ return getValues().getRandom(); }

	/* Linking methods */
	public ControlKey	getBase() 	{ return (ControlKey)super.getBase(); }
	public Values  		getValues() { return (Values)super.getCurrentValues(); }	
	
	/* Field IDs */
	public static final int FIELD_PUBLICKEY	   	= DataItem.NUMFIELDS;
	public static final int FIELD_PRIVATEKEY   	= DataItem.NUMFIELDS+1;
	public static final int FIELD_PASSHASH		= DataItem.NUMFIELDS+2;
	public static final int FIELD_KEYMODE  		= DataItem.NUMFIELDS+3;
	public static final int NUMFIELDS	   		= DataItem.NUMFIELDS+4; 

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
			case FIELD_PUBLICKEY:	return "PublicKey";
			case FIELD_PRIVATEKEY:	return "PrivateKey";
			case FIELD_PASSHASH:	return "PasswordHash";
			case FIELD_KEYMODE:		return "KeyMode";
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
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<ControlKey> pValues) {
		Values	myValues = (Values)pValues;
		String 	myString = "";
		switch (iField) {
			case FIELD_PUBLICKEY:
				myString += Utils.HexStringFromBytes(myValues.getPublicKey()); 
				break;
			case FIELD_PRIVATEKEY:
				myString += Utils.HexStringFromBytes(myValues.getPrivateKey()); 
				break;
			case FIELD_PASSHASH:
				myString += Utils.HexStringFromBytes(myValues.getPasswordHash()); 
				break;
			case FIELD_KEYMODE:
				myString += (myValues.getKeyMode() == null) ? ("Id=" + myValues.theMode) : 
													 		  myValues.getKeyMode().toString(); 
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
	protected HistoryValues<ControlKey> getNewValues() { return new Values(); }
	
	/**
 	 * Construct a copy of a ControlKey
 	 * @param pKey The Key to copy 
 	 */
	protected ControlKey(List pList, ControlKey pKey) {
		/* Set standard values */
		super(pList, pKey.getId());
		Values myValues = getValues();
		
		/* Build values from source key */
		myValues.copyFrom(pKey.getValues());

		/* Switch on the LinkStyle */
		switch (getStyle()) {
			case CLONE:
				theMap 			= new EnumMap<SymKeyType,DataKey>(SymKeyType.class);
				theCipherSet 	= new CipherSet(getRandom(), getKeyMode());				
			case COPY:
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
	 * @param uKeyMode the mode of the Key
	 * @param pPasswordHash the passwordHash
	 * @param pPublicKey the public KeyDef
	 * @param pPrivateKey the encrypted private KeyDef
	 */
	private ControlKey(List     pList,
				   	   int		uId,
				   	   int		uKeyMode,
				   	   byte[]	pPasswordHash,
				   	   byte[]	pPublicKey,
				   	   byte[]	pPrivateKey) throws ModelException {
		/* Initialise the item */
		super(pList, uId);
		Values myValues = getValues();

		/* Record the IDs */
		myValues.setMode(uKeyMode);

		/* Store the details */
		myValues.setPublicKey(pPublicKey);
		myValues.setPrivateKey(pPrivateKey);
		myValues.setPasswordHash(pPasswordHash);

		/* Determine the SecurityMode */
		try { myValues.setKeyMode(new SecurityMode(uKeyMode)); }
		catch (ModelException e) {
			throw new ModelException(ExceptionClass.DATA,
								this,
	            				"Invalid KeyMode " + uKeyMode);
		}
		
		/* Access the Security manager */
		DataSet<?>			myData 		= pList.getData();
		SecureManager 		mySecurity 	= myData.getSecurity();
		SecuritySignature	mySignature	= new SecuritySignature(pPasswordHash,
																getKeyMode(),
																pPublicKey,
																pPrivateKey);
		
		/* Obtain the required security control */
		myValues.setSecurityControl(mySecurity.getSecurityControl(mySignature, "Database"));
		
		/* Create the DataKey Map */
		theMap = new EnumMap<SymKeyType,DataKey>(SymKeyType.class);
		
		/* Create the CipherSet */
		theCipherSet = new CipherSet(getRandom(), getKeyMode());
		
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/**
	 * Constructor for a new ControlKey. This will create a set of DataKeys 
	 * @param pList the list to which to add the key to 
	 */
	private ControlKey(List 	pList) throws ModelException {
		/* Initialise the item */
		super(pList, 0);
		Values myValues = getValues();
				
		/* Access the Security manager */
		DataSet<?>		myData 		= pList.getData();
		SecureManager 	mySecurity 	= myData.getSecurity();
		
		/* Obtain the required security control */
		myValues.setSecurityControl(mySecurity.getSecurityControl(null, "Database"));
		
		/* Access and store the signature */
		SecuritySignature mySign = getSecurityControl().getSignature();
		myValues.setPublicKey(mySign.getPublicKey());
		myValues.setPrivateKey(mySign.getSecuredKeyDef());
		myValues.setPasswordHash(mySign.getPasswordHash());
		myValues.setKeyMode(mySign.getKeyMode());
		
		/* Create the DataKey Map */
		theMap = new EnumMap<SymKeyType,DataKey>(SymKeyType.class);
		
		/* Create the CipherSet */
		theCipherSet = new CipherSet(getRandom(), getKeyMode());
		
		/* Allocate the id */
		pList.setNewId(this);
				
		/* Allocate the DataKeys */
		allocateDataKeys(pList.getData());
	}

	/**
	 * Constructor for a new ControlKey with the same password. This will create a set of DataKeys 
	 * @param pList the list to which to add the key to 
	 */
	private ControlKey(ControlKey 	pKey) throws ModelException {
		/* Initialise the item */
		super(pKey.getList(), 0);
		Values myValues = getValues();
				
		/* Access the Security manager */
		List			myList		= (List)pKey.getList();
		DataSet<?>		myData 		= myList.getData();
		SecureManager 	mySecure	= myData.getSecurity();
		
		/* Create a clone of the security control */
		SecurityControl myControl	= mySecure.cloneSecurityControl(myData.getSecurityControl());
			
		/* Obtain the required security control */
		myValues.setSecurityControl(myControl);
		
		/* Access and store the signature */
		SecuritySignature mySign = myControl.getSignature();
		myValues.setPublicKey(mySign.getPublicKey());
		myValues.setPrivateKey(mySign.getSecuredKeyDef());
		myValues.setPasswordHash(mySign.getPasswordHash());
		myValues.setKeyMode(mySign.getKeyMode());
		
		/* Create the DataKey Map */
		theMap = new EnumMap<SymKeyType,DataKey>(SymKeyType.class);
		
		/* Create the CipherSet */
		theCipherSet = new CipherSet(getRandom(), getKeyMode());
		
		/* Allocate the id */
		myList.setNewId(this);
				
		/* Allocate the DataKeys */
		allocateDataKeys(myData);
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
		ControlKey myThat = (ControlKey)pThat;
		
		/* Check for equality */
		if (getId() != myThat.getId()) 		return false;
		
		/* Compare the changeable values */
		return getValues().histEquals(myThat.getValues()).isIdentical();
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
	private void allocateDataKeys(DataSet<?> pData) throws ModelException {
		/* Access the DataKey List */
		DataKey.List myKeys = pData.getDataKeys();
		
		/* Loop through the SymKeyType values */
		for (SymKeyType myType: SymKeyType.values()) {
			/* Create a new DataKey for this ControlKey */
			DataKey myKey = myKeys.addItem(this, myType);
			
			/* Store the DataKey into the map */
			theMap.put(myType, myKey);
			
			/* Declare the Cipher */
			theCipherSet.addCipher(myKey.getCipher());
		}
	}
	
	/**
	 * Delete the old set of ControlKey and DataKeys 
	 * @param pData the DataSet
	 */
	private void deleteControlSet() {
		/* Loop through the SymKeyType values */
		for (SymKeyType myType: SymKeyType.values()) {
			/* Access the Data Key */
			DataKey myKey = theMap.get(myType);
			
			/* Mark as deleted */
			if (myKey != null) myKey.setState(DataState.DELETED);
		}
		
		/* Mark this control key as deleted */
		setState(DataState.DELETED);
	}
	
	/**
	 * Update security control 
	 * @param pControl the new security control
	 */
	protected void updateSecurityControl(SecurityControl pControl) throws ModelException {
		/* Store the current detail into history */
		pushHistory();

		/* Update the Security Control Key */
		Values 				myValues   	= getValues();
		SecuritySignature 	mySign 		= pControl.getSignature(); 
		myValues.setSecurityControl(pControl);
		myValues.setPublicKey(mySign.getPublicKey());
		myValues.setPrivateKey(mySign.getSecuredKeyDef());
		myValues.setPasswordHash(mySign.getPasswordHash());
		myValues.setKeyMode(mySign.getKeyMode());
		
		/* Loop through the SymKeyType values */
		for (SymKeyType myType: SymKeyType.values()) {
			/* Access the Data Key */
			DataKey myKey = theMap.get(myType);
			
			/* Update the Security Control */
			if (myKey != null) myKey.updateSecurityControl();
		}
		
		/* Check for changes */
		if (checkForHistory()) setState(DataState.CHANGED);
	}
	
	/**
	 * Register DataKey 
	 * @param pKey the DataKey to register
	 */
	protected void registerDataKey(DataKey pKey) {
		/* Store the DataKey into the map */
		theMap.put(pKey.getKeyType(), pKey);
		
		/* Declare the Cipher */
		theCipherSet.addCipher(pKey.getCipher());
	}
	
	/**
	 * Encrypt item 
	 * @param pBytes the bytes to encrypt
	 * @return the encrypted bytes
	 */
	protected byte[] encryptBytes(byte[] pBytes) throws ModelException {
		/* encrypt using the CipherSet */
		return theCipherSet.encryptBytes(pBytes);
	}

	/**
	 * Decrypt item 
	 * @param pBytes the bytes to decrypt
	 * @return the decrypted bytes
	 */
	protected byte[] decryptBytes(byte[] pBytes) throws ModelException {
		/* Decrypt using Cipher Set */
		return theCipherSet.decryptBytes(pBytes);
	}
	
	/**
	 * Determine whether two {@link ControlKey} objects differ.
	 * @param pCurr The current ControlKey 
	 * @param pNew The new ControlKey
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static Difference differs(ControlKey pCurr, ControlKey pNew) {
		/* Handle case where current value is null */
		if  (pCurr == null) return (pNew != null) ? Difference.Different 
												  : Difference.Identical;
		
		/* Handle case where new value is null */
		if  (pNew == null) return Difference.Different;
		
		/* Handle Standard cases */
		return (pCurr.compareTo(pNew) != 0) ? Difference.Different
											: Difference.Identical;
	}
	
	/**
	 * ControlKey List
	 */
	public static class List  extends DataList<List, ControlKey> {
		/* Members */
		private DataSet<?>	theData		= null;
		public 	DataSet<?> 	getData()	{ return theData; }

		/** 
		 * Construct an empty CORE ControlKey list
	 	 * @param pData the DataSet for the list
		 */
		protected List(DataSet<?> pData) { 
			super(List.class, ControlKey.class, ListStyle.CORE, false);
			theData = pData;
		}

		/** 
		 * Construct an empty generic ControlKey list
	 	 * @param pData the DataSet for the list
		 * @param pStyle the style of the list 
		 */
		protected List(DataSet<?> pData, ListStyle pStyle) {
			super(List.class, ControlKey.class, pStyle, false);
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
		public ControlKey addNewItem(DataItem<?> pItem) { 
			ControlKey myKey = new ControlKey(this, (ControlKey)pItem);
			add(myKey);
			return myKey; 
		}

		/**
		 * Add a new item to the edit list
		 * @return the newly added item
		 */
		public ControlKey addNewItem() { return null; }

		/**
		 * 	Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }

		/**
		 *  Add a ControlKey item from a Database/Backup
		 * @param uId the id of the ControlKey
		 * @param uKeyTypeId the id of the KeyType
		 * @param pPasswordHash the passwordHash
		 * @param pPublicKey the public KeyDef
		 * @param pPrivateKey the encrypted private KeyDef
		 */
		public ControlKey addItem(int  		uId,
			   	  				  int		uKeyTypeId,
			   	  				  byte[]	pPasswordHash,
			   	  				  byte[]	pPublicKey,
			   	  				  byte[]	pPrivateKey) throws ModelException {
			ControlKey     	myKey;
			
			/* Create the ControlKey */
			myKey = new ControlKey(this, 
								   uId,
								   uKeyTypeId,
								   pPasswordHash,
								   pPublicKey,
							       pPrivateKey);
			
			/* Check that this KeyId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new ModelException(ExceptionClass.DATA,
									myKey,
									"Duplicate ControlKeyId (" + uId + ")");
			 
			/* Add to the list */
			add(myKey);
			return myKey;
		}			

		/**
		 *  Add a new ControlKey (with associated DataKeys)
		 */
		public ControlKey addItem() throws ModelException {
			ControlKey     	myKey;
			
			/* Create the key */
			myKey = new ControlKey(this);
			
			/* Add to the list */
			add(myKey);
			return myKey;
		}			

		/**
		 *  Add a cloned ControlKey (with associated DataKeys)
		 */
		public ControlKey addItem(ControlKey pSource) throws ModelException {
			ControlKey     	myKey;
			
			/* Check that we are the same list */
			if (pSource.getList() != this)
				throw new ModelException(ExceptionClass.LOGIC,
									"Invalid clone operation");
			
			/* Create the key */
			myKey = new ControlKey(pSource);
			
			/* Add to the list */
			add(myKey);
			return myKey;
		}			

		/**
		 * Initialise Security from a DataBase for a SpreadSheet load
		 * @param pDatabase the DataSet for the Database
		 */
		protected void initialiseSecurity(DataSet<?> pDatabase) throws ModelException {			
			/* Access the active control key from the database */
			ControlKey  myDatabaseKey	= pDatabase.getControlKey();
			ControlKey 	myKey;

			/* If we have an existing security key */
			if (myDatabaseKey != null) {
				/* Clone the Control Key and its DataKeys */
				myKey = cloneControlKey(myDatabaseKey);
			}
			
			/* else create a new security set */
			else {
				/* Create the new security set */
				myKey = addItem();
			}
			
			/* Declare the Control Key */
			theData.getControl().setControlKey(myKey);
		}
		
		/**
		 * Delete old controlKeys 
		 */
		protected void purgeOldControlKeys() {
			/* Access the current control Key */
			ControlKey myKey = theData.getControlKey();
			
			/* Loop through the controlKeys */
			Iterator<ControlKey> myIterator = iterator();
			ControlKey myCurr;
			while ((myCurr = myIterator.next()) != null) { 
				/* Delete if this is not the active key */
				if (!myKey.equals(myCurr)) myCurr.deleteControlSet(); 
			}
		}
		
		/**
		 * Clone Security from a DataBase 
		 * @param pControlKey the ControlKey to clone
		 */
		private ControlKey cloneControlKey(ControlKey pControlKey) throws ModelException {
			/* Clone the control key */
			ControlKey myControl = addItem(pControlKey.getId(),
										   pControlKey.getKeyMode().getMode(),
										   pControlKey.getPasswordHash(),
										   pControlKey.getPublicKey(),
										   pControlKey.getPrivateKey());
			
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
				
				/* Declare the Cipher */
				myControl.theCipherSet.addCipher(myKey.getCipher());
			}
			
			/* return the cloned key */
			return myControl;
		}
	}

	/**
	 * Values for a ControlKey 
	 */
	public class Values extends HistoryValues<ControlKey> {
		private byte[]			thePublicKey	= null;
		private byte[]			thePrivateKey	= null;
		private byte[]			thePasswordHash	= null;
		private SecurityMode	theKeyMode		= null;
		private int				theMode			= -1;
		private SecurityControl	theControl		= null;
		
		/* Access methods */
		public  byte[] 			getPublicKey()  		{ return thePublicKey; }
		public  byte[] 			getPrivateKey()  		{ return thePrivateKey; }
		public  byte[] 			getPasswordHash()  		{ return thePasswordHash; }
		public  int				getMode()  				{ return theMode; }
		public  SecurityMode	getKeyMode()  			{ return theKeyMode; }
		public  SecurityControl	getSecurityControl()	{ return theControl; }
		private SecureRandom	getRandom()				{ return (theControl == null) ? null : theControl.getRandom(); }
		
		private void setPublicKey(byte[] pValue) {
			thePublicKey 	= pValue; }
		private void setPrivateKey(byte[] pValue) {
			thePrivateKey 	= pValue; }
		private void setPasswordHash(byte[] pValue) {
			thePasswordHash = pValue; }
		private void setKeyMode(SecurityMode pValue) {
			theKeyMode 		= pValue;  
			theMode			= (theKeyMode == null) ? -1 : theKeyMode.getMode(); }
		private void setMode(int pValue) {
			theMode			= pValue; }
		private void setSecurityControl(SecurityControl pControl) {
			theControl 		= pControl; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) { copyFrom(pValues); }
		
		/* Check whether this object is equal to that passed */
		public Difference histEquals(HistoryValues<ControlKey> pCompare) {
			/* Make sure that the object is the same class */
			if (pCompare.getClass() != this.getClass()) return Difference.Different;
			
			/* Cast correctly */
			Values 		myValues 		= (Values)pCompare;
			Difference 	myDifference 	= Difference.Identical;
			
			/* Test byte array differences */
			if ((Utils.differs(thePublicKey,		myValues.thePublicKey).isDifferent())	 ||
				(Utils.differs(thePrivateKey,		myValues.thePrivateKey).isDifferent())   || 
				(Utils.differs(thePasswordHash,		myValues.thePasswordHash).isDifferent()) ||
				(SecurityMode.differs(theKeyMode,	myValues.theKeyMode).isDifferent()))
				myDifference = Difference.Different;
				
			/* Return difference */
			return myDifference;
		}
		
		/* Copy values */
		public HistoryValues<ControlKey> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			Values myValues = (Values)pSource;
			theControl		= myValues.getSecurityControl();
			thePublicKey	= myValues.getPublicKey();
			thePrivateKey	= myValues.getPrivateKey();
			thePasswordHash	= myValues.getPasswordHash();
			theMode			= myValues.getMode();
			theKeyMode		= myValues.getKeyMode();
		}
		public Difference	fieldChanged(int fieldNo, HistoryValues<ControlKey> pOriginal) {
			Values 	pValues = (Values)pOriginal;
			Difference	bResult = Difference.Identical;
			switch (fieldNo) {
				case FIELD_PUBLICKEY:
					bResult = (Utils.differs(thePublicKey,		pValues.thePublicKey));
					break;
				case FIELD_PRIVATEKEY:
					bResult = (Utils.differs(thePrivateKey,		pValues.thePrivateKey));
					break;
				case FIELD_PASSHASH:
					bResult = (Utils.differs(thePasswordHash,	pValues.thePasswordHash));
					break;
				case FIELD_KEYMODE:
					bResult = (SecurityMode.differs(theKeyMode, pValues.theKeyMode));
					break;
			}
			return bResult;
		}
	}
}

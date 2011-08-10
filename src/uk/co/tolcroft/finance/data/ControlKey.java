package uk.co.tolcroft.finance.data;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import uk.co.tolcroft.models.DataItem;
import uk.co.tolcroft.models.DataList;
import uk.co.tolcroft.models.DataState;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.HistoryValues;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.security.AsymmetricKey;
import uk.co.tolcroft.security.AsymmetricKey.AsymKeyType;
import uk.co.tolcroft.security.PasswordKey;
import uk.co.tolcroft.security.SecureManager;
import uk.co.tolcroft.security.SecurityControl;
import uk.co.tolcroft.security.SecuritySignature;
import uk.co.tolcroft.security.SymmetricKey;
import uk.co.tolcroft.security.SymmetricKey.SymKeyType;

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
	 * Number of Encryption steps 
	 */
	public final static int NUMSTEPS	= 3;

	/**
	 * PasswordHash Length
	 */
	public final static int HASHLEN 	= PasswordKey.HASHSIZE;

	/**
	 * PublicKey Length
	 */
	public final static int PUBLICLEN 	= AsymmetricKey.PUBLICSIZE;

	/**
	 * PrivateKey Length
	 */
	public final static int PRIVATELEN 	= AsymmetricKey.PRIVATESIZE;

	/**
	 * Control Key Length
	 */
	public final static int KEYIDLEN 	= numKeyBytes(NUMSTEPS);

	/**
	 * The DataKey Map
	 */
	private Map<SymKeyType, DataKey>	theMap	= null;
	
	/* Local values */
	private SecurityControl	theControl		= null;
	private SecureRandom	theRandom		= null;
	private int				theKeyTypeId	= -1;
	
	/* Access methods */
	public  SecurityControl	getSecurityControl()	{ return theControl; }
	public  byte[] 			getPublicKey()  		{ return getValues().getPublicKey(); }
	public  byte[] 			getPrivateKey()  		{ return getValues().getPrivateKey(); }
	public  byte[] 			getPasswordHash()  		{ return getValues().getPasswordHash(); }
	public  AsymKeyType		getKeyType()  			{ return getValues().getKeyType(); }

	/* Linking methods */
	public ControlKey	getBase() 	{ return (ControlKey)super.getBase(); }
	public Values  		getValues() { return (Values)super.getCurrentValues(); }	
	
	/* Field IDs */
	public static final int FIELD_PUBLICKEY	   	= DataItem.NUMFIELDS;
	public static final int FIELD_PRIVATEKEY   	= DataItem.NUMFIELDS+1;
	public static final int FIELD_PASSHASH		= DataItem.NUMFIELDS+2;
	public static final int FIELD_KEYTYPE  		= DataItem.NUMFIELDS+3;
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
			case FIELD_KEYTYPE:		return "KeyType";
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
	public String formatField(int iField, HistoryValues<ControlKey> pValues) {
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
			case FIELD_KEYTYPE:
				myString += (getKeyType() == null) ? ("Id=" + theKeyTypeId) : getKeyType().toString(); 
				break;
			default: 		
				myString += super.formatField(iField, pValues); 
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
		Values myValues = new Values(pKey.getValues());
		setValues(myValues);
		theControl		= pKey.getSecurityControl();
		theRandom		= theControl.getRandom();

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
	 * @param uKeyTypeId the id of the KeyType
	 * @param pPasswordHash the passwordHash
	 * @param pPublicKey the public KeyDef
	 * @param pPrivateKey the encrypted private KeyDef
	 */
	public ControlKey(List     	pList,
				   	  int		uId,
				   	  int		uKeyTypeId,
				   	  byte[]	pPasswordHash,
				   	  byte[]	pPublicKey,
				   	  byte[]	pPrivateKey) throws Exception {
		/* Initialise the item */
		super(pList, uId);
		Values myValues = new Values();
		setValues(myValues);

		/* Record the IDs */
		theKeyTypeId	= uKeyTypeId;

		/* Store the details */
		myValues.setPublicKey(pPublicKey);
		myValues.setPrivateKey(pPrivateKey);
		myValues.setPasswordHash(pPasswordHash);

		/* Determine the AsymKeyType */
		try { myValues.setKeyType(AsymKeyType.fromId(uKeyTypeId)); }
		catch (Exception e) {
			throw new Exception(ExceptionClass.DATA,
								this,
	            				"Invalid KeyType Id " + uKeyTypeId);
		}
		
		/* Access the Security manager */
		DataSet 			myData 		= pList.getData();
		SecureManager 		mySecurity 	= myData.getSecurity();
		SecuritySignature	mySignature	= new SecuritySignature(pPasswordHash,
																getKeyType(),
																pPublicKey,
																pPrivateKey);
		
		/* Obtain the required security control */
		theControl = mySecurity.getSecurityControl(mySignature, "Database");
		theRandom  = theControl.getRandom();
		
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
		Values myValues = new Values();
		setValues(myValues);
				
		/* Access the Security manager */
		DataSet 		myData 		= pList.getData();
		SecureManager 	mySecurity 	= myData.getSecurity();
		
		/* Obtain the required security control */
		theControl = mySecurity.getSecurityControl(null, "Database");
		theRandom  = theControl.getRandom();
		
		/* Access and store the signature */
		SecuritySignature mySign = theControl.getSignature();
		myValues.setPublicKey(mySign.getEncodedPublicKey());
		myValues.setPrivateKey(mySign.getSecuredKeyDef());
		myValues.setPasswordHash(mySign.getPasswordHash());
		myValues.setKeyType(mySign.getKeyType());
		
		/* Create the DataKey Map */
		theMap = new EnumMap<SymKeyType,DataKey>(SymKeyType.class);
		
		/* Allocate the id */
		pList.setNewId(this);
				
		/* Allocate the DataKeys */
		allocateDataKeys(pList.getData());
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
		return getValues().histEquals(myThat.getValues());
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
	 */
	protected void updateSecurityControl() throws Exception {
		/* Store the current detail into history */
		pushHistory();

		/* Update the Security Control Key */
		Values 				myValues   	= getValues();
		SecuritySignature 	mySign 		= theControl.getSignature(); 
		myValues.setPublicKey(mySign.getEncodedPublicKey());
		myValues.setPrivateKey(mySign.getSecuredKeyDef());
		myValues.setPasswordHash(mySign.getPasswordHash());
		myValues.setKeyType(mySign.getKeyType());
		
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
	protected void registerDataKey(DataKey pKey) throws Exception {
		/* Store the DataKey into the map */
		theMap.put(pKey.getKeyType(), pKey);
	}
	
	/**
	 * Encrypt item 
	 * @param pBytes the bytes to encrypt
	 * @return the encrypted bytes
	 */
	protected byte[] encryptBytes(byte[] pBytes) throws Exception {
		/* Allocate a new initialisation vector */
		byte[] myVector = new byte[SymmetricKey.IVSIZE];
		theRandom.nextBytes(myVector);
		
		/* Determine the SymKeyTypes to use */
		SymKeyType[] myKeyTypes = SymKeyType.getRandomTypes(NUMSTEPS, theRandom);
		
		/* Encode the array */
		byte[] myKeyBytes = encodeSymKeyTypes(myKeyTypes);
		
		/* Loop through the SymKeyTypes */
		for (int i=0; i < myKeyTypes.length; i++) {
			/* Access the DataKey */
			DataKey myKey = theMap.get(myKeyTypes[i]);
			
			/* Encrypt the bytes */
			pBytes = myKey.getCipher().encryptBytes(pBytes, myVector);
		}
		
		/* Allocate the bytes */
		byte[] myEncrypt = new byte[SymmetricKey.IVSIZE + myKeyBytes.length + pBytes.length];
		System.arraycopy(myVector, 0, myEncrypt, 0, SymmetricKey.IVSIZE);
		System.arraycopy(myKeyBytes, 0, myEncrypt, SymmetricKey.IVSIZE, myKeyBytes.length);
		System.arraycopy(pBytes, 0, myEncrypt, SymmetricKey.IVSIZE+myKeyBytes.length,  pBytes.length);
		
		/* Return the encrypted bytes */
		return myEncrypt;
	}

	/**
	 * Determine length of bytes to encode the number of keys
	 * @param pNumKeys the number of keys
	 * @return the number of bytes 
	 */
	private static int numKeyBytes(int pNumKeys) {
		/* Determine the number of bytes */
		return 1 + (pNumKeys/2);
	}
	
	/**
	 * Encode SymKeyTypes
	 * @param pTypes the types to encode
	 * @return the encoded types
	 */
	private byte[] encodeSymKeyTypes(SymKeyType[] pTypes) throws Exception {
		/* Determine the number of bytes */
		int myNumBytes = numKeyBytes(pTypes.length);
		
		/* Allocate the bytes */
		byte[] myBytes = new byte[myNumBytes];
		Arrays.fill(myBytes, (byte)0);
		
		/* Loop through the keys */
		for (int i=0, j=0; i<pTypes.length; i++) {
			/* Access the id of the Symmetric Key */
			int myId = pTypes[i].getId();
			
			/* Access the id */
			if ((i % 2) == 1) { myId *= 16; j++; }
			myBytes[j] |= myId;
		}
		
		/* Encode the number of keys */
		myBytes[0] |= (16*pTypes.length);
		
		/* Return the bytes */
		return myBytes;
	}
	
	/**
	 * Decode SymKeyTypes
	 * @param pBytes the encrypted bytes
	 * @return the array of SymKeyTypes
	 */
	private SymKeyType[] decodeSymKeyTypes(byte[] pBytes) throws Exception {
		/* Extract the number of SymKeys */
		int myNumKeys = pBytes[SymmetricKey.IVSIZE] / 16;
		
		/* Allocate the array */
		SymKeyType[] myTypes = new SymKeyType[myNumKeys];
		
		/* Loop through the keys */
		for (int i=0, j=SymmetricKey.IVSIZE; i<myNumKeys; i++) {
			/* Access the id of the Symmetric Key */
			int myId = pBytes[j];
			
			/* Isolate the id */
			if ((i % 2) == 1)  myId /= 16; else j++;
			myId &= 15;
			
			/* Determine the SymKeyType */
			myTypes[i] = SymKeyType.fromId(myId);
		}
		
		/* Return the array */
		return myTypes;
	}
	
	/**
	 * Decrypt item 
	 * @param pBytes the bytes to decrypt
	 * @return the decrypted bytes
	 */
	protected byte[] decryptBytes(byte[] pBytes) throws Exception {
		/* Split the bytes into the separate parts */
		byte[] 			myVector = Arrays.copyOf(pBytes, SymmetricKey.IVSIZE);
		SymKeyType[]	myTypes	 = decodeSymKeyTypes(pBytes);	
		byte[] 			myBytes  = Arrays.copyOfRange(pBytes, 
													  SymmetricKey.IVSIZE+numKeyBytes(myTypes.length), 
													  pBytes.length);
		
		/* Loop through the SymKeyTypes */
		for (int i=myTypes.length-1; i >= 0; i--) {
			/* Access the DataKey */
			DataKey myKey = theMap.get(myTypes[i]);
			
			/* Encrypt the bytes */
			myBytes = myKey.getCipher().decryptBytes(myBytes, myVector);
		}
		
		/* Return the decrypted bytes */
		return myBytes;
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
	 * ControlKey List
	 */
	public static class List  extends DataList<ControlKey> {
		/* Members */
		private DataSet		theData		= null;
		public 	DataSet 	getData()	{ return theData; }

		/** 
		 * Construct an empty CORE ControlKey list
	 	 * @param pData the DataSet for the list
		 */
		protected List(DataSet pData) { 
			super(ControlKey.class, ListStyle.CORE, false);
			theData = pData;
		}

		/** 
		 * Construct an empty generic ControlKey list
	 	 * @param pData the DataSet for the list
		 * @param pStyle the style of the list 
		 */
		protected List(DataSet pData, ListStyle pStyle) {
			super(ControlKey.class, pStyle, false);
			theData = pData;
		}

		/** 
		 * Construct a generic ControlKey list
		 * @param pList the source ControlKey list 
		 * @param pStyle the style of the list 
		 */
		public List(List pList, ListStyle pStyle) { 
			super(ControlKey.class, pList, pStyle);
			theData = pList.theData;
		}

		/** 
		 * Construct a difference ControlKey list
		 * @param pNew the new ControlKey list 
		 * @param pOld the old ControlKey list 
		 */
		protected List(List pNew, List pOld) { 
			super(pNew, pOld);
			theData = pNew.theData;
		}

		/** 
		 * 	Clone a ControlKey list
		 * @return the cloned list
		 */
		protected List cloneIt() {return new List(this, ListStyle.CORE); }

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
			   	  				  byte[]	pPrivateKey) throws Exception {
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
				throw new Exception(ExceptionClass.DATA,
									myKey,
									"Duplicate ControlKeyId <" + uId + ">");
			 
			/* Add to the list */
			add(myKey);
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
			add(myKey);
			return myKey;
		}			

		/**
		 * Initialise Security from a DataBase for a SpreadSheet load
		 * @param pDatabase the DataSet for the Database
		 */
		protected void initialiseSecurity(DataSet pDatabase) throws Exception {			
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
		private ControlKey cloneControlKey(ControlKey pControlKey) throws Exception {
			/* Clone the control key */
			ControlKey myControl = addItem(pControlKey.getId(),
										   pControlKey.getKeyType().getId(),
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
			}
			
			/* return the cloned key */
			return myControl;
		}
	}

	/**
	 * Values for a ControlKey 
	 */
	public class Values implements HistoryValues<ControlKey> {
		private byte[]			thePublicKey	= null;
		private byte[]			thePrivateKey	= null;
		private byte[]			thePasswordHash	= null;
		private AsymKeyType		theKeyType		= null;
		
		/* Access methods */
		public  byte[] 			getPublicKey()  	{ return thePublicKey; }
		public  byte[] 			getPrivateKey()  	{ return thePrivateKey; }
		public  byte[] 			getPasswordHash()  	{ return thePasswordHash; }
		public  AsymKeyType		getKeyType()  		{ return theKeyType; }
		
		public void setPublicKey(byte[] pValue) {
			thePublicKey 	= pValue; }
		public void setPrivateKey(byte[] pValue) {
			thePrivateKey 	= pValue; }
		public void setPasswordHash(byte[] pValue) {
			thePasswordHash = pValue; }
		public void setKeyType(AsymKeyType pValue) {
			theKeyType 		= pValue; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) { copyFrom(pValues); }
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(HistoryValues<ControlKey> pCompare) {
			Values myValues = (Values)pCompare;
			if (theKeyType != myValues.theKeyType)   						return false;
			if (Utils.differs(thePublicKey,		myValues.thePublicKey))   	return false;
			if (Utils.differs(thePrivateKey,	myValues.thePrivateKey))   	return false;
			if (Utils.differs(thePasswordHash,	myValues.thePasswordHash))  return false;
			return true;
		}
		
		/* Copy values */
		public HistoryValues<ControlKey> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			Values myValues = (Values)pSource;
			thePublicKey	= myValues.getPublicKey();
			thePrivateKey	= myValues.getPrivateKey();
			thePasswordHash	= myValues.getPasswordHash();
			theKeyType		= myValues.getKeyType();
		}
		public boolean	fieldChanged(int fieldNo, HistoryValues<ControlKey> pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean	bResult = false;
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
				case FIELD_KEYTYPE:
					bResult = (theKeyType != pValues.theKeyType);
					break;
			}
			return bResult;
		}
	}
}

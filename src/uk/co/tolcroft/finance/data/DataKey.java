package uk.co.tolcroft.finance.data;

import java.security.SecureRandom;

import uk.co.tolcroft.models.DataItem;
import uk.co.tolcroft.models.DataList;
import uk.co.tolcroft.models.DataState;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.security.SecurityControl;
import uk.co.tolcroft.security.SymmetricKey;
import uk.co.tolcroft.security.SymmetricKey.SymKeyType;

public class DataKey extends DataItem {
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

	/**
	 * InitVector length
	 */
	public final static int INITVLEN 	= SymmetricKey.IVSIZE;

	/* Local values */
	private ControlKey		theControl		= null;
	private SecureRandom	theRandom		= null;
	private SymmetricKey	theKey			= null;
	private SymKeyType		theKeyType		= null;
	private int 			theControlId 	= -1;
	private int 			theKeyTypeId 	= -1;
	
	/* Access methods */
	public  byte[] 			getSecurityKey()  	{ return getObj().getSecurityKey(); }
	public  byte[] 			getInitVector()  	{ return getObj().getInitVector(); }
	public  SymKeyType		getKeyType()  		{ return theKeyType; }
	public  ControlKey		getControlKey()		{ return theControl; }
	protected SymmetricKey	getDataKey()		{ return theKey; }

	/* Linking methods */
	public DataKey	getBase() { return (DataKey)super.getBase(); }
	public Values  	getObj()  { return (Values)super.getObj(); }	
	
	/* Field IDs */
	public static final int FIELD_CONTROL  = 1;
	public static final int FIELD_KEYTYPE  = 2;
	public static final int FIELD_KEY	   = 3;
	public static final int FIELD_IV	   = 4;
	public static final int NUMFIELDS	   = 5; 

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
			case FIELD_ID:			return NAME_ID;
			case FIELD_CONTROL:		return "ControlId";
			case FIELD_KEYTYPE:		return "KeyType";
			case FIELD_KEY:			return "DataKey";
			case FIELD_IV:			return "InitVector";
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
			case FIELD_CONTROL: 		
				myString += theControlId; 
				break;
			case FIELD_KEYTYPE: 		
				myString += (theKeyType == null) ? ("Id=" + theKeyTypeId) : theKeyType.toString(); 
				break;
			case FIELD_KEY:
				myString += Utils.HexStringFromBytes(getSecurityKey()); 
				break;
			case FIELD_IV:
				myString += Utils.HexStringFromBytes(getInitVector()); 
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
		Values myObj = new Values(pKey.getObj());
		setObj(myObj);
		theControlId	= pKey.theControlId;
		theControl		= pKey.getControlKey();
		theKey			= pKey.getDataKey();
		theKeyType		= pKey.getKeyType();

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
	 * @param pInitVector the InitVector
 	 */
	public DataKey(List     	pList,
				   int			uId,
				   int			uControlId,
				   int			uKeyTypeId,
				   byte[]		pSecurityKey,
		  		   byte[]		pInitVector) throws Exception {
		/* Initialise the item */
		super(pList, uId);
		Values myObj = new Values();
		setObj(myObj);

		/* Record the IDs */
		theControlId	= uControlId;
		theKeyTypeId	= uKeyTypeId;

		/* Look up the ControlKey */
		theControl = pList.theData.getControlKeys().searchFor(uControlId);
		if (theControl == null) 
			throw new Exception(ExceptionClass.DATA,
		                        this,
					            "Invalid ControlKey Id");
		
		/* Determine the SymKeyType */
		try { theKeyType = SymKeyType.fromId(uKeyTypeId); }
		catch (Exception e) {
			throw new Exception(ExceptionClass.DATA,
								this,
	            				"Invalid KeyType Id");
		}
		
		/* Store the keys */
		myObj.setSecurityKey(pSecurityKey);
		myObj.setInitVector(pInitVector);

		/* Access the Security key */
		SecurityControl myControl = theControl.getSecurityControl();
		theRandom = myControl.getRandom();
		theKey	  = myControl.getSymmetricKey(pSecurityKey, theKeyType); 
		
		/* Register the DataKey */
		theControl.registerDataKey(this);
		
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
		Values myObj = new Values();
		setObj(myObj);
		
		/* Store the key type and ControlKey */
		theKeyType		= pKeyType;
		theControl		= pControlKey;
		theControlId	= pControlKey.getId();
		
		/* Create the new key */
		SecurityControl myControl = theControl.getSecurityControl();
		theRandom = myControl.getRandom();
		theKey	  = myControl.getSymmetricKey(pKeyType);
		
		/* Store its security key */
		myObj.setSecurityKey(theKey.getSecurityKey());			
	
		/* Create the initialisation vector */
		byte[] myInitVector = new byte[INITVLEN];
		theRandom.nextBytes(myInitVector);
		myObj.setInitVector(myInitVector);
		
		/* Register the DataKey */
		theControl.registerDataKey(this);
		
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
		Values myObj = new Values();
		setObj(myObj);
		
		/* Store the key type and ControlKey */
		theKeyType		= pDataKey.getKeyType();
		theControl		= pControlKey;
		theControlId	= pControlKey.getId();
		
		/* Create the new key */
		SecurityControl myControl = theControl.getSecurityControl();
		theRandom = myControl.getRandom();
		theKey	  = pDataKey.getDataKey();
		
		/* Store its security key */
		myObj.setSecurityKey(theKey.getSecurityKey());			
		myObj.setInitVector(pDataKey.getInitVector());
		
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
		DataKey myKey = (DataKey)pThat;
		
		/* Check for equality */
		if (getId() != myKey.getId()) 		return false;
		if (theKeyType != myKey.theKeyType) return false;
		if (ControlKey.differs(getControlKey(), myKey.getControlKey())) return false;
		if (Utils.differs(getSecurityKey(), myKey.getSecurityKey())) 	return false;
		if (Utils.differs(getInitVector(),  myKey.getInitVector()))  	return false;
		return true;
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
	 * DataKey List
	 */
	public static class List  extends DataList<DataKey> {
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
		public DataKey addNewItem(DataItem pItem) { 
			DataKey myKey = new DataKey(this, (DataKey)pItem);
			myKey.addToList();
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
		 * @param pInitVector the InitVector
	 	 */
		public DataKey addItem(int  	uId,
							   int		uControlId,
							   int		uKeyTypeId,
	            			   byte[]	pSecurityKey,
							   byte[]	pInitVector) throws Exception {
			DataKey     	myKey;
			
			/* Create the DataKey */
			myKey = new DataKey(this, 
								uId,
								uControlId,
								uKeyTypeId,
							    pSecurityKey,
								pInitVector);
			
			/* Check that this KeyId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
									myKey,
									"Duplicate DataKeyId <" + uId + ">");
			 
			/* Add to the list */
			myKey.addToList();
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
			myKey.addToList();
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
			myKey.addToList();
			return myKey;
		}			
	}

	/**
	 * Values for a static 
	 */
	public class Values implements histObject {
		private byte[]			theSecurityKey	= null;
		private byte[]			theInitVector	= null;
		
		/* Access methods */
		public  byte[] 			getSecurityKey()  	{ return theSecurityKey; }
		public  byte[] 			getInitVector()  	{ return theInitVector; }
		
		public void setSecurityKey(byte[] pValue) {
			theSecurityKey = pValue; }
		public void setInitVector(byte[] pValue) {
			theInitVector  = pValue; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theSecurityKey	= pValues.getSecurityKey();
			theInitVector	= pValues.getInitVector();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (Utils.differs(theSecurityKey,   pValues.theSecurityKey))   	return false;
			if (Utils.differs(theInitVector,	pValues.theInitVector))   	return false;
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
			theInitVector	= pValues.getInitVector();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_KEY:
					bResult = (Utils.differs(theSecurityKey,	pValues.theSecurityKey));
					break;
				case FIELD_IV:
					bResult = (Utils.differs(theInitVector, 	pValues.theInitVector));
					break;
			}
			return bResult;
		}
	}	
}

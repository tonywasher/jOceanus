package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.security.*;

public class ControlData extends DataItem {
	/**
	 * The name of the object
	 */
	public static final String objName = "Static";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";

	/**
	 * Control Key Length
	 */
	public final static int CTLLEN 		= AsymmetricKey.IDSIZE;

	/**
	 * Symmetric Key Length
	 */
	public final static int KEYLEN 		= SymmetricKey.IDSIZE;

	/**
	 * InitVector length
	 */
	public final static int INITVLEN 	= SymmetricKey.IVSIZE;

	/* Local values */
	private SecurityControl	theControl	= null;
	private SymmetricKey	theKey		= null;
	
	/* Access methods */
	public  int 			getDataVersion()  	{ return getObj().getDataVersion(); }
	public  String 			getControlKey()  	{ return getObj().getControlKey(); }
	public  byte[] 			getSecurityKey()  	{ return getObj().getSecurityKey(); }
	public  byte[] 			getInitVector()  	{ return getObj().getInitVector(); }
	public  SecurityControl	getSecurityControl(){ return theControl; }
	private SymmetricKey	getKey()			{ return theKey; }

	/* Linking methods */
	public ControlData	getBase() { return (ControlData)super.getBase(); }
	public Values  	getObj()  { return (Values)super.getObj(); }	
	
	/* Field IDs */
	public static final int FIELD_ID       = 0;
	public static final int FIELD_VERS	   = 1;
	public static final int FIELD_CONTROL  = 2;
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
			case FIELD_ID:			return "ID";
			case FIELD_VERS:		return "Version";
			case FIELD_CONTROL:		return "Control";
			case FIELD_KEY:			return "SecurityKey";
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
			case FIELD_VERS:
				myString += getDataVersion(); 
				break;
			case FIELD_CONTROL:
				myString += getControlKey(); 
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
 	* Construct a copy of a Static
 	* 
 	* @param pPrice The Price 
 	*/
	protected ControlData(List pList, ControlData pStatic) {
		/* Set standard values */
		super(pList, pStatic.getId());
		Values myObj = new Values(pStatic.getObj());
		setObj(myObj);
		theControl		= pStatic.getSecurityControl();
		theKey			= pStatic.getKey();

		/* Switch on the LinkStyle */
		switch (pList.getStyle()) {
			case CORE:
				pList.setNewId(this);				
				break;
			case EDIT:
				setBase(pStatic);
				setState(DataState.CLEAN);
				break;
			case UPDATE:
				setBase(pStatic);
				setState(pStatic.getState());
				break;
		}
	}

	/* Standard constructor */
	public ControlData(List     pList,
				  	   int		uId,
				  	   int      uVersion, 
				  	   String	pControlKey,
				  byte[]	pSecurityKey,
		  		  byte[]	pInitVector) throws Exception {
		/* Initialise the item */
		super(pList, uId);
		Values myObj = new Values();
		setObj(myObj);

		/* Record the values */
		myObj.setDataVersion(uVersion);
		
		/* Access the Security manager */
		DataSet 		myData 		= pList.getData();
		SecureManager 	mySecurity 	= myData.getSecurity();
		EncryptedPair	myPairs		= myData.getEncryptedPairs();
		
		/* Obtain the required security control */
		theControl = mySecurity.getSecurityControl(pControlKey, "Database");
		
		/* Record the control */
		myObj.setControlKey(pControlKey);			
		
		/* Obtain the relevant symmetric key */
		myObj.setSecurityKey(pSecurityKey);
		theKey	= theControl.getSymmetricKey(getSecurityKey(), 
											 theControl.getControlMode().getSymKeyType());

		/* Record the initialisation vector */
		myObj.setInitVector(pInitVector);			
		
		/* declare key and initialisation vector to the encrypted pairs */
		myPairs.setEncryptionDtl(theKey, getInitVector());

		/* Allocate the id */
		pList.setNewId(this);				
	}

	/* Limited (no security) constructor */
	public ControlData(List pList,
				  	   int	uId,
				  	   int	uVersion) {
		/* Initialise the item */
		super(pList, uId);
		Values myObj = new Values();
		setObj(myObj);

		/* Record the values */
		myObj.setDataVersion(uVersion);
				
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/**
	 *  Initialise security for a spreadsheet load
	 *  @param pStatic the static from the database
	 */
	public void setSecurity(ControlData pStatic) throws Exception {
		Values 			myValues 	= getObj();
		List 			myList 		= (List)getList();
		DataSet 		myData 		= myList.getData();
		EncryptedPair	myPairs		= myData.getEncryptedPairs();
		
		/* If we have static from the database */
		if (pStatic != null) {
			/* Store the required object values */
			myValues.setControlKey(pStatic.getControlKey());			
			myValues.setSecurityKey(pStatic.getSecurityKey());
			myValues.setInitVector(pStatic.getInitVector());			
		
			/* Access the control and symmetric key */
			theControl		= pStatic.getSecurityControl();
			theKey			= pStatic.getKey();
			
			/* declare key and initialisation vector to the encrypted pairs */
			myPairs.setEncryptionDtl(theKey, getInitVector());
		}
		
		/* else we need to allocate a new security control */
		else {
			/* Access the Security manager */
			SecureManager 	mySecurity 	= myData.getSecurity();
			
			/* Obtain a new security control */
			theControl = mySecurity.getSecurityControl(null, "Database");
			
			/* Record the control */
			myValues.setControlKey(theControl.getSecurityKey());			
			
			/* Generate a new key and get its security key */
			theKey	= theControl.getSymmetricKey(theControl.getControlMode().getSymKeyType());
			myValues.setSecurityKey(theKey.getSecurityKey());			

			/* Declare the key to the encryption pairs and record the initialisation vector */
			myValues.setInitVector(myPairs.setEncryptionDtl(theKey));						
		}
		
		/* Ensure encryption of the spreadsheet load */
		myData.ensureEncryption();
	}

	/**
	 * Compare this static to another to establish equality.
	 * 
	 * @param pThat The Static to compare to
	 * @return <code>true</code> if the static is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a Static */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a Static */
		ControlData myStatic = (ControlData)pThat;
		
		/* Check for equality */
		if (getId() != myStatic.getId()) return false;
		if (getDataVersion() != myStatic.getDataVersion()) 				return false;
		if (Utils.differs(getControlKey(),  myStatic.getControlKey()))  return false;
		if (Utils.differs(getSecurityKey(), myStatic.getSecurityKey())) return false;
		if (Utils.differs(getInitVector(),  myStatic.getInitVector())) 	return false;
		return true;
	}

	/**
	 * Compare this price to another to establish sort order. 
	 * @param pThat The Price to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		int iDiff;
		
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a Static */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as a Static */
		ControlData myThat = (ControlData)pThat;

		/* Compare the Versions */
		iDiff =(int)(getDataVersion() - myThat.getDataVersion());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;

		/* Compare the IDs */
		iDiff =(int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}

	/**
	 * Set a new Security control 
	 * @param pControl the new control 
	 */
	protected void setSecurityControl(SecurityControl pControl) throws Exception {
		/* Store the current detail into history */
		pushHistory();

		/* Store the Control and record the new Control Key */
		theControl = pControl;
		getObj().setControlKey(theControl.getSecurityKey());

		/* Re-associate the Symmetric Key and store the new Security Key */
		theKey.setSecurityControl(pControl);
		getObj().setSecurityKey(theKey.getSecurityKey());

		/* Check for changes */
		if (checkForHistory()) setState(DataState.CHANGED);
	}
	
	/**
	 * Static List
	 */
	public static class List  extends DataList<ControlData> {
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
		public ControlData addNewItem(DataItem pItem) { 
			ControlData myStatic = new ControlData(this, (ControlData)pItem);
			myStatic.addToList();
			return myStatic; 
		}

		/**
		 * Add a new item to the edit list
		 * @param isCredit - ignored
		 * @return the newly added item
		 */
		public ControlData addNewItem(boolean isCredit) { return null; }

		/**
		 * 	Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }

		/**
		 *  Add a Static item
		 */
		public void addItem(int  	uId,
							int  	uVersion,
							String	pControlKey,
	            			byte[]	pSecurityKey,
							byte[]	pInitVector) throws Exception {
			ControlData     	myStatic;
			
			/* Create the static */
			myStatic = new ControlData(this, 
								  uId, 
								  uVersion,
								  pControlKey,
								  pSecurityKey,
								  pInitVector);
			
			/* Check that this StaticId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
									myStatic,
									"Duplicate StaticId <" + uId + ">");
			 
			/* Add to the list */
			myStatic.addToList();
		}			

		/**
		 *  Add a Static item (with no security as yet)
		 */
		public void addItem(int  			uId,
							int  			uVersion) throws Exception {
			ControlData     	myStatic;
			
			/* Create the static */
			myStatic = new ControlData(this, 
								  uId, 
								  uVersion);
			
			/* Check that this StaticId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
									myStatic,
									"Duplicate StaticId <" + uId + ">");
			 
			/* Add to the list */
			myStatic.addToList();
		}			

		/**
		 *  Access default Static item
		 */
		protected ControlData getDefault() {
			ControlData     		myStatic;
			ListIterator 	myIterator;

			/* Access the first static element */
			myIterator	 = listIterator();
			myStatic	 = myIterator.next();
			
			/* Return to caller */
			return myStatic;			
		}			
	}

	/**
	 * Values for a static 
	 */
	public class Values implements histObject {
		private int 			theDataVersion	= -1;
		private String			theControlKey	= null;
		private byte[]			theSecurityKey	= null;
		private byte[]			theInitVector	= null;
		
		/* Access methods */
		public  int 			getDataVersion()  	{ return theDataVersion; }
		public  String 			getControlKey()  	{ return theControlKey; }
		public  byte[] 			getSecurityKey()  	{ return theSecurityKey; }
		public  byte[] 			getInitVector()  	{ return theInitVector; }
		
		public void setDataVersion(int pValue) {
			theDataVersion = pValue; }
		public void setControlKey(String pValue) {
			theControlKey  = pValue; }
		public void setSecurityKey(byte[] pValue) {
			theSecurityKey = pValue; }
		public void setInitVector(byte[] pValue) {
			theInitVector  = pValue; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theDataVersion	= pValues.getDataVersion();
			theControlKey	= pValues.getControlKey();
			theSecurityKey	= pValues.getSecurityKey();
			theInitVector	= pValues.getInitVector();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (theDataVersion != pValues.theDataVersion)   				return false;
			if (Utils.differs(theControlKey,    pValues.theControlKey))   	return false;
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
			theDataVersion	= pValues.getDataVersion();
			theControlKey	= pValues.getControlKey();
			theSecurityKey	= pValues.getSecurityKey();
			theInitVector	= pValues.getInitVector();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_VERS:
					bResult = (theDataVersion != pValues.theDataVersion);
					break;
				case FIELD_CONTROL:
					bResult = (Utils.differs(theControlKey,		pValues.theControlKey));
					break;
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

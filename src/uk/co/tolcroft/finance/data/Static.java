package uk.co.tolcroft.finance.data;

import java.util.Arrays;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.security.*;

public class Static extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "Static";

	/* Local values */
	private SymmetricKey	theKey			= null;
	
	/* Access methods */
	public  long 			getDataVersion()  	{ return getObj().getDataVersion(); }
	public  byte[] 			getSecurityKey()  	{ return getObj().getSecurityKey(); }
	public  SymmetricKey	getKey()			{ return theKey; }

	public void setKey(SymmetricKey pKey) {
		theKey = pKey;
		try { getObj().setSecurityKey(theKey.getSecurityKey()); } catch (Throwable e) {}
	}
	
	/* Linking methods */
	public Static	getBase() { return (Static)super.getBase(); }
	public Values  	getObj()  { return (Values)super.getObj(); }	
	
	/* Field IDs */
	public static final int FIELD_ID       = 0;
	public static final int FIELD_VERS	   = 1;
	public static final int FIELD_KEY	   = 2;
	public static final int NUMFIELDS	   = 3; 

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
	public String	fieldName(int iField) {
		switch (iField) {
			case FIELD_ID:			return "ID";
			case FIELD_VERS:		return "Version";
			case FIELD_KEY:			return "Key";
			default:		  		return super.fieldName(iField);
		}
	}
				
	/**
	 * Format the value of a particular field as a table row
	 * @param iField the field number
	 * @param pObj the values to use
	 * @return the formatted field
	 */
	public String formatField(int iField, histObject pObj) {
		String 	myString = "<tr><td>" + fieldName(iField) + "</td><td>";
		switch (iField) {
			case FIELD_ID: 		
				myString += getId(); 
				break;
			case FIELD_VERS:
				myString += getDataVersion(); 
				break;
			case FIELD_KEY:
				myString += "?????"; 
				break;
		}
		return myString + "</td></tr>";
	}
							
	/**
 	* Construct a copy of a Price
 	* 
 	* @param pPrice The Price 
 	*/
	protected Static(List pList, Static pStatic) {
		/* Set standard values */
		super(pList, pStatic.getId());
		Values myObj = new Values(pStatic.getObj());
		setObj(myObj);
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
	public Static(List      pList,
				  long		uId,
			      long      uVersion, 
				  byte[]	pSecurityKey) throws Exception {
		/* Initialise the item */
		super(pList, uId);
		Values myObj = new Values();
		setObj(myObj);


		/* Access the security Control */
		SecurityControl myControl = pList.theData.getSecurity();
		
		/* Record the values */
		myObj.setDataVersion(uVersion);
		
		/* If we are passed a security Key */
		if (pSecurityKey != null) {
			/* Obtain the relevant symmetric key */
			myObj.setSecurityKey(pSecurityKey);
			theKey	= myControl.getSymmetricKey(getSecurityKey());
		}
		
		/* else we must generate a new key */
		else {
			/* Generate a new key and get its security key */
			theKey	= myControl.getSymmetricKey();
			myObj.setSecurityKey(theKey.getSecurityKey());			
		}
		
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/**
	 * Compare this price to another to establish equality.
	 * 
	 * @param pThat The Price to compare to
	 * @return <code>true</code> if the tax year is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a Static */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a Static */
		Static myStatic = (Static)pThat;
		
		/* Check for equality */
		if (getId() != myStatic.getId()) return false;
		if (getDataVersion() != myStatic.getDataVersion()) 					return false;
		if (!Arrays.equals(getSecurityKey(), myStatic.getSecurityKey())) 	return false;
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
		Static myThat = (Static)pThat;

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
	 * Static List
	 */
	public static class List  extends DataList<Static> {
		/* Members */
		private DataSet		theData		= null;

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
		 * 
		 * @param pItem item
		 * @return the newly added item
		 */
		public DataItem addNewItem(DataItem pItem) { 
			Static myStatic = new Static(this, (Static)pItem);
			myStatic.addToList();
			return myStatic; 
		}

		/**
		 * Add a new item to the edit list
		 * 
		 * @param isCredit - ignored
		 */
		public void addNewItem(boolean        isCredit) {}

		/**
		 * 	Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }

		/**
		 *  Add a Static
		 */
		public void addItem(long  	uId,
							long  	uVersion,
	            			byte[]	pSecurityKey) throws Exception {
			Static     	myStatic;
			
			/* Create the static */
			myStatic = new Static(this, uId, uVersion, pSecurityKey);
			
			/* Check that this StaticId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
									myStatic,
									"Duplicate StaticId <" + uId + ">");
			 
			/* Add to the list */
			myStatic.addToList();
		}			
	}

	/**
	 * Values for a static 
	 */
	public class Values implements histObject {
		private long 			theDataVersion	= -1;
		private byte[]			theSecurityKey	= null;
		
		/* Access methods */
		public  long 			getDataVersion()  	{ return theDataVersion; }
		public  byte[] 			getSecurityKey()  	{ return theSecurityKey; }
		
		public void setDataVersion(long pValue) {
			theDataVersion = pValue; }
		public void setSecurityKey(byte[] pValue) {
			theSecurityKey = pValue; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) {
			theDataVersion	= pValues.getDataVersion();
			theSecurityKey	= pValues.getSecurityKey();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			Values myValues = (Values)pCompare;
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (theDataVersion != pValues.theDataVersion)    				return false;
			if (Utils.differs(theSecurityKey,    pValues.theSecurityKey))   return false;
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
			theSecurityKey	= pValues.getSecurityKey();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			Values 	pValues = (Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_VERS:
					bResult = (theDataVersion != pValues.theDataVersion);
					break;
				case FIELD_KEY:
					bResult = (Utils.differs(theSecurityKey,      pValues.theSecurityKey));
					break;
			}
			return bResult;
		}
	}	
}

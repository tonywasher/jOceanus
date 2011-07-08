package uk.co.tolcroft.models;

import uk.co.tolcroft.models.EncryptedPair.StringPair;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public abstract class StaticData<E extends Enum<E>> extends DataItem {
	/**
	 * Interface for Static Classes
	 */
	public interface StaticInterface  {
		public int getClassId();
		public int getOrder();
	}
	
	/**
	 * The class of the Static Data
	 */
	private Class<E>		theDataClass 	= null;

	/**
	 * The instance enum of the Static Data
	 */
	private E				theClass 		= null;

	/**
	 * The sort order of the Static Data
	 */
	private int     		theOrder		= -1;
	
	/**
	 * The Class Id of the Static Data
	 */
	private int     		theClassId		= -1;
	
	/**
	 * Return the name of the Static Data
	 * @return the name
	 */
	public String    	getName() 		{ return getObj().getNameValue(); }

	/**
	 * Return the encrypted name of the Static Data
	 * @return the encrypted name
	 */
	public byte[] 		getNameBytes() 	{ return getObj().getNameBytes(); }
	
	/**
	 * Return the encrypted pair name of the Static Data
	 * @return the encrypted pair name
	 */
	private StringPair	getNamePair() 	{ return getObj().getName(); }
	
	/**
	 * Return the description of the Static Data
	 * @return the description
	 */
	public String    	getDesc()		{ return getObj().getDescValue(); }

	/**
	 * Return the encrypted description of the Static Data
	 * @return the encrypted description
	 */
	public byte[] 		getDescBytes() 	{ return getObj().getDescBytes(); }
	
	/**
	 * Return the encrypted pair description of the Static Data
	 * @return the encrypted pair description
	 */
	private StringPair	getDescPair() 	{ return getObj().getDesc(); }
	
	/**
	 * Return the sort order of the Static Data
	 * @return the order
	 */
	public int			getOrder()			{ return theOrder; }

	/**
	 * Return the Static class of the Static Data
	 * @return the class
	 */
	protected E			getStaticClass()	{ return theClass; }
	
	/**
	 * Return the Static class id of the Static Data
	 * @return the class id
	 */
	public int			getStaticClassId()	{ return theClassId; }
	
	/* Linking methods */
	public StaticData<?>.Values  getObj()  { return (StaticData<?>.Values)super.getObj(); }	

	/* Field IDs */
	public static final int FIELD_ID     	= 0;
	public static final int FIELD_NAME     	= 1;
	public static final int FIELD_DESC     	= 2;
	public static final int FIELD_ORDER     = 3;
	public static final int FIELD_CLASS     = 4;
	public static final int FIELD_CLASSID   = 5;
	public static final int NUMFIELDS	    = 6;
	
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
			case FIELD_ID: 	    return "ID";
			case FIELD_NAME:    return "Name";
			case FIELD_DESC:    return "Description";
			case FIELD_ORDER:   return "Order";
			case FIELD_CLASS:   return "Class";
			case FIELD_CLASSID: return "ClassId";
			default:		    return DataItem.fieldName(iField);
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
		String myString = ""; 
		@SuppressWarnings("unchecked")
		Values 	myObj 	 = (Values)pObj;
		switch (iField) {
			case FIELD_ID: 		myString += getId();  	break;
			case FIELD_NAME:	myString += myObj.getNameValue(); 	break;
			case FIELD_DESC:	myString += myObj.getDescValue(); 	break;
			case FIELD_ORDER: 	myString += getOrder();	break;
			case FIELD_CLASS: 	myString += theClass;	break;
			case FIELD_CLASSID: myString += theClassId;	break;
		}
		return myString;
	}
	
	/**
	 * Compare this Static Data to another to establish equality.
	 * 
	 * @param that The Static Data to compare to
	 * @return <code>true</code> if the static data is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is the same class */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target StaticData */
		StaticData<?> myThat = (StaticData<?>)pThat;

		/* Make sure that the object is the same enumeration class */
		if (myThat.theDataClass != this.theDataClass) 	return false;
		
		/* Compare the id and name */
		if (getId() 	!= myThat.getId()) 								return false;
		if (getStaticClass() != myThat.getStaticClass())				return false;
		if (EncryptedPair.differs(getNamePair(), myThat.getNamePair())) return false;
		if (EncryptedPair.differs(getDescPair(), myThat.getDescPair())) return false;
		return true;
	}

	/**
	 * Compare this StaticData to another to establish sort order.
	 * 
	 * @param pThat The StaticData to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		long result;

		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is the same class */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the target Static Data */
		StaticData<?> myThat = (StaticData<?>)pThat;
		
		/* Make sure that the object is the same enumeration class */
		if (myThat.theDataClass != this.theDataClass) return -1;
		
		/* Compare on order */
		if (theOrder < myThat.theOrder) return -1;
		if (theOrder > myThat.theOrder) return  1;

		/* Compare on name */
		result = getName().compareTo(myThat.getName());
		if (result < 0) return -1;
		if (result > 0) return 1;
		
		/* Compare on id */
		result = (int)(getId() - myThat.getId());
		if (result == 0) return 0;
		else if (result < 0) return -1;
		else return 1;
	}

	/**
	 * Construct a copy of a Static data.
	 * @param pList	The list to associate the Static Data with
	 * @param pSource The static data to copy 
	 */
	protected StaticData(StaticList<?,E>	pList,
			             StaticData<E>		pSource) { 
		super(pList, pSource.getId());
		Values myObj = new Values(pSource.getObj());
		setObj(myObj);
		theClass 		= pSource.theClass;
		theDataClass 	= pSource.theDataClass;
		theOrder 		= pSource.getOrder();
		theClassId 		= pSource.getStaticClassId();
		setBase(pSource);
		setState(pSource.getState());
	}
	
	/**
	 * Initial constructor
	 * @param pList The list to associate the Static Data with
	 * @param uId the id of the new item
	 * @param pValue the name of the new item
	 */
	protected StaticData(StaticList<?,E> 	pList, 
					  	 String				pValue) throws Exception {
		super(pList, 0);
		theDataClass = pList.theDataClass;
		parseEnumValue(pValue);
		Values myObj = new Values();
		setObj(myObj);

		/* Create the Encrypted pair for the name */
		EncryptedPair	myPairs = pList.thePairs;
		myObj.setName(myPairs.new StringPair(pValue));
	}
	
	/**
	 * Clear Text constructor
	 * @param pList The list to associate the Static Data with
	 * @param uClassId the class id of the new item
	 * @param pValue the encrypted name of the new item
	 * @param pDesc the encrypted description of the new item
	 */
	protected StaticData(StaticList<?,E> 	pList, 
					  	 int 				uClassId,
					  	 String				pValue,
					  	 String				pDesc) throws Exception {
		super(pList, 0);
		theDataClass = pList.theDataClass;
		parseEnumId(uClassId);
		Values myObj = new Values();
		setObj(myObj);
		
		/* Create the Encrypted pair for the name */
		EncryptedPair	myPairs = pList.thePairs;
		myObj.setName(myPairs.new StringPair(pValue));
		if (pDesc != null)
			myObj.setDesc(myPairs.new StringPair(pDesc));
	}
	
	/**
	 * Encrypted constructor
	 * @param pList The list to associate the Static Data with
	 * @param uId the id of the new item
	 * @param uClassId the class id of the new item
	 * @param pValue the encrypted name of the new item
	 * @param pDesc the encrypted description of the new item
	 */
	protected StaticData(StaticList<?,E> 	pList, 
					  	 int 				uId,
					  	 int 				uClassId,
					  	 byte[]				pValue,
					  	 byte[]				pDesc) throws Exception {
		super(pList, uId);
		theDataClass = pList.theDataClass;
		parseEnumId(uClassId);
		Values myObj = new Values();
		setObj(myObj);
		
		/* Create the Encrypted pair for the name */
		EncryptedPair	myPairs = pList.thePairs;
		myObj.setName(myPairs.new StringPair(pValue));
		if (pDesc != null)
			myObj.setDesc(myPairs.new StringPair(pDesc));
	}
	
	/**
	 * Parse enum type
	 * @param pValue
	 */
	private void parseEnumValue(String pValue) throws Exception {
		StaticInterface myIFace 	= null;
		E[] 			myValues 	= (E[])theDataClass.getEnumConstants();
		
		/* Loop through the enum constants */
		for (E myValue: myValues) {
			/* If this is the desired value */
			if (myValue.toString().equalsIgnoreCase(pValue)) {
				/* Store the class */
				theClass = myValue;
				
				/* If the enum is of the desired type */
				if (theClass instanceof StaticInterface) {
					/* Access classId and order */
					myIFace		= (StaticInterface) myValue;
					theClassId	= myIFace.getClassId();
					theOrder	= myIFace.getOrder();
				}
				break;
			}
		}
		
		/* Reject if we didn't find the class */
		if (theClass == null) 
			throw new Exception(ExceptionClass.DATA,
							    "Invalid value for " + theDataClass.getSimpleName() +
								": " + pValue);
		
		/* Reject if class was wrong type */
		if (myIFace == null) 
			throw new Exception(ExceptionClass.DATA,
							    "Class: " + theDataClass.getSimpleName() +
								" is not valid for StaticData");
	}
	
	/**
	 * Parse enum id
	 * @param pId
	 */
	private void parseEnumId(int pId) throws Exception {
		StaticInterface myIFace 	= null;
		E[] 			myValues 	= (E[])theDataClass.getEnumConstants();
		
		/* Loop through the enum constants */
		for (E myValue: myValues) {
			/* Ensure that the class is of the right type */
			if (!(myValue instanceof StaticInterface)) 
				throw new Exception(ExceptionClass.DATA,
									"Class: " + theDataClass.getSimpleName() +
									" is not valid for StaticData");
				
			/* Access via interface */
			myIFace		= (StaticInterface) myValue;
			
			/* If this is the desired value */
			if (myIFace.getClassId() == pId) {
				/* Store the class and details */
				theClass 	= myValue;
				theClassId	= myIFace.getClassId();
				theOrder	= myIFace.getOrder();
				break;
			}
		}
		
		/* Reject if we didn't find the class */
		if (theClass == null) 
			throw new Exception(ExceptionClass.DATA,
							    "Invalid id for " + theDataClass.getSimpleName() +
								": " + pId);		
	}
	
	/**
	 * Format a Static Data 
	 * @param pData the static data to format
	 * @return the formatted data
	 */
	public static String format(StaticData<?> pData) {
		String 	myFormat;
		myFormat = (pData != null) ? pData.getName()
							       : "null";
		return myFormat;
	}
	
	/**
	 * Determine whether two StaticData objects differ.
	 * @param pCurr The current data 
	 * @param pNew The new Data
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static boolean differs(StaticData<?> pCurr, StaticData<?> pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (!pCurr.equals(pNew)))));
	}

	/**
	 * Set a new name 
	 * @param pName the name 
	 */
	public void setName(String pName) throws Exception {
		/* If we are setting a non null value */
		if (pName != null) {
			/* Create the Encrypted pair for the values */
			EncryptedPair	myPairs = ((StaticList<?,?>)getList()).thePairs;
			StringPair		myPair	= myPairs.new StringPair(pName);
		
			/* Record the value and encrypt it*/
			getObj().setName(myPair);
			myPair.ensureEncryption();
		}
		
		/* Else we are setting a null value */
		else getObj().setName(null);
	}

	/**
	 * Set a new description
	 * @param pDesc the description 
	 */
	public void setDescription(String pDesc) throws Exception {
		/* If we are setting a non null value */
		if (pDesc != null) {
			/* Create the Encrypted pair for the values */
			EncryptedPair	myPairs = ((StaticList<?,?>)getList()).thePairs;
			StringPair		myPair	= myPairs.new StringPair(pDesc);
		
			/* Record the value and encrypt it*/
			getObj().setDesc(myPair);
			myPair.ensureEncryption();
		}
		
		/* Else we are setting a null value */
		else getObj().setDesc(null);
	}

	/**
	 * Update StaticData from a StaticData extract  
	 * @param pData the updated item 
	 * @return whether changes have been made
	 */
	public boolean applyChanges(DataItem pData) {
		StaticData<?>			myData 		=  (StaticData<?>)pData;
		StaticData<?>.Values	myObj		= getObj();
		StaticData<?>.Values	myNew		= myData.getObj();
		boolean  				bChanged	= false;

		/* Store the current detail into history */
		pushHistory();

		/* Update the name if required */
		if (EncryptedPair.differs(myObj.getName(), myNew.getName())) 
			myObj.setName(myNew.getName());

		/* Update the description if required */
		if (EncryptedPair.differs(myObj.getDesc(), myNew.getDesc())) 
			myObj.setDesc(myNew.getDesc());

		/* Check for changes */
		if (checkForHistory()) {
			/* Mark as changed */
			setState(DataState.CHANGED);
			bChanged = true;
		}
		
		/* Return to caller */
		return bChanged;
	}

	/**
	 * Ensure encryption after spreadsheet load
	 */
	protected void ensureEncryption() throws Exception {
		StaticData<?>.Values myObj = getObj();

		/* Protect against exceptions */
		try {
			/* Ensure the encryption */
			myObj.getName().ensureEncryption();
			if (myObj.getDesc() != null)
				myObj.getDesc().ensureEncryption();
		}
		
		/* Catch exception */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								this,
								"Failed to complete encryption",
								e);
		}
	}
	
	/**
	 * Represents a list of StaticData objects. 
	 */
	public abstract static class StaticList<T extends StaticData<E>, E extends Enum<E>>  extends DataList<T> {
		/**
		 * The class of the Static Data
		 */
		private Class<E>		theDataClass 	= null;
		private EncryptedPair	thePairs 		= null;

		/** 
	 	 * Construct a generic static data list
	 	 * @param enumClass the static class of the items
	 	 * @param pPairs the encrypted pair control  
	 	 * @param pStyle the style of the list 
	 	 */
		public StaticList(Class<E>		enumClass,
						  EncryptedPair	pPairs,
						  ListStyle 	pStyle) { 
			super(pStyle, true);
			theDataClass 	= enumClass;
			thePairs		= pPairs;
		}

		/** 
	 	 * Construct a generic static data list
	 	 * @param pList the source Data list 
	 	 * @param pStyle the style of the list 
	 	 */
		public StaticList(StaticList<T,E> pList, ListStyle pStyle) { 
			super(pList, pStyle);
			theDataClass 	= pList.theDataClass;
			thePairs		= pList.thePairs;
		}

		/** 
	 	 * Construct a difference static data list
	 	 * @param pNew the new static data list 
	 	 * @param pOld the old static data list 
	 	 */
		protected StaticList(StaticList<T,E> pNew, StaticList<T,E> pOld) { 
			super(pNew, pOld);
			theDataClass 	= pNew.theDataClass;
			thePairs		= pNew.thePairs;
		}
				
		/* List Iterators */
		public void setNewId(T pItem)	{ super.setNewId(pItem); }
		
		/**
		 * Search for a particular item by class
		 * @param eClass The class of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		public T searchFor(E eClass) {
			ListIterator 	myIterator;
			T				myCurr;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				if (myCurr.theClass == eClass) break;
			}
			
			/* Return to caller */
			return myCurr;
		}
		
		/**
		 * Search for a particular item by Enum id
		 * @param uId The Enum id of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		protected T searchForEnum(int uId) {
			ListIterator 	myIterator;
			T				myCurr;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				if (myCurr.theClassId == uId) break;
			}
			
			/* Return to caller */
			return myCurr;
		}
		
		/**
		 * Search for a particular item by Name
		 * 
		 * @param sName The name of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		public T searchFor(String sName) {
			ListIterator 	myIterator;
			T				myCurr;
			int         	iDiff;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				iDiff = sName.compareTo(myCurr.getName());
				if (iDiff == 0) break;
			}
			
			/* Return to caller */
			return myCurr;
		}		
	}
	
	/**
	 * Values for a static data
	 */
	public class Values implements histObject {
		private StringPair	theName      = null;
		private StringPair	theDesc      = null;
		
		/* Access methods */
		public StringPair  	getName()      	{ return theName; }
		public StringPair  	getDesc()      	{ return theDesc; }
		private	Class<E>   	getDataClass()	{ return theDataClass; }
		public String  		getNameValue()  { return EncryptedPair.getPairValue(theName); }
		public String  		getDescValue()  { return EncryptedPair.getPairValue(theDesc); }
		public byte[]  		getNameBytes()  { return EncryptedPair.getPairBytes(theName); }
		public byte[]  		getDescBytes()  { return EncryptedPair.getPairBytes(theDesc); }
		
		/* Value setting */
		public void setName(StringPair pName) { theName = pName; }
		public void setDesc(StringPair pDesc) { theDesc = pDesc; }

		/* Constructor */
		public Values() {}
		public Values(StaticData<?>.Values pValues) {
			theName      = pValues.getName();
			theDesc      = pValues.getDesc();
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(histObject pCompare) {
			/* Make sure that the object is the same class */
			if (pCompare.getClass() != this.getClass()) return false;
			
			/* Cast correctly */
			StaticData<?>.Values myValues = (StaticData<?>.Values)pCompare;
			
			/* Make sure that the object is the same enumeration class */
			if (myValues.getDataClass() != theDataClass) return false;

			/* Make the actual comparison */
			return histEquals(myValues);
		}
		public boolean histEquals(Values pValues) {
			if (EncryptedPair.differs(theName,    pValues.theName))    return false;
			if (EncryptedPair.differs(theDesc,    pValues.theDesc))    return false;
			return true;
		}
		
		/* Copy values */
		public void    copyFrom(histObject pSource) {
			StaticData<?>.Values myValues = (StaticData<?>.Values)pSource;
			copyFrom(myValues);
		}
		public histObject copySelf() {
			return new Values(this);
		}
		public void    copyFrom(Values pValues) {
			theName      = pValues.getName();
		}
		public boolean	fieldChanged(int fieldNo, histObject pOriginal) {
			StaticData<?>.Values 	pValues = (StaticData<?>.Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_NAME:
					bResult = (EncryptedPair.differs(theName,      pValues.theName));
					break;
				case FIELD_DESC:
					bResult = (EncryptedPair.differs(theDesc,      pValues.theDesc));
					break;
			}
			return bResult;
		}
	}
}

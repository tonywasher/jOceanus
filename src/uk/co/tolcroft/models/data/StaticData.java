package uk.co.tolcroft.models.data;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataList.ListStyle;

public abstract class StaticData<T extends StaticData<T,E>,
								 E extends Enum<E>> extends EncryptedItem<T> {
	/**
	 * Interface for Static Classes
	 */
	public interface StaticInterface  {
		public int getClassId();
		public int getOrder();
	}
	
	/**
	 * StaticData Name length
	 */
	public final static int NAMELEN = 50;

	/**
	 * StaticData Description length
	 */
	public final static int DESCLEN = 100;

	/**
	 * The Enum class of the Static Data
	 */
	private Class<E>		theEnumClass 	= null;

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
	public StaticData<?,?>.Values  getObj()  { return (StaticData<?,?>.Values)super.getValues(); }	

	/* Field IDs */
	public static final int FIELD_NAME     	= EncryptedItem.NUMFIELDS;
	public static final int FIELD_DESC     	= EncryptedItem.NUMFIELDS+1;
	public static final int FIELD_ORDER     = EncryptedItem.NUMFIELDS+2;
	public static final int FIELD_CLASS     = EncryptedItem.NUMFIELDS+3;
	public static final int FIELD_CLASSID   = EncryptedItem.NUMFIELDS+4;
	public static final int NUMFIELDS	    = EncryptedItem.NUMFIELDS+5;
	
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
			case FIELD_NAME:    return "Name";
			case FIELD_DESC:    return "Description";
			case FIELD_ORDER:   return "Order";
			case FIELD_CLASS:   return "Class";
			case FIELD_CLASSID: return "ClassId";
			default:		    return EncryptedItem.fieldName(iField);
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
	public String formatField(int iField, HistoryValues<T> pObj) {
		String myString = ""; 
		StaticData<?,?>.Values 	myObj 	 = (StaticData<?,?>.Values)pObj;
		switch (iField) {
			case FIELD_NAME:	myString += myObj.getNameValue(); 	break;
			case FIELD_DESC:	myString += myObj.getDescValue(); 	break;
			case FIELD_ORDER: 	myString += getOrder();	break;
			case FIELD_CLASS: 	myString += theClass;	break;
			case FIELD_CLASSID: myString += theClassId;	break;
			default:			myString += super.formatField(iField, pObj); break;
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
		StaticData<?,?> myThat = (StaticData<?,?>)pThat;

		/* Make sure that the object is the same enumeration class */
		if (myThat.theEnumClass != this.theEnumClass) 	return false;
		
		/* Compare the id and class */
		if (getId() 	!= myThat.getId()) 								return false;
		if (getStaticClass() != myThat.getStaticClass())				return false;
		
		/* Compare the changeable values */
		return getObj().histEquals(myThat.getObj());
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
		StaticData<?,?> myThat = (StaticData<?,?>)pThat;
		
		/* Make sure that the object is the same enumeration class */
		if (myThat.theEnumClass != this.theEnumClass) return -1;
		
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
	protected StaticData(StaticList<T,E>	pList,
			             StaticData<T,E>	pSource) { 
		super(pList, pSource.getId());
		Values myValues = new Values(pSource.getObj());
		setValues(myValues);
		setControlKey(pSource.getControlKey());		
		theClass 		= pSource.theClass;
		theEnumClass 	= pSource.theEnumClass;
		theOrder 		= pSource.getOrder();
		theClassId 		= pSource.getStaticClassId();
		ListStyle myOldStyle = pSource.getList().getStyle();

		/* Switch on the ListStyle */
		switch (pList.getStyle()) {
			case EDIT:
				/* If this is a view creation */
				if (myOldStyle == ListStyle.CORE) {
					/* Static is based on the original element */
					setBase(pSource);
					pList.setNewId(getItem());				
					break;
				}
				
				/* Else this is a duplication so treat as new item */
				setId(0);
				pList.setNewId(getItem());				
				break;
			case CORE:
				/* Reset Id if this is an insert from a view */
				if (myOldStyle == ListStyle.EDIT) setId(0);
				pList.setNewId(getItem());				
				break;
			case UPDATE:
				setBase(pSource);
				setState(pSource.getState());
				break;
		}
	}
	
	/**
	 * Initial constructor
	 * @param pList The list to associate the Static Data with
	 * @param uId the id of the new item
	 * @param pValue the name of the new item
	 */
	protected StaticData(StaticList<T,E> 	pList, 
					  	 String				pValue) throws Exception {
		super(pList, 0);
		theEnumClass = pList.getEnumClass();
		parseEnumValue(pValue);
		Values myValues = new Values();
		setValues(myValues);

		/* Create the pair for the name */
		myValues.setName(new StringPair(pValue));

		/* Set the new Id */
		pList.setNewId(getItem());					
	}
	
	/**
	 * Clear Text constructor
	 * @param pList The list to associate the Static Data with
	 * @param uId the id of the new item
	 * @param uClassId the class id of the new item
	 * @param pValue the encrypted name of the new item
	 * @param pDesc the encrypted description of the new item
	 */
	protected StaticData(StaticList<T,E> 	pList,
						 int				uId,
					  	 int 				uClassId,
					  	 String				pValue,
					  	 String				pDesc) throws Exception {
		super(pList, uId);
		theEnumClass = pList.getEnumClass();
		parseEnumId(uClassId);
		Values myValues = new Values();
		setValues(myValues);
				
		/* Create the pairs for the name and description */
		myValues.setName(new StringPair(pValue));
		if (pDesc != null) myValues.setDesc(new StringPair(pDesc));

		/* Set the new Id */
		pList.setNewId(getItem());					
	}
	
	/**
	 * Encrypted constructor
	 * @param pList The list to associate the Static Data with
	 * @param uId the id of the new item
	 * @param uControlId the control id of the new item
	 * @param uClassId the class id of the new item
	 * @param pValue the encrypted name of the new item
	 * @param pDesc the encrypted description of the new item
	 */
	protected StaticData(StaticList<T,E> 	pList, 
					  	 int 				uId,
					  	 int 				uControlId,
					  	 int 				uClassId,
					  	 byte[]				pValue,
					  	 byte[]				pDesc) throws Exception {
		super(pList, uId);
		theEnumClass = pList.getEnumClass();
		parseEnumId(uClassId);
		Values myValues = new Values();
		setValues(myValues);
		
		/* Store the controlId */
		setControlKey(uControlId);
		
		/* Create the pairs for the name and description */
		myValues.setName(new StringPair(pValue));
		if (pDesc != null) myValues.setDesc(new StringPair(pDesc));

		/* Set the new Id */
		pList.setNewId(getItem());					
	}
	
	/**
	 * Parse enum type
	 * @param pValue
	 */
	private void parseEnumValue(String pValue) throws Exception {
		StaticInterface myIFace 	= null;
		E[] 			myValues 	= theEnumClass.getEnumConstants();
		
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
							    "Invalid value for " + theEnumClass.getSimpleName() +
								": " + pValue);
		
		/* Reject if class was wrong type */
		if (myIFace == null) 
			throw new Exception(ExceptionClass.DATA,
							    "Class: " + theEnumClass.getSimpleName() +
								" is not valid for StaticData");
	}
	
	/**
	 * Parse enum id
	 * @param pId
	 */
	private void parseEnumId(int pId) throws Exception {
		StaticInterface myIFace 	= null;
		E[] 			myValues 	= (E[])theEnumClass.getEnumConstants();
		
		/* Loop through the enum constants */
		for (E myValue: myValues) {
			/* Ensure that the class is of the right type */
			if (!(myValue instanceof StaticInterface)) 
				throw new Exception(ExceptionClass.DATA,
									"Class: " + theEnumClass.getSimpleName() +
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
							    "Invalid id for " + theEnumClass.getSimpleName() +
								": " + pId);		
	}
	
	/**
	 * Format a Static Data 
	 * @param pData the static data to format
	 * @return the formatted data
	 */
	public static String format(StaticData<?,?> pData) {
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
	public static boolean differs(StaticData<?,?> pCurr, StaticData<?,?> pNew) {
		return (((pCurr == null) && (pNew != null)) ||
				((pCurr != null) && 
				 ((pNew == null) || (!pCurr.equals(pNew)))));
	}

	/**
	 * Set a new name 
	 * @param pName the name 
	 */
	public void setName(String pName) throws Exception {
		if (pName != null) 	getObj().setName(new StringPair(pName));
		else 				getObj().setName(null);
	}

	/**
	 * Set a new description
	 * @param pDesc the description 
	 */
	public void setDescription(String pDesc) throws Exception {
		/* Set the appropriate value */
		if (pDesc != null) 	getObj().setDesc(new StringPair(pDesc));
		else 				getObj().setDesc(null);
	}

	/**
	 * Update StaticData from a StaticData extract  
	 * @param pData the updated item 
	 * @return whether changes have been made
	 */
	public boolean applyChanges(DataItem<?> pData) {
		StaticData<?,?>			myData 		= (StaticData<?,?>)pData;
		StaticData<?,?>.Values	myObj		= getObj();
		StaticData<?,?>.Values	myNew		= myData.getObj();
		boolean  				bChanged	= false;

		/* Store the current detail into history */
		pushHistory();

		/* Update the name if required */
		if (differs(myObj.getName(), myNew.getName())) 
			myObj.setName(myNew.getName());

		/* Update the description if required */
		if (differs(myObj.getDesc(), myNew.getDesc())) 
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
	 * Represents a list of StaticData objects. 
	 */
	public abstract static class StaticList<T extends StaticData<T, E>, E extends Enum<E>>  extends EncryptedList<T> {
		/**
		 * Obtain the enumClass
		 * @return the enumClass
		 */
		protected abstract Class<E>		getEnumClass();
		
		/** 
	 	 * Construct a generic static data list
	 	 * @param enumClass the static class of the items
	 	 * @param pPairs the encrypted pair control  
	 	 * @param pStyle the style of the list 
	 	 */
		public StaticList(Class<T>		pClass,
						  DataSet<?>	pData,
						  ListStyle 	pStyle) { 
			super(pClass, pData, pStyle);
		}

		/**
		 * Constructor for a cloned List
		 * @param pSource the source List
		 */
		protected StaticList(StaticList<T, E> pSource) { 
			super(pSource);
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
	public class Values extends EncryptedValues {
		private StringPair	theName      = null;
		private StringPair	theDesc      = null;
		
		/* Access methods */
		public StringPair  	getName()      	{ return theName; }
		public StringPair  	getDesc()      	{ return theDesc; }
		private	Class<E>   	getEnumClass()	{ return theEnumClass; }
		public String  		getNameValue()  { return getPairValue(theName); }
		public String  		getDescValue()  { return getPairValue(theDesc); }
		public byte[]  		getNameBytes()  { return getPairBytes(theName); }
		public byte[]  		getDescBytes()  { return getPairBytes(theDesc); }
		
		/* Value setting */
		public void setName(StringPair pName) { theName = pName; }
		public void setDesc(StringPair pDesc) { theDesc = pDesc; }

		/* Constructor */
		public Values() {}
		public Values(StaticData<?,?>.Values pValues) {
			theName      = new StringPair(pValues.getName());
			if (pValues.getDesc() != null)
				theDesc  = new StringPair(pValues.getDesc());
		}
		
		/* Check whether this object is equal to that passed */
		public boolean histEquals(HistoryValues<T> pCompare) {
			/* Make sure that the object is the same class */
			if (pCompare.getClass() != this.getClass()) return false;
			
			/* Cast correctly */
			StaticData<?,?>.Values myValues = (StaticData<?,?>.Values)pCompare;
			
			/* Make sure that the object is the same enumeration class */
			if (myValues.getEnumClass() != theEnumClass) return false;
			
			/* Compare the values */
			if (!super.histEquals(pCompare))			return false;
			if (differs(theName,    myValues.theName))  return false;
			if (differs(theDesc,    myValues.theDesc))  return false;
			return true;
		}
		
		/* Copy values */
		public HistoryValues<T> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			StaticData<?,?>.Values myValues = (StaticData<?,?>.Values)pSource;
			copyFrom(myValues);
		}
		public void    copyFrom(Values pValues) {
			super.copyFrom(pValues);
			theName      = pValues.getName();
			theDesc      = pValues.getDesc();
		}
		public boolean	fieldChanged(int fieldNo, HistoryValues<T> pOriginal) {
			StaticData<?,?>.Values 	pValues = (StaticData<?,?>.Values)pOriginal;
			boolean	bResult = false;
			switch (fieldNo) {
				case FIELD_NAME:
					bResult = (differs(theName,      pValues.theName));
					break;
				case FIELD_DESC:
					bResult = (differs(theDesc,      pValues.theDesc));
					break;
				default:
					bResult = super.fieldChanged(fieldNo, pOriginal);
					break;
			}
			return bResult;
		}

		/**
		 * Ensure encryption after security change
		 */
		protected void applySecurity() throws Exception {
			/* Apply the encryption */
			theName.encryptPair();
			if (theDesc != null) theDesc.encryptPair();
		}		
		
		/**
		 * Adopt encryption from base
		 * @param pBase the Base values
		 */
		protected void adoptSecurity(ControlKey pControl, EncryptedValues pBase) throws Exception {
			StaticData<?,?>.Values myBase = (StaticData<?,?>.Values)pBase;
			
			/* Apply the encryption */
			theName.encryptPair(myBase.getName());
			if (theDesc != null) theDesc.encryptPair(myBase.getDesc());
		}		
	}
}

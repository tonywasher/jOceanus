/*******************************************************************************
 * Copyright 2012 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package uk.co.tolcroft.models.data;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.help.DebugDetail;

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
	 * Return the name of the Static Data
	 * @return the name
	 */
	public String    	getName() 			{ return getValues().getNameValue(); }

	/**
	 * Return the encrypted name of the Static Data
	 * @return the encrypted name
	 */
	public byte[] 		getNameBytes() 		{ return getValues().getNameBytes(); }
	
	/**
	 * Return the description of the Static Data
	 * @return the description
	 */
	public String    	getDesc()			{ return getValues().getDescValue(); }

	/**
	 * Return the encrypted description of the Static Data
	 * @return the encrypted description
	 */
	public byte[] 		getDescBytes() 		{ return getValues().getDescBytes(); }
	
	/**
	 * Return the sort order of the Static Data
	 * @return the order
	 */
	public int			getOrder()			{ return getValues().getOrder(); }

	/**
	 * Return the Static class of the Static Data
	 * @return the class
	 */
	public E			getStaticClass()	{ return getValues().getStaticClass(); }
	
	/**
	 * Is the Static item enabled
	 * @return <code>true/false</code>
	 */
	public boolean		getEnabled()		{ return getValues().getEnabled(); }
	
	/* Linking methods */
	@SuppressWarnings("unchecked")
	@Override
	public StaticValues<T,E>  getValues()  		{ return (StaticValues<T,E>)super.getValues(); }	
	protected	Class<E>   	  getEnumClass()	{ return getValues().getEnumClass(); }

	/* Field IDs */
	public static final int FIELD_NAME     	= EncryptedItem.NUMFIELDS;
	public static final int FIELD_DESC     	= EncryptedItem.NUMFIELDS+1;
	public static final int FIELD_ENABLED	= EncryptedItem.NUMFIELDS+2;
	public static final int FIELD_ORDER    	= EncryptedItem.NUMFIELDS+3;
	public static final int FIELD_CLASS    	= EncryptedItem.NUMFIELDS+4;
	public static final int NUMFIELDS	   	= EncryptedItem.NUMFIELDS+5;
	
	@Override
	public int	numFields() {return NUMFIELDS; }
	
	/**
	 * Determine the field name for a particular field
	 * @return the field name
	 */
	public static String	fieldName(int iField) {
		switch (iField) {
			case FIELD_NAME:    return "Name";
			case FIELD_DESC:    return "Description";
			case FIELD_ENABLED:	return "isEnabled";
			case FIELD_ORDER:  	return "SortOrder";
			case FIELD_CLASS:   return "Class";
			default:		    return EncryptedItem.fieldName(iField);
		}
	}
	
	@Override
	public String getFieldName(int iField) { return fieldName(iField); }
	
	@Override
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<T> pObj) {
		String myString = ""; 
		StaticValues<?,?>	myObj 	 = (StaticValues<?,?>)pObj;
		switch (iField) {
			case FIELD_NAME:	myString += myObj.getNameValue(); 	break;
			case FIELD_DESC:	myString += myObj.getDescValue(); 	break;
			case FIELD_ENABLED:	myString += myObj.getEnabled() ? true : false; 	break;
			case FIELD_ORDER: 	myString += myObj.getOrder();	break;
			case FIELD_CLASS: 	myString += myObj.getStaticClass();	break;
			default:			myString += super.formatField(pDetail, iField, pObj); break;
		}
		return myString;
	}
	
	@Override
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is the same class */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target StaticData */
		StaticData<?,?> myData = (StaticData<?,?>)pThat;

		/* Make sure that the object is the same enumeration class */
		if (myData.getEnumClass() != this.getEnumClass()) 	return false;
		
		/* Access the target StaticData as exact item */
		@SuppressWarnings("unchecked")
		StaticData<T,E> myThat = (StaticData<T,E>)pThat;

		/* Compare the id and class */
		if (getId() 			!= myThat.getId()) 			return false;
		if (getStaticClass() 	!= myThat.getStaticClass())	return false;
		
		/* Compare the changeable values */
		return getValues().histEquals(myThat.getValues()).isIdentical();
	}

	@Override
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
		if (myThat.getEnumClass() != this.getEnumClass()) return -1;
		
		/* Compare on order */
		if (getOrder() < myThat.getOrder()) return -1;
		if (getOrder() > myThat.getOrder()) return  1;

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

	@Override
	public void validate() {
		StaticList<?,?,?>	myList = (StaticList<?,?,?>)getList();

		/* Name must be non-null */
		if (getName() == null) {
			addError("Name must be non-null", FIELD_NAME);
	    }
		
		/* Check that the name is unique */
		else { 
			/* The description must not be too long */
			if (getName().length() > NAMELEN) {
				addError("Name is too long", FIELD_NAME);
			}
				
			if (myList.countInstances(getName()) > 1) {
				addError("Name must be unique", FIELD_NAME);
			}
	    }
		
		/* The order must not be negative */
		if (getOrder() < 0) {
			addError("Order is negative", FIELD_ORDER);
		}
			
		if (myList.countInstances(getOrder()) > 1) {
			addError("Order must be unique", FIELD_ORDER);
		}
				
		/* Set validation flag */
		if (!hasErrors()) setValidEdit();
	}
	
	@Override
	protected HistoryValues<T> getNewValues() { return new StaticValues<T,E>(); }
	
	/**
	 * Construct a copy of a Static data.
	 * @param pList	The list to associate the Static Data with
	 * @param pSource The static data to copy 
	 */
	protected StaticData(StaticList<?,T,E>	pList,
			             StaticData<T,E>	pSource) { 
		super(pList, pSource.getId());
		StaticValues<T,E> myValues = getValues();
		myValues.copyFrom(pSource.getValues());
		ListStyle myOldStyle = pSource.getStyle();

		/* Switch on the ListStyle */
		switch (getStyle()) {
			case EDIT:
				/* If this is a view creation */
				if (myOldStyle == ListStyle.CORE) {
					/* Static is based on the original element */
					setBase(pSource);
					copyFlags(pSource.getItem());
					pList.setNewId(getItem());				
					break;
				}
				
				/* Else this is a duplication so treat as new item */
				setId(0);
				pList.setNewId(getItem());				
				break;
			case CLONE:
				reBuildLinks(pList.getData());
			case COPY:
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
	 * @param pValue the name of the new item
	 */
	protected StaticData(StaticList<?,T,E> 	pList, 
					  	 String				pValue) throws ModelException {
		super(pList, 0);
		StaticValues<T,E> myValues = getValues();
		myValues.setEnumClass(pList.getEnumClass());
		parseEnumValue(pValue);

		/* Record the name */
		myValues.setName(pValue);

		/* Set the new Id */
		pList.setNewId(getItem());					
	}
	
	/**
	 * Clear Text constructor
	 * @param pList The list to associate the Static Data with
	 * @param uId the id of the new item
	 * @param isEnabled is the account type enabled
	 * @param uOrder the sort order
	 * @param pValue the name of the new item
	 * @param pDesc the description of the new item
	 */
	protected StaticData(StaticList<?,T,E> 	pList,
						 int				uId,
					  	 boolean			isEnabled,
					  	 int				uOrder,
					  	 String				pValue,
					  	 String				pDesc) throws ModelException {
		super(pList, uId);
		StaticValues<T,E> myValues = getValues();
		myValues.setEnumClass(pList.getEnumClass());
		parseEnumId(uId);
				
		/* Set enabled flag */
		myValues.setEnabled(isEnabled);
		myValues.setOrder(uOrder);
		
		/* Record the name and description */
		myValues.setName(pValue);
		myValues.setDesc(pDesc);

		/* Set the new Id */
		pList.setNewId(getItem());					
	}
	
	/**
	 * Encrypted constructor
	 * @param pList The list to associate the Static Data with
	 * @param uId the id of the new item
	 * @param uControlId the control id of the new item
	 * @param isEnabled is the account type enabled
	 * @param uOrder the sort order
	 * @param pValue the encrypted name of the new item
	 * @param pDesc the encrypted description of the new item
	 */
	protected StaticData(StaticList<?,T,E> 	pList, 
					  	 int 				uId,
					  	 int 				uControlId,
					  	 boolean			isEnabled,
					  	 int				uOrder,
					  	 byte[]				pValue,
					  	 byte[]				pDesc) throws ModelException {
		super(pList, uId);
		StaticValues<T,E> myValues = getValues();
		myValues.setEnumClass(pList.getEnumClass());
		parseEnumId(uId);
		
		/* Store the controlId */
		setControlKey(uControlId);

		/* Set enabled flag */
		myValues.setEnabled(isEnabled);
		myValues.setOrder(uOrder);
		
		/* Record the name and description */
		myValues.setName(pValue);
		myValues.setDesc(pDesc);

		/* Set the new Id */
		pList.setNewId(getItem());					
	}
	
	/**
	 * Parse enum type
	 * @param pValue
	 */
	private void parseEnumValue(String pValue) throws ModelException {
		StaticInterface myIFace 	= null;
		Class<E>		myClass		= getEnumClass();
		E[] 			myEnums 	= myClass.getEnumConstants();
		StaticValues<T,E> myValues	= getValues();
		
		/* Loop through the enum constants */
		for (E myValue: myEnums) {
			/* If this is the desired value */
			if (myValue.toString().equalsIgnoreCase(pValue)) {
				/* Store the class */
				myValues.setStaticClass(myValue);
				
				/* If the enum is of the desired type */
				if (myValue instanceof StaticInterface) {
					/* Access classId and order */
					myIFace		= (StaticInterface) myValue;
					setId(myIFace.getClassId());
					myValues.setOrder(myIFace.getOrder());
				}
				break;
			}
		}
		
		/* Reject if we didn't find the class */
		if (getStaticClass() == null) 
			throw new ModelException(ExceptionClass.DATA,
							    "Invalid value for " + myClass.getSimpleName() +
								": " + pValue);
		
		/* Reject if class was wrong type */
		if (myIFace == null) 
			throw new ModelException(ExceptionClass.DATA,
							    "Class: " + myClass.getSimpleName() +
								" is not valid for StaticData");
	}
	
	/**
	 * Parse enum id
	 * @param pId
	 */
	private void parseEnumId(int pId) throws ModelException {
		StaticInterface myIFace 		= null;
		Class<E>		myClass			= getEnumClass();
		E[] 			myEnums 		= myClass.getEnumConstants();
		StaticValues<T,E> myValues		= getValues();
		
		/* Loop through the enum constants */
		for (E myValue: myEnums) {
			/* Ensure that the class is of the right type */
			if (!(myValue instanceof StaticInterface)) 
				throw new ModelException(ExceptionClass.DATA,
									"Class: " + myClass.getSimpleName() +
									" is not valid for StaticData");
				
			/* Access via interface */
			myIFace		= (StaticInterface) myValue;
			
			/* If this is the desired value */
			if (myIFace.getClassId() == pId) {
				/* Store the class and details */
				myValues.setStaticClass(myValue);
				break;
			}
		}
		
		/* Reject if we didn't find the class */
		if (getStaticClass() == null) 
			throw new ModelException(ExceptionClass.DATA,
							    "Invalid id for " + myClass.getSimpleName() +
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
	 * Set a new name 
	 * @param pName the name 
	 */
	public void setName(String pName) throws ModelException {
		getValues().setName(pName);
	}

	/**
	 * Set a new description
	 * @param pDesc the description 
	 */
	public void setDescription(String pDesc) throws ModelException {
		/* Set the appropriate value */
		getValues().setDesc(pDesc);
	}

	/**
	 * Set Enabled indicator
	 * @param isEnabled  
	 */
	public void setEnabled(boolean isEnabled) {
		/* Set the appropriate value */
		getValues().setEnabled(isEnabled);
	}

	/**
	 * Set Order indicator
	 * @param iOrder the order 
	 */
	public void setOrder(int iOrder) {
		/* Set the appropriate value */
		getValues().setOrder(iOrder);
	}

	@Override
	public boolean applyChanges(DataItem<?> pData) {
		StaticData<?,?>		myData 		= (StaticData<?,?>)pData;
		StaticValues<?,?>	myObj		= getValues();
		StaticValues<?,?>	myNew		= myData.getValues();
		boolean  			bChanged	= false;

		/* Store the current detail into history */
		pushHistory();

		/* Update the name if required */
		if (Utils.differs(myObj.getName(), myNew.getName()).isDifferent()) 
			myObj.setName(myNew.getName());

		/* Update the description if required */
		if (Utils.differs(myObj.getDesc(), myNew.getDesc()).isDifferent()) 
			myObj.setDesc(myNew.getDesc());

		/* Update the enabled indication if required */
		if (myObj.getEnabled() != myNew.getEnabled()) 
			myObj.setEnabled(myNew.getEnabled());

		/* Update the order indication if required */
		if (myObj.getOrder() != myNew.getOrder()) 
			myObj.setOrder(myNew.getOrder());

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
	public abstract static class StaticList<L extends StaticList<L,T,E>,
											T extends StaticData<T, E>, 
											E extends Enum<E>>  extends EncryptedList<L,T> {
		/**
		 * Obtain the enumClass
		 * @return the enumClass
		 */
		protected abstract Class<E>		getEnumClass();
		
		/** 
	 	 * Construct a generic static data list
	 	 * @param pClass the class
	 	 * @param pBaseClass the class of the underlying object
	 	 * @param pData the dataSet  
	 	 * @param pStyle the style of the list 
	 	 */
		public StaticList(Class<L>		pClass,
						  Class<T>		pBaseClass,
						  DataSet<?>	pData,
						  ListStyle 	pStyle) { 
			super(pClass, pBaseClass, pData, pStyle);
		}

		/**
		 * Constructor for a cloned List
		 * @param pSource the source List
		 */
		protected StaticList(L pSource) { 
			super(pSource);
		}
		
		@Override
		public void setNewId(T pItem)	{ super.setNewId(pItem); }
		
		/**
		 * Search for a particular item by class
		 * @param eClass The class of the item to search for
		 * @return The Item if present (or <code>null</code> if not found)
		 */
		public T searchFor(E eClass) {
			DataListIterator<T>	myIterator;
			T					myCurr;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				if (myCurr.getStaticClass() == eClass) break;
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
			DataListIterator<T>	myIterator;
			T					myCurr;
			int         		iDiff;
			
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

		/**
		 * Count the instances of a string
		 * @param pName the string to check for
		 * @return The # of instances of the name
		 */
		protected int countInstances(String pName) {
			DataListIterator<T>	myIterator;
			T 					myCurr;
			int     			iDiff;
			int     			iCount = 0;
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				iDiff = pName.compareTo(myCurr.getName());
				if (iDiff == 0) iCount++;
			}
			
			/* Return to caller */
			return iCount;
		}	

		/**
		 * Count the instances of an Order
		 * @param iOrder the order to check for
		 * @return The # of instances of the order
		 */
		protected int countInstances(int iOrder) {
			DataListIterator<T> myIterator;
			T 					myCurr;
			int     			iCount = 0;
			
			/* Access the iterator */
			myIterator = listIterator(true);
			
			/* Loop through the items to find the entry */
			while ((myCurr = myIterator.next()) != null) {
				if (iOrder == myCurr.getOrder()) iCount++;
			}
			
			/* Return to caller */
			return iCount;
		}	
	}	
}

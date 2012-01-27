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

import uk.co.tolcroft.models.Difference;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedField;
import uk.co.tolcroft.models.help.DebugDetail;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.ModelException.ExceptionClass;

public abstract class EncryptedItem<T extends EncryptedItem<T>> extends DataItem<T> {
	/**
	 * Encrypted Money length
	 */
	public final static int MONEYLEN 		= 10;

	/**
	 * Encrypted Units length
	 */
	public final static int UNITSLEN 		= 10;

	/**
	 * Encrypted Rate length
	 */
	public final static int RATELEN 		= 10;

	/**
	 * Encrypted Price length
	 */
	public final static int PRICELEN 		= 10;

	/**
	 * Encrypted Dilution length
	 */
	public final static int DILUTELEN 		= 10;

	/**
	 * Get the ControlKey for this item
	 * @return the ControlKey
	 */
	public ControlKey		getControlKey()     { return getValues().getControlKey(); }

	/* Linking methods */
	public EncryptedValues<T>  getValues()  { return (EncryptedValues<T>)super.getCurrentValues(); }
	
	/* Field IDs */
	public static final int 	FIELD_CONTROL  	= DataItem.NUMFIELDS;
	public static final int 	NUMFIELDS	   	= FIELD_CONTROL+1; 
	public static final String 	NAME_CTLID		= "ControlId"; 

	/**
	 * Determine the field name for a particular field
	 * This method is the underlying method called when the id is unknown 
	 * @return the field name
	 */
	public static String	fieldName(int fieldId)	{
		switch (fieldId) {
			case FIELD_CONTROL:	return NAME_CTLID;
			default: 			return DataItem.fieldName(fieldId);
		}
	}
	
	@Override
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<T> pObj) {
		String 				myString = "";
		EncryptedValues<T> 	myObj 	 = (EncryptedValues<T>)pObj;
		switch (iField) {
			case FIELD_CONTROL:
				if (myObj.getControlKey() != null) {
					myString = "Id=" + myObj.getControlId();					
					myString = pDetail.addDebugLink(myObj.getControlKey(), myString);
				}
				else myString = "Id=" + myObj.getControlId();					
				break;
			default:	
				myString = super.formatField(pDetail, iField, pObj);
				break;
		}
		return myString;
	}
							
	/**
	 * Constructor
	 * @param pList the list that this item is associated with
	 * @param uId the Id of the new item (or 0 if not yet known)
	 */
	public EncryptedItem(EncryptedList<?,T> pList, int uId) {
		super(pList, uId);
	}

	/**
	 * Set ControlKey
	 * @param pControlKey the Control Key
	 */
	protected void setControlKey(ControlKey pControlKey) {
		EncryptedValues<T> myValues = getValues();
		if (myValues != null) myValues.setControlKey(pControlKey);
	}
	
	/**
	 * Set ControlKey
	 * @param uControlId the Control Id
	 */
	protected void setControlKey(int uControlId) throws ModelException {
		/* Store the id */
		EncryptedValues<T> myObj = getValues();
		myObj.setControlId(uControlId);

		/* Look up the Control keys */
		DataSet<?>		myData = ((EncryptedList<?,?>)getList()).getData();
		ControlKey.List myKeys = myData.getControlKeys();
			
		/* Look up the ControlKey */
		ControlKey myControl = myKeys.searchFor(uControlId);
		if (myControl == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid ControlKey Id");
		
		/* Store the ControlKey */
		setControlKey(myControl);
	}
	
	/**
	 * Determine whether two ValuePair objects differ.
	 * @param pCurr The current Pair
	 * @param pNew The new Pair
	 * @return <code>true</code> if the objects differ, <code>false</code> otherwise 
	 */	
	public static Difference differs(EncryptedField<?> pCurr, 
								  	 EncryptedField<?> pNew) {
		/* Handle case where current value is null */
		if  (pCurr == null) return (pNew != null) ? Difference.Different 
												  : Difference.Identical;
		
		/* Handle case where new value is null */
		if  (pNew == null) return Difference.Different;
		
		/* Handle Standard cases */
		return pCurr.differs(pNew);
	}
	
	/**
	 * Rebuild Links to partner data
	 * @param pData the DataSet
	 */
	protected void reBuildLinks(DataSet<?> pData) {
		ControlKey.List myKeys = pData.getControlKeys();
		
		/* Update to use the local copy of the ControlKeys */
		EncryptedValues<?>	myValues   	= getValues();
		ControlKey 	myKey 		= myValues.getControlKey();
		ControlKey 	myNewKey 	= myKeys.searchFor(myKey.getId());
		myValues.setControlKey(myNewKey);
	}

	/**
	 * Initialise security for all encrypted values
	 * @param pControl the new Control Key 
	 */	
	protected void adoptSecurity(ControlKey pControl,
								 T		  	pBase) throws ModelException {
		/* Access the values */
		EncryptedValues<T> myValues = getValues();
		
		/* Set the Control Key */
		myValues.setControlKey(pControl);
		
		/* If we have the same control key */
		if ((pBase != null) &&
			(pControl.equals(pBase.getControlKey())))
		{
			/* Try to adopy the underlying */
			myValues.adoptSecurity(pBase.getValues());
		}
		
		/* else we need to initialise security */
		else {
			/* Apply key to all elements */
			myValues.applySecurity();
		}
	}
	
	/**
	 * Update security for all encrypted values
	 * @param pControl the new Control Key 
	 */	
	protected void updateSecurity(ControlKey pControl) throws ModelException {
		/* Access the values */
		EncryptedValues<T> myValues = getValues();

		/* Ignore call if we have the same control key */
		if (pControl.equals(getControlKey()))
			return;
		
		/* Store the current detail into history */
		pushHistory();
		
		/* Set the Control Key */
		setControlKey(pControl);
		
		/* Update all elements */
		myValues.updateSecurity();

		/* Check for changes */
		setState(DataState.CHANGED);
	}
	
	/**
	 * Encrypted DataList
	 */
	public abstract static class EncryptedList<L extends EncryptedList<L,T>,
											   T extends EncryptedItem<T>>  extends DataList<L,T> {
		private DataSet<?>		theData			= null;
		public 	DataSet<?>		getData()		{ return theData; }
		protected 	void		setData(DataSet<?> pData)		{ theData = pData; }
		public 	ControlKey		getControlKey()	{ return theData.getControl().getControlKey(); }

		/** 
	 	 * Construct an empty CORE encrypted list
	 	 * @param pClass the class
	 	 * @param pBaseClass the class of the underlying object
	 	 * @param pData the DataSet for the list
	 	 */
		protected EncryptedList(Class<L>	pClass,
								Class<T> 	pBaseClass,
								DataSet<?> 	pData) { 
			super(pClass, pBaseClass, ListStyle.CORE, true);
			theData = pData;
		}

		/** 
	 	 * Construct a generic encrypted list
	 	 * @param pClass the class
	 	 * @param pBaseClass the class of the underlying object
	 	 * @param pData the DataSet for the list
	 	 * @param pStyle the style of the list 
	 	 */
		public EncryptedList(Class<L>		pClass,
							 Class<T> 		pBaseClass,
							 DataSet<?> 	pData,
							 ListStyle 		pStyle) { 
			super(pClass, pBaseClass, pStyle, true);
			theData = pData;
			setGeneration(pData.getGeneration());
		}

		/**
		 * Constructor for a cloned List
		 * @param pSource the source List
		 */
		protected EncryptedList(L pSource) { 
			super(pSource);
			theData = pSource.getData();
		}
		
		/**
		 * Update Security for items in the list
		 * @param pThread the thread status
		 * @param pControl the control key to apply
		 * @return Continue <code>true/false</code>
		 */
		public boolean updateSecurity(ThreadStatus<?>	pThread,
			   	  					  ControlKey 		pControl) throws ModelException {
			DataListIterator<T> myIterator;
			T					myCurr;
			int					mySteps;
			int					myCount = 0;
			
			/* Declare the new stage */
			if (!pThread.setNewStage(itemType())) return false;

			/* Access reporting steps */
			mySteps = pThread.getReportingSteps();

			/* Count the Number of items */
			if (!pThread.setNumSteps(sizeAll())) return false;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the items */
			while ((myCurr = myIterator.next()) != null) {
				/* Ensure encryption of the item */
				myCurr.updateSecurity(pControl);
				
				/* Report the progress */
				myCount++;
				if ((myCount % mySteps) == 0) 
					if (!pThread.setStepsDone(myCount)) return false;
			}
			
			/* Return to caller */
			return true;
		}	

		/**
		 * Adopt security from underlying list.
		 * If a match for the item is found in the underlying list, its security is adopted.
		 * If no match is found then the security is initialised.
		 * @param pThread the thread status
		 * @param pControl the control key to initialise from
		 * @param pBase The base list to adopt from 
		 * @return Continue <code>true/false</code>
		 */
		protected boolean adoptSecurity(ThreadStatus<?>		pThread,
			   	  						ControlKey 			pControl,
								     	EncryptedList<?,?> 	pBase) throws ModelException {
			/* Local variables */
			DataListIterator<T> myIterator;
			EncryptedItem<T>	myCurr;
			EncryptedItem<?>	myBase;
			T					mySource;
			T					myTarget;
			Class<T>			myClass	= getBaseClass();
			int					mySteps;
			int					myCount = 0;
			
			/* Declare the new stage */
			if (!pThread.setNewStage(itemType())) return false;

			/* Access reporting steps */
			mySteps = pThread.getReportingSteps();

			/* Count the Number of items */
			if (!pThread.setNumSteps(sizeAll())) return false;
			
			/* Create an iterator for our new list */
			myIterator = listIterator(true);
			
			/* Loop through this list */
			while ((myCurr = myIterator.next()) != null) {
				/* Locate the item in the base list */
				myBase = pBase.searchFor(myCurr.getId());
				
				/* Cast the items correctly */
				mySource = (myBase == null) ? null : myClass.cast(myBase);
				myTarget = myClass.cast(myCurr);
				
				/* Adopt/initialise the security */
				myTarget.adoptSecurity(pControl, mySource);
				
				/* Report the progress */
				myCount++;
				if ((myCount % mySteps) == 0) 
					if (!pThread.setStepsDone(myCount)) return false;
			}
			
			/* Return to caller */
			return true;
		}
		
		@Override
		public void setNewId(T pItem)	{ super.setNewId(pItem); }
	}
}

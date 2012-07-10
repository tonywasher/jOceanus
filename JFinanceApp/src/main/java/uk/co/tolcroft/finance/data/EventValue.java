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
package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.Difference;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.help.DebugDetail;

public class EventValue extends DataItem<EventValue>{
	/**
	 * The name of the object
	 */
	public static final String objName = "EventValue";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";
	
	/* Access methods */
	public  Values     		getValues()     { return (Values)super.getCurrentValues(); }	
	public  Event			getEvent()		{ return getValues().getEvent(); }
	public  EventInfoType	getInfoType()	{ return getValues().getInfoType(); }
	public  Integer			getValue()		{ return getValues().getValue(); }
	public  Account			getAccount()	{ return getValues().getAccount(); }

	/* Linking methods */
	public EventValue	getBase() { return (EventValue)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_INFOTYPE	= DataItem.NUMFIELDS;
	public static final int FIELD_EVENT		= DataItem.NUMFIELDS+1;
	public static final int FIELD_VALUE		= DataItem.NUMFIELDS+2;
	public static final int NUMFIELDS	   	= DataItem.NUMFIELDS+3;

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
			case FIELD_INFOTYPE:	return "InfoType";
			case FIELD_EVENT:		return "Event";
			case FIELD_VALUE:		return "Value";
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
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<EventValue> pValues) {
		String 	myString = "";
		Values 	myValues = (Values)pValues;
		EventInfoType myType = myValues.getInfoType();
		switch (iField) {
			case FIELD_INFOTYPE:
				if ((myValues.getInfoType() == null) &&
					(myValues.getInfTypId() != null))
					myString += "Id=" + myValues.getInfTypId();
				else
					myString += EventInfoType.format(myValues.getInfoType()); 
				myString = pDetail.addDebugLink(myValues.getInfoType(), myString);
				break;
			case FIELD_EVENT:
				if ((myValues.getEvent() == null) &&
					(myValues.getEventId() != null))
					myString += "Id=" + myValues.getEventId();
				else
					myString += Event.format(myValues.getEvent()); 
				myString = pDetail.addDebugLink(myValues.getEvent(), myString);
				break;
			case FIELD_VALUE:
				if (myType == null) break;
				switch(myType.getInfoClass()) {
					case XferDelay:
					case QualifyYears:
						myString += myValues.getValue(); 
						break;
					case CashAccount:
						if ((myValues.getAccount() == null) &&
							(myValues.getValue() != null))
							myString += "Id=" + myValues.getValue();
						else
							myString += Account.format(myValues.getAccount()); 
						myString = pDetail.addDebugLink(myValues.getAccount(), myString);
						break;
				}
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
	protected HistoryValues<EventValue> getNewValues() { return new Values(); }
	
	/**
	 * Construct a copy of an EventInfo 
	 * @param pPeriod The Period to copy 
	 */
	protected EventValue(List pList, EventValue pInfo) {
		/* Set standard values */
		super(pList, pInfo.getId());
		Values myValues = getValues();
		myValues.copyFrom(pInfo.getValues());
		ListStyle myOldStyle = pInfo.getStyle();

		/* Switch on the ListStyle */
		switch (getStyle()) {
			case EDIT:
				/* If this is a view creation */
				if (myOldStyle == ListStyle.CORE) {
					/* Rate is based on the original element */
					setBase(pInfo);
					pList.setNewId(this);				
					break;
				}
				
				/* Else this is a duplication so treat as new item */
				setId(0);
				pList.setNewId(this);				
				break;
			case CLONE:
				reBuildLinks(pList.getData());
			case COPY:
			case CORE:
				/* Reset Id if this is an insert from a view */
				if (myOldStyle == ListStyle.EDIT) setId(0);
				pList.setNewId(this);				
				break;
			case UPDATE:
				setBase(pInfo);
				setState(pInfo.getState());
				break;
		}
	}

	/* Encryption constructor */
	private EventValue(List     pList,
				       int		uId,
				       int 		uInfoTypeId,
				       int		uEventId, 
				       Integer	pValue) throws ModelException {
		/* Initialise the item */
		super(pList, uId);
		
		/* Initialise the values */
		Values myValues = getValues();

		/* Record the Id */
		myValues.setInfTypId(uInfoTypeId);
		myValues.setEventId(uEventId);
		
		/* Look up the EventType */
		FinanceData myData		= pList.getData();
		EventInfoType myType	= myData.getInfoTypes().searchFor(uInfoTypeId);
		if (myType == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid EventInfoType Id");
		myValues.setInfoType(myType);
					
		/* Look up the Event */
		Event myEvent	= myData.getEvents().searchFor(uEventId);
		if (myEvent == null) 
			throw new ModelException(ExceptionClass.DATA,
								this,
								"Invalid Event Id");
		myValues.setEvent(myEvent);

		/* Switch on Info Class */
		switch(myType.getInfoClass()) {
			case QualifyYears:
			case XferDelay:
				myValues.setInteger(pValue);
				break;
			case CashAccount:
				/* Look up the Account */
				Account myAccount	= myData.getAccounts().searchFor(pValue);
				if (myAccount == null) 
					throw new ModelException(ExceptionClass.DATA,
										this,
										"Invalid Account Id");
				myValues.setAccount(myAccount);
				break;
		}

		/* Access the EventInfoSet and register this value */
		EventInfoSet mySet = myEvent.getInfoSet();
		mySet.registerValue(this);
		
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/* Edit constructor */
	private EventValue(List     		pList,
				       EventInfoType 	pType,
				       Event			pEvent) {
		/* Initialise the item */
		super(pList, 0);
		
		/* Initialise the values */
		Values myValues = getValues();

		/* Record the Detail */
		myValues.setInfoType(pType);
		myValues.setEvent(pEvent);

		/* Allocate the id */
		pList.setNewId(this);				
	}

	@Override
	public void deRegister() {
		/* Access the EventInfoSet and register this value */
		EventInfoSet mySet = getEvent().getInfoSet();
		mySet.deRegisterValue(this);		
	}
	
	/**
	 * Compare this EventInfo to another to establish equality.
	 * @param pThat The Rate to compare to
	 * @return <code>true</code> if the rate is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is an EventInfo */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as an EventInfo */
		EventValue myThat = (EventValue)pThat;
		
		/* Check for equality on id */
		if (getId() != myThat.getId()) return false;
		
		/* Compare the changeable values */
		return getValues().histEquals(myThat.getValues()).isIdentical();
	}

	/**
	 * Compare this value to another to establish sort order. 
	 * @param pThat The EventValue to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		int iDiff;
		
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is an EventValue */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as an EventValue */
		EventValue myThat = (EventValue)pThat;

		/* Compare the Events */
		iDiff = getEvent().compareTo(myThat.getEvent());
		if (iDiff != 0) return iDiff;

		/* Compare the Info Types */
		iDiff = getInfoType().compareTo(myThat.getInfoType());
		if (iDiff != 0) return iDiff;

		/* Compare the IDs */
		iDiff = (int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}

	/**
	 * Rebuild Links to partner data
	 * @param pData the DataSet
	 */
	protected void reBuildLinks(FinanceData pData) {
		/* Access Events and InfoTypes */
		Event.List 			myEvents 	= pData.getEvents();
		EventInfoType.List 	myTypes 	= pData.getInfoTypes();
		
		/* Update to use the local copy of the Types */
		Values 	myValues   			= getValues();
		EventInfoType	myType		= myValues.getInfoType();
		EventInfoType	myNewType 	= myTypes.searchFor(myType.getId());
		myValues.setInfoType(myNewType);
		
		/* Update to use the local copy of the Events */
		Event	myEvent		= myValues.getEvent();
		Event	myNewEvt 	= myEvents.searchFor(myEvent.getId());
		myValues.setEvent(myNewEvt);
	}

	/**
	 * Validate the Event Info
	 */
	public void validate() {
		EventInfoType	myType 		= getInfoType();
		Event			myEvent		= getEvent();
		Values			myValues	= getValues();

		/* Event must be non-null */
		if (myEvent == null) {
			addError("Event must be non-null", FIELD_EVENT);
		}

		/* InfoType must be non-null */
		if (myType == null) {
			addError("EventInfoType must be non-null", FIELD_INFOTYPE);
		}
		else if (!myType.getEnabled()) 
			addError("EventInfoType must be enabled", FIELD_INFOTYPE);
		else {
			/* Switch on Info Class */
			switch(myType.getInfoClass()) {
				case QualifyYears:
				case XferDelay:
					if (myValues.getValue() == null)
						addError(myType.getName() + " must be non-null", FIELD_VALUE);
					else if (myValues.getValue() <= 0)
						addError(myType.getName() + " must be positive", FIELD_VALUE);
					break;
				case CashAccount:
					Account myAccount = myValues.getAccount();
					if (myAccount == null)
						addError(myType.getName() + " must be non-null", FIELD_VALUE);
					else if (!myAccount.isMoney())
						addError(myType.getName() + " must be money account", FIELD_VALUE);
					break;
			}
		}
		
		/* Set validation flag */
		if (!hasErrors()) setValidEdit();
	}

	/**
	 * Format an Event Value 
	 * @param pValue the value to format
	 * @return the formatted value
	 */
	public static String format(EventValue pValue) {
		Integer myValue;
		
		/* If we have null, return it */
		if ((pValue == null) || ((myValue = pValue.getValue()) == null))
			return "null";
		
		/* Switch on type of Value */
		switch (pValue.getInfoType().getInfoClass()) {
			case CashAccount:
				return Account.format(pValue.getAccount());
			case QualifyYears:
			case XferDelay:
				return myValue.toString();
			default:
				return "null";
		}
	}

	/**
	 * Set Value 
	 * @param pValue the Value
	 */
	protected void setValue(Integer pValue) throws ModelException {
		Values			myValues	= getValues();

		/* Switch on Info type */
		switch (getInfoType().getInfoClass()) {
			case QualifyYears:
			case XferDelay:
				myValues.setInteger(pValue);
				break;
			default:
				throw new ModelException(ExceptionClass.LOGIC,
									this,
									"Invalid Attempt to set Integer value");
		}
	}
	
	/**
	 * Set Account 
	 * @param pAccount the Account
	 */
	protected void setAccount(Account pValue) throws ModelException {
		Values			myValues	= getValues();

		/* Switch on Info type */
		switch (getInfoType().getInfoClass()) {
			case CashAccount:
				myValues.setAccount(pValue);
				break;
			default:
				throw new ModelException(ExceptionClass.LOGIC,
									this,
									"Invalid Attempt to set Account value");
		}
	}
	
	/* List class */
	public static class List  	extends DataList<List, EventValue> {
		/* Access Extra Variables correctly */
		private FinanceData	theData			= null;
		public 	FinanceData getData()		{ return theData; }
		
		/** 
		 * Construct an empty CORE list
	 	 * @param pData the DataSet for the list
		 */
		protected List(FinanceData pData) { 
			super(List.class, EventValue.class, ListStyle.CORE, false);
			theData = pData;
			setGeneration(pData.getGeneration());
		}

		/** 
		 * Construct an empty list
	 	 * @param pData the DataSet for the list
	 	 * @param pStyle the required style
		 */
		protected List(FinanceData pData, ListStyle pStyle) { 
			super(List.class, EventValue.class, pStyle, false);
			theData = pData;
			setGeneration(pData.getGeneration());
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
		public List getUpdateList() { return getExtractList(ListStyle.UPDATE); }
		public List getEditList() 	{ return getExtractList(ListStyle.EDIT); }
		public List getShallowCopy() 	{ return getExtractList(ListStyle.COPY); }
		public List getDeepCopy(DataSet<?> pDataSet)	{ 
			/* Build an empty Extract List */
			List myList = new List(this);
			myList.theData = (FinanceData)pDataSet;
			
			/* Obtain underlying clones */
			myList.populateList(ListStyle.CLONE);
			myList.setStyle(ListStyle.CORE);
			
			/* Return the list */
			return myList;
		}

		/** 
		 * Construct a difference Info list
		 * @param pNew the new Info list 
		 * @param pOld the old Info list 
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
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }

		/**
		 *  Allow an EventInfo to be loaded
		 */
		public void addItem(int     		uId,
							int  	 		uInfoTypeId,
							int				uEventId,
							Integer			pValue) throws ModelException {
			EventValue  	myInfo;
			
			/* Create the info */
			myInfo	= new EventValue(this, uId, uInfoTypeId,
					                   uEventId, pValue);
				
			/* Check that this InfoId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new ModelException(ExceptionClass.DATA,
						            myInfo,
			  			            "Duplicate ValueId");
			 
			/* Validate the information */
			myInfo.validate();

			/* Handle validation failure */
			if (myInfo.hasErrors()) 
				throw new ModelException(ExceptionClass.VALIDATE,
									myInfo,
									"Failed validation");
				
			/* Add to the list */
			add(myInfo);
		}

		/**
		 * Add new item type (into edit session)
		 * @param pType the Item Type
		 * @param pEvent the Event
		 */
		protected EventValue addNewItem(EventInfoType 	pType,
										Event			pEvent) {
			/* Create the new Value */
			EventValue myValue = new EventValue(this, pType, pEvent);
			
			/* Add it to the list and return */
			add(myValue);
			return myValue;
		}
		
		@Override
		public EventValue addNewItem(DataItem<?> pElement) {
			/* Create the new item */
			EventValue mySource	= (EventValue)pElement;
			EventValue myInfo 	= new EventValue(this, mySource);
			
			/* Add to list and return */
			add(myInfo);
			return myInfo;
		}

		@Override
		public EventValue addNewItem() { return null; }
	}

	/* EventInfoValues */
	public class Values extends HistoryValues<EventValue> {
		private EventInfoType	theInfoType	= null;
		private Event			theEvent    = null;
		private Integer			theInteger	= null;
		private Account    		theAccount	= null;
		private Integer 		theEventId	= null;
		private Integer 		theInfTypId	= null;

		/* Access methods */
		public EventInfoType	getInfoType()		{ return theInfoType; }
		public Event			getEvent()			{ return theEvent; }
		public Account			getAccount()		{ return theAccount; }
		public Integer      	getValue()    		{ return theInteger; }
		private Integer        	getEventId()   		{ return theEventId; }
		private Integer        	getInfTypId()    	{ return theInfTypId; }

		public void setInfoType(EventInfoType pType) {
			theInfoType	= pType;  
			theInfTypId = (pType == null) ? null : pType.getId();}
		public void setEvent(Event pEvent) {
			theEvent	= pEvent;  
			theEventId = (pEvent == null) ? null : pEvent.getId();}
		public void setInteger(Integer pValue) {
			theInteger	= pValue; }
		public void setAccount(Account pAccount) {
			theAccount  = pAccount; 
			theInteger	= (pAccount == null) ? null : pAccount.getId(); }
		private void setEventId(Integer pEventId) {
			theEventId	= pEventId; } 
		private void setInfTypId(Integer pInfTypId) {
			theInfTypId = pInfTypId; } 

		/* Constructor */
		public Values() {}
		public Values(Values pValues) { copyFrom(pValues); }

		/* Check whether this object is equal to that passed */
		public Difference histEquals(HistoryValues<EventValue> pCompare) {
			/* Make sure that the object is the same class */
			if (pCompare.getClass() != this.getClass()) return Difference.Different;
			
			/* Access as correct class */
			Values 		myValues = (Values)pCompare;
			Difference 	myDifference;
			
			/* Check for integer differences */
			if ((Utils.differs(theInteger,  myValues.theInteger).isDifferent())		||
				(Utils.differs(theEventId, 	myValues.theEventId).isDifferent())		||
				(Utils.differs(theInfTypId,	myValues.theInfTypId).isDifferent()))
				return Difference.Different;
			
			/* Compare underlying items */
			myDifference = differs(theEvent, myValues.theEvent);
			myDifference = myDifference.combine(differs(theInfoType, myValues.theInfoType));
			
			/* Return the differences */
			return myDifference;
		}

		/* Copy values */
		public HistoryValues<EventValue> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			Values myValues = (Values)pSource;
			theInfoType		= myValues.getInfoType();
			theEvent		= myValues.getEvent();
			theInteger		= myValues.getValue();
			theAccount   	= myValues.getAccount();
			theEventId   	= myValues.getEventId();
			theInfTypId    	= myValues.getInfTypId();
		}
		public Difference	fieldChanged(int fieldNo, HistoryValues<EventValue> pOriginal) {
			Values 		pValues = (Values)pOriginal;	
			Difference	bResult = Difference.Identical;
			switch (fieldNo) {
				case FIELD_INFOTYPE:
					bResult = (differs(theInfoType,	pValues.theInfoType));
					break;
				case FIELD_EVENT:
					bResult = (differs(theInfoType,	pValues.theEvent));
					break;
				case FIELD_VALUE:
					bResult = (Utils.differs(theInteger, pValues.theInteger));
					break;
			}
			return bResult;
		}
	}
}

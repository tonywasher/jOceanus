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
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.Decimal.Dilution;
import uk.co.tolcroft.models.Decimal.Money;
import uk.co.tolcroft.models.Decimal.Units;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.EncryptedData;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedDecimal;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedDilution;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedMoney;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedUnits;
import uk.co.tolcroft.models.data.EncryptedItem;
import uk.co.tolcroft.models.data.EncryptedValues;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.help.DebugDetail;

public class EventData extends EncryptedItem<EventData> {
	/**
	 * The name of the object
	 */
	public static final String objName = "EventData";

	/**
	 * The name of the object
	 */
	public static final String listName = objName;
	
	/* Access methods */
	public  Values     		getValues()     { return (Values)super.getValues(); }	
	public  Event			getEvent()		{ return getValues().getEvent(); }
	public  EventInfoType	getInfoType()	{ return getValues().getInfoType(); }
	public  byte[]			getData()		{ return getValues().getData(); }
	public  Money			getMoney()		{ return getValues().getMoneyValue(); }
	public  Units			getUnits()		{ return getValues().getUnitsValue(); }
	public  Dilution		getDilution()	{ return getValues().getDilutionValue(); }

	/* Linking methods */
	public EventData	getBase() { return (EventData)super.getBase(); }
	
	/* Field IDs */
	public static final int FIELD_INFOTYPE	= EncryptedItem.NUMFIELDS;
	public static final int FIELD_EVENT		= EncryptedItem.NUMFIELDS+1;
	public static final int FIELD_VALUE 	= EncryptedItem.NUMFIELDS+2;
	public static final int NUMFIELDS	   	= EncryptedItem.NUMFIELDS+3;

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
			default:		  		return EncryptedItem.fieldName(iField);
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
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<EventData> pValues) {
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
					case TaxCredit:
					case NatInsurance:
					case Benefit:
					case Pension:
					case CashConsider:
						myString += Money.format(myValues.getMoneyValue()); 
						break;
					case Dilution:
						myString += Dilution.format(myValues.getDilutionValue()); 
						break;
					case CreditUnits:
					case DebitUnits:
						myString += Units.format(myValues.getUnitsValue()); 
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
	protected HistoryValues<EventData> getNewValues() { return new Values(); }
	
	/**
	 * Construct a copy of an EventInfo 
	 * @param pPeriod The Period to copy 
	 */
	protected EventData(List pList, EventData pInfo) {
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
	private EventData(List		pList,
				      int		uId,
				      int		uControlId,
				      int 		uInfoTypeId,
				      int		uEventId, 
				      byte[]	pValue) throws ModelException {
		/* Initialise the item */
		super(pList, uId);
		
		/* Initialise the values */
		Values myValues = getValues();

		/* Record the Id */
		myValues.setInfTypId(uInfoTypeId);
		myValues.setEventId(uEventId);
		
		/* Store the controlId */
		setControlKey(uControlId);
		
		/* Look up the EventType */
		FinanceData 	myData	= pList.getData();
		EventInfoType	myType	= myData.getInfoTypes().searchFor(uInfoTypeId);
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
			case TaxCredit:
			case NatInsurance:
			case Benefit:
			case Pension:
			case CashConsider:
				myValues.setMoney(pValue);
				break;
			case Dilution:
				myValues.setDilution(pValue);
				break;
			case CreditUnits:
			case DebitUnits:
				myValues.setUnits(pValue);
				break;
		}
		
		/* Access the EventInfoSet and register this data */
		EventInfoSet mySet = myEvent.getInfoSet();
		mySet.registerData(this);
		
		/* Allocate the id */
		pList.setNewId(this);				
	}

	@Override
	public void deRegister() {
		/* Access the EventInfoSet and register this value */
		EventInfoSet mySet = getEvent().getInfoSet();
		mySet.deRegisterData(this);		
	}
	
	/* Edit constructor */
	private EventData(List     		pList,
				      EventInfoType pType,
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

	/**
	 * Compare this EventData to another to establish equality.
	 * @param pThat The Data to compare to
	 * @return <code>true</code> if the rate is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is an EventData */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as an EventData */
		EventData myThat = (EventData)pThat;
		
		/* Check for equality on id */
		if (getId() != myThat.getId()) return false;
		
		/* Compare the changeable values */
		return getValues().histEquals(myThat.getValues()).isIdentical();
	}

	/**
	 * Compare this data to another to establish sort order. 
	 * @param pThat The EventData to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		int iDiff;
		
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is an EventData */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as an EventData */
		EventData myThat = (EventData)pThat;

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
		/* Update the Encryption details */
		super.reBuildLinks(pData);
		
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
	 * Validate the Event Data
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
				case TaxCredit:
				case NatInsurance:
				case Benefit:
				case Pension:
				case CashConsider:
					Money myMoney = myValues.getMoneyValue();
					if (myMoney == null)
						addError(myType.getName() + " must be non-null", FIELD_VALUE);
					else if (!myMoney.isPositive())
						addError(myType.getName() + " must be positive", FIELD_VALUE);
					break;
				case Dilution:
					Dilution myDilution = myValues.getDilutionValue();
					if (myDilution == null)
						addError(myType.getName() + " must be non-null", FIELD_VALUE);
					else if (myDilution.outOfRange())
						addError("Dilution factor value is outside allowed range (0-1)", 
								 FIELD_VALUE);			
					break;
				case CreditUnits:
				case DebitUnits:
					Units myUnits = myValues.getUnitsValue();
					if (myUnits == null)
						addError(myType.getName() + " must be non-null", FIELD_VALUE);
					else if (!myUnits.isPositive())
						addError(myType.getName() + " must be positive", FIELD_VALUE);
					break;
			}
		}
		
		/* Set validation flag */
		if (!hasErrors()) setValidEdit();
	}

	/**
	 * Format an Event Data 
	 * @param pData the data to format
	 * @return the formatted data
	 */
	public static String format(EventData pData) {
		/* If we have null, return it */
		if ((pData == null) || (pData.getData() == null))
			return "null";
		
		/* Switch on type of Data */
		switch (pData.getInfoType().getInfoClass()) {
			case TaxCredit:
			case NatInsurance:
			case Benefit:
			case Pension:
			case CashConsider:
				return Money.format(pData.getMoney());
			case CreditUnits:
			case DebitUnits:
				return Units.format(pData.getUnits());
			case Dilution:
				return Dilution.format(pData.getDilution());
			default:
				return "null";
		}
	}

	/**
	 * Set Money
	 * @param pValue the Value
	 */
	protected void setMoney(Money pValue) throws ModelException {
		Values			myValues	= getValues();

		/* Switch on Info type */
		switch (getInfoType().getInfoClass()) {
			case TaxCredit:
			case NatInsurance:
			case Pension:
			case Benefit:
			case CashConsider:
				/* Set the value */
				myValues.setMoney(pValue);
				break;
			default:
				throw new ModelException(ExceptionClass.LOGIC,
									this,
									"Invalid Attempt to set Money value");
		}
	}
	
	/**
	 * Set Units 
	 * @param pValue the Value
	 */
	protected void setUnits(Units pValue) throws ModelException {
		Values			myValues	= getValues();

		/* Switch on Info type */
		switch (getInfoType().getInfoClass()) {
			case CreditUnits:
			case DebitUnits:
				/* Set the value */
				myValues.setUnits(pValue);
				break;
			default:
				throw new ModelException(ExceptionClass.LOGIC,
									this,
									"Invalid Attempt to set Units value");
		}
	}
	
	/**
	 * Set Dilution
	 * @param pValue the Value
	 */
	protected void setDilution(Dilution pValue) throws ModelException {
		Values			myValues	= getValues();

		/* Switch on Info type */
		switch (getInfoType().getInfoClass()) {
			case Dilution:
				/* Set value */
				myValues.setDilution(pValue);				
				break;
			default:
				throw new ModelException(ExceptionClass.LOGIC,
									this,
									"Invalid Attempt to set Dilution value");
		}
	}
	
	/* List class */
	public static class List  	extends EncryptedList<List, EventData> {
		/* Access Extra Variables correctly */
		public FinanceData 	getData() 		{ return (FinanceData) super.getData(); }
		
		/** 
		 * Construct an empty CORE rate list
	 	 * @param pData the DataSet for the list
		 */
		protected List(FinanceData pData) { 
			super(List.class, EventData.class, pData);
		}

		/** 
		 * Construct an empty list
	 	 * @param pData the DataSet for the list
	 	 * @param pStyle the required style
		 */
		protected List(FinanceData pData, ListStyle pStyle) { 
			super(List.class, EventData.class, pData);
			setStyle(pStyle);
			setGeneration(pData.getGeneration());
		}

		/**
		 * Constructor for a cloned List
		 * @param pSource the source List
		 */
		private List(List pSource) { 
			super(pSource);
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
			myList.setData(pDataSet);
			
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
		 *  Allow an EventData to be added
		 */
		public void addItem(int     		uId,
							int				uControlId,
							int  	 		uInfoTypeId,
							int				uEventId,
	            			byte[]   		pValue) throws ModelException {
			EventData  	myInfo;
			
			/* Create the info */
			myInfo	= new EventData(this, uId, uControlId, uInfoTypeId,
					                uEventId, pValue);
				
			/* Check that this DataId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new ModelException(ExceptionClass.DATA,
						            myInfo,
			  			            "Duplicate DataId");
			 
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
		protected EventData addNewItem(EventInfoType 	pType,
									   Event			pEvent) {
			/* Create the new Data */
			EventData myData = new EventData(this, pType, pEvent);
			
			/* Add it to the list and return */
			add(myData);
			return myData;
		}
		
		@Override
		public EventData addNewItem(DataItem<?> pElement) {
			/* Create the new item */
			EventData mySource	= (EventData)pElement;
			EventData myInfo 	= new EventData(this, mySource);
			
			/* Add to list and return */
			add(myInfo);
			return myInfo;
		}

		@Override
		public EventData addNewItem() { return null; }
	}

	/* EventInfoValues */
	public class Values extends EncryptedValues<EventData> {
		private EventInfoType		theInfoType	= null;
		private Event				theEvent    = null;
		private EncryptedDecimal<?> thePair		= null;
		private EncryptedMoney		theMoney	= null;
		private EncryptedUnits		theUnits	= null;
		private EncryptedDilution 	theDilution	= null;
		private Integer 			theEventId	= null;
		private Integer 			theInfTypId	= null;

		/* Access methods */
		public EventInfoType		getInfoType()		{ return theInfoType; }
		public Event				getEvent()			{ return theEvent; }
		public EncryptedDecimal<?>	getPair()			{ return thePair; }
		public EncryptedMoney		getMoney()			{ return theMoney; }
		public EncryptedUnits		getUnits()			{ return theUnits; }
		public EncryptedDilution	getDilution()		{ return theDilution; }
		public Money 				getMoneyValue() 	{ return EncryptedData.getValue(theMoney); }
		public Units 				getUnitsValue() 	{ return EncryptedData.getValue(theUnits); }
		public Dilution				getDilutionValue()	{ return EncryptedData.getValue(theDilution); }
		public byte[]  				getData()			{ return EncryptedData.getBytes(thePair); }
		private Integer        		getEventId()   		{ return theEventId; }
		private Integer        		getInfTypId()    	{ return theInfTypId; }

		public void setInfoType(EventInfoType pType) {
			theInfoType	= pType;  
			theInfTypId = (pType == null) ? null : pType.getId();}
		public void setEvent(Event pEvent) {
			theEvent	= pEvent;  
			theEventId = (pEvent == null) ? null : pEvent.getId();}
		public void setMoney(EncryptedMoney pValue) {
			theMoney	= pValue;
			thePair		= pValue; }
		public void setUnits(EncryptedUnits pValue) {
			theUnits	= pValue;
			thePair		= pValue; }
		public void setDilution(EncryptedDilution pValue) {
			theDilution	= pValue;
			thePair		= pValue; }
		private void setEventId(Integer pEventId) {
			theEventId	= pEventId; } 
		private void setInfTypId(Integer pInfTypId) {
			theInfTypId = pInfTypId; } 

		public EncryptedDecimal<?>	determinePair()			{ 
			if (theMoney != null) return theMoney;
			if (theUnits != null) return theUnits;
			return theDilution;
		}
		
		/* Set Encrypted Values */
		protected 	void setMoney(Money pMoney) throws ModelException			{ theMoney = createEncryptedMoney(theMoney, pMoney); thePair = theMoney; }
		protected 	void setUnits(Units pUnits) throws ModelException			{ theUnits = createEncryptedUnits(theUnits, pUnits); thePair = theUnits; }
		protected 	void setDilution(Dilution pDilution) throws ModelException	{ theDilution = createEncryptedDilution(theDilution, pDilution); thePair = theDilution; }
		protected	void setMoney(byte[] pMoney) throws ModelException			{ theMoney = createEncryptedMoney(pMoney); thePair = theMoney; }
		protected	void setUnits(byte[] pUnits) throws ModelException			{ theUnits = createEncryptedUnits(pUnits); thePair = theUnits; }
		protected	void setDilution(byte[] pDilution) throws ModelException	{ theDilution = createEncryptedDilution(pDilution); thePair = theDilution; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) { copyFrom(pValues); }

		/* Check whether this object is equal to that passed */
		public Difference histEquals(HistoryValues<EventData> pCompare) {
			/* Make sure that the object is the same class */
			if (pCompare.getClass() != this.getClass()) return Difference.Different;
			
			/* Cast correctly */
			Values myValues = (Values)pCompare;

			/* Handle integer values */
			if ((Utils.differs(theEventId, 	myValues.theEventId).isDifferent())		||
				(Utils.differs(theInfTypId,	myValues.theInfTypId).isDifferent()))
				return Difference.Different;
			
			/* Determine underlying differences */
			Difference myDifference = super.histEquals(pCompare);
			
			/* Compare underlying values */
			myDifference = myDifference.combine(differs(thePair,  myValues.thePair));
			myDifference = myDifference.combine(differs(theEvent, myValues.theEvent));
			myDifference = myDifference.combine(differs(theInfoType, myValues.theInfoType));
			
			/* Return differences */
			return myDifference;
		}

		/* Copy values */
		public HistoryValues<EventData> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			Values myValues = (Values)pSource;
			super.copyFrom(myValues);
			theInfoType		= myValues.getInfoType();
			theEvent		= myValues.getEvent();
			thePair			= myValues.getPair();
			theMoney		= myValues.getMoney();
			theUnits		= myValues.getUnits();
			theDilution		= myValues.getDilution();
			theEventId   	= myValues.getEventId();
			theInfTypId    	= myValues.getInfTypId();
		}
		public Difference	fieldChanged(int fieldNo, HistoryValues<EventData> pOriginal) {
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
					bResult = (differs(thePair,   pValues.thePair));
					break;
				default:
					bResult = super.fieldChanged(fieldNo, pValues);
					break;
			}
			return bResult;
		}

		/**
		 * Update encryption after security change
		 */
		protected void updateSecurity() throws ModelException {
			/* Update the encryption */	
			theMoney	= updateEncryptedMoney(theMoney);
			theUnits	= updateEncryptedUnits(theUnits);
			theDilution	= updateEncryptedDilution(theDilution);
			thePair 	= determinePair();
		}		
		
		/**
		 * Ensure encryption after non-encrypted load
		 */
		protected void applySecurity() throws ModelException {
			/* Apply the encryption */
			applyEncryption(thePair);
		}		
		
		/**
		 * Adopt encryption from base
		 * @param pBase the Base values
		 */
		protected void adoptSecurity(EncryptedValues<EventData> pBase) throws ModelException {
			Values myBase = (Values)pBase;

			/* Apply the encryption */
			adoptEncryption(theMoney, myBase.getMoney());
			adoptEncryption(theUnits, myBase.getUnits());
			adoptEncryption(theDilution, myBase.getDilution());
			thePair = determinePair();
		}		
	}
}

package uk.co.tolcroft.finance.data;

import java.util.EnumMap;
import java.util.Map;

import uk.co.tolcroft.finance.data.StaticClass.EventInfoClass;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.Decimal.Dilution;
import uk.co.tolcroft.models.Decimal.Money;
import uk.co.tolcroft.models.Decimal.Units;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.help.DebugDetail;

public class EventInfoSet {
	/**
	 * The Event to which this set belongs
	 */
	private Event							theEvent		= null;
	
	/**
	 * The list of EventInfoTypes
	 */
	private EventInfoType.List				theTypes		= null;
	
	/**
	 * The EventData list for new data
	 */
	private EventData.List					theDataList		= null;
	
	/**
	 * The EventValue list for new data
	 */
	private EventValue.List					theValueList	= null;
	
	/**
	 * The Map of the Event Values
	 */
	private Map<EventInfoClass, EventValue>	theValueMap		= null;
	
	/**
	 * The Map of the Event Data
	 */
	private Map<EventInfoClass, EventData>	theDataMap		= null;
	
	/**
	 * Constructor
	 * @param pEvent the Event to which this Set belongs
	 */
	protected EventInfoSet(Event pEvent) {
		/* Store the Event */
		theEvent = pEvent;
		
		/* Create the Maps */
		theValueMap = new EnumMap<EventInfoClass, EventValue>(EventInfoClass.class);
		theDataMap 	= new EnumMap<EventInfoClass, EventData>(EventInfoClass.class);
		
		/* Access the dataSet */
		FinanceData myData = ((Event.List)pEvent.getList()).getData();

		/* Register the Value and Data lists */
		theValueList = myData.getEventValues();
		theDataList  = myData.getEventData();
		
		/* Access the EventInfo Types */
		theTypes = myData.getInfoTypes();
	}
	
	/**
	 * Constructor
	 * @param pEvent the event to which this is linked
	 * @param pSet the InfoSet to clone
	 */
	protected EventInfoSet(Event 		pEvent,
						   EventInfoSet pSet) {
		/* Store the Event */
		theEvent = pEvent;
		
		/* Create the Maps */
		theValueMap = new EnumMap<EventInfoClass, EventValue>(EventInfoClass.class);
		theDataMap 	= new EnumMap<EventInfoClass, EventData>(EventInfoClass.class);
		
		/* Determine the DataSet */
		Event.List 	myList 		= (Event.List)pEvent.getList();
		FinanceData	myDataSet 	= myList.getData();
		
		/* Create the lists for the Info */
		theValueList = new EventValue.List(myDataSet, ListStyle.EDIT);
		theDataList	 = new EventData.List(myDataSet, ListStyle.EDIT);
		
		/* For each EventInfo in the underlying ValueMap */
		for (EventValue myValue : pSet.theValueMap.values()) {
			/* Create the new value */
			EventValue myNew = new EventValue(theValueList, myValue);
			theValueList.add(myNew);
			
			/* Add to the value map */
			theValueMap.put(myValue.getInfoType().getInfoClass(), myNew);
		}
		
		/* For each EventData in the underlying DataMap */
		for (EventData myData : pSet.theDataMap.values()) {
			/* Create the new data */
			EventData myNew = new EventData(theDataList, myData);
			theDataList.add(myNew);
			
			/* Add to the value map */
			theDataMap.put(myData.getInfoType().getInfoClass(), myNew);
		}
		
		/* Access the EventInfo Types */
		theTypes = pSet.theTypes;
	}
	
	/**
	 * Create a new EventValue
	 * @param pClass the class of the item
	 */
	protected EventValue getNewValue(EventInfoClass pClass) throws ModelException {
		/* Access the EventInfoType */
		EventInfoType myType = theTypes.searchFor(pClass);

		/* Create the new value and add to the list */
		EventValue myValue = theValueList.addNewItem(myType, theEvent);
		
		/* Register the value and return it */
		registerValue(myValue);
		return myValue;
	}
	
	/**
	 * Create a new EventData
	 * @param pClass the class of the item
	 */
	protected EventData getNewData(EventInfoClass pClass) throws ModelException {
		/* Access the EventInfoType */
		EventInfoType myType = theTypes.searchFor(pClass);

		/* Create the new data and add to the list */
		EventData myData = theDataList.addNewItem(myType, theEvent);
		
		/* Register the data and return it */
		registerData(myData);
		return myData;
	}
	
	/**
	 * Validate an InfoSet
	 */
	protected void validate() {
		/* Access Event values */
		Account 		myDebit		= theEvent.getDebit();
		Account			myCredit 	= theEvent.getCredit();
		TransactionType	myTrans		= theEvent.getTransType();
		
		/* Access Units */
		Units			myDebUnits	= getUnits(EventInfoClass.DebitUnits);
		Units			myCredUnits	= getUnits(EventInfoClass.CreditUnits);
		
		/* If we have Credit/Debit Units */
		if ((myDebUnits != null) || (myCredUnits != null)) {
			/* If we have debit units */
			if ((myDebit != null) && (myDebUnits != null)) {				
				/* Debit Units are only allowed if debit is priced */
				if (!myDebit.isPriced()) {
					theEvent.addError("Units are only allowed involving assets", 
							 		  Event.VFIELD_DEBITUNITS);
				}

				/* TranType of dividend cannot debit units */
				if ((myTrans != null) &&
					(myTrans.isDividend())) {
					theEvent.addError("Units cannot be debited for a dividend", 
					 		  		   Event.VFIELD_DEBITUNITS);
				}

				/* Units must be non-zero and positive */
				if ((!myDebUnits.isNonZero()) || (!myDebUnits.isPositive())) { 
					theEvent.addError("Units must be non-Zero and positive", Event.VFIELD_DEBITUNITS);
				}
			}
			
			/* If we have Credit units */
			if ((myCredit != null) && (myCredUnits != null)) {				
				/* Credit Units are only allowed if credit is priced */
				if (!myCredit.isPriced()) {
					theEvent.addError("Units are only allowed involving assets", 
							 		  Event.VFIELD_CREDITUNITS);
				}

				/* TranType of admin charge cannot credit units */
				if ((myTrans != null) &&
					(myTrans.isAdminCharge())) {
					theEvent.addError("Units cannot be credited for an AdminCharge", 
						  		   	  Event.VFIELD_CREDITUNITS);
				}

				/* Units must be non-zero and positive */
				if ((!myCredUnits.isNonZero()) || (!myCredUnits.isPositive())) { 
					theEvent.addError("Units must be non-Zero and positive", Event.VFIELD_CREDITUNITS);
				}
			}
			
			/* If both credit/debit are both priced */
			if ((myCredit != null) && (myDebit != null) &&
				(myCredit.isPriced()) && (myDebit.isPriced())) {
				/* TranType must be stock split or dividend between same account */
				if ((myTrans == null) ||
					((!myTrans.isDividend()) &&
					 (!myTrans.isStockSplit()) &&
					 (!myTrans.isAdminCharge()) &&
					 (!myTrans.isStockDemerger()) &&
					 (!myTrans.isStockTakeover()))) { 
					theEvent.addError("Units can only refer to a single priced asset unless " +
								 	  "transaction is StockSplit/AdminCharge/Demerger/Takeover or Dividend", 
								 	  (myCredit != null) ? Event.VFIELD_CREDITUNITS : Event.VFIELD_DEBITUNITS);
				}
					
				/* Dividend between priced requires identical credit/debit */
				if ((myTrans != null) &&
					(myTrans.isDividend()) &&
					(Account.differs(myCredit, myDebit).isDifferent())) {
					theEvent.addError("Unit Dividends between assets must be between same asset", 
								 	  Event.VFIELD_CREDITUNITS);
				}
				
				/* Cannot have Credit and Debit if accounts are identical */
				if ((myCredUnits != null) && (myDebUnits != null) &&
					(Account.differs(myCredit, myDebit).isIdentical())) {
					theEvent.addError("Cannot credit and debit same account", 
						 	  		  Event.VFIELD_CREDITUNITS);
				}
			}
		}
	
		/* Else check for required units */
		else {
			if (theEvent.isStockSplit()) 
				theEvent.addError("Stock Split requires non-zero Units", Event.VFIELD_CREDITUNITS);
			else if (theEvent.isAdminCharge()) 
				theEvent.addError("Admin Charge requires non-zero Units", Event.VFIELD_DEBITUNITS);
		}
	
		/* Access Dilution */
		Dilution	myDilution	= getDilution(EventInfoClass.Dilution);
		
		/* If we have a dilution */
		if (myDilution != null) {
			/* If the dilution is not allowed */
			if ((!Event.needsDilution(myTrans)) && (!myTrans.isStockSplit()))
				theEvent.addError("Dilution factor given where not allowed", 
						 		  Event.VFIELD_DILUTION);			

			/* If the dilution is out of range */
			if (myDilution.outOfRange())
				theEvent.addError("Dilution factor value is outside allowed range (0-1)", 
						 		  Event.VFIELD_DILUTION);			
		}
	
		/* else if we are missing a required dilution factor */
		else if (Event.needsDilution(myTrans)) {
			theEvent.addError("Dilution factor missing where required", 
					 		  Event.VFIELD_DILUTION);						
		}
	
		/* Access Years and Tax Credit */
		Integer	myYears	= getValue(EventInfoClass.QualifyYears);
		Money	myTax	= getMoney(EventInfoClass.TaxCredit);
		
		/* If we are a taxable gain */
		if ((myTrans != null) && (myTrans.isTaxableGain())) {
			/* Years must be positive */
			if ((myYears == null) || (myYears <= 0)) {
				theEvent.addError("Years must be non-zero and positive", Event.VFIELD_QUALIFYYEARS);
			}
		
			/* Tax Credit must be non-null and positive */
			if ((myTax == null) || (!myTax.isPositive())) {
				theEvent.addError("TaxCredit must be non-null", Event.VFIELD_TAXCREDIT);
			}
		}
	
		/* If we need a tax credit */
		else if ((myTrans != null) && (Event.needsTaxCredit(myTrans, 
													  		myDebit))) {
			/* Tax Credit must be non-null and positive */
			if ((myTax == null) || (!myTax.isPositive())) {
				theEvent.addError("TaxCredit must be non-null", Event.VFIELD_TAXCREDIT);
			}

			/* Years must be null */
			if (myYears != null) {
				theEvent.addError("Years must be null", Event.VFIELD_QUALIFYYEARS);
			}
		}
	
		/* else we should not have a tax credit */
		else if (myTrans != null) {
			/* Tax Credit must be null */
			if (myTax != null) {
				theEvent.addError("TaxCredit must be null", Event.VFIELD_TAXCREDIT);
			}

			/* Years must be null */
			if (myYears != null) {
				theEvent.addError("Years must be null", Event.VFIELD_QUALIFYYEARS);
			}
		}
	}
	
	/**
	 * Register the event value
	 * @param pValue the Value
	 */
	protected void registerValue(EventValue pValue) throws ModelException {
		/* Obtain the Map value */
		EventInfoType 	myType 	= pValue.getInfoType();
		EventValue 		myValue = theValueMap.get(myType.getInfoClass());
		
		/* If we already have a value */
		if (myValue != null)
			throw new ModelException(ExceptionClass.LOGIC,
								theEvent,
								"InfoClass " + myType.getName() + " already registered");

		/* Store the value to the map */
		theValueMap.put(myType.getInfoClass(), pValue);
	}
	
	/**
	 * DeRegister the event value
	 * @param pValue the Value
	 */
	protected void deRegisterValue(EventValue pValue) {
		/* Obtain the Type */
		EventInfoType 	myType 	= pValue.getInfoType();

		/* Remove the reference from the map */
		theValueMap.remove(myType.getInfoClass());
	}
	
	/**
	 * Register the event data
	 * @param pData the Data
	 */
	protected void registerData(EventData pData) throws ModelException {
		/* Obtain the Map value */
		EventInfoType 	myType 	= pData.getInfoType();
		EventData 		myData 	= theDataMap.get(myType.getInfoClass());
		
		/* If we already have a value */
		if (myData != null)
			throw new ModelException(ExceptionClass.LOGIC,
								theEvent,
								"InfoClass " + myType.getName() + " already registered");

		/* Store the value to the map */
		theDataMap.put(myType.getInfoClass(), pData);
	}
	
	/**
	 * DeRegister the event data
	 * @param pData the Data
	 */
	protected void deRegisterData(EventData pData) {
		/* Obtain the Type */
		EventInfoType 	myType 	= pData.getInfoType();

		/* Remove the reference from the map */
		theDataMap.remove(myType.getInfoClass());
	}
	
	/**
	 * Obtain the required Event Value
	 * @param pType the Value Type
	 */
	protected Integer getValue(EventInfoClass pType) {
		/* Obtain the Map value */
		EventValue myValue = theValueMap.get(pType);
		
		/* Return the value */
		return (myValue == null) ? null : myValue.getValue();
	}

	/**
	 * Obtain the required Event Account
	 * @param pType the Value Type
	 */
	protected Account getAccount(EventInfoClass pType) {
		/* Obtain the Map value */
		EventValue myValue = theValueMap.get(pType);
		
		/* Return the value */
		return (myValue == null) ? null : myValue.getAccount();
	}

	/**
	 * Obtain the required Event Data Money
	 * @param pType the Value Type
	 */
	protected Money getMoney(EventInfoClass pType) {
		/* Obtain the Map value */
		EventData myData = theDataMap.get(pType);
		
		/* Return the data */
		return (myData == null) ? null : myData.getMoney();
	}

	/**
	 * Obtain the required Event Data Units
	 * @param pType the Value Type
	 */
	protected Units getUnits(EventInfoClass pType) {
		/* Obtain the Map value */
		EventData myData = theDataMap.get(pType);
		
		/* Return the data */
		return (myData == null) ? null : myData.getUnits();
	}
	
	/**
	 * Obtain the required Event Data Dilution
	 * @param pType the Value Type
	 */
	protected Dilution getDilution(EventInfoClass pType) {
		/* Obtain the Map value */
		EventData myData = theDataMap.get(pType);
		
		/* Return the data */
		return (myData == null) ? null : myData.getDilution();
	}

	/**
	 * Set the required Event Value
	 * @param pType the Value Type
	 * @param pValue the Value (may be null)
	 */
	protected void setValue(EventInfoType pType, Integer pValue) throws ModelException {
		/* Obtain the Map value */
		EventValue myValue = theValueMap.get(pType.getInfoClass());
		
		/* If we do not have a map value */
		if (myValue == null) {
			/* Add a new value */
			myValue = theValueList.addNewItem(pType, theEvent);
			
			/* Store it to the map */
			theValueMap.put(pType.getInfoClass(), myValue);
		}
		
		/* Store the value */
		myValue.setValue(pValue);
	}

	/**
	 * Set the required Event Account
	 * @param pType the Value Type
	 * @param pValue the Value (may be null)
	 */
	protected void setAccount(EventInfoType pType, Account pValue) throws ModelException {
		/* Obtain the Map value */
		EventValue myValue = theValueMap.get(pType.getInfoClass());
		
		/* If we do not have a map value */
		if (myValue == null) {
			/* Add a new value */
			myValue = theValueList.addNewItem(pType, theEvent);
			
			/* Store it to the map */
			theValueMap.put(pType.getInfoClass(), myValue);
		}
		
		/* Store the value */
		myValue.setAccount(pValue);
	}

	/**
	 * Set the required Event Money
	 * @param pType the Value Type
	 * @param pValue the Value (may be null)
	 */
	protected void setMoney(EventInfoType pType, Money pValue) throws ModelException {
		/* Obtain the Map data */
		EventData myData = theDataMap.get(pType.getInfoClass());
		
		/* If we do not have a map value */
		if (myData == null) {
			/* Add a new value */
			myData = theDataList.addNewItem(pType, theEvent);
			
			/* Store it to the map */
			theDataMap.put(pType.getInfoClass(), myData);
		}
		
		/* Store the value */
		myData.setMoney(pValue);
	}

	/**
	 * Set the required Event Units
	 * @param pType the Value Type
	 * @param pValue the Value (may be null)
	 */
	protected void setUnits(EventInfoType pType, Units pValue) throws ModelException {
		/* Obtain the Map data */
		EventData myData = theDataMap.get(pType.getInfoClass());
		
		/* If we do not have a map value */
		if (myData == null) {
			/* Add a new value */
			myData = theDataList.addNewItem(pType, theEvent);
			
			/* Store it to the map */
			theDataMap.put(pType.getInfoClass(), myData);
		}
		
		/* Store the value */
		myData.setUnits(pValue);
	}

	/**
	 * Set the required Event Dilution
	 * @param pType the Value Type
	 * @param pValue the Value (may be null)
	 */
	protected void setDilution(EventInfoType pType, Dilution pValue) throws ModelException {
		/* Obtain the Map data */
		EventData myData = theDataMap.get(pType.getInfoClass());
		
		/* If we do not have a map value */
		if (myData == null) {
			/* Add a new value */
			myData = theDataList.addNewItem(pType, theEvent);
			
			/* Store it to the map */
			theDataMap.put(pType.getInfoClass(), myData);
		}
		
		/* Store the value */
		myData.setDilution(pValue);
	}

	/**
	 * Add additional fields to HTML String
	 * @param pDetail the debug detail
	 * @param pBuffer the string buffer 
	 */
	public void addHTMLFields(DebugDetail 	pDetail, 
							  StringBuilder pBuffer) {
		/* Create new String Builder */
		StringBuilder myBuilder = new StringBuilder(1000);
		int			  myEntries = 0;
		
		/* Loop through the InfoType values */
		for (EventInfoClass myClass : EventInfoClass.values()) {
			switch (myClass) {
				/* Handle values */
				case XferDelay:
				case QualifyYears:
				case CashAccount:
					/* Access the EventValue */
					EventValue myValue = theValueMap.get(myClass);
					
					/* Ignore null entry */
					if (myValue == null) break;
					
					/* Format the Info */
					myBuilder.append("<tr><td>"); 
					myBuilder.append(myClass.name()); 
					myBuilder.append("</td><td>"); 
					myBuilder.append(pDetail.addDebugLink(myValue, EventValue.format(myValue)));
					myBuilder.append("</td></tr>");

					/* Increment values and break loop */
					myEntries++;
					break;
				
				/* Handle Data */
				case TaxCredit:
				case NatInsurance:
				case Pension:
				case Benefit:
				case CashConsider:
				case CreditUnits:
				case DebitUnits:
				case Dilution:
					/* Access the EventData */
					EventData myData = theDataMap.get(myClass);
					
					/* Ignore null entry */
					if (myData == null) break;

					/* Format the Info */
					myBuilder.append("<tr><td>"); 
					myBuilder.append(myClass.name()); 
					myBuilder.append("</td><td>"); 
					myBuilder.append(pDetail.addDebugLink(myData, EventData.format(myData)));
					myBuilder.append("</td></tr>");

					/* Increment values and break loop */
					myEntries++;
					break;
			}
		}
		
		/* If we have any entries */
		if (myEntries > 0) {
			/* Start the Info Section */
			pBuffer.append("<tr><th rowspan=\"");
			pBuffer.append(myEntries+1);
			pBuffer.append("\">Fields</th></tr>");

			/* Add the detail */
			pBuffer.append(myBuilder);
		}
	}
}

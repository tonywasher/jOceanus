package uk.co.tolcroft.finance.data;

import java.util.EnumMap;
import java.util.Map;

import uk.co.tolcroft.finance.data.StaticClass.EventInfoClass;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.Number.Dilution;
import uk.co.tolcroft.models.Number.Money;
import uk.co.tolcroft.models.Number.Units;
import uk.co.tolcroft.models.help.DebugDetail;

public class EventInfoSet {
	/**
	 * The Event to which this set belongs
	 */
	private Event							theEvent		= null;
	
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
	}
	
	/**
	 * Constructor
	 * @param pSet the InfoSet to clone
	 */
	protected EventInfoSet(EventInfoSet pSet) {
		/* Store the Event */
		theEvent = pSet.theEvent;
		
		/* Create the Maps */
		theValueMap = new EnumMap<EventInfoClass, EventValue>(EventInfoClass.class);
		theDataMap 	= new EnumMap<EventInfoClass, EventData>(EventInfoClass.class);
		
		/* Create the lists for the Info */
		theValueList = new EventValue.List(null);
	}
	
	/**
	 * Register the event value
	 * @param pValue the Value
	 */
	protected void registerValue(EventValue pValue) throws Exception {
		/* Obtain the Map value */
		EventInfoType 	myType 	= pValue.getInfoType();
		EventValue 		myValue = theValueMap.get(myType.getInfoClass());
		
		/* If we already have a value */
		if (myValue != null)
			throw new Exception(ExceptionClass.LOGIC,
								theEvent,
								"InfoClass " + myType.getName() + " already registered");

		/* Store the value to the map */
		theValueMap.put(myType.getInfoClass(), pValue);
	}
	
	/**
	 * Register the event data
	 * @param pData the Data
	 */
	protected void registerData(EventData pData) throws Exception {
		/* Obtain the Map value */
		EventInfoType 	myType 	= pData.getInfoType();
		EventData 		myData 	= theDataMap.get(myType.getInfoClass());
		
		/* If we already have a value */
		if (myData != null)
			throw new Exception(ExceptionClass.LOGIC,
								theEvent,
								"InfoClass " + myType.getName() + " already registered");

		/* Store the value to the map */
		theDataMap.put(myType.getInfoClass(), pData);
	}
	
	/**
	 * Obtain the required Event Value
	 * @param pType the Value Type
	 */
	protected Integer getValue(EventInfoType pType) {
		/* Obtain the Map value */
		EventValue myValue = theValueMap.get(pType.getInfoClass());
		
		/* Return the value */
		return (myValue == null) ? null : myValue.getValue();
	}

	/**
	 * Obtain the required Event Account
	 * @param pType the Value Type
	 */
	protected Account getAccount(EventInfoType pType) {
		/* Obtain the Map value */
		EventValue myValue = theValueMap.get(pType.getInfoClass());
		
		/* Return the value */
		return (myValue == null) ? null : myValue.getAccount();
	}

	/**
	 * Obtain the required Event Data Money
	 * @param pType the Value Type
	 */
	protected Money getMoney(EventInfoType pType) {
		/* Obtain the Map value */
		EventData myData = theDataMap.get(pType.getInfoClass());
		
		/* Return the data */
		return (myData == null) ? null : myData.getMoney();
	}

	/**
	 * Obtain the required Event Data Units
	 * @param pType the Value Type
	 */
	protected Units getUnits(EventInfoType pType) {
		/* Obtain the Map value */
		EventData myData = theDataMap.get(pType.getInfoClass());
		
		/* Return the data */
		return (myData == null) ? null : myData.getUnits();
	}
	
	/**
	 * Obtain the required Event Data Dilution
	 * @param pType the Value Type
	 */
	protected Dilution getDilution(EventInfoType pType) {
		/* Obtain the Map value */
		EventData myData = theDataMap.get(pType.getInfoClass());
		
		/* Return the data */
		return (myData == null) ? null : myData.getDilution();
	}

	/**
	 * Set the required Event Value
	 * @param pType the Value Type
	 * @param pValue the Value (may be null)
	 */
	protected void setValue(EventInfoType pType, Integer pValue) throws Exception {
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
	protected void setAccount(EventInfoType pType, Account pValue) throws Exception {
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
	protected void setMoney(EventInfoType pType, Money pValue) throws Exception {
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
	protected void setUnits(EventInfoType pType, Units pValue) throws Exception {
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
	protected void setDilution(EventInfoType pType, Dilution pValue) throws Exception {
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
			pBuffer.append("<tr><th rowspan=\"2\">Fields</th></tr>");

			/* Add the detail */
			pBuffer.append(myBuilder);
		}
	}
}

package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number;

public class ChargeableEvent extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "ChargeableEvent";

	/* Members */
	private		Number.Money	theSlice    = null;
	private		Number.Money	theTaxation = null;
		
	/* Linking methods */
	public Event     		getBase() 		{ return (Event)super.getBase(); }
	
	/**
	 * Get the Date of the chargeable event 
	 * @return the date of the chargeable event
	 */
	public Date 			getDate()		{ return getBase().getDate(); }

	/**
	 * Get the Description of the chargeable event 
	 * @return the description of the chargeable event
	 */
	public String 			getDesc()		{ return getBase().getDesc(); }

	/**
	 * Get the Amount of the chargeable event 
	 * @return the amount of the chargeable event
	 */
	public Number.Money		getAmount()		{ return getBase().getAmount(); }

	/**
	 * Get the TaxCredit of the chargeable event 
	 * @return the tax credit of the chargeable event
	 */
	public Number.Money		getTaxCredit()	{ return getBase().getTaxCredit(); }

	/**
	 * Get the Taxation of the chargeable event 
	 * @return the taxation of the chargeable event
	 */
	public Number.Money		getTaxation()	{ return theTaxation; }

	/**
	 * Get the Slice of the chargeable event 
	 * @return the slice of the chargeable event
	 */
	public Number.Money		getSlice()		{ return theSlice; }

	/**
	 * Get the Years of the chargeable event 
	 * @return the years of the chargeable event
	 */
	public Integer			getYears()		{ return getBase().getYears(); }

	/**
	 * Constructor
	 * @param pList the list
	 * @param pEvent the Event
	 */
	private ChargeableEvent(List pList, Event pEvent) {
		/* Call super constructor */
		super(pList, pEvent.getId());
		
		/* Local variables */
		long			myValue;
		
		/* Access the slice value of the event */
		myValue 	 = pEvent.getAmount().getAmount();
		myValue 	/= pEvent.getYears();
		theSlice	 = new Number.Money(myValue);
		
		/* Link to the event */
		setBase(pEvent);
		
		/* Set status */
		setState(DataState.CLEAN);
	}
	
	/* Field IDs */
	public static final int FIELD_SLICE  	= 0;
	public static final int FIELD_TAXATION  = 1;
	public static final int NUMFIELDS	    = 2;
	
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
			case FIELD_SLICE: 		return "Slice";
			case FIELD_TAXATION: 	return "Taxation";
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
		String myString = ""; 
		switch (iField) {
			case FIELD_SLICE:		
				myString += Utils.formatMoney(theSlice);
				break;
			case FIELD_TAXATION: 		
				myString += Utils.formatMoney(theTaxation);
				break;
		}
		return myString;
	}

	/**
	 * Compare this ChargeableEvent to another to establish equality.
	 * 
	 * @param pThat The ChargeableEvent to compare to
	 * @return <code>true</code> if the event is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a Chargeable Event */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a ChargeableEvent */
		ChargeableEvent myEvent = (ChargeableEvent)pThat;
		
		/* Check for equality */
		if (Utils.differs(getSlice(),      	myEvent.getSlice())) 		return false;
		if (Utils.differs(getTaxation(),   	myEvent.getTaxation()))		return false;
		return getBase().equals(myEvent.getBase());
	}

	/**
	 * Compare this ChargeableEvent to another to establish sort order.
	 * 
	 * @param pThat The Event to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a ChargeableEvent */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as a Chargeable Event */
		ChargeableEvent myThat = (ChargeableEvent)pThat;
		
		/* Compare the underlying events */
		return getBase().compareTo(myThat.getBase());
	}
	
	/**
	 * Apply taxation of total slice to the individual events. This tax is first split 
	 * proportionally among the slices and then multiplied by the years of each individual event   
	 * @param pTax	the calculated taxation for the slice
	 * @param pTotal the slice total of the event list 
	 */
	private void applyTax(Number.Money pTax,
			              Number.Money pTotal)	{
		Number.Money 	myPortion;
		long			myValue;
		
		/* Calculate the portion of tax that applies to this slice */
		myPortion = pTax.valueAtWeight(theSlice, pTotal);
		
		/* Multiply by the number of years */
		myValue = myPortion.getValue() * getYears();
		theTaxation = new Number.Money(myValue);
	}
	
	/**
	 * List of ChargeableEvents
	 */
	public static class List extends DataList<ChargeableEvent> {
		/**
		 * Constructor
		 */
		public List() { super(ListStyle.VIEW, false); }
		
		/** 
	 	 * Clone a Chargeable Event list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return null; }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem the item to add
		 * @return the newly added item
		 */
		public DataItem addNewItem(DataItem pItem) { return null; }

	
		/**
		 * Add a new item to the edit list
		 * 
		 * @param isCredit - ignored
		 */
		public void addNewItem(boolean isCredit) { return; }
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }		

		/**
		 * Add Chargeable Event to List
		 * @param pEvent the base event
		 */
		public void addEvent(Event pEvent) {
			ChargeableEvent myEvent;
			
			/* Create the chargeable event */
			myEvent = new ChargeableEvent(this, pEvent);
			
			/* Add it to the list */
			add(myEvent);
		}
		
		/**
		 * Get the SliceTotal of the chargeable event list. 
		 * Each slice is the Value of the event
		 * divided by the number of years that the charge is to be sliced over
		 * @return the slice total of the chargeable event list 
		 */
		public Number.Money	getSliceTotal()		{
			Number.Money 	myTotal;
			ChargeableEvent myEvent;
			ListIterator 	myIterator;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Initialise the total */
			myTotal = new Number.Money(0);
			
			/* Loop through the list */
			while ((myEvent = myIterator.next()) != null) {				
				/* Add in this slice */
				myTotal.addAmount(myEvent.getSlice());
			}
			
			/* Return the total */
			return myTotal;
		}
		
		/**
		 * Get the TaxTotal of the chargeable event list. 
		 * This is the total of the tax that has been apportioned to each slice
		 * @return the tax total of the chargeable event list 
		 */
		public Number.Money	getTaxTotal()		{
			Number.Money 	myTotal;
			ChargeableEvent myEvent;
			ListIterator 	myIterator;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Initialise the total */
			myTotal = new Number.Money(0);
			
			/* Loop through the list */
			while ((myEvent = myIterator.next()) != null) {				
				/* Add in this slice */
				myTotal.addAmount(myEvent.getTaxation());
			}
			
			/* Return the total */
			return myTotal;
		}
		
		/**
		 * Get the GainsTotal of the chargeable event list. 
		 * Each slice is the Value of the event
		 * divided by the number of years that the charge is to be sliced over
		 * @return the slice total of the chargeable event list 
		 */
		public Number.Money	getGainsTotal()		{
			Number.Money 	myTotal;
			ChargeableEvent myEvent;
			ListIterator 	myIterator;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Initialise the total */
			myTotal = new Number.Money(0);
			
			/* Loop through the list */
			while ((myEvent = myIterator.next()) != null) {				
				/* Add in this slice */
				myTotal.addAmount(myEvent.getBase().getAmount());
			}
			
			/* Return the total */
			return myTotal;
		}

		/**
		 * Apply taxation of total slice to the individual events. This tax is first split 
		 * proportionally among the slices and then multiplied by the years of each individual event   
		 * @param pTax	the calculated taxation for the slice
		 * @param pTotal the slice total of the event list 
		 */
		public void applyTax(Number.Money pTax,
				             Number.Money pTotal)	{
			ChargeableEvent myEvent;
			ListIterator 	myIterator;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the list */
			while ((myEvent = myIterator.next()) != null) {				
				/* Apply tax to this slice */
				myEvent.applyTax(pTax, pTotal);
			}
		}
	}	
}

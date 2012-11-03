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
package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Decimal.*;
import uk.co.tolcroft.models.help.DebugDetail;

public class ChargeableEvent extends ReportItem<ChargeableEvent> {
	/**
	 * The name of the object
	 */
	private static final String objName = "ChargeableEvent";

	/* Members */
	private		Money	theGains    = null;
	private		Money	theSlice    = null;
	private		Money	theTaxation = null;
		
	/* Linking methods */
	public Event     		getBase() 		{ return (Event)super.getBase(); }
	
	/**
	 * Get the Date of the chargeable event 
	 * @return the date of the chargeable event
	 */
	public DateDay 			getDate()		{ return getBase().getDate(); }

	/**
	 * Get the Description of the chargeable event 
	 * @return the description of the chargeable event
	 */
	public String 			getDesc()		{ return getBase().getDesc(); }

	/**
	 * Get the Amount of the chargeable event 
	 * @return the amount of the chargeable event
	 */
	public Money			getAmount()		{ return theGains; }

	/**
	 * Get the TaxCredit of the chargeable event 
	 * @return the tax credit of the chargeable event
	 */
	public Money			getTaxCredit()	{ return getBase().getTaxCredit(); }

	/**
	 * Get the Taxation of the chargeable event 
	 * @return the taxation of the chargeable event
	 */
	public Money			getTaxation()	{ return theTaxation; }

	/**
	 * Get the Slice of the chargeable event 
	 * @return the slice of the chargeable event
	 */
	public Money			getSlice()		{ return theSlice; }

	/**
	 * Get the Years of the chargeable event 
	 * @return the years of the chargeable event
	 */
	public Integer			getYears()		{ return getBase().getYears(); }

	/**
	 * Build History (no history)
	 */
	protected void buildHistory() {}

	/**
	 * Constructor
	 * @param pList the list
	 * @param pEvent the Event
	 * @param pGains the Gains
	 */
	private ChargeableEvent(List 	pList, 
							Event 	pEvent,
							Money	pGains) {
		/* Call super constructor */
		super(pList);
		
		/* Local variables */
		long			myValue;
		
		/* Access the slice value of the event */
		theGains	 = pGains;
		myValue 	 = pGains.getAmount();
		myValue 	/= pEvent.getYears();
		theSlice	 = new Money(myValue);
		
		/* Link to the event */
		setBase(pEvent);
	}
	
	/* Field IDs */
	public static final int FIELD_GAINS 	= 0;
	public static final int FIELD_SLICE  	= 1;
	public static final int FIELD_TAXATION  = 2;
	public static final int NUMFIELDS	    = 3;
	
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
			case FIELD_GAINS: 		return "Gains";
			case FIELD_SLICE: 		return "Slice";
			case FIELD_TAXATION: 	return "Taxation";
			default:		  		return ReportItem.fieldName(iField);
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
	 * @return the formatted field
	 */
	public String formatField(DebugDetail pDetail, int iField) {
		String myString = ""; 
		switch (iField) {
			case FIELD_GAINS:		
				myString += Money.format(theGains);
				break;
			case FIELD_SLICE:		
				myString += Money.format(theSlice);
				break;
			case FIELD_TAXATION: 		
				myString += Money.format(theTaxation);
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
		if (Money.differs(getSlice(),      	myEvent.getSlice()).isDifferent()) 		return false;
		if (Money.differs(getTaxation(),   	myEvent.getTaxation()).isDifferent())	return false;
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
	private void applyTax(Money pTax,
			              Money pTotal)	{
		Money 	myPortion;
		long	myValue;
		
		/* Calculate the portion of tax that applies to this slice */
		myPortion = pTax.valueAtWeight(theSlice, pTotal);
		
		/* Multiply by the number of years */
		myValue = myPortion.getValue() * getYears();
		theTaxation = new Money(myValue);
	}
	
	/**
	 * List of ChargeableEvents
	 */
	public static class List extends ReportList<ChargeableEvent> {
		/**
		 * Constructor
		 */
		public List() { super(ChargeableEvent.class); }
		
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }		

		/**
		 * Add Chargeable Event to List
		 * @param pEvent the base event
		 * @param pGains the gains
		 */
		public void addEvent(Event pEvent,
							 Money pGains) {
			ChargeableEvent myEvent;
			
			/* Create the chargeable event */
			myEvent = new ChargeableEvent(this, pEvent, pGains);
			
			/* Add it to the list */
			add(myEvent);
		}
		
		/**
		 * Get the SliceTotal of the chargeable event list. 
		 * Each slice is the Value of the event
		 * divided by the number of years that the charge is to be sliced over
		 * @return the slice total of the chargeable event list 
		 */
		public Money	getSliceTotal()		{
			Money 								myTotal;
			ChargeableEvent 					myEvent;
			SortedListIterator<ChargeableEvent> myIterator;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Initialise the total */
			myTotal = new Money(0);
			
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
		public Money	getTaxTotal()		{
			Money 								myTotal;
			ChargeableEvent 					myEvent;
			SortedListIterator<ChargeableEvent>	myIterator;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Initialise the total */
			myTotal = new Money(0);
			
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
		public Money	getGainsTotal()		{
			Money 								myTotal;
			ChargeableEvent 					myEvent;
			SortedListIterator<ChargeableEvent>	myIterator;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Initialise the total */
			myTotal = new Money(0);
			
			/* Loop through the list */
			while ((myEvent = myIterator.next()) != null) {				
				/* Add in this slice */
				myTotal.addAmount(myEvent.getAmount());
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
		public void applyTax(Money pTax,
				             Money pTotal)	{
			ChargeableEvent 					myEvent;
			SortedListIterator<ChargeableEvent> myIterator;
			
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

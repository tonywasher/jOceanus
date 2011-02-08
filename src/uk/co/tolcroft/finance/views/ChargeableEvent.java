package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.models.Date;
import uk.co.tolcroft.models.Number;

public class ChargeableEvent {
	/* Members */
	private		Event			theEvent    = null;
	private		Number.Money	theSlice    = null;
	private		Number.Money	theTaxation = null;
	private 	ChargeableEvent theNext 	= null;
	
	/**
	 * Get the next item in the list 
	 * @return the next item or <code>null</code>
	 */
	public ChargeableEvent 	getNext()		{ return theNext; }

	/**
	 * Get the Date of the chargeable event 
	 * @return the date of the chargeable event
	 */
	public Date 			getDate()		{ return theEvent.getDate(); }

	/**
	 * Get the Description of the chargeable event 
	 * @return the description of the chargeable event
	 */
	public String 			getDesc()		{ return theEvent.getDesc(); }

	/**
	 * Get the Amount of the chargeable event 
	 * @return the amount of the chargeable event
	 */
	public Number.Money		getAmount()		{ return theEvent.getAmount(); }

	/**
	 * Get the TaxCredit of the chargeable event 
	 * @return the tax credit of the chargeable event
	 */
	public Number.Money		getTaxCredit()	{ return theEvent.getTaxCredit(); }

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
	public Integer			getYears()		{ return theEvent.getYears(); }

	/**
	 * Constructor
	 * @param pEvent
	 * @param pFirst
	 */
	protected ChargeableEvent(Event pEvent, ChargeableEvent pFirst) {
		ChargeableEvent myPrev = null;
		long			myValue;
		
		/* Store the event */
		theEvent = pEvent;
		
		/* Access the slice value of the event */
		myValue 	 = pEvent.getAmount().getAmount();
		myValue 	/= pEvent.getYears();
		theSlice	 = new Number.Money(myValue);
		
		/* If there are elements in the list */
		if (pFirst != null) {
			/* Determine the last element in the list */
			myPrev = pFirst;
			while (myPrev.theNext != null) 
				myPrev = myPrev.theNext;
			
			/* Link us in */
			myPrev.theNext = this;
		}
	}
	
	/**
	 * Get the SliceTotal of the chargeable event list. Each slice is the TaxCredit of the event
	 * divided by the number of years that the charge is to be sliced over
	 * @return the slice total of the chargeable event list 
	 */
	public Number.Money	getSliceTotal()		{
		Number.Money myTotal;
		
		/* If we are the last slice */
		if (theNext == null) {
			/* Total is just us */
			myTotal = new Number.Money(theSlice);
		}
		
		/* else we have further elements */
		else {
			/* Get the total of the further elements */
			myTotal = theNext.getSliceTotal();
			
			/* Add in our slice */
			myTotal.addAmount(theSlice);
		}
		
		/* Return the total */
		return myTotal;
	}

	/**
	 * Get the TaxTotal of the chargeable event list 
	 * @return the tax total of the chargeable event list 
	 */
	public Number.Money	getTaxTotal()		{
		Number.Money myTotal;
		
		/* If we are the last slice */
		if (theNext == null) {
			/* Total is just us */
			myTotal = new Number.Money(theTaxation);
		}
		
		/* else we have further elements */
		else {
			/* Get the total of the further elements */
			myTotal = theNext.getTaxTotal();
			
			/* Add in our slice */
			myTotal.addAmount(theTaxation);
		}
		
		/* Return the total */
		return myTotal;
	}
	
	/**
	 * Get the GainsTotal of the chargeable event list 
	 * @return the gains total of the chargeable event list 
	 */
	public Number.Money	getGainsTotal()		{
		Number.Money myTotal;
		
		/* If we are the last slice */
		if (theNext == null) {
			/* Total is just us */
			myTotal = new Number.Money(getAmount());
		}
		
		/* else we have further elements */
		else {
			/* Get the total of the further elements */
			myTotal = theNext.getGainsTotal();
			
			/* Add in our slice */
			myTotal.addAmount(getAmount());
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
	public void	applyTax(Number.Money pTax,
			             Number.Money pTotal)	{
		Number.Money 	myPortion;
		long			myValue;
		
		/* Calculate the portion of tax that applies to this slice */
		myPortion = pTax.valueAtWeight(theSlice, pTotal);
		
		/* Multiply by the number of years */
		myValue = myPortion.getValue() * getYears();
		theTaxation = new Number.Money(myValue);
							
		/* If we have further slices */
		if (theNext != null) {
			/* Apply tax further down */
			theNext.applyTax(pTax, pTotal);
		}
	}
}

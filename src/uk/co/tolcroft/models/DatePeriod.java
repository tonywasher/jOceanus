package uk.co.tolcroft.models;

import java.util.Calendar;

public enum DatePeriod {
	OneWeek(Calendar.DAY_OF_MONTH, 7),
	Fortnight(Calendar.DAY_OF_MONTH, 14),
	OneMonth(Calendar.MONTH, 1),
	QuarterYear(Calendar.MONTH, 3),
	HalfYear(Calendar.MONTH, 6),
	OneYear(Calendar.YEAR, 1),
	Unlimited(-1,-1);
	
	/* Properties */
	private final int	theField;
	private final int	theValue;
	
	/**
	 * Constructor
	 */
	private DatePeriod(int pField, int pValue) {
		/* Store values */
		theField = pField;
		theValue = pValue;
	}
	
	/**
	 * Adjust a date appropriately
	 * @param pDate the date to adjust
	 * @param bForward adjust forwards or backwards
	 */
	public Date adjustDate(Date 	pDate,
			               boolean 	bForward) {
		Date myDate;
	
		/* Initialise the date */
		myDate = new Date(pDate);
	
		/* Adjust the field */
		myDate.adjustField(theField, (bForward) ? theValue : -theValue);
		
		/* Return the date */
		return myDate;
	}
}

package uk.co.tolcroft.models;

import java.util.Calendar;

/**
 * Represents a Date object. Dates in the finance package are merely dates with no associated
 * time value 
 */
public class Date {
	/**
	 * The Date object in underlying Java form
	 */
	private Calendar theDate  = null;
	
	/**
	 * The year of the date
	 */
	private int      theYear  = 0;
	
	/**
	 * The month of the date
	 */
	private int      theMonth = 0;
	
	/**
	 * The day of the date
	 */
	private int      theDay   = 0;

	/**
	 * Construct a new Date and initialise with todays date 
	 */
	public Date() {
		theDate = Calendar.getInstance();
		obtainValues();
	}

	/**
	 * Construct a new Date and initialise from a java date
	 * 
	 * @param pDate the java date to initialise from
	 */
	public Date(java.util.Date pDate) {
		if (pDate != null) {
			theDate = Calendar.getInstance();
			theDate.setTime(pDate);
			obtainValues();
		}
	}

	/**
	 * Construct a new Date and initialise from a finance date
	 * 
	 * @param pDate the finance date to initialise from
	 */
	public Date(Date pDate) {
		if ((pDate != null) && 
		    (pDate.theDate != null)) {
			theDate = Calendar.getInstance();
			theDate.setTime(pDate.theDate.getTime());
			obtainValues();
		}
	}
	
	/**
	 * Adjust the date by a number of years
	 * 
	 * @param iYear the number of years to adjust by
	 */
	public void adjustYear(int iYear) {
		if (theDate != null) { 
			theDate.add(Calendar.YEAR, iYear);
			obtainValues();
		}
	}

	/**
	 * Adjust the date by a number of months
	 * 
	 * @param iMonth the number of months to adjust by
	 */
	public void adjustMonth(int iMonth) {
		if (theDate != null) {
			theDate.add(Calendar.MONTH, iMonth);
			obtainValues();
		}
	}

	/**
	 * Adjust the date to the end of the following month
	 */
	public void endNextMonth() {
		if (theDate != null) {
			theDate.add(Calendar.DAY_OF_MONTH, 1-theDay);
			theDate.add(Calendar.MONTH, 2);
			theDate.add(Calendar.DAY_OF_MONTH, -1);
			obtainValues();
		}
	}

	/**
	 * Calculate the age that someone born on this date will be on a given date
	 * @param pDate the date for which to calculate the age
	 * @return the age on that date
	 */
	public int ageOn(Date pDate) {
		int myAge = -1;

		if ((theDate != null) && (pDate.theDate != null)) {
			/* Calculate the initial age assuming same date in year */
			myAge  = pDate.theDate.get(Calendar.YEAR);
			myAge -= theDate.get(Calendar.YEAR);
			
			/* If we are a later day in the year subtract 1 year */
			if (theDate.get(Calendar.DAY_OF_YEAR) > 
			    pDate.theDate.get(Calendar.DAY_OF_YEAR))
				myAge--;
		}
		
		/* Return to caller */
		return myAge;
	}

	/**
	 * Adjust the date by a number of days
	 * 
	 * @param iDay the number of days to adjust by
	 */
	public void adjustDay(int iDay) {
		if (theDate != null) {
			theDate.add(Calendar.DAY_OF_MONTH, iDay);
			obtainValues();
		}
	}

	/**
	 * Copy a date from another finance date
	 * 
	 * @param pDate the date to copy from
	 */
	public void copyDate(Date pDate) {
		theDate.setTime(pDate.theDate.getTime());
		obtainValues();
	}

	/**
	 * Get the year of the date
	 * 
	 * @return the year of the date
	 */
	public int     getYear()  { return theYear; }

	/**
	 * Get the month of the date
	 * 
	 * @return the month of the date
	 */
	public int     getMonth() { return theMonth; }

	/**
	 * Get the day of the date
	 * 
	 * @return the day of the date
	 */
	public int     getDay()   { return theDay; }

	/**
	 * Determine whether the date value is null
	 * 
	 * @return <code>true</code> if the date is null, <code>false</code> otherwise
	 */
	public boolean isNull()   { return (theDate == null); }

	/**
	 * Get the java date associated with this object
	 * 
	 * @return the java date
	 */
	public java.util.Date getDate() { 
		return (theDate == null) ? null 
				                 : theDate.getTime(); }
	
	/**
	 * Obtain the year,month and day values from the date
	 */
	private void obtainValues() {
		theYear  = theDate.get(Calendar.YEAR); 
		theMonth = theDate.get(Calendar.MONTH); 
		theDay   = theDate.get(Calendar.DAY_OF_MONTH); 
	}
	
	/**
	 * Format a Date 
	 * 
	 * @param bShowNULL <code>true</code> if a NULL date is to be formatted as "NULL" rather
	 * than jut being returned as the null string
	 * @return the formatted Date
	 */
	public String formatDate(boolean bShowNULL) {
		StringBuilder   myString = new StringBuilder();
		String[] 		myMonths = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
				              		 "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		String   		myMonth;
		
		if (theDate != null) {
			myMonth = myMonths[theDate.get(Calendar.MONTH)];
			myString.append("0");
			myString.append(theDate.get(Calendar.DAY_OF_MONTH));
			if (myString.length() == 3) myString.deleteCharAt(0);
			myString.append("-");
			myString.append(myMonth);
			myString.append("-");
            myString.append(theDate.get(Calendar.YEAR));
	        return myString.toString();
		}
		return (bShowNULL) ? "NULL" : null;
	}
	
	/**
	 * Compare this Date to another to establish sort order.
	 * 
	 * @param that The Number to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Date that) {
        if (this == that) return 0;
        if (that == null) return -1;
        if (this.theDate == null) return 1;
        if (that.theDate == null) return -1;
        if (this.theYear < that.theYear) return -1;
        if (this.theYear > that.theYear) return 1;
        if (this.theMonth < that.theMonth) return -1;
        if (this.theMonth > that.theMonth) return 1;
        if (this.theDay < that.theDay) return -1;
        if (this.theDay > that.theDay) return 1;
		return 0;
	}
	
	/**
	 * Represents a contiguous Range of dates 
	 */
	public static class Range {
		/**
		 * The Start Date for the range
		 */
		private Date theStart = null;

		/**
		 * The End Date for the range
		 */
		private Date theEnd   = null;
		
		/**
		 * Get the start date for the range
		 * 
		 * @return the Start date
		 */
		public Date  getStart()    { return theStart; }

		/**
		 * Get the end date for the range
		 * 
		 * @return the End date
		 */
		public Date  getEnd()      { return theEnd; }
		
		/**
		 * Construct a Range from a Start Date and an End Date
		 * 
		 * @param pStart the start date
		 * @param pEnd the end date
		 */
		public Range(Date pStart, Date pEnd) {
			theStart = new Date(pStart);
			theEnd   = new Date(pEnd);
		}
		
		/**
		 * Construct a range from another range
		 * 
		 * @param pRange the range to copy from
		 */
		public Range(Range pRange) {
			theStart = new Date(pRange.theStart);
			theEnd   = new Date(pRange.theEnd);
		}
		
		/**
		 * Determine whether a Date is within the date range
		 * 
		 * @return -1, 0, 1 if early, in range or late
		 */
		public short compareTo(Date pDate) {
			if ((theStart != null) && (theStart.compareTo(pDate) > 0)) return 1;
			if ((theEnd != null) && (theEnd.compareTo(pDate) < 0))     return -1;
			return 0;
		}
		
		/**
		 * Compare the range to another range
		 * 		
		 * @return -1, 0, 1 if early, equal or later
		 */
		public int compareTo(Range that) {
			int result;
	        if (this == that) return 0;
	        if (that == null) return -1;
			if (this.getStart() != that.getStart()) {
				if (this.getStart() == null) return 1;
				if (that.getStart() == null) return -1;
				result = theStart.compareTo(that.theStart);
				if (result != 0) return result;
			}
			if (this.getEnd() != that.getEnd()) {
				if (this.getEnd() == null) return 1;
				if (that.getEnd() == null) return -1;
				result = theEnd.compareTo(that.theEnd);
				if (result != 0) return result;
			}
	        return 0;
		}
	}	
}

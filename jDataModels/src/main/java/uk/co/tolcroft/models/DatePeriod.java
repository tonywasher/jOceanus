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
	public DateDay adjustDate(DateDay 	pDate,
							  boolean 	bForward) {
		DateDay myDate;
	
		/* Initialise the date */
		myDate = new DateDay(pDate);
	
		/* Adjust the field */
		myDate.adjustField(theField, (bForward) ? theValue : -theValue);
		
		/* Return the date */
		return myDate;
	}
}

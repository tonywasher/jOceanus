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
package uk.co.tolcroft.finance.sheets;

import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.sheets.SheetReader;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.views.DataControl;

public class FinanceReader extends SheetReader<FinanceData> {
	/**
	 * Thread control
	 */
	private ThreadStatus<FinanceData>	theThread	= null;
	
	/**
	 * Constructor
	 * @param pThread the Thread control
	 */
	public FinanceReader(ThreadStatus<FinanceData> pThread) { 
		/* Call super-constructor */
		super(pThread);
		
		/* Store the thread */
		theThread = pThread;
	}

	/**
	 * Register sheets
	 */
	protected void registerSheets() { 
		/* Register the sheets */
		addSheet(new SheetAccountType(this));
		addSheet(new SheetTransactionType(this));
		addSheet(new SheetTaxType(this));
		addSheet(new SheetTaxRegime(this));
		addSheet(new SheetFrequency(this));
		addSheet(new SheetEventInfoType(this));
		addSheet(new SheetTaxYear(this));
		addSheet(new SheetAccount(this));
		addSheet(new SheetRate(this));
		addSheet(new SheetPrice(this));
		addSheet(new SheetPattern(this));
		addSheet(new SheetEvent(this));
		addSheet(new SheetEventData(this));
		addSheet(new SheetEventValues(this));
	}
	
	/**
	 * Obtain empty DataSet
	 */
	protected FinanceData newDataSet() {
		/* Create the new DataSet */
		DataControl<FinanceData> myControl = theThread.getControl();
		return myControl.getNewData();
	}
}
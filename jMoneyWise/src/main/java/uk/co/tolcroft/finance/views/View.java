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

import uk.co.tolcroft.finance.sheets.FinanceSheet;
import uk.co.tolcroft.finance.ui.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.database.FinanceDatabase;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.threads.WorkerThread;
import uk.co.tolcroft.models.views.DataControl;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;

public class View extends DataControl<FinanceData> {
	/* Members */
	private FinanceData  			theData 			= null;
	private DateDay.Range  			theRange 			= null;
	private MainTab					theCtl 	 			= null;
    private EventAnalysis			theAnalysis			= null;
    private DilutionEvent.List		theDilutions		= null;
    
	/* Access methods */
	public MainTab				getControl()		{ return theCtl; }
	public DateDay.Range		getRange()			{ return theRange; }
	public EventAnalysis    	getAnalysis()		{ return theAnalysis; }
	public DilutionEvent.List   getDilutions()		{ return theDilutions; }
	
 	/* Constructor */
	public View(MainTab pCtl) {
		/* Store access to the main window */
		theCtl	= pCtl;
		
		/* Store access to the Debug Manager */
		setDebugMgr(pCtl.getDebugMgr());

		/* Create an empty data set */
		setData(getNewData());
	}
	
	/**
	 * Obtain a new DataSet
	 * @return new DataSet
	 */
	public FinanceData getNewData() {
		return new FinanceData(getSecurity());
	}
	
	/**
	 * Obtain a Database interface
	 * @return new Database object
	 */
	public Database<FinanceData> getDatabase() throws ModelException {
		return new FinanceDatabase();
	}
	
	/**
	 * Obtain a Database interface
	 * @return new DataSet
	 */
	public SpreadSheet<FinanceData> getSpreadSheet() {
		return new FinanceSheet();
	}

	@Override
	public ThreadStatus<FinanceData> allocateThreadStatus(WorkerThread<?> pThread) { 
		return new ThreadStatus<FinanceData>(pThread, this); }

	/**
	 * Update the data for a view
	 * @param pData the new data set
	 */ 
	public void setData(FinanceData pData) {
		/* Record the data */
		super.setData(pData);
		theData = pData;
		
		/* Analyse the data */
		analyseData(false);
		
		/* Refresh the windows */
		refreshWindow();
	}
	
	/**
	 * Analyse the data in the view
	 * @param bPreserve preserve any error
	 */ 
	protected boolean analyseData(boolean bPreserve) {
		/* Clear the error */
		if (!bPreserve) setError(null);
		
		/* Calculate the Data Range */
		theData.calculateDateRange();
		
		/* Access the range */
		theRange = theData.getDateRange();

		/* Protect against exceptions */
		try {
			/* Analyse the data */
			theData.analyseData(this);
			theAnalysis = theData.getAnalysis();
		
			/* Access the dilutions */
			theDilutions = theAnalysis.getDilutions();

			/* Adjust the updates debug view */
			setUpdates(theData.getUpdateSet());
		}
		
		/* Catch any exceptions */
		catch (ModelException e) {
			if (!bPreserve) setError(e);
		}	

		/* Catch any exceptions */
		catch (Throwable e) {
			/* Report the failure */
			if (!bPreserve)
				setError(new ModelException(ExceptionClass.DATA,
								       "Failed to analyse data",
								       e));
		}	
		
		/* Return whether there was success */
		return (getError() == null);
	}
	
	/**
	 *  refresh the window view
	 */ 
	protected void refreshWindow() {
		/* Protect against exceptions */
		try {
			/* Refresh the Control*/
			theCtl.refreshData();
		}

		/* Catch any exceptions */
		catch (ModelException e) {
			setError(e);
		}	

		/* Catch any exceptions */
		catch (Throwable e) {
			/* Report the failure */
			setError(new ModelException(ExceptionClass.DATA,
								   "Failed refresh window",
								   e));
		}	
	}
}

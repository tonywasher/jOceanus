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
		addSheet(new SheetTaxYear(this));
		addSheet(new SheetAccount(this));
		addSheet(new SheetRate(this));
		addSheet(new SheetPrice(this));
		addSheet(new SheetPattern(this));
		addSheet(new SheetEvent(this));
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

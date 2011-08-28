package uk.co.tolcroft.finance.sheets;

import uk.co.tolcroft.finance.core.Threads.statusCtl;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.sheets.SheetWriter;

public class FinanceWriter  extends SheetWriter<FinanceData>{
	/**
	 * Constructor
	 * @param pThread the Thread control
	 */
	public FinanceWriter(statusCtl pThread) { 
		/* Call super-constructor */
		super( pThread);
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
}
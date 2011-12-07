package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.database.Database;

public class FinanceDatabase extends Database<FinanceData> {
	/**
	 * Construct a new Database class for load
	 */
	public FinanceDatabase() throws Exception {
		/* Add additional tables */
		declareTables();
	}	

	/**
	 * Declare tables
	 */
	private void declareTables() {
		/* Add additional tables */
		addTable(new TableAccountType(this));
		addTable(new TableTransactionType(this));
		addTable(new TableTaxType(this));
		addTable(new TableTaxRegime(this));
		addTable(new TableFrequency(this));
		addTable(new TableEventInfoType(this));
		addTable(new TableTaxYear(this));
		addTable(new TableAccount(this));
		addTable(new TableRate(this));
		addTable(new TablePrice(this));
		addTable(new TablePattern(this));
		addTable(new TableEvent(this));
		addTable(new TableEventData(this));
		addTable(new TableEventValues(this));
	}	
}

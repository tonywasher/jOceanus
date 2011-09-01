package uk.co.tolcroft.models.views;

import uk.co.tolcroft.finance.views.Statement;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.finance.views.ViewPrice;
import uk.co.tolcroft.finance.views.View.*;
import uk.co.tolcroft.finance.views.ViewPrice.List;

public class AccountSet {
	/* Members */
	private View			theView			= null;
	private	Statement		theStatement	= null;
	private ViewPrice.List	thePrices		= null;
	private ViewRates		theRates		= null;
	private ViewPatterns 	thePatterns 	= null;

	/* Access methods */
	public void setPrices(ViewPrice.List pPrices) 		{ thePrices 	= pPrices; }
	public void setRates(ViewRates       pRates) 		{ theRates  	= pRates; }
	public void setPatterns(ViewPatterns pPatterns) 	{ thePatterns	= pPatterns; }
	public void setStatement(Statement	 pStatement)	{ theStatement	= pStatement; }
	
	/**
	 * Constructor
	 * @param pView the view
	 */
	public AccountSet(View pView) {
		/* Store parameters */
		theView = pView;
	}
	
	/** 
	 * Apply changes in an account set back into the underlying finance objects
	 */
	public void applyChanges() {
		/* Prepare the changes */
		prepareChanges();

		/* analyse the data */
		boolean bSuccess = theView.analyseData(false);
		
		/* If we were successful */
		if (bSuccess) {
			/* Commit the changes */
			commitChanges(true);

			/* Refresh windows */
			theView.refreshWindow();
		}

		/* else we failed */
		else {
			/* Rollback the changes */ 
			commitChanges(false);
			
			/* Re-analyse the data */
			theView.analyseData(true);
		}
	}
	
	/** 
	 * Prepare changes in an AccountSet back into the core data
	 */
	private void prepareChanges() {
		/* Prepare the changes */
		if (thePrices    != null) thePrices.prepareChanges();
		if (theRates     != null) theRates.prepareChanges();
		if (thePatterns  != null) thePatterns.prepareChanges();
		if (theStatement != null) theStatement.prepareChanges();
	}

	/** 
	 * Commit/RollBack changes in an AccountSet back into the core data
	 * @param bCo
	 */
	private void commitChanges(boolean bCommit) {
		/* Commit the changes */
		if (thePrices    != null) thePrices.commitChanges(bCommit);
		if (theRates     != null) theRates.commitChanges(bCommit);
		if (thePatterns  != null) thePatterns.commitChanges(bCommit);
		if (theStatement != null) theStatement.commitChanges(bCommit);
	}
}

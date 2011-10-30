package uk.co.tolcroft.models.threads;

import java.io.File;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.ui.FileSelector.BackupCreate;
import uk.co.tolcroft.models.views.DataControl;

public class CreateExtract<T extends DataSet<T>> extends WorkerThread<Void> {
	/* Task description */
	private static String  	theTask		= "Extract Creation";

	/* Properties */
	private DataControl<T>	theControl	= null;
	private ThreadStatus<T>	theStatus	= null;

	/* Constructor (Event Thread)*/
	public CreateExtract(DataControl<T> pControl) {
		/* Call super-constructor */
		super(theTask, pControl.getStatusBar());
		
		/* Store passed parameters */
		theControl	= pControl;

		/* Create the status */
		theStatus = new ThreadStatus<T>(this, theControl);

		/* Initialise the status window */
		initStatusBar("Creating Extract");
	}

	/* Background task (Worker Thread)*/
	public Void performTask() throws Throwable {
		T				myData	  	= null;
		DataSet<T>		myDiff	  	= null;
		SpreadSheet<T>	mySheet		= null;
		boolean			doDelete  	= false;
		File			myFile	  	= null;

		/* Catch Exceptions */
		try {
			/* Determine the name of the file to build */
			BackupCreate myDialog = new BackupCreate(theControl);
			myDialog.selectFile();
			myFile = myDialog.getSelectedFile();

			/* If we did not select a file */
			if (myFile == null) {
				/* Throw cancelled exception */
				throw new Exception(ExceptionClass.EXCEL,
									"Operation Cancelled");					
			}

			/* Create backup */
			mySheet = theControl.getSpreadSheet();
			mySheet.createExtract(theStatus, 
							  	  theControl.getData(), 
							  	  myFile);

			/* File created, so delete on error */
			doDelete = true;

			/* Re-initialise the status window */
			initStatusBar("Reading Extract");
		
			/* .xls will have been added to the file */
			myFile 	= new File(myFile.getPath() + ".xls");

			/* Load workbook */
			myData   = mySheet.loadExtract(theStatus, 
										   myFile);

			/* Re-initialise the status window */
			initStatusBar("Re-applying Security");
		
			/* Initialise the security, from the original data */
			myData.initialiseSecurity(theStatus, theControl.getData());
			
			/* Re-initialise the status window */
			initStatusBar("Verifying Extract");
		
			/* Analyse the Data to ensure that close dates are updated */
			myData.analyseData(theControl);
			
			/* Create a difference set between the two data copies */
			myDiff = myData.getDifferenceSet(theControl.getData());

			/* If the difference set is non-empty */
			if (!myDiff.isEmpty()) {
				/* Throw an exception */
				throw new Exception(ExceptionClass.DATA,
									myDiff,
									"Extract is inconsistent");
			}
		}
		
		/* Catch any exceptions */
		catch (Throwable e) {
			/* Delete the file */
			if (doDelete) myFile.delete();

			/* Report the failure */
			throw e;
		}	

		/* Return nothing */
		return null;
	}
}

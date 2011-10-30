package uk.co.tolcroft.models.threads;

import java.io.File;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.ui.FileSelector.BackupCreate;
import uk.co.tolcroft.models.views.DataControl;

public class CreateBackup<T extends DataSet<T>> extends LoaderThread<T> {
	/* Task description */
	private static String  	theTask		= "Backup Creation";

	/* Properties */
	private DataControl<T>	theControl	= null;
	private ThreadStatus<T>	theStatus	= null;

	/* Constructor (Event Thread)*/
	public CreateBackup(DataControl<T> pControl) {
		/* Call super-constructor */
		super(theTask, pControl);
		
		/* Store passed parameters */
		theControl	= pControl;

		/* Create the status */
		theStatus = new ThreadStatus<T>(this, theControl);

		/* Initialise the status window */
		initStatusBar("Creating Backup");
	}

	/* Background task (Worker Thread)*/
	public T performTask() throws Throwable {
		T				myData		= null;
		DataSet<T>		myDiff		= null;
		SpreadSheet<T>	mySheet		= null;
		boolean			doDelete	= false;
		File			myFile		= null;

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
			mySheet.createBackup(theStatus, 
								 theControl.getData(), 
								 myFile);

			/* File created, so delete on error */
			doDelete = true;

			/* Re-initialise the status window */
			initStatusBar("Verifying Backup");

			/* As we have encrypted then .zip was added to the file */
			myFile 	= new File(myFile.getPath() + ".zip");
			
			/* Load workbook */
			myData   = mySheet.loadBackup(theStatus, 
										  myFile);

			/* Create a difference set between the two data copies */
			myDiff = myData.getDifferenceSet(theControl.getData());

			/* If the difference set is non-empty */
			if (!myDiff.isEmpty()) {
				/* Throw an exception */
				throw new Exception(ExceptionClass.DATA,
									myDiff,
									"Backup is inconsistent");
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

package uk.co.tolcroft.models.threads;

import java.io.File;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.ui.FileSelector.BackupLoad;
import uk.co.tolcroft.models.views.DataControl;

public class LoadBackup<T extends DataSet<T,?>> extends LoaderThread<T> {
	/* Task description */
	private static String  	theTask		= "Backup Restoration";

	/* Properties */
	private DataControl<T> 	theControl 	= null;
	private ThreadStatus<T>	theStatus   = null;

	/* Constructor (Event Thread)*/
	public LoadBackup(DataControl<T> pControl) {
		/* Call super-constructor */
		super(theTask, pControl);
		
		/* Store passed parameters */
		theControl	= pControl;

		/* Create the status */
		theStatus = new ThreadStatus<T>(this, theControl);

		/* Initialise the status window */
		initStatusBar("Loading Backup");
	}

	/* Background task (Worker Thread)*/
	public T performTask() throws Throwable {
		T				myData	  = null;
		T				myStore;
		Database<T>		myDatabase;
		SpreadSheet<T>	mySheet;
		File			myFile;

		/* Determine the name of the file to load */
		BackupLoad myDialog = new BackupLoad(theControl);
		myDialog.selectFile();
		myFile = myDialog.getSelectedFile();
			
		/* If we did not select a file */
		if (myFile == null) {
			/* Throw cancelled exception */
			throw new Exception(ExceptionClass.EXCEL,
								"Operation Cancelled");					
		}
			
		/* Load workbook */
		mySheet	 = theControl.getSpreadSheet();
		myData   = mySheet.loadBackup(theStatus, 
									  myFile);

		/* Initialise the status window */
		initStatusBar("Accessing DataStore");

		/* Create interface */
		myDatabase = theControl.getDatabase();

		/* Load underlying database */
		myStore	= myDatabase.loadDatabase(theStatus);

		/* Re-base the loaded backup onto the database image */
		myData.reBase(myStore);
		
		/* Return the Data */
		return myData;
	}	
}

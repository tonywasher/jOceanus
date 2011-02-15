package uk.co.tolcroft.finance.core;

import java.util.List;
import java.io.File;

import javax.swing.SwingWorker;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.database.*;
import uk.co.tolcroft.finance.sheets.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.ui.*;
import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class Threads {
	public interface ThreadControl {
		public boolean cancel(boolean doInterrupt);
		public boolean isCancelled();
		public void    publish(ThreadStatus pStatus);
	}
	
	public interface StatusControl {
		public boolean setNumStages(int pNumStages);
		public boolean setNewStage(String pStage);
		public boolean setNumSteps(int pNumSteps);
		public boolean setStepsDone(int pStepsDone);
	}
	
	public static class ThreadStatus {
		/* Properties */
		private int	   theNumSteps   = 0;;
		private int    theStepsDone  = 0;
		private int    theNumStages  = 0;
		private int    theStagesDone = 0;;
		private String theStage		 = null;
		
		/* Access methods */
		public int	  getNumSteps()   { return theNumSteps; }
		public int	  getStepsDone()  { return theStepsDone; }
		public int	  getNumStages()  { return theNumStages; }
		public int	  getStagesDone() { return theStagesDone; }
		public String getStage()      { return theStage; }
		
		/* Constructors */
		public ThreadStatus() {}
		public ThreadStatus(ThreadStatus pStatus) {
			theNumSteps   = pStatus.theNumSteps;
			theNumStages  = pStatus.theNumStages;
			theStepsDone  = pStatus.theStepsDone;
			theStagesDone = pStatus.theStagesDone;
			theStage      = pStatus.theStage;
		}
	}

	public static class statusCtl implements StatusControl {
		private ThreadControl 	theThread		= null;
		private ThreadStatus 	theStatus		= null;
		private View			theView			= null;
		private Properties		theProperties	= null;
		private int          	theSteps    	= 50;
		
		/* Access methods */
		public int 			getReportingSteps() { return theSteps; }
		public Properties 	getProperties() 	{ return theProperties; }
		public View			getView() 			{ return theView; }
			
		/* Constructor */
		public statusCtl(ThreadControl 	pThread,
				         Properties 	pProperties,
				         View			pView) {
			/* Store parameter */
			theThread 		= pThread;
			theProperties 	= pProperties;
			theView			= pView;
			
			/* Create the status */
			theStatus = new ThreadStatus();
		}
		
		/* Publish task 0 (Worker Thread)*/
		public boolean setNumStages(int pNumStages) {
			/* Check for cancellation */
			if (theThread.isCancelled()) return false;
			
			/* Set number of Stages and set Stages done to -1 */
			theStatus.theNumStages  = pNumStages;
			theStatus.theStagesDone = -1;
			
			/* Return to caller */
			return true;
		}
		
		/* Publish task 1 (Worker Thread)*/
		public boolean setNewStage(String pStage) {
			ThreadStatus myStatus;
			
			/* Check for cancellation */
			if (theThread.isCancelled()) return false;
			
			/* Store the stage and increment stages done */
			theStatus.theStage = pStage;
			theStatus.theStagesDone++;
			theStatus.theNumSteps  = 100;
			if (theStatus.theStagesDone < theStatus.theNumStages) 
				theStatus.theStepsDone = 0;
			else 
				theStatus.theStepsDone = 100;
			
			/* Create a new Status */
			myStatus = new ThreadStatus(theStatus);
			
			/* Publish it */
			theThread.publish(myStatus);
			
			/* Return to caller */
			return true;
		}
		
		/* Publish task 2 (Worker Thread)*/
		public boolean setNumSteps(int pNumSteps) {
			/* Check for cancellation */
			if (theThread.isCancelled()) return false;
			
			/* Set number of Steps */
			theStatus.theNumSteps  = pNumSteps;
			
			/* Return to caller */
			return true;
		}
		
		/* Publish task 3 (Worker Thread)*/
		public boolean setStepsDone(int pStepsDone) {
			ThreadStatus myStatus;
			
			/* Check for cancellation */
			if (theThread.isCancelled()) return false;
			
			/* Set Steps done */
			theStatus.theStepsDone  = pStepsDone;
			
			/* Create a new Status */
			myStatus = new ThreadStatus(theStatus);
			
			/* Publish it */
			theThread.publish(myStatus);
			
			/* Return to caller */
			return true;
		}
	}
	
	public static class loadDatabase extends SwingWorker <DataSet, ThreadStatus> 
	 								 implements ThreadControl {
		/* Properties */
		private View		theView      = null;
		private MainTab		theWindow    = null;
		private StatusBar   theStatusBar = null;
		private statusCtl	theStatus    = null;
		private Exception 	theError 	 = null;

		/* Constructor (Event Thread)*/
		public loadDatabase(View pView, MainTab pWindow) {
			/* Store passed parameters */
			theView   = pView;
			theWindow = pWindow;

			/* Access the Status Bar */
			theStatusBar = theWindow.getStatusBar();

			/* Create the status */
			theStatus = new statusCtl(this, theWindow.getProperties(), pView);

			/* Initialise the status window */
			theStatusBar.setOperation("Loading Database");
			theStatusBar.setStage("", 0, 100); 
			theStatusBar.setSteps(0, 100);
			theStatusBar.getProgressPanel().setVisible(true);
		}

		/* Background task (Worker Thread)*/
		public DataSet doInBackground() {
			DataSet		myData	   	= null;
			Database	myDatabase	= null;

			try {
				/* Create interface */
				myDatabase = new Database(theWindow.getProperties());

				/* Load database */
				myData    = myDatabase.loadDatabase(theStatus);
			}	

			/* Catch any exceptions */
			catch (Exception e) {
				theError = e;
				return null;
			}	

			/* Catch any exceptions */
			catch (Throwable e) {
				/* Report the failure */
				theError = new Exception(ExceptionClass.DATA,
									     "Failed to load database",
									     e);
				return null;
			}	

			/* Return the loaded data */
			return myData;
		}

		/* Completion task (Event Thread)*/
		public void done() {
			DataSet myData;
			String  myOp = "DataBase load";

			try {
				/* If we are not cancelled */
				if (!isCancelled()) {
					
					/* Get the newly loaded data */
					myData = get();

					/* If we have new data */
					if (myData != null) {
						/* Activate the data and obtain any error */
						theView.setData(myData);
						theError = theView.getError();
					}
					
					/* Set success/failure */
					if (theError == null)
						theStatusBar.setSuccess(myOp);
					else
						theStatusBar.setFailure(myOp, theError);
				}

				/* Report the cancellation */
				else theStatusBar.setFailure(myOp, theError);
			}	 	
			catch (Throwable e) {
				/* Report the failure */
				theError = new Exception(ExceptionClass.DATA,
									     "Failed to obtain and activate new data",
									     e);
				theStatusBar.setFailure(myOp, theError);
			}
		}		

		/* Process task (Event Thread)*/
		protected void process(List<ThreadStatus> pStatus) {
			/* Access the latest status */
			ThreadStatus myStatus = pStatus.get(pStatus.size() - 1);

			/* Update the status window */
			theStatusBar.setStage(myStatus.getStage(), 
							  	  myStatus.getStagesDone(),
							  	  myStatus.getNumStages());
			theStatusBar.setSteps(myStatus.getStepsDone(),
							  	  myStatus.getNumSteps());
		}

		/* Publish */
		public void publish(ThreadStatus pStatus) {
			super.publish(pStatus);
		}
	}

	public static class storeDatabase extends SwingWorker <Void, ThreadStatus> 
	  								  implements ThreadControl {
		/* Properties */
		private View      	theView      = null;
		private MainTab     theWindow    = null;
		private StatusBar   theStatusBar = null;
		private statusCtl	theStatus    = null;
		private Exception 	theError 	 = null;

		/* Constructor (Event Thread)*/
		public storeDatabase(View pView, MainTab pWindow) {
			/* Store passed parameters */
			theView   = pView;
			theWindow = pWindow;

			/* Access the Status Bar */
			theStatusBar = theWindow.getStatusBar();

			/* Create the status */
			theStatus = new statusCtl(this, theWindow.getProperties(), pView);

			/* Initialise the status window */
			theStatusBar.setOperation("Storing to Database");
			theStatusBar.setStage("", 0, 100); 
			theStatusBar.setSteps(0, 100);
			theStatusBar.getProgressPanel().setVisible(true);
		}

		/* Background task (Worker Thread)*/
		public Void doInBackground() {
			Database	myDatabase	= null;
			DataSet		myData;
			DataSet		myDiff;

			try {
				/* Create interface */
				myDatabase = new Database(theWindow.getProperties());

				/* Store database */
				myDatabase.updateItems(theStatus, theView.getData());

				/* Re-initialise the status window */
				theStatusBar.setOperation("Verifying Store");
				theStatusBar.setStage("", 0, 100); 
				theStatusBar.setSteps(0, 100);
				theStatusBar.getProgressPanel().setVisible(true);

				/* Load database */
				myData   = myDatabase.loadDatabase(theStatus);

				/* Create a difference set between the two data copies */
				myDiff = new DataSet(myData, theView.getData());

				/* If the difference set is non-empty */
				if (!myDiff.isEmpty()) {
					/* Throw an exception */
					throw new Exception(ExceptionClass.DATA,
										myDiff,
										"DataStore is inconsistent");
				}
			}	

			/* Catch any exceptions */
			catch (Exception e) {
				theError = e;
				return null;
			}	

			/* Catch any exceptions */
			catch (Throwable e) {
				/* Report the failure */
				theError = new Exception(ExceptionClass.DATA,
										 "Failed to store database",
										 e);
				return null;
			}	

			/* Return null */
			return null;
		}

		/* Completion task (Event Thread)*/
		public void done() {
			String  myOp = "DataBase Store";

			/* If we are not cancelled and have no error */
			if ((!isCancelled()) && (theError == null)) {
				/* Set success */
				theStatusBar.setSuccess(myOp);
			}

			/* Else report the cancellation/failure */
			else theStatusBar.setFailure(myOp, theError);
		}		

		/* Process task (Event Thread)*/
		protected void process(List<ThreadStatus> pStatus) {
			/* Access the latest status */
			ThreadStatus myStatus = pStatus.get(pStatus.size() - 1);

			/* Update the status window */
			theStatusBar.setStage(myStatus.getStage(), 
								  myStatus.getStagesDone(),
								  myStatus.getNumStages());
			theStatusBar.setSteps(myStatus.getStepsDone(),
								  myStatus.getNumSteps());
		}

		/* Publish */
		public void publish(ThreadStatus pStatus) {
			super.publish(pStatus);
		}
	}

	public static class createDatabase extends SwingWorker <Void, ThreadStatus> 
	  								   implements ThreadControl {
		/* Properties */
		private View		theView		 = null;
		private MainTab     theWindow    = null;
		private StatusBar   theStatusBar = null;
		private statusCtl	theStatus    = null;
		private Exception 	theError 	 = null;

		/* Constructor (Event Thread)*/
		public createDatabase(View pView, MainTab pWindow) {
			/* Store passed parameters */
			theWindow = pWindow;
			theView	  = pView;

			/* Access the Status Bar */
			theStatusBar = theWindow.getStatusBar();

			/* Create the status */
			theStatus = new statusCtl(this, theWindow.getProperties(), pView);

			/* Initialise the status window */
			theStatusBar.setOperation("Creating Database");
			theStatusBar.setStage("", 0, 100); 
			theStatusBar.setSteps(0, 100);
			theStatusBar.getProgressPanel().setVisible(true);
		}

		/* Background task (Worker Thread)*/
		public Void doInBackground() {
			Database	myDatabase	= null;
			DataSet		myData;
			DataSet		myNull;

			try {
				/* Create interface */
				myDatabase = new Database(theWindow.getProperties());

				/* Load database */
				myDatabase.createTables(theStatus);

				/* Re-base this set on a null set */
				myNull = new DataSet(null);
				myData = theView.getData();
				myData.reBase(myNull);
			}	

			/* Catch any exceptions */
			catch (Exception e) {
				theError = e;
				return null;
			}	

			/* Catch any exceptions */
			catch (Throwable e) {
				/* Report the failure */
				theError = new Exception(ExceptionClass.DATA,
									     "Failed to create tables",
									     e);
				return null;
			}	

			/* Return the loaded data */
			return null;
		}

		/* Completion task (Event Thread)*/
		public void done() {
			String  myOp = "DataBase Creation";

			/* If we are not cancelled and have no error */
			if ((!isCancelled()) && (theError == null)) {
				/* Set success */
				theStatusBar.setSuccess(myOp);
			}

			/* Else report the cancellation/failure */
			else theStatusBar.setFailure(myOp, theError);
		}		

		/* Process task (Event Thread)*/
		protected void process(List<ThreadStatus> pStatus) {
			/* Access the latest status */
			ThreadStatus myStatus = pStatus.get(pStatus.size() - 1);

			/* Update the status window */
			theStatusBar.setStage(myStatus.getStage(), 
								  myStatus.getStagesDone(),
								  myStatus.getNumStages());
			theStatusBar.setSteps(myStatus.getStepsDone(),
								  myStatus.getNumSteps());
		}

		/* Publish */
		public void publish(ThreadStatus pStatus) {
			super.publish(pStatus);
		}
	}

	public static class purgeDatabase extends SwingWorker <Void, ThreadStatus> 
	   								  implements ThreadControl {
		/* Properties */
		private View		theView		 = null;
		private MainTab     theWindow    = null;
		private StatusBar   theStatusBar = null;
		private statusCtl	theStatus    = null;
		private Exception 	theError 	 = null;

		/* Constructor (Event Thread)*/
		public purgeDatabase(View pView, MainTab pWindow) {
			/* Store passed parameters */
			theWindow = pWindow;
			theView	  = pView;

			/* Access the Status Bar */
			theStatusBar = theWindow.getStatusBar();

			/* Create the status */
			theStatus = new statusCtl(this, theWindow.getProperties(), pView);

			/* Initialise the status window */
			theStatusBar.setOperation("Purging Database");
			theStatusBar.setStage("", 0, 100); 
			theStatusBar.setSteps(0, 100);
			theStatusBar.getProgressPanel().setVisible(true);
		}

		/* Background task (Worker Thread)*/
		public Void doInBackground() {
			Database	myDatabase	= null;
			DataSet		myData;
			DataSet		myNull;

			try {
				/* Create interface */
				myDatabase = new Database(theWindow.getProperties());

				/* Load database */
				myDatabase.purgeTables(theStatus);

				/* Re-base this set on a null set */
				myNull = new DataSet(null);
				myData = theView.getData();
				myData.reBase(myNull);
			}	

			/* Catch any exceptions */
			catch (Exception e) {
				theError = e;
				return null;
			}	

			/* Catch any exceptions */
			catch (Throwable e) {
				/* Report the failure */
				theError = new Exception(ExceptionClass.DATA,
									     "Failed to purge tables",
									     e);
				return null;
			}	

			/* Return the loaded data */
			return null;
		}

		/* Completion task (Event Thread)*/
		public void done() {
			String  myOp = "DataBase Purge";

			/* If we are not cancelled and have no error */
			if ((!isCancelled()) && (theError == null)) {
				/* Set success */
				theStatusBar.setSuccess(myOp);
			}

			/* Else report the cancellation/failure */
			else theStatusBar.setFailure(myOp, theError);
		}		

		/* Process task (Event Thread)*/
		protected void process(List<ThreadStatus> pStatus) {
			/* Access the latest status */
			ThreadStatus myStatus = pStatus.get(pStatus.size() - 1);

			/* Update the status window */
			theStatusBar.setStage(myStatus.getStage(), 
								  myStatus.getStagesDone(),
								  myStatus.getNumStages());
			theStatusBar.setSteps(myStatus.getStepsDone(),
								  myStatus.getNumSteps());
		}

		/* Publish */
		public void publish(ThreadStatus pStatus) {
			super.publish(pStatus);
		}
	}

	public static class loadSpreadsheet extends SwingWorker <DataSet, ThreadStatus> 
										implements ThreadControl {
		/* Properties */
		private View      		theView      	= null;
		private MainTab     	theWindow    	= null;
		private StatusBar   	theStatusBar 	= null;
		private statusCtl		theStatus    	= null;
		private Properties		theProperties	= null;
		private Exception 		theError 	 	= null;

		/* Access methods */
		public Exception 	getError() 			{ return theError; }

		/* Constructor (Event Thread)*/
		public loadSpreadsheet(View pView, MainTab pWindow) {
			/* Store passed parameters */
			theView   		= pView;
			theWindow 		= pWindow;
			theProperties 	= theWindow.getProperties();

			/* Access the Status Bar */
			theStatusBar = theWindow.getStatusBar();

			/* Create the status */
			theStatus = new statusCtl(this, theProperties, pView);

			/* Initialise the status window */
			theStatusBar.setOperation("Loading Spreadsheet");
			theStatusBar.setStage("", 0, 100); 
			theStatusBar.setSteps(0, 100);
			theStatusBar.getProgressPanel().setVisible(true);
		}

		/* Background task (Worker Thread)*/
		public DataSet doInBackground() {
			DataSet    		myData   = null;
			DataSet			myStore;
			Database		myDatabase;

			try {
				/* Load workbook */
				myData   = SpreadSheet.loadArchive(theStatus, 
												   new File(theProperties.getBaseSpreadSheet()));

				/* Re-initialise the status window */
				theStatusBar.setOperation("Accessing Data Store");
				theStatusBar.setStage("", 0, 100); 
				theStatusBar.setSteps(0, 100);
				theStatusBar.getProgressPanel().setVisible(true);

				/* Create interface */
				myDatabase = new Database(theWindow.getProperties());

				/* Load underlying database */
				myStore	= myDatabase.loadDatabase(theStatus);

				/* Initialise the static, either from database or with a new security control */
				myData.adoptStatic(myStore);
				
				/* Re-base the loaded spreadsheet onto the database image */
				myData.reBase(myStore);
			}

			/* Catch any exceptions */
			catch (Exception e) {
				theError = e;
				return null;
			}

			/* Catch any exceptions */
			catch (Throwable e) {
				/* Report the failure */
				theError = new Exception(ExceptionClass.DATA,
										 "Failed to load spreadsheet",
										 e);
				return null;
			}	

			/* Return the loaded data */
			return myData;
		}

		/* Completion task (Event Thread)*/
		public void done() {
			DataSet myData;
			String  myOp = "Spreadsheet load";

			try {
				/* If we are not cancelled */
				if (!isCancelled()) {
					/* Get the newly loaded data */
					myData = get();
					
					/* If we have new data */
					if (myData != null) {
						/* Activate the data and obtain any error */
						theView.setData(myData);
						theError = theView.getError();
					}
					
					/* Set success/failure */
					if (theError == null)
						theStatusBar.setSuccess(myOp);
					else
						theStatusBar.setFailure(myOp, theError);
				}

				/* Report the cancellation */
				else theStatusBar.setFailure(myOp, theError);
			} 
			catch (Throwable e) {
				theError = new Exception(ExceptionClass.DATA,
										 "Failed to obtain and activate new data",
										 e);				
				theStatusBar.setFailure(myOp, theError);
			}
		}		

		/* Process task (Event Thread)*/
		protected void process(List<ThreadStatus> pStatus) {
			/* Access the latest status */
			ThreadStatus myStatus = pStatus.get(pStatus.size() - 1);

			/* Update the status window */
			theStatusBar.setStage(myStatus.getStage(), 
								  myStatus.getStagesDone(),
								  myStatus.getNumStages());
			theStatusBar.setSteps(myStatus.getStepsDone(),
								  myStatus.getNumSteps());
		}

		/* Publish */
		public void publish(ThreadStatus pStatus) {
			super.publish(pStatus);
		}
	}
	
	public static class restoreBackup extends SwingWorker <DataSet, ThreadStatus> 
	  								  implements ThreadControl {
		/* Properties */
		private View      	theView      	= null;
		private MainTab     theWindow    	= null;
		private StatusBar   theStatusBar 	= null;
		private statusCtl	theStatus    	= null;
		private Properties	theProperties	= null;
		private Exception	theError 	 	= null;

		/* Access methods */
		public Exception 	getError() 			{ return theError; }

		/* Constructor (Event Thread)*/
		public restoreBackup(View pView, MainTab pWindow) {
			/* Store passed parameters */
			theView   		= pView;
			theWindow 		= pWindow;
			theProperties 	= theWindow.getProperties();

			/* Access the Status Bar */
			theStatusBar = theWindow.getStatusBar();

			/* Create the status */
			theStatus = new statusCtl(this, theWindow.getProperties(), pView);

			/* Initialise the status window */
			theStatusBar.setOperation("Loading Backup");
			theStatusBar.setStage("", 0, 100); 
			theStatusBar.setSteps(0, 100);
			theStatusBar.getProgressPanel().setVisible(true);
		}

		/* Background task (Worker Thread)*/
		public DataSet doInBackground() {
			DataSet						myData	  = null;
			DataSet						myStore;
			Database					myDatabase;
			String						myName;
			File						myFile;

			try {
				/* Build the file name */
				myName  = theProperties.getBackupDir();
				myName += File.separator;
				myName += theProperties.getBackupFile();
				if (theProperties.doEncryptBackups()) 
					myName += ".zip";
				myFile  = new File(myName);

				/* Load workbook */
				myData   = SpreadSheet.loadBackup(theStatus, 
												  myFile);

				/* Re-initialise the status window */
				theStatusBar.setOperation("Accessing Data Store");
				theStatusBar.setStage("", 0, 100); 
				theStatusBar.setSteps(0, 100);
				theStatusBar.getProgressPanel().setVisible(true);

				/* Create interface */
				myDatabase = new Database(theWindow.getProperties());

				/* Load underlying database */
				myStore	= myDatabase.loadDatabase(theStatus);

				/* Re-base the loaded backup onto the database image */
				myData.reBase(myStore);
			}	

			/* Catch any exceptions */
			catch (Exception e) {
				/* Report the failure */
				theError = e;
				return null;
			}	

			/* Catch any exceptions */
			catch (Throwable e) {
				/* Report the failure */
				theError = new Exception(ExceptionClass.DATA,
										 "Failed to restore backup",
										 e);
				return null;
			}	

			/* Return the new Data */
			return myData;
		}

		/* Completion task (Event Thread)*/
		public void done() {
			DataSet myData;
			String  myOp = "Backup restoration";

			try {
				/* If we are not cancelled */
				if (!isCancelled()) {
					/* Get the newly loaded data */
					myData = get();

					/* If we have new data */
					if (myData != null) {
						/* Activate the data and obtain any error */
						theView.setData(myData);
						theError = theView.getError();
					}
					
					/* Set success/failure */
					if (theError == null)
						theStatusBar.setSuccess(myOp);
					else
						theStatusBar.setFailure(myOp, theError);
				}

				/* Report the cancellation */
				else theStatusBar.setFailure(myOp, theError);
			} 
			catch (Throwable e) {
				theError = new Exception(ExceptionClass.DATA,
										 "Failed to obtain and activate new data",
										 e);				
				theStatusBar.setFailure(myOp, theError);
			}
		}		

		/* Process task (Event Thread)*/
		protected void process(List<ThreadStatus> pStatus) {
			/* Access the latest status */
			ThreadStatus myStatus = pStatus.get(pStatus.size() - 1);

			/* Update the status window */
			theStatusBar.setStage(myStatus.getStage(), 
								  myStatus.getStagesDone(),
								  myStatus.getNumStages());
			theStatusBar.setSteps(myStatus.getStepsDone(),
								  myStatus.getNumSteps());
		}

		/* Publish */
		public void publish(ThreadStatus pStatus) {
			super.publish(pStatus);
		}
	}

	public static class writeBackup extends SwingWorker <Void, ThreadStatus> 
									implements ThreadControl {
		/* Properties */
		private View      	theView      	= null;
		private MainTab    	theWindow    	= null;
		private StatusBar   theStatusBar 	= null;
		private statusCtl	theStatus    	= null;
		private Properties	theProperties	= null;
		private Exception 	theError 	 	= null;

		/* Access methods */
		public Exception 	getError() 			{ return theError; }

		/* Constructor (Event Thread)*/
		public writeBackup(View pView, MainTab pWindow) {
			/* Store passed parameters */
			theView       = pView;
			theWindow     = pWindow;
			theProperties = theWindow.getProperties();

			/* Access the Status Bar */
			theStatusBar = theWindow.getStatusBar();

			/* Create the status */
			theStatus = new statusCtl(this, theWindow.getProperties(), pView);

			/* Initialise the status window */
			theStatusBar.setOperation("Writing Backup");
			theStatusBar.setStage("", 0, 100); 
			theStatusBar.setSteps(0, 100);
			theStatusBar.getProgressPanel().setVisible(true);
		}

		/* Background task (Worker Thread)*/
		public Void doInBackground() {
			DataSet		myData	  = null;
			DataSet		myDiff	  = null;
			boolean		doDelete  = false;
			String		myName;
			File		myFile;

			/* Build the file name */
			myName  = theProperties.getBackupDir();
			myName += File.separator;
			myName += theProperties.getBackupFile();
			myFile  = new File(myName);

			try {
				/* Create backup */
				SpreadSheet.createBackup(theStatus, 
										 theView.getData(), 
										 myFile, 
										 theProperties.doEncryptBackups());

				/* File created, so delete on error */
				doDelete = true;

				/* Re-initialise the status window */
				theStatusBar.setOperation("Verifying Backup");
				theStatusBar.setStage("", 0, 100); 
				theStatusBar.setSteps(0, 100);
				theStatusBar.getProgressPanel().setVisible(true);

				/* If we encrypted then .zip was added to the file */
				if (theProperties.doEncryptBackups())
					myFile 	= new File(myFile.getPath() + ".zip");
				
				/* Load workbook */
				myData   = SpreadSheet.loadBackup(theStatus, 
												  myFile);

				/* Create a difference set between the two data copies */
				myDiff = new DataSet(myData, theView.getData());

				/* If the difference set is non-empty */
				if (!myDiff.isEmpty()) {
					/* Throw an exception */
					throw new Exception(ExceptionClass.DATA,
										myDiff,
										"Backup is inconsistent");
				}
			}	

			/* Catch any exceptions */
			catch (Exception e) {
				/* Delete the file */
				if (doDelete) myFile.delete();

				/* Report the failure */
				theError = e;
				return null;
			}	

			/* Catch any exceptions */
			catch (Throwable e) {
				/* Delete the file */
				if (doDelete) myFile.delete();

				/* Report the failure */
				theError = new Exception(ExceptionClass.DATA,
										 "Failed to validate backup",
										 e);
				return null;
			}	

			/* Return nothing */
			return null;
		}

		/* Completion task (Event Thread)*/
		public void done() {
			String  myOp = "Backup creation";

			/* If we are not cancelled and have no error */
			if ((!isCancelled()) && (theError == null)) {
				/* Set success */
				theStatusBar.setSuccess(myOp);
			}

			/* Else report the cancellation/failure */
			else theStatusBar.setFailure(myOp, theError);
		}		

		/* Process task (Event Thread)*/
		protected void process(List<ThreadStatus> pStatus) {
			/* Access the latest status */
			ThreadStatus myStatus = pStatus.get(pStatus.size() - 1);

			/* Update the status window */
			theStatusBar.setStage(myStatus.getStage(), 
								  myStatus.getStagesDone(),
								  myStatus.getNumStages());
			theStatusBar.setSteps(myStatus.getStepsDone(),
								  myStatus.getNumSteps());
		}

		/* Publish */
		public void publish(ThreadStatus pStatus) {
			super.publish(pStatus);
		}
	}
}

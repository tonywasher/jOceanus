package finance;

import java.util.List;
import java.io.*;
import javax.swing.SwingWorker;

import finance.finObject.ExceptionClass;
import finance.finUtils.Controls.StatusBar;
import finance.finObject.ObjectClass;

public class finThread {
	
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
		private finProperties	theProperties	= null;
		private int          	theSteps    	= 50;
		
		/* Access methods */
		public int 				getReportingSteps() { return theSteps; }
		public finProperties 	getProperties() 	{ return theProperties; }
			
		/* Constructor */
		public statusCtl(ThreadControl pThread,
				         finProperties pProperties) {
			/* Store parameter */
			theThread 		= pThread;
			theProperties 	= pProperties;
			
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
	
	public static class loadSpreadsheet extends SwingWorker <finData, ThreadStatus> 
										implements ThreadControl {
		/* Properties */
		private finView      		theView      	= null;
		private finSwing     		theWindow    	= null;
		private StatusBar    		theStatusBar 	= null;
		private statusCtl	 		theStatus    	= null;
		private finProperties		theProperties	= null;
		private finObject.Exception theError 	 	= null;

		/* Access methods */
		public finObject.Exception 	getError() 			{ return theError; }
		
		/* Constructor (Event Thread)*/
		public loadSpreadsheet(finView pView, finSwing pWindow) {
			/* Store passed parameters */
			theView   		= pView;
			theWindow 		= pWindow;
			theProperties 	= theWindow.getProperties();
			
			/* Access the Status Bar */
			theStatusBar = theWindow.getStatusBar();
			
			/* Create the status */
			theStatus = new statusCtl(this, theProperties);
			
			/* Initialise the status window */
			theStatusBar.setOperation("Loading Spreadsheet");
			theStatusBar.setStage("", 0, 100); 
			theStatusBar.setSteps(0, 100);
			theStatusBar.getProgressPanel().setVisible(true);
		}
		
		/* Background task (Worker Thread)*/
		public finData doInBackground() {
			finData                    myFin    = null;
			finSpreadsheet.ExcelParser myParser = null;
			
			try {
				/* Create parser */
				myParser = new finSpreadsheet.ExcelParser(theProperties.getBaseSpreadSheet(),
						                                  theStatus);
				
				/* Load workbook */
				myFin    = myParser.loadWorkbook();
			}
			
			/* Catch any exceptions */
			catch (finObject.Exception e) {
				theError = e;
				return null;
			}
			
			/* Return the loaded data */
			return myFin;
		}
		
		/* Completion task (Event Thread)*/
		public void done() {
			finData myData;
			String  myOp = "Spreadsheet load";

			try {
				/* If we are not cancelled */
				if (!isCancelled()) {
					/* Get the newly loaded data */
					myData = get();
					
					/* If we have new data */
					if (myData != null) {
						/* Activate the data and set success */
						theView.setData(myData);
						theStatusBar.setSuccess(myOp);
					}
						
					/* Else report the failure */
					else theStatusBar.setFailure(myOp, theError);
				}
					
				/* Report the cancellation */
				else theStatusBar.setFailure(myOp, theError);
			} 
			catch (Exception e) {
				theError = new finObject.Exception(ExceptionClass.DATA,
												   "Failure while refreshing data",
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
	
	public static class loadDatabase extends SwingWorker <finData, ThreadStatus> 
									 implements ThreadControl {
		/* Properties */
		private finView      		theView      = null;
		private finSwing     		theWindow    = null;
		private StatusBar    		theStatusBar = null;
		private statusCtl	 		theStatus    = null;
		private finObject.Exception theError 	 = null;

		/* Constructor (Event Thread)*/
		public loadDatabase(finView pView, finSwing pWindow) {
			/* Store passed parameters */
			theView   = pView;
			theWindow = pWindow;

			/* Access the Status Bar */
			theStatusBar = theWindow.getStatusBar();

			/* Create the status */
			theStatus = new statusCtl(this, theWindow.getProperties());
			
			/* Initialise the status window */
			theStatusBar.setOperation("Loading Database");
			theStatusBar.setStage("", 0, 100); 
			theStatusBar.setSteps(0, 100);
			theStatusBar.getProgressPanel().setVisible(true);
		}

		/* Background task (Worker Thread)*/
		public finData doInBackground() {
			finData                myFin    = null;
			finStorage.Batch    myBatch  = null;

			try {
				/* Create parser */
				myBatch = new finStorage.Batch(theStatus);

				/* Load database */
				myFin    = myBatch.loadDatabase();
			}	

			/* Catch any exceptions */
			catch (finObject.Exception e) {
				theError = e;
				return null;
			}	

			/* Return the loaded data */
			return myFin;
		}

		/* Completion task (Event Thread)*/
		public void done() {
			finData myData;
			String  myOp = "DataBase load";

			try {
				/* If we are not cancelled */
				if (!isCancelled()) {
					/* Get the newly loaded data */
					myData = get();
					
					/* If we have new data */
					if (myData != null) {
						/* Activate the data and set success */
						theView.setData(myData);
						theStatusBar.setSuccess(myOp);
					}
						
					/* Else report the failure */
					else theStatusBar.setFailure(myOp, theError);
				}
					
				/* Report the cancellation */
				else theStatusBar.setFailure(myOp, theError);
			} 
			catch (InterruptedException ignore) {}
			catch (java.util.concurrent.ExecutionException ignore) {}
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
		private finView      		theView      = null;
		private finSwing     		theWindow    = null;
		private StatusBar    		theStatusBar = null;
		private statusCtl	 		theStatus    = null;
		private finObject.Exception theError 	 = null;

		/* Constructor (Event Thread)*/
		public storeDatabase(finView pView, finSwing pWindow) {
			/* Store passed parameters */
			theView   = pView;
			theWindow = pWindow;

			/* Access the Status Bar */
			theStatusBar = theWindow.getStatusBar();

			/* Create the status */
			theStatus = new statusCtl(this, theWindow.getProperties());

			/* Initialise the status window */
			theStatusBar.setOperation("Storing to Database");
			theStatusBar.setStage("", 0, 100); 
			theStatusBar.setSteps(0, 100);
			theStatusBar.getProgressPanel().setVisible(true);
		}

		/* Background task (Worker Thread)*/
		public Void doInBackground() {
			finStorage.Batch    myBatch  = null;

			try {
				/* Create batch interface */
				myBatch = new finStorage.Batch(theView.getData(), theStatus);

				/* Store to database */
				myBatch.applyChanges();
			}	

			/* Catch any exceptions */
			catch (finObject.Exception e) {
				theError = e;
				return null;
			}	

			/* Return the loaded data */
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
	
	public static class writeBackup extends SwingWorker <Void, ThreadStatus> 
	  								implements ThreadControl {
		/* Properties */
		private finView      		theView      	= null;
		private finSwing     		theWindow    	= null;
		private StatusBar    		theStatusBar 	= null;
		private statusCtl	 		theStatus    	= null;
		private finProperties		theProperties	= null;
		private finObject.Exception theError 	 	= null;

		/* Access methods */
		public finObject.Exception 	getError() 			{ return theError; }
		
		/* Constructor (Event Thread)*/
		public writeBackup(finView pView, finSwing pWindow) {
			/* Store passed parameters */
			theView       = pView;
			theWindow     = pWindow;
			theProperties = theWindow.getProperties();

			/* Access the Status Bar */
			theStatusBar = theWindow.getStatusBar();

			/* Create the status */
			theStatus = new statusCtl(this, theWindow.getProperties());
		}

		/* Background task (Worker Thread)*/
		public Void doInBackground() {
			finSpreadsheet.backupWriter myWriter  = null;
			finSpreadsheet.backupReader myReader  = null;
			finData						myData	  = null;
			finData						myDiff	  = null;
			boolean						doDelete  = false;
			String						myName;
			File						myFile;

			/* Build the file name */
			myName  = theProperties.getBackupDir();
			myName += File.separator;
			myName += theProperties.getBackupFile();
			if (theProperties.doEncryptBackups()) 
				myName += ".zip";
			myFile  = new File(myName);
			
			try {
				/* Create Writer interface */
				myWriter = new finSpreadsheet.backupWriter(theView.getData(), theStatus);

				/* Create Reader interface */
				myReader = new finSpreadsheet.backupReader(theStatus);

				/* If we are encrypted */
				if (theProperties.doEncryptBackups()) {
					/* Note the encryption */
					myWriter.setEncryption();
					myReader.setEncryption();
				}
				
				/* Initialise the status window */
				theStatusBar.setOperation("Writing Backup");
				theStatusBar.setStage("", 0, 100); 
				theStatusBar.setSteps(0, 100);
				theStatusBar.getProgressPanel().setVisible(true);
				
				/* Store the backup */
				myWriter.createBackup(myFile);

				/* File created, so delete on error */
				doDelete = true;
				
				/* Re-initialise the status window */
				theStatusBar.setOperation("Verifying Backup");
				theStatusBar.setStage("", 0, 100); 
				theStatusBar.setSteps(0, 100);
				theStatusBar.getProgressPanel().setVisible(true);
				
				/* Read back the newly created backup */
				myData = myReader.loadBackup(myFile);
				
				/* Create a difference set between the two data copies */
				myDiff = new finData(myData, theView.getData());
				
				/* If the difference set is non-empty */
				if (myDiff.hasMembers()) {
					/* Throw an exception */
					throw new finObject.Exception(ExceptionClass.DATA,
												  ObjectClass.DATASET,
												  myDiff,
												  "Backup is inconsistent");
				}
			}	

			/* Catch any exceptions */
			catch (finObject.Exception e) {
				/* Delete the file */
				if (doDelete) myFile.delete();
				
				/* Report the failure */
				theError = e;
				return null;
			}	

			/* Catch any exceptions */
			catch (Exception e) {
				/* Delete the file */
				if (doDelete) myFile.delete();
				
				/* Report the failure */
				theError = new finObject.Exception(ExceptionClass.DATA,
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
	
	public static class restoreBackup extends SwingWorker <finData, ThreadStatus> 
	  								  implements ThreadControl {
		/* Properties */
		private finView      		theView      	= null;
		private finSwing     		theWindow    	= null;
		private StatusBar    		theStatusBar 	= null;
		private statusCtl	 		theStatus    	= null;
		private finProperties		theProperties	= null;
		private finObject.Exception theError 	 	= null;

		/* Access methods */
		public finObject.Exception 	getError() 			{ return theError; }
		
		/* Constructor (Event Thread)*/
		public restoreBackup(finView pView, finSwing pWindow) {
			/* Store passed parameters */
			theView   		= pView;
			theWindow 		= pWindow;
			theProperties 	= theWindow.getProperties();

			/* Access the Status Bar */
			theStatusBar = theWindow.getStatusBar();

			/* Create the status */
			theStatus = new statusCtl(this, theWindow.getProperties());
		}

		/* Background task (Worker Thread)*/
		public finData doInBackground() {
			finSpreadsheet.backupReader myReader  = null;
			finData						myData	  = null;
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
				
				/* Create Reader interface */
				myReader = new finSpreadsheet.backupReader(theStatus);

				/* If we are encrypted */
				if (theProperties.doEncryptBackups()) {
					/* Note the encryption */
					myReader.setEncryption();
				}
				
				/* Initialise the status window */
				theStatusBar.setOperation("Loading Backup");
				theStatusBar.setStage("", 0, 100); 
				theStatusBar.setSteps(0, 100);
				theStatusBar.getProgressPanel().setVisible(true);
				
				/* Read back the backup */
				myData = myReader.loadBackup(myFile);
				
				/* Re-base this set on the existing DataSet */
				myData.reBase(theView.getData());
			}	

			/* Catch any exceptions */
			catch (finObject.Exception e) {
				/* Report the failure */
				theError = e;
				return null;
			}	

			/* Catch any exceptions */
			catch (Exception e) {
				/* Report the failure */
				theError = new finObject.Exception(ExceptionClass.DATA,
												   "Failed to rebase backup",
												   e);
				return null;
			}	

			/* Return the new Data */
			return myData;
		}

		/* Completion task (Event Thread)*/
		public void done() {
			finData myData;
			String  myOp = "Backup restoration";

			try {
				/* If we are not cancelled */
				if (!isCancelled()) {
					/* Get the newly loaded data */
					myData = get();
				
					/* If we have new data */
					if (myData != null) {
						/* Activate the data and set success */
						theView.setData(myData);
						theStatusBar.setSuccess(myOp);
					}
					
					/* Else report the failure */
					else theStatusBar.setFailure(myOp, theError);
				}
				
				/* Report the cancellation */
				else theStatusBar.setFailure(myOp, theError);
			} 
			catch (Exception e) {
				theError = new finObject.Exception(ExceptionClass.DATA,
												   "Failure while refreshing data",
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
}

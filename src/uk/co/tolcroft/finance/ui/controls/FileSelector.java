package uk.co.tolcroft.finance.ui.controls;

import java.io.File;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.ui.*;

public class FileSelector {
	/**
	 *  The ArchiveLoad Selector class
	 */
	public static class ArchiveLoad {
		/* Members */
		private JFrame			theFrame		= null;
		private JFileChooser	theChooser		= null;	
		private File			theResult		= null;

		/**
		 * Obtain the selected file 
		 */
		public File getSelectedFile() { return theResult; }
		
		/**
		 * Constructor
		 */
		public ArchiveLoad(MainTab pWindow) {
			/* Access the properties */
			Properties myProperties = pWindow.getProperties();
		
			/* Access the frame */
			theFrame = pWindow.getFrame();
		
			/* Create the chooser */
			theChooser = new JFileChooser();
		
			/* Initialise it to the selected file */
			theChooser.setSelectedFile(new File(myProperties.getBaseSpreadSheet()));
		
			/* Set the file filter */
			theChooser.setFileFilter(new fileFilter());
		
			/* Set the title */
			theChooser.setDialogTitle("Select archive spreadsheet");
		}
	
		/**
		 *  Show the dialog to select a file using an invokeAndWait clause if necessary 
		 */
		public void selectFile() {
			
			/* If this is the event dispatcher thread */
			if (SwingUtilities.isEventDispatchThread()) {
				/* invoke the dialog directly */
				showDialog();			
			}
			
			/* else we must use invokeAndWait */
			else {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							/* invoke the dialog */
							showDialog();
						}
					});
				}
			
				catch (Throwable e) {}
			}
		}
		
		/**
		 * Show the dialog to select the file 
		 */
		private void showDialog() {
			/* Show the dialog and select the file */
			int iRet = theChooser.showOpenDialog(theFrame);
		
			/* If we selected a file */
			if (iRet == JFileChooser.APPROVE_OPTION)
				theResult = theChooser.getSelectedFile();
		
			/* else set no selection */
			else theResult = null;
		}
	
		/**
		 * FileFilter class
		 */
		private class fileFilter extends FileFilter {
			/**
			 * Accept file 
			 * @param pFile the file to check
			 * @return true/false
			 */
			public boolean accept(File pFile) {
				/* Always accept directories */
				if (pFile.isDirectory()) return true;
			
				/* The filename must end with .xls or .xls.zip */
				String myName = pFile.getName();
				if (myName.endsWith(".xls"))
					return true;
			
				/* reject the file */
				return false;
			}
		
			/**
			 * Description
			 * @returns the description of the filter
			 */
			public String getDescription() {
				return "Finance backup files";
			}
		}
	}

	/**
	 *  The BackupLoad Selector class
	 */
	public static class BackupLoad {
		/* Members */
		private JFrame			theFrame		= null;
		private JFileChooser	theChooser		= null;
		private String			thePrefix		= null;	
		private File			theResult		= null;

		/**
		 * Obtain the selected file 
		 */
		public File getSelectedFile() { return theResult; }
		
		/**
		 * Constructor
		 */
		public BackupLoad(MainTab pWindow) {
			/* Access the properties */
			Properties myProperties = pWindow.getProperties();
		
			/* Access the frame */
			theFrame = pWindow.getFrame();
		
			/* Create the chooser */
			theChooser = new JFileChooser();
		
			/* Initialise it to the required backup directory */
			theChooser.setCurrentDirectory(new File(myProperties.getBackupDir()));
		
			/* Store the prefix value */
			thePrefix  = myProperties.getBackupPrefix();

			/* Set the file filter */
			theChooser.setFileFilter(new fileFilter());
		
			/* Set the title */
			theChooser.setDialogTitle("Select backup file to load");
		}
	
		/**
		 *  Show the dialog to select a file using an invokeAndWait clause if necessary 
		 */
		public void selectFile() {
			
			/* If this is the event dispatcher thread */
			if (SwingUtilities.isEventDispatchThread()) {
				/* invoke the dialog directly */
				showDialog();			
			}
			
			/* else we must use invokeAndWait */
			else {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							/* invoke the dialog */
							showDialog();
						}
					});
				}
			
				catch (Throwable e) {}
			}
		}
		
		/**
		 * Show the dialog to select the file 
		 */
		private void showDialog() {
			/* Show the dialog and select the file */
			int iRet = theChooser.showOpenDialog(theFrame);
		
			/* If we selected a file */
			if (iRet == JFileChooser.APPROVE_OPTION)
				theResult = theChooser.getSelectedFile();
		
			/* else set no selection */
			else theResult = null;
		}
	
		/**
		 * FileFilter class
		 */
		private class fileFilter extends FileFilter {
			/**
			 * Accept file 
			 * @param pFile the file to check
			 * @return true/false
			 */
			public boolean accept(File pFile) {
				/* Always accept directories */
				if (pFile.isDirectory()) return true;
			
				/* The file must start with the prefix */
				String myName = pFile.getName();
				if (!myName.startsWith(thePrefix)) return false;
			
				/* It must end with .xls or .xls.zip */
				if (myName.endsWith(".xls") || myName.endsWith(".xls.zip"))
					return true;
			
				/* reject the file */
				return false;
			}
		
			/**
			 * Description
			 * @returns the description of the filter
			 */
			public String getDescription() {
				return "Finance backup files";
			}
		}
	}

	/**
	 *  The BackupCreate Selector class
	 */
	public static class BackupCreate {
		/* Members */
		private JFrame			theFrame		= null;
		private JFileChooser	theChooser		= null;
		private String			thePrefix		= null;	
		private File			theResult		= null;

		/**
		 * Obtain the selected file 
		 */
		public File getSelectedFile() { return theResult; }
		
		/**
		 * Constructor
		 */
		public BackupCreate(MainTab pWindow) {
			/* Access the properties */
			Properties myProperties = pWindow.getProperties();
		
			/* Access the frame */
			theFrame = pWindow.getFrame();
		
			/* Create the chooser */
			theChooser = new JFileChooser();
		
			/* Store the prefix value */
			thePrefix  = myProperties.getBackupPrefix();

			/* Note the the chooser can only select a directory */
			theChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			/* Initialise it to the required backup directory */
			theChooser.setSelectedFile(new File(myProperties.getBackupDir()));
		
			/* Set the title */
			theChooser.setDialogTitle("Select directory for backup file");
		}
	
		/**
		 *  Show the dialog to select a file using an invokeAndWait clause if necessary 
		 */
		public void selectFile() {
			
			/* If this is the event dispatcher thread */
			if (SwingUtilities.isEventDispatchThread()) {
				/* invoke the dialog directly */
				showDialog();			
			}
			
			/* else we must use invokeAndWait */
			else {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							/* invoke the dialog */
							showDialog();
						}
					});
				}
			
				catch (Throwable e) {}
			}
		}
		
		/**
		 * Show the dialog to select the file 
		 */
		private void showDialog() {
			String 		myName;
			Calendar	myNow;
			int		 	myValue;
			
			/* Show the dialog and select the file */
			int iRet = theChooser.showSaveDialog(theFrame);
		
			/* If we selected a file */
			if (iRet == JFileChooser.APPROVE_OPTION) {
				/* Access the selected directory */
				theResult = theChooser.getSelectedFile();
				
				/* Obtain the current date/time */
				myNow = Calendar.getInstance();
				
				/* Create the name of the file */
				myName 	 = new String(thePrefix);
				myName 	+= myNow.get(Calendar.YEAR);
				myValue  = myNow.get(Calendar.MONTH) + 1;
				if (myValue < 10) myName += "0";
				myName += myValue;
				myValue  = myNow.get(Calendar.DAY_OF_MONTH);
				if (myValue < 10) myName += "0";
				myName += myValue;
				
				/* Add extension */
				myName += ".xls";
				
				/* Build new file name */
				theResult = new File(theResult, myName);
			}
		
			/* else set no selection */
			else theResult = null;
		}
	}
	
	/**
	 *  The BackupDirectory Selector class
	 */
	public static class BackupDirectory {
		/* Members */
		private JFrame			theFrame		= null;
		private JFileChooser	theChooser		= null;
		private File			theResult		= null;

		/**
		 * Obtain the selected file 
		 */
		public File getSelectedFile() { return theResult; }
		
		/**
		 * Constructor
		 */
		public BackupDirectory(MainTab pWindow) {
			/* Access the properties */
			Properties myProperties = pWindow.getProperties();
		
			/* Access the frame */
			theFrame = pWindow.getFrame();
		
			/* Create the chooser */
			theChooser = new JFileChooser();
		
			/* Note the the chooser can only select a directory */
			theChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			/* Initialise it to the required backup directory */
			theChooser.setSelectedFile(new File(myProperties.getBackupDir()));
		
			/* Set the title */
			theChooser.setDialogTitle("Select directory for backup file");
		}
	
		/**
		 *  Show the dialog to select a file using an invokeAndWait clause if necessary 
		 */
		public void selectFile() {
			
			/* If this is the event dispatcher thread */
			if (SwingUtilities.isEventDispatchThread()) {
				/* invoke the dialog directly */
				showDialog();			
			}
			
			/* else we must use invokeAndWait */
			else {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							/* invoke the dialog */
							showDialog();
						}
					});
				}
			
				catch (Throwable e) {}
			}
		}
		
		/**
		 * Show the dialog to select the file 
		 */
		private void showDialog() {
			/* Show the dialog and select the file */
			int iRet = theChooser.showSaveDialog(theFrame);
		
			/* If we selected a file */
			if (iRet == JFileChooser.APPROVE_OPTION) {
				/* Access the selected directory */
				theResult = theChooser.getSelectedFile();
			}
		
			/* else set no selection */
			else theResult = null;
		}
	}
}

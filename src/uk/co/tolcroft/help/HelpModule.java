package uk.co.tolcroft.help;

import java.io.*;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

/* The help module that is implemented by each Help System */
public abstract class HelpModule {
	/**
	 * The list of Help pages
	 */
	private HelpPage.List theList = null;
	
	/* Access methods */
	public HelpPage.List getHelpPages() { return theList; }
	
	/**
	 * Constructor 
	 */
	public HelpModule() throws Exception {
		/* Allocate the list */
		theList = new HelpPage.List();
		
		/* Access the help entities */
		HelpEntry[] myEntries = getHelpEntries();
		
		/* Loop through the entities */
		loadEntries(myEntries);
	}
	
	/**
	 * Constructor 
	 */
	public void loadEntries(HelpEntry[] pEntries) throws Exception {
		InputStream myStream;
		
		/* Loop through the entities */
		for (HelpEntry myEntry : pEntries) {
			/* Check that the entry is not already in the list */
			if (theList.searchFor(myEntry.getName()) != null)
				throw new Exception(ExceptionClass.DATA,
						            "Duplicate Help object Name: " + myEntry.getName());

			/* If we have a file name */
			if (myEntry.getFileName() != null) {
				/* Access the input stream for the entity */
				myStream = this.getClass().getResourceAsStream(myEntry.getFileName());
			
				/* Add it to the list */
				theList.addItem(myEntry, myStream);
			
				/* Close the stream */
				try { myStream.close(); } catch (Throwable e) {}
			}
			
			/* If we have children */
			if (myEntry.getChildren() != null) {
				/* Load the entries */
				loadEntries(myEntry.getChildren());
			}
		}
	}
	
	/**
	 * Access the help entities for this module
	 */
	public abstract HelpEntry[] getHelpEntries();
	
	/**
	 * Identify the initial entry
	 */
	public abstract String getInitialName();
	
	/**
	 * Identify the help set
	 */
	public abstract String getTitle();
}

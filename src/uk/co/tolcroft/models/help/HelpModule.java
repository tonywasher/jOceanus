package uk.co.tolcroft.models.help;

import java.io.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

/* The help module that is implemented by each Help System */
public abstract class HelpModule {
	/**
	 * Document name for Help Contents
	 */
	protected static final String documentHelp 	= "HelpContents";
	
	/**
	 * Attribute name for Initial page
	 */
	protected static final String attrInitial 	= "initial";
	
	/**
	 * The list of Help pages
	 */
	private HelpPage.List 	theList 	= null;
	
	/**
	 * The Help Entries
	 */
	private HelpEntry[] 	theEntries 	= null;
	
	/**
	 * The title of the Help System 
	 */
	private String			theTitle	= "Help System";

	/**
	 * The initial entry of the help system 
	 */
	private String			theInitial	= null;
	
	/* Access methods */
	public HelpPage.List 	getHelpPages() 		{ return theList; }
	public String 			getInitialName()	{ return theInitial; }
	public String			getTitle()			{ return theTitle; }
	public HelpEntry[] 		getHelpEntries()	{ return theEntries; }
	
	/**
	 * Constructor 
	 * @param pDefinitions the definitions file name
	 */
	public HelpModule(String pDefinitions) throws Exception {
		/* Allocate the list */
		theList = new HelpPage.List();
		
		/* Parse the help definitions */
		parseHelpDefinition(pDefinitions);
		
		/* Loop through the entities */
		loadHelpPages(theEntries);
	}
	
	/**
	 * Parse Help Definition
	 * @param pFile the xml file containing the definitions 
	 */
	private void parseHelpDefinition(String pFile) throws Exception {
		InputStream 			myStream;
		DocumentBuilderFactory	myFactory;
		DocumentBuilder 		myBuilder;
		Document				myDoc;
		Element					myElement;
		
		/* Protect against exceptions */ 
		try {
			/* Access the input stream for the entity */
			myStream = this.getClass().getResourceAsStream(pFile);
		
			/* Create the document builder */
			myFactory = DocumentBuilderFactory.newInstance();
			myBuilder = myFactory.newDocumentBuilder();
			
			/* Access the XML document element */
			myDoc 		= myBuilder.parse(myStream);
			myElement	= myDoc.getDocumentElement();
			
			/* Reject if this is not a Help Definitions file */
			if (!myElement.getNodeName().equals(documentHelp))
				throw new Exception(ExceptionClass.DATA,
									"Invalid document name: " + myElement.getNodeName());
			
			/* Set title of document */
			if (myElement.getAttribute(HelpEntry.attrTitle) != null)
				theTitle = myElement.getAttribute(HelpEntry.attrTitle);
			
			/* Access the entries */
			theEntries = HelpEntry.getHelpEntryArray(myElement);
			
			/* Default the initial entry */
			theInitial = theEntries[0].getName();
			
			/* Set initial element */
			if (myElement.getAttribute(attrInitial) != null)
				theInitial = myElement.getAttribute(attrInitial);
			
			/* Close the stream */
			try { myStream.close(); } catch (Throwable e) {}
		}
		
		/* Cascade exceptions */
		catch (Exception e) { throw e; }
		
		/* Catch exceptions */
		catch (Throwable e) {
			/* Throw Exception */
			throw new Exception(ExceptionClass.DATA,
		            			"Failed to load XML Help Definitions",
		            			e);
		}
	}
	
	/**
	 * Load Help entries from the file system
	 * @param pEntries the Help Entries
	 */
	private void loadHelpPages(HelpEntry[] pEntries) throws Exception {
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
				loadHelpPages(myEntry.getChildren());
			}
		}
	}
}
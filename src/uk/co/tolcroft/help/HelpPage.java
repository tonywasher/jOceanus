package uk.co.tolcroft.help;

import java.io.*;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

/**
 *  Help Page class. This class maps between the name of a help page and the html that the name represents.
 */
public class HelpPage extends DataItem {
	/* Members */
	private String		theName		= null;
	private String		theHtml		= null;
	private HelpEntry	theEntry	= null;
	
	/**
	 * The item type
	 */
	private final static String itemType	= "HelpPage";
	
	/* Access methods */
	public	String		getName()	{ return theName; }
	public 	String		getHtml()	{ return theHtml; }
	public 	HelpEntry	getEntry()	{ return theEntry; }

	/**
	 * Constructor
	 * @param pList the list to which this help page belongs
	 * @param pEntry the help entry for the help page
	 * @param pStream the stream to read the help page from 
	 */
	public HelpPage(List pList, HelpEntry pEntry, InputStream pStream) throws Exception {
		/* Call the super-constructor */
		super(pList, 0);
		
		/* Local variables */
		BufferedReader 		myReader;
		InputStreamReader 	myInputReader;
		String				myLine;
		
		/* Allocate a buffered reader on top of the input stream */
		myInputReader 	= new InputStreamReader(pStream);
		myReader 		= new BufferedReader(myInputReader);
		
		/* Allocate a string builder */
		StringBuilder myBuilder = new StringBuilder(10000);

		/* Protect against exceptions */
		try {
			/* Read the header entry */
			while ((myLine = myReader.readLine()) != null) {
				/* Add to the string buffer */
				myBuilder.append(myLine);
				myBuilder.append('\n');
			}
		}
		
		/* Catch exceptions */
		catch (Throwable e) {
			/* Throw an exception */
			throw new Exception(ExceptionClass.DATA,
								"Failed to load help file " + pEntry.getName(),
								e);
		}
		
		/* Build the values */
		theName = pEntry.getName();
		theHtml = myBuilder.toString();
		
		/* Link the entry to us */
		pEntry.setHelpPage(this);
		theEntry = pEntry;
	}

	/**
	 * Return the item type
	 * @return item type
	 */
	public String itemType() {	return itemType; }

	/* Field IDs */
	public static final int FIELD_NAME  	= 0;
	public static final int FIELD_TEXT	    = 1;
	public static final int NUMFIELDS	    = 2;
	
	/**
	 * Obtain the number of fields for an item
	 * @return the number of fields
	 */
	public int	numFields() {return NUMFIELDS; }
	
	/**
	 * Determine the field name for a particular field
	 * @return the field name
	 */
	public String	fieldName(int iField) {
		switch (iField) {
			case FIELD_NAME:		return "Name";
			case FIELD_TEXT:		return "Text";
			default:		  		return super.fieldName(iField);
		}
	}
	
	/**
	 * Format the value of a particular field as a table row
	 * @param iField the field number
	 * @param pObj the values to use
	 * @return the formatted field
	 */
	public String formatField(int iField, histObject pObj) {
		String myString = ""; 
		switch (iField) {
			case FIELD_NAME: 	
				myString += theName;
				break;
			case FIELD_TEXT: 	
				myString += theHtml;
				break;
		}
		return myString;
	}

	/**
	 * Compare this Help Page to another to establish equality.
	 * 
	 * @param pThat The Page to compare to
	 * @return <code>true</code> if the bucket is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a HelpPage */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a Help Page */
		HelpPage myPage = (HelpPage)pThat;
		
		/* Check for equality */
		if (Utils.differs(getName(),    myPage.getName())) 		return false;
		if (Utils.differs(getHtml(),	myPage.getHtml())) 		return false;
		return true;
	}

	/**
	 * Compare this HelpPage to another to establish sort order.
	 * 
	 * @param pThat The HelpPage to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		int result;

		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a Help Page */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as a HelpPage */
		HelpPage myThat = (HelpPage)pThat;
		
		/* Compare the name */
		result = theName.compareTo(myThat.theName);
		if (result == 0) return 0;
		else if (result < 0) return -1;
		else return 1;
	}
	
	/* List class */
	public static class List extends DataList<HelpPage> {
		/**
		 * Construct a top-level List
		 */
		public List() { super(ListStyle.VIEW, false); }

		/**
		 * Return the item type
		 * @return item type
		 */
		public String itemType() {	return itemType; }

		/** 
	 	 * Clone a HelpPage list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return null; }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem the item to add
		 * @return the newly added item
		 */
		public DataItem addNewItem(DataItem pItem) { return null; }
	
		/**
		 * Add a new item to the edit list
		 * 
		 * @param isCredit - ignored
		 */
		public void addNewItem(boolean isCredit) { return; }
		
		/**
		 * Add a help page to the list
		 * @param pEntry the help entry of the help page
		 * @param pStream the stream to read the help page from 
		 */
		public void addItem(HelpEntry pEntry, InputStream pStream) throws Exception {
			HelpPage myPage;
			
			/* Build the help page */
			myPage = new HelpPage(this, pEntry, pStream);
			
			/* Add it to the list */
			myPage.addToList();
		}

		/**
		 * Search for a help page in the list
		 * @param pName the name of the help page
		 * @return the help page 
		 */
		public HelpPage searchFor(String pName) {
			ListIterator	myIterator;
			HelpPage 		myPage;
			
			/* Create an iterator */
			myIterator = listIterator();
			
			/* Loop through the entries */
			while ((myPage = myIterator.next()) != null) {
				/* If we have found the page break loop */
				if (pName.equals(myPage.getName())) break;
			}
			
			/* Return the page */
			return myPage;
		}
	}
}

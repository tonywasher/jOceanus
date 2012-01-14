/*******************************************************************************
 * Copyright 2012 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package uk.co.tolcroft.models.help;

import java.io.*;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.ModelException.ExceptionClass;

/**
 *  Help Page class. This class maps between the name of a help page and the html that the name represents.
 */
public class HelpPage extends SortedItem<HelpPage> {
	/* Members */
	private String		theName		= null;
	private String		theHtml		= null;
	private HelpEntry	theEntry	= null;
	
	/* Access methods */
	public	String		getName()	{ return theName; }
	public 	String		getHtml()	{ return theHtml; }
	public 	HelpEntry	getEntry()	{ return theEntry; }

	/**
	 * Build History (no history)
	 */
	protected void buildHistory() {}

	/**
	 * Constructor
	 * @param pList the list to which this help page belongs
	 * @param pEntry the help entry for the help page
	 * @param pStream the stream to read the help page from 
	 */
	public HelpPage(List pList, HelpEntry pEntry, InputStream pStream) throws ModelException {
		/* Call the super-constructor */
		super(pList);
		
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
			throw new ModelException(ExceptionClass.DATA,
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

	@Override
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a HelpPage */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a Help Page */
		HelpPage myPage = (HelpPage)pThat;
		
		/* Check for equality */
		if (Utils.differs(getName(),    myPage.getName()).isDifferent())	return false;
		if (Utils.differs(getHtml(),	myPage.getHtml()).isDifferent()) 	return false;
		return true;
	}

	@Override
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
	public static class List extends SortedList<HelpPage> {
		/**
		 * Construct a top-level List
		 */
		public List() { super(HelpPage.class); }

		/**
		 * Add a help page to the list
		 * @param pEntry the help entry of the help page
		 * @param pStream the stream to read the help page from 
		 */
		public void addItem(HelpEntry pEntry, InputStream pStream) throws ModelException {
			HelpPage myPage;
			
			/* Build the help page */
			myPage = new HelpPage(this, pEntry, pStream);
			
			/* Add it to the list */
			add(myPage);
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

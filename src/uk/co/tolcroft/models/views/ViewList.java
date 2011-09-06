package uk.co.tolcroft.models.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import uk.co.tolcroft.models.data.DataList;

public class ViewList {
	/* Members */
	private List<ListClass>	theList		= null;
	private DataControl<?>	theControl	= null;

	/**
	 * Constructor for multiple DataLists
	 * @param pControl the Data Control
	 */
	public ViewList(DataControl<?> pControl) {
		/* Store the Control */
		theControl = pControl;
		
		/* Create the list */
		theList = new ArrayList<ListClass>();
	}
	
	/**
	 * Register an entry for a class
	 * @param pClass the class
	 */
	public ListClass registerClass(Class<?> pClass) {
		ListIterator<ListClass>	myIterator;
		ListClass				myList;
		
		/* Loop through the items in the list */
		myIterator = theList.listIterator();
		while (myIterator.hasNext()) {
			/* Access list */
			myList = myIterator.next();
			
			/* If we have found the class */
			if (myList.theClass == pClass) {
				/* Update list to null and return */
				myList.theDataList = null;
				return myList;
			}
		}
		
		/* Not found , so add it */
		myList = new ListClass(pClass);
		theList.add(myList);
		return myList;
	}
	
	/** 
	 * Apply changes in a ViewSet into the core data
	 */
	public void applyChanges() {
		/* Apply the changes */
		prepareChanges();
			
		/* analyse the data */
		boolean bSuccess = theControl.analyseData(false); 
		
		/* If we were successful */
		if (bSuccess) {
			/* Commit the changes */
			commitChanges();

			/* Refresh windows */
			theControl.refreshWindow();
		}

		/* else we failed */
		else {
			/* Rollback the changes */ 
			rollBackChanges(); 
			
			/* Re-analyse the data */
			theControl.analyseData(true);
		}
	}
	
	/** 
	 * Prepare changes in a ViewSet back into the core data
	 */
	private void prepareChanges() {
		Iterator<ListClass>	myIterator;
		ListClass			myList;
		DataList<?>			myDataList;
		
		/* Loop through the items in the list */
		myIterator = theList.iterator();
		while (myIterator.hasNext()) {
			/* Access list */
			myList 		= myIterator.next();
			myDataList 	= myList.theDataList;
			
			/* Prepare the changes */
			if (myDataList != null)
				myDataList.prepareChanges();
		}
	}

	/** 
	 * Commit changes in a ViewSet back into the core data
	 */
	private void commitChanges() {
		Iterator<ListClass>	myIterator;
		ListClass			myList;
		DataList<?>			myDataList;
		
		/* Loop through the items in the list */
		myIterator = theList.iterator();
		while (myIterator.hasNext()) {
			/* Access list */
			myList 		= myIterator.next();
			myDataList 	= myList.theDataList;
			
			/* commit the changes */
			if (myDataList != null)
				myDataList.commitChanges();
		}
	}

	/** 
	 * Rollback changes in a ViewSet back into the core data
	 */
	private void rollBackChanges() {
		ListIterator<ListClass>	myIterator;
		ListClass				myList;
		DataList<?>				myDataList;
		
		/* Loop backwards through the items in the list */
		myIterator = theList.listIterator(theList.size());
		while (myIterator.hasPrevious()) {
			/* Access list */
			myList 		= myIterator.previous();
			myDataList 	= myList.theDataList;
			
			/* commit the changes */
			if (myDataList != null)
				myDataList.rollBackChanges();
		}
	}
	
	/**
	 * DataList items 
	 */
	public class ListClass {
		/* properties */
		private Class<?>	theClass 	= null;
		private DataList<?>	theDataList	= null;
		
		/**
		 * Set the Data list 
		 * @param pDataList the DataList
		 */
		public void setDataList(DataList<?> pDataList) {
			theDataList = pDataList;
		}
		
		/**
		 * Constructor
		 * @param pClass the class
		 */
		private ListClass(Class<?> pClass) {
			/* Store details */
			theClass 	= pClass;
		}
	}
}

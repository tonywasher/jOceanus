package uk.co.tolcroft.finance.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.tolcroft.models.DataItem;
import uk.co.tolcroft.models.DataList;
import uk.co.tolcroft.models.DataState;

public class BatchControl {
	/**
	 * Default batch size for updates
	 */
	protected final static int    	BATCH_SIZE 		= 50;

	/**
	 * Capacity of Batch Control (0=Unlimited)
	 */
	private int 				theCapacity		= BATCH_SIZE; 
	
	/**
	 * Number of items in this batch
	 */
	private int					theItems		= 0;
	
	/**
	 * The List of tables associated with this batch
	 */
	private List<BatchTable>	theList			= null;
	
	/**
	 * The Currently active Database table
	 */
	private DatabaseTable<?>	theCurrTable	= null;
	
	/**
	 * The Currently active Mode
	 */
	private DataState			theCurrMode		= null;
	
	/**
	 * Is the current table in use
	 */
	private boolean				isTableActive	= false;
	
	/**
	 * Constructor
	 */
	protected BatchControl() {
		/* Create the batch table list */
		theList = new ArrayList<BatchTable>();
	}
	
	/**
	 * Is the batch full
	 * @return true/false is the batch full
	 */
	protected boolean isFull() {
		return (theCapacity != 0) && (theItems >= theCapacity);
	}
	
	/**
	 * Is the batch active
	 * @return true/false is the batch active
	 */
	protected boolean isActive() {
		return (theItems >= 0);
	}
	
	/**
	 * Set the currently active state
	 * @param pTable the Table being operated on
	 * @param pMode  the Mode that is in operation
	 */
	protected void setCurrentTable(DatabaseTable<?> pTable, DataState	pMode) {
		/* Store details */
		theCurrTable	= pTable;
		theCurrMode 	= pMode;
		isTableActive	= false;
	}
	
	/**
	 * Add item to the batch
	 */
	protected void addBatchItem() {
		/* Increment batch count */
		theItems++;
		
		/* If the current table is not active */
		if (!isTableActive) {
			/* Create the batch entry */
			BatchTable myTable = new BatchTable();
			
			/* Add to the batch list */
			theList.add(myTable);
			isTableActive = true;
		}
	}
	
	/**
	 * Commit the batch
	 */
	protected void commitItems() {
		/* Access iterator for the list */
		Iterator<BatchTable> myIterator = theList.iterator();
		
		/* Loop through the items */
		while (myIterator.hasNext()) {
			/* Access the next entry */
			BatchTable myTable = myIterator.next();
			
			/* Commit batch items in the table */
			myTable.commitBatch();
		}
		
		/* Clear the list */
		theList.clear();
		isTableActive 	= false;
		theItems		= 0;
	}
	
	/**
	 * Table step
	 */
	private class BatchTable {
		/**
		 * The table that is being controlled
		 */
		private DatabaseTable<?>	theTable	= null;
		
		/**
		 * The State of the table
		 */
		private DataState			theState	= null;
		
		/**
		 * Constructor
		 */
		private BatchTable() {
			/* Store the details */
			theTable = theCurrTable;
			theState = theCurrMode;
		}
		
		/**
		 * Mark a update in the table as committed as committed
		 * @param pState the state of the items to update
		 */
		private void commitBatch() {
			DataList<?>.ListIterator	myIterator;
			DataItem<?>					myCurr;
			DataItem<?>					myBase;
			
			/* Access the iterator */
			myIterator = theTable.getList().listIterator(true);
				
			/* Loop through the list */
			while ((myCurr = myIterator.next()) != null) {
				/* Ignore items that are not this type */
				if (myCurr.getState() != theState) continue;
					
				/* Access the underlying element */
				myBase = myCurr.getBase();
					
				/* If we are handling deletions */
				if (theState == DataState.DELETED) {
					/* Simply unlink the underlying item */
					myBase.unLink();
				}
					
				/* else we are handling new/changed items */
				else { 
					/* Set the item to clean and clear history */
					myBase.setState(DataState.CLEAN);
					myBase.clearHistory();
				}

				/* Mark this item as clean */
				myCurr.setState(DataState.CLEAN);

				/* If we have to worry about batch space */
				if (theCapacity > 0) {
					/* Adjust batch and break if we are finished */
					if (--theItems == 0) break;
				}
			}	
		}
	}
}

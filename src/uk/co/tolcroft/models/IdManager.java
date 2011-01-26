package uk.co.tolcroft.models;

public class IdManager<T extends DataItem> {
	/**
	 * The maximum id
	 */
	private long 	theMaxId 	= 0;
	
	/**
	 * The id map
	 */
	private idMap	theMap		= new idMap();
	
	/**
	 * Is the Id unique in this list
	 * @param uId the Id to check
	 * @return Whether the id is unique <code>true/false</code>
	 */
	protected boolean isIdUnique(long uId) {
		/* Its unique if its unassigned or greater than the max id */
		if ((uId == 0) || (uId > theMaxId)) return true;
		
		/* Locate the id if its possible that we have it */
		T myItem = getItem(uId);
		
		/* Return result */
		return (myItem == null);
	}
	
	/**
	 * Generate/Record new id
	 * @param pRate the new rate
	 */
	protected void setNewId(T pItem) {
		long myId = pItem.getId();
		
		/* If we need to generate a new id */
		if (myId == 0) {
			/* Increment and use the max Id */
			theMaxId++;
			pItem.setId(theMaxId);
			pItem.setState(DataState.NEW);
		}
		
		/* else id is already known */
		else  {
			/* Update the max Id if required */
			if (theMaxId < myId) theMaxId = myId;
			pItem.setState(DataState.CLEAN);
		}
	}
	
	/**
	 * Locate the item by id
	 * @param pId the id of the item to retrieve 
	 * @return the item for the id (or <code>null</code>) 
	 */
	public T getItem(long pId) {
		/* Locate the item in the map */
		return theMap.getItem(pId);
	}
	
	/**
	 * Store the item by id
	 * @param pId the id of the item to retrieve 
	 * @param pItem the item to store (or <code>null</code>) 
	 */
	public void setItem(long pId, T pItem) {
		/* Store the item in the map */
		theMap.setItem(pId, pItem);
	}
	
	/**
	 * remove All list items
	 */
	public void clear() {
		/* Reinitialise the map */
		theMap = new idMap();
	}
	
	/**
	 * Id map to element 
	 */
	private class idMap {
		/**
		 * The size of a single map element
		 */
		private final static int 	maxElements = 10;

		/**
		 * The depth of this map
		 */
		private int 				theDepth	= 0;
		
		/**
		 * The map indexed by id
		 */
		private Object[] 			theArray	= null;
		
		/**
		 * constructor for map
		 */
		private void newArray() {
			theArray = new Object[maxElements];
			java.util.Arrays.fill(theArray, null);
		}
		
		/**
		 * Locate the item by id
		 * @param pId the id of the item to retrieve 
		 * @return the item for the id (or <code>null</code>) 
		 */
		@SuppressWarnings("unchecked")
		private T getItem(long pId) {
			long 	myIndex  = pId;
			int 	myAdjust = 1;
			idMap	myMap;

			/* If we are not the final map pass the call on */
			if (theDepth > 0) {
				/* Loop to find the index in this map */
				for (int i  = theDepth; i > 0; i--)  { 
					myIndex		/= maxElements;
					myAdjust 	*= maxElements;
				}
			
				/* If we are beyond the scope of this map return null */
				if (myIndex >= maxElements) return null;

				/* Access the relevant map */
				myMap = (idMap)theArray[(int)myIndex];

				/* If the map is empty return null */
				if (myMap == null) return null;
				
				/* Adjust the index */
				pId -= (myIndex * myAdjust);
				
				/* Pass the call on */
				return myMap.getItem(pId);
			}
			
			/* If we are beyond the scope of this map return null */
			if (myIndex >= maxElements) return null;

			/* Return null if we have no array */
			if (theArray == null) return null;
			
			/* Calculate final index and return item */
			myIndex = pId % maxElements;
			return (T)theArray[(int)myIndex];
		}

		/**
		 * Store the item by id
		 * @param pId the id of the item to retrieve 
		 * @param pItem the item to store (or <code>null</code>) 
		 */
		@SuppressWarnings("unchecked")
		private void setItem(long pId, T pItem) {
			long 	myIndex 	= pId;
			long 	myAdjust	= 1;
			idMap	myMap		= null;
			
			/* Loop to find the index in this map */
			for (int i  = theDepth; i > 0; i--)  { 
				myIndex		/= maxElements;
				myAdjust 	*= maxElements;
			}
		
			/* If we are beyond the scope of this map */
			if (myIndex >= maxElements) {
				/* If this map is non-empty */
				if (theArray != null) {
					/* Create a sub-map based on this one */
					myMap = new idMap();
					myMap.theDepth = theDepth;
					myMap.theArray = theArray;
				}
				
				/* Increase the depth of this map */
				theDepth++;

				/* Create a new array for this map */
				newArray();
				
				/* Store the map */
				theArray[0] = myMap;

				/* Try with the adjusted map depth */
				setItem(pId, pItem);
				return;
			}

			/* If we are not the final map */
			if (theDepth > 0) {
				/* Access the relevant map */
				myMap = (idMap)theArray[(int)myIndex];

				/* If the map is empty */
				if (myMap == null) {
					/* Create and store a new id map */
					myMap = new idMap();
					myMap.newArray();
					myMap.theDepth = theDepth-1;
					theArray[(int)myIndex] = myMap;
				}
				
				/* Adjust the index */
				pId -= (myIndex * myAdjust);
				
				/* Pass the call on with adjusted id */
				myMap.setItem(pId, pItem);
				return;
			}

			/* Allocate the array if needed */
			if (theArray == null) newArray();
			
			/* Store the item */
			myIndex = pId % maxElements;
			theArray[(int)myIndex] = pItem;
		}
	}
}

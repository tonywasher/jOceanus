package uk.co.tolcroft.models.data;


public class IdManager<T extends DataItem<T>> {
	/**
	 * The maximum id
	 */
	private int 	theMaxId 	= 0;
	
	/**
	 * The id map
	 */
	private idMap	theMap		= new idMap();
	
	/**
	 * Is the Id unique in this list
	 * @param uId the Id to check
	 * @return Whether the id is unique <code>true/false</code>
	 */
	protected boolean isIdUnique(int uId) {
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
		int myId = pItem.getId();
		
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
	public T getItem(int pId) {
		/* Locate the item in the map */
		return theMap.getItem(pId);
	}
	
	/**
	 * Store the item by id
	 * @param pId the id of the item to retrieve 
	 * @param pItem the item to store (or <code>null</code>) 
	 */
	public void setItem(int pId, T pItem) {
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
		private idMap[] 			theMaps		= null;
		
		/**
		 * The map indexed by id
		 */
		private T[] 				theObjects	= null;
		
		/**
		 * Build a new map array
		 */
		@SuppressWarnings("unchecked")
		private void newMaps() {
			theMaps = new IdManager.idMap[maxElements];
			java.util.Arrays.fill(theMaps, null);
		}
		
		/**
		 * Build a new objects array
		 */
		@SuppressWarnings("unchecked")
		private void newObjects() {
			theObjects = (T[])new DataItem[maxElements];
			java.util.Arrays.fill(theObjects, null);
		}
		
		/**
		 * Locate the item by id
		 * @param pId the id of the item to retrieve 
		 * @return the item for the id (or <code>null</code>) 
		 */
		private T getItem(int pId) {
			int 	myIndex  = pId;
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
				myMap = theMaps[myIndex];

				/* If the map is empty return null */
				if (myMap == null) return null;
				
				/* Adjust the index */
				pId -= (myIndex * myAdjust);
				
				/* Pass the call on */
				return myMap.getItem(pId);
			}
			
			/* If we are beyond the scope of this map return null */
			if (myIndex >= maxElements) return null;

			/* Return null if we have no objects */
			if (theObjects == null) return null;
			
			/* Calculate final index and return item */
			myIndex = pId % maxElements;
			return theObjects[myIndex];
		}

		/**
		 * Store the item by id
		 * @param pId the id of the item to retrieve 
		 * @param pItem the item to store (or <code>null</code>) 
		 */
		private void setItem(int pId, T pItem) {
			int 	myIndex 	= pId;
			int 	myAdjust	= 1;
			idMap	myMap		= null;
			
			/* Loop to find the index in this map */
			for (int i  = theDepth; i > 0; i--)  { 
				myIndex		/= maxElements;
				myAdjust 	*= maxElements;
			}
		
			/* If we are beyond the scope of this map */
			if (myIndex >= maxElements) {
				/* If this map is non-empty */
				if ((theMaps != null) || (theObjects != null)) { 
					/* Create a sub-map based on this one */
					myMap = new idMap();
					myMap.theDepth 		= theDepth;
					myMap.theMaps  		= theMaps;
					myMap.theObjects	= theObjects;
				}
				
				/* Increase the depth of this map */
				theDepth++;

				/* Create a new maps array for this map */
				newMaps();
				
				/* Store the map */
				theMaps[0] = myMap;

				/* Try with the adjusted map depth */
				setItem(pId, pItem);
				return;
			}

			/* If we are not the final map */
			if (theDepth > 0) {
				/* Access the relevant map */
				myMap = theMaps[myIndex];

				/* If the map is empty */
				if (myMap == null) {
					/* Create and store a new id map */
					myMap = new idMap();
					myMap.newMaps();
					myMap.theDepth = theDepth-1;
					theMaps[myIndex] = myMap;
				}
				
				/* Adjust the index */
				pId -= (myIndex * myAdjust);
				
				/* Pass the call on with adjusted id */
				myMap.setItem(pId, pItem);
				return;
			}

			/* Allocate the objects array if needed */
			if (theObjects == null) newObjects();
			
			/* Store the item */
			myIndex = pId % maxElements;
			theObjects[myIndex] = pItem;
		}
	}
}

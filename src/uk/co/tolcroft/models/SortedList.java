package uk.co.tolcroft.models;

/**
 * Extension of {@link java.util.List} that provides a sorted list implementation with the ability for objects to be hidden on the list.
 * Objects held in the list must implement the {@link SortedList.linkObject} interface. This requires the object to hold a link that 
 * allows fast access to their position on the list, resulting in no need to search the list to find the object, and also automatic
 * knowledge of the index of an object. A reference to the list is also passed with the link node allowing the object to implement 
 * a store that allows it to reside in multiple lists. This parameter can be ignored which has the side-effect that objects 
 * are uniquely associated with the list and cannot be held in more than one such list.
 * An index map {@link indexMap} is also built allowing fast search for objects by index.
 * 
 * <ul><li>Null objects are not allowed.
 * <li>Duplicate objects are not allowed
 * <li>The semantics of the {@link #add(linkObject)} method are changed such that the element is added at 
 * its natural position in the list rather than at the end of the * list.
 * <li>The {@link #subList(int, int)} method is not supported</ul>
 * @author Tony Washer
 */
public class SortedList<T extends SortedList.linkObject> implements java.util.List<T> {

	/**
	 * Interface for link-able objects allowing comparison functions
	 */
	public static interface linkObject extends java.lang.Comparable<Object> {
		/**
		 * Compares two link-able objects to for equality
		 * @param pCompare the comparison object
		 * @return <code>true/false</code>
		 */
		boolean equals(Object pCompare);

		/**
		 * Determine whether the item is Hidden
		 * @return <code>true/false</code>
		 */
		boolean	isHidden();

		/**
		 * Set the linkNode for a list
		 * @param pList the list
		 * @param pNode the node
		 */
		void 	setLinkNode(Object pList, Object pNode);

		/**
		 * Get the linkNode for a List
		 * @param pList the list
		 * @return the Link node
		 */
		Object 	getLinkNode(Object pList);
	}
	
	/**
	 * The first node in the list
	 */
	private linkNode		theFirst		= null;

	/**
	 * The last node in the list
	 */
	private linkNode		theLast			= null;

	/**
	 * Is the search for insert point conducted from the start or end of the list
	 */
	private boolean			insertFromStart = true;
		
	/**
	 * Do we skip hidden elements
	 */
	private boolean			doSkipHidden	= true;
	
	/**
	 * Index map for list
	 */
	private indexMap		theIndexMap		= new indexMap();
	
	/**
	 * Self reference
	 */
	private SortedList<T>	theList			= this;
	
	/**
	 *  Construct a list. Inserts search backwards from the end for the insert point
	 */
	public SortedList() { this(false); }
		
	/**
	 *  Construct a list
	 *  @param fromStart - should inserts be attempted from start/end of list
	 */
	public SortedList(boolean fromStart) { insertFromStart = fromStart; }
		
	/**
	 *  get setting of option as to whether to skip hidden elements
	 *  @return should we skip hidden elements
	 */
	public boolean getSkipHidden() { return doSkipHidden; }

	/**
	 *  Set option as to whether to skip hidden elements
	 *  @param skipHidden - should we skip hidden elements
	 */
	public void setSkipHidden(boolean skipHidden) { doSkipHidden = skipHidden; }

	/**
	 *  add an object to the list  
	 *  @param pItem - item to add to the list
	 *  @return <code>true</code>
	 */
	public boolean add(T pItem) {
		linkNode myNode;
		
		/* Reject if the object is null */
		if (pItem == null) throw new java.lang.NullPointerException();
		
		/* Reject if the object is already a link member of this list */
		if (pItem.getLinkNode(this) != null) return false;
		
		/* Allocate the new node */
		myNode = new linkNode(pItem);
		
		/* Add in the appropriate fashion */
		if (insertFromStart) 
			addNodeFromStart(myNode);
		else
			addNodeFromEnd(myNode);
				
		/* Set the reference to this node in the item */
		pItem.setLinkNode(this, myNode);
		
		/* Adjust the indexMap */
		theIndexMap.insertNode(myNode);
		
		/* Return to caller */
		return true;
	}
		
	/**
	 *  add Node to the list searching from the start
	 *  @param pNode - node to add to the list
	 */
	private void addNodeFromStart(linkNode pNode) {
		linkNode	myCurr;
		boolean 	isVisible;
		
		/* Determine whether this item is hidden */
		isVisible = !pNode.theObject.isHidden();
		   
	   	/* Loop through the current items */
	    for(myCurr = theFirst;
	        myCurr != null;
	        myCurr = myCurr.theNext)
		{
		   	/* Break if we have found an element that should be later */
		   	if (myCurr.compareTo(pNode) >= 0) break;
		}
		       
		/* If we found an insert point */
		if (myCurr != null) {
		   	/* Set values for the new item */
		    pNode.thePrev = myCurr.thePrev;
		    pNode.theNext = myCurr;
		
		    /* Set new indices */
		    pNode.theIndex 			= myCurr.theIndex;
		    pNode.theHiddenIndex 	= myCurr.theHiddenIndex;
		    if (myCurr.isHidden == isVisible)
		    	myCurr.theHiddenIndex += (isVisible) ? 1 : -1;
		    
		    /* Add to the list */
		    myCurr.thePrev = pNode;
		    if (pNode.thePrev != null)
		      	pNode.thePrev.theNext = pNode;
		    else 
		      	theFirst = pNode;
		    
		    /* Loop through subsequent elements increasing the indices */
		    while (myCurr != null) {
		    	/* Increment indices */
		    	myCurr.theIndex++;
		    	if (isVisible) myCurr.theHiddenIndex++;
		    	myCurr = myCurr.theNext;
		    }
		}
		       	
		/* else we need to add to the end of the list */
		else {
		  	/* Set values for the new item */
		   	pNode.thePrev = theLast;
		   	pNode.theNext = null;
		    	
		   	/* If this is the first item */
		   	if (theLast == null) {
		   		/* Set new indices */
		   		pNode.theIndex 			= 0;
		   		pNode.theHiddenIndex 	= (isVisible) ? 0 : -1;
		    
		   		/* Add to the list */
		   		theLast  = pNode;   
		   		theFirst = pNode;
		   	}
		   	
		   	/* else we have a previous item */
		   	else {
		   		/* Set new indices */
		   		pNode.theIndex 			= theLast.theIndex+1;
		   		pNode.theHiddenIndex 	= (isVisible) ? theLast.theHiddenIndex+1
		   											  : theLast.theHiddenIndex;
		    
		   		/* Add to the list */
		   		theLast 			  = pNode;   
		   		pNode.thePrev.theNext = pNode;
		   	}
		}
	}
		
	/**
	 *  add Node to the list searching from the end
	 *  @param pNode - node to add to the list
	 */
	private void addNodeFromEnd(linkNode pNode) {
		linkNode	myCurr;		   
		boolean 	isVisible;
		
		/* Determine whether this item is hidden */
		isVisible = !pNode.theObject.isHidden();
		   
	   	/* Loop backwards through the current items */
	    for(myCurr = theLast;
	        myCurr != null;
	        myCurr = myCurr.thePrev)
	    {
	    	/* Break if we have found an element that should be earlier */
	    	if (myCurr.compareTo(pNode) <= 0) break;
	    }
	       
	    /* If we found an insert point */
	    if (myCurr != null) {
	    	/* Set values for the new item */
	        pNode.theNext = myCurr.theNext;
	        pNode.thePrev = myCurr;
		    	    
		    /* Set new indices */
		    pNode.theIndex 			= myCurr.theIndex+1;
		    pNode.theHiddenIndex 	= myCurr.theHiddenIndex+1;
		    if (!isVisible) pNode.theHiddenIndex--;
		    
	        /* Add to the list */
	        myCurr.theNext = pNode;
	        if (pNode.theNext != null)
	        	pNode.theNext.thePrev = pNode;
	        else 
	        	theLast = pNode;
	    }
		       	
	    /* else we need to add to the beginning of the list */
	    else {
	   		/* Set values for the new item */
	   		pNode.theNext = theFirst;
	   		pNode.thePrev = null;
    	
	   		/* Set new indices */
	   		pNode.theIndex 			= 0;
	   		pNode.theHiddenIndex 	= (!isVisible) ? -1 : 0;
	    
		   	/* If this is the first item */
		   	if (theFirst == null) {
		   		/* Add to the list */
		   		theLast  = pNode;   
		   		theFirst = pNode;
		   	}
		   	
		   	/* else we have a next item */
		   	else {
		   		/* Add to the list */
		   		theFirst 				= pNode;   
	   			pNode.theNext.thePrev 	= pNode;
		   	}
	    }
	    
        /* Loop through subsequent elements increasing the indices */
        myCurr = pNode.theNext;
	    while (myCurr != null) {
	    	/* Increment indices */
	    	myCurr.theIndex++;
	    	if (isVisible) myCurr.theHiddenIndex++;
	    	myCurr = myCurr.theNext;
	    }
	}
		
	/**
	 *  Remove node from list
	 *  @param pNode - node to remove from list
	 */
	private void removeNode(linkNode pNode) {
		linkNode	myCurr;
		boolean 	isVisible; 
		
		/* Determine whether this item is visible */
		isVisible = !pNode.theObject.isHidden();
		   
		/* Adjust pointers to skip this element */
		if (pNode.thePrev != null)
			pNode.thePrev.theNext = pNode.theNext;
		else 
			theFirst = pNode.theNext;
		
		if (pNode.theNext != null)
			pNode.theNext.thePrev = pNode.thePrev;
		else
			theLast = pNode.thePrev;
				    
        /* Loop through subsequent elements decreasing the indices */
        myCurr = pNode.theNext;
	    while (myCurr != null) {
	    	/* Decrement indices */
	    	myCurr.theIndex--;
	    	if (isVisible) myCurr.theHiddenIndex--;
	    	myCurr = myCurr.theNext;
	    }
	    
	    /* Remove the node from the index map */
	    theIndexMap.removeNode(pNode);
	    
		/* clean our links */
		pNode.theNext = null;
		pNode.thePrev = null;
		
		/* Remove the reference to this node in the item */
		pNode.theObject.setLinkNode(this, null);
	}
		
	/**
	 *  set and object as hidden/visible
	 *  @param o - the relevant object
	 *  @param isHidden - is the object hidden
	 */
	@SuppressWarnings("unchecked")
	public void setHidden(T pItem, boolean isHidden) {
		linkNode	myNode;
		int			iAdjust;
		
		/* Reject if these object is null */
		if (pItem == null) throw new java.lang.NullPointerException();
		
		/* Access the node of the item */
		myNode = (linkNode)pItem.getLinkNode(this);
		
		/* If the node does not belong to the list then ignore */
		if ((myNode == null) || (myNode.getList() != theList)) return;
			
		/* If we are changing things */
		if (isHidden != myNode.isHidden) {
			/* set the hidden value */
			myNode.isHidden = isHidden;

			/* Loop through subsequent elements adjusting the indices */
			iAdjust = (isHidden) ? -1 : 1;
			while (myNode != null) {
				/* Decrement indices */
				myNode.theHiddenIndex += iAdjust;
				myNode = myNode.theNext;
			}			
	    }
	}
		
	/**
	 * remove All list items
	 */
	public void clear() {
		linkNode myNode;
		
		/* Remove the items */
		while (theFirst != null) {
			/* Access and unlink the node */
			myNode 		= theFirst;
			theFirst 	= myNode.theNext;
			
			/* Remove links from node */
			theFirst.theObject.setLinkNode(this, null);
			myNode.theNext = null;
			myNode.thePrev = null;
		}
		
		/* Reset the last item and clear the map */
		theLast = null;
		theIndexMap.clear();
	}
	
	/**
	 * is the list empty of visible items
	 * @return <code>true/false</code>
	 */
	public boolean isEmpty() {
		/* Return details */
		return (getFirst() == null);
	}
	
	/**
	 * is the list empty of all (including hidden) items
	 * @return <code>true/false</code>
	 */
	public boolean isEmptyAll() {
		/* Return details */
		return (theFirst == null);
	}
	
	/**
	 * obtain an Iterator for this list
	 * @return <code>true/false</code>
	 */
	public java.util.Iterator<T> iterator() {
		/* Return a new iterator */
		return new ListIterator();
	}
	
	/**
	 * obtain a list Iterator for this list
	 * @return List iterator
	 */
	public ListIterator listIterator() {
		/* Return a new iterator */
		return new ListIterator();
	}
	
	/**
	 * obtain a list Iterator for this list
	 * @param bShowAll show all items in the list
	 * @return List iterator
	 */
	public ListIterator listIterator(boolean bShowAll) {
		/* Return a new iterator */
		return new ListIterator(bShowAll);
	}
	
	/**
	 * obtain a list Iterator for this list initialised to an index
	 * @param iIndex the index to initialise to
	 * @return List iterator
	 */
	public ListIterator listIterator(int iIndex) {
		int 			iCurr = 0;
		ListIterator	myCurr;
		Object			myObj;
		
		/* Reject if the index is negative */
		if (iIndex < 0) throw new java.lang.IndexOutOfBoundsException();
		
		/* Create a list iterator */
		myCurr = new ListIterator();
		
		/* Loop through the elements */
		while ((myObj = (T)myCurr.next()) != null) {
			/* Break if we have found the item */
			if (iCurr == iIndex) break;
			
			/* Increment count */
			iCurr++;
		}
		
		/* Note if we did not find the item */
		if (myObj == null) throw new java.lang.IndexOutOfBoundsException();
		
		/* Return a new iterator */
		return myCurr;
	}
	
	/**
	 * Determine whether this list is equal to another
	 * @return <code>true/false</code>
	 */
	@SuppressWarnings("unchecked")
	public boolean equals(Object pThat) {
		linkNode 		myCurr;
		linkNode 		myOther;
		SortedList<T> 	myThat;
		
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a finSortedList */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target list */
		myThat = (SortedList<T>)pThat;
		
		/* Loop through the list */
		for (myCurr = theFirst, myOther = myThat.theFirst;
		     (myCurr != null) || (myOther != null);
		     myCurr = myCurr.theNext, myOther = myOther.theNext) {
			/* If either entry is null then we differ */
			if ((myCurr == null) || (myOther == null)) return false;
			
			/* If the entries differ then the lists differ */
			if (!myCurr.theObject.equals(myOther.theObject)) return false;
		}
		
		/* We are identical */
		return true;
	}
	
	/**
	 * obtain the size of the list
	 * @return the number of visible items in the list
	 */
	public int size() {
		int				iSize = 0;
		
		/* If we have an element in the list */
		if (theLast != null) {
			/* Get the relevant index and add 1 */
			iSize = 1 + theLast.getIndex();
		}
		
		/* Return the count */
		return iSize;
	}
	
	/**
	 * obtain the full size of the list (including hidden items)
	 * @return the number of visible items in the list
	 */
	public int sizeAll() {
		int				iSize = 0;
		
		/* If we have an element in the list */
		if (theLast != null) {
			/* Get the full index and add 1 */
			iSize = 1 + theLast.theIndex;
		}
		
		/* Return the count */
		return iSize;
	}
	
	/**
	 * obtain the full size of the list (including hidden items)
	 * @return the number of visible items in the list
	 */
	public int sizeNormal() {
		int				iSize = 0;
		
		/* If we have an element in the list */
		if (theLast != null) {
			/* Get the hidden index and add 1 */
			iSize = 1 + theLast.theHiddenIndex;
		}
		
		/* Return the count */
		return iSize;
	}
	
	/**
	 * get the item at the specified index
	 * @param iIndex index of item
	 * @return the required item
	 */
	public T get(int iIndex) {
		linkNode		myNode;
		
		/* Reject if the index is negative */
		if (iIndex < 0) throw new java.lang.IndexOutOfBoundsException();
		
		/* Access the node */
		myNode = theIndexMap.getNodeAtIndex(iIndex);
		
		/* Note if we did not find the item */
		if (myNode == null) throw new java.lang.IndexOutOfBoundsException();
		
		/* Return the item */
		return myNode.theObject;
	}
	
	/**
	 * remove the item at the specified index
	 * @param iIndex index of item
	 * @return the removed item
	 */
	public T remove(int iIndex) {
		linkNode	myNode;
		
		/* Reject if the index is negative */
		if (iIndex < 0) throw new java.lang.IndexOutOfBoundsException();
		
		/* Access the node */
		myNode = theIndexMap.getNodeAtIndex(iIndex);
		
		/* Note if we did not find the item */
		if (myNode == null) throw new java.lang.IndexOutOfBoundsException();
		
		/* Remove the node */
		removeNode(myNode);
		
		/* Return the item */
		return myNode.theObject;
	}
	
	/**
	 * remove the specified item 
	 * @param o the item to remove
	 * @return <code>true/false</code> was the item removed
	 */
	@SuppressWarnings("unchecked")
	public boolean remove(Object o) {
		linkNode	myNode;
		linkObject	myItem;
		
		/* Reject if the object is null */
		if (o == null) throw new java.lang.NullPointerException();
		
		/* Reject if the object is invalid */
		if (!(o instanceof linkObject)) throw new java.lang.ClassCastException();
		
		/* Access as link object */
		myItem = (linkObject)o;
		
		/* Access the node of the item */
		myNode = (linkNode)myItem.getLinkNode(this);
		
		/* If the node does not belong to the list then ignore */
		if ((myNode == null) || (myNode.getList() != theList)) return false;
			
		/* Remove the item */
		removeNode(myNode);
		
		/* Return the success/failure */
		return true;
	}
	
	/**
	 * re-sort the specified item by removing it from the list and re-adding it
	 * @param o the item to resort
	 */
	public void reSort(T o) {
		/* Remove the object from the list */
		remove(o);
		
		/* Add the item back into the list */
		add(o);		
	}
	
	/**
	 * obtain the index within the list of the object
	 * @param o the object to find the index of
	 * @return the index within the list (or -1 if not visible/present in the list)
	 */
	@SuppressWarnings("unchecked")
	public int indexOf(Object o) {
		int 			iIndex = 0;
		linkNode		myNode;
		linkObject		myItem;
		
		/* Reject if the object is null */
		if (o == null) throw new java.lang.NullPointerException();
		
		/* Reject if the object is invalid */
		if (!(o instanceof linkObject)) throw new java.lang.ClassCastException();
		
		/* Access as link object */
		myItem = (linkObject)o;
		
		/* Access the node of the item */
		myNode = (linkNode)myItem.getLinkNode(this);
		
		/* If the node does not belong to the list then ignore */
		if ((myNode == null) || (myNode.getList() != theList)) return -1;
			
		/* Access the index of the item */
		iIndex = myNode.getIndex();
		
		/* Return the index */
		return iIndex;
	}
	
	/**
	 * obtain the last index within the list of the object
	 * @param o the object to find the last index of
	 * @return the index within the list (or -1 if not visible/present in the list)
	 */
	public int lastIndexOf(Object o) {
		/* Objects cannot be duplicate so redirect to indexOf */
		return indexOf(o);
	}
	
	/**
	 * determine whether the list contains this item
	 * @param o the object to check
	 * @return <code>true/false</code>
	 */
	@SuppressWarnings("unchecked")
	public boolean contains(Object o) {
		linkNode		myNode;
		linkObject		myItem;
		
		/* Reject if the object is null */
		if (o == null) throw new java.lang.NullPointerException();
		
		/* Reject if the object is invalid */
		if (!(o instanceof linkObject)) throw new java.lang.ClassCastException();
		
		/* Access as link object */
		myItem = (linkObject)o;
		
		/* Access the node of the item */
		myNode = (linkNode)myItem.getLinkNode(this);
		
		/* If the node does not belong to the list then ignore */
		if ((myNode == null) || (myNode.getList() != theList)) return false;
			
		/* Return that the object belongs */
		return true;
	}
	
	/**
	 * Peek at the next item
	 * @param pItem the item from which to find the next item
	 * @return the next item or <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public T peekNext(T pItem) {
		linkNode myNode;
		
		/* Reject if the object is null */
		if (pItem == null) throw new java.lang.NullPointerException();
		
		/* Access the node of the item */
		myNode = (linkNode)pItem.getLinkNode(this);
		
		/* If the node does not belong to the list then ignore */
		if ((myNode == null) || (myNode.getList() != theList)) return null;
			
		/* Access the next node */
		myNode = myNode.getNext();
		
		/* Return the next object */
		return (myNode == null) ? null : myNode.theObject;
	}

	/**
	 * Peek at the previous item
	 * @param pItem the item from which to find the previous item
	 * @return the previous item or <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public T peekPrevious(T pItem) {
		linkNode myNode;
		
		/* Reject if the object is null */
		if (pItem == null) throw new java.lang.NullPointerException();
		
		/* Access the node of the item */
		myNode = (linkNode)pItem.getLinkNode(this);
		
		/* If the node does not belong to the list then ignore */
		if ((myNode == null) || (myNode.getList() != theList)) return null;
			
		/* Access the previous node */
		myNode = myNode.getPrev();
		
		/* Return the previous object */
		return (myNode == null) ? null : myNode.theObject;
	}

	/**
	 * determine whether the list contains all items in the collection
	 * @param pCollection the collection of objects to check
	 * @return the index within the list (or -1 if not visible/present in the list)
	 */
	public boolean containsAll(java.util.Collection<?> pCollection) {	
		/* Reject if the collection is null */
		if (pCollection == null) throw new java.lang.NullPointerException();
		
		/* Loop through the collection */
		for (Object o : pCollection) {
			/* If the item is not in the list return false */
			if (!contains(o)) return false;
		}
				
		/* Return success */
		return true;
	}
	
	/**
	 * return an array of the items in this list in sort order
	 * @return the array
	 */
	public Object[] toArray() {
		int 				iSize;
		int					i;
		ListIterator		myIterator;
		Object[] 			myArray;
			
		/* Determine the size of the array */
		iSize = size();
		
		/* Allocate an array list of the estimated size */
		myArray = new Object[iSize];
		
		/* Loop through the list */
		for (i = 0, myIterator = new ListIterator();
			 i < iSize;
			 i++) {
			/* Store the next item */
			myArray[i] = myIterator.next();
		}
				
		/* Return the array */
		return myArray;
	}
	
	/**
	 * return an array of the items in this list in sort order
	 * @return the array
	 */
	@SuppressWarnings("unchecked")
	public <X> X[] toArray(X[] a) {
		int 					iSize;
		java.util.List<X> 		myList;
		
		/* Determine the size of the array */
		iSize = size();
		
		/* Reject if the sample array is null */
		if (a == null) throw new java.lang.NullPointerException();
		
		/* Check that we are using the right type */
		if (!(a[0] instanceof linkObject))			
			throw new java.lang.ArrayStoreException();
		
		/* Allocate an array list of the estimated size */
		myList = new java.util.ArrayList<X>(iSize);
		
		/* Loop through the list */
		for(Object myObj : this) {
			/* Store the next item */
			myList.add((X)myObj);
		}
				
		/* Return the array */
		return myList.toArray(a);
	}
	
	/**
	 * Create a subList. Disallowed.
	 * @param iFromIndex start index of sublist 
	 * @param iToIndex end index of sublist 
	 */
	public SortedList<T> subList(int iFromIndex, int iToIndex) {				
		/* Throw exception */
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Set the contents of the item at index. Disallowed.
	 * @param iIndex index of item to set 
	 * @param object to set 
	 */
	public T set(int iIndex, T o) {				
		/* Throw exception */
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Add at element at an explicit location. Disallowed.
	 * @param iIndex index of item to add after
	 * @param object to add
	 */
	public void add(int iIndex, T o) {				
		/* Throw exception */
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Add All elements in the collection. Disallowed.
	 * @param pCollection collection of items to add
	 */
	public boolean addAll(java.util.Collection<? extends T> pCollection) {				
		/* Throw exception */
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Add All elements in the collection at an index. Disallowed.
	 * @param pCollection collection of items to add
	 */
	public boolean addAll(int iIndex, java.util.Collection<? extends T> pCollection) {				
		/* Throw exception */
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Retain All elements in the collection. Disallowed.
	 * @param pCollection collection of items to retain
	 */
	public boolean retainAll(java.util.Collection<?> pCollection) {				
		/* Throw exception */
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Remove All elements in the collection. Disallowed.
	 * @param pCollection collection of items to remove
	 */
	public boolean removeAll(java.util.Collection<?> pCollection) {				
		/* Throw exception */
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Get the first node in the sequence
	 * @return the First visible node
	 */
	private linkNode getFirst() {
		linkNode myFirst;
		
		/* Get the first item */
		myFirst = theFirst;

		/* Skip to next visible item if required */
		if ((myFirst != null) && 
			(myFirst.isHidden) &&
			(doSkipHidden)) 
			myFirst = myFirst.getNext();
		
		/* Return to caller */
		return myFirst; 
	}
	
	/**
	 * Get the last node in the sequence
	 * @return the Last visible node
	 */
	private linkNode getLast() {
		linkNode myLast;
		
		/* Get the last item */
		myLast = theLast;
		
		/* Skip to previous visible item if required */
		if ((myLast != null) && 
			(myLast.isHidden) &&
			(doSkipHidden)) 
			myLast = myLast.getPrev();
		
		/* Return to caller */
		return myLast; 
	}
	
	/**
	 * Link Node class
	 */
	private class linkNode {
		/**
		 * The object that this node refers to 
		 */
		private T 			theObject 		= null;
		
		/**
		 * Is the object hidden 
		 */
		private boolean		isHidden 		= false;
		
		/**
		 * The standard index of this item 
		 */
		private int			theIndex 		= -1;
		
		/**
		 * The object that this node refers to 
		 */
		private int			theHiddenIndex 	= -1;
		
		/**
		 * The next node in the sequence 
		 */
		private linkNode	theNext 		= null;
		
		/**
		 * The previous node in the sequence 
		 */
		private linkNode	thePrev 		= null;
		
		/**
		 * Initialiser
		 */
		private linkNode(T pObject) {
			theObject = pObject;
		}
		
		/**
		 * Get the next node in the sequence
		 * @return the Next visible node
		 */
		private linkNode getNext() {
			linkNode myNext;
			
			/* Loop until we get the next visible item */
			myNext = theNext;
			while ((myNext != null) && 
				   (myNext.isHidden) &&
				   (doSkipHidden)) 
				myNext = myNext.theNext;
			
			/* Return to caller */
			return myNext; 
		}
		
		/**
		 * Get the previous node in the sequence
		 * @return the previous visible node
		 */
		private linkNode getPrev() { 
			linkNode myPrev;
			
			/* Loop until we get the next visible item */
			myPrev = thePrev;
			while ((myPrev != null) && 
				   (myPrev.isHidden) &&
				   (doSkipHidden))
				myPrev = myPrev.thePrev;
			
			/* Return to caller */
			return myPrev; 
		}

		/**
		 * Get the index of the item
		 * @return the relevant index of the item
		 */
		private int getIndex() { 			
			/* Return the relevant index */
			return (doSkipHidden) ? theHiddenIndex : theIndex; 
		}

		/**
		 * Get the list to which this node belongs
		 * @return the holding list
		 */
		private SortedList<T> getList() { 			
			/* Return the list */
			return theList; 
		}

		/**
		 * Compare this node to another
		 * @param pThat the node to compare to
		 * @return (-1,0,1) depending on order
		 */
		private int compareTo(linkNode pThat) { 
			return theObject.compareTo(pThat.theObject); }
	}
	
	/**
	 * ListIterator class for this list
	 */
	public class ListIterator implements java.util.ListIterator<T> {
		/**
		 * Last node accessed 
		 */
		private linkNode			theNodeBefore 	= null;
		
		/**
		 * Last node accessed 
		 */
		private linkNode			theNodeAfter	= null;
		
		/**
		 * Can we remove the last item 
		 */
		private boolean				canRemove 		= false;
		
		/**
		 * Which direction was the last scan 
		 */
		private boolean				wasForward 		= true;

		/**
		 * Should we show all elements 
		 */
		private boolean				showAll 		= true;

		/**
		 * Constructor for standard iterator 
		 */
		private ListIterator() { this(false); }
		
		/**
		 * Constructor for iterator that can show all elements 
		 * @param bShowAll show all items in the list
		 */
		protected ListIterator(boolean bShowAll) { showAll = bShowAll; }
		
		/**
		 * Does the list have a next item
		 * @return <code>true/false</code>
		 */
		public boolean hasNext() {
			linkNode myNext;
			
			/* Access the next node */
			myNext = (theNodeBefore != null) 
							? ((showAll) ? theNodeBefore.theNext : theNodeBefore.getNext())
					   		: ((showAll) ? theFirst : getFirst());
			
			/* Return whether we have a next node */
			return (myNext != null);
		}

		/**
		 * Does the list have a previous item
		 * @return <code>true/false</code>
		 */
		public boolean hasPrevious() {
			linkNode myPrev;
			
			/* Access the next node */
			myPrev = (theNodeAfter != null) 
							? ((showAll) ? theNodeAfter.thePrev : theNodeAfter.getPrev())
							: ((showAll) ? theLast : getLast());
			
			/* Return whether we have a previous node */
			return (myPrev != null);
		}

		/**
		 * Peek at the next item
		 * @return the next item or <code>null</code>
		 */
		public T peekNext() {
			linkNode myNext;
			
			/* Access the next node */
			myNext = (theNodeBefore != null) 
							? ((showAll) ? theNodeBefore.theNext : theNodeBefore.getNext())
					   		: ((showAll) ? theFirst : getFirst());
			
			/* Return the next object */
			return (myNext == null) ? null : myNext.theObject;
		}

		/**
		 * Peek at the previous item
		 * @return the previous item or <code>null</code>
		 */
		public T peekPrevious() {
			linkNode myPrev;
			
			/* Access the next node */
			myPrev = (theNodeAfter != null) 
							? ((showAll) ? theNodeAfter.thePrev : theNodeAfter.getPrev())
							: ((showAll) ? theLast : getLast());
			
			/* Return the previous object */
			return (myPrev == null) ? null : myPrev.theObject;
		}

		/**
		 * Peek at the first item
		 * @return the first item or <code>null</code>
		 */
		public T peekFirst() {
			linkNode myFirst;
			
			/* Access the first node */
			myFirst = ((showAll) ? theFirst : getFirst());
			
			/* Return the next object */
			return (myFirst == null) ? null : myFirst.theObject;
		}

		/**
		 * Peek at the last item
		 * @return the last item or <code>null</code>
		 */
		public T peekLast() {
			linkNode myLast;
			
			/* Access the last node */
			myLast = ((showAll) ? theLast : getLast());
			
			/* Return the previous object */
			return (myLast == null) ? null : myLast.theObject;
		}

		/**
		 * Access the next item
		 * @return the next item (or null if there is no next item)
		 */
		public T next() {
			linkNode myNext;
			
			/* Access the next node */
			myNext = (theNodeBefore != null) 
							? ((showAll) ? theNodeBefore.theNext : theNodeBefore.getNext())
					   		: ((showAll) ? theFirst : getFirst());											 
	
			/* If we have a next then move the cursor */
			if (myNext != null) {
				/* Record the cursor */
				theNodeBefore 	= myNext;
				theNodeAfter  	= myNext.theNext;
				wasForward	  	= true;
				canRemove		= true;
			}
				
			/* Return the next item */
			return (myNext != null) ? myNext.theObject : null;
		}

		/**
		 * Access the previous item
		 * @return the previous item (or null if there is no previous item)
		 */
		public T previous() {
			linkNode myPrev;
			
			/* Access the previous node */
			myPrev = (theNodeAfter != null) 
							? ((showAll) ? theNodeAfter.thePrev : theNodeAfter.getPrev())
							: ((showAll) ? theLast : getLast());
	
			/* If we have a previous then move the cursor */
			if (myPrev != null) {
				/* Record the cursor */
				theNodeBefore 	= myPrev.thePrev;
				theNodeAfter  	= myPrev;
				wasForward	  	= false;
				canRemove		= true;
			}
				
			/* Return the previous item */
			return (myPrev != null) ? myPrev.theObject : null;
		}
		
		/**
		 * Access the next index
		 * @return the next item index (or -1 if there is no next item)
		 */
		public int nextIndex() {
			linkNode 	myNext;
			int			iIndex = -1;
			
			/* Access the next node */
			myNext = (theNodeBefore != null) 
							? ((showAll) ? theNodeBefore.theNext : theNodeBefore.getNext())
							: ((showAll) ? theFirst : getFirst());											 
	
			/* If we have a next then calculate its index */
			if (myNext != null) iIndex = myNext.getIndex();
				
			/* Return the next item */
			return iIndex;
		}

		/**
		 * Access the previous index
		 * @return the previous item index (or -1 if there is no previous item)
		 */
		public int previousIndex() {
			linkNode	myPrev;
			int		 	iIndex = -1;
			
			/* Access the previous node */
			myPrev = (theNodeAfter != null) 
							? ((showAll) ? theNodeAfter.thePrev : theNodeAfter.getPrev())
							: ((showAll) ? theLast : getLast());
	
			/* If we have a previous then calculate its index */
			if (myPrev != null) iIndex = myPrev.getIndex();
				
			/* Return the index */
			return iIndex;
		}

		/**
		 * Set the contents of the item. Disallowed.
		 * @param o object to set 
		 */
		public void set(T o) {				
			/* Throw exception */
			throw new java.lang.UnsupportedOperationException();
		}

		/**
		 * Add the item at this position. Disallowed.
		 * @param o object to add
		 */
		public void add(T o) {				
			/* Throw exception */
			throw new java.lang.UnsupportedOperationException();
		}

		/**
		 * Remove the last referenced item.
		 */
		public void remove() {				
			/* If we cannot remove the last item throw exception */
			if (!canRemove) throw new java.lang.IllegalStateException();
			
			/* If the last operation was forward */
			if (wasForward) {
				/* Remove the item */
				removeNode(theNodeBefore);
				
				/* Record the new node before */
				theNodeBefore = (theNodeAfter != null) ? theNodeAfter.thePrev
													   : theLast;
			}
			
			/* else the last operation was backwards */
			else {
				/* Remove the item */
				removeNode(theNodeAfter);
				
				/* Record the new node after */
				theNodeAfter = (theNodeBefore != null) ? theNodeBefore.theNext
													   : theFirst;
			}
			
			/* Note that we can no longer remove the item */
			canRemove = false;
		}

		/**
		 * Remove the last referenced item.
		 */
		protected T removeLastItem() {
			T myItem;
			
			/* If we cannot remove the last item throw exception */
			if (!canRemove) throw new java.lang.IllegalStateException();
			
			/* If the last operation was forward */
			if (wasForward) {
				/* Remove the item */
				removeNode(theNodeBefore);
				myItem = theNodeBefore.theObject;
				
				/* Record the new node before */
				theNodeBefore = (theNodeAfter != null) ? theNodeAfter.thePrev
													   : theLast;
			}
			
			/* else the last operation was backwards */
			else {
				/* Remove the item */
				removeNode(theNodeAfter);
				myItem = theNodeAfter.theObject;
				
				/* Record the new node after */
				theNodeAfter = (theNodeBefore != null) ? theNodeBefore.theNext
													   : theFirst;
			}
			
			/* Note that we can no longer remove the item */
			canRemove = false;
			
			/* Return the deleted object */
			return myItem;
		}
	}
	
	/**
	 * IndexMap class for this list. The map class locates its starting search point using the pure index.  The map holds a reference 
	 * to every 50th element, a factor that is controlled by the constant {@link #theGranularity}. If the index required is 251, 
	 * the search will immediately skip to index 250 and start the search from there. If we are skipping hidden
	 * items, the element we are looking for is always later in the list than the calculated start point. The map is updated whenever
	 * items are added to or removed from the list, and only items later in the map than the affected object are updated.
	 */
	private class indexMap {
		/**
		 * Expansion rate of map 
		 */
		private final static int	theExpansion 	= 5;
		
		/**
		 * Granularity of map 
		 */
		private final static int	theGranularity 	= 50;
		
		/**
		 * Array of standard indexes 
		 */
		private Object[]			theMap 			= null;
		
		/**
		 * The length of the map 
		 */
		private int					theMapLength	= 0;
		
		/**
		 * Obtain the node at the specified index
		 * @param iIndex the index of the node
		 * @return the relevant node (or null)
		 */
		@SuppressWarnings("unchecked")
		private linkNode getNodeAtIndex(int iIndex) {
			int 		iMapIndex;
			linkNode 	myNode;
			
			/* Calculate the map index */
			iMapIndex = iIndex / theGranularity;
			
			/* Handle out of range */
			if (iMapIndex > theMapLength-1) return null;
			
			/* Access the start node for the search */
			myNode = (linkNode)theMap[iMapIndex];
			
			/* Search for the correct node */
			while (myNode != null) {
				/* Break if we have found the node */
				if (myNode.getIndex() == iIndex) break;
				
				/* Shift to next node */
				myNode = myNode.theNext;
			}
			
			/* Return the correct node */
			return myNode;
		}

		/**
		 * Insert a map node
		 * @param pNode the node to insert
		 */
		@SuppressWarnings("unchecked")
		private void insertNode(linkNode pNode) {
			int			iIndex;
			int 		iMapIndex;
			linkNode 	myNode;
			
			/* Access the index of the node */
			iIndex = pNode.theIndex;
			
			/* Calculate the map index */
			iMapIndex = iIndex / theGranularity;
			
			/* If we need to extend the map */
			if (iMapIndex > theMapLength-1) {
				/* If we have an existing map */
				if (theMap != null) {
					/* Extend the map by expansion number of entries */
					theMap = java.util.Arrays.copyOf(theMap, theMapLength+theExpansion);
				}
				
				/* Else we have no current map */
				else {
					/* Allocate and initialise the map */
					theMap = new Object[theExpansion];
					java.util.Arrays.fill(theMap, null);
				}
				
				/* Adjust the map length */
				theMapLength += theExpansion;
			}
			
			/* If this is a mapped node */
			if ((iIndex % theGranularity) == 0) {
				/* Store the node into the map */
				theMap[iMapIndex] = pNode;
			}
			
			/* For all subsequent nodes */
			while (++iMapIndex < theMapLength) {
				/* Access the node in the map */
				myNode = (linkNode)theMap[iMapIndex];
				
				/* Break if we have reached the end of the map */
				if (myNode == null) break;
				
				/* Shift the index to the previous item */
				theMap[iMapIndex] = myNode.thePrev;
			}
			
			/* If the last node has been shifted and needs storing, then store it */
			if ((pNode != theLast) && ((theLast.theIndex % theGranularity) == 0))
				insertNode(theLast);
		}
		
		/**
		 * Remove a map node
		 * @param pNode the node to remove
		 */
		@SuppressWarnings("unchecked")
		private void removeNode(linkNode pNode) {
			int			iIndex;
			int 		iMapIndex;
			linkNode 	myNode;
			
			/* Access the index of the node */
			iIndex = pNode.theIndex;
			
			/* Calculate the map index */
			iMapIndex = iIndex / theGranularity;
			
			/* Ignore node if it is past end of map */
			if (iMapIndex > theMapLength-1) return;
				
			/* If this is a mapped node */
			if ((iIndex % theGranularity) == 0) {
				/* Adjust this node explicitly */
				theMap[iMapIndex] = pNode.theNext;
			}
			
			/* For all subsequent nodes */
			while (++iMapIndex < theMapLength) {
				/* Access the node in the map */
				myNode = (linkNode)theMap[iMapIndex];
				
				/* Break if we have reached the end of the map */
				if (myNode == null) break;
				
				/* Shift the index to the next item */
				theMap[iMapIndex] = myNode.theNext;
			}
		}
		
		/**
		 * Clear the indexMap
		 */
		private void clear() {
			/* Reinitialise the map to null */
			if (theMap != null) java.util.Arrays.fill(theMap, null);
		}
	}
}

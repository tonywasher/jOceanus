package uk.co.tolcroft.models;

/**
 * Extension of {@link java.util.List} that provides a sorted list implementation with the ability for objects to be hidden on the list.
 * Objects held in the list must be extensions of the LinkObject class. This requires the object to hold a link that 
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
public class SortedList<T extends LinkObject<T>> implements java.util.List<T> {

	/**
	 * The first node in the list
	 */
	private LinkNode<T>		theFirst		= null;

	/**
	 * The last node in the list
	 */
	private LinkNode<T>		theLast			= null;

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
	 * Class of the objects held in this list
	 */
	private Class<T>		theClass		= null;
	
	/**
	 *  Obtain the class of objects in this sorted list
	 *  @return should we skip hidden elements
	 */
	public Class<T> 		getBaseClass() 	{ return theClass; }

	/**
	 *  get setting of option as to whether to skip hidden elements
	 *  @return should we skip hidden elements
	 */
	public boolean 			getSkipHidden() { return doSkipHidden; }

	/**
	 *  Construct a list. Inserts search backwards from the end for the insert point
	 */
	public SortedList(Class<T> pClass) { this(pClass, false); }
		
	/**
	 *  Construct a list
	 *  @param fromStart - should inserts be attempted from start/end of list
	 */
	public SortedList(Class<T> pClass, boolean fromStart) { 
		insertFromStart = fromStart;
		theClass		= pClass;
	}
		
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
		LinkNode<T> myNode;
		
		/* Reject if the object is null */
		if (pItem == null) throw new java.lang.NullPointerException();
		
		/* Reject if the object is already a link member of this list */
		if (pItem.getLinkNode(this) != null) return false;
		
		/* Allocate the new node */
		myNode = new LinkNode<T>(this, pItem);
		
		/* Add in the appropriate fashion */
		if (insertFromStart) 
			myNode.addFromStart(theFirst, theLast);
		else
			myNode.addFromEnd(theFirst, theLast);
						
		/* Adjust first and last if necessary */
		if (myNode.getPrev(false) == null) theFirst = myNode;
		if (myNode.getNext(false) == null) theLast  = myNode;

		/* Set the reference to this node in the item */
		pItem.setLinkNode(this, myNode);
		
		/* Adjust the indexMap */
		theIndexMap.insertNode(myNode);
		
		/* Return to caller */
		return true;
	}
		
	/**
	 *  Remove node from list
	 *  @param pNode - node to remove from list
	 */
	private void removeNode(LinkNode<T> pNode) {	    
		/* Remove the reference to this node in the item */
		pNode.getObject().setLinkNode(this, null);
		
		/* Adjust first and last indicators if required */
		if (theFirst == pNode) theFirst = pNode.getNext(false);
		if (theLast  == pNode) theLast  = pNode.getPrev(false);
		
        /* Remove the node from the list */
        pNode.remove();

        /* Remove the node from the index map */
	    theIndexMap.removeNode(pNode);
	}
		
	/**
	 *  set an object as hidden/visible
	 *  @param pItem - the relevant object
	 *  @param isHidden - is the object hidden
	 */
	public void setHidden(T pItem, boolean isHidden) {
		LinkNode<T>	myNode;
		
		/* Reject if these object is null */
		if (pItem == null) throw new java.lang.NullPointerException();
		
		/* Access the node of the item */
		myNode = pItem.getLinkNode(this);
		
		/* If the node does not belong to the list then ignore */
		if ((myNode == null) || (myNode.getList() != theList)) return;
			
		/* If we are changing things */
		if (isHidden != myNode.isHidden()) {
			/* set the hidden value */
			myNode.setHidden(isHidden);
	    }
	}
		
	/**
	 * remove All list items
	 */
	public void clear() {
		LinkNode<T> myNode;
		
		/* Remove the items in reverse order */
		while (theLast != null) {
			/* Access and unlink the node */
			myNode 	= theLast;
			theLast	= myNode.getNext(false);
			
			/* Remove links from node and list */
			myNode.getObject().setLinkNode(this, null);
			myNode.remove();
		}
		
		/* Reset the first item and clear the map */
		theFirst = null;
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
		T				myObj;
		
		/* Reject if the index is negative */
		if (iIndex < 0) throw new java.lang.IndexOutOfBoundsException();
		
		/* Create a list iterator */
		myCurr = new ListIterator();
		
		/* Loop through the elements */
		while ((myObj = myCurr.next()) != null) {
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
	public boolean equals(Object pThat) {
		LinkNode<?>		myCurr;
		LinkNode<?>		myOther;
		SortedList<?> 	myThat;
		
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a SortedList */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target list */
		myThat = (SortedList<?>)pThat;
		
		/* Make sure that the object is the same data class */
		if (myThat.theClass != this.theClass) 	return false;
		
		/* Loop through the list */
		for (myCurr = theFirst, myOther = myThat.theFirst;
		     (myCurr != null) || (myOther != null);
		     myCurr = myCurr.getNext(false), myOther = myOther.getNext(false)) {
			/* If either entry is null then we differ */
			if ((myCurr == null) || (myOther == null)) return false;
			
			/* If the entries differ then the lists differ */
			if (!myCurr.getObject().equals(myOther.getObject())) return false;
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
			iSize = 1 + theLast.getIndex(doSkipHidden);
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
			iSize = 1 + theLast.getIndex(false);
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
			iSize = 1 + theLast.getIndex(false);
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
		LinkNode<T>		myNode;
		
		/* Reject if the index is negative */
		if (iIndex < 0) throw new java.lang.IndexOutOfBoundsException();
		
		/* Access the node */
		myNode = theIndexMap.getNodeAtIndex(iIndex);
		
		/* Note if we did not find the item */
		if (myNode == null) throw new java.lang.IndexOutOfBoundsException();
		
		/* Return the item */
		return myNode.getObject();
	}
	
	/**
	 * remove the item at the specified index
	 * @param iIndex index of item
	 * @return the removed item
	 */
	public T remove(int iIndex) {
		LinkNode<T>	myNode;
		
		/* Reject if the index is negative */
		if (iIndex < 0) throw new java.lang.IndexOutOfBoundsException();
		
		/* Access the node */
		myNode = theIndexMap.getNodeAtIndex(iIndex);
		
		/* Note if we did not find the item */
		if (myNode == null) throw new java.lang.IndexOutOfBoundsException();
		
		/* Remove the node */
		removeNode(myNode);
		
		/* Return the item */
		return myNode.getObject();
	}
	
	/**
	 * remove the specified item 
	 * @param o the item to remove
	 * @return <code>true/false</code> was the item removed
	 */
	public boolean remove(Object o) {
		LinkNode<T>	myNode;
		T			myItem;
		
		/* Reject if the object is null */
		if (o == null) throw new java.lang.NullPointerException();
		
		/* Reject if the object is invalid */
		if (!(theClass.isInstance(o))) throw new java.lang.ClassCastException();
		
		/* Access as link object */
		myItem = theClass.cast(o);
		
		/* Access the node of the item */
		myNode = myItem.getLinkNode(this);
		
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
	public int indexOf(Object o) {
		int 		iIndex = 0;
		LinkNode<T>	myNode;
		T			myItem;
		
		/* Reject if the object is null */
		if (o == null) throw new java.lang.NullPointerException();
		
		/* Reject if the object is invalid */
		if (!(theClass.isInstance(o))) throw new java.lang.ClassCastException();
		
		/* Access as link object */
		myItem = theClass.cast(o);
		
		/* Access the node of the item */
		myNode = myItem.getLinkNode(this);
		
		/* If the node does not belong to the list then ignore */
		if ((myNode == null) || (myNode.getList() != theList)) return -1;
			
		/* Access the index of the item */
		iIndex = myNode.getIndex(doSkipHidden);
		
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
	public boolean contains(Object o) {
		LinkNode<T>	myNode;
		T			myItem;
		
		/* Reject if the object is null */
		if (o == null) throw new java.lang.NullPointerException();
		
		/* Reject if the object is invalid */
		if (!(theClass.isInstance(o))) throw new java.lang.ClassCastException();
		
		/* Access as link object */
		myItem = theClass.cast(o);
		
		/* Access the node of the item */
		myNode = myItem.getLinkNode(this);
		
		/* If the node does not belong to the list then ignore */
		if ((myNode == null) || (myNode.getList() != theList)) return false;
			
		/* Return that the object belongs */
		return true;
	}
	
	/**
	 * Peek at the first item
	 * @return the first item or <code>null</code>
	 */
	protected T peekFirst() {
		LinkNode<T> myNode;
		
		/* Access the first item  */
		myNode = getFirst();
		
		/* Return the next object */
		return (myNode == null) ? null : myNode.getObject();
	}

	/**
	 * Peek at the first item
	 * @return the first item or <code>null</code>
	 */
	protected T peekLast() {
		LinkNode<T> myNode;
		
		/* Access the last item  */
		myNode = getLast();
		
		/* Return the next object */
		return (myNode == null) ? null : myNode.getObject();
	}

	/**
	 * Peek at the next item
	 * @param pItem the item from which to find the next item
	 * @return the next item or <code>null</code>
	 */
	public T peekNext(T pItem) {
		LinkNode<T> myNode;
		
		/* Reject if the object is null */
		if (pItem == null) throw new java.lang.NullPointerException();
		
		/* Access the node of the item */
		myNode = pItem.getLinkNode(this);
		
		/* If the node does not belong to the list then ignore */
		if ((myNode == null) || (myNode.getList() != theList)) return null;
			
		/* Access the next node */
		myNode = myNode.getNext(doSkipHidden);
		
		/* Return the next object */
		return (myNode == null) ? null : myNode.getObject();
	}

	/**
	 * Peek at the previous item
	 * @param pItem the item from which to find the previous item
	 * @return the previous item or <code>null</code>
	 */
	public T peekPrevious(T pItem) {
		LinkNode<T> myNode;
		
		/* Reject if the object is null */
		if (pItem == null) throw new java.lang.NullPointerException();
		
		/* Access the node of the item */
		myNode = pItem.getLinkNode(this);
		
		/* If the node does not belong to the list then ignore */
		if ((myNode == null) || (myNode.getList() != theList)) return null;
			
		/* Access the previous node */
		myNode = myNode.getPrev(doSkipHidden);
		
		/* Return the previous object */
		return (myNode == null) ? null : myNode.getObject();
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
		
		/* Reject if the sample array is null or wrong type */
		if (a == null) throw new java.lang.NullPointerException();
		Class<?> myClass = a[0].getClass();
		if (!myClass.isAssignableFrom(theClass))
			throw new java.lang.ArrayStoreException();
		
		/* Allocate an array list of the estimated size */
		myList = new java.util.ArrayList<X>(iSize);
		
		/* Loop through the list */
		for(Object myObj : toArray()) {
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
	private LinkNode<T> getFirst() {
		LinkNode<T> myFirst;
		
		/* Get the first item */
		myFirst = theFirst;

		/* Skip to next visible item if required */
		if ((myFirst != null) && 
			(myFirst.isHidden()) &&
			(doSkipHidden)) 
			myFirst = myFirst.getNext(true);
		
		/* Return to caller */
		return myFirst; 
	}
	
	/**
	 * Get the last node in the sequence
	 * @return the Last visible node
	 */
	private LinkNode<T> getLast() {
		LinkNode<T> myLast;
		
		/* Get the last item */
		myLast = theLast;
		
		/* Skip to previous visible item if required */
		if ((myLast != null) && 
			(myLast.isHidden()) &&
			(doSkipHidden)) 
			myLast = myLast.getPrev(true);
		
		/* Return to caller */
		return myLast; 
	}
	
	/**
	 * ListIterator class for this list
	 */
	public class ListIterator implements java.util.ListIterator<T> {
		/**
		 * Last node accessed 
		 */
		private LinkNode<T>			theNodeBefore 	= null;
		
		/**
		 * Last node accessed 
		 */
		private LinkNode<T>			theNodeAfter	= null;
		
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
			LinkNode<T> myNext;
			
			/* Access the next node */
			myNext = (theNodeBefore != null) 
							? theNodeBefore.getNext(!showAll)
					   		: ((showAll) ? theFirst 
					   					 : getFirst());
			
			/* Return whether we have a next node */
			return (myNext != null);
		}

		/**
		 * Does the list have a previous item
		 * @return <code>true/false</code>
		 */
		public boolean hasPrevious() {
			LinkNode<T> myPrev;
			
			/* Access the next node */
			myPrev = (theNodeAfter != null) 
							? theNodeAfter.getPrev(!showAll)
							: ((showAll) ? theLast 
										 : getLast());
			
			/* Return whether we have a previous node */
			return (myPrev != null);
		}

		/**
		 * Peek at the next item
		 * @return the next item or <code>null</code>
		 */
		public T peekNext() {
			LinkNode<T> myNext;
			
			/* Access the next node */
			myNext = (theNodeBefore != null) 
							? theNodeBefore.getNext(!showAll)
					   		: ((showAll) ? theFirst 
					   					 : getFirst());
			
			/* Return the next object */
			return (myNext == null) ? null : myNext.getObject();
		}

		/**
		 * Peek at the previous item
		 * @return the previous item or <code>null</code>
		 */
		public T peekPrevious() {
			LinkNode<T> myPrev;
			
			/* Access the next node */
			myPrev = (theNodeAfter != null) 
							? theNodeAfter.getPrev(!showAll)
							: ((showAll) ? theLast 
										 : getLast());
			
			/* Return the previous object */
			return (myPrev == null) ? null : myPrev.getObject();
		}

		/**
		 * Peek at the first item
		 * @return the first item or <code>null</code>
		 */
		public T peekFirst() {
			LinkNode<T> myFirst;
			
			/* Access the first node */
			myFirst = ((showAll) ? theFirst : getFirst());
			
			/* Return the next object */
			return (myFirst == null) ? null : myFirst.getObject();
		}

		/**
		 * Peek at the last item
		 * @return the last item or <code>null</code>
		 */
		public T peekLast() {
			LinkNode<T> myLast;
			
			/* Access the last node */
			myLast = ((showAll) ? theLast : getLast());
			
			/* Return the previous object */
			return (myLast == null) ? null : myLast.getObject();
		}

		/**
		 * Access the next item
		 * @return the next item (or null if there is no next item)
		 */
		public T next() {
			LinkNode<T> myNext;
			
			/* Access the next node */
			myNext = (theNodeBefore != null) 
							? theNodeBefore.getNext(!showAll)
					   		: ((showAll) ? theFirst 
					   					 : getFirst());											 
	
			/* If we have a next then move the cursor */
			if (myNext != null) {
				/* Record the cursor */
				theNodeBefore 	= myNext;
				theNodeAfter  	= myNext.getNext(false);
				wasForward	  	= true;
				canRemove		= true;
			}
				
			/* Return the next item */
			return (myNext != null) ? myNext.getObject() : null;
		}

		/**
		 * Access the previous item
		 * @return the previous item (or null if there is no previous item)
		 */
		public T previous() {
			LinkNode<T> myPrev;
			
			/* Access the previous node */
			myPrev = (theNodeAfter != null) 
							? theNodeAfter.getPrev(!showAll)
							: ((showAll) ? theLast 
										 : getLast());
	
			/* If we have a previous then move the cursor */
			if (myPrev != null) {
				/* Record the cursor */
				theNodeBefore 	= myPrev.getPrev(false);
				theNodeAfter  	= myPrev;
				wasForward	  	= false;
				canRemove		= true;
			}
				
			/* Return the previous item */
			return (myPrev != null) ? myPrev.getObject() : null;
		}
		
		/**
		 * Access the next index
		 * @return the next item index (or -1 if there is no next item)
		 */
		public int nextIndex() {
			LinkNode<T> myNext;
			int			iIndex = -1;
			
			/* Access the next node */
			myNext = (theNodeBefore != null) 
							? theNodeBefore.getNext(!showAll)
							: ((showAll) ? theFirst 
										 : getFirst());											 
	
			/* If we have a next then calculate its index */
			if (myNext != null) iIndex = myNext.getIndex(!showAll);
				
			/* Return the next item */
			return iIndex;
		}

		/**
		 * Access the previous index
		 * @return the previous item index (or -1 if there is no previous item)
		 */
		public int previousIndex() {
			LinkNode<T>	myPrev;
			int		 	iIndex = -1;
			
			/* Access the previous node */
			myPrev = (theNodeAfter != null) 
							? theNodeAfter.getPrev(!showAll)
							: ((showAll) ? theLast 
										 : getLast());
	
			/* If we have a previous then calculate its index */
			if (myPrev != null) iIndex = myPrev.getIndex(!showAll);
				
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
				theNodeBefore = (theNodeAfter != null) ? theNodeAfter.getPrev(false)
													   : theLast;
			}
			
			/* else the last operation was backwards */
			else {
				/* Remove the item */
				removeNode(theNodeAfter);
				
				/* Record the new node after */
				theNodeAfter = (theNodeBefore != null) ? theNodeBefore.getNext(false)
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
				myItem = theNodeBefore.getObject();
				
				/* Record the new node before */
				theNodeBefore = (theNodeAfter != null) ? theNodeAfter.getPrev(false)
													   : theLast;
			}
			
			/* else the last operation was backwards */
			else {
				/* Remove the item */
				myItem = theNodeAfter.getObject();
				removeNode(theNodeAfter);
				
				/* Record the new node after */
				theNodeAfter = (theNodeBefore != null) ? theNodeBefore.getNext(false)
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
		private LinkNode<T>[]		theMap 			= null;
		
		/**
		 * The length of the map 
		 */
		private int					theMapLength	= 0;
		
		/**
		 * Obtain the node at the specified index
		 * @param iIndex the index of the node
		 * @return the relevant node (or null)
		 */
		private LinkNode<T> getNodeAtIndex(int iIndex) {
			int 		iMapIndex;
			LinkNode<T> myNode;
			
			/* Calculate the map index */
			iMapIndex = iIndex / theGranularity;
			
			/* Handle out of range */
			if (iMapIndex > theMapLength-1) return null;
			
			/* Access the start node for the search */
			myNode = theMap[iMapIndex];
			
			/* Search for the correct node */
			while (myNode != null) {
				/* Break if we have found the node */
				if (myNode.getIndex(doSkipHidden) == iIndex) break;
				
				/* Shift to next node */
				myNode = myNode.getNext(doSkipHidden);
			}
			
			/* Return the correct node */
			return myNode;
		}

		/**
		 * Insert a map node
		 * @param pNode the node to insert
		 */
		@SuppressWarnings("unchecked")
		private void insertNode(LinkNode<T> pNode) {
			int			iIndex;
			int 		iMapIndex;
			LinkNode<T>	myNode;
			
			/* Access the index of the node */
			iIndex = pNode.getIndex(false);
			
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
					theMap = (LinkNode<T>[])new LinkNode[theExpansion];
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
				myNode = theMap[iMapIndex];
				
				/* Break if we have reached the end of the map */
				if (myNode == null) break;
				
				/* Shift the index to the previous item */
				theMap[iMapIndex] = myNode.getPrev(false);
			}
			
			/* If the last node has been shifted and needs storing, then store it */
			if ((pNode != theLast) && ((theLast.getIndex(false) % theGranularity) == 0))
				insertNode(theLast);
		}
		
		/**
		 * Remove a map node
		 * @param pNode the node to remove
		 */
		private void removeNode(LinkNode<T> pNode) {
			int			iIndex;
			int 		iMapIndex;
			LinkNode<T>	myNode;
			
			/* Access the index of the node */
			iIndex = pNode.getIndex(false);
			
			/* Calculate the map index */
			iMapIndex = iIndex / theGranularity;
			
			/* Ignore node if it is past end of map */
			if (iMapIndex > theMapLength-1) return;
				
			/* If this is a mapped node */
			if ((iIndex % theGranularity) == 0) {
				/* Adjust this node explicitly */
				theMap[iMapIndex] = pNode.getNext(false);
			}
			
			/* For all subsequent nodes */
			while (++iMapIndex < theMapLength) {
				/* Access the node in the map */
				myNode = theMap[iMapIndex];
				
				/* Break if we have reached the end of the map */
				if (myNode == null) break;
				
				/* Shift the index to the next item */
				theMap[iMapIndex] = myNode.getNext(false);
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

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
package uk.co.tolcroft.models;

import java.util.ListIterator;

public class SortedListIterator<T extends LinkObject<T>>  implements ListIterator<T> {
	/**
	 * Last node accessed 
	 */
	private SortedList<T>		theList			= null;
	
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
	 * Constructor for iterator that can show all elements 
	 * @param pList the list to build the iterator on
	 * @param bShowAll show all items in the list
	 */
	protected SortedListIterator(SortedList<T>	pList,
						   		 boolean 		bShowAll) {
		showAll = bShowAll;
		theList = pList;
	}
	
	/**
	 * Constructor for iterator at particular position
	 * @param pList the list to build the iterator on
	 * @param bShowAll show all items in the list
	 */
	protected SortedListIterator(SortedList<T>	pList,
	   		 					 LinkNode<T> 	pNode, 
	   		 					 boolean 		bShowAll) {
		/* Call standard constructor */
		this(pList, bShowAll);
		
		/* Record position */
		theNodeAfter  = pNode;
		theNodeBefore = pNode.getPrev(!showAll);
	}
	
	@Override
	public boolean hasNext() {
		LinkNode<T> myNext;
		
		/* Access the next node */
		myNext = (theNodeBefore != null) 
						? theNodeBefore.getNext(!showAll)
				   		: ((showAll) ? theList.getHead() 
				   					 : theList.getFirst());
		
		/* Return whether we have a next node */
		return (myNext != null);
	}

	@Override
	public boolean hasPrevious() {
		LinkNode<T> myPrev;
		
		/* Access the next node */
		myPrev = (theNodeAfter != null) 
						? theNodeAfter.getPrev(!showAll)
						: ((showAll) ? theList.getTail() 
									 : theList.getLast());
		
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
				   		: ((showAll) ? theList.getHead() 
				   					 : theList.getFirst());
		
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
						: ((showAll) ? theList.getTail()
									 : theList.getLast());
		
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
		myFirst = ((showAll) ? theList.getHead() : theList.getFirst());
		
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
		myLast = ((showAll) ? theList.getTail() : theList.getLast());
		
		/* Return the previous object */
		return (myLast == null) ? null : myLast.getObject();
	}

	@Override
	public T next() {
		LinkNode<T> myNext;
		
		/* Access the next node */
		myNext = (theNodeBefore != null) 
						? theNodeBefore.getNext(!showAll)
				   		: ((showAll) ? theList.getHead()
				   					 : theList.getFirst());											 

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

	@Override
	public T previous() {
		LinkNode<T> myPrev;
		
		/* Access the previous node */
		myPrev = (theNodeAfter != null) 
						? theNodeAfter.getPrev(!showAll)
						: ((showAll) ? theList.getTail() 
									 : theList.getLast());

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
	
	@Override
	public int nextIndex() {
		LinkNode<T> myNext;
		int			iIndex = -1;
		
		/* Access the next node */
		myNext = (theNodeBefore != null) 
						? theNodeBefore.getNext(!showAll)
						: ((showAll) ? theList.getHead()
									 : theList.getFirst());											 

		/* If we have a next then calculate its index */
		if (myNext != null) iIndex = myNext.getIndex(!showAll);
			
		/* Return the next item */
		return iIndex;
	}

	@Override
	public int previousIndex() {
		LinkNode<T>	myPrev;
		int		 	iIndex = -1;
		
		/* Access the previous node */
		myPrev = (theNodeAfter != null) 
						? theNodeAfter.getPrev(!showAll)
						: ((showAll) ? theList.getTail() 
									 : theList.getLast());

		/* If we have a previous then calculate its index */
		if (myPrev != null) iIndex = myPrev.getIndex(!showAll);
			
		/* Return the index */
		return iIndex;
	}

	/**
	 * Set the contents of the item. Disallowed.
	 * @param o object to set 
	 */
	@Override
	public void set(T o) {				
		/* Throw exception */
		throw new UnsupportedOperationException();
	}

	/**
	 * Add the item at this position. Disallowed.
	 * @param o object to add
	 */
	@Override
	public void add(T o) {				
		/* Throw exception */
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove() {				
		/* If we cannot remove the last item throw exception */
		if (!canRemove) throw new java.lang.IllegalStateException();
		
		/* If the last operation was forward */
		if (wasForward) {
			/* Remove the item */
			theList.removeNode(theNodeBefore);
			
			/* Record the new node before */
			theNodeBefore = (theNodeAfter != null) ? theNodeAfter.getPrev(false)
												   : theList.getTail();
		}
		
		/* else the last operation was backwards */
		else {
			/* Remove the item */
			theList.removeNode(theNodeAfter);
			
			/* Record the new node after */
			theNodeAfter = (theNodeBefore != null) ? theNodeBefore.getNext(false)
												   : theList.getHead();
		}
		
		/* Note that we can no longer remove the item */
		canRemove = false;
	}

	/**
	 * ReSort the last referenced item.
	 */
	public void reSort() {				
		/* If we cannot remove the last item throw exception */
		if (!canRemove) throw new java.lang.IllegalStateException();
		
		/* If the last operation was forward */
		if (wasForward) {
			/* Determine Node to remove */
			LinkNode<T> myNode = theNodeBefore;
			
			/* Remove the item */
			theList.removeNode(myNode);
			
			/* Record the new node before */
			theNodeBefore = (theNodeAfter != null) ? theNodeAfter.getPrev(false)
												   : theList.getTail();
			
			/* Re-insert the node */
			theList.insertNode(myNode);
		}
		
		/* else the last operation was backwards */
		else {
			/* Determine Node to remove */
			LinkNode<T> myNode = theNodeAfter;
			
			/* Remove the item */
			theList.removeNode(myNode);
			
			/* Record the new node after */
			theNodeAfter = (theNodeBefore != null) ? theNodeBefore.getNext(false)
												   : theList.getHead();

			/* Re-insert the node */
			theList.insertNode(myNode);
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
			theList.removeNode(theNodeBefore);
			myItem = theNodeBefore.getObject();
			
			/* Record the new node before */
			theNodeBefore = (theNodeAfter != null) ? theNodeAfter.getPrev(false)
												   : theList.getTail();
		}
		
		/* else the last operation was backwards */
		else {
			/* Remove the item */
			myItem = theNodeAfter.getObject();
			theList.removeNode(theNodeAfter);
			
			/* Record the new node after */
			theNodeAfter = (theNodeBefore != null) ? theNodeBefore.getNext(false)
												   : theList.getHead();
		}
		
		/* Note that we can no longer remove the item */
		canRemove = false;
		
		/* Return the deleted object */
		return myItem;
	}
}

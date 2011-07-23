package uk.co.tolcroft.models;

public class LinkNode<T extends LinkObject<T>> {
	/**
	 * The object that this node refers to 
	 */
	private T 				theObject 		= null;
	
	/**
	 * The object that this node refers to 
	 */
	private SortedList<T>	theList 		= null;
	
	/**
	 * Is the object hidden 
	 */
	private boolean			isHidden 		= false;
	
	/**
	 * The standard index of this item 
	 */
	protected int			theIndex 		= -1;
	
	/**
	 * The object that this node refers to 
	 */
	protected int			theHiddenIndex 	= -1;
	
	/**
	 * The next node in the sequence 
	 */
	protected LinkNode<T>	theNext 		= null;
	
	/**
	 * The previous node in the sequence 
	 */
	protected LinkNode<T>	thePrev 		= null;
	
	/**
	 * Initialiser
	 */
	protected LinkNode(SortedList<T>	pList,
					   T 				pObject) {
		theList	  = pList;
		theObject = pObject;
		isHidden  = pObject.isHidden();
	}
	
	/**
	 * Is the Node hidden
	 * @return <code>true/false</code>
	 */
	protected boolean isHidden() { return isHidden; }
	
	/**
	 * Get object
	 * @return <code>true/false</code>
	 */
	protected T		getObject() { return theObject; }
	
	/**
	 * Set Hidden flag
	 * @param isHidden <code>true/false</code>
	 */
	protected void	setHidden(boolean isHidden) { 
		this.isHidden = isHidden;
	}
	
	/**
	 * Get the next node in the sequence
	 * @param doSkipHidden skip hidden items
	 * @return the Next visible node
	 */
	protected LinkNode<T> getNext(boolean doSkipHidden) {
		LinkNode<T> myNext;
		
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
	 * @param doSkipHidden skip hidden items
	 * @return the previous visible node
	 */
	protected LinkNode<T> getPrev(boolean doSkipHidden) { 
		LinkNode<T> myPrev;
		
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
	 * @param doSkipHidden skip hidden items
	 * @return the relevant index of the item
	 */
	protected int getIndex(boolean doSkipHidden) { 			
		/* Return the relevant index */
		return (doSkipHidden) ? theHiddenIndex : theIndex; 
	}

	/**
	 * Get the list to which this node belongs
	 * @return the holding list
	 */
	protected SortedList<T> getList() { 			
		/* Return the list */
		return theList; 
	}

	/**
	 * Compare this node to another
	 * @param pThat the node to compare to
	 * @return (-1,0,1) depending on order
	 */
	protected int compareTo(LinkNode<T> pThat) { 
		return theObject.compareTo(pThat.theObject); }
}

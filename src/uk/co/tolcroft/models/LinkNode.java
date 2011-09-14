package uk.co.tolcroft.models;

public class LinkNode<T extends LinkObject<T>> {
	/**
	 * The object that this node refers to 
	 */
	private T 				theObject 		= null;
	
	/**
	 * The list that this node belongs to 
	 */
	private SortedList<T>	theList 		= null;
	
	/**
	 * Is the object hidden 
	 */
	private boolean			isHidden 		= false;
	
	/**
	 * The standard index of this item 
	 */
	private int				theIndex 		= -1;
	
	/**
	 * The hidden index of this item 
	 */
	private int				theHiddenIndex 	= -1;
	
	/**
	 * The next node in the sequence 
	 */
	private LinkNode<T>		theNext 		= null;
	
	/**
	 * The previous node in the sequence 
	 */
	private LinkNode<T>		thePrev 		= null;
	
	/**
	 * Is the Node hidden
	 * @return <code>true/false</code>
	 */
	public boolean 			isHidden() 	{ return isHidden; }
	
	/**
	 * Get object
	 * @return <code>true/false</code>
	 */
	protected T				getObject() { return theObject; }
	
	/**
	 * Initialiser
	 */
	protected LinkNode(SortedList<T>	pList,
					   T 				pObject) {
		theList	  = pList;
		theObject = pObject;
	}
	
	/**
	 * Set Hidden flag
	 * @param isHidden <code>true/false</code>
	 */
	protected void	setHidden(boolean isHidden) {
		LinkNode<T>	myNode = this;

		/* Record the hidden flag */
		this.isHidden = isHidden;

		/* Determine adjustment factor */
		int iAdjust = (isHidden) ? -1 : 1;
		
		/* Loop through nodes */
		while (myNode != null) {
			/* Adjust hidden index */
			myNode.theHiddenIndex += iAdjust;
			
			/* Shift to next node */
			myNode = myNode.theNext;
		}			
	}
	
	/**
	 *  add Node to the list searching from the start
	 *  @param pFirst - first node in list (or null)
	 *  @param pLast - last node in list (or null)
	 */
	protected void addFromStart(LinkNode<T> pFirst,
								LinkNode<T> pLast) {
		LinkNode<T>	myCurr;
		boolean 	isVisible;
		
		/* Determine whether this item is hidden */
		isVisible = !isHidden;
		   
	   	/* Loop through the current items */
	    for(myCurr = pFirst;
	        myCurr != null;
	        myCurr = myCurr.theNext)
		{
		   	/* Break if we have found an element that should be later */
		   	if (myCurr.compareTo(this) >= 0) break;
		}
		       
		/* If we found an insert point */
		if (myCurr != null) {
		   	/* Set values for the new item */
		    thePrev = myCurr.thePrev;
		    theNext = myCurr;
		
		    /* Copy Indices from insert point */
		    theIndex 		= myCurr.theIndex;
		    theHiddenIndex 	= myCurr.theHiddenIndex;
		    
		    /* If hidden status differs, adjust hidden index */
		    if (myCurr.isHidden == isVisible)
		    	theHiddenIndex += (isVisible) ? 1 : -1;
		    
		    /* Add to the list */
		    myCurr.thePrev = this;
		    if (thePrev != null) thePrev.theNext = this;
		    
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
		   	thePrev = pLast;
		   	theNext = null;
		    	
		   	/* If this is the first item */
		   	if (pLast == null) {
		   		/* Set new indices */
		   		theIndex 		= 0;
		   		theHiddenIndex 	= (isVisible) ? 0 : -1;
		   	}
		   	
		   	/* else we have a previous item */
		   	else {
		   		/* Set new indices */
		   		theIndex 		= thePrev.theIndex+1;
		   		theHiddenIndex 	= (isVisible) ? thePrev.theHiddenIndex+1
		   									  : thePrev.theHiddenIndex;
		    
		   		/* Add to the list */
		   		thePrev.theNext = this;
		   	}
		}
	}
		
	/**
	 *  add Node to the list searching from the end
	 *  @param pFirst - first node in list (or null)
	 *  @param pLast - last node in list (or null)
	 */
	protected void addFromEnd(LinkNode<T> pFirst,
							  LinkNode<T> pLast) {
		LinkNode<T>	myCurr;		   
		boolean 	isVisible;
		
		/* Determine whether this item is hidden */
		isVisible = !isHidden;
		   
	   	/* Loop backwards through the current items */
	    for(myCurr = pLast;
	        myCurr != null;
	        myCurr = myCurr.thePrev)
	    {
	    	/* Break if we have found an element that should be earlier */
	    	if (myCurr.compareTo(this) <= 0) break;
	    }
	       
	    /* If we found an insert point */
	    if (myCurr != null) {
	    	/* Set values for the new item */
	        theNext = myCurr.theNext;
	        thePrev = myCurr;
		    	    
		    /* Set new indices from insert point */
		    theIndex 		= myCurr.theIndex+1;
		    theHiddenIndex 	= myCurr.theHiddenIndex+1;
		    if (!isVisible) theHiddenIndex--;
		    
	        /* Add to the list */
	        myCurr.theNext = this;
	    }
		       	
	    /* else we need to add to the beginning of the list */
	    else {
	   		/* Set values for the new item */
	   		theNext = pFirst;
	   		thePrev = null;
    	
	   		/* Set new indices */
	   		theIndex 		= 0;
	   		theHiddenIndex 	= (!isVisible) ? -1 : 0;
	    }
	    
	    /* Adjust the following link */
        if (theNext != null) theNext.thePrev = this;
        
        /* Loop through subsequent elements increasing the indices */
        myCurr = theNext;
	    while (myCurr != null) {
	    	/* Increment indices */
	    	myCurr.theIndex++;
	    	if (isVisible) myCurr.theHiddenIndex++;
	    	myCurr = myCurr.theNext;
	    }
	}
		
	/**
	 * Remove node from list
	 */
	protected void	remove() {
		LinkNode<T>	myCurr;
		boolean 	isVisible; 
		
		/* Determine whether this item is visible */
		isVisible = !isHidden;
		   
		/* Adjust nodes either side of this node */
		if (thePrev != null) thePrev.theNext = theNext;
		if (theNext != null) theNext.thePrev = thePrev;
				    
        /* Loop through subsequent elements decreasing the indices */
        myCurr = theNext;
	    while (myCurr != null) {
	    	/* Decrement indices */
	    	myCurr.theIndex--;
	    	if (isVisible) myCurr.theHiddenIndex--;
	    	myCurr = myCurr.theNext;
	    }

	    /* clean our links */
		theNext = null;
		thePrev = null;
	}
	
	/**
	 * Get the next node in the sequence
	 * @param doSkipHidden skip hidden items
	 * @return the Next visible node
	 */
	protected LinkNode<T> getNext(boolean doSkipHidden) {
		LinkNode<T> myNext;
		
		/* Access the next item */
		myNext = theNext;
		
		/* If we should skip hidden items */
		if (doSkipHidden) 
			/* Loop skipping hidden items */
			while ((myNext != null) && 
				   (myNext.isHidden))
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
		
		/* Access the previous item */
		myPrev = thePrev;

		/* If we should skip hidden items */
		if (doSkipHidden) 
			/* Loop skipping hidden items */
			while ((myPrev != null) && 
				   (myPrev.isHidden))
				myPrev = myPrev.thePrev;
		
		/* Return to caller */
		return myPrev; 
	}

	/**
	 * Get the index of the item
	 * @param doSkipHidden skip hidden items
	 * @return the relevant index of the item
	 */
	public int getIndex(boolean doSkipHidden) { 			
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

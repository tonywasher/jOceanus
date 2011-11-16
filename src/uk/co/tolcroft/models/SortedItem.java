package uk.co.tolcroft.models;

public abstract class SortedItem<T extends SortedItem<T>> implements LinkObject<T> {
	/**
	 * The list to which this item belongs
	 */
	private SortedList<T>	theList		= null;
	
    /**
	 * Storage for the List Node
	 */
    private LinkNode<T>		theLink		= null;

	/**
	 * Get the list control for this item
	 * @return the list control
	 */
	public SortedList<T>   	getList()  		{ return theList; }
	
	/**
	 * Get the link node for this item
	 * @return the Link node or <code>null</code>
	 */
	public LinkNode<T>		getLinkNode(SortedList<T> pList)	{ return theLink; }

	/**
	 * Get the link node for this item
	 * @return the Link node or <code>null</code>
	 */
	public void				setLinkNode(SortedList<T> l, LinkNode<T> o)	{ theLink = o; }

	/**
	 * Determine whether the item is Hidden
	 * @return <Scode>true/false</code>
	 */
	public boolean			isHidden()	{ return false; }

	/**
	 * Construct a new item
	 * @param pCtl the list that this item is associated with
	 * @param uId the Id of the new item (or 0 if not yet known)
	 */
	protected SortedItem(SortedList<T> pList) {
		theList    = pList;
	}
	
	/**
	 * Add Link details to Debug output
	 * @param pBuffer the string buffer 
	 */
	protected void 			addLinkDebug(StringBuilder pBuffer) {
		/* If we are part of a list */
		if (theLink != null) {
			/* Start the status section */
			pBuffer.append("<tr><th rowspan=\"");
			pBuffer.append((theLink.isHidden()) ? 4 : 3);
			pBuffer.append("\">Indices</th></tr>");
			
			/* Format the Indexes */
			pBuffer.append("<tr><td>Index</td><td>");
			pBuffer.append(theLink.getIndex(false));
			pBuffer.append("</td></tr>");
			pBuffer.append("<tr><td>HiddenIndex</td><td>");
			pBuffer.append(theLink.getIndex(true));
			pBuffer.append("</td></tr>");
			if (theLink.isHidden()) 
				pBuffer.append("<tr><td>Hidden</td><td>true</td></tr>");
		}
	}
}

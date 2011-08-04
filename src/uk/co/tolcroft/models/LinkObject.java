package uk.co.tolcroft.models;

public interface LinkObject<T extends LinkObject<T>> extends java.lang.Comparable<Object> {
	/**
	 * Determine whether the item is Hidden
	 * @return <Scode>true/false</code>
	 */
	public boolean		isHidden();

	/**
	 * Set the linkNode for a list
	 * @param pList the list
	 * @param pNode the node
	 */
	public void 		setLinkNode(SortedList<T> pList, LinkNode<T> pNode);

	/**
	 * Get the linkNode for a List
	 * @param pList the list
	 * @return the Link node
	 */
	public LinkNode<T>	getLinkNode(SortedList<T> pList);
}

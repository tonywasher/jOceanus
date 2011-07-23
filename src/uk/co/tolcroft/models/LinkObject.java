package uk.co.tolcroft.models;

public abstract class LinkObject<T extends LinkObject<T>> implements java.lang.Comparable<Object> {
	/**
	 * Determine whether the item is Hidden
	 * @return <Scode>true/false</code>
	 */
	public abstract boolean	isHidden();

	/**
	 * Set the linkNode for a list
	 * @param pList the list
	 * @param pNode the node
	 */
	public abstract void 	setLinkNode(SortedList<T> pList, LinkNode<T> pNode);

	/**
	 * Get the linkNode for a List
	 * @param pList the list
	 * @return the Link node
	 */
	public abstract LinkNode<T>	getLinkNode(SortedList<T> pList);
}

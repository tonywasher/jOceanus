package uk.co.tolcroft.models;

/**
 * Interface for checking whether history objects are valid in a table view
 */
public interface HistoryCheck<T extends DataItem<T>> {
	/**
	 * Is this object valid for this item in the table view
	 * @param pItem the item to check
	 * @param pObj the history values to check
	 * @return <code>true</code> if the history object is valid <code>false</code> otherwise
	 */
	boolean    isValidHistory(DataItem<T> pItem, HistoryValues<?> pValues);
}

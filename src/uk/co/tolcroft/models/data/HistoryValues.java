package uk.co.tolcroft.models.data;

import uk.co.tolcroft.models.Difference;

public abstract class HistoryValues<T extends DataItem<T>> {
	/**
	 * Is this object identical to the comparison object
	 * @param pCompare the comparison object
	 * @return Difference details of the two values
	 */
	protected abstract Difference	histEquals(HistoryValues<T> pCompare);
	
	/**
	 * Initialises the object with values from another (possibly different type) object. 
	 * @param pSource the object to copy values from
	 */
	protected abstract void		copyFrom(HistoryValues<?> pSource);
	
	/**
	 * Provides a cloned object of its own values
	 * @return the cloned object
	 */
	protected abstract HistoryValues<T> copySelf();
	
	/**
	 * Determines whether the indicated field has changed from the original
	 * @param fieldNo the field to check
	 * @param pOriginal the original values
	 * @return Difference details of the field
	 */
	public abstract Difference	fieldChanged(int fieldNo, HistoryValues<T> pOriginal);
}

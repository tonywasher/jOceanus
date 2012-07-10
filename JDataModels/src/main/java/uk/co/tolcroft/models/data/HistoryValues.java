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
package uk.co.tolcroft.models.data;

import uk.co.tolcroft.models.Difference;

public abstract class HistoryValues<T extends DataItem<T>> {
	/**
	 * Version # of the values
	 */
	private int		theVersion	= 0;
	
	/**
	 * Is this object a record of a deletion event
	 */
	private boolean	isDeletion	= false;
	
	/**
	 * Obtain the version # of the values
	 * @return the version #
	 */
	protected int 		getVersion()	{ return theVersion; }
	
	/**
	 * Set the version # of the values
	 * @param pVersion the version #
	 */
	protected void 		setVersion(int pVersion)	{ theVersion = pVersion; }
	
	/**
	 * Determine if this object is a record of a deletion event
	 * @return true/false
	 */
	protected boolean 	isDeletion()	{ return isDeletion; }
	
	/**
	 * Set deletion indication
	 * @return true/false
	 */
	protected void 		setDeletion()	{ isDeletion = true; }
	
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

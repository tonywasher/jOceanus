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


/**
 * Interface for checking whether history objects are valid in a table view
 */
public interface HistoryCheck<T extends DataItem<T>> {
	/**
	 * Is this object valid for this item in the table view
	 * @param pItem the item to check
	 * @param pValues the history values to check
	 * @return <code>true</code> if the history object is valid <code>false</code> otherwise
	 */
	boolean    isValidHistory(DataItem<T> pItem, HistoryValues<?> pValues);
}

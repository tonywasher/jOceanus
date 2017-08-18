/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.data;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataDeletable;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataList;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataUpdatable;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataValues;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldSetItem;
import net.sourceforge.joceanus.jmetis.lethe.list.MetisOrderedIdItem;

/**
 * Table Item interface.
 */
public interface PrometheusTableItem extends MetisOrderedIdItem<Integer>, MetisDataValues, MetisFieldSetItem, MetisDataDeletable, MetisDataUpdatable {
    /**
     * Is this a header?
     * @return true/false
     */
    default boolean isHeader() {
        return false;
    }

    /**
     * Table List interface.
     * @param <T> the item type
     */
    interface PrometheusTableList<T extends PrometheusTableItem> extends MetisDataList<T> {
        /**
         * Is the list is Locked.
         * @return <code>true/false</code>
         */
        default boolean isLocked() {
            return false;
        }

        /**
         * Obtain the class of objects in this sorted.
         * @return the class
         */
        Class<T> getBaseClass();

        /**
         * Obtain the item at the specified position in the list.
         * @param pIndex the index of the item
         * @return the item
         */
        default T get(final int pIndex) {
            return getUnderlyingList().get(pIndex);
        }

        /**
         * Remove the item from the list.
         * @param pItem the item to remove
         * @return was the item originally in the list?
         */
        default boolean remove(final Object pItem) {
            return getUnderlyingList().remove(pItem);
        }

        /**
         * Obtain the index of the item in the list.
         * @param pItem the item to find
         * @return the index or -1 if the item is not in the list
         */
        default int indexOf(final Object pItem) {
            return getUnderlyingList().indexOf(pItem);
        }
    }
}

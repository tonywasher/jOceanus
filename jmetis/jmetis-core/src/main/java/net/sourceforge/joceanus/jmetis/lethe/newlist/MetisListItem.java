/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.newlist;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisItemValidation;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;

/**
 * Item interfaces.
 */
public final class MetisListItem {
    /**
     * Private constructor.
     */
    private MetisListItem() {
    }

    /**
     * Interface for items that wish to belong to a MetisIndexedList.
     */
    public interface MetisIndexedItem
            extends MetisDataContents {
        /**
         * Get the Id to index the list.
         * @return the Id
         */
        Integer getIndexedId();
    }

    /**
     * Interface for items that wish to control disabled items.
     */
    @FunctionalInterface
    public interface MetisDisableItem {
        /**
         * Determine whether the item is disabled.
         * @return true/false
         */
        boolean isDisabled();
    }

    /**
     * Interface for items that publicise error details.
     */
    @FunctionalInterface
    public interface MetisValidateItem {
        /**
         * Obtain the item validation.
         * @return the validation
         */
        MetisItemValidation getValidation();
    }
}

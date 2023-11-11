/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.list;

import java.util.Collections;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldItemType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;

/**
 * List Item Type.
 */
public interface MetisListKey
        extends MetisFieldItemType {
    /**
     * Obtain the item id.
     * @return the item id
     */
    Integer getItemId();

    /**
     * Obtain the list name.
     * @return the list name
     */
    String getListName();

    /**
     * Do the list items reference another list?
     * @return true/false
     */
    default boolean hasReferences() {
        return false;
    }

    /**
     * Obtain the nameSpace listKey (if any).
     * @return the listKey for the nameSpace or null
     */
    default MetisListKey getNameSpace() {
        return null;
    }

    /**
     * Obtain the list of Unique fieldIds (if any).
     * @return the list of fieldIds
     */
    default List<MetisDataFieldId> getUniqueFields() {
        return Collections.emptyList();
    }

    /**
     * Obtain the list of Singular fieldIds (if any).
     * @return the list of fieldIds
     */
    default List<MetisDataFieldId> getSingularFields() {
        return Collections.emptyList();
    }

    /**
     * Create a new item for the list.
     * @param <T> the item type
     * @param pListSet the listSet
     * @return the new item
     */
    <T extends MetisFieldVersionedItem> T newItem(MetisListSetVersioned pListSet);
}

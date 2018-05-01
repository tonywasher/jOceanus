/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
     * Create a new item for the list.
     * @param <T> the item type
     * @param pListSet the listSet
     * @return the new item
     */
    <T extends MetisFieldVersionedItem> T newItem(MetisListSetVersioned pListSet);
}

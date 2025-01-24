/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.metis.list;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldDef;

/**
 * A set of values for an item.
 */
public class MetisListItemValues {
    /**
     * The item key.
     */
    private final MetisListKey theKey;

    /**
     * Field Values.
     */
    private final Map<MetisFieldDef, Object> theValues;

    /**
     * Constructor.
     * @param pKey the itemKey
     */
    public MetisListItemValues(final MetisListKey pKey) {
        theKey = pKey;
        theValues = new HashMap<>();
    }

    /**
     * Obtain ListKey.
     * @return the listKey
     */
    public final MetisListKey getListKey() {
        return theKey;
    }

    /**
     * Add value.
     * @param pField the Field definition
     * @param pValue the field value
     */
    public void addValue(final MetisFieldDef pField,
                         final Object pValue) {
        /* If the value is non-null */
        if (pValue != null) {
            /* Add the field */
            theValues.put(pField, pValue);
        }
    }

    /**
     * Obtain value.
     * @param pField the Field definition
     * @return the field value
     */
    public Object getValue(final MetisFieldDef pField) {
        /* Return the field */
        return theValues.get(pField);
    }

    /**
     * Obtain Value iterator.
     * @return the Field iterator
     */
    public final Iterator<Entry<MetisFieldDef, Object>> fieldIterator() {
        return theValues.entrySet().iterator();
    }
}

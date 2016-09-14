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
package net.sourceforge.joceanus.jmetis.ui.javafx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.newlist.MetisVersionedItem;

/**
 * Table List fields.
 * @param <R> the item type
 */
public class MetisFXTableListFields<R extends MetisVersionedItem> {
    /**
     * The field list.
     */
    private final List<MetisField> theFields;

    /**
     * The fieldSet Map.
     */
    private final Map<Integer, MetisFXTableFieldSet<R>> theIdMap;

    /**
     * Constructor.
     * @param pFields the fields
     */
    MetisFXTableListFields(final List<MetisField> pFields) {
        theFields = pFields;
        theIdMap = new HashMap<>();
    }

    /**
     * Obtain the field set for the item.
     * @param pItem the item
     * @return the fieldSet
     */
    private MetisFXTableFieldSet<R> getFieldSet(final R pItem) {
        Integer myId = pItem.getIndexedId();
        MetisFXTableFieldSet<R> myFieldSet = theIdMap.get(myId);
        if (myFieldSet == null) {
            myFieldSet = new MetisFXTableFieldSet<>(pItem, theFields);
            theIdMap.put(myId, myFieldSet);
        }
        return myFieldSet;
    }

    /**
     * Obtain the comparisons array for an item.
     * @param pItem the item
     * @return the array
     */
    protected ObjectProperty<Object>[] getComparisons(final R pItem) {
        MetisFXTableFieldSet<R> myFieldSet = getFieldSet(pItem);
        return myFieldSet.getComparisons();
    }

    /**
     * Obtain the ObjectProperty for an item and field.
     * @param <T> the property type
     * @param pItem the item
     * @param pId the field id
     * @return the array
     */
    @SuppressWarnings("unchecked")
    protected <T> ObjectProperty<T> getObjectProperty(final R pItem,
                                                      final MetisField pId) {
        MetisFXTableFieldSet<R> myFieldSet = getFieldSet(pItem);
        return (ObjectProperty<T>) myFieldSet.getPropertyForField(pId);
    }

    /**
     * Populate properties for an item.
     * @param pItem the item
     */
    protected void populateProperties(final R pItem) {
        MetisFXTableFieldSet<R> myFieldSet = getFieldSet(pItem);
        myFieldSet.populateValues();
    }

    /**
     * Remove item.
     * @param pItem the item
     */
    protected void removeItem(final R pItem) {
        theIdMap.remove(pItem.getIndexedId());
    }
}
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
import java.util.Iterator;
import java.util.Map;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisVersionedList;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisListItem.MetisIndexedItem;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.ui.MetisTableCalculator;

/**
 * Table List fields.
 * @param <R> the item type
 */
public class MetisFXTableListFields<R extends MetisIndexedItem> {
    /**
     * The field list.
     */
    private final MetisFields theFields;

    /**
     * The fieldSet Map.
     */
    private final Map<Integer, MetisFXTableFieldSet<R>> theIdMap;

    /**
     * Table Calculator.
     */
    private MetisTableCalculator<R> theCalculator;

    /**
     * Constructor.
     * @param pList the editList
     */
    public MetisFXTableListFields(final MetisVersionedList<R> pList) {
        theFields = pList.getItemFields();
        theIdMap = new HashMap<>();
    }

    /**
     * Obtain the fields.
     * @return the fields
     */
    protected MetisFields getFields() {
        return theFields;
    }

    /**
     * Obtain the calculator.
     * @return the calculator
     */
    protected MetisTableCalculator<R> getCalculator() {
        return theCalculator;
    }

    /**
     * Set the table calculator.
     * @param pCalculator the calculator
     */
    protected void setCalculator(final MetisTableCalculator<R> pCalculator) {
        theCalculator = pCalculator;
        recalculateValues();
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
            myFieldSet = new MetisFXTableFieldSet<>(pItem, this);
            theIdMap.put(myId, myFieldSet);
        }
        return myFieldSet;
    }

    /**
     * Obtain the comparisons array for an item.
     * @param pItem the item
     * @return the array
     */
    protected Observable[] getComparisons(final R pItem) {
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
     * Update properties for an item.
     * @param pItem the item
     */
    protected void updateProperties(final R pItem) {
        MetisFXTableFieldSet<R> myFieldSet = getFieldSet(pItem);
        myFieldSet.updateValues();
    }

    /**
     * Clear the map.
     */
    protected void clear() {
        theIdMap.clear();
    }

    /**
     * Remove item by id.
     * @param pId the iD
     * @return the item that was removed
     */
    protected R removeItem(final Integer pId) {
        /* Obtain the field set */
        MetisFXTableFieldSet<R> myFieldSet = theIdMap.get(pId);
        if (myFieldSet != null) {
            /* Remove from the map and return the item */
            theIdMap.remove(pId);
            return myFieldSet.getItem();
        }

        /* No item found */
        return null;
    }

    /**
     * Do we have comparisons?
     * @return true/false
     */
    protected boolean hasComparisons() {
        return theFields.hasComparisons();
    }

    /**
     * ReCalculate the values.
     */
    private void recalculateValues() {
        /* Iterate through the fieldSets */
        Iterator<MetisFXTableFieldSet<R>> myIterator = theIdMap.values().iterator();
        while (myIterator.hasNext()) {
            MetisFXTableFieldSet<R> myFieldSet = myIterator.next();

            /* Recalculate values */
            myFieldSet.recalculateValues();
        }
    }
}

/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.atlas.ui.javafx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataTableItem;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisIndexedList;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableCalculator;

/**
 * Table List fields.
 * @param <R> the item type
 */
public class MetisFXTableListFields<R extends MetisDataTableItem> {
    /**
     * The fieldSet.
     */
    private final List<MetisDataField> theFields;

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
    public MetisFXTableListFields(final MetisIndexedList<R> pList) {
        theFields = new ArrayList<>();
        theIdMap = new HashMap<>();
    }

    /**
     * Obtain the fields.
     * @return the fields
     */
    protected List<MetisDataField> getFields() {
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
     * Declare field.
     * @param pField the field
     */
    protected void declareField(final MetisDataField pField) {
        theFields.add(pField);
    }

    /**
     * Obtain the field set for the item.
     * @param pItem the item
     * @return the fieldSet
     */
    private MetisFXTableFieldSet<R> getFieldSet(final R pItem) {
        final Integer myId = pItem.getIndexedId();
        MetisFXTableFieldSet<R> myFieldSet = theIdMap.get(myId);
        if (myFieldSet == null) {
            myFieldSet = new MetisFXTableFieldSet<>(pItem, this);
            theIdMap.put(myId, myFieldSet);
        }
        return myFieldSet;
    }

    /**
     * Obtain the observable array for an item.
     * @param pItem the item
     * @return the array
     */
    protected Observable[] getObservables(final R pItem) {
        final MetisFXTableFieldSet<R> myFieldSet = getFieldSet(pItem);
        return myFieldSet.getObservables();
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
                                                      final MetisDataField pId) {
        final MetisFXTableFieldSet<R> myFieldSet = getFieldSet(pItem);
        return (ObjectProperty<T>) myFieldSet.getPropertyForField(pId);
    }

    /**
     * Update properties for an item.
     * @param pItem the item
     */
    protected void updateProperties(final R pItem) {
        final MetisFXTableFieldSet<R> myFieldSet = getFieldSet(pItem);
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
        final MetisFXTableFieldSet<R> myFieldSet = theIdMap.get(pId);
        if (myFieldSet != null) {
            /* Remove from the map and return the item */
            theIdMap.remove(pId);
            return myFieldSet.getItem();
        }

        /* No item found */
        return null;
    }

    /**
     * ReCalculate the values.
     */
    private void recalculateValues() {
        /* Iterate through the fieldSets */
        final Iterator<MetisFXTableFieldSet<R>> myIterator = theIdMap.values().iterator();
        while (myIterator.hasNext()) {
            final MetisFXTableFieldSet<R> myFieldSet = myIterator.next();

            /* Recalculate values */
            myFieldSet.recalculateValues();
        }
    }
}

/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.ui.javafx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableCalculator;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;

/**
 * Table List fields.
 * @param <R> the item type
 */
public class MetisFXTableListFields<R extends MetisFieldTableItem> {
    /**
     * The fieldSet.
     */
    private final List<MetisFieldDef> theFields;

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
    public MetisFXTableListFields(final MetisListIndexed<R> pList) {
        theFields = new ArrayList<>();
        theIdMap = new HashMap<>();
    }

    /**
     * Obtain the fields.
     * @return the fields
     */
    protected List<MetisFieldDef> getFields() {
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
    protected void declareField(final MetisFieldDef pField) {
        theFields.add(pField);
    }

    /**
     * Obtain the field set for the item.
     * @param pItem the item
     * @return the fieldSet
     */
    private MetisFXTableFieldSet<R> getFieldSet(final R pItem) {
        final Integer myId = pItem.getIndexedId();
        return theIdMap.computeIfAbsent(myId, i -> new MetisFXTableFieldSet<>(pItem, this));
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
     * @param pField the field
     * @return the array
     */
    @SuppressWarnings("unchecked")
    protected <T> ObjectProperty<T> getObjectProperty(final R pItem,
                                                      final MetisFieldDef pField) {
        final MetisFXTableFieldSet<R> myFieldSet = getFieldSet(pItem);
        return (ObjectProperty<T>) myFieldSet.getPropertyForField(pField);
    }

    /**
     * Update properties for an item.
     * @param pItem the item
     */
    protected void updateProperties(final R pItem) {
        /* If we have a fieldSet for this item */
        final MetisFXTableFieldSet<R> myFieldSet = theIdMap.get(pItem.getIndexedId());
        if (myFieldSet != null) {
            /* Update the values */
            myFieldSet.updateValues();
        }
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
     */
    protected void removeItem(final Integer pId) {
        theIdMap.remove(pId);
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

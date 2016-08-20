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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import net.sourceforge.joceanus.jmetis.data.MetisEncryptedData.MetisEncryptedField;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.newlist.MetisVersionedItem;

/**
 * Table FieldSet.
 * @param <R> the item type
 */
public class MetisFXTableFieldSet<R extends MetisVersionedItem> {
    /**
     * The Item.
     */
    private final R theItem;

    /**
     * The Map of Field ID to Observable Object.
     */
    private final Map<MetisField, ObjectProperty<Object>> thePropertyMap;

    /**
     * The Array of Comparison properties.
     */
    private final ObjectProperty<Object>[] theComparisons;

    /**
     * Constructor.
     * @param pItem the item
     * @param pFields the fields
     */
    protected MetisFXTableFieldSet(final R pItem,
                                   final List<MetisField> pFields) {
        /* Store the parameters */
        theItem = pItem;

        /* Create the map and populate it */
        thePropertyMap = new HashMap<>();
        theComparisons = populateMap(pFields);
        populateValues();
    }

    /**
     * Return the comparisons array.
     * @return the array
     */
    protected ObjectProperty<Object>[] getComparisons() {
        return theComparisons;
    }

    /**
     * Obtain the object property for the field.
     * @param pField the field
     * @return the property
     */
    protected ObjectProperty<Object> getPropertyForField(final MetisField pField) {
        return thePropertyMap.get(pField);
    }

    /**
     * Populate the map.
     * @param pFields the fields
     * @return the comparisons array
     */
    @SuppressWarnings("unchecked")
    private ObjectProperty<Object>[] populateMap(final List<MetisField> pFields) {
        /* Create the comparisons array */
        int myMax = theItem.getDataFields().getNumValues();
        ObjectProperty<Object>[] myComparisons = (ObjectProperty<Object>[]) new ObjectProperty<?>[myMax];
        int myNumCompares = 0;

        /* Iterate through the keys */
        Iterator<MetisField> myIterator = pFields.iterator();
        while (myIterator.hasNext()) {
            MetisField myField = myIterator.next();

            /* Create the property */
            ObjectProperty<Object> myProperty = new SimpleObjectProperty<>();
            thePropertyMap.put(myField, myProperty);

            /* If the field is a comparison field */
            if (myField.getEquality().isComparison()) {
                /* Add it to the comparisons array */
                myComparisons[myNumCompares++] = myProperty;
            }
        }

        /* If we have less than the max comparisons adjust the array */
        if (myNumCompares == 0) {
            myComparisons = null;
        } else if (myNumCompares < myMax) {
            myComparisons = Arrays.copyOf(myComparisons, myNumCompares);
        }

        /* Return the comparison array */
        return myComparisons;
    }

    /**
     * Populate the values.
     */
    protected void populateValues() {
        /* Iterate through the entries */
        Iterator<Map.Entry<MetisField, ObjectProperty<Object>>> myIterator = thePropertyMap.entrySet().iterator();
        while (myIterator.hasNext()) {
            Map.Entry<MetisField, ObjectProperty<Object>> myEntry = myIterator.next();
            MetisField myField = myEntry.getKey();
            ObjectProperty<Object> myProperty = myEntry.getValue();

            /* Obtain the value */
            Object myValue = theItem.getFieldValue(myField);
            if (myValue == MetisFieldValue.SKIP) {
                myValue = null;
            }
            if (myValue instanceof MetisEncryptedField) {
                myValue = ((MetisEncryptedField<?>) myValue).getValue();
            }

            /* Store into the property */
            myProperty.setValue(myValue);
        }
    }
}

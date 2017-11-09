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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldStorage;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues.MetisEncryptedValue;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableCalculator;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosFieldDef;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosTableItem;

/**
 * Table FieldSet.
 * @param <R> the item type
 */
public class MetisFXTableFieldSet<R extends MetisDataEosTableItem> {
    /**
     * List Fields.
     */
    private final MetisFXTableListFields<R> theFields;

    /**
     * The Item.
     */
    private final R theItem;

    /**
     * The Map of Field ID to Observable Object.
     */
    private final Map<MetisDataEosFieldDef, ObjectProperty<Object>> thePropertyMap;

    /**
     * The Array of Observable properties.
     */
    private Observable[] theObservables;

    /**
     * Constructor.
     * @param pItem the item
     * @param pFields the fields
     */
    protected MetisFXTableFieldSet(final R pItem,
                                   final MetisFXTableListFields<R> pFields) {
        /* Store the parameters */
        theItem = pItem;
        theFields = pFields;

        /* Create the map and populate it */
        thePropertyMap = new HashMap<>();
        theObservables = initialiseMap(pFields.getFields());
    }

    /**
     * Return the item.
     * @return the item
     */
    protected R getItem() {
        return theItem;
    }

    /**
     * Return the observable array.
     * @return the array
     */
    protected Observable[] getObservables() {
        return theObservables;
    }

    /**
     * Obtain the object property for the field.
     * @param pField the field
     * @return the property
     */
    protected ObjectProperty<Object> getPropertyForField(final MetisDataEosFieldDef pField) {
        return thePropertyMap.get(pField);
    }

    /**
     * Initialise the map.
     * @param pFields the fields
     * @return the observable array
     */
    private Observable[] initialiseMap(final List<MetisDataEosFieldDef> pFields) {
        /* Create the observable array */
        final int myMax = pFields.size();
        final Observable[] myObservables = new Observable[myMax];
        int myNumFields = 0;

        /* Iterate through the fields */
        final Iterator<MetisDataEosFieldDef> myIterator = pFields.iterator();
        while (myIterator.hasNext()) {
            final MetisDataEosFieldDef myField = myIterator.next();

            /* Create the property */
            final ObjectProperty<Object> myProperty = new SimpleObjectProperty<>();
            thePropertyMap.put(myField, myProperty);

            /* Initialise the value */
            setValue(myField, myProperty);

            /* Add it to the observable array */
            myObservables[myNumFields++] = myProperty;
        }

        /* Return the observable array */
        return myObservables;
    }

    /**
     * Populate the values.
     */
    protected void updateValues() {
        /* Iterate through the entries */
        final Iterator<Map.Entry<MetisDataEosFieldDef, ObjectProperty<Object>>> myIterator = thePropertyMap.entrySet().iterator();
        while (myIterator.hasNext()) {
            final Map.Entry<MetisDataEosFieldDef, ObjectProperty<Object>> myEntry = myIterator.next();
            final MetisDataEosFieldDef myField = myEntry.getKey();

            /* If the field is changeable */
            final MetisDataFieldStorage myStorage = myField.getStorage();
            if (myStorage.isVersioned()
                || myStorage.isCalculated()) {
                /* Set the value */
                setValue(myField, myEntry.getValue());
            }
        }
    }

    /**
     * ReCalculate the values.
     */
    protected void recalculateValues() {
        /* Iterate through the entries */
        final Iterator<Map.Entry<MetisDataEosFieldDef, ObjectProperty<Object>>> myIterator = thePropertyMap.entrySet().iterator();
        while (myIterator.hasNext()) {
            final Map.Entry<MetisDataEosFieldDef, ObjectProperty<Object>> myEntry = myIterator.next();
            final MetisDataEosFieldDef myField = myEntry.getKey();

            /* If the field is calculated */
            final MetisDataFieldStorage myStorage = myField.getStorage();
            if (myStorage.isCalculated()) {
                /* Set the value */
                setCalculatedValue(myField, myEntry.getValue());
            }
        }
    }

    /**
     * Set a property.
     * @param pField the field
     * @param pProperty the property
     */
    private void setValue(final MetisDataEosFieldDef pField,
                          final ObjectProperty<Object> pProperty) {
        if (pField.getStorage().isCalculated()) {
            setCalculatedValue(pField, pProperty);
        } else {
            setStandardValue(pField, pProperty);
        }
    }

    /**
     * Set a property.
     * @param pField the field
     * @param pProperty the property
     */
    private void setStandardValue(final MetisDataEosFieldDef pField,
                                  final ObjectProperty<Object> pProperty) {
        /* Obtain the value */
        Object myValue = pField.getFieldValue(theItem);
        if (myValue == MetisDataFieldValue.SKIP) {
            myValue = null;
        }
        if (myValue instanceof MetisEncryptedValue) {
            myValue = ((MetisEncryptedValue) myValue).getValue();
        }

        /* Store into the property */
        pProperty.setValue(myValue);
    }

    /**
     * Set a property.
     * @param pField the field
     * @param pProperty the property
     */
    private void setCalculatedValue(final MetisDataEosFieldDef pField,
                                    final ObjectProperty<Object> pProperty) {
        /* Obtain the value */
        final MetisTableCalculator<R> myCalculator = theFields.getCalculator();
        final Object myValue = myCalculator == null
                                                    ? null
                                                    : myCalculator.calculateValue(theItem, pField);

        /* Store into the property */
        pProperty.setValue(myValue);
    }
}

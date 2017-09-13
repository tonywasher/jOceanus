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
package net.sourceforge.joceanus.jmetis.lethe.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionDelta.MetisDataDelta;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataValues;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;

/**
 * Provides the implementation of delta between two valueSets.
 */
public class MetisValueSetDelta
        implements MetisDataContents {
    /**
     * Old ValueSet.
     */
    private final MetisValueSet theOldSet;

    /**
     * New ValueSet.
     */
    private final MetisValueSet theNewSet;

    /**
     * Constructor.
     * @param pNew the new valueSet.
     * @param pOld the old valueSet.
     */
    protected MetisValueSetDelta(final MetisValueSet pNew,
                                 final MetisValueSet pOld) {
        /* Store parameters */
        theOldSet = pOld;
        theNewSet = pNew;
    }

    /**
     * Obtain the version.
     * @return the version
     */
    public int getVersion() {
        return theOldSet.getVersion();
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        /* Access the values */
        final Object[] myNewValues = theNewSet.getValues();
        final Object[] myOldValues = theOldSet.getValues();

        /* Initialise number of differences */
        int myNumDiffs = (theOldSet.isDeletion() == theNewSet.isDeletion())
                                                                            ? 0
                                                                            : 1;

        /* Loop through the objects */
        for (int i = 0; i < myNewValues.length; i++) {
            if (!MetisDataDifference.isEqual(myOldValues[i], myNewValues[i])) {
                /* Increment the number of differences */
                myNumDiffs++;
            }
        }

        /* Return the number of differences */
        return MetisValueSetDelta.class.getSimpleName()
               + "("
               + myNumDiffs
               + ")";
    }

    @Override
    public MetisFields getDataFields() {
        /* Access the owning item fields */
        final MetisDataValues myItem = theOldSet.getItem();
        final MetisFields myFields = myItem.getDataFields();

        /* Allocate new local fields */
        final MetisFields myLocal = new MetisFields(MetisValueSetDelta.class.getSimpleName());

        /* Declare the version field */
        myLocal.declareIndexField(MetisValueSet.FIELD_VERSION);

        /* Declare the deletion field */
        myLocal.declareIndexField(MetisValueSet.FIELD_DELETION);

        /* Loop through the fields */
        final Iterator<MetisField> myIterator = myFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            final MetisField myField = myIterator.next();

            /* Skip if the field is not valueSet */
            if (!myField.getStorage().isValueSet()) {
                continue;
            }

            /* Declare the field */
            myLocal.declareIndexField(myField.getName());
        }

        /* Return the fields */
        return myLocal;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Access the index */
        int myIndex = pField.getIndex();

        /* Return version */
        if (myIndex == 0) {
            return getVersion();

            /* If this is the deletion field, return the flag */
        } else if (myIndex == 1) {
            return (theOldSet.isDeletion() == theNewSet.isDeletion())
                                                                      ? MetisFieldValue.SKIP
                                                                      : new MetisDataDelta(theOldSet.isDeletion(), MetisDataDifference.DIFFERENT);
        }

        /* Adjust index */
        myIndex -= 2;

        /* Obtain the difference */
        final Object myObject = theOldSet.getValue(myIndex);
        final MetisDataDifference myDifference = MetisDataDifference.difference(myObject, theNewSet.getValue(myIndex));

        /* Return the value */
        return new MetisDataDelta(myObject, myDifference);
    }
}

/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmetis.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataDifference;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataValues;

/**
 * Provides the implementation of delta between two valueSets.
 */
public class ValueSetDelta
        implements JDataContents {
    /**
     * Old ValueSet.
     */
    private final ValueSet theOldSet;

    /**
     * New ValueSet.
     */
    private final ValueSet theNewSet;

    /**
     * Constructor.
     * @param pNew the new valueSet.
     * @param pOld the old valueSet.
     */
    protected ValueSetDelta(final ValueSet pNew,
                            final ValueSet pOld) {
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
    public String formatObject() {
        /* Access the values */
        Object[] myNewValues = theNewSet.getValues();
        Object[] myOldValues = theOldSet.getValues();

        /* Initialise number of differences */
        int myNumDiffs = (theOldSet.isDeletion() == theNewSet.isDeletion())
                                                                           ? 0
                                                                           : 1;

        /* Loop through the objects */
        for (int i = 0; i < myNewValues.length; i++) {
            if (!Difference.isEqual(myOldValues[i], myNewValues[i])) {
                /* Increment the number of differences */
                myNumDiffs++;
            }
        }

        /* Return the number of differences */
        return ValueSetDelta.class.getSimpleName()
               + "("
               + myNumDiffs
               + ")";
    }

    @Override
    public JDataFields getDataFields() {
        /* Access the owning item fields */
        JDataValues myItem = theOldSet.getItem();
        JDataFields myFields = myItem.getDataFields();

        /* Allocate new local fields */
        JDataFields myLocal = new JDataFields(ValueSetDelta.class.getSimpleName());

        /* Declare the version field */
        myLocal.declareIndexField(ValueSet.FIELD_VERSION);

        /* Declare the deletion field */
        myLocal.declareIndexField(ValueSet.FIELD_DELETION);

        /* Loop through the fields */
        Iterator<JDataField> myIterator = myFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            JDataField myField = myIterator.next();

            /* Skip if the field is not valueSet */
            if (!myField.isValueSetField()) {
                continue;
            }

            /* Declare the field */
            myLocal.declareIndexField(myField.getName());
        }

        /* Return the fields */
        return myLocal;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Access the index */
        int myIndex = pField.getIndex();

        /* Return version */
        if (myIndex == 0) {
            return getVersion();

            /* If this is the deletion field, return the flag */
        } else if (myIndex == 1) {
            return (theOldSet.isDeletion() == theNewSet.isDeletion())
                                                                     ? JDataFieldValue.SKIP
                                                                     : new JDataDifference(theOldSet.isDeletion(), Difference.DIFFERENT);
        }

        /* Adjust index */
        myIndex -= 2;

        /* Obtain the difference */
        Object myObject = theOldSet.getValue(myIndex);
        Difference myDifference = Difference.getDifference(myObject, theNewSet.getValue(myIndex));

        /* Return the value */
        return new JDataDifference(myObject, myDifference);
    }
}

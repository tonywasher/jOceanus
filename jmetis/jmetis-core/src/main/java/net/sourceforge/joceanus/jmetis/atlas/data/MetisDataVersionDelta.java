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
package net.sourceforge.joceanus.jmetis.atlas.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldItem;

/**
 * Data Version Delta.
 */
public class MetisDataVersionDelta
        implements MetisDataFieldItem {
    /**
     * Old ValueSet.
     */
    private final MetisDataVersionValues theOldSet;

    /**
     * New ValueSet.
     */
    private final MetisDataVersionValues theNewSet;

    /**
     * Constructor.
     * @param pNew the new valueSet.
     * @param pOld the old valueSet.
     */
    protected MetisDataVersionDelta(final MetisDataVersionValues pNew,
                                    final MetisDataVersionValues pOld) {
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
        Object[] myNewValues = theNewSet.getValues();
        Object[] myOldValues = theOldSet.getValues();

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
        return MetisDataVersionDelta.class.getSimpleName()
               + "("
               + myNumDiffs
               + ")";
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        /* Access the owning item fields */
        MetisDataFieldItem myItem = theOldSet.getItem();
        MetisDataFieldSet myFields = myItem.getDataFieldSet();

        /* Allocate new local fields */
        MetisDataFieldSet myLocal = new MetisDataFieldSet(MetisDataVersionDelta.class);

        /* Declare the version field */
        myLocal.declareIndexField(MetisDataResource.DATA_VERSION.getValue());

        /* Declare the deletion field */
        myLocal.declareIndexField(MetisDataResource.DATA_DELETED.getValue());

        /* Loop through the fields */
        Iterator<MetisDataField> myIterator = myFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            MetisDataField myField = myIterator.next();

            /* Skip if the field is not versioned */
            if (!myField.getStorage().isVersioned()) {
                continue;
            }

            /* Declare the field */
            myLocal.declareIndexField(myField.getName());
        }

        /* Return the fields */
        return myLocal;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Access the index */
        int myIndex = pField.getIndex();

        /* Return version */
        if (myIndex == 0) {
            return getVersion();

            /* If this is the deletion field, return the flag */
        } else if (myIndex == 1) {
            return (theOldSet.isDeletion() == theNewSet.isDeletion())
                                                                      ? MetisDataFieldValue.SKIP
                                                                      : new MetisDataDelta(theOldSet.isDeletion(), MetisDataDifference.DIFFERENT);
        }

        /* Adjust index */
        myIndex -= 2;

        /* Obtain the difference */
        Object myObject = theOldSet.getValue(myIndex);
        MetisDataDifference myDifference = MetisDataDifference.difference(myObject, theNewSet.getValue(myIndex));

        /* Return the value */
        return new MetisDataDelta(myObject, myDifference);
    }

    /**
     * Delta class.
     */
    public static class MetisDataDelta {
        /**
         * The object itself.
         */
        private final Object theObject;

        /**
         * The difference.
         */
        private final MetisDataDifference theDifference;

        /**
         * Constructor.
         * @param pObject the object
         * @param pDifference the difference
         */
        public MetisDataDelta(final Object pObject,
                              final MetisDataDifference pDifference) {
            theObject = pObject;
            theDifference = pDifference;
        }

        /**
         * Obtain the object.
         * @return the object
         */
        public Object getObject() {
            return theObject;
        }

        /**
         * Obtain the difference.
         * @return the difference
         */
        public MetisDataDifference getDifference() {
            return theDifference;
        }
    }
}

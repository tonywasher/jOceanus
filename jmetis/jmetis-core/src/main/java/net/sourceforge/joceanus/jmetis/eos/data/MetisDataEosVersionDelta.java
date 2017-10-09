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
package net.sourceforge.joceanus.jmetis.eos.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionDelta;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionDelta.MetisDataDelta;

/**
 * Set of dataValue Deltas.
 */
public class MetisDataEosVersionDelta
        implements MetisDataEosFieldItem {
    /**
     * FieldSet definitions.
     */
    private static final MetisDataEosFieldSet<MetisDataEosVersionDelta> FIELD_SET = MetisDataEosFieldSet.newFieldSet(MetisDataEosVersionDelta.class);

    /**
     * Initialise the Version Field.
     */
    static {
        FIELD_SET.declareLocalField(MetisDataResource.DATA_VERSION.getValue(), MetisDataEosVersionDelta::getVersion);
    }

    /**
     * Old ValueSet.
     */
    private final MetisDataEosVersionValues theOldSet;

    /**
     * New ValueSet.
     */
    private final MetisDataEosVersionValues theNewSet;

    /**
     * Local fieldSet.
     */
    private MetisDataEosFieldSet<MetisDataEosVersionDelta> theLocalFields;

    /**
     * Constructor.
     * @param pNew the new valueSet.
     * @param pOld the old valueSet.
     */
    protected MetisDataEosVersionDelta(final MetisDataEosVersionValues pNew,
                                       final MetisDataEosVersionValues pOld) {
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
        /* Initialise number of differences */
        int myNumDiffs = theOldSet.isDeletion() == theNewSet.isDeletion()
                                                                          ? 0
                                                                          : 1;

        /* Loop through the fields */
        final Iterator<MetisDataEosFieldDef> myIterator = getDataFieldSet().fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            final MetisDataEosFieldDef myField = myIterator.next();

            /* Skip if the field is not versioned */
            if (!myField.getStorage().isVersioned()) {
                continue;
            }

            /* Obtain the difference */
            final Object myObject = theOldSet.getValue(myField);
            final MetisDataDifference myDifference = MetisDataDifference.difference(myObject, theNewSet.getValue(myField));

            /* If there is a difference */
            if (!myDifference.isIdentical()) {
                /* Increment the differences */
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
    public MetisDataEosFieldSetDef getDataFieldSet() {
        if (theLocalFields == null) {
            theLocalFields = buildLocalFieldSet();
        }
        return theLocalFields;
    }

    /**
     * Build localFieldSet.
     * @return the fieldSet
     */
    private MetisDataEosFieldSet<MetisDataEosVersionDelta> buildLocalFieldSet() {
        /* Access the owning item fields */
        final MetisDataEosFieldItem myItem = theOldSet.getItem();
        final MetisDataEosFieldSetDef myFields = myItem.getDataFieldSet();

        /* Allocate new local fields */
        final MetisDataEosFieldSet<MetisDataEosVersionDelta> myLocal = MetisDataEosFieldSet.newFieldSet(this);

        /* If we have a change in deletion status */
        if (theOldSet.isDeletion() != theNewSet.isDeletion()) {
            /* Declare the deleted field */
            myLocal.declareLocalField(MetisDataResource.DATA_DELETED.getValue(), f -> new MetisDataDelta(theOldSet.isDeletion(), MetisDataDifference.DIFFERENT));
        }

        /* Loop through the fields */
        final Iterator<MetisDataEosFieldDef> myIterator = myFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            final MetisDataEosFieldDef myField = myIterator.next();

            /* Skip if the field is not versioned */
            if (!myField.getStorage().isVersioned()) {
                continue;
            }

            /* Obtain the difference */
            final Object myObject = theOldSet.getValue(myField);
            final MetisDataDifference myDifference = MetisDataDifference.difference(myObject, theNewSet.getValue(myField));

            /* If there is a difference */
            if (!myDifference.isIdentical()) {
                /* Declare the field */
                myLocal.declareLocalField(myField.getName(), f -> new MetisDataDelta(myObject, myDifference));
            }
        }

        /* Return the fields */
        return myLocal;
    }
}

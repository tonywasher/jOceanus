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
package net.sourceforge.joceanus.jmetis.data;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;

/**
 * Provides the implementation of a history buffer for a DataItem. Each element represents a changed
 * set of values and refers to a {@link MetisValueSet} object which is the set of changeable values
 * for the object.
 * @see MetisValueSet
 */
public class MetisValueSetHistory
        implements MetisDataContents {
    /**
     * The current set of values for this object.
     */
    private MetisValueSet theCurr;

    /**
     * The original set of values if any changes have been made.
     */
    private MetisValueSet theOriginal;

    /**
     * The stack of valueSet changes.
     */
    private final Deque<MetisValueSet> theStack;

    /**
     * The stack of valueSetDelta fields.
     */
    private final Deque<MetisValueSetDelta> theDeltas;

    /**
     * Constructor.
     */
    public MetisValueSetHistory() {
        /* Allocate the stack */
        theStack = new ArrayDeque<>();
        theDeltas = new ArrayDeque<>();
    }

    @Override
    public MetisFields getDataFields() {
        /* Allocate new local fields */
        MetisFields myFields = new MetisFields(MetisValueSetHistory.class.getSimpleName());

        /* Loop through the fields */
        Iterator<MetisValueSetDelta> myIterator = theDeltas.descendingIterator();
        while (myIterator.hasNext()) {
            /* Access the Delta */
            MetisValueSetDelta myDelta = myIterator.next();

            /* Declare the field */
            myFields.declareIndexField(MetisValueSet.FIELD_VERSION + "(" + myDelta.getVersion() + ")");
        }

        return myFields;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Access the index */
        int myIndex = pField.getIndex();

        /* Loop through the fields */
        Iterator<MetisValueSetDelta> myIterator = theDeltas.descendingIterator();
        int i = 0;
        while (myIterator.hasNext()) {
            MetisValueSetDelta myDelta = myIterator.next();

            /* Return field if we found it */
            if (i == myIndex) {
                return myDelta;
            }

            /* Increment index */
            i++;
        }

        /* Not found */
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return MetisValueSetHistory.class.getSimpleName() + "(" + theStack.size() + ")";
    }

    /**
     * Initialise the current values.
     * @param pValues the current values
     */
    public void setValues(final MetisValueSet pValues) {
        /* Store details and clear the stack */
        theCurr = pValues;
        theOriginal = theCurr;
        theStack.clear();
        theDeltas.clear();
    }

    /**
     * Get the changeable values object for this item.
     * @return the object
     */
    public MetisValueSet getValueSet() {
        return theCurr;
    }

    /**
     * Get original values.
     * @return original values
     */
    public MetisValueSet getOriginalValues() {
        return theOriginal;
    }

    /**
     * Push Item to the history.
     * @param pVersion the new version
     */
    public void pushHistory(final int pVersion) {
        /* Create a new ValueSet */
        MetisValueSet mySet = theCurr.cloneIt();
        mySet.setVersion(pVersion);

        /* Add the delta to the stack */
        theDeltas.push(new MetisValueSetDelta(mySet, theCurr));

        /* Add old values to the stack and record new values */
        theStack.push(theCurr);
        theCurr = mySet;

        /* Declare the active set */
        theCurr.declareActive();
    }

    /**
     * popItem from the history and remove from history.
     */
    public void popTheHistory() {
        /* If we have an item on the stack */
        if (hasHistory()) {
            /* Remove it from the list */
            theCurr = theStack.pop();
            theDeltas.pop();

            /* Declare the active set */
            theCurr.declareActive();
        }
    }

    /**
     * popItem from the history if equal to current.
     * @return was a change made
     */
    public boolean maybePopHistory() {
        /* If there is no change */
        if (theCurr.differs(theStack.peek()).isIdentical()) {
            /* Just pop the history */
            popTheHistory();
            return false;
        }

        /* Return that we have made a change */
        return true;
    }

    /**
     * Is there any history.
     * @return whether there are entries in the history list
     */
    public boolean hasHistory() {
        return !theStack.isEmpty();
    }

    /**
     * Clear history.
     */
    public void clearHistory() {
        /* Remove all history */
        theStack.clear();
        theDeltas.clear();
        theOriginal = theCurr;
        theOriginal.setVersion(0);
    }

    /**
     * Reset history.
     */
    public void resetHistory() {
        /* Remove all history */
        theStack.clear();
        theDeltas.clear();
        theCurr = theOriginal;
        theCurr.declareActive();
    }

    /**
     * Set history explicitly.
     * @param pBase the base item
     */
    public void setHistory(final MetisValueSet pBase) {
        theStack.clear();
        theDeltas.clear();
        theOriginal = theCurr.cloneIt();
        theOriginal.copyFrom(pBase);
        theStack.push(theOriginal);
        theCurr.setVersion(1);

        /* Add the delta to the stack */
        theDeltas.push(new MetisValueSetDelta(theCurr, theOriginal));
    }

    /**
     * Condense history.
     * @param pNewVersion the new maximum version
     */
    public void condenseHistory(final int pNewVersion) {
        /* If we need to condense history */
        if (theCurr.getVersion() > pNewVersion) {
            /* While we have unnecessary stack entries */
            boolean bNewDelta = false;
            while (!theStack.isEmpty()
                   && theStack.peek().getVersion() >= pNewVersion) {
                /* Clear them */
                theStack.pop();
                theDeltas.pop();
                bNewDelta = true;
            }

            /* Set the desired version */
            theCurr.setVersion(pNewVersion);

            /* If we need to adjust the delta */
            if (bNewDelta && !theStack.isEmpty()) {
                /* remove old delta */
                theDeltas.pop();

                /* Add the new delta to the stack */
                theDeltas.push(new MetisValueSetDelta(theCurr, theStack.peek()));
            }
        }
    }

    /**
     * Determines whether a particular field has changed.
     * @param pField the field
     * @return the difference
     */
    public MetisDifference fieldChanged(final MetisField pField) {
        /* Handle irrelevant cases */
        if (!pField.isValueSetField()) {
            return MetisDifference.IDENTICAL;
        }
        if (!pField.isEqualityField()) {
            return MetisDifference.IDENTICAL;
        }

        /* Call the function from the interface */
        return theCurr.fieldChanged(pField, theOriginal);
    }
}

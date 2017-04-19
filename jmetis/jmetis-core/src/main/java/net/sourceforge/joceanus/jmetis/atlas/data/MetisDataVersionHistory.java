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
package net.sourceforge.joceanus.jmetis.atlas.data;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisIndexedItem;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;

/**
 * Data Version History.
 */
public class MetisDataVersionHistory
        implements MetisIndexedItem, MetisDataFieldItem {
    /**
     * The id.
     */
    private Integer theId;

    /**
     * The current set of values for this object.
     */
    private MetisDataVersionValues theCurr;

    /**
     * The original set of values if any changes have been made.
     */
    private MetisDataVersionValues theOriginal;

    /**
     * The dataState.
     */
    private MetisDataState theState;

    /**
     * The stack of valueSet changes.
     */
    private final Deque<MetisDataVersionValues> theStack;

    /**
     * The stack of valueSetDelta fields.
     */
    private final Deque<MetisDataVersionDelta> theDeltas;

    /**
     * The Data Item Validation.
     */
    private final MetisDataItemValidation theValidation;

    /**
     * Constructor.
     */
    public MetisDataVersionHistory() {
        /* Allocate the stack */
        theStack = new ArrayDeque<>();
        theDeltas = new ArrayDeque<>();

        /* Allocate the validation */
        theValidation = new MetisDataItemValidation();
    }

    @Override
    public Integer getIndexedId() {
        return theId;
    }

    /**
     * Set Id.
     * @param pId the Id
     */
    public void setIndexedId(final Integer pId) {
        theId = pId;
    }

    /**
     * Obtain the DataItemValidation.
     * @return the validation
     */
    public MetisDataItemValidation getValidation() {
        return theValidation;
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        /* Allocate new local fields */
        MetisDataFieldSet myFields = new MetisDataFieldSet(MetisDataVersionHistory.class.getSimpleName());

        /* Loop through the fields */
        Iterator<MetisDataVersionDelta> myIterator = theDeltas.descendingIterator();
        while (myIterator.hasNext()) {
            /* Access the Delta */
            MetisDataVersionDelta myDelta = myIterator.next();

            /* Declare the field */
            myFields.declareIndexField(MetisValueSet.FIELD_VERSION + "(" + myDelta.getVersion() + ")");
        }

        return myFields;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Access the index */
        int myIndex = pField.getIndex();

        /* Loop through the fields */
        Iterator<MetisDataVersionDelta> myIterator = theDeltas.descendingIterator();
        int i = 0;
        while (myIterator.hasNext()) {
            MetisDataVersionDelta myDelta = myIterator.next();

            /* Return field if we found it */
            if (i == myIndex) {
                return myDelta;
            }

            /* Increment index */
            i++;
        }

        /* Not found */
        return MetisDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return MetisDataVersionHistory.class.getSimpleName() + "(" + theStack.size() + ")";
    }

    /**
     * Initialise the current values.
     * @param pValues the current values
     */
    public void setValues(final MetisDataVersionValues pValues) {
        /* Store details and clear the stack */
        theCurr = pValues;
        theOriginal = theCurr;
        theStack.clear();
        theDeltas.clear();
        adjustState();
    }

    /**
     * Obtain the State of item.
     * @return the state
     */
    public MetisDataState getState() {
        return theState;
    }

    /**
     * Get the changeable values object for this item.
     * @return the object
     */
    public MetisDataVersionValues getValueSet() {
        return theCurr;
    }

    /**
     * Get original values.
     * @return original values
     */
    public MetisDataVersionValues getOriginalValues() {
        return theOriginal;
    }

    /**
     * Push Item to the history.
     * @param pVersion the new version
     */
    public void pushHistory(final int pVersion) {
        /* Create a new ValueSet */
        MetisDataVersionValues mySet = theCurr.cloneIt();
        mySet.setVersion(pVersion);

        /* Add the delta to the stack */
        theDeltas.push(new MetisDataVersionDelta(mySet, theCurr));

        /* Add old values to the stack and record new values */
        theStack.push(theCurr);
        theCurr = mySet;
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
        adjustState();
    }

    /**
     * Reset history.
     */
    public void resetHistory() {
        /* Remove all history */
        theStack.clear();
        theDeltas.clear();
        theCurr = theOriginal;
        adjustState();
    }

    /**
     * Set history explicitly.
     * @param pBase the base item
     */
    public void setHistory(final MetisDataVersionValues pBase) {
        theStack.clear();
        theDeltas.clear();
        theOriginal = theCurr.cloneIt();
        theOriginal.copyFrom(pBase);
        theStack.push(theOriginal);
        theCurr.setVersion(1);

        /* Add the delta to the stack */
        theDeltas.push(new MetisDataVersionDelta(theCurr, theOriginal));
        adjustState();
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
                theDeltas.push(new MetisDataVersionDelta(theCurr, theStack.peek()));
            }
        }
    }

    /**
     * Determines whether a particular field has changed.
     * @param pField the field
     * @return the difference
     */
    public MetisDataDifference fieldChanged(final MetisDataField pField) {
        /* Handle irrelevant cases */
        if (!pField.getStorage().isVersioned()) {
            return MetisDataDifference.IDENTICAL;
        }
        if (!pField.getEquality().isEquality()) {
            return MetisDataDifference.IDENTICAL;
        }

        /* Call the function from the interface */
        return theCurr.fieldChanged(pField, theOriginal);
    }

    /**
     * Adjust State of item.
     */
    public void adjustState() {
        theState = determineState();
    }

    /**
     * Determine State of item.
     * @return the state of the item
     */
    private MetisDataState determineState() {
        /* If we are a new element */
        if (theOriginal.getVersion() > 0) {
            /* Return status */
            return theCurr.isDeletion()
                                        ? MetisDataState.DELNEW
                                        : MetisDataState.NEW;
        }

        /* If we have no changes we are CLEAN */
        if (theCurr.getVersion() == 0) {
            return MetisDataState.CLEAN;
        }

        /* If we are deleted return so */
        if (theCurr.isDeletion()) {
            return MetisDataState.DELETED;
        }

        /* Return RECOVERED or CHANGED depending on whether we started as deleted */
        return theOriginal.isDeletion()
                                        ? MetisDataState.RECOVERED
                                        : MetisDataState.CHANGED;
    }
}

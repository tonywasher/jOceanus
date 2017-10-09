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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionHistory;

/**
 * Data Version History.
 */
public class MetisDataEosVersionHistory
        implements MetisDataEosFieldItem {
    /**
     * FieldSet definitions.
     */
    static {
        MetisDataEosFieldSet.newFieldSet(MetisDataEosVersionHistory.class);
    }

    /**
     * The stack of valueSet changes.
     */
    private final Deque<MetisDataEosVersionValues> theStack;

    /**
     * The stack of valueSetDelta fields.
     */
    private final Deque<MetisDataEosVersionDelta> theDeltas;

    /**
     * The current set of values for this object.
     */
    private MetisDataEosVersionValues theCurr;

    /**
     * The original set of values if any changes have been made.
     */
    private MetisDataEosVersionValues theOriginal;

    /**
     * Constructor.
     * @param pCurr the current values
     */
    protected MetisDataEosVersionHistory(final MetisDataEosVersionValues pCurr) {
        /* Allocate the values */
        theCurr = pCurr;
        theOriginal = theCurr;

        /* Allocate the stack */
        theStack = new ArrayDeque<>();
        theDeltas = new ArrayDeque<>();
    }

    @Override
    public MetisDataEosFieldSetDef getDataFieldSet() {
        /* Allocate new local fields */
        final MetisDataEosFieldSet<MetisDataEosVersionHistory> myLocal = MetisDataEosFieldSet.newFieldSet(this);
        final String myVersion = MetisDataResource.DATA_VERSION.getValue();

        /* Loop through the fields */
        final Iterator<MetisDataEosVersionDelta> myIterator = theDeltas.descendingIterator();
        while (myIterator.hasNext()) {
            /* Access the Delta */
            final MetisDataEosVersionDelta myDelta = myIterator.next();

            /* Declare the field */
            myLocal.declareLocalField(myVersion + "(" + myDelta.getVersion() + ")", f -> myDelta);
        }

        /* Return the fieldSet */
        return myLocal;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return MetisDataVersionHistory.class.getSimpleName() + "(" + theStack.size() + ")";
    }

    /**
     * Initialise the current values.
     * @param pValues the current values
     */
    protected void setValues(final MetisDataEosVersionValues pValues) {
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
    protected MetisDataEosVersionValues getValueSet() {
        return theCurr;
    }

    /**
     * Get original values.
     * @return original values
     */
    protected MetisDataEosVersionValues getOriginalValues() {
        return theOriginal;
    }

    /**
     * Push Item to the history.
     * @param pVersion the new version
     */
    protected void pushHistory(final int pVersion) {
        /* Create a new ValueSet */
        final MetisDataEosVersionValues mySet = theCurr.cloneIt();
        mySet.setVersion(pVersion);

        /* Add the delta to the stack */
        theDeltas.push(new MetisDataEosVersionDelta(mySet, theCurr));

        /* Add old values to the stack and record new values */
        theStack.push(theCurr);
        theCurr = mySet;
    }

    /**
     * popItem from the history and remove from history.
     */
    protected void popTheHistory() {
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
    protected boolean maybePopHistory() {
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
    protected boolean hasHistory() {
        return !theStack.isEmpty();
    }

    /**
     * Clear history.
     */
    protected void clearHistory() {
        /* Remove all history */
        theStack.clear();
        theDeltas.clear();
        theOriginal = theCurr;
        theOriginal.setVersion(0);
    }

    /**
     * Reset history.
     */
    protected void resetHistory() {
        /* Remove all history */
        theStack.clear();
        theDeltas.clear();
        theCurr = theOriginal;
    }

    /**
     * Set history explicitly.
     * @param pBase the base item
     */
    protected void setHistory(final MetisDataEosVersionValues pBase) {
        theStack.clear();
        theDeltas.clear();
        theOriginal = theCurr.cloneIt();
        theOriginal.copyFrom(pBase);
        theStack.push(theOriginal);
        theCurr.setVersion(1);

        /* Add the delta to the stack */
        theDeltas.push(new MetisDataEosVersionDelta(theCurr, theOriginal));
    }

    /**
     * Condense history.
     * @param pNewVersion the new maximum version
     */
    protected void condenseHistory(final int pNewVersion) {
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
                theDeltas.push(new MetisDataEosVersionDelta(theCurr, theStack.peek()));
            }
        }
    }

    /**
     * Determines whether a particular field has changed.
     * @param pField the field
     * @return the difference
     */
    protected MetisDataDifference fieldChanged(final MetisDataEosFieldDef pField) {
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
}

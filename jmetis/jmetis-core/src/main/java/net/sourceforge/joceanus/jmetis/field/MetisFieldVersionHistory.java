/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmetis.field;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Data Version History.
 */
public class MetisFieldVersionHistory
        implements MetisFieldItem {
    /*
     * FieldSet definitions.
     */
    static {
        MetisFieldSet.newFieldSet(MetisFieldVersionHistory.class);
    }

    /**
     * The stack of valueSet changes.
     */
    private final Deque<MetisFieldVersionValues> theStack;

    /**
     * The stack of valueSetDelta fields.
     */
    private final Deque<MetisFieldVersionDelta> theDeltas;

    /**
     * The current set of values for this object.
     */
    private MetisFieldVersionValues theCurr;

    /**
     * The original set of values if any changes have been made.
     */
    private MetisFieldVersionValues theOriginal;

    /**
     * Constructor.
     * @param pCurr the current values
     */
    protected MetisFieldVersionHistory(final MetisFieldVersionValues pCurr) {
        /* Allocate the values */
        theCurr = pCurr;
        theOriginal = theCurr;

        /* Allocate the stack */
        theStack = new ArrayDeque<>();
        theDeltas = new ArrayDeque<>();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        /* Allocate new local fields */
        final MetisFieldSet<MetisFieldVersionHistory> myLocal = MetisFieldSet.newFieldSet(this);
        final String myVersion = MetisDataResource.DATA_VERSION.getValue();

        /* Loop through the fields */
        final Iterator<MetisFieldVersionDelta> myIterator = theDeltas.descendingIterator();
        while (myIterator.hasNext()) {
            /* Access the Delta */
            final MetisFieldVersionDelta myDelta = myIterator.next();

            /* Declare the field */
            myLocal.declareLocalField(myVersion + "(" + myDelta.getVersion() + ")", f -> myDelta);
        }

        /* Return the fieldSet */
        return myLocal;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return MetisFieldVersionHistory.class.getSimpleName() + "(" + theStack.size() + ")";
    }

    /**
     * Initialise the current values.
     * @param pValues the current values
     */
    protected void setValues(final MetisFieldVersionValues pValues) {
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
    protected MetisFieldVersionValues getValueSet() {
        return theCurr;
    }

    /**
     * Get original values.
     * @return original values
     */
    protected MetisFieldVersionValues getOriginalValues() {
        return theOriginal;
    }

    /**
     * Push Item to the history.
     * @param pVersion the new version
     */
    public void pushHistory(final int pVersion) {
        /* Create a new ValueSet */
        final MetisFieldVersionValues mySet = theCurr.cloneIt();
        mySet.setVersion(pVersion);

        /* Add the delta to the stack */
        theDeltas.push(new MetisFieldVersionDelta(mySet, theCurr));

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
    public void setHistory(final MetisFieldVersionValues pBase) {
        theStack.clear();
        theDeltas.clear();
        theOriginal = theCurr.cloneIt();
        theOriginal.copyFrom(pBase);
        theStack.push(theOriginal);
        theCurr.setVersion(1);

        /* Add the delta to the stack */
        theDeltas.push(new MetisFieldVersionDelta(theCurr, theOriginal));
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
                theDeltas.push(new MetisFieldVersionDelta(theCurr, theStack.peek()));
            }
        }
    }

    /**
     * Determines whether a particular field has changed.
     * @param pField the field
     * @return the difference
     */
    public MetisDataDifference fieldChanged(final MetisFieldDef pField) {
        /* Handle irrelevant cases */
        if (!(pField instanceof MetisFieldVersionedDef)) {
            return MetisDataDifference.IDENTICAL;
        }
        if (!((MetisFieldVersionedDef) pField).isEquality()) {
            return MetisDataDifference.IDENTICAL;
        }

        /* Call the function from the interface */
        return theCurr.fieldChanged(pField, theOriginal);
    }
}

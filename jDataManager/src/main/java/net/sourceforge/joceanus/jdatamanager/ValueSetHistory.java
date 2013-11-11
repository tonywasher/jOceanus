/*******************************************************************************
 * jDataManager: Java Data Manager
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jdatamanager;

import java.util.ListIterator;
import java.util.Stack;

import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;

/**
 * Provides the implementation of a history buffer for a DataItem. Each element represents a changed set of values and refers to a {@link ValueSet} object which
 * is the set of changeable values for the object.
 * @see ValueSet
 */
public class ValueSetHistory
        implements JDataContents {
    @Override
    public JDataFields getDataFields() {
        /* Allocate new local fields */
        JDataFields myFields = new JDataFields(ValueSetHistory.class.getSimpleName());

        /* Loop through the fields */
        ListIterator<ValueSetDelta> myIterator = theDeltas.listIterator(theDeltas.size());
        while (myIterator.hasPrevious()) {
            /* Access the Delta */
            ValueSetDelta myDelta = myIterator.previous();

            /* Declare the field */
            myFields.declareIndexField(ValueSet.FIELD_VERSION
                                       + "("
                                       + myDelta.getVersion()
                                       + ")");
        }

        return myFields;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Access the index */
        int myIndex = pField.getIndex();
        int mySize = theDeltas.size();
        if ((myIndex < 0)
            || (myIndex >= mySize)) {
            return JDataFieldValue.UNKNOWN;
        }

        /* Access the delta */
        return theDeltas.get(mySize
                             - myIndex
                             - 1);
    }

    @Override
    public String formatObject() {
        return ValueSetHistory.class.getSimpleName()
               + "("
               + theStack.size()
               + ")";
    }

    /**
     * The current set of values for this object.
     */
    private ValueSet theCurr = null;

    /**
     * The original set of values if any changes have been made.
     */
    private ValueSet theOriginal = null;

    /**
     * The stack of valueSet changes.
     */
    private final Stack<ValueSet> theStack;

    /**
     * The stack of valueSetDelta fields.
     */
    private final Stack<ValueSetDelta> theDeltas;

    /**
     * Constructor.
     */
    public ValueSetHistory() {
        /* Allocate the stack */
        theStack = new Stack<ValueSet>();
        theDeltas = new Stack<ValueSetDelta>();
    }

    /**
     * Initialise the current values.
     * @param pValues the current values
     */
    public void setValues(final ValueSet pValues) {
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
    public ValueSet getValueSet() {
        return theCurr;
    }

    /**
     * Get original values.
     * @return original values
     */
    public ValueSet getOriginalValues() {
        return theOriginal;
    }

    /**
     * Push Item to the history.
     * @param pVersion the new version
     */
    public void pushHistory(final int pVersion) {
        /* Create a new ValueSet */
        ValueSet mySet = theCurr.cloneIt();
        mySet.setVersion(pVersion);

        /* Add the delta to the stack */
        theDeltas.push(new ValueSetDelta(mySet, theCurr));

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
        return !theStack.empty();
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
    public void setHistory(final ValueSet pBase) {
        theStack.clear();
        theOriginal = theCurr.cloneIt();
        theOriginal.copyFrom(pBase);
        theStack.push(theOriginal);
        theCurr.setVersion(1);

        /* Add the delta to the stack */
        theDeltas.push(new ValueSetDelta(theCurr, theOriginal));
    }

    /**
     * Determines whether a particular field has changed.
     * @param pField the field
     * @return the difference
     */
    public Difference fieldChanged(final JDataField pField) {
        /* Handle irrelevant cases */
        if (!pField.isValueSetField()) {
            return Difference.IDENTICAL;
        }
        if (!pField.isEqualityField()) {
            return Difference.IDENTICAL;
        }

        /* Call the function from the interface */
        return theCurr.fieldChanged(pField, theOriginal);
    }
}

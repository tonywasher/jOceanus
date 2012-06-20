/*******************************************************************************
 * JDataManager: Java Data Manager
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JDataManager;

import java.util.Stack;

import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;

/**
 * Provides the implementation of a history buffer for a DataItem. Each element represents a changed set of
 * values and refers to a {@link ValueSet} object which is the set of changeable values for the object.
 * @see ValueSet
 */
public class ValueSetHistory implements JDataContents {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(ValueSetHistory.class.getSimpleName());

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Stack Field Id.
     */
    public static final JDataField FIELD_STACK = FIELD_DEFS.declareEqualityField("Stack");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_STACK.equals(pField)) {
            return theStack;
        }
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName() + "(" + theStack.size() + ")";
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
     * Constructor.
     */
    public ValueSetHistory() {
        /* Allocate the stack */
        theStack = new Stack<ValueSet>();
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
    public void pushHistory(int pVersion) {
        /* Create a new ValueSet */
        ValueSet mySet = theCurr.cloneIt();
        mySet.setVersion(pVersion);

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
        return (!theStack.empty());
    }

    /**
     * Clear history.
     */
    public void clearHistory() {
        /* Remove all history */
        theStack.clear();
        theOriginal = theCurr;
        theOriginal.setVersion(0);
    }

    /**
     * Reset history.
     */
    public void resetHistory() {
        /* Remove all history */
        theStack.clear();
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
    }

    /**
     * Determines whether a particular field has changed.
     * @param pField the field
     * @return the difference
     */
    public Difference fieldChanged(final JDataField pField) {
        /* Handle irrelevant cases */
        if (!pField.isValueSetField()) {
            return Difference.Identical;
        }
        if (!pField.isEqualityField()) {
            return Difference.Identical;
        }

        /* Call the function from the interface */
        return theCurr.fieldChanged(pField, theOriginal);
    }
}

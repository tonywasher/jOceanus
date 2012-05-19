/*******************************************************************************
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
package uk.co.tolcroft.models.data;

import java.util.Stack;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.ReportFields.ReportField;
import uk.co.tolcroft.models.data.DataList.ListStyle;

/**
 * Provides the implementation of a history buffer for a DataItem Each element represents a changed set of
 * values and refers to a {@link ValueSet} object which is the set of changeable values for the object.
 * @param <T> the item type
 * @see ValueSet
 */
public class HistoryControl<T extends DataItem<T>> {
    /**
     * The item to which this History Control belongs
     */
    private final T theItem;

    /**
     * The current set of values for this object
     */
    private ValueSet<T> theCurr = null;

    /**
     * The original set of values if any changes have been made
     */
    private ValueSet<T> theOriginal = null;

    /**
     * The stack of valueSet changes
     */
    private final Stack<ValueSet<T>> theStack;

    /**
     * Constructor
     * @param pItem the item to which this validation control belongs
     */
    protected HistoryControl(T pItem) {
        /* Store details */
        theItem = pItem;

        /* Allocate the stack */
        theStack = new Stack<ValueSet<T>>();
    }

    /**
     * Initialise the current values
     * @param pValues the current values
     */
    protected void setValues(ValueSet<T> pValues) {
        /* Store details and clear the stack */
        theCurr = pValues;
        theOriginal = theCurr;
        theStack.clear();
    }

    /**
     * Get the changeable values object for this item
     * @return the object
     */
    public ValueSet<T> getValueSet() {
        return theCurr;
    }

    /**
     * Get original values
     * @return original values
     */
    protected ValueSet<T> getOriginalValues() {
        return theOriginal;
    }

    /**
     * Determine State of item
     * @return the state of the item
     */
    protected DataState determineState() {
        /* If we have no history we are clean */
        if (theCurr == null)
            return DataState.CLEAN;

        /* If we are an edit extract */
        if (theItem.getStyle() == ListStyle.EDIT) {
            /* Access the base item */
            DataItem<?> myBase = theItem.getBase();

            /* If the item is deleted */
            if (theCurr.isDeletion()) {
                /* If we have no base then we are DelNew */
                if (myBase == null)
                    return DataState.DELNEW;

                /* If we have no history then we are clean */
                if (theOriginal == null)
                    return DataState.CLEAN;

                /* We are simply deleted */
                return DataState.DELETED;
            }

            /* If we have no base then we are New */
            if (myBase == null)
                return DataState.NEW;

            /* If we have no history we are Clean */
            if (theOriginal == null)
                return DataState.CLEAN;

            /* If we have a single change that is to undelete then we are Recovered */
            if ((theCurr == theOriginal) && (myBase.isDeleted()))
                return DataState.RECOVERED;

            /* else we are changed */
            return DataState.CHANGED;
        }

        /* If the item is deleted */
        if (theCurr.isDeletion()) {
            /* We must have a change */
            if (theOriginal == null) {
                throw new IllegalArgumentException();
            } else if (theOriginal.getVersion() != 0)
                return DataState.DELNEW;

            /* Return deleted */
            return DataState.DELETED;
        }

        /* If we have no changes we are either Clean or New */
        if (theOriginal == null)
            return (theCurr.getVersion() == 0) ? DataState.CLEAN : DataState.NEW;

        /* If we have the original values have version 0 */
        if (theOriginal.getVersion() == 0)
            return DataState.CHANGED;

        /* Return new state */
        return DataState.NEW;
    }

    /**
     * Push Item to the history
     */
    protected void pushHistory() {
        /* Create a new ValueSet */
        ValueSet<T> mySet = theCurr.cloneIt();

        /* Add old values to the stack and record new values */
        theStack.push(theCurr);
        theCurr = mySet;

        /* Declare the active set */
        theCurr.declareActive();
    }

    /**
     * popItem from the history and remove from history
     */
    protected void popTheHistory() {
        /* If we have an item on the stack */
        if (hasHistory()) {
            /* Remove it from the list */
            theCurr = theStack.pop();

            /* Declare the active set */
            theCurr.declareActive();
        }
    }

    /**
     * popItem from the history if equal to current
     * @return was a change made
     */
    protected boolean maybePopHistory() {
        /* If there is no change */
        if (theCurr.differs(theStack.peek()).isValueChanged()) {
            /* Just pop the history */
            popTheHistory();
            return false;
        }

        /* Return that we have made a change */
        return true;
    }

    /**
     * Is there any history
     * @return whether there are entries in the history list
     */
    protected boolean hasHistory() {
        return (!theStack.empty());
    }

    /**
     * Clear history
     */
    protected void clearHistory() {
        /* Remove all history */
        theStack.clear();
        theOriginal = theCurr;
    }

    /**
     * Reset history
     */
    protected void resetHistory() {
        /* Remove all history */
        theStack.clear();
        theCurr = theOriginal;
        theCurr.declareActive();
    }

    /**
     * Set history explicitly
     * @param pBase the base item
     */
    protected void setHistory(DataItem<?> pBase) {
        ValueSet<?> mySource = pBase.getOriginalValues();
        theStack.clear();
        theOriginal = theCurr.cloneIt();
        theOriginal.copyFrom(mySource);
        theStack.push(theOriginal);
    }

    /**
     * Determines whether a particular field has changed
     * @param pField the field
     * @return the difference
     */
    protected Difference fieldChanged(ReportField pField) {
        /* Handle irrelevant cases */
        if (!pField.isValueSetField())
            return Difference.Identical;
        if (!pField.isEqualityField())
            return Difference.Identical;

        /* Call the function from the interface */
        return theCurr.fieldChanged(pField, theOriginal);
    }
}

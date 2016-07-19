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

import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;

/**
 * ValueSet control.
 */
public class MetisValueSetControl {
    /**
     * The ValueSet History.
     */
    private final MetisValueSetHistory theHistory;

    /**
     * Constructor.
     */
    public MetisValueSetControl() {
        theHistory = new MetisValueSetHistory();
    }

    /**
     * Determine whether the item is deleted.
     * @return <code>true/false</code>
     */
    public boolean isDeleted() {
        return getValueSet().isDeletion();
    }

    /**
     * Get the changeable values object for this item.
     * @return the object
     */
    public MetisValueSet getValueSet() {
        return theHistory.getValueSet();
    }

    /**
     * Get original values.
     * @return original values
     */
    public MetisValueSet getOriginalValues() {
        return theHistory.getOriginalValues();
    }

    /**
     * Push Item to the history.
     * @param pVersion the new version
     */
    public void pushHistory(final int pVersion) {
        theHistory.pushHistory(pVersion);
    }

    /**
     * Is there any history?
     * @return true/false
     */
    public boolean hasHistory() {
        return theHistory.hasHistory();
    }

    /**
     * Clear history.
     */
    public void clearHistory() {
        theHistory.clearHistory();
    }

    /**
     * Reset history.
     */
    public void resetHistory() {
        theHistory.resetHistory();
    }

    /**
     * Determines whether a particular field has changed.
     * @param pField the field
     * @return the difference
     */
    public MetisDifference fieldChanged(final MetisField pField) {
        return theHistory.fieldChanged(pField);
    }
}

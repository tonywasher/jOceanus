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

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataTableItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataVersionedItem;

/**
 * Data Version History.
 */
public class MetisDataVersionControl
        implements MetisDataTableItem {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MetisDataVersionControl.class);

    /**
     * Id Field Id.
     */
    private static final MetisDataField FIELD_ID = FIELD_DEFS.declareLocalField(MetisDataResource.DATA_ID.getValue());

    /**
     * Version Field Id.
     */
    private static final MetisDataField FIELD_VERSION = FIELD_DEFS.declareLocalField(MetisDataResource.DATA_VERSION.getValue());

    /**
     * Deleted Field Id.
     */
    private static final MetisDataField FIELD_DELETED = FIELD_DEFS.declareLocalField(MetisDataResource.DATA_DELETED.getValue());

    /**
     * State Field Id.
     */
    private static final MetisDataField FIELD_STATE = FIELD_DEFS.declareLocalField(MetisDataResource.DATA_STATE.getValue());

    /**
     * EditState Field Id.
     */
    private static final MetisDataField FIELD_EDIT = FIELD_DEFS.declareLocalField(MetisDataResource.DATA_EDITSTATE.getValue());

    /**
     * History Field Id.
     */
    private static final MetisDataField FIELD_HISTORY = FIELD_DEFS.declareLocalField(MetisDataResource.DATA_HISTORY.getValue());

    /**
     * Validation Field Id.
     */
    private static final MetisDataField FIELD_VALIDATION = FIELD_DEFS.declareLocalField(MetisDataResource.DATA_ERRORS.getValue());

    /**
     * The history of values for this object.
     */
    private final MetisDataVersionHistory theHistory;

    /**
     * The Data Item Validation.
     */
    private final MetisDataItemValidation theValidation;

    /**
     * The id.
     */
    private Integer theId;

    /**
     * The dataState.
     */
    private MetisDataState theDataState;

    /**
     * The editState.
     */
    private MetisDataEditState theEditState;

    /**
     * Constructor.
     * @param pItem the owning item
     */
    protected MetisDataVersionControl(final MetisDataVersionedItem pItem) {
        /* Allocate the history */
        theHistory = new MetisDataVersionHistory(pItem);

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
     * Obtain the State of item.
     * @return the state
     */
    public MetisDataState getState() {
        return theDataState;
    }

    /**
     * Obtain the editState of item.
     * @return the editState
     */
    public MetisDataEditState getEditState() {
        return theEditState;
    }

    /**
     * Obtain the DataVersionHistory.
     * @return the validation
     */
    public MetisDataVersionHistory getHistory() {
        return theHistory;
    }

    /**
     * Obtain the DataItemValidation.
     * @return the validation
     */
    public MetisDataItemValidation getValidation() {
        return theValidation;
    }

    /**
     * Obtain the current ValueSet of item.
     * @return the current valueSet
     */
    public MetisDataVersionValues getValueSet() {
        return theHistory.getValueSet();
    }

    /**
     * Get original values.
     * @return original values
     */
    public MetisDataVersionValues getOriginalValues() {
        return theHistory.getOriginalValues();
    }

    /**
     * Obtain the Version of item.
     * @return the version
     */
    public Integer getVersion() {
        return getValueSet().getVersion();
    }

    /**
     * Obtain the original Version of item.
     * @return the version
     */
    public Integer getOriginalVersion() {
        return getOriginalValues().getVersion();
    }

    /**
     * is the object in a deleted state?
     * @return true/false
     */
    public boolean isDeleted() {
        return getValueSet().isDeletion();
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle fields */
        if (FIELD_ID.equals(pField)) {
            return theId;
        }
        if (FIELD_VERSION.equals(pField)) {
            return getVersion();
        }
        if (FIELD_DELETED.equals(pField)) {
            return isDeleted()
                               ? Boolean.TRUE
                               : MetisDataFieldValue.SKIP;
        }
        if (FIELD_STATE.equals(pField)) {
            return theDataState;
        }
        if (FIELD_EDIT.equals(pField)) {
            return theEditState;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory.hasHistory()
                                           ? theHistory
                                           : MetisDataFieldValue.SKIP;
        }
        if (FIELD_VALIDATION.equals(pField)) {
            return theValidation.hasErrors()
                                             ? theValidation
                                             : MetisDataFieldValue.SKIP;
        }

        /* Not found */
        return MetisDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }

    /**
     * Initialise the current values.
     * @param pValues the current values
     */
    public void setValues(final MetisDataVersionValues pValues) {
        theHistory.setValues(pValues);
        adjustState();
    }

    /**
     * Push Item to the history.
     * @param pVersion the new version
     */
    public void pushHistory(final int pVersion) {
        theHistory.pushHistory(pVersion);
    }

    /**
     * popItem from the history and remove from history.
     */
    public void popTheHistory() {
        theHistory.popTheHistory();
    }

    /**
     * popItem from the history if equal to current.
     * @return was a change made
     */
    public boolean maybePopHistory() {
        return theHistory.maybePopHistory();
    }

    /**
     * Is there any history.
     * @return whether there are entries in the history list
     */
    public boolean hasHistory() {
        return theHistory.hasHistory();
    }

    /**
     * Clear history.
     */
    public void clearHistory() {
        theHistory.clearHistory();
        adjustState();
    }

    /**
     * Reset history.
     */
    public void resetHistory() {
        theHistory.resetHistory();
        adjustState();
    }

    /**
     * Set history explicitly.
     * @param pBase the base item
     */
    public void setHistory(final MetisDataVersionValues pBase) {
        theHistory.setHistory(pBase);
        adjustState();
    }

    /**
     * Condense history.
     * @param pNewVersion the new maximum version
     */
    public void condenseHistory(final int pNewVersion) {
        theHistory.condenseHistory(pNewVersion);
    }

    /**
     * Determines whether a particular field has changed.
     * @param pField the field
     * @return the difference
     */
    public MetisDataDifference fieldChanged(final MetisDataField pField) {
        return theHistory.fieldChanged(pField);
    }

    /**
     * Adjust State of item.
     */
    public void adjustState() {
        theDataState = determineState();
        theEditState = determineEditState();
    }

    /**
     * Determine dataState of item.
     * @return the dataState
     */
    private MetisDataState determineState() {
        MetisDataVersionValues myCurr = getValueSet();
        MetisDataVersionValues myOriginal = getOriginalValues();

        /* If we are a new element */
        if (myOriginal.getVersion() > 0) {
            /* Return status */
            return myCurr.isDeletion()
                                       ? MetisDataState.DELNEW
                                       : MetisDataState.NEW;
        }

        /* If we have no changes we are CLEAN */
        if (myCurr.getVersion() == 0) {
            return MetisDataState.CLEAN;
        }

        /* If we are deleted return so */
        if (myCurr.isDeletion()) {
            return MetisDataState.DELETED;
        }

        /* Return RECOVERED or CHANGED depending on whether we started as deleted */
        return myOriginal.isDeletion()
                                       ? MetisDataState.RECOVERED
                                       : MetisDataState.CHANGED;
    }

    /**
     * Determine editState of item.
     * @return the editState
     */
    private MetisDataEditState determineEditState() {
        /* If we have errors */
        if (theValidation.hasErrors()) {
            /* Return status */
            return MetisDataEditState.ERROR;
        }

        /* If we have no changes we are CLEAN */
        return getVersion() == 0
                                 ? MetisDataEditState.CLEAN
                                 : MetisDataEditState.DIRTY;
    }
}

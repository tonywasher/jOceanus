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
package net.sourceforge.joceanus.jmetis.field;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataEditState;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;

/**
 * Data Version Control.
 */
public class MetisFieldVersionedItem
        implements MetisFieldTableItem {
    /**
     * Report fields.
     */
    private static final MetisFieldVersionedSet<MetisFieldVersionedItem> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MetisFieldVersionedItem.class);

    /**
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_ID.getValue(), MetisFieldVersionedItem::getIndexedId);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_ITEMTYPE.getValue(), MetisFieldVersionedItem::getItemType);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_VERSION.getValue(), MetisFieldVersionedItem::getVersion);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_DELETED.getValue(), MetisFieldVersionedItem::isDeleted);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_STATE.getValue(), MetisFieldVersionedItem::getState);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_EDITSTATE.getValue(), MetisFieldVersionedItem::getEditState);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_HISTORY.getValue(), MetisFieldVersionedItem::getHistory);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_ERRORS.getValue(), MetisFieldVersionedItem::getValidation);
    }

    /**
     * The history of values for this object.
     */
    private final MetisFieldVersionHistory theHistory;

    /**
     * The Item Validation.
     */
    private final MetisFieldValidation theValidation;

    /**
     * The id.
     */
    private Integer theId;

    /**
     * The itemType.
     */
    private MetisFieldItemType theItemType;

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
     */
    protected MetisFieldVersionedItem() {
        /* Allocate the history */
        final MetisFieldVersionValues myValues = newVersionValues();
        theHistory = new MetisFieldVersionHistory(myValues);

        /* Allocate the validation */
        theValidation = new MetisFieldValidation();
    }

    /**
     * Constructor.
     * @param pValues the initial values
     */
    protected MetisFieldVersionedItem(final MetisFieldVersionValues pValues) {
        /* Allocate the history */
        theHistory = new MetisFieldVersionHistory(pValues);

        /* Allocate the validation */
        theValidation = new MetisFieldValidation();
    }

    /**
     * Obtain new version values.
     * @return new values
     */
    protected MetisFieldVersionValues newVersionValues() {
        return new MetisFieldVersionValues(this);
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
     * Obtain the itemType.
     * @return the item type
     */
    public MetisFieldItemType getItemType() {
        return theItemType;
    }

    /**
     * Set ItemType.
     * @param pItemType the itemType
     */
    public void setItemType(final MetisFieldItemType pItemType) {
        theItemType = pItemType;
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
    public MetisFieldVersionHistory getHistory() {
        return theHistory;
    }

    /**
     * Obtain the Validation.
     * @return the validation
     */
    public MetisFieldValidation getValidation() {
        return theValidation;
    }

    /**
     * Obtain the current ValueSet of item.
     * @return the current valueSet
     */
    public MetisFieldVersionValues getValueSet() {
        return theHistory.getValueSet();
    }

    /**
     * Get original values.
     * @return original values
     */
    public MetisFieldVersionValues getOriginalValues() {
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
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }

    /**
     * Initialise the current values.
     * @param pValues the current values
     */
    public void setValues(final MetisFieldVersionValues pValues) {
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
    public void setHistory(final MetisFieldVersionValues pBase) {
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
    public MetisDataDifference fieldChanged(final MetisFieldDef pField) {
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
        final MetisFieldVersionValues myCurr = getValueSet();
        final MetisFieldVersionValues myOriginal = getOriginalValues();

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

    /**
     * Obtain a versioned field.
     * @param pId the field id
     * @return the versioned field
     */
    public MetisFieldVersionedDef getVersionedField(final MetisDataFieldId pId) {
        final MetisFieldDef myField = getDataFieldSet().getField(pId);
        return myField instanceof MetisFieldVersionedDef
                                                         ? (MetisFieldVersionedDef) myField
                                                         : null;
    }
}

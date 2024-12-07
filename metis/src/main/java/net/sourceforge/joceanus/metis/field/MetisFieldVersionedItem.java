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
package net.sourceforge.joceanus.metis.field;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataEditState;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataDeletableItem;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.data.MetisDataState;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldUpdatableItem;
import net.sourceforge.joceanus.metis.field.MetisFieldValidation.MetisFieldError;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

/**
 * Data Version Control.
 */
public abstract class MetisFieldVersionedItem
        implements MetisFieldTableItem, MetisDataDeletableItem, MetisFieldUpdatableItem {
    /**
     * Report fields.
     */
    private static final MetisFieldVersionedSet<MetisFieldVersionedItem> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MetisFieldVersionedItem.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_ID, MetisFieldVersionedItem::getIndexedId);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_ITEMTYPE, MetisFieldVersionedItem::getItemType);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_VERSION, MetisFieldVersionedItem::getVersion);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_DELETED, MetisFieldVersionedItem::isDeleted);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_STATE, MetisFieldVersionedItem::getState);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_EDITSTATE, MetisFieldVersionedItem::getEditState);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_HISTORY, MetisFieldVersionedItem::getValuesHistory);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_ERRORS, MetisFieldVersionedItem::getValidation);
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
        theEditState = MetisDataEditState.CLEAN;
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
        return determineState();
    }

    /**
     * Obtain the editState of item.
     * @return the editState
     */
    public MetisDataEditState getEditState() {
        return theEditState;
    }

    @Override
    public MetisFieldVersionHistory getValuesHistory() {
        return theHistory;
    }

    /**
     * Obtain the Validation.
     * @return the validation
     */
    public MetisFieldValidation getValidation() {
        return theValidation;
    }

    @Override
    public MetisFieldVersionValues getValues() {
        return theHistory.getValueSet();
    }

    @Override
    public MetisFieldVersionValues getOriginalValues() {
        return theHistory.getOriginalValues();
    }

    /**
     * Obtain the Version of item.
     * @return the version
     */
    public Integer getVersion() {
        return getValues().getVersion();
    }

    /**
     * Obtain the original Version of item.
     * @return the version
     */
    public Integer getOriginalVersion() {
        return getOriginalValues().getVersion();
    }

    @Override
    public boolean isDeleted() {
        return getValues().isDeletion();
    }

    @Override
    public void setDeleted(final boolean pFlag) {
        /* If the state has changed */
        if (pFlag != isDeleted()) {
            /* Push history and set flag */
            pushHistory();
            getValues().setDeletion(pFlag);
        }
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }

    /**
     * Set the Edit State.
     * @param pState the Edit Status
     */
    protected void setEditState(final MetisDataEditState pState) {
        theEditState = pState;
    }

    /**
     * Determine whether the item has Errors.
     * @return <code>true/false</code>
     */
    public boolean hasErrors() {
        return theEditState == MetisDataEditState.ERROR;
    }

    /**
     * Determine whether the item has Changes.
     * @return <code>true/false</code>
     */
    public boolean hasChanges() {
        return theEditState != MetisDataEditState.CLEAN;
    }

    /**
     * Determine whether the item is Valid.
     * @return <code>true/false</code>
     */
    public boolean isValid() {
        return theEditState == MetisDataEditState.CLEAN
                || theEditState == MetisDataEditState.VALID;
    }

    @Override
    public boolean hasErrors(final MetisDataFieldId pFieldId) {
        return pFieldId != null
                && theValidation.hasErrors(pFieldId);
    }

    /**
     * Add an error for this item.
     * @param pError the error text
     * @param pFieldId the associated field
     */
    public void addError(final String pError,
                         final MetisDataFieldId pFieldId) {
        /* Set edit state and add the error */
        theEditState = MetisDataEditState.ERROR;
        theValidation.addError(pError, pFieldId);
    }

    /**
     * Clear all errors for this item.
     */
    public void clearErrors() {
        theEditState = getValues().getVersion() > 0
                ? MetisDataEditState.DIRTY
                : MetisDataEditState.CLEAN;
        theValidation.clearErrors();
    }

    /**
     * Get the first error element for an item.
     * @return the first error (or <code>null</code>)
     */
    public MetisFieldError getFirstError() {
        return theValidation.getFirst();
    }

    @Override
    public String getFieldErrors(final MetisDataFieldId pField) {
        return pField != null
                ? theValidation.getFieldErrors(pField)
                : null;
    }

    @Override
    public String getFieldErrors(final MetisDataFieldId[] pFields) {
        final MetisFieldSetDef myFieldSet = getDataFieldSet();
        final MetisFieldDef[] myFields = new MetisFieldDef[pFields.length];
        for (int i = 0; i < pFields.length; i++) {
            myFields[i] = myFieldSet.getField(pFields[i]);
        }
        return theValidation.getFieldErrors(myFields);
    }

    /**
     * Initialise the current values.
     * @param pValues the current values
     */
    public void setValues(final MetisFieldVersionValues pValues) {
        theHistory.setValues(pValues);
        adjustState();
    }

    @Override
    public void pushHistory() {
        final int myVersion = getNextVersion();
        theHistory.pushHistory(myVersion);
    }

    /**
     * Push history to a specific version.
     * @param pVersion the version
     */
    public void pushHistory(final int pVersion) {
        theHistory.pushHistory(pVersion);
    }

    @Override
    public void popHistory() {
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

    @Override
    public boolean checkForHistory() {
        return theHistory.maybePopHistory();
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
    public MetisDataDifference fieldChanged(final MetisDataFieldId pField) {
        final MetisFieldDef myField = getDataFieldSet().getField(pField);
        return theHistory.fieldChanged(myField);
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
        theEditState = determineEditState();
    }

    /**
     * Determine dataState of item.
     * @return the dataState
     */
    private MetisDataState determineState() {
        final MetisFieldVersionValues myCurr = getValues();
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

        /* We must have changed */
        return MetisDataState.CHANGED;
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

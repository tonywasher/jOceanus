/*******************************************************************************
 * jDataModels: Data models
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
package net.sourceforge.jOceanus.jDataModels.data;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.DataState;
import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.EditState;
import net.sourceforge.jOceanus.jDataManager.ItemValidation;
import net.sourceforge.jOceanus.jDataManager.ItemValidation.ErrorElement;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataValues;
import net.sourceforge.jOceanus.jDataManager.ValueSet;
import net.sourceforge.jOceanus.jDataManager.ValueSetHistory;
import net.sourceforge.jOceanus.jDataModels.data.DataList.ListStyle;
import net.sourceforge.jOceanus.jFieldSet.JFieldSetItem;
import net.sourceforge.jOceanus.jFieldSet.JFieldState;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedValueSet;
import net.sourceforge.jOceanus.jSortedList.OrderedIdItem;

/**
 * Provides the abstract DataItem class as the basis for data items. The implementation of the interface means that this object can only be held in one list at
 * a time and is unique within that list
 * @see DataList
 */
public abstract class DataItem
        implements OrderedIdItem<Integer>, JDataValues, JFieldSetItem {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(DataItem.class.getSimpleName());

    /**
     * Validation error.
     */
    public static final String ERROR_VALIDATION = "Failed Validation";

    /**
     * Resolution error.
     */
    public static final String ERROR_RESOLUTION = "Failed Resolution";

    /**
     * Duplicate Id error.
     */
    public static final String ERROR_DUPLICATE = "Duplicate Value";

    /**
     * Unknown Id error.
     */
    public static final String ERROR_UNKNOWN = "Unknown Value";

    /**
     * Existing value error.
     */
    public static final String ERROR_EXIST = "Value must be null";

    /**
     * Missing value error.
     */
    public static final String ERROR_MISSING = "Missing Value";

    /**
     * Value too long error.
     */
    public static final String ERROR_LENGTH = "Value too long";

    /**
     * Value negative error.
     */
    public static final String ERROR_NEGATIVE = "Value is negative";

    /**
     * Value zero error.
     */
    public static final String ERROR_ZERO = "Value is zero";

    /**
     * Value outside valid range.
     */
    public static final String ERROR_RANGE = "Value outside valid range";

    /**
     * Value disabled error.
     */
    public static final String ERROR_DISABLED = "Value is disabled";

    /**
     * Instance ReportFields.
     */
    private final JDataFields theFields;

    @Override
    public JDataFields getDataFields() {
        return theFields;
    }

    /**
     * Declare fields.
     * @return the fields
     */
    public abstract JDataFields declareFields();

    /**
     * ValueSet.
     */
    private ValueSet theValueSet;

    @Override
    public final void declareValues(final ValueSet pValues) {
        theValueSet = pValues;
    }

    @Override
    public ValueSet getValueSet() {
        return theValueSet;
    }

    /**
     * Obtain valueSet version.
     * @return the valueSet version
     */
    public int getValueSetVersion() {
        return theValueSet.getVersion();
    }

    /**
     * Id Field Id.
     */
    public static final JDataField FIELD_ID = FIELD_DEFS.declareEqualityField("Id");

    /**
     * List Field Id.
     */
    public static final JDataField FIELD_LIST = FIELD_DEFS.declareLocalField("List");

    /**
     * Base Field Id.
     */
    public static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField("Base");

    /**
     * Active Field Id.
     */
    public static final JDataField FIELD_ACTIVE = FIELD_DEFS.declareLocalField("isActive");

    /**
     * Deleted Field Id.
     */
    public static final JDataField FIELD_DELETED = FIELD_DEFS.declareLocalField(ValueSet.FIELD_DELETION);

    /**
     * DataState Field Id.
     */
    public static final JDataField FIELD_STATE = FIELD_DEFS.declareLocalField("State");

    /**
     * Edit State Field Id.
     */
    public static final JDataField FIELD_EDITSTATE = FIELD_DEFS.declareLocalField("EditState");

    /**
     * Version Field Id.
     */
    public static final JDataField FIELD_VERSION = FIELD_DEFS.declareLocalField(ValueSet.FIELD_VERSION);

    /**
     * Header Field Id.
     */
    public static final JDataField FIELD_HEADER = FIELD_DEFS.declareLocalField("Header");

    /**
     * History Field Id.
     */
    public static final JDataField FIELD_HISTORY = FIELD_DEFS.declareLocalField("History");

    /**
     * Errors Field Id.
     */
    public static final JDataField FIELD_ERRORS = FIELD_DEFS.declareLocalField("Errors");

    @Override
    public String formatObject() {
        return getDataFields().getName();
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* If this is a valueSet field */
        if (pField.isValueSetField()) {
            /* Access from valueSet */
            return theValueSet.getValue(pField);
        }

        /* If the field is not an attribute handle normally */
        if (FIELD_ID.equals(pField)) {
            return getId();
        }
        if (FIELD_LIST.equals(pField)) {
            return getList();
        }
        if (FIELD_ACTIVE.equals(pField)) {
            return isActive();
        }
        if (FIELD_BASE.equals(pField)) {
            return (theBase == null)
                    ? JDataFieldValue.SkipField
                    : theBase;
        }
        if (FIELD_STATE.equals(pField)) {
            return getState();
        }
        if (FIELD_EDITSTATE.equals(pField)) {
            return getEditState();
        }
        if (FIELD_DELETED.equals(pField)) {
            return isDeleted()
                    ? Boolean.TRUE
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_VERSION.equals(pField)) {
            return (theValueSet != null)
                    ? theValueSet.getVersion()
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_HEADER.equals(pField)) {
            return (isHeader)
                    ? isHeader
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return hasHistory()
                    ? theHistory
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_ERRORS.equals(pField)) {
            return hasErrors()
                    ? theErrors
                    : JDataFieldValue.SkipField;
        }

        /* Not recognised */
        return JDataFieldValue.UnknownField;
    }

    /**
     * The list to which this item belongs.
     */
    private DataList<?> theList = null;

    /**
     * Self reference.
     */
    private DataItem theItem;

    /**
     * The item that this DataItem is based upon.
     */
    private DataItem theBase = null;

    /**
     * The Edit state of this item {@link EditState}.
     */
    private EditState theEdit = EditState.CLEAN;

    /**
     * Is the item a header.
     */
    private boolean isHeader = false;

    /**
     * The id number of the item.
     */
    private Integer theId = 0;

    /**
     * The history control {@link ValueSetHistory}.
     */
    private ValueSetHistory theHistory = null;

    /**
     * The validation control {@link ItemValidation}.
     */
    private ItemValidation theErrors = null;

    /**
     * Is the item active.
     */
    private boolean isActive = false;

    /**
     * Obtain the list.
     * @return the list
     */
    public DataList<?> getList() {
        return theList;
    }

    /**
     * Obtain the dataSet.
     * @return the dataSet
     */
    public DataSet<?> getDataSet() {
        return theList.getDataSet();
    }

    /**
     * Get the list style for this item.
     * @return the list style
     */
    public ListStyle getStyle() {
        return theList.getStyle();
    }

    /**
     * Get the Id for this item.
     * @return the Id
     */
    public Integer getId() {
        return theId;
    }

    @Override
    public Integer getOrderedId() {
        return theId;
    }

    /**
     * Is the Item Active?
     * @return <code>true/false</code>
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Get the EditState for this item.
     * @return the EditState
     */
    public EditState getEditState() {
        return theEdit;
    }

    /**
     * Get the State for this item.
     * @return the State
     */
    public DataState getState() {
        return DataState.determineState(theHistory);
    }

    /**
     * Get the Generation.
     * @return the Generation
     */
    public int getGeneration() {
        return theList.getGeneration();
    }

    /**
     * Get the history for this item.
     * @return the history
     */
    protected ValueSetHistory getHistory() {
        return theHistory;
    }

    /**
     * Determine whether the item is visible to standard searches.
     * @param bDeleted <code>true/false</code>
     */
    public void setDeleted(final boolean bDeleted) {
        /* If the state has changed */
        if (bDeleted != isDeleted()) {
            /* Push history and set flag */
            pushHistory();
            theValueSet.setDeletion(bDeleted);
        }
    }

    /**
     * Set the Edit State.
     * @param pState the Edit Status
     */
    protected void setEditState(final EditState pState) {
        theEdit = pState;
    }

    @Override
    public boolean isEditable() {
        return !isDeleted();
    }

    /**
     * Determine whether the item is visible to standard searches.
     * @return <code>true/false</code>
     */
    public boolean isDeleted() {
        return theValueSet.isDeletion();
    }

    /**
     * Determine whether the item is a header item.
     * @return <code>true/false</code>
     */
    public boolean isHeader() {
        return isHeader;
    }

    /**
     * Set the header indication.
     * @param pHeader true/false
     */
    protected void setHeader(final boolean pHeader) {
        isHeader = pHeader;
    }

    /**
     * Set the id of the item.
     * @param id of the item
     */
    public void setId(final Integer id) {
        theId = id;
    }

    /**
     * Determine whether the item is locked (overridden if required).
     * @return <code>true/false</code>
     */
    public boolean isLocked() {
        return false;
    }

    /**
     * Determine whether the list is locked (overridden if required).
     * @return <code>true/false</code>
     */
    public boolean isListLocked() {
        return false;
    }

    /**
     * DeRegister any infoSet links.
     */
    public void deRegister() {
    }

    /**
     * Clear the Item Active flag.
     */
    protected void clearActive() {
        isActive = false;
    }

    /**
     * Touch the item.
     * @param pObject object that references the item
     */
    public void touchItem(final DataItem pObject) {
        isActive = true;
    }

    /**
     * Touch underlying items that are referenced by this item.
     */
    public void touchUnderlyingItems() {
    }

    /**
     * Obtain properly cast reference to self.
     * @return self reference
     */
    public DataItem getItem() {
        return theItem;
    }

    /**
     * Get the base item for this item.
     * @return the Base item or <code>null</code>
     */
    public DataItem getBase() {
        return theBase;
    }

    /**
     * Set the base item for this item.
     * @param pBase the Base item
     */
    public void setBase(final DataItem pBase) {
        theBase = pBase;
    }

    /**
     * Unlink the item from the list.
     */
    public void unLink() {
        theList.remove(this);
    }

    /**
     * Determine whether the item has changes.
     * @return <code>true/false</code>
     */
    public boolean hasHistory() {
        return (theHistory != null)
               && (theHistory.hasHistory());
    }

    /**
     * Set new version.
     */
    public void setNewVersion() {
        theValueSet.setVersion(theList.getVersion() + 1);
    }

    /**
     * Clear the history for the item (leaving current values).
     */
    public void clearHistory() {
        theHistory.clearHistory();
        theEdit = EditState.CLEAN;
    }

    /**
     * Reset the history for the item (restoring original values).
     */
    public void resetHistory() {
        theHistory.resetHistory();
    }

    /**
     * Set Change history for an update list so that the first and only entry in the change list is the original values of the base.
     * @param pBase the base item
     */
    public final void setHistory(final DataItem pBase) {
        theHistory.setHistory(pBase.getOriginalValues());
    }

    /**
     * Return the base history object.
     * @return the original values for this object
     */
    public ValueSet getOriginalValues() {
        return theHistory.getOriginalValues();
    }

    /**
     * Check to see whether any changes were made. If no changes were made remove last saved history since it is not needed.
     * @return <code>true</code> if changes were made, <code>false</code> otherwise
     */
    public boolean checkForHistory() {
        return theHistory.maybePopHistory();
    }

    /**
     * Push current values into history buffer ready for changes to be made.
     */
    public void pushHistory() {
        theHistory.pushHistory(theList.getVersion() + 1);
    }

    /**
     * Remove the last changes for the history buffer and restore values from it.
     */
    public void popHistory() {
        rewindToVersion(theList.getVersion());
    }

    /**
     * Rewind item to the required version.
     * @param pVersion the version to rewind to
     */
    public void rewindToVersion(final int pVersion) {
        /* If the item was newly created */
        if (getOriginalValues().getVersion() > pVersion) {
            /* Remove from list */
            unLink();
            deRegister();

            /* Return */
            return;
        }

        /* Loop while version is too high */
        while (theValueSet.getVersion() > pVersion) {
            /* Pop history */
            theHistory.popTheHistory();
        }

        /* clear errors */
        clearErrors();
    }

    /**
     * Determine whether a particular field has changed in this edit view.
     * @param pField the field to test
     * @return <code>true/false</code>
     */
    public Difference fieldChanged(final JDataField pField) {
        return ((pField != null) && (pField.isValueSetField()))
                ? theHistory.fieldChanged(pField)
                : Difference.Identical;
    }

    /**
     * Determine whether the item has Errors.
     * @return <code>true/false</code>
     */
    public boolean hasErrors() {
        return (theEdit == EditState.ERROR);
    }

    /**
     * Determine whether the item has Changes.
     * @return <code>true/false</code>
     */
    public boolean hasChanges() {
        return (theEdit != EditState.CLEAN);
    }

    /**
     * Determine whether the item is Valid.
     * @return <code>true/false</code>
     */
    public boolean isValid() {
        return ((theEdit == EditState.CLEAN) || (theEdit == EditState.VALID));
    }

    /**
     * Determine whether a particular field has Errors.
     * @param pField the particular field
     * @return <code>true/false</code>
     */
    public boolean hasErrors(final JDataField pField) {
        return (pField != null)
                ? theErrors.hasErrors(pField)
                : false;
    }

    /**
     * Note that this item has been validated.
     */
    public void setValidEdit() {
        DataState myState = getState();
        if (myState == DataState.CLEAN) {
            theEdit = EditState.CLEAN;
        } else if (theList.getStyle() == ListStyle.CORE) {
            theEdit = EditState.DIRTY;
        } else {
            theEdit = EditState.VALID;
        }
    }

    /**
     * Clear all errors for this item.
     */
    public void clearErrors() {
        theEdit = (theValueSet.getVersion() > 0)
                ? EditState.DIRTY
                : EditState.CLEAN;
        theErrors.clearErrors();
    }

    /**
     * Add an error for this item.
     * @param pError the error text
     * @param pField the associated field
     */
    public void addError(final String pError,
                         final JDataField pField) {
        /* Set edit state and add the error */
        theEdit = EditState.ERROR;
        theErrors.addError(pError, pField);

        /* Note that the list has errors */
        theList.setEditState(EditState.ERROR);
    }

    @Override
    public String getFieldErrors(final JDataField pField) {
        return (pField != null)
                ? theErrors.getFieldErrors(pField)
                : null;
    }

    @Override
    public String getFieldErrors(final JDataField[] pFields) {
        return theErrors.getFieldErrors(pFields);
    }

    /**
     * Get the first error element for an item.
     * @return the first error (or <code>null</code>)
     */
    public ErrorElement getFirstError() {
        return theErrors.getFirst();
    }

    /**
     * Copy flags.
     * @param pItem the original item
     */
    private void copyFlags(final DataItem pItem) {
        isActive = pItem.isActive();
    }

    /**
     * Resolve all references to current dataSet.
     * @throws JDataException on error
     */
    public void resolveDataSetLinks() throws JDataException {
    }

    /**
     * Construct a new item.
     * @param pList the list that this item is associated with
     * @param uId the Id of the new item (or 0 if not yet known)
     */
    public DataItem(final DataList<?> pList,
                    final Integer uId) {
        /* Record list and item references */
        theId = uId;
        theList = pList;
        theItem = pList.getBaseClass().cast(this);

        /* Declare fields (allowing for subclasses) */
        theFields = declareFields();

        /* Create validation control */
        theErrors = new ItemValidation();

        /* Create history control */
        theHistory = new ValueSetHistory();

        /* Allocate initial value set and declare it */
        ValueSet myValues = (this instanceof EncryptedItem)
                ? new EncryptedValueSet(this)
                : new ValueSet(this);
        declareValues(myValues);
        theHistory.setValues(myValues);

        /* Allocate id */
        pList.setNewId(this);
    }

    /**
     * Construct a new item based on an old item.
     * @param pList the list that this item is associated with
     * @param pBase the old item
     */
    protected DataItem(final DataList<?> pList,
                       final DataItem pBase) {
        /* Initialise using standard constructor */
        this(pList, pBase.getId());

        /* Initialise the valueSet */
        theValueSet.copyFrom(pBase.getValueSet());

        /* Access the varying styles and the source state */
        ListStyle myStyle = pList.getStyle();
        ListStyle myBaseStyle = pBase.getList().getStyle();
        DataState myState = pBase.getState();

        /* Switch on the styles */
        switch (myStyle) {
        /* We are building an update list (from Core) */
            case UPDATE:
                switch (myState) {
                /* NEW/DELNEW need to be at version 1 */
                    case DELNEW:
                    case NEW:
                        theValueSet.setVersion(1);
                        break;

                    case DELETED:
                        pushHistory();
                        break;

                    /* Changed items need to have new values at version 1 and originals at version 0 */
                    case CHANGED:
                        setHistory(pBase);
                        break;

                    /* No change for other states */
                    default:
                        break;
                }

                /* Record the base item */
                theBase = pBase;
                break;

            /* We are building an edit item (from Core/Edit) */
            case EDIT:
                /* Switch on the base style */
                switch (myBaseStyle) {
                /* New item from core we need to link back and copy flags */
                    case CORE:
                        theBase = pBase;
                        copyFlags(pBase);
                        break;
                    /* Duplication in edit */
                    case EDIT:
                        /* set as a new item */
                        theValueSet.setVersion(pList.getVersion() + 1);

                        /* Reset the Id */
                        theId = 0;
                        pList.setNewId(this);
                        break;
                    default:
                        break;
                }
                break;

            /* We are building a CORE item */
            case CORE:
                /* set as a new item */
                theValueSet.setVersion(pList.getVersion() + 1);

                /* If we are adding from Edit */
                if (myBaseStyle == ListStyle.EDIT) {
                    /* Reset the Id */
                    theId = 0;
                    pList.setNewId(this);
                }
                break;

            /* Creation of copy element not allowed */
            case COPY:
                throw new IllegalArgumentException("Illegal creation of COPY element");

                /* Nothing special for other styles */
            case CLONE:
            case DIFFER:
            default:
                break;
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (pThat.getClass() != getClass()) {
            return false;
        }

        /* Access the object as a DataItem */
        DataItem myItem = (DataItem) pThat;

        /* Check the id */
        if (compareId(myItem) != 0) {
            return false;
        }

        /* Loop through the fields */
        Iterator<JDataField> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            JDataField myField = myIterator.next();

            /* Skip if not used in equality */
            if (!myField.isEqualityField()) {
                continue;
            }

            /* Access the values */
            Object myValue = getFieldValue(myField);
            Object myNew = myItem.getFieldValue(myField);

            /* Check the field */
            if (!Difference.isEqual(myValue, myNew)) {
                return false;
            }
        }

        /* Return identical */
        return true;
    }

    @Override
    public int hashCode() {
        /* Initialise hash code */
        int myHash = theId;

        /* Loop through the fields */
        Iterator<JDataField> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            JDataField myField = myIterator.next();

            /* Skip if not used in equality */
            if (!myField.isEqualityField()) {
                continue;
            }

            /* Access the values */
            Object myValue = getFieldValue(myField);

            /* Adjust existing hash */
            myHash *= DataSet.HASH_PRIME;

            /* Add the hash for the field */
            if (myValue != null) {
                myHash += myValue.hashCode();
            }
        }

        /* Return the hash */
        return myHash;
    }

    /**
     * compareTo another dataItem.
     * @param pThat the DataItem to compare
     * @return the order
     */
    protected int compareId(final DataItem pThat) {
        return (theId - pThat.theId);
    }

    /**
     * Get the state of the underlying record.
     * @return the underlying state
     */
    protected DataState getBaseState() {
        DataItem myBase = getBase();
        return (myBase == null)
                ? DataState.NOSTATE
                : myBase.getState();
    }

    /**
     * Determine index of element within the list.
     * @return The index
     */
    public int indexOf() {
        /* Return index */
        return theList.indexOf(this);
    }

    /**
     * Apply changes to the item from a changed version. Overwritten by objects that have changes
     * @param pElement the changed element.
     * @return were changes made?
     */
    public boolean applyChanges(final DataItem pElement) {
        return false;
    };

    /**
     * Validate the element Dirty items become valid.
     */
    public void validate() {
        if (getEditState() == EditState.DIRTY) {
            setEditState(EditState.VALID);
        }
    }

    @Override
    public JFieldState getFieldState(final JDataField pField) {
        /* Determine DELETED state */
        if (isDeleted()) {
            return JFieldState.DELETED;

            /* Determine Error state */
        } else if ((hasErrors())
                   && (hasErrors(pField))) {
            return JFieldState.ERROR;

            /* Determine Changed state */
        } else if (fieldChanged(pField).isDifferent()) {
            return JFieldState.CHANGED;

            /* Determine standard states */
        } else {
            switch (getState()) {
                case NEW:
                    return JFieldState.NEW;
                case RECOVERED:
                    return JFieldState.RESTORED;
                default:
                    return JFieldState.NORMAL;
            }
        }
    }

    @Override
    public JFieldState getItemState() {
        /* Determine DELETED state */
        if (isDeleted()) {
            return JFieldState.DELETED;

            /* Determine Error state */
        } else if (hasErrors()) {
            return JFieldState.ERROR;

            /* Determine Changed state */
        } else if (hasHistory()) {
            return JFieldState.CHANGED;

            /* Determine standard states */
        } else {
            switch (getState()) {
                case NEW:
                    return JFieldState.NEW;
                case RECOVERED:
                    return JFieldState.RESTORED;
                default:
                    return JFieldState.NORMAL;
            }
        }
    }
}

/*******************************************************************************
 * JDataModel: Data models
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

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.HistoryControl;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject;
import net.sourceforge.JDataManager.JDataObject.JDataValues;
import net.sourceforge.JDataManager.ReportItem;
import net.sourceforge.JDataManager.ValueSet;
import uk.co.tolcroft.models.data.DataList.ListStyle;

/**
 * Provides the abstract DataItem class as the basis for data items. The implementation of the interface means
 * that this object can only be held in one list at a time and is unique within that list
 * @param <T> the data type
 * @see uk.co.tolcroft.models.data.DataList
 */
public abstract class DataItem<T extends DataItem<T>> extends ReportItem<T> implements JDataValues {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(DataItem.class.getSimpleName(),
            ReportItem.theLocalFields);

    /**
     * ValueSet.
     */
    private ValueSet theValueSet;

    @Override
    public void declareValues(final ValueSet pValues) {
        theValueSet = pValues;
    }

    @Override
    public ValueSet getValueSet() {
        return theValueSet;
    }

    /**
     * Id Field Id.
     */
    public static final JDataField FIELD_ID = FIELD_DEFS.declareEqualityField("Id");

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
    public static final JDataField FIELD_DELETED = FIELD_DEFS.declareLocalField("isDeleted");

    /**
     * DataState Field Id.
     */
    public static final JDataField FIELD_STATE = FIELD_DEFS.declareLocalField("State");

    /**
     * Edit State Field Id.
     */
    public static final JDataField FIELD_EDITSTATE = FIELD_DEFS.declareLocalField("EditState");

    /**
     * Next Version Field Id.
     */
    public static final JDataField FIELD_NEXTVERS = FIELD_DEFS.declareLocalField("NextVersion");

    /**
     * Version Field Id.
     */
    public static final JDataField FIELD_VERSION = FIELD_DEFS.declareLocalField("Version");

    /**
     * History Field Id.
     */
    public static final JDataField FIELD_HISTORY = FIELD_DEFS.declareLocalField("History");

    /**
     * Errors Field Id.
     */
    public static final JDataField FIELD_ERRORS = FIELD_DEFS.declareLocalField("Errors");

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* If the field is not an attribute handle normally */
        if (pField == FIELD_ID) {
            return getId();
        }
        if (pField == FIELD_ACTIVE) {
            return isActive();
        }
        if (pField == FIELD_BASE) {
            return getBase();
        }
        if (pField == FIELD_STATE) {
            return getState();
        }
        if (pField == FIELD_EDITSTATE) {
            return getEditState();
        }
        if (pField == FIELD_DELETED) {
            return isDeleted ? isDeleted : JDataObject.FIELD_SKIP;
        }
        if (pField == FIELD_NEXTVERS) {
            return (theHistory != null) ? getList().getNextVersion() : JDataObject.FIELD_SKIP;
        }
        if (pField == FIELD_VERSION) {
            return (theValueSet != null) ? theValueSet.getVersion() : JDataObject.FIELD_SKIP;
        }
        if (pField == FIELD_HISTORY) {
            return hasHistory() ? theHistory : JDataObject.FIELD_SKIP;
        }
        if (pField == FIELD_ERRORS) {
            return hasErrors() ? theErrors : JDataObject.FIELD_SKIP;
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    /**
     * The list to which this item belongs.
     */
    private DataList<?, T> theList = null;

    /**
     * Self reference (built as cast during constructor).
     */
    private T theItem;

    /**
     * The item that this DataItem is based upon.
     */
    private DataItem<?> theBase = null;

    /**
     * The Change state of this item {@link DataState}.
     */
    private DataState theState = DataState.NOSTATE;

    /**
     * The Edit state of this item {@link EditState}.
     */
    private EditState theEdit = EditState.CLEAN;

    /**
     * Is the item visible to standard searches.
     */
    private boolean isDeleted = false;

    /**
     * Is the item in the process of being changed.
     */
    private boolean isChangeing = false;

    /**
     * Is the item in the process of being restored.
     */
    private boolean isRestoring = false;

    /**
     * The id number of the item.
     */
    private Integer theId = 0;

    /**
     * The history control {@link HistoryControl}.
     */
    private HistoryControl theHistory = null;

    /**
     * The validation control {@link ValidationControl}.
     */
    private ValidationControl<T> theErrors = null;

    /**
     * Is the item active.
     */
    private boolean isActive = false;

    @Override
    public DataList<?, T> getList() {
        return theList;
    }

    /**
     * Get the list control for this item.
     * @return the list control
     */
    public ListStyle getStyle() {
        return theList.getStyle();
    }

    /**
     * Get the Id for this item.
     * @return the Id
     */
    public int getId() {
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
        return theState;
    }

    /**
     * Get the Generation.
     * @return the Generation
     */
    public int getGeneration() {
        return theList.getGeneration();
    }

    /**
     * Get the base item for this item.
     * @return the Base item or <code>null</code>
     */
    protected HistoryControl getHistory() {
        return theHistory;
    }

    /**
     * Determine whether the item is visible to standard searches.
     * @param bDeleted <code>true/false</code>
     */
    private void setDeleted(final boolean bDeleted) {
        isDeleted = bDeleted;
        theList.setHidden(theItem, isDeleted);
    }

    /**
     * Determine whether the item is in the process of being changed.
     * @param bChangeing <code>true/false</code>
     */
    protected void setChangeing(final boolean bChangeing) {
        isChangeing = bChangeing;
    }

    /**
     * Determine whether the item is in the process of being restored.
     * @param bRestoring <code>true/false</code>
     */
    protected void setRestoring(final boolean bRestoring) {
        isRestoring = bRestoring;
    }

    /**
     * Set the Data State.
     * @param pState the Data Status
     */
    protected void setDataState(final DataState pState) {
        theState = pState;
    }

    /**
     * Set the Edit State.
     * @param pState the Edit Status
     */
    protected void setEditState(final EditState pState) {
        theEdit = pState;
    }

    /**
     * Set the item as hidden to standard searches.
     */
    public void setHidden() {
        setDeleted(true);
    }

    /**
     * Determine whether the item is visible to standard searches.
     * @return <code>true/false</code>
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * Determine whether the item is in the process of being changed.
     * @return <code>true/false</code>
     */
    protected boolean isChangeing() {
        return isChangeing;
    }

    /**
     * Determine whether the item is in the process of being restored.
     * @return <code>true/false</code>
     */
    protected boolean isRestoring() {
        return isRestoring;
    }

    @Override
    public boolean isHidden() {
        return isDeleted;
    }

    /**
     * Determine whether the underlying base item is deleted.
     * @return <code>true/false</code>
     */
    public boolean isCoreDeleted() {
        DataItem<?> myBase = getBase();
        return (myBase != null) && (myBase.isDeleted);
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
    public void touchItem(final DataItem<?> pObject) {
        isActive = true;
    }

    /**
     * Obtain properly cast reference to self.
     * @return self reference
     */
    public T getItem() {
        return theItem;
    }

    /**
     * Get the base item for this item.
     * @return the Base item or <code>null</code>
     */
    public DataItem<?> getBase() {
        return theBase;
    }

    /**
     * Set the base item for this item.
     * @param pBase the Base item
     */
    public void setBase(final DataItem<?> pBase) {
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
        return (theHistory != null) && (theHistory.hasHistory());
    }

    /**
     * Clear the history for the item (leaving current values).
     */
    public void clearHistory() {
        theHistory.clearHistory();
    }

    /**
     * Reset the history for the item (restoring original values).
     */
    public void resetHistory() {
        theHistory.resetHistory();
    }

    /**
     * Set Change history for an update list so that the first and only entry in the change list is the
     * original values of the base.
     * @param pBase the base item
     */
    public void setHistory(final DataItem<?> pBase) {
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
     * Check to see whether any changes were made. If no changes were made remove last saved history since it
     * is not needed.
     * @return <code>true</code> if changes were made, <code>false</code> otherwise
     */
    public boolean checkForHistory() {
        return theHistory.maybePopHistory();
    }

    /**
     * Push current values into history buffer ready for changes to be made.
     */
    public void pushHistory() {
        theHistory.pushHistory();
    }

    /**
     * Remove the last changes for the history buffer and restore values from it.
     */
    public void popHistory() {
        theHistory.popTheHistory();
    }

    /**
     * Determine whether a particular field has changed in this edit view.
     * @param pField the field to test
     * @return <code>true/false</code>
     */
    public Difference fieldChanged(final JDataField pField) {
        return theHistory.fieldChanged(pField);
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
        return theErrors.hasErrors(pField);
    }

    /**
     * Note that this item has been validated.
     */
    public void setValidEdit() {
        switch (theList.getStyle()) {
            case CORE:
                if (theState == DataState.CLEAN) {
                    theEdit = EditState.CLEAN;
                } else {
                    theEdit = EditState.DIRTY;
                }
                break;
            default:
                if (isCoreDeleted()) {
                    theEdit = (isDeleted) ? EditState.CLEAN : EditState.VALID;
                } else if (isDeleted) {
                    theEdit = EditState.VALID;
                } else {
                    theEdit = ((hasHistory()) || (getBase() == null)) ? EditState.VALID : EditState.CLEAN;
                }
                break;
        }
    }

    /**
     * Clear all errors for this item.
     */
    public void clearErrors() {
        theEdit = EditState.CLEAN;
        theErrors.clearErrors();
    }

    /**
     * Add an error for this item.
     * @param pError the error text
     * @param pField the associated field
     */
    protected void addError(final String pError,
                            final JDataField pField) {
        theEdit = EditState.ERROR;
        theErrors.addError(pError, pField);
    }

    /**
     * Get the error text for a field.
     * @param pField the associated field
     * @return the error text
     */
    public String getFieldErrors(final JDataField pField) {
        return theErrors.getFieldErrors(pField);
    }

    /**
     * Get the error text for a set of fields.
     * @param pFields the set of fields
     * @return the error text
     */
    public String getFieldErrors(final JDataField[] pFields) {
        return theErrors.getFieldErrors(pFields);
    }

    /**
     * Get the first error element for an item.
     * @return the first error (or <code>null</code>)
     */
    public ValidationControl<T>.ErrorElement getFirstError() {
        return theErrors.getFirst();
    }

    /**
     * Copy flags.
     * @param pItem the original item
     */
    protected void copyFlags(final T pItem) {
        isActive = pItem.isActive();
    }

    /**
     * Allocate the initial value set and associated controls.
     */
    public void allocateValueSet() {
        /* Create history and validation control */
        theErrors = new ValidationControl<T>(theItem);

        /* Allocate history control */
        theHistory = new HistoryControl();

        /* Allocate initial value set and declare it */
        ValueSet myValues = new ValueSet(theItem);
        declareValues(myValues);
        theHistory.setValues(myValues);
    }

    /**
     * Construct a new item.
     * @param pList the list that this item is associated with
     * @param uId the Id of the new item (or 0 if not yet known)
     */
    public DataItem(final DataList<?, T> pList,
                    final Integer uId) {
        /* Initialise as a ReportItem */
        super(pList);

        /* Record list and item references */
        theId = uId;
        theList = pList;
        theItem = pList.getBaseClass().cast(this);

        /* Allocate the value set */
        allocateValueSet();
    }

    /**
     * Construct a new item based on an old item.
     * @param pList the list that this item is associated with
     * @param pBase the old item
     */
    protected DataItem(final DataList<?, T> pList,
                       final T pBase) {
        /* Initialise as a ReportItem */
        this(pList, pBase.getId());

        /* Initialise the valueSet */
        theValueSet.copyFrom(pBase.getValueSet());
    }

    /**
     * Get the state of the underlying record.
     * @return the underlying state
     */
    protected DataState getBaseState() {
        DataItem<?> myBase = getBase();
        return (myBase == null) ? DataState.NOSTATE : myBase.getState();
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
     * @return were changes made
     */
    public boolean applyChanges(final DataItem<?> pElement) {
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

    /**
     * State Management algorithm.
     * <p>
     * In a Core list we generally have three states
     * <ul>
     * <li>NEW - Newly created but not added to DB
     * <li>CLEAN - In sync with DB
     * <li>CHANGED - Changed from DB
     * </ul>
     * <p>
     * In addition we have the Delete States
     * <ul>
     * <li>DELETED - DELETED from CLEAN
     * <li>DELNEW - DELETED from NEW
     * <li>DELCHG - DELETED from CHANGED
     * </ul>
     * <p>
     * The reason for holding the DELETE states as three separate states is
     * <ol>
     * <li>To allow a restore to the correct state
     * <li>To ensure that re-synchronisation to DB does not attempt to Delete a DELNEW record which does not
     * exist anyway
     * </ol>
     * <p>
     * <ul>
     * <li>When changes are made to a NEW record it remains NEW
     * <li>When changes are made to a CLEAN/CHANGED record it becomes CHANGED
     * <li>No changes can be made to a DELETED etc record
     * </ul>
     * <p>
     * In an Update list we stick to the three states
     * <ul>
     * <li>NEW - Record needing an insert
     * <li>CHANGED - Record needing an update
     * <li>DELETED - Record requiring deletion
     * </ul>
     * <p>
     * The underlying Delete state is held in CoreState, allowing proper handling of DELNEW records
     * <p>
     * In Edit views, we start off with everything in CLEAN state which means that it is unchanged with
     * respect to the core. New additions to the Edit view become NEW, and changes and deletes are handled in
     * the same fashion as for core
     * <p>
     * For restore operations deletes are handled as follows
     * <ul>
     * <li>DELETED -> CLEAN
     * <li>DELNEW -> NEW
     * <li>DELCHG -> CHANGED
     * <li>CLEAN(Underlying delete state) -> RESTORED
     * </ul>
     * <p>
     * A RESTORED record can now be handled as a special case of CLEAN. It is necessary to have this extra
     * case to indicate that the underlying record is to be restored, whereas CLEAN would imply no change. If
     * subsequent changes are made to a restored record, the restore is still implied since it can never
     * return to the CLEAN state
     * <p>
     * Undo operations are currently simplistic with the only change points that can be recovered being the
     * current underlying core state and the original core state. However this algorithm holds even if we
     * implement multiple change history with NEW being treated the same as CHANGED
     * <p>
     * Edit Undo operations are performed on CHANGED state records only. No history is kept for NEW records in
     * Edit view. Undo restores the record to the values in the underlying core record. The State is changed
     * to CLEAN or RESTORED depending on whether the underlying record is deleted or not. If the current state
     * is CLEAN and the underlying state is CHANGED then the values are reset to the original core state and
     * the Edit status is set to CHANGED. No other CLEAN state is possible to Undo since NEW has no history,
     * CLEAN has no changes and DELETED records are unavailable. If the current value is RESTORED then if the
     * underlying status is DELCHG then we can restore changes as for CLEAN and set the status to CHANGED.
     * Other underlying deleted value are invalid (DELNEW has no history, DELETED has no changes)
     * <p>
     * Applying Edit changes is performed as follows
     * <ul>
     * <li>NEW -> insert record with status of NEW into CORE
     * <li>DELNEW -> Discard
     * <li>CLEAN -> Discard
     * <li>DELETED/DELCHG -
     * <ul>
     * <li>NEW -> DELNEW (no changes copied down)
     * <li>CHANGED -> DELCHG (no changes copied down)
     * <li>CLEAN -> DELETED (no changes copied down)
     * <li>DEL* -> No change to status (no changes copied down)
     * </ul
     * <li>RECOVERED
     * <ul>
     * <li>DELNEW -> NEW
     * <li>DELETED -> CLEAN
     * <li>DELCHG -> CHANGED
     * </ul>
     * <li>CHANGED
     * <ul>
     * <li>NEW -> NEW (changes copied down) -
     * <li>CHANGED -> CHANGED (changes copied down)
     * <li>CLEAN -> CHANGED (changes copied down)
     * <li>DELNEW -> NEW (changes copied down)
     * <li>DELCHG -> CHANGED (changes copied down)
     * <li>DELETED -> CHANGED (changes copied down)
     * </ul>
     * </ul>
     */

    /**
     * Set the state of the item.
     * @param newState the new state to set
     */
    public void setState(final DataState newState) {
        /* Police the action */
        switch (newState) {
            case NEW:
                theState = newState;
                setDeleted(false);
                theEdit = EditState.DIRTY;
                break;
            case CLEAN:
                theState = newState;
                theEdit = EditState.CLEAN;
                switch (getBaseState()) {
                    case NOSTATE:
                        if (getStyle() == ListStyle.EDIT) {
                            theState = DataState.NEW;
                            theEdit = EditState.DIRTY;
                        }
                        setDeleted(false);
                        break;
                    case DELETED:
                    case DELNEW:
                    case DELCHG:
                        setDeleted(true);
                        break;
                    default:
                        setDeleted(false);
                        break;
                }
                break;
            case RECOVERED:
                theEdit = EditState.DIRTY;
                setDeleted(false);
                switch (theState) {
                    case DELETED:
                        theState = DataState.CLEAN;
                        break;
                    case DELNEW:
                        theState = DataState.NEW;
                        break;
                    case DELCHG:
                        theState = DataState.CHANGED;
                        break;
                    case CLEAN:
                        theState = newState;
                        break;
                    default:
                        break;
                }
                break;
            case DELCHG:
            case DELNEW:
                theState = DataState.DELETED;
                setDeleted(true);
                setValidEdit();
                break;
            case CHANGED:
                theList.setEditState(EditState.DIRTY);
                theEdit = EditState.DIRTY;
                setDeleted(false);
                switch (theState) {
                    case NEW:
                    case DELNEW:
                        theState = DataState.NEW;
                        break;
                    case CHANGED:
                    case CLEAN:
                    case RECOVERED:
                    case DELETED:
                    case DELCHG:
                    case NOSTATE:
                        theState = newState;
                        break;
                    default:
                        break;
                }
                break;
            case DELETED:
                setDeleted(true);
                setValidEdit();
                switch (theState) {
                    case NEW:
                        theState = DataState.DELNEW;
                        break;
                    case CHANGED:
                        theState = DataState.DELCHG;
                        break;
                    case CLEAN:
                    case RECOVERED:
                    case NOSTATE:
                        theState = newState;
                        break;
                    case DELETED:
                    case DELNEW:
                    case DELCHG:
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }
}

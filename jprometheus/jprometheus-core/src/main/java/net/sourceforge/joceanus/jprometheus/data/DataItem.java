/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataValues;
import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisEditState;
import net.sourceforge.joceanus.jmetis.data.MetisEncryptedValueSet;
import net.sourceforge.joceanus.jmetis.data.MetisFieldSetItem;
import net.sourceforge.joceanus.jmetis.data.MetisFieldState;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisItemValidation;
import net.sourceforge.joceanus.jmetis.data.MetisItemValidation.MetisErrorElement;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmetis.data.MetisValueSetHistory;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedIdItem;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.DataList.ListStyle;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Provides the abstract DataItem class as the basis for data items. The implementation of the
 * interface means that this object can only be held in one list at a time and is unique within that
 * list
 * @see DataList
 * @param <E> the data type enum class
 */
public abstract class DataItem<E extends Enum<E>>
        implements MetisOrderedIdItem<Integer>, MetisDataValues, MetisFieldSetItem {
    /**
     * Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(PrometheusDataResource.DATAITEM_NAME.getValue());

    /**
     * Validation error.
     */
    public static final String ERROR_VALIDATION = PrometheusDataResource.DATAITEM_ERROR_VALIDATION.getValue();

    /**
     * Resolution error.
     */
    public static final String ERROR_RESOLUTION = PrometheusDataResource.DATAITEM_ERROR_RESOLUTION.getValue();

    /**
     * Duplicate Id error.
     */
    public static final String ERROR_DUPLICATE = PrometheusDataResource.DATAITEM_ERROR_DUPLICATE.getValue();

    /**
     * Unknown Id error.
     */
    public static final String ERROR_UNKNOWN = PrometheusDataResource.DATAITEM_ERROR_UNKNOWN.getValue();

    /**
     * Existing value error.
     */
    public static final String ERROR_EXIST = PrometheusDataResource.DATAITEM_ERROR_EXIST.getValue();

    /**
     * Missing value error.
     */
    public static final String ERROR_MISSING = PrometheusDataResource.DATAITEM_ERROR_MISSING.getValue();

    /**
     * Value too long error.
     */
    public static final String ERROR_LENGTH = PrometheusDataResource.DATAITEM_ERROR_LENGTH.getValue();

    /**
     * Value negative error.
     */
    public static final String ERROR_NEGATIVE = PrometheusDataResource.DATAITEM_ERROR_NEGATIVE.getValue();

    /**
     * Value zero error.
     */
    public static final String ERROR_ZERO = PrometheusDataResource.DATAITEM_ERROR_ZERO.getValue();

    /**
     * Value outside valid range.
     */
    public static final String ERROR_RANGE = PrometheusDataResource.DATAITEM_ERROR_RANGE.getValue();

    /**
     * Value disabled error.
     */
    public static final String ERROR_DISABLED = PrometheusDataResource.DATAITEM_ERROR_DISABLED.getValue();

    /**
     * Creation failure.
     */
    public static final String ERROR_CREATEITEM = PrometheusDataResource.DATAITEM_ERROR_CREATE.getValue();

    /**
     * Multiple instances Error.
     */
    public static final String ERROR_MULT = PrometheusDataResource.DATAITEM_ERROR_MULTIPLE.getValue();

    /**
     * Reserved name error.
     */
    public static final String ERROR_INVALIDCHAR = PrometheusDataResource.DATAITEM_ERROR_INVALIDCHAR.getValue();

    /**
     * Standard Name length.
     */
    public static final int NAMELEN = 30;

    /**
     * Standard Description length.
     */
    public static final int DESCLEN = 50;

    /**
     * Id Field Id.
     */
    public static final MetisField FIELD_ID = FIELD_DEFS.declareComparisonField(PrometheusDataResource.DATAITEM_ID.getValue());

    /**
     * Type Field Id.
     */
    public static final MetisField FIELD_DATATYPE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_TYPE.getValue());

    /**
     * List Field Id.
     */
    public static final MetisField FIELD_LIST = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_NAME.getValue());

    /**
     * Base Field Id.
     */
    public static final MetisField FIELD_BASE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_BASE.getValue());

    /**
     * TouchStatus Field Id.
     */
    public static final MetisField FIELD_TOUCH = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_TOUCH.getValue());

    /**
     * Deleted Field Id.
     */
    public static final MetisField FIELD_DELETED = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_DELETED.getValue());

    /**
     * DataState Field Id.
     */
    public static final MetisField FIELD_STATE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_STATE.getValue());

    /**
     * Edit State Field Id.
     */
    public static final MetisField FIELD_EDITSTATE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_EDITSTATE.getValue());

    /**
     * Version Field Id.
     */
    public static final MetisField FIELD_VERSION = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATASET_VERSION.getValue());

    /**
     * Header Field Id.
     */
    public static final MetisField FIELD_HEADER = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_HEADER.getValue());

    /**
     * History Field Id.
     */
    public static final MetisField FIELD_HISTORY = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_HISTORY.getValue());

    /**
     * Errors Field Id.
     */
    public static final MetisField FIELD_ERRORS = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_ERRORS.getValue());

    /**
     * Instance ReportFields.
     */
    private final MetisFields theFields;

    /**
     * ValueSet.
     */
    private MetisValueSet theValueSet;

    /**
     * The list to which this item belongs.
     */
    private DataList<?, E> theList;

    /**
     * The item that this DataItem is based upon.
     */
    private DataItem<?> theBase;

    /**
     * The Edit state of this item {@link MetisEditState}.
     */
    private MetisEditState theEdit = MetisEditState.CLEAN;

    /**
     * Is the item a header.
     */
    private boolean isHeader;

    /**
     * The id number of the item.
     */
    private Integer theId;

    /**
     * The history control {@link MetisValueSetHistory}.
     */
    private MetisValueSetHistory theHistory;

    /**
     * The validation control {@link MetisItemValidation}.
     */
    private MetisItemValidation theErrors;

    /**
     * Status.
     */
    private final DataTouch<E> theTouchStatus;

    /**
     * Construct a new item.
     * @param pList the list that this item is associated with
     * @param uId the Id of the new item (or 0 if not yet known)
     */
    public DataItem(final DataList<?, E> pList,
                    final Integer uId) {
        /* Record list and item references */
        theId = uId;
        theList = pList;

        /* Declare fields (allowing for subclasses) */
        theFields = declareFields();

        /* Create validation control */
        theErrors = new MetisItemValidation();

        /* Create history control */
        theHistory = new MetisValueSetHistory();

        /* Allocate initial value set and declare it */
        MetisValueSet myValues = (this instanceof EncryptedItem)
                                                                 ? new MetisEncryptedValueSet(this)
                                                                 : new MetisValueSet(this);
        declareValues(myValues);
        theHistory.setValues(myValues);

        /* Allocate id */
        pList.setNewId(this);

        /* Create the touch status */
        @SuppressWarnings("unchecked")
        DataSet<?, E> myData = (DataSet<?, E>) getTheDataSet();
        Class<E> myClass = myData.getEnumClass();
        theTouchStatus = new DataTouch<>(myClass);
    }

    /**
     * Construct a new item.
     * @param pList the list that this item is associated with
     * @param pValues the data values
     */
    public DataItem(final DataList<?, E> pList,
                    final DataValues<E> pValues) {
        /* Record list and item references */
        this(pList, pValues.getValue(FIELD_ID, Integer.class));
    }

    /**
     * Construct a new item based on an old item.
     * @param pList the list that this item is associated with
     * @param pBase the old item
     */
    protected DataItem(final DataList<?, E> pList,
                       final DataItem<E> pBase) {
        /* Initialise using standard constructor */
        this(pList, pBase.getId());

        /* Initialise the valueSet */
        theValueSet.copyFrom(pBase.getValueSet());

        /* Access the varying styles and the source state */
        ListStyle myStyle = pList.getStyle();
        ListStyle myBaseStyle = pBase.getList().getStyle();
        MetisDataState myState = pBase.getState();

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
                        theHistory.pushHistory(1);
                        break;

                    /*
                     * Changed items need to have new values at version 1 and originals at version 0
                     */
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
    public MetisFields getDataFields() {
        return theFields;
    }

    /**
     * Declare fields.
     * @return the fields
     */
    public abstract MetisFields declareFields();

    @Override
    public final void declareValues(final MetisValueSet pValues) {
        theValueSet = pValues;
    }

    @Override
    public MetisValueSet getValueSet() {
        return theValueSet;
    }

    @Override
    public MetisValueSetHistory getValueSetHistory() {
        return theHistory;
    }

    /**
     * Obtain valueSet version.
     * @return the valueSet version
     */
    public int getValueSetVersion() {
        return theValueSet.getVersion();
    }

    @Override
    public String formatObject() {
        return getDataFields().getName();
    }

    @Override
    public boolean skipField(final MetisField pField) {
        return false;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* If this is a valueSet field */
        if (pField.getStorage().isValueSet()) {
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
        if (FIELD_DATATYPE.equals(pField)) {
            return getItemType();
        }
        if (FIELD_TOUCH.equals(pField)) {
            return theTouchStatus;
        }
        if (FIELD_BASE.equals(pField)) {
            return (theBase == null)
                                     ? MetisFieldValue.SKIP
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
                               : MetisFieldValue.SKIP;
        }
        if (FIELD_VERSION.equals(pField)) {
            return (theValueSet != null)
                                         ? theValueSet.getVersion()
                                         : MetisFieldValue.SKIP;
        }
        if (FIELD_HEADER.equals(pField)) {
            return isHeader
                            ? isHeader
                            : MetisFieldValue.SKIP;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return hasHistory()
                                ? theHistory
                                : MetisFieldValue.SKIP;
        }
        if (FIELD_ERRORS.equals(pField)) {
            return hasErrors()
                               ? theErrors
                               : MetisFieldValue.SKIP;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }

    /**
     * Obtain the list.
     * @return the list
     */
    public DataList<?, E> getList() {
        return theList;
    }

    /**
     * Obtain the dataSet.
     * @return the dataSet
     */
    public DataSet<?, ?> getDataSet() {
        return getTheDataSet();
    }

    /**
     * Obtain the dataSet.
     * @return the dataSet
     */
    private DataSet<?, ?> getTheDataSet() {
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
     * Get the type of the item.
     * @return the item type
     */
    public E getItemType() {
        return theList.getItemType();
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
        return theTouchStatus.isActive();
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    /**
     * Obtain the touchStatus.
     * @return the touch status
     */
    public DataTouch<E> getTouchStatus() {
        return theTouchStatus;
    }

    /**
     * Get the EditState for this item.
     * @return the EditState
     */
    public MetisEditState getEditState() {
        return theEdit;
    }

    /**
     * Get the State for this item.
     * @return the State
     */
    public MetisDataState getState() {
        return MetisDataState.determineState(theHistory);
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
    protected MetisValueSetHistory getHistory() {
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
    protected void setEditState(final MetisEditState pState) {
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
     * Clear the touch status flag.
     */
    public void clearActive() {
        theTouchStatus.resetTouches();
    }

    /**
     * Clear the item touches.
     * @param pItemType the item type
     */
    public void clearTouches(final E pItemType) {
        theTouchStatus.resetTouches(pItemType);
    }

    /**
     * Touch the item.
     * @param pObject object that references the item
     */
    public void touchItem(final DataItem<E> pObject) {
        theTouchStatus.touchItem(pObject.getItemType());
    }

    /**
     * Touch underlying items that are referenced by this item.
     */
    public void touchUnderlyingItems() {
    }

    /**
     * Adjust touches on update.
     */
    public void touchOnUpdate() {
    }

    /**
     * Adjust map for this item.
     */
    public void adjustMapForItem() {
    }

    /**
     * Prepare for analysis.
     */
    public void prepareForAnalysis() {
        /* Clear active flag and touch underlying items */
        clearActive();
        touchUnderlyingItems();

        /* Adjust the map for this item */
        adjustMapForItem();
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
        theEdit = MetisEditState.CLEAN;
    }

    /**
     * Reset the history for the item (restoring original values).
     */
    public void resetHistory() {
        theHistory.resetHistory();
    }

    /**
     * Set Change history for an update list so that the first and only entry in the change list is
     * the original values of the base.
     * @param pBase the base item
     */
    public final void setHistory(final DataItem<?> pBase) {
        theHistory.setHistory(pBase.getOriginalValues());
    }

    /**
     * Return the base history object.
     * @return the original values for this object
     */
    public MetisValueSet getOriginalValues() {
        return theHistory.getOriginalValues();
    }

    /**
     * Check to see whether any changes were made. If no changes were made remove last saved history
     * since it is not needed.
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
    }

    /**
     * Condense history.
     * @param pNewVersion the new maximum version
     */
    public void condenseHistory(final int pNewVersion) {
        theHistory.condenseHistory(pNewVersion);
    }

    /**
     * Determine whether a particular field has changed in this edit view.
     * @param pField the field to test
     * @return <code>true/false</code>
     */
    public MetisDifference fieldChanged(final MetisField pField) {
        return ((pField != null)
                && (pField.getStorage().isValueSet()))
                                                       ? theHistory.fieldChanged(pField)
                                                       : MetisDifference.IDENTICAL;
    }

    /**
     * Determine whether the item has Errors.
     * @return <code>true/false</code>
     */
    public boolean hasErrors() {
        return theEdit == MetisEditState.ERROR;
    }

    /**
     * Determine whether the item has Changes.
     * @return <code>true/false</code>
     */
    public boolean hasChanges() {
        return theEdit != MetisEditState.CLEAN;
    }

    /**
     * Determine whether the item is Valid.
     * @return <code>true/false</code>
     */
    public boolean isValid() {
        return (theEdit == MetisEditState.CLEAN) || (theEdit == MetisEditState.VALID);
    }

    /**
     * Determine whether a particular field has Errors.
     * @param pField the particular field
     * @return <code>true/false</code>
     */
    public boolean hasErrors(final MetisField pField) {
        return pField != null
               && theErrors.hasErrors(pField);
    }

    /**
     * Note that this item has been validated.
     */
    public void setValidEdit() {
        MetisDataState myState = getState();
        if (myState == MetisDataState.CLEAN) {
            theEdit = MetisEditState.CLEAN;
        } else if (theList.getStyle() == ListStyle.CORE) {
            theEdit = MetisEditState.DIRTY;
        } else {
            theEdit = MetisEditState.VALID;
        }
    }

    /**
     * Clear all errors for this item.
     */
    public void clearErrors() {
        theEdit = (theValueSet.getVersion() > 0)
                                                 ? MetisEditState.DIRTY
                                                 : MetisEditState.CLEAN;
        theErrors.clearErrors();
    }

    /**
     * Add an error for this item.
     * @param pError the error text
     * @param pField the associated field
     */
    public void addError(final String pError,
                         final MetisField pField) {
        /* Set edit state and add the error */
        theEdit = MetisEditState.ERROR;
        theErrors.addError(pError, pField);

        /* Note that the list has errors */
        theList.setEditState(MetisEditState.ERROR);
    }

    @Override
    public String getFieldErrors(final MetisField pField) {
        return (pField != null)
                                ? theErrors.getFieldErrors(pField)
                                : null;
    }

    @Override
    public String getFieldErrors(final MetisField[] pFields) {
        return theErrors.getFieldErrors(pFields);
    }

    /**
     * Get the first error element for an item.
     * @return the first error (or <code>null</code>)
     */
    public MetisErrorElement getFirstError() {
        return theErrors.getFirst();
    }

    /**
     * Copy flags.
     * @param pItem the original item
     */
    private void copyFlags(final DataItem<E> pItem) {
        theTouchStatus.copyMap(pItem.theTouchStatus);
    }

    /**
     * Resolve all references to current dataSet.
     * @throws OceanusException on error
     */
    public void resolveDataSetLinks() throws OceanusException {
    }

    /**
     * Resolve a data link into a list.
     * @param pField the field to resolve
     * @param pList the list to resolve against
     * @throws OceanusException on error
     */
    protected void resolveDataLink(final MetisField pField,
                                   final DataList<?, ?> pList) throws OceanusException {
        /* Access the values */
        MetisValueSet myValues = getValueSet();

        /* Access value for field */
        Object myValue = myValues.getValue(pField);

        /* Convert dataItem reference to Id */
        if (myValue instanceof DataItem) {
            myValue = ((DataItem<?>) myValue).getId();
        }

        /* Lookup Id reference */
        if (myValue instanceof Integer) {
            DataItem<?> myItem = pList.findItemById((Integer) myValue);
            if (myItem == null) {
                addError(ERROR_UNKNOWN, pField);
                throw new PrometheusDataException(this, ERROR_RESOLUTION);
            }
            myValues.setValue(pField, myItem);

            /* Lookup Name reference */
        } else if (myValue instanceof String) {
            DataItem<?> myItem = pList.findItemByName((String) myValue);
            if (myItem == null) {
                addError(ERROR_UNKNOWN, pField);
                throw new PrometheusDataException(this, ERROR_RESOLUTION);
            }
            myValues.setValue(pField, myItem);
        }
    }

    /**
     * Is the item to be included in output XML?
     * @param pField the field to check
     * @return true/false
     */
    public boolean includeXmlField(final MetisField pField) {
        return false;
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
        DataItem<?> myItem = (DataItem<?>) pThat;

        /* Check the id */
        if (compareId(myItem) != 0) {
            return false;
        }

        /* Loop through the fields */
        Iterator<MetisField> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            MetisField myField = myIterator.next();

            /* Skip if not used in equality */
            if (!myField.getEquality().isEquality()) {
                continue;
            }

            /* Access the values */
            Object myValue = getFieldValue(myField);
            Object myNew = myItem.getFieldValue(myField);

            /* Check the field */
            if (!MetisDifference.isEqual(myValue, myNew)) {
                return false;
            }
        }

        /* Return identical */
        return true;
    }

    @Override
    public int hashCode() {
        /* hash code is Id for simplicity */
        return theId;
    }

    /**
     * compareTo another dataItem.
     * @param pThat the DataItem to compare
     * @return the order
     */
    protected int compareId(final DataItem<?> pThat) {
        return theId - pThat.theId;
    }

    /**
     * Get the state of the underlying record.
     * @return the underlying state
     */
    protected MetisDataState getBaseState() {
        DataItem<?> myBase = getBase();
        return (myBase == null)
                                ? MetisDataState.NOSTATE
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
    public boolean applyChanges(final DataItem<?> pElement) {
        return false;
    }

    /**
     * Validate the element
     * <p>
     * Dirty items become valid.
     */
    public void validate() {
        if (getEditState() == MetisEditState.DIRTY) {
            setEditState(MetisEditState.VALID);
        }
    }

    @Override
    public MetisFieldState getFieldState(final MetisField pField) {
        /* Determine DELETED state */
        if (isDeleted()) {
            return MetisFieldState.DELETED;

            /* Determine Error state */
        } else if ((hasErrors()) && (hasErrors(pField))) {
            return MetisFieldState.ERROR;

            /* Determine Changed state */
        } else if (fieldChanged(pField).isDifferent()) {
            return MetisFieldState.CHANGED;

            /* Determine standard states */
        } else {
            switch (getState()) {
                case NEW:
                    return MetisFieldState.NEW;
                case RECOVERED:
                    return MetisFieldState.RESTORED;
                default:
                    return MetisFieldState.NORMAL;
            }
        }
    }

    @Override
    public MetisFieldState getItemState() {
        /* Determine DELETED state */
        if (isDeleted()) {
            return MetisFieldState.DELETED;

            /* Determine Error state */
        } else if (hasErrors()) {
            return MetisFieldState.ERROR;

            /* Determine Changed state */
        } else if (hasHistory()) {
            return MetisFieldState.CHANGED;

            /* Determine standard states */
        } else {
            switch (getState()) {
                case NEW:
                    return MetisFieldState.NEW;
                case RECOVERED:
                    return MetisFieldState.RESTORED;
                default:
                    return MetisFieldState.NORMAL;
            }
        }
    }
}

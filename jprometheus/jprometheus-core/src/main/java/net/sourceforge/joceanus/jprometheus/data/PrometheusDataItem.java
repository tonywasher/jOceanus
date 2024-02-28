/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataEditState;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.field.MetisFieldState;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataList.PrometheusListStyle;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Provides the abstract DataItem class as the basis for data items. The implementation of the
 * interface means that this object can only be held in one list at a time and is unique within that
 * list
 * @see PrometheusDataList
 */
public abstract class PrometheusDataItem
        extends MetisFieldVersionedItem
        implements PrometheusTableItem, Comparable<Object> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<PrometheusDataItem> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusDataItem.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_NAME, PrometheusDataItem::getList);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_BASE, PrometheusDataItem::getBase);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_TOUCH, PrometheusDataItem::getTouchStatus);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_HEADER, PrometheusDataItem::isHeader);
    }

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
     * Value positive error.
     */
    public static final String ERROR_POSITIVE = PrometheusDataResource.DATAITEM_ERROR_POSITIVE.getValue();

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
     * The list to which this item belongs.
     */
    private PrometheusDataList<?> theList;

    /**
     * The item that this DataItem is based upon.
     */
    private PrometheusDataItem theBase;

    /**
     * Is the item a header.
     */
    private boolean isHeader;

    /**
     * Status.
     */
    private final PrometheusDataTouch theTouchStatus;

    /**
     * Construct a new item.
     * @param pList the list that this item is associated with
     * @param uId the Id of the new item (or 0 if not yet known)
     */
    protected PrometheusDataItem(final PrometheusDataList<?> pList,
                                 final Integer uId) {
        /* Record list and item references */
        setIndexedId(uId);
        theList = pList;

        /* Allocate id */
        pList.setNewId(this);

        /* Create the touch status */
        theTouchStatus = new PrometheusDataTouch();
    }

    /**
     * Construct a new item.
     * @param pList the list that this item is associated with
     * @param pValues the data values
     */
    protected PrometheusDataItem(final PrometheusDataList<?> pList,
                                 final PrometheusDataValues pValues) {
        /* Record list and item references */
        this(pList, pValues.getValue(MetisDataResource.DATA_ID, Integer.class));
    }

    /**
     * Construct a new item based on an old item.
     * @param pList the list that this item is associated with
     * @param pBase the old item
     */
    protected PrometheusDataItem(final PrometheusDataList<?> pList,
                                 final PrometheusDataItem pBase) {
        /* Initialise using standard constructor */
        this(pList, pBase.getIndexedId());

        /* Initialise the valueSet */
        getValues().copyFrom(pBase.getValues());

        /* Access the varying styles and the source state */
        final PrometheusListStyle myStyle = pList.getStyle();
        final PrometheusListStyle myBaseStyle = pBase.getList().getStyle();
        final MetisDataState myState = pBase.getState();

        /* Switch on the styles */
        switch (myStyle) {
            /* We are building an update list (from Core) */
            case UPDATE:
                switch (myState) {
                    /* NEW/DELNEW need to be at version 1 */
                    case DELNEW:
                    case NEW:
                        getValues().setVersion(1);
                        break;

                    case DELETED:
                        getValuesHistory().pushHistory(1);
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
                        getValues().setVersion(pList.getVersion() + 1);

                        /* Reset the Id */
                        setIndexedId(0);
                        pList.setNewId(this);
                        break;
                    default:
                        break;
                }
                break;

            /* We are building a CORE item */
            case CORE:
                /* set as a new item */
                getValues().setVersion(pList.getVersion() + 1);

                /* If we are adding from Edit */
                if (myBaseStyle == PrometheusListStyle.EDIT) {
                    /* Reset the Id */
                    setIndexedId(0);
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

    /**
     * Obtain valueSet version.
     * @return the valueSet version
     */
    public int getValueSetVersion() {
        return getValues().getVersion();
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return this.getDataFieldSet().getName();
    }

    /**
     * Obtain the list.
     * @return the list
     */
    public PrometheusDataList<?> getList() {
        return theList;
    }

    /**
     * Obtain the dataSet.
     * @return the dataSet
     */
    public PrometheusDataSet getDataSet() {
        return getTheDataSet();
    }

    /**
     * Obtain the dataSet.
     * @return the dataSet
     */
    private PrometheusDataSet getTheDataSet() {
        return theList.getDataSet();
    }

    /**
     * Get the list style for this item.
     * @return the list style
     */
    public PrometheusListStyle getStyle() {
        return theList.getStyle();
    }

    @Override
    public PrometheusListKey getItemType() {
        return theList.getItemType();
    }

    @Override
    public boolean isActive() {
        return theTouchStatus.isActive();
    }

    /**
     * Is the item disabled?
     * @return true/false
     */
    public boolean isDisabled() {
        return false;
    }

    /**
     * Obtain the touchStatus.
     * @return the touch status
     */
    public PrometheusDataTouch getTouchStatus() {
        return theTouchStatus;
    }

    /**
     * Get the Generation.
     * @return the Generation
     */
    public int getGeneration() {
        return theList.getGeneration();
    }

    @Override
    public boolean isEditable() {
        return !isDeleted();
    }

    @Override
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
    public void clearTouches(final PrometheusListKey pItemType) {
        theTouchStatus.resetTouches(pItemType);
    }

    /**
     * Touch the item.
     * @param pObject object that references the item
     */
    public void touchItem(final PrometheusDataItem pObject) {
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
    public PrometheusDataItem getBase() {
        return theBase;
    }

    /**
     * Set the base item for this item.
     * @param pBase the Base item
     */
    public void setBase(final PrometheusDataItem pBase) {
        theBase = pBase;
    }

    /**
     * Unlink the item from the list.
     */
    public void unLink() {
        theList.remove(this);
    }

    /**
     * Set new version.
     */
    public void setNewVersion() {
        getValues().setVersion(getNextVersion());
    }

    @Override
    public int getNextVersion() {
        return theList.getVersion() + 1;
    }

    @Override
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
        while (getValues().getVersion() > pVersion) {
            /* Pop history */
            popHistory();
        }
    }

    /**
     * Set Change history for an update list so that the first and only entry in the change list is
     * the original values of the base.
     * @param pBase the base item
     */
    public final void setHistory(final PrometheusDataItem pBase) {
        getValuesHistory().setHistory(pBase.getOriginalValues());
    }

    @Override
    public MetisDataDifference fieldChanged(final MetisFieldDef pField) {
        return pField instanceof MetisFieldVersionedDef
                ? getValuesHistory().fieldChanged(pField)
                : MetisDataDifference.IDENTICAL;
    }

    /**
     * Note that this item has been validated.
     */
    public void setValidEdit() {
        final MetisDataState myState = getState();
        if (myState == MetisDataState.CLEAN) {
            setEditState(MetisDataEditState.CLEAN);
        } else if (theList.getStyle() == PrometheusListStyle.CORE) {
            setEditState(MetisDataEditState.DIRTY);
        } else {
            setEditState(MetisDataEditState.VALID);
        }
    }

    @Override
    public void addError(final String pError,
                         final MetisDataFieldId pField) {
        /* Set edit state and add the error */
        super.addError(pError, pField);

        /* Note that the list has errors */
        theList.setEditState(MetisDataEditState.ERROR);
    }

    /**
     * Copy flags.
     * @param pItem the original item
     */
    private void copyFlags(final PrometheusDataItem pItem) {
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
     * @param pFieldId the fieldId to resolve
     * @param pList the list to resolve against
     * @throws OceanusException on error
     */
    protected void resolveDataLink(final MetisDataFieldId pFieldId,
                                   final PrometheusDataList<?> pList) throws OceanusException {
        /* Access the values */
        final MetisFieldVersionValues myValues = getValues();

        /* Access value for field */
        Object myValue = myValues.getValue(pFieldId);

        /* Convert dataItem reference to Id */
        if (myValue instanceof PrometheusDataItem) {
            myValue = ((PrometheusDataItem) myValue).getIndexedId();
        }

        /* Lookup Id reference */
        if (myValue instanceof Integer) {
            final PrometheusDataItem myItem = pList.findItemById((Integer) myValue);
            if (myItem == null) {
                addError(ERROR_UNKNOWN, pFieldId);
                throw new PrometheusDataException(this, ERROR_RESOLUTION);
            }
            myValues.setValue(pFieldId, myItem);

            /* Lookup Name reference */
        } else if (myValue instanceof String) {
            final PrometheusDataItem myItem = pList.findItemByName((String) myValue);
            if (myItem == null) {
                addError(ERROR_UNKNOWN, pFieldId);
                throw new PrometheusDataException(this, ERROR_RESOLUTION);
            }
            myValues.setValue(pFieldId, myItem);
        }
    }

    /**
     * Is the item to be included in output XML?
     * @param pField the field to check
     * @return true/false
     */
    public boolean includeXmlField(final MetisDataFieldId pField) {
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
        final PrometheusDataItem myItem = (PrometheusDataItem) pThat;

        /* Check the id */
        if (compareId(myItem) != 0) {
            return false;
        }

        /* Loop through the fields */
        final Iterator<MetisFieldDef> myIterator = getDataFieldSet().fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            final MetisFieldDef myField = myIterator.next();

            /* Skip if not used in equality */
            if (!(myField instanceof MetisFieldVersionedDef)
                || !((MetisFieldVersionedDef) myField).isEquality()) {
                continue;
            }

            /* Access the values */
            final Object myValue = myField.getFieldValue(this);
            final Object myNew = myField.getFieldValue(myItem);

            /* Check the field */
            if (!MetisDataDifference.isEqual(myValue, myNew)) {
                return false;
            }
        }

        /* Return identical */
        return true;
    }

    @Override
    public int hashCode() {
        /* hash code is Id for simplicity */
        return getIndexedId();
    }

    @Override
    public int compareTo(final Object pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Non-DataItems are last */
        if (!(pThat instanceof PrometheusDataItem)) {
            return -1;
        }

        /* Check data type */
        final PrometheusDataItem myThat = (PrometheusDataItem) pThat;
        int iDiff = getItemType().getItemKey() - myThat.getItemType().getItemKey();
        if (iDiff != 0) {
            return iDiff;
        }

        /* Check values and finally id */
        iDiff = compareValues(myThat);
        return iDiff != 0 ? iDiff : compareId(myThat);
    }

    /**
     * compareTo another dataItem.
     * @param pThat the DataItem to compare
     * @return the order
     */
    protected abstract int compareValues(PrometheusDataItem pThat);

    /**
     * compareTo another dataItem.
     * @param pThat the DataItem to compare
     * @return the order
     */
    protected int compareId(final PrometheusDataItem pThat) {
        return getIndexedId() - pThat.getIndexedId();
    }

    /**
     * Get the state of the underlying record.
     * @return the underlying state
     */
    protected MetisDataState getBaseState() {
        final PrometheusDataItem myBase = getBase();
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
    public boolean applyChanges(final PrometheusDataItem pElement) {
        return false;
    }

    /**
     * Validate the element
     * <p>
     * Dirty items become valid.
     */
    public void validate() {
        if (getEditState() == MetisDataEditState.DIRTY) {
            setEditState(MetisDataEditState.VALID);
        }
    }

    /**
     * Does the string contain only valid characters (no control chars)?
     * @param pString the string
     * @param pDisallowed the set of additional disallowed characters
     * @return true/false
     */
    public static boolean validString(final String pString,
                                      final String pDisallowed) {
        /* Loop through the string */
        for (int i = 0; i < pString.length(); i++) {
            final int myChar = pString.codePointAt(i);
            /* Check for ISO control */
            if (Character.isISOControl(myChar)) {
                return false;
            }

            /* Check for disallowed value */
            if (pDisallowed != null
                    && pDisallowed.indexOf(myChar) != -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Obtain the byte length of a string.
     * @param pString the string
     * @return the length
     */
    public static int byteLength(final String pString) {
        return TethysDataConverter.stringToByteArray(pString).length;
    }

    /**
     * Obtain the field state.
     * @param pField the field
     * @return the state
     */
    public MetisFieldState getFieldState(final MetisDataFieldId pField) {
        /* Determine DELETED state */
        if (isDeleted()) {
            return MetisFieldState.DELETED;

            /* Determine Error state */
        } else if (hasErrors() && hasErrors(pField)) {
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

    /**
     * Obtain the item State.
     * @return the state
     */
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

/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2014 Tony Washer
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
import java.util.Map;

import net.sourceforge.joceanus.jmetis.list.OrderedIdList;
import net.sourceforge.joceanus.jmetis.list.OrderedListIterator;
import net.sourceforge.joceanus.jmetis.viewer.DataState;
import net.sourceforge.joceanus.jmetis.viewer.EditState;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Generic implementation of a DataList for DataItems.
 * @author Tony Washer
 * @param <T> the item type
 * @param <E> the data type enum class
 */
public abstract class DataList<T extends DataItem<E> & Comparable<? super T>, E extends Enum<E>>
        extends OrderedIdList<Integer, T>
        implements JDataContents {
    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(PrometheusDataResource.DATALIST_NAME.getValue());

    /**
     * Instance ReportFields.
     */
    private final JDataFields theFields;

    @Override
    public JDataFields getDataFields() {
        return theFields;
    }

    /**
     * Obtain item fields.
     * @return the item fields
     */
    public abstract JDataFields getItemFields();

    /**
     * Declare fields.
     * @return the fields
     */
    public abstract JDataFields declareFields();

    /**
     * Size Field Id.
     */
    public static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

    /**
     * Granularity Field Id.
     */
    public static final JDataField FIELD_GRANULARITY = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATASET_GRANULARITY.getValue());

    /**
     * ListStyle Field Id.
     */
    public static final JDataField FIELD_STYLE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_STYLE.getValue());

    /**
     * DataSet Field Id.
     */
    public static final JDataField FIELD_DATASET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATASET_NAME.getValue());

    /**
     * Generation Field Id.
     */
    public static final JDataField FIELD_GENERATION = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATASET_GENERATION.getValue());

    /**
     * NextVersion Field Id.
     */
    public static final JDataField FIELD_VERS = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATASET_VERSION.getValue());

    /**
     * EditState Field Id.
     */
    public static final JDataField FIELD_EDIT = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_EDITSTATE.getValue());

    /**
     * ListType Field Id.
     */
    public static final JDataField FIELD_TYPE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_TYPE.getValue());

    /**
     * Base Field Id.
     */
    public static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_BASE.getValue());

    /**
     * Errors Field Id.
     */
    public static final JDataField FIELD_ERRORS = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_ERRORS.getValue());

    @Override
    public String formatObject() {
        return getDataFields().getName() + "(" + size() + ")";
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_SIZE.equals(pField)) {
            return size();
        }
        if (FIELD_GRANULARITY.equals(pField)) {
            return 1 << theGranularity;
        }
        if (FIELD_STYLE.equals(pField)) {
            return theStyle;
        }
        if (FIELD_DATASET.equals(pField)) {
            return theDataSet;
        }
        if (FIELD_GENERATION.equals(pField)) {
            return theGeneration;
        }
        if (FIELD_VERS.equals(pField)) {
            return theVersion;
        }
        if (FIELD_EDIT.equals(pField)) {
            return theEdit;
        }
        if (FIELD_BASE.equals(pField)) {
            return (theBase == null)
                                    ? JDataFieldValue.SKIP
                                    : theBase;
        }
        if (FIELD_ERRORS.equals(pField)) {
            return JDataFieldValue.SKIP;
        }
        if (FIELD_TYPE.equals(pField)) {
            return theItemType;
        }
        return JDataFieldValue.UNKNOWN;
    }

    /**
     * Should this list be included in DataXml?
     * @return true/false
     */
    public boolean includeDataXML() {
        return true;
    }

    /**
     * Obtain List Name.
     * @return the ListName
     */
    public abstract String listName();

    /**
     * The style of the list.
     */
    private ListStyle theStyle = ListStyle.CORE;

    /**
     * The edit state of the list.
     */
    private EditState theEdit = EditState.CLEAN;

    /**
     * The DataSet.
     */
    private DataSet<?, ?> theDataSet;

    /**
     * The granularity of the list.
     */
    private final int theGranularity;

    /**
     * The item type.
     */
    private final E theItemType;

    /**
     * The id manager.
     */
    private final IdManager<T, E> theMgr;

    /**
     * The base list (for extracts).
     */
    private DataList<? extends DataItem<E>, E> theBase = null;

    /**
     * The generation.
     */
    private int theGeneration = 0;

    /**
     * The version.
     */
    private int theVersion = 0;

    /**
     * Get the style of the list.
     * @return the list style
     */
    public ListStyle getStyle() {
        return theStyle;
    }

    /**
     * Get the type of the list.
     * @return the item type
     */
    public E getItemType() {
        return theItemType;
    }

    /**
     * Get the dataSet.
     * @return the dataSet
     */
    public DataSet<?, ?> getDataSet() {
        return theDataSet;
    }

    /**
     * Set the version.
     * @param pVersion the version
     */
    public void setVersion(final int pVersion) {
        theVersion = pVersion;
    }

    /**
     * Set the style of the list.
     * @param pStyle the list style
     */
    protected void setStyle(final ListStyle pStyle) {
        theStyle = pStyle;
    }

    /**
     * Get the EditState of the list.
     * @return the Edit State
     */
    public EditState getEditState() {
        return theEdit;
    }

    /**
     * Get the Generation of the list.
     * @return the Generation
     */
    public int getGeneration() {
        return theGeneration;
    }

    /**
     * Get the Granularity of the list.
     * @return the Granularity
     */
    public int getGranularity() {
        return theGranularity;
    }

    /**
     * Set the Generation of the list.
     * @param pGeneration the generation
     */
    protected void setGeneration(final int pGeneration) {
        theGeneration = pGeneration;
    }

    /**
     * Get the Version of the list.
     * @return the Version
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Get the Base of the list.
     * @return the Base list
     */
    public DataList<?, E> getBaseList() {
        return theBase;
    }

    /**
     * Determine whether the list got any errors.
     * @return <code>true/false</code>
     */
    public boolean hasErrors() {
        return theEdit == EditState.ERROR;
    }

    /**
     * Determine whether the list got any updates.
     * @return <code>true/false</code>
     */
    public boolean hasUpdates() {
        /* We have changes if version is non-zero */
        return theVersion != 0;
    }

    /**
     * Determine whether the list is valid (or are there errors/non-validated changes).
     * @return <code>true/false</code>
     */
    public boolean isValid() {
        return (theEdit == EditState.CLEAN) || (theEdit == EditState.VALID);
    }

    /**
     * Determine whether the list is Locked (overwritten as required).
     * @return <code>true/false</code>
     */
    public boolean isLocked() {
        return false;
    }

    /**
     * Set the base DataList.
     * @param pBase the list that this list is based upon
     */
    protected void setBase(final DataList<? extends DataItem<E>, E> pBase) {
        theBase = pBase;
    }

    /**
     * Get Max Id.
     * @return the Maximum Id
     */
    public Integer getMaxId() {
        return theMgr.getMaxId();
    }

    /**
     * Set Max Id.
     * @param uMaxId the Maximum Id
     */
    public void setMaxId(final Integer uMaxId) {
        theMgr.setMaxId(uMaxId);
    }

    /**
     * Get Manager index.
     * @return the manager index
     */
    @SuppressWarnings("unchecked")
    private IdManager<T, E> getManagerIndex() {
        return (IdManager<T, E>) super.getIndex();
    }

    /**
     * Construct a new object.
     * @param pBaseClass the class of the underlying object
     * @param pDataSet the owning dataSet
     * @param pItemType the item type
     * @param pStyle the new {@link ListStyle}
     */
    protected DataList(final Class<T> pBaseClass,
                       final DataSet<?, ?> pDataSet,
                       final E pItemType,
                       final ListStyle pStyle) {
        super(pBaseClass, new IdManager<T, E>(pDataSet.getGranularity()));
        theStyle = pStyle;
        theItemType = pItemType;
        theDataSet = pDataSet;
        theGranularity = pDataSet.getGranularity();
        theGeneration = pDataSet.getGeneration();
        theMgr = getManagerIndex();

        /* Declare fields (allowing for subclasses) */
        theFields = declareFields();
    }

    /**
     * Construct a clone object.
     * @param pSource the list to clone
     */
    protected DataList(final DataList<T, E> pSource) {
        super(pSource.getBaseClass(), new IdManager<T, E>(pSource.getGranularity()));
        theStyle = ListStyle.COPY;
        theItemType = pSource.getItemType();
        theMgr = getManagerIndex();
        theBase = pSource;
        theDataSet = pSource.getDataSet();
        theGranularity = pSource.getGranularity();
        theGeneration = pSource.getGeneration();

        /* Declare fields (allowing for subclasses) */
        theFields = declareFields();
    }

    /**
     * Obtain an empty list based on this list.
     * @param pStyle the style of the empty list
     * @return the list
     */
    protected abstract DataList<T, E> getEmptyList(final ListStyle pStyle);

    /**
     * Derive an cloned extract of the source list.
     * @param pData the dataSet
     * @param pSource the source list
     * @throws JOceanusException on error
     */
    protected void cloneList(final DataSet<?, ?> pData,
                             final DataList<?, E> pSource) throws JOceanusException {
        /* Correct the dataSet reference */
        theDataSet = pData;

        /* Populate the list */
        pSource.populateList(this);

        /* Remove base reference and reset to CORE list */
        theBase = null;
        theStyle = ListStyle.CORE;
    }

    /**
     * Derive an extract of this list.
     * @param pStyle the Style of the extract
     * @return the derived list
     * @throws JOceanusException on error
     */
    public DataList<T, E> deriveList(final ListStyle pStyle) throws JOceanusException {
        /* Obtain an empty list of the correct style */
        DataList<T, E> myList = getEmptyList(pStyle);

        /* Populate the list */
        populateList(myList);

        /* Return the derived list */
        return myList;
    }

    /**
     * Populate a list extract.
     * @param pList the list to populate
     * @throws JOceanusException on error
     */
    protected void populateList(final DataList<?, E> pList) throws JOceanusException {
        /* Determine special styles */
        ListStyle myStyle = pList.getStyle();
        boolean isUpdate = myStyle == ListStyle.UPDATE;
        boolean isClone = myStyle == ListStyle.CLONE;

        /* Create an iterator for all items in the list */
        Iterator<? extends DataItem<E>> myIterator = iterator();

        /* Loop through the list */
        while (myIterator.hasNext()) {
            /* Access the item and its state */
            DataItem<E> myCurr = myIterator.next();
            DataState myState = myCurr.getState();

            /* If this is an UPDATE list, ignore clean elements */
            if ((isUpdate) && (myState == DataState.CLEAN)) {
                continue;
            }

            /* Copy the item */
            pList.addCopyItem(myCurr);
        }

        /* If this is a Clone list */
        if (isClone) {
            /* Adjust the links */
            pList.resolveDataSetLinks();
        }
    }

    /**
     * Adjust links.
     * @throws JOceanusException on error
     */
    public void resolveDataSetLinks() throws JOceanusException {
        /* Create an iterator for all items in the list */
        Iterator<? extends DataItem<E>> myIterator = iterator();

        /* Loop through the list */
        while (myIterator.hasNext()) {
            /* Access the item */
            DataItem<E> myCurr = myIterator.next();

            /* Adjust the links */
            myCurr.resolveDataSetLinks();
        }
    }

    /**
     * Construct a difference extract between two DataLists. The difference extract will only have items that differ between the two lists. Items that are in
     * the new list, but not in the old list will be viewed as inserted. Items that are in the old list but not in the new list will be viewed as deleted. Items
     * that are in both list but differ will be viewed as changed
     * @param pDataSet the difference DataSet
     * @param pOld The old list to compare to
     * @return the difference list
     */
    public DataList<T, E> deriveDifferences(final DataSet<?, ?> pDataSet,
                                            final DataList<?, E> pOld) {
        /* Obtain an empty list of the correct style */
        DataList<T, E> myList = getEmptyList(ListStyle.DIFFER);
        myList.theDataSet = pDataSet;

        /* Access an Id Map of the old list */
        Map<Integer, ?> myOld = pOld.getIdMap();

        /* Loop through the new list */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            /* Locate the item in the old list */
            DataItem<E> myCurr = myIterator.next();
            DataItem<?> myItem = (DataItem<?>) myOld.get(myCurr.getId());

            /* If the item does not exist in the old list */
            if (myItem == null) {
                /* Insert a new item */
                myItem = myList.addCopyItem(myCurr);
                myItem.setNewVersion();

                /* else the item exists in the old list */
            } else {
                /* If the item has changed */
                if (!myCurr.equals(myItem)) {
                    /* Copy the item */
                    DataItem<E> myNew = myList.addCopyItem(myCurr);
                    myNew.setBase(myItem);

                    /* Ensure that we record the correct history */
                    myNew.setHistory(myItem);
                }

                /* Remove the item from the map */
                myOld.remove(myItem.getId());
            }
        }

        /* Loop through the remaining items in the old list */
        Iterator<?> myOldIterator = myOld.values().iterator();
        while (myOldIterator.hasNext()) {
            /* Insert a new item */
            DataItem<?> myCurr = (DataItem<?>) myOldIterator.next();
            DataItem<E> myItem = myList.addCopyItem(myCurr);
            myItem.setBase(null);
            myItem.setDeleted(true);
        }

        /* Return the difference list */
        return myList;
    }

    /**
     * Re-base the list against a database image. This method is used to re-synchronise between two sources. Items that are in this list, but not in the base
     * list will be viewed as inserted. Items that are in the base list but not in this list list will be viewed as deleted. Items that are in both list but
     * differ will be viewed as changed
     * @param pBase The base list to re-base on
     * @return are there any changes
     */
    public boolean reBase(final DataList<?, E> pBase) {
        /* Access an Id Map of the old list */
        Map<Integer, ?> myBase = pBase.getIdMap();
        boolean bChanges = false;

        /* Loop through this list */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            /* Locate the item in the base list */
            T myCurr = myIterator.next();
            DataItem<?> myItem = (DataItem<?>) myBase.get(myCurr.getId());

            /* If the underlying item does not exist */
            if (myItem == null) {
                /* Mark this as a new item */
                myCurr.getValueSet().setVersion(getVersion() + 1);
                myCurr.setBase(null);
                bChanges = true;

                /* else the item exists in the old list */
            } else {
                /* if it has changed */
                if (!myCurr.equals(myItem)) {
                    /* Set correct history */
                    myCurr.setHistory(myItem);
                    myCurr.setBase(null);
                    bChanges = true;

                    /* else it is identical */
                } else {
                    /* Mark this as a clean item */
                    myCurr.clearHistory();
                    myCurr.setBase(null);
                }

                /* Remove the old item */
                myBase.remove(myItem.getId());
            }
        }

        /* Loop through the remaining items in the base list */
        Iterator<?> myBaseIterator = myBase.values().iterator();
        while (myBaseIterator.hasNext()) {
            /* Insert a new item */
            DataItem<?> myCurr = (DataItem<?>) myBaseIterator.next();
            T myItem = addCopyItem(myCurr);
            myItem.setBase(null);
            myItem.setHistory(myCurr);
            myItem.getValueSet().setDeletion(true);
            bChanges = true;
        }

        /* Return flag */
        return bChanges;
    }

    /**
     * Is the Id unique in this list.
     * @param uId the Id to check
     * @return Whether the id is unique <code>true/false</code>
     */
    public boolean isIdUnique(final Integer uId) {
        /* Ask the Id Manager for the answer */
        return theMgr.isIdUnique(uId);
    }

    /**
     * Generate/Record new id for the item.
     * @param pItem the new item
     */
    protected void setNewId(final DataItem<E> pItem) {
        /* Ask the Id Manager to manage the request */
        theMgr.setNewId(pItem);
    }

    /**
     * Touch underlying items that are referenced by items in this list.
     */
    public void touchUnderlyingItems() {
        /* Loop through items in the list */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myItem = myIterator.next();

            /* If the item is not deleted */
            if (!myItem.isDeleted()) {
                /* Touch underlying items */
                myItem.touchUnderlyingItems();
            }
        }
    }

    /**
     * Set the EditState for the list (forcible on error/change).
     * @param pState the new {@link EditState} (only ERROR/DIRTY)
     */
    public void setEditState(final EditState pState) {
        switch (pState) {
            case CLEAN:
            case VALID:
            case ERROR:
                theEdit = pState;
                break;
            case DIRTY:
                if (theEdit != EditState.ERROR) {
                    theEdit = pState;
                }
                break;
            default:
                break;
        }
    }

    /**
     * Validate the data items.
     * @return the error list (or null if no errors)
     */
    public DataErrorList<DataItem<E>> validate() {
        /* Allocate error list */
        DataErrorList<DataItem<E>> myErrors = null;
        EditState myState = EditState.CLEAN;

        /* Loop through the items */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

            /* Clear errors for the item */
            myCurr.clearErrors();

            /* Skip deleted items */
            if (myCurr.isDeleted()) {
                myCurr.setValidEdit();
                continue;
            }

            /* Validate the item and build up the state */
            myCurr.validate();
            myState = myState.combineState(myCurr.getEditState());

            /* If the item is in error */
            if (myCurr.hasErrors()) {
                /* If this is the first error */
                if (myErrors == null) {
                    /* Allocate error list */
                    myErrors = new DataErrorList<DataItem<E>>();
                }

                /* Add to the error list */
                myErrors.add(myCurr);
            }
        }

        /* Store the edit state */
        theEdit = myState;

        /* Return the errors */
        return myErrors;
    }

    /**
     * Perform a validation on data load.
     * @throws JOceanusException on error
     */
    public void validateOnLoad() throws JOceanusException {
        /* Validate the list */
        DataErrorList<DataItem<E>> myErrors = validate();
        if (myErrors != null) {
            throw new JPrometheusDataException(myErrors, DataItem.ERROR_VALIDATION);
        }
    }

    /**
     * Reset active.
     */
    public void clearActive() {
        /* Create an iterator for the list */
        Iterator<T> myIterator = iterator();

        /* Loop through items clearing active flag */
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();
            myCurr.clearActive();
        }
    }

    /**
     * Create a new element in the list copied from another element (to be over-written).
     * @param pElement - element to base new item on
     * @return the newly allocated item
     */
    public abstract T addCopyItem(final DataItem<?> pElement);

    /**
     * Create a new empty element in the edit list (to be over-written).
     * @return the newly allocated item
     */
    public abstract T addNewItem();

    /**
     * Create a new element according to the DataValues.
     * @param pValues the data values
     * @return the newly allocated item
     * @throws JOceanusException on error
     */
    public abstract T addValuesItem(final DataValues<E> pValues) throws JOceanusException;

    /**
     * Locate an item by name (if possible).
     * @param pName the name of the item
     * @return the matching item
     */
    public T findItemByName(final String pName) {
        return null;
    }

    /**
     * Rewind items to the required version.
     * @param pVersion the version to rewind to
     */
    public void rewindToVersion(final int pVersion) {
        /* Loop through the elements */
        OrderedListIterator<T> myIterator = listIterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

            /* If the version is before required version */
            if (myCurr.getValueSet().getVersion() <= pVersion) {
                /* Ignore */
                continue;
            }

            /* If the item was created after the required version */
            if (myCurr.getOriginalValues().getVersion() > pVersion) {
                /* Remove from list */
                myIterator.remove();
                myCurr.deRegister();

                /* Re-Loop */
                continue;
            }

            /* Adjust values */
            myCurr.rewindToVersion(pVersion);
        }

        /* Adjust list value */
        setVersion(pVersion);

        /* ReSort the list unless we are an edit list */
        if (theStyle != ListStyle.EDIT) {
            reSort();
        }

        /* Validate the list */
        validate();
    }

    /**
     * Condense history.
     * @param pNewVersion the new maximum version
     */
    public void condenseHistory(final int pNewVersion) {
        /* Loop through the elements */
        OrderedListIterator<T> myIterator = listIterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

            /* If the version is before required version */
            if (myCurr.getValueSet().getVersion() < pNewVersion) {
                /* Ignore */
                continue;
            }

            /* If the item is in DELNEW state */
            if (myCurr.isDeleted()
                && (myCurr.getOriginalValues().getVersion() >= pNewVersion)) {
                /* Remove from list */
                myIterator.remove();
                myCurr.deRegister();

                /* Re-Loop */
                continue;
            }

            /* Condense the history */
            myCurr.condenseHistory(pNewVersion);
        }

        /* Adjust list value */
        setVersion(pNewVersion);

        /* Validate the list */
        validate();
    }

    /**
     * ListStyles.
     */
    public enum ListStyle {
        /**
         * Core list holding the true version of the data.
         */
        CORE,

        /**
         * Deep Copy clone for security updates.
         */
        CLONE,

        /**
         * Shallow Copy list for comparison purposes. Only references to other items can be added to the list
         */
        COPY,

        /**
         * Partial extract of the data for the purposes of editing.
         */
        EDIT,

        /**
         * List of changes to be applied to database.
         */
        UPDATE,

        /**
         * List of differences.
         */
        DIFFER;
    }
}

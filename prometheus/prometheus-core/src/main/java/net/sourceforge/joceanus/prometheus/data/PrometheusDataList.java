/*******************************************************************************
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.prometheus.data;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.metis.data.MetisDataEditState;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.data.MetisDataState;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.list.MetisListIndexed;
import net.sourceforge.joceanus.prometheus.PrometheusDataException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoItem.PrometheusDataInfoList;
import net.sourceforge.joceanus.prometheus.data.PrometheusTableItem.PrometheusTableList;
import net.sourceforge.joceanus.tethys.OceanusException;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * Generic implementation of a DataList for DataItems.
 * @author Tony Washer
 * @param <T> the item type
 */
public abstract class PrometheusDataList<T extends PrometheusDataItem>
        implements MetisFieldItem, MetisDataList<T>, PrometheusTableList<T> {
    /**
     * DataList interface.
     */
    public interface PrometheusDataListSet {
        /**
         * Obtain the list for a class.
         * @param <L> the list type
         * @param pDataType the data type
         * @param pClass the list class
         * @return the list
         */
        <L extends PrometheusDataList<?>> L getDataList(PrometheusListKey pDataType,
                                                        Class<L> pClass);

        /**
         * Does this list have the dataType?
         * @param pDataType the dataType
         * @return true/false
         */
        boolean hasDataType(PrometheusListKey pDataType);
    }

    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<PrometheusDataList> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusDataList.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE, PrometheusDataList::size);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_STYLE, PrometheusDataList::getStyle);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATASET_NAME, PrometheusDataList::getDataSet);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_MAPS, PrometheusDataList::getDataMap);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATASET_GENERATION, PrometheusDataList::getGeneration);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATASET_VERSION, PrometheusDataList::getVersion);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_EDITSTATE, PrometheusDataList::getEditState);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_TYPE, PrometheusDataList::getItemType);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_BASE, PrometheusDataList::getBaseList);
    }

    /**
     * The list.
     */
    private final MetisListIndexed<T> theList;

    /**
     * The class.
     */
    private final Class<T> theBaseClazz;

    /**
     * The style of the list.
     */
    private PrometheusListStyle theStyle = PrometheusListStyle.CORE;

    /**
     * The edit state of the list.
     */
    private MetisDataEditState theEdit = MetisDataEditState.CLEAN;

    /**
     * The DataSet.
     */
    private PrometheusDataSet theDataSet;

    /**
     * The item type.
     */
    private final PrometheusListKey theItemType;

    /**
     * The base list (for extracts).
     */
    private PrometheusDataList<? extends PrometheusDataItem> theBase;

    /**
     * DataMap.
     */
    private PrometheusDataMapItem theDataMap;

    /**
     * The generation.
     */
    private int theGeneration;

    /**
     * The version.
     */
    private int theVersion;

    /**
     * Construct a new object.
     * @param pBaseClass the class of the underlying object
     * @param pDataSet the owning dataSet
     * @param pItemType the item type
     * @param pStyle the new {@link PrometheusListStyle}
     */
    protected PrometheusDataList(final Class<T> pBaseClass,
                                 final PrometheusDataSet pDataSet,
                                 final PrometheusListKey pItemType,
                                 final PrometheusListStyle pStyle) {
        theBaseClazz = pBaseClass;
        theStyle = pStyle;
        theItemType = pItemType;
        theDataSet = pDataSet;
        theGeneration = pDataSet.getGeneration();

        /* Create the list */
        theList = new MetisListIndexed<>();
        theList.setComparator(PrometheusDataItem::compareTo);
    }

    /**
     * Construct a clone object.
     * @param pSource the list to clone
     */
    protected PrometheusDataList(final PrometheusDataList<T> pSource) {
        this(pSource.getBaseClass(), pSource.getDataSet(), pSource.getItemType(), PrometheusListStyle.COPY);
        theBase = pSource;
        theGeneration = pSource.getGeneration();
    }

    @Override
    public List<T> getUnderlyingList() {
        return theList.getUnderlyingList();
    }

    /**
     * Obtain item fields.
     * @return the item fields
     */
    public abstract MetisFieldSetDef getItemFields();

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return getDataFieldSet().getName();
    }

    @Override
    public boolean add(final T pItem) {
        return theList.add(pItem);
    }

    @Override
    public void add(final int pIndex,
                    final T pItem) {
        theList.add(pIndex, pItem);
    }

    @Override
    public boolean remove(final Object pItem) {
        return theList.remove(pItem);
    }

    @Override
    public void clear() {
        theList.clear();
    }

    /**
     * reSort the list.
     */
    public void reSort() {
        theList.sortList();
    }

    @Override
    public Class<T> getBaseClass() {
        return theBaseClazz;
    }

    /**
     * Obtain item by id.
     * @param pId the id to lookup
     * @return the item (or null if not present)
     */
    public T findItemById(final Integer pId) {
        return theList.getItemById(pId);
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
     * Get the style of the list.
     * @return the list style
     */
    public PrometheusListStyle getStyle() {
        return theStyle;
    }

    /**
     * Get the type of the list.
     * @return the item type
     */
    public PrometheusListKey getItemType() {
        return theItemType;
    }

    /**
     * Get the dataSet.
     * @return the dataSet
     */
    public PrometheusDataSet getDataSet() {
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
    protected void setStyle(final PrometheusListStyle pStyle) {
        theStyle = pStyle;
    }

    /**
     * Get the EditState of the list.
     * @return the Edit State
     */
    public MetisDataEditState getEditState() {
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
    public PrometheusDataList<?> getBaseList() {
        return theBase;
    }

    /**
     * Obtain the DataMap.
     * @return the enumClass
     */
    protected PrometheusDataMapItem getDataMap() {
        return theDataMap;
    }

    /**
     * Determine whether the list got any errors.
     * @return <code>true/false</code>
     */
    public boolean hasErrors() {
        return theEdit == MetisDataEditState.ERROR;
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
        return theEdit == MetisDataEditState.CLEAN
                || theEdit == MetisDataEditState.VALID;
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    /**
     * Set the base DataList.
     * @param pBase the list that this list is based upon
     */
    protected void setBase(final PrometheusDataList<? extends PrometheusDataItem> pBase) {
        theBase = pBase;
    }

    /**
     * Obtain a copy of the Id Map.
     * @return the Id map.
     */
    public Map<Integer, T> copyIdMap() {
        return theList.copyIdMap();
    }

    /**
     * Obtain an empty list based on this list.
     * @param pStyle the style of the empty list
     * @return the list
     */
    protected abstract PrometheusDataList<T> getEmptyList(PrometheusListStyle pStyle);

    /**
     * Derive an cloned extract of the source list.
     * @param pData the dataSet
     * @param pSource the source list
     * @throws OceanusException on error
     */
    protected void cloneList(final PrometheusDataSet pData,
                             final PrometheusDataList<?> pSource) throws OceanusException {
        /* Correct the dataSet reference */
        theDataSet = pData;

        /* Populate the list */
        pSource.populateList(this);

        /* Remove base reference and reset to CORE list */
        theBase = null;
        theStyle = PrometheusListStyle.CORE;
    }

    /**
     * Derive an extract of this list.
     * @param pStyle the Style of the extract
     * @return the derived list
     * @throws OceanusException on error
     */
    public PrometheusDataList<T> deriveList(final PrometheusListStyle pStyle) throws OceanusException {
        /* Obtain an empty list of the correct style */
        final PrometheusDataList<T> myList = getEmptyList(pStyle);

        /* Populate the list */
        populateList(myList);

        /* Return the derived list */
        return myList;
    }

    /**
     * Populate a list extract.
     * @param pList the list to populate
     * @throws OceanusException on error
     */
    protected void populateList(final PrometheusDataList<?> pList) throws OceanusException {
        /* Determine special styles */
        final PrometheusListStyle myStyle = pList.getStyle();
        final boolean isUpdate = myStyle == PrometheusListStyle.UPDATE;
        final boolean isClone = myStyle == PrometheusListStyle.CLONE;

        /* Create an iterator for all items in the list */
        final Iterator<? extends PrometheusDataItem> myIterator = iterator();

        /* Loop through the list */
        while (myIterator.hasNext()) {
            /* Access the item and its state */
            final PrometheusDataItem myCurr = myIterator.next();
            final MetisDataState myState = myCurr.getState();

            /* If this is an UPDATE list, ignore clean elements */
            if (isUpdate && myState == MetisDataState.CLEAN) {
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
     * @throws OceanusException on error
     */
    public void resolveDataSetLinks() throws OceanusException {
        /* Loop through the list */
        final Iterator<? extends PrometheusDataItem> myIterator = iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            final PrometheusDataItem myCurr = myIterator.next();

            /* Adjust the links */
            myCurr.resolveDataSetLinks();
        }
    }

    /**
     * Construct a difference extract between two DataLists. The difference extract will only have
     * items that differ between the two lists. Items that are in the new list, but not in the old
     * list will be viewed as inserted. Items that are in the old list but not in the new list will
     * be viewed as deleted. Items that are in both list but differ will be viewed as changed
     * @param pDataSet the difference DataSet
     * @param pOld The old list to compare to
     * @return the difference list
     */
    public PrometheusDataList<T> deriveDifferences(final PrometheusDataSet pDataSet,
                                                   final PrometheusDataList<?> pOld) {
        /* Obtain an empty list of the correct style */
        final PrometheusDataList<T> myList = getEmptyList(PrometheusListStyle.DIFFER);
        myList.theDataSet = pDataSet;

        /* Access an Id Map of the old list */
        final Map<Integer, ?> myOld = pOld.copyIdMap();

        /* Loop through the new list */
        final Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            /* Locate the item in the old list */
            final PrometheusDataItem myCurr = myIterator.next();
            PrometheusDataItem myItem = (PrometheusDataItem) myOld.get(myCurr.getIndexedId());

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
                    final PrometheusDataItem myNew = myList.addCopyItem(myCurr);
                    myNew.setBase(myItem);

                    /* Ensure that we record the correct history */
                    myNew.setHistory(myItem);
                }

                /* Remove the item from the map */
                myOld.remove(myItem.getIndexedId());
            }
        }

        /* Loop through the remaining items in the old list */
        final Iterator<?> myOldIterator = myOld.values().iterator();
        while (myOldIterator.hasNext()) {
            /* Insert a new item */
            final PrometheusDataItem myCurr = (PrometheusDataItem) myOldIterator.next();
            final PrometheusDataItem myItem = myList.addCopyItem(myCurr);
            myItem.setBase(null);
            myItem.setDeleted(true);
        }

        /* Return the difference list */
        return myList;
    }

    /**
     * Re-base the list against a database image. This method is used to re-synchronise between two
     * sources. Items that are in this list, but not in the base list will be viewed as inserted.
     * Items that are in the base list but not in this list list will be viewed as deleted. Items
     * that are in both list but differ will be viewed as changed
     * @param pBase The base list to re-base on
     * @return are there any changes
     */
    public boolean reBase(final PrometheusDataList<?> pBase) {
        /* Access an Id Map of the old list */
        final Map<Integer, ?> myBase = pBase.copyIdMap();
        boolean bChanges = false;

        /* Loop through this list */
        final Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            /* Locate the item in the base list */
            final T myCurr = myIterator.next();
            final PrometheusDataItem myItem = (PrometheusDataItem) myBase.get(myCurr.getIndexedId());

            /* If the underlying item does not exist */
            if (myItem == null) {
                /* Mark this as a new item */
                myCurr.getValues().setVersion(getVersion() + 1);
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
                myBase.remove(myItem.getIndexedId());
            }
        }

        /* Loop through the remaining items in the base list */
        final Iterator<?> myBaseIterator = myBase.values().iterator();
        while (myBaseIterator.hasNext()) {
            /* Insert a new item */
            final PrometheusDataItem myCurr = (PrometheusDataItem) myBaseIterator.next();
            final T myItem = addCopyItem(myCurr);
            myItem.setBase(null);
            myItem.setHistory(myCurr);
            myItem.getValues().setDeletion(true);
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
        /* Its unique if its unassigned or greater than the max id */
        if (uId == null
                || uId == 0
                || uId > theList.getNextId()) {
            return true;
        }

        /* Check in list */
        return !theList.containsId(uId);
    }

    /**
     * Generate/Record new id for the item.
     * @param pItem the new item
     */
    protected void setNewId(final PrometheusDataItem pItem) {
        /* Access the Id */
        final Integer myId = pItem.getIndexedId();

        /* If we need to generate a new id */
        if (myId == null
                || myId == 0) {
            /* Obtain the next Id */
            pItem.setIndexedId(theList.allocateNextId());
        }
    }

    /**
     * Touch underlying items that are referenced by items in this list.
     */
    public void touchUnderlyingItems() {
        /* Loop through items in the list */
        final Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            final T myItem = myIterator.next();

            /* If the item is not deleted */
            if (!myItem.isDeleted()) {
                /* Touch underlying items */
                myItem.touchUnderlyingItems();
            }
        }
    }

    /**
     * Set the EditState for the list (forcible on error/change).
     * @param pState the new {@link MetisDataEditState} (only ERROR/DIRTY)
     */
    public void setEditState(final MetisDataEditState pState) {
        switch (pState) {
            case CLEAN:
            case VALID:
            case ERROR:
                theEdit = pState;
                break;
            case DIRTY:
                if (theEdit != MetisDataEditState.ERROR) {
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
    public PrometheusDataErrorList validate() {
        /* Allocate error list */
        PrometheusDataErrorList myErrors = null;
        MetisDataEditState myState = MetisDataEditState.CLEAN;

        /* Loop through the items */
        final Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

            /* Clear errors for the item */
            myCurr.clearErrors();

            /* Skip deleted items */
            if (myCurr.isDeleted()) {
                myCurr.setValidEdit();
                myState = myState.combineState(MetisDataEditState.VALID);
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
                    myErrors = new PrometheusDataErrorList();
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
     * @throws OceanusException on error
     */
    public void validateOnLoad() throws OceanusException {
        /* Validate the list */
        final PrometheusDataErrorList myErrors = validate();
        if (myErrors != null) {
            throw new PrometheusDataException(myErrors, PrometheusDataItem.ERROR_VALIDATION);
        }
    }

    /**
     * Allocate the dataMap.
     * @return the dataMap
     */
    protected abstract PrometheusDataMapItem allocateDataMap();

    /**
     * Set map.
     * @param pMap the map
     */
    protected void setDataMap(final PrometheusDataMapItem pMap) {
        theDataMap = pMap;
    }

    /**
     * Ensure map.
     */
    public void ensureMap() {
        /* Allocate/Reset the map */
        if (theDataMap == null) {
            theDataMap = allocateDataMap();
        } else {
            theDataMap.resetMap();
        }
    }

    /**
     * Build map of the data.
     */
    public void mapData() {
        /* Ensure the map */
        ensureMap();

        /* Loop through the items */
        final Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            final T myItem = myIterator.next();

            /* If the item is not deleted */
            if (!myItem.isDeleted()) {
                /* Map the item */
                myItem.touchUnderlyingItems();
                theDataMap.adjustForItem(myItem);
            }
        }
    }

    /**
     * PostProcess a loaded list.
     * @throws OceanusException on error
     */
    public void postProcessOnLoad() throws OceanusException {
        /* Default action is to resolve links and then sort */
        resolveDataSetLinks();
        theList.sortList();

        /* Map the data */
        mapData();

        /* Now validate the list */
        validateOnLoad();
    }

    /**
     * Prepare for Analysis.
     */
    public void prepareForAnalysis() {
        /* Ensure the map */
        ensureMap();

        /* Loop through items clearing active flag */
        final Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            final T myItem = myIterator.next();

            /* If the item is not deleted */
            if (!myItem.isDeleted()) {
                /* Prepare item for analysis */
                myItem.prepareForAnalysis();
            }
        }
    }

    /**
     * postProcessOnUpdate.
     */
    public void postProcessOnUpdate() {
        /* Note whether this is a DataInfoList */
        final boolean isDataInfo = this instanceof PrometheusDataInfoList;

        /* Reset the map */
        if (theDataMap != null) {
            theDataMap.resetMap();
        }

        /* Loop through items clearing active flag */
        final Iterator<T> myIterator = iterator();
        MetisDataEditState myState = MetisDataEditState.CLEAN;
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

            /* Clear errors for the item */
            myCurr.clearErrors();

            /* Skip deleted items */
            if (myCurr.isDeleted()) {
                myCurr.setValidEdit();
                myState = myState.combineState(MetisDataEditState.VALID);
                continue;
            }

            /* If this is not a DataInfo */
            if (!isDataInfo) {
                /* Adjust touches and update map */
                myCurr.touchOnUpdate();
                myCurr.adjustMapForItem();
            }

            /* Validate the item and build up the state */
            myCurr.validate();
            myState = myState.combineState(myCurr.getEditState());
        }

        /* Store the edit state */
        theEdit = myState;
    }

    /**
     * Create a new element in the list copied from another element (to be over-written).
     * @param pElement - element to base new item on
     * @return the newly allocated item
     */
    public abstract T addCopyItem(PrometheusDataItem pElement);

    /**
     * Create a new empty element in the edit list (to be over-written).
     * @return the newly allocated item
     */
    public abstract T addNewItem();

    /**
     * Create a new element according to the DataValues.
     * @param pValues the data values
     * @return the newly allocated item
     * @throws OceanusException on error
     */
    public abstract T addValuesItem(PrometheusDataValues pValues) throws OceanusException;

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
        final Iterator<T> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

            /* If the version is before required version */
            if (myCurr.getValues().getVersion() <= pVersion) {
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
        if (theStyle != PrometheusListStyle.EDIT) {
            theList.sortList();
        }
    }

    /**
     * Condense history.
     * @param pNewVersion the new maximum version
     */
    public void condenseHistory(final int pNewVersion) {
        /* Loop through the elements */
        final Iterator<T> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

            /* If the version is before required version */
            if (myCurr.getValues().getVersion() < pNewVersion) {
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
    }

    /**
     * ListStyles.
     */
    public enum PrometheusListStyle {
        /**
         * Core list holding the true version of the data.
         */
        CORE,

        /**
         * Deep Copy clone for security updates.
         */
        CLONE,

        /**
         * Shallow Copy list for comparison purposes. Only references to other items can be added to
         * the list
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

/*******************************************************************************
 * jDataModels: Data models
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
package net.sourceforge.jOceanus.jDataModels.data;

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.jOceanus.jDataManager.DataState;
import net.sourceforge.jOceanus.jDataManager.EditState;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;
import net.sourceforge.jOceanus.jSortedList.OrderedListIterator;

/**
 * Generic implementation of a DataList for DataItems.
 * @author Tony Washer
 * @param <T> the item type
 */
public abstract class DataList<T extends DataItem & Comparable<? super T>>
        extends OrderedIdList<Integer, T>
        implements JDataContents {
    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(DataList.class.getSimpleName());

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
     * Size Field Id.
     */
    public static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

    /**
     * Granularity Field Id.
     */
    public static final JDataField FIELD_GRANULARITY = FIELD_DEFS.declareLocalField("Granularity");

    /**
     * ListStyle Field Id.
     */
    public static final JDataField FIELD_STYLE = FIELD_DEFS.declareLocalField("ListStyle");

    /**
     * Generation Field Id.
     */
    public static final JDataField FIELD_GENERATION = FIELD_DEFS.declareLocalField("Generation");

    /**
     * NextVersion Field Id.
     */
    public static final JDataField FIELD_VERS = FIELD_DEFS.declareLocalField("Version");

    /**
     * EditState Field Id.
     */
    public static final JDataField FIELD_EDIT = FIELD_DEFS.declareLocalField("EditState");

    /**
     * Class Field Id.
     */
    public static final JDataField FIELD_CLASS = FIELD_DEFS.declareLocalField("Class");

    /**
     * Base Field Id.
     */
    public static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField("Base");

    @Override
    public String formatObject() {
        return getDataFields().getName()
               + "("
               + size()
               + ")";
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_SIZE.equals(pField)) {
            return size();
        }
        if (FIELD_GRANULARITY.equals(pField)) {
            return (1 << theGranularity);
        }
        if (FIELD_STYLE.equals(pField)) {
            return theStyle;
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
                    ? JDataFieldValue.SkipField
                    : theBase;
        }
        if (FIELD_CLASS.equals(pField)) {
            return getBaseClass().getSimpleName();
        }
        return JDataFieldValue.UnknownField;
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
    private DataSet<?> theDataSet;

    /**
     * The granularity of the list.
     */
    private final int theGranularity;

    /**
     * The id manager.
     */
    private final IdManager<T> theMgr;

    /**
     * The base list (for extracts).
     */
    private DataList<? extends DataItem> theBase = null;

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
     * Get the dataSet.
     * @return the dataSet
     */
    public DataSet<?> getDataSet() {
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
    public DataList<?> getBaseList() {
        return theBase;
    }

    /**
     * Determine whether the list got any errors.
     * @return <code>true/false</code>
     */
    public boolean hasErrors() {
        return (theEdit == EditState.ERROR);
    }

    /**
     * Determine whether the list got any changes.
     * @return <code>true/false</code>
     */
    public boolean hasChanges() {
        return (theEdit != EditState.CLEAN);
    }

    /**
     * Determine whether the list is valid (or are there errors/non-validated changes).
     * @return <code>true/false</code>
     */
    public boolean isValid() {
        return ((theEdit == EditState.CLEAN) || (theEdit == EditState.VALID));
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
    protected void setBase(final DataList<? extends DataItem> pBase) {
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
     * Construct a new object.
     * @param pBaseClass the class of the underlying object
     * @param pDataSet the owning dataSet
     * @param pStyle the new {@link ListStyle}
     */
    protected DataList(final Class<T> pBaseClass,
                       final DataSet<?> pDataSet,
                       final ListStyle pStyle) {
        super(pBaseClass, new IdManager<T>(pDataSet.getGranularity()));
        theStyle = pStyle;
        theDataSet = pDataSet;
        theGranularity = pDataSet.getGranularity();
        theGeneration = pDataSet.getGeneration();
        theMgr = (IdManager<T>) getIndex();

        /* Declare fields (allowing for subclasses) */
        theFields = declareFields();
    }

    /**
     * Construct a clone object.
     * @param pSource the list to clone
     */
    protected DataList(final DataList<T> pSource) {
        super(pSource.getBaseClass(), new IdManager<T>(pSource.getGranularity()));
        theStyle = ListStyle.COPY;
        theMgr = (IdManager<T>) getIndex();
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
    protected abstract DataList<T> getEmptyList(final ListStyle pStyle);

    /**
     * Derive an cloned extract of this list.
     * @param pDataSet the new DataSet
     * @return the cloned list
     */
    public DataList<T> cloneList(final DataSet<?> pDataSet) {
        /* Obtain an empty list of the correct style */
        DataList<T> myList = getEmptyList(ListStyle.CLONE);
        myList.theDataSet = pDataSet;

        /* Populate the list */
        populateList(myList);

        /* Remove base reference and reset to CORE list */
        myList.theBase = null;
        myList.theStyle = ListStyle.CORE;

        /* Return the cloned list */
        return myList;
    }

    /**
     * Derive an extract of this list.
     * @param pStyle the Style of the extract
     * @return the derived list
     */
    public DataList<T> deriveList(final ListStyle pStyle) {
        /* Obtain an empty list of the correct style */
        DataList<T> myList = getEmptyList(pStyle);

        /* Populate the list */
        populateList(myList);

        /* Return the derived list */
        return myList;
    }

    /**
     * Populate a list extract.
     * @param pList the list to populate
     */
    protected void populateList(final DataList<T> pList) {
        /* Determine special styles */
        ListStyle myStyle = pList.getStyle();
        boolean isUpdate = (myStyle == ListStyle.UPDATE);
        boolean isClone = (myStyle == ListStyle.CLONE);

        /* Create an iterator for all items in the list */
        Iterator<? extends DataItem> myIterator = iterator();

        /* Loop through the list */
        while (myIterator.hasNext()) {
            /* Access the item and its state */
            DataItem myCurr = myIterator.next();
            DataState myState = myCurr.getState();

            /* If this is an UPDATE list, ignore clean elements */
            if ((isUpdate)
                && (myState == DataState.CLEAN)) {
                continue;
            }

            /* Copy the item */
            pList.addCopyItem(myCurr);
        }

        /* If this is a Clone list */
        if (isClone) {
            /* Adjust the links */
            resolveDataSetLinks();
        }
    }

    /**
     * Adjust links.
     */
    public void resolveDataSetLinks() {
        /* Create an iterator for all items in the list */
        Iterator<? extends DataItem> myIterator = iterator();

        /* Loop through the list */
        while (myIterator.hasNext()) {
            /* Access the item */
            DataItem myCurr = myIterator.next();

            /* Adjust the links */
            myCurr.resolveDataSetLinks();
        }
    }

    /**
     * Construct a difference extract between two DataLists. The difference extract will only have items that differ between the two lists. Items that are in
     * the new list, but not in the old list will be viewed as inserted. Items that are in the old list but not in the new list will be viewed as deleted. Items
     * that are in both list but differ will be viewed as changed
     * @param pOld The old list to compare to
     * @return the difference list
     */
    public DataList<T> deriveDifferences(final DataList<T> pOld) {
        /* Obtain an empty list of the correct style */
        DataList<T> myList = getEmptyList(ListStyle.DIFFER);

        /* Access an Id Map of the old list */
        Map<Integer, T> myOld = pOld.getIdMap();

        /* Create an iterator for all items in the list */
        Iterator<T> myIterator = iterator();

        /* Loop through the new list */
        while (myIterator.hasNext()) {
            /* Locate the item in the old list */
            DataItem myCurr = myIterator.next();
            DataItem myItem = myOld.get(myCurr.getId());

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
                    DataItem myNew = myList.addCopyItem(myCurr);
                    myNew.setBase(myItem);

                    /* Ensure that we record the correct history */
                    myNew.setHistory(myCurr);
                }

                /* Remove the item from the map */
                myOld.remove(myItem.getId());
            }
        }

        /* Create an iterator for all remaining items in the old list */
        myIterator = myOld.values().iterator();

        /* Loop through the remaining items in the old list */
        while (myIterator.hasNext()) {
            /* Insert a new item */
            DataItem myCurr = myIterator.next();
            DataItem myItem = myList.addCopyItem(myCurr);
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
     */
    public void reBase(final DataList<T> pBase) {
        /* Access an Id Map of the old list */
        Map<Integer, T> myBase = pBase.getIdMap();

        /* Create an iterator for our new list */
        Iterator<T> myIterator = iterator();

        /* Loop through this list */
        while (myIterator.hasNext()) {
            /* Locate the item in the base list */
            T myCurr = myIterator.next();
            T myItem = myBase.get(myCurr.getId());

            /* If the underlying item does not exist */
            if (myItem == null) {
                /* Mark this as a new item */
                myCurr.getValueSet().setVersion(getVersion() + 1);
                myCurr.setBase(null);

                /* else the item exists in the old list */
            } else {
                /* if it has changed */
                if (!myCurr.equals(myItem)) {
                    /* Set correct history */
                    myCurr.setHistory(myItem);
                    myCurr.setBase(null);

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

        /* Create an iterator for the source base list */
        myIterator = myBase.values().iterator();

        /* Loop through the remaining items in the base list */
        while (myIterator.hasNext()) {
            /* Insert a new item */
            T myCurr = myIterator.next();
            T myItem = addCopyItem(myCurr);
            myItem.setBase(null);
            myItem.setHistory(myCurr);
            myItem.getValueSet().setDeletion(true);
        }
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
    protected void setNewId(final DataItem pItem) {
        /* Ask the Id Manager to manage the request */
        theMgr.setNewId(pItem);
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
     * Calculate the Edit State for the list.
     */
    public void findEditState() {
        boolean isDirty = false;
        boolean isValid = false;

        /* Create an iterator for the list */
        Iterator<T> myIterator = iterator();

        /* Loop through the items to find the match */
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

            /* If the item is deleted */
            if (myCurr.isDeleted()) {
                /* If this is a deleted change then we are valid */
                if (myCurr.getState() != DataState.CLEAN) {
                    isValid = true;
                }

                /* Else the item is active */
            } else {
                switch (myCurr.getEditState()) {
                    case CLEAN:
                        break;
                    case DIRTY:
                        isDirty = true;
                        break;
                    case VALID:
                        isValid = true;
                        break;
                    case ERROR:
                        theEdit = EditState.ERROR;
                        return;
                    default:
                        break;
                }
            }
        }

        /* Set state */
        if (isDirty) {
            theEdit = EditState.DIRTY;
        } else if (isValid) {
            theEdit = EditState.VALID;
        } else {
            theEdit = EditState.CLEAN;
        }
    }

    /**
     * Validate the data items.
     */
    public void validate() {
        /* Clear the errors */
        clearErrors();

        /* Create an iterator for the list */
        Iterator<T> myIterator = iterator();

        /* Loop through the items */
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

            /* Skip deleted items */
            if (myCurr.isDeleted()) {
                myCurr.setValidEdit();
                continue;
            }

            /* Validate the item */
            myCurr.validate();
        }

        /* Determine the Edit State */
        findEditState();
    }

    /**
     * Check whether we have updates.
     * @return <code>true/false</code>
     */
    public boolean hasUpdates() {
        /* Create an iterator for the list */
        Iterator<T> myIterator = iterator();

        /* Loop through the items */
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

            /* Ignore clean items */
            if (myCurr.getState() == DataState.CLEAN) {
                continue;
            }

            /* We have an update */
            return true;
        }

        /* We have no updates */
        return false;
    }

    /**
     * Clear errors.
     */
    public void clearErrors() {
        /* Create an iterator for the list */
        Iterator<T> myIterator = iterator();

        /* Loop through items clearing validation errors */
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();
            myCurr.clearErrors();
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
    public abstract T addCopyItem(final DataItem pElement);

    /**
     * Create a new empty element in the edit list (to be over-written).
     * @return the newly allocated item
     */
    public abstract T addNewItem();

    /**
     * Rewind items to the required version.
     * @param pVersion the version to rewind to
     */
    public void rewindToVersion(final int pVersion) {
        /* Create an iterator for the list */
        OrderedListIterator<T> myIterator = listIterator();

        /* Loop through the elements */
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

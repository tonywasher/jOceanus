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

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JSortedList.OrderedIdList;
import net.sourceforge.JSortedList.OrderedListIterator;

/**
 * Generic implementation of a DataList for DataItems.
 * @author Tony Washer
 * @param <L> the list type
 * @param <T> the item type
 */
public abstract class DataList<L extends DataList<L, T>, T extends DataItem & Comparable<T>> extends
        OrderedIdList<Integer, T> implements JDataContents {
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
        return getDataFields().getName() + "(" + size() + ")";
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
            return theBase;
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
    private final DataSet<?> theDataSet;

    /**
     * The granularity of the list.
     */
    private final int theGranularity;

    /**
     * The id manager.
     */
    private final IdManager<T> theMgr;

    /**
     * The class.
     */
    private final Class<L> theClass;

    /**
     * The list self reference.
     */
    private final L theList;

    /**
     * The base list (for extracts).
     */
    private DataList<?, ? extends DataItem> theBase = null;

    /**
     * The generation.
     */
    private int theGeneration = 0;

    /**
     * The version.
     */
    private int theVersion = 0;

    /**
     * Do we show deleted items.
     */
    private boolean doShowDeleted = false;

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
     * Determine whether we should count deleted items as present.
     * @return <code>true/false</code>
     */
    public boolean getShowDeleted() {
        return doShowDeleted;
    }

    /**
     * Set whether we should count deleted items as present.
     * @param bShow <code>true/false</code>
     */
    public void setShowDeleted(final boolean bShow) {
        doShowDeleted = bShow;
    }

    /**
     * Set the base DataList.
     * @param pBase the list that this list is based upon
     */
    protected void setBase(final DataList<?, ? extends DataItem> pBase) {
        theBase = pBase;
    }

    /**
     * Get List.
     * @return the List
     */
    public L getList() {
        return theList;
    }

    /**
     * Get ListClass.
     * @return the ListClass
     */
    protected Class<L> getListClass() {
        return theClass;
    }

    /**
     * Get Max Id.
     * @return the Maximum Id
     */
    public int getMaxId() {
        return theMgr.getMaxId();
    }

    /**
     * Set Max Id.
     * @param uMaxId the Maximum Id
     */
    public void setMaxId(final int uMaxId) {
        theMgr.setMaxId(uMaxId);
    }

    /**
     * Construct a new object.
     * @param pClass the class
     * @param pBaseClass the class of the underlying object
     * @param pDataSet the owning dataSet
     * @param pStyle the new {@link ListStyle}
     */
    protected DataList(final Class<L> pClass,
                       final Class<T> pBaseClass,
                       final DataSet<?> pDataSet,
                       final ListStyle pStyle) {
        super(pBaseClass, new IdManager<T>(pDataSet.getGranularity()));
        theClass = pClass;
        theList = pClass.cast(this);
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
    protected DataList(final L pSource) {
        super(pSource.getBaseClass(), new IdManager<T>(pSource.getGranularity()));
        theStyle = ListStyle.VIEW;
        theClass = pSource.getListClass();
        theList = theClass.cast(this);
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
     * @return the list)
     */
    protected abstract L getEmptyList();

    /**
     * Derive an extract of this list.
     * @param pStyle the Style of the extract
     * @return the derived list
     */
    public L deriveList(final ListStyle pStyle) {
        /* Obtain an empty list of the correct style */
        L myList = getEmptyList();
        myList.theStyle = pStyle;

        /* Determine special styles */
        boolean isUpdate = (pStyle == ListStyle.UPDATE);
        boolean isClone = (pStyle == ListStyle.CLONE);

        /* Create an iterator for all items in the list */
        Iterator<? extends DataItem> myIterator = iterator();

        /* Loop through the list */
        while (myIterator.hasNext()) {
            /* Access the item and its state */
            DataItem myCurr = myIterator.next();
            DataState myState = myCurr.getState();

            /* If this is an UPDATE list, ignore clean elements */
            if ((isUpdate) && (myState == DataState.CLEAN)) {
                continue;
            }

            /* Copy the item */
            DataItem myItem = myList.addNewItem(myCurr);

            /* If this is a Clone list */
            if (isClone) {
                /* Rebuild the links */
                myItem.relinkToDataSet();
            }
        }

        /* For Clone lists */
        if (isClone) {
            /* Remove base reference and reset to CORE list */
            myList.theBase = null;
            myList.theStyle = ListStyle.CORE;
        }

        /* Return the derived list */
        return myList;
    }

    /**
     * Construct a difference extract between two DataLists. The difference extract will only have items that
     * differ between the two lists. Items that are in the new list, but not in the old list will be viewed as
     * inserted. Items that are in the old list but not in the new list will be viewed as deleted. Items that
     * are in both list but differ will be viewed as changed
     * @param pOld The old list to compare to
     * @return the difference list
     */
    public L deriveDifferences(final L pOld) {
        /* Obtain an empty list of the correct style */
        L myList = getEmptyList();
        myList.theStyle = ListStyle.DIFFER;

        /* Access an Id Map of the old list */
        Map<Integer, T> myOld = pOld.getIdMap();

        /* Create an iterator for all items in the list */
        Iterator<T> myIterator = iterator();

        /* Loop through the new list */
        while (myIterator.hasNext()) {
            /* Locate the item in the old list */
            DataItem myCurr = myIterator.next();
            DataItem myItem = myOld.get(myCurr.getId());

            /* If the item does not exist */
            if (myItem == null) {
                /* Insert a new item */
                myItem = myList.addNewItem(myCurr);
                myItem.getValueSet().setVersion(1);

                /* else the item exists in the old list */
            } else {
                /* If the item has changed */
                if (!myCurr.equals(myItem)) {
                    /* Copy the item */
                    DataItem myNew = myList.addNewItem(myCurr);
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
            DataItem myItem = myList.addNewItem(myCurr);
            myItem.setBase(null);
            myItem.getValueSet().setDeletion(true);
        }

        /* Return the difference list */
        return myList;
    }

    /**
     * Re-base the list against a database image. This method is used to re-synchronise between two sources.
     * Items that are in this list, but not in the base list will be viewed as inserted. Items that are in the
     * base list but not in this list list will be viewed as deleted. Items that are in both list but differ
     * will be viewed as changed
     * @param pBase The base list to re-base on
     */
    public void reBase(final L pBase) {
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
            T myItem = addNewItem(myCurr);
            myItem.setBase(null);
            myItem.getValueSet().setDeletion(true);
        }
    }

    /**
     * Is the Id unique in this list.
     * @param uId the Id to check
     * @return Whether the id is unique <code>true/false</code>
     */
    public boolean isIdUnique(final int uId) {
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
        boolean isError = false;
        boolean isValid = false;

        /* Create an iterator for the list */
        Iterator<T> myIterator = iterator();

        /* Loop through the items to find the match */
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

            /* If the item is deleted */
            if (myCurr.isDeleted()) {
                /* If this is a clean change then we are valid */
                if (myCurr.getState() == DataState.CLEAN) {
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
                        isError = true;
                        break;
                    default:
                        break;
                }
            }
        }

        /* Set state */
        if (isError) {
            theEdit = EditState.ERROR;
        } else if (isDirty) {
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

            /* Clear Errors */
            myCurr.clearErrors();

            /* Skip deleted items */
            if (myCurr.isDeleted()) {
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
     * Create a new element in the core list from an edit session (to be over-written).
     * @param pElement - element to base new item on
     * @return the newly allocated item
     */
    public abstract T addNewItem(final DataItem pElement);

    /**
     * Create a new empty element in the edit list (to be over-written).
     * @return the newly allocated item
     */
    public abstract T addNewItem();

    /**
     * Rewind items to the require version.
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

                /* Re-Loop */
                continue;
            }

            /* Adjust values and reSort */
            myCurr.rewindToVersion(pVersion);
            myIterator.reSort();
        }

        /* Adjust list value */
        setVersion(pVersion);

        /* Validate the list */
        validate();
    }

    /**
     * Reset changes in an edit view.
     */
    public void resetChanges() {
        /* Create an iterator for the list */
        OrderedListIterator<T> myIterator = listIterator();

        /* Loop through the elements */
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

            /* Switch on the state */
            switch (myCurr.getState()) {
            /* Delete the item if it is new or a deleted new item */
                case NEW:
                case DELNEW:
                    myIterator.remove();
                    break;

                /* If this is a clean item, just ignore */
                case CLEAN:
                    break;

                /* If this is a changed or DELCHG item */
                case CHANGED:
                case DELCHG:
                    /* Clear changes and fall through */
                    myCurr.resetHistory();
                    myCurr.clearErrors();
                    myIterator.reSort();
                    break;

                /* If this is a deleted or recovered item */
                case DELETED:
                case RECOVERED:
                    /* Clear errors and mark the item as clean */
                    myCurr.clearErrors();
                    // myCurr.setState(DataState.CLEAN);
                    myIterator.reSort();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Prepare changes in an edit view back into the core data.
     */
    public void prepareChanges() {
        /* Create an iterator for the changes list */
        Iterator<T> myIterator = iterator();

        /* Loop through the elements */
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();
            DataItem myBase;

            /* Switch on the state */
            switch (myCurr.getState()) {
            /* Ignore the item if it is clean or DELNEW */
                case CLEAN:
                case DELNEW:
                    break;

                /* If this is a new item, add it to the list */
                case NEW:
                    /* Link this item to the new item */
                    myBase = theBase.addNewItem(myCurr);
                    myBase.setNewVersion();
                    myCurr.setBase(myBase);
                    break;

                /* If this is a deleted or deleted-changed item */
                case DELETED:
                case DELCHG:
                    /* Access the underlying item and mark as deleted */
                    myBase = myCurr.getBase();
                    // myBase.setState(DataState.DELETED);
                    break;

                /* If this is a recovered item */
                case RECOVERED:
                    /* Access the underlying item and mark as restored */
                    myBase = myCurr.getBase();
                    // myBase.setState(DataState.RECOVERED);
                    myBase.setRestoring(true);
                    break;

                /* If this is a changed item */
                case CHANGED:
                    /* Access underlying item */
                    myBase = myCurr.getBase();

                    /* Apply changes and note if history has been applied */
                    if (myBase.applyChanges(myCurr)) {
                        myBase.setChangeing(true);
                    }

                    /* Note if we are restoring an item */
                    if (myBase.isDeleted()) {
                        myBase.setRestoring(true);
                    }

                    /* Set new state */
                    // myBase.setState(DataState.CHANGED);

                    /* Re-sort the item */
                    theBase.reSort(myBase);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * RollBack changes in an edit view that have been applied to core data.
     */
    public void rollBackChanges() {
        /* Create an iterator for this list */
        Iterator<T> myIterator = iterator();

        /* Loop through the elements */
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();
            DataItem myBase;

            /* Switch on the state */
            switch (myCurr.getState()) {
            /* Ignore the item if it is clean or DelNew */
                case CLEAN:
                case DELNEW:
                    break;

                /* If this is a new item, remove the base item */
                case NEW:
                    /* Remove the base item and its reference */
                    theBase.remove(myCurr.getBase());
                    myCurr.setBase(null);
                    break;

                /* If this is a deleted or deleted-changed item */
                case DELETED:
                case DELCHG:
                    /* Access the underlying item and mark as not deleted */
                    // myBase = myCurr.getBase();
                    // myBase.setState(DataState.RECOVERED);
                    break;

                /* If this is a recovered item */
                case RECOVERED:
                    /* Access the underlying item and mark as deleted */
                    myBase = myCurr.getBase();
                    // myBase.setState(DataState.DELETED);
                    myBase.setRestoring(false);
                    break;

                /* If this is a changed item */
                case CHANGED:
                    /* Access underlying item */
                    myBase = myCurr.getBase();

                    /* If we were changing pop the changes */
                    if (myBase.isChangeing()) {
                        myBase.popHistory();
                    }

                    /* If we were restoring */
                    if (myBase.isRestoring()) {
                        /* Set the item to be deleted again */
                        // myBase.setState(DataState.DELETED);
                        myBase.setRestoring(false);

                    }

                    /* Re-sort the item */
                    theBase.reSort(myBase);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Commit changes in an edit view that have been applied to the core data.
     */
    public void commitChanges() {
        Iterator<T> myIterator;
        DataItem myCurr;

        /* Create an iterator for this list */
        myIterator = iterator();

        /* Loop through the elements */
        while ((myCurr = myIterator.next()) != null) {
            /* Switch on the state */
            switch (myCurr.getState()) {
            /* Ignore the item if it is clean */
                case CLEAN:
                    break;

                /* Delete the item from the list if it is a deleted new item */
                case DELNEW:
                    myIterator.remove();
                    break;

                /* All other states clear history and, convert it to Clean */
                case NEW:
                case DELETED:
                case DELCHG:
                case RECOVERED:
                case CHANGED:
                    /* Clear history and set as a clean item */
                    myCurr.clearHistory();
                    myCurr.setRestoring(false);
                    // myCurr.setState(DataState.CLEAN);
                    break;
                default:
                    break;
            }
        }
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
         * Shallow Copy list for comparison purposes.
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
         * Temporary View for validation purposes.
         */
        VIEW,

        /**
         * List of differences.
         */
        DIFFER;
    }
}

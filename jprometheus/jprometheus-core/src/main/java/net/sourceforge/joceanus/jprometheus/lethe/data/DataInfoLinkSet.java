/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysListButtonManager;

/**
 * Representation of an set of DataInfo links for a DataItem.
 * @author Tony Washer
 * @param <T> the data type
 * @param <O> the Owner DataItem that is extended by this item
 * @param <I> the Info Type that applies to this item
 * @param <S> the Info type class
 * @param <E> the data type enum class
 */
public class DataInfoLinkSet<T extends DataInfo<T, O, I, S, E>,
                             O extends DataItem<E>,
                             I extends StaticData<I, S, E>,
                             S extends Enum<S> & DataInfoClass,
                             E extends Enum<E>>
        extends DataInfo<T, O, I, S, E> {
    /**
     * Item separator.
     */
    public static final String ITEM_SEP = TethysListButtonManager.ITEM_SEP;

    /**
     * The local fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(DataInfoLinkSet.class.getSimpleName());

    /**
     * The Active LinkSet.
     */
    private static final MetisField FIELD_ACTIVE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFO_ACTIVE.getValue());

    /**
     * The LinkSet.
     */
    private static final MetisField FIELD_LINKSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFO_LINKSET.getValue());

    /**
     * List of active items.
     */
    private final MetisInfoSetValueList theActive;

    /**
     * List of underlying items.
     */
    private final DataList<T, E> theLinkSet;

    /**
     * The owner.
     */
    private final O theOwner;

    /**
     * The infoType.
     */
    private final I theInfoType;

    /**
     * The infoType.
     */
    private final DataInfoList<T, O, I, S, E> theInfoList;

    /**
     * Constructor.
     * @param pList the infoList
     * @param pOwner the set owner
     * @param pInfoType the info type
     */
    protected DataInfoLinkSet(final DataInfoList<T, O, I, S, E> pList,
                              final O pOwner,
                              final I pInfoType) {
        /* Call super-constructor */
        super(pList);

        /* Save parameters */
        theOwner = pOwner;
        theInfoType = pInfoType;
        theInfoList = pList;

        /* Allocate the lists */
        theActive = new MetisInfoSetValueList();
        theLinkSet = pList.getEmptyList(pList.getStyle());
    }

    /**
     * Constructor.
     * @param pList the infoList
     * @param pSet the infoLinkSet to clone
     */
    protected DataInfoLinkSet(final DataInfoList<T, O, I, S, E> pList,
                              final DataInfoLinkSet<T, O, I, S, E> pSet) {
        /* Call standard constructor */
        this(pList, pSet.getOwner(), pSet.getInfoType());

        /* Iterator through the links */
        final Iterator<T> myIterator = pSet.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

            /* Add a copy item */
            final T myNew = pList.addCopyItem(myLink);
            theLinkSet.add(myNew);
        }

        /* build active links */
        buildActiveLinks();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_ACTIVE.equals(pField)) {
            return theActive;
        }
        if (FIELD_LINKSET.equals(pField)) {
            return theLinkSet;
        }

        /* Unknown */
        return MetisDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return theActive.toString();
    }

    @Override
    public O getOwner() {
        return theOwner;
    }

    @Override
    public I getInfoType() {
        return theInfoType;
    }

    @Override
    public S getInfoClass() {
        return theInfoType.getStaticClass();
    }

    /**
     * Obtain Active Links.
     * @return the Owner
     */
    public List<Object> getActive() {
        return theActive.isEmpty()
                                   ? null
                                   : theActive.getUnderlyingList();
    }

    /**
     * Is the link list empty?
     * @return true/false
     */
    public boolean isEmpty() {
        return theLinkSet.isEmpty();
    }

    /**
     * Add link to Item.
     * @param pItem the item to link to
     */
    public void linkItem(final T pItem) {
        /* If the item is not already linked */
        if (!isItemLinked(pItem)) {
            /* Add the item to the list */
            theLinkSet.add(pItem);
            sortLinks();
        }
    }

    /**
     * Remove link to Item.
     * @param pItem the item to unlink
     */
    public void unlinkItem(final T pItem) {
        /* If the item is already linked */
        if (isItemLinked(pItem)) {
            /* Remove the item from the list */
            theLinkSet.remove(pItem);
            sortLinks();
        }
    }

    /**
     * Check whether an item is linked.
     * @param pItem the item to check
     * @return true/false
     */
    public boolean isItemLinked(final T pItem) {
        return theLinkSet.indexOf(pItem) != -1;
    }

    /**
     * Obtain item linked to value.
     * @param pValue the value to check
     * @return true/false
     */
    public T getItemForValue(final DataItem<E> pValue) {
        /* Loop through the list */
        T myItem = null;
        final Iterator<T> myIterator = theLinkSet.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

            /* If this item is the correct link */
            if (pValue.equals(myLink.getLink())) {
                myItem = myLink;
                break;
            }
        }

        /* Return the item */
        return myItem;
    }

    /**
     * Clear all the links.
     */
    public void clearAllLinks() {
        /* For each existing value */
        final Iterator<T> myIterator = theLinkSet.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

            /* If the value is active */
            if (!myLink.isDeleted()) {
                /* Set the value as deleted */
                myLink.setDeleted(true);
            }
        }

        /* Clear the active links */
        theActive.clear();
    }

    /**
     * Add/restore all required links.
     * @param pActive the active items
     * @throws OceanusException on error
     */
    public void addNewLinks(final List<? extends DataItem<E>> pActive) throws OceanusException {
        /* For each existing value */
        final Iterator<? extends DataItem<E>> myIterator = pActive.iterator();
        while (myIterator.hasNext()) {
            final DataItem<E> myItem = myIterator.next();

            /* Link the item if it is not currently selected */
            if (!theActive.getUnderlyingList().contains(myItem)) {
                /* If we have never had such a link */
                T myLink = getItemForValue(myItem);
                if (myLink == null) {
                    /* Create the new item */
                    myLink = theInfoList.addNewItem(theOwner, theInfoType);
                    myLink.setValue(myItem);
                    myLink.setValueLink(myItem);
                    myLink.setNewVersion();
                    theLinkSet.add(myLink);

                    /* else restore the value */
                } else {
                    myLink.setDeleted(false);
                }
            }
        }
    }

    /**
     * Clear all unnecessary links.
     * @param pActive the active items
     */
    public void clearUnnecessaryLinks(final List<? extends DataItem<E>> pActive) {
        /* For each existing link */
        final Iterator<T> myIterator = theLinkSet.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();
            final DataItem<E> myItem = myLink.getLink();

            /* Link the item if it is not currently selected */
            if (!myLink.isDeleted()
                && !pActive.contains(myItem)) {
                myLink.setDeleted(true);
            }
        }
    }

    /**
     * Sort the list.
     */
    protected void sortLinks() {
        /* Sort using natural comparison */
        theLinkSet.reSort();
        buildActiveLinks();
    }

    /**
     * Build the active links.
     */
    private void buildActiveLinks() {
        /* Clear the list */
        theActive.clear();

        /* Loop through the list */
        final Iterator<T> myIterator = theLinkSet.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

            /* Ignore deleted elements */
            if (myLink.isDeleted()) {
                continue;
            }

            /* Add to the list */
            theActive.add(myLink.getLink());
        }
    }

    /**
     * Determine whether any item has changed in this edit view.
     * @return <code>true/false</code>
     */
    public MetisDataDifference fieldChanged() {
        /* Loop through the list */
        final Iterator<T> myIterator = theLinkSet.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

            /* Notify if the item has changed */
            if (myLink.hasHistory()
                || myLink.getOriginalValues().getVersion() > 0) {
                return MetisDataDifference.DIFFERENT;
            }
        }

        /* No change has occurred */
        return MetisDataDifference.IDENTICAL;
    }

    @Override
    public boolean hasHistory() {
        /* Loop through the list */
        final Iterator<T> myIterator = theLinkSet.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

            /* Notify if the item has changed */
            if (myLink.hasHistory()) {
                return true;
            }
        }

        /* No change has occurred */
        return false;
    }

    @Override
    public void pushHistory() {
        /* Loop through the list */
        final Iterator<T> myIterator = theLinkSet.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

            /* Notify if the item has changed */
            myLink.pushHistory();
        }
    }

    @Override
    public void popHistory() {
        /* Iterate through table values */
        final Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            final T myValue = myIterator.next();

            /* If the entry should be removed */
            if (myValue.getOriginalValues().getVersion() > theInfoList.getVersion()) {
                /* Remove the value */
                myIterator.remove();
                myValue.unLink();
                continue;
            }

            /* Pop the value */
            myValue.popHistory();
        }
    }

    @Override
    public boolean checkForHistory() {
        /* Iterate through table values */
        boolean bChanges = false;
        final Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            final T myValue = myIterator.next();

            /* If this is a newly created item */
            if (!myValue.hasHistory()) {
                bChanges = true;

                /* else existing entry */
            } else {
                /* Check for history */
                bChanges |= myValue.checkForHistory();
            }
        }

        /* return result */
        return bChanges;
    }

    /**
     * Get the State for this infoSet.
     * @return the State
     */
    @Override
    public MetisDataState getState() {
        /* Default to clean */
        final MetisDataState myState = MetisDataState.CLEAN;

        /* Loop through each existing value */
        final Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            final T myValue = myIterator.next();

            /* If we have changes */
            if (myValue.getState() != MetisDataState.CLEAN) {
                /* Note that new state is changed */
                return MetisDataState.CHANGED;
            }
        }

        /* return result */
        return myState;
    }

    /**
     * Is there active values for the infoClass?
     * @return true/false
     */
    public boolean isExisting() {
        /* Loop through each existing value */
        final Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            final T myValue = myIterator.next();

            /* If we have changes */
            if (!myValue.isDeleted()) {
                /* Note that new state is changed */
                return true;
            }
        }

        /* No active entry found */
        return false;
    }

    @Override
    public void setDeleted(final boolean bDeleted) {
        /* If we are restoring */
        if (!bDeleted) {
            /* Handle separately */
            setRestored();
            return;
        }

        /* Clear all the links */
        clearAllLinks();
    }

    /**
     * Restore values that we deleted at the same time as the owner.
     */
    private void setRestored() {
        /* Access the version of the owner */
        int myVersion = theOwner.getValueSetVersion();

        /* We are restoring an edit version if delete was in this session */
        final boolean bEditRestore = myVersion > 0;
        if (!bEditRestore) {
            /* Access underlying version if not editRestore */
            myVersion = theOwner.getBase().getValueSetVersion();
        }

        /* For each existing value */
        final Iterator<T> myIterator = theLinkSet.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

            /* Access version of value */
            final int myValueVersion = bEditRestore
                                                    ? myLink.getValueSetVersion()
                                                    : myLink.getBase().getValueSetVersion();

            /* If the value was deleted at same time as owner */
            if (myValueVersion == myVersion) {
                /* Set the value as restored */
                myLink.setDeleted(false);
            }
        }

        /* build the active links */
        buildActiveLinks();
    }

    @Override
    public void touchUnderlyingItems() {
        /* Loop through the list */
        final Iterator<T> myIterator = theLinkSet.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

            /* If the link is not deleted */
            if (!myLink.isDeleted()) {
                /* Touch the underlying items */
                myLink.touchUnderlyingItems();
            }
        }
    }

    @Override
    public void touchOnUpdate() {
        /* Loop through the list */
        final Iterator<T> myIterator = theLinkSet.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

            /* If the link is not deleted */
            if (!myLink.isDeleted()) {
                /* Touch the underlying items */
                myLink.touchOnUpdate();
            }
        }
    }

    /**
     * Obtain an iterator through the list.
     * @return the iterator
     */
    public Iterator<T> iterator() {
        return theLinkSet.iterator();
    }

    @Override
    public int compareTo(final DataInfo<T, O, I, S, E> pThat) {
        return getInfoType().compareTo(pThat.getInfoType());
    }

    /**
     * Value List.
     */
    public static final class MetisInfoSetValueList
            implements MetisDataObjectFormat, MetisDataList<Object> {
        /**
         * The list.
         */
        private final List<Object> theList;

        /**
         * Constructor.
         */
        protected MetisInfoSetValueList() {
            theList = new ArrayList<>();
        }

        @Override
        public String toString() {
            /* Create the string builder */
            final StringBuilder myBuilder = new StringBuilder();
            boolean isFirst = true;

            /* Loop through the list */
            final Iterator<?> myIterator = iterator();
            while (myIterator.hasNext()) {
                final Object myLink = myIterator.next();

                /* If this is not the first item */
                if (!isFirst) {
                    /* add separator */
                    myBuilder.append(ITEM_SEP);
                }

                /* Append the name */
                myBuilder.append(myLink.toString());
                isFirst = false;
            }

            /* Return the list */
            return myBuilder.toString();
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return toString();
        }

        @Override
        public List<Object> getUnderlyingList() {
            return theList;
        }
    }
}

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.viewer.DataState;
import net.sourceforge.joceanus.jmetis.viewer.Difference;

/**
 * Representation of an set of DataInfo links for a DataItem.
 * @author Tony Washer
 * @param <T> the data type
 * @param <O> the Owner DataItem that is extended by this item
 * @param <I> the Info Type that applies to this item
 * @param <S> the Info type class
 * @param <E> the data type enum class
 */
public class DataInfoLinkSet<T extends DataInfo<T, O, I, S, E>, O extends DataItem<E>, I extends StaticData<I, S, E>, S extends Enum<S> & DataInfoClass, E extends Enum<E>>
        extends DataInfo<T, O, I, S, E> {
    /**
     * Item separator.
     */
    private static final char ITEM_SEP = ',';

    /**
     * List of items.
     */
    private final List<T> theLinks;

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

    @Override
    public String formatObject() {
        return getNameList();
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
     * Is the link list empty?
     * @return true/false
     */
    public boolean isEmpty() {
        return theLinks.isEmpty();
    }

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

        /* Allocate the list */
        theLinks = new ArrayList<T>();
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
        Iterator<T> myIterator = pSet.iterator();
        while (myIterator.hasNext()) {
            T myLink = myIterator.next();

            /* Add a copy item */
            T myNew = pList.addCopyItem(myLink);
            theLinks.add(myNew);
        }
    }

    /**
     * Add link to Item.
     * @param pItem the item to link to
     */
    public void linkItem(final T pItem) {
        /* If the item is not already linked */
        if (!isItemLinked(pItem)) {
            /* Add the item to the list */
            theLinks.add(pItem);

            /* Sort the links */
            sortLinks();
        }
    }

    /**
     * Remove link to Item.
     * @param pItem the item to unlink
     */
    public void unlinkItem(final T pItem) {
        /* If the item is not already linked */
        if (isItemLinked(pItem)) {
            /* Remove the item from the list */
            theLinks.remove(pItem);
        }
    }

    /**
     * Check whether an item is linked.
     * @param pItem the item to check
     * @return true/false
     */
    public boolean isItemLinked(final T pItem) {
        return theLinks.indexOf(pItem) == -1;
    }

    /**
     * Sort the list.
     */
    private void sortLinks() {
        /* Sort using natural comparison */
        Collections.sort(theLinks);
    }

    /**
     * Obtain the name list.
     * @return the name list
     */
    private String getNameList() {
        /* Create the string builder */
        StringBuilder myBuilder = new StringBuilder();
        boolean isFirst = true;

        /* Loop through the list */
        Iterator<T> myIterator = theLinks.iterator();
        while (myIterator.hasNext()) {
            T myLink = myIterator.next();

            /* If this is not the first item */
            if (!isFirst) {
                /* add separator */
                myBuilder.append(ITEM_SEP);
            }

            /* Append the name */
            myBuilder.append(myLink.getLinkName());
            isFirst = false;
        }

        /* Return the list */
        return myBuilder.toString();
    }

    /**
     * Determine whether any item has changed in this edit view.
     * @return <code>true/false</code>
     */
    public Difference fieldChanged() {
        /* Loop through the list */
        Iterator<T> myIterator = theLinks.iterator();
        while (myIterator.hasNext()) {
            T myLink = myIterator.next();

            /* Notify if the item has changed */
            if (myLink.hasHistory()) {
                return Difference.DIFFERENT;
            }
        }

        /* No change has occurred */
        return Difference.IDENTICAL;
    }

    @Override
    public boolean hasHistory() {
        /* Loop through the list */
        Iterator<T> myIterator = theLinks.iterator();
        while (myIterator.hasNext()) {
            T myLink = myIterator.next();

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
        Iterator<T> myIterator = theLinks.iterator();
        while (myIterator.hasNext()) {
            T myLink = myIterator.next();

            /* Notify if the item has changed */
            myLink.pushHistory();
        }
    }

    @Override
    public void popHistory() {
        /* Iterate through table values */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myValue = myIterator.next();

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
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myValue = myIterator.next();

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
    public DataState getState() {
        /* Default to clean */
        DataState myState = DataState.CLEAN;

        /* Loop through each existing value */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myValue = myIterator.next();

            /* If we have changes */
            if (myValue.getState() != DataState.CLEAN) {
                /* Note that new state is changed */
                return DataState.CHANGED;
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
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myValue = myIterator.next();

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

        /* For each existing value */
        Iterator<T> myIterator = theLinks.iterator();
        while (myIterator.hasNext()) {
            T myLink = myIterator.next();

            /* If the value is active */
            if (!myLink.isDeleted()) {
                /* Set the value as deleted */
                myLink.setDeleted(true);
            }
        }
    }

    /**
     * Restore values that we deleted at the same time as the owner.
     */
    private void setRestored() {
        /* Access the version of the owner */
        int myVersion = theOwner.getValueSetVersion();

        /* We are restoring an edit version if delete was in this session */
        boolean bEditRestore = myVersion > 0;
        if (!bEditRestore) {
            /* Access underlying version if not editRestore */
            myVersion = theOwner.getBase().getValueSetVersion();
        }

        /* For each existing value */
        Iterator<T> myIterator = theLinks.iterator();
        while (myIterator.hasNext()) {
            T myLink = myIterator.next();

            /* Access version of value */
            int myValueVersion = (bEditRestore)
                                               ? myLink.getValueSetVersion()
                                               : myLink.getBase().getValueSetVersion();

            /* If the value was deleted at same time as owner */
            if (myValueVersion == myVersion) {
                /* Set the value as restored */
                myLink.setDeleted(false);
            }
        }
    }

    @Override
    public void touchUnderlyingItems() {
        /* Loop through the list */
        Iterator<T> myIterator = theLinks.iterator();
        while (myIterator.hasNext()) {
            T myLink = myIterator.next();

            /* Touch the owner TODO */
            myLink.touchItem(theOwner);

            /* Touch the underlying items */
            myLink.touchUnderlyingItems();
        }
    }

    /**
     * Obtain an iterator through the list.
     * @return the iterator
     */
    public Iterator<T> iterator() {
        return theLinks.iterator();
    }

    @Override
    public int compareTo(final T arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

}
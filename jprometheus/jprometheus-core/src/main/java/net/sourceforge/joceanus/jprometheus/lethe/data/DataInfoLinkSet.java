/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
    public static final String ITEM_SEP = ",";

    /**
     * The local fields.
     */
    private final MetisFields theLocalFields = new MetisFields(DataInfoLinkSet.class.getSimpleName());

    /**
     * The number of fields.
     */
    private int theNumFields;

    /**
     * List of items.
     */
    private final DataList<T, E> theLinks;

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

        /* Allocate the list */
        theLinks = pList.getEmptyList(pList.getStyle());
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
            theLinks.append(myNew);
            theLocalFields.declareIndexField(theInfoType.getName());
            theNumFields++;
        }
    }

    @Override
    public MetisFields getDataFields() {
        return (theLocalFields == null)
                                        ? DataInfo.FIELD_DEFS
                                        : theLocalFields;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle out of range */
        final int iIndex = pField.getIndex();
        if ((iIndex < 0)
            || iIndex >= theNumFields) {
            return MetisFieldValue.UNKNOWN;
        }
        if (iIndex >= theLinks.size()) {
            return MetisFieldValue.SKIP;
        }

        /* Access the element */
        return theLinks.get(iIndex);
    }

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
     * Add link to Item.
     * @param pItem the item to link to
     * @throws OceanusException on error
     */
    public void linkItem(final T pItem) throws OceanusException {
        /* If the item is not already linked */
        if (!isItemLinked(pItem)) {
            /* Perform any necessary splits on the item */
            splitItem(pItem);

            /* Add the item to the list */
            theLinks.append(pItem);

            /* If we need an additional field */
            if (theLinks.size() > theNumFields) {
                /* Allocate it */
                theLocalFields.declareIndexField(theInfoType.getName());
                theNumFields++;
            }
        }
    }

    /**
     * Split multi-name item.
     * @param pItem the item to split
     * @throws OceanusException on error
     */
    private void splitItem(final T pItem) throws OceanusException {
        /* Ignore if this is not a load of a string */
        final Object myValue = pItem.getLink();
        if (!(myValue instanceof String)) {
            return;
        }

        /* Ignore if this is not a load of a combined list */
        final String myString = (String) myValue;
        final int iIndex = myString.indexOf(ITEM_SEP);
        if (iIndex == -1) {
            return;
        }

        /* Adjust the existing value */
        pItem.setValueLink(myString.substring(0, iIndex));

        /* Create the new item */
        final T myItem = theInfoList.addNewItem(theOwner, theInfoType);
        myItem.setValueLink(myString.substring(iIndex + 1));

        /* Link the new item */
        linkItem(myItem);
    }

    /**
     * Remove link to Item.
     * @param pItem the item to unlink
     */
    public void unlinkItem(final T pItem) {
        /* If the item is already linked */
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
        return theLinks.indexOf(pItem) != -1;
    }

    /**
     * Obtain item linked to value.
     * @param pValue the value to check
     * @return true/false
     */
    public T getItemForValue(final DataItem<E> pValue) {
        /* Loop through the list */
        T myItem = null;
        final Iterator<T> myIterator = theLinks.iterator();
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
     * Sort the list.
     */
    protected void sortLinks() {
        /* Sort using natural comparison */
        theLinks.reSort();
    }

    /**
     * Obtain the name list.
     * @return the name list
     */
    protected String getNameList() {
        /* Create the string builder */
        final StringBuilder myBuilder = new StringBuilder();
        boolean isFirst = true;

        /* Loop through the list */
        final Iterator<T> myIterator = theLinks.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

            /* Ignore deleted elements */
            if (myLink.isDeleted()) {
                continue;
            }

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
    public MetisDifference fieldChanged() {
        /* Loop through the list */
        final Iterator<T> myIterator = theLinks.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

            /* Notify if the item has changed */
            if (myLink.hasHistory()) {
                return MetisDifference.DIFFERENT;
            }
        }

        /* No change has occurred */
        return MetisDifference.IDENTICAL;
    }

    @Override
    public boolean hasHistory() {
        /* Loop through the list */
        final Iterator<T> myIterator = theLinks.iterator();
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
        final Iterator<T> myIterator = theLinks.iterator();
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

        /* For each existing value */
        final Iterator<T> myIterator = theLinks.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

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
        final boolean bEditRestore = myVersion > 0;
        if (!bEditRestore) {
            /* Access underlying version if not editRestore */
            myVersion = theOwner.getBase().getValueSetVersion();
        }

        /* For each existing value */
        final Iterator<T> myIterator = theLinks.iterator();
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
    }

    @Override
    public void touchUnderlyingItems() {
        /* Loop through the list */
        final Iterator<T> myIterator = theLinks.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

            /* Touch the underlying items */
            myLink.touchUnderlyingItems();
        }
    }

    @Override
    public void touchOnUpdate() {
        /* Loop through the list */
        final Iterator<T> myIterator = theLinks.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

            /* Touch the underlying items */
            myLink.touchOnUpdate();
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
    public int compareTo(final DataInfo<T, O, I, S, E> pThat) {
        return getInfoType().compareTo(pThat.getInfoType());
    }
}

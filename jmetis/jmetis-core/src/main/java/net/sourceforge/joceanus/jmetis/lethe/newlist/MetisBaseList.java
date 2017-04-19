/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.lethe.newlist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSetHistory;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataValues;
import net.sourceforge.joceanus.jmetis.lethe.newlist.MetisListChange.MetisListEvent;
import net.sourceforge.joceanus.jmetis.lethe.newlist.MetisListItem.MetisIndexedItem;

/**
 * Base List implementation.
 * @param <T> the item type
 */
public class MetisBaseList<T extends MetisIndexedItem>
        extends MetisVersionedList<T> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MetisBaseList.class.getSimpleName(), MetisVersionedList.getBaseFields());

    /**
     * Constructor.
     * @param pClass the class of the item
     */
    public MetisBaseList(final Class<T> pClass) {
        super(MetisListType.BASE, pClass);
    }

    /**
     * Constructor for readOnly items.
     * @param pClass the class of the item
     * @param pFields the fields
     */
    public MetisBaseList(final Class<T> pClass,
                         final MetisFields pFields) {
        super(MetisListType.BASE, pClass, pFields);
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Reset content.
     * @param pSource the source list
     */
    public void resetContent(final MetisBaseList<T> pSource) {
        /* Clear the list */
        resetContent(pSource.iterator(), pSource.getVersion());
    }

    /**
     * Reset content.
     * @param pSource the source list
     */
    public void resetContent(final Iterator<T> pSource) {
        resetContent(pSource, 0);
    }

    /**
     * Reset content.
     * @param pSource the source iterator
     * @param pVersion the version
     */
    private void resetContent(final Iterator<T> pSource,
                              final int pVersion) {
        /* Clear the list */
        clear();

        /* Loop through the list */
        while (pSource.hasNext()) {
            T myCurr = pSource.next();

            /* Add the item to the list */
            addToList(myCurr);
        }

        /* Reset the version */
        setVersion(pVersion);

        /* Report the refresh */
        MetisListChange<T> myChange = new MetisListChange<>(MetisListEvent.REFRESH);
        fireEvent(myChange);
    }

    /**
     * Reset content.
     * @param pSource the source list
     */
    protected void doResetContent(final MetisVersionedList<?> pSource) {
        resetContent((MetisBaseList<T>) castList(pSource));
    }

    /**
     * ReBase the list.
     * @param pBase the base list
     */
    public void reBaseList(final MetisBaseList<T> pBase) {
        /* Not supported for readOnly lists */
        if (isReadOnly()) {
            throw new UnsupportedOperationException();
        }

        /* Access a copy of the idMap of the base list */
        Map<Integer, T> myOld = new HashMap<>(pBase.getIdMap());
        boolean hasChanges = false;

        /* List versions must be 0 */
        if ((getVersion() != 0)
            || (pBase.getVersion() != 0)) {
            throw new IllegalStateException("Versioned List being reBased");
        }

        /* Create a new Change Detail */
        MetisListChange<T> myChange = new MetisListChange<>(MetisListEvent.REBASE);

        /* Loop through the list */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            /* Locate the item in the old list */
            T myCurr = myIterator.next();
            Integer myId = myCurr.getIndexedId();
            T myItem = myOld.get(myId);

            /* Access history */
            MetisDataValues myVersioned = (MetisDataValues) myCurr;
            MetisValueSetHistory myHistory = myVersioned.getValueSetHistory();

            /* If the item does not exist in the old list */
            if (myItem == null) {
                /* Set the version to 1 */
                myHistory.getValueSet().setVersion(1);
                hasChanges = true;

                /* else the item exists in the old list */
            } else {
                /* If the item has changed */
                if (!myCurr.equals(myItem)) {
                    /* ReBase the history */
                    myVersioned = (MetisDataValues) myItem;
                    MetisValueSet myBase = myVersioned.getValueSet().cloneIt();
                    myHistory.setHistory(myBase);
                    hasChanges = true;
                }

                /* Remove the item from the map */
                myOld.remove(myId);
            }
        }

        /* Loop through the remaining items in the old list */
        myIterator = myOld.values().iterator();
        while (myIterator.hasNext()) {
            /* Insert a new item */
            T myCurr = myIterator.next();
            T myItem = newDiffDeletedItem(myCurr);
            addToList(myItem);
            hasChanges = true;
        }

        /* Note changes */
        if (hasChanges) {
            setVersion(1);
            fireEvent(myChange);
        }
    }

    /**
     * Reset content.
     * @param pSource the source list
     */
    protected void doReBaseList(final MetisVersionedList<?> pSource) {
        reBaseList((MetisBaseList<T>) castList(pSource));
    }

    /**
     * Obtain difference list from this list as new to the comparison list as old.
     * @param pCompare the list to compare against
     * @return the difference list
     */
    public MetisDifferenceList<T> deriveDifferences(final MetisBaseList<T> pCompare) {
        /* Not supported for readOnly lists */
        if (isReadOnly()) {
            throw new UnsupportedOperationException();
        }

        /* Create the difference list */
        MetisDifferenceList<T> myDifferences = new MetisDifferenceList<>(getTheClass(), getItemFields());
        myDifferences.deriveTheDifferences(this, pCompare);
        return myDifferences;
    }

    /**
     * Derive differences.
     * @param pCompare the comparison list
     * @return the difference list
     */
    protected MetisDifferenceList<T> doDeriveDifferences(final MetisVersionedList<?> pCompare) {
        return deriveDifferences((MetisBaseList<T>) castList(pCompare));
    }

    /**
     * Obtain update list from this list.
     * @return the update list
     */
    public MetisUpdateList<T> deriveUpdates() {
        /* Not supported for readOnly lists */
        if (isReadOnly()) {
            throw new UnsupportedOperationException();
        }

        /* Create the update list */
        return new MetisUpdateList<>(this);
    }

    /**
     * Obtain edit list from this list.
     * @return the edit list
     */
    public MetisEditList<T> deriveEditList() {
        /* Not supported for readOnly lists */
        if (isReadOnly()) {
            throw new UnsupportedOperationException();
        }

        /* Create the edit list */
        return new MetisEditList<>(this);
    }

    /**
     * Reset the list.
     */
    public void reset() {
        /* If we have changes */
        if (getVersion() != 0) {
            /* ReWind to initial version */
            reWindToVersion(0);
        }
    }

    /**
     * ReWind the list to a particular version.
     * @param pVersion the version to reWind to
     */
    public void reWindToVersion(final int pVersion) {
        /* Check that the rewind version is valid */
        checkReWindVersion(pVersion);

        /* ReWind it */
        doReWindToVersion(pVersion);
    }
}

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
package net.sourceforge.joceanus.jmetis.newlist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmetis.data.MetisValueSetHistory;
import net.sourceforge.joceanus.jmetis.newlist.MetisListChange.MetisListEvent;

/**
 * Base List implementation.
 * @param <T> the item type
 */
public class MetisBaseList<T extends MetisVersionedItem>
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
        clear();

        /* Loop through the list */
        Iterator<T> myIterator = pSource.iterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

            /* Add the item to the list */
            addToList(myCurr);
        }

        /* Reset the version */
        setVersion(pSource.getVersion());

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
            MetisValueSetHistory myHistory = myCurr.getValueSetHistory();

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
                    MetisValueSet myBase = myItem.getValueSet().cloneIt();
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
        MetisDifferenceList<T> myDifferences = new MetisDifferenceList<>(getTheClass());
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
        return new MetisUpdateList<>(this);
    }

    /**
     * Obtain edit list from this list.
     * @return the edit list
     */
    public MetisEditList<T> deriveEditList() {
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

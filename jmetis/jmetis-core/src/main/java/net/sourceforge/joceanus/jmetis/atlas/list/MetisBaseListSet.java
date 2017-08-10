/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.atlas.list;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataVersionedItem;

/**
 * Set of BaseLists.
 */
public final class MetisBaseListSet
        extends MetisVersionedListSet {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MetisBaseListSet.class, MetisVersionedListSet.getBaseFieldSet());

    /**
     * Constructor.
     */
    protected MetisBaseListSet() {
        super();
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public MetisBaseList<MetisDataVersionedItem> getList(final MetisListKey pListKey) {
        return (MetisBaseList<MetisDataVersionedItem>) super.getList(pListKey);
    }

    /**
     * Reset the content.
     * @param pSource the source content to reset to
     */
    public void resetContent(final MetisBaseListSet pSource) {
        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* Obtain the source list */
            final MetisBaseList<MetisDataVersionedItem> mySource = pSource.getList(myKey);
            final MetisBaseList<MetisDataVersionedItem> myTarget = getList(myKey);

            /* Reset the content */
            myTarget.doResetContent(mySource);
        }

        /* Set the version correctly */
        setVersion(pSource.getVersion());
    }

    /**
     * ReBase the listSet.
     * @param pBase the base listSet
     */
    public void reBaseListSet(final MetisBaseListSet pBase) {
        /* ListSet versions must be 0 */
        if ((getVersion() != 0)
            || (pBase.getVersion() != 0)) {
            throw new IllegalStateException("Versioned ListSet being reBased");
        }

        /* Determine the new Version */
        int myNewVersion = 0;

        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* Obtain the source list */
            final MetisBaseList<MetisDataVersionedItem> myBase = pBase.getList(myKey);
            final MetisBaseList<MetisDataVersionedItem> myTarget = getList(myKey);

            /* reBase the list */
            myTarget.doReBaseList(myBase);

            /* Note maximum version */
            myNewVersion = Math.max(myNewVersion, myTarget.getVersion());
        }

        /* Set the version correctly */
        setVersion(myNewVersion);
    }

    /**
     * Derive a difference set.
     * @param pOld the old listSet to compare to
     * @return the difference set
     */
    public MetisDifferenceListSet deriveDifferences(final MetisBaseListSet pOld) {
        /* Create a new difference set */
        final MetisDifferenceListSet myDifferences = new MetisDifferenceListSet();

        /* Determine the new Version */
        int myNewVersion = 0;

        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* Obtain the source list */
            final MetisBaseList<MetisDataVersionedItem> myOld = pOld.getList(myKey);
            final MetisBaseList<MetisDataVersionedItem> myNew = getList(myKey);

            /* Obtain the difference list and add if non-empty */
            final MetisDifferenceList<MetisDataVersionedItem> myDifference = myNew.doDeriveDifferences(myOld);
            if (!myDifference.isEmpty()) {
                myDifferences.declareList(myKey, myDifference);
                myNewVersion = 1;
            }
        }

        /* Return the differenceSet */
        myDifferences.setVersion(myNewVersion);
        return myDifferences;
    }

    /**
     * Derive an update set.
     * @return the update set
     */
    public MetisUpdateListSet deriveUpdates() {
        /* Create a new update set */
        final MetisUpdateListSet myUpdates = new MetisUpdateListSet();

        /* Determine the new Version */
        int myNewVersion = 0;

        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* Obtain the list */
            final MetisBaseList<MetisDataVersionedItem> myList = getList(myKey);

            /* Obtain the update list and add */
            final MetisUpdateList<MetisDataVersionedItem> myUpdate = myList.deriveUpdates();
            myUpdates.declareList(myKey, myUpdate);

            /* Note maximum version */
            myNewVersion = Math.max(myNewVersion, myUpdates.getVersion());
        }

        /* Return the updateSet */
        myUpdates.setVersion(myNewVersion);
        return myUpdates;
    }

    /**
     * Derive an edit set.
     * @return the edit set
     */
    public MetisEditListSet deriveEditSet() {
        /* Create a new edit set */
        final MetisEditListSet myEdits = new MetisEditListSet(this);

        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* Obtain the list */
            final MetisBaseList<MetisDataVersionedItem> myList = getList(myKey);

            /* Obtain the edit list and add */
            final MetisEditList<MetisDataVersionedItem> myEdit = myList.deriveEditList();
            myEdits.declareList(myKey, myEdit);
        }

        /* Return the editSet */
        return myEdits;
    }

    /**
     * Reset the listSet.
     */
    public void reset() {
        /* If we have changes */
        if (getVersion() != 0) {
            /* ReWind to initial version */
            reWindToVersion(0);
        }
    }

    /**
     * ReWind the listSet to a particular version.
     * @param pVersion the version to reWind to
     */
    public void reWindToVersion(final int pVersion) {
        /* Check that the rewind version is valid */
        checkReWindVersion(pVersion);

        /* ReWind it */
        doReWindToVersion(pVersion);
    }
}

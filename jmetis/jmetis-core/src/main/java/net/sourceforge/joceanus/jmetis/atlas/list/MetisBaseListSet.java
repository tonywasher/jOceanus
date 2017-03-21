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
package net.sourceforge.joceanus.jmetis.atlas.list;

import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.list.MetisListItem.MetisIndexedItem;
import net.sourceforge.joceanus.jmetis.data.MetisFields;

/**
 * Set of BaseLists.
 * @param <E> the list type identifier
 */
public class MetisBaseListSet<E extends Enum<E>>
        extends MetisVersionedListSet<E, MetisBaseList<MetisIndexedItem>> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MetisBaseListSet.class.getSimpleName(), MetisVersionedListSet.getBaseFields());

    /**
     * Constructor.
     * @param pClass the enum class
     */
    protected MetisBaseListSet(final Class<E> pClass) {
        super(MetisListType.BASE, pClass, FIELD_DEFS);
    }

    /**
     * Reset the content.
     * @param pSource the source content to reset to
     */
    public void resetContent(final MetisBaseListSet<E> pSource) {
        /* Check that this is a valid Base partner */
        checkValidBasePartner(pSource);

        /* Loop through the lists */
        Iterator<Map.Entry<E, MetisBaseList<MetisIndexedItem>>> myIterator = entrySetIterator();
        while (myIterator.hasNext()) {
            Map.Entry<E, MetisBaseList<MetisIndexedItem>> myEntry = myIterator.next();

            /* Obtain the source list */
            MetisBaseList<MetisIndexedItem> mySource = pSource.getList(myEntry.getKey());
            MetisBaseList<MetisIndexedItem> myTarget = myEntry.getValue();

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
    public void reBaseListSet(final MetisBaseListSet<E> pBase) {
        /* Not supported for readOnly listSets */
        if (isReadOnly()) {
            throw new UnsupportedOperationException();
        }

        /* Check that this is a valid Base partner */
        checkValidBasePartner(pBase);

        /* ListSet versions must be 0 */
        if ((getVersion() != 0)
            || (pBase.getVersion() != 0)) {
            throw new IllegalStateException("Versioned ListSet being reBased");
        }

        /* Determine the new Version */
        int myNewVersion = 0;

        /* Loop through the lists */
        Iterator<Map.Entry<E, MetisBaseList<MetisIndexedItem>>> myIterator = entrySetIterator();
        while (myIterator.hasNext()) {
            Map.Entry<E, MetisBaseList<MetisIndexedItem>> myEntry = myIterator.next();

            /* Obtain the source list */
            MetisBaseList<MetisIndexedItem> myBase = pBase.getList(myEntry.getKey());
            MetisBaseList<MetisIndexedItem> myTarget = myEntry.getValue();

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
    public MetisDifferenceListSet<E> deriveDifferences(final MetisBaseListSet<E> pOld) {
        /* Not supported for readOnly listSets */
        if (isReadOnly()) {
            throw new UnsupportedOperationException();
        }

        /* Check that this is a valid partner */
        checkValidBasePartner(pOld);

        /* Create a new difference set */
        MetisDifferenceListSet<E> myDifferences = new MetisDifferenceListSet<>(getEnumClass());

        /* Determine the new Version */
        int myNewVersion = 0;

        /* Loop through the lists */
        Iterator<Map.Entry<E, MetisBaseList<MetisIndexedItem>>> myIterator = entrySetIterator();
        while (myIterator.hasNext()) {
            Map.Entry<E, MetisBaseList<MetisIndexedItem>> myEntry = myIterator.next();

            /* Obtain the source list */
            E myType = myEntry.getKey();
            MetisBaseList<MetisIndexedItem> myOld = pOld.getList(myType);
            MetisBaseList<MetisIndexedItem> myNew = myEntry.getValue();

            /* Obtain the difference list and add if non-empty */
            MetisDifferenceList<MetisIndexedItem> myDifference = myNew.doDeriveDifferences(myOld);
            if (!myDifference.isEmpty()) {
                myDifferences.declareList(myType, myDifference);
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
    public MetisUpdateListSet<E> deriveUpdates() {
        /* Not supported for readOnly listSets */
        if (isReadOnly()) {
            throw new UnsupportedOperationException();
        }

        /* Create a new update set */
        MetisUpdateListSet<E> myUpdates = new MetisUpdateListSet<>(getEnumClass());

        /* Determine the new Version */
        int myNewVersion = 0;

        /* Loop through the lists */
        Iterator<Map.Entry<E, MetisBaseList<MetisIndexedItem>>> myIterator = entrySetIterator();
        while (myIterator.hasNext()) {
            Map.Entry<E, MetisBaseList<MetisIndexedItem>> myEntry = myIterator.next();

            /* Obtain the list */
            E myType = myEntry.getKey();
            MetisBaseList<MetisIndexedItem> myList = myEntry.getValue();

            /* Obtain the update list and add */
            MetisUpdateList<MetisIndexedItem> myUpdate = myList.deriveUpdates();
            myUpdates.declareList(myType, myUpdate);

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
    public MetisEditListSet<E> deriveEditSet() {
        /* Not supported for readOnly listSets */
        if (isReadOnly()) {
            throw new UnsupportedOperationException();
        }

        /* Create a new edit set */
        MetisEditListSet<E> myEdits = new MetisEditListSet<>(this);

        /* Loop through the lists */
        Iterator<Map.Entry<E, MetisBaseList<MetisIndexedItem>>> myIterator = entrySetIterator();
        while (myIterator.hasNext()) {
            Map.Entry<E, MetisBaseList<MetisIndexedItem>> myEntry = myIterator.next();

            /* Obtain the list */
            E myType = myEntry.getKey();
            MetisBaseList<MetisIndexedItem> myList = myEntry.getValue();

            /* Obtain the edit list and add */
            MetisEditList<MetisIndexedItem> myEdit = myList.deriveEditList();
            myEdits.declareList(myType, myEdit);
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

    /**
     * Check validity of base partner set.
     * @param pPartner the partnerSet
     */
    private void checkValidBasePartner(final MetisBaseListSet<E> pPartner) {
        /* Loop through the lists */
        Iterator<E> myIterator = enumIterator();
        while (myIterator.hasNext()) {
            E myKey = myIterator.next();

            /* Obtain the source list */
            MetisBaseList<?> mySource = pPartner.getList(myKey);
            MetisBaseList<?> myTarget = getList(myKey);

            /* Check that we have a similar set */
            boolean bValid = mySource == null
                                              ? myTarget == null
                                              : myTarget != null;
            if (!bValid) {
                throw new InvalidParameterException("Inconsistent Partner listSet");
            }
        }
    }
}

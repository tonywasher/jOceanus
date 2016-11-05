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

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;

/**
 * Set of EditLists.
 * @param <E> the list type identifier
 */
public class MetisEditListSet<E extends Enum<E>>
        extends MetisVersionedListSet<E, MetisEditList<?>> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MetisEditListSet.class.getSimpleName(), MetisVersionedListSet.getBaseFields());

    /**
     * EditVersion Field Id.
     */
    private static final MetisField FIELD_EDITVERSION = FIELD_DEFS.declareLocalField(MetisListResource.FIELD_EDITVERSION.getValue());

    /**
     * The edit version of the listSet.
     */
    private int theEditVersion;

    /**
     * Constructor.
     * @param pClass the enum class
     */
    protected MetisEditListSet(final Class<E> pClass) {
        super(MetisListType.EDIT, pClass, FIELD_DEFS);
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_EDITVERSION.equals(pField)) {
            return theEditVersion != 0
                                       ? theEditVersion
                                       : MetisFieldValue.SKIP;
        }

        /* Pass call on */
        return super.getFieldValue(pField);
    }

    /**
     * Reset the listSet.
     */
    public void reset() {
        /* Commit any pending editVersion */
        commitEditVersion();

        /* If we have changes */
        if (getVersion() != 0) {
            /* ReWind to initial version */
            doReWindToVersion(0);
        }
    }

    /**
     * ReWind the listSet to a particular version.
     * @param pVersion the version to reWind to
     */
    public void reWindToVersion(final int pVersion) {
        /* Check that the rewind version is valid */
        checkReWindVersion(pVersion);

        /* Commit any pending editVersion */
        commitEditVersion();

        /* ReWind it */
        doReWindToVersion(pVersion);
    }

    /**
     * Is the listSet Editing?
     * @return true/false
     */
    public boolean isEditing() {
        return theEditVersion != 0;
    }

    /**
     * Start Edit Version.
     */
    public void startEditVersion() {
        /* If we are not currently editing */
        if (!isEditing()) {
            /* Set next edit version */
            theEditVersion = getVersion() + 1;
        }
    }

    /**
     * Cancel the edit Version.
     */
    public void cancelEditVersion() {
        /* If we are currently editing */
        if (isEditing()) {
            /* Loop through the lists */
            Iterator<MetisEditList<?>> myIterator = listIterator();
            while (myIterator.hasNext()) {
                MetisEditList<?> myList = myIterator.next();

                /* If the list is editing */
                if (myList.isEditing()) {
                    /* Cancel the edit version */
                    myList.doCancelEditVersion();
                }
            }

            /* Clear editing version */
            theEditVersion = 0;
        }
    }

    /**
     * Commit the edit Version.
     */
    public void commitEditVersion() {
        /* Commit the edit version */
        if (isEditing()) {
            /* Loop through the lists */
            Iterator<MetisEditList<?>> myIterator = listIterator();
            while (myIterator.hasNext()) {
                MetisEditList<?> myList = myIterator.next();

                /* If the list is editing */
                if (myList.isEditing()) {
                    /* Cancel the edit version */
                    myList.doCommitEditVersion();
                }
            }

            /* Commit the edit version */
            setVersion(theEditVersion);
            theEditVersion = 0;
        }
    }

    /**
     * Commit the edit Session.
     */
    public void commitEditSession() {
        /* If we have editing updates */
        if (getVersion() > 0) {
            /* Loop through the lists */
            Iterator<MetisEditList<?>> myIterator = listIterator();
            while (myIterator.hasNext()) {
                MetisEditList<?> myList = myIterator.next();

                /* Commit any pending version */
                myList.commitEditVersion();

                /* If the list is editing */
                if (myList.getVersion() > 0) {
                    /* Commit the edit session */
                    myList.doCommitEditSession(theEditVersion);
                }
            }

            /* Reset the version */
            setVersion(0);
            theEditVersion = 0;
        }
    }

    /**
     * Prepare item for edit.
     * @param pItemType the item type
     * @param pItem the item
     */
    public void prepareItemForEdit(final E pItemType,
                                   final Object pItem) {
        /* Start editing */
        startEditVersion();

        /* Obtain the list */
        MetisEditList<?> myList = getList(pItemType);
        myList.prepareItemForEdit(pItem);
    }
}

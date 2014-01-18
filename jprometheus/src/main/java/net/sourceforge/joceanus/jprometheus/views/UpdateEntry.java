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
package net.sourceforge.joceanus.jprometheus.views;

import java.util.Iterator;

import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Update entry items.
 * @param <T> the data type
 */
public final class UpdateEntry<T extends DataItem & Comparable<? super T>> {
    /**
     * The class.
     */
    private final Class<T> theClass;

    /**
     * The DataList.
     */
    private DataList<T> theDataList;

    /**
     * Obtain the name of the entry.
     * @return the name
     */
    public String getName() {
        return theClass.getSimpleName();
    }

    /**
     * Obtain the list for the entry.
     * @return the list
     */
    public DataList<T> getDataList() {
        return theDataList;
    }

    /**
     * Is this entry related to this class.
     * @param pClass the class to compare
     * @return true/false
     */
    public boolean isClass(final Class<?> pClass) {
        return theClass.equals(pClass);
    }

    /**
     * Set the Data list.
     * @param pDataList the DataList
     */
    public void setDataList(final DataList<T> pDataList) {
        theDataList = pDataList;
    }

    /**
     * Constructor.
     * @param pClass the class
     */
    protected UpdateEntry(final Class<T> pClass) {
        /* Store details */
        theClass = pClass;
        theDataList = null;
    }

    /**
     * Prepare changes in an edit view back into the core data.
     * @throws JOceanusException on error
     */
    public void prepareChanges() throws JOceanusException {
        /* Ignore if we have no list */
        if (theDataList == null) {
            return;
        }

        /* Create an iterator for the changes list */
        Iterator<T> myIterator = theDataList.iterator();
        DataList<?> myBaseList = theDataList.getBaseList();

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
                    myBase = myBaseList.addCopyItem(myCurr);
                    myBase.setNewVersion();
                    myCurr.setBase(myBase);
                    myCurr.setId(myBase.getId());

                    /* Resolve the links */
                    myBase.resolveDataSetLinks();
                    break;

                /* If this is a deleted or deleted-changed item */
                case DELETED:
                case DELCHG:
                    /* Access the underlying item and mark as deleted */
                    myBase = myCurr.getBase();
                    myBase.setDeleted(true);
                    break;

                /* If this is a recovered item */
                case RECOVERED:
                    /* Access the underlying item and mark as restored */
                    myBase = myCurr.getBase();
                    myBase.setDeleted(false);
                    break;

                /* If this is a changed item */
                case CHANGED:
                    /* Access underlying item and apply changes */
                    myBase = myCurr.getBase();
                    myBase.applyChanges(myCurr);
                    break;
                default:
                    break;
            }
        }

        /* Re-sort the underlying list */
        myBaseList.reSort();
    }

    /**
     * RollBack changes in an edit view that have been applied to core data.
     */
    public void rollBackChanges() {
        /* Ignore if we have no list */
        if (theDataList == null) {
            return;
        }

        /* Create an iterator for this list */
        Iterator<T> myIterator = theDataList.iterator();
        DataList<?> myBaseList = theDataList.getBaseList();
        int myVersion = theDataList.getVersion();

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
                    myBaseList.remove(myCurr.getBase());
                    myCurr.setBase(null);
                    break;

                /* If we made changes to the underlying item */
                case DELETED:
                case DELCHG:
                case RECOVERED:
                case CHANGED:
                    /* Access underlying item */
                    myBase = myCurr.getBase();

                    /* Rewind any changes */
                    myBase.rewindToVersion(myVersion);
                    break;
                default:
                    break;
            }
        }

        /* Re-sort the underlying list */
        myBaseList.reSort();
    }

    /**
     * Commit changes in an edit view that have been applied to the core data.
     */
    public void commitChanges() {
        /* Ignore if we have no list */
        if (theDataList == null) {
            return;
        }

        /* Create an iterator for this list */
        Iterator<T> myIterator = theDataList.iterator();

        /* Loop through the elements */
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

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
                    break;
                default:
                    break;
            }
        }

        /* Set version back to zero */
        theDataList.setVersion(0);
    }
}

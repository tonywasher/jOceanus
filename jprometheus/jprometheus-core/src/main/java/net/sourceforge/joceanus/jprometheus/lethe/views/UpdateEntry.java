/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.views;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusListKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Update entry items.
 * @param <T> the data type
 */
public final class UpdateEntry<T extends DataItem & Comparable<? super T>>
        implements MetisDataList<T> {
    /**
     * The data type.
     */
    private final PrometheusListKey theDataType;

    /**
     * The DataList.
     */
    private DataList<T> theDataList;

    /**
     * Constructor.
     * @param pDataType the dataType
     */
    UpdateEntry(final PrometheusListKey pDataType) {
        /* Store details */
        theDataType = pDataType;
        theDataList = null;
    }

    @Override
    public List<T> getUnderlyingList() {
        return theDataList == null
                                   ? null
                                   : theDataList.getUnderlyingList();
    }

    /**
     * Obtain the name of the entry.
     * @return the name
     */
    public String getName() {
        return theDataType.toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Obtain the list for the entry.
     * @return the list
     */
    public DataList<T> getDataList() {
        return theDataList;
    }

    /**
     * Is this entry related to this dataType.
     * @param pDataType the dataType to compare
     * @return true/false
     */
    public boolean isDataType(final PrometheusListKey pDataType) {
        return theDataType.equals(pDataType);
    }

    /**
     * Set the Data list.
     * @param pDataList the DataList
     */
    public void setDataList(final DataList<T> pDataList) {
        theDataList = pDataList;
    }

    /**
     * Prepare changes in an edit view back into the core data.
     * @throws OceanusException on error
     */
    public void prepareChanges() throws OceanusException {
        /* Ignore if we have no list */
        if (theDataList == null) {
            return;
        }

        /* Create an iterator for the changes list */
        final Iterator<T> myIterator = theDataList.iterator();
        final DataList<?> myBaseList = theDataList.getBaseList();

        /* Loop through the elements */
        while (myIterator.hasNext()) {
            final DataItem myCurr = myIterator.next();
            final DataItem myBase;

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

                /* If this is a deleted item */
                case DELETED:
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

        /* Resolve links and re-sort the underlying list */
        myBaseList.resolveDataSetLinks();
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
        final Iterator<T> myIterator = theDataList.iterator();
        final DataList<?> myBaseList = theDataList.getBaseList();
        final int myVersion = theDataList.getVersion();

        /* Loop through the elements */
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();
            final DataItem myBase;

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
        final Iterator<T> myIterator = theDataList.iterator();

        /* Loop through the elements */
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

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

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

import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmetis.data.MetisValueSetHistory;
import net.sourceforge.joceanus.jmetis.newlist.MetisListChange.MetisListEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;

/**
 * Edit List.
 * @param <T> the item type
 * @param <B> the base item type
 */
public abstract class MetisEditList<T extends B, B extends MetisVersionedItem>
        extends MetisVersionedList<T> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MetisEditList.class.getSimpleName(), MetisVersionedList.getBaseFields());

    /**
     * Size Field Id.
     */
    private static final MetisField FIELD_SOURCE = FIELD_DEFS.declareLocalField("Source");

    /**
     * The source list.
     */
    private final MetisVersionedList<B> theSource;

    /**
     * Constructor.
     * @param pClass the item class
     * @param pSource the source list
     */
    protected MetisEditList(final Class<T> pClass,
                            final MetisVersionedList<B> pSource) {
        /* Initialise underlying class */
        super(pClass);
        theSource = pSource;

        /* Register listeners on underlying class */
        TethysEventRegistrar<MetisListEvent> myRegistrar = theSource.getEventRegistrar();
        myRegistrar.addEventListener(MetisListEvent.REWIND, this::handleReWind);
        myRegistrar.addEventListener(MetisListEvent.UPDATE, this::handleUpdate);
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_SOURCE.equals(pField)) {
            return theSource.isEmpty()
                                       ? MetisFieldValue.SKIP
                                       : theSource;
        }
        return super.getFieldValue(pField);
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisFields getBaseFields() {
        return FIELD_DEFS;
    }

    /**
     * Refresh the list.
     */
    public void refresh() {
        /* Clear the list */
        clear();

        /* Loop through the list */
        Iterator<B> myIterator = theSource.iterator();
        while (myIterator.hasNext()) {
            B myCurr = myIterator.next();

            /* Create the new item */
            T myItem = newEditItem(myCurr);
            addToList(myItem);
        }

        /* Reset the version */
        setVersion(0);
    }

    /**
     * Commit the list.
     */
    public void commitItems() {
        /* Create a new Change Detail */
        MetisListChange<T> myChange = new MetisListChange<>(MetisListEvent.COMMIT);
        MetisListChange<B> myBaseChange = new MetisListChange<>(MetisListEvent.COMMIT);

        /* Loop through the list */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();
            MetisValueSetHistory myHistory = myCurr.getValueSetHistory();

            /* Switch on state */
            switch (MetisDataState.determineState(myHistory)) {
                case NEW:
                    handleNewCommit(myCurr, myChange, myBaseChange);
                    break;
                case DELNEW:
                    handleDelNewCommit(myCurr, myChange);
                    break;
                case CHANGED:
                case DELETED:
                case RECOVERED:
                    handleCommit(myCurr, myChange, myBaseChange);
                    break;
                default:
                    break;
            }

            T myItem = newEditItem(myCurr);
            addToList(myItem);
        }

        /* If there are changes to the base list */
        if (myBaseChange.haveChanged()) {
            /* Update the version */
            theSource.setVersion(theSource.getVersion() + 1);
        }

        /* Fire the events */
        fireEvent(myChange);
        theSource.fireEvent(myBaseChange);
    }

    /**
     * handle delNew commit.
     * @param pItem the item
     * @param pChange the change details
     * @param pBaseChange the change details
     */
    private void handleNewCommit(final T pItem,
                                 final MetisListChange<T> pChange,
                                 final MetisListChange<B> pBaseChange) {
        /* Commit the item */
        B myItem = theSource.newCommittedItem(pItem);
        theSource.addToList(myItem);
        pItem.getValueSetHistory().clearHistory();

        /* Add to the change */
        pChange.registerChanged(pItem);
        pBaseChange.registerAdded(myItem);
    }

    /**
     * handle delNew commit.
     * @param pItem the item
     * @param pChange the change details
     */
    private void handleDelNewCommit(final T pItem,
                                    final MetisListChange<T> pChange) {
        removeFromList(pItem);
        pChange.registerDeleted(pItem);
    }

    /**
     * handle commit.
     * @param pItem the item
     * @param pChange the change details
     * @param pBaseChange the change details
     */
    private void handleCommit(final T pItem,
                              final MetisListChange<T> pChange,
                              final MetisListChange<B> pBaseChange) {
        /* Commit the item */
        B myItem = theSource.newItemValues(pItem);
        pItem.getValueSetHistory().clearHistory();

        /* Add to the change */
        pChange.registerChanged(pItem);
        pBaseChange.registerChanged(myItem);
    }

    /**
     * Create a New "edit" item.
     * @param pBase the base item
     * @return the new item
     */
    protected T newEditItem(final B pBase) {
        /* Obtain a new item */
        T myItem = newItem();

        /* Access the valueSet */
        MetisValueSet mySet = myItem.getValueSet();

        /* Obtain a clone of the value set as the base value */
        MetisValueSet myBase = pBase.getValueSet();
        mySet.copyFrom(myBase);

        /* Return the new item */
        return myItem;
    }

    /**
     * Handle Update.
     * @param pChange the change
     */
    private void handleReWind(final TethysEvent<MetisListEvent> pChange) {
        /* Access the change detail */
        @SuppressWarnings("unchecked")
        MetisListChange<B> myBaseChange = (MetisListChange<B>) pChange.getDetails(MetisListChange.class);
        MetisListChange<T> myChange = new MetisListChange<>(MetisListEvent.REWIND);

        /* Loop through the deleted items */
        Iterator<Integer> myIdIterator = myBaseChange.deletedIterator();
        while (myIdIterator.hasNext()) {
            Integer myId = myIdIterator.next();

            /* Obtain the item to be deleted */
            T myItem = getItemById(myId);
            removeFromList(myItem);

            /* Record deletion */
            myChange.registerDeleted(myItem);
        }

        /* Loop through the changed items */
        Iterator<B> myIterator = myBaseChange.changedIterator();
        while (myIterator.hasNext()) {
            B myCurr = myIterator.next();
            Integer myId = myCurr.getIndexedId();

            /* Obtain the item to be changed */
            T myItem = getItemById(myId);
            myItem.getValueSetHistory().resetHistory();
            MetisValueSet mySet = myItem.getValueSet();

            /* Obtain a clone of the value set as the base value */
            MetisValueSet myBase = myCurr.getValueSet();
            mySet.copyFrom(myBase);

            /* Record change */
            myChange.registerChanged(myItem);
        }

        /* Fire the events */
        fireEvent(myChange);
    }

    /**
     * Handle Update.
     * @param pChange the change
     */
    private void handleUpdate(final TethysEvent<MetisListEvent> pChange) {
        /* Access the change detail */
        @SuppressWarnings("unchecked")
        MetisListChange<B> myBaseChange = (MetisListChange<B>) pChange.getDetails(MetisListChange.class);
        MetisListChange<T> myChange = new MetisListChange<>(MetisListEvent.REWIND);

        /* Loop through the deleted items */
        Iterator<Integer> myIterator = myBaseChange.deletedIterator();
        while (myIterator.hasNext()) {
            Integer myId = myIterator.next();

            /* Obtain the item to be deleted */
            T myItem = getItemById(myId);
            removeFromList(myItem);

            /* Record deletion */
            myChange.registerDeleted(myItem);
        }

        /* Fire the events */
        fireEvent(myChange);
    }
}

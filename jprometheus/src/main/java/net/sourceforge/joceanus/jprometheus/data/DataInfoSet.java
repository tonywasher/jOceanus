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

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.function.Consumer;

import net.sourceforge.joceanus.jmetis.viewer.DataState;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EditState;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jprometheus.data.DataInfo.DataInfoList;
import net.sourceforge.joceanus.jprometheus.data.StaticData.StaticList;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Representation of an information set extension of a DataItem.
 * @author Tony Washer
 * @param <T> the data type
 * @param <O> the Owner DataItem that is extended by this item
 * @param <I> the Info Type that applies to this item
 * @param <S> the Info type class
 * @param <E> the data type enum class
 */
public abstract class DataInfoSet<T extends DataInfo<T, O, I, S, E>, O extends DataItem<E>, I extends StaticData<I, S, E>, S extends Enum<S> & DataInfoClass, E extends Enum<E>>
        implements JDataContents, Iterable<T> {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(PrometheusDataResource.DATAINFOSET_NAME.getValue());

    /**
     * Owner Field Id.
     */
    public static final JDataField FIELD_OWNER = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFO_OWNER.getValue());

    /**
     * Values Field Id.
     */
    public static final JDataField FIELD_VALUES = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_VALUES.getValue());

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_OWNER.equals(pField)) {
            return theOwner;
        }
        if (FIELD_VALUES.equals(pField)) {
            return theMap;
        }
        return JDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return getDataFields().getName();
    }

    /**
     * The Owner to which this set belongs.
     */
    private final O theOwner;

    /**
     * The InfoTypes for the InfoSet.
     */
    private final StaticList<I, S, E> theTypeList;

    /**
     * The DataInfoList for the InfoSet.
     */
    private final DataInfoList<T, O, I, S, E> theInfoList;

    /**
     * The Map of the DataInfo.
     */
    private final Map<S, DataInfo<T, O, I, S, E>> theMap;

    /**
     * The class of the entries.
     */
    private final Class<T> theClass;

    /**
     * Obtain owner.
     * @return the owner
     */
    public O getOwner() {
        return theOwner;
    }

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pTypeList the infoTypeList for the set
     * @param pInfoList the infoList for the set
     */
    protected DataInfoSet(final O pOwner,
                          final StaticList<I, S, E> pTypeList,
                          final DataInfoList<T, O, I, S, E> pInfoList) {
        /* Store the Owner and InfoType List */
        theOwner = pOwner;
        theTypeList = pTypeList;
        theInfoList = pInfoList;
        theClass = theInfoList.getBaseClass();

        /* Create the Map */
        theMap = new EnumMap<S, DataInfo<T, O, I, S, E>>(theTypeList.getEnumClass());
    }

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneTheDataInfoSet(final DataInfoSet<T, O, I, S, E> pSource) {
        /* Clone the InfoSet for each Event in the underlying Map */
        for (Entry<S, DataInfo<T, O, I, S, E>> myEntry : pSource.theMap.entrySet()) {
            /* Create the new value */
            DataInfo<T, O, I, S, E> myValue = myEntry.getValue();

            /* If this is an instance of a LinkSet */
            if (myValue instanceof DataInfoLinkSet) {
                /* Clone the infoLinkSet */
                DataInfoLinkSet<T, O, I, S, E> mySet = (DataInfoLinkSet<T, O, I, S, E>) myValue;
                DataInfo<T, O, I, S, E> myNew = new DataInfoLinkSet<T, O, I, S, E>(theInfoList, mySet);
                theMap.put(myEntry.getKey(), myNew);

                /* else its a standard entry */
            } else {
                /* Add to the map */
                DataInfo<T, O, I, S, E> myNew = theInfoList.addCopyItem(myValue);
                theMap.put(myEntry.getKey(), myNew);
            }
        }
    }

    /**
     * Obtain the value for the infoClass.
     * @param <X> the infoClass
     * @param pInfoClass the Info Class
     * @param pClass the Value Class
     * @return the value
     */
    public <X> X getValue(final S pInfoClass,
                          final Class<X> pClass) {
        /* Reject if called for LinkSet */
        if (pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Access existing entry */
        DataInfo<T, O, I, S, E> myValue = theMap.get(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the value */
        return myValue.getValue(pClass);
    }

    /**
     * Is there active values for the infoClass?
     * @param pInfoClass the info class
     * @return true/false
     */
    public boolean isExisting(final S pInfoClass) {
        /* Access the value */
        DataInfo<T, O, I, S, E> myInfo = theMap.get(pInfoClass);
        if (myInfo instanceof DataInfoLinkSet) {
            /* Access the info */
            DataInfoLinkSet<T, O, I, S, E> mySet = getInfoLinkSet(pInfoClass);
            return mySet.isExisting();
        }

        /* Handle standard info */
        return (myInfo != null) && !myInfo.isDeleted();
    }

    /**
     * Obtain the field value for the infoClass.
     * @param pInfoClass the Info Class
     * @return the value
     */
    public EncryptedField<?> getField(final S pInfoClass) {
        /* Reject if called for LinkSet */
        if (pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Access existing entry */
        DataInfo<T, O, I, S, E> myValue = theMap.get(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the field */
        return myValue.getField();
    }

    /**
     * Obtain the field value for the infoClass.
     * @param pInfoClass the Info Class
     * @return the value
     */
    protected T getInfo(final S pInfoClass) {
        /* Reject if called for LinkSet */
        if (pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Return the info */
        DataInfo<T, O, I, S, E> myInfo = theMap.get(pInfoClass);
        return theClass.cast(myInfo);
    }

    /**
     * Obtain the link iterator for the infoClass.
     * @param pInfoClass the Info Class
     * @return the iterator
     */
    public Iterator<T> linkIterator(final S pInfoClass) {
        /* Reject if not called for LinkSet */
        if (!pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Access existing entry */
        DataInfoLinkSet<T, O, I, S, E> mySet = getInfoLinkSet(pInfoClass);

        /* If we have no entry, return null */
        if (mySet == null) {
            return null;
        }

        /* Return the iterator */
        return mySet.iterator();
    }

    /**
     * link the value for the the infoClass.
     * @param pInfoClass the Info Class
     * @param pLink the link value
     */
    public void linkValue(final S pInfoClass,
                          final DataItem<E> pLink) {
        /* Reject if not called for LinkSet */
        if (!pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Access existing set */
        DataInfoLinkSet<T, O, I, S, E> mySet = getInfoLinkSet(pInfoClass);

        /* If we have no set, create one */
        if (mySet == null) {
            /* Obtain infoType */
            I myInfoType = theTypeList.findItemByClass(pInfoClass);

            /* Allocate the new set */
            mySet = new DataInfoLinkSet<T, O, I, S, E>(theInfoList, theOwner, myInfoType);

            /* Add to the map */
            theMap.put(pInfoClass, mySet);
        }

        /* Locate any existing link */
        T myItem = mySet.getItemForValue(pLink);

        /* If this is a new link */
        if (myItem == null) {
            /* Obtain infoType */
            I myInfoType = theTypeList.findItemByClass(pInfoClass);

            /* Create the entry and add to list */
            myItem = theInfoList.addNewItem(theOwner, myInfoType);
            myItem.setNewVersion();

            /* else if this is a deleted link */
        } else if (myItem.isDeleted()) {
            /* Restore the value */
            myItem.setDeleted(false);
        }
    }

    /**
     * clear the value for the the infoClass.
     * @param pInfoClass the Info Class
     * @param pLink the link value
     */
    public void clearValue(final S pInfoClass,
                           final DataItem<E> pLink) {
        /* Reject if not called for LinkSet */
        if (!pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Access existing set */
        DataInfoLinkSet<T, O, I, S, E> mySet = getInfoLinkSet(pInfoClass);

        /* If we have a set */
        if (mySet != null) {
            /* Locate the link */
            T myItem = mySet.getItemForValue(pLink);

            /* If it exists and is not deleted */
            if ((myItem != null)
                && !myItem.isDeleted()) {
                /* Delete the value */
                myItem.setDeleted(true);
            }
        }
    }

    /**
     * Obtain the infoLinkSet for the infoClass.
     * @param pInfoClass the Info Class
     * @return the value
     */
    @SuppressWarnings("unchecked")
    protected DataInfoLinkSet<T, O, I, S, E> getInfoLinkSet(final S pInfoClass) {
        /* Reject if not called for LinkSet */
        if (!pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Return the info */
        DataInfo<T, O, I, S, E> myInfo = theMap.get(pInfoClass);
        return DataInfoLinkSet.class.cast(myInfo);
    }

    /**
     * Determine whether a particular field has changed in this edit view.
     * @param pInfoClass the class to test
     * @return <code>true/false</code>
     */
    public Difference fieldChanged(final S pInfoClass) {
        /* If this is called for LinkSet */
        if (pInfoClass.isLinkSet()) {
            /* Access the info */
            DataInfoLinkSet<T, O, I, S, E> mySet = getInfoLinkSet(pInfoClass);
            return mySet.fieldChanged();
        }

        /* Access the info */
        T myInfo = getInfo(pInfoClass);

        /* Return change details */
        return (myInfo != null) && myInfo.hasHistory()
                                                      ? Difference.DIFFERENT
                                                      : Difference.IDENTICAL;
    }

    /**
     * Set the value for the infoClass.
     * @param pInfoClass the Info Class
     * @param pValue the Value
     * @throws JOceanusException on error
     */
    public void setValue(final S pInfoClass,
                         final Object pValue) throws JOceanusException {
        /* Reject if called for LinkSet */
        if (pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Determine whether this is a deletion */
        boolean bDelete = pValue == null;

        /* Obtain the Map value */
        DataInfo<T, O, I, S, E> myValue = theMap.get(pInfoClass);

        /* If we are deleting */
        if (bDelete) {
            /* Mark existing value as deleted */
            if (myValue != null) {
                myValue.markDeleted();
            }

            /* Return to caller */
            return;
        }

        /* If we have no entry */
        if (myValue == null) {
            /* Obtain infoType */
            I myInfoType = theTypeList.findItemByClass(pInfoClass);

            /* Create the entry and add to list */
            myValue = theInfoList.addNewItem(theOwner, myInfoType);
            myValue.setNewVersion();

            /* Insert the value into the map */
            theMap.put(pInfoClass, myValue);
        }

        /* Update the value */
        myValue.setValue(pValue);
    }

    /**
     * Sort linkSets.
     */
    public void sortLinkSets() {
        /* Loop through each existing value */
        for (DataInfo<T, O, I, S, E> myValue : theMap.values()) {
            /* If this is an instance of a LinkSet */
            if (myValue instanceof DataInfoLinkSet) {
                /* Pass call to linkSet */
                DataInfoLinkSet<T, O, I, S, E> mySet = (DataInfoLinkSet<T, O, I, S, E>) myValue;
                mySet.sortLinks();
            }
        }
    }

    /**
     * Register Info.
     * @param pInfo the info to register
     * @throws JOceanusException on error
     */
    public void registerInfo(final T pInfo) throws JOceanusException {
        /* Obtain the existing Map value */
        S myClass = pInfo.getInfoClass();

        /* If this is an instance of a LinkSet */
        if (myClass.isLinkSet()) {
            /* Access existing entry */
            DataInfoLinkSet<T, O, I, S, E> mySet = getInfoLinkSet(myClass);
            if (mySet == null) {
                /* Allocate the new set */
                mySet = new DataInfoLinkSet<T, O, I, S, E>(theInfoList, theOwner, pInfo.getInfoType());

                /* Add to the map */
                theMap.put(myClass, mySet);
            }

            /* link the item */
            mySet.linkItem(pInfo);

            /* else it is a standard item */
        } else {
            /* Access existing entry */
            DataInfo<T, O, I, S, E> myValue = theMap.get(myClass);

            /* Reject if duplicate and not re-registration */
            if ((myValue != null) && !myValue.getId().equals(pInfo.getId())) {
                throw new IllegalArgumentException("Duplicate information type " + pInfo.getInfoClass());
            }

            /* Add to the map */
            theMap.put(myClass, pInfo);
        }
    }

    /**
     * deRegister Info.
     * @param pInfo the info to deRegister
     */
    public void deRegisterInfo(final T pInfo) {
        /* Obtain the existing Map value */
        S myClass = pInfo.getInfoClass();

        /* If this is an instance of a LinkSet */
        if (myClass.isLinkSet()) {
            /* Access existing entry */
            DataInfoLinkSet<T, O, I, S, E> mySet = getInfoLinkSet(myClass);
            if (mySet != null) {
                /* Unlink the item */
                mySet.unlinkItem(pInfo);

                /* If the set is now empty */
                if (mySet.isEmpty()) {
                    /* Remove the set from the map */
                    theMap.remove(myClass);
                }
            }

            /* else it is a standard info */
        } else {
            /* Remove from the map */
            theMap.remove(myClass);
        }
    }

    /**
     * touch underlying items.
     */
    public void touchUnderlyingItems() {
        /* Loop through each existing value */
        for (DataInfo<T, O, I, S, E> myValue : theMap.values()) {
            /* If this is an instance of a LinkSet */
            if (myValue instanceof DataInfoLinkSet) {
                /* Pass call to linkSet */
                DataInfoLinkSet<T, O, I, S, E> mySet = (DataInfoLinkSet<T, O, I, S, E>) myValue;
                mySet.touchUnderlyingItems();

                /* else this is a standard DataInfo */
            } else {
                /* Touch the owner */
                theOwner.touchItem(myValue);

                /* Touch the underlying items */
                myValue.touchUnderlyingItems();
            }
        }
    }

    /**
     * Determine whether the set has changes.
     * @return <code>true/false</code>
     */
    public boolean hasHistory() {
        boolean bChanges = false;

        /* Loop through each existing value */
        for (DataInfo<T, O, I, S, E> myValue : theMap.values()) {
            /* If this is an instance of a LinkSet */
            if (myValue instanceof DataInfoLinkSet) {
                /* Pass call to linkSet */
                DataInfoLinkSet<T, O, I, S, E> mySet = (DataInfoLinkSet<T, O, I, S, E>) myValue;
                bChanges |= mySet.hasHistory();

            } else {
                /* Check for history */
                bChanges |= myValue.hasHistory();
            }
        }

        /* return result */
        return bChanges;
    }

    /**
     * Push history.
     */
    public void pushHistory() {
        /* Loop through each existing value */
        for (DataInfo<T, O, I, S, E> myValue : theMap.values()) {
            /* If this is an instance of a LinkSet */
            if (myValue instanceof DataInfoLinkSet) {
                /* Pass call to linkSet */
                DataInfoLinkSet<T, O, I, S, E> mySet = (DataInfoLinkSet<T, O, I, S, E>) myValue;
                mySet.pushHistory();

            } else {
                /* Push history for the value */
                myValue.pushHistory();
            }
        }
    }

    /**
     * Pop history.
     */
    public void popHistory() {
        /* Iterate through table values */
        Iterator<DataInfo<T, O, I, S, E>> myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            DataInfo<T, O, I, S, E> myValue = myIterator.next();

            /* If this is an instance of a LinkSet */
            if (myValue instanceof DataInfoLinkSet) {
                /* Pass call to linkSet */
                DataInfoLinkSet<T, O, I, S, E> mySet = (DataInfoLinkSet<T, O, I, S, E>) myValue;
                mySet.popHistory();

                /* If the set is now empty */
                if (mySet.isEmpty()) {
                    /* Remove the value */
                    myIterator.remove();
                }

                /* else this is a standard element */
            } else {
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
    }

    /**
     * Check for history.
     * @return <code>true</code> if changes were made, <code>false</code> otherwise
     */
    public boolean checkForHistory() {
        /* Loop through each existing value */
        boolean bChanges = false;
        for (DataInfo<T, O, I, S, E> myValue : theMap.values()) {
            /* If this is an instance of a LinkSet */
            if (myValue instanceof DataInfoLinkSet) {
                /* Pass call to linkSet */
                DataInfoLinkSet<T, O, I, S, E> mySet = (DataInfoLinkSet<T, O, I, S, E>) myValue;
                bChanges |= mySet.checkForHistory();

            } else {
                /* If this is a newly created item */
                if (!myValue.hasHistory()) {
                    bChanges = true;

                    /* else existing entry */
                } else {
                    /* Check for history */
                    bChanges |= myValue.checkForHistory();
                }
            }
        }

        /* return result */
        return bChanges;
    }

    /**
     * Set values as deleted/restored.
     * @param bDeleted <code>true/false</code>
     */
    public void setDeleted(final boolean bDeleted) {
        /* If we are restoring */
        if (!bDeleted) {
            /* Handle separately */
            setRestored();
            return;
        }

        /* For each existing value */
        for (DataInfo<T, O, I, S, E> myValue : theMap.values()) {
            /* If the value is active */
            if (!myValue.isDeleted()) {
                /* Set the value as deleted */
                myValue.setDeleted(true);
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
        for (DataInfo<T, O, I, S, E> myValue : theMap.values()) {
            /* If this is an instance of a LinkSet */
            if (myValue instanceof DataInfoLinkSet) {
                /* Pass call to linkSet */
                myValue.setDeleted(false);

            } else {
                /* Access version of value */
                int myValueVersion = (bEditRestore)
                                                   ? myValue.getValueSetVersion()
                                                   : myValue.getBase().getValueSetVersion();

                /* If the value was deleted at same time as owner */
                if (myValueVersion == myVersion) {
                    /* Set the value as restored */
                    myValue.setDeleted(false);
                }
            }
        }
    }

    /**
     * Get the EditState for this item.
     * @return the EditState
     */
    public EditState getEditState() {
        /* Loop through each existing value */
        for (DataInfo<T, O, I, S, E> myValue : theMap.values()) {
            /* If we have changes */
            if (myValue.hasHistory()) {
                /* Note that new state is changed */
                return EditState.VALID;
            }
        }

        /* Default to clean */
        return EditState.CLEAN;
    }

    /**
     * Get the State for this infoSet.
     * @return the State
     */
    public DataState getState() {
        /* Default to clean */
        DataState myState = DataState.CLEAN;

        /* Loop through each existing value */
        for (DataInfo<T, O, I, S, E> myValue : theMap.values()) {
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
     * Obtain iterator.
     * @return the iterator over the values.
     */
    public Iterator<T> iterator() {
        return new InfoIterator();
    }

    @Override
    public void forEach(final Consumer<? super T> pAction) {
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            pAction.accept(myIterator.next());
        }
    }

    @Override
    public Spliterator<T> spliterator() {
        throw new UnsupportedOperationException();
    }

    /**
     * Is the infoSet empty?
     * @return true/false.
     */
    public boolean isEmpty() {
        return theMap.isEmpty();
    }

    /**
     * Iterator class.
     */
    private final class InfoIterator
            implements Iterator<T> {
        /**
         * Overall iterator.
         */
        private final Iterator<DataInfo<T, O, I, S, E>> theIterator;

        /**
         * Local iterator.
         */
        private Iterator<T> theSetIterator;

        /**
         * Constructor.
         */
        private InfoIterator() {
            /* Allocate iterator */
            theIterator = theMap.values().iterator();
        }

        @Override
        public boolean hasNext() {
            /* If we have a set iterator with more to go */
            if ((theSetIterator != null) && (theSetIterator.hasNext())) {
                /* We have a next item */
                return true;
            }

            /* No longer have a valid setIterator */
            theSetIterator = null;

            /* Check for next entry */
            return theIterator.hasNext();
        }

        @Override
        public T next() {
            /* If we have a set iterator with more to go */
            if ((theSetIterator != null) && (theSetIterator.hasNext())) {
                /* Return the item */
                return theSetIterator.next();
            }

            /* No longer have a valid setIterator */
            theSetIterator = null;

            /* Obtain the next item */
            DataInfo<T, O, I, S, E> myItem = theIterator.next();

            /* If this is an infoLinkSet */
            if (myItem instanceof DataInfoLinkSet) {
                /* Access set iterator and return first element */
                DataInfoLinkSet<T, O, I, S, E> mySet = (DataInfoLinkSet<T, O, I, S, E>) myItem;
                theSetIterator = mySet.iterator();
                return theSetIterator.next();
            }

            /* Return the item */
            return theClass.cast(myItem);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachRemaining(final Consumer<? super T> pAction) {
            while (hasNext()) {
                pAction.accept(next());
            }
        }
    }
}

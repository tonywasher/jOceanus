/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.prometheus.data;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataEditState;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataState;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoItem.PrometheusDataInfoList;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataItem.PrometheusStaticList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Representation of an information set extension of a DataItem.
 * @author Tony Washer
 * @param <T> the data type
 */
public abstract class PrometheusDataInfoSet<T extends PrometheusDataInfoItem>
        implements MetisFieldItem, Iterable<T> {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<PrometheusDataInfoSet> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusDataInfoSet.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFO_OWNER, PrometheusDataInfoSet::getOwner);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_VALUES, PrometheusDataInfoSet::getValues);
    }

    /**
     * The Owner to which this set belongs.
     */
    private final PrometheusDataItem theOwner;

    /**
     * The InfoTypes for the InfoSet.
     */
    private final PrometheusStaticList<?> theTypeList;

    /**
     * The DataInfoList for the InfoSet.
     */
    private final PrometheusDataInfoList<T> theInfoList;

    /**
     * The Map of the DataInfo.
     */
    private final Map<PrometheusDataInfoClass, PrometheusDataInfoItem> theMap;

    /**
     * The class of the entries.
     */
    private final Class<T> theClass;

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pTypeList the infoTypeList for the set
     * @param pInfoList the infoList for the set
     */
    protected PrometheusDataInfoSet(final PrometheusDataItem pOwner,
                                    final PrometheusStaticList<?> pTypeList,
                                    final PrometheusDataInfoList<T> pInfoList) {
        /* Store the Owner and InfoType List */
        theOwner = pOwner;
        theTypeList = pTypeList;
        theInfoList = pInfoList;
        theClass = theInfoList.getBaseClass();

        /* Create the Map */
        theMap = new HashMap<>();
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return this.getDataFieldSet().getName();
    }

    /**
     * Obtain owner.
     * @return the owner
     */
    public PrometheusDataItem getOwner() {
        return theOwner;
    }

    /**
     * Obtain values.
     * @return the values
     */
    private Map<PrometheusDataInfoClass, PrometheusDataInfoItem> getValues() {
        return theMap;
    }

    /**
     * Obtain class iterator for the underlying infoClasses.
     * @return the Iterator
     */
    public abstract Iterator<PrometheusDataInfoClass> classIterator();

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    @SuppressWarnings("unchecked")
    protected void cloneTheDataInfoSet(final PrometheusDataInfoSet<T> pSource) {
        /* Clone the InfoSet for each Event in the underlying Map */
        for (Entry<PrometheusDataInfoClass, PrometheusDataInfoItem> myEntry : pSource.theMap.entrySet()) {
            /* Create the new value */
            final PrometheusDataInfoItem myValue = myEntry.getValue();

            /* If this is an instance of a LinkSet */
            if (myValue instanceof PrometheusDataInfoLinkSet) {
                /* Clone the infoLinkSet */
                final PrometheusDataInfoLinkSet<T> mySet = (PrometheusDataInfoLinkSet<T>) myValue;
                final PrometheusDataInfoItem myNew = new PrometheusDataInfoLinkSet<>(theInfoList, mySet);
                theMap.put(myEntry.getKey(), myNew);

                /* else its a standard entry */
            } else {
                /* Add to the map */
                final PrometheusDataInfoItem myNew = theInfoList.addCopyItem(myValue);
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
    public <X> X getValue(final PrometheusDataInfoClass pInfoClass,
                          final Class<X> pClass) {
        /* Reject if called for LinkSet */
        if (pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Access existing entry */
        final PrometheusDataInfoItem myValue = theMap.get(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the value */
        return myValue.getValue(pClass);
    }

    /**
     * Obtain the list iterator for the infoClass.
     * @param pInfoClass the Info Class
     * @return the iterator
     */
    public List<?> getListValue(final PrometheusDataInfoClass pInfoClass) {
        /* Reject if not called for LinkSet */
        if (!pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Access existing entry */
        final PrometheusDataInfoLinkSet<T> mySet = getInfoLinkSet(pInfoClass);

        /* Return list if it is available */
        return mySet == null
                ? null
                : mySet.getActive();
    }

    /**
     * Is there active values for the infoClass?
     * @param pInfoClass the info class
     * @return true/false
     */
    public boolean isExisting(final PrometheusDataInfoClass pInfoClass) {
        /* Access the value */
        final PrometheusDataInfoItem myInfo = theMap.get(pInfoClass);
        if (myInfo instanceof PrometheusDataInfoLinkSet) {
            /* Access the info */
            final PrometheusDataInfoLinkSet<T> mySet = getInfoLinkSet(pInfoClass);
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
    public PrometheusEncryptedPair getField(final PrometheusDataInfoClass pInfoClass) {
        /* Reject if called for LinkSet */
        if (pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Access existing entry */
        final PrometheusDataInfoItem myValue = theMap.get(pInfoClass);

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
    public T getInfo(final PrometheusDataInfoClass pInfoClass) {
        /* Reject if called for LinkSet */
        if (pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Return the info */
        final PrometheusDataInfoItem myInfo = theMap.get(pInfoClass);
        return theClass.cast(myInfo);
    }

    /**
     * link the value for the infoClass.
     * @param pInfoClass the Info Class
     * @param pLink the link value
     * @throws OceanusException on error
     */
    public void linkValue(final PrometheusDataInfoClass pInfoClass,
                          final PrometheusDataItem pLink) throws OceanusException {
        /* Reject if not called for LinkSet */
        if (!pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Access existing set */
        PrometheusDataInfoLinkSet<T> mySet = getInfoLinkSet(pInfoClass);

        /* If we have no set, create one */
        if (mySet == null) {
            /* Obtain infoType */
            final PrometheusStaticDataItem myInfoType = theTypeList.findItemByClass(pInfoClass);

            /* Allocate the new set */
            mySet = new PrometheusDataInfoLinkSet<>(theInfoList, theOwner, myInfoType);

            /* Add to the map */
            theMap.put(pInfoClass, mySet);
        }

        /* Locate any existing link */
        T myItem = mySet.getItemForValue(pLink);

        /* If this is a new link */
        if (myItem == null) {
            /* Obtain infoType */
            final PrometheusStaticDataItem myInfoType = theTypeList.findItemByClass(pInfoClass);

            /* Create the entry and add to list */
            myItem = theInfoList.addNewItem(theOwner, myInfoType);
            myItem.setValue(pLink);
            myItem.setNewVersion();

            /* link the item */
            mySet.linkItem(myItem);
            mySet.sortLinks();

            /* else if this is a deleted link */
        } else if (myItem.isDeleted()) {
            /* Restore the value */
            myItem.setDeleted(false);
        }
    }

    /**
     * set the list value for the infoClass.
     * @param pInfoClass the Info Class
     * @param pLinks the links value
     * @throws OceanusException on error
     */
    public void setListValue(final PrometheusDataInfoClass pInfoClass,
                             final List<? extends PrometheusDataItem> pLinks) throws OceanusException {
        /* Reject if not called for LinkSet */
        if (!pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Access existing set */
        PrometheusDataInfoLinkSet<T> mySet = getInfoLinkSet(pInfoClass);

        /* If we now have no selected values */
        if (pLinks == null || pLinks.isEmpty()) {
            /* If we have a set */
            if (mySet != null) {
                /* Clear all values in linkSet */
                mySet.clearAllLinks();
                theMap.remove(pInfoClass);
            }

            /* Else we have selected values */
        } else {
            /* If we do not currently have a set */
            if (mySet == null) {
                /* Obtain infoType */
                final PrometheusStaticDataItem myInfoType = theTypeList.findItemByClass(pInfoClass);

                /* Allocate the new set */
                mySet = new PrometheusDataInfoLinkSet<>(theInfoList, theOwner, myInfoType);

                /* Add to the map */
                theMap.put(pInfoClass, mySet);
            }

            /* Clear out unnecessary links */
            mySet.clearUnnecessaryLinks(pLinks);

            /* Add new links */
            mySet.addNewLinks(pLinks);
            mySet.sortLinks();
        }
    }

    /**
     * Obtain the infoLinkSet for the infoClass.
     * @param pInfoClass the Info Class
     * @return the value
     */
    @SuppressWarnings("unchecked")
    protected PrometheusDataInfoLinkSet<T> getInfoLinkSet(final PrometheusDataInfoClass pInfoClass) {
        /* Reject if not called for LinkSet */
        if (!pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Return the info */
        final PrometheusDataInfoItem myInfo = theMap.get(pInfoClass);
        return (PrometheusDataInfoLinkSet<T>) myInfo;
    }

    /**
     * Determine whether a particular field has changed in this edit view.
     * @param pInfoClass the class to test
     * @return <code>true/false</code>
     */
    public MetisDataDifference fieldChanged(final PrometheusDataInfoClass pInfoClass) {
        /* If this is called for LinkSet */
        if (pInfoClass.isLinkSet()) {
            /* Access the info */
            final PrometheusDataInfoLinkSet<T> mySet = getInfoLinkSet(pInfoClass);
            return mySet == null
                    ? MetisDataDifference.IDENTICAL
                    : mySet.fieldChanged();
        }

        /* Access the info */
        final T myInfo = getInfo(pInfoClass);

        /* Return change details */
        if (myInfo == null) {
            return MetisDataDifference.DIFFERENT;
        }
        return myInfo.hasHistory()
                || MetisDataState.NEW.equals(myInfo.getState())
                ? MetisDataDifference.DIFFERENT
                : MetisDataDifference.IDENTICAL;
    }

    /**
     * Set the value for the infoClass.
     * @param pInfoClass the Info Class
     * @param pValue the Value
     * @throws OceanusException on error
     */
    public void setValue(final PrometheusDataInfoClass pInfoClass,
                         final Object pValue) throws OceanusException {
        /* Reject if called for LinkSet */
        if (pInfoClass.isLinkSet()) {
            throw new UnsupportedOperationException();
        }

        /* Determine whether this is a deletion */
        final boolean bDelete = pValue == null;

        /* Obtain the Map value */
        PrometheusDataInfoItem myValue = theMap.get(pInfoClass);

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
            final PrometheusStaticDataItem myInfoType = theTypeList.findItemByClass(pInfoClass);

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
    @SuppressWarnings("unchecked")
    public void sortLinkSets() {
        /* Loop through each existing value */
        for (PrometheusDataInfoItem myValue : theMap.values()) {
            /* If this is an instance of a LinkSet */
            if (myValue instanceof PrometheusDataInfoLinkSet) {
                /* Pass call to linkSet */
                final PrometheusDataInfoLinkSet<T> mySet = (PrometheusDataInfoLinkSet<T>) myValue;
                mySet.sortLinks();
            }
        }
    }

    /**
     * Register Info.
     * @param pInfo the info to register
     * @throws OceanusException on error
     */
    public void registerInfo(final T pInfo) throws OceanusException {
        /* Obtain the existing Map value */
        final PrometheusDataInfoClass myClass = pInfo.getInfoClass();

        /* If this is an instance of a LinkSet */
        if (myClass.isLinkSet()) {
            /* Access existing entry */
            PrometheusDataInfoLinkSet<T> mySet = getInfoLinkSet(myClass);
            if (mySet == null) {
                /* Allocate the new set */
                mySet = new PrometheusDataInfoLinkSet<>(theInfoList, theOwner, pInfo.getInfoType());

                /* Add to the map */
                theMap.put(myClass, mySet);
            }

            /* link the item */
            mySet.linkItem(pInfo);

            /* else it is a standard item */
        } else {
            /* Access existing entry */
            final PrometheusDataInfoItem myValue = theMap.get(myClass);

            /* Reject if duplicate and not re-registration */
            if ((myValue != null) && !myValue.getIndexedId().equals(pInfo.getIndexedId())) {
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
        final PrometheusDataInfoClass myClass = pInfo.getInfoClass();

        /* If this is an instance of a LinkSet */
        if (myClass.isLinkSet()) {
            /* Access existing entry */
            final PrometheusDataInfoLinkSet<T> mySet = getInfoLinkSet(myClass);
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
     * deRegister Info.
     * @param pInfo the info to deRegister
     */
    public void rewindInfoLinkSet(final T pInfo) {
        /* Obtain the existing Map value */
        final PrometheusDataInfoClass myClass = pInfo.getInfoClass();

        /* If this is an instance of a LinkSet */
        if (myClass.isLinkSet()) {
            /* Access existing entry */
            final PrometheusDataInfoLinkSet<T> mySet = getInfoLinkSet(myClass);
            if (mySet != null) {
                /* reSort the links */
                mySet.sortLinks();
            } else {
                /* Remove the set from the map */
                theMap.remove(myClass);
            }

            /* illegal operation */
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * wipe information regarding the infoClass.
     * @param pInfoClass the Info Class
     */
    public void wipeInfo(final PrometheusDataInfoClass pInfoClass) {
        /* If we have an item for the class */
        final PrometheusDataInfoItem myInfo = theMap.get(pInfoClass);
        if (myInfo != null) {
            /* Remove and unlink it */
            theMap.remove(pInfoClass);
            theInfoList.remove(myInfo);
        }
    }

    /**
     * touch underlying items.
     */
    @SuppressWarnings("unchecked")
    public void touchUnderlyingItems() {
        /* Loop through each existing value */
        for (PrometheusDataInfoItem myValue : theMap.values()) {
            /* If this is an instance of a LinkSet */
            if (myValue instanceof PrometheusDataInfoLinkSet) {
                /* Pass call to linkSet */
                final PrometheusDataInfoLinkSet<T> mySet = (PrometheusDataInfoLinkSet<T>) myValue;
                mySet.touchUnderlyingItems();

                /* else this is a standard DataInfo */
            } else {
                /* Touch the underlying items */
                myValue.touchUnderlyingItems();
            }
        }
    }

    /**
     * touch underlying items after update.
     */
    @SuppressWarnings("unchecked")
    public void touchOnUpdate() {
        /* Loop through each existing value */
        for (PrometheusDataInfoItem myValue : theMap.values()) {
            /* If this is an instance of a LinkSet */
            if (myValue instanceof PrometheusDataInfoLinkSet) {
                /* Pass call to linkSet */
                final PrometheusDataInfoLinkSet<T> mySet = (PrometheusDataInfoLinkSet<T>) myValue;
                mySet.touchOnUpdate();

                /* else this is a standard DataInfo */
            } else {
                /* Touch the underlying items */
                myValue.touchOnUpdate();
            }
        }
    }

    /**
     * Determine whether the set has changes.
     * @return <code>true/false</code>
     */
    @SuppressWarnings("unchecked")
    public boolean hasHistory() {
        boolean bChanges = false;

        /* Loop through each existing value */
        for (PrometheusDataInfoItem myValue : theMap.values()) {
            /* If this is an instance of a LinkSet */
            if (myValue instanceof PrometheusDataInfoLinkSet) {
                /* Pass call to linkSet */
                final PrometheusDataInfoLinkSet<T> mySet = (PrometheusDataInfoLinkSet<T>) myValue;
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
    @SuppressWarnings("unchecked")
    public void pushHistory() {
        /* Loop through each existing value */
        for (PrometheusDataInfoItem myValue : theMap.values()) {
            /* If this is an instance of a LinkSet */
            if (myValue instanceof PrometheusDataInfoLinkSet) {
                /* Pass call to linkSet */
                final PrometheusDataInfoLinkSet<T> mySet = (PrometheusDataInfoLinkSet<T>) myValue;
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
    @SuppressWarnings("unchecked")
    public void popHistory() {
        /* Iterate through table values */
        final Iterator<PrometheusDataInfoItem> myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            final PrometheusDataInfoItem myValue = myIterator.next();

            /* If this is an instance of a LinkSet */
            if (myValue instanceof PrometheusDataInfoLinkSet) {
                /* Pass call to linkSet */
                final PrometheusDataInfoLinkSet<T> mySet = (PrometheusDataInfoLinkSet<T>) myValue;
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
    @SuppressWarnings("unchecked")
    public boolean checkForHistory() {
        /* Loop through each existing value */
        boolean bChanges = false;
        for (PrometheusDataInfoItem myValue : theMap.values()) {
            /* If this is an instance of a LinkSet */
            if (myValue instanceof PrometheusDataInfoLinkSet) {
                /* Pass call to linkSet */
                final PrometheusDataInfoLinkSet<T> mySet = (PrometheusDataInfoLinkSet<T>) myValue;
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
        for (PrometheusDataInfoItem myValue : theMap.values()) {
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
        final boolean bEditRestore = myVersion > 0;
        if (!bEditRestore) {
            /* Access underlying version if not editRestore */
            myVersion = theOwner.getBase().getValueSetVersion();
        }

        /* For each existing value */
        for (PrometheusDataInfoItem myValue : theMap.values()) {
            /* If this is an instance of a LinkSet */
            if (myValue instanceof PrometheusDataInfoLinkSet) {
                /* Pass call to linkSet */
                myValue.setDeleted(false);

            } else {
                /* Access version of value */
                final int myValueVersion = bEditRestore
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
    public MetisDataEditState getEditState() {
        /* Loop through each existing value */
        for (PrometheusDataInfoItem myValue : theMap.values()) {
            /* If we have changes */
            if (myValue.hasHistory()) {
                /* Note that new state is changed */
                return MetisDataEditState.VALID;
            }
        }

        /* Default to clean */
        return MetisDataEditState.CLEAN;
    }

    /**
     * Get the State for this infoSet.
     * @return the State
     */
    public MetisDataState getState() {
        /* Default to clean */
        final MetisDataState myState = MetisDataState.CLEAN;

        /* Loop through each existing value */
        for (PrometheusDataInfoItem myValue : theMap.values()) {
            /* If we have changes */
            if (myValue.getState() != MetisDataState.CLEAN) {
                /* Note that new state is changed */
                return MetisDataState.CHANGED;
            }
        }

        /* return result */
        return myState;
    }

    @Override
    public Iterator<T> iterator() {
        return new InfoIterator();
    }

    @Override
    public void forEach(final Consumer<? super T> pAction) {
        final Iterator<T> myIterator = iterator();
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
     * Remove items.
     */
    public void removeItems() {
        final InfoIterator myIterator = new InfoIterator();
        while (myIterator.hasNext()) {
            final T myItem = myIterator.next();
            myItem.removeItem();
        }
    }

    /**
     * Iterator class.
     */
    private final class InfoIterator
            implements Iterator<T> {
        /**
         * Overall iterator.
         */
        private final Iterator<PrometheusDataInfoItem> theIterator;

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
            if (theSetIterator != null
                    && theSetIterator.hasNext()) {
                /* We have a next item */
                return true;
            }

            /* No longer have a valid setIterator */
            theSetIterator = null;

            /* Check for next entry */
            return theIterator.hasNext();
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            /* If we have a set iterator with more to go */
            if (theSetIterator != null
                    && theSetIterator.hasNext()) {
                /* Return the item */
                return theSetIterator.next();
            }

            /* No longer have a valid setIterator */
            theSetIterator = null;

            /* Obtain the next item */
            final PrometheusDataInfoItem myItem = theIterator.next();

            /* If this is an infoLinkSet */
            if (myItem instanceof PrometheusDataInfoLinkSet) {
                /* Access set iterator and return first element */
                final PrometheusDataInfoLinkSet<T> mySet = (PrometheusDataInfoLinkSet<T>) myItem;
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

    /**
     * Obtain the field for the infoSet class.
     * @param pClass the class
     * @return the field
     */
    public abstract MetisDataFieldId getFieldForClass(PrometheusDataInfoClass pClass);
}

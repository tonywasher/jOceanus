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
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.DataState;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EditState;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jprometheus.data.DataInfo.DataInfoList;
import net.sourceforge.joceanus.jprometheus.data.StaticData.StaticList;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedField;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Representation of an information set extension of a DataItem.
 * @author Tony Washer
 * @param <T> the data type
 * @param <O> the Owner DataItem that is extended by this item
 * @param <I> the Info Type that applies to this item
 * @param <E> the Info type class
 */
public abstract class DataInfoSet<T extends DataInfo<T, O, I, E>, O extends DataItem, I extends StaticData<I, E>, E extends Enum<E> & DataInfoClass>
        implements JDataContents, Iterable<T> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(DataInfoSet.class.getName());

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * Version Field Id.
     */
    public static final JDataField FIELD_VERSION = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataVersion"));

    /**
     * Owner Field Id.
     */
    public static final JDataField FIELD_OWNER = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataOwner"));

    /**
     * Values Field Id.
     */
    public static final JDataField FIELD_VALUES = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataValues"));

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_VERSION.equals(pField)) {
            return theVersion;
        }
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
     * Interface for an infoSet item.
     */
    public interface InfoSetItem {
        /**
         * Obtain infoSet.
         * @return the infoSet
         */
        DataInfoSet<?, ?, ?, ?> getInfoSet();
    }

    /**
     * Version # of the values.
     */
    private int theVersion;

    /**
     * The Owner to which this set belongs.
     */
    private final O theOwner;

    /**
     * The InfoTypes for the InfoSet.
     */
    private final StaticList<I, E> theTypeList;

    /**
     * The DataInfoList for the InfoSet.
     */
    private final DataInfoList<T, O, I, E> theInfoList;

    /**
     * The Map of the DataInfo.
     */
    private final Map<E, T> theMap;

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
                          final StaticList<I, E> pTypeList,
                          final DataInfoList<T, O, I, E> pInfoList) {
        /* Store the Owner and InfoType List */
        theOwner = pOwner;
        theTypeList = pTypeList;
        theInfoList = pInfoList;

        /* Create the Map */
        theMap = new EnumMap<E, T>(theTypeList.getEnumClass());
    }

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneTheDataInfoSet(final DataInfoSet<T, O, I, E> pSource) {
        /* Clone the InfoSet for each Event in the underlying Map */
        for (Entry<E, T> myEntry : pSource.theMap.entrySet()) {
            /* Create the new value */
            T myNew = theInfoList.addCopyItem(myEntry.getValue());

            /* Add to the map */
            theMap.put(myEntry.getKey(), myNew);
        }
    }

    /**
     * Obtain the value for the infoClass.
     * @param <X> the infoClass
     * @param pInfoClass the Info Class
     * @param pClass the Value Class
     * @return the value
     */
    public <X> X getValue(final E pInfoClass,
                          final Class<X> pClass) {
        /* Access existing entry */
        T myValue = theMap.get(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the value */
        return myValue.getValue(pClass);
    }

    /**
     * Obtain the field value for the infoClass.
     * @param pInfoClass the Info Class
     * @return the value
     */
    public EncryptedField<?> getField(final E pInfoClass) {
        /* Access existing entry */
        T myValue = theMap.get(pInfoClass);

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
    protected T getInfo(final E pInfoClass) {
        /* Return the info */
        return theMap.get(pInfoClass);
    }

    /**
     * Determine whether a particular field has changed in this edit view.
     * @param pInfoClass the class to test
     * @return <code>true/false</code>
     */
    public Difference fieldChanged(final E pInfoClass) {
        /* Access the info */
        T myInfo = getInfo(pInfoClass);

        /* Return change details */
        return (myInfo != null)
               && myInfo.hasHistory()
                ? Difference.DIFFERENT
                : Difference.IDENTICAL;
    }

    /**
     * Set the value for the infoClass.
     * @param pInfoClass the Info Class
     * @param pValue the Value
     * @throws JOceanusException on error
     */
    public void setValue(final E pInfoClass,
                         final Object pValue) throws JOceanusException {
        /* Determine whether this is a deletion */
        boolean bDelete = pValue == null;

        /* Obtain the Map value */
        T myValue = theMap.get(pInfoClass);

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
     * Register Info.
     * @param pInfo the info to register
     */
    public void registerInfo(final T pInfo) {
        /* Obtain the existing Map value */
        E myClass = pInfo.getInfoClass();
        T myValue = theMap.get(myClass);

        /* Reject if duplicate */
        if (myValue != null) {
            throw new IllegalArgumentException("Duplicate information type "
                                               + pInfo.getInfoClass());
        }

        /* Add to the map */
        theMap.put(myClass, pInfo);
    }

    /**
     * deRegister Info.
     * @param pInfo the info to deRegister
     */
    public void deRegisterInfo(final T pInfo) {
        /* Obtain the existing Map value */
        E myClass = pInfo.getInfoClass();

        /* Remove from the map */
        theMap.remove(myClass);
    }

    /**
     * touch underlying items.
     */
    public void touchUnderlyingItems() {
        /* Loop through each existing value */
        for (T myValue : theMap.values()) {
            /* Touch the item */
            myValue.touchItem(theOwner);

            /* Touch the underlying items */
            myValue.touchUnderlyingItems();
        }
    }

    /**
     * Determine whether the set has changes.
     * @return <code>true/false</code>
     */
    public boolean hasHistory() {
        boolean bChanges = false;

        /* Loop through each existing value */
        for (T myValue : theMap.values()) {
            /* Check for history */
            bChanges |= myValue.hasHistory();
        }

        /* return result */
        return bChanges;
    }

    /**
     * Push history.
     */
    public void pushHistory() {
        /* Loop through each existing value */
        for (T myValue : theMap.values()) {
            /* Push history for the value */
            myValue.pushHistory();
        }
    }

    /**
     * Pop history.
     */
    public void popHistory() {
        /* Iterate through table values */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myValue = myIterator.next();

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

    /**
     * Check for history.
     * @return <code>true</code> if changes were made, <code>false</code> otherwise
     */
    public boolean checkForHistory() {
        /* Loop through each existing value */
        boolean bChanges = false;
        for (T myValue : theMap.values()) {
            /* If this is a newly created item */
            if (!myValue.hasHistory()) {
                bChanges = true;

                /* else existing entry */
            } else {
                /* Check for history */
                bChanges |= myValue.checkForHistory();
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
        for (T myValue : theMap.values()) {
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
        for (T myValue : theMap.values()) {
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

    /**
     * Get the EditState for this item.
     * @return the EditState
     */
    public EditState getEditState() {
        /* Loop through each existing value */
        for (T myValue : theMap.values()) {
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
        for (T myValue : theMap.values()) {
            /* If we have changes */
            if (myValue.getState() != DataState.CLEAN) {
                /* Note that new state is changed */
                myState = DataState.CHANGED;
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
        return theMap.values().iterator();
    }
}

/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jDataModels.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataModels.data.ControlData.ControlDataList;
import net.sourceforge.jOceanus.jDataModels.data.ControlKey.ControlKeyList;
import net.sourceforge.jOceanus.jDataModels.data.DataKey.DataKeyList;
import net.sourceforge.jOceanus.jDataModels.data.DataList.ListStyle;
import net.sourceforge.jOceanus.jDataModels.data.EncryptedItem.EncryptedList;
import net.sourceforge.jOceanus.jDataModels.preferences.DataListPreferences;
import net.sourceforge.jOceanus.jGordianKnot.PasswordHash;
import net.sourceforge.jOceanus.jGordianKnot.SecureManager;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceManager;

/**
 * DataSet definition and list. A DataSet is a set of DataLists backed by the three security lists.
 * @author Tony Washer
 * @param <T> the data type
 */
public abstract class DataSet<T extends DataSet<T>>
        implements JDataContents {
    /**
     * The Hash prime.
     */
    protected static final int HASH_PRIME = 19;

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(DataSet.class.getSimpleName());

    /**
     * Generation Field Id.
     */
    public static final JDataField FIELD_GENERATION = FIELD_DEFS.declareLocalField("Generation");

    /**
     * Granularity Field Id.
     */
    public static final JDataField FIELD_GRANULARITY = FIELD_DEFS.declareLocalField("Granularity");

    /**
     * Version Field Id.
     */
    public static final JDataField FIELD_VERSION = FIELD_DEFS.declareLocalField("Version");

    /**
     * Security Field Id.
     */
    public static final JDataField FIELD_SECURITY = FIELD_DEFS.declareLocalField("Security");

    /**
     * ControlKeys Field Id.
     */
    public static final JDataField FIELD_CONTROLKEYS = FIELD_DEFS.declareLocalField("ControlKeys");

    /**
     * DataKeys Field Id.
     */
    public static final JDataField FIELD_DATAKEYS = FIELD_DEFS.declareLocalField("DataKeys");

    /**
     * ControlData Field Id.
     */
    public static final JDataField FIELD_CONTROLDATA = FIELD_DEFS.declareLocalField("ControlData");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_GENERATION.equals(pField)) {
            return theGeneration;
        }
        if (FIELD_GRANULARITY.equals(pField)) {
            return theGranularity;
        }
        if (FIELD_VERSION.equals(pField)) {
            return theVersion;
        }
        if (FIELD_SECURITY.equals(pField)) {
            return theSecurity;
        }
        if (FIELD_CONTROLKEYS.equals(pField)) {
            return (theControlKeys.size() > 0)
                    ? theControlKeys
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_DATAKEYS.equals(pField)) {
            return (theDataKeys.size() > 0)
                    ? theDataKeys
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_CONTROLDATA.equals(pField)) {
            return (theControlData.size() > 0)
                    ? theControlData
                    : JDataFieldValue.SkipField;
        }
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return DataSet.class.getSimpleName();
    }

    /**
     * Security Manager.
     */
    private final SecureManager theSecurity;

    /**
     * ControlKeys.
     */
    private ControlKeyList theControlKeys = null;

    /**
     * DataKeys.
     */
    private DataKeyList theDataKeys = null;

    /**
     * ControlData.
     */
    private ControlDataList theControlData = null;

    /**
     * Number of encrypted lists.
     */
    private int theNumEncrypted = 0;

    /**
     * Generation of dataSet.
     */
    private int theGeneration = 0;

    /**
     * Granularity of dataSet.
     */
    private final int theGranularity;

    /**
     * Version of dataSet.
     */
    private int theVersion = 0;

    /**
     * The DataList Array.
     */
    private final List<DataList<?>> theList;

    /**
     * General formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    public JDataFormatter getDataFormatter() {
        return theFormatter;
    }

    /**
     * Get Security Manager.
     * @return the security manager
     */
    public SecureManager getSecurity() {
        return theSecurity;
    }

    /**
     * Get ControlKeys.
     * @return the controlKeys
     */
    public ControlKeyList getControlKeys() {
        return theControlKeys;
    }

    /**
     * Get DataKeys.
     * @return the dataKeys
     */
    public DataKeyList getDataKeys() {
        return theDataKeys;
    }

    /**
     * Get ControlData.
     * @return the controlData
     */
    public ControlDataList getControlData() {
        return theControlData;
    }

    /**
     * Get Generation.
     * @return the generation
     */
    public int getGeneration() {
        return theGeneration;
    }

    /**
     * Get Granularity.
     * @return the granularity
     */
    public int getGranularity() {
        return theGranularity;
    }

    /**
     * Get Version.
     * @return the version
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Constructor for new empty DataSet.
     * @param pSecurity the secure manager
     * @param pPreferenceMgr the preference manager
     * @param pFormatter the data formatter
     */
    protected DataSet(final SecureManager pSecurity,
                      final PreferenceManager pPreferenceMgr,
                      final JDataFormatter pFormatter) {
        /* Store the security manager and class */
        theSecurity = pSecurity;

        /* Access the DataListPreferences */
        DataListPreferences myPreferences = pPreferenceMgr.getPreferenceSet(DataListPreferences.class);
        theGranularity = myPreferences.getIntegerValue(DataListPreferences.NAME_GRANULARITY);

        /* Create the empty security lists */
        theControlKeys = new ControlKeyList(this);
        theDataKeys = new DataKeyList(this);
        theControlData = new ControlDataList(this);

        /* Create the list of additional DataLists */
        theList = new ArrayList<DataList<?>>();

        /* record formatter */
        theFormatter = pFormatter;
    }

    /**
     * Constructor for a cloned DataSet.
     * @param pSource the source DataSet
     */
    protected DataSet(final DataSet<T> pSource) {
        /* Access the Granularity */
        theGranularity = pSource.getGranularity();

        /* Store the security manager and class */
        theSecurity = pSource.getSecurity();

        /* Create the map of additional lists */
        theList = new ArrayList<DataList<?>>();

        /* Copy formatter */
        theFormatter = pSource.getDataFormatter();
    }

    /**
     * Construct a Clone for a DataSet.
     * @return the extract
     * @throws JDataException on error
     */
    public abstract T deriveCloneSet() throws JDataException;

    /**
     * Construct a Clone for a DataSet.
     * @param pSource the source DataSet
     * @throws JDataException on error
     */
    protected void deriveCloneSet(final DataSet<T> pSource) throws JDataException {
        /* Clone the Security items */
        theControlKeys = pSource.getControlKeys().cloneList(this);
        theDataKeys = pSource.getDataKeys().cloneList(this);
        theControlData = pSource.getControlData().cloneList(this);
    }

    /**
     * Construct an update extract for a FinanceData Set.
     * @return the extract
     * @throws JDataException on error
     */
    public abstract T deriveUpdateSet() throws JDataException;

    /**
     * Construct an update extract for a DataSet.
     * @param pSource the source of the extract
     * @throws JDataException on error
     */
    protected void deriveUpdateSet(final T pSource) throws JDataException {
        /* Build the static differences */
        theControlKeys = pSource.getControlKeys().deriveList(ListStyle.UPDATE);
        theDataKeys = pSource.getDataKeys().deriveList(ListStyle.UPDATE);
        theControlData = pSource.getControlData().deriveList(ListStyle.UPDATE);
    }

    /**
     * Construct a difference extract between two DataSets. The difference extract will only contain items that differ between the two DataSets. Items that are
     * in the new list, but not in the old list will be viewed as inserted. Items that are in the old list but not in the new list will be viewed as deleted.
     * Items that are in both list but differ will be viewed as changed
     * @param pOld The old list to extract from
     * @return the difference set
     * @throws JDataException on error
     */
    public abstract T getDifferenceSet(final T pOld) throws JDataException;

    /**
     * Construct a difference extract between two DataSets. The difference extract will only contain items that differ between the two DataSets. Items that are
     * in the new list, but not in the old list will be viewed as inserted. Items that are in the old list but not in the new list will be viewed as deleted.
     * Items that are in both list but differ will be viewed as changed
     * @param pNew The new list to compare
     * @param pOld The old list to compare
     * @throws JDataException on error
     */
    protected void deriveDifferences(final T pNew,
                                     final T pOld) throws JDataException {
        /* Build the security differences */
        theControlKeys = pNew.getControlKeys().deriveDifferences(pOld.getControlKeys());
        theDataKeys = pNew.getDataKeys().deriveDifferences(pOld.getDataKeys());
        theControlData = pNew.getControlData().deriveDifferences(pOld.getControlData());
    }

    /**
     * ReBase this data set against an earlier version.
     * @param pOld The old data to reBase against
     * @throws JDataException on error
     */
    public void reBase(final T pOld) throws JDataException {
        /* ReBase the security items */
        theControlKeys.reBase(pOld.getControlKeys());
        theDataKeys.reBase(pOld.getDataKeys());
        theControlData.reBase(pOld.getControlData());
    }

    /**
     * Add DataList to list of lists.
     * @param pList the list to add
     */
    protected void addList(final DataList<?> pList) {
        /* Add the DataList to the list */
        theList.add(pList);

        /* Note if the list is an encrypted list */
        if (pList instanceof EncryptedList) {
            theNumEncrypted++;
        }
    }

    /**
     * Obtain DataList for an item type.
     * @param <L> the List type
     * @param <D> the item type
     * @param pItemType the type of items
     * @return the list of items
     */
    public <L extends DataList<D>, D extends DataItem & Comparable<? super D>> L getDataList(final Class<L> pItemType) {
        /* Access the class */
        DataList<?> myList = getDataListForClass(pItemType);

        /* Cast correctly */
        return (myList == null)
                ? null
                : pItemType.cast(myList);
    }

    /**
     * Obtain DataList for an item type.
     * @param pItemType the type of items
     * @return the list of items
     */
    protected DataList<?> getDataListForClass(final Class<?> pItemType) {
        /* Create the iterator */
        ListIterator<DataList<?>> myIterator = theList.listIterator();

        /* Loop through the list */
        while (myIterator.hasNext()) {
            /* Return list if it is requested one */
            DataList<?> myList = myIterator.next();
            if (pItemType == myList.getClass()) {
                return myList;
            }
        }

        /* Return not found */
        return null;
    }

    /**
     * Set Generation.
     * @param pGeneration the generation
     */
    public void setGeneration(final int pGeneration) {
        /* Record the generation */
        theGeneration = pGeneration;

        /* Set the security lists */
        theControlKeys.setGeneration(pGeneration);
        theDataKeys.setGeneration(pGeneration);
        theControlData.setGeneration(pGeneration);

        /* Loop through the List values */
        for (DataList<?> myList : theList) {
            /* Set the Generation */
            myList.setGeneration(pGeneration);
        }
    }

    /**
     * Set Version.
     * @param pVersion the version
     */
    public void setVersion(final int pVersion) {
        /* Record the version */
        theVersion = pVersion;

        /* Set the security lists */
        theControlKeys.setVersion(pVersion);
        theDataKeys.setVersion(pVersion);
        theControlData.setVersion(pVersion);

        /* Loop through the List values */
        for (DataList<?> myList : theList) {
            /* Set the Version */
            myList.setVersion(pVersion);
        }
    }

    /**
     * Rewind items to the require version.
     * @param pVersion the version to rewind to
     */
    public void rewindToVersion(final int pVersion) {
        /* Record the version */
        theVersion = pVersion;

        /* rewind the security lists */
        theControlKeys.rewindToVersion(pVersion);
        theDataKeys.rewindToVersion(pVersion);
        theControlData.rewindToVersion(pVersion);

        /* Loop through the List values */
        for (DataList<?> myList : theList) {
            /* Rewind the list */
            myList.rewindToVersion(pVersion);
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a DataSet */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the object as a DataSet */
        DataSet<?> myThat = (DataSet<?>) pThat;

        /* Compare security data */
        if (!theControlKeys.equals(myThat.getControlKeys())) {
            return false;
        }
        if (!theDataKeys.equals(myThat.getDataKeys())) {
            return false;
        }
        if (!theControlData.equals(myThat.getControlData())) {
            return false;
        }

        /* Loop through the List values */
        for (DataList<?> myThisList : theList) {
            /* Access equivalent list */
            DataList<?> myThatList = myThat.getDataListForClass(myThisList.getClass());

            /* Handle trivial cases */
            if (myThisList == myThatList) {
                continue;
            }

            /* Compare list */
            if (!myThisList.equals(myThatList)) {
                return false;
            }
        }

        /* We are identical */
        return true;
    }

    @Override
    public int hashCode() {
        /* Build initial hashCode */
        int myHashCode = theControlKeys.hashCode();
        myHashCode *= HASH_PRIME;
        myHashCode += theDataKeys.hashCode();
        myHashCode *= HASH_PRIME;
        myHashCode += theControlData.hashCode();

        /* Loop through the List values */
        for (DataList<?> myList : theList) {
            /* Access equivalent list */
            myHashCode *= HASH_PRIME;
            myHashCode += myList.hashCode();
        }

        /* Return the hashCode */
        return myHashCode;
    }

    /**
     * Determine whether a DataSet has entries.
     * @return <code>true</code> if the DataSet has entries
     */
    public boolean isEmpty() {
        /* Determine whether the security data is empty */
        if (!theControlKeys.isEmpty()
            || !theDataKeys.isEmpty()
            || !theControlData.isEmpty()) {
            return false;
        }

        /* Loop through the List values */
        for (DataList<?> myList : theList) {
            /* Determine whether the list is empty */
            if (!myList.isEmpty()) {
                return false;
            }
        }

        /* Return the indication */
        return true;
    }

    /**
     * Determine whether the Data-set has updates.
     * @return <code>true</code> if the Data-set has updates, <code>false</code> if not
     */
    public boolean hasUpdates() {
        /* Determine whether we have updates */
        if ((theControlKeys.hasUpdates())
            || (theDataKeys.hasUpdates())
            || (theControlData.hasUpdates())) {
            return true;
        }

        /* Loop through the List values */
        for (DataList<?> myList : theList) {
            /* Determine whether the list has updates */
            if (myList.hasUpdates()) {
                return true;
            }
        }

        /* We have no updates */
        return false;
    }

    /**
     * Get the control record.
     * @return the control record
     */
    public ControlData getControl() {
        /* Set the control */
        return getControlData().getControl();
    }

    /**
     * Get the active control key.
     * @return the control key
     */
    public ControlKey getControlKey() {
        /* Access the control element from the database */
        ControlData myControl = getControl();
        ControlKey myKey = null;

        /* Access control key from control data */
        if (myControl != null) {
            myKey = myControl.getControlKey();
        }

        /* Return the key */
        return myKey;
    }

    /**
     * Initialise Security from database (if present).
     * @param pTask the task control
     * @param pBase the database data
     * @return Continue <code>true/false</code>
     * @throws JDataException on error
     */
    public boolean initialiseSecurity(final TaskControl<T> pTask,
                                      final T pBase) throws JDataException {
        /* Set the number of stages */
        if (!pTask.setNumStages(1 + theNumEncrypted)) {
            return false;
        }

        /* Initialise Security */
        theControlKeys.initialiseSecurity(pBase);

        /* Access the control key */
        ControlKey myControl = getControlKey();

        /* Loop through the List values */
        for (DataList<?> myList : theList) {
            /* Access equivalent base list */
            DataList<?> myBase = pBase.getDataListForClass(myList.getClass());

            /* If the list is an encrypted list */
            if (myList instanceof EncryptedList) {
                /* Adopt the security */
                EncryptedList<?> myEncrypted = (EncryptedList<?>) myList;
                if (!myEncrypted.adoptSecurity(pTask, myControl, (EncryptedList<?>) myBase)) {
                    return false;
                }
            }
        }

        /* Return success */
        return true;
    }

    /**
     * Renew Security.
     * @param pTask the task control
     * @return Continue <code>true/false</code>
     * @throws JDataException on error
     */
    public boolean renewSecurity(final TaskControl<T> pTask) throws JDataException {
        /* Access ControlData */
        ControlData myControl = getControl();

        /* Clone the control key */
        ControlKey myKey = theControlKeys.cloneItem(myControl.getControlKey());

        /* Declare the New Control Key */
        myControl.setControlKey(myKey);

        /* Update Security */
        return updateSecurity(pTask);
    }

    /**
     * Check Security for multiple controlKeys.
     * @param pTask the task control
     * @return Continue <code>true/false</code>
     * @throws JDataException on error
     */
    public boolean checkSecurity(final TaskControl<T> pTask) throws JDataException {
        /* If there is more than one controlKey */
        if (theControlKeys.size() > 1) {
            /* Update to the selected controlKey */
            return updateSecurity(pTask);
        }

        /* Return success */
        return true;
    }

    /**
     * Update Security.
     * @param pTask the task control
     * @return Continue <code>true/false</code>
     * @throws JDataException on error
     */
    public boolean updateSecurity(final TaskControl<T> pTask) throws JDataException {
        /* Access the control key */
        ControlKey myControl = getControlKey();

        /* Set the number of stages */
        if (!pTask.setNumStages(1 + theNumEncrypted)) {
            return false;
        }

        /* Loop through the List values */
        for (DataList<?> myList : theList) {
            /* If the list is an encrypted list */
            if (myList instanceof EncryptedList) {
                /* Update the security */
                EncryptedList<?> myEncrypted = (EncryptedList<?>) myList;
                if (!myEncrypted.updateSecurity(pTask, myControl)) {
                    return false;
                }
            }
        }

        /* Delete old ControlSets */
        theControlKeys.purgeOldControlKeys();

        /* Return success */
        return true;
    }

    /**
     * Get the Password Hash.
     * @return the password hash
     * @throws JDataException on error
     */
    public PasswordHash getPasswordHash() throws JDataException {
        /* Access the active control key */
        ControlKey myKey = getControlKey();

        /* Set the control */
        return (myKey == null)
                ? null
                : myKey.getPasswordHash();
    }

    /**
     * Update data with a new password.
     * @param pTask the task control
     * @param pSource the source of the data
     * @throws JDataException on error
     */
    public void updatePasswordHash(final TaskControl<T> pTask,
                                   final String pSource) throws JDataException {
        /* Obtain a new password hash */
        PasswordHash myHash = theSecurity.resolvePasswordHash(null, pSource);

        /* Update the control details */
        getControlKey().updatePasswordHash(myHash);
    }

    /**
     * Obtain list iterator.
     * @return the iterator
     */
    public Iterator<DataList<?>> iterator() {
        return theList.iterator();
    }
}

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

import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jprometheus.data.ControlData.ControlDataList;
import net.sourceforge.joceanus.jprometheus.data.ControlKey.ControlKeyList;
import net.sourceforge.joceanus.jprometheus.data.DataKey.DataKeyList;
import net.sourceforge.joceanus.jprometheus.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem.EncryptedList;
import net.sourceforge.joceanus.jprometheus.preferences.DataListPreferences;
import net.sourceforge.joceanus.jgordianknot.crypto.PasswordHash;
import net.sourceforge.joceanus.jgordianknot.crypto.SecureManager;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * DataSet definition and list. A DataSet is a set of DataLists backed by the three security lists.
 * @param <T> the data type
 * @param <E> the list enum type
 */
public abstract class DataSet<T extends DataSet<T, E>, E extends Enum<E>>
        implements JDataContents {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(DataSet.class.getName());

    /**
     * The Hash prime.
     */
    protected static final int HASH_PRIME = 19;

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * Generation Field Id.
     */
    public static final JDataField FIELD_GENERATION = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataGeneration"));

    /**
     * Granularity Field Id.
     */
    public static final JDataField FIELD_GRANULARITY = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataGranularity"));

    /**
     * Version Field Id.
     */
    public static final JDataField FIELD_VERSION = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataVersion"));

    /**
     * Security Field Id.
     */
    public static final JDataField FIELD_SECURITY = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataSecurity"));

    /**
     * ControlKeys Field Id.
     */
    public static final JDataField FIELD_CONTROLKEYS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataControlKeys"));

    /**
     * DataKeys Field Id.
     */
    public static final JDataField FIELD_DATAKEYS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataDataKeys"));

    /**
     * ControlData Field Id.
     */
    public static final JDataField FIELD_CONTROLDATA = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataControlData"));

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
            return (theControlKeys.isEmpty())
                    ? JDataFieldValue.SKIP
                    : theControlKeys;
        }
        if (FIELD_DATAKEYS.equals(pField)) {
            return (theDataKeys.isEmpty())
                    ? JDataFieldValue.SKIP
                    : theDataKeys;
        }
        if (FIELD_CONTROLDATA.equals(pField)) {
            return (theControlData.isEmpty())
                    ? JDataFieldValue.SKIP
                    : theControlData;
        }
        return JDataFieldValue.UNKNOWN;
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
     * Enum class.
     */
    private final Class<E> theEnumClass;

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
     * The DataList Map.
     */
    private final Map<E, DataList<?>> theListMap;

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
     * Get List Map.
     * @return the list map
     */
    protected Map<E, DataList<?>> getListMap() {
        return theListMap;
    }

    /**
     * Get Enum Class.
     * @return the enum class
     */
    protected Class<E> getEnumClass() {
        return theEnumClass;
    }

    /**
     * Constructor for new empty DataSet.
     * @param pEnumClass the EnumClass
     * @param pSecurity the secure manager
     * @param pPreferenceMgr the preference manager
     * @param pFormatter the data formatter
     */
    protected DataSet(final Class<E> pEnumClass,
                      final SecureManager pSecurity,
                      final PreferenceManager pPreferenceMgr,
                      final JDataFormatter pFormatter) {
        /* Store the security manager and Enum class */
        theSecurity = pSecurity;
        theEnumClass = pEnumClass;

        /* Access the DataListPreferences */
        DataListPreferences myPreferences = pPreferenceMgr.getPreferenceSet(DataListPreferences.class);
        theGranularity = myPreferences.getIntegerValue(DataListPreferences.NAME_GRANULARITY);

        /* Create the empty security lists */
        theControlKeys = new ControlKeyList(this);
        theDataKeys = new DataKeyList(this);
        theControlData = new ControlDataList(this);

        /* Create the map of additional DataLists */
        theListMap = new EnumMap<E, DataList<?>>(pEnumClass);

        /* record formatter */
        theFormatter = pFormatter;
    }

    /**
     * Constructor for a cloned DataSet.
     * @param pSource the source DataSet
     */
    protected DataSet(final DataSet<T, E> pSource) {
        /* Access the Enum class */
        theEnumClass = pSource.getEnumClass();

        /* Access the Granularity */
        theGranularity = pSource.getGranularity();

        /* Store the security manager and class */
        theSecurity = pSource.getSecurity();

        /* Create the map of additional DataLists */
        theListMap = new EnumMap<E, DataList<?>>(theEnumClass);

        /* Copy formatter */
        theFormatter = pSource.getDataFormatter();
    }

    /**
     * Construct a Clone for a DataSet.
     * @return the extract
     * @throws JOceanusException on error
     */
    public abstract T deriveCloneSet() throws JOceanusException;

    /**
     * Construct a Clone for a DataSet.
     * @param pSource the source DataSet
     * @throws JOceanusException on error
     */
    protected void deriveCloneSet(final DataSet<T, E> pSource) throws JOceanusException {
        /* Clone the Security items */
        theControlKeys = pSource.getControlKeys().cloneList(this);
        theDataKeys = pSource.getDataKeys().cloneList(this);
        theControlData = pSource.getControlData().cloneList(this);

        /* Loop through the source lists */
        Iterator<Entry<E, DataList<?>>> myIterator = pSource.entryIterator();
        while (myIterator.hasNext()) {
            Entry<E, DataList<?>> myEntry = myIterator.next();

            /* Access components */
            E myType = myEntry.getKey();
            DataList<?> myList = myEntry.getValue();

            /* Create the cloned list */
            addList(myType, myList.cloneList(this));
        }
    }

    /**
     * Construct an update extract for a FinanceData Set.
     * @return the extract
     * @throws JOceanusException on error
     */
    public abstract T deriveUpdateSet() throws JOceanusException;

    /**
     * Construct an update extract for a DataSet.
     * @param pSource the source of the extract
     * @throws JOceanusException on error
     */
    protected void deriveUpdateSet(final T pSource) throws JOceanusException {
        /* Build the security extract */
        theControlKeys = pSource.getControlKeys().deriveList(ListStyle.UPDATE);
        theDataKeys = pSource.getDataKeys().deriveList(ListStyle.UPDATE);
        theControlData = pSource.getControlData().deriveList(ListStyle.UPDATE);

        /* Loop through the source lists */
        Iterator<Entry<E, DataList<?>>> myIterator = pSource.entryIterator();
        while (myIterator.hasNext()) {
            Entry<E, DataList<?>> myEntry = myIterator.next();

            /* Access components */
            E myType = myEntry.getKey();
            DataList<?> myList = myEntry.getValue();

            /* Create the update list */
            addList(myType, myList.deriveList(ListStyle.UPDATE));
        }
    }

    /**
     * Construct a difference extract between two DataSets. The difference extract will only contain items that differ between the two DataSets. Items that are
     * in the new list, but not in the old list will be viewed as inserted. Items that are in the old list but not in the new list will be viewed as deleted.
     * Items that are in both list but differ will be viewed as changed
     * @param pOld The old list to extract from
     * @return the difference set
     * @throws JOceanusException on error
     */
    public abstract T getDifferenceSet(final T pOld) throws JOceanusException;

    /**
     * Construct a difference extract between two DataSets. The difference extract will only contain items that differ between the two DataSets. Items that are
     * in the new list, but not in the old list will be viewed as inserted. Items that are in the old list but not in the new list will be viewed as deleted.
     * Items that are in both list but differ will be viewed as changed
     * @param pNew The new list to compare
     * @param pOld The old list to compare
     * @throws JOceanusException on error
     */
    protected void deriveDifferences(final T pNew,
                                     final T pOld) throws JOceanusException {
        /* Build the security differences */
        theControlKeys = pNew.getControlKeys().deriveDifferences(this, pOld.getControlKeys());
        theDataKeys = pNew.getDataKeys().deriveDifferences(this, pOld.getDataKeys());
        theControlData = pNew.getControlData().deriveDifferences(this, pOld.getControlData());

        /* Obtain listMaps */
        Map<E, DataList<?>> myOldMap = pOld.getListMap();

        /* Loop through the new lists */
        Iterator<Entry<E, DataList<?>>> myIterator = pNew.entryIterator();
        while (myIterator.hasNext()) {
            Entry<E, DataList<?>> myEntry = myIterator.next();

            /* Access components */
            E myType = myEntry.getKey();
            DataList<?> myNew = myEntry.getValue();
            DataList<?> myOld = myOldMap.get(myType);

            /* Derive Differences */
            addList(myType, myNew.deriveDifferences(this, myOld));
        }
    }

    /**
     * ReBase this data set against an earlier version.
     * @param pOld The old data to reBase against
     * @throws JOceanusException on error
     */
    public void reBase(final T pOld) throws JOceanusException {
        /* ReBase the security items */
        theControlKeys.reBase(pOld.getControlKeys());
        theDataKeys.reBase(pOld.getDataKeys());
        theControlData.reBase(pOld.getControlData());

        /* Obtain old listMap */
        Map<E, DataList<?>> myMap = pOld.getListMap();

        /* Loop through the lists */
        Iterator<Entry<E, DataList<?>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            Entry<E, DataList<?>> myEntry = myIterator.next();

            /* Access components */
            E myType = myEntry.getKey();
            DataList<?> myList = myEntry.getValue();

            /* ReBase on Old dataList */
            myList.reBase(myMap.get(myType));
        }
    }

    /**
     * Add DataList to list of lists.
     * @param pListType the list type
     * @param pList the list to add
     */
    protected void addList(final E pListType,
                           final DataList<?> pList) {
        /* Add the DataList to the map */
        theListMap.put(pListType, pList);

        /* Note if the list is an encrypted list */
        if (pList instanceof EncryptedList) {
            theNumEncrypted++;
        }
    }

    /**
     * Obtain DataList for an item type.
     * @param <L> the List type
     * @param pListType the list type
     * @param pListClass the class of the list
     * @return the list of items
     */
    protected <L extends DataList<?>> L getDataList(final E pListType,
                                                    final Class<L> pListClass) {
        /* Access the class */
        DataList<?> myList = theListMap.get(pListType);

        /* Cast correctly */
        return (myList == null)
                ? null
                : pListClass.cast(myList);
    }

    /**
     * Obtain DataList for an list class.
     * @param <L> the List type
     * @param pListClass the class of the list
     * @return the list of items
     */
    public <L extends DataList<?>> L getDataList(final Class<L> pListClass) {
        /* Loop through the lists */
        Iterator<Entry<E, DataList<?>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            Entry<E, DataList<?>> myEntry = myIterator.next();

            /* Access components */
            DataList<?> myList = myEntry.getValue();
            if (pListClass.equals(myList.getClass())) {
                return pListClass.cast(myList);
            }
        }

        /* Not found */
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
        Iterator<DataList<?>> myIterator = iterator();
        while (myIterator.hasNext()) {
            DataList<?> myList = myIterator.next();

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
        Iterator<DataList<?>> myIterator = iterator();
        while (myIterator.hasNext()) {
            DataList<?> myList = myIterator.next();

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
        Iterator<DataList<?>> myIterator = iterator();
        while (myIterator.hasNext()) {
            DataList<?> myList = myIterator.next();

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
        DataSet<?, ?> myThat = (DataSet<?, ?>) pThat;

        /* Check enum class */
        if (!theEnumClass.equals(myThat.getEnumClass())) {
            return false;
        }

        /* Check version and generation */
        if (myThat.getVersion() != theVersion) {
            return false;
        }
        if (myThat.getGeneration() != theGeneration) {
            return false;
        }

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

        /* Compare the maps */
        if (!theListMap.equals(myThat.getListMap())) {
            return false;
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
        Iterator<DataList<?>> myIterator = iterator();
        while (myIterator.hasNext()) {
            DataList<?> myList = myIterator.next();

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
        Iterator<DataList<?>> myIterator = iterator();
        while (myIterator.hasNext()) {
            DataList<?> myList = myIterator.next();

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
        Iterator<DataList<?>> myIterator = iterator();
        while (myIterator.hasNext()) {
            DataList<?> myList = myIterator.next();

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
     * @throws JOceanusException on error
     */
    public boolean initialiseSecurity(final TaskControl<T> pTask,
                                      final T pBase) throws JOceanusException {
        /* Set the number of stages */
        if (!pTask.setNumStages(1 + theNumEncrypted)) {
            return false;
        }

        /* Initialise Security */
        theControlKeys.initialiseSecurity(pBase);

        /* Access the control key */
        ControlKey myControl = getControlKey();

        /* Obtain base listMap */
        Map<E, DataList<?>> myMap = pBase.getListMap();

        /* Loop through the List values */
        Iterator<Entry<E, DataList<?>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            Entry<E, DataList<?>> myEntry = myIterator.next();

            /* Access the two lists */
            DataList<?> myList = myEntry.getValue();
            DataList<?> myBase = myMap.get(myEntry.getKey());

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
     * @throws JOceanusException on error
     */
    public boolean renewSecurity(final TaskControl<T> pTask) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    public boolean checkSecurity(final TaskControl<T> pTask) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    public boolean updateSecurity(final TaskControl<T> pTask) throws JOceanusException {
        /* Access the control key */
        ControlKey myControl = getControlKey();

        /* Set the number of stages */
        if (!pTask.setNumStages(1 + theNumEncrypted)) {
            return false;
        }

        /* Loop through the List values */
        Iterator<DataList<?>> myIterator = iterator();
        while (myIterator.hasNext()) {
            DataList<?> myList = myIterator.next();

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
     * @throws JOceanusException on error
     */
    public PasswordHash getPasswordHash() throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    public void updatePasswordHash(final TaskControl<T> pTask,
                                   final String pSource) throws JOceanusException {
        /* Obtain a new password hash */
        PasswordHash myHash = theSecurity.resolvePasswordHash(null, pSource);

        /* Update the control details */
        getControlKey().updatePasswordHash(myHash);
    }

    /**
     * Undo changes in a dataSet.
     */
    public void undoLastChange() {
        /* Ignore if we have no changes */
        if (theVersion == 0) {
            return;
        }

        /* Decrement version */
        theVersion--;

        /* Rewind to the version */
        rewindToVersion(theVersion);
    }

    /**
     * Reset changes in a dataSet.
     */
    public void resetChanges() {
        /* Ignore if we have no changes */
        if (theVersion == 0) {
            return;
        }

        /* Decrement version */
        theVersion = 0;

        /* Rewind to the version */
        rewindToVersion(theVersion);
    }

    /**
     * Obtain list iterator.
     * @return the iterator
     */
    public Iterator<DataList<?>> iterator() {
        return theListMap.values().iterator();
    }

    /**
     * Obtain list iterator.
     * @return the iterator
     */
    public Iterator<Entry<E, DataList<?>>> entryIterator() {
        return theListMap.entrySet().iterator();
    }
}

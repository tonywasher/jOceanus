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

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSecurity.MetisSecurityPreferenceKey;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSecurity.MetisSecurityPreferences;
import net.sourceforge.joceanus.jprometheus.JOceanusUtilitySet;
import net.sourceforge.joceanus.jprometheus.data.ControlData.ControlDataList;
import net.sourceforge.joceanus.jprometheus.data.ControlKey.ControlKeyList;
import net.sourceforge.joceanus.jprometheus.data.DataKey.DataKeyList;
import net.sourceforge.joceanus.jprometheus.data.DataKeySet.DataKeySetList;
import net.sourceforge.joceanus.jprometheus.data.DataList.DataListSet;
import net.sourceforge.joceanus.jprometheus.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem.EncryptedList;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusDataList.PrometheusDataListPreferenceKey;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusDataList.PrometheusDataListPreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * DataSet definition and list. A DataSet is a set of DataLists backed by the three security lists.
 * @param <T> the dataSet type
 * @param <E> the data type enum class
 */
public abstract class DataSet<T extends DataSet<T, E>, E extends Enum<E>>
        implements MetisDataContents, DataListSet<E> {
    /**
     * The Hash prime.
     */
    protected static final int HASH_PRIME = 19;

    /**
     * Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(PrometheusDataResource.DATASET_NAME.getValue());

    /**
     * Generation Field Id.
     */
    public static final MetisField FIELD_GENERATION = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATASET_GENERATION.getValue());

    /**
     * Granularity Field Id.
     */
    public static final MetisField FIELD_GRANULARITY = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATASET_GRANULARITY.getValue());

    /**
     * Version Field Id.
     */
    public static final MetisField FIELD_VERSION = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATASET_VERSION.getValue());

    /**
     * Security Field Id.
     */
    public static final MetisField FIELD_SECURITY = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATASET_SECURITY.getValue());

    /**
     * ControlKeys Field Id.
     */
    public static final MetisField FIELD_CONTROLKEYS = FIELD_DEFS.declareLocalField(PrometheusDataResource.CONTROLKEY_LIST.getValue());

    /**
     * DataKeySets Field Id.
     */
    public static final MetisField FIELD_DATAKEYSETS = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAKEYSET_LIST.getValue());

    /**
     * DataKeys Field Id.
     */
    public static final MetisField FIELD_DATAKEYS = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAKEY_LIST.getValue());

    /**
     * ControlData Field Id.
     */
    public static final MetisField FIELD_CONTROLDATA = FIELD_DEFS.declareLocalField(PrometheusDataResource.CONTROLDATA_LIST.getValue());

    /**
     * Security Manager.
     */
    private final GordianHashManager theSecurity;

    /**
     * Enum class.
     */
    private final Class<E> theEnumClass;

    /**
     * ControlKeys.
     */
    private ControlKeyList theControlKeys = null;

    /**
     * DataKeySets.
     */
    private DataKeySetList theDataKeySets = null;

    /**
     * DataKeys.
     */
    private DataKeyList theDataKeys = null;

    /**
     * ControlData.
     */
    private ControlDataList theControlData = null;

    /**
     * Number of activeKeySets.
     */
    private final int theNumActiveKeySets;

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
    private final Map<E, DataList<?, E>> theListMap;

    /**
     * General formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * Constructor for new empty DataSet.
     * @param pEnumClass the EnumClass
     * @param pUtilitySet the utility set
     */
    protected DataSet(final Class<E> pEnumClass,
                      final JOceanusUtilitySet pUtilitySet) {
        /* Store the security manager and Enum class */
        theSecurity = pUtilitySet.getSecureManager();
        theEnumClass = pEnumClass;

        /* Access the DataListPreferences */
        MetisPreferenceManager myPrefMgr = pUtilitySet.getPreferenceManager();
        PrometheusDataListPreferences myDataPreferences = myPrefMgr.getPreferenceSet(PrometheusDataListPreferences.class);
        theGranularity = myDataPreferences.getIntegerValue(PrometheusDataListPreferenceKey.GRANULARITY);
        MetisSecurityPreferences mySecPreferences = myPrefMgr.getPreferenceSet(MetisSecurityPreferences.class);
        theNumActiveKeySets = mySecPreferences.getIntegerValue(MetisSecurityPreferenceKey.ACTIVEKEYSETS);

        /* Create the empty security lists */
        theControlKeys = new ControlKeyList(this);
        theDataKeySets = new DataKeySetList(this);
        theDataKeys = new DataKeyList(this);
        theControlData = new ControlDataList(this);

        /* Create the map of additional DataLists */
        theListMap = new EnumMap<>(pEnumClass);

        /* record formatter */
        theFormatter = pUtilitySet.getDataFormatter();
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

        /* Access the #activeKeySets */
        theNumActiveKeySets = pSource.getNumActiveKeySets();

        /* Store the security manager and class */
        theSecurity = pSource.getSecurity();

        /* Create the map of additional DataLists */
        theListMap = new EnumMap<>(theEnumClass);

        /* Copy formatter */
        theFormatter = pSource.getDataFormatter();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
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
            return theControlKeys.isEmpty()
                                            ? MetisFieldValue.SKIP
                                            : theControlKeys;
        }
        if (FIELD_DATAKEYSETS.equals(pField)) {
            return theDataKeySets.isEmpty()
                                            ? MetisFieldValue.SKIP
                                            : theDataKeySets;
        }
        if (FIELD_DATAKEYS.equals(pField)) {
            return theDataKeys.isEmpty()
                                         ? MetisFieldValue.SKIP
                                         : theDataKeys;
        }
        if (FIELD_CONTROLDATA.equals(pField)) {
            return theControlData.isEmpty()
                                            ? MetisFieldValue.SKIP
                                            : theControlData;
        }
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return DataSet.class.getSimpleName();
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    public MetisDataFormatter getDataFormatter() {
        return theFormatter;
    }

    /**
     * Get Security Manager.
     * @return the security manager
     */
    public GordianHashManager getSecurity() {
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
     * Get DataKeySets.
     * @return the dataKeySets
     */
    public DataKeySetList getDataKeySets() {
        return theDataKeySets;
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
     * Get Number of activeKeySets.
     * @return the # active KeySets
     */
    public int getNumActiveKeySets() {
        return theNumActiveKeySets;
    }

    /**
     * Get List Map.
     * @return the list map
     */
    protected Map<E, DataList<?, E>> getListMap() {
        return theListMap;
    }

    /**
     * Get Enum Class.
     * @return the enum class
     */
    public Class<E> getEnumClass() {
        return theEnumClass;
    }

    /**
     * Construct a Clone for a DataSet.
     * @return the extract
     * @throws OceanusException on error
     */
    public abstract T deriveCloneSet() throws OceanusException;

    /**
     * Build an empty clone dataSet.
     * @param pSource the source DataSet
     * @throws OceanusException on error
     */
    protected void buildEmptyCloneSet(final DataSet<T, E> pSource) throws OceanusException {
        /* Clone the Security items */
        theControlKeys = pSource.getControlKeys().getEmptyList(ListStyle.CLONE);
        theDataKeySets = pSource.getDataKeySets().getEmptyList(ListStyle.CLONE);
        theDataKeys = pSource.getDataKeys().getEmptyList(ListStyle.CLONE);
        theControlData = pSource.getControlData().getEmptyList(ListStyle.CLONE);

        /* Loop through the source lists */
        Iterator<Entry<E, DataList<?, E>>> myIterator = pSource.entryIterator();
        while (myIterator.hasNext()) {
            Entry<E, DataList<?, E>> myEntry = myIterator.next();

            /* Access components */
            E myType = myEntry.getKey();
            DataList<?, E> myList = myEntry.getValue();

            /* Create the empty cloned list */
            addList(myType, myList.getEmptyList(ListStyle.CLONE));
        }
    }

    /**
     * Construct a Clone for a DataSet.
     * @param pSource the source DataSet
     * @throws OceanusException on error
     */
    protected void deriveCloneSet(final DataSet<T, E> pSource) throws OceanusException {
        /* Clone the Security items */
        theControlKeys.cloneList(this, pSource.getControlKeys());
        theDataKeySets.cloneList(this, pSource.getDataKeySets());
        theDataKeys.cloneList(this, pSource.getDataKeys());
        theControlData.cloneList(this, pSource.getControlData());

        /* Obtain listMaps */
        Map<E, DataList<?, E>> myOldMap = pSource.getListMap();

        /* Loop through the new lists */
        Iterator<Entry<E, DataList<?, E>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            Entry<E, DataList<?, E>> myEntry = myIterator.next();

            /* Access components */
            E myType = myEntry.getKey();
            DataList<?, E> myNew = myEntry.getValue();
            DataList<?, E> myOld = myOldMap.get(myType);

            /* Clone the list */
            myNew.cloneList(this, myOld);
        }
    }

    /**
     * Construct an update extract for a FinanceData Set.
     * @return the extract
     * @throws OceanusException on error
     */
    public abstract T deriveUpdateSet() throws OceanusException;

    /**
     * Construct an update extract for a DataSet.
     * @param pSource the source of the extract
     * @throws OceanusException on error
     */
    protected void deriveUpdateSet(final T pSource) throws OceanusException {
        /* Build the security extract */
        theControlKeys = pSource.getControlKeys().deriveList(ListStyle.UPDATE);
        theDataKeySets = pSource.getDataKeySets().deriveList(ListStyle.UPDATE);
        theDataKeys = pSource.getDataKeys().deriveList(ListStyle.UPDATE);
        theControlData = pSource.getControlData().deriveList(ListStyle.UPDATE);

        /* Loop through the source lists */
        Iterator<Entry<E, DataList<?, E>>> myIterator = pSource.entryIterator();
        while (myIterator.hasNext()) {
            Entry<E, DataList<?, E>> myEntry = myIterator.next();

            /* Access components */
            E myType = myEntry.getKey();
            DataList<?, E> myList = myEntry.getValue();

            /* Create the update list */
            addList(myType, myList.deriveList(ListStyle.UPDATE));
        }

        /* If we have updates */
        if (!isEmpty()) {
            /* Update the version */
            setVersion(1);
        }
    }

    /**
     * Construct a difference extract between two DataSets. The difference extract will only contain
     * items that differ between the two DataSets. Items that are in the new list, but not in the
     * old list will be viewed as inserted. Items that are in the old list but not in the new list
     * will be viewed as deleted. Items that are in both list but differ will be viewed as changed
     * @param pTask the task control
     * @param pOld The old list to extract from
     * @return the difference set
     * @throws OceanusException on error
     */
    public abstract T getDifferenceSet(final TaskControl<T> pTask,
                                       final T pOld) throws OceanusException;

    /**
     * Construct a difference extract between two DataSets. The difference extract will only contain
     * items that differ between the two DataSets. Items that are in the new list, but not in the
     * old list will be viewed as inserted. Items that are in the old list but not in the new list
     * will be viewed as deleted. Items that are in both list but differ will be viewed as changed
     * @param pTask the task control
     * @param pNew The new list to compare
     * @param pOld The old list to compare
     * @throws OceanusException on error
     */
    protected void deriveDifferences(final TaskControl<T> pTask,
                                     final T pNew,
                                     final T pOld) throws OceanusException {
        /* Access current profile */
        MetisProfile myTask = pTask.getActiveTask();
        MetisProfile myStage = myTask.startTask("checkDifferences");

        /* Build the security differences */
        theControlKeys = pNew.getControlKeys().deriveDifferences(this, pOld.getControlKeys());
        theDataKeySets = pNew.getDataKeySets().deriveDifferences(this, pOld.getDataKeySets());
        theDataKeys = pNew.getDataKeys().deriveDifferences(this, pOld.getDataKeys());
        theControlData = pNew.getControlData().deriveDifferences(this, pOld.getControlData());

        /* Obtain listMaps */
        Map<E, DataList<?, E>> myOldMap = pOld.getListMap();

        /* Loop through the new lists */
        Iterator<Entry<E, DataList<?, E>>> myIterator = pNew.entryIterator();
        while (myIterator.hasNext()) {
            Entry<E, DataList<?, E>> myEntry = myIterator.next();

            /* Access components */
            E myType = myEntry.getKey();
            DataList<?, E> myNew = myEntry.getValue();
            DataList<?, E> myOld = myOldMap.get(myType);

            /* Derive Differences */
            myStage.startTask(myNew.listName());
            addList(myType, myNew.deriveDifferences(this, myOld));
        }

        /* Complete task */
        myStage.end();
    }

    /**
     * ReBase this data set against an earlier version.
     * @param pTask the task control
     * @param pOld The old data to reBase against
     * @throws OceanusException on error
     */
    public void reBase(final TaskControl<T> pTask,
                       final T pOld) throws OceanusException {
        /* Access current profile */
        MetisProfile myTask = pTask.getActiveTask();
        MetisProfile myStage = myTask.startTask("ReBase");

        /* ReBase the security items */
        boolean bUpdates = theControlKeys.reBase(pOld.getControlKeys());
        bUpdates |= theDataKeySets.reBase(pOld.getDataKeySets());
        bUpdates |= theDataKeys.reBase(pOld.getDataKeys());
        bUpdates |= theControlData.reBase(pOld.getControlData());

        /* Obtain old listMap */
        Map<E, DataList<?, E>> myMap = pOld.getListMap();

        /* Loop through the lists */
        Iterator<Entry<E, DataList<?, E>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            Entry<E, DataList<?, E>> myEntry = myIterator.next();

            /* Access components */
            E myType = myEntry.getKey();
            DataList<?, E> myList = myEntry.getValue();

            /* ReBase on Old dataList */
            myStage.startTask(myList.listName());
            bUpdates |= myList.reBase(myMap.get(myType));
        }

        /* If we have updates */
        if (bUpdates) {
            /* Update the version */
            setVersion(getVersion() + 1);
        }

        /* Complete task */
        myStage.end();
    }

    /**
     * Add DataList to list of lists.
     * @param pListType the list type
     * @param pList the list to add
     */
    protected void addList(final E pListType,
                           final DataList<?, E> pList) {
        /* Add the DataList to the map */
        theListMap.put(pListType, pList);

        /* Note if the list is an encrypted list */
        if (pList instanceof EncryptedList) {
            theNumEncrypted++;
        }
    }

    @Override
    public <L extends DataList<?, E>> L getDataList(final E pListType,
                                                    final Class<L> pListClass) {
        /* Access the list */
        DataList<?, E> myList = theListMap.get(pListType);

        /* Cast correctly */
        return (myList == null)
                                ? null
                                : pListClass.cast(myList);
    }

    /**
     * Obtain debug value for list.
     * @param pListType the list type
     * @return true/false
     */
    protected Object getFieldListValue(final E pListType) {
        /* Access the class */
        DataList<?, E> myList = theListMap.get(pListType);

        /* Cast correctly */
        return ((myList == null) || (myList.isEmpty()))
                                                        ? MetisFieldValue.SKIP
                                                        : myList;
    }

    /**
     * Obtain DataList for an list class.
     * @param <L> the List type
     * @param pListClass the class of the list
     * @return the list of items
     */
    public <L extends DataList<?, E>> L getDataList(final Class<L> pListClass) {
        /* Loop through the lists */
        Iterator<Entry<E, DataList<?, E>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            Entry<E, DataList<?, E>> myEntry = myIterator.next();

            /* Access components */
            DataList<?, E> myList = myEntry.getValue();
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
        theDataKeySets.setGeneration(pGeneration);
        theDataKeys.setGeneration(pGeneration);
        theControlData.setGeneration(pGeneration);

        /* Loop through the List values */
        Iterator<DataList<?, E>> myIterator = iterator();
        while (myIterator.hasNext()) {
            DataList<?, E> myList = myIterator.next();

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
        theDataKeySets.setVersion(pVersion);
        theDataKeys.setVersion(pVersion);
        theControlData.setVersion(pVersion);

        /* Loop through the List values */
        Iterator<DataList<?, E>> myIterator = iterator();
        while (myIterator.hasNext()) {
            DataList<?, E> myList = myIterator.next();

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
        theDataKeySets.rewindToVersion(pVersion);
        theDataKeys.rewindToVersion(pVersion);
        theControlData.rewindToVersion(pVersion);

        /* Loop through the List values */
        Iterator<DataList<?, E>> myIterator = iterator();
        while (myIterator.hasNext()) {
            DataList<?, E> myList = myIterator.next();

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
        if (!theDataKeySets.equals(myThat.getDataKeySets())) {
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
        myHashCode += theDataKeySets.hashCode();
        myHashCode *= HASH_PRIME;
        myHashCode += theDataKeys.hashCode();
        myHashCode *= HASH_PRIME;
        myHashCode += theControlData.hashCode();

        /* Loop through the List values */
        Iterator<DataList<?, E>> myIterator = iterator();
        while (myIterator.hasNext()) {
            DataList<?, E> myList = myIterator.next();

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
        if (!theControlKeys.isEmpty() || !theControlData.isEmpty()) {
            return false;
        }
        if (!theDataKeySets.isEmpty() || !theDataKeys.isEmpty()) {
            return false;
        }

        /* Loop through the List values */
        Iterator<DataList<?, E>> myIterator = iterator();
        while (myIterator.hasNext()) {
            DataList<?, E> myList = myIterator.next();

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
        if (theControlKeys.hasUpdates() || theControlData.hasUpdates()) {
            return true;
        }
        if (theDataKeySets.hasUpdates() || theDataKeys.hasUpdates()) {
            return true;
        }

        /* Loop through the List values */
        Iterator<DataList<?, E>> myIterator = iterator();
        while (myIterator.hasNext()) {
            DataList<?, E> myList = myIterator.next();

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
     * @throws OceanusException on error
     */
    public boolean initialiseSecurity(final TaskControl<T> pTask,
                                      final T pBase) throws OceanusException {
        /* Access current profile */
        MetisProfile myTask = pTask.getActiveTask();
        MetisProfile myStage = myTask.startTask("InitSecurity");

        /* Set the number of stages */
        if (!pTask.setNumStages(1 + theNumEncrypted)) {
            return false;
        }

        /* Initialise Security */
        theControlKeys.initialiseSecurity(pBase);

        /* Access the control key */
        ControlKey myControl = getControlKey();

        /* Obtain base listMap */
        Map<E, DataList<?, E>> myMap = pBase.getListMap();

        /* Loop through the List values */
        Iterator<Entry<E, DataList<?, E>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            Entry<E, DataList<?, E>> myEntry = myIterator.next();

            /* Access the two lists */
            DataList<?, E> myList = myEntry.getValue();
            DataList<?, E> myBase = myMap.get(myEntry.getKey());

            /* If the list is an encrypted list */
            if (myList instanceof EncryptedList) {
                /* Adopt the security */
                myStage.startTask(myList.listName());
                EncryptedList<?, E> myEncrypted = (EncryptedList<?, E>) myList;
                if (!myEncrypted.adoptSecurity(pTask, myControl, (EncryptedList<?, E>) myBase)) {
                    return false;
                }
            }
        }

        /* Complete the task */
        myStage.end();

        /* Return success */
        return true;
    }

    /**
     * Renew Security.
     * @param pTask the task control
     * @return Continue <code>true/false</code>
     * @throws OceanusException on error
     */
    public boolean renewSecurity(final TaskControl<T> pTask) throws OceanusException {
        /* Access current profile */
        MetisProfile myTask = pTask.getActiveTask();
        MetisProfile myStage = myTask.startTask("ReNewSecurity");

        /* Access ControlData */
        ControlData myControl = getControl();

        /* Clone the control key */
        ControlKey myKey = theControlKeys.cloneItem(myControl.getControlKey());

        /* Declare the New Control Key */
        myControl.setControlKey(myKey);

        /* Update Security */
        boolean bSuccess = updateSecurity(pTask);

        /* Complete task */
        myStage.end();
        return bSuccess;
    }

    /**
     * Check Security for incomplete security operations.
     * @param pTask the task control
     * @return Continue <code>true/false</code>
     * @throws OceanusException on error
     */
    public boolean checkSecurity(final TaskControl<T> pTask) throws OceanusException {
        /* Access current profile */
        MetisProfile myTask = pTask.getActiveTask();
        MetisProfile myStage = myTask.startTask("CheckSecurity");

        /* If there is more than one controlKey */
        if (theControlKeys.size() > 1) {
            /* Update to the selected controlKey */
            return updateSecurity(pTask);
        } else {
            /* Make sure that password changes are OK */
            ControlKey myKey = getControlKey();
            if (myKey != null) {
                myKey.ensureKeySetHash();
            }
        }

        /* Return success */
        myStage.end();
        return true;
    }

    /**
     * Update Security.
     * @param pTask the task control
     * @return Continue <code>true/false</code>
     * @throws OceanusException on error
     */
    public boolean updateSecurity(final TaskControl<T> pTask) throws OceanusException {
        /* Access the control key */
        ControlKey myControl = getControlKey();

        /* Set the number of stages */
        if (!pTask.setNumStages(1 + theNumEncrypted)) {
            return false;
        }

        /* Loop through the List values */
        Iterator<DataList<?, E>> myIterator = iterator();
        while (myIterator.hasNext()) {
            DataList<?, E> myList = myIterator.next();

            /* If the list is an encrypted list */
            if (myList instanceof EncryptedList) {
                /* Update the security */
                EncryptedList<?, E> myEncrypted = (EncryptedList<?, E>) myList;
                if (!myEncrypted.updateSecurity(pTask, myControl)) {
                    return false;
                }
            }
        }

        /* Delete old ControlSets */
        theControlKeys.purgeOldControlKeys();
        setVersion(1);

        /* Return success */
        return true;
    }

    /**
     * Get the Password Hash.
     * @return the password hash
     * @throws OceanusException on error
     */
    public GordianKeySetHash getKeySetHash() throws OceanusException {
        /* Access the active control key */
        ControlKey myKey = getControlKey();

        /* Set the control */
        return (myKey == null)
                               ? null
                               : myKey.getKeySetHash();
    }

    /**
     * Update data with a new password.
     * @param pTask the task control
     * @param pSource the source of the data
     * @throws OceanusException on error
     */
    public void updatePasswordHash(final TaskControl<T> pTask,
                                   final String pSource) throws OceanusException {
        /* Obtain a new keySet hash */
        GordianKeySetHash myHash = theSecurity.resolveKeySetHash(null, pSource);

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
    public Iterator<DataList<?, E>> iterator() {
        return theListMap.values().iterator();
    }

    /**
     * Obtain list iterator.
     * @return the iterator
     */
    public Iterator<Entry<E, DataList<?, E>>> entryIterator() {
        return theListMap.entrySet().iterator();
    }

    /**
     * Cryptography Data Enum Types.
     */
    public enum CryptographyDataType {
        /**
         * ControlKey.
         */
        CONTROLKEY,

        /**
         * DataKey.
         */
        DATAKEYSET,

        /**
         * DataKey.
         */
        DATAKEY,

        /**
         * ControlData.
         */
        CONTROLDATA;

        /**
         * The String name.
         */
        private String theName;

        /**
         * The list name.
         */
        private String theListName;

        @Override
        public String toString() {
            /* If we have not yet loaded the name */
            if (theName == null) {
                /* Load the name */
                theName = PrometheusDataResource.getKeyForCryptoItem(this).getValue();
            }

            /* return the name */
            return theName;
        }

        /**
         * Obtain name of item.
         * @return the item name
         */
        public String getItemName() {
            return toString();
        }

        /**
         * Obtain name of associated list.
         * @return the list name
         */
        public String getListName() {
            /* If we have not yet loaded the name */
            if (theListName == null) {
                /* Load the name */
                theListName = PrometheusDataResource.getKeyForCryptoList(this).getValue();
            }

            /* return the list name */
            return theListName;
        }
    }
}

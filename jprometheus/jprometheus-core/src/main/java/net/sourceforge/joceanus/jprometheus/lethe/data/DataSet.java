/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.data;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.toolkit.MetisToolkit;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusPreferenceManager;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusPreferenceSecurity.PrometheusSecurityPreferenceKey;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusPreferenceSecurity.PrometheusSecurityPreferences;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.data.ControlData.ControlDataList;
import net.sourceforge.joceanus.jprometheus.lethe.data.ControlKey.ControlKeyList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataKeySet.DataKeySetList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.DataListSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.data.EncryptedItem.EncryptedList;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;

/**
 * DataSet definition and list. A DataSet is a set of DataLists backed by the three security lists.
 */
public abstract class DataSet
        implements MetisFieldItem, DataListSet {
    /**
     * The Hash prime.
     */
    protected static final int HASH_PRIME = 19;

    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<DataSet> FIELD_DEFS = MetisFieldSet.newFieldSet(DataSet.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResourceX.DATASET_GENERATION, DataSet::getGeneration);
        FIELD_DEFS.declareLocalField(PrometheusDataResourceX.DATASET_VERSION, DataSet::getVersion);
        FIELD_DEFS.declareLocalField(PrometheusDataResourceX.CONTROLKEY_LIST, DataSet::getControlKeys);
        FIELD_DEFS.declareLocalField(PrometheusDataResourceX.DATAKEYSET_LIST, DataSet::getDataKeySets);
        FIELD_DEFS.declareLocalField(PrometheusDataResourceX.CONTROLDATA_LIST, DataSet::getControlData);
    }

    /**
     * SecurityInit Task.
     */
    private static final String TASK_SECINIT = PrometheusDataResourceX.TASK_SECURITY_INIT.getValue();

    /**
     * SecurityCheck Task.
     */
    private static final String TASK_SECCHECK = PrometheusDataResourceX.TASK_SECURITY_CHECK.getValue();

    /**
     * SecurityUpdate Task.
     */
    private static final String TASK_SECUPDATE = PrometheusDataResourceX.TASK_SECURITY_UPDATE.getValue();

    /**
     * SecurityReNew Task.
     */
    private static final String TASK_SECRENEW = PrometheusDataResourceX.TASK_SECURITY_RENEW.getValue();

    /**
     * DataReBase Task.
     */
    private static final String TASK_DATAREBASE = PrometheusDataResourceX.TASK_DATA_REBASE.getValue();

    /**
     * DataDiff Task.
     */
    private static final String TASK_DATADIFF = PrometheusDataResourceX.TASK_DATA_DIFF.getValue();

    /**
     * Password Manager.
     */
    private final GordianPasswordManager thePasswordMgr;

    /**
     * ControlKeys.
     */
    private ControlKeyList theControlKeys;

    /**
     * DataKeySets.
     */
    private DataKeySetList theDataKeySets;

    /**
     * ControlData.
     */
    private ControlDataList theControlData;

    /**
     * Number of activeKeySets.
     */
    private final int theNumActiveKeySets;

    /**
     * Number of encrypted lists.
     */
    private int theNumEncrypted;

    /**
     * Generation of dataSet.
     */
    private int theGeneration;

    /**
     * Version of dataSet.
     */
    private int theVersion;

    /**
     * The DataList Map.
     */
    private final Map<PrometheusListKeyX, DataList<?>> theListMap;

    /**
     * General formatter.
     */
    private final TethysUIDataFormatter theFormatter;

    /**
     * Constructor for new empty DataSet.
     * @param pToolkit the toolkit set
     */
    protected DataSet(final PrometheusToolkit pToolkit) {
        /* Store the password manager and Enum class */
        thePasswordMgr = pToolkit.getPasswordManager();

        /* Access the SecurityPreferences */
        final PrometheusPreferenceManager myPrefMgr = pToolkit.getPreferenceManager();
        final PrometheusSecurityPreferences mySecPreferences = myPrefMgr.getPreferenceSet(PrometheusSecurityPreferences.class);
        theNumActiveKeySets = mySecPreferences.getIntegerValue(PrometheusSecurityPreferenceKey.ACTIVEKEYSETS);

        /* Create the empty security lists */
        theControlKeys = new ControlKeyList(this);
        theDataKeySets = new DataKeySetList(this);
        theControlData = new ControlDataList(this);

        /* Create the map of additional DataLists */
        theListMap = new LinkedHashMap<>();

        /* record formatter */
        final MetisToolkit myToolkit = pToolkit.getToolkit();
        theFormatter = myToolkit.getFormatter();
    }

    /**
     * Constructor for a cloned DataSet.
     * @param pSource the source DataSet
     */
    protected DataSet(final DataSet pSource) {

        /* Access the #activeKeySets */
        theNumActiveKeySets = pSource.getNumActiveKeySets();

        /* Store the password manager */
        thePasswordMgr = pSource.getPasswordMgr();

        /* Create the map of additional DataLists */
        theListMap = new LinkedHashMap<>();

        /* Copy formatter */
        theFormatter = pSource.getDataFormatter();
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return DataSet.class.getSimpleName();
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    public TethysUIDataFormatter getDataFormatter() {
        return theFormatter;
    }

    /**
     * Get Password Manager.
     * @return the password manager
     */
    public GordianPasswordManager getPasswordMgr() {
        return thePasswordMgr;
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
    protected Map<PrometheusListKeyX, DataList<?>> getListMap() {
        return theListMap;
    }

    /**
     * Construct a Clone for a DataSet.
     * @return the extract
     * @throws OceanusException on error
     */
    public abstract DataSet deriveCloneSet() throws OceanusException;

    /**
     * Build an empty clone dataSet.
     * @param pSource the source DataSet
     * @throws OceanusException on error
     */
    protected void buildEmptyCloneSet(final DataSet pSource) throws OceanusException {
        /* Clone the Security items */
        theControlKeys = pSource.getControlKeys().getEmptyList(ListStyle.CLONE);
        theDataKeySets = pSource.getDataKeySets().getEmptyList(ListStyle.CLONE);
        theControlData = pSource.getControlData().getEmptyList(ListStyle.CLONE);

        /* Loop through the source lists */
        final Iterator<Entry<PrometheusListKeyX, DataList<?>>> myIterator = pSource.entryIterator();
        while (myIterator.hasNext()) {
            final Entry<PrometheusListKeyX, DataList<?>> myEntry = myIterator.next();

            /* Access components */
            final PrometheusListKeyX myType = myEntry.getKey();
            final DataList<?> myList = myEntry.getValue();

            /* Create the empty cloned list */
            addList(myType, myList.getEmptyList(ListStyle.CLONE));
        }
    }

    /**
     * Construct a Clone for a DataSet.
     * @param pSource the source DataSet
     * @throws OceanusException on error
     */
    protected void deriveCloneSet(final DataSet pSource) throws OceanusException {
        /* Clone the Security items */
        theControlKeys.cloneList(this, pSource.getControlKeys());
        theDataKeySets.cloneList(this, pSource.getDataKeySets());
        theControlData.cloneList(this, pSource.getControlData());

        /* Obtain listMaps */
        final Map<PrometheusListKeyX, DataList<?>> myOldMap = pSource.getListMap();

        /* Loop through the new lists */
        final Iterator<Entry<PrometheusListKeyX, DataList<?>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            final Entry<PrometheusListKeyX, DataList<?>> myEntry = myIterator.next();

            /* Access components */
            final PrometheusListKeyX myType = myEntry.getKey();
            final DataList<?> myNew = myEntry.getValue();
            final DataList<?> myOld = myOldMap.get(myType);

            /* Clone the list */
            myNew.cloneList(this, myOld);
        }
    }

    /**
     * Construct an update extract for a FinanceData Set.
     * @return the extract
     * @throws OceanusException on error
     */
    public abstract DataSet deriveUpdateSet() throws OceanusException;

    /**
     * Construct an update extract for a DataSet.
     * @param pSource the source of the extract
     * @throws OceanusException on error
     */
    protected void deriveUpdateSet(final DataSet pSource) throws OceanusException {
        /* Build the security extract */
        theControlKeys = pSource.getControlKeys().deriveList(ListStyle.UPDATE);
        theDataKeySets = pSource.getDataKeySets().deriveList(ListStyle.UPDATE);
        theControlData = pSource.getControlData().deriveList(ListStyle.UPDATE);

        /* Loop through the source lists */
        final Iterator<Entry<PrometheusListKeyX, DataList<?>>> myIterator = pSource.entryIterator();
        while (myIterator.hasNext()) {
            final Entry<PrometheusListKeyX, DataList<?>> myEntry = myIterator.next();

            /* Access components */
            final PrometheusListKeyX myType = myEntry.getKey();
            final DataList<?> myList = myEntry.getValue();

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
     * @param pReport the report
     * @param pOld The old list to extract from
     * @return the difference set
     * @throws OceanusException on error
     */
    public abstract DataSet getDifferenceSet(TethysUIThreadStatusReport pReport,
                                             DataSet pOld) throws OceanusException;

    /**
     * Construct a difference extract between two DataSets. The difference extract will only contain
     * items that differ between the two DataSets. Items that are in the new list, but not in the
     * old list will be viewed as inserted. Items that are in the old list but not in the new list
     * will be viewed as deleted. Items that are in both list but differ will be viewed as changed
     * @param pReport the report
     * @param pNew The new list to compare
     * @param pOld The old list to compare
     * @throws OceanusException on error
     */
    protected void deriveDifferences(final TethysUIThreadStatusReport pReport,
                                     final DataSet pNew,
                                     final DataSet pOld) throws OceanusException {
        /* Access current profile */
        final TethysProfile myTask = pReport.getActiveTask();
        final TethysProfile myStage = myTask.startTask(TASK_DATADIFF);

        /* Build the security differences */
        theControlKeys = pNew.getControlKeys().deriveDifferences(this, pOld.getControlKeys());
        theDataKeySets = pNew.getDataKeySets().deriveDifferences(this, pOld.getDataKeySets());
        theControlData = pNew.getControlData().deriveDifferences(this, pOld.getControlData());

        /* Obtain listMaps */
        final Map<PrometheusListKeyX, DataList<?>> myOldMap = pOld.getListMap();

        /* Loop through the new lists */
        final Iterator<Entry<PrometheusListKeyX, DataList<?>>> myIterator = pNew.entryIterator();
        while (myIterator.hasNext()) {
            final Entry<PrometheusListKeyX, DataList<?>> myEntry = myIterator.next();

            /* Access components */
            final PrometheusListKeyX myType = myEntry.getKey();
            final DataList<?> myNew = myEntry.getValue();
            final DataList<?> myOld = myOldMap.get(myType);

            /* Derive Differences */
            myStage.startTask(myNew.listName());
            addList(myType, myNew.deriveDifferences(this, myOld));
        }

        /* Complete task */
        myStage.end();
    }

    /**
     * ReBase this data set against an earlier version.
     * @param pReport the report
     * @param pOld The old data to reBase against
     * @throws OceanusException on error
     */
    public void reBase(final TethysUIThreadStatusReport pReport,
                       final DataSet pOld) throws OceanusException {
        /* Access current profile */
        final TethysProfile myTask = pReport.getActiveTask();
        final TethysProfile myStage = myTask.startTask(TASK_DATAREBASE);

        /* ReBase the security items */
        boolean bUpdates = theControlKeys.reBase(pOld.getControlKeys());
        bUpdates |= theDataKeySets.reBase(pOld.getDataKeySets());
        bUpdates |= theControlData.reBase(pOld.getControlData());

        /* Obtain old listMap */
        final Map<PrometheusListKeyX, DataList<?>> myMap = pOld.getListMap();

        /* Loop through the lists */
        final Iterator<Entry<PrometheusListKeyX, DataList<?>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            final Entry<PrometheusListKeyX, DataList<?>> myEntry = myIterator.next();

            /* Access components */
            final PrometheusListKeyX myType = myEntry.getKey();
            final DataList<?> myList = myEntry.getValue();

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
    protected void addList(final PrometheusListKeyX pListType,
                           final DataList<?> pList) {
        /* Add the DataList to the map */
        theListMap.put(pListType, pList);

        /* Note if the list is an encrypted list */
        if (pList instanceof EncryptedList) {
            theNumEncrypted++;
        }
    }

    @Override
    public <L extends DataList<?>> L getDataList(final PrometheusListKeyX pListType,
                                                 final Class<L> pListClass) {
        /* Access the list */
        final DataList<?> myList = theListMap.get(pListType);

        /* Cast correctly */
        return myList == null
                              ? null
                              : pListClass.cast(myList);
    }

    /**
     * Obtain debug value for list.
     * @param pListType the list type
     * @return true/false
     */
    protected Object getFieldListValue(final PrometheusListKeyX pListType) {
        /* Access the class */
        final DataList<?> myList = theListMap.get(pListType);

        /* Cast correctly */
        return myList == null
               || myList.isEmpty()
                                   ? MetisDataFieldValue.SKIP
                                   : myList;
    }

    /**
     * Obtain DataList for an list class.
     * @param <L> the List type
     * @param pListClass the class of the list
     * @return the list of items
     */
    public <L extends DataList<?>> L getDataList(final Class<L> pListClass) {
        /* Loop through the lists */
        final Iterator<Entry<PrometheusListKeyX, DataList<?>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            final Entry<PrometheusListKeyX, DataList<?>> myEntry = myIterator.next();

            /* Access components */
            final DataList<?> myList = myEntry.getValue();
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
        theControlData.setGeneration(pGeneration);

        /* Loop through the List values */
        final Iterator<DataList<?>> myIterator = iterator();
        while (myIterator.hasNext()) {
            final DataList<?> myList = myIterator.next();

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
        theControlData.setVersion(pVersion);

        /* Loop through the List values */
        final Iterator<DataList<?>> myIterator = iterator();
        while (myIterator.hasNext()) {
            final DataList<?> myList = myIterator.next();

            /* Set the Version */
            myList.setVersion(pVersion);
        }
    }

    /**
     * Rewind items to the required version.
     * @param pVersion the version to rewind to
     */
    public void rewindToVersion(final int pVersion) {
        /* Record the version */
        theVersion = pVersion;

        /* rewind the security lists */
        theControlKeys.rewindToVersion(pVersion);
        theDataKeySets.rewindToVersion(pVersion);
        theControlData.rewindToVersion(pVersion);

        /* Loop through the List values */
        final Iterator<DataList<?>> myIterator = iterator();
        while (myIterator.hasNext()) {
            final DataList<?> myList = myIterator.next();

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
        final DataSet myThat = (DataSet) pThat;

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
        myHashCode += theControlData.hashCode();

        /* Loop through the List values */
        final Iterator<DataList<?>> myIterator = iterator();
        while (myIterator.hasNext()) {
            final DataList<?> myList = myIterator.next();

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
            || !theControlData.isEmpty()
            || !theDataKeySets.isEmpty()) {
            return false;
        }

        /* Loop through the List values */
        final Iterator<DataList<?>> myIterator = iterator();
        while (myIterator.hasNext()) {
            final DataList<?> myList = myIterator.next();

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
        if (theControlKeys.hasUpdates()
            || theControlData.hasUpdates()
            || theDataKeySets.hasUpdates()) {
            return true;
        }

        /* Loop through the List values */
        final Iterator<DataList<?>> myIterator = iterator();
        while (myIterator.hasNext()) {
            final DataList<?> myList = myIterator.next();

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
        final ControlData myControl = getControl();
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
     * @param pReport the report
     * @param pBase the database data
     * @throws OceanusException on error
     */
    public void initialiseSecurity(final TethysUIThreadStatusReport pReport,
                                   final DataSet pBase) throws OceanusException {
        /* Access current profile */
        final TethysProfile myTask = pReport.getActiveTask();
        final TethysProfile myStage = myTask.startTask(TASK_SECINIT);

        /* Set the number of stages */
        pReport.initTask(TASK_SECINIT);
        pReport.setNumStages(theNumEncrypted);

        /* Initialise Security */
        theControlKeys.initialiseSecurity(pBase);

        /* Obtain base listMap */
        final Map<PrometheusListKeyX, DataList<?>> myMap = pBase.getListMap();

        /* Loop through the List values */
        final Iterator<Entry<PrometheusListKeyX, DataList<?>>> myIterator = entryIterator();
        while (myIterator.hasNext()) {
            final Entry<PrometheusListKeyX, DataList<?>> myEntry = myIterator.next();

            /* Access the two lists */
            final DataList<?> myList = myEntry.getValue();
            final DataList<?> myBase = myMap.get(myEntry.getKey());

            /* If the list is an encrypted list */
            if (myList instanceof EncryptedList) {
                /* Adopt the security */
                myStage.startTask(myList.listName());
                final EncryptedList<?> myEncrypted = (EncryptedList<?>) myList;
                myEncrypted.adoptSecurity(pReport, (EncryptedList<?>) myBase);
            }
        }

        /* Complete the task */
        myStage.end();
    }

    /**
     * Renew Security.
     * @param pReport the report
     * @throws OceanusException on error
     */
    public void renewSecurity(final TethysUIThreadStatusReport pReport) throws OceanusException {
        /* Access current profile */
        final TethysProfile myTask = pReport.getActiveTask();
        final TethysProfile myStage = myTask.startTask(TASK_SECRENEW);

        /* Access ControlData */
        final ControlData myControl = getControl();

        /* Clone the control key */
        final ControlKey myKey = theControlKeys.cloneItem(myControl.getControlKey());

        /* Declare the New Control Key */
        myControl.setControlKey(myKey);

        /* Update Security */
        updateSecurity(pReport);

        /* Complete task */
        myStage.end();
    }

    /**
     * Check Security for incomplete security operations.
     * @param pReport the report
     * @throws OceanusException on error
     */
    public void checkSecurity(final TethysUIThreadStatusReport pReport) throws OceanusException {
        /* Access current profile */
        final TethysProfile myTask = pReport.getActiveTask();
        final TethysProfile myStage = myTask.startTask(TASK_SECCHECK);

        /* If there is more than one controlKey */
        if (theControlKeys.size() > 1) {
            /* Update to the selected controlKey */
            updateSecurity(pReport);
        } else {
            /* Make sure that password changes are OK */
            final ControlKey myKey = getControlKey();
            if (myKey != null) {
                myKey.ensureKeySetHash();
            }
        }

        /* Complete task */
        myStage.end();
    }

    /**
     * Update Security.
     * @param pReport the report
     * @throws OceanusException on error
     */
    public void updateSecurity(final TethysUIThreadStatusReport pReport) throws OceanusException {
        /* Access the control key */
        final ControlKey myControl = getControlKey();

        /* Set the number of stages */
        pReport.initTask(TASK_SECUPDATE);
        pReport.setNumStages(theNumEncrypted);

        /* Loop through the List values */
        final Iterator<DataList<?>> myIterator = iterator();
        while (myIterator.hasNext()) {
            final DataList<?> myList = myIterator.next();

            /* If the list is an encrypted list */
            if (myList instanceof EncryptedList) {
                /* Update the security */
                final EncryptedList<?> myEncrypted = (EncryptedList<?>) myList;
                myEncrypted.updateSecurity(pReport, myControl);
            }
        }

        /* Delete old ControlSets */
        theControlKeys.purgeOldControlKeys();
        setVersion(1);
    }

    /**
     * Get the Password Hash.
     * @return the password hash
     * @throws OceanusException on error
     */
    public GordianKeySetHash getKeySetHash() throws OceanusException {
        /* Access the active control key */
        final ControlKey myKey = getControlKey();

        /* Set the control */
        return (myKey == null)
                               ? null
                               : myKey.getKeySetHash();
    }

    /**
     * Update data with a new password.
     * @param pReport the report
     * @param pSource the source of the data
     * @throws OceanusException on error
     */
    public void updatePasswordHash(final TethysUIThreadStatusReport pReport,
                                   final String pSource) throws OceanusException {
        /* Obtain a new keySet hash */
        final GordianKeySetHash myHash = thePasswordMgr.newKeySetHash(pSource);

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
    public Iterator<Entry<PrometheusListKeyX, DataList<?>>> entryIterator() {
        return theListMap.entrySet().iterator();
    }

    /**
     * Cryptography Data Enum Types.
     */
    public enum CryptographyDataType
        implements PrometheusListKeyX {
        /**
         * ControlData.
         */
        CONTROLDATA(1),

        /**
         * ControlKey.
         */
        CONTROLKEY(2),

        /**
         * DataKey.
         */
        DATAKEYSET(3);

        /**
         * Maximum keyId.
         */
        public static final Integer MAXKEYID = DATAKEYSET.getItemKey();

        /**
         * The list key.
         */
        private final Integer theKey;

        /**
         * The String name.
         */
        private String theName;

        /**
         * The list name.
         */
        private String theListName;

        /**
         * Constructor.
         * @param pKey the key
         */
        CryptographyDataType(final Integer pKey) {
            theKey = pKey;
        }

        @Override
        public String toString() {
            /* If we have not yet loaded the name */
            if (theName == null) {
                /* Load the name */
                theName = PrometheusDataResourceX.getKeyForCryptoItem(this).getValue();
            }

            /* return the name */
            return theName;
        }

        @Override
        public String getItemName() {
            return toString();
        }

        @Override
        public String getListName() {
            /* If we have not yet loaded the name */
            if (theListName == null) {
                /* Load the name */
                theListName = PrometheusDataResourceX.getKeyForCryptoList(this).getValue();
            }

            /* return the list name */
            return theListName;
        }

        @Override
        public Integer getItemKey() {
            return theKey;
        }
    }
}

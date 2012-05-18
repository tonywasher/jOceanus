/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.models.data;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.JDataWalker.ModelException;
import net.sourceforge.JDataWalker.ReportFields;
import net.sourceforge.JDataWalker.ReportFields.ReportField;
import net.sourceforge.JDataWalker.ReportObject.ReportDetail;
import net.sourceforge.JGordianKnot.PasswordHash;
import net.sourceforge.JGordianKnot.SecureManager;
import uk.co.tolcroft.models.data.ControlData.ControlDataList;
import uk.co.tolcroft.models.data.ControlKey.ControlKeyList;
import uk.co.tolcroft.models.data.DataKey.DataKeyList;
import uk.co.tolcroft.models.data.EncryptedItem.EncryptedList;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.views.DataControl;

public abstract class DataSet<T extends DataSet<T>> implements ReportDetail {
    /**
     * Report fields
     */
    protected static final ReportFields theFields = new ReportFields(DataSet.class.getSimpleName());

    /* Field IDs */
    public static final ReportField FIELD_GENERATION = theFields.declareLocalField("Generation");
    public static final ReportField FIELD_SECURITY = theFields.declareLocalField("Security");
    public static final ReportField FIELD_CONTROLKEYS = theFields.declareLocalField("ControlKeys");
    public static final ReportField FIELD_DATAKEYS = theFields.declareLocalField("DataKeys");
    public static final ReportField FIELD_CONTROLDATA = theFields.declareLocalField("ControlData");

    @Override
    public ReportFields getReportFields() {
        return theFields;
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        if (pField == FIELD_GENERATION)
            return theGeneration;
        if (pField == FIELD_SECURITY)
            return theSecurity;
        if (pField == FIELD_CONTROLKEYS)
            return theControlKeys;
        if (pField == FIELD_DATAKEYS)
            return theDataKeys;
        if (pField == FIELD_CONTROLDATA)
            return theDataKeys;
        return null;
    }

    @Override
    public String getObjectSummary() {
        return DataSet.class.getSimpleName();
    }

    private SecureManager theSecurity = null;
    private ControlKeyList theControlKeys = null;
    private DataKeyList theDataKeys = null;
    private ControlDataList theControlData = null;
    private int theNumEncrypted = 0;
    private int theGeneration = 0;

    /**
     * The DataList Array
     */
    private List<DataList<?, ?>> theList = null;

    /* Access methods */
    public SecureManager getSecurity() {
        return theSecurity;
    }

    public ControlKeyList getControlKeys() {
        return theControlKeys;
    }

    public DataKeyList getDataKeys() {
        return theDataKeys;
    }

    public ControlDataList getControlData() {
        return theControlData;
    }

    public int getGeneration() {
        return theGeneration;
    }

    /**
     * Constructor for new empty DataSet
     * @param pSecurity the secure manager
     */
    protected DataSet(SecureManager pSecurity) {
        /* Store the security manager and class */
        theSecurity = pSecurity;

        /* Create the empty security lists */
        theControlKeys = new ControlKeyList(this);
        theDataKeys = new DataKeyList(this);
        theControlData = new ControlDataList(this);

        /* Create the list of additional DataLists */
        theList = new ArrayList<DataList<?, ?>>();
    }

    /**
     * Constructor for a cloned DataSet
     * @param pSource the source DataSet
     */
    protected DataSet(DataSet<T> pSource) {
        /* Store the security manager and class */
        theSecurity = pSource.getSecurity();

        /* Create the map of additional lists */
        theList = new ArrayList<DataList<?, ?>>();
    }

    /**
     * Construct a Deep Copy for for a DataSet.
     * @return the copied dataSet
     */
    public abstract T getDeepCopy();

    /**
     * Construct a Deep Copy for a DataSet.
     * @param pSource the source DataSet
     */
    protected void getDeepCopy(DataSet<T> pSource) {
        /* Deep Copy the Security items */
        theControlKeys = pSource.getControlKeys().getDeepCopy(this);
        theDataKeys = pSource.getDataKeys().getDeepCopy(this);
        theControlData = pSource.getControlData().getDeepCopy(this);
    }

    /**
     * Construct an update extract for a DataSet.
     * @param pSource the source of the extract
     */
    protected void getUpdateSet(T pSource) {
        /* Build the static differences */
        theControlKeys = pSource.getControlKeys().getUpdateList();
        theDataKeys = pSource.getDataKeys().getUpdateList();
        theControlData = pSource.getControlData().getUpdateList();
    }

    /**
     * Construct a difference extract between two DataSets. The difference extract will only contain items
     * that differ between the two DataSets. Items that are in the new list, but not in the old list will be
     * viewed as inserted. Items that are in the old list but not in the new list will be viewed as deleted.
     * Items that are in both list but differ will be viewed as changed
     * @param pOld The old list to extract from
     * @return the difference set
     * @throws ModelException
     */
    public abstract T getDifferenceSet(T pOld) throws ModelException;

    /**
     * Construct a difference extract between two DataSets. The difference extract will only contain items
     * that differ between the two DataSets. Items that are in the new list, but not in the old list will be
     * viewed as inserted. Items that are in the old list but not in the new list will be viewed as deleted.
     * Items that are in both list but differ will be viewed as changed
     * @param pNew The new list to compare
     * @param pOld The old list to compare
     * @throws ModelException
     */
    protected void getDifferenceSet(T pNew,
                                    T pOld) throws ModelException {
        /* Build the security differences */
        theControlKeys = pNew.getControlKeys().getDifferences(pOld.getControlKeys());
        theDataKeys = pNew.getDataKeys().getDifferences(pOld.getDataKeys());
        theControlData = pNew.getControlData().getDifferences(pOld.getControlData());
    }

    /**
     * ReBase this data set against an earlier version.
     * @param pOld The old data to reBase against
     * @throws ModelException
     */
    public void reBase(T pOld) throws ModelException {
        /* ReBase the security items */
        theControlKeys.reBase(pOld.getControlKeys());
        theDataKeys.reBase(pOld.getDataKeys());
        theControlData.reBase(pOld.getControlData());
    }

    /**
     * Add DataList to list of lists
     * @param pList the list to add
     */
    protected void addList(DataList<?, ?> pList) {
        /* Add the DataList to the list */
        theList.add(pList);

        /* Note if the list is an encrypted list */
        if (pList instanceof EncryptedList)
            theNumEncrypted++;
    }

    /**
     * Obtain DataList for an item type
     * @param <L> the List type
     * @param <D> the item type
     * @param pItemType the type of items
     * @return the list of items
     */
    public <L extends DataList<L, D>, D extends DataItem<D>> L getDataList(Class<L> pItemType) {
        /* Access the class */
        DataList<?, ?> myList = getDataListForClass(pItemType);

        /* Cast correctly */
        return (myList == null) ? null : pItemType.cast(myList);
    }

    /**
     * Obtain DataList for an item type
     * @param pItemType the type of items
     * @return the list of items
     */
    protected DataList<?, ?> getDataListForClass(Class<?> pItemType) {
        /* Create the iterator */
        ListIterator<DataList<?, ?>> myIterator = theList.listIterator();

        /* Loop through the list */
        while (myIterator.hasNext()) {
            /* Return list if it is requested one */
            DataList<?, ?> myList = myIterator.next();
            if (pItemType == myList.getClass())
                return myList;
        }

        /* Return not found */
        return null;
    }

    /**
     * Set Generation
     * @param pGeneration the generation
     */
    public void setGeneration(int pGeneration) {
        /* Record the generation */
        theGeneration = pGeneration;

        /* Set the security lists */
        theControlKeys.setGeneration(pGeneration);
        theDataKeys.setGeneration(pGeneration);
        theControlData.setGeneration(pGeneration);

        /* Loop through the List values */
        for (DataList<?, ?> myList : theList) {
            /* Set the Generation */
            myList.setGeneration(pGeneration);
        }
    }

    /**
     * Analyse the DataSet
     * @param pControl The DataControl
     * @throws ModelException
     */
    public abstract void analyseData(DataControl<?> pControl) throws ModelException;

    @Override
    public boolean equals(Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat)
            return true;
        if (pThat == null)
            return false;

        /* Make sure that the object is a DataSet */
        if (pThat.getClass() != this.getClass())
            return false;

        /* Access the object as a DataSet */
        DataSet<?> myThat = (DataSet<?>) pThat;

        /* Compare security data */
        if (!theControlKeys.equals(myThat.getControlKeys()))
            return false;
        if (!theDataKeys.equals(myThat.getDataKeys()))
            return false;
        if (!theControlData.equals(myThat.getControlData()))
            return false;

        /* Loop through the List values */
        for (DataList<?, ?> myThisList : theList) {
            /* Access equivalent list */
            DataList<?, ?> myThatList = myThat.getDataListForClass(myThisList.getClass());

            /* Handle trivial cases */
            if (myThisList == myThatList)
                continue;

            /* Compare list */
            if (!myThisList.equals(myThatList))
                return false;
        }

        /* We are identical */
        return true;
    }

    @Override
    public int hashCode() {
        /* Build initial hashCode */
        int myHashCode = theControlKeys.hashCode();
        myHashCode *= 17;
        myHashCode += theDataKeys.hashCode();
        myHashCode *= 17;
        myHashCode += theControlData.hashCode();

        /* Loop through the List values */
        for (DataList<?, ?> myList : theList) {
            /* Access equivalent list */
            myHashCode *= 17;
            myHashCode += myList.hashCode();
        }

        /* Return the hashCode */
        return myHashCode;
    }

    /**
     * Determine whether a DataSet has entries
     * @return <code>true</code> if the DataSet has entries
     */
    public boolean isEmpty() {
        /* Determine whether the security data is empty */
        if (!theControlKeys.isEmpty() || !theDataKeys.isEmpty() || !theControlData.isEmpty())
            return false;

        /* Loop through the List values */
        for (DataList<?, ?> myList : theList) {
            /* Determine whether the list is empty */
            if (!myList.isEmpty())
                return false;
        }

        /* Return the indication */
        return true;
    }

    /**
     * Determine whether the Data-set has updates
     * @return <code>true</code> if the Data-set has updates, <code>false</code> if not
     */
    public boolean hasUpdates() {
        /* Determine whether we have updates */
        if (theControlKeys.hasUpdates())
            return true;
        if (theDataKeys.hasUpdates())
            return true;
        if (theControlData.hasUpdates())
            return true;

        /* Loop through the List values */
        for (DataList<?, ?> myList : theList) {
            /* Determine whether the list has updates */
            if (myList.hasUpdates())
                return true;
        }

        /* We have no updates */
        return false;
    }

    /**
     * Get the control record
     * @return the control record
     */
    public ControlData getControl() {
        /* Set the control */
        return getControlData().getControl();
    }

    /**
     * Get the active control key
     * @return the control key
     */
    public ControlKey getControlKey() {
        /* Access the control element from the database */
        ControlData myControl = getControl();
        ControlKey myKey = null;

        /* Access control key from control data */
        if (myControl != null)
            myKey = myControl.getControlKey();

        /* Return the key */
        return myKey;
    }

    /**
     * Initialise Security from database (if present)
     * @param pThread the thread status
     * @param pBase the database data
     * @return Continue <code>true/false</code>
     * @throws ModelException
     */
    public boolean initialiseSecurity(ThreadStatus<T> pThread,
                                      T pBase) throws ModelException {
        /* Set the number of stages */
        if (!pThread.setNumStages(1 + theNumEncrypted))
            return false;

        /* Initialise Security */
        theControlKeys.initialiseSecurity(pBase);

        /* Access the control key */
        ControlKey myControl = getControlKey();

        /* Loop through the List values */
        for (DataList<?, ?> myList : theList) {
            /* Access equivalent base list */
            DataList<?, ?> myBase = pBase.getDataListForClass(myList.getClass());

            /* If the list is an encrypted list */
            if (myList instanceof EncryptedList) {
                /* Adopt the security */
                EncryptedList<?, ?> myEncrypted = (EncryptedList<?, ?>) myList;
                if (!myEncrypted.adoptSecurity(pThread, myControl, (EncryptedList<?, ?>) myBase))
                    return false;
            }
        }

        /* Return success */
        return true;
    }

    /**
     * Renew Security
     * @param pThread the thread status
     * @return Continue <code>true/false</code>
     * @throws ModelException
     */
    public boolean renewSecurity(ThreadStatus<T> pThread) throws ModelException {
        /* Access ControlData */
        ControlData myControl = getControl();

        /* Clone the control key */
        ControlKey myKey = theControlKeys.addItem(myControl.getControlKey());

        /* Declare the New Control Key */
        myControl.setControlKey(myKey);

        /* Update Security */
        return updateSecurity(pThread);
    }

    /**
     * Update Security
     * @param pThread the thread status
     * @return Continue <code>true/false</code>
     * @throws ModelException
     */
    public boolean updateSecurity(ThreadStatus<T> pThread) throws ModelException {
        /* Access the control key */
        ControlKey myControl = getControlKey();

        /* Set the number of stages */
        if (!pThread.setNumStages(1 + theNumEncrypted))
            return false;

        /* Loop through the List values */
        for (DataList<?, ?> myList : theList) {
            /* If the list is an encrypted list */
            if (myList instanceof EncryptedList) {
                /* Update the security */
                EncryptedList<?, ?> myEncrypted = (EncryptedList<?, ?>) myList;
                if (!myEncrypted.updateSecurity(pThread, myControl))
                    return false;
            }
        }

        /* Delete old ControlSets */
        theControlKeys.purgeOldControlKeys();

        /* Return success */
        return true;
    }

    /**
     * Get the Password Hash
     * @return the password hash
     * @throws ModelException
     */
    public PasswordHash getPasswordHash() throws ModelException {
        /* Access the active control key */
        ControlKey myKey = getControlKey();

        /* Set the control */
        return (myKey == null) ? null : myKey.getPasswordHash();
    }

    /**
     * Update data with a new password
     * @param pThread the thread status
     * @param pSource the source of the data
     * @throws ModelException
     */
    public void updatePasswordHash(ThreadStatus<T> pThread,
                                   String pSource) throws ModelException {
        /* Obtain a new password hash */
        PasswordHash myHash = theSecurity.resolvePasswordHash(null, pSource);

        /* Update the control details */
        getControlKey().updatePasswordHash(myHash);
    }
}

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
import java.util.Map;

import net.sourceforge.joceanus.jgordianknot.crypto.CipherSet;
import net.sourceforge.joceanus.jgordianknot.crypto.HashKey;
import net.sourceforge.joceanus.jgordianknot.crypto.PasswordHash;
import net.sourceforge.joceanus.jgordianknot.crypto.SecureManager;
import net.sourceforge.joceanus.jgordianknot.crypto.SecurityGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.SymKeyType;
import net.sourceforge.joceanus.jmetis.viewer.EncryptionGenerator;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.DataKey.DataKeyList;
import net.sourceforge.joceanus.jprometheus.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * ControlKey definition and list. The Control Key represents the passwordHash that controls securing of the dataKeys. It maintains a map of the associated
 * DataKeys.
 * @author Tony Washer
 */
public class DataKeySet
        extends DataItem<CryptographyDataType>
        implements Comparable<DataKeySet> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = CryptographyDataType.DATAKEYSET.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = CryptographyDataType.DATAKEYSET.getListName();

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, DataItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Field ID for ControlKey.
     */
    public static final JDataField FIELD_CONTROLKEY = FIELD_DEFS.declareEqualityValueField(CryptographyDataType.CONTROLKEY.getItemName());

    /**
     * Field ID for CreationDate.
     */
    public static final JDataField FIELD_CREATEDATE = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.DATAKEYSET_CREATION.getValue());

    /**
     * Field ID for DataKeyMap.
     */
    public static final JDataField FIELD_MAP = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAKEYSET_KEYMAP.getValue());

    /**
     * Field ID for CipherSet.
     */
    public static final JDataField FIELD_CIPHER = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAKEYSET_CIPHERSET.getValue());

    /**
     * The DataKey Map.
     */
    private Map<SymKeyType, DataKey> theMap = null;

    /**
     * The Encryption CipherSet.
     */
    private CipherSet theCipherSet = null;

    /**
     * The Security Generator.
     */
    private SecurityGenerator theSecurityGenerator = null;

    /**
     * The Encryption Field Generator.
     */
    private EncryptionGenerator theFieldGenerator = null;

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_MAP.equals(pField)) {
            return theMap;
        }
        if (FIELD_CIPHER.equals(pField)) {
            return theCipherSet;
        }
        return super.getFieldValue(pField);
    }

    /**
     * Get the Encryption Field Generator.
     * @return the field generator
     */
    public EncryptionGenerator getFieldGenerator() {
        return theFieldGenerator;
    }

    /**
     * Get the ControlKey.
     * @return the controlKey
     */
    public final ControlKey getControlKey() {
        return getControlKey(getValueSet());
    }

    /**
     * Get the ControlKeyId for this item.
     * @return the ControlKeyId
     */
    public Integer getControlKeyId() {
        ControlKey myKey = getControlKey();
        return (myKey == null)
                              ? null
                              : myKey.getId();
    }

    /**
     * Get the CreationDate.
     * @return the creationDate
     */
    public final JDateDay getCreationDate() {
        return getCreationDate(getValueSet());
    }

    /**
     * Get the ControlKey.
     * @param pValueSet the valueSet
     * @return the control Key
     */
    public static ControlKey getControlKey(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CONTROLKEY, ControlKey.class);
    }

    /**
     * Get the CreationDate.
     * @param pValueSet the valueSet
     * @return the creationDate
     */
    public static JDateDay getCreationDate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CREATEDATE, JDateDay.class);
    }

    /**
     * Is this locked by prime hash.
     * @return true/false
     */
    protected Boolean isHashPrime() {
        return getControlKey().isHashPrime();
    }

    /**
     * Get the PassWordHash.
     * @return the passwordHash
     * @throws JOceanusException on error
     */
    protected PasswordHash getPasswordHash() throws JOceanusException {
        return getControlKey().getPasswordHash();
    }

    /**
     * Get the PassWordHash.
     * @param useHashPrime true/false
     * @return the passwordHash
     * @throws JOceanusException on error
     */
    protected PasswordHash getPasswordHash(final Boolean useHashPrime) throws JOceanusException {
        return getControlKey().getPasswordHash(useHashPrime);
    }

    /**
     * Set the ControlKey.
     * @param pValue the controlKey
     */
    private void setValueControlKey(final ControlKey pValue) {
        getValueSet().setValue(FIELD_CONTROLKEY, pValue);
    }

    /**
     * Set the ControlKey Id.
     * @param pId the controlKey id
     */
    private void setValueControlKey(final Integer pId) {
        getValueSet().setValue(FIELD_CONTROLKEY, pId);
    }

    /**
     * Set the CreationDate.
     * @param pValue the creationDate
     */
    private void setValueCreationDate(final JDateDay pValue) {
        getValueSet().setValue(FIELD_CREATEDATE, pValue);
    }

    /**
     * Get the HashKey.
     * @return the HashKey
     */
    protected final HashKey getHashKey() {
        return getControlKey().getHashKey();
    }

    /**
     * Resolve the Active HashKey.
     * @throws JOceanusException on error
     */
    protected final void resolveHashKey() throws JOceanusException {
        getControlKey().resolveHashKey();
    }

    @Override
    public DataKeySet getBase() {
        return (DataKeySet) super.getBase();
    }

    @Override
    public DataKeySetList getList() {
        return (DataKeySetList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list the copy belongs to
     * @param pSource The Key to copy
     */
    protected DataKeySet(final DataKeySetList pList,
                         final DataKeySet pSource) {
        /* Set standard values */
        super(pList, pSource);

        /* Switch on the LinkStyle */
        switch (getStyle()) {
            case CLONE:
                theSecurityGenerator = pSource.theSecurityGenerator;
                theMap = new EnumMap<SymKeyType, DataKey>(SymKeyType.class);
                theCipherSet = new CipherSet(theSecurityGenerator, getHashKey());
                theFieldGenerator = new EncryptionGenerator(theCipherSet, getDataSet().getDataFormatter());
                break;
            default:
                break;
        }
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private DataKeySet(final DataKeySetList pList,
                       final DataValues<CryptographyDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access the Security manager */
        DataSet<?, ?> myData = getDataSet();
        SecureManager mySecure = myData.getSecurity();
        JDataFormatter myFormatter = myData.getDataFormatter();

        /* Store the ControlKey */
        Object myValue = pValues.getValue(FIELD_CONTROLKEY);
        if (myValue instanceof Integer) {
            /* Store the integer */
            Integer myInt = (Integer) myValue;
            setValueControlKey(myInt);

            /* Resolve the ControlKey */
            resolveDataLink(FIELD_CONTROLKEY, myData.getControlKeys());

            /* Resolve the Active HashKey */
            resolveHashKey();

            /* Record the security generator */
            theSecurityGenerator = mySecure.getSecurityGenerator();

            /* Create the DataKey Map */
            theMap = new EnumMap<SymKeyType, DataKey>(SymKeyType.class);

            /* Create the CipherSet and security generator */
            theCipherSet = new CipherSet(theSecurityGenerator, getHashKey());
            theFieldGenerator = new EncryptionGenerator(theCipherSet, myFormatter);

            /* Register the DataKeySet */
            getControlKey().registerDataKeySet(this);

            /* Store the CreationDate */
            myValue = pValues.getValue(FIELD_CREATEDATE);
            if (!(myValue instanceof JDateDay)) {
                myValue = new JDateDay();
            }
            setValueCreationDate((JDateDay) myValue);

        } else {
            /* Pass on exception */
            throw new JPrometheusDataException(this, ERROR_CREATEITEM);
        }
    }

    /**
     * Constructor for a new DataKeySet. This will create a set of DataKeys.
     * @param pList the list to which to add the keySet to
     * @param pControlKey the control key
     * @throws JOceanusException on error
     */
    protected DataKeySet(final DataKeySetList pList,
                         final ControlKey pControlKey) throws JOceanusException {
        /* Initialise the item */
        super(pList, 0);

        /* Protect against exceptions */
        try {
            /* Store the Details */
            setValueControlKey(pControlKey);

            /* Access the Security manager */
            DataSet<?, ?> myData = getDataSet();
            SecureManager mySecure = myData.getSecurity();
            JDataFormatter myFormatter = myData.getDataFormatter();

            /* Record the security generator */
            theSecurityGenerator = mySecure.getSecurityGenerator();

            /* Create the DataKey Map */
            theMap = new EnumMap<SymKeyType, DataKey>(SymKeyType.class);

            /* Create the CipherSet */
            theCipherSet = new CipherSet(theSecurityGenerator, getHashKey());
            theFieldGenerator = new EncryptionGenerator(theCipherSet, myFormatter);

            /* Set the creationDate */
            setValueCreationDate(new JDateDay());

            /* Allocate the DataKeys */
            allocateDataKeys(myData);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JPrometheusDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public int compareTo(final DataKeySet pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the underlying object */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Resolve the ControlKey */
        DataSet<?, ?> myData = getDataSet();
        resolveDataLink(FIELD_CONTROLKEY, myData.getControlKeys());
        ControlKey myControlKey = getControlKey();

        /* Register the KeySet */
        myControlKey.registerDataKeySet(this);
    }

    /**
     * Allocate a new set of DataKeys.
     * @param pData the DataSet
     * @throws JOceanusException on error
     */
    private void allocateDataKeys(final DataSet<?, ?> pData) throws JOceanusException {
        /* Access the DataKey List */
        DataKeyList myKeys = pData.getDataKeys();
        setNewVersion();

        /* Loop through the SymKeyType values */
        for (SymKeyType myType : SymKeyType.values()) {
            /* Create a new DataKey for this DataKeySet */
            DataKey myKey = myKeys.createNewKey(this, myType);
            myKey.setNewVersion();

            /* Store the DataKey into the map */
            theMap.put(myType, myKey);

            /* Declare the Cipher */
            theCipherSet.addCipher(myKey.getCipher());
        }
    }

    /**
     * Delete the old set of DataKeySet and DataKeys.
     */
    protected void deleteDataKeySet() {
        /* Loop through the SymKeyType values */
        for (SymKeyType myType : SymKeyType.values()) {
            /* Access the Data Key */
            DataKey myKey = theMap.get(myType);

            /* Mark as deleted */
            if (myKey != null) {
                myKey.setDeleted(true);
            }
        }

        /* Mark this dataKeySet as deleted */
        setDeleted(true);
    }

    /**
     * Update password hash.
     * @param pPrimeHash this is the prime hash
     * @param pHash the new password hash
     * @return were there changes? true/false
     * @throws JOceanusException on error
     */
    protected boolean updatePasswordHash(final Boolean pPrimeHash,
                                         final PasswordHash pHash) throws JOceanusException {
        /* Loop through the SymKeyType values */
        boolean bChanges = false;
        for (SymKeyType myType : SymKeyType.values()) {
            /* Access the Data Key */
            DataKey myKey = theMap.get(myType);

            /* Update the password hash */
            if (myKey != null) {
                bChanges |= myKey.updatePasswordHash(pPrimeHash, pHash);
            }
        }

        /* return the flag */
        return bChanges;
    }

    /**
     * Register DataKey.
     * @param pKey the DataKey to register
     */
    protected void registerDataKey(final DataKey pKey) {
        /* Store the DataKey into the map */
        theMap.put(pKey.getKeyType(), pKey);

        /* Declare the Cipher */
        theCipherSet.addCipher(pKey.getCipher());
    }

    /**
     * DataKeySet List.
     */
    public static class DataKeySetList
            extends DataList<DataKeySet, CryptographyDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public JDataFields getItemFields() {
            return DataKeySet.FIELD_DEFS;
        }

        @Override
        public boolean includeDataXML() {
            return false;
        }

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         */
        protected DataKeySetList(final DataSet<?, ?> pData) {
            this(pData, ListStyle.CORE);
        }

        /**
         * Construct an empty generic ControlKey list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected DataKeySetList(final DataSet<?, ?> pData,
                                 final ListStyle pStyle) {
            super(DataKeySet.class, pData, CryptographyDataType.DATAKEYSET, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private DataKeySetList(final DataKeySetList pSource) {
            super(pSource);
        }

        @Override
        protected DataKeySetList getEmptyList(final ListStyle pStyle) {
            DataKeySetList myList = new DataKeySetList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public DataKeySetList deriveList(final ListStyle pStyle) throws JOceanusException {
            return (DataKeySetList) super.deriveList(pStyle);
        }

        @Override
        public DataKeySetList deriveDifferences(final DataSet<?, ?> pDataSet,
                                                final DataList<?, CryptographyDataType> pOld) {
            return (DataKeySetList) super.deriveDifferences(pDataSet, pOld);
        }

        @Override
        public DataKeySet addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a DataKeySet */
            if (!(pItem instanceof DataKeySet)) {
                return null;
            }

            /* Clone the data key set */
            DataKeySet mySet = new DataKeySet(this, (DataKeySet) pItem);
            add(mySet);
            return mySet;
        }

        @Override
        public DataKeySet addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataKeySet addValuesItem(final DataValues<CryptographyDataType> pValues) throws JOceanusException {
            /* Create the dataKeySet */
            DataKeySet mySet = new DataKeySet(this, pValues);

            /* Check that this keyId has not been previously added */
            if (!isIdUnique(mySet.getId())) {
                mySet.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JPrometheusDataException(mySet, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(mySet);

            /* Return it */
            return mySet;
        }

        /**
         * Clone KeySet from a DataBase.
         * @param pControlKey the ControlKey to clone
         * @param pKeySet the DataKeySet to clone
         * @return the new DataKeySet
         * @throws JOceanusException on error
         */
        protected DataKeySet cloneDataKeySet(final ControlKey pControlKey,
                                             final DataKeySet pKeySet) throws JOceanusException {
            /* Build data values */
            DataValues<CryptographyDataType> myValues = new DataValues<CryptographyDataType>(DataKeySet.OBJECT_NAME);
            myValues.addValue(DataKeySet.FIELD_ID, pKeySet.getId());
            myValues.addValue(DataKeySet.FIELD_CONTROLKEY, pControlKey.getId());
            myValues.addValue(DataKeySet.FIELD_CREATEDATE, pKeySet.getCreationDate());

            /* Clone the dataKeySet */
            DataKeySet myKeySet = addValuesItem(myValues);

            /* Access the DataKey List */
            DataSet<?, ?> myData = getDataSet();
            DataKeyList myKeys = myData.getDataKeys();

            /* Loop through the SymKeyType values */
            for (SymKeyType myType : SymKeyType.values()) {
                /* Access the source Data key */
                DataKey mySrcKey = pKeySet.theMap.get(myType);

                /* Create a new DataKey for this ControlKey */
                DataKey myKey = myKeys.cloneItem(myKeySet, mySrcKey);

                /* Store the DataKey into the map */
                myKeySet.theMap.put(myType, myKey);

                /* Declare the Cipher */
                myKeySet.theCipherSet.addCipher(myKey.getCipher());
            }

            /* return the cloned keySet */
            return myKeySet;
        }

        @Override
        public void postProcessOnLoad() throws JOceanusException {
            /* Just sort the list */
            reSort();
        }

        @Override
        protected DataMapItem<DataKeySet, CryptographyDataType> allocateDataMap() {
            /* Unused */
            throw new UnsupportedOperationException();
        }
    }
}

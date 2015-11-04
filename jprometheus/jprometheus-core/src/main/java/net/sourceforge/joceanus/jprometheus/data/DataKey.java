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

import net.sourceforge.joceanus.jgordianknot.crypto.CipherSet;
import net.sourceforge.joceanus.jgordianknot.crypto.DataCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.PasswordHash;
import net.sourceforge.joceanus.jgordianknot.crypto.SecurityGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.SymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.SymmetricKey;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.ValueSet;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * DataKey definition and list. The Data Key represents a SymmetricKey that is secured via a the
 * ControlKey. For a single control key, one DataKey is allocated for each available SymmetricKey
 * Type and the set forms a CipherSet for encryption purposes.
 * @author Tony Washer
 */
public class DataKey
        extends DataItem<CryptographyDataType>
        implements Comparable<DataKey> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = CryptographyDataType.DATAKEY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = CryptographyDataType.DATAKEY.getListName();

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, DataItem.FIELD_DEFS);

    /**
     * DataKeySet Field Id.
     */
    public static final JDataField FIELD_KEYSET = FIELD_DEFS.declareEqualityValueField(DataKeySet.OBJECT_NAME);

    /**
     * KeyType Field Id.
     */
    public static final JDataField FIELD_KEYTYPE = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.DATAKEY_TYPE.getValue());

    /**
     * HashPrime Field Id.
     */
    public static final JDataField FIELD_HASHPRIME = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.CONTROLKEY_PRIME.getValue());

    /**
     * KeyDefinition Field Id.
     */
    public static final JDataField FIELD_KEYDEF = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.DATAKEY_DEF.getValue());

    /**
     * DataKey Field Id.
     */
    public static final JDataField FIELD_KEY = FIELD_DEFS.declareDerivedValueField(PrometheusDataResource.DATAKEY_KEY.getValue());

    /**
     * Cipher Field Id.
     */
    public static final JDataField FIELD_CIPHER = FIELD_DEFS.declareDerivedValueField(PrometheusDataResource.DATAKEY_CIPHER.getValue());

    /**
     * Encrypted Symmetric Key Length.
     */
    public static final int KEYLEN = SymmetricKey.IDSIZE;

    /**
     * Copy Constructor.
     * @param pList the list to add to
     * @param pSource The Key to copy
     */
    protected DataKey(final DataKeyList pList,
                      final DataKey pSource) {
        /* Set standard values */
        super(pList, pSource);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private DataKey(final DataKeyList pList,
                    final DataValues<CryptographyDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the DataKeySet */
        Object myValue = pValues.getValue(FIELD_KEYSET);
        if (myValue instanceof Integer) {
            /* Store the integer */
            Integer myInt = (Integer) myValue;
            setValueDataKeySet(myInt);

            /* Resolve the DataKeySet */
            DataSet<?, ?> myData = getDataSet();
            resolveDataLink(FIELD_KEYSET, myData.getDataKeySets());
            DataKeySet myKeySet = getDataKeySet();
            SecurityGenerator myGenerator = myData.getSecurity().getSecurityGenerator();

            /* Store the KeyType */
            myValue = pValues.getValue(FIELD_KEYTYPE);
            if (myValue instanceof Integer) {
                myInt = (Integer) myValue;
                setValueKeyType(myInt);
                setValueKeyType(myGenerator.deriveSymKeyTypeFromExternalId(myInt));
            }

            /* Store the PrimeHash indicator */
            myValue = pValues.getValue(FIELD_HASHPRIME);
            Boolean isHashPrime = (myValue instanceof Boolean)
                                                               ? (Boolean) myValue
                                                               : Boolean.TRUE;
            setValueHashPrime(isHashPrime);

            /* Store the KeyDef */
            myValue = pValues.getValue(FIELD_KEYDEF);
            if (myValue instanceof byte[]) {
                byte[] myBytes = (byte[]) myValue;
                setValueSecuredKeyDef(myBytes);

                /* Create the Symmetric Key from the wrapped data */
                PasswordHash myHash = myKeySet.getPasswordHash(isHashPrime);
                CipherSet myCipher = myHash.getCipherSet();
                SymmetricKey myKey = myCipher.deriveSymmetricKey(myBytes, getKeyType());
                setValueDataKey(myKey);

                /* Access the Cipher */
                setValueCipher(myKey.getDataCipher());

                /* Register the DataKey */
                myKeySet.registerDataKey(this);
            }
        }
    }

    /**
     * Constructor for a new DataKey in a new DataKeySet.
     * @param pList the list to add to
     * @param pKeySet the KeySet to which this key belongs
     * @param pKeyType the Key type of the new key
     * @throws JOceanusException on error
     */
    private DataKey(final DataKeyList pList,
                    final DataKeySet pKeySet,
                    final SymKeyType pKeyType) throws JOceanusException {
        /* Initialise the item */
        super(pList, 0);

        /* Protect against exceptions */
        try {
            /* Store the Details */
            setValueDataKeySet(pKeySet);
            setValueKeyType(pKeyType);

            /* Create the new key */
            Boolean isHashPrime = pKeySet.isHashPrime();
            setValueHashPrime(isHashPrime);

            /* Create the new key */
            PasswordHash myHash = pKeySet.getPasswordHash(isHashPrime);
            CipherSet myCipher = myHash.getCipherSet();
            SecurityGenerator myGenerator = myHash.getSecurityGenerator();
            SymmetricKey myKey = myGenerator.generateSymmetricKey(pKeyType);
            setValueDataKey(myKey);

            /* Store its secured keyDef */
            setValueSecuredKeyDef(myCipher.secureSymmetricKey(myKey));

            /* Access the Cipher */
            setValueCipher(myKey.getDataCipher());

            /* Register the DataKey */
            pKeySet.registerDataKey(this);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JPrometheusDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Constructor for a cloned DataKey in a new DataKeySet.
     * @param pList the list to add to
     * @param pKeySet the ControlKey to which this key belongs
     * @param pDataKey the DataKey to clone
     * @throws JOceanusException on error
     */
    private DataKey(final DataKeyList pList,
                    final DataKeySet pKeySet,
                    final DataKey pDataKey) throws JOceanusException {
        /* Initialise the item */
        super(pList, 0);

        /* Store the KeySet details */
        setValueDataKeySet(pKeySet);

        /* Copy the key details */
        setValueDataKey(pDataKey.getDataKey());
        setValueHashPrime(pDataKey.isHashPrime());
        setValueSecuredKeyDef(pDataKey.getSecuredKeyDef());
        setValueCipher(pDataKey.getCipher());
        setValueKeyType(pDataKey.getKeyType());
    }

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Get the DataKeySet.
     * @return the dataKeySet
     */
    public DataKeySet getDataKeySet() {
        return getDataKeySet(getValueSet());
    }

    /**
     * Get the DataKeySetId for this item.
     * @return the DataKeySetId
     */
    public Integer getDataKeySetId() {
        DataKeySet myKey = getDataKeySet();
        return (myKey == null)
                               ? null
                               : myKey.getId();
    }

    /**
     * Get the Key Type.
     * @return the key type
     */
    public SymKeyType getKeyType() {
        return getKeyType(getValueSet());
    }

    /**
     * Get the Key Type Id.
     * @return the key type id
     */
    public Integer getKeyTypeId() {
        SymKeyType myType = getKeyType();
        DataSet<?, ?> myData = getDataSet();
        SecurityGenerator myGenerator = myData.getSecurity().getSecurityGenerator();
        return (myType == null)
                                ? null
                                : myGenerator.getExternalId(myType);
    }

    /**
     * Is this locked by prime hash.
     * @return true/false
     */
    public Boolean isHashPrime() {
        return isHashPrime(getValueSet());
    }

    /**
     * Get the Key Definition.
     * @return the key definition
     */
    public byte[] getSecuredKeyDef() {
        return getSecuredKeyDef(getValueSet());
    }

    /**
     * Get the DataKey.
     * @return the data key
     */
    protected SymmetricKey getDataKey() {
        return getDataKey(getValueSet());
    }

    /**
     * Get the Cipher.
     * @return the cipher
     */
    protected DataCipher getCipher() {
        return getCipher(getValueSet());
    }

    /**
     * Get the DataKeySet.
     * @param pValueSet the valueSet
     * @return the dataKeySet
     */
    public static DataKeySet getDataKeySet(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_KEYSET, DataKeySet.class);
    }

    /**
     * Get the Key type.
     * @param pValueSet the valueSet
     * @return the Key type
     */
    public static SymKeyType getKeyType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_KEYTYPE, SymKeyType.class);
    }

    /**
     * Is this locked by prime hash.
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isHashPrime(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_HASHPRIME, Boolean.class);
    }

    /**
     * Get the Key Definition .
     * @param pValueSet the valueSet
     * @return the Key Definition
     */
    public static byte[] getSecuredKeyDef(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_KEYDEF, byte[].class);
    }

    /**
     * Get the DataKey.
     * @param pValueSet the valueSet
     * @return the data Key
     */
    protected static SymmetricKey getDataKey(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_KEY, SymmetricKey.class);
    }

    /**
     * Get the Cipher.
     * @param pValueSet the valueSet
     * @return the cipher
     */
    protected static DataCipher getCipher(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CIPHER, DataCipher.class);
    }

    /**
     * Set the DataKeySet.
     * @param pValue the dataKeySet
     */
    private void setValueDataKeySet(final DataKeySet pValue) {
        getValueSet().setValue(FIELD_KEYSET, pValue);
    }

    /**
     * Set the DataKeySet Id.
     * @param pId the dataKeySet id
     */
    private void setValueDataKeySet(final Integer pId) {
        getValueSet().setValue(FIELD_KEYSET, pId);
    }

    /**
     * Set the Key Type.
     * @param pValue the KeyType
     */
    private void setValueKeyType(final SymKeyType pValue) {
        getValueSet().setValue(FIELD_KEYTYPE, pValue);
    }

    /**
     * Set the KeyType id.
     * @param pId the KeyType id
     */
    private void setValueKeyType(final Integer pId) {
        getValueSet().setValue(FIELD_KEYTYPE, pId);
    }

    /**
     * Set the HashPrime indicator.
     * @param pPrime true/false
     */
    private void setValueHashPrime(final Boolean pPrime) {
        getValueSet().setValue(FIELD_HASHPRIME, pPrime);
    }

    /**
     * Set the Key Definition.
     * @param pValue the KeyDefinition
     */
    private void setValueSecuredKeyDef(final byte[] pValue) {
        getValueSet().setValue(FIELD_KEYDEF, pValue);
    }

    /**
     * Set the DataKey.
     * @param pValue the dataKey
     */
    private void setValueDataKey(final SymmetricKey pValue) {
        getValueSet().setValue(FIELD_KEY, pValue);
    }

    /**
     * Set the Cipher.
     * @param pValue the cipher
     */
    private void setValueCipher(final DataCipher pValue) {
        getValueSet().setValue(FIELD_CIPHER, pValue);
    }

    @Override
    public DataKey getBase() {
        return (DataKey) super.getBase();
    }

    @Override
    public DataKeyList getList() {
        return (DataKeyList) super.getList();
    }

    @Override
    public int compareTo(final DataKey pThat) {
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
        resolveDataLink(FIELD_KEYSET, myData.getDataKeySets());
        DataKeySet myKeySet = getDataKeySet();

        /* Register the Key */
        myKeySet.registerDataKey(this);
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
        /* Determine whether we need to update */
        if (!pPrimeHash.equals(isHashPrime())) {
            /* Store the current detail into history */
            pushHistory();

            /* Update the Security Control Key and obtain the new secured KeyDef */
            CipherSet myCipher = pHash.getCipherSet();
            setValueHashPrime(pPrimeHash);
            setValueSecuredKeyDef(myCipher.secureSymmetricKey(getDataKey()));

            /* Check for changes */
            if (checkForHistory()) {
                return true;
            }
        }

        /* No changes */
        return false;
    }

    /**
     * DataKey List.
     */
    public static class DataKeyList
            extends DataList<DataKey, CryptographyDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        /**
         * Construct an empty CORE DataKey list.
         * @param pData the DataSet for the list
         */
        protected DataKeyList(final DataSet<?, ?> pData) {
            this(pData, ListStyle.CORE);
        }

        /**
         * Construct an empty generic DataKey list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected DataKeyList(final DataSet<?, ?> pData,
                              final ListStyle pStyle) {
            super(DataKey.class, pData, CryptographyDataType.DATAKEY, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private DataKeyList(final DataKeyList pSource) {
            super(pSource);
        }

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
            return DataKey.FIELD_DEFS;
        }

        @Override
        public boolean includeDataXML() {
            return false;
        }

        @Override
        protected DataKeyList getEmptyList(final ListStyle pStyle) {
            DataKeyList myList = new DataKeyList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public DataKeyList deriveList(final ListStyle pStyle) throws JOceanusException {
            return (DataKeyList) super.deriveList(pStyle);
        }

        @Override
        public DataKeyList deriveDifferences(final DataSet<?, ?> pDataSet,
                                             final DataList<?, CryptographyDataType> pOld) {
            return (DataKeyList) super.deriveDifferences(pDataSet, pOld);
        }

        @Override
        public DataKey addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a DataKey */
            if (!(pItem instanceof DataKey)) {
                return null;
            }

            /* Clone the data key */
            DataKey myKey = new DataKey(this, (DataKey) pItem);
            add(myKey);
            return myKey;
        }

        @Override
        public DataKey addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataKey addValuesItem(final DataValues<CryptographyDataType> pValues) throws JOceanusException {
            /* Create the dataKey */
            DataKey myKey = new DataKey(this, pValues);

            /* Check that this keyId has not been previously added */
            if (!isIdUnique(myKey.getId())) {
                myKey.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JPrometheusDataException(myKey, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myKey);

            /* Return it */
            return myKey;
        }

        /**
         * Add a new DataKey for the passed KeySet.
         * @param pKeySet the dataKeySet
         * @param pKeyType the KeyType
         * @return the new DataKey
         * @throws JOceanusException on error
         */
        public DataKey createNewKey(final DataKeySet pKeySet,
                                    final SymKeyType pKeyType) throws JOceanusException {
            /* Create the key */
            DataKey myKey = new DataKey(this, pKeySet, pKeyType);

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Add a clone of the passed DataKey for the passed KeySet.
         * @param pKeySet the KeySet
         * @param pDataKey the DataKey
         * @return the new DataKey
         * @throws JOceanusException on error
         */
        public DataKey cloneItem(final DataKeySet pKeySet,
                                 final DataKey pDataKey) throws JOceanusException {
            /* Create the key */
            DataKey myKey = new DataKey(this, pKeySet, pDataKey);

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        @Override
        public void postProcessOnLoad() throws JOceanusException {
            /* Just sort the list */
            reSort();
        }

        @Override
        protected DataMapItem<DataKey, CryptographyDataType> allocateDataMap() {
            /* Unused */
            throw new UnsupportedOperationException();
        }
    }
}

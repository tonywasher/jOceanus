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

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jgordianknot.crypto.CipherSet;
import net.sourceforge.joceanus.jgordianknot.crypto.DataCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.PasswordHash;
import net.sourceforge.joceanus.jgordianknot.crypto.SecurityGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.SymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.SymmetricKey;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.ControlKey.ControlKeyList;
import net.sourceforge.joceanus.jprometheus.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * DataKey definition and list. The Data Key represents a SymmetricKey that is secured via a the ControlKey. For a single control key, one DataKey is allocated
 * for each available SymmetricKey Type and the set forms a CipherSet for encryption purposes.
 * @author Tony Washer
 */
public class DataKey
        extends DataItem<CryptographyDataType>
        implements Comparable<DataKey> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(DataKey.class.getName());

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

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * ControlKey Field Id.
     */
    public static final JDataField FIELD_CONTROLKEY = FIELD_DEFS.declareEqualityValueField(ControlKey.OBJECT_NAME);

    /**
     * KeyType Field Id.
     */
    public static final JDataField FIELD_KEYTYPE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataType"));

    /**
     * KeyDefinition Field Id.
     */
    public static final JDataField FIELD_KEYDEF = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDefinition"));

    /**
     * Password Hash Field Id.
     */
    public static final JDataField FIELD_HASH = FIELD_DEFS.declareDerivedValueField(NLS_BUNDLE.getString("DataHash"));

    /**
     * DataKey Field Id.
     */
    public static final JDataField FIELD_KEY = FIELD_DEFS.declareDerivedValueField(NLS_BUNDLE.getString("DataKey"));

    /**
     * Cipher Field Id.
     */
    public static final JDataField FIELD_CIPHER = FIELD_DEFS.declareDerivedValueField(NLS_BUNDLE.getString("DataCipher"));

    /**
     * Encrypted Symmetric Key Length.
     */
    public static final int KEYLEN = SymmetricKey.IDSIZE;

    /**
     * Get the ControlKey.
     * @return the controlKey
     */
    public ControlKey getControlKey() {
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
        return (myType == null)
                               ? null
                               : myType.getId();
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
     * Get the PasswordHash.
     * @return the passwordHash
     */
    protected PasswordHash getPasswordHash() {
        return getPasswordHash(getValueSet());
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
     * Get the Key type.
     * @param pValueSet the valueSet
     * @return the Key type
     */
    public static SymKeyType getKeyType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_KEYTYPE, SymKeyType.class);
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
     * Get the PasswordHash.
     * @param pValueSet the valueSet
     * @return the passwordHash
     */
    protected static PasswordHash getPasswordHash(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_HASH, PasswordHash.class);
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

    /**
     * Set the PasswordHash.
     * @param pValue the passwordHash
     */
    private void setValuePasswordHash(final PasswordHash pValue) {
        getValueSet().setValue(FIELD_HASH, pValue);
    }

    @Override
    public DataKey getBase() {
        return (DataKey) super.getBase();
    }

    @Override
    public DataKeyList getList() {
        return (DataKeyList) super.getList();
    }

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
     * Secure Constructor.
     * @param pList the list to add to
     * @param pId the id of the DataKey
     * @param pControlId the id of the ControlKey
     * @param pKeyTypeId the id of the KeyType
     * @param pSecurityKey the encrypted symmetric key
     * @throws JOceanusException on error
     */
    private DataKey(final DataKeyList pList,
                    final Integer pId,
                    final Integer pControlId,
                    final Integer pKeyTypeId,
                    final byte[] pSecurityKey) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Record the IDs */
            setValueControlKey(pControlId);
            setValueKeyType(pKeyTypeId);
            setValueSecuredKeyDef(pSecurityKey);

            /* Determine the SymKeyType */
            setValueKeyType(SymKeyType.fromId(pKeyTypeId));

            /* Look up the ControlKey */
            DataSet<?, ?> myData = getDataSet();
            ControlKeyList myKeys = myData.getControlKeys();
            ControlKey myControlKey = myKeys.findItemById(pControlId);
            if (myControlKey == null) {
                addError(ERROR_UNKNOWN, FIELD_CONTROLKEY);
                throw new JPrometheusDataException(this, ERROR_RESOLUTION);
            }

            /* Store the keys */
            setValueControlKey(myControlKey);

            /* Create the Symmetric Key from the wrapped data */
            PasswordHash myHash = myControlKey.getPasswordHash();
            CipherSet myCipher = myHash.getCipherSet();
            SymmetricKey myKey = myCipher.deriveSymmetricKey(pSecurityKey, getKeyType());
            setValueDataKey(myKey);
            setValuePasswordHash(myHash);

            /* Access the Cipher */
            setValueCipher(myKey.getDataCipher());

            /* Register the DataKey */
            myControlKey.registerDataKey(this);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JPrometheusDataException(this, ERROR_CREATEITEM, e);
        }
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

        /* Store the ControlKey */
        Object myValue = pValues.getValue(FIELD_CONTROLKEY);
        if (myValue instanceof Integer) {
            /* Store the integer */
            Integer myInt = (Integer) myValue;
            setValueControlKey(myInt);

            /* Look up the ControlKey */
            DataSet<?, ?> myData = getDataSet();
            ControlKeyList myKeys = myData.getControlKeys();
            ControlKey myControlKey = myKeys.findItemById(myInt);
            if (myControlKey == null) {
                addError(ERROR_UNKNOWN, FIELD_CONTROLKEY);
                throw new JPrometheusDataException(this, ERROR_RESOLUTION);
            }

            /* Store the keys */
            setValueControlKey(myControlKey);

            /* Store the KeyType */
            myValue = pValues.getValue(FIELD_KEYTYPE);
            if (myValue instanceof Integer) {
                myInt = (Integer) myValue;
                setValueKeyType(myInt);
                setValueKeyType(SymKeyType.fromId(myInt));
            }

            /* Store the KeyDef */
            myValue = pValues.getValue(FIELD_KEYDEF);
            if (myValue instanceof byte[]) {
                byte[] myBytes = (byte[]) myValue;
                setValueSecuredKeyDef(myBytes);

                /* Create the Symmetric Key from the wrapped data */
                PasswordHash myHash = getControlKey().getPasswordHash();
                CipherSet myCipher = myHash.getCipherSet();
                SymmetricKey myKey = myCipher.deriveSymmetricKey(myBytes, getKeyType());
                setValueDataKey(myKey);
                setValuePasswordHash(myHash);

                /* Access the Cipher */
                setValueCipher(myKey.getDataCipher());

                /* Register the DataKey */
                myControlKey.registerDataKey(this);
            }
        }
    }

    /**
     * Constructor for a new DataKey in a new ControlKey set.
     * @param pList the list to add to
     * @param pControlKey the ControlKey to which this key belongs
     * @param pKeyType the Key type of the new key
     * @throws JOceanusException on error
     */
    private DataKey(final DataKeyList pList,
                    final ControlKey pControlKey,
                    final SymKeyType pKeyType) throws JOceanusException {
        /* Initialise the item */
        super(pList, 0);

        /* Protect against exceptions */
        try {
            /* Store the Details */
            setValueControlKey(pControlKey);
            setValueKeyType(pKeyType);

            /* Create the new key */
            PasswordHash myHash = pControlKey.getPasswordHash();
            CipherSet myCipher = myHash.getCipherSet();
            SecurityGenerator myGenerator = myHash.getSecurityGenerator();
            SymmetricKey myKey = myGenerator.generateSymmetricKey(pKeyType);
            setValuePasswordHash(myHash);
            setValueDataKey(myKey);

            /* Store its secured keyDef */
            setValueSecuredKeyDef(myCipher.secureSymmetricKey(myKey));

            /* Access the Cipher */
            setValueCipher(myKey.getDataCipher());

            /* Register the DataKey */
            pControlKey.registerDataKey(this);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JPrometheusDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Constructor for a cloned DataKey in a new ControlKey set.
     * @param pList the list to add to
     * @param pControlKey the ControlKey to which this key belongs
     * @param pDataKey the DataKey to clone
     * @throws JOceanusException on error
     */
    private DataKey(final DataKeyList pList,
                    final ControlKey pControlKey,
                    final DataKey pDataKey) throws JOceanusException {
        /* Initialise the item */
        super(pList, 0);

        /* Store the Control details */
        setValueControlKey(pControlKey);

        /* Copy the key details */
        setValueDataKey(pDataKey.getDataKey());
        setValueSecuredKeyDef(pDataKey.getSecuredKeyDef());
        setValueCipher(pDataKey.getCipher());
        setValueKeyType(pDataKey.getKeyType());
    }

    @Override
    public int compareTo(final DataKey pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the underlying object */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() {
        DataSet<?, ?> myData = getDataSet();
        ControlKeyList myKeys = myData.getControlKeys();

        /* Update to use the local copy of the ControlKeys */
        ControlKey myKey = getControlKey();
        ControlKey myNewKey = myKeys.findItemById(myKey.getId());
        setValueControlKey(myNewKey);

        /* Register the Key */
        myNewKey.registerDataKey(this);
    }

    /**
     * Update password hash.
     * @throws JOceanusException on error
     */
    protected void updatePasswordHash() throws JOceanusException {
        /* Store the current detail into history */
        pushHistory();

        /* Update the Security Control Key and obtain the new secured KeyDef */
        ControlKey myControlKey = getControlKey();
        PasswordHash myHash = myControlKey.getPasswordHash();
        CipherSet myCipher = myHash.getCipherSet();
        setValuePasswordHash(myHash);
        setValueSecuredKeyDef(myCipher.secureSymmetricKey(getDataKey()));

        /* Check for changes */
        checkForHistory();
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
        protected DataKeyList getEmptyList(final ListStyle pStyle) {
            DataKeyList myList = new DataKeyList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public DataKeyList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (DataKeyList) super.cloneList(pDataSet);
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

        /**
         * Add a DataKey from Database/Backup.
         * @param pId the id of the DataKey
         * @param pControlId the id of the ControlKey
         * @param pKeyTypeId the id of the KeyType
         * @param pSecurityKey the encrypted symmetric key
         * @return the new item
         * @throws JOceanusException on error
         */
        public DataKey addSecureItem(final Integer pId,
                                     final Integer pControlId,
                                     final Integer pKeyTypeId,
                                     final byte[] pSecurityKey) throws JOceanusException {
            /* Create the DataKey */
            DataKey myKey = new DataKey(this, pId, pControlId, pKeyTypeId, pSecurityKey);

            /* Check that this KeyId has not been previously added */
            if (!isIdUnique(pId)) {
                myKey.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JPrometheusDataException(myKey, ERROR_DUPLICATE);
            }

            /* Add to the list */
            add(myKey);
            return myKey;
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
         * Add a new DataKey for the passed ControlKey.
         * @param pControlKey the ControlKey
         * @param pKeyType the KeyType
         * @return the new DataKey
         * @throws JOceanusException on error
         */
        public DataKey createNewKey(final ControlKey pControlKey,
                                    final SymKeyType pKeyType) throws JOceanusException {
            /* Create the key */
            DataKey myKey = new DataKey(this, pControlKey, pKeyType);

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Add a clone of the passed DataKey for the passed ControlKey.
         * @param pControlKey the ControlKey
         * @param pDataKey the DataKey
         * @return the new DataKey
         * @throws JOceanusException on error
         */
        public DataKey cloneItem(final ControlKey pControlKey,
                                 final DataKey pDataKey) throws JOceanusException {
            /* Create the key */
            DataKey myKey = new DataKey(this, pControlKey, pDataKey);

            /* Add to the list */
            add(myKey);
            return myKey;
        }
    }
}

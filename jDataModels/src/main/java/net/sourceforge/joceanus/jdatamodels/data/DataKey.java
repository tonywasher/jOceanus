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

import java.util.ResourceBundle;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.ValueSet;
import net.sourceforge.jOceanus.jDataModels.data.ControlKey.ControlKeyList;
import net.sourceforge.jOceanus.jGordianKnot.CipherSet;
import net.sourceforge.jOceanus.jGordianKnot.DataCipher;
import net.sourceforge.jOceanus.jGordianKnot.PasswordHash;
import net.sourceforge.jOceanus.jGordianKnot.SecurityGenerator;
import net.sourceforge.jOceanus.jGordianKnot.SymKeyType;
import net.sourceforge.jOceanus.jGordianKnot.SymmetricKey;

/**
 * DataKey definition and list. The Data Key represents a SymmetricKey that is secured via a the ControlKey. For a single control key, one DataKey is allocated
 * for each available SymmetricKey Type and the set forms a CipherSet for encryption purposes.
 * @author Tony Washer
 */
public class DataKey
        extends DataItem
        implements Comparable<DataKey> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(DataKey.class.getName());

    /**
     * Object name.
     */
    public static final String OBJECT_NAME = DataKey.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), DataItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * ControlKey Field Id.
     */
    public static final JDataField FIELD_CONTROLKEY = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataControl"));

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
     * @throws JDataException on error
     */
    private DataKey(final DataKeyList pList,
                    final Integer pId,
                    final Integer pControlId,
                    final Integer pKeyTypeId,
                    final byte[] pSecurityKey) throws JDataException {
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
            DataSet<?> myData = getDataSet();
            ControlKeyList myKeys = myData.getControlKeys();
            ControlKey myControlKey = myKeys.findItemById(pControlId);
            if (myControlKey == null) {
                addError(ERROR_UNKNOWN, FIELD_CONTROLKEY);
                throw new JDataException(ExceptionClass.DATA, this, ERROR_RESOLUTION);
            }

            /* Store the keys */
            setValueControlKey(myControlKey);

            /* Create the Symmetric Key from the wrapped data */
            PasswordHash myHash = myControlKey.getPasswordHash();
            CipherSet myCipher = myHash.getCipherSet();
            SymmetricKey myKey = myCipher.deriveSymmetricKey(pSecurityKey);
            setValueDataKey(myKey);
            setValuePasswordHash(myHash);

            /* Access the Cipher */
            setValueCipher(myKey.initDataCipher());

            /* Register the DataKey */
            myControlKey.registerDataKey(this);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Constructor for a new DataKey in a new ControlKey set.
     * @param pList the list to add to
     * @param pControlKey the ControlKey to which this key belongs
     * @param pKeyType the Key type of the new key
     * @throws JDataException on error
     */
    private DataKey(final DataKeyList pList,
                    final ControlKey pControlKey,
                    final SymKeyType pKeyType) throws JDataException {
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
            setValueCipher(myKey.initDataCipher());

            /* Register the DataKey */
            pControlKey.registerDataKey(this);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Constructor for a cloned DataKey in a new ControlKey set.
     * @param pList the list to add to
     * @param pControlKey the ControlKey to which this key belongs
     * @param pDataKey the DataKey to clone
     * @throws JDataException on error
     */
    private DataKey(final DataKeyList pList,
                    final ControlKey pControlKey,
                    final DataKey pDataKey) throws JDataException {
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
        DataSet<?> myData = getDataSet();
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
     * @throws JDataException on error
     */
    protected void updatePasswordHash() throws JDataException {
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
            extends DataList<DataKey> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public boolean includeDataXML() {
            return false;
        }

        /**
         * Construct an empty CORE DataKey list.
         * @param pData the DataSet for the list
         */
        protected DataKeyList(final DataSet<?> pData) {
            super(DataKey.class, pData, ListStyle.CORE);
        }

        /**
         * Construct an empty generic DataKey list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected DataKeyList(final DataSet<?> pData,
                              final ListStyle pStyle) {
            super(DataKey.class, pData, pStyle);
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
        public DataKeyList cloneList(final DataSet<?> pDataSet) throws JDataException {
            return (DataKeyList) super.cloneList(pDataSet);
        }

        @Override
        public DataKeyList deriveList(final ListStyle pStyle) throws JDataException {
            return (DataKeyList) super.deriveList(pStyle);
        }

        @Override
        public DataKeyList deriveDifferences(final DataList<DataKey> pOld) {
            return (DataKeyList) super.deriveDifferences(pOld);
        }

        @Override
        public DataKey addCopyItem(final DataItem pItem) {
            /* Can only clone a DataKey */
            if (!(pItem instanceof DataKey)) {
                return null;
            }

            /* Clone the control key */
            DataKey myKey = new DataKey(this, (DataKey) pItem);
            add(myKey);
            return myKey;
        }

        @Override
        public DataKey addNewItem() {
            return null;
        }

        /**
         * Add a DataKey from Database/Backup.
         * @param pId the id of the DataKey
         * @param pControlId the id of the ControlKey
         * @param pKeyTypeId the id of the KeyType
         * @param pSecurityKey the encrypted symmetric key
         * @return the new item
         * @throws JDataException on error
         */
        public DataKey addSecureItem(final Integer pId,
                                     final Integer pControlId,
                                     final Integer pKeyTypeId,
                                     final byte[] pSecurityKey) throws JDataException {
            /* Create the DataKey */
            DataKey myKey = new DataKey(this, pId, pControlId, pKeyTypeId, pSecurityKey);

            /* Check that this KeyId has not been previously added */
            if (!isIdUnique(pId)) {
                myKey.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myKey, ERROR_DUPLICATE);
            }

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Add a new DataKey for the passed ControlKey.
         * @param pControlKey the ControlKey
         * @param pKeyType the KeyType
         * @return the new DataKey
         * @throws JDataException on error
         */
        public DataKey createNewKey(final ControlKey pControlKey,
                                    final SymKeyType pKeyType) throws JDataException {
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
         * @throws JDataException on error
         */
        public DataKey cloneItem(final ControlKey pControlKey,
                                 final DataKey pDataKey) throws JDataException {
            /* Create the key */
            DataKey myKey = new DataKey(this, pControlKey, pDataKey);

            /* Add to the list */
            add(myKey);
            return myKey;
        }
    }
}
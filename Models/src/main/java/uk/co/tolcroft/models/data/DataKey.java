/*******************************************************************************
 * JDataModel: Data models
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

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JGordianKnot.CipherSet;
import net.sourceforge.JGordianKnot.DataCipher;
import net.sourceforge.JGordianKnot.PasswordHash;
import net.sourceforge.JGordianKnot.SecurityGenerator;
import net.sourceforge.JGordianKnot.SymKeyType;
import net.sourceforge.JGordianKnot.SymmetricKey;
import uk.co.tolcroft.models.data.ControlKey.ControlKeyList;

/**
 * DataKey definition and list. The Data Key represents a SymmetricKey that is secured via a the ControlKey.
 * For a single control key, one DataKey is allocated for each available SymmetricKey Type and the set forms a
 * CipherSet for encryption purposes.
 * @author Tony Washer
 */
public class DataKey extends DataItem<DataKey> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = DataKey.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

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
    public static final JDataField FIELD_CONTROLKEY = FIELD_DEFS.declareEqualityValueField("ControlKey");

    /**
     * KeyType Field Id.
     */
    public static final JDataField FIELD_KEYTYPE = FIELD_DEFS.declareEqualityValueField("KeyType");

    /**
     * KeyDefinition Field Id.
     */
    public static final JDataField FIELD_KEYDEF = FIELD_DEFS.declareEqualityValueField("KeyDefinition");

    /**
     * Password Hash Field Id.
     */
    public static final JDataField FIELD_HASH = FIELD_DEFS.declareDerivedValueField("PasswordHash");

    /**
     * DataKey Field Id.
     */
    public static final JDataField FIELD_KEY = FIELD_DEFS.declareDerivedValueField("DataKey");

    /**
     * Cipher Field Id.
     */
    public static final JDataField FIELD_CIPHER = FIELD_DEFS.declareDerivedValueField("Cipher");

    /**
     * Encrypted Symmetric Key Length.
     */
    public static final int KEYLEN = SymmetricKey.IDSIZE;

    /**
     * The active set of values.
     */
    private ValueSet<DataKey> theValueSet;

    @Override
    public void declareValues(final ValueSet<DataKey> pValues) {
        super.declareValues(pValues);
        theValueSet = pValues;
    }

    /**
     * Get the ControlKey.
     * @return the controlKey
     */
    public ControlKey getControlKey() {
        return getControlKey(theValueSet);
    }

    /**
     * Get the Key Type.
     * @return the key type
     */
    public SymKeyType getKeyType() {
        return getKeyType(theValueSet);
    }

    /**
     * Get the Key Definition.
     * @return the key definition
     */
    public byte[] getSecuredKeyDef() {
        return getSecuredKeyDef(theValueSet);
    }

    /**
     * Get the DataKey.
     * @return the data key
     */
    protected SymmetricKey getDataKey() {
        return getDataKey(theValueSet);
    }

    /**
     * Get the Cipher.
     * @return the cipher
     */
    protected DataCipher getCipher() {
        return getCipher(theValueSet);
    }

    /**
     * Get the PasswordHash.
     * @return the passwordHash
     */
    protected PasswordHash getPasswordHash() {
        return getPasswordHash(theValueSet);
    }

    /**
     * Get the ControlKey.
     * @param pValueSet the valueSet
     * @return the control Key
     */
    public static ControlKey getControlKey(final ValueSet<DataKey> pValueSet) {
        return pValueSet.getValue(FIELD_CONTROLKEY, ControlKey.class);
    }

    /**
     * Get the Key type.
     * @param pValueSet the valueSet
     * @return the Key type
     */
    public static SymKeyType getKeyType(final ValueSet<DataKey> pValueSet) {
        return pValueSet.getValue(FIELD_KEYTYPE, SymKeyType.class);
    }

    /**
     * Get the Key Definition .
     * @param pValueSet the valueSet
     * @return the Key Definition
     */
    public static byte[] getSecuredKeyDef(final ValueSet<DataKey> pValueSet) {
        return pValueSet.getValue(FIELD_KEYDEF, byte[].class);
    }

    /**
     * Get the DataKey.
     * @param pValueSet the valueSet
     * @return the data Key
     */
    protected static SymmetricKey getDataKey(final ValueSet<DataKey> pValueSet) {
        return pValueSet.getValue(FIELD_KEY, SymmetricKey.class);
    }

    /**
     * Get the Cipher.
     * @param pValueSet the valueSet
     * @return the cipher
     */
    protected static DataCipher getCipher(final ValueSet<DataKey> pValueSet) {
        return pValueSet.getValue(FIELD_CIPHER, DataCipher.class);
    }

    /**
     * Get the PasswordHash.
     * @param pValueSet the valueSet
     * @return the passwordHash
     */
    protected static PasswordHash getPasswordHash(final ValueSet<DataKey> pValueSet) {
        return pValueSet.getValue(FIELD_HASH, PasswordHash.class);
    }

    /**
     * Set the ControlKey.
     * @param pValue the controlKey
     */
    private void setValueControlKey(final ControlKey pValue) {
        theValueSet.setValue(FIELD_CONTROLKEY, pValue);
    }

    /**
     * Set the ControlKey Id.
     * @param pId the controlKey id
     */
    private void setValueControlKey(final Integer pId) {
        theValueSet.setValue(FIELD_CONTROLKEY, pId);
    }

    /**
     * Set the Key Type.
     * @param pValue the KeyType
     */
    private void setValueKeyType(final SymKeyType pValue) {
        theValueSet.setValue(FIELD_KEYTYPE, pValue);
    }

    /**
     * Set the KeyType id.
     * @param pId the KeyType id
     */
    private void setValueKeyType(final Integer pId) {
        theValueSet.setValue(FIELD_KEYTYPE, pId);
    }

    /**
     * Set the Key Definition.
     * @param pValue the KeyDefinition
     */
    private void setValueSecuredKeyDef(final byte[] pValue) {
        theValueSet.setValue(FIELD_KEYDEF, pValue);
    }

    /**
     * Set the DataKey.
     * @param pValue the dataKey
     */
    private void setValueDataKey(final SymmetricKey pValue) {
        theValueSet.setValue(FIELD_KEY, pValue);
    }

    /**
     * Set the Cipher.
     * @param pValue the cipher
     */
    private void setValueCipher(final DataCipher pValue) {
        theValueSet.setValue(FIELD_CIPHER, pValue);
    }

    /**
     * Set the PasswordHash.
     * @param pValue the passwordHash
     */
    private void setValuePasswordHash(final PasswordHash pValue) {
        theValueSet.setValue(FIELD_HASH, pValue);
    }

    @Override
    public DataKey getBase() {
        return (DataKey) super.getBase();
    }

    /**
     * Construct a copy of a DataKey.
     * @param pList the list to add to
     * @param pSource The Key to copy
     */
    protected DataKey(final DataKeyList pList,
                      final DataKey pSource) {
        /* Set standard values */
        super(pList, pSource);

        /* Switch on the LinkStyle */
        switch (getStyle()) {
            case CLONE:
                isolateCopy(pList.getData());
            case CORE:
            case COPY:
                pList.setNewId(this);
                break;
            case EDIT:
                setBase(pSource);
                setState(DataState.CLEAN);
                break;
            case UPDATE:
                setBase(pSource);
                setState(pSource.getState());
                break;
            default:
                break;
        }
    }

    /**
     * Construct a DataKey from Database/Backup.
     * @param pList the list to add to
     * @param uId the id of the DataKey
     * @param uControlId the id of the ControlKey
     * @param uKeyTypeId the id of the KeyType
     * @param pSecurityKey the encrypted symmetric key
     * @throws JDataException on error
     */
    private DataKey(final DataKeyList pList,
                    final int uId,
                    final int uControlId,
                    final int uKeyTypeId,
                    final byte[] pSecurityKey) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Record the IDs */
            setValueControlKey(uControlId);
            setValueKeyType(uKeyTypeId);
            setValueSecuredKeyDef(pSecurityKey);

            /* Determine the SymKeyType */
            try {
                setValueKeyType(SymKeyType.fromId(uKeyTypeId));
            } catch (JDataException e) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid KeyType Id " + uKeyTypeId, e);
            }

            /* Look up the ControlKey */
            ControlKey myControlKey = pList.theData.getControlKeys().searchFor(uControlId);
            if (myControlKey == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid ControlKey Id");
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

            /* Allocate the id */
            pList.setNewId(this);

            /* Catch Exceptions */
        } catch (Exception e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
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

            /* Allocate the id */
            pList.setNewId(this);

            /* Catch Exceptions */
        } catch (Exception e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
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

        /* Protect against exceptions */
        try {
            /* Store the Control details */
            setValueControlKey(pControlKey);

            /* Copy the key details */
            setValueDataKey(pDataKey.getDataKey());
            setValueCipher(pDataKey.getCipher());
            setValueKeyType(pDataKey.getKeyType());

            /* Access Password Hash */
            PasswordHash myHash = pControlKey.getPasswordHash();

            /* Store its secured keyDef */
            setValuePasswordHash(myHash);
            CipherSet myCipher = myHash.getCipherSet();
            setValueSecuredKeyDef(myCipher.secureSymmetricKey(getDataKey()));

            /* Allocate the id */
            pList.setNewId(this);

            /* Catch Exceptions */
        } catch (Exception e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    @Override
    public int compareTo(final Object pThat) {
        int iDiff;

        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Make sure that the object is a DataKey */
        if (pThat.getClass() != this.getClass()) {
            return -1;
        }

        /* Access the object as a DataKey */
        DataKey myThat = (DataKey) pThat;

        /* Compare the IDs */
        iDiff = (int) (getId() - myThat.getId());
        if (iDiff < 0) {
            return -1;
        }
        if (iDiff > 0) {
            return 1;
        }
        return 0;
    }

    /**
     * Isolate Data Copy.
     * @param pData the DataSet
     */
    private void isolateCopy(final DataSet<?> pData) {
        ControlKeyList myKeys = pData.getControlKeys();

        /* Update to use the local copy of the ControlKeys */
        ControlKey myKey = getControlKey();
        ControlKey myNewKey = myKeys.searchFor(myKey.getId());
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
        if (checkForHistory()) {
            setState(DataState.CHANGED);
        }
    }

    /**
     * DataKey List.
     */
    public static class DataKeyList extends DataList<DataKeyList, DataKey> {
        /**
         * The owning data set.
         */
        private DataSet<?> theData = null;

        /**
         * Get the owning data set.
         * @return the data set
         */
        public DataSet<?> getData() {
            return theData;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        /**
         * Construct an empty CORE DataKey list.
         * @param pData the DataSet for the list
         */
        protected DataKeyList(final DataSet<?> pData) {
            super(DataKeyList.class, DataKey.class, ListStyle.CORE, false);
            theData = pData;
        }

        /**
         * Construct an empty generic DataKey list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected DataKeyList(final DataSet<?> pData,
                              final ListStyle pStyle) {
            super(DataKeyList.class, DataKey.class, pStyle, false);
            theData = pData;
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private DataKeyList(final DataKeyList pSource) {
            super(pSource);
            theData = pSource.theData;
        }

        /**
         * Construct an update extract for the List.
         * @param pStyle the list style
         * @return the update Extract
         */
        private DataKeyList getExtractList(final ListStyle pStyle) {
            /* Build an empty Extract List */
            DataKeyList myList = new DataKeyList(this);

            /* Obtain underlying updates */
            myList.populateList(pStyle);

            /* Return the list */
            return myList;
        }

        @Override
        public DataKeyList getUpdateList() {
            return getExtractList(ListStyle.UPDATE);
        }

        @Override
        public DataKeyList getEditList() {
            return null;
        }

        @Override
        public DataKeyList getShallowCopy() {
            return getExtractList(ListStyle.COPY);
        }

        @Override
        public DataKeyList getDeepCopy(final DataSet<?> pDataSet) {
            /* Build an empty Extract List */
            DataKeyList myList = new DataKeyList(this);
            myList.theData = pDataSet;

            /* Obtain underlying clones */
            myList.populateList(ListStyle.CLONE);
            myList.setStyle(ListStyle.CORE);

            /* Return the list */
            return myList;
        }

        @Override
        protected DataKeyList getDifferences(final DataKeyList pOld) {
            /* Build an empty Difference List */
            DataKeyList myList = new DataKeyList(this);

            /* Calculate the differences */
            myList.getDifferenceList(this, pOld);

            /* Return the list */
            return myList;
        }

        @Override
        public DataKey addNewItem(final DataItem<?> pItem) {
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
         * @param uId the id of the DataKey
         * @param uControlId the id of the ControlKey
         * @param uKeyTypeId the id of the KeyType
         * @param pSecurityKey the encrypted symmetric key
         * @return the new item
         * @throws JDataException on error
         */
        public DataKey addItem(final int uId,
                               final int uControlId,
                               final int uKeyTypeId,
                               final byte[] pSecurityKey) throws JDataException {
            DataKey myKey;

            /* Create the DataKey */
            myKey = new DataKey(this, uId, uControlId, uKeyTypeId, pSecurityKey);

            /* Check that this KeyId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myKey, "Duplicate DataKeyId (" + uId + ")");
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
        public DataKey addItem(final ControlKey pControlKey,
                               final SymKeyType pKeyType) throws JDataException {
            DataKey myKey;

            /* Create the key */
            myKey = new DataKey(this, pControlKey, pKeyType);

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
        public DataKey addItem(final ControlKey pControlKey,
                               final DataKey pDataKey) throws JDataException {
            DataKey myKey;

            /* Create the key */
            myKey = new DataKey(this, pControlKey, pDataKey);

            /* Add to the list */
            add(myKey);
            return myKey;
        }
    }
}

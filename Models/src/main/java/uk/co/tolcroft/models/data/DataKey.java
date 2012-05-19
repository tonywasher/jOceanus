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

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDataManager.ReportFields;
import net.sourceforge.JDataManager.ReportFields.ReportField;
import net.sourceforge.JGordianKnot.CipherSet;
import net.sourceforge.JGordianKnot.DataCipher;
import net.sourceforge.JGordianKnot.PasswordHash;
import net.sourceforge.JGordianKnot.SecurityGenerator;
import net.sourceforge.JGordianKnot.SymKeyType;
import net.sourceforge.JGordianKnot.SymmetricKey;
import uk.co.tolcroft.models.data.ControlKey.ControlKeyList;

public class DataKey extends DataItem<DataKey> {
    /**
     * Object name
     */
    public static String objName = DataKey.class.getSimpleName();

    /**
     * List name
     */
    public static String listName = objName + "s";

    /**
     * Report fields
     */
    private static final ReportFields theLocalFields = new ReportFields(objName, DataItem.theLocalFields);

    /* Called from constructor */
    @Override
    public ReportFields declareFields() {
        return theLocalFields;
    }

    /* Field IDs */
    public static final ReportField FIELD_CONTROLKEY = theLocalFields.declareEqualityValueField("ControlKey");
    public static final ReportField FIELD_KEYTYPE = theLocalFields.declareEqualityValueField("KeyType");
    public static final ReportField FIELD_KEYDEF = theLocalFields.declareEqualityValueField("KeyDefinition");
    public static final ReportField FIELD_HASH = theLocalFields.declareDerivedValueField("PasswordHash");
    public static final ReportField FIELD_KEY = theLocalFields.declareDerivedValueField("DataKey");
    public static final ReportField FIELD_CIPHER = theLocalFields.declareDerivedValueField("Cipher");

    /**
     * Encrypted Symmetric Key Length
     */
    public final static int KEYLEN = SymmetricKey.IDSIZE;

    /**
     * The active set of values
     */
    private ValueSet<DataKey> theValueSet;

    @Override
    public void declareValues(ValueSet<DataKey> pValues) {
        super.declareValues(pValues);
        theValueSet = pValues;
    }

    /* Access methods */
    public ControlKey getControlKey() {
        return getControlKey(theValueSet);
    }

    public SymKeyType getKeyType() {
        return getKeyType(theValueSet);
    }

    public byte[] getSecuredKeyDef() {
        return getSecuredKeyDef(theValueSet);
    }

    protected SymmetricKey getDataKey() {
        return getDataKey(theValueSet);
    }

    protected DataCipher getCipher() {
        return getCipher(theValueSet);
    }

    protected PasswordHash getPasswordHash() {
        return getPasswordHash(theValueSet);
    }

    public static ControlKey getControlKey(ValueSet<DataKey> pValueSet) {
        return pValueSet.getValue(FIELD_CONTROLKEY, ControlKey.class);
    }

    public static SymKeyType getKeyType(ValueSet<DataKey> pValueSet) {
        return pValueSet.getValue(FIELD_KEYTYPE, SymKeyType.class);
    }

    public static byte[] getSecuredKeyDef(ValueSet<DataKey> pValueSet) {
        return pValueSet.getValue(FIELD_KEYDEF, byte[].class);
    }

    protected static SymmetricKey getDataKey(ValueSet<DataKey> pValueSet) {
        return pValueSet.getValue(FIELD_KEY, SymmetricKey.class);
    }

    protected static DataCipher getCipher(ValueSet<DataKey> pValueSet) {
        return pValueSet.getValue(FIELD_CIPHER, DataCipher.class);
    }

    protected static PasswordHash getPasswordHash(ValueSet<DataKey> pValueSet) {
        return pValueSet.getValue(FIELD_HASH, PasswordHash.class);
    }

    private void setValueControlKey(ControlKey pKey) {
        theValueSet.setValue(FIELD_CONTROLKEY, pKey);
    }

    private void setValueControlKey(Integer pId) {
        theValueSet.setValue(FIELD_CONTROLKEY, pId);
    }

    private void setValueKeyType(SymKeyType pKeyType) {
        theValueSet.setValue(FIELD_KEYTYPE, pKeyType);
    }

    private void setValueKeyType(Integer pId) {
        theValueSet.setValue(FIELD_KEYTYPE, pId);
    }

    private void setValueSecuredKeyDef(byte[] pKeyDef) {
        theValueSet.setValue(FIELD_KEYDEF, pKeyDef);
    }

    private void setValueDataKey(SymmetricKey pKey) {
        theValueSet.setValue(FIELD_KEY, pKey);
    }

    private void setValueCipher(DataCipher pCipher) {
        theValueSet.setValue(FIELD_CIPHER, pCipher);
    }

    private void setValuePasswordHash(PasswordHash pHash) {
        theValueSet.setValue(FIELD_HASH, pHash);
    }

    /* Linking methods */
    @Override
    public DataKey getBase() {
        return (DataKey) super.getBase();
    }

    /**
     * Construct a copy of a DataKey
     * @param pList the list to add to
     * @param pSource The Key to copy
     */
    protected DataKey(DataKeyList pList,
                      DataKey pSource) {
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
        }
    }

    /**
     * Construct a DataKey from Database/Backup
     * @param pList the list to add to
     * @param uId the id of the DataKey
     * @param uControlId the id of the ControlKey
     * @param uKeyTypeId the id of the KeyType
     * @param pSecurityKey the encrypted symmetric key
     * @throws ModelException
     */
    private DataKey(DataKeyList pList,
                    int uId,
                    int uControlId,
                    int uKeyTypeId,
                    byte[] pSecurityKey) throws ModelException {
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
            } catch (ModelException e) {
                throw new ModelException(ExceptionClass.DATA, this, "Invalid KeyType Id " + uKeyTypeId);
            }

            /* Look up the ControlKey */
            ControlKey myControlKey = pList.theData.getControlKeys().searchFor(uControlId);
            if (myControlKey == null)
                throw new ModelException(ExceptionClass.DATA, this, "Invalid ControlKey Id");

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
        }

        /* Catch Exceptions */
        catch (Exception e) {
            /* Pass on exception */
            throw new ModelException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Constructor for a new DataKey in a new ControlKey set
     * @param pList the list to add to
     * @param pControlKey the ControlKey to which this key belongs
     * @param pKeyType the Key type of the new key
     * @throws ModelException
     */
    private DataKey(DataKeyList pList,
                    ControlKey pControlKey,
                    SymKeyType pKeyType) throws ModelException {
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
        }

        /* Catch Exceptions */
        catch (Exception e) {
            /* Pass on exception */
            throw new ModelException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Constructor for a cloned DataKey in a new ControlKey set
     * @param pList the list to add to
     * @param pControlKey the ControlKey to which this key belongs
     * @param pDataKey the DataKey to clone
     * @throws ModelException
     */
    private DataKey(DataKeyList pList,
                    ControlKey pControlKey,
                    DataKey pDataKey) throws ModelException {
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
        }

        /* Catch Exceptions */
        catch (Exception e) {
            /* Pass on exception */
            throw new ModelException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    @Override
    public int compareTo(Object pThat) {
        int iDiff;

        /* Handle the trivial cases */
        if (this == pThat)
            return 0;
        if (pThat == null)
            return -1;

        /* Make sure that the object is a DataKey */
        if (pThat.getClass() != this.getClass())
            return -1;

        /* Access the object as a DataKey */
        DataKey myThat = (DataKey) pThat;

        /* Compare the IDs */
        iDiff = (int) (getId() - myThat.getId());
        if (iDiff < 0)
            return -1;
        if (iDiff > 0)
            return 1;
        return 0;
    }

    /**
     * Isolate Data Copy
     * @param pData the DataSet
     */
    private void isolateCopy(DataSet<?> pData) {
        ControlKeyList myKeys = pData.getControlKeys();

        /* Update to use the local copy of the ControlKeys */
        ControlKey myKey = getControlKey();
        ControlKey myNewKey = myKeys.searchFor(myKey.getId());
        setValueControlKey(myNewKey);

        /* Register the Key */
        myNewKey.registerDataKey(this);
    }

    /**
     * Update password hash
     * @throws ModelException
     */
    protected void updatePasswordHash() throws ModelException {
        /* Store the current detail into history */
        pushHistory();

        /* Update the Security Control Key and obtain the new secured KeyDef */
        ControlKey myControlKey = getControlKey();
        PasswordHash myHash = myControlKey.getPasswordHash();
        CipherSet myCipher = myHash.getCipherSet();
        setValuePasswordHash(myHash);
        setValueSecuredKeyDef(myCipher.secureSymmetricKey(getDataKey()));

        /* Check for changes */
        if (checkForHistory())
            setState(DataState.CHANGED);
    }

    /**
     * DataKey List
     */
    public static class DataKeyList extends DataList<DataKeyList, DataKey> {
        /* Members */
        private DataSet<?> theData = null;

        public DataSet<?> getData() {
            return theData;
        }

        @Override
        public String listName() {
            return listName;
        }

        /**
         * Construct an empty CORE DataKey list
         * @param pData the DataSet for the list
         */
        protected DataKeyList(DataSet<?> pData) {
            super(DataKeyList.class, DataKey.class, ListStyle.CORE, false);
            theData = pData;
        }

        /**
         * Construct an empty generic DataKey list
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected DataKeyList(DataSet<?> pData,
                              ListStyle pStyle) {
            super(DataKeyList.class, DataKey.class, pStyle, false);
            theData = pData;
        }

        /**
         * Constructor for a cloned List
         * @param pSource the source List
         */
        private DataKeyList(DataKeyList pSource) {
            super(pSource);
            theData = pSource.theData;
        }

        /**
         * Construct an update extract for the List.
         * @param pStyle the list style
         * @return the update Extract
         */
        private DataKeyList getExtractList(ListStyle pStyle) {
            /* Build an empty Extract List */
            DataKeyList myList = new DataKeyList(this);

            /* Obtain underlying updates */
            myList.populateList(pStyle);

            /* Return the list */
            return myList;
        }

        /* Obtain extract lists. */
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
        public DataKeyList getDeepCopy(DataSet<?> pDataSet) {
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
        protected DataKeyList getDifferences(DataKeyList pOld) {
            /* Build an empty Difference List */
            DataKeyList myList = new DataKeyList(this);

            /* Calculate the differences */
            myList.getDifferenceList(this, pOld);

            /* Return the list */
            return myList;
        }

        @Override
        public DataKey addNewItem(DataItem<?> pItem) {
            DataKey myKey = new DataKey(this, (DataKey) pItem);
            add(myKey);
            return myKey;
        }

        @Override
        public DataKey addNewItem() {
            return null;
        }

        /**
         * Add a DataKey from Database/Backup
         * @param uId the id of the DataKey
         * @param uControlId the id of the ControlKey
         * @param uKeyTypeId the id of the KeyType
         * @param pSecurityKey the encrypted symmetric key
         * @return the new item
         * @throws ModelException
         */
        public DataKey addItem(int uId,
                               int uControlId,
                               int uKeyTypeId,
                               byte[] pSecurityKey) throws ModelException {
            DataKey myKey;

            /* Create the DataKey */
            myKey = new DataKey(this, uId, uControlId, uKeyTypeId, pSecurityKey);

            /* Check that this KeyId has not been previously added */
            if (!isIdUnique(uId))
                throw new ModelException(ExceptionClass.DATA, myKey, "Duplicate DataKeyId (" + uId + ")");

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Add a new DataKey for the passed ControlKey
         * @param pControlKey the ControlKey
         * @param pKeyType the KeyType
         * @return the new DataKey
         * @throws ModelException
         */
        public DataKey addItem(ControlKey pControlKey,
                               SymKeyType pKeyType) throws ModelException {
            DataKey myKey;

            /* Create the key */
            myKey = new DataKey(this, pControlKey, pKeyType);

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Add a clone of the passed DataKey for the passed ControlKey
         * @param pControlKey the ControlKey
         * @param pDataKey the DataKey
         * @return the new DataKey
         * @throws ModelException
         */
        public DataKey addItem(ControlKey pControlKey,
                               DataKey pDataKey) throws ModelException {
            DataKey myKey;

            /* Create the key */
            myKey = new DataKey(this, pControlKey, pDataKey);

            /* Add to the list */
            add(myKey);
            return myKey;
        }
    }
}

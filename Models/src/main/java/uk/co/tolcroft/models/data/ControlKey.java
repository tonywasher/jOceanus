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

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JGordianKnot.CipherSet;
import net.sourceforge.JGordianKnot.EncryptionGenerator;
import net.sourceforge.JGordianKnot.HashMode;
import net.sourceforge.JGordianKnot.PasswordHash;
import net.sourceforge.JGordianKnot.SecureManager;
import net.sourceforge.JGordianKnot.SecurityGenerator;
import net.sourceforge.JGordianKnot.SymKeyType;
import uk.co.tolcroft.models.data.DataKey.DataKeyList;

/**
 * ControlKey definition and list. The Control Key represents the passwordHash that controls securing of the
 * dataKeys. It maintains a map of the associated DataKeys.
 * @author Tony Washer
 */
public class ControlKey extends DataItem implements Comparable<ControlKey> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = ControlKey.class.getSimpleName();

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
     * Field ID for passwordHash.
     */
    public static final JDataField FIELD_PASSHASH = FIELD_DEFS.declareEqualityValueField("PasswordHash");

    /**
     * Field ID for HashMode.
     */
    public static final JDataField FIELD_HASHMODE = FIELD_DEFS.declareDerivedValueField("HashMode");

    /**
     * Field ID for HashBytes.
     */
    public static final JDataField FIELD_HASHBYTES = FIELD_DEFS.declareDerivedValueField("HashBytes");

    /**
     * Field ID for DataKeyMap.
     */
    public static final JDataField FIELD_MAP = FIELD_DEFS.declareLocalField("DataKeyMap");

    /**
     * Field ID for CipherSet.
     */
    public static final JDataField FIELD_CIPHER = FIELD_DEFS.declareLocalField("CipherSet");

    /**
     * PasswordHash Length.
     */
    public static final int HASHLEN = PasswordHash.HASHSIZE;

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

    /**
     * The active set of values.
     */
    private ValueSet theValueSet;

    @Override
    public void declareValues(final ValueSet pValues) {
        super.declareValues(pValues);
        theValueSet = pValues;
    }

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
     * Get the HashBytes.
     * @return the hash bytes
     */
    public byte[] getHashBytes() {
        return getHashBytes(theValueSet);
    }

    /**
     * Get the PassWordHash.
     * @return the passwordHash
     */
    protected PasswordHash getPasswordHash() {
        return getPasswordHash(theValueSet);
    }

    /**
     * Get the HashMode.
     * @return the hash mode
     */
    private HashMode getHashMode() {
        return getHashMode(theValueSet);
    }

    /**
     * Get the HashBytes for the valueSet.
     * @param pValueSet the ValueSet
     * @return the hash bytes
     */
    public static byte[] getHashBytes(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_HASHBYTES, byte[].class);
    }

    /**
     * Get the PasswordHash for the valueSet.
     * @param pValueSet the ValueSet
     * @return the passwordHash
     */
    protected static PasswordHash getPasswordHash(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PASSHASH, PasswordHash.class);
    }

    /**
     * Get the HashMode for the valueSet.
     * @param pValueSet the ValueSet
     * @return the hash mode
     */
    private static HashMode getHashMode(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_HASHMODE, HashMode.class);
    }

    /**
     * Get the Encryption Field Generator.
     * @return the field generator
     */
    public EncryptionGenerator getFieldGenerator() {
        return theFieldGenerator;
    }

    /**
     * Set the PasswordHash.
     * @param pValue the PasswordHash
     */
    private void setValuePasswordHash(final PasswordHash pValue) {
        theValueSet.setValue(FIELD_PASSHASH, pValue);
        setValueHashMode((pValue == null) ? null : pValue.getHashMode());
        setValueHashBytes((pValue == null) ? null : pValue.getHashBytes());
    }

    /**
     * Set the Hash Bytes.
     * @param pValue the Hash bytes
     */
    private void setValueHashBytes(final byte[] pValue) {
        theValueSet.setValue(FIELD_HASHBYTES, pValue);
    }

    /**
     * Set the Hash Mode.
     * @param pValue the Hash Mode
     */
    private void setValueHashMode(final HashMode pValue) {
        theValueSet.setValue(FIELD_HASHMODE, pValue);
    }

    @Override
    public ControlKey getBase() {
        return (ControlKey) super.getBase();
    }

    /**
     * Construct a copy of a ControlKey.
     * @param pList the list the copy belongs to
     * @param pSource The Key to copy
     */
    protected ControlKey(final ControlKeyList pList,
                         final ControlKey pSource) {
        /* Set standard values */
        super(pList, pSource);

        /* Switch on the LinkStyle */
        switch (getStyle()) {
            case CLONE:
                theSecurityGenerator = pSource.theSecurityGenerator;
                theMap = new EnumMap<SymKeyType, DataKey>(SymKeyType.class);
                theCipherSet = new CipherSet(theSecurityGenerator, getHashMode());
                theFieldGenerator = new EncryptionGenerator(theCipherSet);
                break;
            default:
                break;
        }
    }

    /**
     * Constructor for loading an encrypted ControlKey.
     * @param pList the list to which to add the key to
     * @param uId the id of the ControlKey
     * @param pHashBytes the hash bytes
     * @throws JDataException on error
     */
    private ControlKey(final ControlKeyList pList,
                       final int uId,
                       final byte[] pHashBytes) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Store the details */
            setValueHashBytes(pHashBytes);

            /* Access the Security manager */
            DataSet<?> myData = getDataSet();
            SecureManager mySecure = myData.getSecurity();

            /* Record the security generator */
            theSecurityGenerator = mySecure.getSecurityGenerator();

            /* Resolve the password hash */
            PasswordHash myHash = mySecure.resolvePasswordHash(pHashBytes, "Database");

            /* Store the password hash */
            setValuePasswordHash(myHash);

            /* Create the DataKey Map */
            theMap = new EnumMap<SymKeyType, DataKey>(SymKeyType.class);

            /* Create the CipherSet */
            theCipherSet = new CipherSet(theSecurityGenerator, getHashMode());
            theFieldGenerator = new EncryptionGenerator(theCipherSet);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Constructor for a new ControlKey. This will create a set of DataKeys.
     * @param pList the list to which to add the key to
     * @throws JDataException on error
     */
    private ControlKey(final ControlKeyList pList) throws JDataException {
        /* Initialise the item */
        super(pList, 0);

        /* Protect against exceptions */
        try {
            /* Access the Security manager */
            DataSet<?> myData = getDataSet();
            SecureManager mySecure = myData.getSecurity();

            /* Record the security generator */
            theSecurityGenerator = mySecure.getSecurityGenerator();

            /* Create a new password hash */
            PasswordHash myHash = mySecure.resolvePasswordHash(null, "Database");

            /* Store the password hash */
            setValuePasswordHash(myHash);

            /* Create the DataKey Map */
            theMap = new EnumMap<SymKeyType, DataKey>(SymKeyType.class);

            /* Create the CipherSet */
            theCipherSet = new CipherSet(theSecurityGenerator, getHashMode());
            theFieldGenerator = new EncryptionGenerator(theCipherSet);

            /* Allocate the DataKeys */
            allocateDataKeys(myData);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Constructor for a new ControlKey with the same password. This will create a set of DataKeys.
     * @param pKey the key to copy
     * @throws JDataException on error
     */
    private ControlKey(final ControlKey pKey) throws JDataException {
        /* Initialise the item */
        super(pKey.getList(), 0);

        /* Protect against exceptions */
        try {
            /* Access the Security manager */
            DataSet<?> myData = getDataSet();
            SecureManager mySecure = myData.getSecurity();

            /* Record the security generator */
            theSecurityGenerator = mySecure.getSecurityGenerator();

            /* Create a clone of the password hash */
            PasswordHash myHash = mySecure.clonePasswordHash(myData.getPasswordHash());

            /* Store the password Hash */
            setValuePasswordHash(myHash);

            /* Create the DataKey Map */
            theMap = new EnumMap<SymKeyType, DataKey>(SymKeyType.class);

            /* Create the CipherSet */
            theCipherSet = new CipherSet(theSecurityGenerator, getHashMode());
            theFieldGenerator = new EncryptionGenerator(theCipherSet);

            /* Allocate the DataKeys */
            allocateDataKeys(myData);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    @Override
    public int compareTo(final ControlKey pThat) {
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

    /**
     * Allocate a new set of DataKeys.
     * @param pData the DataSet
     * @throws JDataException on error
     */
    private void allocateDataKeys(final DataSet<?> pData) throws JDataException {
        /* Access the DataKey List */
        DataKeyList myKeys = pData.getDataKeys();

        /* Loop through the SymKeyType values */
        for (SymKeyType myType : SymKeyType.values()) {
            /* Create a new DataKey for this ControlKey */
            DataKey myKey = myKeys.addItem(this, myType);

            /* Store the DataKey into the map */
            theMap.put(myType, myKey);

            /* Declare the Cipher */
            theCipherSet.addCipher(myKey.getCipher());
        }
    }

    /**
     * Delete the old set of ControlKey and DataKeys.
     */
    private void deleteControlSet() {
        /* Loop through the SymKeyType values */
        for (SymKeyType myType : SymKeyType.values()) {
            /* Access the Data Key */
            DataKey myKey = theMap.get(myType);

            /* Mark as deleted */
            if (myKey != null) {
                myKey.setDeleted(true);
            }
        }

        /* Mark this control key as deleted */
        setDeleted(true);
    }

    /**
     * Update password hash.
     * @param pHash the new password hash
     * @throws JDataException on error
     */
    protected void updatePasswordHash(final PasswordHash pHash) throws JDataException {
        /* Store the current detail into history */
        pushHistory();

        /* Update the password hash */
        setValuePasswordHash(pHash);

        /* Loop through the SymKeyType values */
        for (SymKeyType myType : SymKeyType.values()) {
            /* Access the Data Key */
            DataKey myKey = theMap.get(myType);

            /* Update the password hash */
            if (myKey != null) {
                myKey.updatePasswordHash();
            }
        }

        /* Check for changes */
        checkForHistory();
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
     * ControlKey List.
     */
    public static class ControlKeyList extends DataList<ControlKeyList, ControlKey> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ControlKeyList.class.getSimpleName(),
                DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        /**
         * Construct an empty CORE ControlKey list.
         * @param pData the DataSet for the list
         */
        protected ControlKeyList(final DataSet<?> pData) {
            super(ControlKeyList.class, ControlKey.class, pData, ListStyle.CORE);
        }

        /**
         * Construct an empty generic ControlKey list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected ControlKeyList(final DataSet<?> pData,
                                 final ListStyle pStyle) {
            super(ControlKeyList.class, ControlKey.class, pData, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private ControlKeyList(final ControlKeyList pSource) {
            super(pSource);
        }

        @Override
        protected ControlKeyList getEmptyList() {
            return new ControlKeyList(this);
        }

        @Override
        public ControlKey addNewItem(final DataItem pItem) {
            /* Can only clone a ControlKey */
            if (!(pItem instanceof ControlKey)) {
                return null;
            }

            /* Clone the control key */
            ControlKey myKey = new ControlKey(this, (ControlKey) pItem);
            add(myKey);
            return myKey;
        }

        @Override
        public ControlKey addNewItem() {
            return null;
        }

        /**
         * Add a ControlKey item from a Database/Backup.
         * @param uId the id of the ControlKey
         * @param pHashBytes the HashBytes
         * @return the new item
         * @throws JDataException on error
         */
        public ControlKey addItem(final int uId,
                                  final byte[] pHashBytes) throws JDataException {
            /* Create the ControlKey */
            ControlKey myKey = new ControlKey(this, uId, pHashBytes);

            /* Check that this KeyId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myKey, "Duplicate ControlKeyId (" + uId + ")");
            }

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Add a new ControlKey (with associated DataKeys).
         * @return the new item
         * @throws JDataException on error
         */
        public ControlKey addItem() throws JDataException {
            /* Create the key */
            ControlKey myKey = new ControlKey(this);

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Add a cloned ControlKey (with associated DataKeys).
         * @param pSource the source key
         * @return the new item
         * @throws JDataException on error
         */
        public ControlKey addItem(final ControlKey pSource) throws JDataException {
            /* Check that we are the same list */
            if (pSource.getList() != this) {
                throw new JDataException(ExceptionClass.LOGIC, "Invalid clone operation");
            }

            /* Create the key */
            ControlKey myKey = new ControlKey(pSource);

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Initialise Security from a DataBase for a SpreadSheet load.
         * @param pDatabase the DataSet for the Database
         * @throws JDataException on error
         */
        protected void initialiseSecurity(final DataSet<?> pDatabase) throws JDataException {
            /* Access the active control key from the database */
            DataSet<?> myData = getDataSet();
            ControlKey myDatabaseKey = pDatabase.getControlKey();
            ControlKey myKey;

            /* If we have an existing security key */
            if (myDatabaseKey != null) {
                /* Clone the Control Key and its DataKeys */
                myKey = cloneControlKey(myDatabaseKey);

                /* else create a new security set */
            } else {
                /* Create the new security set */
                myKey = addItem();
            }

            /* Declare the Control Key */
            myData.getControl().setControlKey(myKey);
        }

        /**
         * Delete old controlKeys.
         */
        protected void purgeOldControlKeys() {
            /* Access the current control Key */
            DataSet<?> myData = getDataSet();
            ControlKey myKey = myData.getControlKey();

            /* Loop through the controlKeys */
            Iterator<ControlKey> myIterator = iterator();
            ControlKey myCurr;
            while ((myCurr = myIterator.next()) != null) {
                /* Delete if this is not the active key */
                if (!myKey.equals(myCurr)) {
                    myCurr.deleteControlSet();
                }
            }
        }

        /**
         * Clone Security from a DataBase.
         * @param pControlKey the ControlKey to clone
         * @return the new control key
         * @throws JDataException on error
         */
        private ControlKey cloneControlKey(final ControlKey pControlKey) throws JDataException {
            /* Clone the control key */
            ControlKey myControl = addItem(pControlKey.getId(), pControlKey.getHashBytes());

            /* Access the DataKey List */
            DataSet<?> myData = getDataSet();
            DataKeyList myKeys = myData.getDataKeys();

            /* Loop through the SymKeyType values */
            for (SymKeyType myType : SymKeyType.values()) {
                /* Access the source Data key */
                DataKey mySrcKey = pControlKey.theMap.get(myType);

                /* Create a new DataKey for this ControlKey */
                DataKey myKey = myKeys.addItem(myControl, mySrcKey);

                /* Store the DataKey into the map */
                myControl.theMap.put(myType, myKey);

                /* Declare the Cipher */
                myControl.theCipherSet.addCipher(myKey.getCipher());
            }

            /* return the cloned key */
            return myControl;
        }
    }
}

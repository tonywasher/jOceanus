/*******************************************************************************
 * jDataModels: Data models
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
package net.sourceforge.joceanus.jdatamodels.data;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.EncryptionGenerator;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdatamanager.ValueSet;
import net.sourceforge.joceanus.jdatamodels.data.DataKey.DataKeyList;
import net.sourceforge.joceanus.jgordianknot.crypto.CipherSet;
import net.sourceforge.joceanus.jgordianknot.crypto.HashKey;
import net.sourceforge.joceanus.jgordianknot.crypto.PasswordHash;
import net.sourceforge.joceanus.jgordianknot.crypto.SecureManager;
import net.sourceforge.joceanus.jgordianknot.crypto.SecurityGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.SymKeyType;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * ControlKey definition and list. The Control Key represents the passwordHash that controls securing of the dataKeys. It maintains a map of the associated
 * DataKeys.
 * @author Tony Washer
 */
public class ControlKey
        extends DataItem
        implements Comparable<ControlKey> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(ControlKey.class.getName());

    /**
     * Object name.
     */
    public static final String OBJECT_NAME = ControlKey.class.getSimpleName();

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
     * Field ID for passwordHash.
     */
    public static final JDataField FIELD_PASSHASH = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataHash"));

    /**
     * Field ID for HashKey.
     */
    public static final JDataField FIELD_HASHKEY = FIELD_DEFS.declareDerivedValueField(NLS_BUNDLE.getString("DataKey"));

    /**
     * Field ID for HashBytes.
     */
    public static final JDataField FIELD_HASHBYTES = FIELD_DEFS.declareDerivedValueField(NLS_BUNDLE.getString("DataBytes"));

    /**
     * Field ID for DataKeyMap.
     */
    public static final JDataField FIELD_MAP = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataKeyMap"));

    /**
     * Field ID for CipherSet.
     */
    public static final JDataField FIELD_CIPHER = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataCipher"));

    /**
     * Name of Database.
     */
    public static final String NAME_DATABASE = NLS_BUNDLE.getString("NameDataBase");

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
        return getHashBytes(getValueSet());
    }

    /**
     * Get the PassWordHash.
     * @return the passwordHash
     */
    protected PasswordHash getPasswordHash() {
        return getPasswordHash(getValueSet());
    }

    /**
     * Get the HashKey.
     * @return the hash key
     */
    private HashKey getHashKey() {
        return getHashKey(getValueSet());
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
     * Get the HashKey for the valueSet.
     * @param pValueSet the ValueSet
     * @return the hash mode
     */
    private static HashKey getHashKey(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_HASHKEY, HashKey.class);
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
        getValueSet().setValue(FIELD_PASSHASH, pValue);
        setValueHashKey((pValue == null)
                ? null
                : pValue.getHashKey());
        setValueHashBytes((pValue == null)
                ? null
                : pValue.getHashBytes());
    }

    /**
     * Set the Hash Bytes.
     * @param pValue the Hash bytes
     */
    private void setValueHashBytes(final byte[] pValue) {
        getValueSet().setValue(FIELD_HASHBYTES, pValue);
    }

    /**
     * Set the Hash Key.
     * @param pValue the Hash Key
     */
    private void setValueHashKey(final HashKey pValue) {
        getValueSet().setValue(FIELD_HASHKEY, pValue);
    }

    @Override
    public ControlKey getBase() {
        return (ControlKey) super.getBase();
    }

    @Override
    public ControlKeyList getList() {
        return (ControlKeyList) super.getList();
    }

    /**
     * Copy Constructor.
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
                theCipherSet = new CipherSet(theSecurityGenerator, getHashKey());
                theFieldGenerator = new EncryptionGenerator(theCipherSet, getDataSet().getDataFormatter());
                break;
            default:
                break;
        }
    }

    /**
     * Secure Constructor.
     * @param pList the list to which to add the key to
     * @param pId the id of the ControlKey
     * @param pHashBytes the hash bytes
     * @throws JOceanusException on error
     */
    private ControlKey(final ControlKeyList pList,
                       final Integer pId,
                       final byte[] pHashBytes) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Store the details */
            setValueHashBytes(pHashBytes);

            /* Access the Security manager */
            DataSet<?, ?> myData = getDataSet();
            SecureManager mySecure = myData.getSecurity();
            JDataFormatter myFormatter = myData.getDataFormatter();

            /* Record the security generator */
            theSecurityGenerator = mySecure.getSecurityGenerator();

            /* Resolve the password hash */
            PasswordHash myHash = mySecure.resolvePasswordHash(pHashBytes, NAME_DATABASE);

            /* Store the password hash */
            setValuePasswordHash(myHash);

            /* Create the DataKey Map */
            theMap = new EnumMap<SymKeyType, DataKey>(SymKeyType.class);

            /* Create the CipherSet and security generator */
            theCipherSet = new CipherSet(theSecurityGenerator, getHashKey());
            theFieldGenerator = new EncryptionGenerator(theCipherSet, myFormatter);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JOceanusException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Constructor for a new ControlKey. This will create a set of DataKeys.
     * @param pList the list to which to add the key to
     * @throws JOceanusException on error
     */
    private ControlKey(final ControlKeyList pList) throws JOceanusException {
        /* Initialise the item */
        super(pList, 0);

        /* Protect against exceptions */
        try {
            /* Access the Security manager */
            DataSet<?, ?> myData = getDataSet();
            SecureManager mySecure = myData.getSecurity();
            JDataFormatter myFormatter = myData.getDataFormatter();

            /* Record the security generator */
            theSecurityGenerator = mySecure.getSecurityGenerator();

            /* Create a new password hash */
            PasswordHash myHash = mySecure.resolvePasswordHash(null, NAME_DATABASE);

            /* Store the password hash */
            setValuePasswordHash(myHash);

            /* Create the DataKey Map */
            theMap = new EnumMap<SymKeyType, DataKey>(SymKeyType.class);

            /* Create the CipherSet */
            theCipherSet = new CipherSet(theSecurityGenerator, getHashKey());
            theFieldGenerator = new EncryptionGenerator(theCipherSet, myFormatter);

            /* Allocate the DataKeys */
            allocateDataKeys(myData);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JOceanusException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Constructor for a new ControlKey with the same password. This will create a set of DataKeys.
     * @param pKey the key to copy
     * @throws JOceanusException on error
     */
    private ControlKey(final ControlKey pKey) throws JOceanusException {
        /* Initialise the item */
        super(pKey.getList(), 0);

        /* Protect against exceptions */
        try {
            /* Access the Security manager */
            DataSet<?, ?> myData = getDataSet();
            SecureManager mySecure = myData.getSecurity();
            JDataFormatter myFormatter = myData.getDataFormatter();

            /* Record the security generator */
            theSecurityGenerator = mySecure.getSecurityGenerator();

            /* ReSeed the security generator */
            theSecurityGenerator.reSeedRandom();

            /* Create a clone of the password hash */
            PasswordHash myHash = mySecure.clonePasswordHash(myData.getPasswordHash());

            /* Store the password Hash */
            setValuePasswordHash(myHash);

            /* Create the DataKey Map */
            theMap = new EnumMap<SymKeyType, DataKey>(SymKeyType.class);

            /* Create the CipherSet */
            theCipherSet = new CipherSet(theSecurityGenerator, getHashKey());
            theFieldGenerator = new EncryptionGenerator(theCipherSet, myFormatter);

            /* Allocate the DataKeys */
            allocateDataKeys(myData);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JOceanusException(this, ERROR_CREATEITEM, e);
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
     * @throws JOceanusException on error
     */
    private void allocateDataKeys(final DataSet<?, ?> pData) throws JOceanusException {
        /* Access the DataKey List */
        DataKeyList myKeys = pData.getDataKeys();
        setNewVersion();

        /* Loop through the SymKeyType values */
        for (SymKeyType myType : SymKeyType.values()) {
            /* Create a new DataKey for this ControlKey */
            DataKey myKey = myKeys.createNewKey(this, myType);
            myKey.setNewVersion();

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
     * @throws JOceanusException on error
     */
    protected void updatePasswordHash(final PasswordHash pHash) throws JOceanusException {
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
    public static class ControlKeyList
            extends DataList<ControlKey> {
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
         * Construct an empty CORE ControlKey list.
         * @param pData the DataSet for the list
         */
        protected ControlKeyList(final DataSet<?, ?> pData) {
            super(ControlKey.class, pData, ListStyle.CORE);
        }

        /**
         * Construct an empty generic ControlKey list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected ControlKeyList(final DataSet<?, ?> pData,
                                 final ListStyle pStyle) {
            super(ControlKey.class, pData, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private ControlKeyList(final ControlKeyList pSource) {
            super(pSource);
        }

        @Override
        protected ControlKeyList getEmptyList(final ListStyle pStyle) {
            ControlKeyList myList = new ControlKeyList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public ControlKeyList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (ControlKeyList) super.cloneList(pDataSet);
        }

        @Override
        public ControlKeyList deriveList(final ListStyle pStyle) throws JOceanusException {
            return (ControlKeyList) super.deriveList(pStyle);
        }

        @Override
        public ControlKeyList deriveDifferences(final DataSet<?, ?> pDataSet,
                                                final DataList<?> pOld) {
            return (ControlKeyList) super.deriveDifferences(pDataSet, pOld);
        }

        @Override
        public ControlKey addCopyItem(final DataItem pItem) {
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
         * Add a ControlKey item from a secure store.
         * @param pId the id of the ControlKey
         * @param pHashBytes the HashBytes
         * @return the new item
         * @throws JOceanusException on error
         */
        public ControlKey addSecureItem(final Integer pId,
                                        final byte[] pHashBytes) throws JOceanusException {
            /* Create the ControlKey */
            ControlKey myKey = new ControlKey(this, pId, pHashBytes);

            /* Check that this KeyId has not been previously added */
            if (!isIdUnique(pId)) {
                myKey.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JOceanusException(myKey, ERROR_DUPLICATE);
            }

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Create a new ControlKey (with associated DataKeys).
         * @return the new item
         * @throws JOceanusException on error
         */
        public ControlKey createNewKeySet() throws JOceanusException {
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
         * @throws JOceanusException on error
         */
        public ControlKey cloneItem(final ControlKey pSource) throws JOceanusException {
            /* Create the key */
            ControlKey myKey = new ControlKey(pSource);

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Initialise Security from a DataBase for a SpreadSheet load.
         * @param pDatabase the DataSet for the Database
         * @throws JOceanusException on error
         */
        protected void initialiseSecurity(final DataSet<?, ?> pDatabase) throws JOceanusException {
            /* Access the active control key from the database */
            DataSet<?, ?> myData = getDataSet();
            ControlKey myDatabaseKey = pDatabase.getControlKey();
            ControlKey myKey;

            /* If we have an existing security key */
            if (myDatabaseKey != null) {
                /* Clone the Control Key and its DataKeys */
                myKey = cloneControlKey(myDatabaseKey);

                /* else create a new security set */
            } else {
                /* Create the new security set */
                myKey = createNewKeySet();
            }

            /* Declare the Control Key */
            myData.getControl().setControlKey(myKey);
        }

        /**
         * Delete old controlKeys.
         */
        protected void purgeOldControlKeys() {
            /* Access the current control Key */
            DataSet<?, ?> myData = getDataSet();
            ControlKey myKey = myData.getControlKey();

            /* Loop through the controlKeys */
            Iterator<ControlKey> myIterator = iterator();
            while (myIterator.hasNext()) {
                ControlKey myCurr = myIterator.next();

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
         * @throws JOceanusException on error
         */
        private ControlKey cloneControlKey(final ControlKey pControlKey) throws JOceanusException {
            /* Clone the control key */
            ControlKey myControl = addSecureItem(pControlKey.getId(), pControlKey.getHashBytes());

            /* Access the DataKey List */
            DataSet<?, ?> myData = getDataSet();
            DataKeyList myKeys = myData.getDataKeys();

            /* Loop through the SymKeyType values */
            for (SymKeyType myType : SymKeyType.values()) {
                /* Access the source Data key */
                DataKey mySrcKey = pControlKey.theMap.get(myType);

                /* Create a new DataKey for this ControlKey */
                DataKey myKey = myKeys.cloneItem(myControl, mySrcKey);

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

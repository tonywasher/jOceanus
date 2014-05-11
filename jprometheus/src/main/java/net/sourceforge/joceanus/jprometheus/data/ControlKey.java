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

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jgordianknot.crypto.HashKey;
import net.sourceforge.joceanus.jgordianknot.crypto.PasswordHash;
import net.sourceforge.joceanus.jgordianknot.crypto.SecureManager;
import net.sourceforge.joceanus.jgordianknot.crypto.SecurityGenerator;
import net.sourceforge.joceanus.jmetis.viewer.EncryptionGenerator;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.DataKeySet.DataKeySetList;
import net.sourceforge.joceanus.jprometheus.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * ControlKey definition and list. The Control Key represents the passwordHash that controls securing of the dataKeys. It maintains a map of the associated
 * DataKeys.
 * @author Tony Washer
 */
public final class ControlKey
        extends DataItem<CryptographyDataType>
        implements Comparable<ControlKey> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(ControlKey.class.getName());

    /**
     * Object name.
     */
    public static final String OBJECT_NAME = CryptographyDataType.CONTROLKEY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = CryptographyDataType.CONTROLKEY.getListName();

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
     * Field ID for DataKeySet.
     */
    public static final JDataField FIELD_SETS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataKeySet"));

    /**
     * Name of Database.
     */
    public static final String NAME_DATABASE = NLS_BUNDLE.getString("NameDataBase");

    /**
     * PasswordHash Length.
     */
    public static final int HASHLEN = PasswordHash.HASHSIZE;

    /**
     * The DataKeySet.
     */
    private DataKeySet theDataKeySet = null;

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_SETS.equals(pField)) {
            return theDataKeySet;
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
    protected HashKey getHashKey() {
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
        return theDataKeySet.getFieldGenerator();
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
     * Obtain the next DataKeySet.
     * @return the next dataKeySet
     */
    protected DataKeySet getNextDataKeySet() {
        return theDataKeySet;
    }

    /**
     * Copy constructor.
     * @param pList the List to add to
     * @param pSource the source key to copy
     */
    private ControlKey(final ControlKeyList pList,
                       final ControlKey pSource) {
        /* Initialise the item */
        super(pList, pSource);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private ControlKey(final ControlKeyList pList,
                       final DataValues<CryptographyDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the HashBytes */
        Object myValue = pValues.getValue(FIELD_PASSHASH);
        if (myValue instanceof byte[]) {
            setValueHashBytes((byte[]) myValue);
        }

        /* Access the Security manager */
        DataSet<?, ?> myData = getDataSet();
        SecureManager mySecure = myData.getSecurity();

        /* Resolve the password hash */
        PasswordHash myHash = mySecure.resolvePasswordHash(getHashBytes(), NAME_DATABASE);

        /* Store the password hash */
        setValuePasswordHash(myHash);
    }

    /**
     * Constructor for a new ControlKey.
     * <p>
     * This will create a new DataKeySet with a new set of DataKeys.
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

            /* Create a new password hash with new password */
            PasswordHash myHash = mySecure.resolvePasswordHash(null, NAME_DATABASE);

            /* Store the password hash */
            setValuePasswordHash(myHash);

            /* Allocate the DataKeySets */
            allocateDataKeySets(myData);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JPrometheusDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Constructor for a new ControlKey with the same password.
     * <p>
     * This will create a new DataKeySet with a new cloned set of DataKeys.
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

            /* ReSeed the security generator */
            SecurityGenerator myGenerator = mySecure.getSecurityGenerator();
            myGenerator.reSeedRandom();

            /* Create a clone of the password hash */
            PasswordHash myHash = mySecure.clonePasswordHash(myData.getPasswordHash());

            /* Store the password Hash */
            setValuePasswordHash(myHash);

            /* Allocate the DataKeySets */
            allocateDataKeySets(myData);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JPrometheusDataException(this, ERROR_CREATEITEM, e);
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
     * Allocate a new DataKeySet.
     * @param pData the DataSet
     * @throws JOceanusException on error
     */
    private void allocateDataKeySets(final DataSet<?, ?> pData) throws JOceanusException {
        /* Access the DataKeySet List */
        DataKeySetList mySets = pData.getDataKeySets();
        setNewVersion();

        /* Allocate the DataKeySet */
        theDataKeySet = new DataKeySet(mySets, this);
        mySets.add(theDataKeySet);
    }

    /**
     * Delete the old set of ControlKey and DataKeys.
     */
    private void deleteControlSet() {
        /* Delete the DataKeySet */
        theDataKeySet.deleteDataKeySet();

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

        /* Update the hash for the KeySet */
        theDataKeySet.updatePasswordHash(pHash);

        /* Check for changes */
        checkForHistory();
    }

    /**
     * Register DataKeySet.
     * @param pKeySet the DataKeySet to register
     */
    protected void registerDataKeySet(final DataKeySet pKeySet) {
        /* Store the DataKey into the map */
        theDataKeySet = pKeySet;
    }

    /**
     * ControlKey List.
     */
    public static class ControlKeyList
            extends DataList<ControlKey, CryptographyDataType> {
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
            return ControlKey.FIELD_DEFS;
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
            this(pData, ListStyle.CORE);
        }

        /**
         * Construct an empty generic ControlKey list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected ControlKeyList(final DataSet<?, ?> pData,
                                 final ListStyle pStyle) {
            super(ControlKey.class, pData, CryptographyDataType.CONTROLKEY, pStyle);
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
        public ControlKeyList deriveList(final ListStyle pStyle) throws JOceanusException {
            return (ControlKeyList) super.deriveList(pStyle);
        }

        @Override
        public ControlKeyList deriveDifferences(final DataSet<?, ?> pDataSet,
                                                final DataList<?, CryptographyDataType> pOld) {
            return (ControlKeyList) super.deriveDifferences(pDataSet, pOld);
        }

        @Override
        public ControlKey addCopyItem(final DataItem<?> pItem) {
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
            throw new UnsupportedOperationException();
        }

        @Override
        public ControlKey addValuesItem(final DataValues<CryptographyDataType> pValues) throws JOceanusException {
            /* Create the controlKey */
            ControlKey myKey = new ControlKey(this, pValues);

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
                /* Clone the Control Key and its DataKeySets */
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
         * Clone ControlKey from dataBase.
         * @param pControlKey the ControlKey to clone
         * @return the new control key
         * @throws JOceanusException on error
         */
        private ControlKey cloneControlKey(final ControlKey pControlKey) throws JOceanusException {
            /* Build data values */
            DataValues<CryptographyDataType> myValues = new DataValues<CryptographyDataType>(ControlKey.OBJECT_NAME);
            myValues.addValue(ControlKey.FIELD_ID, pControlKey.getId());
            myValues.addValue(ControlKey.FIELD_PASSHASH, pControlKey.getHashBytes());

            /* Clone the control key */
            ControlKey myControl = addValuesItem(myValues);

            /* Access the DataKey List */
            DataSet<?, ?> myData = getDataSet();
            DataKeySetList myKeySets = myData.getDataKeySets();

            /* Create a new DataKeySet for this ControlKey */
            myControl.theDataKeySet = myKeySets.cloneDataKeySet(myControl, pControlKey.theDataKeySet);

            /* return the cloned key */
            return myControl;
        }
    }
}

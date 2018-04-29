/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2018 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.lethe.data;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptionGenerator;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataKey.DataKeyList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * ControlKey definition and list. The Control Key represents the passwordHash that controls
 * securing of the dataKeys. It maintains a map of the associated DataKeys.
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
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataItem.FIELD_DEFS);

    /**
     * Field ID for ControlKey.
     */
    public static final MetisField FIELD_CONTROLKEY = FIELD_DEFS.declareEqualityValueField(CryptographyDataType.CONTROLKEY.getItemName(), MetisDataType.LINK);

    /**
     * Field ID for CreationDate.
     */
    public static final MetisField FIELD_CREATEDATE = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.DATAKEYSET_CREATION.getValue(), MetisDataType.DATE);

    /**
     * Field ID for SymKeyMap.
     */
    public static final MetisField FIELD_SYMMAP = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAKEYSET_SYMKEYMAP.getValue());

    /**
     * Field ID for KeySet.
     */
    public static final MetisField FIELD_KEYSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAKEYSET_KEYSET.getValue());

    /**
     * The SymDataKey Map.
     */
    private Map<GordianSymKeyType, DataKey> theSymMap;

    /**
     * The Encryption KeySet.
     */
    private GordianKeySet theKeySet;

    /**
     * The Security Factory.
     */
    private GordianFactory theSecurityFactory;

    /**
     * The Encryption Field Generator.
     */
    private MetisEncryptionGenerator theFieldGenerator;

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
                theSecurityFactory = pSource.theSecurityFactory;
                theSymMap = new EnumMap<>(GordianSymKeyType.class);
                theKeySet = theSecurityFactory.createKeySet();
                theFieldGenerator = new MetisEncryptionGenerator(theKeySet, getDataSet().getDataFormatter());
                break;
            default:
                break;
        }
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private DataKeySet(final DataKeySetList pList,
                       final DataValues<CryptographyDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access the Security manager */
        final DataSet<?, ?> myData = getDataSet();
        final GordianHashManager mySecure = myData.getSecurity();
        final MetisDataFormatter myFormatter = myData.getDataFormatter();

        /* Record the security factory */
        theSecurityFactory = mySecure.getSecurityFactory();

        /* Create the DataKey Maps */
        theSymMap = new EnumMap<>(GordianSymKeyType.class);

        /* Store the ControlKey */
        Object myValue = pValues.getValue(FIELD_CONTROLKEY);
        if (myValue instanceof Integer) {
            /* Store the integer */
            final Integer myInt = (Integer) myValue;
            setValueControlKey(myInt);

            /* Resolve the ControlKey */
            resolveDataLink(FIELD_CONTROLKEY, myData.getControlKeys());
        } else if (myValue instanceof ControlKey) {
            /* Store the controlKey */
            setValueControlKey((ControlKey) myValue);
        }

        /* Access the controlKey */
        final ControlKey myControl = getControlKey();

        /* Create the KeySet and security generator */
        theKeySet = theSecurityFactory.createKeySet();
        theFieldGenerator = new MetisEncryptionGenerator(theKeySet, myFormatter);

        /* Store the CreationDate */
        myValue = pValues.getValue(FIELD_CREATEDATE);
        if (!(myValue instanceof TethysDate)) {
            myValue = new TethysDate();
        }
        setValueCreationDate((TethysDate) myValue);

        /* Register the DataKeySet */
        myControl.registerDataKeySet(this);
    }

    /**
     * Constructor for a new DataKeySet. This will create a set of DataKeys.
     * @param pList the list to which to add the keySet to
     * @param pControlKey the control key
     * @throws OceanusException on error
     */
    protected DataKeySet(final DataKeySetList pList,
                         final ControlKey pControlKey) throws OceanusException {
        /* Initialise the item */
        super(pList, 0);

        /* Protect against exceptions */
        try {
            /* Store the Details */
            setValueControlKey(pControlKey);

            /* Access the Security manager */
            final DataSet<?, ?> myData = getDataSet();
            final GordianHashManager mySecure = myData.getSecurity();
            final MetisDataFormatter myFormatter = myData.getDataFormatter();

            /* Record the security factory */
            theSecurityFactory = mySecure.getSecurityFactory();

            /* Create the DataKey Map */
            theSymMap = new EnumMap<>(GordianSymKeyType.class);

            /* Create the KeySet */
            theKeySet = theSecurityFactory.createKeySet();
            theFieldGenerator = new MetisEncryptionGenerator(theKeySet, myFormatter);

            /* Set the creationDate */
            setValueCreationDate(new TethysDate());

            /* Allocate the DataKeys */
            allocateDataKeys(myData);

            /* Catch Exceptions */
        } catch (OceanusException e) {
            /* Pass on exception */
            throw new PrometheusDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_SYMMAP.equals(pField)) {
            return theSymMap;
        }
        if (FIELD_KEYSET.equals(pField)) {
            return theKeySet;
        }
        return super.getFieldValue(pField);
    }

    /**
     * Get the Encryption Field Generator.
     * @return the field generator
     */
    public MetisEncryptionGenerator getFieldGenerator() {
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
        final ControlKey myKey = getControlKey();
        return (myKey == null)
                               ? null
                               : myKey.getId();
    }

    /**
     * Get the CreationDate.
     * @return the creationDate
     */
    public final TethysDate getCreationDate() {
        return getCreationDate(getValueSet());
    }

    /**
     * Get the ControlKey.
     * @param pValueSet the valueSet
     * @return the control Key
     */
    public static ControlKey getControlKey(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CONTROLKEY, ControlKey.class);
    }

    /**
     * Get the CreationDate.
     * @param pValueSet the valueSet
     * @return the creationDate
     */
    public static TethysDate getCreationDate(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CREATEDATE, TethysDate.class);
    }

    /**
     * Is this locked by prime hash.
     * @return true/false
     */
    protected Boolean isHashPrime() {
        return getControlKey().isHashPrime();
    }

    /**
     * Get the keySetHash.
     * @return the keySetHash
     * @throws OceanusException on error
     */
    protected GordianKeySetHash getKeySetHash() throws OceanusException {
        return getControlKey().getKeySetHash();
    }

    /**
     * Get the PassWordHash.
     * @param useHashPrime true/false
     * @return the passwordHash
     * @throws OceanusException on error
     */
    protected GordianKeySetHash getKeySetHash(final Boolean useHashPrime) throws OceanusException {
        return getControlKey().getKeySetHash(useHashPrime);
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
    private void setValueCreationDate(final TethysDate pValue) {
        getValueSet().setValue(FIELD_CREATEDATE, pValue);
    }

    /**
     * Resolve the Active HashKey.
     * @throws OceanusException on error
     */
    protected final void resolveHash() throws OceanusException {
        getControlKey().resolveHash();
    }

    @Override
    public DataKeySet getBase() {
        return (DataKeySet) super.getBase();
    }

    @Override
    public DataKeySetList getList() {
        return (DataKeySetList) super.getList();
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
    public void resolveDataSetLinks() throws OceanusException {
        /* Resolve the ControlKey */
        final DataSet<?, ?> myData = getDataSet();
        resolveDataLink(FIELD_CONTROLKEY, myData.getControlKeys());
        final ControlKey myControlKey = getControlKey();

        /* Register the KeySet */
        myControlKey.registerDataKeySet(this);
    }

    /**
     * Allocate a new set of DataKeys.
     * @param pData the DataSet
     * @throws OceanusException on error
     */
    private void allocateDataKeys(final DataSet<?, ?> pData) throws OceanusException {
        /* Access the DataKey List */
        final DataKeyList myKeys = pData.getDataKeys();
        setNewVersion();

        /* Access the KeyPredicates */
        final DataSet<?, ?> myData = getDataSet();
        final GordianFactory myFactory = myData.getSecurity().getSecurityFactory();
        final Predicate<GordianSymKeyType> mySymPredicate = myFactory.supportedKeySetSymKeyTypes();

        /* Loop through the SymKeyType values */
        for (GordianSymKeyType myType : GordianSymKeyType.values()) {
            /* If this is valid for this keyLength */
            if (mySymPredicate.test(myType)) {
                /* Create a new DataKey for this DataKeySet */
                final DataKey myKey = myKeys.createNewKey(this, myType);
                myKey.setNewVersion();
            }
        }
    }

    /**
     * Delete the old set of DataKeySet and DataKeys.
     */
    protected void deleteDataKeySet() {
        /* Loop through the SymKeyType values */
        for (GordianSymKeyType myType : GordianSymKeyType.values()) {
            /* Access the Data Key */
            final DataKey myKey = theSymMap.get(myType);

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
     * @param pHash the new keySetHash
     * @return were there changes? true/false
     * @throws OceanusException on error
     */
    protected boolean updateKeySetHash(final Boolean pPrimeHash,
                                       final GordianKeySetHash pHash) throws OceanusException {
        /* Loop through the SymKeyType values */
        boolean bChanges = false;
        for (GordianSymKeyType myType : GordianSymKeyType.values()) {
            /* Access the Data Key */
            final DataKey myKey = theSymMap.get(myType);

            /* Update the password hash */
            if (myKey != null) {
                bChanges |= myKey.updateKeySetHash(pPrimeHash, pHash);
            }
        }

        /* return the flag */
        return bChanges;
    }

    /**
     * Register DataKey.
     * @param pKey the DataKey to register
     * @throws OceanusException on error
     */
    protected void registerDataKey(final DataKey pKey) throws OceanusException {
        /* Store the DataKey into the map */
        theSymMap.put(pKey.getSymKeyType(), pKey);

        /* Declare the Key */
        theKeySet.declareSymKey(pKey.getSymKey());
    }

    /**
     * DataKeySet List.
     */
    public static class DataKeySetList
            extends DataList<DataKeySet, CryptographyDataType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<DataKeySetList> FIELD_DEFS = MetisFieldSet.newFieldSet(DataKeySetList.class);

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
        public MetisFieldSet<DataKeySetList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return DataKeySet.FIELD_DEFS;
        }

        @Override
        public boolean includeDataXML() {
            return false;
        }

        @Override
        protected DataKeySetList getEmptyList(final ListStyle pStyle) {
            final DataKeySetList myList = new DataKeySetList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public DataKeySetList deriveList(final ListStyle pStyle) throws OceanusException {
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
            final DataKeySet mySet = new DataKeySet(this, (DataKeySet) pItem);
            add(mySet);
            return mySet;
        }

        @Override
        public DataKeySet addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataKeySet addValuesItem(final DataValues<CryptographyDataType> pValues) throws OceanusException {
            /* Create the dataKeySet */
            final DataKeySet mySet = new DataKeySet(this, pValues);

            /* Check that this keyId has not been previously added */
            if (!isIdUnique(mySet.getId())) {
                mySet.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new PrometheusDataException(mySet, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(mySet);

            /* Return it */
            return mySet;
        }

        /**
         * Clone KeySet from a DataBase.
         * @param pControlKey the ControlKey to clone
         * @param pKeySet the DataKeySet to clone
         * @return the new DataKeySet
         * @throws OceanusException on error
         */
        protected DataKeySet cloneDataKeySet(final ControlKey pControlKey,
                                             final DataKeySet pKeySet) throws OceanusException {
            /* Build data values */
            final DataValues<CryptographyDataType> myValues = new DataValues<>(DataKeySet.OBJECT_NAME);
            myValues.addValue(DataKeySet.FIELD_ID, pKeySet.getId());
            myValues.addValue(DataKeySet.FIELD_CONTROLKEY, pControlKey);
            myValues.addValue(DataKeySet.FIELD_CREATEDATE, pKeySet.getCreationDate());

            /* Clone the dataKeySet */
            final DataKeySet myKeySet = addValuesItem(myValues);

            /* Access the DataKey List */
            final DataSet<?, ?> myData = getDataSet();
            final DataKeyList myKeys = myData.getDataKeys();

            /* Access the symKeyPredicate */
            final GordianFactory myFactory = myData.getSecurity().getSecurityFactory();
            final Predicate<GordianSymKeyType> mySymPredicate = myFactory.supportedKeySetSymKeyTypes();

            /* Loop through the SymKeyType values */
            for (GordianSymKeyType myType : GordianSymKeyType.values()) {
                /* If this is valid for this keyLength */
                if (mySymPredicate.test(myType)) {
                    /* Access the source Data key */
                    final DataKey mySrcKey = pKeySet.theSymMap.get(myType);

                    /* Clone the DataKey for this DataKeySet */
                    final DataKey myKey = myKeys.cloneDataKey(myKeySet, mySrcKey);

                    /* Store the DataKey into the map */
                    myKeySet.theSymMap.put(myType, myKey);

                    /* Declare the Key */
                    myKeySet.theKeySet.declareSymKey(myKey.getSymKey());
                }
            }

            /* return the cloned keySet */
            return myKeySet;
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
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

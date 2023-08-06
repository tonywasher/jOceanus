/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.util.GordianUtilities;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptionGenerator;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * ControlKey definition and list. The Control Key represents the passwordHash that controls
 * securing of the dataKeys. It maintains a map of the associated DataKeys.
 * @author Tony Washer
 */
public class DataKeySet
        extends DataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = CryptographyDataType.DATAKEYSET.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = CryptographyDataType.DATAKEYSET.getListName();

    /**
     * KeySetWrapLength.
     */
    public static final int WRAPLEN = GordianUtilities.getMaximumKeySetWrapLength();

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataItem.FIELD_DEFS);

    /**
     * Field ID for ControlKey.
     */
    public static final MetisLetheField FIELD_CONTROLKEY = FIELD_DEFS.declareEqualityValueField(CryptographyDataType.CONTROLKEY.getItemName(), MetisDataType.LINK);

    /**
     * HashPrime Field Id.
     */
    public static final MetisLetheField FIELD_HASHPRIME = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.CONTROLKEY_PRIME.getValue(), MetisDataType.BOOLEAN);

    /**
     * Field ID for KeySetDef.
     */
    public static final MetisLetheField FIELD_KEYSETDEF = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.DATAKEYSET_KEYSETDEF.getValue(), MetisDataType.BYTEARRAY, WRAPLEN);

    /**d
     * Field ID for CreationDate.
     */
    public static final MetisLetheField FIELD_CREATEDATE = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.DATAKEYSET_CREATION.getValue(), MetisDataType.DATE);

    /**
     * Field ID for KeySet.
     */
    public static final MetisLetheField FIELD_KEYSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAKEYSET_KEYSET.getValue());

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
                theKeySet = pSource.theKeySet;
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
                       final DataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access the Password manager */
        final DataSet myData = getDataSet();
        final GordianPasswordManager mySecure = myData.getPasswordMgr();
        final TethysUIDataFormatter myFormatter = myData.getDataFormatter();

        /* Record the security factory */
        theSecurityFactory = mySecure.getSecurityFactory();

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

        /* Store the PrimeHash indicator */
        myValue = pValues.getValue(FIELD_HASHPRIME);
        final Boolean isHashPrime = (myValue instanceof Boolean)
                                    ? (Boolean) myValue
                                    : Boolean.TRUE;
        setValueHashPrime(isHashPrime);

        /* Store the WrappedKeySetDef */
        myValue = pValues.getValue(FIELD_KEYSETDEF);
        if (myValue instanceof byte[]) {
            final byte[] myBytes = (byte[]) myValue;
            setValueSecuredKeySetDef(myBytes);
            theKeySet = myControl.getKeySetHash(isHashPrime()).getKeySet().deriveKeySet(myBytes);
            theFieldGenerator = new MetisEncryptionGenerator(theKeySet, myFormatter);
        }

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
     * Constructor for a new DataKeySet.
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
            final DataSet myData = getDataSet();
            final GordianPasswordManager mySecure = myData.getPasswordMgr();
            final TethysUIDataFormatter myFormatter = myData.getDataFormatter();

            /* Record the security factory */
            theSecurityFactory = mySecure.getSecurityFactory();

            /* Create the KeySet */
            final GordianKeySetFactory myKeySets = theSecurityFactory.getKeySetFactory();
            theKeySet = myKeySets.generateKeySet(new GordianKeySetSpec());
            theFieldGenerator = new MetisEncryptionGenerator(theKeySet, myFormatter);

            /* Set the wrappedKeySetDef */
            setValueHashPrime(pControlKey.isHashPrime());
            setValueSecuredKeySetDef(pControlKey.getKeySetHash().getKeySet().secureKeySet(theKeySet));

            /* Set the creationDate */
            setValueCreationDate(new TethysDate());

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
    public Object getFieldValue(final MetisLetheField pField) {
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
     * Obtain the security factory.
     * @return the security factory
     */
    GordianFactory getSecurityFactory() {
        return theSecurityFactory;
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
     * Is this locked by prime hash.
     * @return true/false
     */
    public Boolean isHashPrime() {
        return isHashPrime(getValueSet());
    }

    /**
     * Get the securedKeySetDef.
     * @return the securedKeySetDef
     */
    public final byte[] getSecuredKeySetDef() {
        return getSecuredKeySetDef(getValueSet());
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
     * Is this locked by prime hash.
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isHashPrime(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_HASHPRIME, Boolean.class);
    }

    /**
     * Get the securedKeySetDef.
     * @param pValueSet the valueSet
     * @return the securedKeySetDef
     */
    public static byte[] getSecuredKeySetDef(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_KEYSETDEF, byte[].class);
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
     * Set the HashPrime indicator.
     * @param pPrime true/false
     */
    private void setValueHashPrime(final Boolean pPrime) {
        getValueSet().setValue(FIELD_HASHPRIME, pPrime);
    }

    /**
     * Set the securedKeySetDef.
     * @param pValue the securedKeySetDef
     */
    private void setValueSecuredKeySetDef(final byte[] pValue) {
        getValueSet().setValue(FIELD_KEYSETDEF, pValue);
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
    public int compareValues(final DataItem pThat) {
        /* Only sort on id */
        return 0;
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Resolve the ControlKey */
        final DataSet myData = getDataSet();
        resolveDataLink(FIELD_CONTROLKEY, myData.getControlKeys());
        final ControlKey myControlKey = getControlKey();

        /* Register the KeySet */
        myControlKey.registerDataKeySet(this);
    }

    /**
     * Delete the old set of DataKeySet and DataKeys.
     */
    protected void deleteDataKeySet() {
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
    boolean updateKeySetHash(final Boolean pPrimeHash,
                             final GordianKeySetHash pHash) throws OceanusException {
        /* Determine whether we need to update */
        if (!pPrimeHash.equals(isHashPrime())) {
            /* Store the current detail into history */
            pushHistory();

            /* Update the Security Control Key and obtain the new secured KeySetDef */
            final GordianKeySet myKeySet = pHash.getKeySet();
            setValueHashPrime(pPrimeHash);
            setValueSecuredKeySetDef(myKeySet.secureKeySet(theKeySet));

            /* Check for changes */
            return checkForHistory();
        }

        /* No changes */
        return false;
    }

    /**
     * DataKeySet List.
     */
    public static class DataKeySetList
            extends DataList<DataKeySet> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<DataKeySetList> FIELD_DEFS = MetisFieldSet.newFieldSet(DataKeySetList.class);

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         */
        protected DataKeySetList(final DataSet pData) {
            this(pData, ListStyle.CORE);
        }

        /**
         * Construct an empty generic ControlKey list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected DataKeySetList(final DataSet pData,
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
        public DataKeySetList deriveDifferences(final DataSet pDataSet,
                                                final DataList<?> pOld) {
            return (DataKeySetList) super.deriveDifferences(pDataSet, pOld);
        }

        @Override
        public DataKeySet addCopyItem(final DataItem pItem) {
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
        public DataKeySet addValuesItem(final DataValues pValues) throws OceanusException {
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
            final DataValues myValues = new DataValues(DataKeySet.OBJECT_NAME);
            myValues.addValue(DataKeySet.FIELD_ID, pKeySet.getId());
            myValues.addValue(DataKeySet.FIELD_CONTROLKEY, pControlKey);
            myValues.addValue(DataKeySet.FIELD_HASHPRIME, pKeySet.isHashPrime());
            myValues.addValue(DataKeySet.FIELD_KEYSETDEF, pKeySet.getSecuredKeySetDef());
            myValues.addValue(DataKeySet.FIELD_CREATEDATE, pKeySet.getCreationDate());

            /* Clone the dataKeySet */
            return addValuesItem(myValues);
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Just sort the list */
            reSort();
        }

        @Override
        protected DataMapItem allocateDataMap() {
            /* Unused */
            throw new UnsupportedOperationException();
        }
    }
}

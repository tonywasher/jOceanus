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
package net.sourceforge.joceanus.jprometheus.atlas.data;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.util.GordianUtilities;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataSet.PrometheusCryptographyDataType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * ControlKey definition and list. The Control Key represents the passwordHash that controls
 * securing of the dataKeys. It maintains a map of the associated DataKeys.
 * @author Tony Washer
 */
public class PrometheusDataKeySet
        extends PrometheusDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = PrometheusCryptographyDataType.DATAKEYSET.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = PrometheusCryptographyDataType.DATAKEYSET.getListName();

    /**
     * KeySetWrapLength.
     */
    public static final int WRAPLEN = GordianUtilities.getMaximumKeySetWrapLength();

    /**
     * Report fields.
     */
    private static final MetisFieldVersionedSet<PrometheusDataKeySet> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(PrometheusDataKeySet.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLinkField(PrometheusCryptographyDataType.CONTROLKEYSET);
        FIELD_DEFS.declareByteArrayField(PrometheusDataResource.KEYSET_KEYSETDEF, WRAPLEN);
        FIELD_DEFS.declareDerivedVersionedField(PrometheusDataResource.KEYSET_KEYSET);
    }

    /**
     * The Security Factory.
     */
    private GordianFactory theSecurityFactory;

    /**
     * The Encryptor.
     */
    private PrometheusEncryptor theEncryptor;

    /**
     * Copy Constructor.
     * @param pList the list the copy belongs to
     * @param pSource The Key to copy
     */
    protected PrometheusDataKeySet(final PrometheusDataKeySetList pList,
                                   final PrometheusDataKeySet pSource) {
        /* Set standard values */
        super(pList, pSource);

        /* Switch on the LinkStyle */
        switch (getStyle()) {
            case CLONE:
                theSecurityFactory = pSource.theSecurityFactory;
                final GordianKeySet myKeySet = pSource.getKeySet();
                theEncryptor = new PrometheusEncryptor(getDataSet().getDataFormatter(), myKeySet);
                setValueKeySet(myKeySet);
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
    private PrometheusDataKeySet(final PrometheusDataKeySetList pList,
                                 final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access the Password manager */
        final PrometheusDataSet myData = getDataSet();
        final GordianPasswordManager mySecure = myData.getPasswordMgr();
        final TethysUIDataFormatter myFormatter = myData.getDataFormatter();

        /* Record the security factory */
        theSecurityFactory = mySecure.getSecurityFactory();

        /* Store the ControlKey */
        Object myValue = pValues.getValue(PrometheusCryptographyDataType.CONTROLKEYSET);
        if (myValue instanceof Integer) {
            /* Store the integer */
            final Integer myInt = (Integer) myValue;
            setValueControlKeySet(myInt);

            /* Resolve the ControlKey */
            resolveDataLink(PrometheusCryptographyDataType.CONTROLKEYSET, myData.getControlKeySets());
        } else if (myValue instanceof PrometheusControlKeySet) {
            /* Store the controlKey */
            setValueControlKeySet((PrometheusControlKeySet) myValue);
        }

        /* Access the controlKey */
        final PrometheusControlKeySet myControlKeySet = getControlKeySet();

        /* Store the WrappedKeySetDef */
        myValue = pValues.getValue(PrometheusDataResource.KEYSET_KEYSETDEF);
        if (myValue instanceof byte[]) {
            setValueSecuredKeySetDef((byte[]) myValue);
        }

        /* Store/Resolve the keySet */
        myValue = pValues.getValue(PrometheusDataResource.KEYSET_KEYSET);
        if (myValue instanceof GordianKeySet) {
            setValueKeySet((GordianKeySet) myValue);
        } else if (getSecuredKeySetDef() != null) {
            final GordianKeySet myKeySet = myControlKeySet.getKeySet().deriveKeySet(getSecuredKeySetDef());
            theEncryptor = new PrometheusEncryptor(myFormatter, myKeySet);
            setValueKeySet(myKeySet);
        }

        /* Register the DataKeySet */
        myControlKeySet.registerDataKeySet(this);
    }

    /**
     * Constructor for a new DataKeySet.
     * @param pList the list to which to add the keySet to
     * @param pControlKeySet the control keySet
     * @throws OceanusException on error
     */
    protected PrometheusDataKeySet(final PrometheusDataKeySetList pList,
                                   final PrometheusControlKeySet pControlKeySet) throws OceanusException {
        /* Initialise the item */
        super(pList, 0);

        /* Protect against exceptions */
        try {
            /* Store the Details */
            setValueControlKeySet(pControlKeySet);

            /* Access the Security manager */
            final PrometheusDataSet myData = getDataSet();
            final GordianPasswordManager mySecure = myData.getPasswordMgr();
            final TethysUIDataFormatter myFormatter = myData.getDataFormatter();

            /* Record the security factory */
            theSecurityFactory = mySecure.getSecurityFactory();

            /* Create the KeySet */
            final GordianKeySetFactory myKeySets = theSecurityFactory.getKeySetFactory();
            final GordianKeySet myKeySet = myKeySets.generateKeySet(new GordianKeySetSpec());
            theEncryptor = new PrometheusEncryptor(myFormatter, myKeySet);
            setValueKeySet(myKeySet);

            /* Set the wrappedKeySetDef */
            setValueSecuredKeySetDef(pControlKeySet.getKeySet().secureKeySet(myKeySet));

            /* Catch Exceptions */
        } catch (OceanusException e) {
            /* Pass on exception */
            throw new PrometheusDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Get the Field Generator.
     * @return the generator
     */
    public PrometheusEncryptor getEncryptor() {
        return theEncryptor;
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
    public final PrometheusControlKey getControlKey() {
        return getControlKeySet().getControlKey();
    }

    /**
     * Get the ControlKeySet.
     * @return the controlKeySet
     */
    public final PrometheusControlKeySet getControlKeySet() {
        return getValues().getValue(PrometheusCryptographyDataType.CONTROLKEYSET, PrometheusControlKeySet.class);
    }

    /**
     * Get the ControlKeyId for this item.
     * @return the ControlKeyId
     */
    public Integer getControlKeySetId() {
        final PrometheusControlKeySet myKeySet = getControlKeySet();
        return myKeySet == null
                ? null
                : myKeySet.getIndexedId();
    }

    /**
     * Get the securedKeySetDef.
     * @return the securedKeySetDef
     */
    public final byte[] getSecuredKeySetDef() {
        return getValues().getValue(PrometheusDataResource.KEYSET_KEYSETDEF, byte[].class);
    }

    /**
     * Get the KeySet.
     * @return the keySet
     */
    public GordianKeySet getKeySet() {
        return getValues().getValue(PrometheusDataResource.KEYSET_KEYSET, GordianKeySet.class);
    }

    /**
     * Set the ControlKeySet Id.
     * @param pId the controlKeySet id
     * @throws OceanusException on error
     */
    private void setValueControlKeySet(final Integer pId) throws OceanusException {
        getValues().setValue(PrometheusCryptographyDataType.CONTROLKEYSET, pId);
    }

    /**
     * Set the ControlKeySet.
     * @param pKeySet the controlKeySet
     * @throws OceanusException on error
     */
    private void setValueControlKeySet(final PrometheusControlKeySet pKeySet) throws OceanusException {
        getValues().setValue(PrometheusCryptographyDataType.CONTROLKEYSET, pKeySet);
    }

    /**
     * Set the securedKeySetDef.
     * @param pValue the securedKeySetDef
     */
    private void setValueSecuredKeySetDef(final byte[] pValue) {
        getValues().setUncheckedValue(PrometheusDataResource.KEYSET_KEYSETDEF, pValue);
    }

    /**
     * Set the keySet.
     * @param pValue the keySet
     */
    private void setValueKeySet(final GordianKeySet pValue) {
        getValues().setUncheckedValue(PrometheusDataResource.KEYSET_KEYSET, pValue);
    }

    @Override
    public PrometheusDataKeySet getBase() {
        return (PrometheusDataKeySet) super.getBase();
    }

    @Override
    public PrometheusDataKeySetList getList() {
        return (PrometheusDataKeySetList) super.getList();
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Only sort on id */
        return 0;
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Resolve the ControlKey */
        final PrometheusDataSet myData = getDataSet();
        resolveDataLink(PrometheusCryptographyDataType.CONTROLKEYSET, myData.getControlKeySets());
        final PrometheusControlKeySet myControlKeySet = getControlKeySet();

        /* Register the KeySet */
        myControlKeySet.registerDataKeySet(this);
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
     * @param pHash the new keySetHash
     * @return were there changes? true/false
     * @throws OceanusException on error
     */
    boolean updateKeySetHash(final GordianKeySetHash pHash) throws OceanusException {
        /* Store the current detail into history */
        pushHistory();

        /* Update the Security Control Key and obtain the new secured KeySetDef */
        final GordianKeySet myKeySet = pHash.getKeySet();
        setValueSecuredKeySetDef(myKeySet.secureKeySet(myKeySet));

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * DataKeySet List.
     */
    public static class PrometheusDataKeySetList
            extends PrometheusDataList<PrometheusDataKeySet> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<PrometheusDataKeySetList> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusDataKeySetList.class);

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         */
        protected PrometheusDataKeySetList(final PrometheusDataSet pData) {
            this(pData, PrometheusListStyle.CORE);
        }

        /**
         * Construct an empty generic ControlKey list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected PrometheusDataKeySetList(final PrometheusDataSet pData,
                                           final PrometheusListStyle pStyle) {
            super(PrometheusDataKeySet.class, pData, PrometheusCryptographyDataType.DATAKEYSET, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private PrometheusDataKeySetList(final PrometheusDataKeySetList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<PrometheusDataKeySetList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSet<PrometheusDataKeySet> getItemFields() {
            return PrometheusDataKeySet.FIELD_DEFS;
        }

        @Override
        public boolean includeDataXML() {
            return false;
        }

        @Override
        protected PrometheusDataKeySetList getEmptyList(final PrometheusListStyle pStyle) {
            final PrometheusDataKeySetList myList = new PrometheusDataKeySetList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public PrometheusDataKeySetList deriveList(final PrometheusListStyle pStyle) throws OceanusException {
            return (PrometheusDataKeySetList) super.deriveList(pStyle);
        }

        @Override
        public PrometheusDataKeySetList deriveDifferences(final PrometheusDataSet pDataSet,
                                                          final PrometheusDataList<?> pOld) {
            return (PrometheusDataKeySetList) super.deriveDifferences(pDataSet, pOld);
        }

        @Override
        public PrometheusDataKeySet addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a DataKeySet */
            if (!(pItem instanceof PrometheusDataKeySet)) {
                return null;
            }

            /* Clone the data key set */
            final PrometheusDataKeySet mySet = new PrometheusDataKeySet(this, (PrometheusDataKeySet) pItem);
            add(mySet);
            return mySet;
        }

        @Override
        public PrometheusDataKeySet addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PrometheusDataKeySet addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the dataKeySet */
            final PrometheusDataKeySet mySet = new PrometheusDataKeySet(this, pValues);

            /* Check that this keyId has not been previously added */
            if (!isIdUnique(mySet.getIndexedId())) {
                mySet.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new PrometheusDataException(mySet, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(mySet);

            /* Return it */
            return mySet;
        }

        /**
         * Clone KeySet from a DataBase.
         * @param pControlKeySet the ControlKeySet to clone
         * @param pKeySet the DataKeySet to clone
         * @return the new DataKeySet
         * @throws OceanusException on error
         */
        protected PrometheusDataKeySet cloneDataKeySet(final PrometheusControlKeySet pControlKeySet,
                                                       final PrometheusDataKeySet pKeySet) throws OceanusException {
            /* Build data values */
            final PrometheusDataValues myValues = new PrometheusDataValues(PrometheusDataKeySet.OBJECT_NAME);
            myValues.addValue(MetisDataResource.DATA_ID, pKeySet.getIndexedId());
            myValues.addValue(PrometheusCryptographyDataType.CONTROLKEYSET, pControlKeySet);
            myValues.addValue(PrometheusDataResource.KEYSET_KEYSETDEF, pKeySet.getSecuredKeySetDef());
            myValues.addValue(PrometheusDataResource.KEYSET_KEYSET, pKeySet.getKeySet());

            /* Clone the dataKeySet */
            return addValuesItem(myValues);
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Just sort the list */
            reSort();
        }

        @Override
        protected PrometheusDataMapItem allocateDataMap() {
            /* Unused */
            throw new UnsupportedOperationException();
        }
    }
}

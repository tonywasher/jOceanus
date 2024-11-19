/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.data;

import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.prometheus.PrometheusDataException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet.PrometheusCryptographyDataType;
import net.sourceforge.joceanus.tethys.OceanusException;

/**
 * ControlData definition and list. The Control Data represents the data version of the entire data
 * set, allowing for migration code to be written to map between different versions. It also holds a
 * pointer to the active ControlKey.
 * <p>
 * When code is loaded from a database, it is possible that more than one control key will be
 * active. This will occur if a failure occurs when we are writing the results of a renew security
 * request to the database and we have changed some records, but not all to the required controlKey.
 * This record points to the active controlKey. All records that are not encrypted by the correct
 * controlKey should be re-encrypted and written to the database.
 * @author Tony Washer
 */
public class PrometheusControlData
        extends PrometheusDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = PrometheusCryptographyDataType.CONTROLDATA.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = PrometheusCryptographyDataType.CONTROLDATA.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldVersionedSet<PrometheusControlData> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(PrometheusControlData.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareIntegerField(PrometheusDataResource.CONTROLDATA_VERSION);
        FIELD_DEFS.declareLinkField(PrometheusCryptographyDataType.CONTROLKEY);
    }

    /**
     * Error message for already exists.
     */
    public static final String ERROR_CTLEXISTS = PrometheusDataResource.CONTROLDATA_ERROR_EXISTS.getValue();

    /**
     * Copy Constructor.
     * @param pList the associated list
     * @param pSource The source
     */
    protected PrometheusControlData(final PrometheusControlDataList pList,
                                    final PrometheusControlData pSource) {
        /* Set standard values */
        super(pList, pSource);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private PrometheusControlData(final PrometheusControlDataList pList,
                                  final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the Version */
        Object myValue = pValues.getValue(PrometheusDataResource.CONTROLDATA_VERSION);
        if (myValue instanceof Integer) {
            setValueDataVersion((Integer) myValue);
        } else if (myValue instanceof String) {
            setValueDataVersion(Integer.valueOf((String) myValue));
        }

        /* Store the ControlKey */
        myValue = pValues.getValue(PrometheusCryptographyDataType.CONTROLKEY);
        if (myValue instanceof Integer) {
            /* Store value */
            final Integer myInt = (Integer) myValue;
            setValueControlKey(myInt);

            /* Resolve the ControlKey */
            final PrometheusDataSet myData = getDataSet();
            resolveDataLink(PrometheusCryptographyDataType.CONTROLKEY, myData.getControlKeys());
        }
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Get the data version.
     * @return data version
     */
    public Integer getDataVersion() {
        return getValues().getValue(PrometheusDataResource.CONTROLDATA_VERSION, Integer.class);
    }

    /**
     * Get the control key.
     * @return the control key
     */
    public PrometheusControlKey getControlKey() {
        return getValues().getValue(PrometheusCryptographyDataType.CONTROLKEY, PrometheusControlKey.class);
    }

    /**
     * Get the ControlKeyId for this item.
     * @return the ControlKeyId
     */
    public Integer getControlKeyId() {
        final PrometheusControlKey myKey = getControlKey();
        return (myKey == null)
                ? null
                : myKey.getIndexedId();
    }

    /**
     * Set the data version value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueDataVersion(final Integer pValue) throws OceanusException {
        getValues().setValue(PrometheusDataResource.CONTROLDATA_VERSION, pValue);
    }

    /**
     * Set the control key value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueControlKey(final PrometheusControlKey pValue) throws OceanusException {
        getValues().setValue(PrometheusCryptographyDataType.CONTROLKEY, pValue);
    }

    /**
     * Set the control key value as Id.
     * @param pId the value
     * @throws OceanusException on error
     */
    private void setValueControlKey(final Integer pId) throws OceanusException {
        getValues().setValue(PrometheusCryptographyDataType.CONTROLKEY, pId);
    }

    @Override
    public PrometheusControlData getBase() {
        return (PrometheusControlData) super.getBase();
    }

    @Override
    public PrometheusControlDataList getList() {
        return (PrometheusControlDataList) super.getList();
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Check the versions */
        final PrometheusControlData myThat = (PrometheusControlData) pThat;
        return getDataVersion() - myThat.getDataVersion();
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Resolve the ControlKey */
        final PrometheusDataSet myData = getDataSet();
        resolveDataLink(PrometheusCryptographyDataType.CONTROLKEY, myData.getControlKeys());
    }

    /**
     * Set a new ControlKey.
     * @param pControl the new control key
     * @throws OceanusException on error
     */
    protected void setControlKey(final PrometheusControlKey pControl) throws OceanusException {
        /* If we do not have a control Key */
        if (getControlKey() == null) {
            /* Store the control details and return */
            setValueControlKey(pControl);
            return;
        }

        /* Store the current detail into history */
        pushHistory();

        /* Store the control details */
        setValueControlKey(pControl);

        /* Check for changes */
        checkForHistory();
    }

    /**
     * Control Data List.
     */
    public static class PrometheusControlDataList
            extends PrometheusDataList<PrometheusControlData> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<PrometheusControlDataList> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusControlDataList.class);

        /**
         * Construct an empty CORE Control Data list.
         * @param pData the DataSet for the list
         */
        protected PrometheusControlDataList(final PrometheusDataSet pData) {
            this(pData, PrometheusListStyle.CORE);
        }

        /**
         * Construct an empty generic ControlData list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected PrometheusControlDataList(final PrometheusDataSet pData,
                                            final PrometheusListStyle pStyle) {
            super(PrometheusControlData.class, pData, PrometheusCryptographyDataType.CONTROLDATA, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private PrometheusControlDataList(final PrometheusControlDataList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<PrometheusControlDataList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSet<PrometheusControlData> getItemFields() {
            return PrometheusControlData.FIELD_DEFS;
        }

        @Override
        public boolean includeDataXML() {
            return false;
        }

        /**
         * Get the single element.
         * @return the control data
         */
        public PrometheusControlData getControl() {
            return isEmpty()
                    ? null
                    : get(0);
        }

        @Override
        protected PrometheusControlDataList getEmptyList(final PrometheusListStyle pStyle) {
            final PrometheusControlDataList myList = new PrometheusControlDataList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public PrometheusControlDataList deriveList(final PrometheusListStyle pStyle) throws OceanusException {
            return (PrometheusControlDataList) super.deriveList(pStyle);
        }

        @Override
        public PrometheusControlDataList deriveDifferences(final PrometheusDataSet pDataSet,
                                                           final PrometheusDataList<?> pOld) {
            return (PrometheusControlDataList) super.deriveDifferences(pDataSet, pOld);
        }

        @Override
        public PrometheusControlData addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a ControlData */
            if (!(pItem instanceof PrometheusControlData)) {
                return null;
            }

            /* Clone the control data */
            final PrometheusControlData myControl = new PrometheusControlData(this, (PrometheusControlData) pItem);
            add(myControl);
            return myControl;
        }

        @Override
        public PrometheusControlData addNewItem() {
            throw new UnsupportedOperationException();
        }

        /**
         * Add new ControlData item for new security.
         * @param pVersion the version
         * @throws OceanusException on error
         */
        public void addNewControl(final Integer pVersion) throws OceanusException {
            /* Create the ControlData */
            final PrometheusDataValues myValues = new PrometheusDataValues(OBJECT_NAME);
            myValues.addValue(PrometheusDataResource.CONTROLDATA_VERSION, pVersion);

            /* Add the item */
            addValuesItem(myValues);
        }

        @Override
        public PrometheusControlData addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the controlData */
            final PrometheusControlData myControl = new PrometheusControlData(this, pValues);

            /* Check that this controlId has not been previously added */
            if (!isIdUnique(myControl.getIndexedId())) {
                myControl.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new PrometheusDataException(myControl, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myControl);

            /* Return it */
            return myControl;
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

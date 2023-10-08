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

import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
public class ControlData
        extends DataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = CryptographyDataType.CONTROLDATA.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = CryptographyDataType.CONTROLDATA.getListName();

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataItem.FIELD_DEFS);

    /**
     * Field ID for Data Version.
     */
    public static final MetisLetheField FIELD_DATAVERSION = FIELD_DEFS.declareEqualityValueField(PrometheusDataResourceX.CONTROLDATA_VERSION.getValue(), MetisDataType.INTEGER);

    /**
     * Field ID for Control Key.
     */
    public static final MetisLetheField FIELD_CONTROLKEY = FIELD_DEFS.declareEqualityValueField(CryptographyDataType.CONTROLKEY.getItemName(), MetisDataType.LINK);

    /**
     * Error message for already exists.
     */
    public static final String ERROR_CTLEXISTS = PrometheusDataResourceX.CONTROLDATA_ERROR_EXISTS.getValue();

    /**
     * Copy Constructor.
     * @param pList the associated list
     * @param pSource The source
     */
    protected ControlData(final ControlDataList pList,
                          final ControlData pSource) {
        /* Set standard values */
        super(pList, pSource);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private ControlData(final ControlDataList pList,
                        final DataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the Version */
        Object myValue = pValues.getValue(FIELD_DATAVERSION);
        if (myValue instanceof Integer) {
            setValueDataVersion((Integer) myValue);
        } else if (myValue instanceof String) {
            setValueDataVersion(Integer.valueOf((String) myValue));
        }

        /* Store the ControlKey */
        myValue = pValues.getValue(FIELD_CONTROLKEY);
        if (myValue instanceof Integer) {
            /* Store value */
            final Integer myInt = (Integer) myValue;
            setValueControlKey(myInt);

            /* Resolve the ControlKey */
            final DataSet myData = getDataSet();
            resolveDataLink(FIELD_CONTROLKEY, myData.getControlKeys());
        }
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Get the data version.
     * @return data version
     */
    public Integer getDataVersion() {
        return getDataVersion(getValueSet());
    }

    /**
     * Get the control key.
     * @return the control key
     */
    public ControlKey getControlKey() {
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
     * Get the data version for a ValueSet.
     * @param pValueSet the value set
     * @return data version
     */
    public static Integer getDataVersion(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATAVERSION, Integer.class);
    }

    /**
     * Get the control key for a ValueSet.
     * @param pValueSet the value set
     * @return control key
     */
    public static ControlKey getControlKey(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CONTROLKEY, ControlKey.class);
    }

    /**
     * Set the data version value.
     * @param pValue the value
     */
    private void setValueDataVersion(final Integer pValue) {
        getValueSet().setValue(FIELD_DATAVERSION, pValue);
    }

    /**
     * Set the control key value.
     * @param pValue the value
     */
    private void setValueControlKey(final ControlKey pValue) {
        getValueSet().setValue(FIELD_CONTROLKEY, pValue);
    }

    /**
     * Set the control key value as Id.
     * @param pId the value
     */
    private void setValueControlKey(final Integer pId) {
        getValueSet().setValue(FIELD_CONTROLKEY, pId);
    }

    @Override
    public ControlData getBase() {
        return (ControlData) super.getBase();
    }

    @Override
    public ControlDataList getList() {
        return (ControlDataList) super.getList();
    }

    @Override
    public int compareValues(final DataItem pThat) {
        /* Check the versions */
        final ControlData myThat = (ControlData) pThat;
        return getDataVersion() - myThat.getDataVersion();
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Resolve the ControlKey */
        final DataSet myData = getDataSet();
        resolveDataLink(FIELD_CONTROLKEY, myData.getControlKeys());
    }

    /**
     * Set a new ControlKey.
     * @param pControl the new control key
     */
    protected void setControlKey(final ControlKey pControl) {
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
    public static class ControlDataList
            extends DataList<ControlData> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<ControlDataList> FIELD_DEFS = MetisFieldSet.newFieldSet(ControlDataList.class);

        /**
         * Construct an empty CORE Control Data list.
         * @param pData the DataSet for the list
         */
        protected ControlDataList(final DataSet pData) {
            this(pData, ListStyle.CORE);
        }

        /**
         * Construct an empty generic ControlData list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected ControlDataList(final DataSet pData,
                                  final ListStyle pStyle) {
            super(ControlData.class, pData, CryptographyDataType.CONTROLDATA, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private ControlDataList(final ControlDataList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<ControlDataList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return ControlData.FIELD_DEFS;
        }

        @Override
        public boolean includeDataXML() {
            return false;
        }

        /**
         * Get the single element.
         * @return the control data
         */
        public ControlData getControl() {
            return isEmpty()
                             ? null
                             : get(0);
        }

        @Override
        protected ControlDataList getEmptyList(final ListStyle pStyle) {
            final ControlDataList myList = new ControlDataList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public ControlDataList deriveList(final ListStyle pStyle) throws OceanusException {
            return (ControlDataList) super.deriveList(pStyle);
        }

        @Override
        public ControlDataList deriveDifferences(final DataSet pDataSet,
                                                 final DataList<?> pOld) {
            return (ControlDataList) super.deriveDifferences(pDataSet, pOld);
        }

        @Override
        public ControlData addCopyItem(final DataItem pItem) {
            /* Can only clone a ControlData */
            if (!(pItem instanceof ControlData)) {
                return null;
            }

            /* Clone the control data */
            final ControlData myControl = new ControlData(this, (ControlData) pItem);
            add(myControl);
            return myControl;
        }

        @Override
        public ControlData addNewItem() {
            throw new UnsupportedOperationException();
        }

        /**
         * Add new ControlData item for new security.
         * @param pVersion the version
         * @throws OceanusException on error
         */
        public void addNewControl(final Integer pVersion) throws OceanusException {
            /* Create the ControlData */
            final DataValues myValues = new DataValues(ControlData.OBJECT_NAME);
            myValues.addValue(FIELD_DATAVERSION, pVersion);

            /* Add the item */
            addValuesItem(myValues);
        }

        @Override
        public ControlData addValuesItem(final DataValues pValues) throws OceanusException {
            /* Create the controlData */
            final ControlData myControl = new ControlData(this, pValues);

            /* Check that this controlId has not been previously added */
            if (!isIdUnique(myControl.getId())) {
                myControl.addError(ERROR_DUPLICATE, FIELD_ID);
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
        protected DataMapItem allocateDataMap() {
            /* Unused */
            throw new UnsupportedOperationException();
        }
    }
}

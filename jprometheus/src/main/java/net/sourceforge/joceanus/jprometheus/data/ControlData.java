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

import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr;

/**
 * ControlData definition and list. The Control Data represents the data version of the entire data set, allowing for migration code to be written to map
 * between different versions. It also holds a pointer to the active ControlKey.
 * <p>
 * When code is loaded from a database, it is possible that more than one control key will be active. This will occur if a failure occurs when we are writing
 * the results of a renew security request to the database and we have changed some records, but not all to the required controlKey. This record points to the
 * active controlKey. All records that are not encrypted by the correct controlKey should be re-encrypted and written to the database.
 * @author Tony Washer
 */
public class ControlData
        extends DataItem<CryptographyDataType>
        implements Comparable<ControlData> {
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
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, DataItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Field ID for Data Version.
     */
    public static final JDataField FIELD_DATAVERSION = FIELD_DEFS.declareEqualityValueField(ResourceMgr.getString(PrometheusDataResource.CONTROLDATA_VERSION));

    /**
     * Field ID for Control Key.
     */
    public static final JDataField FIELD_CONTROLKEY = FIELD_DEFS.declareEqualityValueField(CryptographyDataType.CONTROLKEY.getItemName());

    /**
     * Error message for already exists.
     */
    public static final String ERROR_CTLEXISTS = ResourceMgr.getString(PrometheusDataResource.CONTROLDATA_ERROR_EXISTS);

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
        ControlKey myKey = getControlKey();
        return (myKey == null)
                              ? null
                              : myKey.getId();
    }

    /**
     * Get the data version for a ValueSet.
     * @param pValueSet the value set
     * @return data version
     */
    public static Integer getDataVersion(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATAVERSION, Integer.class);
    }

    /**
     * Get the control key for a ValueSet.
     * @param pValueSet the value set
     * @return control key
     */
    public static ControlKey getControlKey(final ValueSet pValueSet) {
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
     * @throws JOceanusException on error
     */
    private ControlData(final ControlDataList pList,
                        final DataValues<CryptographyDataType> pValues) throws JOceanusException {
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
            Integer myInt = (Integer) myValue;
            setValueControlKey(myInt);

            /* Resolve the ControlKey */
            DataSet<?, ?> myData = getDataSet();
            resolveDataLink(FIELD_CONTROLKEY, myData.getControlKeys());
        }
    }

    @Override
    public int compareTo(final ControlData pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Versions */
        int iDiff = getDataVersion() - pThat.getDataVersion();
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Resolve the ControlKey */
        DataSet<?, ?> myData = getDataSet();
        resolveDataLink(FIELD_CONTROLKEY, myData.getControlKeys());
    }

    /**
     * Set a new ControlKey.
     * @param pControl the new control key
     * @throws JOceanusException on error
     */
    protected void setControlKey(final ControlKey pControl) throws JOceanusException {
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
            extends DataList<ControlData, CryptographyDataType> {
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
            return peekFirst();
        }

        /**
         * Construct an empty CORE Control Data list.
         * @param pData the DataSet for the list
         */
        protected ControlDataList(final DataSet<?, ?> pData) {
            this(pData, ListStyle.CORE);
        }

        /**
         * Construct an empty generic ControlData list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected ControlDataList(final DataSet<?, ?> pData,
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
        protected ControlDataList getEmptyList(final ListStyle pStyle) {
            ControlDataList myList = new ControlDataList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public ControlDataList deriveList(final ListStyle pStyle) throws JOceanusException {
            return (ControlDataList) super.deriveList(pStyle);
        }

        @Override
        public ControlDataList deriveDifferences(final DataSet<?, ?> pDataSet,
                                                 final DataList<?, CryptographyDataType> pOld) {
            return (ControlDataList) super.deriveDifferences(pDataSet, pOld);
        }

        @Override
        public ControlData addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a ControlData */
            if (!(pItem instanceof ControlData)) {
                return null;
            }

            /* Clone the control data */
            ControlData myControl = new ControlData(this, (ControlData) pItem);
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
         * @throws JOceanusException on error
         */
        public void addNewControl(final Integer pVersion) throws JOceanusException {
            /* Create the ControlData */
            DataValues<CryptographyDataType> myValues = new DataValues<CryptographyDataType>(ControlData.OBJECT_NAME);
            myValues.addValue(FIELD_DATAVERSION, pVersion);

            /* Add the item */
            addValuesItem(myValues);
        }

        @Override
        public ControlData addValuesItem(final DataValues<CryptographyDataType> pValues) throws JOceanusException {
            /* Create the controlData */
            ControlData myControl = new ControlData(this, pValues);

            /* Check that this controlId has not been previously added */
            if (!isIdUnique(myControl.getId())) {
                myControl.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JPrometheusDataException(myControl, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myControl);

            /* Return it */
            return myControl;
        }
    }
}

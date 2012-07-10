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

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.ValueSet;
import uk.co.tolcroft.models.data.ControlKey.ControlKeyList;

/**
 * ControlData definition and list. The Control Data represents the data version of the entire data set,
 * allowing for migration code to be written to map between different versions. It also holds a pointer to the
 * active ControlKey.
 * <p>
 * When code is loaded from a database, it is possible that more than one control key will be active. This
 * will occur if a failure occurs when we are writing the results of a renew security request to the database
 * and we have changed some records, but not all to the required controlKey. This record points to the active
 * controlKey. All records that are not encrypted by the correct controlKey should be re-encrypted and written
 * to the database.
 * @author Tony Washer
 */
public class ControlData extends DataItem implements Comparable<ControlData> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = ControlData.class.getSimpleName();

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
     * Field ID for Data Version.
     */
    public static final JDataField FIELD_VERSION = FIELD_DEFS.declareEqualityValueField("Version");

    /**
     * Field ID for Control Key.
     */
    public static final JDataField FIELD_CONTROLKEY = FIELD_DEFS.declareEqualityValueField("ControlKey");

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
     * Get the data version for a ValueSet.
     * @param pValueSet the value set
     * @return data version
     */
    public static Integer getDataVersion(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_VERSION, Integer.class);
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
        getValueSet().setValue(FIELD_VERSION, pValue);
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

    /**
     * Construct a copy of a ControlData.
     * @param pList the associated list
     * @param pSource The source
     */
    protected ControlData(final ControlDataList pList,
                          final ControlData pSource) {
        /* Set standard values */
        super(pList, pSource);
    }

    /**
     * Standard Constructor.
     * @param pList the owning list
     * @param uId the id
     * @param uVersion the data version
     * @param uControlId the control key id
     * @throws JDataException on error
     */
    private ControlData(final ControlDataList pList,
                        final int uId,
                        final int uVersion,
                        final int uControlId) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Record the values */
            setValueDataVersion(uVersion);
            setValueControlKey(uControlId);

            /* Look up the ControlKey */
            DataSet<?> myData = getDataSet();
            ControlKeyList myKeys = myData.getControlKeys();
            ControlKey myControl = myKeys.findItemById(uControlId);
            if (myControl == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid ControlKey Id");
            }
            setValueControlKey(myControl);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Limited (no security) constructor.
     * @param pList the owning list
     * @param uId the id
     * @param uVersion the data version
     */
    private ControlData(final ControlDataList pList,
                        final int uId,
                        final int uVersion) {
        /* Initialise the item */
        super(pList, uId);

        /* Record the values */
        setValueDataVersion(uVersion);
    }

    @Override
    public int compareTo(final ControlData pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
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
    protected void relinkToDataSet() {
        DataSet<?> myData = getDataSet();
        ControlKeyList myKeys = myData.getControlKeys();

        /* Update to use the local copy of the ControlKeys */
        ControlKey myKey = getControlKey();
        ControlKey myNewKey = myKeys.findItemById(myKey.getId());
        setValueControlKey(myNewKey);
    }

    /**
     * Set a new ControlKey.
     * @param pControl the new control key
     * @throws JDataException on error
     */
    protected void setControlKey(final ControlKey pControl) throws JDataException {
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
    public static class ControlDataList extends DataList<ControlDataList, ControlData> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(
                ControlDataList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        /**
         * The single element of the list.
         */
        private ControlData theControl = null;

        /**
         * Get the single element.
         * @return the control data
         */
        public ControlData getControl() {
            return theControl;
        }

        /**
         * Construct an empty CORE Control Data list.
         * @param pData the DataSet for the list
         */
        protected ControlDataList(final DataSet<?> pData) {
            super(ControlDataList.class, ControlData.class, pData, ListStyle.CORE);
        }

        /**
         * Construct an empty generic ControlData list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected ControlDataList(final DataSet<?> pData,
                                  final ListStyle pStyle) {
            super(ControlDataList.class, ControlData.class, pData, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private ControlDataList(final ControlDataList pSource) {
            super(pSource);
        }

        @Override
        protected ControlDataList getEmptyList() {
            return new ControlDataList(this);
        }

        @Override
        public ControlData addNewItem(final DataItem pItem) {
            /* Can only clone a ControlData */
            if (!(pItem instanceof ControlData)) {
                return null;
            }

            /* Clone the control key */
            ControlData myControl = new ControlData(this, (ControlData) pItem);
            add(myControl);
            theControl = myControl;
            return myControl;
        }

        @Override
        public ControlData addNewItem() {
            return null;
        }

        /**
         * Add a ControlData item.
         * @param uId the id
         * @param uVersion the version
         * @param uControlId the controlId
         * @throws JDataException on error
         */
        public void addItem(final int uId,
                            final int uVersion,
                            final int uControlId) throws JDataException {
            ControlData myControl;

            /* Create the ControlData */
            myControl = new ControlData(this, uId, uVersion, uControlId);

            /* Check that this ControlId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myControl, "Duplicate ControlId (" + uId + ")");
            }

            /* Only one control data is allowed */
            if (theControl != null) {
                throw new JDataException(ExceptionClass.DATA, myControl, "Control record already exists");
            }

            /* Add to the list */
            theControl = myControl;
            add(myControl);
        }

        /**
         * Add a ControlData item (with no security as yet).
         * @param uId the id
         * @param uVersion the version
         * @throws JDataException on error
         */
        public void addItem(final int uId,
                            final int uVersion) throws JDataException {
            ControlData myControl;

            /* Create the ControlData */
            myControl = new ControlData(this, uId, uVersion);

            /* Only one static is allowed */
            if (theControl != null) {
                throw new JDataException(ExceptionClass.DATA, myControl, "Control record already exists");
            }

            /* Add to the list */
            theControl = myControl;
            add(myControl);
        }
    }
}

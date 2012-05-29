/*******************************************************************************
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

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDataManager.ReportFields;
import net.sourceforge.JDataManager.ReportFields.ReportField;
import net.sourceforge.JDataManager.ValueSet;
import uk.co.tolcroft.models.data.ControlKey.ControlKeyList;

public class ControlData extends DataItem<ControlData> {
    /**
     * Object name
     */
    public static String objName = ControlData.class.getSimpleName();

    /**
     * List name
     */
    public static String listName = objName + "s";

    /**
     * Report fields
     */
    private static final ReportFields theLocalFields = new ReportFields(objName, DataItem.theLocalFields);

    /* Called from constructor */
    @Override
    public ReportFields declareFields() {
        return theLocalFields;
    }

    /* Field IDs */
    public static final ReportField FIELD_VERSION = theLocalFields.declareEqualityValueField("Version");
    public static final ReportField FIELD_CONTROLKEY = theLocalFields.declareEqualityValueField("ControlKey");

    /**
     * The active set of values
     */
    private ValueSet<ControlData> theValueSet;

    @Override
    public void declareValues(ValueSet<ControlData> pValues) {
        super.declareValues(pValues);
        theValueSet = pValues;
    }

    /* Access methods */
    public Integer getDataVersion() {
        return getDataVersion(theValueSet);
    }

    public ControlKey getControlKey() {
        return getControlKey(theValueSet);
    }

    public static Integer getDataVersion(ValueSet<ControlData> pValueSet) {
        return pValueSet.getValue(FIELD_VERSION, Integer.class);
    }

    public static ControlKey getControlKey(ValueSet<ControlData> pValueSet) {
        return pValueSet.getValue(FIELD_CONTROLKEY, ControlKey.class);
    }

    private void setValueDataVersion(Integer pId) {
        theValueSet.setValue(FIELD_VERSION, pId);
    }

    private void setValueControlKey(ControlKey pKey) {
        theValueSet.setValue(FIELD_CONTROLKEY, pKey);
    }

    private void setValueControlKey(Integer pId) {
        theValueSet.setValue(FIELD_CONTROLKEY, pId);
    }

    /* Linking methods */
    @Override
    public ControlData getBase() {
        return (ControlData) super.getBase();
    }

    /**
     * Construct a copy of a ControlData
     * @param pList the associated list
     * @param pSource The source
     */
    protected ControlData(ControlDataList pList,
                          ControlData pSource) {
        /* Set standard values */
        super(pList, pSource);

        /* Switch on the LinkStyle */
        switch (getStyle()) {
            case CLONE:
                isolateCopy(pList.getData());
            case CORE:
            case COPY:
                pList.setNewId(this);
                break;
            case EDIT:
                setBase(pSource);
                setState(DataState.CLEAN);
                break;
            case UPDATE:
                setBase(pSource);
                setState(pSource.getState());
                break;
        }
    }

    /* Standard constructor */
    private ControlData(ControlDataList pList,
                        int uId,
                        int uVersion,
                        int uControlId) throws ModelException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Record the values */
            setValueDataVersion(uVersion);
            setValueControlKey(uControlId);

            /* Look up the ControlKey */
            ControlKey myControl = pList.theData.getControlKeys().searchFor(uControlId);
            if (myControl == null)
                throw new ModelException(ExceptionClass.DATA, this, "Invalid ControlKey Id");
            setValueControlKey(myControl);

            /* Allocate the id */
            pList.setNewId(this);
        }

        /* Catch Exceptions */
        catch (Exception e) {
            /* Pass on exception */
            throw new ModelException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /* Limited (no security) constructor */
    private ControlData(ControlDataList pList,
                        int uId,
                        int uVersion) throws ModelException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Record the values */
            setValueDataVersion(uVersion);

            /* Allocate the id */
            pList.setNewId(this);
        }

        /* Catch Exceptions */
        catch (Exception e) {
            /* Pass on exception */
            throw new ModelException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    @Override
    public int compareTo(Object pThat) {
        int iDiff;

        /* Handle the trivial cases */
        if (this == pThat)
            return 0;
        if (pThat == null)
            return -1;

        /* Make sure that the object is a ControlData */
        if (pThat.getClass() != this.getClass())
            return -1;

        /* Access the object as a ControlData */
        ControlData myThat = (ControlData) pThat;

        /* Compare the Versions */
        iDiff = (int) (getDataVersion() - myThat.getDataVersion());
        if (iDiff < 0)
            return -1;
        if (iDiff > 0)
            return 1;

        /* Compare the IDs */
        iDiff = (int) (getId() - myThat.getId());
        if (iDiff < 0)
            return -1;
        if (iDiff > 0)
            return 1;
        return 0;
    }

    /**
     * Isolate Data Copy
     * @param pData the DataSet
     */
    private void isolateCopy(DataSet<?> pData) {
        ControlKeyList myKeys = pData.getControlKeys();

        /* Update to use the local copy of the ControlKeys */
        ControlKey myKey = getControlKey();
        ControlKey myNewKey = myKeys.searchFor(myKey.getId());
        setValueControlKey(myNewKey);
    }

    /**
     * Set a new ControlKey
     * @param pControl the new control key
     * @throws ModelException
     */
    protected void setControlKey(ControlKey pControl) throws ModelException {
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
        if (checkForHistory())
            setState(DataState.CHANGED);
    }

    /**
     * Static List
     */
    public static class ControlDataList extends DataList<ControlDataList, ControlData> {
        /* Members */
        private DataSet<?> theData = null;

        public DataSet<?> getData() {
            return theData;
        }

        @Override
        public String listName() {
            return listName;
        }

        private ControlData theControl = null;

        public ControlData getControl() {
            return theControl;
        }

        /**
         * Construct an empty CORE static list
         * @param pData the DataSet for the list
         */
        protected ControlDataList(DataSet<?> pData) {
            super(ControlDataList.class, ControlData.class, ListStyle.CORE, false);
            theData = pData;
        }

        /**
         * Construct an empty generic ControlData list
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected ControlDataList(DataSet<?> pData,
                                  ListStyle pStyle) {
            super(ControlDataList.class, ControlData.class, pStyle, false);
            theData = pData;
        }

        /**
         * Constructor for a cloned List
         * @param pSource the source List
         */
        private ControlDataList(ControlDataList pSource) {
            super(pSource);
            theData = pSource.theData;
        }

        /**
         * Construct an update extract for the List.
         * @param pStyle the list style
         * @return the update Extract
         */
        private ControlDataList getExtractList(ListStyle pStyle) {
            /* Build an empty Extract List */
            ControlDataList myList = new ControlDataList(this);

            /* Obtain underlying updates */
            myList.populateList(pStyle);

            /* Return the list */
            return myList;
        }

        /* Obtain extract lists. */
        @Override
        public ControlDataList getUpdateList() {
            return getExtractList(ListStyle.UPDATE);
        }

        @Override
        public ControlDataList getEditList() {
            return null;
        }

        @Override
        public ControlDataList getShallowCopy() {
            return getExtractList(ListStyle.COPY);
        }

        @Override
        public ControlDataList getDeepCopy(DataSet<?> pDataSet) {
            /* Build an empty Extract List */
            ControlDataList myList = new ControlDataList(this);
            myList.theData = pDataSet;

            /* Obtain underlying clones */
            myList.populateList(ListStyle.CLONE);
            myList.setStyle(ListStyle.CORE);

            /* Return the list */
            return myList;
        }

        @Override
        protected ControlDataList getDifferences(ControlDataList pOld) {
            /* Build an empty Difference List */
            ControlDataList myList = new ControlDataList(this);

            /* Calculate the differences */
            myList.getDifferenceList(this, pOld);

            /* Return the list */
            return myList;
        }

        @Override
        public ControlData addNewItem(DataItem<?> pItem) {
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
         * Add a ControlData item
         * @param uId the id
         * @param uVersion the version
         * @param uControlId the controlId
         * @throws ModelException
         */
        public void addItem(int uId,
                            int uVersion,
                            int uControlId) throws ModelException {
            ControlData myControl;

            /* Create the ControlData */
            myControl = new ControlData(this, uId, uVersion, uControlId);

            /* Check that this ControlId has not been previously added */
            if (!isIdUnique(uId))
                throw new ModelException(ExceptionClass.DATA, myControl, "Duplicate ControlId (" + uId + ")");

            /* Only one static is allowed */
            if (theControl != null)
                throw new ModelException(ExceptionClass.DATA, myControl, "Control record already exists");

            /* Add to the list */
            theControl = myControl;
            add(myControl);
        }

        /**
         * Add a ControlData item (with no security as yet)
         * @param uId the id
         * @param uVersion the version
         * @throws ModelException
         */
        public void addItem(int uId,
                            int uVersion) throws ModelException {
            ControlData myControl;

            /* Create the ControlData */
            myControl = new ControlData(this, uId, uVersion);

            /* Only one static is allowed */
            if (theControl != null)
                throw new ModelException(ExceptionClass.DATA, myControl, "Control record already exists");

            /* Add to the list */
            theControl = myControl;
            add(myControl);
        }
    }
}

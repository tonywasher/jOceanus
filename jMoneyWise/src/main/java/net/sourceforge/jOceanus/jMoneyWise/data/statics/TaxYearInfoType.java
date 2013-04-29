/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.data.statics;

import net.sourceforge.jOceanus.jDataManager.DataType;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.data.StaticData;

/**
 * TaxYearInfoType data type.
 * @author Tony Washer
 */
public class TaxYearInfoType
        extends StaticData<TaxYearInfoType, TaxYearInfoClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TaxYearInfoType.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the TaxYear Info class of the TaxYearInfoType.
     * @return the class
     */
    public TaxYearInfoClass getInfoClass() {
        return super.getStaticClass();
    }

    /**
     * Return the Data Type of the TaxYearInfoType.
     * @return the data type
     */
    public DataType getDataType() {
        return getInfoClass().getDataType();
    }

    @Override
    public TaxYearInfoType getBase() {
        return (TaxYearInfoType) super.getBase();
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the TaxYear Info Type with
     * @param pInfoType The TaxYear Info Type to copy
     */
    protected TaxYearInfoType(final TaxYearInfoTypeList pList,
                              final TaxYearInfoType pInfoType) {
        super(pList, pInfoType);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the TaxYear Info Type with
     * @param pName Name of TaxYear Info Type
     * @throws JDataException on error
     */
    private TaxYearInfoType(final TaxYearInfoTypeList pList,
                            final String pName) throws JDataException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the TaxYearInfo Type with
     * @param pClass Class of TaxYearInfo Type
     * @throws JDataException on error
     */
    private TaxYearInfoType(final TaxYearInfoTypeList pList,
                            final TaxYearInfoClass pClass) throws JDataException {
        super(pList, pClass);
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the TaxYear Info Type with
     * @param pId the id
     * @param isEnabled is the TaxYear info type enabled
     * @param pOrder the sort order
     * @param pName Name of TaxYear Info Type
     * @param pDesc Description of TaxYear Info Type
     * @throws JDataException on error
     */
    private TaxYearInfoType(final TaxYearInfoTypeList pList,
                            final Integer pId,
                            final Boolean isEnabled,
                            final Integer pOrder,
                            final String pName,
                            final String pDesc) throws JDataException {
        super(pList, pId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the TaxYear Info Type with
     * @param pId ID of TaxYear Info Type
     * @param pControlId the control id of the new item
     * @param isEnabled is the TaxYear info type enabled
     * @param pOrder the sort order
     * @param pName Encrypted Name of TaxYear Info Type
     * @param pDesc Encrypted Description of TaxYear Info Type
     * @throws JDataException on error
     */
    private TaxYearInfoType(final TaxYearInfoTypeList pList,
                            final Integer pId,
                            final Integer pControlId,
                            final Boolean isEnabled,
                            final Integer pOrder,
                            final byte[] pName,
                            final byte[] pDesc) throws JDataException {
        super(pList, pId, pControlId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Represents a list of {@link TaxYearInfoType} objects.
     */
    public static class TaxYearInfoTypeList
            extends StaticList<TaxYearInfoType, TaxYearInfoClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TaxYearInfoTypeList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<TaxYearInfoClass> getEnumClass() {
            return TaxYearInfoClass.class;
        }

        /**
         * Construct an empty CORE account type list.
         * @param pData the DataSet for the list
         */
        public TaxYearInfoTypeList(final DataSet<?> pData) {
            super(TaxYearInfoType.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TaxYearInfoTypeList(final TaxYearInfoTypeList pSource) {
            super(pSource);
        }

        @Override
        protected TaxYearInfoTypeList getEmptyList(final ListStyle pStyle) {
            TaxYearInfoTypeList myList = new TaxYearInfoTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public TaxYearInfoTypeList cloneList(final DataSet<?> pDataSet) throws JDataException {
            return (TaxYearInfoTypeList) super.cloneList(pDataSet);
        }

        @Override
        public TaxYearInfoTypeList deriveList(final ListStyle pStyle) throws JDataException {
            return (TaxYearInfoTypeList) super.deriveList(pStyle);
        }

        @Override
        public TaxYearInfoTypeList deriveDifferences(final DataList<TaxYearInfoType> pOld) {
            return (TaxYearInfoTypeList) super.deriveDifferences(pOld);
        }

        @Override
        public TaxYearInfoType addCopyItem(final DataItem pItem) {
            /* Can only clone an TaxYearInfoType */
            if (!(pItem instanceof TaxYearInfoType)) {
                return null;
            }

            TaxYearInfoType myType = new TaxYearInfoType(this, (TaxYearInfoType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public TaxYearInfoType addNewItem() {
            return null;
        }

        /**
         * Obtain the type of the item.
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Add a TaxYearInfoType to the list.
         * @param pInfoType the Name of the TaxYear info type
         * @throws JDataException on error
         */
        public void addBasicItem(final String pInfoType) throws JDataException {
            /* Create a new TaxYear Info Type */
            TaxYearInfoType myInfoType = new TaxYearInfoType(this, pInfoType);

            /* Check that this InfoType has not been previously added */
            if (findItemByName(pInfoType) != null) {
                myInfoType.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JDataException(ExceptionClass.DATA, myInfoType, ERROR_VALIDATION);
            }

            /* Check that this TaxYearTypeId has not been previously added */
            if (!isIdUnique(myInfoType.getId())) {
                myInfoType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myInfoType, ERROR_VALIDATION);
            }

            /* Add the TaxYear Info Type to the list */
            append(myInfoType);

            /* Validate the ActType */
            myInfoType.validate();

            /* Handle validation failure */
            if (myInfoType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfoType, ERROR_VALIDATION);
            }
        }

        /**
         * Add a TaxYearInfoType to the list.
         * @param pId the Id of the TaxYear info type
         * @param isEnabled is the TaxYear info type enabled
         * @param pOrder the sort order
         * @param pInfoType the Name of the TaxYear info type
         * @param pDesc the Description of the TaxYear info type
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer pId,
                                final Boolean isEnabled,
                                final Integer pOrder,
                                final String pInfoType,
                                final String pDesc) throws JDataException {
            /* Create a new TaxYear Info Type */
            TaxYearInfoType myInfoType = new TaxYearInfoType(this, pId, isEnabled, pOrder, pInfoType, pDesc);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfoType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myInfoType, ERROR_VALIDATION);
            }

            /* Add the TaxYear Info Type to the list */
            append(myInfoType);

            /* Validate the TaxYearInfoType */
            myInfoType.validate();

            /* Handle validation failure */
            if (myInfoType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfoType, ERROR_VALIDATION);
            }
        }

        /**
         * Add a TaxYearInfoType to the list.
         * @param pId the Id of the TaxYear info type
         * @param pControlId the control id of the new item
         * @param isEnabled is the TaxYear info type enabled
         * @param pOrder the sort order
         * @param pInfoType the encrypted Name of the TaxYear info type
         * @param pDesc the Encrypted Description of the TaxYear info type
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Boolean isEnabled,
                                  final Integer pOrder,
                                  final byte[] pInfoType,
                                  final byte[] pDesc) throws JDataException {
            /* Create a new TaxYear Info Type */
            TaxYearInfoType myInfoType = new TaxYearInfoType(this, pId, pControlId, isEnabled, pOrder, pInfoType, pDesc);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfoType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myInfoType, ERROR_VALIDATION);
            }

            /* Add the Info Type to the list */
            append(myInfoType);

            /* Validate the InfoType */
            myInfoType.validate();

            /* Handle validation failure */
            if (myInfoType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfoType, ERROR_VALIDATION);
            }
        }

        /**
         * Populate default values.
         * @throws JDataException on error
         */
        public void populateDefaults() throws JDataException {
            /* Loop through all elements */
            for (TaxYearInfoClass myClass : TaxYearInfoClass.values()) {
                /* Create new element */
                TaxYearInfoType myType = new TaxYearInfoType(this, myClass);

                /* Add the InfoType to the list */
                append(myType);

                /* Validate the InfoType */
                myType.validate();

                /* Handle validation failure */
                if (myType.hasErrors()) {
                    throw new JDataException(ExceptionClass.VALIDATE, myType, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}

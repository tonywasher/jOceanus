/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.data.statics;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.data.StaticData;

/**
 * TaxType data type.
 * @author Tony Washer
 */
public class TaxType extends StaticData<TaxType, TaxClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TaxType.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Tax class of the Tax Type.
     * @return the class
     */
    public TaxClass getTaxClass() {
        return super.getStaticClass();
    }

    @Override
    public TaxType getBase() {
        return (TaxType) super.getBase();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the Tax Type with
     * @param pTaxType The Tax Type to copy
     */
    protected TaxType(final TaxTypeList pList,
                      final TaxType pTaxType) {
        super(pList, pTaxType);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Tax Type with
     * @param sName Name of Tax Type
     * @throws JDataException on error
     */
    private TaxType(final TaxTypeList pList,
                    final String sName) throws JDataException {
        super(pList, sName);
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the Tax Type with
     * @param uId ID of TaxType
     * @param isEnabled is the TaxType enabled
     * @param uOrder the sort order
     * @param pName Name of Tax Type
     * @param pDesc Description of Tax Type
     * @throws JDataException on error
     */
    private TaxType(final TaxTypeList pList,
                    final Integer uId,
                    final Boolean isEnabled,
                    final Integer uOrder,
                    final String pName,
                    final String pDesc) throws JDataException {
        super(pList, uId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the TaxType with
     * @param uId ID of TaxType
     * @param uControlId the control id of the new item
     * @param isEnabled is the TaxType enabled
     * @param uOrder the sort order
     * @param sName Encrypted Name of TaxType
     * @param pDesc Encrypted Description of TaxType
     * @throws JDataException on error
     */
    private TaxType(final TaxTypeList pList,
                    final Integer uId,
                    final Integer uControlId,
                    final Boolean isEnabled,
                    final Integer uOrder,
                    final byte[] sName,
                    final byte[] pDesc) throws JDataException {
        super(pList, uId, uControlId, isEnabled, uOrder, sName, pDesc);
    }

    /**
     * Determine whether we should add tax credits to the total.
     * @return <code>true</code> if we should add tax credits to the total, <code>false</code> otherwise.
     */
    public boolean hasTaxCredits() {
        switch (getTaxClass()) {
            case GROSSSALARY:
            case GROSSINTEREST:
            case GROSSDIVIDEND:
            case GROSSUTDIVS:
            case GROSSTAXGAINS:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether we this is the tax paid bucket.
     * @return <code>true</code> if we should add tax credits to the total, <code>false</code> otherwise.
     */
    public boolean isTaxPaid() {
        switch (getTaxClass()) {
            case TAXPAID:
                return true;
            default:
                return false;
        }
    }

    /**
     * Represents a list of {@link TaxType} objects.
     */
    public static class TaxTypeList extends StaticList<TaxType, TaxClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TaxTypeList.class.getSimpleName(),
                DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<TaxClass> getEnumClass() {
            return TaxClass.class;
        }

        /**
         * Construct an empty CORE tax type list.
         * @param pData the DataSet for the list
         */
        public TaxTypeList(final DataSet<?> pData) {
            super(TaxType.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TaxTypeList(final TaxTypeList pSource) {
            super(pSource);
        }

        @Override
        protected TaxTypeList getEmptyList() {
            return new TaxTypeList(this);
        }

        @Override
        public TaxTypeList cloneList(final DataSet<?> pDataSet) {
            return (TaxTypeList) super.cloneList(pDataSet);
        }

        @Override
        public TaxTypeList deriveList(final ListStyle pStyle) {
            return (TaxTypeList) super.deriveList(pStyle);
        }

        @Override
        public TaxTypeList deriveDifferences(final DataList<TaxType> pOld) {
            return (TaxTypeList) super.deriveDifferences(pOld);
        }

        /**
         * Add a new item to the list.
         * @param pItem item to be added
         * @return the newly added item
         */
        @Override
        public TaxType addCopyItem(final DataItem pItem) {
            /* Can only clone a TaxType */
            if (!(pItem instanceof TaxType)) {
                return null;
            }

            TaxType myType = new TaxType(this, (TaxType) pItem);
            add(myType);
            return myType;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public TaxType addNewItem() {
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
         * Add a TaxType.
         * @param pTaxType the Name of the tax type
         * @throws JDataException on error
         */
        public void addBasicItem(final String pTaxType) throws JDataException {
            /* Create a new Tax Type */
            TaxType myTaxType = new TaxType(this, pTaxType);

            /* Check that this TaxTypeId has not been previously added */
            if (!isIdUnique(myTaxType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myTaxType, "Duplicate TaxTypeId");
            }

            /* Check that this TaxType has not been previously added */
            if (findItemByName(pTaxType) != null) {
                throw new JDataException(ExceptionClass.DATA, myTaxType, "Duplicate Tax Type");
            }

            /* Add the Tax Type to the list */
            append(myTaxType);

            /* Validate the TaxType */
            myTaxType.validate();

            /* Handle validation failure */
            if (myTaxType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myTaxType, "Failed validation");
            }
        }

        /**
         * Add a TaxType to the list.
         * @param uId ID of TaxType
         * @param isEnabled is the TaxType enabled
         * @param uOrder the sort order
         * @param pTaxType the Name of the tax type
         * @param pDesc the Description of the tax type
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer uId,
                                final Boolean isEnabled,
                                final Integer uOrder,
                                final String pTaxType,
                                final String pDesc) throws JDataException {
            /* Create a new Tax Type */
            TaxType myTaxType = new TaxType(this, uId, isEnabled, uOrder, pTaxType, pDesc);

            /* Check that this TaxTypeId has not been previously added */
            if (!isIdUnique(myTaxType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myTaxType, "Duplicate TaxTypeId");
            }

            /* Add the Tax Type to the list */
            append(myTaxType);

            /* Validate the TaxType */
            myTaxType.validate();

            /* Handle validation failure */
            if (myTaxType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myTaxType, "Failed validation");
            }
        }

        /**
         * Add a TaxType.
         * @param uId the Id of the tax type
         * @param uControlId the control id of the new item
         * @param isEnabled is the TaxType enabled
         * @param uOrder the sort order
         * @param pTaxType the Encrypted Name of the tax type
         * @param pDesc the Encrypted Description of the tax type
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer uId,
                                  final Integer uControlId,
                                  final Boolean isEnabled,
                                  final Integer uOrder,
                                  final byte[] pTaxType,
                                  final byte[] pDesc) throws JDataException {
            /* Create a new Tax Type */
            TaxType myTaxType = new TaxType(this, uId, uControlId, isEnabled, uOrder, pTaxType, pDesc);

            /* Check that this TaxTypeId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myTaxType, "Duplicate TaxTypeId");
            }

            /* Add the Tax Type to the list */
            append(myTaxType);

            /* Validate the TaxType */
            myTaxType.validate();

            /* Handle validation failure */
            if (myTaxType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myTaxType, "Failed validation");
            }
        }
    }
}

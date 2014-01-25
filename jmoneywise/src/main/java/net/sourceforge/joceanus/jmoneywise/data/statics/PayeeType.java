/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * PayeeType data type.
 */
public class PayeeType
        extends StaticData<PayeeType, PayeeTypeClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = PayeeType.class.getSimpleName();

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
     * Return the PayeeTypeClass of the PayeeType.
     * @return the class
     */
    public PayeeTypeClass getPayeeClass() {
        return super.getStaticClass();
    }

    @Override
    public PayeeType getBase() {
        return (PayeeType) super.getBase();
    }

    @Override
    public PayeeTypeList getList() {
        return (PayeeTypeList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the PayeeType with
     * @param pPayeeType The Payee Type to copy
     */
    protected PayeeType(final PayeeTypeList pList,
                        final PayeeType pPayeeType) {
        super(pList, pPayeeType);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the PayeeType with
     * @param pName Name of PayeeType
     * @throws JOceanusException on error
     */
    private PayeeType(final PayeeTypeList pList,
                      final String pName) throws JOceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the PayeeType with
     * @param pClass Class of PayeeType
     * @throws JOceanusException on error
     */
    private PayeeType(final PayeeTypeList pList,
                      final PayeeTypeClass pClass) throws JOceanusException {
        super(pList, pClass);
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the PayeeType with
     * @param pId the id
     * @param isEnabled is the payeeType enabled
     * @param pOrder the sort order
     * @param pName Name of PayeeType
     * @param pDesc Description of PayeeType
     * @throws JOceanusException on error
     */
    private PayeeType(final PayeeTypeList pList,
                      final Integer pId,
                      final Boolean isEnabled,
                      final Integer pOrder,
                      final String pName,
                      final String pDesc) throws JOceanusException {
        super(pList, pId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the PayeeType with
     * @param pId ID of PayeeType
     * @param pControlId the control id of the new item
     * @param isEnabled is the payeeType enabled
     * @param pOrder the sort order
     * @param pName Encrypted Name of PayeeType
     * @param pDesc Encrypted Description of PayeeType
     * @throws JOceanusException on error
     */
    private PayeeType(final PayeeTypeList pList,
                      final Integer pId,
                      final Integer pControlId,
                      final Boolean isEnabled,
                      final Integer pOrder,
                      final byte[] pName,
                      final byte[] pDesc) throws JOceanusException {
        super(pList, pId, pControlId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Represents a list of {@link PayeeType} objects.
     */
    public static class PayeeTypeList
            extends StaticList<PayeeType, PayeeTypeClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(PayeeTypeList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<PayeeTypeClass> getEnumClass() {
            return PayeeTypeClass.class;
        }

        /**
         * Construct an empty CORE payeeType list.
         * @param pData the DataSet for the list
         */
        public PayeeTypeList(final DataSet<?, ?> pData) {
            super(PayeeType.class, pData, MoneyWiseDataType.PAYEETYPE, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private PayeeTypeList(final PayeeTypeList pSource) {
            super(pSource);
        }

        @Override
        protected PayeeTypeList getEmptyList(final ListStyle pStyle) {
            PayeeTypeList myList = new PayeeTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public PayeeType addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a PayeeType */
            if (!(pItem instanceof PayeeType)) {
                return null;
            }

            PayeeType myType = new PayeeType(this, (PayeeType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public PayeeType addNewItem() {
            throw new UnsupportedOperationException();
        }

        /**
         * Obtain the type of the item.
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Add a PayeeType to the list.
         * @param pPayeeType the Name of the account category type
         * @throws JOceanusException on error
         */
        public void addBasicItem(final String pPayeeType) throws JOceanusException {
            /* Create a new Payee Type */
            PayeeType myPayeeType = new PayeeType(this, pPayeeType);

            /* Check that this PayeeType has not been previously added */
            if (findItemByName(pPayeeType) != null) {
                myPayeeType.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JMoneyWiseDataException(myPayeeType, ERROR_VALIDATION);
            }

            /* Check that this PayeeTypeId has not been previously added */
            if (!isIdUnique(myPayeeType.getId())) {
                myPayeeType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myPayeeType, ERROR_VALIDATION);
            }

            /* Add the PayeeType to the list */
            append(myPayeeType);

            /* Validate the PayeeType */
            myPayeeType.validate();

            /* Handle validation failure */
            if (myPayeeType.hasErrors()) {
                throw new JMoneyWiseDataException(myPayeeType, ERROR_VALIDATION);
            }
        }

        /**
         * Add a PayeeType to the list.
         * @param pId the Id of the payee type
         * @param isEnabled is the payee type enabled
         * @param pOrder the sort order
         * @param pPayeeType the Name of the payee type
         * @param pDesc the Description of the payee type
         * @throws JOceanusException on error
         */
        public void addOpenItem(final Integer pId,
                                final Boolean isEnabled,
                                final Integer pOrder,
                                final String pPayeeType,
                                final String pDesc) throws JOceanusException {
            /* Create a new Payee Type */
            PayeeType myPayeeType = new PayeeType(this, pId, isEnabled, pOrder, pPayeeType, pDesc);

            /* Check that this PayeeTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myPayeeType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myPayeeType, ERROR_VALIDATION);
            }

            /* Add the Payee Type to the list */
            append(myPayeeType);

            /* Validate the PayeeType */
            myPayeeType.validate();

            /* Handle validation failure */
            if (myPayeeType.hasErrors()) {
                throw new JMoneyWiseDataException(myPayeeType, ERROR_VALIDATION);
            }
        }

        /**
         * Add a PayeeType to the list.
         * @param pId the Id of the payee type
         * @param pControlId the control id of the new item
         * @param isEnabled is the payee type enabled
         * @param pOrder the sort order
         * @param pPayeeType the encrypted Name of the payee type
         * @param pDesc the Encrypted Description of the payee type
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Boolean isEnabled,
                                  final Integer pOrder,
                                  final byte[] pPayeeType,
                                  final byte[] pDesc) throws JOceanusException {
            /* Create a new PayeeType */
            PayeeType myPayeeType = new PayeeType(this, pId, pControlId, isEnabled, pOrder, pPayeeType, pDesc);

            /* Check that this PayeeTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myPayeeType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myPayeeType, ERROR_VALIDATION);
            }

            /* Add the PayeeType to the list */
            append(myPayeeType);

            /* Validate the PayeeType */
            myPayeeType.validate();

            /* Handle validation failure */
            if (myPayeeType.hasErrors()) {
                throw new JMoneyWiseDataException(myPayeeType, ERROR_VALIDATION);
            }
        }

        /**
         * Populate default values.
         * @throws JOceanusException on error
         */
        public void populateDefaults() throws JOceanusException {
            /* Loop through all elements */
            for (PayeeTypeClass myClass : PayeeTypeClass.values()) {
                /* Create new element */
                PayeeType myPayeeType = new PayeeType(this, myClass);

                /* Add the PayeeType to the list */
                append(myPayeeType);

                /* Validate the PayeeType */
                myPayeeType.validate();

                /* Handle validation failure */
                if (myPayeeType.hasErrors()) {
                    throw new JMoneyWiseDataException(myPayeeType, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
